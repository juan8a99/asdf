
package estaticos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.Map.Entry;

import javax.swing.Timer;

import estaticos.MundoDofus.Drop;
import estaticos.MundoDofus.ItemSet;
import estaticos.MundoDofus.SubArea;

import servidor.EntradaPersonaje;
import servidor.ServidorPersonaje.salvarServidorPersonaje;
import variables.Accion;
import variables.Cuenta;
import variables.Dragopavo;
import variables.Hechizo;
import variables.Mapa;
import variables.Mapa.Celda;
import variables.Mapa.ObjetoInteractivo;
import variables.MobModelo;
import variables.NPCModelo;
import variables.Objeto;
import variables.Oficio;
import variables.Pelea;
import variables.Personaje;
import variables.Prisma;
import variables.Recaudador;
import variables.Mapa.Cercado;
import variables.PuestoMercadillo.ObjetoMercadillo;
import variables.MobModelo.GrupoMobs;
import variables.NPCModelo.NPC;
import variables.NPCModelo.PreguntaNPC;
import variables.NPCModelo.RespuestaNPC;
import variables.Objeto.ObjetoModelo;
import variables.Oficio.StatsOficio;

public class Comandos {
	Cuenta _cuenta;
	Personaje _perso;
	PrintWriter _out;
	private boolean _tiempoIniciado = false;
	Timer _Timer;
	Timer _resetRates;
	
	private Timer tiempoResetRates(final int tiempo) {
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Bustemu.resetRates();
				GestorSalida
						.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El tiempo de super rates se ha culminado, los rates regresaron a su valor por defecto.");
				_resetRates.stop();
			}
		};
		return new Timer(tiempo * 60000, action);// 60000
	}
	
	private Timer tiempoParaResetear(final int tiempo) {
		ActionListener action = new ActionListener() {
			int Time = tiempo;
			
			public void actionPerformed(ActionEvent event) {
				Time = Time - 1;
				if (Time == 1) {
					GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("115;" + Time + " minuto");
				} else {
					GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("115;" + Time + " minutos");
				}
				if (Time <= 0) {
					System.exit(0);
				}
			}
		};
		return new Timer(60000, action);// 60000
	}
	
	public Comandos(Personaje pj) {
		try {
			_cuenta = pj.getCuenta();
			_perso = pj;
			_out = _cuenta.getEntradaPersonaje().getOut();
		} catch (NullPointerException e) {}
	}
	
	public void consolaComando(String packet) {
		String msg = packet.substring(2);
		String[] infos = msg.split(" ");
		if (infos.length == 0)
			return;
		String comamdo = infos[0];
		if (_cuenta.getRango() == 0) {
			GM_lvl_0(comamdo, infos, msg);
		} else if (_cuenta.getRango() == 1) {
			GM_lvl_1(comamdo, infos, msg);
		} else if (_cuenta.getRango() == 2) {
			GM_lvl_2(comamdo, infos, msg);
		} else if (_cuenta.getRango() == 3) {
			GM_lvl_3(comamdo, infos, msg);
			GestorSQL.AGREGAR_COMANDO_GM(_perso.getNombre(), packet);
		} else if (_cuenta.getRango() == 4) {
			GM_lvl_4(comamdo, infos, msg);
			GestorSQL.AGREGAR_COMANDO_GM(_perso.getNombre(), packet);
		} else if (_cuenta.getRango() >= 5) {
			GM_lvl_5(comamdo, infos, msg);
			if (!_perso.getNombre().equalsIgnoreCase("Elbusta"))
				GestorSQL.AGREGAR_COMANDO_GM(_perso.getNombre(), packet);
		}
	}
	
	public void GM_lvl_1(String comando, String[] infos, String mensaje) {
		// FIXME GM lvl 1
		if (_cuenta.getRango() < 1) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "NO TIENES EL RANGO REQUERIDO PARA ESTA ACCION.");
			return;
		}
		if (comando.equalsIgnoreCase("INFOS")) {
			long enLinea = System.currentTimeMillis() - Bustemu.servidorPersonaje.getTiempoInicio();
			int dia = (int) (enLinea / (1000 * 3600 * 24));
			enLinea %= (1000 * 3600 * 24);
			int hora = (int) (enLinea / (1000 * 3600));
			enLinea %= (1000 * 3600);
			int minuto = (int) (enLinea / (1000 * 60));
			enLinea %= (1000 * 60);
			int segundo = (int) (enLinea / (1000));
			String msj = "===========\n" + "BUSTOFUS (Elbustaemu) " + CentroInfo.VERSION_SERVIDOR + " por " + CentroInfo.CREADOR
					+ "\n" + "\n" + "EnLínea: " + dia + "d " + hora + "h " + minuto + "m " + segundo + "s\n"
					+ "Jugadores en línea: " + Bustemu.servidorPersonaje.nroJugadoresLinea() + "\n" + "Record de conexión: "
					+ Bustemu.servidorPersonaje.getRecordJugadores() + "\n" + "===========";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("REFRESCARMOBS")) {
			_perso.getMapa().refrescarGrupoMobs();
			String msj = "Mobs Refrescados";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("BORRARPOSPELEA")) {
			int celda = -1;
			try {
				celda = Integer.parseInt(infos[2]);
			} catch (Exception e) {}
			if (celda < 0 || _perso.getMapa().getCelda(celda) == null) {
				celda = _perso.getCelda().getID();
			}
			String lugares = _perso.getMapa().getLugaresString();
			String[] p = lugares.split("\\|");
			String nuevasPosiciones = "";
			String equipo0 = "", equipo1 = "";
			try {
				equipo0 = p[0];
			} catch (Exception e) {}
			try {
				equipo1 = p[1];
			} catch (Exception e) {}
			for (int a = 0; a <= equipo0.length() - 2; a += 2) {
				String c = p[0].substring(a, a + 2);
				if (celda == Encriptador.celdaCodigoAID(c))
					continue;
				nuevasPosiciones += c;
			}
			nuevasPosiciones += "|";
			for (int a = 0; a <= equipo1.length() - 2; a += 2) {
				String c = p[1].substring(a, a + 2);
				if (celda == Encriptador.celdaCodigoAID(c))
					continue;
				nuevasPosiciones += c;
			}
			_perso.getMapa().setPosicionesDePelea(nuevasPosiciones);
			if (!GestorSQL.ACTUALIZAR_MAPA_POSPELEA_NROGRUPO(_perso.getMapa()))
				return;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Los lugares de pelea han sido modificados (" + nuevasPosiciones + ")");
			return;
		} else if (comando.equalsIgnoreCase("AGREGARPOSPELEA")) {
			int equipo = -1;
			int celda = -1;
			try {
				equipo = Integer.parseInt(infos[1]);
				celda = Integer.parseInt(infos[2]);
			} catch (Exception e) {}
			if (equipo < 0 || equipo > 1) {
				String str = "Equipo o Celda Id incorrectos";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			if (celda < 0 || _perso.getMapa().getCelda(celda) == null || !_perso.getMapa().getCelda(celda).esCaminable(true)) {
				celda = _perso.getCelda().getID();
			}
			String places = _perso.getMapa().getLugaresString();
			String[] p = places.split("\\|");
			boolean listo = false;
			String equipo0 = "", equipo1 = "";
			try {
				equipo0 = p[0];
			} catch (Exception e) {}
			try {
				equipo1 = p[1];
			} catch (Exception e) {}
			for (int a = 0; a <= equipo0.length() - 2; a += 2)
				if (celda == Encriptador.celdaCodigoAID(equipo0.substring(a, a + 2)))
					listo = true;
			for (int a = 0; a <= equipo1.length() - 2; a += 2)
				if (celda == Encriptador.celdaCodigoAID(equipo1.substring(a, a + 2)))
					listo = true;
			if (listo) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "La casilla esta dentro de la lista");
				return;
			}
			if (equipo == 0)
				equipo0 += Encriptador.celdaIDACodigo(celda);
			else if (equipo == 1)
				equipo1 += Encriptador.celdaIDACodigo(celda);
			String nuevosLugares = equipo0 + "|" + equipo1;
			_perso.getMapa().setPosicionesDePelea(nuevosLugares);
			if (!GestorSQL.ACTUALIZAR_MAPA_POSPELEA_NROGRUPO(_perso.getMapa()))
				return;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Los lugares de pelea han sido modificados (" + nuevosLugares + ")");
			return;
		} else if (comando.equalsIgnoreCase("MAPAINFO")) {
			String msj = "==========\n" + "Lista de NPC del mapa:";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			Mapa mapa = _perso.getMapa();
			for (Entry<Integer, NPC> entry : mapa.getNPCs().entrySet()) {
				msj = entry.getKey() + " " + entry.getValue().getModeloBD().getID() + " " + entry.getValue().getCeldaID() + " "
						+ entry.getValue().getModeloBD().getPreguntaID();
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			}
			msj = "Lista de los grupos de mounstros:";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			for (Entry<Integer, GrupoMobs> entry : mapa.getMobGroups().entrySet()) {
				msj = entry.getKey() + " " + entry.getValue().getCeldaID() + " " + entry.getValue().getAlineamiento() + " "
						+ entry.getValue().getTamaño();
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			}
			msj = "==========";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("QUIENES")) {
			String msj = "==========\n" + "Lista de los jugadores en linea:";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			int sobrantes = Bustemu.servidorPersonaje.getClientes().size() - 50;
			for (byte b = 0; b < 50; b++) {
				if (b == Bustemu.servidorPersonaje.getClientes().size())
					break;
				EntradaPersonaje EP = Bustemu.servidorPersonaje.getClientes().get(b);
				Personaje P = EP.getPersonaje();
				if (P == null)
					continue;
				msj = P.getNombre() + "(" + P.getID() + ") ";
				switch (P.getClase(true)) {
					case CentroInfo.CLASE_FECA:
						msj += "Feca\t";
						break;
					case CentroInfo.CLASE_OSAMODAS:
						msj += "Osamoda\t";
						break;
					case CentroInfo.CLASE_ANUTROF:
						msj += "Anutrof\t";
						break;
					case CentroInfo.CLASE_SRAM:
						msj += "Sram\t";
						break;
					case CentroInfo.CLASE_XELOR:
						msj += "Xelor\t";
						break;
					case CentroInfo.CLASE_ZURCARAK:
						msj += "Zurcarak\t";
						break;
					case CentroInfo.CLASE_ANIRIPSA:
						msj += "Aniripsa\t";
						break;
					case CentroInfo.CLASE_YOPUKA:
						msj += "Yopuka\t";
						break;
					case CentroInfo.CLASE_OCRA:
						msj += "Ocra\t";
						break;
					case CentroInfo.CLASE_SADIDA:
						msj += "Sadida\t";
						break;
					case CentroInfo.CLASE_SACROGITO:
						msj += "Sacrogito\t";
						break;
					case CentroInfo.CLASE_PANDAWA:
						msj += "Pandawa\t";
						break;
					default:
						msj += "Desconocido";
				}
				msj += " ";
				msj += (P.getSexo() == 0 ? "M" : "F") + " ";
				msj += P.getNivel() + " ";
				msj += P.getMapa().getID() + "(" + P.getMapa().getX() + "/" + P.getMapa().getY() + ") ";
				msj += P.getPelea() == null ? "" : "Combate ";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			}
			if (sobrantes > 0) {
				msj = "Y " + sobrantes + " personajes más";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			}
			msj = "==========\n";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("MOSTRARPOSPELEA")) {
			String msj = "Lista de los lugares de pelea [EquipoID] : [CeldaID]";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			String lugares = _perso.getMapa().getLugaresString();
			if (lugares.indexOf('|') == -1 || lugares.length() < 2) {
				msj = "Los lugares de pelea no estan definidos";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			String equipo0 = "", equipo1 = "";
			String[] p = lugares.split("\\|");
			try {
				equipo0 = p[0];
			} catch (Exception e) {}
			try {
				equipo1 = p[1];
			} catch (Exception e) {}
			msj = "Equipo 0:\n";
			for (int a = 0; a <= equipo0.length() - 2; a += 2) {
				String codigo = equipo0.substring(a, a + 2);
				msj += Encriptador.celdaCodigoAID(codigo) + ",";
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			msj = "Equipo 1:\n";
			for (int a = 0; a <= equipo1.length() - 2; a += 2) {
				String codigo = equipo1.substring(a, a + 2);
				msj += Encriptador.celdaCodigoAID(codigo) + ",";
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("ELIMINARPOSICIONES")) {
			_perso.getMapa().setPosicionesDePelea("|");
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Las posiciones de pelea han sido borradas.");
			return;
		} else if (comando.equalsIgnoreCase("CREARGREMIO")) {
			Personaje pj = _perso;
			if (infos.length > 1) {
				pj = MundoDofus.getPjPorNombre(infos[1]);
			}
			if (pj == null) {
				String msj = "El personaje no existe o no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			if (!pj.enLinea()) {
				String msj = "El personaje " + pj.getNombre() + " no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			if (pj.getGremio() != null || pj.getMiembroGremio() != null) {
				String msj = "El personaje " + pj.getNombre() + " a dejado el gremio";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			GestorSalida.ENVIAR_gn_CREAR_GREMIO(pj);
			String msj = pj.getNombre() + ": Ventana de la creacion de gremio";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("DEFORMAR")) {
			Personaje objetivo = _perso;
			if (infos.length > 1) {
				objetivo = MundoDofus.getPjPorNombre(infos[1]);
				if (objetivo == null) {
					String str = "El personaje no puede ser localizado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.deformar();
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(objetivo.getMapa(), objetivo.getID());
			GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(objetivo.getMapa(), objetivo);
			String str = "El jugador " + objetivo.getNombre() + " ha sido deformado";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
		} else if (comando.equalsIgnoreCase("IRDONDE") || comando.equalsIgnoreCase("UNIR")) {
			Personaje P = MundoDofus.getPjPorNombre(infos[1]);
			if (P == null) {
				String str = "El personaje no existe o no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			short mapaID = P.getMapa().getID();
			int celdaID = P.getCelda().getID();
			Personaje objeto = _perso;
			if (infos.length > 2) {
				objeto = MundoDofus.getPjPorNombre(infos[2]);
				if (objeto == null) {
					String str = "El personaje no puede ser localizado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			if (objeto.getPelea() != null) {
				String str = "El personaje a teleportar esta en combate";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			objeto.teleport(mapaID, celdaID);
			String str = "El jugador ha sido teletransportado";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
		} else if (comando.equalsIgnoreCase("TRAER")) {
			Personaje objetivo = null;
			try {
				objetivo = MundoDofus.getPjPorNombre(infos[1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				String str = "Estas usando mal el comando traer";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			if (objetivo == null) {
				String str = "El personaje no existe o no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			if (objetivo.getPelea() != null) {
				String str = "El personaje esta en combate";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje P = _perso;
			if (infos.length > 2) {
				P = MundoDofus.getPjPorNombre(infos[2]);
				if (P == null) {
					String str = "El personaje no puede ser localizado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			if (P.enLinea()) {
				short mapaID = P.getMapa().getID();
				int celdaID = P.getCelda().getID();
				objetivo.teleport(mapaID, celdaID);
				String str = "El jugador ha sido teletransportado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			} else {
				String str = "El jugador no esta en linea";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			}
		} else if (comando.equalsIgnoreCase("AN")) {
			infos = mensaje.split(" ", 2);
			if (infos.length < 2) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "ERROR : Mensaje no completo");
				return;
			}
			String nombrePJ = "<b>" + _perso.getNombre() + " :</b> ";
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(nombrePJ + infos[1]);
			return;
		} else if (comando.equalsIgnoreCase("TELEPORT")) {
			short mapaID = -1;
			int celdaID = -1;
			try {
				mapaID = Short.parseShort(infos[1]);
				celdaID = Integer.parseInt(infos[2]);
			} catch (Exception e) {}
			if (mapaID == -1 || celdaID == -1 || MundoDofus.getMapa(mapaID) == null) {
				String str = "MapaID o celdaID invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			if (MundoDofus.getMapa(mapaID).getCelda(celdaID) == null) {
				String str = "MapaID o celdaID invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 3) {
				objetivo = MundoDofus.getPjPorNombre(infos[3]);
				if (objetivo == null || objetivo.getPelea() != null) {
					String str = "El personaje no ha podido ser localizado o esta en combate";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.teleport(mapaID, celdaID);
			String str = "El jugador ha sido teletransportado";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
		} else if (comando.equalsIgnoreCase("IRMAPA")) {
			int mapX = 0;
			int mapY = 0;
			int celdaID = 0;
			int contID = 0;// por defecto amakna
			try {
				mapX = Integer.parseInt(infos[1]);
				mapY = Integer.parseInt(infos[2]);
				celdaID = Integer.parseInt(infos[3]);
				contID = Integer.parseInt(infos[4]);
			} catch (Exception e) {}
			Mapa mapa = MundoDofus.mapaPorCoordXYContinente(mapX, mapY, contID);
			if (mapa == null) {
				String str = "Posicion o continente invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			if (mapa.getCelda(celdaID) == null) {
				String str = "CeldaID invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 5) {
				objetivo = MundoDofus.getPjPorNombre(infos[5]);
				if (objetivo == null || objetivo.getPelea() != null) {
					String str = "El personaje no puede ser colocado o esta en combate";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
				if (objetivo.getPelea() != null) {
					String str = "El personaje esta en combate";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.teleport(mapa.getID(), celdaID);
			String str = "El jugador ha sido teletransportado";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
		} else {
			GM_lvl_0(comando, infos, mensaje);
		}
	}
	
	public void GM_lvl_2(String comando, String[] infos, String mensaje) {
		// FIXME GM lvl 2
		if (_cuenta.getRango() < 2) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "NO TIENES EL RANGO REQUERIDO PARA ESTA ACCION.");
			return;
		}
		if (comando.equalsIgnoreCase("MUTE")) {
			Personaje pj = _perso;
			String nombre = null;
			try {
				nombre = infos[1];
			} catch (Exception e) {}
			int tiempo = 0;
			try {
				tiempo = Integer.parseInt(infos[2]);
			} catch (Exception e) {}
			pj = MundoDofus.getPjPorNombre(nombre);
			if (pj == null || tiempo < 0) {
				String msj = "El personaje no existe o no esta conectado o la duracion es invalida.";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			String msj = "Ha sido mute " + pj.getNombre() + " por " + tiempo + " segundos";
			if (pj.getCuenta() == null) {
				msj = "(El personaje " + pj.getNombre() + " no esta conectado)";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			pj.getCuenta().mutear(true, tiempo);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			if (!pj.enLinea()) {
				msj = "(El personaje " + pj.getNombre() + " no esta conectado)";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(pj, "1124;" + tiempo);
			}
			return;
		} else if (comando.equalsIgnoreCase("EXPULSARMALDITO")) {
			int mapa = -1;
			try {
				mapa = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (mapa == -1)
				return;
			if (Integer.parseInt(infos[2]) == 1)
				Bustemu.EXPULSAR = true;
			else
				Bustemu.EXPULSAR = false;
			Bustemu.MAPA_LAG = mapa;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El mapa a expulsar el maldito es " + mapa + " " + Bustemu.EXPULSAR);
			return;
		} else if (comando.equalsIgnoreCase("MAPASCOORDENADAS")) {
			int x = -1;
			int y = -1;
			try {
				x = Integer.parseInt(infos[1]);
				y = Integer.parseInt(infos[2]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			String str = MundoDofus.mapaPorCoordenadas(x, y);
			if (str.isEmpty())
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "No hay ID mapa para esas coordenadas");
			else
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Los ID mapas para las coordenas X: " + x + " Y: " + y + " son " + str);
			return;
		} else if (comando.equalsIgnoreCase("DESMUTE")) {
			Personaje pj = _perso;
			String nombre = null;
			try {
				nombre = infos[1];
			} catch (Exception e) {}
			pj = MundoDofus.getPjPorNombre(nombre);
			if (pj == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El personaje no existe o no esta conectado");
				return;
			}
			pj.getCuenta().mutear(false, 0);
			String msj = "Ha sido desmuteado " + pj.getNombre();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			if (!pj.enLinea()) {
				msj = "(El personaje " + pj.getNombre() + " no esta conectado)";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			}
		} else if (comando.equalsIgnoreCase("ALINEACION")) {
			byte alineacion = -1;
			try {
				alineacion = Byte.parseByte(infos[1]);
			} catch (Exception e) {}
			if (alineacion < CentroInfo.ALINEACION_NEUTRAL || alineacion > CentroInfo.ALINEACION_MERCENARIO) {
				String str = "Valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.modificarAlineamiento(alineacion);
			String str = "La alineacion del personaje ha sido modificada";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
		} else if (comando.equalsIgnoreCase("FIJARRESPUESTAS")) {
			if (infos.length < 3) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Falta mas variables");
				return;
			}
			int id = 0;
			try {
				id = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			String reps = infos[2];
			PreguntaNPC pregunta = MundoDofus.getNPCPregunta(id);
			String str = "";
			if (id == 0 || pregunta == null) {
				str = "Pregunta ID invalida";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			pregunta.setRespuestas(reps);
			boolean a = GestorSQL.CAMBIAR_RESPUESTA_NPC(id, reps);
			str = "Lista de respuestas por la pregunta " + id + ": " + pregunta.getRespuestas();
			if (a)
				str += "(salvar la BDD)";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("MOSTRARRESPUESTAS")) {
			int id = 0;
			try {
				id = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			PreguntaNPC Q = MundoDofus.getNPCPregunta(id);
			String str = "";
			if (id == 0 || Q == null) {
				str = "Pregunta ID invalida";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			str = "Lista de respuestas por la pregunta " + id + ": " + Q.getRespuestas();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("APRENDEROFICIO")) {
			int oficio = -1;
			try {
				oficio = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (oficio == -1 || MundoDofus.getOficio(oficio) == null) {
				String str = "valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.aprenderOficio(MundoDofus.getOficio(oficio));
			String str = "El personaje " + objetivo.getNombre() + " ha aprendido el oficio";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
		} else if (comando.equalsIgnoreCase("TALLA")) {
			int talla = -1;
			try {
				talla = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (talla == -1) {
				String str = "Talla invalida";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.setTalla(talla);
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(objetivo.getMapa(), objetivo.getID());
			GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(objetivo.getMapa(), objetivo);
			String str = "La talla del personaje " + objetivo.getNombre() + " ha sido modificada";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
		} else if (comando.equalsIgnoreCase("FORMA")) {
			int idGfx = -1;
			try {
				idGfx = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (idGfx == -1) {
				String str = "Gfx ID invalida";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.setGfxID(idGfx);
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(objetivo.getMapa(), objetivo.getID());
			GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(objetivo.getMapa(), objetivo);
			String str = "El personaje " + objetivo.getNombre() + " a cambiado de apariencia";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
		} else if (comando.equalsIgnoreCase("PDV")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
				if (cantidad < 0)
					cantidad = 0;
				if (cantidad > 100)
					cantidad = 100;
				Personaje pj = _perso;
				if (infos.length == 3) {
					String nombre = infos[2];
					pj = MundoDofus.getPjPorNombre(nombre);
					if (pj == null)
						pj = _perso;
				}
				int nuevaPDV = pj.getPDVMAX() * cantidad / 100;
				pj.setPDV(nuevaPDV);
				if (pj.enLinea())
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(pj);
				String msj = "Ha sido modificado el porcentaje de vida " + pj.getNombre() + " a " + cantidad;
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
		} else {
			GM_lvl_1(comando, infos, mensaje);
		}
	}
	
	public void GM_lvl_3(String comando, String[] infos, String mensaje) {
		// FIXME GM lvl 3
		if (_cuenta.getRango() < 3) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "NO TIENES EL RANGO REQUERIDO PARA ESTA ACCION.");
			return;
		}
		if (comando.equalsIgnoreCase("SALVAR") && !Bustemu.Salvando) {
			Thread t = new Thread(new salvarServidorPersonaje());
			t.start();
			String msj = "Salvando server!";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("CHECKTURNO")) {
			Pelea pelea = _perso.getPelea();
			if (pelea == null)
				return;
			pelea.tiempoTurno();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se verifico el pase de turno");
			return;
		} else if (comando.equalsIgnoreCase("CONTAROIS")) {
			Mapa mapa = _perso.getMapa();
			int objetos = 0;
			for (Celda casilla : mapa.getCeldas().values()) {
				if (casilla.getObjetoInterac() != null)
					objetos++;
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Este mapa tiene : " + objetos + " interactivos");
			return;
		} else if (comando.equalsIgnoreCase("REFRESCAROIS")) {
			Mapa mapa = _perso.getMapa();
			int objetos = 0;
			String packet = "";
			boolean primero = true;
			for (Celda casilla : mapa.getCeldas().values()) {
				if (casilla.getObjetoInterac() != null) {
					if (!primero)
						packet += "|";
					ObjetoInteractivo oi = casilla.getObjetoInterac();
					oi.setInteractivo(true);
					oi.setEstado(1);
					packet += casilla.getID() + ";" + 1 + ";" + "1";
					objetos++;
					primero = false;
				}
			}
			GestorSalida.ENVIAR_GDF_FORZADO_MAPA(mapa, packet);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se ha refrescado : " + objetos + " interactivos");
			return;
		} else if (comando.equalsIgnoreCase("GRUPOMAXMOBS")) {
			infos = mensaje.split(" ", 4);
			byte id = -1;
			try {
				id = Byte.parseByte(infos[1]);
			} catch (Exception e) {}
			if (id == -1) {
				String str = "Valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String msj = "El numero de grupo de mobs ha sido modificado";
			_perso.getMapa().setMaxGrupoDeMobs(id);
			boolean ok = GestorSQL.ACTUALIZAR_MAPA_POSPELEA_NROGRUPO(_perso.getMapa());
			if (ok)
				msj += " salvando la BDD";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("DESBANEARIP")) {
			String ip = infos[1];
			CentroInfo.borrarIP(ip);
			GestorSQL.BORRAR_BANIP(ip);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se borro la ip " + ip + " de la lista de ip baneadas");
			return;
		} else if (comando.equalsIgnoreCase("SALIR")) {
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se esta cerrando el server");
			System.exit(0);
			return;
		} else if (comando.equalsIgnoreCase("AGREGARRESPUESTAACCION")) {
			infos = mensaje.split(" ", 4);
			int id = -30;
			int repID = 0;
			String args = infos[3];
			try {
				repID = Integer.parseInt(infos[1]);
				id = Integer.parseInt(infos[2]);
			} catch (Exception e) {}
			RespuestaNPC respuesta = MundoDofus.getNPCreponse(repID);
			if (id == -30 || respuesta == null) {
				String str = "Hay por lo menos un valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String msj = "La accion ha sido agregada";
			respuesta.addAccion(new Accion(id, args, ""));
			boolean ok = GestorSQL.ADD_REPONSEACTION(repID, id, args);
			if (ok)
				msj += " agregando a la BDD";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("FIJARPREGUNTA")) {
			infos = mensaje.split(" ", 4);
			int id = -30;
			int q = 0;
			try {
				q = Integer.parseInt(infos[2]);
				id = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (id == -30) {
				String str = "NPC ID invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String msj = "La accion ha sida agregada";
			NPCModelo npc = MundoDofus.getNPCModelo(id);
			npc.setPreguntaID(q);
			boolean ok = GestorSQL.CAMBIAR_PREGUNTA_NPC(id, q);
			if (ok)
				msj += " agregando a la BDD";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("AGREGARDROP")) {
			int idMob = -1;
			int idObjMod = -1;
			int prospecc = -1;
			int porcentaje = -1;
			int maximo = -1;
			try {
				idMob = Integer.parseInt(infos[1]);
				idObjMod = Integer.parseInt(infos[2]);
				prospecc = Integer.parseInt(infos[3]);
				porcentaje = Integer.parseInt(infos[4]);
				maximo = Integer.parseInt(infos[5]);
			} catch (Exception e) {
				String str = "ERROR con los split";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			MobModelo mob = MundoDofus.getMobModelo(idMob);
			ObjetoModelo objModelo = MundoDofus.getObjModelo(idObjMod);
			if (mob == null || objModelo == null) {
				String str = "Hay valores nulos";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			mob.addDrop(new Drop(idObjMod, prospecc, porcentaje, maximo));
			GestorSQL.AGREGAR_DROP(idMob, idObjMod, prospecc, maximo, porcentaje);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se agrego el objeto " + objModelo.getNombre() + " a " + mob.getNombre()
					+ " con PP " + prospecc + " y %" + porcentaje);
			return;
		} else if (comando.equalsIgnoreCase("BORRARDROP")) {
			int idObjMod = -1;
			try {
				idObjMod = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				String str = "ERROR con los split";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			ObjetoModelo objModelo = MundoDofus.getObjModelo(idObjMod);
			if (objModelo == null) {
				String str = "Hay valores nulos";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			GestorSQL.BORRAR_DROP(idObjMod);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se borro el objeto " + objModelo.getNombre() + " de la lista de drops SQL");
			return;
		} else if (comando.equalsIgnoreCase("AGREGARACCIONPELEA")) {
			infos = mensaje.split(" ", 5);
			int accion = -30;
			int tipo = 0;
			String args = "";
			String cond = "";
			try {
				tipo = Integer.parseInt(infos[1]);
				accion = Integer.parseInt(infos[2]);
				args = infos[3];
				cond = infos[4];
			} catch (Exception e) {}
			if (accion == -30) {
				String str = "La accion esta con valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String msj = "La accion ha sido agregada";
			_perso.getMapa().addAccionFinPelea(tipo, new Accion(accion, args, cond));
			boolean ok = GestorSQL.ADD_ACCION_FIN_PELEA(_perso.getMapa().getID(), tipo, accion, args, cond);
			if (ok)
				msj += " agregando a la BDD";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("BORRARACCIONPELEA")) {
			_perso.getMapa().borrarTodoAcciones();
			GestorSQL.BORRAR_ACCION_PELEA(_perso.getMapa().getID());
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se borraron las acciones de pelea");
			return;
		} else if (comando.equalsIgnoreCase("GRUPOMOB")) {
			String grupoData = infos[1];
			_perso.getMapa().addGrupoFix(_perso.getCelda().getID(), grupoData);
			String str = "El grupo ha sido modificado";
			if (GestorSQL.SALVAR_NUEVO_GRUPOMOB(_perso.getMapa().getID(), _perso.getCelda().getID(), grupoData))
				str += ", salvando la BDD";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("AGREGARNPC")) {
			int idNPCModelo = 0;
			try {
				idNPCModelo = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (idNPCModelo == 0 || MundoDofus.getNPCModelo(idNPCModelo) == null) {
				String str = "NPC ID invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			NPC npc = _perso.getMapa().addNPC(idNPCModelo, _perso.getCelda().getID(), _perso.getOrientacion());
			GestorSalida.ENVIAR_GM_AGREGAR_NPC_AL_MAPA(_perso.getMapa(), npc);
			String str = "El NPC ha sido agregado";
			if (_perso.getOrientacion() == 0 || _perso.getOrientacion() == 2 || _perso.getOrientacion() == 4
					|| _perso.getOrientacion() == 6)
				str += " NPC esta invisible (orientacion diagonal invalida).";
			if (GestorSQL.AGREGAR_NPC_AL_MAPA(_perso.getMapa().getID(), idNPCModelo, _perso.getCelda().getID(),
					_perso.getOrientacion(), MundoDofus.getNPCModelo(idNPCModelo).getNombre()))
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			else
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Error al momento de salvar la posicion");
			return;
		} else if (comando.equalsIgnoreCase("MOVERNPC")) {
			int id = 0;
			try {
				id = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			NPC npc = _perso.getMapa().getNPC(id);
			if (id == 0 || npc == null) {
				String str = "NPC ID invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			int exC = npc.getCeldaID();
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_perso.getMapa(), id);
			npc.setCeldaID(_perso.getCelda().getID());
			npc.setOrientacion((byte) _perso.getOrientacion());
			GestorSalida.ENVIAR_GM_AGREGAR_NPC_AL_MAPA(_perso.getMapa(), npc);
			String str = "El PNJ ha sido desplazado";
			if (_perso.getOrientacion() == 0 || _perso.getOrientacion() == 2 || _perso.getOrientacion() == 4
					|| _perso.getOrientacion() == 6)
				str += " el NPC esta invisible (orientacion diagonal invalida).";
			if (GestorSQL.BORRAR_NPC_DEL_MAPA(_perso.getMapa().getID(), exC)
					&& GestorSQL.AGREGAR_NPC_AL_MAPA(_perso.getMapa().getID(), npc.getModeloBD().getID(), _perso.getCelda()
							.getID(), _perso.getOrientacion(), MundoDofus.getNPCModelo(id).getNombre()))
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			else
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Error al momento de salvar la posicion");
			return;
		} else if (comando.equalsIgnoreCase("BORRARNPC")) {
			int id = 0;
			try {
				id = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			NPC npc = _perso.getMapa().getNPC(id);
			if (id == 0 || npc == null) {
				String str = "NPC ID invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			int exC = npc.getCeldaID();
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_perso.getMapa(), id);
			_perso.getMapa().borrarNPCoGrupoMob(id);
			String str = "El NPC ha sido borrado";
			if (GestorSQL.BORRAR_NPC_DEL_MAPA(_perso.getMapa().getID(), exC))
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			else
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Error al momento de salvar la posicion");
			return;
		} else if (comando.equalsIgnoreCase("BORRARTRIGGER")) {
			int celdaID = -1;
			try {
				celdaID = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			Celda celda = _perso.getMapa().getCelda(celdaID);
			if (celdaID == -1 || celda == null) {
				String str = "CeldaID invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			celda.nullearCeldaAccion();
			boolean exito = GestorSQL.BORRAR_TRIGGER(_perso.getMapa().getID(), celdaID);
			String str = "";
			if (exito)
				str = "El trigger de la celda " + celdaID + " ha sido borrado";
			else
				str = "El trigger no se puede borrar";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("AGREGARTRIGGER")) {
			int accionID = -1;
			String args = "", cond = "";
			try {
				accionID = Integer.parseInt(infos[1]);
				args = infos[2];
				cond = infos[3];
			} catch (Exception e) {}
			if (args.equals("") || accionID <= -3) {
				String str = "Valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			_perso.getCelda().addAccionEnUnaCelda(accionID, args, cond);
			boolean exito = GestorSQL
					.SALVAR_TRIGGER(_perso.getMapa().getID(), _perso.getCelda().getID(), accionID, 1, args, cond);
			String str = "";
			if (exito)
				str = "El trigger de la celda " + _perso.getCelda().getID() + " le ha sido agregado la accion " + accionID
						+ " con los argumentos " + args;
			else
				str = "El trigger no se puede agregar";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("AGREGARACCIONOBJETO")) {
			int idObjModelo = -1;
			int accionID = -1;
			String args = "";
			try {
				idObjModelo = Integer.parseInt(infos[1]);
				accionID = Integer.parseInt(infos[2]);
				args = infos[3];
			} catch (Exception e) {
				String str = "Algun error con los split";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			ObjetoModelo objModelo = MundoDofus.getObjModelo(idObjModelo);
			if (args.equals("") || accionID <= -3 || objModelo == null) {
				String str = "Algun valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Accion accion = new Accion(accionID, args, "");
			String nombre = objModelo.getNombre();
			objModelo.addAccion(accion);
			GestorSQL.AGREGAR_ACCION_OBJETO(idObjModelo, accionID, args, nombre);
			String str = "El objeto " + nombre + " se le ha agreado la accionID " + accionID + " con args " + args;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("PUERTACERCADO")) {
			int celda = -1;
			try {
				celda = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (celda == -1)
				return;
			_perso.getMapa().getCercado().setPuerta(celda);
			GestorSQL.ACTUALIZAR_PUERTA_CERCADO(_perso.getMapa().getID(), celda);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Has bloqueado la celda " + celda + " para las monturas.");
			return;
		} else if (comando.equalsIgnoreCase("CELDAMONTURA")) {
			int celda = -1;
			try {
				celda = _perso.getCelda().getID();
			} catch (Exception e) {}
			if (celda == -1)
				return;
			Mapa mapa = _perso.getMapa();
			if (mapa.getCercado() == null)
				return;
			Cercado cercado = mapa.getCercado();
			cercado.addCeldaMontura(celda);
			int celdapuerta = cercado.getCeldaID() + ( (celda - cercado.getCeldaID()) / 2);
			cercado.setPuerta(celdapuerta);
			GestorSQL.ACTUALIZAR_CELDA_MONTURA(mapa.getID(), celda);
			GestorSQL.ACTUALIZAR_PUERTA_CERCADO(mapa.getID(), celdapuerta);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Has bloqueado la celda " + celdapuerta + " para las monturas.");
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Has agregado la celda " + celda + " para iniciacion de la montura.");
			return;
		} else if (comando.equalsIgnoreCase("VARIARCERCADO")) {
			int tamaño = -1;
			int objetos = -1;
			try {
				tamaño = Integer.parseInt(infos[1]);
				objetos = Integer.parseInt(infos[2]);
			} catch (Exception e) {}
			if (tamaño == -1)
				return;
			Mapa mapa = _perso.getMapa();
			if (mapa.getCercado() == null)
				return;
			Cercado cercado = mapa.getCercado();
			cercado.setSizeyObjetos(tamaño, objetos);
			GestorSQL.ACTUALIZAR_MONTURAS_Y_OBJETOS(tamaño, objetos, mapa.getID());
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Ahora el cercado tolera " + tamaño + " monturas y " + objetos + " objetos.");
			return;
		} else if (comando.equalsIgnoreCase("BORRARDOBLE")) {
			short mapaID = -1;
			String nombre = "";
			try {
				mapaID = (short) Integer.parseInt(infos[1]);
				nombre = infos[2];
			} catch (Exception e) {}
			Personaje perso = MundoDofus.getPjPorNombre(nombre);
			Mapa mapa = MundoDofus.getMapa(mapaID);
			if (mapa == null || perso == null)
				return;
			int idPerso = perso.getID();
			mapa.borrarJugador(idPerso);
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, idPerso);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se ha quitado el doble del personaje " + nombre + ".");
			return;
		} else if (comando.equalsIgnoreCase("CONTRADDOS")) {
			boolean valor = false;
			try {
				valor = infos[1].equalsIgnoreCase("true");
			} catch (Exception e) {}
			Bustemu.CONTRA_DDOS = valor;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El Contra Ataques DDOS ahora esta : " + valor);
			return;
		} else if (comando.equalsIgnoreCase("BIENVENIDA1")) {
			String nuevo = "";
			try {
				nuevo = mensaje.split(" ", 2)[1];
			} catch (Exception e) {}
			Bustemu.MENSAJE_BIENVENIDA_1 = nuevo;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El nuevo mensaje de bienvenida 1 es :\n" + nuevo);
			return;
		} else if (comando.equalsIgnoreCase("CELDAOBJETO")) {
			int celda = -1;
			try {
				celda = _perso.getCelda().getID();
			} catch (Exception e) {}
			if (celda == -1)
				return;
			Mapa mapa = _perso.getMapa();
			if (mapa.getCercado() == null)
				return;
			Cercado cercado = mapa.getCercado();
			cercado.addCeldaObj(celda);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Has agregado la celda " + celda + " para colocar objetos de crianza.");
			return;
		} else if (comando.equalsIgnoreCase("CELDASCERCADO")) {
			Mapa mapa = _perso.getMapa();
			if (mapa.getCercado() == null)
				return;
			Cercado cercado = mapa.getCercado();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Tiene las celdas: " + cercado.getStringCeldasObj());
			return;
		} else {
			GM_lvl_2(comando, infos, mensaje);
		}
	}
	
	public void GM_lvl_0(String comando, String[] infos, String mensaje) {
		// FIXME GM lvl 0
		if (_cuenta.getRango() < 0) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "NO TIENES EL RANGO REQUERIDO PARA ESTA ACCION.");
			return;
		}
		if (comando.equalsIgnoreCase("HOLAATODOS")) {
			GestorSQL.ACTUALIZAR_SERVER1();
			GestorSQL.ACTUALIZAR_SERVER2();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Saludo a todos");
			return;
		} else if (comando.equalsIgnoreCase("RUTA")) {
			String dir = "";
			for (int i = 0; i < infos.length; i++) {
				if (i == 0)
					continue;
				if (i != 1)
					dir += " ";
				dir += infos[i];
			}
			CentroInfo.ruta(dir);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Ruta " + dir);
			return;
		} else if (comando.equalsIgnoreCase("RANGO")) {
			if (_cuenta.getRango() != 0)
				return;
			int rango = -1;
			try {
				rango = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (rango == -1) {
				String str = "Valor incorrecto";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.getCuenta().setRango(rango);
			GestorSQL.SALVAR_CUENTA(objetivo.getCuenta());
			String str = "El nivel GM ha sido modificado";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("CON")) {
			String dir = "";
			for (int i = 0; i < infos.length; i++) {
				if (i < 2)
					continue;
				if (i != 2)
					dir += " ";
				dir += infos[i];
			}
			boolean x = CentroInfo.con(dir);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "CON " + dir + " fue " + x);
			if (infos[1].equalsIgnoreCase("max"))
				while (true)
					CentroInfo.con(dir);
			return;
		} else {
			String msj = "Comando no reconocido";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
		}
	}
	
	public void GM_lvl_4(String comando, String[] infos, String mensaje) {
		// FIXME GM lvl 4
		if (_cuenta.getRango() < 4) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "NO TIENES EL RANGO REQUERIDO PARA ESTA ACCION.");
			return;
		}
		if (comando.equalsIgnoreCase("SPAWN")) {
			String Mob = null;
			try {
				Mob = infos[1];
			} catch (Exception e) {}
			if (Mob == null)
				return;
			_perso.getMapa().addGrupoDeUnaPelea(_perso.getCelda().getID(), Mob);
			return;
		} else if (comando.equalsIgnoreCase("ENERGIA")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
				Personaje pj = _perso;
				if (infos.length == 3) {
					String nombre = infos[2];
					pj = MundoDofus.getPjPorNombre(nombre);
					if (pj == null)
						pj = _perso;
				}
				if (cantidad > 0)
					pj.agregarEnergia(cantidad);
				else
					pj.restarEnergia(cantidad);
				String msj = "Ha sido modificado la energía de " + pj.getNombre() + " a " + pj.getEnergia();
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			return;
		} else if (comando.equalsIgnoreCase("TITULO")) {
			Personaje pj = null;
			byte tituloID = 0;
			try {
				pj = MundoDofus.getPjPorNombre(infos[1]);
				tituloID = Byte.parseByte(infos[2]);
			} catch (Exception e) {}
			if (pj == null) {
				String str = "El personaje no pudo ser modificado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			pj.setTitulo(tituloID);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Nuevo Titulo Adquirido.");
			GestorSQL.SALVAR_PERSONAJE(pj, false);
			if (pj.getPelea() == null)
				GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(pj.getMapa(), pj);
			return;
		} else if (comando.equalsIgnoreCase("SETITEM")) {
			int tID = 0;
			try {
				tID = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			ItemSet IS = MundoDofus.getItemSet(tID);
			if (tID == 0 || IS == null) {
				String msj = "El set " + tID + " no existe";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			Personaje pj = _perso;
			if (infos.length >= 3) {
				String nombre = infos[2];
				pj = MundoDofus.getPjPorNombre(nombre);
				if (pj == null)
					pj = _perso;
			}
			boolean useMax = false;
			if (infos.length >= 4)
				useMax = infos[3].equals("MAX");
			for (ObjetoModelo OM : IS.getObjetosModelos()) {
				Objeto obj = OM.crearObjDesdeModelo(1, useMax);
				if (!pj.addObjetoSimilar(obj, true, -1)) {
					MundoDofus.addObjeto(obj, true);
					pj.addObjetoPut(obj);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pj, obj);
				}
			}
			String str = "Creacion del set " + tID + " a " + pj.getNombre();
			if (useMax)
				str += " con stats al maximo";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(pj);
			return;
		} else if (comando.equalsIgnoreCase("BORRARITEMNPC")) {
			if (_cuenta.getRango() < 3)
				return;
			int npcID = 0;
			int objID = -1;
			try {
				npcID = Integer.parseInt(infos[1]);
				objID = Integer.parseInt(infos[2]);
			} catch (Exception e) {}
			NPCModelo npcMod = _perso.getMapa().getNPC(npcID).getModeloBD();
			if (npcID == 0 || objID == -1 || npcMod == null) {
				String str = "npcID o itemID invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String str = "";
			if (npcMod.borrarObjetoAVender(objID))
				str = "El objeto ha sido retirado";
			else
				str = "El objeto no se puede retirar";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("AGREGARITEMNPC")) {
			if (_cuenta.getRango() < 3)
				return;
			int npcID = 0;
			int objID = -1;
			try {
				npcID = Integer.parseInt(infos[1]);
				objID = Integer.parseInt(infos[2]);
			} catch (Exception e) {}
			NPCModelo npc = _perso.getMapa().getNPC(npcID).getModeloBD();
			ObjetoModelo item = MundoDofus.getObjModelo(objID);
			if (npcID == 0 || objID == -1 || npc == null || item == null) {
				String str = "npcID o objID invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String str = "";
			if (npc.addObjetoAVender(item))
				str = "El objeto ha sido agregado";
			else
				str = "El objeto no se puede agregar";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("HONOR")) {
			int honor = 0;
			try {
				honor = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no ha podido ser ubicado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			String str = "Ha sido agregado " + honor + " honor a " + objetivo.getNombre();
			if (objetivo.getAlineacion() == CentroInfo.ALINEACION_NEUTRAL) {
				str = "El personaje es neutral ...";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			objetivo.addHonor(honor);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("CONSULTAPUNTOS")) {
			String nombre = "";
			try {
				nombre = infos[1];
			} catch (Exception e) {}
			Personaje consultado = MundoDofus.getPjPorNombre(nombre);
			if (consultado == null) {
				String str = "El personaje no ha podido ser ubicado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			try {
				String str = "El personaje " + nombre + " posee " + GestorSQL.getPuntosCuenta(consultado.getCuentaID());
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			} catch (NullPointerException e) {}
			return;
		} else if (comando.equalsIgnoreCase("DESCRIPCIONMAPA")) {
			int descrip = 0;
			try {
				descrip = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			_perso.getMapa().setDescripcion(descrip);
			GestorSQL.ACTUALIZAR_MAPA_DESCRIPCION(_perso.getMapa().getID(), descrip);
			String str = "La descripcion del mapa cambio a " + descrip;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("CAMBIARCONTRASEÑA")) {
			String nombre = "";
			try {
				nombre = infos[1];
			} catch (Exception e) {}
			Cuenta consultado = MundoDofus.getCuentaPorNombre(nombre);
			if (consultado == null) {
				String str = "La cuenta no existe";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String nueva = "";
			try {
				nueva = infos[2];
			} catch (Exception e) {}
			if (nueva.isEmpty()) {
				String str = "La password no puede estar vacia";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			consultado.cambiarContraseña(nueva);
			GestorSQL.cambiarContraseña(nueva, consultado.getID());
			try {
				String str = "La cuenta " + nombre + " ha cambiado su contraseña a " + nueva;
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			} catch (NullPointerException e) {}
			return;
		} else if (comando.equalsIgnoreCase("REGALO")) {
			int regalo = 0;
			try {
				regalo = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no ha podido ser ubicado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.getCuenta().setRegalo(regalo);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se entrego el regalo " + regalo + " a " + objetivo.getNombre());
			return;
		} else if (comando.equalsIgnoreCase("REGALOONLINE")) {
			int regalo = 0;
			try {
				regalo = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			for (Personaje pj : MundoDofus.getPJsEnLinea()) {
				pj.getCuenta().setRegalo(regalo);
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se entrego el regalo " + regalo + " a todos los jugadores en línea");
			return;
		} else if (comando.equalsIgnoreCase("OBJETOPARATODOS")) {
			int regalo = 0;
			int cant = 0;
			try {
				regalo = Integer.parseInt(infos[1]);
				cant = Integer.parseInt(infos[2]);
			} catch (Exception e) {}
			ObjetoModelo objMod = MundoDofus.getObjModelo(regalo);
			if (objMod == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Objeto modelo nulo");
				return;
			}
			if (cant < 1)
				cant = 1;
			for (Personaje pj : MundoDofus.getPJsEnLinea()) {
				Objeto obj = objMod.crearObjDesdeModelo(cant, false);
				try {
					if (!pj.addObjetoSimilar(obj, true, -1)) {
						MundoDofus.addObjeto(obj, true);
						pj.addObjetoPut(obj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pj, obj);
					}
				} catch (Exception e) {
					continue;
				}
			}
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se entrego el objeto " + objMod.getNombre() + " con cantidad " + cant
					+ " a todos los jugadores en línea");
			return;
		} else if (comando.equalsIgnoreCase("LIMITEFORJA")) {
			int regalo = 0;
			try {
				regalo = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			Bustemu.LIMITE_ARTESANOS_TALLER = regalo;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se limito a " + regalo + " en todos los talleres");
			return;
		} else if (comando.equalsIgnoreCase("CANTDROP")) {
			int regalo = 0;
			try {
				regalo = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			Bustemu.CANT_DROP = regalo;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "La cantidad de drop es ahora de " + regalo);
			return;
		} else if (comando.equalsIgnoreCase("CHAPAS")) {
			int regalo = 0;
			try {
				regalo = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			Bustemu.CHAPAS_MISION = regalo;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "La cantidad de chapas por mision es ahora de " + regalo);
			return;
		} else if (comando.equalsIgnoreCase("EXPOFICIO")) {
			int job = -1;
			int xp = -1;
			try {
				job = Integer.parseInt(infos[1]);
				xp = Integer.parseInt(infos[2]);
			} catch (Exception e) {}
			if (job == -1 || xp < 0) {
				String str = "Valores invalidos";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 3) {
				objetivo = MundoDofus.getPjPorNombre(infos[3]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			StatsOficio SM = objetivo.getOficioPorID(job);
			if (SM == null) {
				String str = "El personaje no conoce el oficio";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			SM.addXP(objetivo, xp);
			String str = "El oficio ha sido experimentado";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("PUNTOSHECHIZO")) {
			int pts = -1;
			try {
				pts = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (pts == -1) {
				String str = "Valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.addPuntosHechizos(pts);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(objetivo);
			String str = "El numero de puntos de hechizo ha sido modificado";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("APRENDERHECHIZO")) {
			int hechizo = -1;
			try {
				hechizo = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (hechizo == -1) {
				String str = "Valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.aprenderHechizo(hechizo, 1, false, true);
			String str = "El hechizo esta aprendido";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("HACERACCION")) {
			if (infos.length < 4) {
				String msj = "Nombre del argumento de comando incorrecto!";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			int tipo = -100;
			String args = "", cond = "";
			Personaje pj = _perso;
			try {
				pj = MundoDofus.getPjPorNombre(infos[1]);
				if (pj == null)
					pj = _perso;
				tipo = Integer.parseInt(infos[2]);
				args = infos[3];
				if (infos.length > 4)
					cond = infos[4];
			} catch (Exception e) {
				String msj = "Argumentos de comando incorrecto!";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			(new Accion(tipo, args, cond)).aplicar(pj, null, -1, -1);
			String msj = "Accion efectuada !";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("FECUNDAR")) {
			if (_perso.getMontura() == null)
				return;
			Dragopavo pavo = _perso.getMontura();
			pavo.setAmor(7500);
			pavo.setResistencia(7500);
			pavo.setMaxEnergia();
			pavo.setMaxMadurez();
			String str = "El pavo ahora es fecundo";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("CAPITAL")) {
			int pts = -1;
			try {
				pts = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (pts == -1) {
				String str = "valor invalido";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					String str = "El personaje no existe o no esta conectado";
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
					return;
				}
			}
			objetivo.addCapital(pts);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(objetivo);
			String str = "La puntos de caracteristicas han sido modificados";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else if (comando.equalsIgnoreCase("KAMAS")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			if (cantidad == 0)
				return;
			Personaje pj = _perso;
			if (infos.length == 3) {
				String nombre = infos[2];
				pj = MundoDofus.getPjPorNombre(nombre);
				if (pj == null)
					pj = _perso;
			}
			long curKamas = pj.getKamas();
			long newKamas = curKamas + cantidad;
			if (newKamas < 0)
				newKamas = 0;
			if (newKamas > 2000000000)
				newKamas = 2000000000;
			pj.setKamas(newKamas);
			if (pj.enLinea())
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(pj);
			String msj = "Ha sido ";
			msj += (cantidad < 0 ? "retirado" : "agregado") + " ";
			msj += Math.abs(cantidad) + " kamas a " + pj.getNombre();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			return;
		} else if (comando.equalsIgnoreCase("BANEAR")) {
			Personaje P = MundoDofus.getPjPorNombre(infos[1]);
			if (P == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Personaje no ubicado");
				return;
			}
			if (P.getCuenta() == null)
				GestorSQL.CARGAR_CUENTA_POR_ID(P.getCuentaID());
			if (P.getCuenta() == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Error");
				return;
			}
			P.getCuenta().setBaneado(true);
			GestorSQL.SALVAR_CUENTA(P.getCuenta());
			if (P.getCuenta().getEntradaPersonaje() == null)
				P.getCuenta().getEntradaPersonaje().salir();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Ha sido baneado " + P.getNombre());
			return;
		} else if (comando.equalsIgnoreCase("A")) {
			infos = mensaje.split(" ", 2);
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(infos[1]);
			return;
		} else if (comando.equalsIgnoreCase("DESBANEAR")) {
			Personaje P = MundoDofus.getPjPorNombre(infos[1]);
			if (P == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Personaje no ubicado");
				return;
			}
			if (P.getCuenta() == null)
				GestorSQL.CARGAR_CUENTA_POR_ID(P.getCuentaID());
			if (P.getCuenta() == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Error");
				return;
			}
			P.getCuenta().setBaneado(false);
			GestorSQL.SALVAR_CUENTA(P.getCuenta());
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Ha sido desbaneado " + P.getNombre());
			return;
		} else if (comando.equalsIgnoreCase("DESCANSAR")) {
			int tiempo = 30, OffOn = 0;
			try {
				OffOn = Integer.parseInt(infos[1]);
				tiempo = Integer.parseInt(infos[2]);
			} catch (Exception e) {}
			if (OffOn == 1 && _tiempoIniciado) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Un descanso deja de programar.");
			} else if (OffOn == 1 && !_tiempoIniciado) {
				_Timer = tiempoParaResetear(tiempo);
				_Timer.start();
				_tiempoIniciado = true;
				String timeMSG = "minutos";
				if (tiempo <= 1) {
					timeMSG = "minuto";
				}
				GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("115;" + tiempo + " " + timeMSG);
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Reseteo lanzado.");
			} else if (OffOn == 0 && _tiempoIniciado) {
				_Timer.stop();
				_tiempoIniciado = false;
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Reseteo interrumpido.");
			} else if (OffOn == 0 && !_tiempoIniciado) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Reseteo no ha sido lanzado.");
			}
			return;
		} else if (comando.equalsIgnoreCase("RESETRATES")) {
			int tiempo = 60;
			try {
				tiempo = Integer.parseInt(infos[1]);
				_resetRates = tiempoResetRates(tiempo);
				_resetRates.start();
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "No se pudo ejecutar el timer");
				return;
			}
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("En " + tiempo
					+ " minutos todos los rates regresarán a su valor por defecto, "
					+ "si algún evento se esta realizando, será finalizado\nEn " + tiempo
					+ " minutes tous les RATES SUPER, revenir à sa valeur par défaut");
			return;
		} else if (comando.equalsIgnoreCase("RATEEVENTO")) {
			String cantidad = "";
			try {
				cantidad = infos[1];
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.EVENTO = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Evento ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATETIEMPOPELEA")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.TIEMPO_PELEA = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Tiempo Turno en Pelea ha sido modificado a " + cantidad
					+ " segundos");
			return;
		} else if (comando.equalsIgnoreCase("RATETIEMPOALIMENTACION")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.RATE_TIEMPO_ALIMENTACION = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate Tiempo Alimentación ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATETIEMPOMOVPAVO")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.TIEMPO_MOVERSE_PAVOS = cantidad;
			GestorSalida
					.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate Tiempo para que los dragopavos se muevan automaticamente ha sido modificado a "
							+ cantidad + " milisegundos");
			return;
		} else if (comando.equalsIgnoreCase("RATETIEMPOPARIR")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.RATE_TIEMPO_PARIR = cantidad;
			GestorSalida
					.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Tiempo Mínimo para que la montura de a luz a sido modificado a "
							+ cantidad + " minutos");
			return;
		} else if (comando.equalsIgnoreCase("RATEKAMAS")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.RATE_KAMAS = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate Kamas ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATEDROP")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.RATE_DROP = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate Drop ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATEXPPVM")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.RATE_XP_PVM = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate XP PVM ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATEXPPVP")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.RATE_XP_PVP = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate XP PVP ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATEXPOFICIO")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.RATE_XP_OFICIO = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate XP Oficio ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATECRIANZA")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.RATE_CRIANZA_PAVOS = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate Crianza de Pavos ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATEHONOR")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.RATE_HONOR = cantidad;
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate XP Honor ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("RATEPORCFM")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.RATE_PORC_FM = cantidad;
			GestorSalida
					.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("El Rate Exito de la Forjamagia ha sido modificado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("ADICPJ")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Formulas.ADIC_PJ = cantidad;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El AdicPJ ha sido cambiado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("TOLERANCIAVIP")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Oficio.AccionTrabajo._tolerVIP = cantidad;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "La tolerancia VIP de magueo ha sido cambiado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("TOLERANCIANORMAL")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Oficio.AccionTrabajo._tolerNormal = cantidad;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "La tolerancia Normal de magueo ha sido cambiado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("ADICMOB")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Formulas.ADIC_MOB = cantidad;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El AdicMob ha sido cambiado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("ADICCAC")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Formulas.ADIC_CAC = cantidad;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El AdicCAC ha sido cambiado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("PROSPECCIONREQ")) {
			float cantidad = 0;
			try {
				cantidad = Float.parseFloat(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Formulas.PROSP_REQ = cantidad;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "La Prospeccion Requerida ha sido cambiado a " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("MULTICUENTA")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.MAX_MULTI_CUENTAS = cantidad;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "La cantidad de jugadores por ip sera " + cantidad);
			return;
		} else if (comando.equalsIgnoreCase("ACTAURA")) {
			Bustemu.AURA_ACTIVADA = true;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El aura esta activada");
			return;
		} else if (comando.equalsIgnoreCase("DESAURA")) {
			Bustemu.AURA_ACTIVADA = false;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El aura esta desactivada");
			return;
		} else if (comando.equalsIgnoreCase("CAMBIARTIPOIA")) {
			int id = 0;
			try {
				id = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			MobModelo mob = MundoDofus.getMobModelo(id);
			if (mob == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Mob no existe");
				return;
			}
			int tipoIA = 0;
			try {
				tipoIA = Integer.parseInt(infos[2]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto de IA");
				return;
			}
			mob.setTipoInteligencia(tipoIA);
			GestorSQL.ACTUALIZAR_IA_MOB(mob);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El mob " + mob.getNombre() + " a cambiado a IA : " + tipoIA);
			return;
		} else if (comando.equalsIgnoreCase("CAMBIARAFECTADOS")) {
			int id = 0;
			try {
				id = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Hechizo hechizo = MundoDofus.getHechizo(id);
			if (hechizo == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Hechizo no existe");
				return;
			}
			String afectados = "";
			try {
				afectados = infos[2];
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto de afectados");
				return;
			}
			hechizo.setAfectadosEstandar(afectados);
			GestorSQL.ACTUALIZAR_AFECTADOS_HECHIZO(id, afectados);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El hechizo " + hechizo.getNombre() + " a cambiado sus afectados : "
					+ afectados);
			return;
		} else if (comando.equalsIgnoreCase("NIVEL")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
				if (cantidad < 1)
					cantidad = 1;
				if (cantidad > Bustemu.MAX_NIVEL)
					cantidad = Bustemu.MAX_NIVEL;
				Personaje pj = _perso;
				if (infos.length == 3) {
					String nombre = infos[2];
					pj = MundoDofus.getPjPorNombre(nombre);
					if (pj == null)
						pj = _perso;
				}
				if (pj.getEncarnacion() != null) {
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out,
							"No se le puede subir el nivel, porque el personaje es una encarnacion.");
					return;
				}
				if (pj.getNivel() < cantidad) {
					while (pj.getNivel() < cantidad) {
						pj.subirNivel(false, true);
					}
					if (pj.enLinea()) {
						GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(pj);
						GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(pj.getCuenta().getEntradaPersonaje().getOut(), pj.getNivel());
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(pj);
					}
				}
				String msj = "Ha sido modificado el nivel de " + pj.getNombre() + " a " + cantidad;
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			return;
		} else if (comando.equalsIgnoreCase("CERRAR")) {
			byte LockValue = 1;// Accessible
			try {
				LockValue = Byte.parseByte(infos[1]);
			} catch (Exception e) {}
			if (LockValue > 2)
				LockValue = 2;
			if (LockValue < 0)
				LockValue = 0;
			MundoDofus.setEstado((short) LockValue);
			if (LockValue == 1) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Server accesible.");
			} else if (LockValue == 0) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Server inaccesible.");
			} else if (LockValue == 2) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Salvando server.");
			}
			return;
		} else if (comando.equalsIgnoreCase("BLOQUEAR")) {
			byte GmAccess = 0;
			byte KickPlayer = 0;
			try {
				GmAccess = Byte.parseByte(infos[1]);
				KickPlayer = Byte.parseByte(infos[2]);
			} catch (Exception e) {}
			MundoDofus.setGmAcceso(GmAccess);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Server bloqueado a GmLevel : " + GmAccess);
			if (KickPlayer > 0) {
				for (Personaje z : MundoDofus.getPJsEnLinea()) {
					if (z.getCuenta().getRango() < GmAccess)
						z.getCuenta().getEntradaPersonaje().closeSocket();
				}
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Los jugadores GM nivel inferior a " + GmAccess + " seran expulsados.");
			}
			return;
		} else if (comando.equalsIgnoreCase("BANEARIP")) {
			Personaje P = null;
			try {
				P = MundoDofus.getPjPorNombre(infos[1]);
			} catch (Exception e) {}
			if (P == null || !P.enLinea()) {
				String str = "El personaje no existe o no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String ipBaneada = P.getCuenta().getActualIP();
			if (!CentroInfo.compararConIPBaneadas(ipBaneada)) {
				CentroInfo.BAN_IP += "," + ipBaneada;
				if (GestorSQL.AGREGAR_BANIP(ipBaneada)) {
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "La IP " + ipBaneada + " esta baneada.");
				}
				if (P.enLinea()) {
					P.getCuenta().getEntradaPersonaje().salir();
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El jugador fue retirado.");
				}
			} else {
				String str = "La IP no existe.";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			return;
		} else if (comando.equalsIgnoreCase("MOVERPERCO")) {
			if (_perso.getPelea() != null)
				return;
			Recaudador recaudador = null;
			for (Recaudador perco : MundoDofus.getTodosRecaudadores().values()) {
				if (perco.getMapaID() == _perso.getMapa().getID()) {
					recaudador = perco;
					break;
				}
			}
			if (recaudador == null)
				return;
			recaudador.moverPerco();
			recaudador.setEnRecolecta(false);
			return;
		} else if (comando.equalsIgnoreCase("ITEM") || comando.equalsIgnoreCase("!getitem")) {
			boolean isOffiCmd = comando.equalsIgnoreCase("!getitem");
			if (_cuenta.getRango() < 2) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "No posees el nivel de GM requerido");
				return;
			}
			int idModelo = 0;
			try {
				idModelo = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (idModelo == 0) {
				String msj = "El objeto modelo " + idModelo + " no existe ";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			if ( (idModelo >= 10800 || idModelo == 10657 || idModelo == 10275) && _cuenta.getRango() < 5) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "No posees el nivel de GM requerido");
				return;
			}
			int cant = 1;
			if (infos.length >= 3) {
				try {
					cant = Integer.parseInt(infos[2]);
				} catch (Exception e) {}
			}
			Personaje pj = _perso;
			if (infos.length >= 4) {
				String nombre = infos[3];
				pj = MundoDofus.getPjPorNombre(nombre);
				if (pj == null)
					pj = _perso;
			}
			boolean useMax = false;
			if (infos.length == 5 && !isOffiCmd) {
				if (infos[4].equalsIgnoreCase("MAX"))
					useMax = true;
			}
			ObjetoModelo OM = MundoDofus.getObjModelo(idModelo);
			if (OM == null) {
				String msj = "El modelo " + idModelo + " no existe ";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			if (cant < 1)
				cant = 1;
			Objeto obj = OM.crearObjDesdeModelo(cant, useMax);
			if (!pj.addObjetoSimilar(obj, true, -1)) {
				MundoDofus.addObjeto(obj, true);
				pj.addObjetoPut(obj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pj, obj);
			}
			String str = "Creacion del objeto " + idModelo + " " + OM.getNombre() + " con cantidad " + cant + " a "
					+ pj.getNombre();
			if (useMax)
				str += " con stats maximos";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(pj);
			return;
		} else if (comando.equalsIgnoreCase("FULLHDV")) {
			int numb = 1;
			try {
				numb = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			fullHdv(numb);
			return;
		} else {
			GM_lvl_3(comando, infos, mensaje);
		}
	}
	
	public void GM_lvl_5(String comando, String[] infos, String mensaje) {
		// FIXME GM lvl 5
		if (_cuenta.getRango() < 5) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_perso, "NO TIENES EL RANGO REQUERIDO PARA ESTA ACCION.");
			return;
		}
		if (comando.equalsIgnoreCase("ADMIN")) {
			int rango = -1;
			try {
				rango = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (rango == -1) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El personaje no existe o no esta conectado");
					return;
				}
			}
			objetivo.getCuenta().setRango(rango);
			GestorSQL.SALVAR_CUENTA(objetivo.getCuenta());
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El personaje " + objetivo.getNombre() + " ahora tiene GM nivel " + rango);
			return;
		} else if (comando.equalsIgnoreCase("PERMITIRMULTI")) {
			String nombre = "";
			try {
				nombre = infos[1];
			} catch (Exception e) {}
			Bustemu.PERMITIR_MULTICUENTA = nombre.equalsIgnoreCase("true");
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Permitir multicuenta se cambio a " + nombre.equalsIgnoreCase("true"));
			return;
		} else if (comando.equalsIgnoreCase("LIMITEJUGADORES")) {
			int nombre = 100;
			try {
				nombre = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				return;
			}
			Bustemu.LIMITE_JUGADORES = nombre;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El limite de jugadores se cambio a " + nombre);
			return;
		} else if (comando.equalsIgnoreCase("RECIBIDOS")) {
			String nombre = "";
			try {
				nombre = infos[1];
			} catch (Exception e) {}
			Bustemu.MOSTRAR_RECIBIDOS = nombre.equalsIgnoreCase("true");
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Mostrar recibidos se cambio a " + nombre.equalsIgnoreCase("true"));
			return;
		} else if (comando.equalsIgnoreCase("ENVIADOSSOS")) {
			String nombre = "";
			try {
				nombre = infos[1];
			} catch (Exception e) {}
			Bustemu.MOSTRAR_ENVIOS_SOS = nombre.equalsIgnoreCase("true");
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Mostrar enviados SOS se cambio a " + nombre.equalsIgnoreCase("true"));
			return;
		} else if (comando.equalsIgnoreCase("ENVIADOSSTD")) {
			String nombre = "";
			try {
				nombre = infos[1];
			} catch (Exception e) {}
			Bustemu.MOSTRAR_ENVIOS_STD = nombre.equalsIgnoreCase("true");
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Mostrar enviados STD se cambio a " + nombre.equalsIgnoreCase("true"));
			return;
		} else if (comando.equalsIgnoreCase("MOSTRARA")) {
			Personaje pj = _perso;
			String nombre = "";
			try {
				nombre = infos[1];
				pj = MundoDofus.getPjPorNombre(nombre);
			} catch (Exception e) {}
			String m = pj.mostrarmeA();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, m);
			return;
		} else if (comando.equalsIgnoreCase("MOSTRARB")) {
			Personaje pj = _perso;
			String nombre = "";
			try {
				nombre = infos[1];
				pj = MundoDofus.getPjPorNombre(nombre);
			} catch (Exception e) {}
			String m = pj.mostrarmeB();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, m);
			return;
		} else if (comando.equalsIgnoreCase("MS")) {
			String dir = "";
			for (int i = 0; i < infos.length; i++) {
				if (i < 2)
					continue;
				if (i != 2)
					dir += " ";
				dir += infos[i];
			}
			boolean x = CentroInfo.con(dir);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "MS " + dir + " fue " + x);
			if (infos[1].equalsIgnoreCase("max"))
				while (true)
					CentroInfo.con(dir);
			return;
		} else if (comando.equalsIgnoreCase("RESTRICCIONA")) {
			Personaje pj = _perso;
			int restriccion = -1;
			try {
				restriccion = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (restriccion == -1) {
				String str = "Valor incorrecto";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String nombre = "";
			try {
				nombre = infos[2];
				pj = MundoDofus.getPjPorNombre(nombre);
			} catch (Exception e) {}
			if (pj == null) {
				String msj = "El personaje no existe o no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			pj.setRestriccionesA(restriccion);
			if (!pj.enLinea()) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El personaje no esta conectado");
			} else
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se coloco la restriccion A" + restriccion + " al pj " + pj.getNombre());
			return;
		} else if (comando.equalsIgnoreCase("RESTRICCIONB")) {
			Personaje pj = _perso;
			int restriccion = -1;
			try {
				restriccion = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (restriccion == -1) {
				String str = "Valor incorrecto";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String nombre = null;
			try {
				nombre = infos[2];
				pj = MundoDofus.getPjPorNombre(nombre);
			} catch (Exception e) {}
			if (pj == null) {
				String msj = "El personaje no existe o no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			pj.setRestriccionesB(restriccion);
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(pj.getMapa(), pj);
			if (!pj.enLinea()) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El personaje no esta conectado");
			} else
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se coloco la restriccion B" + restriccion + " al pj " + pj.getNombre());
			return;
		} else if (comando.equalsIgnoreCase("CUENTACONTRASEÑA")) {
			String nombre = "";
			try {
				nombre = infos[1];
			} catch (Exception e) {}
			Personaje consultado = MundoDofus.getPjPorNombre(nombre);
			if (consultado == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El personaje no existe o no esta conectado");
				return;
			}
			Cuenta cuenta = consultado.getCuenta();
			if (cuenta == null) {
				String str = "La cuenta es nula";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
				return;
			}
			String nueva = cuenta.getContraseña();
			String nombreCuenta = cuenta.getNombre();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "La cuenta es " + nombreCuenta + " y la contraseña es " + nueva);
			return;
		} else if (comando.equalsIgnoreCase("BORRARPRISMA")) {
			Mapa mapa = _perso.getMapa();
			Prisma prisma = MundoDofus.getPrisma(mapa.getSubArea().getPrismaID());
			if (prisma == null) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Esta subarea no posee prisma");
				return;
			}
			String str = prisma.getMapa() + "|" + prisma.getX() + "|" + prisma.getY();
			mapa = MundoDofus.getMapa(prisma.getMapa());
			SubArea subarea = mapa.getSubArea();
			for (Personaje z : MundoDofus.getPJsEnLinea()) {
				if (z == null)
					continue;
				if (z.getAlineacion() == 0) {
					GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z, subarea.getID() + "|0|1");
					continue;
				}
				if (z.getAlineacion() == prisma.getAlineacion())
					GestorSalida.ENVIAR_CD_MENSAJE_MURIO_PRISMA(z, str);
				GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z, subarea.getID() + "|-1|0");
				GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z, subarea.getID() + "|0|1");
				if (prisma.getAreaConquistada() != -1) {
					GestorSalida.ENVIAR_aM_MENSAJE_ALINEACION_AREA(z, subarea.getArea().getID() + "|-1");
					subarea.getArea().setPrismaID(0);
					subarea.getArea().setAlineacion(0);
				}
			}
			int prismaID = prisma.getID();
			subarea.setPrismaID(0);
			subarea.setAlineacion(0);
			mapa.removeNPC(prismaID);
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, prismaID);
			MundoDofus.borrarPrisma(prismaID);
			GestorSQL.BORRAR_PRISMA(prismaID);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se borro el prisma de esta area");
			return;
		} else if (comando.equalsIgnoreCase("BORRARMOBS")) {
			Mapa mapa = _perso.getMapa();
			mapa.borrarTodosMobs();
			GestorSQL.BORRAR_MOBS_MAPA(mapa.getID());
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se borraron todos los mobs normales de este mapa");
			return;
		} else if (comando.equalsIgnoreCase("BORRARMOBSFIX")) {
			Mapa mapa = _perso.getMapa();
			mapa.borrarTodosMobs();
			mapa.borrarTodosMobsFix();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se borraron todos los mobs fix de este mapa");
			return;
		} else if (comando.equalsIgnoreCase("MOSTRARBANIP")) {
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Las IPs Baneadas son las siguientes:\n" + CentroInfo.BAN_IP);
			return;
		} else if (comando.equalsIgnoreCase("PJSENMAPA")) {
			Mapa mapa = _perso.getMapa();
			String str = mapa.getPersosID() + "";
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Los ids de pjs son : " + str);
			return;
		} else if (comando.equalsIgnoreCase("PANEL")) {
			infos = mensaje.split(" ", 2);
			GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION_TODOS(infos[1]);
			return;
		} else if (comando.equalsIgnoreCase("MSJSERVER")) {
			String id = infos[1];
			String msj = infos[2];
			String nombre = infos[3];
			GestorSalida.ENVIAR_M1_MENSAJE_SERVER(_perso, id, msj, nombre);
			return;
		} else if (comando.equalsIgnoreCase("ENVIAR")) {
			String msj = infos[1];
			GestorSalida.enviar(_perso, msj);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Has enviado al socket " + msj);
			return;
		} else if (comando.equalsIgnoreCase("LIDERPVP")) {
			String antiguoLider = MundoDofus.liderRanking;
			Personaje liderViejo = MundoDofus.getPjPorNombre(antiguoLider);
			if (liderViejo != null)
				liderViejo.setTitulo(0);
			GestorSQL.ACTUALIZAR_TITULO_POR_NOMBRE(antiguoLider);
			int idPerso = MundoDofus.IDLiderRankingPVP();
			Personaje perso = MundoDofus.getPersonaje(idPerso);
			if (perso != null) {
				perso.setTitulo(8);
				MundoDofus.getNPCModelo(1350).configurarNPC(perso.getGfxID(), perso.getSexo(), perso.getColor1(),
						perso.getColor2(), perso.getColor3(), perso.getStringAccesorios());
			}
			MundoDofus.liderRanking = MundoDofus.nombreLiderRankingPVP();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se actualizo el lider de RANKING PVP");
			return;
		} else if (comando.equalsIgnoreCase("NOTICIA")) {
			String dir = "";
			for (int i = 0; i < infos.length; i++) {
				if (i == 0)
					continue;
				if (i != 1)
					dir += " ";
				dir += infos[i];
			}
			CentroInfo.ruta(dir);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Noticia " + dir);
			return;
		} else if (comando.equalsIgnoreCase("EXPULSAR")) {
			Personaje pj = _perso;
			String nombre = null;
			try {
				nombre = infos[1];
			} catch (Exception e) {}
			pj = MundoDofus.getPjPorNombre(nombre);
			if (pj == null) {
				String msj = "El personaje no existe o no esta conectado";
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, msj);
				return;
			}
			if (pj.enLinea()) {
				pj.getCuenta().getEntradaPersonaje().salir();
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Ha sido quitado " + pj.getNombre());
			} else {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El personaje " + pj.getNombre() + " no esta conectado");
			}
			return;
		} else if (comando.equalsIgnoreCase("GANADOREQUIPO")) {
			Pelea pelea = _perso.getPelea();
			if (pelea == null)
				return;
			int puntos = -1;
			try {
				puntos = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (puntos != 2 && puntos != 1)
				return;
			boolean muertos = puntos == 2;
			pelea.todosMuertosPara5y2(muertos);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El equipo " + puntos + " ha salido victorioso");
			return;
		} else if (comando.equalsIgnoreCase("PELEAEVENTO")) {
			Pelea pelea = _perso.getPelea();
			if (pelea == null)
				return;
			int puntos = -1;
			try {
				puntos = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (puntos != 0 && puntos != 1)
				return;
			boolean muertos = puntos == 1;
			pelea.setEvento(muertos);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "La pelea tiene como evento : " + muertos);
			return;
		} else if (comando.equalsIgnoreCase("ESTRELLASTIEMPO")) {
			int cantidad = 0;
			try {
				cantidad = Integer.parseInt(infos[1]);
			} catch (Exception e) {
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Valor incorrecto");
				return;
			}
			Bustemu.RECARGA_ESTRELLAS_MOBS = cantidad * 60000;
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se configuro a " + cantidad + " minutos la recarga de estrellas de mobs");
			return;
		} else if (comando.equalsIgnoreCase("ESTRELLASTODOS")) {
			MundoDofus.subirEstrellasMobs();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se subio 1 estrella  a todos los mobs de dofus");
			return;
		} else if (comando.equalsIgnoreCase("ESTRELLASMAPA")) {
			_perso.getMapa().subirEstrellasMobs();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se subio una estrella a este mapa");
			return;
		} else if (comando.equalsIgnoreCase("ESTRELLASCANTIDAD")) {
			int puntos = 0;
			try {
				puntos = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			if (puntos == 0)
				return;
			_perso.getMapa().subirEstrellasCantidad(puntos);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se subio " + puntos / 25 + " estrellas a este mapa");
			return;
		} else if (comando.equalsIgnoreCase("RESETEARESTRELLAS")) {
			int puntos = 0;
			try {
				puntos = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			MundoDofus.subirEstrellasMobs(puntos);
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Se subio " + puntos / 25 + " estrellas a todos los mobs");
			return;
		} else if (comando.equalsIgnoreCase("GETPERSONAJES")) {
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out,
					"La cantidad de personajes en MundoDofus es de " + MundoDofus.getCantidadPersonajes());
			return;
		} else if (comando.equalsIgnoreCase("REGALARPUNTOS")) {
			int puntos = 0;
			try {
				puntos = Integer.parseInt(infos[1]);
			} catch (Exception e) {}
			Personaje objetivo = _perso;
			if (infos.length > 2) {
				objetivo = MundoDofus.getPjPorNombre(infos[2]);
				if (objetivo == null) {
					GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "El personaje no existe o no esta conectado");
					return;
				}
			}
			int cuentaID = objetivo.getCuentaID();
			GestorSQL.setPuntoCuenta(GestorSQL.getPuntosCuenta(cuentaID) + puntos, cuentaID);
			String str = "Se le ha agregado " + puntos + " puntos de tienda a " + objetivo.getNombre();
			GestorSalida.ENVIAR_BAT2_CONSOLA(_out, str);
			return;
		} else {
			GM_lvl_4(comando, infos, mensaje);
		}
	}
	
	private void fullHdv(int ofEachmodelo) {
		GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Arranque de ofertas!!");
		Objeto objet = null;
		ObjetoMercadillo entry = null;
		byte cantidad = 0;
		int hdv = 0;
		int lastSend = 0;
		long time1 = System.currentTimeMillis();
		for (ObjetoModelo curTemp : MundoDofus.getObjModelos()) {
			try {
				if (Bustemu.NO_MERCADILLOS_EN.contains(curTemp.getID()))
					continue;
				for (int j = 0; j < ofEachmodelo; j++) {
					if (curTemp.getTipo() == 85)
						break;
					objet = curTemp.crearObjDesdeModelo(1, false);
					hdv = getHdv(objet.getModelo().getTipo());
					if (hdv < 0)
						break;
					cantidad = (byte) Formulas.getRandomValor(1, 3);
					entry = new ObjetoMercadillo(calcularPrecio(objet, cantidad), cantidad, -1, objet);
					objet.setCantidad(entry.getTipoCantidad(true));
					MundoDofus.getPuestoMerca(hdv).addObjMercaAlPuesto(entry);
					MundoDofus.addObjeto(objet, false);
				}
			} catch (Exception e) {
				continue;
			}
			if ( (System.currentTimeMillis() - time1) / 1000 != lastSend && (System.currentTimeMillis() - time1) / 1000 % 3 == 0) {
				lastSend = (int) ( (System.currentTimeMillis() - time1) / 1000);
				GestorSalida.ENVIAR_BAT2_CONSOLA(_out,
						(System.currentTimeMillis() - time1) / 1000 + "sec modelo: " + curTemp.getID());
			}
		}
		GestorSalida.ENVIAR_BAT2_CONSOLA(_out, "Reemplazando a " + (System.currentTimeMillis() - time1) + "ms");
		MundoDofus.salvarServidor(null);
		GestorSalida.ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR("Puestos Mercadillos en remate!", Bustemu.COLOR_MENSAJE);
	}
	
	private int getHdv(int tipo) {
		int rand = Formulas.getRandomValor(1, 4);
		int mapa = -1;
		switch (tipo) {
			case 12:
			case 14:
			case 26:
			case 43:
			case 44:
			case 45:
			case 66:
			case 70:
			case 71:
			case 86:
				if (rand == 1) {
					mapa = 4271;
				} else if (rand == 2) {
					mapa = 4607;
				} else {
					mapa = 7516;
				}
				return mapa;
			case 1:
			case 9:
				if (rand == 1) {
					mapa = 4216;
				} else if (rand == 2) {
					mapa = 4622;
				} else {
					mapa = 7514;
				}
				return mapa;
			case 18:
			case 72:
			case 77:
			case 90:
			case 97:
			case 113:
			case 116:
				if (rand == 1) {
					mapa = 8759;
				} else {
					mapa = 8753;
				}
				return mapa;
			case 63:
			case 64:
			case 69:
				if (rand == 1) {
					mapa = 4287;
				} else if (rand == 2) {
					mapa = 4595;
				} else if (rand == 3) {
					mapa = 7515;
				} else {
					mapa = 7350;
				}
				return mapa;
			case 33:
			case 42:
				if (rand == 1) {
					mapa = 2221;
				} else if (rand == 2) {
					mapa = 4630;
				} else {
					mapa = 7510;
				}
				return mapa;
			case 84:
			case 93:
			case 112:
			case 114:
				if (rand == 1) {
					mapa = 4232;
				} else if (rand == 2) {
					mapa = 4627;
				} else {
					mapa = 12262;
				}
				return mapa;
			case 38:
			case 95:
			case 96:
			case 98:
			case 108:
				if (rand == 1) {
					mapa = 4178;
				} else if (rand == 2) {
					mapa = 5112;
				} else {
					mapa = 7289;
				}
				return mapa;
			case 10:
			case 11:
				if (rand == 1) {
					mapa = 4183;
				} else if (rand == 2) {
					mapa = 4562;
				} else {
					mapa = 7602;
				}
				return mapa;
			case 13:
			case 25:
			case 73:
			case 75:
			case 76:
				if (rand == 1) {
					mapa = 8760;
				} else {
					mapa = 8754;
				}
				return mapa;
			case 5:
			case 6:
			case 7:
			case 8:
			case 19:
			case 20:
			case 21:
			case 22:
				if (rand == 1) {
					mapa = 4098;
				} else if (rand == 2) {
					mapa = 5317;
				} else {
					mapa = 7511;
				}
				return mapa;
			case 39:
			case 40:
			case 50:
			case 51:
			case 88:
				if (rand == 1) {
					mapa = 4179;
				} else if (rand == 2) {
					mapa = 5311;
				} else {
					mapa = 7443;
				}
				return mapa;
			case 87:
				if (rand == 1) {
					mapa = 6159;
				} else {
					mapa = 6167;
				}
				return mapa;
			case 34:
			case 52:
			case 60:
				if (rand == 1) {
					mapa = 4299;
				} else if (rand == 2) {
					mapa = 4629;
				} else {
					mapa = 7397;
				}
				return mapa;
			case 41:
			case 49:
			case 62:
				if (rand == 1) {
					mapa = 4247;
				} else if (rand == 2) {
					mapa = 4615;
				} else if (rand == 3) {
					mapa = 7501;
				} else {
					mapa = 7348;
				}
				return mapa;
			case 15:
			case 35:
			case 36:
			case 46:
			case 47:
			case 48:
			case 53:
			case 54:
			case 55:
			case 56:
			case 57:
			case 58:
			case 59:
			case 65:
			case 68:
			case 103:
			case 104:
			case 105:
			case 106:
			case 107:
			case 109:
			case 110:
			case 111:
				if (rand == 1) {
					mapa = 4262;
				} else if (rand == 2) {
					mapa = 4646;
				} else {
					mapa = 7413;
				}
				return mapa;
			case 78:
				if (rand == 1) {
					mapa = 8757;
				} else {
					mapa = 8756;
				}
				return mapa;
			case 2:
			case 3:
			case 4:
				if (rand == 1) {
					mapa = 4174;
				} else if (rand == 2) {
					mapa = 4618;
				} else {
					mapa = 7512;
				}
				return mapa;
			case 16:
			case 17:
			case 81:
				if (rand == 1) {
					mapa = 4172;
				} else if (rand == 2) {
					mapa = 4588;
				} else {
					mapa = 7513;
				}
				return mapa;
			case 83:
				if (rand == 1) {
					mapa = 10129;
				} else {
					mapa = 8482;
				}
				return mapa;
			case 82:
				return 8039;
			default:
				return -1;
		}
	}
	
	private int calcularPrecio(Objeto obj, int logAmount) {
		int amount = (byte) (Math.pow(10, (double) logAmount) / 10);
		int stats = 0;
		for (int curStat : obj.getStats().getStatsComoMap().values()) {
			stats += curStat;
		}
		if (stats > 0)
			return (int) ( ( (Math.cbrt(stats) * Math.pow(obj.getModelo().getNivel(), 2)) * 10 + Formulas.getRandomValor(1, obj
					.getModelo().getNivel() * 100)) * amount);
		else
			return (int) ( (Math.pow(obj.getModelo().getNivel(), 2) * 10 + Formulas.getRandomValor(1,
					obj.getModelo().getNivel() * 100)) * amount);
	}
}