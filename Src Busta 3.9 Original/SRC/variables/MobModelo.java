
package variables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Map.Entry;

import java.util.Timer;

import variables.Hechizo.StatsHechizos;
import variables.Mapa.Celda;
import variables.Pelea.Luchador;
import variables.Personaje.Stats;
import estaticos.*;
import estaticos.MundoDofus.*;

public class MobModelo {
	private int _ID;
	private int _gfxID;
	private int _alineacion;
	private String _colores;
	private int _tipoIA = 0;
	private int _minKamas;
	private int _maxKamas;
	private Map<Integer, MobGrado> _grados = new TreeMap<Integer, MobGrado>();
	private ArrayList<Drop> _drops = new ArrayList<Drop>();
	private boolean _esCapturable;
	private int _talla;
	private String _nombre;
	public static class GrupoMobs {
		private int _id;
		private int _celdaID;
		private int _orientacion = 3;
		private int _alin = -1;
		private int _distanciaAgresion = 0;
		private int _estrellas = 0;
		private boolean _esFixeado = false;
		private Map<Integer, MobGrado> _mobs = new TreeMap<Integer, MobGrado>();
		private Map<Integer, Integer> _almas = new TreeMap<Integer, Integer>();
		private String condicion = "";
		private Timer _tiempoCondicion;
		
		public GrupoMobs(int Aid, int Alineacion, ArrayList<MobGrado> posiblesMobs, Mapa Mapa, int celda, int maxMobs) {
			_id = Aid;
			_alin = Alineacion;
			int rand = 0;
			int nroMobs = 0;
			switch (maxMobs) {
				case 0:
					return;
				case 1:
					nroMobs = 1;
					break;
				case 2:
					nroMobs = Formulas.getRandomValor(1, 2);
					break;
				case 3:
					nroMobs = Formulas.getRandomValor(1, 3);
					break;
				case 4:
					rand = Formulas.getRandomValor(0, 99);
					if (rand < 22)
						nroMobs = 1;
					else if (rand < 48)
						nroMobs = 2;
					else if (rand < 74)
						nroMobs = 3;
					else
						nroMobs = 4;
					break;
				case 5:
					rand = Formulas.getRandomValor(0, 99);
					if (rand < 15)
						nroMobs = 1;
					else if (rand < 35)
						nroMobs = 2;
					else if (rand < 60)
						nroMobs = 3;
					else if (rand < 85)
						nroMobs = 4;
					else
						nroMobs = 5;
					break;
				case 6:
					rand = Formulas.getRandomValor(0, 99);
					if (rand < 10)
						nroMobs = 1;
					else if (rand < 25)
						nroMobs = 2;
					else if (rand < 45)
						nroMobs = 3;
					else if (rand < 65)
						nroMobs = 4;
					else if (rand < 85)
						nroMobs = 5;
					else
						nroMobs = 6;
					break;
				case 7:
					rand = Formulas.getRandomValor(0, 99);
					if (rand < 9)
						nroMobs = 1;
					else if (rand < 20)
						nroMobs = 2;
					else if (rand < 35)
						nroMobs = 3;
					else if (rand < 55)
						nroMobs = 4;
					else if (rand < 75)
						nroMobs = 5;
					else if (rand < 91)
						nroMobs = 6;
					else
						nroMobs = 7;
					break;
				default:
					rand = Formulas.getRandomValor(0, 99);
					if (rand < 9)
						nroMobs = 1;
					else if (rand < 20)
						nroMobs = 2;
					else if (rand < 33)
						nroMobs = 3;
					else if (rand < 50)
						nroMobs = 4;
					else if (rand < 67)
						nroMobs = 5;
					else if (rand < 80)
						nroMobs = 6;
					else if (rand < 91)
						nroMobs = 7;
					else
						nroMobs = 8;
					break;
			}
			boolean tieneMismaAlineacion = false;
			for (MobGrado mob : posiblesMobs) {
				if (mob.getModelo().getAlineacion() == _alin)
					tieneMismaAlineacion = true;
			}
			if (!tieneMismaAlineacion)
				return;
			int idMob = -1;
			int maxNivel = 0;
			for (int a = 0; a < nroMobs; a++) {
				MobGrado mob = null;
				do {
					int random = Formulas.getRandomValor(0, posiblesMobs.size() - 1);
					mob = posiblesMobs.get(random).getCopy();
				} while (mob.getModelo().getAlineacion() != _alin);
				if (mob.getNivel() > maxNivel)
					maxNivel = mob.getNivel();
				int idModeloMob = mob.getModelo().getID();
				if (_almas.containsKey(idModeloMob)) {
					int valor = _almas.get(idModeloMob);
					_almas.remove(idModeloMob);
					_almas.put(idModeloMob, valor + 1);
				} else {
					_almas.put(idModeloMob, 1);
				}
				_mobs.put(idMob, mob);
				idMob--;
			}
			_distanciaAgresion = CentroInfo.agresionPorNivel(maxNivel);
			if (_alin != CentroInfo.ALINEACION_NEUTRAL)
				_distanciaAgresion = 15;
			_celdaID = (celda == -1 ? Mapa.getRandomCeldaIDLibre() : celda);
			if (_celdaID == 0)
				return;
			_orientacion = Formulas.getRandomValor(0, 3) * 2;
			_esFixeado = false;
			_estrellas = 0;
		}
		
		public GrupoMobs(int idGrupoMob, int celdaID, String grupo) {
			int maxNivel = 0;
			_id = idGrupoMob;
			_alin = -1;
			_celdaID = celdaID;
			_distanciaAgresion = CentroInfo.agresionPorNivel(maxNivel);
			_esFixeado = true;
			int idMob = -1;
			for (String data : grupo.split(";")) {
				String[] infos = data.split(",");
				try {
					int idMobModelo = Integer.parseInt(infos[0]);
					int min = Integer.parseInt(infos[1]);
					int max = Integer.parseInt(infos[2]);
					MobModelo m = MundoDofus.getMobModelo(idMobModelo);
					List<MobGrado> mgs = new ArrayList<MobGrado>();
					for (MobGrado MG : m.getGrados().values())
						if (MG._nivel >= min && MG._nivel <= max)
							mgs.add(MG);
					if (mgs.isEmpty())
						continue;
					if (_almas.containsKey(idMobModelo)) {
						int valor = _almas.get(idMobModelo);
						_almas.remove(idMobModelo);
						_almas.put(idMobModelo, valor + 1);
					} else {
						_almas.put(idMobModelo, 1);
					}
					_mobs.put(idMob, mgs.get(Formulas.getRandomValor(0, mgs.size() - 1)));
					idMob--;
				} catch (Exception e) {
					continue;
				}
			}
			_orientacion = (Formulas.getRandomValor(0, 3) * 2) + 1;// 3 kralamar
		}
		
		public int getID() {
			return _id;
		}
		
		public int getEstrellas() {
			return _estrellas;
		}
		
		public void setEstrellas(int estre) {
			_estrellas = estre;
		}
		
		public void aumentarEstrellas() {
			if (_estrellas == 0) {
				_estrellas = 1;
				return;
			}
			_estrellas += 25;
			if (_estrellas > 1025)
				_estrellas = 0;
		}
		
		public void aumentarEstrellasCant(int cant) {
			_estrellas += cant;
			if (_estrellas > 1025)
				_estrellas = 0;
		}
		
		public int getCeldaID() {
			return _celdaID;
		}
		
		public int getOrientacion() {
			return _orientacion;
		}
		
		public int getDistanciaAgresion() {
			return _distanciaAgresion;
		}
		
		public boolean esFixeado() {
			return _esFixeado;
		}
		
		public void setOrientacion(int o) {
			_orientacion = 0;
		}
		
		public void setCeldaID(int id) {
			_celdaID = id;
		}
		
		public int getAlineamiento() {
			return _alin;
		}
		
		public MobGrado getMobGradoPorID(int id) {
			return _mobs.get(id);
		}
		
		public int getTamaño() {
			return _mobs.size();
		}
		
		public String enviarGM() {
			String mobIDs = "";
			String mobGFX = "";
			String mobNiveles = "";
			String colores = "";
			boolean primero = true;
			if (_mobs.isEmpty())
				return "";
			for (Entry<Integer, MobGrado> entry : _mobs.entrySet()) {
				if (!primero) {
					mobIDs += ",";
					mobGFX += ",";
					mobNiveles += ",";
				}
				MobModelo mob = entry.getValue().getModelo();
				mobIDs += mob.getID();
				mobGFX += mob.getGfxID() + "^100";
				mobNiveles += entry.getValue().getNivel();
				colores += mob.getColores() + ";0,0,0,0;";
				primero = false;
			}
			return "+" + _celdaID + ";" + _orientacion + ";" + _estrellas + ";" + _id + ";" + mobIDs + ";-3;" + mobGFX + ";"
					+ mobNiveles + ";" + colores;
		}
		
		public Map<Integer, MobGrado> getMobs() {
			return _mobs;
		}
		
		public Map<Integer, Integer> getAlmasMobs() {
			return _almas;
		}
		
		public void setCondicion(String cond) {
			condicion = cond;
		}
		
		public String getCondicion() {
			return condicion;
		}
		
		public void setEsFixeado(boolean fix) {
			_esFixeado = fix;
		}
		
		public void inicioTiempoCondicion() {
			_tiempoCondicion = new Timer();
			_tiempoCondicion.schedule(new TimerTask() {
				public void run() {
					condicion = "";
				}
			}, Bustemu.TIEMPO_ARENA);
		}
		
		public void stopConditionTimer() {
			try {
				_tiempoCondicion.cancel();
			} catch (Exception e) {}
		}
	}
	public static class MobGrado {
		private MobModelo _modelo;
		private int _grado;
		private int _nivel;
		private int _PDV;
		private int _idEnPelea;
		private int _PDVMAX;
		private int _iniciativa;
		private int _PA;
		private int _PM;
		private Celda celdaPelea;
		private int _baseXp = 10;
		private Map<Integer, Integer> _stats = new TreeMap<Integer, Integer>();
		private Map<Integer, StatsHechizos> _hechizos = new TreeMap<Integer, StatsHechizos>();
		
		public MobGrado(MobModelo modelo, int grado, int nivel, int PA, int PM, String Aresist, String Astats, String Aspells,
				int pdvMax, int iniciativa, int xp) {
			_modelo = modelo;
			_grado = grado;
			_nivel = nivel;
			_PDVMAX = pdvMax;
			_PDV = _PDVMAX;
			_PA = PA;
			_PM = PM;
			_baseXp = xp;
			_iniciativa = iniciativa;
			String[] resistencias = Aresist.split(";");
			String[] statsArray = Astats.split(",");
			int RNeutral = 0, RFuego = 0, RAgua = 0, RAire = 0, RTierra = 0, EPA = 0, EPM = 0, fuerza = 0, inteligencia = 0, sabiduria = 0, suerte = 0, agilidad = 0;
			try {
				RNeutral = Integer.parseInt(resistencias[0]);
				RTierra = Integer.parseInt(resistencias[1]);
				RFuego = Integer.parseInt(resistencias[2]);
				RAgua = Integer.parseInt(resistencias[3]);
				RAire = Integer.parseInt(resistencias[4]);
				EPA = Integer.parseInt(resistencias[5]);
				EPM = Integer.parseInt(resistencias[6]);
				fuerza = Integer.parseInt(statsArray[0]);
				sabiduria = Integer.parseInt(statsArray[1]);
				inteligencia = Integer.parseInt(statsArray[2]);
				suerte = Integer.parseInt(statsArray[3]);
				agilidad = Integer.parseInt(statsArray[4]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			_stats.clear();
			_stats.put(118, fuerza);
			_stats.put(124, sabiduria);
			_stats.put(126, inteligencia);
			_stats.put(123, suerte);
			_stats.put(119, agilidad);
			_stats.put(CentroInfo.STATS_ADD_ResPorc_NEUTRAL, RNeutral);
			_stats.put(CentroInfo.STATS_ADD_ResPorc_FUEGO, RFuego);
			_stats.put(CentroInfo.STATS_ADD_ResPorc_AGUA, RAgua);
			_stats.put(CentroInfo.STATS_ADD_ResPorc_AIRE, RAire);
			_stats.put(CentroInfo.STATS_ADD_ResPorc_TIERRA, RTierra);
			_stats.put(CentroInfo.STATS_ADD_ProbPerdida_PA, EPA);
			_stats.put(CentroInfo.STATS_ADD_ProbPerdida_PM, EPM);
			_stats.put(111, PA);
			_stats.put(128, PM);
			if (_modelo.getID() == 423) {
				_stats.put(CentroInfo.STATS_ADD_CRIATURAS_INVO, 4);
				_stats.put(105, 100);
			} else
				_stats.put(CentroInfo.STATS_ADD_CRIATURAS_INVO, 2);
			_hechizos.clear();
			String[] spellsArray = Aspells.split(";");
			for (String str : spellsArray) {
				if (str.equals(""))
					continue;
				String[] spellInfo = str.split("@");
				int hechizoID = 0;
				int hechizoNivel = 0;
				try {
					hechizoID = Integer.parseInt(spellInfo[0]);
					hechizoNivel = Integer.parseInt(spellInfo[1]);
				} catch (Exception e) {
					continue;
				}
				if (hechizoID == 0 || hechizoNivel == 0)
					continue;
				Hechizo hechizo = MundoDofus.getHechizo(hechizoID);
				if (hechizo == null)
					continue;
				StatsHechizos hechizoStats = hechizo.getStatsPorNivel(hechizoNivel);
				if (hechizoStats == null)
					continue;
				_hechizos.put(hechizoID, hechizoStats);
			}
		}
		
		private MobGrado(MobModelo modelo, int grado, int nivel, int pdv, int pdvmax, int PA, int PM,
				Map<Integer, Integer> stats, Map<Integer, StatsHechizos> hechizos, int iniciativa, int xp) {
			_modelo = modelo;
			_grado = grado;
			_nivel = nivel;
			_PDV = pdv;
			_PDVMAX = pdvmax;
			_PA = PA;
			_PM = PM;
			_iniciativa = iniciativa;
			_stats = stats;
			_hechizos = hechizos;
			_idEnPelea = -1;
			_baseXp = xp;
		}
		
		public int getBaseXp() {
			return _baseXp;
		}
		
		public int getIniciativa() {
			return _iniciativa;
		}
		
		public MobGrado getCopy() {
			Map<Integer, Integer> nuevoStats = new TreeMap<Integer, Integer>();
			nuevoStats.putAll(_stats);
			return new MobGrado(_modelo, _grado, _nivel, _PDV, _PDVMAX, _PA, _PM, nuevoStats, _hechizos, _iniciativa, _baseXp);
		}
		
		public Stats getStats() {
			return new Stats(_stats);
		}
		
		public int getNivel() {
			return _nivel;
		}
		
		public Celda getCeldaPelea() {
			return celdaPelea;
		}
		
		public void setCeldaPelea(Celda celda) {
			celdaPelea = celda;
		}
		
		public Map<Integer, StatsHechizos> getHechizos() {
			return _hechizos;
		}
		
		public MobModelo getModelo() {
			return _modelo;
		}
		
		public int getPDV() {
			return _PDV;
		}
		
		public void setPDV(int pdv) {
			_PDV = pdv;
		}
		
		public int getPDVMAX() {
			return _PDVMAX;
		}
		
		public int getGrado() {
			return _grado;
		}
		
		public void setIdEnPelea(int i) {
			_idEnPelea = i;
		}
		
		public int getIdEnPelea() {
			return _idEnPelea;
		}
		
		public int getPA() {
			return _PA;
		}
		
		public int getPM() {
			return _PM;
		}
		
		public void modificarStatsPorInvocador(Luchador invocador) {
			float coef = 0;
			if (invocador.getPersonaje() != null) {
				coef = invocador.getNivel() / 200F;
				if (invocador.getPersonaje().getClase(true) == 2)
					coef += 0.5f;
			} else
				coef = invocador.getNivel() / 100F;
			_PDV = (int) (_PDVMAX * coef) + (invocador.getTotalStatsSinBuff().getEfecto(125) / 5);
			_PDVMAX = _PDV;
			int fuerza = (int) (_stats.get(118) * coef) + (invocador.getTotalStatsSinBuff().getEfecto(118) / 3);
			int inteligencia = (int) (_stats.get(126) * coef) + (invocador.getTotalStatsSinBuff().getEfecto(126) / 3);
			int agilidad = (int) (_stats.get(119) * coef) + (invocador.getTotalStatsSinBuff().getEfecto(119) / 3);
			int sabiduria = (int) (_stats.get(124) * coef) + (invocador.getTotalStatsSinBuff().getEfecto(124) / 3);
			int suerte = (int) (_stats.get(123) * coef) + (invocador.getTotalStatsSinBuff().getEfecto(123) / 3);
			_stats.remove(118);
			_stats.remove(126);
			_stats.remove(119);
			_stats.remove(124);
			_stats.remove(123);
			_stats.put(118, fuerza);
			_stats.put(126, inteligencia);
			_stats.put(119, agilidad);
			_stats.put(124, sabiduria);
			_stats.put(123, suerte);
		}
	}
	
	public MobModelo(int id, String nombre, int gfx, int alineacion, String colores, String grados, String hechizos,
			String stats, String pdvs, String puntos, String init, int mK, int MK, String xpstr, int tipoIA, boolean capturable,
			int talla) {
		_ID = id;
		_gfxID = gfx;
		_alineacion = alineacion;
		_colores = colores;
		_minKamas = mK;
		_maxKamas = MK;
		_tipoIA = tipoIA;
		_esCapturable = capturable;
		_talla = talla;
		_nombre = nombre;
		int idGrado = 1;
		for (int n = 0; n < 11; n++) {
			try {
				String grado = grados.split("\\|")[n];
				String[] infos = grado.split("@");
				int nivel = Integer.parseInt(infos[0]);
				String resistencias = infos[1];
				String aStats = stats.split("\\|")[n];
				String aHechizos = hechizos.split("\\|")[n];
				if (aHechizos.equals("-1"))
					aHechizos = "";
				int pdvMax = 1;
				int aInit = 1;
				try {
					pdvMax = Integer.parseInt(pdvs.split("\\|")[n]);
					aInit = Integer.parseInt(init.split("\\|")[n]);
				} catch (Exception e) {}
				int PA = 3;
				int PM = 3;
				int xp = 10;
				try {
					String[] pts = puntos.split("\\|")[n].split(";");
					try {
						PA = Integer.parseInt(pts[0]);
					} catch (Exception e1) {}
					try {
						PM = Integer.parseInt(pts[1]);
					} catch (Exception e1) {}
					try {
						xp = Integer.parseInt(xpstr.split("\\|")[n]);
					} catch (Exception e1) {}
				} catch (Exception e) {}
				_grados.put(idGrado, new MobGrado(this, idGrado, nivel, PA, PM, resistencias, aStats, aHechizos, pdvMax, aInit,
						xp));
				idGrado++;
			} catch (Exception e) {
				continue;
			}
		}
	}
	
	public int getID() {
		return _ID;
	}
	
	public void addDrop(Drop D) {
		_drops.add(D);
	}
	
	public int getTalla() {
		return _talla;
	}
	
	public ArrayList<Drop> getDrops() {
		return _drops;
	}
	
	public int getGfxID() {
		return _gfxID;
	}
	
	public int getMinKamas() {
		return _minKamas;
	}
	
	public int getMaxKamas() {
		return _maxKamas;
	}
	
	public int getAlineacion() {
		return _alineacion;
	}
	
	public String getColores() {
		return _colores;
	}
	
	public int getTipoInteligencia() {
		return _tipoIA;
	}
	
	public void setTipoInteligencia(int IA) {
		_tipoIA = IA;
	}
	
	public Map<Integer, MobGrado> getGrados() {
		return _grados;
	}
	
	public MobGrado getGradoPorNivel(int nivel) {
		for (Entry<Integer, MobGrado> grado : _grados.entrySet()) {
			if (grado.getValue().getNivel() == nivel)
				return grado.getValue();
		}
		return null;
	}
	
	public MobGrado getRandomGrado() {
		int randomgrade = (int) (Math.random() * (6 - 1)) + 1;
		int graderandom = 1;
		for (Entry<Integer, MobGrado> grade : _grados.entrySet()) {
			if (graderandom == randomgrade) {
				return grade.getValue();
			} else {
				graderandom++;
			}
		}
		return null;
	}
	
	public boolean esCapturable() {
		return _esCapturable;
	}
	
	public String getNombre() {
		return _nombre;
	}
}
