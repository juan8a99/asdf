
package variables;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import variables.Hechizo.StatsHechizos;
import variables.Pelea.Luchador;
import variables.Personaje.Stats;
import estaticos.CentroInfo;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import estaticos.MundoDofus.Area;
import estaticos.MundoDofus.SubArea;

public class Prisma {
	private int _id;
	private int _alineacion;
	private int _nivel;
	private short _mapa;
	private int _celda;
	private int _dir;
	private int _nombre;
	private int _gfx;
	private int _estadoPelea;
	private int _peleaID;
	private int _tiempoTurno = 45000;
	private int _honor = 0;
	private int _area = -1;
	private Pelea _pelea;
	private Map<Integer, Integer> _stats = new TreeMap<Integer, Integer>();
	private Map<Integer, StatsHechizos> _hechizos = new TreeMap<Integer, StatsHechizos>();
	
	public Prisma(int id, int alineacion, int lvl, short mapa, int celda, int honor, int area) {
		_id = id;
		_alineacion = alineacion;
		_nivel = lvl;
		_mapa = mapa;
		_celda = celda;
		_dir = 1;
		if (alineacion == 1) {
			_nombre = 1111;
			_gfx = 8101;
		} else {
			_nombre = 1112;
			_gfx = 8100;
		}
		_estadoPelea = -1;
		_peleaID = -1;
		_honor = honor;
		_area = area;
		_pelea = null;
	}
	
	public int getID() {
		return _id;
	}
	
	public int getAreaConquistada() {
		return _area;
	}
	
	public void setAreaConquistada(int area) {
		_area = area;
	}
	
	public int getAlineacion() {
		return _alineacion;
	}
	
	public int getNivel() {
		return _nivel;
	}
	
	public short getMapa() {
		return _mapa;
	}
	
	public int getCelda() {
		return _celda;
	}
	
	public Stats getStats() {
		return new Stats(_stats);
	}
	
	public Map<Integer, StatsHechizos> getHechizos() {
		return _hechizos;
	}
	
	public void actualizarStats() {
		int fuerza = 1000 + (500 * _nivel);
		int inteligencia = 1000 + (500 * _nivel);
		int agilidad = 1000 + (500 * _nivel);
		int sabiduria = 1000 + (500 * _nivel);
		int suerte = 1000 + (500 * _nivel);
		int resistencia = 9 * _nivel;
		_stats.clear();
		_stats.put(CentroInfo.STATS_ADD_FUERZA, fuerza);
		_stats.put(CentroInfo.STATS_ADD_INTELIGENCIA, inteligencia);
		_stats.put(CentroInfo.STATS_ADD_AGILIDAD, agilidad);
		_stats.put(CentroInfo.STATS_ADD_SABIDURIA, sabiduria);
		_stats.put(CentroInfo.STATS_ADD_SUERTE, suerte);
		_stats.put(CentroInfo.STATS_ADD_ResPorc_NEUTRAL, resistencia);
		_stats.put(CentroInfo.STATS_ADD_ResPorc_FUEGO, resistencia);
		_stats.put(CentroInfo.STATS_ADD_ResPorc_AGUA, resistencia);
		_stats.put(CentroInfo.STATS_ADD_ResPorc_AIRE, resistencia);
		_stats.put(CentroInfo.STATS_ADD_ResPorc_TIERRA, resistencia);
		_stats.put(CentroInfo.STATS_ADD_ProbPerdida_PA, resistencia);
		_stats.put(CentroInfo.STATS_ADD_ProbPerdida_PM, resistencia);
		_stats.put(CentroInfo.STATS_ADD_PA, 12);
		_stats.put(CentroInfo.STATS_ADD_PM, 0);
		_hechizos.clear();
		String hechizos = "56@6;24@6;157@6;63@6;8@6;81@6";
		String[] spellsArray = hechizos.split(";");
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
	
	public void setNivel(int nivel) {
		_nivel = nivel;
	}
	
	public int getEstadoPelea() {
		return _estadoPelea;
	}
	
	public void setEstadoPelea(int pelea) {
		_estadoPelea = pelea;
	}
	
	public int getPeleaID() {
		return _peleaID;
	}
	
	public void setPeleaID(int pelea) {
		_peleaID = pelea;
	}
	
	public void setPelea(Pelea pelea) {
		_pelea = pelea;
	}
	
	public Pelea getPelea() {
		return _pelea;
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
	
	public int getX() {
		Mapa mapa = MundoDofus.getMapa(_mapa);
		return mapa.getX();
	}
	
	public int getY() {
		Mapa mapa = MundoDofus.getMapa(_mapa);
		return mapa.getY();
	}
	
	public SubArea getSubArea() {
		Mapa mapa = MundoDofus.getMapa(_mapa);
		return mapa.getSubArea();
	}
	
	public Area getArea() {
		Mapa mapa = MundoDofus.getMapa(_mapa);
		return mapa.getSubArea().getArea();
	}
	
	public int getAlinSubArea() {
		Mapa mapa = MundoDofus.getMapa(_mapa);
		return mapa.getSubArea().getAlineacion();
	}
	
	public int getAlinArea() {
		Mapa mapa = MundoDofus.getMapa(_mapa);
		return mapa.getSubArea().getAlineacion();
	}
	
	public int getHonor() {
		return _honor;
	}
	
	public void addHonor(int honor) {
		_honor += honor;
		if (_honor >= 25000) {
			_nivel = 10;
			_honor = 25000;
		}
		for (int n = 1; n <= 10; n++) {
			if (_honor < MundoDofus.getExpNivel(n)._pvp) {
				_nivel = n - 1;
				break;
			}
		}
	}
	
	public void setCelda(int celda) {
		_celda = celda;
	}
	
	public String getGMPrisma() {
		if (_estadoPelea != -1)
			return "";
		String str = "GM|+";
		str += _celda + ";";
		str += _dir + ";0;" + _id + ";" + _nombre + ";-10;" + _gfx + "^100;" + _nivel + ";" + _nivel + ";" + _alineacion;
		return str;
	}
	
	public static void analizarAtaque(Personaje perso) {
		for (Prisma prisma : MundoDofus.TodosPrismas()) {
			if ( (prisma._estadoPelea == 0 || prisma._estadoPelea == -2) && perso.getAlineacion() == prisma.getAlineacion()) {
				GestorSalida.ENVIAR_Cp_INFO_ATACANTES_PRISMA(perso, atacantesDePrisma(prisma._id, prisma._mapa, prisma._peleaID));
			}
		}
	}
	
	public static void analizarDefensa(Personaje perso) {
		for (Prisma prisma : MundoDofus.TodosPrismas()) {
			if ( (prisma._estadoPelea == 0 || prisma._estadoPelea == -2) && perso.getAlineacion() == prisma.getAlineacion()) {
				GestorSalida.ENVIAR_CP_INFO_DEFENSORES_PRISMA(perso,
						defensoresDePrisma(prisma._id, prisma._mapa, prisma._peleaID));
			}
		}
	}
	
	public static String atacantesDePrisma(int id, short mapaId, int peleaId) {
		String str = "+";
		str += Integer.toString(id, 36);
		for (Entry<Integer, Pelea> pelea : MundoDofus.getMapa(mapaId).getPeleas().entrySet()) {
			if (pelea.getValue().getID() == peleaId) {
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
	
	public static String defensoresDePrisma(int id, short mapaId, int peleaId) {
		String str = "+";
		String stra = "";
		str += Integer.toString(id, 36);
		for (Entry<Integer, Pelea> pelea : MundoDofus.getMapa(mapaId).getPeleas().entrySet()) {
			if (pelea.getValue().getID() == peleaId) {
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
					if (pelea.getValue().luchadoresDeEquipo(2).size() > 7)
						str += "1;";
					else
						str += "0;";
				}
				stra = str.substring(1);
				stra = "-" + stra;
				pelea.getValue().setListaDefensores(stra);
			}
		}
		return str;
	}
}
