
package variables;

import java.util.Map;
import java.util.TreeMap;

import estaticos.CentroInfo;
import estaticos.GestorSalida;
import estaticos.MundoDofus;

import variables.Hechizo.StatsHechizos;

public class Encarnacion {
	private int _id;
	private int _clase;
	private long _experiencia;
	private int _nivel;
	private int _segundos;
	private int _gfx;
	private Map<Integer, StatsHechizos> _hechizos = new TreeMap<Integer, StatsHechizos>();
	private Map<Integer, Character> _lugaresHechizos = new TreeMap<Integer, Character>();
	
	public Encarnacion(int id, int clase, int nivel, long experiencia, int segundos, String hechizos) {
		_id = id;
		_clase = clase;
		_nivel = nivel;
		_experiencia = experiencia;
		_gfx = getGFXPorClase();
		_segundos = segundos;
		hechizosClase(_clase, (_nivel / 10) + 1);
		if (hechizos.isEmpty())
			hechizos = stringHechizosABD();
		analizarPosHechizos(hechizos);
	}
	
	public int getGFXPorClase() {
		switch (_clase) {
			case CentroInfo.ATORMENTADOR_NUBE:
				break;
			case CentroInfo.ATORMENTADOR_GOTA:
				break;
			case CentroInfo.ATORMENTADOR_TINIEBLAS:
				break;
			case CentroInfo.ATORMENTADOR_LLAMAS:
				break;
			case CentroInfo.ATORMENTADOR_HOJA:
				break;
			case CentroInfo.BANDIDO_HECHIZERO:
				return 8034;
			case CentroInfo.BANDIDO_ARQUERO:
				return 8032;
			case CentroInfo.BANDIDO_PENDENCIERO:
				return 8033;
			case CentroInfo.BANDIDO_ESPADACHIN:
				return 8035;
		}
		return 9999;
	}
	
	public boolean sePuedePoner(int segundos) {
		int resta = Math.abs(_segundos - segundos);
		return resta > 60;
	}
	
	public Map<Integer, StatsHechizos> getMapHechizos() {
		return _hechizos;
	}
	
	public void subirNivel(Personaje perso) {
		if (_nivel == 50)
			return;
		_nivel++;
		if (_nivel % 10 == 0) {
			hechizosClase(_clase, (_nivel / 10) + 1);
			GestorSalida.enviar(perso, actualizarNivelHechizos( (_nivel / 10) + 1, true));
		}
		GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso, MundoDofus.getObjeto(_id));
		perso.setPDVMAX(getPDVMAX());
		perso.fullPDV();
		perso.actualizarInfoGrupo();
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
	}
	
	public String actualizarNivelHechizos(int nivel, boolean encarnacion) {
		boolean primero = true;
		String packet = "";
		for (StatsHechizos SH : getMapHechizos().values()) {
			if (!primero)
				packet += (char) 0x00;
			if (encarnacion)
				packet += "SUK" + SH.getHechizoID() + "~" + nivel;
			else {
				packet += "SUK" + SH.getHechizoID() + "~" + SH.getNivel();
			}
			primero = false;
		}
		return packet;
	}
	
	public int getPDVMAX() {
		return (50 + _nivel * 20);
	}
	
	public void setSegundos(int segundos) {
		_segundos = segundos;
	}
	
	public void addExp(long xp, Personaje perso) {
		_experiencia += xp;
		while (_experiencia >= MundoDofus.getExpMaxPersonaje(_nivel) && _nivel < 50)
			subirNivel(perso);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
	}
	
	public String stringHechizosABD() {
		String hechizos = "";
		if (_hechizos.isEmpty())
			return "";
		for (int key : _hechizos.keySet()) {
			StatsHechizos SH = _hechizos.get(key);
			hechizos += SH.getHechizoID() + ";" + SH.getNivel() + ";";
			if (_lugaresHechizos.get(key) != null)
				hechizos += _lugaresHechizos.get(key);
			else
				hechizos += "_";
			hechizos += ",";
		}
		hechizos = hechizos.substring(0, hechizos.length() - 1);
		return hechizos;
	}
	
	private void analizarPosHechizos(String str) {
		String[] hechizos = str.split(",");
		for (String e : hechizos) {
			try {
				int id = Integer.parseInt(e.split(";")[0]);
				char pos = e.split(";")[2].charAt(0);
				hechizosClase(_clase, (_nivel / 10) + 1);
				_lugaresHechizos.put(id, pos);
			} catch (NumberFormatException e1) {
				continue;
			}
		}
	}
	
	public void hechizosClase(int clase, int nivel) {
		_hechizos = new TreeMap<Integer, StatsHechizos>();
		switch (clase) {
			case CentroInfo.ATORMENTADOR_NUBE:
				_hechizos.put(1291, MundoDofus.getHechizo(1291).getStatsPorNivel(nivel));
				_hechizos.put(1296, MundoDofus.getHechizo(1296).getStatsPorNivel(nivel));
				_hechizos.put(1289, MundoDofus.getHechizo(1289).getStatsPorNivel(nivel));
				_hechizos.put(1285, MundoDofus.getHechizo(1285).getStatsPorNivel(nivel));
				_hechizos.put(1290, MundoDofus.getHechizo(1290).getStatsPorNivel(nivel));
				break;
			case CentroInfo.ATORMENTADOR_GOTA:
				_hechizos.put(1299, MundoDofus.getHechizo(1299).getStatsPorNivel(nivel));
				_hechizos.put(1288, MundoDofus.getHechizo(1288).getStatsPorNivel(nivel));
				_hechizos.put(1297, MundoDofus.getHechizo(1297).getStatsPorNivel(nivel));
				_hechizos.put(1285, MundoDofus.getHechizo(1285).getStatsPorNivel(nivel));
				_hechizos.put(1298, MundoDofus.getHechizo(1298).getStatsPorNivel(nivel));
				break;
			case CentroInfo.ATORMENTADOR_TINIEBLAS:
				_hechizos.put(1300, MundoDofus.getHechizo(1300).getStatsPorNivel(nivel));
				_hechizos.put(1301, MundoDofus.getHechizo(1301).getStatsPorNivel(nivel));
				_hechizos.put(1303, MundoDofus.getHechizo(1303).getStatsPorNivel(nivel));
				_hechizos.put(1285, MundoDofus.getHechizo(1285).getStatsPorNivel(nivel));
				_hechizos.put(1302, MundoDofus.getHechizo(1302).getStatsPorNivel(nivel));
				break;
			case CentroInfo.ATORMENTADOR_LLAMAS:
				_hechizos.put(1292, MundoDofus.getHechizo(1292).getStatsPorNivel(nivel));
				_hechizos.put(1293, MundoDofus.getHechizo(1293).getStatsPorNivel(nivel));
				_hechizos.put(1294, MundoDofus.getHechizo(1294).getStatsPorNivel(nivel));
				_hechizos.put(1285, MundoDofus.getHechizo(1285).getStatsPorNivel(nivel));
				_hechizos.put(1295, MundoDofus.getHechizo(1295).getStatsPorNivel(nivel));
				break;
			case CentroInfo.ATORMENTADOR_HOJA:
				_hechizos.put(1283, MundoDofus.getHechizo(1283).getStatsPorNivel(nivel));
				_hechizos.put(1284, MundoDofus.getHechizo(1284).getStatsPorNivel(nivel));
				_hechizos.put(1286, MundoDofus.getHechizo(1286).getStatsPorNivel(nivel));
				_hechizos.put(1285, MundoDofus.getHechizo(1285).getStatsPorNivel(nivel));
				_hechizos.put(1287, MundoDofus.getHechizo(1287).getStatsPorNivel(nivel));
				break;
			case CentroInfo.BANDIDO_HECHIZERO:
				_hechizos.put(1601, MundoDofus.getHechizo(1601).getStatsPorNivel(nivel));
				_hechizos.put(1602, MundoDofus.getHechizo(1602).getStatsPorNivel(nivel));
				_hechizos.put(1603, MundoDofus.getHechizo(1603).getStatsPorNivel(nivel));
				_hechizos.put(1604, MundoDofus.getHechizo(1604).getStatsPorNivel(nivel));
				_hechizos.put(1605, MundoDofus.getHechizo(1605).getStatsPorNivel(nivel));
				_hechizos.put(1606, MundoDofus.getHechizo(1606).getStatsPorNivel(nivel));
				_hechizos.put(1607, MundoDofus.getHechizo(1607).getStatsPorNivel(nivel));
				_hechizos.put(1608, MundoDofus.getHechizo(1608).getStatsPorNivel(nivel));
				_hechizos.put(1609, MundoDofus.getHechizo(1609).getStatsPorNivel(nivel));
				_hechizos.put(1610, MundoDofus.getHechizo(1610).getStatsPorNivel(nivel));
				_hechizos.put(1611, MundoDofus.getHechizo(1611).getStatsPorNivel(nivel));
				_hechizos.put(1612, MundoDofus.getHechizo(1612).getStatsPorNivel(nivel));
				_hechizos.put(1613, MundoDofus.getHechizo(1613).getStatsPorNivel(nivel));
				_hechizos.put(1614, MundoDofus.getHechizo(1614).getStatsPorNivel(nivel));
				_hechizos.put(1615, MundoDofus.getHechizo(1615).getStatsPorNivel(nivel));
				_hechizos.put(1616, MundoDofus.getHechizo(1616).getStatsPorNivel(nivel));
				_hechizos.put(1617, MundoDofus.getHechizo(1617).getStatsPorNivel(nivel));
				_hechizos.put(1618, MundoDofus.getHechizo(1618).getStatsPorNivel(nivel));
				_hechizos.put(1619, MundoDofus.getHechizo(1619).getStatsPorNivel(nivel));
				_hechizos.put(1620, MundoDofus.getHechizo(1620).getStatsPorNivel(nivel));
				break;
			case CentroInfo.BANDIDO_ARQUERO:
				_hechizos.put(1561, MundoDofus.getHechizo(1561).getStatsPorNivel(nivel));
				_hechizos.put(1562, MundoDofus.getHechizo(1562).getStatsPorNivel(nivel));
				_hechizos.put(1563, MundoDofus.getHechizo(1563).getStatsPorNivel(nivel));
				_hechizos.put(1564, MundoDofus.getHechizo(1564).getStatsPorNivel(nivel));
				_hechizos.put(1565, MundoDofus.getHechizo(1565).getStatsPorNivel(nivel));
				_hechizos.put(1566, MundoDofus.getHechizo(1566).getStatsPorNivel(nivel));
				_hechizos.put(1567, MundoDofus.getHechizo(1567).getStatsPorNivel(nivel));
				_hechizos.put(1568, MundoDofus.getHechizo(1568).getStatsPorNivel(nivel));
				_hechizos.put(1569, MundoDofus.getHechizo(1569).getStatsPorNivel(nivel));
				_hechizos.put(1570, MundoDofus.getHechizo(1570).getStatsPorNivel(nivel));
				_hechizos.put(1571, MundoDofus.getHechizo(1571).getStatsPorNivel(nivel));
				_hechizos.put(1572, MundoDofus.getHechizo(1572).getStatsPorNivel(nivel));
				_hechizos.put(1573, MundoDofus.getHechizo(1573).getStatsPorNivel(nivel));
				_hechizos.put(1574, MundoDofus.getHechizo(1574).getStatsPorNivel(nivel));
				_hechizos.put(1575, MundoDofus.getHechizo(1575).getStatsPorNivel(nivel));
				_hechizos.put(1576, MundoDofus.getHechizo(1576).getStatsPorNivel(nivel));
				_hechizos.put(1577, MundoDofus.getHechizo(1577).getStatsPorNivel(nivel));
				_hechizos.put(1578, MundoDofus.getHechizo(1578).getStatsPorNivel(nivel));
				_hechizos.put(1579, MundoDofus.getHechizo(1579).getStatsPorNivel(nivel));
				_hechizos.put(1580, MundoDofus.getHechizo(1580).getStatsPorNivel(nivel));
				break;
			case CentroInfo.BANDIDO_PENDENCIERO:
				_hechizos.put(1581, MundoDofus.getHechizo(1581).getStatsPorNivel(nivel));
				_hechizos.put(1582, MundoDofus.getHechizo(1582).getStatsPorNivel(nivel));
				_hechizos.put(1583, MundoDofus.getHechizo(1583).getStatsPorNivel(nivel));
				_hechizos.put(1584, MundoDofus.getHechizo(1584).getStatsPorNivel(nivel));
				_hechizos.put(1585, MundoDofus.getHechizo(1585).getStatsPorNivel(nivel));
				_hechizos.put(1586, MundoDofus.getHechizo(1586).getStatsPorNivel(nivel));
				_hechizos.put(1587, MundoDofus.getHechizo(1587).getStatsPorNivel(nivel));
				_hechizos.put(1588, MundoDofus.getHechizo(1588).getStatsPorNivel(nivel));
				_hechizos.put(1589, MundoDofus.getHechizo(1589).getStatsPorNivel(nivel));
				_hechizos.put(1590, MundoDofus.getHechizo(1590).getStatsPorNivel(nivel));
				_hechizos.put(1591, MundoDofus.getHechizo(1591).getStatsPorNivel(nivel));
				_hechizos.put(1592, MundoDofus.getHechizo(1592).getStatsPorNivel(nivel));
				_hechizos.put(1593, MundoDofus.getHechizo(1593).getStatsPorNivel(nivel));
				_hechizos.put(1594, MundoDofus.getHechizo(1594).getStatsPorNivel(nivel));
				_hechizos.put(1595, MundoDofus.getHechizo(1595).getStatsPorNivel(nivel));
				_hechizos.put(1596, MundoDofus.getHechizo(1596).getStatsPorNivel(nivel));
				_hechizos.put(1597, MundoDofus.getHechizo(1597).getStatsPorNivel(nivel));
				_hechizos.put(1598, MundoDofus.getHechizo(1598).getStatsPorNivel(nivel));
				_hechizos.put(1599, MundoDofus.getHechizo(1599).getStatsPorNivel(nivel));
				_hechizos.put(1600, MundoDofus.getHechizo(1600).getStatsPorNivel(nivel));
				break;
			case CentroInfo.BANDIDO_ESPADACHIN:
				_hechizos.put(1541, MundoDofus.getHechizo(1541).getStatsPorNivel(nivel));
				_hechizos.put(1542, MundoDofus.getHechizo(1542).getStatsPorNivel(nivel));
				_hechizos.put(1543, MundoDofus.getHechizo(1543).getStatsPorNivel(nivel));
				_hechizos.put(1544, MundoDofus.getHechizo(1544).getStatsPorNivel(nivel));
				_hechizos.put(1545, MundoDofus.getHechizo(1545).getStatsPorNivel(nivel));
				_hechizos.put(1546, MundoDofus.getHechizo(1546).getStatsPorNivel(nivel));
				_hechizos.put(1547, MundoDofus.getHechizo(1547).getStatsPorNivel(nivel));
				_hechizos.put(1548, MundoDofus.getHechizo(1548).getStatsPorNivel(nivel));
				_hechizos.put(1549, MundoDofus.getHechizo(1549).getStatsPorNivel(nivel));
				_hechizos.put(1550, MundoDofus.getHechizo(1550).getStatsPorNivel(nivel));
				_hechizos.put(1551, MundoDofus.getHechizo(1551).getStatsPorNivel(nivel));
				_hechizos.put(1552, MundoDofus.getHechizo(1552).getStatsPorNivel(nivel));
				_hechizos.put(1553, MundoDofus.getHechizo(1553).getStatsPorNivel(nivel));
				_hechizos.put(1554, MundoDofus.getHechizo(1554).getStatsPorNivel(nivel));
				_hechizos.put(1555, MundoDofus.getHechizo(1555).getStatsPorNivel(nivel));
				_hechizos.put(1556, MundoDofus.getHechizo(1556).getStatsPorNivel(nivel));
				_hechizos.put(1557, MundoDofus.getHechizo(1557).getStatsPorNivel(nivel));
				_hechizos.put(1558, MundoDofus.getHechizo(1558).getStatsPorNivel(nivel));
				_hechizos.put(1559, MundoDofus.getHechizo(1559).getStatsPorNivel(nivel));
				_hechizos.put(1560, MundoDofus.getHechizo(1560).getStatsPorNivel(nivel));
				break;
		}
		return;
	}
	
	public boolean tieneHechizoID(int hechizo) {
		return _hechizos.get(hechizo) != null;
	}
	
	public String stringListaHechizos() {
		String str = "";
		for (StatsHechizos SH : _hechizos.values()) {
			if (_lugaresHechizos.get(SH.getHechizoID()) == null)
				str += SH.getHechizoID() + "~" + SH.getNivel() + "~_;";
			else
				str += SH.getHechizoID() + "~" + SH.getNivel() + "~" + _lugaresHechizos.get(SH.getHechizoID()) + ";";
		}
		return str;
	}
	
	public void setPosHechizo(int hechizo, char pos) {
		reemplazarHechizoEnPos(pos);
		_lugaresHechizos.remove(hechizo);
		_lugaresHechizos.put(hechizo, pos);
	}
	
	private void reemplazarHechizoEnPos(char pos) {
		for (int key : _hechizos.keySet()) {
			if (_lugaresHechizos.get(key) != null) {
				if (_lugaresHechizos.get(key).equals(pos)) {
					_lugaresHechizos.remove(key);
				}
			}
		}
	}
	
	public int getClase() {
		return _clase;
	}
	
	public int getID() {
		return _id;
	}
	
	public int getGfx() {
		return _gfx;
	}
	
	public long getExperiencia() {
		return _experiencia;
	}
	
	public int getNivel() {
		return _nivel;
	}
	
	public int getSegundos() {
		return _segundos;
	}
	
	public StatsHechizos getStatsHechizo(int hechizoID) {
		return _hechizos.get(hechizoID);
	}
}
