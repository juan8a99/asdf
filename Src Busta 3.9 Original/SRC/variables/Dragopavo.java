
package variables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map.Entry;

import java.util.List;

import javax.swing.Timer;

import estaticos.Bustemu;
import estaticos.Camino;
import estaticos.CentroInfo;
import estaticos.Encriptador;
import estaticos.Formulas;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;

import variables.Mapa.Cercado;
import variables.Objeto.ObjetoModelo;
import variables.Personaje.Stats;

public class Dragopavo {
	private int _id;
	private int _colorID;
	private int _sexo;
	private int _amor;
	private int _resistencia;
	private int _nivel;
	private long _experiencia;
	private String _nombre;
	private int _fatiga;
	private int _energia;
	private int _reprod;
	private int _madurez;
	private int _serenidad;
	private Stats _stats = new Stats();
	private String _ancestros = "?,?,?,?,?,?,?,?,?,?,?,?,?,?";
	private ArrayList<Objeto> _objetos = new ArrayList<Objeto>();
	private List<Integer> _capacidades = new ArrayList<Integer>(2);
	String _habilidad = ",";
	private int _celdaID;
	private int _dueño;
	private int _talla;
	private short _mapaID;
	private int _orientacion;
	private int _fecundadaHace;
	private int _pareja;
	private Timer _aumentarFecundo = new Timer(60000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_fecundadaHace++;
			startFecundo();
		}
	});
	private String _vip = "";
	
	public Dragopavo(int color, int dueño) {
		int sexo = Formulas.getRandomValor(0, 1);
		_id = MundoDofus.getSigIDMontura();
		_colorID = color;
		_sexo = sexo;
		_nivel = 5;
		_experiencia = 0;
		_nombre = "Elbusta";
		_fatiga = 0;
		_energia = getMaxEnergia();
		_reprod = 0;
		_madurez = getMaxMadurez();
		_serenidad = 0;
		_stats = _colorID == 75 ? CentroInfo.getStatsMonturaVIP(_vip, _nivel) : CentroInfo.getStatsMontura(_colorID, _nivel);
		_ancestros = "?,?,?,?,?,?,?,?,?,?,?,?,?,?";
		_habilidad = "0";
		_talla = 100;
		_dueño = dueño;
		_celdaID = -1;
		_mapaID = -1;
		_orientacion = 1;
		_fecundadaHace = -1;
		_pareja = -1;
		_vip = "";
		MundoDofus.addDragopavo(this);
		GestorSQL.CREAR_MONTURA(this);
	}
	
	public Dragopavo(int color, Dragopavo madre, Dragopavo padre) {
		int[] sexo = { 0, 0, 1, 0 };
		_id = MundoDofus.getSigIDMontura();
		_colorID = color;
		_sexo = sexo[Formulas.getRandomValor(0, 3)];
		_nivel = 5;
		_experiencia = 0;
		_nombre = "(Sin Nombre)";
		_fatiga = 0;
		_energia = 0;
		_reprod = 0;
		_madurez = 0;
		_serenidad = 0;
		_stats = CentroInfo.getStatsMontura(_colorID, _nivel);
		String[] papa = padre.getAncestros().split(",");
		String[] mama = madre.getAncestros().split(",");
		String primerapapa = papa[0] + "," + papa[1];
		String primeramama = mama[0] + "," + mama[1];
		String segundapapa = papa[2] + "," + papa[3] + "," + papa[4] + "," + papa[5];
		String segundamama = mama[2] + "," + mama[3] + "," + mama[4] + "," + mama[5];
		_ancestros = padre.getColor() + "," + madre.getColor() + "," + primerapapa + "," + primeramama + "," + segundapapa + ","
				+ segundamama;
		int habilidad = Formulas.getRandomValor(1, 8);
		_habilidad = "" + habilidad;
		_celdaID = -1;
		_mapaID = -1;
		_dueño = madre.getDueño();
		_talla = 50;
		_orientacion = 1;
		_fecundadaHace = -1;
		_pareja = -1;
		_vip = "";
		MundoDofus.addDragopavo(this);
		GestorSQL.CREAR_MONTURA(this);
	}
	
	public Dragopavo(int id, int color, int sexo, int amor, int resistencia, int nivel, long exp, String nombre, int fatiga,
			int energia, int reprod, int madurez, int serenidad, String objetos, String anc, String habilidad, int talla,
			int celda, short mapa, int dueño, int orientacion, int fecundable, int pareja, String vip) {
		_id = id;
		_colorID = color;
		_sexo = sexo;
		_amor = amor;
		_resistencia = resistencia;
		_nivel = nivel;
		_experiencia = exp;
		_nombre = nombre;
		_fatiga = fatiga;
		_energia = energia;
		_reprod = reprod;
		_madurez = madurez;
		_serenidad = serenidad;
		_ancestros = anc;
		_vip = vip;
		_stats = _colorID == 75 ? CentroInfo.getStatsMonturaVIP(_vip, _nivel) : CentroInfo.getStatsMontura(_colorID, _nivel);
		_habilidad = habilidad;
		_talla = talla;
		_celdaID = celda;
		_mapaID = mapa;
		_dueño = dueño;
		_orientacion = orientacion;
		_fecundadaHace = fecundable;
		_pareja = pareja;
		for (String s : habilidad.split(",", 2)) {
			if (s != null) {
				int a = Integer.parseInt(s);
				try {
					_capacidades.add(a);
				} catch (Exception e) {}
			}
		}
		for (String str : objetos.split(";")) {
			try {
				Objeto obj = MundoDofus.getObjeto(Integer.parseInt(str));
				if (obj != null)
					_objetos.add(obj);
			} catch (Exception e) {
				continue;
			}
		}
	}
	
	public void startFecundo() {
		if (_fecundadaHace < 120 && _fecundadaHace > 0) {
			_aumentarFecundo.restart();
		} else {
			_aumentarFecundo.stop();
		}
	}
	
	public int getID() {
		return _id;
	}
	
	public int getColor() {
		return _colorID;
	}
	
	public void setStatsVIP(String vip) {
		_vip = vip;
		_stats = CentroInfo.getStatsMonturaVIP(vip, _nivel);
	}
	
	public String getVIP() {
		return _vip;
	}
	
	public int getSexo() {
		return _sexo;
	}
	
	public int getAmor() {
		return _amor;
	}
	
	public String getAncestros() {
		return _ancestros;
	}
	
	public int getResistencia() {
		return _resistencia;
	}
	
	public int getPodsActuales() {
		int pods = 0;
		for (Objeto obj : _objetos) {
			if (obj == null)
				continue;
			pods += (obj.getModelo().getPeso() * obj.getCantidad());
		}
		return pods;
	}
	
	public String getListaObjDragopavo() {
		String objetos = "";
		for (Objeto obj : _objetos) {
			objetos += "O" + obj.stringObjetoConGuiño();
		}
		return objetos;
	}
	
	public void addObjAMochila(int id, int cant, Personaje perso) {
		Objeto objetoAgregar = MundoDofus.getObjeto(id);
		if (objetoAgregar.getPosicion() != -1)
			return;
		Objeto objIgualEnMochila = getSimilarObjeto(objetoAgregar);
		int nuevaCant = objetoAgregar.getCantidad() - cant;
		if (objIgualEnMochila == null) {
			if (nuevaCant <= 0) {
				perso.borrarObjetoRemove(objetoAgregar.getID());
				_objetos.add(objetoAgregar);
				String str = "O+" + objetoAgregar.getID() + "|" + objetoAgregar.getCantidad() + "|"
						+ objetoAgregar.getModelo().getID() + "|" + objetoAgregar.convertirStatsAString();
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(perso, id);
			} else {
				objetoAgregar.setCantidad(nuevaCant);
				objIgualEnMochila = Objeto.clonarObjeto(objetoAgregar, cant);
				MundoDofus.addObjeto(objIgualEnMochila, true);
				_objetos.add(objIgualEnMochila);
				String str = "O+" + objIgualEnMochila.getID() + "|" + objIgualEnMochila.getCantidad() + "|"
						+ objIgualEnMochila.getModelo().getID() + "|" + objIgualEnMochila.convertirStatsAString();
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objetoAgregar);
			}
		} else {
			if (nuevaCant <= 0) {
				perso.borrarObjetoRemove(objetoAgregar.getID());
				objIgualEnMochila.setCantidad(objIgualEnMochila.getCantidad() + objetoAgregar.getCantidad());
				String str = "O+" + objIgualEnMochila.getID() + "|" + objIgualEnMochila.getCantidad() + "|"
						+ objIgualEnMochila.getModelo().getID() + "|" + objIgualEnMochila.convertirStatsAString();
				MundoDofus.eliminarObjeto(objetoAgregar.getID());
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(perso, id);
			} else {
				objetoAgregar.setCantidad(nuevaCant);
				objIgualEnMochila.setCantidad(objIgualEnMochila.getCantidad() + cant);
				String str = "O+" + objIgualEnMochila.getID() + "|" + objIgualEnMochila.getCantidad() + "|"
						+ objIgualEnMochila.getModelo().getID() + "|" + objIgualEnMochila.convertirStatsAString();
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objetoAgregar);
			}
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
		GestorSalida.ENVIAR_Ew_PODS_MONTURA(perso, getPodsActuales());
		GestorSQL.ACTUALIZAR_MONTURA(this, false);
	}
	
	private Objeto getSimilarObjeto(Objeto obj) {
		for (Objeto value : _objetos) {
			ObjetoModelo objetoMod = value.getModelo();
			if (objetoMod.getTipo() == 85)
				continue;
			if (objetoMod.getID() == obj.getModelo().getID() && value.getStats().sonStatsIguales(obj.getStats()))
				return value;
		}
		return null;
	}
	
	public void removerDeLaMochila(int id, int cant, Personaje perso) {
		Objeto objARetirar = MundoDofus.getObjeto(id);
		if (!_objetos.contains(objARetirar)) {
			return;
		}
		Objeto objIgualInventario = perso.getObjSimilarInventario(objARetirar);
		int nuevaCant = objARetirar.getCantidad() - cant;
		if (objIgualInventario == null) {
			if (nuevaCant <= 0) {
				_objetos.remove(objARetirar);
				if (perso.addObjetoSimilar(objARetirar, true, -1))
					MundoDofus.eliminarObjeto(id);
				else {
					perso.addObjetoPut(objARetirar);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, objARetirar);
				}
				String str = "O-" + id;
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str);
			} else {
				objIgualInventario = Objeto.clonarObjeto(objARetirar, cant);
				MundoDofus.addObjeto(objIgualInventario, true);
				objARetirar.setCantidad(nuevaCant);
				perso.addObjetoPut(objIgualInventario);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, objIgualInventario);
				String str = "O+" + objARetirar.getID() + "|" + objARetirar.getCantidad() + "|" + objARetirar.getModelo().getID()
						+ "|" + objARetirar.convertirStatsAString();
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str);
			}
		} else {
			if (nuevaCant <= 0) {
				_objetos.remove(objARetirar);
				objIgualInventario.setCantidad(objIgualInventario.getCantidad() + objARetirar.getCantidad());
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objIgualInventario);
				MundoDofus.eliminarObjeto(objARetirar.getID());
				String str = "O-" + id;
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str);
			} else {
				objARetirar.setCantidad(nuevaCant);
				objIgualInventario.setCantidad(objIgualInventario.getCantidad() + cant);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objIgualInventario);
				String str = "O+" + objARetirar.getID() + "|" + objARetirar.getCantidad() + "|" + objARetirar.getModelo().getID()
						+ "|" + objARetirar.convertirStatsAString();
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str);
			}
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
		GestorSalida.ENVIAR_Ew_PODS_MONTURA(perso, getPodsActuales());
		GestorSQL.ACTUALIZAR_MONTURA(this, false);
	}
	
	public void setDueño(int dueño) {
		_dueño = dueño;
	}
	
	public int getNivel() {
		return _nivel;
	}
	
	public long getExp() {
		return _experiencia;
	}
	
	public String getNombre() {
		return _nombre;
	}
	
	public int getDueño() {
		return _dueño;
	}
	
	public boolean estaCriando() {
		if (_celdaID == -1)
			return false;
		else
			return true;
	}
	
	public void actCapacidades() {
		_capacidades.clear();
		for (String s : _habilidad.split(",", 2)) {
			if (s != null) {
				int a = Integer.parseInt(s);
				try {
					_capacidades.add(a);
				} catch (Exception e) {}
			}
		}
	}
	
	public int getFecundadaHace() {
		if (_reprod == -1 || _reprod >= 20) {
			_fecundadaHace = -1;
			return -1;
		}
		if (_fecundadaHace >= 1)
			return _fecundadaHace;
		_fecundadaHace = -1;
		return _fecundadaHace;
	}
	
	public void setHabilidad(String habilidad) {
		_habilidad = habilidad;
		actCapacidades();
	}
	
	public int getFecunda() {
		if (_reprod == -1 || _fecundadaHace >= 1)
			return 0;
		if (_amor >= 7500 && _resistencia >= 7500 && _nivel > 4)
			return 10;
		return 0;
	}
	
	public void setMapaCelda(short mapa, int celda) {
		_mapaID = mapa;
		_celdaID = celda;
	}
	
	public int getFatiga() {
		return _fatiga;
	}
	
	public short getMapa() {
		return _mapaID;
	}
	
	public int getCelda() {
		return _celdaID;
	}
	
	public int getTalla() {
		return _talla;
	}
	
	public int getEnergia() {
		return _energia;
	}
	
	public int getReprod() {
		return _reprod;
	}
	
	public int getMadurez() {
		return _madurez;
	}
	
	public int getSerenidad() {
		return _serenidad;
	}
	
	public Stats getStats() {
		return _stats;
	}
	
	public ArrayList<Objeto> getObjetos() {
		return _objetos;
	}
	
	public List<Integer> getCapacidades() {
		return _capacidades;
	}
	
	public String detallesMontura() {
		String str = _id + ":";
		str += _colorID + ":";
		str += _ancestros + ":";
		str += ",," + _habilidad + ":";
		str += _nombre + ":";
		str += _sexo + ":";
		str += parseXpString() + ":";
		str += _nivel + ":";
		str += esMontable() + ":";
		str += getTotalPod() + ":";
		str += "0" + ":"; // estado salvaje
		str += _resistencia + ",10000:";
		str += _madurez + "," + getMaxMadurez() + ":";
		str += _energia + "," + getMaxEnergia() + ":";
		str += _serenidad + ",-10000,10000:";
		str += _amor + ",10000:";
		str += getFecundadaHace() + ":";
		str += getFecunda() + ":";
		str += convertirStringAStats() + ":";
		str += _fatiga + ",240:";
		str += _reprod + ",20:";
		return str;
	}
	
	public void castrarPavo() {
		_reprod = -1;
	}
	
	private String convertirStringAStats() {
		String stats = "";
		for (Entry<Integer, Integer> entry : _stats.getStatsComoMap().entrySet()) {
			if (entry.getValue() <= 0)
				continue;
			if (stats.length() > 0)
				stats += ",";
			stats += Integer.toHexString(entry.getKey()) + "#" + Integer.toHexString(entry.getValue()) + "#0#0";
		}
		return stats;
	}
	
	private int getMaxEnergia() {
		return (10 * _nivel) + (150 * CentroInfo.getGeneracion(_colorID));
	}
	
	private int getMaxMadurez() {
		return 1500 * CentroInfo.getGeneracion(_colorID);
	}
	
	private int getTotalPod() {
		int habilidad = 0;
		if (_capacidades.contains(2))
			habilidad = 20 * _nivel;
		return (10 * _nivel) + (100 * CentroInfo.getGeneracion(_colorID) + habilidad);
	}
	
	private String parseXpString() {
		return _experiencia + "," + MundoDofus.getExpNivel(_nivel)._montura + "," + MundoDofus.getExpNivel(_nivel + 1)._montura;
	}
	
	public int esMontable() {
		if (_energia < 10 || _madurez < getMaxMadurez() || _fatiga == 240)
			return 0;
		return 1;
	}
	
	public void aumFatiga() {
		_fatiga += 2;
		if (_capacidades.contains(1))
			_fatiga -= 1;
		if (_fatiga > 240)
			_fatiga = 240;
	}
	
	public void aumResistencia() {
		_resistencia += 100 * Bustemu.RATE_CRIANZA_PAVOS;
		if (_capacidades.contains(5))
			_resistencia += 100 * Bustemu.RATE_CRIANZA_PAVOS;
		if (_resistencia > 10000)
			_resistencia = 10000;
	}
	
	public void setAmor(int amor) {
		_amor = amor;
	}
	
	public void setResistencia(int resistencia) {
		_resistencia = resistencia;
	}
	
	public void setMaxMadurez() {
		_madurez = getMaxMadurez();
	}
	
	public void setMaxEnergia() {
		_energia = getMaxEnergia();
	}
	
	public void aumMadurez() {
		int maxMadurez = getMaxMadurez();
		if (_madurez < maxMadurez) {
			_madurez += 100 * Bustemu.RATE_CRIANZA_PAVOS;
			if (_capacidades.contains(7))
				_madurez += 100 * Bustemu.RATE_CRIANZA_PAVOS;
			if (_talla < 100) {
				Mapa mapa = MundoDofus.getMapa(_mapaID);
				if ((int) (maxMadurez / _madurez) <= 1) {
					_talla = 100;
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, _id);
					GestorSalida.ENVIAR_GM_DRAGOPAVO_A_MAPA(mapa, this);
					return;
				} else if (_talla < 75 && (int) (maxMadurez / _madurez) == 2) {
					_talla = 75;
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, _id);
					GestorSalida.ENVIAR_GM_DRAGOPAVO_A_MAPA(mapa, this);
					return;
				} else if (_talla < 50 && (int) (maxMadurez / _madurez) == 3) {
					_talla = 50;
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, _id);
					GestorSalida.ENVIAR_GM_DRAGOPAVO_A_MAPA(mapa, this);
					return;
				}
			}
		}
		if (_madurez > maxMadurez)
			_madurez = maxMadurez;
	}
	
	public void aumAmor() {
		_amor += 100 * Bustemu.RATE_CRIANZA_PAVOS;
		if (_amor > 10000)
			_amor = 10000;
	}
	
	public void aumSerenidad() {
		_serenidad += 100 * Bustemu.RATE_CRIANZA_PAVOS;
		if (_serenidad > 10000)
			_serenidad = 10000;
	}
	
	public void resSerenidad() {
		_serenidad -= 100 * Bustemu.RATE_CRIANZA_PAVOS;
		if (_serenidad < -10000)
			_serenidad = -10000;
	}
	
	public void aumEnergia() {
		_energia += 10 * Bustemu.RATE_CRIANZA_PAVOS;
		int maxEnergia = getMaxEnergia();
		if (_energia > maxEnergia)
			_energia = maxEnergia;
	}
	
	public void aumEnergia(int valor, int veces) {
		_energia += (valor * veces);
		int maxEnergia = getMaxEnergia();
		if (_energia > maxEnergia)
			_energia = maxEnergia;
	}
	
	public void resFatiga() {
		_fatiga -= 20;
		if (_fatiga < 0)
			_fatiga = 0;
	}
	
	public void resAmor(int amor) {
		_amor -= amor;
		if (_amor < 0)
			_amor = 0;
	}
	
	public void resResistencia(int resistencia) {
		_resistencia -= resistencia;
		if (_resistencia < 0)
			_resistencia = 0;
	}
	
	public void aumReproduccion() {
		if (_reprod == -1)
			return;
		_reprod += 1;
	}
	
	public String stringObjetosBD() {
		String str = "";
		for (Objeto obj : _objetos)
			str += (str.length() > 0 ? ";" : "") + obj.getID();
		return str;
	}
	
	public void setNombre(String nombre) {
		_nombre = nombre;
	}
	
	public void addXp(long aumentar) {
		if (_capacidades.contains(4))
			aumentar = aumentar * 2;
		_experiencia += aumentar;
		while (_experiencia >= MundoDofus.getExpNivel(_nivel + 1)._montura && _nivel < 200)
			subirNivel();
	}
	
	public void subirNivel() {
		_nivel++;
		if (_colorID != 74)
			_stats = CentroInfo.getStatsMontura(_colorID, _nivel);
		else
			_stats = CentroInfo.getStatsMonturaVIP(_vip, _nivel);
	}
	
	public String getStringColor(String colorDueñoPavo) {
		String b = "";
		if (_capacidades.contains(9))
			b = "," + colorDueñoPavo;
		if (_colorID == 75) {
			int colorRandom = Formulas.getRandomValor(1, 87);
			b = "," + CentroInfo.getStringColorDragopavo(colorRandom);
		}
		return _colorID + b;
	}
	
	public String getHabilidad() {
		return _habilidad;
	}
	
	public boolean addCapacidad(String capa) {
		int c = 0;
		for (String s : capa.split(",", 2)) {
			if (_capacidades.size() >= 2)
				return false;
			try {
				c = Integer.parseInt(s);
			} catch (Exception e) {}
			if (c != 0) {
				_capacidades.add(c);
			}
			if (_capacidades.size() == 1) {
				_habilidad = _capacidades.get(0) + ",";
			} else {
				_habilidad = _capacidades.get(0) + "," + _capacidades.get(1);
			}
		}
		return true;
	}
	
	public void energiaPerdida(int energia) {
		_energia = energia;
	}
	
	public int getPareja() {
		return _pareja;
	}
	
	public void setPareja(int pareja) {
		_pareja = pareja;
	}
	
	public int getOrientacion() {
		return _orientacion;
	}
	
	public void setFecundadaHace(int fecundable) {
		if (_reprod == -1)
			return;
		_fecundadaHace = fecundable;
	}
	
	public boolean esCastrado() {
		if (_reprod == -1)
			return true;
		else
			return false;
	}
	
	public synchronized String getCriarMontura(Cercado cercado) {
		String str = "GM|+";
		if (_celdaID == -1 && _mapaID == -1)
			str += cercado.getColocarCelda() + ";";
		else
			str += _celdaID + ";";
		str += _orientacion + ";0;" + _id + ";" + _nombre + ";-9;";
		if (_colorID == 88)
			str += 7005;
		else
			str += 7002;
		str += "^" + _talla + ";";
		if (MundoDofus.getPersonaje(_dueño) == null)
			str += "Sin Dueño";
		else
			str += MundoDofus.getPersonaje(_dueño).getNombre();
		str += ";" + _nivel + ";" + _colorID;
		return str;
	}
	
	public synchronized void moverMontura(Personaje dueño, int casillas, boolean alejar) {
		int accion = 0;
		if (dueño == null)
			return;
		if (dueño.getCelda().getID() == _celdaID)
			return;
		String path = "";
		Mapa mapa = dueño.getMapa();
		if (mapa.getCercado() == null)
			return;
		Cercado cercado = mapa.getCercado();
		char direccion;
		int azar = Formulas.getRandomValor(1, 10);
		direccion = Camino.getDirEntreDosCeldas(mapa, _celdaID, dueño.getCelda().getID());
		if (alejar)
			direccion = Camino.getDireccionOpuesta(direccion);
		int celda = _celdaID;
		int celdaprueba = _celdaID;
		for (int i = 0; i < casillas; i++) {
			celdaprueba = Camino.getSigIDCeldaMismaDir(celdaprueba, direccion, _mapaID);
			if (mapa.getCelda(celdaprueba) == null)
				return;
			if (cercado.getCeldayObjeto().containsKey(celdaprueba)) {
				int item = cercado.getCeldayObjeto().get(celdaprueba);
				if (item == 7758) {// Aporreadora de olmo
					resSerenidad();
				} else if (item == 7781) {// Fulminadora de olmo
					if (_serenidad < 0)
						aumResistencia();
				} else if (item == 7613) {// Pesebre de carpe
					resFatiga();
					aumEnergia();
				} else if (item == 7696) {// Dragonalgas de cuero violeta de bwork
					if (_serenidad > 0)
						aumAmor();
				} else if (item == 7628) {// Acariciador de Pluma del Último Pohoyo
					aumSerenidad();
				} else if (item == 7594) {// Abrevadero de Carpe
					if (_serenidad <= 2000 && _serenidad >= -2000)
						aumMadurez();
				}
				aumFatiga();
				break;
			}
			if (mapa.getCelda(celdaprueba).esCaminable(false) && cercado.getPuerta() != celdaprueba
					&& !mapa.celdaSalienteLateral(celda, celdaprueba)) {
				celda = celdaprueba;
				path += direccion + Encriptador.celdaIDACodigo(celda);
			} else
				break;
		}
		if (celda == _celdaID) {
			_orientacion = Encriptador.getNumeroPorValorHash(direccion);
			GestorSalida.ENVIAR_eD_CAMBIAR_ORIENTACION(mapa, _id, _orientacion);
			GestorSalida.ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(mapa, celdaprueba + ";4");
			GestorSalida.ENVIAR_eUK_EMOTE_MAPA(mapa, _id, accion, "");
			return;
		}
		if (azar == 5)
			accion = 8;
		if (cercado.getListaCriando().size() > 1) {
			for (Integer pavo : cercado.getListaCriando()) {
				Dragopavo dragopavo = MundoDofus.getDragopavoPorID(pavo);
				if (dragopavo._sexo != _sexo && dragopavo.getFecunda() != 0 && getFecunda() != 0
						&& dragopavo.getCelda() == celdaprueba) {
					if (dragopavo._reprod < 20 && _reprod < 20 && !dragopavo.esCastrado() && !esCastrado()) {
						int aparearce = Formulas.getRandomValor(2, 4);
						if (dragopavo._capacidades.contains(6) || _capacidades.contains(6))
							aparearce = 3;
						if (aparearce == 3) {
							if (dragopavo._sexo == 1) {
								dragopavo._fecundadaHace = 1;
								dragopavo._aumentarFecundo.start();
								dragopavo.setPareja(_id);
								resAmor(7500);
								resResistencia(7500);
							} else if (_sexo == 1) {
								_fecundadaHace = 1;
								_aumentarFecundo.start();
								_pareja = dragopavo.getID();
								dragopavo.resAmor(7500);
								dragopavo.resResistencia(7500);
							}
							accion = 4;
							break;
						}
					}
				}
			}
		}
		GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(mapa, "" + 0, 1, _id + "", "a" + Encriptador.celdaIDACodigo(_celdaID) + path);
		_celdaID = celda;
		_orientacion = Encriptador.getNumeroPorValorHash(direccion);
		int ID = _id;
		try {
			Thread.sleep(1250);
		} catch (Exception e) {}
		GestorSalida.ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(mapa, celdaprueba + ";4");
		try {
			Thread.sleep(500);
		} catch (Exception e) {}
		if (accion != 0)
			GestorSalida.ENVIAR_eUK_EMOTE_MAPA(mapa, ID, accion, "");
		return;
	}
	
	public synchronized void moverMonturaAuto(char dir, int casillas, boolean alejar) {
		int accion = 0;
		String path = "";
		Mapa mapa = MundoDofus.getMapa(_mapaID);
		if (mapa == null)
			return;
		if (mapa.getCercado() == null)
			return;
		Cercado cercado = mapa.getCercado();
		char direccion = dir;
		int azar = Formulas.getRandomValor(1, 10);
		int celda = _celdaID;
		int celdaprueba = _celdaID;
		for (int i = 0; i < casillas; i++) {
			celdaprueba = Camino.getSigIDCeldaMismaDir(celdaprueba, direccion, _mapaID);
			if (mapa.getCelda(celdaprueba) == null)
				return;
			if (cercado.getCeldayObjeto().containsKey(celdaprueba)) {
				int item = cercado.getCeldayObjeto().get(celdaprueba);
				if (item == 7758) {// Aporreadora de olmo
					resSerenidad();
				} else if (item == 7781) {// Fulminadora de olmo
					if (_serenidad < 0)
						aumResistencia();
				} else if (item == 7613) {// Pesebre de carpe
					resFatiga();
					aumEnergia();
				} else if (item == 7696) {// Dragonalgas de cuero violeta de bwork
					if (_serenidad > 0)
						aumAmor();
				} else if (item == 7628) {// Acariciador de Pluma del Último Pohoyo
					aumSerenidad();
				} else if (item == 7594) {// Abrevadero de Carpe
					if (_serenidad <= 2000 && _serenidad >= -2000)
						aumMadurez();
				}
				aumFatiga();
				break;
			}
			if (mapa.getCelda(celdaprueba).esCaminable(false) && cercado.getPuerta() != celdaprueba
					&& !mapa.celdaSalienteLateral(celda, celdaprueba)) {
				celda = celdaprueba;
				path += direccion + Encriptador.celdaIDACodigo(celda);
			} else
				break;
		}
		if (celda == _celdaID) {
			_orientacion = Encriptador.getNumeroPorValorHash(direccion);
			GestorSalida.ENVIAR_eD_CAMBIAR_ORIENTACION(mapa, _id, _orientacion);
			GestorSalida.ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(mapa, celdaprueba + ";4");
			GestorSalida.ENVIAR_eUK_EMOTE_MAPA(mapa, _id, accion, "");
			return;
		}
		if (azar == 5)
			accion = 8;
		if (cercado.getListaCriando().size() > 1) {
			for (Integer pavo : cercado.getListaCriando()) {
				Dragopavo dragopavo = MundoDofus.getDragopavoPorID(pavo);
				if (dragopavo._sexo != _sexo && dragopavo.getFecunda() != 0 && getFecunda() != 0
						&& dragopavo.getCelda() == celdaprueba) {
					if (dragopavo._reprod < 20 && _reprod < 20 && !dragopavo.esCastrado() && !esCastrado()) {
						int aparearce = Formulas.getRandomValor(2, 4);
						if (dragopavo._capacidades.contains(6) || _capacidades.contains(6))
							aparearce = 3;
						if (aparearce == 3) {
							if (dragopavo._sexo == 1) {
								dragopavo._fecundadaHace = 1;
								dragopavo._aumentarFecundo.start();
								dragopavo.setPareja(_id);
								resAmor(7500);
								resResistencia(7500);
							} else if (_sexo == 1) {
								_fecundadaHace = 1;
								_aumentarFecundo.start();
								_pareja = dragopavo.getID();
								dragopavo.resAmor(7500);
								dragopavo.resResistencia(7500);
							}
							accion = 4;
							break;
						}
					}
				}
			}
		}
		GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(mapa, "" + 0, 1, _id + "", "a" + Encriptador.celdaIDACodigo(_celdaID) + path);
		_celdaID = celda;
		_orientacion = Encriptador.getNumeroPorValorHash(direccion);
		int ID = _id;
		try {
			Thread.sleep(1250);
		} catch (Exception e) {}
		GestorSalida.ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(mapa, celdaprueba + ";4");
		try {
			Thread.sleep(500);
		} catch (Exception e) {}
		if (accion != 0)
			GestorSalida.ENVIAR_eUK_EMOTE_MAPA(mapa, ID, accion, "");
		return;
	}
	
	public int minutosParir() {
		if (_reprod == 0)
			return Bustemu.RATE_TIEMPO_PARIR;
		else if (_reprod < 5)
			return 2 * Bustemu.RATE_TIEMPO_PARIR;
		else if (_reprod < 11)
			return 3 * Bustemu.RATE_TIEMPO_PARIR;
		else if (_reprod <= 20)
			return 4 * Bustemu.RATE_TIEMPO_PARIR;
		return 1;
	}
}
