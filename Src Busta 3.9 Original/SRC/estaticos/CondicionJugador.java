
package estaticos;

import java.util.ArrayList;

import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;

import variables.*;
import variables.Personaje.Stats;

public class CondicionJugador {
	public static boolean validaCondiciones(Personaje perso, String condiciones) {
		if (condiciones == null || condiciones.equals("") || condiciones.equalsIgnoreCase("EVENTO"))
			return true;
		if (condiciones.contains("BI"))
			return false;
		Jep jep = new Jep();
		if (condiciones.contains("PO"))
			condiciones = tieneObjetoModelo(condiciones, perso);
		condiciones = condiciones.replace("&", "&&").replace("=", "==").replace("|", "||").replace("!", "!=");
		try {
			Stats totalStas = perso.getTotalStats();
			jep.addVariable("CI", totalStas.getEfecto(CentroInfo.STATS_ADD_INTELIGENCIA));
			jep.addVariable("CV", totalStas.getEfecto(CentroInfo.STATS_ADD_VITALIDAD));
			jep.addVariable("CA", totalStas.getEfecto(CentroInfo.STATS_ADD_AGILIDAD));
			jep.addVariable("CW", totalStas.getEfecto(CentroInfo.STATS_ADD_SABIDURIA));
			jep.addVariable("CC", totalStas.getEfecto(CentroInfo.STATS_ADD_SUERTE));
			jep.addVariable("CS", totalStas.getEfecto(CentroInfo.STATS_ADD_FUERZA));
			jep.addVariable("Ci", perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_INTELIGENCIA));
			jep.addVariable("Cs", perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_FUERZA));
			jep.addVariable("Cv", perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_VITALIDAD));
			jep.addVariable("Ca", perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_AGILIDAD));
			jep.addVariable("Cw", perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_SABIDURIA));
			jep.addVariable("Cc", perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_SUERTE));
			jep.addVariable("Ps", perso.getAlineacion());
			jep.addVariable("Pa", perso.getNivelAlineacion());
			jep.addVariable("PP", perso.getNivelAlineacion());
			jep.addVariable("PL", perso.getNivel());
			jep.addVariable("PK", perso.getKamas());
			jep.addVariable("PG", perso.getClase(true));
			jep.addVariable("PS", perso.getSexo());
			jep.addVariable("PZ", true);
			jep.addVariable("esCasado", perso.getEsposo());
			jep.addVariable("siKamas", perso.getKamas());
			jep.addVariable("esKoliseo", perso.getEnKoliseo());
			jep.addVariable("MiS", perso.getID());
			jep.parse(condiciones);
			Object resultado = jep.evaluate();
			boolean ok = false;
			if (resultado != null)
				ok = Boolean.valueOf(resultado.toString());
			return ok;
		} catch (JepException e) {
			System.out.println("Un error ocurrio: " + e.getMessage());
		}
		return true;
	}
	
	public static String tieneObjetoModelo(String condiciones, Personaje perso) {
		String[] str = condiciones.replaceAll("[ ()]", "").split("[|&]");
		ArrayList<Integer> valores = new ArrayList<Integer>(str.length);
		for (String condicion : str) {
			if (!condicion.contains("PO"))
				continue;
			if (perso.tieneObjModeloNoEquip(Integer.parseInt(condicion.split("[=]")[1]), 1))
				valores.add(Integer.parseInt(condicion.split("[=]")[1]));
			else
				valores.add(-1);
		}
		for (int valor : valores) {
			condiciones = condiciones.replaceFirst("PO", valor + "");
		}
		return condiciones;
	}
}
