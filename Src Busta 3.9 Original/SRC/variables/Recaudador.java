
package variables;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import variables.Mapa.Celda;
import variables.Pelea.Luchador;

import estaticos.Bustemu;
import estaticos.Camino;
import estaticos.Encriptador;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;

public class Recaudador {
	private int _id;
	private short _mapaID;
	private int _celdaID;
	private int _orientacion;
	private int _gremioID = 0;
	private String _nombre_1 = "";
	private String _nombre_2 = "";
	private int _estadoPelea = 0;
	private int _peleaID = -1;
	private Map<Integer, Objeto> _objetos = new TreeMap<Integer, Objeto>();
	private long _kamas = 0;
	private long _xp = 0;
	private boolean _enRecolecta = false;
	private int _tiempoTurno = 45000;
	private Pelea _pelea;
	
	public Recaudador(int ID, short mapa, int celdaID, byte orientacion, int gremioID, String N1, String N2, String items,
			long kamas, long xp) {
		_id = ID;
		_mapaID = mapa;
		_celdaID = celdaID;
		_orientacion = orientacion;
		_gremioID = gremioID;
		_nombre_1 = N1;
		_nombre_2 = N2;
		for (String item : items.split("\\|")) {
			if (item.equals(""))
				continue;
			String[] infos = item.split(":");
			int id = Integer.parseInt(infos[0]);
			Objeto obj = MundoDofus.getObjeto(id);
			if (obj == null)
				continue;
			_objetos.put(obj.getID(), obj);
		}
		_xp = xp;
		_kamas = kamas;
		_pelea = null;
	}
	
	public long getKamas() {
		return _kamas;
	}
	
	public int getPodsActuales() {
		int pods = 0;
		for (Entry<Integer, Objeto> entry : _objetos.entrySet()) {
			Objeto obj = entry.getValue();
			if (obj == null)
				continue;
			pods += (obj.getModelo().getPeso() * obj.getCantidad());
		}
		return pods;
	}
	
	public void setKamas(long kamas) {
		this._kamas = kamas;
	}
	
	public long getXp() {
		return _xp;
	}
	
	public void setXp(long xp) {
		this._xp = xp;
	}
	
	public void addXp(long xp) {
		this._xp += xp;
	}
	
	public Map<Integer, Objeto> getObjetos() {
		return _objetos;
	}
	
	public void borrarObjeto(int id) {
		_objetos.remove(id);
	}
	
	public boolean tieneObjeto(int id) {
		if (_objetos.get(id) != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public void descontarTiempoTurno(int tiempo) {
		_tiempoTurno -= tiempo;
	}
	
	public void setTiempoTurno(int tiempo) {
		_tiempoTurno = tiempo;
	}
	
	public int getTiempoTurno() {
		return _tiempoTurno;
	}
	
	public static String enviarGMDeRecaudador(Mapa mapa) {
		String packet = "GM|";
		boolean primero = true;
		Map<Integer, Recaudador> todosRecaudadores = MundoDofus.getTodosRecaudadores();
		for (Entry<Integer, Recaudador> recau : todosRecaudadores.entrySet()) {
			Recaudador recaudador = recau.getValue();
			if (recau.getValue()._estadoPelea > 0)
				continue;
			if (recaudador._mapaID == mapa.getID()) {
				if (!primero)
					packet += "|";
				Gremio G = MundoDofus.getGremio(recaudador._gremioID);
				if (G == null) {
					recaudador.borrarRecaudador(recaudador.getID());
					continue;
				}
				packet += "+";
				packet += recaudador._celdaID + ";";
				packet += recaudador._orientacion + ";";
				packet += "0" + ";";
				packet += recaudador._id + ";";
				packet += (recaudador._nombre_1 + "," + recaudador._nombre_2 + ";");
				packet += "-6" + ";";
				packet += "6000^100;";
				packet += G.getNivel() + ";";
				packet += G.getNombre() + ";" + G.getEmblema();
				primero = false;
			} else {
				continue;
			}
		}
		return packet;
	}
	
	public int getGremioID() {
		return _gremioID;
	}
	
	public void borrarRecaudador(int idRecaudador) {
		for (Objeto obj : _objetos.values()) {
			MundoDofus.eliminarObjeto(obj._id);
		}
		MundoDofus.borrarRecaudador(idRecaudador);
	}
	
	public void borrarRecauPorRecolecta(int idRecaudador, Personaje perso) {
		perso.addKamas(_kamas);
		try {
			for (Objeto obj : _objetos.values()) {
				if (obj == null)
					continue;
				int id = 0;
				int cant = 0;
				id = obj.getID();
				cant = obj.getCantidad();
				if (id <= 0 || cant <= 0)
					continue;
				if (perso.addObjetoSimilar(obj, true, -1))
					MundoDofus.eliminarObjeto(id);
				else {
					perso.addObjetoPut(obj);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, obj);
				}
			}
		} catch (NumberFormatException e) {}
		MundoDofus.borrarRecaudador(idRecaudador);
	}
	
	public int getEstadoPelea() {
		return _estadoPelea;
	}
	
	public void setEstadoPelea(int estado) {
		_estadoPelea = estado;
	}
	
	public int getID() {
		return _id;
	}
	
	public int getCeldalID() {
		return _celdaID;
	}
	
	public void setPeleaID(int ID) {
		_peleaID = ID;
	}
	
	public void setPelea(Pelea pelea) {
		_pelea = pelea;
	}
	
	public Pelea getPelea() {
		return _pelea;
	}
	
	public int getPeleaID() {
		return _peleaID;
	}
	
	public int getMapaID() {
		return _mapaID;
	}
	
	public String getN1() {
		return _nombre_1;
	}
	
	public String getN2() {
		return _nombre_2;
	}
	
	public static String analizarRecaudadores(int gremioID) {
		String packet = "+";
		boolean primero = false;
		Map<Integer, Recaudador> todosRecaudadores = MundoDofus.getTodosRecaudadores();
		for (Entry<Integer, Recaudador> recau : todosRecaudadores.entrySet()) {
			Recaudador recaudador = recau.getValue();
			if (recaudador.getGremioID() == gremioID) {
				Mapa mapa = MundoDofus.getMapa((short) recaudador.getMapaID());
				if (primero)
					packet += "|";
				packet += Integer.toString(recaudador.getID(), 36) + ";" + recaudador.getN1() + "," + recaudador.getN2() + ";";
				packet += Integer.toString(mapa.getID(), 36) + "," + mapa.getX() + "," + mapa.getY() + ";";
				packet += recaudador.getEstadoPelea() + ";";
				if (recaudador.getEstadoPelea() == 1) {
					if (mapa.getPelea(recaudador.getPeleaID()) == null) {
						packet += "45000;";
					} else {
						packet += recaudador.getTiempoTurno() + ";";
					}
					packet += "45000;";
					packet += "7;";
					packet += "?,?,";
				} else {
					packet += "0;";
					packet += "45000;";
					packet += "7;";
					packet += "?,?,";
				}
				packet += "1,2,3,4,5";
				primero = true;
			} else {
				continue;
			}
		}
		if (packet.length() == 1)
			packet = null;
		return packet;
	}
	
	public static int getIDGremioPorMapaID(int id) {
		for (Entry<Integer, Recaudador> recau : MundoDofus.getTodosRecaudadores().entrySet()) {
			if (recau.getValue().getMapaID() == id) {
				return recau.getValue().getGremioID();
			}
		}
		return 0;
	}
	
	public static void analizarAtaque(Personaje perso, int gremioID) {
		for (Entry<Integer, Recaudador> recau : MundoDofus.getTodosRecaudadores().entrySet()) {
			Recaudador recaudador = recau.getValue();
			if (recaudador._estadoPelea > 0 && recaudador._gremioID == gremioID) {
				GestorSalida.ENVIAR_gITp_INFO_ATACANTES_RECAUDADOR(perso,
						atacantesAlGremio(recaudador._id, recaudador._mapaID, recaudador._peleaID));
			}
		}
	}
	
	public static void analizarDefensa(Personaje perso, int gremioID) {
		for (Entry<Integer, Recaudador> perco : MundoDofus.getTodosRecaudadores().entrySet()) {
			Recaudador recaudador = perco.getValue();
			if (recaudador._estadoPelea > 0 && recaudador._gremioID == gremioID) {
				GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(perso,
						defensoresDelGremio(recaudador._id, recaudador._mapaID, recaudador._peleaID));
			}
		}
	}
	
	public static String atacantesAlGremio(int id, short mapaID, int peleaID) {
		String str = "+";
		str += Integer.toString(id, 36);
		for (Entry<Integer, Pelea> pelea : MundoDofus.getMapa(mapaID).getPeleas().entrySet()) {
			if (pelea.getValue().getID() == peleaID) {
				for (Luchador luchador : pelea.getValue().luchadoresDeEquipo(1)) {
					if (luchador.getPersonaje() == null)
						continue;
					str += "|";
					str += Integer.toString(luchador.getPersonaje().getID(), 36) + ";";
					str += luchador.getPersonaje().getNombre() + ";";
					str += luchador.getPersonaje().getNivel() + ";";
					str += "0;";
				}
			}
		}
		return str;
	}
	
	public static String defensoresDelGremio(int id, short mapaID, int peleaID) {
		String str = "+";
		String stra = "-";
		str += Integer.toString(id, 36);
		for (Entry<Integer, Pelea> pelea : MundoDofus.getMapa(mapaID).getPeleas().entrySet()) {
			if (pelea.getValue().getID() == peleaID) {
				for (Luchador luchador : pelea.getValue().luchadoresDeEquipo(2)) {
					if (luchador.getPersonaje() == null)
						continue;
					str += "|";
					str += Integer.toString(luchador.getPersonaje().getID(), 36) + ";";
					str += luchador.getPersonaje().getNombre() + ";";
					str += luchador.getPersonaje().getGfxID() + ";";
					str += luchador.getPersonaje().getNivel() + ";";
					str += Integer.toString(luchador.getPersonaje().getColor1(), 36) + ";";
					str += Integer.toString(luchador.getPersonaje().getColor2(), 36) + ";";
					str += Integer.toString(luchador.getPersonaje().getColor3(), 36) + ";";
					str += "0;";
				}
				stra = str.substring(1);
				stra = "-" + stra;
				pelea.getValue().setListaDefensores(stra);
			}
		}
		return str;
	}
	
	public String getListaObjRecaudador() {
		String objetos = "";
		for (Objeto obj : _objetos.values()) {
			objetos += "O" + obj.stringObjetoConGuiño();
		}
		if (_kamas != 0)
			objetos += "G" + _kamas;
		return objetos;
	}
	
	public String stringListaObjetosBD() {
		String objetos = "";
		for (Objeto obj : _objetos.values()) {
			objetos += obj._id + "|";
		}
		return objetos;
	}
	
	public void borrarDesdeRecaudador(Personaje P, int idObjeto, int cantidad) {
		Objeto RecauObj = MundoDofus.getObjeto(idObjeto);
		Objeto PersoObj = P.getObjSimilarInventario(RecauObj);
		int nuevaCant = RecauObj.getCantidad() - cantidad;
		if (PersoObj == null) {
			if (nuevaCant <= 0) {
				borrarObjeto(idObjeto);
				P.addObjetoPut(RecauObj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(P, RecauObj);
				String str = "O-" + idObjeto;
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(P, str);
			} else {
				PersoObj = Objeto.clonarObjeto(RecauObj, cantidad);
				MundoDofus.addObjeto(PersoObj, true);
				RecauObj.setCantidad(nuevaCant);
				P.addObjetoPut(PersoObj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(P, PersoObj);
				String str = "O+" + RecauObj.getID() + "|" + RecauObj.getCantidad() + "|" + RecauObj.getModelo().getID() + "|"
						+ RecauObj.convertirStatsAString();
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(P, str);
			}
		} else {
			if (nuevaCant <= 0) {
				this.borrarObjeto(idObjeto);
				MundoDofus.eliminarObjeto(RecauObj.getID());
				PersoObj.setCantidad(PersoObj.getCantidad() + RecauObj.getCantidad());
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(P, PersoObj);
				String str = "O-" + idObjeto;
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(P, str);
			} else {
				RecauObj.setCantidad(nuevaCant);
				PersoObj.setCantidad(PersoObj.getCantidad() + cantidad);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(P, PersoObj);
				String str = "O+" + RecauObj.getID() + "|" + RecauObj.getCantidad() + "|" + RecauObj.getModelo().getID() + "|"
						+ RecauObj.convertirStatsAString();
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(P, str);
			}
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(P);
		GestorSQL.SALVAR_PERSONAJE(P, true);
	}
	
	public String stringObjetos() {
		String str = "";
		boolean esPrimero = true;
		for (Objeto obj : _objetos.values()) {
			if (!esPrimero)
				str += ";";
			str += obj.getModelo().getID() + "," + obj.getCantidad();
			esPrimero = false;
		}
		return str;
	}
	
	public void addObjeto(Objeto nuevoObj) {
		_objetos.put(nuevoObj.getID(), nuevoObj);
	}
	
	public void setEnRecolecta(boolean Exchange) {
		_enRecolecta = Exchange;
	}
	
	public boolean getEnRecolecta() {
		return _enRecolecta;
	}
	
	public int getOrientacion() {
		return _orientacion;
	}
	
	public Mapa getMapa() {
		return MundoDofus.getMapa(_mapaID);
	}
	
	public void moverPerco() {
		Mapa mapa = MundoDofus.getMapa(_mapaID);
		int celdadestino = Camino.celdaMovPerco(mapa, _celdaID);
		ArrayList<Celda> finalPath = Camino.pathMasCortoEntreDosCeldas(mapa, _celdaID, celdadestino, 0);
		String pathstr = "";
		int tempCeldaID = _celdaID;
		int tempDireccion = 0;
		for (Celda celda : finalPath) {
			char dir = Camino.getDirEntreDosCeldas(tempCeldaID, celda.getID(), mapa, true);
			if (dir == 0)
				return;
			if (dir != 0) {
				if (finalPath.indexOf(celda) != 0)
					pathstr += Encriptador.celdaIDACodigo(tempCeldaID);
				pathstr += dir;
				switch (dir) {
					case 97:
						tempDireccion = 0;
						break;
					case 98:
						tempDireccion = 1;
						break;
					case 99:
						tempDireccion = 2;
						break;
					case 100:
						tempDireccion = 3;
						break;
					case 101:
						tempDireccion = 4;
						break;
					case 102:
						tempDireccion = 5;
						break;
					case 103:
						tempDireccion = 6;
						break;
					case 104:
						tempDireccion = 7;
						break;
				}
			}
			tempCeldaID = celda.getID();
		}
		if (tempCeldaID != _celdaID)
			pathstr += Encriptador.celdaIDACodigo(tempCeldaID);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
		String path = pathstr;
		if (path.equals("")) {
			if (Bustemu.MOSTRAR_RECIBIDOS)
				System.out.println("Fallo de desplazamiento: camino vacio");
			return;
		}
		AtomicReference<String> pathRef = new AtomicReference<String>(path);
		int result = Camino.caminoValido(getMapa(), getCeldalID(), pathRef, null);
		if (result == 0) {
			return;
		}
		if (result != -1000 && result < 0)
			result = -result;
		path = pathRef.get();
		if (result == -1000) {
			path = Encriptador.getValorHashPorNumero(getOrientacion()) + Encriptador.celdaIDACodigo(getCeldalID());
		}
		GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(getMapa(), "" + 0, 1, _id + "", "a" + Encriptador.celdaIDACodigo(_celdaID)
				+ path);
		_celdaID = celdadestino;
		_orientacion = tempDireccion;
		return;
	}
}