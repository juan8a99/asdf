
package variables;

import java.util.ArrayList;

import estaticos.MundoDofus;
import estaticos.MundoDofus.Duo;

public class PiedraDeAlma extends Objeto {
	private ArrayList<Duo<Integer, Integer>> _mobs;
	
	public PiedraDeAlma(int id, int cantidad, int modelo, int pos, String strStats) {
		_id = id;
		_modelo = MundoDofus.getObjModelo(modelo); // 7010 = piedra de alma
		_idObjModelo = _modelo.getID();
		_cantidad = 1;
		_posicion = -1;
		_mobs = new ArrayList<Duo<Integer, Integer>>();
		convertirStringAStats(strStats);
	}
	
	public void convertirStringAStats(String mounstros) {
		String[] split = mounstros.split(",");
		for (String s : split) {
			try {
				int mob = Integer.parseInt(s.split("#")[3], 16);
				int nivel = Integer.parseInt(s.split("#")[1], 16);
				_mobs.add(new Duo<Integer, Integer>(mob, nivel));
			} catch (Exception e) {
				continue;
			}
		}
	}
	
	public String convertirStatsAString() {
		String stats = "";
		boolean primero = true;
		for (Duo<Integer, Integer> coupl : _mobs) {
			if (!primero)
				stats += ",";
			try {
				stats += "26f#" + Integer.toHexString(coupl._segundo) + "#0#" + Integer.toHexString(coupl._primero);
			} catch (Exception e) {
				continue;
			}
			primero = false;
		}
		return stats;
	}
	
	public String analizarGrupo() {
		String string = "";
		boolean primero = true;
		for (Duo<Integer, Integer> mob : _mobs) {
			if (!primero)
				string += ";";
			string += mob._primero + "," + mob._segundo + "," + mob._segundo;
			primero = false;
		}
		return string;
	}
}
