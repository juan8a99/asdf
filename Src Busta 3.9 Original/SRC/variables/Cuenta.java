
package variables;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import real.EntradaGeneral;
import servidor.EntradaPersonaje;
import variables.PuestoMercadillo.ObjetoMercadillo;

import estaticos.*;

public class Cuenta {
	private int _ID;
	private String _nombre;
	private String _contraseña;
	private String _apodo;
	private String _key;
	private String _ultimoIP = "";
	private String _pregunta;
	private String _respuesta;
	private boolean _baneado = false;
	private int _rango = 0;
	private int _vip = 0;
	private String _tempIP = "";
	private String _ultimaFechaConeccion = "";
	private EntradaPersonaje _entradaPersonaje;
	private EntradaGeneral _entradaGeneral;
	private Personaje _tempPerso;
	private long _kamasBanco = 0;
	private Map<Integer, Objeto> _objetosEnBanco = new TreeMap<Integer, Objeto>();
	private ArrayList<Integer> _idsAmigos = new ArrayList<Integer>();
	private ArrayList<Integer> _idsEnemigos = new ArrayList<Integer>();
	private ArrayList<Dragopavo> _establo = new ArrayList<Dragopavo>();
	private boolean _muteado = false;
	public long _tiempoMuteado;
	public int _posicion = -1;// Posicion en la cola de espera
	private int _primeraVez;
	private Map<Integer, ArrayList<ObjetoMercadillo>> _objMercadillos;// Contiene los items de HDV <hdvID,<cheapestID>>
	private Map<Integer, Personaje> _personajes = new TreeMap<Integer, Personaje>();
	private int _regalo;
	
	// private boolean _logeado = false;
	public Cuenta(int ID, String nombre, String password, String apodo, String pregunta, String respuesta, int nivelGM, int vip,
			boolean baneado, String ultimaIP, String ultimaConeccion, String banco, int kamasBanco, String amigos,
			String enemigos, String establo, int primeravez, int regalo) {
		_ID = ID;
		_nombre = nombre;
		_contraseña = password;
		_apodo = apodo;
		_pregunta = pregunta;
		_respuesta = respuesta;
		_rango = nivelGM;
		_vip = vip;
		_baneado = baneado;
		_ultimoIP = ultimaIP;
		_ultimaFechaConeccion = ultimaConeccion;
		_kamasBanco = kamasBanco;
		_objMercadillos = MundoDofus.getMisObjetos(_ID);
		// cargando el banco
		for (String item : banco.split("\\|")) {
			if (item.equals(""))
				continue;
			String[] infos = item.split(":");
			int id = Integer.parseInt(infos[0]);
			Objeto obj = MundoDofus.getObjeto(id);
			if (obj == null)
				continue;
			_objetosEnBanco.put(obj.getID(), obj);
		}
		// Cargando lista de amigos
		for (String f : amigos.split(";")) {
			try {
				_idsAmigos.add(Integer.parseInt(f));
			} catch (Exception E) {}
		}
		// Cargando lista de enemigos
		for (String f : enemigos.split(";")) {
			try {
				_idsEnemigos.add(Integer.parseInt(f));
			} catch (Exception E) {}
		}
		for (String d : establo.split(";")) {
			try {
				Dragopavo DP = MundoDofus.getDragopavoPorID(Integer.parseInt(d));
				if (DP != null)
					_establo.add(DP);
			} catch (Exception E) {}
		}
		_primeraVez = primeravez;
		_regalo = regalo;
	}
	
	public ArrayList<Dragopavo> getEstablo() {
		return _establo;
	}
	
	public int getPrimeraVez() {
		return _primeraVez;
	}
	
	public void setKamasBanco(long i) {
		_kamasBanco = i;
		GestorSQL.SALVAR_CUENTA(this);
	}
	
	public boolean estaMuteado() {
		return _muteado;
	}
	
	public int getRegalo() {
		return _regalo;
	}
	
	public void setRegalo() {
		_regalo = 0;
	}
	
	public void setRegalo(int regalo) {
		_regalo = regalo;
	}
	public long _horaMuteada = 0;
	
	public void mutear(boolean b, int tiempo) {
		_muteado = b;
		String msg = "";
		if (_muteado)
			msg = "Ha sido muteado";
		else
			msg = "Ha sido desmuteado";
		GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_tempPerso, msg, Bustemu.COLOR_MENSAJE);
		if (tiempo == 0)
			return;
		_tiempoMuteado = tiempo * 1000;
		_horaMuteada = System.currentTimeMillis();
	}
	
	public String stringBancoObjetosBD() {
		String str = "";
		for (Entry<Integer, Objeto> entry : _objetosEnBanco.entrySet()) {
			Objeto obj = entry.getValue();
			str += obj.getID() + "|";
		}
		return str;
	}
	
	public Map<Integer, Objeto> getBanco() {
		return _objetosEnBanco;
	}
	
	public long getKamasBanco() {
		return _kamasBanco;
	}
	
	public void setEntradaPersonaje(EntradaPersonaje t) {
		_entradaPersonaje = t;
	}
	
	public void setTempIP(String ip) {
		_tempIP = ip;
	}
	
	public String getUltimaConeccion() {
		return _ultimaFechaConeccion;
	}
	
	public void setUltimoIP(String ultimoIP) {
		_ultimoIP = ultimoIP;
	}
	
	public void setUltimaConeccion(String ultimaConeccion) {
		_ultimaFechaConeccion = ultimaConeccion;
	}
	
	public EntradaPersonaje getEntradaPersonaje() {
		return _entradaPersonaje;
	}
	
	public EntradaGeneral getEntradaGeneral() {
		return _entradaGeneral;
	}
	
	public int getID() {
		return _ID;
	}
	
	public String getNombre() {
		return _nombre;
	}
	
	public String getContraseña() {
		return _contraseña;
	}
	
	public String getApodo() {
		if (_apodo.isEmpty() || _apodo == "")
			_apodo = _nombre;
		return _apodo;
	}
	
	public String getClaveCliente() {
		return _key;
	}
	
	public void setClaveCliente(String aKey) {
		_key = aKey;
	}
	
	public Map<Integer, Personaje> getPersonajes() {
		return _personajes;
	}
	
	public String getUltimoIP() {
		return _ultimoIP;
	}
	
	public String getPregunta() {
		return _pregunta;
	}
	
	public Personaje getTempPersonaje() {
		return _tempPerso;
	}
	
	public String getRespuesta() {
		return _respuesta;
	}
	
	public boolean estaBaneado() {
		return _baneado;
	}
	
	public void setBaneado(boolean baneado) {
		_baneado = baneado;
	}
	
	public boolean enLinea() {
		if (_entradaGeneral != null || _entradaPersonaje != null)
			return true;
		return false;
	}
	
	public int getRango() {
		return _rango;
	}
	
	public String getActualIP() {
		return _tempIP;
	}
	
	public void cambiarContraseña(String nueva) {
		_contraseña = nueva;
	}
	
	public int getNumeroPersonajes() {
		return _personajes.size();
	}
	
	public static boolean cuentaLogin(String nombre, String contraseña, String codigoLlave) {
		Cuenta cuenta = MundoDofus.getCuentaPorNombre(nombre);
		if (cuenta != null && cuenta.esContraseñaValida(contraseña, codigoLlave)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean esContraseñaValida(String contraseña, String codigoLlave) {
		return contraseña.equals(Encriptador.encriptarPassword(codigoLlave, _contraseña));
	}
	
	public void addPerso(Personaje perso) {
		if (_personajes.containsKey(perso.getID()))
			return;
		_personajes.put(perso.getID(), perso);
	}
	
	public boolean crearPj(String nombre, int sexo, int clase, int color1, int color2, int color3) {
		Personaje perso = Personaje.crearPersonaje(nombre, sexo, clase, color1, color2, color3, this);
		if (perso == null) {
			return false;
		}
		_personajes.put(perso.getID(), perso);
		GestorSalida.ENVIAR_TB_CINEMA_INICIO_JUEGO(perso);
		return true;
	}
	
	public void borrarPerso(int id) {
		if (!_personajes.containsKey(id))
			return;
		MundoDofus.eliminarPersonaje(_personajes.get(id));
		_personajes.remove(id);
	}
	
	public void setEntradaGeneral(EntradaGeneral thread) {
		_entradaGeneral = thread;
	}
	
	public void setTempPerso(Personaje perso) {
		_tempPerso = perso;
	}
	
	public void actualizarInformacion(int id, String nombre, String contraseña, String apodo, String pregunta, String respuesta,
			int rango, boolean baneado) {
		_ID = id;
		_nombre = nombre;
		_contraseña = contraseña;
		_apodo = apodo;
		_pregunta = pregunta;
		_respuesta = respuesta;
		_rango = rango;
		_baneado = baneado;
	}
	
	public synchronized void desconexion() {
		_tempPerso = null;
		_entradaPersonaje = null;
		_entradaGeneral = null;
		_tempIP = "";
		GestorSQL.SALVAR_CUENTA(this);
		resetTodosPjs();
		GestorSQL.SET_OFFLINE(getID());
	}
	
	public synchronized void resetTodosPjs() {
		for (Personaje perso : _personajes.values()) {
			if (perso.getIntercambio() != null)
				perso.getIntercambio().cancel();
			Pelea pelea = perso.getPelea();
			if (pelea != null) {
				if (pelea.getEstado() != 3)
					pelea.retirarsePelea(perso, null);
				else {
					if (pelea.acabaPeleaSiSeVa(perso.getID()))
						pelea.retirarsePelea(perso, null);
					else {
						perso.getCelda().removerPersonaje(perso.getID());
						perso.setEnLinea(false);
						pelea.desconectarLuchador(perso);
						continue;
					}
				}
			} else if (perso.getMapa() != null)
				GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
			if (perso.getGrupo() != null)
				perso.getGrupo().dejarGrupo(perso);
			perso.getCelda().removerPersonaje(perso.getID());
			perso.setEnLinea(false);
			perso.resetVariables();
			GestorSQL.SALVAR_PERSONAJE(perso, true);
			MundoDofus.desconectarPerso(perso);
		}
	}
	
	public void mensajeAAmigos() {
		for (int i : _idsAmigos) {
			Personaje perso = MundoDofus.getPersonaje(i);
			if (perso != null && perso.mostrarConeccionAmigo() && perso.enLinea())
				GestorSalida.ENVIAR_Im0143_AMIGO_CONECTADO(_tempPerso, perso);
		}
	}
	
	public void addAmigo(int id) {
		if (_ID == id) {
			GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_tempPerso, "Ey");
			return;
		}
		if (_idsEnemigos.contains(id)) {
			GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO(_tempPerso, "Ea");
			return;
		}
		if (!_idsAmigos.contains(id)) {
			_idsAmigos.add(id);
			Cuenta amigo = MundoDofus.getCuenta(id);
			GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_tempPerso, "K" + amigo.getApodo()
					+ amigo.getTempPersonaje().analizarListaAmigos(_ID));
		} else
			GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_tempPerso, "Ea");
	}
	
	public void borrarAmigo(int id) {
		_idsAmigos.remove((Object) id);
		GestorSalida.ENVIAR_FD_BORRAR_AMIGO(_tempPerso, "K");
	}
	
	public boolean esAmigo(int id) {
		return _idsAmigos.contains(id);
	}
	
	public String analizarListaAmigosABD() {
		String str = "";
		for (int i : _idsAmigos) {
			if (!str.equalsIgnoreCase(""))
				str += ";";
			str += i + "";
		}
		return str;
	}
	
	public String stringListaAmigos() {
		String str = "";
		for (int i : _idsAmigos) {
			Cuenta C = MundoDofus.getCuenta(i);
			if (C == null)
				continue;
			str += "|" + C.getApodo();
			if (!C.enLinea())
				continue;
			Personaje P = C.getTempPersonaje();
			if (P == null)
				continue;
			str += P.analizarListaAmigos(_ID);
		}
		return str;
	}
	
	public void addEnemigo(String packet, int id) {
		if (_ID == id) {
			GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO(_tempPerso, "Ey");
			return;
		}
		if (_idsAmigos.contains(id)) {
			GestorSalida.ENVIAR_FA_AGREGAR_AMIGO(_tempPerso, "Ea");
			return;
		}
		if (!_idsEnemigos.contains(id)) {
			_idsEnemigos.add(id);
			Cuenta amigo = MundoDofus.getCuenta(id);
			GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO(_tempPerso, "K" + amigo.getApodo()
					+ amigo.getTempPersonaje().analizarListaEnemigos(_ID));
		} else
			GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO(_tempPerso, "Ea");
	}
	
	public void borrarEnemigo(int id) {
		_idsEnemigos.remove((Object) id);
		GestorSalida.ENVIAR_iD_BORRAR_ENEMIGO(_tempPerso, "K");
	}
	
	public boolean esEnemigo(int id) {
		return _idsEnemigos.contains(id);
	}
	
	public String stringListaEnemigosABD() {
		String str = "";
		for (int i : _idsEnemigos) {
			if (!str.equalsIgnoreCase(""))
				str += ";";
			str += i + "";
		}
		return str;
	}
	
	public String stringListaEnemigos() {
		String str = "";
		for (int i : _idsEnemigos) {
			Cuenta C = MundoDofus.getCuenta(i);
			if (C == null)
				continue;
			str += "|" + C.getApodo();
			if (!C.enLinea())
				continue;
			Personaje P = C.getTempPersonaje();
			if (P == null)
				continue;
			str += P.analizarListaEnemigos(_ID);
		}
		return str;
	}
	
	public void setPrimeraVez(int valor) {
		_primeraVez = valor;
	}
	
	public synchronized String stringIDsEstablo() {
		String str = "";
		boolean primero = false;
		for (Dragopavo DD : _establo) {
			if (primero)
				str += ";";
			str += DD.getID();
			primero = true;
		}
		return str;
	}
	
	public void setRango(int rango) {
		_rango = rango;
	}
	
	public int getVIP() {
		return _vip;
	}
	
	public boolean recuperarObjeto(int lineaID, int cantidad) {
		if (_tempPerso == null)
			return false;
		if (_tempPerso.getIntercambiandoCon() >= 0)
			return false;
		int idPuestoMerca = Math.abs(_tempPerso.getIntercambiandoCon());
		ObjetoMercadillo objMerca = null;
		try {
			for (ObjetoMercadillo tempEntry : _objMercadillos.get(idPuestoMerca)) {
				if (tempEntry.getLineaID() == lineaID) {
					objMerca = tempEntry;
					break;
				}
			}
		} catch (NullPointerException e) {
			return false;
		}
		if (objMerca == null)
			return false;
		_objMercadillos.get(idPuestoMerca).remove(objMerca);// Retira el item de la lista de objetos a vender en la cuenta
		Objeto obj = objMerca.getObjeto();
		if (_tempPerso.addObjetoSimilar(obj, true, -1)) {
			MundoDofus.eliminarObjeto(obj.getID());
		} else {
			_tempPerso.addObjetoPut(obj);
			GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_entradaPersonaje.getOut(), obj);
		}
		MundoDofus.getPuestoMerca(idPuestoMerca).borrarObjMercaDelPuesto(objMerca);// retira el item de HDV
		return true;
	}
	
	public ObjetoMercadillo[] getObjMercaDePuesto(int idPuestoMerca) {
		if (_objMercadillos.get(idPuestoMerca) == null)
			return new ObjetoMercadillo[1];
		ObjetoMercadillo[] listaObjMercadillos = new ObjetoMercadillo[20];
		for (int i = 0; i < _objMercadillos.get(idPuestoMerca).size(); i++) {
			listaObjMercadillos[i] = _objMercadillos.get(idPuestoMerca).get(i);
		}
		return listaObjMercadillos;
	}
	
	public int cantidadObjMercadillo(int idPuestoMerca) {
		if (_objMercadillos.get(idPuestoMerca) == null)
			return 0;
		return _objMercadillos.get(idPuestoMerca).size();
	}
}
