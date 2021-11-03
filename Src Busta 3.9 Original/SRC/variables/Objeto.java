
package variables;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import variables.Personaje.Stats;

import estaticos.Bustemu;
import estaticos.CentroInfo;
import estaticos.Formulas;

import estaticos.MundoDofus;

public class Objeto {
	public static class ObjetoModelo {
		private int _idModelo;
		private String _statsModelo;
		private String _nombre;
		private int _tipo;
		private int _nivel;
		private int _peso;
		private int _precio;
		private int _setID;
		private int _precioVIP;
		private String _condiciones;
		private int _costePA, _alcanceMinimo, _alcanceMax, _porcentajeGC, _porcentajeFC, _bonusGC;
		private boolean _esDosManos;
		private ArrayList<Accion> _accionesDeUso = new ArrayList<Accion>();
		private long _vendidos;
		private int _precioMedio;
		
		public ObjetoModelo(int id, String statModeloDB, String nombre, int tipo, int nivel, int peso, int precio, int setObjeto,
				String condiciones, String infoArma, int vendidos, int precioMedio, int precioVIP) {
			_idModelo = id;
			_statsModelo = statModeloDB;
			_nombre = nombre;
			_tipo = tipo;
			_nivel = nivel;
			_peso = peso;
			_precio = precio;
			_setID = setObjeto;
			_condiciones = condiciones;
			_costePA = -1;
			_alcanceMinimo = 1;
			_alcanceMax = 1;
			_porcentajeGC = 100;
			_porcentajeFC = 2;
			_bonusGC = 0;
			_vendidos = vendidos;
			_precioMedio = precioMedio;
			_precioVIP = precioVIP;
			try {
				String[] infos = infoArma.split(";");
				_costePA = Integer.parseInt(infos[0]);
				_alcanceMinimo = Integer.parseInt(infos[1]);
				_alcanceMax = Integer.parseInt(infos[2]);
				_porcentajeGC = Integer.parseInt(infos[3]);
				_porcentajeFC = Integer.parseInt(infos[4]);
				_bonusGC = Integer.parseInt(infos[5]);
				_esDosManos = infos[6].equals("1");
			} catch (Exception e) {}
		}
		
		public void addAccion(Accion A) {
			_accionesDeUso.add(A);
		}
		
		public boolean esDosManos() {
			return _esDosManos;
		}
		
		public int getBonusGC() {
			return _bonusGC;
		}
		
		public int getPrecioVIP() {
			return _precioVIP;
		}
		
		public int getAlcMinimo() {
			return _alcanceMinimo;
		}
		
		public int getAlcanceMax() {
			return _alcanceMax;
		}
		
		public int getPorcGC() {
			return _porcentajeGC;
		}
		
		public int getPorcFC() {
			return _porcentajeFC;
		}
		
		public int getCostePA() {
			return _costePA;
		}
		
		public int getID() {
			return _idModelo;
		}
		
		public String getStringStatsObj() {
			return _statsModelo;
		}
		
		public String getNombre() {
			return _nombre;
		}
		
		public int getTipo() {
			return _tipo;
		}
		
		public int getNivel() {
			return _nivel;
		}
		
		public int getPeso() {
			return _peso;
		}
		
		public int getPrecio() {
			return _precio;
		}
		
		public int getSetID() {
			return _setID;
		}
		
		public String getCondiciones() {
			return _condiciones;
		}
		
		public Objeto crearObjDesdeModelo(int cantidad, boolean maxStats) {
			Objeto objeto;
			if (cantidad < 1)
				cantidad = 1;
			if (_tipo == 18)
				objeto = new Objeto(0, _idModelo, cantidad, -1, "320#0#0#a,326#0#0#0", 0);
			else if (_tipo == 33 || _tipo == 42 || _tipo == 49 || _tipo == 69 || _tipo == 12)
				objeto = new Objeto(0, _idModelo, cantidad, -1, _statsModelo, 0);
			else if (_tipo == 85)
				objeto = new PiedraDeAlma(0, cantidad, _idModelo, -1, _statsModelo);
			else if (Bustemu.ARMAS_ENCARNACIONES.contains(_idModelo))
				objeto = new Objeto(0, _idModelo, cantidad, -1, _statsModelo, 0);
			else
				objeto = new Objeto(0, _idModelo, cantidad, -1, generarStatsModeloDB(_statsModelo, maxStats),
						generarEfectoModelo(_statsModelo), 0);
			return objeto;
		}
		
		public Objeto crearObjPosDesdeModelo(int cantidad, int pos, boolean maxStats) {// solo para caramelos
			Objeto objeto = new Objeto(0, _idModelo, cantidad, pos, generarStatsModeloDB(_statsModelo, maxStats),
					generarEfectoModelo(_statsModelo), 0);
			return objeto;
		}
		
		public static String generarStatsModeloDB(String statsModelo, boolean maxStats) {
			String statsObjeto = "";
			if (statsModelo.equals("") || statsModelo == null)
				return statsObjeto;
			String[] splitted = statsModelo.split(",");
			boolean primero = false;
			for (String s : splitted) {
				String[] stats = s.split("#");
				int statID = Integer.parseInt(stats[0], 16);
				for (int a : CentroInfo.ID_EFECTOS_ARMAS)
					if (a == statID)
						continue;
				if (primero)
					statsObjeto += ",";
				String rango = "";
				int valor = 1;
				if (statID == 811 || statID == 800) {
					statsObjeto += stats[0] + "#0#0#" + stats[3];
					primero = true;
					continue;
				}
				try {
					rango = stats[4];
					if (maxStats) {
						try {
							int min = Integer.parseInt(stats[1], 16);
							int max = Integer.parseInt(stats[2], 16);
							valor = min;
							if (max != 0)
								valor = max;
						} catch (Exception e) {
							valor = Formulas.getRandomValor(rango);
						}
					} else {
						valor = Formulas.getRandomValor(rango);
					}
				} catch (Exception e) {}
				statsObjeto += stats[0] + "#" + Integer.toHexString(valor) + "#0#"
						+ Integer.toHexString(Integer.parseInt(stats[3], 16)) + "#0d0+" + valor;
				primero = true;
			}
			return statsObjeto;
		}
		
		private ArrayList<EfectoHechizo> generarEfectoModelo(String statsModelo) {
			ArrayList<EfectoHechizo> efectos = new ArrayList<EfectoHechizo>();
			if (statsModelo.equals("") || statsModelo == null)
				return efectos;
			String[] splitted = statsModelo.split(",");
			for (String s : splitted) {
				String[] stats = s.split("#");
				int statID = Integer.parseInt(stats[0], 16);
				for (int a : CentroInfo.ID_EFECTOS_ARMAS) {
					if (a == statID) {
						int id = statID;
						String min = stats[1];
						String max = stats[2];
						String jet = stats[4];
						String args = min + ";" + max + ";-1;-1;0;" + jet;
						efectos.add(new EfectoHechizo(id, args, 0, -1));
					}
				}
			}
			return efectos;
		}
		
		public String stringDeStatsParaTienda() {
			String str = "";
			str += _idModelo + ";";
			str += _statsModelo;
			return str;
		}
		
		public void aplicarAccion(Personaje pj, Personaje objetivo, int objID, short celda) {
			for (Accion a : _accionesDeUso)
				a.aplicar(pj, objetivo, objID, celda);
		}
		
		public int getPrecioPromedio() {
			return _precioMedio;
		}
		
		public long getVendidos() {
			return _vendidos;
		}
		
		public synchronized void nuevoPrecio(int cantidad, int precio) {
			long viejaVenta = _vendidos;
			_vendidos += cantidad;
			_precioMedio = (int) ( (_precioMedio * viejaVenta + precio) / _vendidos);
		}
	}
	protected ObjetoModelo _modelo;
	protected int _cantidad = 1;
	protected int _posicion = -1;
	protected int _id;
	private Stats _stats = new Stats();
	private ArrayList<EfectoHechizo> _efectos = new ArrayList<EfectoHechizo>();
	private Map<Integer, String> _textoStats = new TreeMap<Integer, String>();
	private ArrayList<String> _hechizoStats = new ArrayList<String>();
	protected int _objevivoID;
	protected int _idObjModelo;
	
	public Objeto() {
		_cantidad = 1;
		_posicion = -1;
		_stats = new Stats();
		_efectos = new ArrayList<EfectoHechizo>();
		_textoStats = new TreeMap<Integer, String>();
		_hechizoStats = new ArrayList<String>();
	}
	
	public Objeto(int id, int modeloBD, int cant, int pos, String strStats, int idObjevi) {
		_id = id;
		_modelo = MundoDofus.getObjModelo(modeloBD);
		_cantidad = cant;
		_posicion = pos;
		_stats = new Stats();
		_textoStats = new TreeMap<Integer, String>();
		_hechizoStats = new ArrayList<String>();
		_objevivoID = idObjevi;
		_idObjModelo = modeloBD;
		convertirStringAStats(strStats);
	}
	
	public Objeto(int id, int modeloBD, int cant, int pos, String strStats, ArrayList<EfectoHechizo> efectos, int idObjevi) {
		_id = id;
		_modelo = MundoDofus.getObjModelo(modeloBD);
		_cantidad = cant;
		_posicion = pos;
		_stats = new Stats();
		_textoStats = new TreeMap<Integer, String>();
		_hechizoStats = new ArrayList<String>();
		_efectos = efectos;
		_objevivoID = idObjevi;
		_idObjModelo = modeloBD;
		convertirStringAStats(strStats);
	}
	
	public int getObjeviID() {
		return _objevivoID;
	}
	
	public void setObjeviID(int id) {
		_objevivoID = id;
	}
	
	public int getIDModelo() {
		return _idObjModelo;
	}
	
	public void setIDModelo(int idModelo) {
		_idObjModelo = idModelo;
		_modelo = MundoDofus.getObjModelo(idModelo);
	}
	
	public void convertirStringAStats(String strStats) {
		_stats = new Stats();
		_textoStats = new TreeMap<Integer, String>();
		_hechizoStats = new ArrayList<String>();
		_efectos = new ArrayList<EfectoHechizo>();
		String[] split = strStats.split(",");
		for (String s : split) {
			try {
				String hechizo = s;
				String[] stats = s.split("#");
				int statID = Integer.parseInt(stats[0], 16);
				if (statID == 998 || statID == 997 || statID == 996 || statID == 994 || statID == 989 || statID == 988
						|| statID == 987 || statID == 986 || statID == 985 || statID == 983) {
					_textoStats.put(statID, stats[4]);
					continue;
				}
				if (statID == 800 || statID == 811 || statID == 961 || statID == 962 || statID == 960 || statID == 950
						|| statID == 951) {
					_textoStats.put(statID, stats[3]);
					continue;
				}
				if (statID >= 281 && statID <= 294) {
					_hechizoStats.add(hechizo);
					continue;
				}
				String jet = stats[4];
				boolean siguiente = true;
				for (int a : CentroInfo.ID_EFECTOS_ARMAS) {
					if (a == statID) {
						int id = statID;
						String min = stats[1];
						String max = stats[2];
						String args = min + ";" + max + ";-1;-1;0;" + jet;
						_efectos.add(new EfectoHechizo(id, args, 0, -1));
						siguiente = false;
					}
				}
				if (!siguiente)
					continue;
				int valor = Integer.parseInt(stats[1], 16);
				_stats.addUnStat(statID, valor);
			} catch (Exception e) {
				continue;
			}
		}
		if (Bustemu.ARMAS_ENCARNACIONES.contains(_idObjModelo)) {
			Encarnacion encarnacion = MundoDofus.getEncarnacion(_id);
			if (encarnacion == null) {
				_stats.addUnStat(118, 1);
				_stats.addUnStat(119, 1);
				_stats.addUnStat(123, 1);
				_stats.addUnStat(126, 1);
			} else {
				int valor = encarnacion.getNivel();
				_stats.addUnStat(118, valor);
				_stats.addUnStat(119, valor);
				_stats.addUnStat(123, valor);
				_stats.addUnStat(126, valor);
			}
		}
	}
	
	public String convertirStatsAString() {
		int tipoModelo = _modelo.getTipo();
		int idSetModelo = _modelo.getSetID();
		if (tipoModelo == 83)
			return _modelo.getStringStatsObj();
		if (Bustemu.ARMAS_ENCARNACIONES.contains(_idObjModelo)) {
			return _modelo._statsModelo;
		}
		if ( (tipoModelo == 18 || tipoModelo == 90) && MundoDofus.getIdTodasMascotas().contains(_id)) {
			Mascota mascota = MundoDofus.getMascota(_id);
			if (tipoModelo == 18)
				return mascota.analizarStatsMascota();
			else
				return mascota.analizarStatsFantasma();
		}
		if (_stats.getStatsComoMap().isEmpty() && _hechizoStats.isEmpty() && _efectos.isEmpty() && _textoStats.isEmpty())
			return "";
		if (tipoModelo == 113 && (_objevivoID == 0)) {
			int tipo = 0;
			if (_idObjModelo == 9233)
				tipo = 17;
			else if (_idObjModelo == 9234)
				tipo = 16;
			else if (_idObjModelo == 9255)
				tipo = 1;
			else if (_idObjModelo == 9256)
				tipo = 9;
			return "3cc#0#0#" + Integer.toHexString(1) + "," + "3cb#0#0#1," + "3cd#0#0#" + Integer.toHexString(tipo) + ","
					+ "3ca#0#0#0," + "3ce#0#0#0";
		}
		if (tipoModelo == 113 && (_objevivoID != 0)) {
			Objevivo objevi = MundoDofus.getObjevivos(_objevivoID);
			if (objevi != null)
				return objevi.convertirAString();
			else
				_objevivoID = 0;
		}
		String stats = "";
		boolean primero = false;
		if ( (idSetModelo >= 81 && idSetModelo <= 92) || (idSetModelo >= 201 && idSetModelo <= 212)) {
			for (String hechizo : _hechizoStats) {
				if (primero)
					stats += ",";
				String[] hechi = hechizo.split("#");
				int idhechizo = Integer.parseInt(hechi[1], 16);
				String cantidad = "";
				try {
					cantidad = hechi[3].split("\\+")[1];
				} catch (ArrayIndexOutOfBoundsException e) {
					cantidad = hechi[3].split("\\+")[0];
				}
				stats += hechi[0] + "#" + hechi[1] + "#0#" + cantidad + "#0d0+" + idhechizo;
				primero = true;
			}
		}
		for (EfectoHechizo EH : _efectos) {
			if (primero)
				stats += ",";
			String[] infos = EH.getArgs().split(";");
			try {
				stats += Integer.toHexString(EH.getEfectoID()) + "#" + infos[0] + "#" + infos[1] + "#0#" + infos[5];
			} catch (Exception e) {
				continue;
			}
			primero = true;
		}
		for (Entry<Integer, Integer> entry : _stats.getStatsComoMap().entrySet()) {
			int statID = entry.getKey();
			if (statID == 998 || statID == 997 || statID == 996 || statID == 994 || statID == 988 || statID == 987
					|| statID == 986 || statID == 985 || statID == 983 || statID == 960 || statID == 961 || statID == 962
					|| statID == 963 || statID == 964)
				continue;
			if (primero)
				stats += ",";
			String jet = "0d0+" + entry.getValue();
			stats += Integer.toHexString(statID) + "#" + Integer.toHexString(entry.getValue()) + "#0#0#" + jet;
			primero = true;
		}
		for (Entry<Integer, String> entry : _textoStats.entrySet()) {
			int statID = entry.getKey();
			if (primero)
				stats += ",";
			if (statID == 800 || statID == 811 || statID == 961 || statID == 962 || statID == 960 || statID == 950
					|| statID == 951) {
				stats += Integer.toHexString(statID) + "#0#0#" + entry.getValue();
			} else {
				stats += Integer.toHexString(statID) + "#0#0#0#" + entry.getValue();
			}
			primero = true;
		}
		if (_objevivoID != 0) {
			Objevivo objevi = MundoDofus.getObjevivos(_objevivoID);
			if (objevi == null)
				_objevivoID = 0;
			else {
				if (primero)
					stats += ",";
				stats += objevi.convertirAString();
				primero = true;
			}
		}
		return stats;
	}
	
	public void addTextoStat(int i, String s) {
		_textoStats.put(i, s);
	}
	
	public String getNombreMision() {
		for (Entry<Integer, String> entry : _textoStats.entrySet()) {
			if (Integer.toHexString(entry.getKey()).compareTo("3dd") == 0)
				return entry.getValue();
		}
		return null;
	}
	
	public Stats getStats() {
		return _stats;
	}
	
	public int getCantidad() {
		return _cantidad;
	}
	
	public void setCantidad(int cantidad) {
		_cantidad = cantidad;
	}
	
	public int getPosicion() {
		return _posicion;
	}
	
	public void setPosicion(int posicion) {
		_posicion = posicion;
	}
	
	public ObjetoModelo getModelo() {
		return _modelo;
	}
	
	public int getID() {
		return _id;
	}
	
	public void setID(int id) {
		_id = id;
	}
	
	public String stringObjetoConGuiño() {
		String str = Integer.toHexString(_id) + "~" + Integer.toHexString(_idObjModelo) + "~" + Integer.toHexString(_cantidad)
				+ "~" + (_posicion == -1 ? "" : Integer.toHexString(_posicion)) + "~" + convertirStatsAString();
		if (Bustemu.ARMAS_ENCARNACIONES.contains(_idObjModelo)) {
			Encarnacion encarnacion = MundoDofus.getEncarnacion(_id);
			if (encarnacion == null) {
				str += ",76#1#0#0#0d0+1,77#1#0#0#0d0+1,7b#1#0#0#0d0+1,7e#1#0#0#0d0+1,29d#0#0#1";
			} else {
				String a = "#" + Integer.toHexString(encarnacion.getNivel()) + "#0#0#0d0+" + encarnacion.getNivel();
				str += ",76" + a + ",77" + a + ",7b" + a + ",7e" + a + ",29d#0#0#" + Integer.toHexString(encarnacion.getNivel());
			}
		}
		return str + ";";
	}
	
	public String stringObjetoConPalo(int cantidad) {// sirve para enviar la info a la pantalla
		String str = _id + "|" + cantidad + "|" + _idObjModelo + "|" + convertirStatsAString();
		if (Bustemu.ARMAS_ENCARNACIONES.contains(_idObjModelo)) {
			Encarnacion encarnacion = MundoDofus.getEncarnacion(_id);
			if (encarnacion == null) {
				str += ",76#1#0#0#0d0+1,77#1#0#0#0d0+1,7b#1#0#0#0d0+1,7e#1#0#0#0d0+1,29d#0#0#1";
			} else {
				String a = "#" + Integer.toHexString(encarnacion.getNivel()) + "#0#0#0d0+" + encarnacion.getNivel();
				str += ",76" + a + ",77" + a + ",7b" + a + ",7e" + a + ",29d#0#0#" + Integer.toHexString(encarnacion.getNivel());
			}
		}
		return str;
	}
	
	public String convertirStatsAStringFM(String statsstr, Objeto obj, int agregar, boolean negativo) {
		String stats = "";
		boolean primero = false;
		for (EfectoHechizo SE : obj._efectos) {
			if (primero)
				stats += ",";
			String[] infos = SE.getArgs().split(";");
			try {
				stats += Integer.toHexString(SE.getEfectoID()) + "#" + infos[0] + "#" + infos[1] + "#0#" + infos[5];
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			primero = true;
		}
		for (Entry<Integer, Integer> entry : obj._stats.getStatsComoMap().entrySet()) {
			int statID = entry.getKey();
			if (statID == 998 || statID == 997 || statID == 996 || statID == 994 || statID == 988 || statID == 987
					|| statID == 986 || statID == 985 || statID == 983 || statID == 960 || statID == 961 || statID == 962
					|| statID == 963 || statID == 964)
				continue;
			if (primero)
				stats += ",";
			if (Integer.toHexString(statID).compareTo(statsstr) == 0) {
				int newstats = 0;
				if (negativo) {
					newstats = entry.getValue() - agregar;
					if (newstats < 1)
						continue;
				} else {
					newstats = entry.getValue() + agregar;
				}
				String jet = "0d0+" + newstats;
				stats += Integer.toHexString(statID) + "#" + Integer.toHexString(entry.getValue() + agregar) + "#0#0#" + jet;
			} else {
				String jet = "0d0+" + entry.getValue();
				stats += Integer.toHexString(statID) + "#" + Integer.toHexString(entry.getValue()) + "#0#0#" + jet;
			}
			primero = true;
		}
		for (Entry<Integer, String> entry : obj._textoStats.entrySet()) {
			int statID = entry.getKey();
			if (primero)
				stats += ",";
			if (statID == 800 || statID == 811 || statID == 961 || statID == 962 || statID == 960 || statID == 950
					|| statID == 951) {
				stats += Integer.toHexString(statID) + "#0#0#" + entry.getValue();
			} else {
				stats += Integer.toHexString(statID) + "#0#0#0#" + entry.getValue();
			}
		}
		return stats;
	}
	
	public String stringStatsFCForja(Objeto obj, double runa) {
		String stats = "";
		boolean primero = false;
		for (EfectoHechizo EH : obj._efectos) {
			if (primero)
				stats += ",";
			String[] infos = EH.getArgs().split(";");
			try {
				stats += Integer.toHexString(EH.getEfectoID()) + "#" + infos[0] + "#" + infos[1] + "#0#" + infos[5];
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			primero = true;
		}
		for (Entry<Integer, Integer> entry : obj._stats.getStatsComoMap().entrySet()) {
			int nuevosStats = 0;
			int statID = entry.getKey();
			int valor = entry.getValue();
			if (statID == 152 || statID == 154 || statID == 155 || statID == 157 || statID == 116 || statID == 153) {
				float a = (float) ( (valor * runa) / 100);
				if (a < 1)
					a = 1;
				float chute = (float) (valor + a);
				nuevosStats = (int) Math.floor(chute);
				if (nuevosStats > Oficio.getStatBaseMax(obj._modelo, Integer.toHexString(statID))) {
					nuevosStats = Oficio.getStatBaseMax(obj._modelo, Integer.toHexString(statID));
				}
			} else {
				if (statID == 127 || statID == 101)
					continue;
				float chute = (float) (valor - ( (valor * runa) / 100));
				nuevosStats = (int) Math.floor(chute);
			}
			if (nuevosStats < 1)
				continue;
			String jet = "0d0+" + nuevosStats;
			if (primero)
				stats += ",";
			stats += Integer.toHexString(statID) + "#" + Integer.toHexString(nuevosStats) + "#0#0#" + jet;
			primero = true;
		}
		for (Entry<Integer, String> entry : obj._textoStats.entrySet()) {
			if (primero)
				stats += ",";
			stats += Integer.toHexString(entry.getKey()) + "#0#0#0#" + entry.getValue();
			primero = true;
		}
		return stats;
	}
	
	public ArrayList<EfectoHechizo> getEfectos() {
		return _efectos;
	}
	
	public ArrayList<EfectoHechizo> getEfectosCriticos() {
		ArrayList<EfectoHechizo> efectos = new ArrayList<EfectoHechizo>();
		for (EfectoHechizo SE : _efectos) {
			try {
				boolean boost = false;
				if (SE.getEfectoID() == 101)
					boost = true;
				String[] infos = SE.getArgs().split(";");
				if (boost) {
					efectos.add(SE);
					continue;
				}
				int min = Integer.parseInt(infos[0], 16);
				int max = Integer.parseInt(infos[1], 16);
				if (min > max)
					max = min;
				String jet = "0d0+" + (max + _modelo.getBonusGC());
				String newArgs = infos[0] + ";" + infos[1] + ";0;-1;0;" + jet;
				efectos.add(new EfectoHechizo(SE.getEfectoID(), newArgs, 0, -1));
			} catch (Exception e) {
				continue;
			}
		}
		return efectos;
	}
	
	public synchronized static Objeto clonarObjeto(Objeto obj, int cantidad) {
		Objeto objeto;
		if (cantidad < 1)
			cantidad = 1;
		int tipo = obj.getModelo().getTipo();
		if (obj.getModelo().getTipo() == 18)
			objeto = new Objeto(0, obj.getIDModelo(), cantidad, -1, "320#0#0#a,326#0#0#0", 0);
		else if (tipo == 33 || tipo == 42 || tipo == 49 || tipo == 69 || tipo == 12)
			objeto = new Objeto(0, obj._idObjModelo, cantidad, -1, obj._modelo._statsModelo, 0);
		else if (tipo == 85)
			objeto = new PiedraDeAlma(0, cantidad, obj._idObjModelo, -1, obj._modelo._statsModelo);
		else if (Bustemu.ARMAS_ENCARNACIONES.contains(obj._idObjModelo))
			objeto = new Objeto(0, obj._idObjModelo, cantidad, -1, obj._modelo._statsModelo, 0);
		else
			objeto = new Objeto(0, obj._idObjModelo, cantidad, -1, obj.convertirStatsAString(), obj.getEfectos(), 0);
		return objeto;
	}
	
	public static String getRunas(Objeto Obj) {
		String runas = "";
		String statsModelo = Obj.convertirStatsAString();
		boolean esPrimero = true;
		String[] Splitted = statsModelo.split(",");
		int random = Formulas.getRandomValor(0, 3);
		int cantidad = 4 - random;
		for (String s : Splitted) {
			String[] Stats = s.split("#");
			if (Stats[0].isEmpty())
				continue;
			int statID = Integer.parseInt(Stats[0], 16);
			int numero = 0;
			try {
				numero = Integer.parseInt(Stats[4].replaceAll("0d0\\+", ""));
			} catch (Exception e) {
				continue;
			}
			if (numero <= 0)
				continue;
			if (random < CentroInfo.getValorStatRuna(statID))
				continue;
			boolean siguiente = true;
			for (int a : CentroInfo.ID_EFECTOS_ARMAS)
				if (a == statID)
					siguiente = false;
			if (!siguiente)
				continue;
			if (!esPrimero)
				runas += ";";
			runas += CentroInfo.statDeRunas(statID, numero) + "," + cantidad;
			esPrimero = false;
		}
		return runas;
	}
	
	public void clearTodo() {
		_stats = new Stats();
		_efectos.clear();
		_textoStats.clear();
		_hechizoStats.clear();
	}
	
	public void clearStats() {
		_stats = new Stats();
		_textoStats.clear();
		_hechizoStats.clear();
	}
}
