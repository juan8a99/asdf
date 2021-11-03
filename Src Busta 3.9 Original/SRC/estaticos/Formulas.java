
package estaticos;

import java.util.ArrayList;
import java.util.Random;

import estaticos.MundoDofus.Duo;

import variables.*;
import variables.Gremio.MiembroGremio;
import variables.Pelea.Luchador;
import variables.Personaje.Stats;

public class Formulas {
	public static float ADIC_PJ = 0.95F;
	public static float ADIC_MOB = 1.0F;
	public static float ADIC_CAC = 0.90F;
	public static float PROSP_REQ = 1.0F;
	
	public static int getRandomValor(int i1, int i2) {
		Random rand = new Random();
		return (rand.nextInt( (i2 - i1) + 1)) + i1;
	}
	
	public static int getRandomValor(String rango) {// 1d4+5 2d5+10 0d0+157
		try {
			int num = 0;
			int veces = Integer.parseInt(rango.split("d")[0]);
			int margen = Integer.parseInt(rango.split("d")[1].split("\\+")[0]);
			int adicional = Integer.parseInt(rango.split("d")[1].split("\\+")[1]);
			for (int a = 0; a < veces; a++) {
				num += getRandomValor(1, margen);
			}
			num += adicional;
			return num;
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public static int getMaxValor(String rango) {
		try {
			int num = 0;
			int veces = Integer.parseInt(rango.split("d")[0]);
			int margen = Integer.parseInt(rango.split("d")[1].split("\\+")[0]);
			int adicional = Integer.parseInt(rango.split("d")[1].split("\\+")[1]);
			for (int a = 0; a < veces; a++) {
				num += margen;
			}
			num += adicional;
			return num;
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public static int getMinValor(String rango) {
		try {
			int num = 0;
			int veces = Integer.parseInt(rango.split("d")[0]);
			int adicional = Integer.parseInt(rango.split("d")[1].split("\\+")[1]);
			for (int a = 0; a < veces; a++) {
				num += 1;
			}
			num += adicional;
			return num;
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public static int getMedioValor(String rango) {
		try {
			int num = 0;
			int veces = Integer.parseInt(rango.split("d")[0]);
			int margen = Integer.parseInt(rango.split("d")[1].split("\\+")[0]);
			int adicional = Integer.parseInt(rango.split("d")[1].split("\\+")[1]);
			num += ( (1 + margen) / 2) * veces;
			num += adicional;
			return num;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static int getPorcTacleo(Luchador tacleador, Luchador tacleado) {
		int agiTR = tacleador.getTotalStatsConBuff().getEfecto(119);
		int agiT = tacleado.getTotalStatsConBuff().getEfecto(119);
		int a = agiTR + 25;
		int b = agiTR + agiT + 50;
		if (b <= 0)
			b = 1;
		int suerte = (int) ((long) (300 * a / b) - 100);
		if (suerte < 10)
			suerte = 10;
		if (suerte > 90)
			suerte = 90;
		return suerte;
	}
	
	public static int calculFinalCura(Luchador curador, int rango, boolean esCaC) {
		int inteligencia = curador.getTotalStatsConBuff().getEfecto(126);
		int curas = curador.getTotalStatsConBuff().getEfecto(178);
		if (inteligencia < 0)
			inteligencia = 0;
		float adic = 100;
		if (esCaC)
			adic = 105;
		return (int) (rango * ( (100.00 + inteligencia) / adic) + curas / 2);
	}
	
	public static int calculFinalDaño(Pelea pelea, Luchador lanzador, Luchador objetivo, int statID, int rango, boolean esCaC,
			int hechizoID, boolean veneno) {
		float adicPj = ADIC_PJ;
		float adicMob = ADIC_MOB;
		float a = 1;
		float num = 0;
		int statC = 0;
		float masDaños = 0, porcDaños = 0, resMasT = 0, resPorcT = 0;
		int multiplicaDaños = 0;
		Stats totalLanzador = lanzador.getTotalStatsConBuff();
		Stats totalObjetivo = objetivo.getTotalStatsConBuff();
		masDaños = totalLanzador.getEfecto(112);
		porcDaños = totalLanzador.getEfecto(138);
		multiplicaDaños = totalLanzador.getEfecto(114);
		switch (statID) {
			case CentroInfo.ELEMENTO_NULO:
				statC = 0;
				resMasT = 0;
				resPorcT = 0;
				break;
			case CentroInfo.ELEMENTO_NEUTRAL:// neutral
				statC = totalLanzador.getEfecto(118);
				resMasT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_R_NEUTRAL);
				resPorcT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_ResPorc_NEUTRAL);
				if (objetivo.getPersonaje() != null) {
					resPorcT += totalObjetivo.getEfecto(CentroInfo.STATS_ADD_RP_PVP_NEUTRAL);
					resMasT += totalObjetivo.getEfecto(CentroInfo.STATS_ADD_R_PVP_NEUTRAL);
				}
				masDaños += totalLanzador.getEfecto(142);
				resMasT += totalObjetivo.getEfecto(184);
				break;
			case CentroInfo.ELEMENTO_TIERRA:// fuerza
				statC = totalLanzador.getEfecto(118);
				resMasT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_R_TIERRA);
				resPorcT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_ResPorc_TIERRA);
				if (objetivo.getPersonaje() != null) {
					resPorcT += totalObjetivo.getEfecto(CentroInfo.STATS_ADD_RP_PVP_TIERRA);
					resMasT += totalObjetivo.getEfecto(CentroInfo.STATS_ADD_R_PVP_TIERRA);
				}
				resMasT += totalObjetivo.getEfecto(183);
				break;
			case CentroInfo.ELEMENTO_AGUA:// suerte
				statC = totalLanzador.getEfecto(123);
				resMasT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_R_AGUA);
				resPorcT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_ResPorc_AGUA);
				if (objetivo.getPersonaje() != null) {
					resPorcT += totalObjetivo.getEfecto(CentroInfo.STATS_ADD_RP_PVP_AGUA);
					resMasT += totalObjetivo.getEfecto(CentroInfo.STATS_ADD_R_PVP_AGUA);
				}
				resMasT += totalObjetivo.getEfecto(183);
				break;
			case CentroInfo.ELEMENTO_FUEGO:// inteligencia
				statC = totalLanzador.getEfecto(126);
				resMasT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_R_FUEGO);
				resPorcT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_ResPorc_FUEGO);
				if (objetivo.getPersonaje() != null) {
					resPorcT += totalObjetivo.getEfecto(CentroInfo.STATS_ADD_RP_PVP_FUEGO);
					resMasT += totalObjetivo.getEfecto(CentroInfo.STATS_ADD_R_PVP_FUEGO);
				}
				resMasT += totalObjetivo.getEfecto(183);
				break;
			case CentroInfo.ELEMENTO_AIRE:// agilidad
				statC = totalLanzador.getEfecto(119);
				resMasT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_R_AIRE);
				resPorcT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_ResPorc_AIRE);
				if (objetivo.getPersonaje() != null) {
					resPorcT += totalObjetivo.getEfecto(CentroInfo.STATS_ADD_RP_PVP_AIRE);
					resMasT += totalObjetivo.getEfecto(CentroInfo.STATS_ADD_R_PVP_AIRE);
				}
				resMasT += totalObjetivo.getEfecto(183);
				break;
		}
		if (objetivo.getPersonaje() != null && resPorcT > 75)
			resPorcT = 75;
		if (statC < 0)
			statC = 0;
		Personaje perso = lanzador.getPersonaje();
		if (perso != null && esCaC) {
			adicPj = ADIC_CAC;
			int armaTipo = perso.getObjPosicion(1).getModelo().getTipo();
			float i = 0;
			int porc = 90;
			int clase = perso.getClase(true);
			switch (armaTipo) {
				case 2:// arco
					if (lanzador.tieneBuffHechizoID(392))
						i = lanzador.getDañoDominio(392);
					if (clase == 4)
						porc = 95;
					else if (clase == 9)
						porc = 100;
					break;
				case 3:// varita
					if (lanzador.tieneBuffHechizoID(394))
						i = lanzador.getDañoDominio(394);
					if (clase == 1 || clase == 5)
						porc = 95;
					else if (clase == 7)
						porc = 100;
					break;
				case 4:// baston
					if (lanzador.tieneBuffHechizoID(390))
						i = lanzador.getDañoDominio(390);
					if (clase == 7 || clase == 2 || clase == 12)
						porc = 95;
					else if (clase == 1 || clase == 10)
						porc = 100;
					break;
				case 5:// dagas
					if (lanzador.tieneBuffHechizoID(395))
						i = lanzador.getDañoDominio(395);
					if (clase == 9 || clase == 6)
						porc = 95;
					else if (clase == 4)
						porc = 100;
					break;
				case 6:// espada
					if (lanzador.tieneBuffHechizoID(391))
						i = lanzador.getDañoDominio(391);
					if (clase == 8 || clase == 6)
						porc = 100;
					break;
				case 7:// martillo
					if (lanzador.tieneBuffHechizoID(393))
						i = lanzador.getDañoDominio(393);
					if (clase == 3 || clase == 8 || clase == 10)
						porc = 95;
					else if (clase == 2 || clase == 5)
						porc = 100;
					break;
				case 8:// palas
					if (lanzador.tieneBuffHechizoID(396))
						i = lanzador.getDañoDominio(396);
					if (clase == 3)
						porc = 100;
					break;
				case 19:// hachas
					if (lanzador.tieneBuffHechizoID(397))
						i = lanzador.getDañoDominio(397);
					if (clase == 12)
						porc = 100;
					break;
			}
			a = ( (porc + i) / 100);
		}
		num = a * (rango * ( (100 + statC + porcDaños + (multiplicaDaños * 100)) / 100)) + masDaños;
		// Reenvio
		if (lanzador.getMob() != null) {
			if (lanzador.getMob().getModelo().getID() == 116) {
				num = lanzador.getPDVConBuff();
			}
		}
		num -= resMasT;
		int reduc = (int) ( (num * (float) resPorcT) / 100);
		num -= reduc;
		if (!veneno) {
			int armadura = getResisArmadura(objetivo, statID);
			if (armadura > 0) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 105, lanzador.getID() + "", objetivo.getID() + "," + armadura);
				num -= armadura;
			}
		}
		if (num < 1)
			num = 0;
		if (lanzador.getMob() != null) {
			if (lanzador.getMob().getModelo().getID() == 116) {
				return (int) num;
			} else {
				float calculCoef = (5.5F * (float) lanzador.getNivel()) / (200.0F + lanzador.getNivel());
				if (calculCoef < 1)
					calculCoef = 1;
				if (calculCoef > 5)
					calculCoef = 5;
				return (int) (num * calculCoef * adicMob);
			}
		} else {
			if (num > 50) {
				num = (int) (num * (adicPj - 0.01));
				if (num > 100) {
					num = (int) (num * (adicPj - 0.03));
					if (num > 200) {
						num = (int) (num * (adicPj - 0.06));
						if (num > 400) {
							num = (int) (num * (adicPj - 0.1));
						}
					}
				}
			}
			if (pelea.getTipoPelea() != 4) {
				int pdvMax = objetivo.getPDVMax();
				pdvMax = pdvMax - (int) (num * 0.1f);
				if (pdvMax < 0)
					pdvMax = 0;
				objetivo.setPDVMAX(pdvMax);
			}
			return (int) num;
		}
	}
	
	public static int calcularCosteZaap(Mapa map1, Mapa map2) {
		return (int) (10 * (Math.abs(map2.getX() - map1.getX()) + Math.abs(map2.getY() - map1.getY()) - 1));
	}
	
	private static int getResisArmadura(Luchador afectado, int statID) {
		int defensa = 0;
		int adic = 100;
		for (EfectoHechizo EH : afectado.getBuffsPorEfectoID(265)) {
			Luchador lanzArmadura;
			float div = 2;
			switch (EH.getHechizoID()) {
				case 1:// Armadura incadescente
					if (statID != CentroInfo.ELEMENTO_FUEGO)
						continue;
					div = 1.5F;
					lanzArmadura = EH.getLanzador();
					break;
				case 6:// Armuadura terrestre
					if (statID != CentroInfo.ELEMENTO_TIERRA && statID != CentroInfo.ELEMENTO_NEUTRAL)
						continue;
					div = 1.5F;
					lanzArmadura = EH.getLanzador();
					break;
				case 14:// Armadura Ventisca
					if (statID != CentroInfo.ELEMENTO_AIRE)
						continue;
					div = 1.5F;
					lanzArmadura = EH.getLanzador();
					break;
				case 18:// Armadura acuosa
					if (statID != CentroInfo.ELEMENTO_AGUA)
						continue;
					div = 1.5F;
					lanzArmadura = EH.getLanzador();
					break;
				default:
					lanzArmadura = EH.getLanzador();
					break;
			}
			Stats statsLanzArmadura = lanzArmadura.getTotalStatsConBuff();
			int inteligencia = statsLanzArmadura.getEfecto(126);
			int carac = 0;
			switch (statID) {
				case CentroInfo.ELEMENTO_AIRE:
					carac = statsLanzArmadura.getEfecto(119);
					break;
				case CentroInfo.ELEMENTO_FUEGO:
					carac = statsLanzArmadura.getEfecto(126);
					break;
				case CentroInfo.ELEMENTO_AGUA:
					carac = statsLanzArmadura.getEfecto(123);
					break;
				case CentroInfo.ELEMENTO_NEUTRAL:
				case CentroInfo.ELEMENTO_TIERRA:
					carac = statsLanzArmadura.getEfecto(118);
					break;
			}
			int valor = EH.getValor();
			int a = valor * (100 + (int) (inteligencia / div) + (int) (carac / 2)) / adic;
			defensa += a;
		}
		Stats statsAfectado = afectado.getTotalStatsConBuff();
		for (EfectoHechizo SE : afectado.getBuffsPorEfectoID(105)) {
			int inteligencia = statsAfectado.getEfecto(126);
			int carac = 0;
			switch (statID) {
				case CentroInfo.ELEMENTO_AIRE:
					carac = statsAfectado.getEfecto(119);
					break;
				case CentroInfo.ELEMENTO_FUEGO:
					carac = statsAfectado.getEfecto(126);
					break;
				case CentroInfo.ELEMENTO_AGUA:
					carac = statsAfectado.getEfecto(123);
					break;
				case CentroInfo.ELEMENTO_NEUTRAL:
				case CentroInfo.ELEMENTO_TIERRA:
					carac = statsAfectado.getEfecto(118);
					break;
			}
			int valor = SE.getValor();
			int a = valor * (100 + inteligencia / 2 + (int) (carac / 2)) / adic;
			defensa += a;
		}
		return defensa;
	}
	
	public static int getPuntosPerdidos(char z, int valor, Luchador lanzador, Luchador objetivo) {
		int esquivaLanzador = z == 'a' ? lanzador.getTotalStatsConBuff().getEfecto(160) : lanzador.getTotalStatsConBuff()
				.getEfecto(161);
		int esquivaObjetivo = z == 'a' ? objetivo.getTotalStatsConBuff().getEfecto(160) : objetivo.getTotalStatsConBuff()
				.getEfecto(161);
		int ptsMax = z == 'a' ? objetivo.getTotalStatsConBuff().getEfecto(111) : objetivo.getTotalStatsConBuff().getEfecto(128);
		int resta = 0;
		int acierto = esquivaLanzador - esquivaObjetivo;
		acierto = acierto / 10;
		if (acierto > 15)
			acierto = 15;
		if (acierto < 1)
			acierto = 1;
		for (int i = 0; i < valor; i++) {
			int azar = getRandomValor(0, 16);
			if (azar <= acierto)
				resta++;
		}
		if (resta > ptsMax)
			resta = ptsMax;
		return resta;
	}
	
	public static long getXpGanadaRecau(Recaudador recaudador, long totalXP) {
		Gremio G = MundoDofus.getGremio(recaudador.getGremioID());
		float sabi = G.getStats(CentroInfo.STATS_ADD_SABIDURIA);
		float coef = (sabi + 100) / 100;
		long xpGanada = 0;
		xpGanada = (int) (coef * totalXP);
		return (long) (xpGanada * Bustemu.RATE_XP_PVM);
	}
	
	public static long getXpGanadaPVM(Luchador perso, ArrayList<Luchador> ganadores, ArrayList<Luchador> perdedores, long grupoXP) {
		if (perso.getPersonaje() == null)
			return 0;
		if (ganadores.contains(perso)) {
			float sabiduria = perso.getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_SABIDURIA);
			float coef = (sabiduria + 100) / 100;
			long xpGanada = 0;
			int nivelMax = 0;
			for (Luchador entry : ganadores) {
				if (entry.getNivel() > nivelMax)
					nivelMax = entry.getNivel();
			}
			int nro = 0;
			for (Luchador entry : ganadores) {
				if (entry.getNivel() > (nivelMax / 3))
					nro += 1;
			}
			float bonus = 1;
			if (nro == 2)
				bonus = 1.1F;
			if (nro == 3)
				bonus = 1.3F;
			if (nro == 4)
				bonus = 2.2F;
			if (nro == 5)
				bonus = 2.5F;
			if (nro == 6)
				bonus = 2.8F;
			if (nro == 7)
				bonus = 3.1F;
			if (nro >= 8)
				bonus = 3.5F;
			int nivelPerdedores = 0;
			for (Luchador entry : perdedores)
				nivelPerdedores += entry.getNivel();
			int nivelGanadores = 0;
			for (Luchador entry : ganadores)
				nivelGanadores += entry.getNivel();
			float porcEntreGyP = 1 + ((float) nivelPerdedores / (float) nivelGanadores);
			if (porcEntreGyP <= 1.3)
				porcEntreGyP = 1.3F;
			int nivel = perso.getNivel();
			float porcEntrePjyG = 1 + ((float) nivel / (float) nivelGanadores);
			xpGanada = (long) (grupoXP * porcEntreGyP * bonus * coef * porcEntrePjyG);
			return (long) (xpGanada * Bustemu.RATE_XP_PVM);
		}
		return 0;
	}
	
	public static long getXPDonadaGremio(Luchador perso, long xpGanada) {
		if (perso.getPersonaje() == null)
			return 0;
		if (perso.getPersonaje().getMiembroGremio() == null)
			return 0;
		MiembroGremio gm = perso.getPersonaje().getMiembroGremio();
		float xp = (float) xpGanada, Lvl = perso.getNivel(), LvlGuild = perso.getPersonaje().getGremio().getNivel(), porcXPDonada = (float) gm
				.getPorcXpDonada() / 100;
		float maxP = xp * porcXPDonada * 0.10F;
		float diff = Math.abs(Lvl - LvlGuild);
		float alGremio;
		if (diff >= 70) {
			alGremio = maxP * 0.10F;
		} else if (diff >= 31 && diff <= 69) {
			alGremio = (float) (maxP - ( (maxP * 0.10F) * (Math.floor( (diff + 30) / 10))));
		} else if (diff >= 10 && diff <= 30) {
			alGremio = (float) (maxP - ( (maxP * 0.20F) * (Math.floor(diff / 10))));
		} else {
			alGremio = maxP;
		}
		return (long) Math.round(alGremio);
	}
	
	public static long getXPDonadaDragopavo(Luchador luchador, long xpGanada) {
		Personaje perso = luchador.getPersonaje();
		if (perso == null)
			return 0;
		Dragopavo pavo = perso.getMontura();
		if (pavo == null)
			return 0;
		float xp = (float) xpGanada;
		float coef = 1.0F;
		float porcMontura = (float) perso.getXpDonadaMontura() / 100;
		if (pavo.getNivel() < 50)
			coef = 1.0F;
		else if (pavo.getNivel() < 100)
			coef = 0.75F;
		else if (pavo.getNivel() < 150)
			coef = 0.5F;
		else if (pavo.getNivel() <= 200)
			coef = 0.25F;
		long xpdonada = (long) (xp * porcMontura * coef) / 100;
		return xpdonada;
	}
	
	public static int getKamasGanadas(Luchador luchador, int maxkamas, int minkamas) {
		int prospeccion = luchador.getTotalStatsConBuff().getEfecto(176);
		float coef = (prospeccion + 100F) / 100F;
		maxkamas++;
		int kamas = (int) (Math.random() * (maxkamas - minkamas)) + minkamas;
		return (int) (kamas * coef * Bustemu.RATE_KAMAS);
	}
	
	public static int getKamasGanadaRecau(int maxkamas, int minkamas) {
		maxkamas++;
		int kamas = (int) (Math.random() * (maxkamas - minkamas)) + minkamas;
		return (int) (kamas * Bustemu.RATE_KAMAS);
	}
	
	public static int calculoPorcCambioElenemto(int nivelOficio, int nivelObjeto, int nivelRunaElemento) {
		int K = 1;
		if (nivelRunaElemento == 1)
			K = 100;
		else if (nivelRunaElemento == 25)
			K = 175;
		else if (nivelRunaElemento == 50)
			K = 350;
		return (int) ( (nivelOficio * 100) / (K + nivelObjeto));
	}
	
	public static int calcularHonorGanado(ArrayList<Luchador> ganadores, ArrayList<Luchador> perdedores, Luchador luchador) {
		float totalNivAlineacionGanador = 0;
		float totalNivelGanador = 0;
		float totalNivAlineacionPerdedor = 0;
		float totalNivelPerdedor = 0;
		boolean prisma = false;
		int luchadores = 0;
		for (Luchador lucha : ganadores) {
			if (lucha.getPersonaje() == null && lucha.getPrisma() == null)
				continue;
			if (lucha.getPersonaje() != null) {
				totalNivelGanador += lucha.getNivel();
				totalNivAlineacionGanador += lucha.getPersonaje().getNivelAlineacion();
			} else {
				prisma = true;
				totalNivelGanador += 200;
				totalNivAlineacionGanador += (lucha.getPrisma().getNivel() * 20) + 80;
			}
		}
		for (Luchador lucha : perdedores) {
			if (lucha.getPersonaje() == null && lucha.getPrisma() == null)
				continue;
			if (lucha.getPersonaje() != null) {
				totalNivelPerdedor += lucha.getNivel();
				totalNivAlineacionPerdedor += lucha.getPersonaje().getNivelAlineacion();
				luchadores++;
			} else {
				prisma = true;
				totalNivelPerdedor += 200;
				totalNivAlineacionPerdedor += (lucha.getPrisma().getNivel() * 15) + 80;
			}
		}
		if (!prisma)
			if (totalNivelGanador - totalNivelPerdedor > 15 * luchadores)
				return 0;
		int base = (int) (100 * (float) ( (totalNivAlineacionPerdedor * totalNivelPerdedor) / (totalNivAlineacionGanador * totalNivelGanador)))
				/ ganadores.size();
		if (perdedores.contains(luchador))
			base = -base;
		return (int) (base * Bustemu.RATE_HONOR);
	}
	
	public static Duo<Integer, Integer> decompilarPiedraAlma(Objeto objeto) {
		Duo<Integer, Integer> duo;
		String[] stats = objeto.convertirStatsAString().split("#");
		int nivelMax = Integer.parseInt(stats[3], 16);
		int suerte = Integer.parseInt(stats[1], 16);
		duo = new Duo<Integer, Integer>(suerte, nivelMax);
		return duo;
	}
	
	public static int totalPorcCaptura(int suertePiedra, Personaje perso) {
		int suerteHechizo = 0;
		switch (perso.getStatsHechizo(413).getNivel()) {
			case 1:
				suerteHechizo = 1;
				break;
			case 2:
				suerteHechizo = 3;
				break;
			case 3:
				suerteHechizo = 6;
				break;
			case 4:
				suerteHechizo = 10;
				break;
			case 5:
				suerteHechizo = 15;
				break;
			case 6:
				suerteHechizo = 25;
				break;
		}
		return suerteHechizo + suertePiedra;
	}
	
	public static int costeHechizo(int nb) {
		int total = 0;
		for (int i = 1; i < nb; i++) {
			total += i;
		}
		return total;
	}
	
	public static float suerteFM(int pesoTotalBase, int pesoTotalActual, int pesoStatActual, int runa, int diferencia, float coef) {
		float porcentaje = 0;
		// float resta = 0;
		float a = (int) ( (pesoTotalBase + diferencia) * coef * Bustemu.RATE_PORC_FM);
		float b = (int) Math.sqrt(pesoTotalActual + pesoStatActual) + runa;
		if (b < 1)
			b = 1;
		porcentaje = (a / b);
		if (porcentaje < 1)
			porcentaje = 1;
		return porcentaje;
	}
	
	public static int getXPMision(int nivel) {
		if (nivel < 50)
			return 10000;
		if (nivel < 60)
			return 65000;
		if (nivel < 70)
			return 90000;
		if (nivel < 80)
			return 120000;
		if (nivel < 90)
			return 160000;
		if (nivel < 100)
			return 210000;
		if (nivel < 110)
			return 270000;
		if (nivel < 120)
			return 350000;
		if (nivel < 130)
			return 440000;
		if (nivel < 140)
			return 540000;
		if (nivel < 150)
			return 650000;
		if (nivel < 155)
			return 760000;
		if (nivel < 160)
			return 880000;
		if (nivel < 165)
			return 1000000;
		if (nivel < 170)
			return 1130000;
		if (nivel < 175)
			return 1300000;
		if (nivel < 180)
			return 1500000;
		if (nivel < 185)
			return 1800000;
		if (nivel < 190)
			return 2100000;
		if (nivel < 195)
			return 2500000;
		if (nivel < 200)
			return 3200000;
		if (nivel >= 200)
			return 5000000;
		return 0;
	}
}
