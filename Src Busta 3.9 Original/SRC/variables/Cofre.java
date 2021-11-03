
package variables;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import estaticos.Bustemu;
import estaticos.CentroInfo;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;

public class Cofre {
	private int _id;
	private int _casaID;
	private short _mapaID;
	private int _celdaID;
	private Map<Integer, Objeto> _objetos = new TreeMap<Integer, Objeto>();
	private long _kamas;
	private String _clave;
	private int _dueñoID;
	
	public Cofre(int id, int casaID, short mapaID, int celdaID, String objetos, long kamas, String clave, int dueñoID) {
		_id = id;
		_casaID = casaID;
		_mapaID = mapaID;
		_celdaID = celdaID;
		for (String objeto : objetos.split("\\|")) {
			if (objeto.equals(""))
				continue;
			String[] infos = objeto.split(":");
			int idObjeto = Integer.parseInt(infos[0]);
			Objeto obj = MundoDofus.getObjeto(idObjeto);
			if (obj == null)
				continue;
			_objetos.put(obj.getID(), obj);
		}
		_kamas = kamas;
		_clave = clave;
		_dueñoID = dueñoID;
	}
	
	public int getID() {
		return _id;
	}
	
	public int getCasaPorID() {
		return _casaID;
	}
	
	public int getMapaID() {
		return _mapaID;
	}
	
	public int getCeldaID() {
		return _celdaID;
	}
	
	public Map<Integer, Objeto> getObjetos() {
		return _objetos;
	}
	
	public long getKamas() {
		return _kamas;
	}
	
	public void setKamas(long kamas) {
		_kamas = kamas;
	}
	
	public String getClave() {
		return _clave;
	}
	
	public void setClave(String clave) {
		_clave = clave;
	}
	
	public int getDueñoID() {
		return _dueñoID;
	}
	
	public void setDueñoID(int dueñoID) {
		_dueñoID = dueñoID;
	}
	
	public void bloquear(Personaje perso) {
		GestorSalida.ENVIAR_K_CLAVE(perso, "CK1|8");
	}
	
	public static Cofre getCofrePorUbicacion(int mapaID, int celdaID) {
		for (Entry<Integer, Cofre> cofres : MundoDofus.getCofres().entrySet()) {
			Cofre cofre = cofres.getValue();
			if (cofre.getMapaID() == mapaID && cofre.getCeldaID() == celdaID) {
				return cofre;
			}
		}
		return null;
	}
	
	public static void codificarCofre(Personaje perso, String packet) {
		Cofre cofre = perso.getCofre();
		if (cofre == null)
			return;
		if (cofre.esSuCofre(perso, cofre)) {
			GestorSQL.CODIFICAR_COFRE(perso, cofre, packet);
			cofre.setClave(packet);
			cerrarVentanaCofre(perso);
		} else {
			cerrarVentanaCofre(perso);
		}
		perso.setCofre(null);
		return;
	}
	
	public void chekeadoPor(Personaje perso) {
		if (perso.getPelea() != null || perso.getConversandoCon() != 0 || perso.getIntercambiandoCon() != 0
				|| perso.getHaciendoTrabajo() != null || perso.getIntercambio() != null) {
			return;
		}
		Cofre cofre = perso.getCofre();
		Casa casa = MundoDofus.getCasa(_casaID);
		if (cofre == null)
			return;
		if (cofre.getDueñoID() == perso.getCuentaID()
				|| (perso.getGremio() == null ? false : perso.getGremio().getID() == casa.getGremioID()
						&& casa.tieneDerecho(CentroInfo.C_SINCODIGOGREMIO))) {
			abrirCofre(perso, "-", true);
		} else if (perso.getGremio() == null && casa.tieneDerecho(CentroInfo.C_ABRIRGREMIO)) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Este cofre esta abierto sólo para los miembros del gremio.",
					Bustemu.COLOR_MENSAJE);
			return;
		} else if (cofre.getDueñoID() > 0) {
			GestorSalida.ENVIAR_K_CLAVE(perso, "CK0|8");
		} else {
			return;
		}
	}
	
	public static void abrirCofre(Personaje perso, String packet, boolean esSuCofre) {
		Cofre cofre = perso.getCofre();
		if (cofre == null)
			return;
		if (packet.compareTo(cofre.getClave()) == 0 || esSuCofre) {
			GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso.getCuenta().getEntradaPersonaje().getOut(), 5, "");
			GestorSalida.ENVIAR_EL_LISTA_OBJETOS_COFRE(perso, cofre);
			cerrarVentanaCofre(perso);
		} else if (packet.compareTo(cofre.getClave()) != 0) {
			GestorSalida.ENVIAR_K_CLAVE(perso, "KE");
			cerrarVentanaCofre(perso);
			perso.setCofre(null);
		}
	}
	
	public static void cerrarVentanaCofre(Personaje perso) {
		GestorSalida.ENVIAR_K_CLAVE(perso, "V");
	}
	
	public boolean esSuCofre(Personaje perso, Cofre cofre) {
		if (cofre.getDueñoID() == perso.getCuentaID())
			return true;
		else
			return false;
	}
	
	public static ArrayList<Cofre> getCofresPorCasa(Casa casa) {
		ArrayList<Cofre> cofres = new ArrayList<Cofre>();
		for (Entry<Integer, Cofre> cofre : MundoDofus.getCofres().entrySet()) {
			if (cofre.getValue().getCasaPorID() == casa.getID()) {
				cofres.add(cofre.getValue());
			}
		}
		return cofres;
	}
	
	public String analizarCofre() {
		String packet = "";
		for (Objeto obj : _objetos.values())
			packet += "O" + obj.stringObjetoConGuiño() + ";";
		if (getKamas() != 0)
			packet += "G" + getKamas();
		return packet;
	}
	
	public void addEnCofre(int id, int cantidad, Personaje perso) {
		if (_objetos.size() >= 80) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Llegaste al máximo de objetos que puede soportar este cofre",
					Bustemu.COLOR_MENSAJE);
			return;
		}
		Objeto persoObj = MundoDofus.getObjeto(id);
		if (persoObj == null)
			return;
		if (perso.getObjetos().get(id) == null) {
			System.out.println("El jugador " + perso.getNombre()
					+ " a intentado agregar un objeto que no tenía dentro del cofre.");
			return;
		}
		String str = "";
		if (persoObj.getPosicion() != -1)
			return;
		Objeto cofreObj = objetoSimilarEnElCofre(persoObj);
		int nuevaCant = persoObj.getCantidad() - cantidad;
		if (cofreObj == null) {
			if (nuevaCant <= 0) {
				perso.borrarObjetoRemove(persoObj.getID());
				_objetos.put(persoObj.getID(), persoObj);
				str = "O+" + persoObj.getID() + "|" + persoObj.getCantidad() + "|" + persoObj.getModelo().getID() + "|"
						+ persoObj.convertirStatsAString();
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(perso, id);
			} else {
				persoObj.setCantidad(nuevaCant);
				cofreObj = Objeto.clonarObjeto(persoObj, cantidad);
				MundoDofus.addObjeto(cofreObj, true);
				_objetos.put(cofreObj.getID(), cofreObj);
				str = "O+" + cofreObj.getID() + "|" + cofreObj.getCantidad() + "|" + cofreObj.getModelo().getID() + "|"
						+ cofreObj.convertirStatsAString();
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, persoObj);
			}
		} else {
			if (nuevaCant <= 0) {
				perso.borrarObjetoRemove(persoObj.getID());
				MundoDofus.eliminarObjeto(persoObj.getID());
				cofreObj.setCantidad(cofreObj.getCantidad() + persoObj.getCantidad());
				str = "O+" + cofreObj.getID() + "|" + cofreObj.getCantidad() + "|" + cofreObj.getModelo().getID() + "|"
						+ cofreObj.convertirStatsAString();
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(perso, id);
			} else {
				persoObj.setCantidad(nuevaCant);
				cofreObj.setCantidad(cofreObj.getCantidad() + cantidad);
				str = "O+" + cofreObj.getID() + "|" + cofreObj.getCantidad() + "|" + cofreObj.getModelo().getID() + "|"
						+ cofreObj.convertirStatsAString();
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, persoObj);
			}
		}
		for (Personaje pj : perso.getMapa().getPersos()) {
			if (pj.getCofre() != null && getID() == pj.getCofre().getID()) {
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(pj, str);
			}
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
	}
	
	public void retirarDelCofre(int id, int cant, Personaje perso) {
		if (perso.getCofre().getID() != getID())
			return;
		Objeto cofreObj = MundoDofus.getObjeto(id);
		if (cofreObj == null)
			return;
		if (_objetos.get(id) == null) {
			System.out.println("El jugador " + perso.getNombre() + " a intentado retirar un objeto que no habia en el cofre.");
			return;
		}
		Objeto persoObj = perso.getObjSimilarInventario(cofreObj);
		String str = "";
		int nuevaCant = cofreObj.getCantidad() - cant;
		if (persoObj == null) {
			if (nuevaCant <= 0) {
				_objetos.remove(id);
				perso.getObjetos().put(id, cofreObj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, cofreObj);
				str = "O-" + id;
			} else {
				persoObj = Objeto.clonarObjeto(cofreObj, cant);
				MundoDofus.addObjeto(persoObj, true);
				cofreObj.setCantidad(nuevaCant);
				perso.getObjetos().put(persoObj.getID(), persoObj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, persoObj);
				str = "O+" + cofreObj.getID() + "|" + cofreObj.getCantidad() + "|" + cofreObj.getModelo().getID() + "|"
						+ cofreObj.convertirStatsAString();
			}
		} else {
			if (nuevaCant <= 0) {
				_objetos.remove(cofreObj.getID());
				MundoDofus.eliminarObjeto(cofreObj.getID());
				persoObj.setCantidad(persoObj.getCantidad() + cofreObj.getCantidad());
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, persoObj);
				str = "O-" + id;
			} else {
				cofreObj.setCantidad(nuevaCant);
				persoObj.setCantidad(persoObj.getCantidad() + cant);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, persoObj);
				str = "O+" + cofreObj.getID() + "|" + cofreObj.getCantidad() + "|" + cofreObj.getModelo().getID() + "|"
						+ cofreObj.convertirStatsAString();
			}
		}
		for (Personaje pj : perso.getMapa().getPersos()) {
			if (pj.getCofre() != null && getID() == pj.getCofre().getID()) {
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(pj, str);
			}
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
	}
	
	private Objeto objetoSimilarEnElCofre(Objeto obj) {
		for (Objeto objeto : _objetos.values()) {
			if (objeto.getModelo().getTipo() == 85)
				continue;
			if (objeto.getModelo().getID() == obj.getModelo().getID() && objeto.getStats().sonStatsIguales(obj.getStats()))
				return objeto;
		}
		return null;
	}
	
	public String analizarObjetoCofreABD() {
		String str = "";
		for (Objeto objeto : _objetos.values()) {
			str += objeto.getID() + "|";
		}
		return str;
	}
	
	public void limpiarCofre() {
		for (Entry<Integer, Objeto> obj : getObjetos().entrySet()) {
			MundoDofus.eliminarObjeto(obj.getKey());
		}
		getObjetos().clear();
	}
	
	public void moverCofreABanco(Cuenta cuenta) {
		for (Entry<Integer, Objeto> obj : getObjetos().entrySet()) {
			cuenta.getBanco().put(obj.getKey(), obj.getValue());
		}
		getObjetos().clear();
	}
}