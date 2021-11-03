
package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import variables.*;
import variables.Gremio.MiembroGremio;
import variables.Hechizo.StatsHechizos;
import variables.Mapa.*;
import variables.PuestoMercadillo.ObjetoMercadillo;
import variables.NPCModelo.*;
import variables.Casa;
import variables.Cofre;
import variables.Dragopavo;
import variables.Mapa.Celda;
import variables.Mapa.ObjetoInteractivo;
import variables.NPCModelo.NPC;
import variables.Objeto;
import variables.Objeto.ObjetoModelo;
import variables.Objevivo;
import variables.Oficio.AccionTrabajo;
import variables.Oficio.StatsOficio;
import variables.Pelea;
import variables.Pelea.Luchador;
import variables.Personaje;
import variables.Personaje.Grupo;
import variables.PuestoMercadillo;
import variables.Recaudador;
import variables.Tienda;
import estaticos.*;
import estaticos.MundoDofus.Intercambio;
import estaticos.MundoDofus.InvitarTaller;

public class EntradaPersonaje implements Runnable {
	private BufferedReader _in;
	private Thread _thread;
	private PrintWriter _out;
	private Socket _socket;
	private Cuenta _cuenta;
	private Personaje _perso;
	private Map<Integer, AccionDeJuego> _acciones = new TreeMap<Integer, AccionDeJuego>();
	private long _tiempoUltComercio = 0, _tiempoUltReclutamiento = 0, _tiempoUltSalvada = 0, _tiempoUltAlineacion = 0;
	private Comandos _comando;
	private boolean _entrar = true;
	public static class AccionDeJuego {
		public int _idUnica;
		public int _accionID;
		public String _packet;
		public String _args;
		
		public AccionDeJuego(int id, int accionId, String packet) {
			_idUnica = id;
			_accionID = accionId;
			_packet = packet;
		}
	}
	
	public EntradaPersonaje(Socket socket) {
		try {
			_socket = socket;
			_entrar = true;
			if (CentroInfo.compararConIPBaneadas(_socket.getInetAddress().getHostAddress())) {
				_entrar = false;
				_socket.close();
				return;
			}
			_in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
			_out = new PrintWriter(_socket.getOutputStream());
			_thread = new Thread(this);
			_thread.setDaemon(true);
			_thread.start();
		} catch (IOException e) {
			try {
				System.out.println(e.getMessage());
				if (!_socket.isClosed())
					_socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public boolean poderEntrar() {
		return _entrar;
	}
	
	public Socket getSock() {
		return _socket;
	}
	
	public void run() {
		try {
			String packet = "";
			char charCur[] = new char[1];
			GestorSalida.ENVIAR_HG_SALUDO_JUEGO_GENERAL(_out);
			while (_in.read(charCur, 0, 1) != -1 && Bustemu.Corriendo) {
				if (charCur[0] != '\u0000' && charCur[0] != '\n' && charCur[0] != '\r') {
					packet += charCur[0];
				} else if (!packet.isEmpty()) {
					packet = Encriptador.aUnicode(packet);
					if (Bustemu.MOSTRAR_RECIBIDOS)
						System.out.println("<<RECIBIR:  " + packet);
					analizar_Packets(packet);
					packet = "";
				}
			}
		} catch (IOException e) {
			try {
				_in.close();
				_out.close();
				if (_cuenta != null) {
					_cuenta.setTempPerso(null);
					_cuenta.setEntradaPersonaje(null);
					_cuenta.setEntradaGeneral(null);
				}
				if (!_socket.isClosed())
					_socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			salir();
		}
	}
	
	private void analizar_Packets(String packet) {
		if (packet.length() > 3 && packet.substring(0, 4).equalsIgnoreCase("ping")) {
			GestorSalida.ENVIAR_pong(_out);
			return;
		}
		if (packet.length() > 4 && packet.substring(0, 5).equalsIgnoreCase("qping")) {
			if (_perso != null) {
				Pelea pelea = _perso.getPelea();
				if (pelea != null) {
					Luchador luchador = pelea.getLuchadorPorPJ(_perso);
					if (luchador != null && luchador.puedeJugar()) {
						pelea.tiempoTurno();
					}
				}
			}
			GestorSalida.ENVIAR_qpong(_out);
			return;
		}
		if (_perso != null) {
			if (_perso.cambiarNombre()) {
				if (GestorSQL.personajeYaExiste(packet)) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, "El nombre no se encuentra disponible");
					GestorSalida.ENVIAR_AlE_CAMBIAR_NOMBRE(_out, "r");
					GestorSalida.ENVIAR_AlE_CAMBIAR_NOMBRE(_out, "r");
					return;
				}
				boolean esValido = true;
				String nombre = packet.toLowerCase();
				if (nombre.length() > 20) {
					esValido = false;
				}
				if (esValido) {
					int tiretCount = 0;
					char exLetterA = ' ';
					char exLetterB = ' ';
					for (char curLetter : nombre.toCharArray()) {
						if (! ( (curLetter >= 'a' && curLetter <= 'z') || curLetter == '-')) {
							esValido = false;
							break;
						}
						if (curLetter == exLetterA && curLetter == exLetterB) {
							esValido = false;
							break;
						}
						if (curLetter >= 'a' && curLetter <= 'z') {
							exLetterA = exLetterB;
							exLetterB = curLetter;
						}
						if (curLetter == '-') {
							if (tiretCount >= 1) {
								esValido = false;
								break;
							} else {
								tiretCount++;
							}
						}
					}
				}
				if (!esValido) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, "El nombre no se encuentra disponible");
					GestorSalida.ENVIAR_AlE_CAMBIAR_NOMBRE(_out, "r");
					GestorSalida.ENVIAR_AlE_CAMBIAR_NOMBRE(_out, "r");
					return;
				}
				_perso.setNombre(packet);
				GestorSalida.ENVIAR_AlE_CAMBIAR_NOMBRE(_out, "r");
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, "Tu nombre ha sido cambiado a " + packet);
				return;
			}
		}
		switch (packet.charAt(0)) {
			case 'A':// cuenta
				analizar_Cuenta(packet);
				break;
			case 'B':// basic
				analizar_Basicos(packet);
				break;
			case 'C':// conquista
				analizar_Conquista(packet);
				break;
			case 'c':// canal
				analizar_Canal(packet);
				break;
			case 'D':// dialogo
				analizar_Dialogos(packet);
				break;
			case 'E':// intercambio
				analizar_Intercambios(packet);
				break;
			case 'e':// entorno, ambiente
				analizar_Ambiente(packet);
				break;
			case 'F':// amigo
				analizar_Amigos(packet);
				break;
			case 'f':// pelea
				analizar_Peleas(packet);
				break;
			case 'G':// juego
				analizar_Juego(packet);
				break;
			case 'g':// gremio
				analizar_Gremio(packet);
				break;
			case 'h':// casa
				analizar_Casas(packet);
				break;
			case 'i':// enemigo
				analizar_Enemigos(packet);
				break;
			case 'J':// oficios
				analizar_Oficios(packet);
				break;
			case 'K':// casa
				analizar_Claves(packet);
				break;
			case 'O':// objetos
				analizar_Objetos(packet);
				break;
			case 'P':// grupo
				analizar_Grupo(packet);
				break;
			case 'Q':// misiones
				analizar_Misiones(packet);
				break;
			case 'R':// montura
				analizar_Montura(packet);
				break;
			case 'S':// hechizo
				analizar_Hechizos(packet);
				break;
			case 'T': // tutoriales
				analizar_Tutoriales(packet);
				break;
			case 'W':
				analizar_Areas(packet);
				break;
		}
	}
	
	private void analizar_Tutoriales(String packet) {
		String[] param = packet.split("\\|");
		Tutorial tuto = _perso.getTutorial();
		_perso.setTutorial(null);
		switch (packet.charAt(1)) {
			case 'V':// fin de tutorial
				if (packet.charAt(2) != '0') {
					try {
						int index = packet.charAt(2) - 1;
						tuto.getRecompensa().get(index).aplicar(_perso, null, -1, -1);
					} catch (Exception e) {}
				}
				try {
					tuto.getFin().aplicar(_perso, null, -1, -1);
				} catch (Exception e) {}
				_perso.setOcupado(false);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
				try {
					_perso.setOrientacion(Integer.parseInt(param[2]));
					_perso.setCelda(_perso.getMapa().getCelda(Integer.parseInt(param[1])));
				} catch (Exception e) {}
				break;
		}
	}
	
	private void analizar_Conquista(String packet) {
		switch (packet.charAt(1)) {
			case 'b':// balance de mundo y area
				GestorSalida.ENVIAR_Cb_BALANCE_CONQUISTA(_perso, MundoDofus.getBalanceMundo(_perso.getAlineacion()) + ";"
						+ MundoDofus.getBalanceArea(_perso.getMapa().getSubArea().getArea(), _perso.getAlineacion()));
				break;
			case 'B':// bonus de alineacion
				double porc = MundoDofus.getBalanceMundo(_perso.getAlineacion());
				double porcN = Math.rint( (_perso.getNivelAlineacion() / 2.5) + 1);
				GestorSalida.ENVIAR_CB_BONUS_CONQUISTA(_perso, porc + "," + porc + "," + porc + ";" + porcN + "," + porcN + ","
						+ porcN + ";" + porc + "," + porc + "," + porc);
				break;
			case 'W':// info de mapa sobre los mapas
				conquista_Geoposicion(packet);
				break;
			case 'I':// Modificacion de precio de venta
				conquista_Defensa(packet);
				break;
			case 'F':// Cerrar ventana de compra
				conquista_Unirse_Defensa_Prisma(packet);
				break;
		}
	}
	
	private void analizar_Misiones(String packet) {
		switch (packet.charAt(1)) {
			case 'L':// balance de mundo y area
				GestorSalida.ENVIAR_QL_LISTA_MISIONES(_perso, "");
				break;
			case 'S':// bonus de alineacion
				GestorSalida.ENVIAR_QS_PASOS_RECOMPENSA_MISION(_perso, "");
				break;
		}
	}
	
	private void conquista_Defensa(String packet) {
		switch (packet.charAt(2)) {
			case 'J':// info de prismas defensa
				String str = _perso.analizarPrismas();
				Prisma prisma = MundoDofus.getPrisma(_perso.getMapa().getSubArea().getPrismaID());
				if (prisma != null) {
					Prisma.analizarAtaque(_perso);
					Prisma.analizarDefensa(_perso);
				}
				GestorSalida.ENVIAR_CIJ_INFO_UNIRSE_PRISMA(_perso, str);
				break;
			case 'V':
				GestorSalida.ENVIAR_CIV_CERRAR_INFO_CONQUISTA(_perso);
				break;
		}
	}
	
	private void conquista_Geoposicion(String packet) {
		switch (packet.charAt(2)) {
			case 'J':// info de prismas defensa
				GestorSalida.ENVIAR_CW_INFO_MUNDO_CONQUISTA(_perso, MundoDofus.prismasGeoposicion(_perso.getAlineacion()));
				break;
			case 'V':
				GestorSalida.ENVIAR_CIV_CERRAR_INFO_CONQUISTA(_perso);
				break;
		}
	}
	
	private void conquista_Unirse_Defensa_Prisma(String packet) {
		switch (packet.charAt(2)) {
			case 'J':// info de prismas defensa
				int prismaID = _perso.getMapa().getSubArea().getPrismaID();
				Prisma prisma = MundoDofus.getPrisma(prismaID);
				if (prisma == null)
					return;
				short mapaID = prisma.getMapa();
				int celdaID = prisma.getCelda();
				if (prisma.getAlineacion() != _perso.getAlineacion())
					return;
				if (_perso.getPelea() != null)
					return;
				if (prisma.getPelea().unirsePeleaPrisma(_perso, prismaID, mapaID, celdaID)) {
					for (Personaje z : MundoDofus.getPJsEnLinea()) {
						if (z == null || z.getAlineacion() != _perso.getAlineacion())
							continue;
						Prisma.analizarDefensa(z);
					}
				}
				break;
		}
	}
	
	private void analizar_Casas(String packet) {
		switch (packet.charAt(1)) {
			case 'B':// Comprar casa
				packet = packet.substring(2);
				Casa.comprarCasa(_perso);
				break;
			case 'G':// Casa de Gremio
				packet = packet.substring(2);
				if (packet.isEmpty())
					packet = null;
				Casa.analizarCasaGremio(_perso, packet);
				break;
			case 'Q':// Quitar/Expulsar de la casa
				packet = packet.substring(2);
				Casa.salir(_perso, packet);
				break;
			case 'S':// Modificacion de precio de venta
				packet = packet.substring(2);
				Casa.precioVenta(_perso, packet);
				break;
			case 'V':// Cerrar ventana de compra
				Casa.cerrarVentanaCompra(_perso);
				break;
		}
	}
	
	private void analizar_Claves(String packet) {
		switch (packet.charAt(1)) {
			case 'V':// Cerrar ventana de codigo
				Casa.cerrarVentana(_perso);
				break;
			case 'K':// Envio de codigo
				casa_Codigo(packet);
				break;
		}
	}
	
	private void casa_Codigo(String packet) {
		switch (packet.charAt(2)) {
			case '0':// Envio de codigo
				packet = packet.substring(4);
				if (_perso.getCofre() != null)
					Cofre.abrirCofre(_perso, packet, false);
				else
					Casa.abrirCasa(_perso, packet, false);
				break;
			case '1':// Cambio de codigo
				packet = packet.substring(4);
				if (_perso.getCofre() != null)
					Cofre.codificarCofre(_perso, packet);
				else
					Casa.codificarCasa(_perso, packet);
				break;
		}
	}
	
	private void analizar_Enemigos(String packet) {
		switch (packet.charAt(1)) {
			case 'A':// agregar
				enemigo_Agregar(packet);
				break;
			case 'D':// Deletrear
				enemigo_Borrar(packet);
				break;
			case 'L':// Lista
				GestorSalida.ENVIAR_iL_LISTA_ENEMIGOS(_perso);
				break;
		}
	}
	
	private void enemigo_Agregar(String packet) {
		if (_perso == null)
			return;
		int guid = -1;
		switch (packet.charAt(2)) {
			case '%':// Numero de jugadores
				packet = packet.substring(3);
				Personaje P = MundoDofus.getPjPorNombre(packet);
				if (P == null) {
					GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
					return;
				}
				guid = P.getCuentaID();
				break;
			case '*':// Numero de cuentas
				packet = packet.substring(3);
				Cuenta C = MundoDofus.getCuentaPorApodo(packet);
				if (C == null) {
					GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
					return;
				}
				guid = C.getID();
				break;
			default:
				packet = packet.substring(2);
				Personaje Pr = MundoDofus.getPjPorNombre(packet);
				if (Pr == null ? true : !Pr.enLinea()) {
					GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
					return;
				}
				guid = Pr.getCuenta().getID();
				break;
		}
		_cuenta.addEnemigo(packet, guid);
	}
	
	private void enemigo_Borrar(String packet) {
		int id = -1;
		switch (packet.charAt(2)) {
			case '%':// Nombre de jugador
				packet = packet.substring(3);
				Personaje pj = MundoDofus.getPjPorNombre(packet);
				if (pj == null) {
					GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
					return;
				}
				id = pj.getCuentaID();
				break;
			case '*':// apodo
				packet = packet.substring(3);
				Cuenta cuenta = MundoDofus.getCuentaPorApodo(packet);
				if (cuenta == null) {
					GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
					return;
				}
				id = cuenta.getID();
				break;
			default:
				packet = packet.substring(2);
				Personaje perso = MundoDofus.getPjPorNombre(packet);
				if (perso == null ? true : !perso.enLinea()) {
					GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
					return;
				}
				id = perso.getCuenta().getID();
				break;
		}
		_cuenta.borrarEnemigo(id);
	}
	
	private void analizar_Oficios(String packet) {
		switch (packet.charAt(1)) {
			case 'O':
				String[] infos = packet.substring(2).split("\\|");
				int posOficio = Integer.parseInt(infos[0]);
				int opciones = Integer.parseInt(infos[1]);
				int slots = Integer.parseInt(infos[2]);
				StatsOficio statOficio = _perso.getStatsOficios().get(posOficio);
				if (statOficio == null)
					return;
				statOficio.setOpciones(opciones);
				statOficio.setSlotsPublico(slots);
				GestorSalida.ENVIAR_JO_OFICIO_OPCIONES(_perso, statOficio);
				break;
		}
	}
	
	private void analizar_Areas(String packet) {
		switch (packet.charAt(1)) {
			case 'U':
				zaap_Usar(packet);
				break;
			case 'u':
				zaapi_Usar(packet);
				break;
			case 'v':
				zaapi_Cerrar();
				break;
			case 'V':
				zaap_Cerrar();
				break;
			case 'w':
				prisma_Cerrar();
				break;
			case 'p':
				prisma_Usar(packet);
				break;
		}
	}
	
	private void zaapi_Cerrar() {
		_perso.cerrarZaapi();
	}
	
	private void prisma_Cerrar() {
		_perso.cerrarPrisma();
	}
	
	private void zaapi_Usar(String packet) {
		if (_perso.getDeshonor() >= 2) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "183");
			return;
		}
		_perso.usarZaapi(packet);
	}
	
	private void prisma_Usar(String packet) {
		if (_perso.getDeshonor() >= 2) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "183");
			return;
		}
		_perso.usarPrisma(packet);
	}
	
	private void zaap_Cerrar() {
		_perso.cerrarZaap();
	}
	
	private void zaap_Usar(String packet) {
		short id = -1;
		try {
			id = Short.parseShort(packet.substring(2));
		} catch (Exception e) {}
		if (id == -1)
			return;
		_perso.usarZaap(id);
	}
	
	private void analizar_Gremio(String packet) {
		switch (packet.charAt(1)) {
			case 'B':// Stats
				gremio_Stats(packet);
				break;
			case 'b':// hechizos
				gremio_Hechizos(packet);
				break;
			case 'C':// Creacion
				gremio_Crear(packet);
				break;
			case 'f':// Teleport a cercado de gremio
				gremio_Cercado(packet.substring(2));
				break;
			case 'F':// Retirar recaudador
				gremio_Borrar_Recaudador(packet.substring(2));
				break;
			case 'h':// Teleport a casa del gremio
				gremio_Casa(packet.substring(2));
				break;
			case 'H':// Poner recaudador
				gremio_Poner_Recaudador();
				break;
			case 'I':// Infos
				gremio_Informacion(packet.charAt(2));
				break;
			case 'J':// unir al gremio
				gremio_Unirse(packet.substring(2));
				break;
			case 'K':// quitar del gremio
				gremio_Expulsar(packet.substring(2));
				break;
			case 'P':// Promover de rango
				gremio_Promover_Rango(packet.substring(2));
				break;
			case 'T':// unirse al ataq de un recaudador
				gremio_Unirse_Pelea_Recaudador(packet.substring(2));
				break;
			case 'V':// cerrar panel de creacion del gremio
				gremio_CancelarCreacion();
				break;
		}
	}
	
	private void gremio_Stats(String packet) {
		if (_perso.getGremio() == null)
			return;
		Gremio G = _perso.getGremio();
		if (!_perso.getMiembroGremio().puede(CentroInfo.G_MODIFBOOST))
			return;
		switch (packet.charAt(2)) {
			case 'p':// Prospeccion
				if (G.getCapital() < 1)
					return;
				if (G.getStats(176) >= 500)
					return;
				G.setCapital(G.getCapital() - 1);
				G.actualizarStats(176, 1);
				break;
			case 'x':// Sabiduria
				if (G.getCapital() < 1)
					return;
				if (G.getStats(124) >= 400)
					return;
				G.setCapital(G.getCapital() - 1);
				G.actualizarStats(124, 1);
				break;
			case 'o':// Pod
				if (G.getCapital() < 1)
					return;
				if (G.getStats(158) >= 5000)
					return;
				G.setCapital(G.getCapital() - 1);
				G.actualizarStats(158, 20);
				break;
			case 'k':// Numero de recaudadores
				if (G.getCapital() < 10)
					return;
				if (G.getNroRecau() >= 50)
					return;
				G.setCapital(G.getCapital() - 10);
				G.setNroRecau(G.getNroRecau() + 1);
				break;
		}
		GestorSQL.ACTUALIZAR_GREMIO(G);
		GestorSalida.GAME_SEND_gIB_PACKET(_perso, _perso.getGremio().analizarRecauAGrmio());
	}
	
	private void gremio_Hechizos(String packet) {
		if (_perso.getGremio() == null)
			return;
		Gremio G2 = _perso.getGremio();
		if (!_perso.getMiembroGremio().puede(CentroInfo.G_MODIFBOOST))
			return;
		int spellID = Integer.parseInt(packet.substring(2));
		if (G2.getHechizos().containsKey(spellID)) {
			if (G2.getCapital() < 5)
				return;
			G2.setCapital(G2.getCapital() - 5);
			G2.boostHechizo(spellID);
			GestorSQL.ACTUALIZAR_GREMIO(G2);
			GestorSalida.GAME_SEND_gIB_PACKET(_perso, _perso.getGremio().analizarRecauAGrmio());
		}
	}
	
	private void gremio_Unirse_Pelea_Recaudador(String packet) {
		switch (packet.charAt(0)) {
			case 'J':// unir
				int recauID = Integer.parseInt(packet.substring(1));
				Recaudador recau = MundoDofus.getRecaudador(recauID);
				if (recau == null)
					return;
				short mapaID = (short) recau.getMapaID();
				int celdaID = recau.getCeldalID();
				if (_perso.getPelea() != null)
					return;
				if (recau.getPelea().unirsePeleaRecaudador(_perso, recauID, mapaID, celdaID)) {
					for (Personaje miembros : _perso.getGremio().getPjMiembros()) {
						if (miembros == null || !miembros.enLinea())
							continue;
						Recaudador.analizarDefensa(miembros, _perso.getGremio().getID());
					}
				}
				break;
		}
	}
	
	private void gremio_Borrar_Recaudador(String packet) {
		if (_perso.getGremio() == null)
			return;
		if (!_perso.getMiembroGremio().puede(CentroInfo.G_RECOLECTARRECAUDADOR))
			return;
		int IDPerco = Integer.parseInt(packet);
		Recaudador recau = MundoDofus.getRecaudador(IDPerco);
		if (recau == null || recau.getEstadoPelea() > 0)
			return;
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_perso.getMapa(), IDPerco);
		recau.borrarRecauPorRecolecta(recau.getID(), _perso);
		for (Personaje z : _perso.getGremio().getPjMiembros()) {
			if (z == null)
				continue;
			if (z.enLinea()) {
				GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z, Recaudador.analizarRecaudadores(z.getGremio().getID()));
				String str = "";
				str += "R" + recau.getN1() + "," + recau.getN2() + "|";
				str += recau.getMapaID() + "|";
				str += MundoDofus.getMapa((short) recau.getMapaID()).getX() + "|"
						+ MundoDofus.getMapa((short) recau.getMapaID()).getY() + "|" + _perso.getNombre();
				GestorSalida.GAME_SEND_gT_PACKET(z, str);
			}
		}
	}
	
	private void gremio_Poner_Recaudador() {
		Gremio gremio = _perso.getGremio();
		if (gremio == null)
			return;
		if (!_perso.getMiembroGremio().puede(CentroInfo.G_PONERRECAUDADOR))
			return;
		if (gremio.getPjMiembros().size() < 10)
			return;
		short precio = (short) (1000 + 10 * gremio.getNivel());
		if (_perso.getKamas() < precio) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "182");
			return;
		}
		Mapa mapa = _perso.getMapa();
		if (Recaudador.getIDGremioPorMapaID(mapa.getID()) > 0) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1168;1");
			return;
		}
		if (mapa.getLugaresString().length() < 5 || mapa.getID() == 11095) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "113");
			return;
		}
		if (MundoDofus.cantRecauDelGremio(gremio.getID()) >= gremio.getNroRecau())
			return;
		String random1 = Integer.toString(Formulas.getRandomValor(1, 129), 36);
		String random2 = Integer.toString(Formulas.getRandomValor(1, 227), 36);
		int id = GestorSQL.getSigIDRecaudador();
		Recaudador recaudador = new Recaudador(id, mapa.getID(), _perso.getCelda().getID(), (byte) 3, gremio.getID(), random1,
				random2, "", 0, 0);
		MundoDofus.addRecaudador(recaudador);
		GestorSalida.ENVIAR_GM_AGREGAR_RECAUDADOR_AL_MAPA(mapa);
		GestorSQL.ADD_RECAUDADOR_EN_MAPA(id, mapa.getID(), gremio.getID(), _perso.getCelda().getID(), 3, random1, random2);
		for (Personaje z : gremio.getPjMiembros()) {
			if (z != null && z.enLinea()) {
				GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z, Recaudador.analizarRecaudadores(gremio.getID()));
				String str = "";
				str += "S" + recaudador.getN1() + "," + recaudador.getN2() + "|";
				str += recaudador.getMapaID() + "|";
				str += MundoDofus.getMapa((short) recaudador.getMapaID()).getX() + "|"
						+ MundoDofus.getMapa((short) recaudador.getMapaID()).getY() + "|" + _perso.getNombre();
				GestorSalida.GAME_SEND_gT_PACKET(z, str);
			}
		}
	}
	
	private void gremio_Cercado(String packet) {
		if (_perso == null) {
			return;
		}
		if (_perso.getGremio() == null) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1135");
			return;
		}
		short mapaID = Short.parseShort(packet);
		Cercado MP = MundoDofus.getMapa(mapaID).getCercado();
		if (MP.getGremio().getID() != _perso.getGremio().getID()) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1135");
			return;
		}
		int celdaID = MundoDofus.getCeldaCercadoPorMapaID(mapaID);
		if (_perso.tieneObjModeloNoEquip(9035, 1)) {
			_perso.removerObjetoPorModYCant(9035, 1);
			_perso.teleport(mapaID, celdaID);
		} else {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1159");
			return;
		}
	}
	
	private void gremio_Casa(String packet) {
		if (_perso.getGremio() == null) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1135");
			return;
		}
		if (_perso.getPelea() != null || _perso.estaOcupado())
			return;
		int HouseID = Integer.parseInt(packet);
		Casa h = MundoDofus.getCasas().get(HouseID);
		if (h == null)
			return;
		if (_perso.getGremio().getID() != h.getGremioID()) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1135");
			return;
		}
		if (!h.tieneDerecho(CentroInfo.H_TELEPORTGREMIO)) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1136");
			return;
		}
		if (_perso.tieneObjModeloNoEquip(8883, 1)) {
			_perso.removerObjetoPorModYCant(8883, 1);
			_perso.teleport((short) h.getMapaIDDentro(), h.getCeldaIDDentro());
		} else {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1137");
			return;
		}
	}
	
	private void gremio_Promover_Rango(String packet) {
		if (_perso.getGremio() == null)
			return;
		String[] infos = packet.split("\\|");
		int id = Integer.parseInt(infos[0]);
		int rango = Integer.parseInt(infos[1]);
		byte xpDonada = Byte.parseByte(infos[2]);
		int derecho = Integer.parseInt(infos[3]);
		Personaje p = MundoDofus.getPersonaje(id);
		MiembroGremio aCambiar;
		MiembroGremio cambiador = _perso.getMiembroGremio();
		if (p == null) {
			int guildId = GestorSQL.esPJenGremio(id);
			if (guildId < 0)
				return;
			if (guildId != _perso.getGremio().getID()) {
				GestorSalida.GAME_SEND_gK_PACKET(_perso, "Ed");
				return;
			}
			aCambiar = MundoDofus.getGremio(guildId).getMiembro(id);
		} else {
			if (p.getGremio() == null)
				return;
			if (_perso.getGremio().getID() != p.getGremio().getID()) {
				GestorSalida.GAME_SEND_gK_PACKET(_perso, "Ea");
				return;
			}
			aCambiar = p.getMiembroGremio();
		}
		if (cambiador.getRango() == 1) {
			if (cambiador.getID() == aCambiar.getID()) {
				rango = -1;
				derecho = -1;
			} else {
				if (rango == 1) {
					cambiador.setTodosDerechos(2, (byte) -1, 29694);
					rango = 1;
					xpDonada = -1;
					derecho = 1;
				}
			}
		} else {
			if (aCambiar.getRango() == 1) {
				rango = -1;
				derecho = -1;
			} else {
				if (!cambiador.puede(CentroInfo.G_MODRANGOS) || rango == 1)
					rango = -1;
				if (!cambiador.puede(CentroInfo.G_MODIFDERECHOS) || derecho == 1)
					derecho = -1;
				if (!cambiador.puede(CentroInfo.G_SUXPDONADA) && !cambiador.puede(CentroInfo.G_TODASXPDONADAS)
						&& cambiador.getID() == aCambiar.getID())
					xpDonada = -1;
			}
			if (!cambiador.puede(CentroInfo.G_TODASXPDONADAS) && !cambiador.equals(aCambiar))
				xpDonada = -1;
		}
		aCambiar.setTodosDerechos(rango, xpDonada, derecho);
		GestorSalida.GAME_SEND_gS_PACKET(_perso, _perso.getMiembroGremio());
		if (p != null && p.getID() != _perso.getID())
			GestorSalida.GAME_SEND_gS_PACKET(p, p.getMiembroGremio());
	}
	
	private void gremio_CancelarCreacion() {
		GestorSalida.ENVIAR_gV_CERRAR_PANEL_GREMIO(_perso);
	}
	
	private void gremio_Expulsar(String nombre) {
		if (_perso.getGremio() == null)
			return;
		Personaje P = MundoDofus.getPjPorNombre(nombre);
		int idMiembro = -1, idGremio = -1;
		Gremio gremio;
		MiembroGremio miembroExpulsar;
		if (P == null) {
			int infos[] = GestorSQL.esPJenGremio(nombre);
			idMiembro = infos[0];
			idGremio = infos[1];
			if (idGremio < 0 || idMiembro < 0)
				return;
			gremio = MundoDofus.getGremio(idGremio);
			miembroExpulsar = gremio.getMiembro(idMiembro);
		} else {
			gremio = P.getGremio();
			if (gremio == null) {
				gremio = MundoDofus.getGremio(_perso.getGremio().getID());
			}
			miembroExpulsar = gremio.getMiembro(P.getID());
			if (miembroExpulsar == null)
				return;
			if (miembroExpulsar.getGremio().getID() != _perso.getGremio().getID())
				return;
		}
		if (gremio.getID() != _perso.getGremio().getID()) {
			GestorSalida.GAME_SEND_gK_PACKET(_perso, "Ea");
			return;
		}
		MiembroGremio expulsador = _perso.getMiembroGremio();
		if (!expulsador.puede(CentroInfo.G_BANEAR) && expulsador.getID() != miembroExpulsar.getID()) {
			GestorSalida.GAME_SEND_gK_PACKET(_perso, "Ed");
			return;
		}
		if (expulsador.getID() != miembroExpulsar.getID()) {
			if (miembroExpulsar.getRango() == 1)
				return;
			gremio.expulsarMiembro(miembroExpulsar.getPerso());
			if (P != null)
				P.setMiembroGremio(null);
			GestorSalida.GAME_SEND_gK_PACKET(_perso, "K" + _perso.getNombre() + "|" + nombre);
			if (P != null)
				GestorSalida.GAME_SEND_gK_PACKET(P, "K" + _perso.getNombre());
		} else {
			Gremio G = _perso.getGremio();
			if (expulsador.getRango() == 1 && G.getPjMiembros().size() > 1) {
				for (Personaje pj : G.getPjMiembros()) {
					G.expulsarMiembro(pj);
					pj.setMiembroGremio(null);
				}
			} else {
				G.expulsarMiembro(_perso);
				_perso.setMiembroGremio(null);
			}
			if (G.getPjMiembros().isEmpty())
				MundoDofus.borrarGremio(G.getID());
			GestorSalida.GAME_SEND_gK_PACKET(_perso, "K" + nombre + "|" + nombre);
		}
	}
	
	private void gremio_Unirse(String packet) {
		switch (packet.charAt(0)) {
			case 'R':// Invitar
				Personaje P = MundoDofus.getPjPorNombre(packet.substring(1));
				if (P == null || _perso.getGremio() == null) {
					GestorSalida.GAME_SEND_gJ_PACKET(_perso, "Eu");
					return;
				}
				if (!P.enLinea()) {
					GestorSalida.GAME_SEND_gJ_PACKET(_perso, "Eu");
					return;
				}
				if (P.estaOcupado()) {
					GestorSalida.GAME_SEND_gJ_PACKET(_perso, "Eo");
					return;
				}
				if (P.getGremio() != null) {
					GestorSalida.GAME_SEND_gJ_PACKET(_perso, "Ea");
					return;
				}
				if (!_perso.getMiembroGremio().puede(CentroInfo.G_INVITAR)) {
					GestorSalida.GAME_SEND_gJ_PACKET(_perso, "Ed");
					return;
				}
				if (_perso.getGremio().getPjMiembros().size() >= (40 + _perso.getGremio().getNivel()))// Limite maximo de miembros
				{
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "155;" + (40 + _perso.getGremio().getNivel()));
					return;
				}
				_perso.setInvitado(P.getID());
				P.setInvitado(_perso.getID());
				GestorSalida.GAME_SEND_gJ_PACKET(_perso, "R" + packet.substring(1));
				GestorSalida.GAME_SEND_gJ_PACKET(P, "r" + _perso.getID() + "|" + _perso.getNombre() + "|"
						+ _perso.getGremio().getNombre());
				break;
			case 'E':// o rechazar
				if (_perso == null)
					return;
				if (_perso.getInvitado() == 0)
					return;
				GestorSalida.ENVIAR_BN_NADA(_out);
				Personaje t = MundoDofus.getPersonaje(_perso.getInvitado());
				if (t == null)
					return;
				GestorSalida.GAME_SEND_gJ_PACKET(_perso, "Ec");
				GestorSalida.GAME_SEND_gJ_PACKET(t, "Ec");
				t.setInvitado(0);
				_perso.setInvitado(0);
				break;
			case 'K':// Aceptar
				if (packet.substring(1).equalsIgnoreCase(_perso.getInvitado() + "")) {
					Personaje p = MundoDofus.getPersonaje(_perso.getInvitado());
					if (p == null)
						return;
					Gremio G = p.getGremio();
					if (G == null)
						return;
					MiembroGremio GM = G.addNuevoMiembro(_perso);
					GestorSQL.ACTUALIZAR_MIEMBRO_GREMIO(GM);
					_perso.setMiembroGremio(GM);
					_perso.setInvitado(-1);
					p.setInvitado(-1);
					GestorSalida.GAME_SEND_gJ_PACKET(p, "Ka" + _perso.getNombre());
					GestorSalida.GAME_SEND_gS_PACKET(_perso, GM);
					GestorSalida.GAME_SEND_gJ_PACKET(_perso, "Kj");
				}
				break;
		}
	}
	
	private void gremio_Informacion(char c) {
		Gremio gremio = _perso.getGremio();
		switch (c) {
			case 'B':// Recaudador
				GestorSalida.GAME_SEND_gIB_PACKET(_perso, gremio.analizarRecauAGrmio());
				break;
			case 'F':// Cercados
				GestorSalida.GAME_SEND_gIF_PACKET(_perso, gremio.analizarInfoCercados());
				break;
			case 'G':// General
				GestorSalida.GAME_SEND_gIG_PACKET(_perso, gremio);
				break;
			case 'H':// Casa
				GestorSalida.GAME_SEND_gIH_PACKET(_perso, Casa.analizarCasaGremio(_perso));
				break;
			case 'M':// Miembros
				GestorSalida.ENVIAR_gIM_INFO_MIEMBROS_GREMIO(_perso, gremio, '+');
				break;
			case 'T':// Recaudador
				GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(_perso, Recaudador.analizarRecaudadores(gremio.getID()));
				Recaudador.analizarAtaque(_perso, gremio.getID());
				Recaudador.analizarDefensa(_perso, gremio.getID());
				break;
		}
	}
	
	private void gremio_Crear(String packet) {
		if (_perso == null)
			return;
		if (_perso.getGremio() != null || _perso.getMiembroGremio() != null) {
			GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Ea");
			return;
		}
		if (_perso.getPelea() != null)
			return;
		try {
			String[] infos = packet.substring(2).split("\\|");
			String escudoId = Integer.toString(Integer.parseInt(infos[0]), 36);
			String colorEscudo = Integer.toString(Integer.parseInt(infos[1]), 36);
			String emblemaId = Integer.toString(Integer.parseInt(infos[2]), 36);
			String colorEmblema = Integer.toString(Integer.parseInt(infos[3]), 36);
			String nombre = infos[4];
			if (MundoDofus.nombreGremioUsado(nombre)) {
				GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Ean");
				return;
			}
			String tempName = nombre.toLowerCase();
			boolean esValido = true;
			if (tempName.length() > 20) {
				esValido = false;
			}
			if (esValido) {
				int tiretCount = 0;
				for (char curLetter : tempName.toCharArray()) {
					if (! ( (curLetter >= 'a' && curLetter <= 'z') || curLetter == '-')) {
						esValido = false;
						break;
					}
					if (curLetter == '-') {
						if (tiretCount >= 2) {
							esValido = false;
							break;
						} else {
							tiretCount++;
						}
					}
				}
			}
			if (!esValido) {
				GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Ean");
				return;
			}
			String emblema = escudoId + "," + colorEscudo + "," + emblemaId + "," + colorEmblema;
			if (MundoDofus.emblemaGremioUsado(emblema)) {
				GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Eae");
				return;
			}
			if (_perso.getMapa().getID() == 2196) {
				if (!_perso.tieneObjModeloNoEquip(1575, 1)) {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "14");
					return;
				}
				_perso.removerObjetoPorModYCant(1575, 1);
			}
			Gremio G = new Gremio(_perso, nombre, emblema);
			MiembroGremio gm = G.addNuevoMiembro(_perso);
			gm.setTodosDerechos(1, (byte) 0, 1);
			_perso.setMiembroGremio(gm);
			MundoDofus.addGremio(G, true);
			GestorSQL.ACTUALIZAR_MIEMBRO_GREMIO(gm);
			GestorSalida.GAME_SEND_gS_PACKET(_perso, gm);
			GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "K");
			GestorSalida.ENVIAR_gV_CERRAR_PANEL_GREMIO(_perso);
		} catch (Exception e) {
			return;
		}
	}
	
	private void analizar_Canal(String packet) {
		switch (packet.charAt(1)) {
			case 'C':// Cambio de canal
				canal_Cambiar(packet);
				break;
		}
	}
	
	private void canal_Cambiar(String packet) {
		String chan = packet.charAt(3) + "";
		switch (packet.charAt(2)) {
			case '+':// agregar Canal
				_perso.addCanal(chan);
				break;
			case '-':// desactivacion de canal
				_perso.removerCanal(chan);
				break;
		}
		GestorSQL.SALVAR_PERSONAJE(_perso, false);
	}
	
	private void analizar_Montura(String packet) {
		switch (packet.charAt(1)) {
			case 'b':// comprar cercados
				montura_Comprar_Cercado(packet);
				break;
			case 'd':// Manda descripcion
				montura_Descripcion(packet);
				break;
			case 'c':// castrar montura
				montura_Castrar();
				break;
			case 'p':// Manda descripcion
				montura_Descripcion(packet);
				break;
			case 'f':
				montura_Liberar();
				break;
			case 'n':// cambiar el nombre
				montura_Nombre(packet.substring(2));
				break;
			case 'o':// borrar objetos de crianza
				montura_Borrar_Objeto_Crianza(packet);
				break;
			case 'r':// montar el dragopavo
				montura_Montar();
				break;
			case 's':// Vender cercado
				montura_Vender_Cercado(packet);
				break;
			case 'v':// cerrar el panel de compra
				GestorSalida.GAME_SEND_R_PACKET(_perso, "v");
				break;
			case 'x':// Cambiar la experiencia donada al dragopavo
				montura_CambiarXP_Donada(packet);
				break;
		}
	}
	
	private void montura_Vender_Cercado(String packet) {
		GestorSalida.GAME_SEND_R_PACKET(_perso, "v");// cerrar panel
		int price = Integer.parseInt(packet.substring(2));
		Cercado MP1 = _perso.getMapa().getCercado();
		if (MP1.getDueño() == -1) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "194");
			return;
		}
		if (MP1.getDueño() != _perso.getID()) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "195");
			return;
		}
		MP1.setPrecio(price);
		GestorSQL.SALVAR_CERCADO(MP1);
		GestorSQL.SALVAR_PERSONAJE(_perso, true);
		for (Personaje z : _perso.getMapa().getPersos()) {
			GestorSalida.ENVIAR_Rp_INFORMACION_CERCADO(z, MP1);
		}
	}
	
	private void montura_Comprar_Cercado(String packet) {
		GestorSalida.GAME_SEND_R_PACKET(_perso, "v");
		Cercado cercado = _perso.getMapa().getCercado();
		Personaje vendedor = MundoDofus.getPersonaje(cercado.getDueño());
		if (cercado.getDueño() == -1) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "196");
			return;
		}
		if (cercado.getPrecio() == 0) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "197");
			return;
		}
		if (_perso.getGremio() == null) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1135");
			return;
		}
		if (_perso.getMiembroGremio().getRango() != 1) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "198");
			return;
		}
		byte cercadosMax = (byte) Math.floor(_perso.getGremio().getNivel() / 10);
		byte cercadosTotalGremio = GestorSQL.totalCercadosDelGremio(_perso.getGremio().getID());
		if (cercadosTotalGremio >= cercadosMax) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1103");
			return;
		}
		if (_perso.getKamas() < cercado.getPrecio()) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "182");
			return;
		}
		long nuevasKamas = _perso.getKamas() - cercado.getPrecio();
		_perso.setKamas(nuevasKamas);
		if (vendedor != null) {
			long NewSellerBankKamas = vendedor.getKamasBanco() + cercado.getPrecio();
			vendedor.setKamasBanco(NewSellerBankKamas);
			if (vendedor.enLinea()) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "El cercado esta vendido a " + cercado.getPrecio() + ".",
						Bustemu.COLOR_MENSAJE);
			}
		}
		cercado.setPrecio(0);
		cercado.setPropietario(_perso.getID());
		cercado.setGremio(_perso.getGremio());
		GestorSQL.SALVAR_CERCADO(cercado);
		GestorSQL.SALVAR_PERSONAJE(_perso, true);
		for (Personaje pj : _perso.getMapa().getPersos()) {
			GestorSalida.ENVIAR_Rp_INFORMACION_CERCADO(pj, cercado);
		}
	}
	
	private void montura_CambiarXP_Donada(String packet) {
		int xp = Integer.parseInt(packet.substring(2));
		if (xp < 0)
			xp = 0;
		if (xp > 90)
			xp = 90;
		_perso.setDonarXPMontura(xp);
		GestorSalida.ENVIAR_Rx_EXP_DONADA_MONTURA(_perso);
	}
	
	private void montura_Borrar_Objeto_Crianza(String packet) {
		int celda = Integer.parseInt(packet.substring(2));
		Mapa mapa = _perso.getMapa();
		if (mapa.getCercado() == null)
			return;
		Cercado cercado = mapa.getCercado();
		if (_perso.getNombre() != "Elbusta") {
			if (_perso.getGremio() == null) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			if (!_perso.getMiembroGremio().puede(8192)) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "193");
				return;
			}
		}
		if (cercado.delObjetoCria(celda)) {
			GestorSalida.ENVIAR_GDO_PONER_OBJETO_CRIA(mapa, celda + ";0;0");
			return;
		}
	}
	
	private void montura_Nombre(String nombre) {
		if (_perso == null)
			return;
		if (_perso.getMontura() == null)
			return;
		_perso.getMontura().setNombre(nombre);
		GestorSalida.ENVIAR_Rn_CAMBIO_NOMBRE_MONTURA(_perso, nombre);
	}
	
	private void montura_Montar() {
		if (_perso.getNivel() < 60 || _perso.getMontura() == null || _perso.getMontura().esMontable() == 0 || _perso.esFantasma()) {
			GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "Er", null);
			return;
		}
		_perso.subirBajarMontura();
	}
	
	private void montura_Castrar() {
		if (_perso.getMontura() == null) {
			GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "Er", null);
			return;
		}
		_perso.getMontura().castrarPavo();
		GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "+", _perso.getMontura());
	}
	
	private void montura_Liberar() {
		if (_perso.getMontura() == null) {
			GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "Er", null);
			return;
		}
		GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, "Lo sentimos pero esta opción aún no esta habilitada, gracias.");
	}
	
	private void montura_Descripcion(String packet) {
		int DPid = -1;
		try {
			DPid = Integer.parseInt(packet.substring(2).split("\\|")[0]);
		} catch (Exception e) {}
		if (DPid == -1)
			return;
		if (DPid > 0)
			DPid = -DPid;
		Dragopavo DD = MundoDofus.getDragopavoPorID(DPid);
		if (DD == null)
			return;
		GestorSalida.ENVIAR_Rd_DESCRIPCION_MONTURA(_perso, DD);
	}
	
	private void analizar_Amigos(String packet) {
		switch (packet.charAt(1)) {
			case 'A':// Agregar
				amigo_Agregar(packet);
				break;
			case 'D':// Borrar un amigo
				amigo_Borrar(packet);
				break;
			case 'L':// Lista
				GestorSalida.ENVIAR_FL_LISTA_DE_AMIGOS(_perso);
				break;
			case 'O':
				switch (packet.charAt(2)) {
					case '-':
						_perso.mostrarAmigosEnLinea(false);
						GestorSalida.ENVIAR_BN_NADA(_out);
						break;
					case '+':
						_perso.mostrarAmigosEnLinea(true);
						GestorSalida.ENVIAR_BN_NADA(_out);
						break;
				}
				break;
			case 'J': // Amante
				amigo_Esposo(packet);
				break;
		}
	}
	
	private void amigo_Esposo(String packet) {
		Personaje esposo = MundoDofus.getPersonaje(_perso.getEsposo());
		if (esposo == null)
			return;
		if (!esposo.enLinea()) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Vuestro amor ya no está en línea", Bustemu.COLOR_MENSAJE);
			GestorSalida.ENVIAR_FL_LISTA_DE_AMIGOS(_perso);
			return;
		}
		switch (packet.charAt(2)) {
			case 'S':
				if (_perso.getPelea() != null)
					return;
				else
					_perso.casarse(esposo);
				break;
			case 'C':
				if (packet.charAt(3) == '+') {
					if (_perso.getSiguiendo() != null) {
						_perso.getSiguiendo().getSeguidores().remove(_perso.getID());
					}
					GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(_perso, esposo);
					_perso.setSiguiendo(esposo);
					esposo.getSeguidores().put(_perso.getID(), _perso);
				} else {
					GestorSalida.ENVIAR_IC_BORRAR_BANDERA_COMPAS(_perso);
					_perso.setSiguiendo(null);
					esposo.getSeguidores().remove(_perso.getID());
				}
				break;
		}
	}
	
	private void amigo_Borrar(String packet) {
		if (_perso == null)
			return;
		int id = -1;
		switch (packet.charAt(2)) {
			case '%':// nombre de personaje
				packet = packet.substring(3);
				Personaje P = MundoDofus.getPjPorNombre(packet);
				if (P == null) {
					GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
					return;
				}
				id = P.getCuentaID();
				break;
			case '*':// Apodo
				packet = packet.substring(3);
				Cuenta C = MundoDofus.getCuentaPorApodo(packet);
				if (C == null) {
					GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
					return;
				}
				id = C.getID();
				break;
			default:
				packet = packet.substring(2);
				Personaje Pj = MundoDofus.getPjPorNombre(packet);
				if (Pj == null ? true : !Pj.enLinea()) {
					GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
					return;
				}
				id = Pj.getCuenta().getID();
				break;
		}
		if (id == -1 || !_cuenta.esAmigo(id)) {
			GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_perso, "Ef");
			return;
		}
		_cuenta.borrarAmigo(id);
	}
	
	private void amigo_Agregar(String packet) {
		if (_perso == null)
			return;
		int id = -1;
		switch (packet.charAt(2)) {
			case '%':// nombre de personaje
				packet = packet.substring(3);
				Personaje P = MundoDofus.getPjPorNombre(packet);
				if (P == null ? true : !P.enLinea()) {
					GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_perso, "Ef");
					return;
				}
				id = P.getCuentaID();
				break;
			case '*':// apodo
				packet = packet.substring(3);
				Cuenta C = MundoDofus.getCuentaPorApodo(packet);
				if (C == null ? true : !C.enLinea()) {
					GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_perso, "Ef");
					return;
				}
				id = C.getID();
				break;
			default:
				packet = packet.substring(2);
				Personaje Pj = MundoDofus.getPjPorNombre(packet);
				if (Pj == null ? true : !Pj.enLinea()) {
					GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_perso, "Ef");
					return;
				}
				id = Pj.getCuenta().getID();
				break;
		}
		if (id == -1) {
			GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_perso, "Ef");
			return;
		}
		_cuenta.addAmigo(id);
	}
	
	private void analizar_Grupo(String packet) {
		switch (packet.charAt(1)) {
			case 'A':// Aceptar invitacion
				grupo_Aceptar(packet);
				break;
			case 'F':
				Grupo g = _perso.getGrupo();
				if (g == null)
					return;
				int pId = -1;
				try {
					pId = Integer.parseInt(packet.substring(3));
				} catch (NumberFormatException e) {
					return;
				}
				if (pId == -1)
					return;
				Personaje perso = MundoDofus.getPersonaje(pId);
				if (perso == null || !perso.enLinea())
					return;
				if (packet.charAt(2) == '+') {
					if (_perso.getSiguiendo() != null) {
						_perso.getSiguiendo().getSeguidores().remove(_perso.getID());
					}
					GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(_perso, perso);
					GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE(_perso, "+" + perso.getID());
					_perso.setSiguiendo(perso);
					perso.getSeguidores().put(_perso.getID(), _perso);
				} else if (packet.charAt(2) == '-') {
					GestorSalida.ENVIAR_IC_BORRAR_BANDERA_COMPAS(_perso);
					GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE(_perso, "-");
					_perso.setSiguiendo(null);
					perso.getSeguidores().remove(_perso.getID());
				}
				break;
			case 'G':
				Grupo g2 = _perso.getGrupo();
				if (g2 == null)
					return;
				int pId2 = -1;
				try {
					pId2 = Integer.parseInt(packet.substring(3));
				} catch (NumberFormatException e) {
					return;
				}
				if (pId2 == -1)
					return;
				Personaje P2 = MundoDofus.getPersonaje(pId2);
				if (P2 == null || !P2.enLinea())
					return;
				if (packet.charAt(2) == '+') {
					for (Personaje integrante : g2.getPersos()) {
						if (integrante.getID() == P2.getID())
							continue;
						if (integrante.getSiguiendo() != null) {
							integrante.getSiguiendo().getSeguidores().remove(_perso.getID());
						}
						GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(integrante, P2);
						GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE(integrante, "+" + P2.getID());
						integrante.setSiguiendo(P2);
						P2.getSeguidores().put(integrante.getID(), integrante);
					}
				} else if (packet.charAt(2) == '-') {
					for (Personaje integrante : g2.getPersos()) {
						if (integrante.getID() == P2.getID())
							continue;
						GestorSalida.ENVIAR_IC_BORRAR_BANDERA_COMPAS(integrante);
						GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE(integrante, "-");
						integrante.setSiguiendo(null);
						P2.getSeguidores().remove(integrante.getID());
					}
				}
				break;
			case 'I':// invitacion
				grupo_Invitar(packet);
				break;
			case 'R':// Rechazar
				grupo_Rechazar();
				break;
			case 'V':// Quitar
				grupo_Expulsar(packet);
				break;
			case 'W':// Localizacion del grupo
				grupo_Localizar();
				break;
		}
	}
	
	private void grupo_Localizar() {
		if (_perso == null)
			return;
		Grupo g = _perso.getGrupo();
		if (g == null)
			return;
		String str = "";
		boolean primero = false;
		for (Personaje pj : g.getPersos()) {
			if (primero)
				str += "|";
			Mapa mapa = pj.getMapa();
			str += mapa.getX() + ";" + mapa.getY() + ";" + mapa.getID() + ";2;" + pj.getID() + ";" + pj.getNombre();
			primero = true;
		}
		GestorSalida.ENVIAR_IH_COORDINAS_UBICACION(_perso, str);
	}
	
	private void grupo_Expulsar(String packet) {
		if (_perso == null)
			return;
		Grupo grupo = _perso.getGrupo();
		if (grupo == null)
			return;
		if (packet.length() == 2) {
			grupo.dejarGrupo(_perso);
			GestorSalida.ENVIAR_PV_DEJAR_GRUPO(_out, "");
			GestorSalida.ENVIAR_IH_COORDINAS_UBICACION(_perso, "");
		} else if (grupo.esLiderGrupo(_perso.getID())) {
			int id = -1;
			try {
				id = Integer.parseInt(packet.substring(2));
			} catch (NumberFormatException e) {
				return;
			}
			if (id == -1)
				return;
			Personaje expulsado = MundoDofus.getPersonaje(id);
			if (expulsado == null)
				return;
			grupo.dejarGrupo(expulsado);
			if (expulsado.enLinea()) {
				GestorSalida.ENVIAR_PV_DEJAR_GRUPO(expulsado.getCuenta().getEntradaPersonaje().getOut(), "" + _perso.getID());
				GestorSalida.ENVIAR_IH_COORDINAS_UBICACION(expulsado, "");
			}
		}
	}
	
	private void grupo_Invitar(String packet) {
		if (_perso == null)
			return;
		String nombre = packet.substring(2);
		Personaje invitado = MundoDofus.getPjPorNombre(nombre);
		if (invitado == null)
			return;
		if (!invitado.enLinea()) {
			GestorSalida.ENVIAR_PIE_ERROR_INVITACION_GRUPO(_out, "n" + nombre);
			return;
		}
		if (invitado.getGrupo() != null) {
			GestorSalida.ENVIAR_PIE_ERROR_INVITACION_GRUPO(_out, "a" + nombre);
			return;
		}
		if (_perso.getGrupo() != null && _perso.getGrupo().getNumeroPjs() == 8) {
			GestorSalida.ENVIAR_PIE_ERROR_INVITACION_GRUPO(_out, "f");
			return;
		}
		invitado.setInvitado(_perso.getID());
		_perso.setInvitado(invitado.getID());
		GestorSalida.ENVIAR_PIK_INVITAR_GRUPO(_out, _perso.getNombre(), nombre);
		GestorSalida.ENVIAR_PIK_INVITAR_GRUPO(invitado.getCuenta().getEntradaPersonaje().getOut(), _perso.getNombre(), nombre);
	}
	
	private void grupo_Rechazar() {
		if (_perso == null)
			return;
		if (_perso.getInvitado() == 0)
			return;
		GestorSalida.ENVIAR_BN_NADA(_out);
		Personaje t = MundoDofus.getPersonaje(_perso.getInvitado());
		if (t == null)
			return;
		GestorSalida.ENVIAR_PR_RECHAZAR_INVITACION_GRUPO(t);
		t.setInvitado(0);
		_perso.setInvitado(0);
	}
	
	private void grupo_Aceptar(String packet) {
		if (_perso == null)
			return;
		if (_perso.getInvitado() == 0)
			return;
		Personaje invitado = MundoDofus.getPersonaje(_perso.getInvitado());
		if (invitado == null)
			return;
		Grupo grupo = invitado.getGrupo();
		try {
			if (grupo == null) {
				PrintWriter out = invitado.getCuenta().getEntradaPersonaje().getOut();
				grupo = new Grupo(invitado, _perso);
				GestorSalida.ENVIAR_PCK_CREAR_GRUPO(_out, grupo);
				GestorSalida.ENVIAR_PL_LIDER_GRUPO(_out, grupo);
				GestorSalida.ENVIAR_PCK_CREAR_GRUPO(out, grupo);
				GestorSalida.ENVIAR_PL_LIDER_GRUPO(out, grupo);
				invitado.setGrupo(grupo);
				GestorSalida.ENVIAR_PM_TODOS_MIEMBROS_GRUPO(out, grupo);
			} else {
				GestorSalida.ENVIAR_PCK_CREAR_GRUPO(_out, grupo);
				GestorSalida.ENVIAR_PL_LIDER_GRUPO(_out, grupo);
				GestorSalida.ENVIAR_PM_AGREGAR_PJ_GRUPO(grupo, _perso);
				grupo.addPerso(_perso);
			}
			_perso.setGrupo(grupo);
			GestorSalida.ENVIAR_PM_TODOS_MIEMBROS_GRUPO(_out, grupo);
			GestorSalida.ENVIAR_PR_RECHAZAR_INVITACION_GRUPO(invitado);
		} catch (NullPointerException e) {
			GestorSalida.ENVIAR_BN_NADA(_out);
		}
	}
	
	private void analizar_Objetos(String packet) {
		switch (packet.charAt(1)) {
			case 'd':// Supresion de un objeto
				objeto_Eliminar(packet);
				break;
			case 'D':// Dejar un objeto en el suelo
				objeto_Tirar(packet);
				break;
			case 'M':// mover un objeto (Equipar/desequipar)
				objeto_Mover(packet);
				break;
			case 'U':// Utilizar un objeto (pociones)
				objeto_Usar(packet);
				break;
			case 's':// cambiar Skin
				aparienciaObjevivo(packet);
				break;
			case 'f':// Alimentar, dar de comer
				alimentarObjevivo(packet);
				break;
			case 'x':// Desequipar
				desequiparObjevivo(packet);
				break;
		}
	}
	
	private synchronized void objeto_Tirar(String packet) {
		int id = -1;
		int cant = -1;
		try {
			id = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			cant = Integer.parseInt(packet.split("\\|")[1]);
		} catch (Exception e) {}
		if (id == -1 || cant <= 0 || !_perso.tieneObjetoID(id))
			return;
		Objeto obj = MundoDofus.getObjeto(id);
		if (obj == null)
			return;
		int idObjModelo = obj.getModelo().getID();
		if (idObjModelo == 10085) {
			_perso.borrarObjetoRemove(id);
			GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, id);
			MundoDofus.eliminarObjeto(id);
			return;
		}
		int celdaDrop = CentroInfo.getCeldaIDCercanaNoUsada(_perso);
		if (celdaDrop == 0) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "1145");
			return;
		}
		Celda celdaTirar = _perso.getMapa().getCelda(celdaDrop);
		if (cant >= obj.getCantidad()) {
			_perso.borrarObjetoRemove(id);
			celdaTirar.addObjetoTirado(obj, _perso);
			obj.setPosicion(-1);
			GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, id);
		} else {
			obj.setCantidad(obj.getCantidad() - cant);
			Objeto obj2 = Objeto.clonarObjeto(obj, cant);
			obj2.setPosicion(-1);
			MundoDofus.addObjeto(obj2, false);
			celdaTirar.addObjetoTirado(obj2, _perso);
			GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj);
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
		GestorSalida.ENVIAR_GDO_OBJETO_TIRADO_AL_SUELO(_perso.getMapa(), '+', celdaTirar.getID(), idObjModelo, 0);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
	}
	
	private synchronized void objeto_Usar(String packet) {
		int id = -1;
		int idPjObjetivo = -1;
		short celdaId = -1;
		Personaje pjObjetivo = null;
		try {
			String[] infos = packet.substring(2).split("\\|");
			id = Integer.parseInt(infos[0]);
			try {
				idPjObjetivo = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				idPjObjetivo = -1;
			}
			try {
				celdaId = Short.parseShort(infos[2]);
			} catch (Exception e) {
				celdaId = -1;
			}
		} catch (Exception e) {
			return;
		}
		if (MundoDofus.getPersonaje(idPjObjetivo) != null) {
			pjObjetivo = MundoDofus.getPersonaje(idPjObjetivo);
		}
		if (!_perso.tieneObjetoID(id))
			return;
		Objeto obj = MundoDofus.getObjeto(id);
		if (obj == null)
			return;
		ObjetoModelo objModeloBD = obj.getModelo();
		if (_perso.getPelea() != null)
			if (_perso.getPelea().getEstado() > 2 || (objModeloBD.getTipo() != CentroInfo.ITEM_TIPO_PAN)) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "191");
				return;
			}
		if (!objModeloBD.getCondiciones().equalsIgnoreCase("")
				&& !CondicionJugador.validaCondiciones(_perso, objModeloBD.getCondiciones())) {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "119|43");
			return;
		}
		objModeloBD.aplicarAccion(_perso, pjObjetivo, id, celdaId);
	}
	
	private void objeto_Eliminar(String packet) {
		String[] infos = packet.substring(2).split("\\|");
		try {
			int id = Integer.parseInt(infos[0]);
			int cant = 1;
			try {
				cant = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			Objeto obj = MundoDofus.getObjeto(id);
			if (obj == null || !_perso.tieneObjetoID(id) || cant <= 0) {
				GestorSalida.ENVIAR_OdE_ERROR_ELIMINAR_OBJETO(_out);
				return;
			}
			int nuevaCant = obj.getCantidad() - cant;
			if (nuevaCant <= 0) {
				_perso.borrarObjetoRemove(id);
				MundoDofus.eliminarObjeto(id);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, id);
			} else {
				obj.setCantidad(nuevaCant);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj);
			}
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
		} catch (Exception e) {
			GestorSalida.ENVIAR_OdE_ERROR_ELIMINAR_OBJETO(_out);
		}
	}
	
	private synchronized void objeto_Mover(String packet) {
		String[] infos = packet.substring(2).split("" + (char) 0x0A)[0].split("\\|");
		try {
			int cantObjMover;
			int idObjMover = Integer.parseInt(infos[0]);
			int posAMover = Integer.parseInt(infos[1]);
			try {
				cantObjMover = Integer.parseInt(infos[2]);
			} catch (Exception e) {
				cantObjMover = 1;
			}
			Objeto objMover = MundoDofus.getObjeto(idObjMover);
			if (!_perso.tieneObjetoID(idObjMover) || objMover == null)
				return;
			Pelea pelea = _perso.getPelea();
			if (pelea != null) {
				if (pelea.getEstado() > 2) {
					return;
				}
			}
			ObjetoModelo objetoMod = objMover.getModelo();
			if (objetoMod.getNivel() > _perso.getNivel()) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "13");
				return;
			}
			if (Bustemu.ARMAS_ENCARNACIONES.contains(objetoMod.getID())) {
				int segundos = Calendar.getInstance().get(Calendar.MINUTE) * 60 + Calendar.getInstance().get(Calendar.SECOND);
				if (_perso.getEncarnacion() == null && posAMover == 1) {
					Encarnacion encarnacion = MundoDofus.getEncarnacion(idObjMover);
					if (encarnacion == null) {
						encarnacion = new Encarnacion(idObjMover, CentroInfo.getClasePorObjMod(objetoMod.getID()), 1, 0,
								segundos, "");
						MundoDofus.addEncarnacion(encarnacion);
						GestorSQL.AGREGAR_ENCARNACION(encarnacion);
					} else if (!encarnacion.sePuedePoner(segundos)) {
						GestorSalida.ENVIAR_Im_INFORMACION(_out, "1166");
						return;
					}
					if (_perso.estaMontando())
						_perso.bajarMontura();
					_perso.setEncarnacion(encarnacion);
					_perso.setGfxID(encarnacion.getGfx());
					GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
					GestorSalida.ENVIAR_ASK_PERSONAJE_SELECCIONADO(_out, _perso);
					GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(_perso);
				} else if (_perso.getEncarnacion() != null && posAMover == -1) {
					_perso.getEncarnacion().setSegundos(segundos);
					_perso.deformar();
					_perso.setEncarnacion(null);
					GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_perso.getMapa(), _perso);
					GestorSalida.ENVIAR_ASK_PERSONAJE_SELECCIONADO(_out, _perso);
					GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(_perso);
				}
			}
			if (objetoMod.getTipo() == 18) {
				if (_perso.estaMontando()) {
					_perso.subirBajarMontura();
				}
				if (posAMover == 8 && _perso.getObjPosicion(8) == null) {
					if (objMover.getCantidad() > 1) {
						if (cantObjMover > objMover.getCantidad())
							cantObjMover = objMover.getCantidad();
						if (objMover.getCantidad() - cantObjMover > 0) {
							int nuevaCant = objMover.getCantidad() - cantObjMover;
							Objeto nuevoObj = Objeto.clonarObjeto(objMover, nuevaCant);
							if (!_perso.addObjetoSimilar(nuevoObj, true, idObjMover)) {
								MundoDofus.addObjeto(nuevoObj, true);
								_perso.addObjetoPut(nuevoObj);
								GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObj);
							}
							objMover.setCantidad(cantObjMover);
							GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objMover);
						}
					}
					objMover.setPosicion(8);
					GestorSalida.ENVIAR_OM_MOVER_OBJETO(_perso, objMover);
					equiparMascota(objMover);
					GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA(_perso.getMapa(), _perso);
					if (pelea != null)
						GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_PELEA(_perso, pelea);
					return;
				}
			}
			if (posAMover == 8 && _perso.getObjPosicion(8) != null) {
				alimentarMascota(objMover, _perso.getObjPosicion(8), cantObjMover);
				return;
			}
			if (posAMover == 16 && _perso.getMontura() != null) {
				if (CentroInfo.alimentoMontura(objetoMod.getTipo())) {
					if (objMover.getCantidad() > 0) {
						if (cantObjMover > objMover.getCantidad())
							cantObjMover = objMover.getCantidad();
						if (objMover.getCantidad() - cantObjMover > 0) {
							int nuevaCant = objMover.getCantidad() - cantObjMover;
							objMover.setCantidad(nuevaCant);
							GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objMover);
						} else {
							_perso.borrarObjetoRemove(idObjMover);
							MundoDofus.eliminarObjeto(idObjMover);
							GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, idObjMover);
						}
					}
					_perso.getMontura().aumEnergia(objMover.getModelo().getNivel(), cantObjMover);
					GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "+", _perso.getMontura());
					return;
				} else {
					GestorSalida.ENVIAR_Im_INFORMACION(_perso, "190");
					return;
				}
			}
			int idSetObjeto = objetoMod.getSetID();
			if ( ( (idSetObjeto >= 81 && idSetObjeto <= 92) || (idSetObjeto >= 201 && idSetObjeto <= 212))
					&& (posAMover == 2 || posAMover == 3 || posAMover == 4 || posAMover == 5 || posAMover == 6 || posAMover == 7 || posAMover == 0)) {
				String[] stats = objetoMod.getStringStatsObj().split(",");
				for (String stat : stats) {
					String[] val = stat.split("#");
					int efecto = Integer.parseInt(val[0], 16);
					int hechizo = Integer.parseInt(val[1], 16);
					int modif = Integer.parseInt(val[3], 16);
					String modificacion = efecto + ";" + hechizo + ";" + modif;
					GestorSalida.ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(_perso, modificacion);
					_perso.addHechizosSetClase(hechizo, efecto, modif);
				}
				_perso.agregarSetClase(objetoMod.getID());
			}
			if ( ( (idSetObjeto >= 81 && idSetObjeto <= 92) || (idSetObjeto >= 201 && idSetObjeto <= 212)) && posAMover == -1) {
				String[] stats = objetoMod.getStringStatsObj().split(",");
				for (String stat : stats) {
					String[] val = stat.split("#");
					String modificacion = Integer.parseInt(val[0], 16) + ";" + Integer.parseInt(val[1], 16) + ";0";
					GestorSalida.ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(_perso, modificacion);
					_perso.delHechizosSetClase(Integer.parseInt(val[1], 16));
				}
				_perso.borrarSetClase(objetoMod.getID());
			}
			if (objetoMod.getTipo() == 113) {
				if (_perso.getObjPosicion(posAMover) == null) {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "1161");
					return;
				} else {
					if (objMover.getCantidad() > 1) {
						if (cantObjMover > objMover.getCantidad())
							cantObjMover = objMover.getCantidad();
						if (objMover.getCantidad() - cantObjMover > 0) {
							int nuevaCant = objMover.getCantidad() - cantObjMover;
							Objeto nuevoObj = Objeto.clonarObjeto(objMover, nuevaCant);
							if (!_perso.addObjetoSimilar(nuevoObj, true, idObjMover)) {
								MundoDofus.addObjeto(nuevoObj, true);
								_perso.addObjetoPut(nuevoObj);
								GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObj);
							}
							objMover.setCantidad(cantObjMover);
							GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objMover);
						}
					}
					Objeto objet = _perso.getObjPosicion(posAMover);
					equiparObjevivo(objMover, objet);
				}
				return;
			}
			if (!CentroInfo.esUbicacionValidaObjeto(objetoMod, posAMover) && posAMover != -1) {
				return;
			}
			if (!objetoMod.getCondiciones().isEmpty() && posAMover != -1
					&& !CondicionJugador.validaCondiciones(_perso, objetoMod.getCondiciones())) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "119|43");
				return;
			}
			if (posAMover != -1 && objetoMod.getNivel() > _perso.getNivel()) {
				GestorSalida.ENVIAR_OAEL_ERROR_AGREGAR_OBJETO(_out);
				return;
			}
			if (posAMover != -1 && (idSetObjeto != -1 || objetoMod.getTipo() == 23) && _perso.tieneEquipado(objetoMod.getID()))
				return;
			if (posAMover != -1 && objetoMod.getTipo() == 23) {
				switch (objetoMod.getID()) {
					case 694:
						if (_perso.tieneEquipado(11012))
							return;
						else
							break;
					case 737:
						if (_perso.tieneEquipado(11007))
							return;
						else
							break;
					case 739:
						if (_perso.tieneEquipado(11013))
							return;
						else
							break;
					case 972:
						if (_perso.tieneEquipado(11008))
							return;
						else
							break;
					case 6980:
						if (_perso.tieneEquipado(11009))
							return;
						else
							break;
					case 7754:
						if (_perso.tieneEquipado(11011))
							return;
						else
							break;
					case 8072:
						if (_perso.tieneEquipado(11010))
							return;
						else
							break;
					case 11012:
						if (_perso.tieneEquipado(694))
							return;
						else
							break;
					case 11007:
						if (_perso.tieneEquipado(737))
							return;
						else
							break;
					case 11013:
						if (_perso.tieneEquipado(739))
							return;
						else
							break;
					case 11008:
						if (_perso.tieneEquipado(972))
							return;
						else
							break;
					case 11009:
						if (_perso.tieneEquipado(6980))
							return;
						else
							break;
					case 11011:
						if (_perso.tieneEquipado(7754))
							return;
						else
							break;
					case 11010:
						if (_perso.tieneEquipado(8072))
							return;
						else
							break;
				}
			}
			Objeto exObj = _perso.getObjPosicion(posAMover);
			if (exObj != null) {
				Objeto obj2;
				ObjetoModelo exObjModelo = exObj.getModelo();
				int idSetExObj = exObj.getModelo().getSetID();
				if ( (obj2 = _perso.getObjSimilarInventario(exObj)) != null) {
					obj2.setCantidad(obj2.getCantidad() + exObj.getCantidad());
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj2);
					MundoDofus.eliminarObjeto(exObj.getID());
					_perso.borrarObjetoRemove(exObj.getID());
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, exObj.getID());
				} else {
					exObj.setPosicion(-1);
					if ( (idSetExObj >= 81 && idSetExObj <= 92) || (idSetExObj >= 201 && idSetExObj <= 212)) {
						String[] stats = exObjModelo.getStringStatsObj().split(",");
						for (String stat : stats) {
							String[] val = stat.split("#");
							String modificacion = Integer.parseInt(val[0], 16) + ";" + Integer.parseInt(val[1], 16) + ";0";
							GestorSalida.ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(_perso, modificacion);
							_perso.delHechizosSetClase(Integer.parseInt(val[1], 16));
						}
						_perso.borrarSetClase(exObjModelo.getID());
					}
					GestorSalida.ENVIAR_OM_MOVER_OBJETO(_perso, exObj);
				}
				if (_perso.getObjPosicion(1) == null) {
					GestorSalida.ENVIAR_OT_OBJETO_HERRAMIENTA(_out, -1);
					if (_perso.getMapa().esTaller() && _perso.getOficioPublico())
						GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_out, "-", _perso.getID(), "");
					_perso.setStrOficiosPublicos("");
				}
				if (idSetExObj > 0)
					GestorSalida.ENVIAR_OS_BONUS_SET(_perso, idSetExObj, -1);
			} else {
				Objeto obj2;
				if ( (obj2 = _perso.getObjSimilarInventario(objMover)) != null) {
					if (cantObjMover > objMover.getCantidad())
						cantObjMover = objMover.getCantidad();
					obj2.setCantidad(obj2.getCantidad() + cantObjMover);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj2);
					if (objMover.getCantidad() - cantObjMover > 0) {
						objMover.setCantidad(objMover.getCantidad() - cantObjMover);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objMover);
					} else {
						MundoDofus.eliminarObjeto(objMover.getID());
						_perso.borrarObjetoRemove(objMover.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, objMover.getID());
					}
				} else {
					objMover.setPosicion(posAMover);
					if (objetoMod.getTipo() == 18 && posAMover == -1) {
						_perso.setMascota(null);
					}
					GestorSalida.ENVIAR_OM_MOVER_OBJETO(_perso, objMover);
					if (objMover.getCantidad() > 1) {
						if (cantObjMover > objMover.getCantidad())
							cantObjMover = objMover.getCantidad();
						if (objMover.getCantidad() - cantObjMover > 0) {
							int nuevaCant = objMover.getCantidad() - cantObjMover;
							Objeto nuevoObj = Objeto.clonarObjeto(objMover, nuevaCant);
							if (!_perso.addObjetoSimilar(nuevoObj, true, idObjMover)) {
								MundoDofus.addObjeto(nuevoObj, true);
								_perso.addObjetoPut(nuevoObj);
								GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObj);
							}
							objMover.setCantidad(cantObjMover);
							GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objMover);
						}
					}
				}
			}
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
			_perso.refrescarVida();
			if (_perso.getGrupo() != null) {
				GestorSalida.ENVIAR_PM_ACTUALIZAR_INFO_PJ_GRUPO(_perso.getGrupo(), _perso);
			}
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			if (posAMover == 1 || posAMover == 6 || posAMover == 7 || posAMover == 15 || posAMover == -1)
				GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA(_perso.getMapa(), _perso);
			Objeto arma = null;
			if (posAMover == -1 && _perso.getObjPosicion(1) == null) {
				GestorSalida.ENVIAR_OT_OBJETO_HERRAMIENTA(_out, -1);
				if (_perso.getMapa().esTaller() && _perso.getOficioPublico())
					GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_out, "-", _perso.getID(), "");
				_perso.setStrOficiosPublicos("");
			} else if (posAMover == 1 && (arma = _perso.getObjPosicion(1)) != null) {
				int idModArma = arma.getModelo().getID();
				for (Entry<Integer, StatsOficio> statOficio : _perso.getStatsOficios().entrySet()) {
					Oficio oficio = statOficio.getValue().getOficio();
					if (oficio.herramientaValida(idModArma)) {
						GestorSalida.ENVIAR_OT_OBJETO_HERRAMIENTA(_out, oficio.getID());
						String strOficioPub = CentroInfo.trabajosOficioTaller(oficio.getID());
						if (_perso.getMapa().esTaller() && _perso.getOficioPublico())
							GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_out, "+", _perso.getID(), strOficioPub);
						_perso.setStrOficiosPublicos(strOficioPub);
						break;
					}
				}
			}
			if (idSetObjeto > 0)
				GestorSalida.ENVIAR_OS_BONUS_SET(_perso, idSetObjeto, -1);
			if (pelea != null) {
				GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_PELEA(_perso, pelea);
			}
		} catch (Exception e) {
			GestorSalida.ENVIAR_OdE_ERROR_ELIMINAR_OBJETO(_out);
		}
	}
	
	private synchronized void aparienciaObjevivo(String packet) {
		try {
			int idObjeto = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			Objeto objeto = MundoDofus.getObjeto(idObjeto);
			if (objeto == null) {
				GestorSalida
						.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								"<b>[Elbusta]</b> El objeto no existe o esta bugeado, porfavor cambia de personaje para evitar más errores.");
				return;
			}
			int objeviId = objeto.getObjeviID();
			Objevivo objevi = MundoDofus.getObjevivos(objeviId);
			if (objevi == null) {
				GestorSalida
						.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								"<b>[Elbusta]</b> El objeto no existe o esta bugeado, porfavor cambia de personaje para evitar más errores.");
				return;
			}
			int aparienciaId = Integer.parseInt(packet.split("\\|")[2]);
			objevi.setSkin(aparienciaId);
			GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_out, objeto);
			GestorSalida.ENVIAR_Oa_ACTUALIZAR_FIGURA_PJ(_perso);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print("apariencia objevivo " + e.getMessage());
		}
	}
	
	private synchronized void alimentarObjevivo(String packet) {
		try {
			int idObjeto = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			Objeto objeto = MundoDofus.getObjeto(idObjeto);
			int idObjAlimento = Integer.parseInt(packet.split("\\|")[2]);
			Objeto objetoAlimento = MundoDofus.getObjeto(idObjAlimento);
			if (objetoAlimento == null || objeto == null) {
				GestorSalida
						.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								"<b>[Elbusta]</b> El objeto no existe o esta bugeado, porfavor cambia de personaje para evitar más errores.");
				return;
			}
			Objevivo objevi = MundoDofus.getObjevivos(objeto.getObjeviID());
			if (objevi == null) {
				GestorSalida
						.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								"<b>[Elbusta]</b> El objeto no existe o esta bugeado, porfavor cambia de personaje para evitar más errores.");
				return;
			}
			if (objetoAlimento.getCantidad() > 1) {
				if (objetoAlimento.getCantidad() - 1 > 0) {
					int nuevaCant = objetoAlimento.getCantidad() - 1;
					Objeto nuevoObj = Objeto.clonarObjeto(objetoAlimento, nuevaCant);
					if (!_perso.addObjetoSimilar(nuevoObj, true, idObjAlimento)) {
						MundoDofus.addObjeto(nuevoObj, true);
						_perso.addObjetoPut(nuevoObj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObj);
					}
					objetoAlimento.setCantidad(1);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objetoAlimento);
				}
			}
			long xp = Long.parseLong(Integer.toString(objetoAlimento.getModelo().getNivel()));
			objevi.setXp(objevi.getXp() + xp);
			MundoDofus.eliminarObjeto(idObjAlimento);
			_perso.borrarObjetoRemove(idObjAlimento);
			GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, idObjAlimento);
			GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_out, objeto);
			GestorSalida.ENVIAR_Oa_ACTUALIZAR_FIGURA_PJ(_perso);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print("alimentar objevivo " + e.getMessage());
		}
	}
	
	private synchronized void equiparObjevivo(Objeto objevivo, Objeto objeto) {
		try {
			for (Objevivo objevis : MundoDofus.getTodosObjevivos()) {
				if ( (objevis.getItemID() == objevivo.getID()) && (objevis.getAsociado() == 0)) {
					objeto.setObjeviID(objevis.getID());
					objevis.setStat(objeto.convertirStatsAString());
					objevis.setItemId(objeto.getID());
					objevis.setAsociado(1);
					_perso.borrarObjetoRemove(objevivo.getID());
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, objevivo.getID());
					GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_out, objeto);
					GestorSalida.ENVIAR_Oa_ACTUALIZAR_FIGURA_PJ(_perso);
					return;
				}
			}
			int sigId = MundoDofus.getSigIDParaObjevivo();
			String fecha = Calendar.getInstance().get(Calendar.MONTH) + "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			String fecharInter = Calendar.getInstance().get(Calendar.MONTH + 3) + ""
					+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			String tiempo = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + "" + Calendar.getInstance().get(Calendar.MINUTE);
			Objevivo nuevoObjevivo = new Objevivo(sigId, 2012, Integer.parseInt(fecha), Integer.parseInt(tiempo), 1, 1, objeto
					.getModelo().getTipo(), objeto.getID(), 0, 2012, Integer.parseInt(fecharInter), Integer.parseInt(tiempo),
					2012, Integer.parseInt(fecha), Integer.parseInt(tiempo), 1, objevivo.getModelo().getID(), objevivo.getID(),
					objeto.convertirStatsAString());
			MundoDofus.addObjevivo(nuevoObjevivo);
			GestorSQL.AGREGAR_OBJEVIVOS(nuevoObjevivo);
			objeto.setObjeviID(sigId);
			_perso.borrarObjetoRemove(objevivo.getID());
			GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, objevivo.getID());
			GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_out, objeto);
			GestorSalida.ENVIAR_Oa_ACTUALIZAR_FIGURA_PJ(_perso);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print("equipar objevivo " + e.getMessage());
		}
	}
	
	private synchronized void desequiparObjevivo(String packet) {
		try {
			int id = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			Objeto objeto = MundoDofus.getObjeto(id);
			int idObjevivo = objeto.getObjeviID();
			Objevivo objevivo = MundoDofus.getObjevivos(idObjevivo);
			if (objevivo == null || objeto == null) {
				GestorSalida
						.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
								"<b>[Elbusta]</b> El objeto no existe o esta bugeado, porfavor cambia de personaje para evitar más errores.");
				return;
			}
			if (objevivo.getAsociado() == 1) {
				int idObjObjevivo = objevivo.getItemObjevivo();// adquiere el valor de item objevivo
				Objeto objObjevivo = MundoDofus.getObjeto(idObjObjevivo);
				_perso.addObjetoPut(objObjevivo);// se agrega a personaje el objeto objevivo
				objeto.setObjeviID(0);
				if (objObjevivo == null)
					return;
				objObjevivo.setObjeviID(idObjevivo);// le da al item objevivo el id del objevivo
				objevivo.setItemId(idObjObjevivo);// le da como item asociado el id del item objevivo
				objevivo.setAsociado(0);// le da valor 0 a asociado del objevivo
				GestorSQL.ACTUALIZAR_STATS_OBJETO(objeto, objevivo.getStat());
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, objObjevivo);
				GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_out, objeto);
				GestorSalida.ENVIAR_Oa_ACTUALIZAR_FIGURA_PJ(_perso);
				Thread.sleep(200);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print("desequipar objevivo " + e.getMessage());
		}
	}
	
	private synchronized void equiparMascota(Objeto objeto) {
		for (Mascota mascota : MundoDofus.getTodasMascotas()) {
			if (objeto.getID() == mascota.getID()) {
				_perso.setMascota(mascota);
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
				return;
			}
		}
		Calendar calendar = Calendar.getInstance();
		int mes = calendar.get(Calendar.MONTH) + 1;
		int dia = calendar.get(Calendar.DAY_OF_MONTH);
		int hora = calendar.get(Calendar.HOUR_OF_DAY);
		int minuto = calendar.get(+Calendar.MINUTE);
		Mascota mascota = new Mascota(objeto.getID(), 10, "", 0, 2012, mes, dia, hora, minuto, -1, "", 0, 0, objeto.getModelo()
				.getID());
		MundoDofus.addMascota(mascota);
		GestorSQL.AGREGAR_MASCOTA(mascota);
		GestorSalida.ENVIAR_Oa_ACTUALIZAR_FIGURA_PJ(_perso);
		GestorSQL.SALVAR_PERSONAJE(_perso, true);
		_perso.setMascota(mascota);
	}
	
	private synchronized void alimentarMascota(Objeto comida, Objeto masc, int cantidad) {
		try {
			Mascota mascota = MundoDofus.getMascota(masc.getID());
			int idModComida = comida.getModelo().getID();
			if (idModComida == 11045) {
				if (mascota.getPDV() < 10)
					mascota.setPDV(mascota.getPDV() + 1);
				if (comida.getCantidad() > 0) {
					if (cantidad > comida.getCantidad())
						cantidad = comida.getCantidad();
					if (comida.getCantidad() - cantidad > 0) {
						int nuevaCant = comida.getCantidad() - cantidad;
						comida.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, comida);
					} else {
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, comida.getID());
						_perso.borrarObjetoRemove(comida.getID());
						MundoDofus.eliminarObjeto(comida.getID());
					}
				}
				masc.clearTodo();
				masc.convertirStringAStats(mascota.getStringStats());
				GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_out, masc);
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
			} else if (!mascota.esDevoraAlmas() && (idModComida >= 11170 && idModComida <= 11184)
					|| mascota.esComestible(idModComida)) {
				if (comida.getCantidad() - cantidad > 0) {
					int nuevaCant = comida.getCantidad() - cantidad;
					comida.setCantidad(nuevaCant);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, comida);
				} else {
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, comida.getID());
					_perso.borrarObjetoRemove(comida.getID());
					MundoDofus.eliminarObjeto(comida.getID());
				}
				if ( (idModComida >= 11170 && idModComida <= 11184) || mascota.horaComer()) {
					mascota.comerComida(idModComida);
					masc.clearTodo();
					masc.convertirStringAStats(mascota.getStringStats());
					GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_out, masc);
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "032");
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
				} else {
					if (mascota.getObeso())
						_perso.restarVidaMascota(mascota);
					mascota.setCorpulencia(1);
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "026");
				}
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "153");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print("alimentar mascota " + e.getMessage());
		}
	}
	
	private void analizar_Dialogos(String packet) {
		switch (packet.charAt(1)) {
			case 'C':// Demanda la pregunta
				dialogo_Iniciar(packet);
				break;
			case 'R':// Respuesta del jugador
				dialogo_Respuesta(packet);
				break;
			case 'V':// Fin del dialogo
				dialogo_Fin();
				break;
		}
	}
	
	private void dialogo_Respuesta(String packet) {
		String[] infos = packet.substring(2).split("\\|");
		try {
			int preguntaID = Integer.parseInt(infos[0]);
			int respuestaID = Integer.parseInt(infos[1]);
			PreguntaNPC pregunta = MundoDofus.getNPCPregunta(preguntaID);
			RespuestaNPC respuesta = MundoDofus.getNPCreponse(respuestaID);
			if (pregunta == null || respuesta == null || !respuesta.esOtroDialogo()) {
				GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(_out);
				_perso.setConversandoCon(0);
			}
			respuesta.aplicar(_perso);
		} catch (Exception e) {
			GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(_out);
		}
	}
	
	private void dialogo_Fin() {
		GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(_out);
		if (_perso.getConversandoCon() != 0)
			_perso.setConversandoCon(0);
	}
	
	private void dialogo_Iniciar(String packet) {
		try {
			int ID = Integer.parseInt(packet.substring(2).split((char) 0x0A + "")[0]);
			if (ID > -50) {
				int npcID = ID;
				NPC npc = _perso.getMapa().getNPC(npcID);
				if (npc == null)
					return;
				GestorSalida.ENVIAR_DCK_CREAR_DIALOGO(_out, npcID);
				int pID = npc.getModeloBD().getPreguntaID();
				PreguntaNPC pregunta = MundoDofus.getNPCPregunta(pID);
				if (pregunta == null) {
					GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(_out);
					return;
				}
				GestorSalida.ENVIAR_DQ_DIALOGO_PREGUNTA(_out, pregunta.stringArgParaDialogo(_perso));
				_perso.setConversandoCon(npcID);
			} else {
				Recaudador recauda = MundoDofus.getRecaudador(ID);
				if (recauda == null)
					return;
				GestorSalida.ENVIAR_DCK_CREAR_DIALOGO(_out, ID);
				PreguntaNPC pregunta = MundoDofus.getNPCPregunta(1);
				if (pregunta == null) {
					GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(_out);
					return;
				}
				Gremio gremio = MundoDofus.getGremio(recauda.getGremioID());
				GestorSalida.ENVIAR_DQ_DIALOGO_PREGUNTA(_out, pregunta.stringGremio(_perso, gremio));
				_perso.setConversandoCon(ID);
			}
		} catch (NumberFormatException e) {}
	}
	
	private void analizar_Intercambios(String packet) {
		switch (packet.charAt(1)) {
			case 'A':// aceptar abrir la ventana de intercambio
				intercambio_Aceptar();
				break;
			case 'B':// compra de un item
				intercambio_Comprar(packet);
				break;
			case 'f':// montura a criar
				intercambio_Cercado(packet);
				break;
			case 'H':// Demanda precio recurso + categoria
				intercambio_Mercadillo(packet);
				break;
			case 'J':// oficios
				intercambio_Oficios(packet);
				break;
			case 'K':// Ok
				intercambio_Ok();
				break;
			case 'L':// oficio : repetir el craft antecedente
				intercambio_Repetir();
				break;
			case 'M':// Mover (agregar retirar un objeto al intercambio)
				intercambio_Mover_Objeto(packet);
				break;
			case 'q':// pregunta si desea entrar a modo mercante
				intercambio_Preg_Mercante();
				break;
			case 'P':
				intercambio_Pago_Por_Trabajo(packet);
				break;
			case 'Q':
				intercambio_Ok_Mercante();
				break;
			case 'r':// Montura
				intercambio_Establo(packet);
				break;
			case 'R':// iniciar
				intercambio_Iniciar(packet);
				break;
			case 'S':// Venta
				intercambio_Vender(packet);
				break;
			case 'V':// Fin del intercambio
				intercambio_Cerrar();
				break;
			case 'W':// oficio modo publico
				intercambio_Oficio_Publico(packet);
				break;
		}
	}
	
	private void intercambio_Oficios(String packet) {
		switch (packet.charAt(2)) {
			case 'F':
				int Oficio = Integer.parseInt(packet.substring(3));
				for (Personaje artesano : MundoDofus.getPJsEnLinea()) {
					if (artesano.getStatsOficios().isEmpty())
						continue;
					String enviar = "";
					int id = artesano.getID();
					String nombre = artesano.getNombre();
					String colores = artesano.getColor1() + "," + artesano.getColor2() + "," + artesano.getColor3();
					String accesorios = artesano.getStringAccesorios();
					int sexo = artesano.getSexo();
					int mapa = artesano.getMapa().getID();
					int entaller = (mapa == 8731 || mapa == 8732) ? 1 : 0;
					int clase = artesano.getClase(true);
					for (StatsOficio oficio : artesano.getStatsOficios().values()) {
						if (oficio.getOficio().getID() != Oficio)
							continue;
						enviar = "+" + oficio.getOficio().getID() + ";" + id + ";" + nombre + ";" + oficio.getNivel() + ";"
								+ mapa + ";" + entaller + ";" + clase + ";" + sexo + ";" + colores + ";" + accesorios + ";"
								+ oficio.getOpcionBin() + "," + oficio.getSlotsPublico();
						GestorSalida.ENVIAR_EJ_DESCRIPCION_LIBRO_ARTESANO(_perso, enviar);
					}
				}
				break;
		}
	}
	
	private void intercambio_Oficio_Publico(String packet) {
		switch (packet.charAt(2)) {
			case '+':
				_perso.setOficioPublico(true);
				for (StatsOficio oficio : _perso.getStatsOficios().values()) {
					int idModOficio = oficio.getOficio().getID();
					GestorSalida.ENVIAR_Ej_AGREGAR_LIBRO_ARTESANO(_perso, "+" + idModOficio);
				}
				GestorSalida.ENVIAR_EW_OFICIO_MODO_PUBLICO(_out, "+");
				if (_perso.getMapa().esTaller())
					GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_out, "+", _perso.getID(), _perso.getStringOficiosPublicos());
				break;
			case '-':
				_perso.setOficioPublico(false);
				for (StatsOficio oficio : _perso.getStatsOficios().values()) {
					GestorSalida.ENVIAR_Ej_AGREGAR_LIBRO_ARTESANO(_perso, "-" + oficio.getOficio().getID());
				}
				GestorSalida.ENVIAR_EW_OFICIO_MODO_PUBLICO(_out, "-");
				GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_out, "-", _perso.getID(), "");
				break;
		}
	}
	
	private void intercambio_Mover_Objeto(String packet) {
		if (_perso.getTallerInvitado() != null) {
			InvitarTaller taller = _perso.getTallerInvitado();
			switch (packet.charAt(2)) {
				case 'O':
					if (packet.charAt(3) == '+') {
						String[] infos = packet.substring(4).split("\\|");
						int id = -1;
						int cant = -1;
						try {
							id = Integer.parseInt(infos[0]);
							cant = Integer.parseInt(infos[1]);
						} catch (NumberFormatException e) {}
						if (id == -1 || cant == -1)
							return;
						try {
							int cantInter = taller.getCantObjeto(id, _perso.getID());
							if (!_perso.tieneObjetoID(id))
								return;
							Objeto obj = MundoDofus.getObjeto(id);
							if (obj == null)
								return;
							int nuevaCant = obj.getCantidad() - cantInter;
							if (cant > nuevaCant)
								cant = nuevaCant;
							taller.addObjeto(obj, cant, _perso.getID());
						} catch (NullPointerException e) {}
					} else {
						String[] infos = packet.substring(4).split("\\|");
						try {
							int id = Integer.parseInt(infos[0]);
							int cant = Integer.parseInt(infos[1]);
							if (cant <= 0)
								return;
							if (!_perso.tieneObjetoID(id))
								return;
							Objeto obj = MundoDofus.getObjeto(id);
							if (obj == null)
								return;
							int cantInter = taller.getCantObjeto(id, _perso.getID());
							if (cant > cantInter)
								cant = cantInter;
							taller.borrarObjeto(obj, cant, _perso.getID());
						} catch (NumberFormatException e) {}
					}
					break;
			}
			return;
		} else if (_perso.getRecaudando()) {
			Recaudador recaudador = MundoDofus.getRecaudador(_perso.getRecaudandoRecauID());
			if (recaudador == null || recaudador.getEstadoPelea() > 0)
				return;
			switch (packet.charAt(2)) {
				case 'G':// Kamas
					if (packet.charAt(3) == '-') {
						long kamas = Integer.parseInt(packet.substring(4));
						long kamasRetiradas = recaudador.getKamas() - kamas;
						if (kamasRetiradas < 0) {
							kamasRetiradas = 0;
							kamas = recaudador.getKamas();
						}
						recaudador.setKamas(kamasRetiradas);
						_perso.addKamas(kamas);
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
						GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(_perso, "G" + recaudador.getKamas());
					}
					break;
				case 'O':// Objetos
					if (packet.charAt(3) == '-') {
						String[] infos = packet.substring(4).split("\\|");
						int id = 0;
						int cant = 0;
						try {
							id = Integer.parseInt(infos[0]);
							cant = Integer.parseInt(infos[1]);
						} catch (NumberFormatException e) {}
						if (id <= 0 || cant <= 0)
							return;
						Objeto obj = MundoDofus.getObjeto(id);
						if (obj == null)
							return;
						if (recaudador.tieneObjeto(id)) {
							recaudador.borrarDesdeRecaudador(_perso, id, cant);
						}
					}
					break;
			}
			_perso.getGremio().addXp(recaudador.getXp());
			recaudador.setXp(0);
			GestorSQL.ACTUALIZAR_GREMIO(_perso.getGremio());
			return;
		} else if (_perso.getRompiendo()) {
			if (packet.charAt(2) == 'O') {
				if (packet.charAt(3) == '+') {
					if (_perso.getObjetoARomper() == 0) {
						String[] Infos = packet.substring(4).split("\\|");
						try {
							int id = Integer.parseInt(Infos[0]);
							int cantidad = 1;
							if (!_perso.tieneObjetoID(id))
								return;
							Objeto Obj = MundoDofus.getObjeto(id);
							if (Obj == null)
								return;
							if (Obj.getModelo().getTipo() == 18) {
								GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, "No es posible romper una mascota.");
								return;
							}
							_perso.setObjetoARomper(id);
							GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "+", id + "|" + cantidad);
						} catch (NumberFormatException e) {}
					} else {
						String[] Infos = packet.substring(4).split("\\|");
						try {
							int ultimo = _perso.getObjetoARomper();
							GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "-", ultimo + "|1");
							int id = Integer.parseInt(Infos[0]);
							int cantidad = 1;
							if (!_perso.tieneObjetoID(id))
								return;
							Objeto Obj = MundoDofus.getObjeto(id);
							if (Obj == null)
								return;
							_perso.setObjetoARomper(id);
							GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "+", id + "|" + cantidad);
						} catch (NumberFormatException e) {}
					}
				} else if (packet.charAt(3) == '-') {
					String[] Infos = packet.substring(4).split("\\|");
					try {
						int id = Integer.parseInt(Infos[0]);
						int cantidad = Integer.parseInt(Infos[1]);
						Objeto Obj = MundoDofus.getObjeto(id);
						if (Obj == null)
							return;
						GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "-", id + "|" + cantidad);
						_perso.setObjetoARomper(0);
					} catch (NumberFormatException e) {}
				}
			}
			return;
		} else if (_perso.getIntercambiandoCon() < 0 && !_perso.getMochilaMontura() && !_perso.getRompiendo()) {// HDV
			switch (packet.charAt(3)) {
				case '-':
					int cheapestID = Integer.parseInt(packet.substring(4).split("\\|")[0]);
					int cant = Integer.parseInt(packet.substring(4).split("\\|")[1]);
					if (cant <= 0)
						return;
					_perso.getCuenta().recuperarObjeto(cheapestID, cant);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, '-', "", cheapestID + "");
					break;
				case '+':// Poner un objeto en venta
					int objetoID = Integer.parseInt(packet.substring(4).split("\\|")[0]);
					int cantidad = Integer.parseInt(packet.substring(4).split("\\|")[1]);
					int precio = 0;
					try {
						precio = Integer.parseInt(packet.substring(4).split("\\|")[2]);
					} catch (ArrayIndexOutOfBoundsException e) {
						precio = 0;
					}
					if (cantidad <= 0 || precio <= 0)
						return;
					PuestoMercadillo puesto = MundoDofus.getPuestoMerca(Math.abs(_perso.getIntercambiandoCon()));
					int porcentaje = (int) (precio * (puesto.getPorcentaje() / 100));
					if (!_perso.tieneObjetoID(objetoID))
						return;
					if (_perso.getCuenta().cantidadObjMercadillo(puesto.getIDMercadillo()) >= puesto.getMaxObjCuenta()) {
						GestorSalida.ENVIAR_Im_INFORMACION(_out, "058");
						return;
					}
					if (_perso.getKamas() < porcentaje) {
						GestorSalida.ENVIAR_Im_INFORMACION(_out, "176");
						return;
					}
					_perso.addKamas(porcentaje * -1);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
					Objeto obj = MundoDofus.getObjeto(objetoID);
					if (cantidad > obj.getCantidad())
						return;
					int cantReal = (int) (Math.pow(10, cantidad) / 10);
					int nuevaCant = (obj.getCantidad() - cantReal);
					if (nuevaCant <= 0) {
						_perso.borrarObjetoRemove(objetoID);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, objetoID);
					} else {
						obj.setCantidad(obj.getCantidad() - cantReal);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj);
						Objeto nuevoObj = Objeto.clonarObjeto(obj, cantReal);
						MundoDofus.addObjeto(nuevoObj, true);
						obj = nuevoObj;
					}
					ObjetoMercadillo objMerca = new ObjetoMercadillo(precio, cantidad, _perso.getCuenta().getID(), obj);
					puesto.addObjMercaAlPuesto(objMerca);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, '+', "", objMerca.analizarParaEmK());
					break;
			}
		} else if (_perso.getHaciendoTrabajo() != null) {
			if (!_perso.getHaciendoTrabajo().esReceta())
				return;
			if (packet.charAt(2) == 'O') {
				if (packet.charAt(3) == '+') {
					String[] infos = packet.substring(4).split("\\|");
					try {
						int id = Integer.parseInt(infos[0]);
						int cantidad = Integer.parseInt(infos[1]);
						if (cantidad <= 0)
							return;
						if (!_perso.tieneObjetoID(id))
							return;
						Objeto obj = MundoDofus.getObjeto(id);
						if (obj == null)
							return;
						if (obj.getCantidad() < cantidad)
							cantidad = obj.getCantidad();
						_perso.getHaciendoTrabajo().modificarIngrediente(_out, id, cantidad);
					} catch (NumberFormatException e) {}
				} else {
					String[] infos = packet.substring(4).split("\\|");
					try {
						int id = Integer.parseInt(infos[0]);
						int cantidad = Integer.parseInt(infos[1]);
						if (cantidad <= 0)
							return;
						Objeto obj = MundoDofus.getObjeto(id);
						if (obj == null)
							return;
						_perso.getHaciendoTrabajo().modificarIngrediente(_out, id, -cantidad);
					} catch (NumberFormatException e) {}
				}
			} else if (packet.charAt(2) == 'R') {
				int c = 0;
				try {
					c = Integer.parseInt(packet.substring(3));
				} catch (Exception e) {}
				_perso.getHaciendoTrabajo().variasMagueadas(c, _perso);
			}
			return;
		} else if (_perso.enBanco()) {
			if (_perso.getIntercambio() != null)
				return;
			_perso.setOcupado(true);
			switch (packet.charAt(2)) {
				case 'G':// Kamas
					long kamas = 0;
					try {
						kamas = Integer.parseInt(packet.substring(3));
					} catch (Exception e) {}
					if (kamas == 0)
						return;
					if (kamas > 0) {
						if (_perso.getKamas() < kamas)
							kamas = _perso.getKamas();
						_perso.setKamasBanco(_perso.getKamasBanco() + kamas);
						_perso.setKamas(_perso.getKamas() - kamas);
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
						GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(_perso, "G" + _perso.getKamasBanco());
					} else {
						kamas = -kamas;
						if (_perso.getKamasBanco() < kamas)
							kamas = _perso.getKamasBanco();
						_perso.setKamasBanco(_perso.getKamasBanco() - kamas);
						_perso.setKamas(_perso.getKamas() + kamas);
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
						GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(_perso, "G" + _perso.getKamasBanco());
					}
					break;
				case 'O':// Objeto
					int id = 0;
					int cant = 0;
					try {
						id = Integer.parseInt(packet.substring(4).split("\\|")[0]);
						cant = Integer.parseInt(packet.substring(4).split("\\|")[1]);
					} catch (Exception e) {}
					if (id == 0 || cant <= 0)
						return;
					if (MundoDofus.getObjeto(id) == null) {
						GestorSalida
								.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
										"<b>[Elbusta]</b> El objeto no existe o esta bugeado, porfavor cambia de personaje para evitar más errores.");
						return;
					}
					int idModObj = MundoDofus.getObjeto(id).getModelo().getID();
					if (idModObj >= 7808 && idModObj <= 7876 && idModObj != 7864 && idModObj != 7865 && idModObj != 7819
							&& idModObj != 7811 && idModObj != 7817) {
						int color = CentroInfo.getColorDragoPavoPorPerga(idModObj);
						int idScroll = CentroInfo.getScrollporMontura(color);
						if (idScroll == -1)
							return;
						_perso.borrarObjetoRemove(id);
						MundoDofus.eliminarObjeto(id);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, id);
						Objeto scroll = MundoDofus.getObjModelo(idScroll).crearObjDesdeModelo(2, false);
						if (!_perso.addObjetoSimilar(scroll, true, -1)) {
							MundoDofus.addObjeto(scroll, true);
							_perso.addObjetoPut(scroll);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, scroll);
						}
						GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
						return;
					}
					switch (packet.charAt(3)) {
						case '+':
							_perso.addObjAlBanco(id, cant);
							break;
						case '-':
							_perso.removerDelBanco(id, cant);
							break;
					}
					break;
			}
			return;
		} else if (_perso.getMochilaMontura()) {
			Dragopavo drago = _perso.getMontura();
			if (drago == null) {
				return;
			}
			switch (packet.charAt(2)) {
				case 'O':// Objeto
					int id = 0;
					int cant = 0;
					try {
						id = Integer.parseInt(packet.substring(4).split("\\|")[0]);
						cant = Integer.parseInt(packet.substring(4).split("\\|")[1]);
					} catch (Exception e) {}
					if (id == 0 || cant <= 0)
						return;
					if (MundoDofus.getObjeto(id) == null) {
						GestorSalida
								.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
										"<b>[Elbusta]</b> El objeto no existe o esta bugeado, porfavor cambia de personaje para evitar más errores.");
						return;
					}
					switch (packet.charAt(3)) {
						case '+':
							drago.addObjAMochila(id, cant, _perso);
							break;
						case '-':
							drago.removerDeLaMochila(id, cant, _perso);
							break;
					}
					break;
			}
			return;
		} else if (_perso.getCofre() != null) {
			if (_perso.getIntercambio() != null)
				return;
			Cofre cofre = _perso.getCofre();
			switch (packet.charAt(2)) {
				case 'G':
					long kamas = 0;
					try {
						kamas = Integer.parseInt(packet.substring(3));
					} catch (Exception e) {}
					if (kamas == 0)
						return;
					if (kamas > 0) {
						if (_perso.getKamas() < kamas)
							kamas = _perso.getKamas();
						cofre.setKamas(cofre.getKamas() + kamas);
						_perso.setKamas(_perso.getKamas() - kamas);
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
					} else {
						kamas = -kamas;
						if (cofre.getKamas() < kamas)
							kamas = cofre.getKamas();
						cofre.setKamas(cofre.getKamas() - kamas);
						_perso.setKamas(_perso.getKamas() + kamas);
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
					}
					for (Personaje P : MundoDofus.getPJsEnLinea()) {
						if (P.getCofre() != null && _perso.getCofre().getID() == P.getCofre().getID()) {
							GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(P, "G" + cofre.getKamas());
						}
					}
					GestorSQL.ACTUALIZAR_COFRE(cofre);
					break;
				case 'O':
					int id = 0;
					int cant = 0;
					try {
						id = Integer.parseInt(packet.substring(4).split("\\|")[0]);
						cant = Integer.parseInt(packet.substring(4).split("\\|")[1]);
					} catch (Exception e) {}
					if (id == 0 || cant <= 0)
						return;
					switch (packet.charAt(3)) {
						case '+':
							cofre.addEnCofre(id, cant, _perso);
							break;
						case '-':
							cofre.retirarDelCofre(id, cant, _perso);
							break;
					}
					break;
			}
			return;
		} else if (_perso.getTrueque() != null) {
			switch (packet.charAt(2)) {
				case 'O':// Objeto
					if (packet.charAt(3) == '+') {
						String[] infos = packet.substring(4).split("\\|");
						try {
							int id = Integer.parseInt(infos[0]);
							int cant = Integer.parseInt(infos[1]);
							int cantInter = _perso.getTrueque().getCantObj(id, _perso.getID());
							if (!_perso.tieneObjetoID(id))
								return;
							Objeto obj = MundoDofus.getObjeto(id);
							if (obj == null)
								return;
							int nuevaCant = obj.getCantidad() - cantInter;
							if (cant > nuevaCant)
								cant = nuevaCant;
							_perso.getTrueque().addObjetoTrueque(id, cant);
						} catch (NumberFormatException e) {}
					} else {
						String[] infos = packet.substring(4).split("\\|");
						try {
							int id = Integer.parseInt(infos[0]);
							int cant = Integer.parseInt(infos[1]);
							if (cant <= 0)
								return;
							if (!_perso.tieneObjetoID(id))
								return;
							Objeto obj = MundoDofus.getObjeto(id);
							if (obj == null)
								return;
							int cantInter = _perso.getTrueque().getCantObj(id, _perso.getID());
							if (cant > cantInter)
								cant = cantInter;
							_perso.getTrueque().quitarObjeto(id, cant);
						} catch (NumberFormatException e) {}
					}
					break;
			}
			return;
		} else if (_perso.getIntercambiandoCon() == _perso.getID()) {
			switch (packet.charAt(3)) {
				case '-':
					int idObj = Integer.parseInt(packet.substring(4).split("\\|")[0]);
					int cant = Integer.parseInt(packet.substring(4).split("\\|")[1]);
					if (cant <= 0)
						return;
					_perso.objetoAInvetario(idObj);
					GestorSalida.ENVIAR_EiK_MOVER_OBJETO_TIENDA(_out, '-', "", idObj + "");
					GestorSQL.SALVAR_PERSONAJE(_perso, true);
					break;
				case '+':// Poner un objeto en venta
					int idObjeto = Integer.parseInt(packet.substring(4).split("\\|")[0]);
					int cantidad = Integer.parseInt(packet.substring(4).split("\\|")[1]);
					int precio = Integer.parseInt(packet.substring(4).split("\\|")[2]);
					if (!_perso.getTienda().contains(MundoDofus.getObjeto(idObjeto))) {
						if (cantidad <= 0 || precio <= 0 || !_perso.tieneObjetoID(idObjeto))
							return;
						if (_perso.contarTienda() >= _perso.maxTienda()) {
							GestorSalida.ENVIAR_Im_INFORMACION(_out, "176");
							return;
						}
						Objeto obj = MundoDofus.getObjeto(idObjeto);
						if (cantidad > obj.getCantidad())
							return;
						int sobrante = obj.getCantidad() - cantidad;
						if (sobrante <= 0) {
							_perso.borrarObjetoRemove(idObjeto);
							GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, idObjeto);
						} else {
							obj.setCantidad(sobrante);
							GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj);
							Objeto nuevoObj = Objeto.clonarObjeto(obj, cantidad);
							MundoDofus.addObjeto(nuevoObj, true);
							obj = nuevoObj;
						}
						Tienda nuevoObjeto = new Tienda(idObjeto, precio, cantidad);
						MundoDofus.agregarTienda(nuevoObjeto, true);
						String venta = obj.getID() + "|" + cantidad + "|" + obj.getModelo().getID() + "|"
								+ obj.convertirStatsAString() + "|" + precio;
						GestorSalida.ENVIAR_EiK_MOVER_OBJETO_TIENDA(_out, '+', "", venta + "");
						_perso.agregarObjTienda(obj);
						GestorSQL.SALVAR_PERSONAJE(_perso, true);
						break;
					} else {
						if (precio <= 0)
							return;
						Objeto obj = MundoDofus.getObjeto(idObjeto);// Recupera el item
						String venta = idObjeto + "|" + cantidad + "|" + obj.getModelo().getID() + "|"
								+ obj.convertirStatsAString() + "|" + precio;
						GestorSalida.ENVIAR_EiK_MOVER_OBJETO_TIENDA(_out, '+', "", venta + "");
						_perso.actualizarObjTienda(idObjeto, precio);
						GestorSQL.SALVAR_PERSONAJE(_perso, true);
						break;
					}
			}
			return;
		} else if (_perso.getIntercambio() == null)
			return;
		Intercambio inter = _perso.getIntercambio();
		switch (packet.charAt(2)) {
			case 'O':
				if (packet.charAt(3) == '+') {
					String[] infos = packet.substring(4).split("\\|");
					int id = -1;
					int cant = -1;
					try {
						id = Integer.parseInt(infos[0]);
						cant = Integer.parseInt(infos[1]);
					} catch (NumberFormatException e) {}
					if (id == -1 || cant == -1)
						return;
					try {
						int cantInter = inter.getCantObjeto(id, _perso.getID());
						if (!_perso.tieneObjetoID(id))
							return;
						Objeto obj = MundoDofus.getObjeto(id);
						if (obj == null)
							return;
						int nuevaCant = obj.getCantidad() - cantInter;
						if (cant > nuevaCant)
							cant = nuevaCant;
						inter.addObjeto(obj, cant, _perso.getID());
					} catch (NullPointerException e) {}
				} else {
					String[] infos = packet.substring(4).split("\\|");
					try {
						int id = Integer.parseInt(infos[0]);
						int cant = Integer.parseInt(infos[1]);
						if (cant <= 0)
							return;
						if (!_perso.tieneObjetoID(id))
							return;
						Objeto obj = MundoDofus.getObjeto(id);
						if (obj == null)
							return;
						int cantInter = inter.getCantObjeto(id, _perso.getID());
						if (cant > cantInter)
							cant = cantInter;
						inter.borrarObjeto(obj, cant, _perso.getID());
					} catch (NumberFormatException e) {}
				}
				break;
			case 'G':// Kamas
				try {
					long numero = Integer.parseInt(packet.substring(3));
					if (_perso.getKamas() < numero)
						numero = _perso.getKamas();
					inter.setKamas(_perso.getID(), numero);
				} catch (NumberFormatException e) {}
				break;
		}
	}
	
	private void intercambio_Pago_Por_Trabajo(String packet) {
		if (_perso.getIntercambiandoCon() == 0) {
			GestorSalida.ENVIAR_BN_NADA(_perso);
			return;
		}
		int tipoPago = Integer.parseInt(packet.substring(2, 3));
		char caracter = packet.charAt(3);
		char signo = packet.charAt(4);
		InvitarTaller taller = _perso.getTallerInvitado();
		if (caracter == 'G') {
			long kamas = Long.parseLong(packet.substring(4));
			_perso.getTallerInvitado().setKamas(tipoPago, kamas, _perso.getKamas());
		} else {
			if (signo == '+') {
				String[] infos = packet.substring(5).split("\\|");
				int id = -1;
				int cant = -1;
				try {
					id = Integer.parseInt(infos[0]);
					cant = Integer.parseInt(infos[1]);
				} catch (NumberFormatException e) {}
				if (id == -1 || cant == -1)
					return;
				try {
					int cantInter = taller.getCantObjetoPago(id, tipoPago);
					if (!_perso.tieneObjetoID(id))
						return;
					Objeto obj = MundoDofus.getObjeto(id);
					if (obj == null)
						return;
					int nuevaCant = obj.getCantidad() - cantInter;
					if (cant > nuevaCant)
						cant = nuevaCant;
					taller.addObjetoPaga(obj, cant, tipoPago);
				} catch (NullPointerException e) {}
			} else {
				String[] infos = packet.substring(5).split("\\|");
				try {
					int id = Integer.parseInt(infos[0]);
					int cant = Integer.parseInt(infos[1]);
					if (cant <= 0)
						return;
					if (!_perso.tieneObjetoID(id))
						return;
					Objeto obj = MundoDofus.getObjeto(id);
					if (obj == null)
						return;
					int cantInter = taller.getCantObjetoPago(id, tipoPago);
					if (cant > cantInter)
						cant = cantInter;
					taller.borrarObjetoPaga(obj, cant, tipoPago);
				} catch (NumberFormatException e) {}
			}
		}
	}
	
	private void intercambio_Preg_Mercante() {
		int objTienda = _perso.contarTienda();
		int tasa = _perso.getNivel() / 2;
		long impuesto = _perso.precioTotal() * tasa / 1000;
		GestorSalida.ENVIAR_Eq_PREGUNTAR_MERCANTE(_perso, objTienda, tasa, impuesto);
	}
	
	private synchronized void intercambio_Ok_Mercante() {
		Mapa mapa = _perso.getMapa();
		int tasa = _perso.getNivel() / 2;
		long pagar = _perso.precioTotal() * tasa / 1000;
		long kamas = _perso.getKamas();
		if (kamas >= pagar) {
			if (mapa.cantMercantes() < mapa.getCapacidad() - 1) {
				_perso.setKamas(kamas - pagar);
				_perso.setMercante(1);
				mapa.agregarMercante(_perso.getID());
				GestorSQL.SALVAR_MERCANTES(mapa);
				String mercante = _perso.stringGMmercante();
				salir();// botar al personaje
				GestorSalida.ENVIAR_GM_MERCANTE_A_MAPA(mapa, mercante);
				return;
			}
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "125;" + mapa.getCapacidad());
		} else {
			GestorSalida.ENVIAR_Im_INFORMACION(_out, "176");
		}
	}
	
	private synchronized void intercambio_Mercadillo(String packet) {
		if (_perso.getIntercambiandoCon() > 0)
			return;
		int templateID;
		switch (packet.charAt(2)) {
			case 'B': // Confirmacion de compra
				String[] info = packet.substring(3).split("\\|");
				PuestoMercadillo curHdv = MundoDofus.getPuestoMerca(Math.abs(_perso.getIntercambiandoCon()));
				int ligneID = Integer.parseInt(info[0]);
				int amount = Integer.parseInt(info[1]);
				if (curHdv.comprarObjeto(ligneID, amount, Integer.parseInt(info[2]), _perso)) {
					GestorSalida.GAME_SEND_EHm_PACKET(_perso, "-", ligneID + "");// quita la linea
					if (curHdv.getLinea(ligneID) != null && !curHdv.getLinea(ligneID).categoriaVacia())
						GestorSalida.GAME_SEND_EHm_PACKET(_perso, "+", curHdv.getLinea(ligneID).analizarParaEHm());
					_perso.refrescarVida();
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "068");
				} else {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "172");
				}
				break;
			case 'l':// solicita la lista de un template (los precios)
				templateID = Integer.parseInt(packet.substring(3));
				try {
					GestorSalida.GAME_SEND_EHl(_perso, MundoDofus.getPuestoMerca(Math.abs(_perso.getIntercambiandoCon())),
							templateID);
				} catch (NullPointerException e) {
					GestorSalida.GAME_SEND_EHM_PACKET(_perso, "-", templateID + "");
				}
				break;
			case 'P':// demanda el precio promedio
				templateID = Integer.parseInt(packet.substring(3));
				GestorSalida.GAME_SEND_EHP_PACKET(_perso, templateID);
				break;
			case 'T':// demanda los template de la categoria
				int categ = Integer.parseInt(packet.substring(3));
				String allTemplate = MundoDofus.getPuestoMerca(Math.abs(_perso.getIntercambiandoCon())).stringModelo(categ);
				GestorSalida.GAME_SEND_EHL_PACKET(_perso, categ, allTemplate);
				break;
		}
	}
	
	private synchronized void intercambio_Cercado(String packet) {
		if (_perso.getEnCercado() != null) {
			char c = packet.charAt(2);
			packet = packet.substring(3);
			int id = -1;
			try {
				id = Integer.parseInt(packet);
			} catch (Exception e) {}
			switch (c) {
				case 'g':// guardar en el establo
					Dragopavo DP3 = MundoDofus.getDragopavoPorID(id);
					if (!_cuenta.getEstablo().contains(DP3))
						_cuenta.getEstablo().add(DP3);
					_perso.getMapa().getCercado().delCriando(id);
					GestorSalida.ENVIAR_Ef_MONTURA_A_CRIAR(_perso, '-', DP3.getID() + "");
					if (DP3.getFecundadaHace() >= DP3.minutosParir() && DP3.getFecundadaHace() <= 1440) {
						int crias = Formulas.getRandomValor(1, 2);
						if (DP3.getCapacidades().contains(3))
							crias = crias * 2;
						if (DP3.getReprod() + crias > 20)
							crias = 20 - DP3.getReprod();
						GestorSalida.ENVIAR_Im_INFORMACION(_out, "1111;" + crias);
						Dragopavo DragoPadre = MundoDofus.getDragopavoPorID(DP3.getPareja());
						for (int i = 0; i < crias; i++) {
							int color;
							if (DragoPadre != null)
								color = CentroInfo.colorCria(DP3.getColor(), DragoPadre.getColor());
							else
								color = CentroInfo.colorCria(DP3.getColor(), DP3.getColor());
							Dragopavo Drago = new Dragopavo(color, DP3, DragoPadre);
							GestorSQL.CREAR_MONTURA(Drago);
							GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '~', Drago.detallesMontura());
							DP3.aumReproduccion();
							_cuenta.getEstablo().add(Drago);
						}
						DP3.resAmor(7500);
						DP3.resResistencia(7500);
						DP3.setFecundadaHace(-1);
					} else if (DP3.getFecundadaHace() > 1440) {
						GestorSalida.ENVIAR_Im_INFORMACION(_out, "1112");
						DP3.aumReproduccion();
						DP3.resAmor(7500);
						DP3.resResistencia(7500);
						DP3.setFecundadaHace(-1);
					}
					GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '+', DP3.detallesMontura());
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_perso.getMapa(), id);
					DP3.setMapaCelda((short) -1, -1);
					break;
				case 'p':// Poner a Criar
					Mapa mapa = _perso.getMapa();
					if (mapa.getCercado().getListaCriando().size() >= mapa.getCercado().getTamaño()) {
						GestorSalida.ENVIAR_Im_INFORMACION(_out, "1107");
						return;
					}
					if (_perso.getMontura() != null ? _perso.getMontura().getID() == id : false) {
						if (_perso.estaMontando())
							_perso.subirBajarMontura();
						if (_perso.estaMontando())
							return;
						_perso.setMontura(null);
					}
					Dragopavo DP2 = MundoDofus.getDragopavoPorID(id);
					DP2.setDueño(_perso.getID());
					_cuenta.getEstablo().remove(DP2);
					mapa.getCercado().addCriando(id);
					GestorSalida.ENVIAR_Ef_MONTURA_A_CRIAR(_perso, '+', DP2.detallesMontura());
					GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '-', DP2.getID() + "");
					GestorSalida.ENVIAR_GM_DRAGOPAVO_A_MAPA(mapa, DP2);
					DP2.setMapaCelda(mapa.getID(), mapa.getCercado().getColocarCelda());
					break;
			}
		}
	}
	
	private synchronized void intercambio_Establo(String packet) {
		// Si dentro de un cercado
		if (_perso.getEnCercado() != null) {
			char c = packet.charAt(2);
			packet = packet.substring(3);
			int id = -1;
			try {
				id = Integer.parseInt(packet);
			} catch (Exception e) {}
			switch (c) {
				case 'C':// pergamino => establo (Stocker)
					if (id == -1 || !_perso.tieneObjetoID(id))
						return;
					Objeto obj = MundoDofus.getObjeto(id);
					int DPid = obj.getStats().getEfecto(995);
					Dragopavo DP = MundoDofus.getDragopavoPorID(-DPid);
					if (DP == null) {
						int color = CentroInfo.getColorDragoPavoPorPerga(obj.getModelo().getID());
						if (color < 1)
							return;
						DP = new Dragopavo(color, _perso.getID());
					}
					_perso.borrarObjetoRemove(id);
					MundoDofus.eliminarObjeto(id);
					if (!_cuenta.getEstablo().contains(DP))
						_cuenta.getEstablo().add(DP);
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, obj.getID());
					if (DP.getFecundadaHace() >= DP.minutosParir() && DP.getFecundadaHace() <= 1440) {
						int crias = Formulas.getRandomValor(1, 2);
						if (DP.getCapacidades().contains(3))
							crias = crias * 2;
						if (DP.getReprod() + crias > 20)
							crias = 20 - DP.getReprod();
						GestorSalida.ENVIAR_Im_INFORMACION(_out, "1111;" + crias);
						Dragopavo DragoPadre = MundoDofus.getDragopavoPorID(DP.getPareja());
						for (int i = 0; i < crias; i++) {
							int color;
							if (DragoPadre != null)
								color = CentroInfo.colorCria(DP.getColor(), DragoPadre.getColor());
							else
								color = CentroInfo.colorCria(DP.getColor(), DP.getColor());
							Dragopavo Drago = new Dragopavo(color, DP, DragoPadre);
							GestorSQL.CREAR_MONTURA(Drago);
							GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '~', Drago.detallesMontura());
							DP.aumReproduccion();
							_cuenta.getEstablo().add(Drago);
						}
						DP.resAmor(7500);
						DP.resResistencia(7500);
						DP.setFecundadaHace(-1);
					} else if (DP.getFecundadaHace() > 1440) {
						GestorSalida.ENVIAR_Im_INFORMACION(_out, "1112");
						DP.aumReproduccion();
						DP.resAmor(7500);
						DP.resResistencia(7500);
						DP.setFecundadaHace(-1);
					}
					GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '+', DP.detallesMontura());
					break;
				case 'c':// establo => pergamino(intercambio)
					Dragopavo DP1 = MundoDofus.getDragopavoPorID(id);
					if (!_cuenta.getEstablo().contains(DP1) || DP1 == null)
						return;
					_cuenta.getEstablo().remove(DP1);
					ObjetoModelo OM = CentroInfo.getPergaPorColorDragopavo(DP1.getColor());
					Objeto obj1 = OM.crearObjDesdeModelo(1, false);
					MundoDofus.addObjeto(obj1, true);
					obj1.clearTodo();
					obj1.getStats().addUnStat(995, - (DP1.getID()));
					obj1.addTextoStat(996, _perso.getNombre());
					obj1.addTextoStat(997, DP1.getNombre());
					_perso.addObjetoPut(obj1);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, obj1);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
					GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '-', DP1.getID() + "");
					break;
				case 'g':// Equipar dragopavo
					Dragopavo DP3 = MundoDofus.getDragopavoPorID(id);
					if (!_cuenta.getEstablo().contains(DP3) || DP3 == null) {
						GestorSalida.ENVIAR_Im_INFORMACION(_out, "1104");
						return;
					}
					if (_perso.getMontura() != null) {
						GestorSalida.ENVIAR_BN_NADA(_out);
						return;
					}
					_cuenta.getEstablo().remove(DP3);
					_perso.setMontura(DP3);
					GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "+", DP3);
					GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '-', DP3.getID() + "");
					GestorSalida.ENVIAR_Rx_EXP_DONADA_MONTURA(_perso);
					break;
				case 'p':// Equipar => Establo
					if (_perso.getMontura() != null ? _perso.getMontura().getID() == id : false) {
						Dragopavo DP2 = _perso.getMontura();
						if (DP2.getObjetos().size() == 0) {
							if (_perso.estaMontando())
								_perso.subirBajarMontura();
							if (!_cuenta.getEstablo().contains(DP2))
								_cuenta.getEstablo().add(DP2);
							_perso.setMontura(null);
							if (DP2.getFecundadaHace() >= DP2.minutosParir() && DP2.getFecundadaHace() <= 1440) {
								int crias = Formulas.getRandomValor(1, 2);
								if (DP2.getCapacidades().contains(3))
									crias = crias * 2;
								if (DP2.getReprod() + crias > 20)
									crias = 20 - DP2.getReprod();
								GestorSalida.ENVIAR_Im_INFORMACION(_out, "1111;" + crias);
								Dragopavo DragoPadre = MundoDofus.getDragopavoPorID(DP2.getPareja());
								for (int i = 0; i < crias; i++) {
									int color;
									if (DragoPadre != null)
										color = CentroInfo.colorCria(DP2.getColor(), DragoPadre.getColor());
									else
										color = CentroInfo.colorCria(DP2.getColor(), DP2.getColor());
									Dragopavo Drago = new Dragopavo(color, DP2, DragoPadre);
									GestorSQL.CREAR_MONTURA(Drago);
									GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '~', Drago.detallesMontura());
									DP2.aumReproduccion();
									_cuenta.getEstablo().add(Drago);
								}
								DP2.resAmor(7500);
								DP2.resResistencia(7500);
								DP2.setFecundadaHace(-1);
							} else if (DP2.getFecundadaHace() > 1440) {
								GestorSalida.ENVIAR_Im_INFORMACION(_out, "1112");
								DP2.aumReproduccion();
								DP2.resAmor(7500);
								DP2.resResistencia(7500);
								DP2.setFecundadaHace(-1);
							}
							GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '+', DP2.detallesMontura());
							GestorSalida.ENVIAR_Re_DETALLES_MONTURA(_perso, "-", null);
							GestorSalida.ENVIAR_Rx_EXP_DONADA_MONTURA(_perso);
						} else
							GestorSalida.ENVIAR_Im_INFORMACION(_out, "106");
					}
					break;
			}
		}
	}
	
	private void intercambio_Repetir() {
		if (_perso.getHaciendoTrabajo() != null)
			_perso.getHaciendoTrabajo().ponerIngredUltRecet();
	}
	
	private void intercambio_Ok() {
		if (_perso.getTallerInvitado() != null) {
			_perso.getTallerInvitado().botonOK(_perso.getID());
		} else if (_perso.getHaciendoTrabajo() != null) {
			if (!_perso.getHaciendoTrabajo().esReceta())
				return;
			_perso.getHaciendoTrabajo().unaMagueada();
		} else if (_perso.getRompiendo()) {
			if (_perso.getObjetoARomper() == 0)
				return;
			int id = _perso.getObjetoARomper();
			Objeto Obj = MundoDofus.getObjeto(id);
			if (Obj == null)
				return;
			String runaId = Objeto.getRunas(Obj);
			String[] objLista = runaId.split(";");
			boolean creado = false;
			if (Formulas.getRandomValor(0, 1) != 1 && objLista.length > 0)
				try {
					creado = true;
					String runa = objLista[Formulas.getRandomValor(0, objLista.length - 1)];
					int objModeloID = Integer.parseInt(runa.split(",")[0]);
					int cantidad = Integer.parseInt(runa.split(",")[1]);
					ObjetoModelo ObjTemp = MundoDofus.getObjModelo(objModeloID);
					Objeto nuevoObj = ObjTemp.crearObjDesdeModelo(cantidad, true);
					if (!_perso.addObjetoSimilar(nuevoObj, true, -1)) {
						MundoDofus.addObjeto(nuevoObj, true);
						_perso.addObjetoPut(nuevoObj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObj);
					}
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_perso, "K;" + objModeloID);// Exito
				} catch (Exception e) {}
			Objeto Obj2 = MundoDofus.getObjeto(id);
			if (Obj2.getCantidad() == 1) {
				_perso.borrarObjetoRemove(id);
				MundoDofus.eliminarObjeto(id);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, id);
			} else {
				Obj2.setCantidad( (Obj2.getCantidad() - 1));
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, Obj2);
			}
			if (!creado) {
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_perso, "EF");
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_perso.getMapa(), _perso.getID(), "-");
			} else {
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_perso.getMapa(), _perso.getID(), "+8378");
			}
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			_perso.iniciarAccionEnCelda(_perso.getTaller());
		} else if (_perso.getTrueque() != null) {
			_perso.getTrueque().botonOK(_perso.getID());
		} else if (_perso.getIntercambio() != null) {
			_perso.getIntercambio().botonOK(_perso.getID());
		}
	}
	
	private void intercambio_Aceptar() {
		if (_perso.getIntercambiandoCon() != 0) {
			if (_perso.getHaciendoTrabajo() != null) {
				AccionTrabajo trabajo = _perso.getHaciendoTrabajo();
				Personaje artesano = MundoDofus.getPersonaje(_perso.getIntercambiandoCon());
				try {
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(artesano.getCuenta().getEntradaPersonaje().getOut(), 12,
							trabajo.getCasillasMax() + ";" + trabajo.getIDTrabajo());// artesano
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 13,
							trabajo.getCasillasMax() + ";" + trabajo.getIDTrabajo());// invitado
				} catch (NullPointerException e) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					_perso.setIntercambiandoCon(0);
					artesano.setIntercambiandoCon(0);
					return;
				}
				InvitarTaller taller = new InvitarTaller(artesano, _perso, trabajo.getCasillasMax());
				try {
					artesano.setTallerInvitado(taller);
					_perso.setTallerInvitado(taller);
				} catch (NullPointerException e) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
			} else {
				Personaje pjInter = MundoDofus.getPersonaje(_perso.getIntercambiandoCon());
				try {
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(pjInter.getCuenta().getEntradaPersonaje().getOut(), 1, "");
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 1, "");
				} catch (NullPointerException e) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					_perso.setIntercambiandoCon(0);
					pjInter.setIntercambiandoCon(0);
					return;
				}
				Intercambio intercambio = new Intercambio(pjInter, _perso);
				try {
					pjInter.setIntercambio(intercambio);
					_perso.setIntercambio(intercambio);
				} catch (NullPointerException e) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
			}
		}
	}
	
	private void intercambio_Vender(String packet) {
		try {
			String[] infos = packet.substring(2).split("\\|");
			int id = Integer.parseInt(infos[0]);
			int cant = Integer.parseInt(infos[1]);
			if (!_perso.tieneObjetoID(id)) {
				GestorSalida.ENVIAR_ESE_ERROR_VENTA(_out);
				return;
			}
			_perso.venderObjeto(id, cant);
		} catch (Exception e) {
			GestorSalida.ENVIAR_ESE_ERROR_VENTA(_out);
		}
	}
	
	private void intercambio_Comprar(String packet) {
		String[] infos = packet.substring(2).split("\\|");
		if (_perso.getIntercambiandoCon() < 0) {
			try {
				int idObjModelo = 0;
				int cantidad = 0;
				try {
					idObjModelo = Integer.parseInt(infos[0]);
					cantidad = Integer.parseInt(infos[1]);
				} catch (NumberFormatException e) {}
				if (cantidad <= 0 || idObjModelo <= 0)
					return;
				ObjetoModelo objModelo = MundoDofus.getObjModelo(idObjModelo);
				if (objModelo == null) {
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
					return;
				}
				NPC npc = _perso.getMapa().getNPC(_perso.getIntercambiandoCon());
				if (npc == null) {
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
					return;
				}
				NPCModelo npcMod = npc.getModeloBD();
				if (npcMod == null || !npcMod.tieneObjeto(idObjModelo)) {
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
					return;
				}
				int precioUnitario = objModelo.getPrecio();
				int precioVIP = objModelo.getPrecioVIP();
				int precio = precioUnitario * cantidad;
				if (precioUnitario == 0 && precioVIP > 0) {
					precio = precioVIP * cantidad;
					int mispuntos = GestorSQL.getPuntosCuenta(_perso.getCuentaID());
					if (mispuntos < precio) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, "No se pudo realizar la compra, dado que le falta "
								+ (precio - mispuntos) + " puntos de tienda.");
						return;
					}
					int puntosnuevos = mispuntos - precio;
					GestorSQL.setPuntoCuenta(puntosnuevos, _perso.getCuentaID());
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
							"Gracias por comprar en Bustofus, cualquier queja o inconveniente con su producto, comunicarle a ELBUSTA. Aún le queda "
									+ puntosnuevos + " puntos de tienda.");
				} else {
					if (npcMod.getID() == 809) {
						_perso.setKamas(0);
						GestorSalida.ENVIAR_EBK_COMPRADO(_out);
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
						return;
					}
					if (_perso.getKamas() < precio) {
						GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
						return;
					}
					long nuevasKamas = _perso.getKamas() - precio;
					_perso.setKamas(nuevasKamas);
				}
				Objeto nuevoObj = null;
				nuevoObj = objModelo.crearObjDesdeModelo(cantidad, false);
				if (!_perso.addObjetoSimilar(nuevoObj, true, -1)) {
					MundoDofus.addObjeto(nuevoObj, true);
					_perso.addObjetoPut(nuevoObj);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObj);
				}
				GestorSalida.ENVIAR_EBK_COMPRADO(_out);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
			} catch (Exception e) {
				e.printStackTrace();
				GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
				return;
			}
		} else {
			Personaje mercante = MundoDofus.getPersonaje(_perso.getIntercambiandoCon());
			try {
				int id = Integer.parseInt(infos[0]);
				int cant = Integer.parseInt(infos[1]);
				if (cant <= 0)
					return;
				Objeto objeto = MundoDofus.getObjeto(id);
				Tienda tienda = MundoDofus.getObjTienda(id);
				if (objeto == null || tienda == null) {
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
					return;
				}
				int precio = tienda.getPrecio() * cant;
				if (_perso.getKamas() < precio) {
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
					return;
				}
				int cantObjeto = objeto.getCantidad();
				if (cant == cantObjeto) {
					long nuevasKamas = _perso.getKamas() - precio;
					_perso.setKamas(nuevasKamas);
					mercante.setKamas(mercante.getKamas() + precio);
					mercante.borrarObjTienda(objeto);
					if (_perso.addObjetoSimilar(objeto, true, -1))
						MundoDofus.eliminarObjeto(id);
					else {
						_perso.addObjetoPut(objeto);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, objeto);
					}
					GestorSalida.ENVIAR_EBK_COMPRADO(_out);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
					GestorSQL.SALVAR_PERSONAJE(_perso, true);
					GestorSalida.ENVIAR_EL_LISTA_TIENDA_PERSONAJE(_out, mercante);
				} else if (cant < cantObjeto) {
					Objeto nuevoObj = Objeto.clonarObjeto(objeto, cant);
					int nuevaCant = cantObjeto - cant;
					objeto.setCantidad(nuevaCant);
					tienda.setCantidad(nuevaCant);
					GestorSQL.ACTUALIZAR_CANT_TIENDA(id, nuevaCant);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, objeto);
					long nuevasKamas = _perso.getKamas() - precio;
					_perso.setKamas(nuevasKamas);
					mercante.setKamas(mercante.getKamas() + precio);
					GestorSQL.SALVAR_PERSONAJE(mercante, true);
					if (!_perso.addObjetoSimilar(nuevoObj, true, id)) {
						MundoDofus.addObjeto(nuevoObj, true);
						_perso.addObjetoPut(nuevoObj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObj);
					}
					GestorSalida.ENVIAR_EBK_COMPRADO(_out);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
					GestorSQL.SALVAR_PERSONAJE(_perso, true);
					GestorSalida.ENVIAR_EL_LISTA_TIENDA_PERSONAJE(_out, mercante);
				} else {
					GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA(_out);
				GestorSalida.ENVIAR_EL_LISTA_TIENDA_PERSONAJE(_out, mercante);
				return;
			}
		}
	}
	
	public void intercambio_Cerrar() {
		if (_perso.getIntercambio() == null && !_perso.enBanco() && _perso.getHaciendoTrabajo() == null
				&& _perso.getIntercambiandoCon() == 0 && _perso.getEnCercado() == null && _perso.getTrueque() == null
				&& !_perso.getListaArtesanos() && _perso.getCofre() == null && !_perso.getMochilaMontura()
				&& !_perso.getRompiendo() && _perso.getTallerInvitado() == null) {
			return;
		} else if (_perso.getCofre() != null) {
			_perso.setCofre(null);
			_perso.setOcupado(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.enBanco()) {
			_perso.setEnBanco(false);
			_perso.setOcupado(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getListaArtesanos()) {
			_perso.setListaArtesanos(false);
			_perso.setIntercambiandoCon(0);
			_perso.setOcupado(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getRompiendo()) {
			_perso.setOcupado(false);
			_perso.setObjetoARomper(0);
			_perso.setRompiendo(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getTrueque() != null) {
			_perso.setIntercambiandoCon(0);
			_perso.setOcupado(false);
			_perso.setTrueque(null);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getIntercambio() != null) {
			_perso.setOcupado(false);
			_perso.getIntercambio().cancel();
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getMochilaMontura()) {
			_perso.setOcupado(false);
			_perso.setDragopaveando(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getTallerInvitado() != null) {
			Personaje perso = MundoDofus.getPersonaje(_perso.getIntercambiandoCon());
			if (perso != null) {
				if (perso.enLinea()) {
					PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
					perso.setIntercambiandoCon(0);
					perso.setOcupado(false);
					perso.setTallerInvitado(null);
					perso.setHaciendoTrabajo(null);
					GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(out);
				}
			}
			_perso.setIntercambiandoCon(0);
			_perso.setOcupado(false);
			_perso.setTallerInvitado(null);
			_perso.setHaciendoTrabajo(null);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getHaciendoTrabajo() != null) {
			_perso.getHaciendoTrabajo().resetReceta();
			_perso.setOcupado(false);
			_perso.setHaciendoTrabajo(null);
			_perso.setIntercambiandoCon(0);
			if (_perso.getIntercambiandoCon() > 0) {
				Personaje perso = MundoDofus.getPersonaje(_perso.getIntercambiandoCon());
				if (perso != null) {
					if (perso.enLinea()) {
						PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
						perso.setHaciendoTrabajo(null);
						perso.setOcupado(false);
						perso.setIntercambiandoCon(0);
						GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(out);
					}
				}
			}
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getEnCercado() != null) {
			_perso.salirDeCercado();
			_perso.setOcupado(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getIntercambiandoCon() < 0) {
			_perso.setIntercambiandoCon(0);
			_perso.setOcupado(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getIntercambiandoCon() > 0) {
			Personaje perso = MundoDofus.getPersonaje(_perso.getIntercambiandoCon());
			if (perso != null) {
				if (perso.enLinea()) {
					PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
					perso.setIntercambiandoCon(0);
					perso.setIntercambio(null);
					perso.setOcupado(false);
					GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(out);
				}
			}
			_perso.setIntercambiandoCon(0);
			_perso.setOcupado(false);
			_perso.setIntercambio(null);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		} else if (_perso.getRecaudando()) {
			Recaudador recau = MundoDofus.getRecaudador(_perso.getRecaudandoRecauID());
			for (Personaje z : MundoDofus.getGremio(recau.getGremioID()).getPjMiembros()) {
				if (z == null)
					continue;
				if (z.enLinea()) {
					GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z, Recaudador.analizarRecaudadores(z.getGremio().getID()));
					String str = "";
					str += "G" + recau.getN1() + "," + recau.getN2();
					str += "|.|" + MundoDofus.getMapa((short) recau.getMapaID()).getX() + "|"
							+ MundoDofus.getMapa((short) recau.getMapaID()).getY() + "|";
					str += _perso.getNombre() + "|";
					str += recau.getXp();
					if (!recau.stringObjetos().isEmpty())
						str += ";" + recau.stringObjetos();
					GestorSalida.GAME_SEND_gT_PACKET(z, str);
				}
			}
			recau.setEnRecolecta(false);
			_perso.getMapa().removeNPC(recau.getID());
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_perso.getMapa(), recau.getID());
			recau.borrarRecauPorRecolecta(recau.getID(), _perso);
			_perso.setRecaudando(false);
			_perso.setRecaudandoRecaudadorID(0);
			_perso.setIntercambiandoCon(0);
			_perso.setOcupado(false);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			return;
		}
	}
	
	private void intercambio_Iniciar(String packet) {
		if (_perso.esFantasma()) {
			GestorSalida.ENVIAR_BN_NADA(_out);
			return;
		}
		if (packet.substring(2, 4).equals("11")) {// abrir HDV compra
			if (_perso.getIntercambiandoCon() < 0)
				GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			if (_perso.getDeshonor() >= 5) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "183");
				return;
			}
			PuestoMercadillo toOpen = MundoDofus.getPuestoMerca(_perso.getMapa().getID());
			if (toOpen == null)
				return;
			String info = "1,10,100;" + toOpen.getTipoObjPermitidos() + ";" + toOpen.porcentajeImpuesto() + ";"
					+ toOpen.getNivelMax() + ";" + toOpen.getMaxObjCuenta() + ";-1;" + toOpen.getTiempoVenta();
			GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, 11, info);
			_perso.setIntercambiandoCon(0 - _perso.getMapa().getID());
			return;
		} else if (packet.substring(2, 4).equals("10")) {// abre HDV venta
			if (_perso.getIntercambiandoCon() < 0)
				GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			if (_perso.getDeshonor() >= 5) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "183");
				return;
			}
			PuestoMercadillo mercadillo = MundoDofus.getPuestoMerca(_perso.getMapa().getID());
			if (mercadillo == null)
				return;
			String info = "1,10,100;" + mercadillo.getTipoObjPermitidos() + ";" + mercadillo.porcentajeImpuesto() + ";"
					+ mercadillo.getNivelMax() + ";" + mercadillo.getMaxObjCuenta() + ";-1;" + mercadillo.getTiempoVenta();
			GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_perso, 10, info);
			_perso.setIntercambiandoCon(0 - _perso.getMapa().getID());
			GestorSalida.GAME_SEND_HDVITEM_SELLING(_perso);
			return;
		} else if (packet.substring(2, 4).equals("15")) {// dragopavo
			try {
				Dragopavo montura = _perso.getMontura();
				int idMontura = montura.getID();
				GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 15, _perso.getMontura().getID() + "");
				GestorSalida.ENVIAR_EL_LISTA_OBJETOS_DRAGOPAVO(_out, montura);
				GestorSalida.ENVIAR_Ew_PODS_MONTURA(_perso, montura.getPodsActuales());
				_perso.setIntercambiandoCon(idMontura);
				_perso.setDragopaveando(true);
				_perso.setOcupado(true);
			} catch (Exception e) {}
			return;
		} else if (packet.substring(2, 4).equals("12")) {// invitar a taller
			try {
				String[] nuevo = packet.substring(5).split("\\|");
				int idInvitado = Integer.parseInt(nuevo[0]);
				int idTrabajo = Integer.parseInt(nuevo[1]);
				AccionTrabajo accionT = null;
				boolean paso = false;
				for (StatsOficio statOficio : _perso.getStatsOficios().values()) {
					Oficio oficio = statOficio.getOficio();
					for (AccionTrabajo trabajo : CentroInfo.getTrabajosPorOficios(oficio.getID(), statOficio.getNivel())) {
						if (trabajo.getIDTrabajo() != idTrabajo)
							continue;
						accionT = trabajo;
						paso = true;
						break;
					}
					if (paso)
						break;
				}
				if (accionT == null) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				Personaje invitado = MundoDofus.getPersonaje(idInvitado);
				_perso.setHaciendoTrabajo(accionT);
				invitado.setHaciendoTrabajo(accionT);
				_perso.setIntercambiandoCon(idInvitado);
				invitado.setIntercambiandoCon(_perso.getID());
				GestorSalida.ENVIAR_ERK_CONSULTA_INTERCAMBIO(_out, _perso.getID(), idInvitado, 12);// invitador
				GestorSalida.ENVIAR_ERK_CONSULTA_INTERCAMBIO(invitado.getCuenta().getEntradaPersonaje().getOut(), _perso.getID(),
						idInvitado, 13);// invitado
			} catch (Exception e) {}
			return;
		}
		switch (packet.charAt(2)) {
			case '0':// Si NPC
				try {
					int npcID = Integer.parseInt(packet.substring(4));
					NPCModelo.NPC npc = _perso.getMapa().getNPC(npcID);
					if (npc == null)
						return;
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 0, npcID + "");
					GestorSalida.ENVIAR_EL_LISTA_OBJETOS_NPC(_out, npc);
					_perso.setIntercambiandoCon(npcID);
					_perso.setOcupado(true);
				} catch (NumberFormatException e) {}
				break;
			case '1':// Si jugador
				try {
					int idObjetivo = Integer.parseInt(packet.substring(4));
					Personaje objetivo = MundoDofus.getPersonaje(idObjetivo);
					if (objetivo == null || objetivo.getMapa() != _perso.getMapa() || !objetivo.enLinea()) {
						GestorSalida.ENVIAR_ERE_ERROR_CONSULTA(_out, 'E');
						return;
					}
					if (objetivo.estaOcupado() || _perso.estaOcupado() || objetivo.getIntercambiandoCon() != 0) {
						GestorSalida.ENVIAR_ERE_ERROR_CONSULTA(_out, 'O');
						return;
					}
					GestorSalida.ENVIAR_ERK_CONSULTA_INTERCAMBIO(_out, _perso.getID(), idObjetivo, 1);
					GestorSalida.ENVIAR_ERK_CONSULTA_INTERCAMBIO(objetivo.getCuenta().getEntradaPersonaje().getOut(),
							_perso.getID(), idObjetivo, 1);
					_perso.setIntercambiandoCon(idObjetivo);
					objetivo.setIntercambiandoCon(_perso.getID());
					_perso.setOcupado(true);
					objetivo.setOcupado(true);
				} catch (NumberFormatException e) {}
				break;
			case '4':// Si mercante
				try {
					int idMercante = Integer.parseInt(packet.split("\\|")[1]);
					Personaje mercante = MundoDofus.getPersonaje(idMercante);
					if (mercante == null)
						return;
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 4, idMercante + "");
					GestorSalida.ENVIAR_EL_LISTA_TIENDA_PERSONAJE(_out, mercante);
					_perso.setIntercambiandoCon(idMercante);
					_perso.setOcupado(true);
				} catch (NumberFormatException e) {}
				break;
			case '6':// si abro tienda
				try {
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 6, _perso.getID() + "");
					GestorSalida.ENVIAR_EL_LISTA_TIENDA_PERSONAJE(_out, _perso);
					_perso.setIntercambiandoCon(_perso.getID());
					_perso.setOcupado(true);
				} catch (Exception e) {}
			case '8':// Si Recaudador
				try {
					int RecaudadorID = Integer.parseInt(packet.substring(4));
					Recaudador recau = MundoDofus.getRecaudador(RecaudadorID);
					if (recau == null || recau.getEstadoPelea() > 0 || recau.getEnRecolecta() || _perso.getGremio() == null
							|| !_perso.getMiembroGremio().puede(CentroInfo.G_RECOLECTARRECAUDADOR))
						return;
					recau.setEnRecolecta(true);
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_out, 8, recau.getID() + "");
					GestorSalida.ENVIAR_EL_LISTA_OBJETOS_RECAUDADOR(_out, recau);
					_perso.setIntercambiandoCon(recau.getID());
					_perso.setRecaudando(true);
					_perso.setRecaudandoRecaudadorID(recau.getID());
					_perso.setOcupado(true);
				} catch (NumberFormatException e) {}
				break;
		}
	}
	
	private void analizar_Ambiente(String packet) {
		switch (packet.charAt(1)) {
			case 'D':// cambia de direccion
				ambiente_Cambio_Direccion(packet);
				break;
			case 'U':// Emote
				ambiente_Emote(packet);
				break;
		}
	}
	
	private void ambiente_Emote(String packet) {
		int emote = -1;
		try {
			emote = Integer.parseInt(packet.substring(2));
		} catch (Exception e) {}
		if (emote == -1 || _perso.getPelea() != null)
			return;
		switch (emote) {
			case 19:
			case 1:
				if (_perso.estaSentado())
					emote = 0;
				_perso.setSentado(!_perso.estaSentado());
				break;
		}
		_perso.setEmoteActivado(emote);
		String tiempo = "";
		if (emote == 7) {
			tiempo = "9000";
		} else if (emote == 7) {
			tiempo = "5000";
		}
		Cercado cercado = _perso.getMapa().getCercado();
		GestorSalida.ENVIAR_eUK_EMOTE_MAPA(_perso.getMapa(), _perso.getID(), emote, tiempo);
		if ( (emote == 2 || emote == 4 || emote == 3 || emote == 6 || emote == 8 || emote == 10) && cercado != null) {
			ArrayList<Dragopavo> pavos = new ArrayList<Dragopavo>();
			for (Integer pavo : cercado.getListaCriando()) {
				if (MundoDofus.getDragopavoPorID(pavo).getDueño() == _perso.getID()) {
					pavos.add(MundoDofus.getDragopavoPorID(pavo));
				}
			}
			if (pavos.size() > 0) {
				int casillas = 0;
				switch (emote) {
					case 2:
					case 4:
						casillas = 1;
						break;
					case 3:
					case 8:
						casillas = Formulas.getRandomValor(2, 3);
						break;
					case 6:
					case 10:
						casillas = Formulas.getRandomValor(4, 7);
						break;
				}
				boolean alejar;
				if (emote == 2 || emote == 3 || emote == 10)
					alejar = false;
				else
					alejar = true;
				Dragopavo dragopavo = pavos.get(Formulas.getRandomValor(0, pavos.size() - 1));
				dragopavo.moverMontura(_perso, casillas, alejar);
			}
		}
	}
	
	private void ambiente_Cambio_Direccion(String packet) {
		try {
			if (_perso.getPelea() != null)
				return;
			int dir = Integer.parseInt(packet.substring(2));
			_perso.setOrientacion(dir);
			GestorSalida.ENVIAR_eD_CAMBIAR_ORIENTACION(_perso.getMapa(), _perso.getID(), dir);
		} catch (NumberFormatException e) {
			return;
		}
	}
	
	private void analizar_Hechizos(String packet) {
		switch (packet.charAt(1)) {
			case 'B':
				hechizos_Boost(packet);
				break;
			case 'F':
				hechizos_Olvidar(packet);
				break;
			case 'M':
				hechizos_Acceso_Rapido(packet);
				break;
		}
	}
	
	private void hechizos_Acceso_Rapido(String packet) {
		try {
			int hechizoID = Integer.parseInt(packet.substring(2).split("\\|")[0]);
			int posicion = Integer.parseInt(packet.substring(2).split("\\|")[1]);
			StatsHechizos hechizo = _perso.getStatsHechizo(hechizoID);
			if (hechizo != null) {
				_perso.setPosHechizo(hechizoID, Encriptador.getValorHashPorNumero(posicion), false);
			}
			GestorSalida.ENVIAR_BN_NADA(_out);
		} catch (Exception e) {}
	}
	
	private void hechizos_Boost(String packet) {
		try {
			int id = Integer.parseInt(packet.substring(2));
			if (_perso.boostearHechizo(id)) {
				GestorSalida.ENVIAR_SUK_SUBIR_NIVEL_HECHIZO(_out, id, _perso.getStatsHechizo(id).getNivel());
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			} else {
				GestorSalida.ENVIAR_SUE_SUBIR_NIVEL_HECHIZO_ERROR(_out);
				return;
			}
		} catch (Exception e) {
			GestorSalida.ENVIAR_SUE_SUBIR_NIVEL_HECHIZO_ERROR(_out);
			return;
		}
	}
	
	private void hechizos_Olvidar(String packet) {
		if (!_perso.estaOlvidandoHechizo())
			return;
		int id = Integer.parseInt(packet.substring(2));
		if (_perso.olvidarHechizo(id)) {
			GestorSalida.ENVIAR_SUK_SUBIR_NIVEL_HECHIZO(_out, id, _perso.getStatsHechizo(id).getNivel());
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			_perso.setOlvidandoHechizo(false);
		}
	}
	
	private void analizar_Peleas(String packet) {
		Pelea pelea = _perso.getPelea();
		switch (packet.charAt(1)) {
			case 'D':// Detalles de un combate (lista de combates)
				int key = -1;
				try {
					key = Integer.parseInt(packet.substring(2).replace( ((int) 0x0) + "", ""));
				} catch (Exception e) {}
				if (key == -1)
					return;
				GestorSalida.ENVIAR_fD_DETALLES_PELEA(_out, _perso.getMapa().getPeleas().get(key));
				break;
			case 'H':// Ayuda
				if (pelea == null)
					return;
				pelea.botonAyuda(_perso.getID());
				GestorSalida.ENVIAR_BN_NADA(_out);
				break;
			case 'L':// lista de combates
				GestorSalida.ENVIAR_fL_LISTA_PELEAS(_out, _perso.getMapa());
				break;
			case 'N':// Bloquear el combate a otros jugadores
				if (pelea == null)
					return;
				pelea.botonBloquearMasJug(_perso.getID());
				GestorSalida.ENVIAR_BN_NADA(_out);
				break;
			case 'P':// solamente el grupo
				if (pelea == null || _perso.getGrupo() == null)
					return;
				pelea.botonSoloGrupo(_perso.getID());
				GestorSalida.ENVIAR_BN_NADA(_out);
				break;
			case 'S':// Bloquear a los espectadores
				if (pelea == null)
					return;
				pelea.botonBloquearEspect(_perso.getID());
				GestorSalida.ENVIAR_BN_NADA(_out);
				break;
		}
	}
	
	private void analizar_Basicos(String packet) {
		switch (packet.charAt(1)) {
			case 'A':// Console
				basicos_Consola(packet);
				break;
			case 'D':// datos y hora
				basicos_Enviar_Fecha();
				break;
			case 'M':// chat mensaje
				basicos_Chat(packet);
				break;
			case 'W':// mensaje de informacion
				basicos_Mensaje_Informacion(packet);
				break;
			case 'S':// emotico
				_perso.emote(packet.substring(2));
				break;
			case 'Y':// estado
				basicos_Estado(packet);
				break;
		}
	}
	
	public void basicos_Estado(String packet) {
		switch (packet.charAt(2)) {
			case 'A': // Ausente
				if (_perso.estaAusente()) {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "038");
					_perso.setEstaAusente(false);
				} else {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "037");
					_perso.setEstaAusente(true);
				}
				break;
			case 'I': // Invisible
				if (_perso.esInvisible()) {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "051");
					_perso.setEsInvisible(false);
				} else {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "050");
					_perso.setEsInvisible(true);
				}
				break;
		}
	}
	
	private void basicos_Consola(String packet) {
		if (_comando == null)
			_comando = new Comandos(_perso);
		_comando.consolaComando(packet);
	}
	
	private void basicos_Chat(String packet) {
		String mensajeC = "";
		if (_perso.estaMuteado()) {
			Cuenta cuenta = _perso.getCuenta();
			long tiempoTrans = System.currentTimeMillis() - cuenta._horaMuteada;
			if (tiempoTrans > cuenta._tiempoMuteado) {
				cuenta.mutear(false, 0);
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "1124;" + (cuenta._tiempoMuteado - tiempoTrans) / 1000);
				return;
			}
		}
		packet = packet.replace("<", "");
		packet = packet.replace(">", "");
		if (packet.length() == 3) {
			GestorSalida.ENVIAR_BN_NADA(_out);
			return;
		}
		switch (packet.charAt(2)) {
			case '*':// Canal negro
				if (!_perso.getCanal().contains(packet.charAt(2) + "")) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				mensajeC = packet.split("\\|", 2)[1];
				if (mensajeC.length() <= 0) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				if (mensajeC.charAt(0) == '.') {
					try {
						if (mensajeC.length() > 8 && mensajeC.substring(1, 9).equalsIgnoreCase("comandos")) {
							GestorSalida
									.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(
											_out,
											"Comandos Disponibles : \n.turnoP  =  Debugea el turno de un jugador bug\n.startP  =  Sirve para iniciar la pelea, si esta se bugea\n.borrardoble  =  Borra el Doble en un mapa (bug) asi se escribe: .borrardoble (mapaID donde esta el doble) Ejemplo: .borrardoble 7411\n.rates  =  Te da la información de los rates del servidor\n.puntos  =  Te muestra la cantidad de puntos de tienda\n.atacar  =  Sirve para desbugear, cuando no puedes lanzar hechizo");
							return;
						} else if (mensajeC.length() > 8 && mensajeC.substring(1, 9).equalsIgnoreCase("commande")) {
							GestorSalida
									.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(
											_out,
											"Commandes disponibles: \n.turnoP  = Pass tour toute jouer. Debug tour\n.startP  =  Ilisé pour commencer le combat, si ce n'est Bug\n.rates  =  Il vous donne les taux de serveur d'information\n.points  = Il indique la quantité de points de shop");
							return;
						} else if (mensajeC.length() > 2 && mensajeC.substring(1, 3).equalsIgnoreCase("as")) {
							if (mensajeC.split(" ").length < 2) {
								GestorSalida.ENVIAR_BN_NADA(_out);
								return;
							}
							String mensaje = mensajeC.split(" ", 2)[1].substring(0, mensajeC.split(" ", 2)[1].length() - 1);
							GestorSalida
									.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("<b>[" + _perso.getNombre() + "]</b>  " + mensaje);
							return;
						} else if (mensajeC.length() > 7 && mensajeC.substring(1, 8).equalsIgnoreCase("recurso")) {
							String[] r = mensajeC.split(" ", 3);
							try {
								int cantidad = Integer.parseInt(r[1]);
								int objeto = Integer.parseInt(r[2].split("\\|")[1].split("!")[0]);
								ObjetoModelo objMod = MundoDofus.getObjModelo(objeto);
								if (objMod == null) {
									GestorSalida.ENVIAR_BN_NADA(_out);
									return;
								}
								int tipoObj = objMod.getTipo();
								if (!Bustemu.TIPO_RECURSOS.contains(tipoObj) || Bustemu.OBJETOS_NO_PERMITIDOS.contains(objeto)) {
									GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out,
											"Este tipo de objeto no se puede comprar.");
									return;
								}
								int nivelObj = objMod.getNivel();
								int precio = 100 * cantidad * nivelObj;
								if (_perso.getKamas() >= precio) {
									_perso.setKamas(_perso.getKamas() - precio);
									Objeto obj = objMod.crearObjDesdeModelo(cantidad, false);
									if (!_perso.addObjetoSimilar(obj, true, -1)) {
										MundoDofus.addObjeto(obj, true);
										_perso.addObjetoPut(obj);
										GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, obj);
									}
									GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
									GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
								} else {
									GestorSalida.ENVIAR_Im_INFORMACION(_out, "1128;" + precio);
								}
							} catch (Exception e) {
								GestorSalida.ENVIAR_BN_NADA(_out);
							}
							return;
						} else if (mensajeC.length() > 5 && mensajeC.substring(1, 6).equalsIgnoreCase("feria")) {
							_perso.teleport((short) 6863, 324);
							return;
						} else if (mensajeC.length() > 5 && mensajeC.substring(1, 6).equalsIgnoreCase("infos")) {
							String msj = "===========\n"
									+ "BUSTOFUS (Elbustaemu) "
									+ CentroInfo.VERSION_SERVIDOR
									+ " por "
									// String msj = "===========\n" + Bustemu.NOMBRE_SERVER + " (ALONEEMU) " +
									// CentroInfo.VERSION_SERVIDOR
									+ " por " + CentroInfo.CREADOR + "\nJugadores en línea: "
									+ Bustemu.servidorPersonaje.nroJugadoresLinea() + "\n" + "Record de conexión: "
									+ Bustemu.servidorPersonaje.getRecordJugadores() + "\n" + "===========";
							GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_out, msj);
							return;
						} else if (mensajeC.length() > 2 && mensajeC.substring(1, 3).equalsIgnoreCase("ok")) {
							if (_perso.getEnKoliseo())
								_perso.setAceptar(true);
							return;
						} else if (mensajeC.length() > 5 && mensajeC.substring(1, 6).equalsIgnoreCase("rates")) {
							_perso.mostrarRates();
							return;
						} else if (mensajeC.length() > 5 && mensajeC.substring(1, 6).equalsIgnoreCase("bonju")) {
							GestorSalida.ENVIAR_BAIO_MENSAJE_PANEL(_perso, "SALUT JOUER ^^");
							return;
						} else if (mensajeC.length() > 11 && mensajeC.substring(1, 12).equalsIgnoreCase("borrardoble")) {
							if (_perso.getPelea() != null) {
								GestorSalida.ENVIAR_BN_NADA(_out);
								return;
							}
							if (mensajeC.split(" ").length > 2) {
								GestorSalida.ENVIAR_BN_NADA(_out);
								return;
							}
							String mensaje = mensajeC.split(" ", 2)[1];
							String mensajet = mensaje.substring(0, mensaje.length() - 1);
							short mapaID = (short) Integer.parseInt(mensajet);
							Mapa mapa = MundoDofus.getMapa(mapaID);
							if (mapa == null) {
								GestorSalida.ENVIAR_BN_NADA(_out);
								return;
							}
							mapa.borrarJugador(_perso.getID());
							GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, _perso.getID());
							_perso.teleport(_perso.getMapa().getID(), _perso.getCelda().getID());
							return;
						} else if ( (mensajeC.length() > 6 && mensajeC.substring(1, 7).equalsIgnoreCase("salvar"))
								|| (mensajeC.length() > 4 && mensajeC.substring(1, 5).equalsIgnoreCase("save"))) {
							if ( (System.currentTimeMillis() - _tiempoUltSalvada) < 360000 || _perso.getPelea() != null) {
								GestorSalida.ENVIAR_BN_NADA(_out);
								return;
							}
							_tiempoUltSalvada = System.currentTimeMillis();
							GestorSQL.SALVAR_PERSONAJE(_perso, true);
							if (mensajeC.substring(1, 5).equalsIgnoreCase("save"))
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
										_perso.getNombre() + " a été sauvegardé avec succès.", Bustemu.COLOR_MENSAJE);
							else
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, _perso.getNombre() + " se ha salvado correctamente.",
										Bustemu.COLOR_MENSAJE);
							return;
						} else if (mensajeC.length() > 6
								&& (mensajeC.substring(1, 7).equalsIgnoreCase("puntos") || mensajeC.substring(1, 7)
										.equalsIgnoreCase("points"))) {
							if (mensajeC.substring(1, 7).equalsIgnoreCase("puntos"))
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
										"Usted tiene " + GestorSQL.getPuntosCuenta(_perso.getCuenta().getID())
												+ " puntos de tienda.", Bustemu.COLOR_MENSAJE);
							else
								GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso,
										"Vous avez " + GestorSQL.getPuntosCuenta(_perso.getCuenta().getID()) + " points shop.",
										Bustemu.COLOR_MENSAJE);
							return;
						} else if (mensajeC.length() > 6 && mensajeC.substring(1, 7).equalsIgnoreCase("atacar")) {
							Pelea pelea = _perso.getPelea();
							if (pelea == null) {
								GestorSalida.ENVIAR_BN_NADA(_out);
								return;
							}
							Luchador luchador = pelea.getLuchadorPorPJ(_perso);
							if (!luchador.puedeJugar()) {
								GestorSalida.ENVIAR_BN_NADA(_out);
								return;
							}
							GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(pelea, 7, 2, luchador.getID());
							if (_perso.getClase(true) == 12) {
								if (!luchador.tieneEstado(CentroInfo.ESTADO_PORTADOR))
									GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, luchador.getID() + "", luchador.getID()
											+ "," + CentroInfo.ESTADO_PORTADOR + ",0");
							}
							return;
						} else if (mensajeC.length() > 6 && mensajeC.substring(1, 7).equalsIgnoreCase("startP")) {
							Pelea pelea = _perso.getPelea();
							if (pelea == null) {
								GestorSalida.ENVIAR_BN_NADA(_out);
								return;
							}
							if (pelea.getEstado() < 3) {
								pelea.tiempoTurno();
							}
							return;
						} else if (mensajeC.length() > 6 && mensajeC.substring(1, 7).equalsIgnoreCase("turnoP")) {
							Pelea pelea = _perso.getPelea();
							if (pelea == null) {
								GestorSalida.ENVIAR_BN_NADA(_out);
								return;
							}
							if (pelea.getEstado() == 3) {
								pelea.turnoDebug();
							}
							return;
						} else if (mensajeC.length() > 13 && mensajeC.substring(1, 14).equalsIgnoreCase("dragopavovip_")) {
							if (_perso.getPelea() != null) {
								GestorSalida.ENVIAR_BN_NADA(_out);
								return;
							}
							Dragopavo drago = _perso.getMontura();
							if (drago == null || drago.getColor() != 75) {
								GestorSalida.ENVIAR_BN_NADA(_out);
								return;
							}
							if (_perso.estaMontando())
								_perso.subirBajarMontura();
							String definicion = mensajeC.substring(14, mensajeC.length() - 1);
							drago.setStatsVIP(definicion);
							GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_perso, "Los stats de tu montura ya fueron cambiados.",
									Bustemu.COLOR_MENSAJE);
							_perso.subirBajarMontura();
							return;
						
						
						}
					} catch (Exception e) {}
				}
				if (_perso.getPelea() == null)
					GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_MAPA(_perso.getMapa(), "", _perso.getID(), _perso.getNombre(), mensajeC);
				else
					GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PELEA(_perso.getPelea(), 7, "", _perso.getID(), _perso.getNombre(),
							mensajeC);
				break;
			case '#':// Canal Equipo
				if (!_perso.getCanal().contains(packet.charAt(2) + "")) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				if (_perso.getPelea() != null) {
					mensajeC = packet.split("\\|", 2)[1];
					int team = _perso.getPelea().getParamEquipo(_perso.getID());
					if (team == -1) {
						GestorSalida.ENVIAR_BN_NADA(_out);
						return;
					}
					GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PELEA(_perso.getPelea(), team, "#", _perso.getID(), _perso.getNombre(),
							mensajeC);
				}
				break;
			case '$':// Canal grupo
				if (!_perso.getCanal().contains(packet.charAt(2) + "")) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				if (_perso.getGrupo() == null) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				mensajeC = packet.split("\\|", 2)[1];
				GestorSalida.ENVIAR_cMK_MENSAJE_CHAT_GRUPO(_perso.getGrupo(), "$", _perso.getID(), _perso.getNombre(), mensajeC);
				break;
			case ':':// Canal comercio
				if (!_perso.getCanal().contains(packet.charAt(2) + "")) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				long l;
				if ( (l = System.currentTimeMillis() - _tiempoUltComercio) < 20000) {
					l = (20000 - l) / 1000;
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "0115;" + ((int) Math.ceil(l) + 1));
					return;
				}
				_tiempoUltComercio = System.currentTimeMillis();
				mensajeC = packet.split("\\|", 2)[1];
				GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(":", _perso.getID(), _perso.getNombre(), mensajeC);
				break;
			case '@':// Canal Admin
				if (_perso.getCuenta().getRango() <= 1) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				mensajeC = packet.split("\\|", 2)[1];
				GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_ADMINS("@", _perso.getID(), _perso.getNombre(), mensajeC);
				break;
			case '?':// Canal reclutamiento
				if (!_perso.getCanal().contains(packet.charAt(2) + "")) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				long j;
				if ( (j = System.currentTimeMillis() - _tiempoUltReclutamiento) < 20000) {
					j = (20000 - j) / 1000;
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "0115;" + ((int) Math.ceil(j) + 1));
					return;
				}
				_tiempoUltReclutamiento = System.currentTimeMillis();
				mensajeC = packet.split("\\|", 2)[1];
				GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS("?", _perso.getID(), _perso.getNombre(), mensajeC);
				break;
			case '%':// Canal gremio
				if (!_perso.getCanal().contains(packet.charAt(2) + "")) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				if (_perso.getGremio() == null) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				mensajeC = packet.split("\\|", 2)[1];
				GestorSalida
						.ENVIAR_cMK_CHAT_MENSAJE_GREMIO(_perso.getGremio(), "%", _perso.getID(), _perso.getNombre(), mensajeC);
				break;
			case 0xC2:// Canal
				break;
			case '!':// canal Alineamiento
				if (!_perso.getCanal().contains(packet.charAt(2) + "")) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				if (_perso.getAlineacion() == 0) {
					GestorSalida.ENVIAR_BN_NADA(_out);
					return;
				}
				if (_perso.getDeshonor() >= 1) {
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "183");
					return;
				}
				long k;
				if ( (k = System.currentTimeMillis() - _tiempoUltAlineacion) < Bustemu.TIEMPO_COMERCIO_RECLUTA) {
					k = (Bustemu.TIEMPO_COMERCIO_RECLUTA - k) / 1000;
					GestorSalida.ENVIAR_Im_INFORMACION(_out, "0115;" + ((int) Math.ceil(k) + 1));
					return;
				}
				_tiempoUltAlineacion = System.currentTimeMillis();
				mensajeC = packet.split("\\|", 2)[1];
				GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_ALINEACION("!", _perso.getID(), _perso.getNombre(), mensajeC, _perso);
				break;
			default:
				String nombre = packet.substring(2).split("\\|")[0];
				mensajeC = packet.split("\\|", 2)[1];
				if (nombre.length() <= 1)
					break;
				else {
					Personaje perso = MundoDofus.getPjPorNombre(nombre);
					if (perso == null) {
						GestorSalida.ENVIAR_cMEf_CHAT_ERROR(_out, nombre);
						return;
					}
					Cuenta cuenta = perso.getCuenta();
					if (cuenta == null) {
						GestorSalida.ENVIAR_cMEf_CHAT_ERROR(_out, nombre);
						return;
					}
					EntradaPersonaje gestor = cuenta.getEntradaPersonaje();
					if (gestor == null) {
						GestorSalida.ENVIAR_cMEf_CHAT_ERROR(_out, nombre);
						return;
					}
					if (cuenta.esEnemigo(_perso.getCuenta().getID()) == true || !perso.estaDisponible(_perso)) {
						GestorSalida.ENVIAR_Im_INFORMACION(_out, "114;" + perso.getNombre());
						return;
					}
					GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(perso, "F", _perso.getID(), _perso.getNombre(), mensajeC);
					GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(_perso, "T", perso.getID(), perso.getNombre(), mensajeC);
				}
				break;
		}
	}
	
	private void basicos_Enviar_Fecha() {
		GestorSalida.ENVIAR_BD_FECHA_SERVER(_out);
		GestorSalida.ENVIAR_BT_TIEMPO_SERVER(_out);
	}
	
	private void basicos_Mensaje_Informacion(String packet) {
		packet = packet.substring(2);
		Personaje perso = MundoDofus.getPjPorNombre(packet);
		if (perso == null)
			return;
		GestorSalida.GAME_SEND_BWK(_perso, perso.getCuenta().getApodo() + "|1|" + perso.getNombre() + "|-1");
	}
	
	private void analizar_Juego(String packet) {
		switch (packet.charAt(1)) {
			case 'A':
				if (_perso == null)
					return;
				juego_Acciones(packet);
				break;
			case 'C':
				if (_perso == null)
					return;
				_perso.crearJuegoPJ();
				break;
			case 'f':
				juego_Mostrar_Celda(packet);
				break;
			case 'F':
				_perso.setFantasma();
				break;
			case 'I':
				if (_perso == null)
					return;
				juego_Extra_Informacion();
				break;
			case 'K':
				juego_Finalizar_Accion(packet);
				break;
			case 'P':
				_perso.botonActDesacAlas(packet.charAt(2));
				break;
			case 'p':
				juego_Cambio_Posicion(packet);
				break;
			case 'Q':
				juego_Retirar_Pelea(packet);
				break;
			case 'R':
				juego_Listo(packet);
				break;
			case 't':
				Pelea pelea = _perso.getPelea();
				if (pelea == null)
					return;
				pelea.pasarTurno(_perso);
				break;
		}
	}
	
	private void casa_Accion(String packet) {
		int actionID = Integer.parseInt(packet.substring(5));
		Casa casa = _perso.getCasa();
		if (casa == null)
			return;
		switch (actionID) {
			case 81:// Codificar una casa
				casa.bloquear(_perso);
				break;
			case 97:// Comprar Casa
				casa.comprarEstaCasa(_perso);
				break;
			case 98:// Vender
			case 108:// Modifier precio de venta
				casa.venderla(_perso);
				break;
		}
	}
	
	private void juego_Retirar_Pelea(String packet) {
		int objetivoID = -1;
		if (!packet.substring(2).isEmpty()) {
			try {
				objetivoID = Integer.parseInt(packet.substring(2));
			} catch (Exception e) {}
		}
		Pelea pelea = _perso.getPelea();
		if (pelea == null) {
			return;
		}
		if (objetivoID > 0) {
			Personaje expulsado = MundoDofus.getPersonaje(objetivoID);
			if (expulsado == null || expulsado.getPelea() == null
					|| expulsado.getPelea().getParamEquipo(expulsado.getID()) != pelea.getParamEquipo(_perso.getID()))
				return;
			pelea.retirarsePelea(_perso, expulsado);
		} else {
			pelea.retirarsePelea(_perso, null);
		}
	}
	
	private void juego_Mostrar_Celda(String packet) {
		if (_perso == null || _perso.getPelea() == null || _perso.getPelea().getEstado() != CentroInfo.PELEA_ESTADO_ACTIVO)
			return;
		int celdaID = -1;
		try {
			celdaID = Integer.parseInt(packet.substring(2));
		} catch (Exception e) {}
		if (celdaID == -1)
			return;
		GestorSalida.ENVIAR_Gf_MOSTRAR_CASILLA_EN_PELEA(_perso.getPelea(), 7, _perso.getID(), celdaID);
	}
	
	private void juego_Listo(String packet) {
		Pelea pelea = _perso.getPelea();
		if (pelea == null || pelea.getEstado() != CentroInfo.PELEA_ESTADO_POSICION)
			return;
		_perso.setListo(packet.substring(2).equalsIgnoreCase("1"));
		pelea.verificaTodosListos();
		GestorSalida.ENVIAR_GR_TODOS_LUCHADORES_LISTOS(pelea, 3, _perso.getID(), packet.substring(2).equalsIgnoreCase("1"));
	}
	
	private void juego_Cambio_Posicion(String packet) {
		if (_perso.getPelea() == null)
			return;
		try {
			int celda = Integer.parseInt(packet.substring(2));
			_perso.getPelea().cambiarLugar(_perso, celda);
		} catch (NumberFormatException e) {
			return;
		}
	}
	
	private void juego_Finalizar_Accion(String packet) {
		int idUnica = -1;
		String[] infos = packet.substring(3).split("\\|");
		try {
			idUnica = Integer.parseInt(infos[0]);
		} catch (Exception e) {
			return;
		}
		if (idUnica == -1)
			return;
		AccionDeJuego AJ = _acciones.get(idUnica);
		if (AJ == null)
			return;
		boolean esOk = packet.charAt(2) == 'K';
		switch (AJ._accionID) {
			case 1:// Desplazamiento, moverse
				if (esOk) {
					if (_perso.getPelea() == null) {
						String path = AJ._args;
						Mapa mapa = _perso.getMapa();
						Celda sigCelda = mapa.getCelda(Encriptador.celdaCodigoAID(path.substring(path.length() - 2)));
						if (sigCelda == null) {
							GestorSalida.ENVIAR_BN_NADA(_out);
							return;
						}
						Celda celdaObjetivo = mapa
								.getCelda(Encriptador.celdaCodigoAID(AJ._packet.substring(AJ._packet.length() - 2)));
						_perso.setCelda(sigCelda);
						_perso.setOrientacion(Encriptador.getNumeroPorValorHash(path.charAt(path.length() - 3)));
						ObjetoInteractivo objeto = null;
						if (celdaObjetivo != null)
							objeto = celdaObjetivo.getObjetoInterac();
						if (_perso.estaOcupado())
							_perso.setOcupado(false);
						if (objeto != null) {
							if (objeto.getID() == 1324) {
								CentroInfo.aplicarAccionOI(_perso, mapa.getID(), celdaObjetivo.getID());
							} else if (objeto.getID() == 542) {
								if (_perso.esFantasma() || _perso.getGfxID() == 8004)
									_perso.setRevivir();
							}
						}
						mapa.jugadorLLegaACelda(_perso, _perso.getCelda().getID());
					} else {
						_perso.getPelea().finalizarMovimiento(_perso);
						return;
					}
				} else {
					int nuevaCeldaID = -1;
					try {
						nuevaCeldaID = Integer.parseInt(infos[1]);
					} catch (Exception e) {
						return;
					}
					if (nuevaCeldaID == -1)
						return;
					Celda celda = _perso.getMapa().getCelda(nuevaCeldaID);
					String path = AJ._args;
					_perso.setCelda(celda);
					_perso.setOrientacion(Encriptador.getNumeroPorValorHash(path.charAt(path.length() - 3)));
					GestorSalida.ENVIAR_BN_NADA(_out);
				}
				break;
			case 500:// Accion sobre el mapa
				_perso.finalizarAccionEnCelda(AJ);
				break;
			default:
				System.out.println("No se ha establecido el final de la accion ID: " + AJ._accionID);
		}
		borrarGA(AJ);
	}
	
	private void juego_Extra_Informacion() {
		if (_perso.getPelea() != null) {
			GestorSalida.ENVIAR_GDK_CARGAR_MAPA(_out);// GDK
			try {
				Thread.sleep(500);
			} catch (Exception e) {}
			if (_perso.getReconectado()) {
				_perso.getPelea().reconectarLuchador(_perso);
				_perso.setReconectado(false);
			}
			return;
		}
		if (_perso.getDefendiendo()) {
			_perso.setDefendiendo(false);
			return;
		}
		Mapa mapa = _perso.getMapa();
		if (_perso.getPelea() != null) {
			GestorSalida.ENVIAR_GM_PERSONAJE_A_MAPA(mapa, _perso);// GM|+
			GestorSalida.ENVIAR_GDK_CARGAR_MAPA(_out);// GDK
			return;
		}
		if (_perso.getMapaDefPerco() != null) {
			_perso.setMapa(_perso.getMapaDefPerco());
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, _perso.getID());
			_perso.setCelda(_perso.getCeldaDefPerco());
		}
		GestorSalida.ENVIAR_Rp_INFORMACION_CERCADO(_perso, mapa.getCercado());// Rp
		GestorSalida.ENVIAR_GM_PERSONAJE_A_MAPA(mapa, _perso);// GM|+
		GestorSalida.ENVIAR_GM_GRUPOMOBS(_out, mapa);// GM|+
		GestorSalida.ENVIAR_GM_NPCS(_out, mapa);// GM|+
		GestorSalida.ENVIAR_GM_RECAUDADORES_EN_MAPA(_out, mapa);// GM|
		GestorSalida.ENVIAR_GDF_OBJETOS_INTERACTIVOS(_out, mapa);// GDF|
		GestorSalida.ENVIAR_GDK_CARGAR_MAPA(_out);// GDK
		GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(_out, mapa);// fCmapfight
		GestorSalida.ENVIAR_GM_MERCANTES(_out, mapa);
		GestorSalida.ENVIAR_GM_PRISMAS_EN_MAPA(_out, mapa);
		GestorSalida.ENVIAR_GM_MONTURAS_EN_MAPA(_out, mapa);
		GestorSalida.ENVIAR_GDO_OBJETOS_CRIAS_EN_MAPA(_out, mapa);
		GestorSalida.ENVIAR_ILS_TIEMPO_REGENERAR_VIDA(_out, 1000);
		Casa.cargarCasa(_perso, mapa.getID());
		Pelea.agregarEspadaDePelea(mapa, _perso);
		mapa.objetosTirados(_perso);
		if (mapa.esTaller() && _perso.getOficioPublico()) {
			GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(_out, "+", _perso.getID(), _perso.getStringOficiosPublicos());
		}
	}
	
	private void juego_Acciones(String packet) {
		int accionID;
		try {
			accionID = Integer.parseInt(packet.substring(2, 5));
		} catch (NumberFormatException e) {
			return;
		}
		int sigAccionJuegoID = 0;
		if (_acciones.size() > 0) {
			sigAccionJuegoID = (Integer) (_acciones.keySet().toArray()[_acciones.size() - 1]) + 1;
		}
		AccionDeJuego AJ = new AccionDeJuego(sigAccionJuegoID, accionID, packet);
		switch (accionID) {
			case 1:// Desplazamiento
				juego_Desplazamiento(AJ);
				break;
			case 300:// hechizo
				juego_Lanzar_Hechizo(packet);
				break;
			case 303:// Ataque cuerpo a cuerpo
				juego_Ataque_CAC(packet);
				break;
			case 500:// Action sobre el mapa
				juego_Accion(AJ);
				_perso.setTaller(AJ);
				break;
			case 512:// usar prisma
				_perso.abrirMenuPrisma();
				break;
			case 507:// Panel interior de casa
				casa_Accion(packet);
				break;
			case 618:// Matrimonio Si
				_perso.setEsOK(Integer.parseInt(packet.substring(5, 6)));
				GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_MAPA(_perso.getMapa(), "", _perso.getID(), _perso.getNombre(), "Sí");
				if (MundoDofus.getCasado(0).getEsOK() > 0 && MundoDofus.getCasado(1).getEsOK() > 0)
					MundoDofus.Wedding(MundoDofus.getCasado(0), MundoDofus.getCasado(1), 1);
				break;
			case 619:// Matrimonio No
				_perso.setEsOK(0);
				GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_MAPA(_perso.getMapa(), "", _perso.getID(), _perso.getNombre(),
						"No, lo siento");
				MundoDofus.Wedding(MundoDofus.getCasado(0), MundoDofus.getCasado(1), 0);
				break;
			case 900:// Solicita duelo
				if (_perso.esFantasma())
					return;
				juego_Desafiar(packet);
				break;
			case 901:// Acepta duelo
				juego_Aceptar_Desafio(packet);
				break;
			case 902:// Rechazar/anular desafio
				juego_Cancelar_Desafio(packet);
				break;
			case 903:// unir al combate
				juego_Unirse_Pelea(packet);
				break;
			case 906:// Agresion
				juego_Agresion(packet);
				break;
			case 909:// Recaudador
				juego_Ataque_Recaudador(packet);
				break;
			case 919:// ataque de mutante
				break;
			case 912:// ataque prisma
				juego_Ataque_Prisma(packet);
				break;
		}
	}
	
	private void juego_Ataque_Recaudador(String packet) {
		try {
			if (_perso.esFantasma() || _perso.getPelea() != null || _perso.estaOcupado()) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			int id = Integer.parseInt(packet.substring(5));
			Recaudador recaudador = MundoDofus.getRecaudador(id);
			if (recaudador == null || recaudador.getEstadoPelea() > 0)
				return;
			if (recaudador.getEnRecolecta()) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "1180");
				return;
			}
			GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(_perso.getMapa(), "", 909, _perso.getID() + "", id + "");
			_perso.getMapa().iniciarPeleaVSRecaudador(_perso, recaudador);
		} catch (Exception e) {}
	}
	
	private void juego_Ataque_Prisma(String packet) {
		try {
			if (_perso.esFantasma() || _perso.getPelea() != null || _perso.estaOcupado()) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			int id = Integer.parseInt(packet.substring(5));
			Prisma prisma = MundoDofus.getPrisma(id);
			if ( (prisma.getEstadoPelea() == 0 || prisma.getEstadoPelea() == -2))
				return;
			GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(_perso.getMapa(), "", 909, _perso.getID() + "", id + "");
			_perso.getMapa().iniciarPeleaVSPrisma(_perso, prisma);
		} catch (Exception e) {}
	}
	
	private void juego_Agresion(String packet) {
		try {
			if (_perso.esFantasma() || _perso.getPelea() != null || _perso.estaOcupado()) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			int id = Integer.parseInt(packet.substring(5));
			Personaje agredido = MundoDofus.getPersonaje(id);
			Mapa mapa = _perso.getMapa();
			if (agredido == null || !agredido.enLinea() || agredido.esFantasma() || agredido.getPelea() != null
					|| agredido.estaOcupado() || agredido.getMapa().getID() != mapa.getID()
					|| agredido.getAlineacion() == _perso.getAlineacion() || mapa.getLugaresString().equalsIgnoreCase("|")
					|| mapa.getLugaresString().isEmpty()) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, "No se le puede agredir al objetivo, por diferentes causas.");
				return;
			}
			if (agredido.getAlineacion() == 0) {
				_perso.setDeshonor(_perso.getDeshonor() + 1);
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "084;1");
			}
			_perso.botonActDesacAlas('+');
			if (agredido.getAgresion() || _perso.getAgresion()) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_out, "El objetivo se encuentra en una agresion.");
				return;
			}
			agredido.setAgresion(true);
			_perso.setAgresion(true);
			GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(mapa, "", 906, _perso.getID() + "", id + "");
			mapa.nuevaPelea(_perso, agredido, CentroInfo.PELEA_TIPO_PVP);
			agredido.setAgresion(false);
			_perso.setAgresion(false);
		} catch (Exception e) {}
	}
	
	public void juego_Accion(AccionDeJuego AJ) {
		String packet = AJ._packet.substring(5);
		int celdaID = -1;
		int accionID = -1;
		try {
			celdaID = Integer.parseInt(packet.split(";")[0]);
			accionID = Integer.parseInt(packet.split(";")[1]);
		} catch (Exception e) {}
		if (celdaID == -1 || accionID == -1 || _perso == null || _perso.getMapa() == null
				|| _perso.getMapa().getCelda(celdaID) == null)
			return;
		AJ._args = celdaID + ";" + accionID;
		addGA(AJ);
		_perso.iniciarAccionEnCelda(AJ);
	}
	
	private void juego_Ataque_CAC(String packet) {
		try {
			if (_perso.getPelea() == null)
				return;
			int celdaID = -1;
			try {
				celdaID = Integer.parseInt(packet.substring(5));
			} catch (Exception e) {
				return;
			}
			_perso.getPelea().intentarCaC(_perso, celdaID);
		} catch (Exception e) {}
	}
	
	private void juego_Lanzar_Hechizo(String packet) {
		try {
			String[] splt = packet.split(";");
			int hechizoID = Integer.parseInt(splt[0].substring(5));
			int celdaID = Integer.parseInt(splt[1]);
			Pelea pelea = _perso.getPelea();
			if (pelea != null) {
				StatsHechizos SS = _perso.getStatsHechizo(hechizoID);
				if (SS == null)
					return;
				pelea.intentarLanzarHechizo(pelea.getLuchadorPorPJ(_perso), SS, celdaID);
			}
		} catch (NumberFormatException e) {
			return;
		}
	}
	
	private void juego_Unirse_Pelea(String packet) {
		String[] infos = packet.substring(5).split(";");
		if (infos.length == 1) {
			try {
				if (_perso.getPelea() != null) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(_out, 'o');
					return;
				}
				Pelea pelea = _perso.getMapa().getPelea(Integer.parseInt(infos[0]));
				pelea.unirseEspectador(_perso);
			} catch (Exception e) {
				return;
			}
		} else {
			try {
				if (_perso.getPelea() != null) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(_out, 'o');
					return;
				}
				int id = Integer.parseInt(infos[1]);
				if (_perso.estaOcupado()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(_out, 'o');
					return;
				}
				if (_perso.esFantasma()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(_out, 'd');
					return;
				}
				if (id < -100) {
					int resta = (id + 100) % 3;
					if (resta == -2) {
						Prisma prisma = MundoDofus.getPrisma(id);
						if (prisma == null) {
							GestorSalida.ENVIAR_BN_NADA(_out);
							return;
						}
						short mapaID = prisma.getMapa();
						int celdaID = prisma.getCelda();
						if (prisma.getAlineacion() != _perso.getAlineacion())
							return;
						if (prisma.getPelea().unirsePeleaPrisma(_perso, id, mapaID, celdaID)) {
							for (Personaje z : MundoDofus.getPJsEnLinea()) {
								if (z == null || z.getAlineacion() != _perso.getAlineacion())
									continue;
								Prisma.analizarDefensa(z);
							}
						}
					} else if (resta == 0) {
						Recaudador recau = MundoDofus.getRecaudador(id);
						if (recau == null) {
							GestorSalida.ENVIAR_BN_NADA(_out);
							return;
						}
						short mapaID = (short) recau.getMapaID();
						int celdaID = recau.getCeldalID();
						if (_perso.getPelea() != null)
							return;
						if (recau.getPelea().unirsePeleaRecaudador(_perso, id, mapaID, celdaID)) {
							for (Personaje miembros : _perso.getGremio().getPjMiembros()) {
								if (miembros == null || !miembros.enLinea())
									continue;
								Recaudador.analizarDefensa(miembros, _perso.getGremio().getID());
							}
						}
					}
				} else {
					MundoDofus.getPersonaje(id).getPelea().unirsePelea(_perso, id);
				}
			} catch (Exception e) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
		}
	}
	
	private void juego_Aceptar_Desafio(String packet) {
		int id = -1;
		try {
			id = Integer.parseInt(packet.substring(5));
		} catch (NumberFormatException e) {
			return;
		}
		int idDuelo = _perso.getDueloID();
		if (idDuelo != id || idDuelo == -1)
			return;
		Mapa mapa = _perso.getMapa();
		GestorSalida.ENVIAR_GA901_ACEPTAR_DESAFIO(mapa, idDuelo, _perso.getID());
		Pelea pelea = mapa.nuevaPelea(MundoDofus.getPersonaje(idDuelo), _perso, CentroInfo.PELEA_TIPO_DESAFIO);
		_perso.setPelea(pelea);
		MundoDofus.getPersonaje(idDuelo).setPelea(pelea);
	}
	
	private void juego_Cancelar_Desafio(String packet) {
		if (_perso.getDueloID() == -1)
			return;
		GestorSalida.ENVIAR_GA902_RECHAZAR_DESAFIO(_perso.getMapa(), _perso.getDueloID(), _perso.getID());
		try {
			Personaje desafiador = MundoDofus.getPersonaje(_perso.getDueloID());
			desafiador.setOcupado(false);
			desafiador.setDueloID(-1);
		} catch (NullPointerException e) {}
		_perso.setOcupado(false);
		_perso.setDueloID(-1);
	}
	
	private void juego_Desafiar(String packet) {
		Mapa mapa = _perso.getMapa();
		int idPerso = _perso.getID();
		if (mapa.getLugaresString().equalsIgnoreCase("|") || mapa.getLugaresString().isEmpty()) {
			GestorSalida.ENVIAR_GA903_ERROR_PELEA(_out, 'p');
			return;
		}
		try {
			int id = Integer.parseInt(packet.substring(5));
			if (_perso.estaOcupado() || _perso.getPelea() != null) {
				GestorSalida.ENVIAR_GA903_UNIRSE_PELEA_Y_ESTAR_OCUPADO(_out, idPerso);
				return;
			}
			Personaje desafiado = MundoDofus.getPersonaje(id);
			if (desafiado == null)
				return;
			if (desafiado.estaOcupado() || desafiado.getPelea() != null || desafiado.getMapa().getID() != mapa.getID()) {
				GestorSalida.ENVIAR_GA903_UNIRSE_PELEA_Y_OPONENTE_OCUPADO(_out, idPerso);
				return;
			}
			_perso.setDueloID(id);
			_perso.setOcupado(true);
			desafiado.setDueloID(_perso.getID());
			desafiado.setOcupado(true);
			GestorSalida.ENVIAR_GA900_DESAFIAR(mapa, idPerso, id);
		} catch (NumberFormatException e) {
			return;
		}
	}
	
	private void juego_Desplazamiento(AccionDeJuego AJ) {
		String path = AJ._packet.substring(5);
		Pelea pelea = _perso.getPelea();
		if (pelea == null) {
			if (_perso.esTumba()) {
				GestorSalida.ENVIAR_BN_NADA(_out);
				return;
			}
			if (_perso.getPodUsados() > _perso.getMaxPod()) {
				GestorSalida.ENVIAR_Im_INFORMACION(_out, "112");
				_perso.setOcupado(false);
				GestorSalida.ENVIAR_GA_ACCION_DE_JUEGO(_out, "", "0", "", "");
				borrarGA(AJ);
				return;
			}
			AtomicReference<String> pathRef = new AtomicReference<String>(path);
			int celdaID = _perso.getCelda().getID();
			Mapa mapa = _perso.getMapa();
			int result = Camino.caminoValido(mapa, celdaID, pathRef, null);
			if (result == 0) {
				GestorSalida.ENVIAR_GA_ACCION_DE_JUEGO(_out, "", "0", "", "");
				borrarGA(AJ);
				return;
			}
			if (result != -1000 && result < 0)
				result = -result;
			path = pathRef.get();
			if (result == -1000) {
				path = Encriptador.getValorHashPorNumero(_perso.getOrientacion()) + Encriptador.celdaIDACodigo(celdaID);
			}
			AJ._args = path;
			GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(mapa, "" + AJ._idUnica, 1, _perso.getID() + "",
					"a" + Encriptador.celdaIDACodigo(celdaID) + path);
			addGA(AJ);
			if (_perso.estaSentado())
				_perso.setSentado(false);
			_perso.setOcupado(true);
		} else {
			Luchador luchador = pelea.getLuchadorPorPJ(_perso);
			if (luchador == null)
				return;
			AJ._args = path;
			pelea.puedeMoverseLuchador(luchador, AJ);
		}
	}
	
	private void analizar_Cuenta(String packet) {
		switch (packet.charAt(1)) {
			case 'A':
				cuenta_Crear_Personaje(packet);
				break;
			case 'B':
				int stat = -1;
				try {
					stat = Integer.parseInt(packet.substring(2).split("/u000A")[0]);
					_perso.boostStat(stat);
				} catch (NumberFormatException e) {
					return;
				}
				break;
			case 'D':
				cuenta_Eliminar_Personaje(packet);
				break;
			case 'f':
				int colaID = 1;
				int posicion = 1;
				GestorSalida.ENVIAR_Af_ABONADOS_POSCOLA(_out, posicion, 1, 1, "" + 1, colaID);
				break;
			case 'g':
				int regalo = _cuenta.getRegalo();
				if (regalo != 0) {
					String idModObjeto = Integer.toString(regalo, 16);
					String efectos = MundoDofus.getObjModelo(regalo).getStringStatsObj();
					GestorSalida.ENVIAR_Ag_LISTA_REGALOS(_out, regalo, "1~" + idModObjeto + "~1~~" + efectos);
				}
				break;
			case 'G':
				cuenta_Entregar_Regalo(packet.substring(2));
			case 'i':
				_cuenta.setClaveCliente(packet.substring(2));
				break;
			case 'L':
				GestorSalida.ENVIAR_ALK_LISTA_DE_PERSONAJES(_out, _cuenta.getPersonajes());
				break;
			case 'S':
				int idPerso = Integer.parseInt(packet.substring(2));
				if (_cuenta.getPersonajes().get(idPerso) != null) {
					_cuenta.setEntradaPersonaje(this);
					_perso = _cuenta.getPersonajes().get(idPerso);
					if (_perso != null) {
						_perso.Conectarse();
						return;
					}
				}
				GestorSalida.ENVIAR_ASE_SELECCION_PERSONAJE_FALLIDA(_out);
				break;
			case 'T':
				try {
					int id = Integer.parseInt(packet.substring(2));
					_cuenta = Bustemu.servidorPersonaje.getEsperandoCuenta(id);
					if (_cuenta != null) {
						String ip = _socket.getInetAddress().getHostAddress();
						_cuenta.setEntradaPersonaje(this);
						_cuenta.setTempIP(ip);
						Bustemu.servidorPersonaje.delEsperandoCuenta(_cuenta);
						GestorSalida.GAME_SEND_ATTRIBUTE_SUCCESS(_out);
					} else {
						GestorSalida.GAME_SEND_ATTRIBUTE_FAILED(_out);
					}
				} catch (Exception e) {
					return;
				}
				break;
			case 'V':
				GestorSalida.ENVIAR_AV_VERSION_REGIONAL(_out);
				break;
			case 'P':
				GestorSalida.REALM_SEND_REQUIRED_APK(_out);
				break;
		}
	}
	
	private void cuenta_Entregar_Regalo(String packet) {
		String[] info = packet.split("\\|");
		int idObjeto = Integer.parseInt(info[0]);
		int idPj = Integer.parseInt(info[1]);
		Personaje pj = null;
		Objeto objeto = null;
		try {
			pj = MundoDofus.getPersonaje(idPj);
			objeto = MundoDofus.getObjModelo(idObjeto).crearObjDesdeModelo(1, true);
		} catch (Exception e) {}
		if (pj == null || objeto == null) {
			return;
		}
		if (!pj.addObjetoSimilar(objeto, true, -1)) {
			MundoDofus.addObjeto(objeto, true);
			pj.addObjetoPut(objeto);
			GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pj, objeto);
		}
		_cuenta.setRegalo();
		GestorSQL.ACTUALIZAR_REGALO(_cuenta);
		GestorSalida.ENVIAR_AG_SIGUIENTE_REGALO(_out);
	}
	
	private void cuenta_Eliminar_Personaje(String packet) {
		String[] split = packet.substring(2).split("\\|");
		int id = Integer.parseInt(split[0]);
		String reponse = split.length > 1 ? split[1] : "";
		if (_cuenta.getPersonajes().containsKey(id)) {
			if (_cuenta.getPersonajes().get(id).getNivel() < 100
					|| (_cuenta.getPersonajes().get(id).getNivel() >= 100 && reponse.equals(_cuenta.getRespuesta()))) {
				_cuenta.borrarPerso(id);
				GestorSalida.ENVIAR_ALK_LISTA_DE_PERSONAJES(_out, _cuenta.getPersonajes());
			} else
				GestorSalida.ENVIAR_ADE_ERROR_BORRAR_PJ(_out);
		} else
			GestorSalida.ENVIAR_ADE_ERROR_BORRAR_PJ(_out);
	}
	
	private void cuenta_Crear_Personaje(String packet) {
		String[] infos = packet.substring(2).split("\\|");
		if (GestorSQL.personajeYaExiste(infos[0])) {
			GestorSalida.ENVIAR_AAEa_NOMBRE_YA_EXISTENTE(_out);
			return;
		}
		boolean esValido = true;
		String nombre = infos[0].toLowerCase();
		if (nombre.length() > 20) {
			esValido = false;
		}
		if (esValido) {
			int cantSimbol = 0;
			char letra_A = ' ';
			char letra_B = ' ';
			for (char letra : nombre.toCharArray()) {
				if (! ( (letra >= 'a' && letra <= 'z') || letra == '-')) {
					esValido = false;
					break;
				}
				if (letra == letra_A && letra == letra_B) {
					esValido = false;
					break;
				}
				if (letra >= 'a' && letra <= 'z') {
					letra_A = letra_B;
					letra_B = letra;
				}
				if (letra == '-') {
					if (cantSimbol >= 1) {
						esValido = false;
						break;
					} else {
						cantSimbol++;
					}
				}
			}
		}
		if (!esValido) {
			GestorSalida.ENVIAR_AAEa_NOMBRE_YA_EXISTENTE(_out);
			return;
		}
		if (_cuenta.getNumeroPersonajes() >= Bustemu.MAX_PJS_POR_CUENTA) {
			GestorSalida.ENVIAR_AAEf_MAXIMO_PJS_CREADOS(_out);
			return;
		}
		if (_cuenta.crearPj(infos[0], Integer.parseInt(infos[2]), Integer.parseInt(infos[1]), Integer.parseInt(infos[3]),
				Integer.parseInt(infos[4]), Integer.parseInt(infos[5]))) {
			GestorSalida.ENVIAR_AAK_CREACION_PJ(_out);
			GestorSalida.ENVIAR_ALK_LISTA_DE_PERSONAJES(_out, _cuenta.getPersonajes());
		} else {
			GestorSalida.ENVIAR_AAEF_ERROR_CREAR_PJ(_out);
		}
	}
	
	public PrintWriter getOut() {
		return _out;
	}
	
	public void salir() {
		try {
			Bustemu.servidorPersonaje.delGestorCliente(this);
			if (_cuenta != null) {
				_cuenta.desconexion();
			}
			if (!_socket.isClosed())
				_socket.close();
			_in.close();
			_out.close();
			_thread.interrupt();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void closeSocket() {
		try {
			_socket.close();
		} catch (IOException e) {}
	}
	
	public Thread getThread() {
		return _thread;
	}
	
	public Personaje getPersonaje() {
		return _perso;
	}
	
	public void borrarGA(AccionDeJuego AJ) {
		_acciones.remove(AJ._idUnica);
	}
	
	public void addGA(AccionDeJuego AJ) {
		_acciones.put(AJ._idUnica, AJ);
	}
}
