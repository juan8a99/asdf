
package variables;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import estaticos.Camino;
import estaticos.CentroInfo;
import estaticos.Formulas;

import variables.Mapa.Celda;
import variables.Pelea.Luchador;

public class Hechizo {
	private String _nombre;
	private int _ID;
	private int _spriteID;
	private String _spriteInfos;
	private Map<Integer, StatsHechizos> _statsHechizos = new TreeMap<Integer, StatsHechizos>();
	private ArrayList<Integer> _afectadosEstandar = new ArrayList<Integer>();
	public static class StatsHechizos {
		private int _hechizoID;// id hechizo
		private int _nivel;// nivel
		private int _costePA;// coste de PA
		private int _minAlc;// minimo alcance
		private int _maxAlc;// maximo alcance
		private int _porcGC;// probabilidad de golpe critico
		private int _porcFC;// probabilidad de fallo critico
		private boolean _esLanzLinea;// lanzar en linea
		private boolean _tieneLineaVuelo;// linea de vuelo
		private boolean _esCeldaVacia;// celda vacia
		private boolean _esModifAlc;// alcance modificalble
		private int _maxLanzPorTurno;// cantidad de veces por turno
		private int _maxLanzPorObjetivo;// cantidad de veces por objetivo
		private int _sigLanzamiento;// cantidad de turnos para volver a lanzar el hechizo
		private int _reqLevel;// nivel requerido
		private boolean _esFinTurnoSiFC;// si falla, es final del turno
		private ArrayList<EfectoHechizo> _efectos;// efectos
		private ArrayList<EfectoHechizo> _efectosGC;// efectos critico
		private String _afectados;// genera un estado, tipo portador
		private ArrayList<Integer> _estadosProhibidos = new ArrayList<Integer>(); // estados q no deben tener
		private ArrayList<Integer> _estadosNecesarios = new ArrayList<Integer>(); // estados q deben tener
		private Hechizo _hechizo;
		
		public StatsHechizos(int hechizoID, int nivel, int costePA, int minAlc, int maxAlc, int porcGC, int porcFC,
				boolean esLanzLinea, boolean tieneLineaVuelo, boolean esCeldaVacia, boolean esModifAlc, int maxLanzPorTurno,
				int maxLanzPorObjetivo, int sigLanzamiento, int reqLevel, boolean esFinTurnoSiFC, String efectos,
				String efectosGC, String afectados, String estadosProhibidos, String estadosNecesarios, Hechizo hechizo) {
			_hechizoID = hechizoID;// ID
			_nivel = nivel;// nivel
			_costePA = costePA;// coste de PA
			_minAlc = minAlc;// minimo alcance
			_maxAlc = maxAlc;// maximo alcance
			_porcGC = porcGC;// tasa de golpe critico
			_porcFC = porcFC;// tasa de fallo critico
			_esLanzLinea = esLanzLinea;// lanzado en linea
			_tieneLineaVuelo = tieneLineaVuelo;// linea de vuelo
			_esCeldaVacia = esCeldaVacia;// celda libre
			_esModifAlc = esModifAlc;// alcance modificable
			_maxLanzPorTurno = maxLanzPorTurno;// cantidad de veces por turno
			_maxLanzPorObjetivo = maxLanzPorObjetivo;// cantidad de veces por objetivo
			_sigLanzamiento = sigLanzamiento;// cantidad de turnos para volver a lanzar el hechizo
			_reqLevel = reqLevel;// nivel requerido
			_esFinTurnoSiFC = esFinTurnoSiFC;// si es fallo critico , final de turno
			_efectos = analizarEfectos(efectos);// efectos
			_efectosGC = analizarEfectos(efectosGC);// efectos criticos
			_afectados = afectados;// tipo portador de algun estado
			String[] estados = estadosProhibidos.split(";");
			for (String esta : estados) {
				esta = esta.trim();
				_estadosProhibidos.add(Integer.parseInt(esta));
			}
			String[] estadosN = estadosNecesarios.split(";");
			for (String esta : estadosN) {
				esta = esta.trim();
				_estadosNecesarios.add(Integer.parseInt(esta));
			}
			_hechizo = hechizo;
		}
		
		private ArrayList<EfectoHechizo> analizarEfectos(String e) {
			ArrayList<EfectoHechizo> efectos = new ArrayList<EfectoHechizo>();
			String[] splt = e.split("\\|");
			for (String a : splt) {
				try {
					if (e.equals("-1"))
						continue;
					int id = Integer.parseInt(a.split(";", 2)[0]);
					String args = a.split(";", 2)[1];
					efectos.add(new EfectoHechizo(id, args, _hechizoID, _nivel));
				} catch (Exception f) {
					f.printStackTrace();
					System.out.println(a);
					System.exit(1);
				}
			}
			return efectos;
		}
		
		public int getHechizoID() {
			return _hechizoID;
		}
		
		public Hechizo getHechizo() {
			return _hechizo;
		}
		
		public int getSpriteID() {
			return _hechizo.getSpriteID();
		}
		
		public String getSpriteInfos() {
			return _hechizo.getSpriteInfos();
		}
		
		public int getNivel() {
			return _nivel;
		}
		
		public ArrayList<Integer> getEstadosProhi() {
			return _estadosProhibidos;
		}
		
		public ArrayList<Integer> getEstadosNeces() {
			return _estadosNecesarios;
		}
		
		public int getCostePA() {
			return _costePA;
		}
		
		public int getMinAlc() {
			return _minAlc;
		}
		
		public int getMaxAlc() {
			return _maxAlc;
		}
		
		public int getPorcGC() {
			return _porcGC;
		}
		
		public int getPorcFC() {
			return _porcFC;
		}
		
		public boolean esLanzLinea() {
			return _esLanzLinea;
		}
		
		public boolean tieneLineaVuelo() {
			return _tieneLineaVuelo;
		}
		
		public boolean esCeldaVacia() {
			return _esCeldaVacia;
		}
		
		public boolean esModifAlc() {
			return _esModifAlc;
		}
		
		public int getMaxLanzPorTurno() {
			return _maxLanzPorTurno;
		}
		
		public int getMaxLanzPorJugador() {
			return _maxLanzPorObjetivo;
		}
		
		public int getSigLanzamiento() {
			return _sigLanzamiento;
		}
		
		public int getReqNivel() {
			return _reqLevel;
		}
		
		public boolean esFinTurnoSiFC() {
			return _esFinTurnoSiFC;
		}
		
		public ArrayList<EfectoHechizo> getEfectos() {
			return _efectos;
		}
		
		public ArrayList<EfectoHechizo> getEfectosGC() {
			return _efectosGC;
		}
		
		public String getAfectados() {
			return _afectados;
		}
		
		public void aplicaTrampaAPelea(Pelea pelea, Luchador lanzador, Celda celda, ArrayList<Celda> celdas, boolean esGC) {
			ArrayList<EfectoHechizo> efectosH;
			if (esGC)
				efectosH = _efectosGC;
			else
				efectosH = _efectos;
			int azar = Formulas.getRandomValor(0, 99);
			int suerte = 0;
			for (EfectoHechizo EH : efectosH) {
				if (EH.getSuerte() != 0 && EH.getSuerte() != 100)// as 100% lanzamiento
				{
					if (azar <= suerte || azar >= (EH.getSuerte() + suerte)) {
						suerte += EH.getSuerte();
						continue;
					}
					suerte += EH.getSuerte();
				}
				ArrayList<Luchador> ojetivos = EfectoHechizo.getAfectados(pelea, celdas, _hechizoID);
				EH.aplicarHechizoAPelea(pelea, lanzador, celda, ojetivos, celdas);
			}
		}
		
		public void aplicaHechizoAPelea(Pelea pelea, Luchador lanzador, Celda celda, boolean esGC) {
			ArrayList<EfectoHechizo> efectosH;
			if (esGC)
				efectosH = _efectosGC;
			else
				efectosH = _efectos;
			int suerte = 0;
			int num = 0;
			int azar = Formulas.getRandomValor(0, 99);
			for (EfectoHechizo EH : efectosH) {
				if (pelea.getEstado() >= CentroInfo.PELEA_ESTADO_FINALIZADO)
					return;
				if (EH.getSuerte() != 0 && EH.getSuerte() != 100) {
					if (azar <= suerte || azar > (EH.getSuerte() + suerte)) {
						suerte += EH.getSuerte();// formula desbugeada de ruleta
						num++;// sirve para desbugear la ruleta
						continue;
					}
					suerte += EH.getSuerte();
				}
				int tipoAlcance = num * 2;
				if (esGC) {
					tipoAlcance += _efectos.size() * 2;
				}
				ArrayList<Celda> celdas = Camino.getCeldasAfectadasEnElArea(pelea.getMapaCopia(), celda.getID(), lanzador
						.getCeldaPelea().getID(), _afectados, tipoAlcance, esGC);
				ArrayList<Celda> celdasFinales = new ArrayList<Celda>();
				int afectados = 0;
				if (_hechizo != null ? _hechizo.getAfectadosEstandar().size() > num : false)
					afectados = _hechizo.getAfectadosEstandar().get(num);
				for (Celda C : celdas) {
					if (C == null)
						continue;
					Luchador L = C.getPrimerLuchador();
					if (L == null)
						continue;
					// no toca a los aliados
					if ( ( (afectados & 1) == 1) && (L.getEquipoBin() == lanzador.getEquipoBin()))
						continue;
					// no toca al lanzador
					if ( ( ( (afectados >> 1) & 1) == 1) && (L.getID() == lanzador.getID()))
						continue;
					// no toca a los enemigos
					if ( ( ( (afectados >> 2) & 1) == 1) && (L.getEquipoBin() != lanzador.getEquipoBin()))
						continue;
					// no toca a los combatientes (solamente invocaciones)
					if ( ( ( (afectados >> 3) & 1) == 1) && (!L.esInvocacion()))
						continue;
					// No toca a las invocations
					if ( ( ( (afectados >> 4) & 1) == 1) && (L.esInvocacion()))
						continue;
					// no afecta al lanzador
					if ( ( ( (afectados >> 5) & 1) == 1) && (L.getID() != lanzador.getID()))
						continue;
					celdasFinales.add(C);
				}
				if ( ( (afectados >> 5) & 1) == 1)
					if (!celdasFinales.contains(lanzador.getCeldaPelea()))
						celdasFinales.add(lanzador.getCeldaPelea());
				if ( ( (afectados >> 6) & 1) == 1) {
					Luchador invocador = lanzador.getInvocador();
					if (invocador != null && !celdasFinales.contains(invocador.getCeldaPelea()))
						celdasFinales.add(invocador.getCeldaPelea());
				}
				ArrayList<Luchador> objetivos = EfectoHechizo.getAfectados(pelea, celdasFinales, _hechizoID);
				EH.aplicarHechizoAPelea(pelea, lanzador, celda, objetivos, celdasFinales);
				num++;
			}
		}
	}
	
	public Hechizo(int aHechizoID, String aNombre, int aSpriteID, String aSpriteInfos, String afectados) {
		_ID = aHechizoID;
		_nombre = aNombre;
		_spriteID = aSpriteID;
		_spriteInfos = aSpriteInfos;
		String afectadosEstandar = afectados.split(":")[0];
		String afectadosPorGC = "";
		if (afectados.split(":").length > 1)
			afectadosPorGC = afectados.split(":")[1];
		for (String num : afectadosEstandar.split(";")) {
			try {
				_afectadosEstandar.add(Integer.parseInt(num));
			} catch (Exception e) {
				_afectadosEstandar.add(0);
				continue;
			}
		}
		for (String num : afectadosPorGC.split(";")) {
			try {
				_afectadosEstandar.add(Integer.parseInt(num));
			} catch (Exception e) {
				_afectadosEstandar.add(0);
				continue;
			}
		}
	}
	
	public ArrayList<Integer> getAfectadosEstandar() {
		return _afectadosEstandar;
	}
	
	public void setAfectadosEstandar(String afectados) {
		_afectadosEstandar.clear();
		String afectadosEstandar = afectados.split(":")[0];
		String afectadosPorGC = "";
		if (afectados.split(":").length > 1)
			afectadosPorGC = afectados.split(":")[1];
		for (String num : afectadosEstandar.split(";")) {
			try {
				_afectadosEstandar.add(Integer.parseInt(num));
			} catch (Exception e) {
				_afectadosEstandar.add(0);
				continue;
			}
		}
		for (String num : afectadosPorGC.split(";")) {
			try {
				_afectadosEstandar.add(Integer.parseInt(num));
			} catch (Exception e) {
				_afectadosEstandar.add(0);
				continue;
			}
		}
	}
	
	public int getSpriteID() {
		return _spriteID;
	}
	
	public String getSpriteInfos() {
		return _spriteInfos;
	}
	
	public String getNombre() {
		return _nombre;
	}
	
	public int getID() {
		return _ID;
	}
	
	public StatsHechizos getStatsPorNivel(int lvl) {
		return _statsHechizos.get(lvl);
	}
	
	public void addStatsHechizos(Integer lvl, StatsHechizos stats) {
		if (_statsHechizos.get(lvl) != null)
			return;
		_statsHechizos.put(lvl, stats);
	}
}
