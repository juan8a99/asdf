
package variables;

import estaticos.Formulas;
import estaticos.GestorSalida;

import variables.Pelea;
import variables.Pelea.Luchador;

public class Reto {
	private int _id;
	private String _bonus;
	private int _bonusXP;
	private int _bonusPP;
	
	public Reto(int ID, String bonusreto) {
		_id = ID;
		_bonus = bonusreto;
		String[] bonus = _bonus.split(";");
		_bonusXP = Integer.parseInt(bonus[0]) + Integer.parseInt(bonus[1]);
		_bonusPP = Integer.parseInt(bonus[2]) + Integer.parseInt(bonus[3]);
	}
	
	public String getBonus() {
		return _bonus;
	}
	
	public int getId() {
		return _id;
	}
	
	public String getDetalleReto(Pelea pelea) {
		String datosReto = "";
		int idMob = 0;
		int celda = 0;
		if (_id == 3 || _id == 4 || _id == 32) {
			int azar = Formulas.getRandomValor(0, pelea.luchadoresDeEquipo(2).size() - 1);
			Luchador mob = pelea.luchadoresDeEquipo(2).get(azar);
			idMob = mob.getID();
			celda = mob.getCeldaPelea().getID();
		}
		switch (_id) {
			case 1:// zombi, muevete solo 1 PM en cada turno
			case 2:// estatua, Acaba tu turno en la misma casilla donde lo empezaste
			case 5:// ahorrador, durante el combate solo se podra usar 1 accion por unica vez
			case 6:// vesrsatil, durante su turno cada jugador solo podra usar 1 accion, no repetir
			case 7:// jardinero, durante el combate, plantar una zanahowia cada vez q se pueda ZZZZZ
			case 8:// nomada, utlizar todos los PM disponibles en cada turno
			case 9:// barbaro, no utilizar ningun hechizo mientras dure el combate
			case 10:// cruel, debes matar a los mobs en orden creciente de nivel ZZZZZZ
			case 11:// mistico, solamente usa hechizos
			case 12:// sepultero, invoca un chaferloko cada vez q se pueda ZZZZZZ
			case 14:// casino real, lanzar el hechizo ruleta cada vez q se pueda ZZZZZZZ
			case 15:// aracnofilo, invocar una araña cada vez q se pueda ZZZZZZ
			case 16:// entomologo, invocar una llamita cada vez q se pueda ZZZZZZZ
			case 17:// intocable, no perder puntos de vida durante el tiempo que dure el combate
			case 18:// incurable, no curar durante el tiempo que dure el combate
			case 19:// manos limpias,acabar con los mounstros sin ocasionarles daños directos
			case 20:// elemental, utliza el mismo elemento de ataque durante el combate
			case 21:// circulen, no kitar PM a los adversarios mientras dure el combate
			case 22:// el tiempo pasa, no kitar PA a los adversarios mientras dure el combate
			case 23:// perdido de vista, no reducir el alcance a los adversarios
			case 24:// limitado, utlizar el mismo hechizo o CaC mientras dure el combate
			case 25:// ordenado, se deben acabar con los adversarios en orden decreciente de nivel
			case 28:// ni pias ni sumisas, las mujeres deben acabar con todos los mobs
			case 29:// ni pios ni sumisos, los hombres deben acabar con todos los mobs
			case 30:// los pequeños antes, el personaje de menor nivel debe acabar con todos los mobs
			case 31:// focalizacion, cuando se ataca a un adversario, hay q matarlo para atacar a otro
			case 33:// superviviente
			case 34:// imprevisible
			case 35:// asesino a sueldo
			case 36:// audaz
			case 37:// pegajoso
			case 38:// Blitzkrieg
			case 39:// anacoreta
			case 40:// pusilanime
			case 41:// impetuoso
			case 42:// el dos por uno
			case 43:// abnegacion
			case 44:// reparto
			case 45:// duelo
			case 46:// cada uno con su mounstro
			case 47:// contaminacion
			case 48:// los personajes secundarios primero
			case 49:// protejan a sus personajes secundarios
			case 50:// la trampa de los desarrolladores
				datosReto = _id + ";0;0;" + _bonus + ";0";
				break;
			case 3:// eligido
			case 4:// aplazamiento
			case 32:// elitista
				datosReto = _id + ";1;" + idMob + ";" + _bonus + ";0";
				pelea.setIDMobReto(idMob);
				GestorSalida.ENVIAR_Gf_MOSTRAR_CASILLA_EN_PELEA(pelea, 5, idMob, celda);
				break;
		}
		pelea.putStringReto(_id, datosReto);
		return datosReto;
	}
	
	public int bonusXP() {
		return _bonusXP;
	}
	
	public int bonusPP() {
		return _bonusPP;
	}
}
