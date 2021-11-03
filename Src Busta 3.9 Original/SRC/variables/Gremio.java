
package variables;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.joda.time.LocalDate;
import org.joda.time.Days;

import variables.Hechizo.StatsHechizos;
import variables.Mapa.Cercado;
import variables.Personaje.Stats;

import estaticos.CentroInfo;
import estaticos.GestorSQL;
import estaticos.MundoDofus;

public class Gremio {
	private int _id;
	private String _nombre = "";
	private String _emblema = "";
	private Map<Integer, MiembroGremio> _miembros = new TreeMap<Integer, MiembroGremio>();
	private int _nvl;
	private long _xp;
	private int _capital = 0;
	private int _nroRecaudadores = 0;
	private Map<Integer, StatsHechizos> _hechizos = new TreeMap<Integer, StatsHechizos>();
	private Map<Integer, Integer> _stats = new TreeMap<Integer, Integer>();
	private Map<Integer, Integer> _statsPelea = new TreeMap<Integer, Integer>();
	public static class MiembroGremio {
		private int _id;
		private Gremio _gremio;
		private String _nombre;
		private int _nivel;
		private int _gfx;
		private int _rango = 0;
		private byte _porcXpDonada = 0;
		private long _xpDonada = 0;
		private int _derechos = 0;
		private String _ultConeccion;
		private Map<Integer, Boolean> tieneDerecho = new TreeMap<Integer, Boolean>();
		
		public MiembroGremio(int id, Gremio gremio, String nombre, int nivel, int gfx, int r, long x, byte pXp, int derechos,
				String ultConeccion) {
			_id = id;
			_gremio = gremio;
			_nombre = nombre;
			_nivel = nivel;
			_gfx = gfx;
			_rango = r;
			_xpDonada = x;
			_porcXpDonada = pXp;
			_derechos = derechos;
			_ultConeccion = ultConeccion;
			convertirDerechosAInt(_derechos);
		}
		
		public int getGfx() {
			return _gfx;
		}
		
		public int getNivel() {
			return _nivel;
		}
		
		public String getNombre() {
			return _nombre;
		}
		
		public String getVerdaderoNombre() {
			Personaje perso = MundoDofus.getPersonaje(_id);
			if (perso == null)
				return _nombre;
			_nombre = perso.getNombre();
			return _nombre;
		}
		
		public int getID() {
			return _id;
		}
		
		public int getRango() {
			return _rango;
		}
		
		public Gremio getGremio() {
			return _gremio;
		}
		
		public String analizarDerechos() {
			return Integer.toString(_derechos, 36);
		}
		
		public int getDerechos() {
			return _derechos;
		}
		
		public long getXpDonada() {
			return _xpDonada;
		}
		
		public int getPorcXpDonada() {
			return _porcXpDonada;
		}
		
		public String getUltimaConeccino() {
			return _ultConeccion;
		}
		
		public int getHorasDeUltimaConeccion() {
			String[] strFecha = _ultConeccion.toString().split("~");
			LocalDate ultConeccion = new LocalDate(Integer.parseInt(strFecha[0]), Integer.parseInt(strFecha[1]),
					Integer.parseInt(strFecha[2]));
			LocalDate ahora = new LocalDate();
			return Days.daysBetween(ultConeccion, ahora).getDays() * 24;
		}
		
		public Personaje getPerso() {
			return MundoDofus.getPersonaje(_id);
		}
		
		public boolean puede(int derecho) {
			if (this._derechos == 1)
				return true;
			return tieneDerecho.get(derecho);
		}
		
		public void setRango(int i) {
			_rango = i;
		}
		
		public void setTodosDerechos(int rank, byte xp, int right) {
			if (rank == -1)
				rank = this._rango;
			if (xp < 0)
				xp = this._porcXpDonada;
			if (xp > 90)
				xp = 90;
			if (right == -1)
				right = this._derechos;
			this._rango = rank;
			this._porcXpDonada = xp;
			if (right != this._derechos && right != 1)
				convertirDerechosAInt(right);
			this._derechos = right;
			GestorSQL.ACTUALIZAR_MIEMBRO_GREMIO(this);
		}
		
		public void setNivel(int nivel) {
			this._nivel = nivel;
		}
		
		public void darXpAGremio(long xp) {
			this._xpDonada += xp;
			this._gremio.addXp(xp);
		}
		
		public void derechosIniciales() {
			tieneDerecho.put(CentroInfo.G_MODIFBOOST, false);
			tieneDerecho.put(CentroInfo.G_MODIFDERECHOS, false);
			tieneDerecho.put(CentroInfo.G_INVITAR, false);
			tieneDerecho.put(CentroInfo.G_BANEAR, false);
			tieneDerecho.put(CentroInfo.G_TODASXPDONADAS, false);
			tieneDerecho.put(CentroInfo.G_SUXPDONADA, false);
			tieneDerecho.put(CentroInfo.G_MODRANGOS, false);
			tieneDerecho.put(CentroInfo.G_PONERRECAUDADOR, false);
			tieneDerecho.put(CentroInfo.G_RECOLECTARRECAUDADOR, false);
			tieneDerecho.put(CentroInfo.G_USARCERCADOS, false);
			tieneDerecho.put(CentroInfo.G_MEJORARCERCADOS, false);
			tieneDerecho.put(CentroInfo.G_OTRASMONTURAS, false);
		}
		
		public void convertirDerechosAInt(int total) {
			if (tieneDerecho.isEmpty()) {
				derechosIniciales();
			}
			if (total == 1)
				return;
			if (tieneDerecho.size() > 0)
				tieneDerecho.clear();
			derechosIniciales();
			Integer[] mapKey = tieneDerecho.keySet().toArray(new Integer[tieneDerecho.size()]);
			while (total > 0) {
				for (int i = tieneDerecho.size() - 1; i < tieneDerecho.size(); i--) {
					if (mapKey[i].intValue() <= total) {
						total ^= mapKey[i].intValue();
						tieneDerecho.put(mapKey[i], true);
						break;
					}
				}
			}
		}
		
		public void setUltConeccion(String ultConeccion) {
			_ultConeccion = ultConeccion;
		}
	}
	
	public Gremio(Personaje dueño, String nombre, String emblema) {
		_id = MundoDofus.getSigIdGremio();
		_nombre = nombre;
		_emblema = emblema;
		_nvl = 1;
		_xp = 0;
		decompilarHechizos("462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0|");
		decompilarStats("176;100|158;1000|124;100|");
	}
	
	public Gremio(int id, String nombre, String emblema, int nivel, long xp, int capital, int nroMaxRecau, String hechizos,
			String stats) {
		_id = id;
		_nombre = nombre;
		_emblema = emblema;
		_xp = xp;
		_nvl = nivel;
		_capital = capital;
		_nroRecaudadores = nroMaxRecau;
		decompilarHechizos(hechizos);
		decompilarStats(stats);
		_statsPelea.clear();
		_statsPelea.put(CentroInfo.STATS_ADD_PA, 7);
		_statsPelea.put(CentroInfo.STATS_ADD_PM, 4);
		_statsPelea.put(CentroInfo.STATS_ADD_FUERZA, _nvl * 9);
		_statsPelea.put(CentroInfo.STATS_ADD_SABIDURIA, getStats(CentroInfo.STATS_ADD_SABIDURIA));
		_statsPelea.put(CentroInfo.STATS_ADD_INTELIGENCIA, _nvl * 9);
		_statsPelea.put(CentroInfo.STATS_ADD_SUERTE, _nvl * 9);
		_statsPelea.put(CentroInfo.STATS_ADD_AGILIDAD, _nvl * 9);
		_statsPelea.put(CentroInfo.STATS_ADD_ResPorc_NEUTRAL, (int) Math.floor(getNivel() / 3));
		_statsPelea.put(CentroInfo.STATS_ADD_ResPorc_FUEGO, (int) Math.floor(getNivel() / 3));
		_statsPelea.put(CentroInfo.STATS_ADD_ResPorc_AGUA, (int) Math.floor(getNivel() / 3));
		_statsPelea.put(CentroInfo.STATS_ADD_ResPorc_AIRE, (int) Math.floor(getNivel() / 3));
		_statsPelea.put(CentroInfo.STATS_ADD_ResPorc_TIERRA, (int) Math.floor(getNivel() / 3));
		_statsPelea.put(CentroInfo.STATS_ADD_ProbPerdida_PA, (int) Math.floor(getNivel() / 3));
		_statsPelea.put(CentroInfo.STATS_ADD_ProbPerdida_PM, (int) Math.floor(getNivel() / 3));
	}
	
	public MiembroGremio addMiembro(int id, String nombre, int nivel, int gfx, int r, byte pXp, long x, int derechos,
			String ultConeccion) {
		MiembroGremio GM = new MiembroGremio(id, this, nombre, nivel, gfx, r, x, pXp, derechos, ultConeccion);
		_miembros.put(id, GM);
		return GM;
	}
	
	public MiembroGremio addNuevoMiembro(Personaje perso) {
		MiembroGremio GM = new MiembroGremio(perso.getID(), this, perso.getNombre(), perso.getNivel(), perso.getGfxID(), 0, 0,
				(byte) 0, 0, perso.getCuenta().getUltimaConeccion());
		_miembros.put(perso.getID(), GM);
		return GM;
	}
	
	public int getID() {
		return _id;
	}
	
	public int getNroRecau() {
		return _nroRecaudadores;
	}
	
	public String getInfoGremio() {
		String str = _nombre + "," + getStats(158) + "," + getStats(176) + "," + getStats(124) + "," + getRecauColocados();
		return str;
	}
	
	public String getInfo() {
		String str = _nombre;
		return str;
	}
	
	public void setNroRecau(int nro) {
		_nroRecaudadores = nro;
	}
	
	public int getRecauColocados() {
		int numero = 0;
		for (Recaudador perco : MundoDofus.getTodosRecaudadores().values()) {
			if (perco.getGremioID() == _id)
				numero++;
		}
		return numero;
	}
	
	public int getCapital() {
		return _capital;
	}
	
	public void setCapital(int nro) {
		_capital = nro;
	}
	
	public Map<Integer, StatsHechizos> getHechizos() {
		return _hechizos;
	}
	
	public Map<Integer, Integer> getStats() {
		return _stats;
	}
	
	public void addStat(int stat, int cant) {
		int vieja = _stats.get(stat);
		_stats.put(stat, vieja + cant);
	}
	
	public void boostHechizo(int ID) {
		StatsHechizos SS = _hechizos.get(ID);
		if (SS != null && SS.getNivel() == 5)
			return;
		_hechizos.put(ID, ( (SS == null) ? MundoDofus.getHechizo(ID).getStatsPorNivel(1) : MundoDofus.getHechizo(ID)
				.getStatsPorNivel(SS.getNivel() + 1)));
	}
	
	public Stats getStatsPelea() {
		return new Stats(_statsPelea);
	}
	
	public String getNombre() {
		return _nombre;
	}
	
	public String getEmblema() {
		return _emblema;
	}
	
	public long getXP() {
		return _xp;
	}
	
	public int getNivel() {
		return _nvl;
	}
	
	public int getSize() {
		return _miembros.size();
	}
	
	public String analizarMiembrosGM() {
		String str = "";
		for (MiembroGremio GM : _miembros.values()) {
			String enLinea = "0";
			if (GM.getPerso() == null)
				continue;
			if (GM.getPerso().enLinea())
				enLinea = "1";
			if (str.length() != 0)
				str += "|";
			str += GM.getID() + ";";
			str += GM.getNombre() + ";";
			str += GM.getNivel() + ";";
			str += GM.getGfx() + ";";
			str += GM.getRango() + ";";
			str += GM.getXpDonada() + ";";
			str += GM.getPorcXpDonada() + ";";
			str += GM.getDerechos() + ";";
			str += enLinea + ";";
			str += GM.getPerso().getAlineacion() + ";";
			str += GM.getHorasDeUltimaConeccion();
		}
		return str;
	}
	
	public ArrayList<Personaje> getPjMiembros() {
		ArrayList<Personaje> a = new ArrayList<Personaje>();
		for (MiembroGremio MG : _miembros.values())
			a.add(MG.getPerso());
		return a;
	}
	
	public ArrayList<MiembroGremio> getTodosMiembros() {
		ArrayList<MiembroGremio> a = new ArrayList<MiembroGremio>();
		for (MiembroGremio MG : _miembros.values())
			a.add(MG);
		return a;
	}
	
	public MiembroGremio getMiembro(int idMiembro) {
		return _miembros.get(idMiembro);
	}
	
	public void expulsarMiembro(Personaje perso) {
		Casa h = Casa.getCasaDePj(perso);
		if (h != null) {
			if (Casa.casaGremio(_id) > 0) {
				GestorSQL.ACTUALIZAR_CASA_GREMIO(h, 0, 0);
			}
		}
		_miembros.remove(perso.getID());
		GestorSQL.BORRAR_MIEMBRO_GREMIO(perso.getID());
	}
	
	public void addXp(long xp) {
		this._xp += xp;
		while (_xp >= MundoDofus.getXPMaxGremio(_nvl) && _nvl < 200)
			subirNivel();
	}
	
	public void subirNivel() {
		this._nvl++;
		this._capital = this._capital + 5;
	}
	
	public String analizarInfoCercados() {
		byte enclosMax = (byte) Math.floor(_nvl / 10);
		String packet = "" + enclosMax;
		for (Cercado cercados : MundoDofus.todosCercados()) {
			if (cercados.getGremio() == this) {
				packet += "|" + cercados.getMapa().getID() + ";" + cercados.getTamaño() + ";" + cercados.getCantObjMax();
				if (cercados.getListaCriando().size() > 0) {
					packet += ";";
					boolean primero = false;
					for (Integer monturas : cercados.getListaCriando()) {
						Dragopavo montura = MundoDofus.getDragopavoPorID(monturas);
						if (montura == null)
							continue;
						if (primero)
							packet += ",";
						primero = true;
						if (MundoDofus.getPersonaje(montura.getDueño()) == null) {
							packet += montura.getColor() + "," + montura.getNombre() + ",SIN DUEÑO";
						} else
							packet += montura.getColor() + "," + montura.getNombre() + ","
									+ MundoDofus.getPersonaje(montura.getDueño()).getNombre();
					}
				}
			}
		}
		return packet;
	}
	
	public void decompilarHechizos(String strHechizo) {
		int id;
		int nivel;
		for (String split : strHechizo.split("\\|")) {
			id = Integer.parseInt(split.split(";")[0]);
			nivel = Integer.parseInt(split.split(";")[1]);
			_hechizos.put(id, MundoDofus.getHechizo(id).getStatsPorNivel(nivel));
		}
	}
	
	public void decompilarStats(String statsStr) {
		int id;
		int value;
		for (String split : statsStr.split("\\|")) {
			id = Integer.parseInt(split.split(";")[0]);
			value = Integer.parseInt(split.split(";")[1]);
			_stats.put(id, value);
		}
	}
	
	public String compilarHechizo() {
		String str = "";
		boolean primero = true;
		for (Entry<Integer, StatsHechizos> statHechizo : _hechizos.entrySet()) {
			if (!primero)
				str += "|";
			str += statHechizo.getKey() + ";" + ( (statHechizo.getValue() == null) ? 0 : statHechizo.getValue().getNivel());
			primero = false;
		}
		return str;
	}
	
	public String compilarStats() {
		String str = "";
		boolean primero = true;
		for (Entry<Integer, Integer> stats : _stats.entrySet()) {
			if (!primero)
				str += "|";
			str += stats.getKey() + ";" + stats.getValue();
			primero = false;
		}
		return str;
	}
	
	public void actualizarStats(int statsID, int add) {
		int actual = _stats.get(statsID).intValue();
		_stats.put(statsID, (actual + add));
	}
	
	public int getStats(int statsID) {
		int valor = 0;
		for (Entry<Integer, Integer> tempStats : _stats.entrySet()) {
			if (tempStats.getKey() == statsID) {
				valor = tempStats.getValue();
			}
		}
		return valor;
	}
	
	public String analizarRecauAGrmio() {
		String packet = getNroRecau() + "|" + MundoDofus.cantRecauDelGremio(getID()) + "|" + 100 * getNivel() + "|" + getNivel()
				+ "|" + getStats(158) + "|" + getStats(176) + "|" + getStats(124) + "|" + getNroRecau() + "|" + getCapital()
				+ "|" + (1000 + (10 * getNivel())) + "|" + compilarHechizo();
		return packet;
	}
}
