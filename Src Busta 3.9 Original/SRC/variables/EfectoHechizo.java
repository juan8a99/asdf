
package variables;

import java.util.ArrayList;
import java.util.TreeMap;

import java.util.Map;
import java.util.Map.Entry;

import variables.Hechizo.StatsHechizos;
import variables.Mapa.Celda;
import variables.MobModelo.MobGrado;
import variables.Pelea.Glifo;
import variables.Pelea.Luchador;
import variables.Pelea.Trampa;
import variables.Personaje.Stats;

import estaticos.Camino;
import estaticos.CentroInfo;
import estaticos.Encriptador;
import estaticos.Formulas;
import estaticos.GestorSalida;
import estaticos.MundoDofus;

public class EfectoHechizo {
	private int _efectoID;
	private int _turnos = 0;
	private String _valores = "0d0+0";
	private int _suerte = 100;
	private String _args;
	private int _valor = 0;
	private Luchador _lanzador = null;
	private int _hechizoID = 0;
	private int _nivelHechizo = 1;
	private boolean _desbuffeable = true;
	private int _duracion = 0;
	private int _turnosOriginales = 0;
	private Celda _celdaLanz = null;
	private boolean _veneno = false;
	
	public EfectoHechizo(int aID, String aArgs, int aHechizo, int aNivelHechizo) {
		_efectoID = aID;
		_args = aArgs;
		_hechizoID = aHechizo;
		_nivelHechizo = aNivelHechizo;
		try {
			String[] args = _args.split(";");
			_valor = Integer.parseInt(args[0]);
			_turnos = Integer.parseInt(args[3]);
			_turnosOriginales = Integer.parseInt(args[3]);
			_suerte = Integer.parseInt(args[4]);
			_valores = args[5];
		} catch (Exception e) {}
	}
	
	public EfectoHechizo(int id, int aValor, int aDuracion, int aTurnos, boolean debuffeable, Luchador aLanzador, String args2,
			int aHechizoID, boolean veneno) {
		_efectoID = id;
		_valor = aValor;
		_turnos = aTurnos;
		_desbuffeable = debuffeable;
		_lanzador = aLanzador;
		_duracion = aDuracion;
		_args = args2;
		_hechizoID = aHechizoID;
		_veneno = veneno;
		try {
			String[] args = _args.split(";");
			_turnosOriginales = Integer.parseInt(args[3]);
			_valores = args[5];
		} catch (Exception e) {}
	}
	private static int[] efectosReto = { 77, 169, 84, 168, 108, 116, 320, 81, 82, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97,
			98, 99, 100, 101 };
	
	private static boolean esEfectoReto(int efecto) {
		for (Integer e : efectosReto) {
			if (e == efecto)
				return true;
		}
		return false;
	}
	
	public boolean esMismoHechizo(int id) {
		return _hechizoID == id;
	}
	
	public int getDuracion() {
		return _duracion;
	}
	
	public int getTurnos() {
		return _turnos;
	}
	
	public boolean esDesbufeable() {
		return _desbuffeable;
	}
	
	public void setTurnos(int aturnos) {
		_turnos = aturnos;
	}
	
	public int getEfectoID() {
		return _efectoID;
	}
	
	public int getValor() {
		return _valor;
	}
	
	public String getValores() {
		return _valores;
	}
	
	public boolean getVenenoso() {
		return _veneno;
	}
	
	public int getSuerte() {
		return _suerte;
	}
	
	public String getArgs() {
		return _args;
	}
	
	public void setArgs(String nuevasArgs) {
		_args = nuevasArgs;
		try {
			String[] args = _args.split(";");
			_valor = Integer.parseInt(args[0]);
			_turnos = Integer.parseInt(args[3]);
			_suerte = Integer.parseInt(args[4]);
			_valores = args[5];
		} catch (Exception e) {}
	}
	
	public void setEfectoID(int id) {
		_efectoID = id;
	}
	
	public void setValor(int v) {
		_valor = v;
	}
	
	public int disminuirDuracion() {
		_duracion--;
		return _duracion;
	}
	
	public void aplicarBuffDeInicioTurno(Pelea pelea, Luchador afectado) {
		ArrayList<Luchador> objetivos = new ArrayList<Luchador>();
		objetivos.add(afectado);
		_turnos = -1;
		aplicarAPelea(pelea, _lanzador, objetivos, false, null);
	}
	
	public void aplicarHechizoAPelea(Pelea pelea, Luchador lanzador, Celda casilla, ArrayList<Luchador> objetivos,
			ArrayList<Celda> celdas) {
		_celdaLanz = casilla;
		aplicarAPelea(pelea, lanzador, objetivos, false, celdas);
	}
	
	public Luchador getLanzador() {
		return _lanzador;
	}
	
	public int getHechizoID() {
		return _hechizoID;
	}
	
	public int getMaxMinHechizo(Luchador objetivo, int valor) {
		int val = valor;
		if (objetivo.tieneBuff(782)) {
			int max = Integer.parseInt(_args.split(";")[1]);
			if (max == -1)
				max = Integer.parseInt(_args.split(";")[0]);
			valor = max;
		}
		if (objetivo.tieneBuff(781))
			valor = Integer.parseInt(_args.split(";")[0]);
		return val;
	}
	
	public static ArrayList<Luchador> getAfectados(Pelea pelea, ArrayList<Celda> celdas, int hechizo) {
		ArrayList<Luchador> objetivos = new ArrayList<Luchador>();
		if (hechizo == 418) {
			int i = 4;
			ArrayList<Celda> celdas1 = new ArrayList<Celda>();
			ArrayList<Celda> celdas2 = new ArrayList<Celda>();
			ArrayList<Celda> celdas3 = new ArrayList<Celda>();
			ArrayList<Celda> celdas4 = new ArrayList<Celda>();
			for (Celda celda : celdas) {
				if ( (i % 4) == 0)
					celdas1.add(celda);
				else if ( (i % 4) == 1)
					celdas2.add(celda);
				else if ( (i % 4) == 2)
					celdas3.add(celda);
				else
					celdas4.add(celda);
				i++;
			}
			celdas.clear();
			celdas.addAll(celdas4);
			celdas.addAll(celdas3);
			celdas.addAll(celdas2);
			celdas.addAll(celdas1);
		} else if (hechizo == 165) {
			ArrayList<Celda> celdas1 = new ArrayList<Celda>();
			int j = celdas.size() - 1;
			while (j >= 0) {
				celdas1.add(celdas.get(j));
				j--;
			}
			celdas.clear();
			celdas.addAll(celdas1);
		}
		for (Celda celda : celdas) {
			if (celda == null)
				continue;
			Luchador f = celda.getPrimerLuchador();
			if (f == null)
				continue;
			objetivos.add(f);
		}
		return objetivos;
	}
	
	public static int aplicarBuffContraGolpe(int dañoFinal, Luchador objetivo, Luchador lanzador, Pelea pelea, int hechizo) {
		for (int id : CentroInfo.BUFF_ACCION_RESPUESTA) {
			for (EfectoHechizo buff : objetivo.getBuffsPorEfectoID(id)) {
				if (objetivo.estaMuerto())
					return 0;
				switch (id) {
					case 9:// retrocede al ser golpeado
						int d = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), objetivo.getCeldaPelea().getID(), lanzador
								.getCeldaPelea().getID());
						if (d > 1)
							continue;
						int elusion = buff.getValor();
						int azar = Formulas.getRandomValor(1, 100);
						if (azar > elusion)
							continue;
						int nroCasillas = 0;
						try {
							nroCasillas = Integer.parseInt(buff.getArgs().split(";")[1]);
						} catch (Exception e) {}
						if (nroCasillas == 0 || objetivo.tieneEstado(6))
							continue;
						Celda aCelda = lanzador.getCeldaPelea();
						Luchador afectado = null;
						Mapa mapaCopia = pelea.getMapaCopia();
						int nuevaCeldaID = Camino.getCeldaDespEmpujon(mapaCopia, aCelda, objetivo.getCeldaPelea(), nroCasillas,
								pelea, objetivo);
						if (nuevaCeldaID == 0)
							continue;
						dañoFinal = 0;
						if (nuevaCeldaID < 0) {
							int a = -nuevaCeldaID;
							int coef = Formulas.getRandomValor("1d5+8");
							float b = (lanzador.getNivel() / (float) (100.00));
							if (b < 0.1)
								b = 0.1f;
							float c = b * a;
							dañoFinal = (int) (coef * c);
							if (dañoFinal < 1)
								dañoFinal = 1;
							if (dañoFinal > objetivo.getPDVConBuff())
								dañoFinal = objetivo.getPDVConBuff();
							if (dañoFinal > 0) {
								objetivo.restarPDV(dañoFinal);
								if (objetivo.getPDVConBuff() <= 0) {
									GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID() + "", objetivo.getID()
											+ ",-" + dañoFinal);
									pelea.agregarAMuertos(objetivo);
									continue;
								}
							}
							a = nroCasillas - a;
							nuevaCeldaID = Camino.getCeldaDespEmpujon(mapaCopia, aCelda, objetivo.getCeldaPelea(), a, pelea,
									objetivo);
							char dir = Camino.getDirEntreDosCeldas(aCelda.getID(), objetivo.getCeldaPelea().getID(), mapaCopia,
									true);
							int celdaSigID = 0;
							if (nuevaCeldaID == 0)
								celdaSigID = Camino.getSigIDCeldaMismaDir(objetivo.getCeldaPelea().getID(), dir, mapaCopia, true);
							else
								celdaSigID = Camino.getSigIDCeldaMismaDir(nuevaCeldaID, dir, mapaCopia, true);
							Celda celdaSig = mapaCopia.getCelda(celdaSigID);
							if (celdaSig != null) {
								afectado = celdaSig.getPrimerLuchador();
							}
						}
						if (nuevaCeldaID != 0) {
							Celda nueva = mapaCopia.getCelda(nuevaCeldaID);
							if (nueva != null) {
								objetivo.getCeldaPelea().getLuchadores().clear();
								objetivo.setCeldaPelea(nueva);
								nueva.addLuchador(objetivo);
								GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 5, lanzador.getID() + "", objetivo.getID() + ","
										+ nuevaCeldaID);
								try {
									Thread.sleep(500);
								} catch (Exception e) {}
							}
						}
						if (dañoFinal > 0)
							GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID() + "", objetivo.getID() + ",-"
									+ dañoFinal);
						if (afectado != null) {
							int dañoFinal2 = (dañoFinal / 2);
							if (dañoFinal2 < 1)
								dañoFinal2 = 1;
							if (dañoFinal2 > afectado.getPDVConBuff())
								dañoFinal2 = afectado.getPDVConBuff();
							if (dañoFinal2 > 0) {
								afectado.restarPDV(dañoFinal2);
								GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID() + "", afectado.getID() + ",-"
										+ dañoFinal2);
								if (afectado.getPDVConBuff() <= 0) {
									pelea.agregarAMuertos(afectado);
								}
							}
						}
						try {
							Thread.sleep(300);
						} catch (Exception e) {}
						for (Trampa trampa : pelea.getTrampas()) {
							int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(),
									nuevaCeldaID);
							if (dist <= trampa.getTamaño()) {
								trampa.activaTrampa(objetivo);
								break;
							}
						}
						return 0;
					case 79:// Suerte de zurcarak
						try {
							String[] infos = buff.getArgs().split(";");
							int coefDaño = Integer.parseInt(infos[0]);
							int coefCura = Integer.parseInt(infos[1]);
							int suerte = Integer.parseInt(infos[2]);
							int jet = Formulas.getRandomValor(0, 99);
							if (jet < suerte)// Cura
							{
								dañoFinal = - (dañoFinal * coefCura);
								if (-dañoFinal > (objetivo.getPDVMaxConBuff() - objetivo.getPDVConBuff()))
									dañoFinal = - (objetivo.getPDVMaxConBuff() - objetivo.getPDVConBuff());
							} else
								dañoFinal = dañoFinal * coefDaño;
						} catch (Exception e) {}
						break;
					case 220:// reenvio de daño
					case 107:// reenvio de daño
						switch (hechizo) {
							case 66:
							case 71:
							case 181:
							case 196:
							case 200:
							case 219:
							case 164:
								continue;
						}
						if (buff.getVenenoso())
							continue;
						String[] args = buff.getArgs().split(";");
						float coef = 1 + (objetivo.getTotalStatsConBuff().getEfecto(124) / 100);
						int reenvio = 0;
						try {
							if (Integer.parseInt(args[1]) != -1) {
								reenvio = (int) (coef * Formulas.getRandomValor(Integer.parseInt(args[0]),
										Integer.parseInt(args[1])));
							} else {
								reenvio = (int) (coef * Integer.parseInt(args[0]));
							}
						} catch (Exception e) {
							continue;
						}
						if (reenvio > dañoFinal)
							reenvio = dañoFinal;
						dañoFinal -= reenvio;
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 107, "-1", objetivo.getID() + "," + reenvio);
						if (reenvio > lanzador.getPDVConBuff())
							reenvio = lanzador.getPDVConBuff();
						if (dañoFinal < 0)
							dañoFinal = 0;
						lanzador.restarPDV(reenvio);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.getID() + "", lanzador.getID() + ",-"
								+ reenvio);
						break;
					case 776:
						if (objetivo.tieneBuff(776)) {// si posee daños incurables
							int pdvMax = objetivo.getPDVMax();
							float pdaño = objetivo.getValorBuffPelea(776) / 100.00f;
							pdvMax = pdvMax - (int) (dañoFinal * pdaño);
							if (pdvMax < 0)
								pdvMax = 0;
							objetivo.setPDVMAX(pdvMax);
						}
						break;
					case 788:// Castigos
						int porc = (lanzador.getPersonaje() == null ? 1 : 2);
						int gana = (dañoFinal / porc);
						int stat = buff.getValor();
						int max = 0;
						try {
							max = Integer.parseInt(buff.getArgs().split(";")[1]);
						} catch (Exception e) {}
						max = max - objetivo.getBonusCastigo(stat);
						if (max <= 0)
							continue;
						if (gana > max)
							gana = max;
						objetivo.setBonusCastigo(objetivo.getBonusCastigo(stat) + gana, stat);
						objetivo.addBuff(stat, gana, 5, 1, false, buff.getHechizoID(), buff.getArgs(), lanzador,
								buff.getVenenoso());
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, stat, lanzador.getID() + "", objetivo.getID() + "," + gana
								+ "," + 5);
						break;
					default:
						System.out.println("Efecto id " + id + " no definido como ON HIT BUFF.");
						break;
				}
			}
		}
		return dañoFinal;
	}
	
	public void aplicarAPelea(Pelea pelea, Luchador aLanzador, ArrayList<Luchador> objetivos, boolean esCaC,
			ArrayList<Celda> celdas) {
		try {
			if (_turnos != -1)
				_turnos = Integer.parseInt(_args.split(";")[3]);
		} catch (NumberFormatException e) {}
		_lanzador = aLanzador;
		try {
			_valores = _args.split(";")[5];
		} catch (Exception e) {}
		if (_lanzador.getPersonaje() != null) {
			Personaje perso = _lanzador.getPersonaje();
			if (perso.getHechizosSetClase().containsKey(_hechizoID)) {
				int modi = 0;
				if (_efectoID == 108)
					modi = perso.getModifSetClase(_hechizoID, 284);
				else if (_efectoID >= 91 && _efectoID <= 100)
					modi = perso.getModifSetClase(_hechizoID, 283);
				String jeta = _valores.split("\\+")[0];
				int bonus = Integer.parseInt(_valores.split("\\+")[1]) + modi;
				_valores = jeta + "+" + bonus;
			}
		}
		pelea.setUltAfec(objetivos.size());
		if (pelea.getTipoPelea() == 4 && _lanzador.getPersonaje() != null && esEfectoReto(_efectoID)) {
			Map<Integer, Integer> copiaRetos = new TreeMap<Integer, Integer>();
			copiaRetos.putAll(pelea.getRetos());
			for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
				int reto = entry.getKey();
				int exitoReto = entry.getValue();
				if (exitoReto != 0)
					continue;
				switch (reto) {
					case 21: // circulen
						if (_efectoID == 77 || _efectoID == 169) {
							for (Luchador luch : pelea._inicioLucEquipo2) {
								if (objetivos.contains(luch)) {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
									exitoReto = 2;
									break;
								}
							}
						}
						break;
					case 22: // el tiempo pasa
						if (_efectoID == 84 || _efectoID == 101 || _efectoID == 168) {
							for (Luchador luch : pelea._inicioLucEquipo2) {
								if (objetivos.contains(luch)) {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
									exitoReto = 2;
									break;
								}
							}
						}
						break;
					case 23: // perdido de vista
						if (_efectoID == 116 || _efectoID == 320) {
							for (Luchador luch : pelea._inicioLucEquipo2) {
								if (objetivos.contains(luch)) {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
									exitoReto = 2;
									break;
								}
							}
						}
						break;
					case 31: // focalizacion
						if (pelea.getIDMobReto() != 0) {
							for (Luchador luch : objetivos) {
								if (!pelea._inicioLucEquipo2.contains(luch))
									continue;
								if (luch.getID() != pelea.getIDMobReto()) {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
									exitoReto = 2;
									break;
								}
							}
						}
						break;
					case 32: // elitisa
						if (pelea.getIDMobReto() != 0) {
							for (Luchador luch : objetivos) {
								if (!pelea._inicioLucEquipo2.contains(luch))
									continue;
								if (luch.getID() != pelea.getIDMobReto()) {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
									exitoReto = 2;
									break;
								}
							}
						}
						break;
					case 34: // imprevisible
						if ( (_efectoID >= 91 && _efectoID <= 100) || (_efectoID >= 85 && _efectoID <= 89)) {
							if (pelea.getIDMobReto() != 0) {
								for (Luchador luch : objetivos) {
									if (!pelea._inicioLucEquipo2.contains(luch))
										continue;
									if (luch.getID() != pelea.getIDMobReto()) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
										exitoReto = 2;
										break;
									}
								}
							}
						}
						break;
					case 43: // abnegacion
						if (_efectoID == 108) {
							for (Luchador luch : objetivos) {
								if (luch.getID() == _lanzador.getID()) {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
									exitoReto = 2;
									break;
								}
							}
						}
						break;
					case 45: // duelo
					case 46: // cada uno con su monstruo
						if ( (_efectoID >= 91 && _efectoID <= 100) || (_efectoID >= 85 && _efectoID <= 89)) {
							for (Luchador luch : objetivos) {
								if (!pelea._inicioLucEquipo2.contains(luch))
									continue;
								if (luch.getPjAtacante() == 0) {
									luch.setPjAtacante(_lanzador.getID());
								} else {
									if (luch.getPjAtacante() != _lanzador.getID()) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(pelea, reto);
										exitoReto = 2;
										break;
									}
								}
							}
						}
						break;
				}
				if (exitoReto != 0) {
					pelea.getRetos().remove(reto);
					pelea.getRetos().put(reto, exitoReto);
				}
			}
		}
		switch (_efectoID) {
		// FIXME
			case 4:// Teletransporta a una casilla Huida/Salto felino/ Salto / Teleport
				aplicarEfecto_4(pelea);
				break;
			case 5:// Hace retroceder X casillas
				aplicarEfecto_5(objetivos, pelea);
				break;
			case 6:// Hace avanzar X casillas
				aplicarEfecto_6(objetivos, pelea);
				break;
			case 8:// Intercambia la posicion de 2 jugadores
				aplicarEfecto_8(objetivos, pelea);
				break;
			case 9:// Esquiva un X% del ataque haciendolo retroceder Y casillas
				aplicarEfecto_9(objetivos, pelea);
				break;
			case 50:// Permite levantar un jugador
				aplicarEfecto_50(pelea);
				break;
			case 51:// Lanzar el jugador levantado
				aplicarEfecto_51(pelea);
				break;
			case 77:// Robar PM
				aplicarEfecto_77(objetivos, pelea);
				break;
			case 78:// + PM
				aplicarEfecto_78(objetivos, pelea);
				break;
			case 79:// + X % de posibilidades de que sufras daños x X, o de que te cure x Y
				aplicarEfecto_79(objetivos, pelea);
				break;
			case 81:// Cura, PDV devueltos
				aplicarEfecto_81(objetivos, pelea);
				break;
			case 82:// Robar Vida(fijo)
				aplicarEfecto_82(objetivos, pelea);
				break;
			case 84:// Robar PA
				aplicarEfecto_84(objetivos, pelea);
				break;
			case 85:// Daños Agua %vida del atacante
				aplicarEfecto_85(objetivos, pelea);
				break;
			case 86:// Daños Tierra %vida del atacante
				aplicarEfecto_86(objetivos, pelea);
				break;
			case 87:// Daños Aire %vida del atacante
				aplicarEfecto_87(objetivos, pelea);
				break;
			case 88:// Daños Fuego %vida del atacante
				aplicarEfecto_88(objetivos, pelea);
				break;
			case 89:// Daños Neutral %vida del atacante
				aplicarEfecto_89(objetivos, pelea);
				break;
			case 90:// Dona % de su vida
				aplicarEfecto_90(objetivos, pelea);
				break;
			case 91:// Robar Vida(agua)
				aplicarEfecto_91(objetivos, pelea, esCaC);
				break;
			case 92:// Robar Vida(tierra)
				aplicarEfecto_92(objetivos, pelea, esCaC);
				break;
			case 93:// Robar Vida(aire)
				aplicarEfecto_93(objetivos, pelea, esCaC);
				break;
			case 94:// Robar Vida(fuego)
				aplicarEfecto_94(objetivos, pelea, esCaC);
				break;
			case 95:// Robar Vida(neutral)
				aplicarEfecto_95(objetivos, pelea, esCaC);
				break;
			case 96:// Daños Agua
				aplicarEfecto_96(objetivos, pelea, esCaC);
				break;
			case 97:// Daños Tierra
				aplicarEfecto_97(objetivos, pelea, esCaC);
				break;
			case 98:// Daños Aire
				aplicarEfecto_98(objetivos, pelea, esCaC);
				break;
			case 99:// Daños Fuego
				aplicarEfecto_99(objetivos, pelea, esCaC);
				break;
			case 100:// Daños Neutral
				aplicarEfecto_100(objetivos, pelea, esCaC);
				break;
			case 101:// - PA
				aplicarEfecto_101(objetivos, pelea);
				break;
			case 105:// Daños reducidos a X
				aplicarEfecto_105(objetivos, pelea);
				break;
			case 106:// Reenvia un hechizo de nivel
				aplicarEfecto_106(objetivos, pelea);
				break;
			case 107:// Reevnia daños, Daños devueltos
				aplicarEfecto_107(objetivos, pelea);
				break;
			case 108:// Cura, PDV devueltos
				aplicarEfecto_108(objetivos, pelea, esCaC);
				break;
			case 109:// Daños para el lanzador
				aplicarEfecto_109(pelea);
				break;
			case 110:// + X vida
				aplicarEfecto_110(objetivos, pelea);
				break;
			case 111:// + X PA
				aplicarEfecto_111(objetivos, pelea);
				break;
			case 112:// + Daños
				aplicarEfecto_112(objetivos, pelea);
				break;
			case 114:// Multiplica los daños por X
				aplicarEfecto_114(objetivos, pelea);
				break;
			case 115:// + Golpes Criticos
				aplicarEfecto_115(objetivos, pelea);
				break;
			case 116:// - Alcance
				aplicarEfecto_116(objetivos, pelea);
				break;
			case 117:// + Alcance
				aplicarEfecto_117(objetivos, pelea);
				break;
			case 118:// + Fuerza
				aplicarEfecto_118(objetivos, pelea);
				break;
			case 119:// + Agilidad
				aplicarEfecto_119(objetivos, pelea);
				break;
			case 120:// Añade X PA
				aplicarEfecto_120(pelea);
				break;
			case 121:// + Daños
				aplicarEfecto_121(objetivos, pelea);
				break;
			case 122:// + Fallos Criticos
				aplicarEfecto_122(objetivos, pelea);
				break;
			case 123:// + Suerte
				aplicarEfecto_123(objetivos, pelea);
				break;
			case 124:// + Sabiduria
				aplicarEfecto_124(objetivos, pelea);
				break;
			case 125:// + Vitalidad
				aplicarEfecto_125(objetivos, pelea);
				break;
			case 126:// + Inteligencia
				aplicarEfecto_126(objetivos, pelea);
				break;
			case 127:// Pierde X PM
				aplicarEfecto_127(objetivos, pelea);
				break;
			case 128:// + PM
				aplicarEfecto_128(objetivos, pelea);
				break;
			case 130:// robo kamas
				aplicarEfecto_130(objetivos, pelea);
				break;
			case 131:// Veneno : X Pdv por PA
				aplicarEfecto_131(objetivos, pelea);
				break;
			case 132:// Desechiza
				aplicarEfecto_132(objetivos, pelea);
				break;
			case 138:// + % daños
				aplicarEfecto_138(objetivos, pelea);
				break;
			case 140:// Pasar el turno
				aplicarEfecto_140(objetivos, pelea);
				break;
			case 141:// Mata al blanco
				aplicarEfecto_141(objetivos, pelea);
				break;
			case 142:// + Daños Fisicos
				aplicarEfecto_142(objetivos, pelea);
				break;
			case 143:// PDV devueltos para castigo
				aplicarEfecto_143(objetivos, pelea);
				break;
			case 144:// - Daños (no boosteados)
				aplicarEfecto_144(objetivos, pelea);
			case 145:// - Daños
				aplicarEfecto_145(objetivos, pelea);
				break;
			case 149:// Cambia la apariencia
				aplicarEfecto_149(objetivos, pelea);
				break;
			case 150:// Invisible
				aplicarEfecto_150(objetivos, pelea);
				break;
			case 152:// - Suerte
				aplicarEfecto_152(objetivos, pelea);
				break;
			case 153:// - Vitalidad
				aplicarEfecto_153(objetivos, pelea);
				break;
			case 154:// - Agilidad
				aplicarEfecto_154(objetivos, pelea);
				break;
			case 155:// - Inteligencia
				aplicarEfecto_155(objetivos, pelea);
				break;
			case 156:// - Sabiduria
				aplicarEfecto_156(objetivos, pelea);
				break;
			case 157:// - Fuerza
				aplicarEfecto_157(objetivos, pelea);
				break;
			case 160:// + Esquiva PA
				aplicarEfecto_160(objetivos, pelea);
				break;
			case 161:// + Esquiva PM
				aplicarEfecto_161(objetivos, pelea);
				break;
			case 162:// - Esquiva PA
				aplicarEfecto_162(objetivos, pelea);
				break;
			case 163:// - Esquiva PM
				aplicarEfecto_163(objetivos, pelea);
				break;
			case 164:// Daños reducidos en x%
				aplicarEfecto_164(objetivos, pelea);
				break;
			case 165:// Aumenta los daños %
				aplicarEfecto_165(objetivos, pelea);
				break;
			case 168:// - PA , no esquivable
				aplicarEfecto_168(objetivos, pelea);
				break;
			case 169:// - PM , no esquivable
				aplicarEfecto_169(objetivos, pelea);
				break;
			case 171:// - Golpes Criticos
				aplicarEfecto_171(objetivos, pelea);
				break;
			case 176:// + a las prospecciones
				aplicarEfecto_176(objetivos, pelea);
				break;
			case 177:// - a las prospecciones
				aplicarEfecto_177(objetivos, pelea);
				break;
			case 178:// + a las curaciones
				aplicarEfecto_178(objetivos, pelea);
				break;
			case 179:// - a las curaciones
				aplicarEfecto_179(objetivos, pelea);
				break;
			case 180:// Doble de sram
				aplicarEfecto_180(pelea);
				break;
			case 181:// Invoca una criatura
				aplicarEfecto_181(pelea);
				break;
			case 182:// + Invocaciones
				aplicarEfecto_182(objetivos, pelea);
				break;
			case 183:// Reduccion Magica
				aplicarEfecto_183(objetivos, pelea);
				break;
			case 184:// Reduccion Fisica
				aplicarEfecto_184(objetivos, pelea);
				break;
			case 185:// Invoca una criatura estatica
				aplicarEfecto_185(pelea);
				break;
			case 186:// Disminuye los daños
				aplicarEfecto_186(objetivos, pelea);
				break;
			case 202:// Revela todos los objetos invisibles
				aplicarEfecto_202(objetivos, pelea, celdas);
				break;
			case 210:// Resist % tierra
				aplicarEfecto_210(objetivos, pelea);
				break;
			case 211:// Resist % agua
				aplicarEfecto_211(objetivos, pelea);
				break;
			case 212:// Resist % aire
				aplicarEfecto_212(objetivos, pelea);
				break;
			case 213:// Resist % fuego
				aplicarEfecto_213(objetivos, pelea);
				break;
			case 214:// Resist % neutral
				aplicarEfecto_214(objetivos, pelea);
				break;
			case 215:// Debilidad % tierra
				aplicarEfecto_215(objetivos, pelea);
				break;
			case 216:// Debilidad % agua
				aplicarEfecto_216(objetivos, pelea);
				break;
			case 217:// Debilidad % aire
				aplicarEfecto_217(objetivos, pelea);
				break;
			case 218:// Debilidad % fuego
				aplicarEfecto_218(objetivos, pelea);
				break;
			case 219:// Debilidad % neutral
				aplicarEfecto_219(objetivos, pelea);
				break;
			case 220:// Reevnia daños, Daños devueltos
				aplicarEfecto_220(objetivos, pelea);
				break;
			case 265:// Daños reducidos a X
				aplicarEfecto_265(objetivos, pelea);
				break;
			case 266:// Robar Suerte
				aplicarEfecto_266(objetivos, pelea);
				break;
			case 267:// Robar Vitalidad
				aplicarEfecto_267(objetivos, pelea);
				break;
			case 268:// Robar agilidad
				aplicarEfecto_268(objetivos, pelea);
				break;
			case 269:// Robar inteligencia
				aplicarEfecto_269(objetivos, pelea);
				break;
			case 270:// Robar sabiduria
				aplicarEfecto_270(objetivos, pelea);
				break;
			case 271:// Robar fuerza
				aplicarEfecto_271(objetivos, pelea);
				break;
			case 275:// Daños Agua %vida del atacante
				aplicarEfecto_275(objetivos, pelea);
				break;
			case 276:// Daños Tierra %vida del atacante
				aplicarEfecto_276(objetivos, pelea);
				break;
			case 277:// Daños Aire %vida del atacante
				aplicarEfecto_277(objetivos, pelea);
				break;
			case 278:// Daños Fuego %vida del atacante
				aplicarEfecto_278(objetivos, pelea);
				break;
			case 279:// Daños Neutral %vida del atacante
				aplicarEfecto_279(objetivos, pelea);
				break;
			case 293:// Aumenta los daños de base del hechizo X de Y
				aplicarEfecto_293(pelea);
				break;
			case 320:// Robar Alcance
				aplicarEfecto_320(objetivos, pelea);
				break;
			case 400:// Crea una trampa
				aplicarEfecto_400(pelea);
				break;
			case 401:// Crea un glifo de nivel
				aplicarEfecto_401(pelea);
				break;
			case 402:// Glifo de blops Crea un glifo de nivel
				aplicarEfecto_402(pelea);
				break;
			case 666:// Paso de effecto complementario
				break;
			case 671: // Daños : X% de la vida del atacante (neutre)
				aplicarEfecto_671(objetivos, pelea);
				break;
			case 672:// Daños : X% de la vida del atacante (neutre)
				aplicarEfecto_672(objetivos, pelea);
				break;
			case 750:// bonus de captura
				aplicarEfecto_750(objetivos, pelea);
				break;
			case 765:// Interacambia la posicion de 2 jugadores aliados (sacrificio)
				aplicarEfecto_765(objetivos, pelea);
				break;
			case 776:// +% de los daños incurables sufridos
				aplicarEfecto_776(objetivos, pelea);
				break;
			case 780:// lazo espiritual (invoca a un aliado muerto en combate)
				aplicarEfecto_780(pelea);
				break;
			case 782:// Maximiza los efectos aleatorios
				aplicarEfecto_782(objetivos, pelea);
				break;
			case 781:// Minimiza los efectos aleatorios
				aplicarEfecto_781(objetivos, pelea);
				break;
			case 783:// Hace retroceder hasta la casilla objetivo
				aplicarEfecto_783(pelea);
				break;
			case 784:// teleporta al punto de inicio
				aplicarEfecto_784(objetivos, pelea);
				break;
			case 786:// Cura durante el ataque
				aplicarEfecto_786(objetivos, pelea);
				break;
			case 787:// Cura durante el ataque
				aplicarEfecto_787(objetivos, pelea);
				break;
			case 788:// Castigo X durante Y turnos
				aplicarEfecto_788(objetivos, pelea);
				break;
			case 950:// Estado X
				aplicarEfecto_950(objetivos, pelea);
				break;
			case 951:// Sale del Estado X
				aplicarEfecto_951(objetivos, pelea);
				break;
			default:
				System.out.println("efecto no implantado : " + _efectoID + " formula: " + _args);
				break;
		}
	}
	
	private void aplicarEfecto_4(Pelea pelea) {// teletransporta
		if (_turnos > 1)
			return;
		if (_celdaLanz.esCaminable(true) && !pelea.celdaOcupada(_celdaLanz.getID())) {
			_lanzador.getCeldaPelea().removerLuchador(_lanzador);
			_lanzador.setCeldaPelea(_celdaLanz);
			_lanzador.getCeldaPelea().addLuchador(_lanzador);
			ArrayList<Trampa> trampas = (new ArrayList<Trampa>());
			trampas.addAll(pelea.getTrampas());
			for (Trampa trampa : trampas) {
				int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), _lanzador
						.getCeldaPelea().getID());
				if (dist <= trampa.getTamaño())
					trampa.activaTrampa(_lanzador);
			}
			GestorSalida
					.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, _lanzador.getID() + "", _lanzador.getID() + "," + _celdaLanz.getID());
		} else {
			if (_lanzador.getPersonaje() != null)
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_lanzador.getPersonaje(),
						"La celda a donde se quiere transportar esta ocupada.");
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_5(ArrayList<Luchador> objetivos, Pelea pelea) {// hace retroceder X casillas
		if (objetivos.size() == 1 && _hechizoID == 120) {// destino de zurcarak
			if (objetivos.get(0).tieneEstado(6))
				return;
			if (!objetivos.get(0).estaMuerto()) {
				_lanzador.setObjetivoDestZurca(objetivos.get(0));
			}
		}
		Mapa mapaCopia = pelea.getMapaCopia();
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto() || objetivo.tieneEstado(6))
					continue;
				Celda celdaLanz = _celdaLanz;
				int dañoFinal = 0;
				Luchador afectado = null;
				if (_hechizoID == 1688 || _hechizoID == 368 || _hechizoID == 2028 || _hechizoID == 151 || _hechizoID == 1688) {
					if (objetivo.getCeldaPelea().getID() == _celdaLanz.getID())
						continue;
				} else {
					if (objetivo.getCeldaPelea().getID() == _celdaLanz.getID()) {
						celdaLanz = _lanzador.getCeldaPelea();
					}
				}
				int nuevaCeldaID = Camino.getCeldaDespEmpujon(mapaCopia, celdaLanz, objetivo.getCeldaPelea(), _valor, pelea,
						objetivo);
				if (nuevaCeldaID == 0)
					continue;
				if (nuevaCeldaID < 0) {
					int a = -nuevaCeldaID;
					int coef = Formulas.getRandomValor("1d5+8");
					float b = (_lanzador.getNivel() / (float) (100.00));
					if (b < 0.1)
						b = 0.1f;
					float c = b * a;
					dañoFinal = (int) (coef * c);
					if (dañoFinal < 1)
						dañoFinal = 1;
					if (dañoFinal > objetivo.getPDVConBuff())
						dañoFinal = objetivo.getPDVConBuff();
					if (dañoFinal > 0) {
						objetivo.restarPDV(dañoFinal);
						if (objetivo.getPDVConBuff() <= 0) {
							GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + ",-"
									+ dañoFinal);
							pelea.agregarAMuertos(objetivo);
							continue;
						}
					}
					a = _valor - a;
					nuevaCeldaID = Camino.getCeldaDespEmpujon(mapaCopia, celdaLanz, objetivo.getCeldaPelea(), a, pelea, objetivo);
					char dir = Camino.getDirEntreDosCeldas(celdaLanz.getID(), objetivo.getCeldaPelea().getID(), mapaCopia, true);
					int celdaSigID = 0;
					if (nuevaCeldaID == 0)
						celdaSigID = Camino.getSigIDCeldaMismaDir(objetivo.getCeldaPelea().getID(), dir, mapaCopia, true);
					else
						celdaSigID = Camino.getSigIDCeldaMismaDir(nuevaCeldaID, dir, mapaCopia, true);
					Celda celdaSig = mapaCopia.getCelda(celdaSigID);
					if (celdaSig != null) {
						afectado = celdaSig.getPrimerLuchador();
					}
				}
				if (nuevaCeldaID != 0) {
					Celda nueva = mapaCopia.getCelda(nuevaCeldaID);
					if (nueva != null) {
						objetivo.getCeldaPelea().getLuchadores().clear();
						objetivo.setCeldaPelea(nueva);
						nueva.addLuchador(objetivo);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 5, _lanzador.getID() + "", objetivo.getID() + ","
								+ nuevaCeldaID);
						try {
							Thread.sleep(500);
						} catch (Exception e) {}
					}
				}
				if (dañoFinal > 0)
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + ",-"
							+ dañoFinal);
				if (afectado != null) {
					int dañoFinal2 = (dañoFinal / 2);
					if (dañoFinal2 < 1)
						dañoFinal2 = 1;
					if (dañoFinal2 > afectado.getPDVConBuff())
						dañoFinal2 = afectado.getPDVConBuff();
					if (dañoFinal2 > 0) {
						afectado.restarPDV(dañoFinal2);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", afectado.getID() + ",-"
								+ dañoFinal2);
						if (afectado.getPDVConBuff() <= 0) {
							pelea.agregarAMuertos(afectado);
						}
					}
				}
				try {
					Thread.sleep(300);
				} catch (Exception e) {}
				for (Trampa trampa : pelea.getTrampas()) {
					int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), nuevaCeldaID);
					if (dist <= trampa.getTamaño()) {
						trampa.activaTrampa(objetivo);
						break;
					}
				}
				try {
					Thread.sleep(300);
				} catch (Exception e) {}
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_6(ArrayList<Luchador> objetivos, Pelea pelea) { // hace avanzar X casillas
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneEstado(6))
					continue;
				Celda eCelda = _celdaLanz;
				if (objetivo.getCeldaPelea().getID() == _celdaLanz.getID()) {
					eCelda = _lanzador.getCeldaPelea();
				}
				int nuevaCeldaID = Camino.getNuevaCeldaDespuesGolpe(pelea.getMapaCopia(), eCelda, objetivo.getCeldaPelea(),
						-_valor, pelea, objetivo);
				if (nuevaCeldaID == 0)
					continue;
				if (nuevaCeldaID < 0) {
					int a = - (_valor + nuevaCeldaID);
					nuevaCeldaID = Camino.getNuevaCeldaDespuesGolpe(pelea.getMapaCopia(), _lanzador.getCeldaPelea(),
							objetivo.getCeldaPelea(), a, pelea, objetivo);
					if (nuevaCeldaID == 0)
						continue;
					if (pelea.getMapaCopia().getCelda(nuevaCeldaID) == null)
						continue;
				}
				objetivo.getCeldaPelea().getLuchadores().clear();
				objetivo.setCeldaPelea(pelea.getMapaCopia().getCelda(nuevaCeldaID));
				objetivo.getCeldaPelea().addLuchador(objetivo);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 5, _lanzador.getID() + "", objetivo.getID() + "," + nuevaCeldaID);
				try {
					Thread.sleep(300);
				} catch (Exception e) {}
				for (Trampa trampa : pelea.getTrampas()) {
					int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), nuevaCeldaID);
					if (dist <= trampa.getTamaño()) {
						trampa.activaTrampa(objetivo);
						break;
					}
				}
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_8(ArrayList<Luchador> objetivos, Pelea pelea) {// intercambia la posicion de 2 jugadores
		if (objetivos.isEmpty())
			return;
		Luchador objetivo = objetivos.get(0);
		if (objetivo == null || objetivo.estaMuerto() || objetivo.tieneEstado(6))
			return;
		switch (_hechizoID) {
			case 438:// Transposicion
				if (objetivo.getEquipoBin() != _lanzador.getEquipoBin())
					return;
				break;
			case 445:// Cooperacion
				if (objetivo.getEquipoBin() == _lanzador.getEquipoBin())
					return;
				break;
		}
		Celda exCeldaObjetivo = objetivo.getCeldaPelea();
		Celda exCeldaLanzador = _lanzador.getCeldaPelea();
		exCeldaObjetivo.getLuchadores().clear();
		exCeldaLanzador.getLuchadores().clear();
		objetivo.setCeldaPelea(exCeldaLanzador);
		_lanzador.setCeldaPelea(exCeldaObjetivo);
		exCeldaLanzador.addLuchador(objetivo);
		exCeldaObjetivo.addLuchador(_lanzador);
		ArrayList<Trampa> trampas = (new ArrayList<Trampa>());
		trampas.addAll(pelea.getTrampas());
		for (Trampa trampa : trampas) {
			int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), objetivo.getCeldaPelea()
					.getID());
			int dist2 = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), _lanzador.getCeldaPelea()
					.getID());
			if (dist <= trampa.getTamaño())
				trampa.activaTrampa(objetivo);
			else if (dist2 <= trampa.getTamaño())
				trampa.activaTrampa(_lanzador);
		}
		GestorSalida
				.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, _lanzador.getID() + "", objetivo.getID() + "," + exCeldaLanzador.getID());
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, _lanzador.getID() + "",
				_lanzador.getID() + "," + exCeldaObjetivo.getID());
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_9(ArrayList<Luchador> objetivos, Pelea pelea) {// esquiva golpes retrocediendo casillas
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(_efectoID, _valor, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_50(Pelea pelea) {// permite levantar a un jugador
		Luchador objetivo = _celdaLanz.getPrimerLuchador();
		if (objetivo == null)
			return;
		if (objetivo.estaMuerto())
			return;
		objetivo.getCeldaPelea().getLuchadores().clear();
		objetivo.setCeldaPelea(_lanzador.getCeldaPelea());
		objetivo.setEstado(CentroInfo.ESTADO_TRANSPORTADO, -1);
		_lanzador.setEstado(CentroInfo.ESTADO_PORTADOR, -1);
		objetivo.setTransportadoPor(_lanzador);
		_lanzador.setTransportado(objetivo);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, objetivo.getID() + "", objetivo.getID() + ","
				+ CentroInfo.ESTADO_TRANSPORTADO + ",1");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "", _lanzador.getID() + ","
				+ CentroInfo.ESTADO_PORTADOR + ",1");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 50, _lanzador.getID() + "", "" + objetivo.getID());
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_51(Pelea pelea) {// lanza a un jugador
		if (!_celdaLanz.esCaminable(true) || _celdaLanz.getLuchadores().size() > 0)
			return;
		Luchador objetivo = _lanzador.getTransportando();
		if (objetivo == null)
			return;
		if (objetivo.estaMuerto())
			return;
		Celda celdaLanz = _lanzador.getCeldaPelea();
		celdaLanz.removerLuchador(objetivo);
		objetivo.setCeldaPelea(_celdaLanz);
		objetivo.getCeldaPelea().addLuchador(objetivo);
		objetivo.setEstado(CentroInfo.ESTADO_TRANSPORTADO, 0);
		_lanzador.setEstado(CentroInfo.ESTADO_PORTADOR, 0);
		objetivo.setTransportadoPor(null);
		_lanzador.setTransportado(null);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 51, _lanzador.getID() + "", _celdaLanz.getID() + "");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, objetivo.getID() + "", objetivo.getID() + ","
				+ CentroInfo.ESTADO_TRANSPORTADO + ",0");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "", _lanzador.getID() + ","
				+ CentroInfo.ESTADO_PORTADOR + ",0");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_77(ArrayList<Luchador> afectados, Pelea pelea) {// robo de PM
		int valor = -1;
		try {
			valor = Integer.parseInt(_args.split(";")[0]);
		} catch (NumberFormatException e) {}
		if (valor == -1)
			return;
		int num = 0;
		for (Luchador objetivo : afectados) {
			if (objetivo.estaMuerto())
				continue;
			int perdidos = Formulas.getPuntosPerdidos('m', valor, _lanzador, objetivo);
			if (perdidos < valor) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 309, _lanzador.getID() + "", objetivo.getID() + ","
						+ (valor - perdidos));
			}
			if (perdidos < 1)
				continue;
			objetivo.addBuff(CentroInfo.STATS_REM_PM, perdidos, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_PM, _lanzador.getID() + "", objetivo.getID()
					+ ",-" + perdidos + "," + _turnos);
			num += perdidos;
		}
		if (num != 0) {
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_PM, _lanzador.getID() + "", _lanzador.getID()
					+ "," + num + "," + _turnos);
			_lanzador.addBuff(CentroInfo.STATS_ADD_PM, num, 1, 0, true, _hechizoID, _args, _lanzador, _veneno);
			if (_lanzador.puedeJugar())
				_lanzador.addTempPM(pelea, num);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_78(ArrayList<Luchador> objetivos, Pelea pelea) {// + PM
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			if (objetivo.puedeJugar())
				objetivo.addTempPM(pelea, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_79(ArrayList<Luchador> objetivos, Pelea pelea) { // posib recibir daños o curas
		if (_turnos < 1)
			return;
		else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, -1, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_82(ArrayList<Luchador> objetivos, Pelea pelea) {// robo de PDV fijo
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				int dañoFinal = _valor;
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				int cura = (int) (-dañoFinal);
				if ( (_lanzador.getPDVConBuff() + cura) > _lanzador.getPDVMaxConBuff())
					cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				_lanzador.restarPDV(-cura);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + "," + cura);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_84(ArrayList<Luchador> objetivos, Pelea pelea) { // robo de PA
		int value = -1;
		try {
			value = Integer.parseInt(_args.split(";")[0]);
		} catch (NumberFormatException e) {}
		if (value == -1)
			return;
		int num = 0;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			int perdidos = Formulas.getPuntosPerdidos('m', value, _lanzador, objetivo);
			if (perdidos < value)
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 308, _lanzador.getID() + "", objetivo.getID() + ","
						+ (value - perdidos));
			if (perdidos < 1)
				continue;
			if (_hechizoID == 95 || _hechizoID == 2079) {// reloj de pared, reloj de pared(dopeul)
				objetivo.addBuff(CentroInfo.STATS_REM_PA, perdidos, 1, 1, true, _hechizoID, _args, _lanzador, _veneno);
			} else {
				objetivo.addBuff(CentroInfo.STATS_REM_PA, perdidos, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_PA, _lanzador.getID() + "", objetivo.getID()
					+ ",-" + perdidos + "," + _turnos);
			num += perdidos;
		}
		if (num != 0) {
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_PA, _lanzador.getID() + "", _lanzador.getID()
					+ "," + num + "," + _turnos);
			_lanzador.addBuff(CentroInfo.STATS_ADD_PA, num, 0, 0, true, _hechizoID, _args, _lanzador, _veneno);
			if (_lanzador.puedeJugar())
				_lanzador.addTempPA(pelea, num);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_85(ArrayList<Luchador> objetivos, Pelea pelea) { // daños % vida atacante agua
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				daño = (int) ( (daño / 100.00) * _lanzador.getPDVConBuff());
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_AGUA, daño, false,
						_hechizoID, _veneno);
				if (daño < 0)
					daño = 0;
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (daño > objetivo.getPDVConBuff())
					daño = objetivo.getPDVConBuff();
				objetivo.restarPDV(daño);
				int cura = daño;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				daño = - (daño);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + daño);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_86(ArrayList<Luchador> objetivos, Pelea pelea) {// daños % de la vida del atacante (tierra)
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				daño = (int) ( (daño / 100.00) * _lanzador.getPDVConBuff());
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_TIERRA, daño, false,
						_hechizoID, _veneno);
				if (daño < 0)
					daño = 0;
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (daño > objetivo.getPDVConBuff())
					daño = objetivo.getPDVConBuff();
				objetivo.restarPDV(daño);
				int cura = daño;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				daño = - (daño);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + daño);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_87(ArrayList<Luchador> objetivos, Pelea pelea) {// daños % de la vida del atacante aire
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				daño = (int) ( (daño / 100.00) * _lanzador.getPDVConBuff());
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_AIRE, daño, false,
						_hechizoID, _veneno);
				if (daño < 0)
					daño = 0;
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (daño > objetivo.getPDVConBuff())
					daño = objetivo.getPDVConBuff();
				objetivo.restarPDV(daño);
				int cura = daño;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				daño = - (daño);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + daño);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_88(ArrayList<Luchador> objetivos, Pelea pelea) {// daños % de la vida del atacante fuego
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				daño = (int) ( (daño / 100.00) * _lanzador.getPDVConBuff());
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_FUEGO, daño, false,
						_hechizoID, _veneno);
				if (daño < 0)
					daño = 0;
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (daño > objetivo.getPDVConBuff())
					daño = objetivo.getPDVConBuff();
				objetivo.restarPDV(daño);
				int cura = daño;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				daño = - (daño);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + daño);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_89(ArrayList<Luchador> objetivos, Pelea pelea) {// daños % de la vida del atacante neutral
		if (_hechizoID == 1679) {
			char[] dir = { 'b', 'd', 'f', 'h' };
			Luchador victima = objetivos.get(0);
			objetivos.clear();
			for (int i = 0; i < 4; i++) {
				int idSigCelda = Camino
						.getSigIDCeldaMismaDir(victima.getCeldaPelea().getID(), dir[i], pelea.getMapaCopia(), true);
				Celda sigCelda = pelea.getMapaCopia().getCelda(idSigCelda);
				if (sigCelda == null)
					continue;
				Luchador objetivo = sigCelda.getPrimerLuchador();
				if (objetivo != null)
					objetivos.add(objetivo);
			}
			try {
				Thread.sleep(500);
			} catch (Exception e) {}
		}
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				daño = (int) ( (daño / 100.00) * _lanzador.getPDVConBuff());
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_NEUTRAL, daño, false,
						_hechizoID, _veneno);
				if (daño < 0)
					daño = 0;
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (daño > objetivo.getPDVConBuff())
					daño = objetivo.getPDVConBuff();
				objetivo.restarPDV(daño);
				int cura = daño;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				daño = - (daño);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + daño);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_81(ArrayList<Luchador> objetivos, Pelea pelea) {// curacion
		if (_turnos <= 0) {
			String[] jet = _args.split(";");
			int cura = 0;
			if (jet.length < 6) {
				cura = 1;
			} else {
				cura = Formulas.getRandomValor(jet[5]);
			}
			int cura2 = cura;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				cura = getMaxMinHechizo(objetivo, cura);
				int pdvMax = objetivo.getPDVMaxConBuff();
				int curaFinal = Formulas.calculFinalCura(_lanzador, cura, false);
				if ( (curaFinal + objetivo.getPDVConBuff()) > pdvMax)
					curaFinal = pdvMax - objetivo.getPDVConBuff();
				if (curaFinal < 1)
					curaFinal = 0;
				objetivo.restarPDV(-curaFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 108, _lanzador.getID() + "", objetivo.getID() + "," + curaFinal);
				cura = cura2;
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_90(ArrayList<Luchador> objetivos, Pelea pelea) {// entrega X de su vida
		if (_turnos <= 0) {
			int porc = Formulas.getRandomValor(_valores);
			int dañoFinal = (int) ( (porc / 100.00) * _lanzador.getPDVConBuff());
			if (dañoFinal > _lanzador.getPDVConBuff())
				dañoFinal = _lanzador.getPDVConBuff();
			_lanzador.restarPDV(dañoFinal);
			dañoFinal = - (dañoFinal);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", _lanzador.getID() + "," + dañoFinal);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if ( (dañoFinal + objetivo.getPDVConBuff()) > objetivo.getPDVMaxConBuff())
					dañoFinal = objetivo.getPDVMaxConBuff() - objetivo.getPDVConBuff();
				objetivo.restarPDV(-dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + ",+" + dañoFinal);
			}
			if (_lanzador.getPDVConBuff() <= 0)
				pelea.agregarAMuertos(_lanzador);
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_91(ArrayList<Luchador> objetivos, Pelea pelea, boolean esCaC) {// roba PDV agua
		if (esCaC) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(-1);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_AGUA, daño, true,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				int cura = (int) (-dañoFinal) / 2;
				if ( (_lanzador.getPDVConBuff() + cura) > _lanzador.getPDVMaxConBuff())
					cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				_lanzador.restarPDV(-cura);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + "," + cura);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(_turnosOriginales);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_AGUA, daño, false,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				int cura = (int) (-dañoFinal) / 2;
				if ( (_lanzador.getPDVConBuff() + cura) > _lanzador.getPDVMaxConBuff())
					cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				_lanzador.restarPDV(-cura);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + "," + cura);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_92(ArrayList<Luchador> objetivos, Pelea pelea, boolean esCaC) {// roba PDV tierra
		if (esCaC) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(-1);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_TIERRA, daño, true,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				int cura = (int) (-dañoFinal) / 2;
				if ( (_lanzador.getPDVConBuff() + cura) > _lanzador.getPDVMaxConBuff())
					cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				_lanzador.restarPDV(-cura);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + "," + cura);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(_turnosOriginales);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_TIERRA, daño, false,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				int cura = (int) (-dañoFinal) / 2;
				if ( (_lanzador.getPDVConBuff() + cura) > _lanzador.getPDVMaxConBuff())
					cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				_lanzador.restarPDV(-cura);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + "," + cura);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_93(ArrayList<Luchador> objetivos, Pelea pelea, boolean esCaC) {// roba PDV aire
		if (esCaC) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(-1);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_AIRE, daño, true,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				int cura = (int) (-dañoFinal) / 2;
				if ( (_lanzador.getPDVConBuff() + cura) > _lanzador.getPDVMaxConBuff())
					cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				_lanzador.restarPDV(-cura);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + "," + cura);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(_turnosOriginales);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_AIRE, daño, false,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				int cura = (int) (-dañoFinal) / 2;
				if ( (_lanzador.getPDVConBuff() + cura) > _lanzador.getPDVMaxConBuff())
					cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				_lanzador.restarPDV(-cura);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + "," + cura);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_94(ArrayList<Luchador> objetivos, Pelea pelea, boolean esCaC) {// roba PDV fuego
		if (esCaC) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(-1);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_FUEGO, daño, true,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				int cura = (int) (-dañoFinal) / 2;
				if ( (_lanzador.getPDVConBuff() + cura) > _lanzador.getPDVMaxConBuff())
					cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				_lanzador.restarPDV(-cura);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + "," + cura);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(_turnosOriginales);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_FUEGO, daño, false,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				int cura = (int) (-dañoFinal) / 2;
				if ( (_lanzador.getPDVConBuff() + cura) > _lanzador.getPDVMaxConBuff())
					cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				_lanzador.restarPDV(-cura);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + "," + cura);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_95(ArrayList<Luchador> objetivos, Pelea pelea, boolean esCaC) {// roba PDV neutral
		if (esCaC) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(-1);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_NEUTRAL, daño, true,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				int cura = (int) (-dañoFinal) / 2;
				if ( (_lanzador.getPDVConBuff() + cura) > _lanzador.getPDVMaxConBuff())
					cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				_lanzador.restarPDV(-cura);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + "," + cura);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(_turnosOriginales);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_NEUTRAL, daño, false,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				int cura = (int) (-dañoFinal) / 2;
				if ( (_lanzador.getPDVConBuff() + cura) > _lanzador.getPDVMaxConBuff())
					cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
				_lanzador.restarPDV(-cura);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + "," + cura);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_96(ArrayList<Luchador> objetivos, Pelea pelea, boolean esCaC) { // daños agua
		if (esCaC) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(-1);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				for (EfectoHechizo EH : _lanzador.getBuffsPorEfectoID(293)) {
					if (EH.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(EH.getArgs().split(";")[2]);
						} catch (Exception e) {}
						if (add <= 0)
							continue;
						daño += add;
					}
				}
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_AGUA, daño, true,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				int cura = dañoFinal;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(_turnosOriginales);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				for (EfectoHechizo EH : _lanzador.getBuffsPorEfectoID(293)) {
					if (EH.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(EH.getArgs().split(";")[2]);
						} catch (Exception e) {}
						if (add <= 0)
							continue;
						daño += add;
					}
				}
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_AGUA, daño, false,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				int cura = dañoFinal;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_97(ArrayList<Luchador> objetivos, Pelea pelea, boolean esCaC) {// daños tierra
		if (esCaC) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(-1);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				for (EfectoHechizo EH : _lanzador.getBuffsPorEfectoID(293)) {
					if (EH.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(EH.getArgs().split(";")[2]);
						} catch (Exception e) {}
						if (add <= 0)
							continue;
						daño += add;
					}
				}
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_TIERRA, daño, true,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				int cura = dañoFinal;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(_turnosOriginales);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				for (EfectoHechizo EH : _lanzador.getBuffsPorEfectoID(293)) {
					if (EH.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(EH.getArgs().split(";")[2]);
						} catch (Exception e) {}
						if (add <= 0)
							continue;
						daño += add;
					}
				}
				if (_suerte > 0 && _hechizoID == 108) {
					int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, _lanzador, CentroInfo.ELEMENTO_TIERRA, daño,
							false, _hechizoID, _veneno);
					dañoFinal = aplicarBuffContraGolpe(dañoFinal, _lanzador, _lanzador, pelea, _hechizoID);
					if (dañoFinal > _lanzador.getPDVConBuff())
						dañoFinal = _lanzador.getPDVConBuff();
					_lanzador.restarPDV(dañoFinal);
					dañoFinal = - (dañoFinal);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", _lanzador.getID() + ","
							+ dañoFinal);
					if (_lanzador.getPDVConBuff() <= 0)
						pelea.agregarAMuertos(_lanzador);
				} else {
					int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_TIERRA, daño, false,
							_hechizoID, _veneno);
					dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
					if (dañoFinal > objetivo.getPDVConBuff())
						dañoFinal = objetivo.getPDVConBuff();
					objetivo.restarPDV(dañoFinal);
					int cura = dañoFinal;
					if (objetivo.tieneBuff(786)) {
						if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
							cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
						_lanzador.restarPDV(-cura);
						GestorSalida
								.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
					}
					dañoFinal = - (dañoFinal);
					GestorSalida
							.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
					if (objetivo.getPDVConBuff() <= 0)
						pelea.agregarAMuertos(objetivo);
				}
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_98(ArrayList<Luchador> objetivos, Pelea pelea, boolean esCaC) {// daños aire
		if (esCaC) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(-1);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				for (EfectoHechizo EH : _lanzador.getBuffsPorEfectoID(293)) {
					if (EH.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(EH.getArgs().split(";")[2]);
						} catch (Exception e) {}
						if (add <= 0)
							continue;
						daño += add;
					}
				}
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_AIRE, daño, true,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				int cura = dañoFinal;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(_turnosOriginales);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				for (EfectoHechizo EH : _lanzador.getBuffsPorEfectoID(293)) {
					if (EH.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(EH.getArgs().split(";")[2]);
						} catch (Exception e) {}
						if (add <= 0)
							continue;
						daño += add;
					}
				}
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_AIRE, daño, false,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				int cura = dañoFinal;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
			if (_hechizoID == 233 || _hechizoID == 2006) {
				try {
					Thread.sleep(1000);// un tiempo de pausa de un medio de segundo
				} catch (InterruptedException e1) {}
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_99(ArrayList<Luchador> objetivos, Pelea pelea, boolean esCaC) {// daños fuego
		if (esCaC) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(-1);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				for (EfectoHechizo EH : _lanzador.getBuffsPorEfectoID(293)) {
					if (EH.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(EH.getArgs().split(";")[2]);
						} catch (Exception e) {}
						if (add <= 0)
							continue;
						daño += add;
					}
				}
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_FUEGO, daño, true,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				int cura = dañoFinal;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(_turnosOriginales);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				for (EfectoHechizo EH : _lanzador.getBuffsPorEfectoID(293)) {
					if (EH.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(EH.getArgs().split(";")[2]);
						} catch (Exception e) {}
						if (add <= 0)
							continue;
						daño += add;
					}
				}
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_FUEGO, daño, false,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				int cura = dañoFinal;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_100(ArrayList<Luchador> objetivos, Pelea pelea, boolean esCaC) {// daños neutral
		if (esCaC) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(-1);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				for (EfectoHechizo EH : _lanzador.getBuffsPorEfectoID(293)) {
					if (EH.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(EH.getArgs().split(";")[2]);
						} catch (Exception e) {}
						if (add <= 0)
							continue;
						daño += add;
					}
				}
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_NEUTRAL, daño, true,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				int cura = dañoFinal;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else if (_turnos <= 0) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(_turnosOriginales);
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				for (EfectoHechizo EH : _lanzador.getBuffsPorEfectoID(293)) {
					if (EH.getValor() == _hechizoID) {
						int add = -1;
						try {
							add = Integer.parseInt(EH.getArgs().split(";")[2]);
						} catch (Exception e) {}
						if (add <= 0)
							continue;
						daño += add;
					}
				}
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_NEUTRAL, daño, false,
						_hechizoID, _veneno);
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				int cura = dañoFinal;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_101(ArrayList<Luchador> objetivos, Pelea pelea) { // PA perdidos por el blanco
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				int perdidos = Formulas.getPuntosPerdidos('a', _valor, _lanzador, objetivo);
				if ( (_valor - perdidos) > 0)
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 308, _lanzador.getID() + "", objetivo.getID() + ","
							+ (_valor - perdidos));
				if (perdidos > 0) {
					objetivo.addBuff(CentroInfo.STATS_REM_PA, perdidos, 1, 1, true, _hechizoID, _args, _lanzador, _veneno);
					if (_turnos <= 1 || _duracion <= 1)
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 101, objetivo.getID() + "", objetivo.getID() + ",-"
								+ perdidos);
				}
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				int perdidos = Formulas.getPuntosPerdidos('a', _valor, _lanzador, objetivo);
				if ( (_valor - perdidos) > 0)
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 308, _lanzador.getID() + "", objetivo.getID() + ","
							+ (_valor - perdidos));
				if (perdidos > 0) {
					if (_hechizoID == 89) {
						objetivo.addBuff(_efectoID, perdidos, 0, 1, true, _hechizoID, _args, _lanzador, _veneno);
					} else {
						objetivo.addBuff(_efectoID, perdidos, 1, 1, true, _hechizoID, _args, _lanzador, _veneno);
					}
					if (_turnos <= 1 || _duracion <= 1)
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 101, objetivo.getID() + "", objetivo.getID() + ",-"
								+ perdidos);
				}
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_105(ArrayList<Luchador> objetivos, Pelea pelea) { // daños reducidos(tregua, inmunidad, sapo)
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, _veneno);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_106(ArrayList<Luchador> objetivos, Pelea pelea) { // reenvio de hechizo
		int val = -1;
		try {
			val = Integer.parseInt(_args.split(";")[1]);// Nivel del hechizo maximo
		} catch (Exception e) {}
		if (val == -1)
			return;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, _veneno);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_107(ArrayList<Luchador> objetivos, Pelea pelea) { // daños devueltos
		if (_turnos < 1)
			return;
		else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_108(ArrayList<Luchador> objetivos, Pelea pelea, boolean esCaC) {// curacion
		if (_hechizoID == 441)
			return;
		if (esCaC) {
			if (_lanzador.esInvisible())
				_lanzador.hacerseVisible(-1);
			String[] jet = _args.split(";");
			int cura = 0;
			if (jet.length < 6) {
				cura = 1;
			} else {
				cura = Formulas.getRandomValor(jet[5]);
			}
			int cura2 = cura;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				cura = getMaxMinHechizo(objetivo, cura);
				int pdvMax = objetivo.getPDVMaxConBuff();
				int curaFinal = Formulas.calculFinalCura(_lanzador, cura, esCaC);
				if ( (curaFinal + objetivo.getPDVConBuff()) > pdvMax)
					curaFinal = pdvMax - objetivo.getPDVConBuff();
				if (curaFinal < 1)
					curaFinal = 0;
				objetivo.restarPDV(-curaFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 108, _lanzador.getID() + "", objetivo.getID() + "," + curaFinal);
				cura = cura2;
			}
		} else if (_turnos <= 0) {
			String[] jet = _args.split(";");
			int cura = 0;
			if (jet.length < 6) {
				cura = 1;
			} else {
				cura = Formulas.getRandomValor(jet[5]);
			}
			int cura2 = cura;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				cura = getMaxMinHechizo(objetivo, cura);
				int pdvMax = objetivo.getPDVMaxConBuff();
				int curaFinal = Formulas.calculFinalCura(_lanzador, cura, esCaC);
				if ( (curaFinal + objetivo.getPDVConBuff()) > pdvMax)
					curaFinal = pdvMax - objetivo.getPDVConBuff();
				if (curaFinal < 1)
					curaFinal = 0;
				objetivo.restarPDV(-curaFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 108, _lanzador.getID() + "", objetivo.getID() + "," + curaFinal);
				cura = cura2;
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_109(Pelea pelea) {// daños para el lanzador (fixe)
		if (_turnos <= 0) {
			int daño = Formulas.getRandomValor(_valores);
			int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, _lanzador, CentroInfo.ELEMENTO_NULO, daño, false,
					_hechizoID, _veneno);
			dañoFinal = aplicarBuffContraGolpe(dañoFinal, _lanzador, _lanzador, pelea, _hechizoID);
			if (dañoFinal > _lanzador.getPDVConBuff())
				dañoFinal = _lanzador.getPDVConBuff();
			_lanzador.restarPDV(dañoFinal);
			dañoFinal = - (dañoFinal);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", _lanzador.getID() + "," + dañoFinal);
			if (_lanzador.getPDVConBuff() <= 0)
				pelea.agregarAMuertos(_lanzador);
		} else {
			_lanzador.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_110(ArrayList<Luchador> objetivos, Pelea pelea) { // + a la vida
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_111(ArrayList<Luchador> objetivos, Pelea pelea) {// + PA
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			if (objetivo.puedeJugar())
				objetivo.addTempPA(pelea, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_112(ArrayList<Luchador> objetivos, Pelea pelea) {// + a los daños
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_114(ArrayList<Luchador> objetivos, Pelea pelea) {// multiplica los daños por X
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_115(ArrayList<Luchador> objetivos, Pelea pelea) {// + a los golpes criticos
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_116(ArrayList<Luchador> objetivos, Pelea pelea) {// - alcance
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			if (objetivo.puedeJugar() && objetivo == _lanzador)
				objetivo.getTotalStatsConBuff().addUnStat(CentroInfo.STATS_REM_ALCANCE, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_117(ArrayList<Luchador> objetivos, Pelea pelea) {// + alcance
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			if (objetivo.puedeJugar() && objetivo == _lanzador)
				objetivo.getTotalStatsConBuff().addUnStat(CentroInfo.STATS_ADD_ALCANCE, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_118(ArrayList<Luchador> objetivos, Pelea pelea) {// + fuerza
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_119(ArrayList<Luchador> objetivos, Pelea pelea) {// + agilidad
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_120(Pelea pelea) {// + PA segun el porcenta de 3er arg
		int val = Formulas.getRandomValor(_valores);
		_lanzador.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
		_lanzador.addTempPA(pelea, val);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", _lanzador.getID() + "," + val + ","
				+ _turnos);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_121(ArrayList<Luchador> objetivos, Pelea pelea) {// + a los daños
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_122(ArrayList<Luchador> objetivos, Pelea pelea) { // + fallos criticos
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_123(ArrayList<Luchador> objetivos, Pelea pelea) {// + suerte
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_124(ArrayList<Luchador> objetivos, Pelea pelea) { // + sabiduria
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_125(ArrayList<Luchador> objetivos, Pelea pelea) { // + vitalidad
		if (_hechizoID == 441)
			return;
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			// GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES(pelea, 7);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_126(ArrayList<Luchador> objetivos, Pelea pelea) {// + Inteligencia
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_127(ArrayList<Luchador> objetivos, Pelea pelea) { // PM perdidos por el blanco
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				int perdidos = Formulas.getPuntosPerdidos('m', _valor, _lanzador, objetivo);
				if ( (_valor - perdidos) > 0)
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 309, _lanzador.getID() + "", objetivo.getID() + ","
							+ (_valor - perdidos));
				if (perdidos > 0) {
					objetivo.addBuff(CentroInfo.STATS_REM_PM, perdidos, 1, 1, true, _hechizoID, _args, _lanzador, _veneno);
					if (_turnos <= 1 || _duracion <= 1)
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 127, objetivo.getID() + "", objetivo.getID() + ",-"
								+ perdidos);
				}
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				int perdidos = Formulas.getPuntosPerdidos('m', _valor, _lanzador, objetivo);
				if ( (_valor - perdidos) > 0)
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 309, _lanzador.getID() + "", objetivo.getID() + ","
							+ (_valor - perdidos));
				if (perdidos > 0) {
					if (_hechizoID == 136) {// Palabra de Inmovilizacion
						objetivo.addBuff(_efectoID, perdidos, _turnos, _turnos, true, _hechizoID, _args, _lanzador, _veneno);
					} else {
						objetivo.addBuff(_efectoID, perdidos, 1, 1, true, _hechizoID, _args, _lanzador, _veneno);
					}
					if (_turnos <= 1 || _duracion <= 1)
						GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 127, objetivo.getID() + "", objetivo.getID() + ",-"
								+ perdidos);
				}
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_128(ArrayList<Luchador> objetivos, Pelea pelea) {// + PMs
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			if (objetivo.puedeJugar())
				objetivo.addTempPM(pelea, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_130(ArrayList<Luchador> objetivos, Pelea pelea) {}
	
	private void aplicarEfecto_131(ArrayList<Luchador> objetivos, Pelea pelea) {// PA utlizados hacen perder X PDV
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(_efectoID, _valor, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_132(ArrayList<Luchador> objetivos, Pelea pelea) {// desechiza
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.desbuffear();
			if (objetivo.puedeJugar() && objetivo == _lanzador) {
				Stats s1 = objetivo.getTotalStatsConBuff();
				Stats s2 = objetivo.getTotalStatsSinBuff();
				for (int a = 0; a < 1000; a++) {
					if (s1._statsEfecto.get(a) == null || s2._statsEfecto.get(a) == null)
						continue;
					int nuevo = s2._statsEfecto.get(a);
					s1.especificarStat(a, nuevo);
				}
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 132, _lanzador.getID() + "", objetivo.getID() + "");
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_138(ArrayList<Luchador> objetivos, Pelea pelea) {// aumenta los daños en un X %
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_140(ArrayList<Luchador> objetivos, Pelea pelea) {// pasar turno
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_141(ArrayList<Luchador> objetivos, Pelea pelea) {// matar al blanco
		if (_hechizoID == 265) {
			pelea.agregarAMuertos(_lanzador);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			if (objetivo.tieneBuff(765)) {
				if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
					aplicarEfecto_765B(pelea, objetivo);
					objetivo = objetivo.getBuff(765).getLanzador();
				}
			}
			pelea.agregarAMuertos(objetivo);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
	}
	
	private void aplicarEfecto_143(ArrayList<Luchador> objetivos, Pelea pelea) {// pdv devueltos para castigo
		if (_turnos <= 0) {
			String[] jet = _args.split(";");
			int cura = 0;
			if (jet.length < 6) {
				cura = 1;
			} else {
				cura = Formulas.getRandomValor(jet[5]);
			}
			int dmg2 = cura;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				cura = getMaxMinHechizo(objetivo, cura);
				int curaFinal = Formulas.calculFinalCura(_lanzador, cura, false);
				if ( (curaFinal + objetivo.getPDVConBuff()) > objetivo.getPDVMaxConBuff())
					curaFinal = objetivo.getPDVMaxConBuff() - objetivo.getPDVConBuff();
				if (curaFinal < 1)
					curaFinal = 0;
				objetivo.restarPDV(-curaFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 108, _lanzador.getID() + "", objetivo.getID() + "," + curaFinal);
				cura = dmg2;
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_142(ArrayList<Luchador> objetivos, Pelea pelea) { // + a los daños fisicos
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_144(ArrayList<Luchador> objetivos, Pelea pelea) { // - a los daños (no boosteados)
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(145, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 145, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_145(ArrayList<Luchador> objetivos, Pelea pelea) { // - a los daños
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_149(ArrayList<Luchador> objetivos, Pelea pelea) { // cambia la apariencia
		int id = -1;
		try {
			id = Integer.parseInt(_args.split(";")[2]);
		} catch (Exception e) {}
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			if (id == -1)
				id = objetivo.getGfxDefecto();
			objetivo.addBuff(_efectoID, id, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			int defecto = objetivo.getGfxDefecto();
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + defecto
					+ "," + id + "," + (objetivo.puedeJugar() ? _turnos + 1 : _turnos));
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_150(ArrayList<Luchador> objetivos, Pelea pelea) {// vuelve invisible al personaje
		if (_turnos == 0)
			return;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 150, _lanzador.getID() + "", objetivo.getID() + ",4");
			objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_152(ArrayList<Luchador> objetivos, Pelea pelea) {// - suerte
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_153(ArrayList<Luchador> objetivos, Pelea pelea) {// - vitalidad
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_154(ArrayList<Luchador> objetivos, Pelea pelea) {// - agilidad
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_155(ArrayList<Luchador> objetivos, Pelea pelea) {// - inteligencia
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_156(ArrayList<Luchador> objetivos, Pelea pelea) {// - sabiduria
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_157(ArrayList<Luchador> objetivos, Pelea pelea) {// - fuerza
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_160(ArrayList<Luchador> objetivos, Pelea pelea) {// + % evitar perdidasde PA
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_161(ArrayList<Luchador> objetivos, Pelea pelea) {// + % evitar perdidas de PM
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_162(ArrayList<Luchador> objetivos, Pelea pelea) {// - % evitar perdidas de PA
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_163(ArrayList<Luchador> objetivos, Pelea pelea) { // - % evitar perdidas de PM
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_164(ArrayList<Luchador> objetivos, Pelea pelea) { // daños reducidos x %
		int val = _valor;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, _veneno);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_165(ArrayList<Luchador> objetivos, Pelea pelea) {// aumenta los daños en un X%
		int valor = -1;
		try {
			valor = Integer.parseInt(_args.split(";")[1]);
		} catch (Exception e) {}
		if (valor == -1)
			return;
		int val2 = valor;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			valor = getMaxMinHechizo(objetivo, valor);
			objetivo.addBuff(_efectoID, valor, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			valor = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_168(ArrayList<Luchador> objetivos, Pelea pelea) {// - PA, no esquivables
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, _valor, 1, 1, true, _hechizoID, _args, _lanzador, _veneno);
				if (_turnos <= 1 || _duracion <= 1) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 168, objetivo.getID() + "", objetivo.getID() + ",-" + _valor);
				}
			}
		} else {
			boolean repetibles = false;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (_hechizoID == 197 || _hechizoID == 112) { // potencia silvestre, garra - ceangal (critico)
					objetivo.addBuff(_efectoID, _valor, _turnos, _turnos, true, _hechizoID, _args, _lanzador, _veneno);
				} else if (_hechizoID == 115) {// Olfato
					if (!repetibles) {
						int perdidosPA = Formulas.getRandomValor(_valores);
						if (perdidosPA == -1)
							continue;
						_valor = perdidosPA;
					}
					objetivo.addBuff(_efectoID, _valor, _turnos, _turnos, true, _hechizoID, _args, _lanzador, _veneno);
					repetibles = true;
				} else {
					objetivo.addBuff(_efectoID, _valor, 1, 1, true, _hechizoID, _args, _lanzador, _veneno);
				}
				if (_turnos <= 1 || _duracion <= 1)
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 168, objetivo.getID() + "", objetivo.getID() + ",-" + _valor);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_169(ArrayList<Luchador> objetivos, Pelea pelea) { // - PM, no esquivables
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, _valor, 1, 1, true, _hechizoID, _args, _lanzador, _veneno);
				if (_turnos <= 1 || _duracion <= 1)
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 169, objetivo.getID() + "", objetivo.getID() + ",-" + _valor);
			}
		} else {
			if (!objetivos.isEmpty() && _hechizoID == 120 && _lanzador.getObjetivoDestZurca() != null) {
				_lanzador.getObjetivoDestZurca().addBuff(_efectoID, _valor, _turnos, _turnos, true, _hechizoID, _args, _lanzador,
						_veneno);
				if (_turnos <= 1 || _duracion <= 1)
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 169, _lanzador.getObjetivoDestZurca().getID() + "", _lanzador
							.getObjetivoDestZurca().getID() + ",-" + _valor);
			}
			for (Luchador objetivo : objetivos) {
				boolean repetibles = false;
				if (objetivo.estaMuerto())
					continue;
				if (_hechizoID == 192) {// zarza tranquilizadora
					objetivo.addBuff(_efectoID, _valor, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
				} else if (_hechizoID == 115) {// olfato
					if (!repetibles) {
						int perdidosPM = Formulas.getRandomValor(_valores);
						if (perdidosPM == -1)
							continue;
						_valor = perdidosPM;
					}
					objetivo.addBuff(_efectoID, _valor, _turnos, _turnos, true, _hechizoID, _args, _lanzador, _veneno);
					repetibles = true;
				} else if (_hechizoID == 197) {// portencia sivelstre
					objetivo.addBuff(_efectoID, _valor, _turnos, _turnos, true, _hechizoID, _args, _lanzador, _veneno);
				} else {
					objetivo.addBuff(_efectoID, _valor, 1, 1, true, _hechizoID, _args, _lanzador, _veneno);
				}
				if (_turnos <= 1 || _duracion <= 1)
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 169, objetivo.getID() + "", objetivo.getID() + ",-" + _valor);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_171(ArrayList<Luchador> objetivos, Pelea pelea) { // - a los golpes criticos
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_176(ArrayList<Luchador> objetivos, Pelea pelea) { // + prospecciones
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_PROSPECCION, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_177(ArrayList<Luchador> objetivos, Pelea pelea) { // - prospecciones
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_PROSPECCION, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_178(ArrayList<Luchador> objetivos, Pelea pelea) { // + curaciones
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_179(ArrayList<Luchador> objetivos, Pelea pelea) { // - curaciones
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_180(Pelea pelea) {// invocacion de un doble
		int idInvocacion = pelea.getSigIDLuchador();
		Personaje clon = Personaje.personajeClonado(_lanzador.getPersonaje(), idInvocacion);
		Luchador doble = new Luchador(pelea, clon);
		doble.setEquipoBin(_lanzador.getEquipoBin());
		doble.setInvocador(_lanzador);
		_celdaLanz.addLuchador(doble);
		doble.setCeldaPelea(_celdaLanz);
		pelea.getOrdenJug().add( (pelea.getOrdenJug().indexOf(_lanzador) + 1), doble);
		pelea.addLuchadorEnEquipo(doble, _lanzador.getEquipoBin());
		String gm = "+" + doble.stringGM();
		String gtl = pelea.stringOrdenJugadores();
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 180, _lanzador.getID() + "", gm);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", gtl);
		ArrayList<Trampa> trampas = (new ArrayList<Trampa>());
		trampas.addAll(pelea.getTrampas());
		for (Trampa trampa : trampas) {
			int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), doble.getCeldaPelea()
					.getID());
			if (dist <= trampa.getTamaño())
				trampa.activaTrampa(doble);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_181(Pelea pelea) {// invocar una criatura
		int mobID = -1;
		int mobNivel = -1;
		try {
			mobID = Integer.parseInt(_args.split(";")[0]);
			mobNivel = Integer.parseInt(_args.split(";")[1]);
		} catch (Exception e) {}
		MobGrado mob = null;
		try {
			mob = MundoDofus.getMobModelo(mobID).getGradoPorNivel(mobNivel).getCopy();
		} catch (Exception e1) {
			System.out.println("El Mob ID esta mal configurado: " + mobID);
			return;
		}
		if (mobID == -1 || mobNivel == -1 || mob == null)
			return;
		int idInvocacion = pelea.getSigIDLuchador() - pelea.getNumeroInvos();
		mob.setIdEnPelea(idInvocacion);
		mob.modificarStatsPorInvocador(_lanzador);
		Luchador invocacion = new Luchador(pelea, mob);
		invocacion.setEquipoBin(_lanzador.getEquipoBin());
		invocacion.setInvocador(_lanzador);
		_celdaLanz.addLuchador(invocacion);
		invocacion.setCeldaPelea(_celdaLanz);
		pelea.getOrdenJug().add( (pelea.getOrdenJug().indexOf(_lanzador) + 1), invocacion);
		pelea.addLuchadorEnEquipo(invocacion, _lanzador.getEquipoBin());
		String gm = "+" + invocacion.stringGM();
		String gtl = pelea.stringOrdenJugadores();
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 181, _lanzador.getID() + "", gm);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", gtl);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
		_lanzador.aumentarInvocaciones();
		ArrayList<Trampa> trampas = (new ArrayList<Trampa>());
		trampas.addAll(pelea.getTrampas());
		for (Trampa trampa : trampas) {
			int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), invocacion.getCeldaPelea()
					.getID());
			if (dist <= trampa.getTamaño())
				trampa.activaTrampa(invocacion);
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_182(ArrayList<Luchador> objetivos, Pelea pelea) { // + criaturas invocables
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_183(ArrayList<Luchador> objetivos, Pelea pelea) { // + reduccion magica
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_184(ArrayList<Luchador> objetivos, Pelea pelea) { // + reduccion fisica
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, false, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_185(Pelea pelea) { // invoca una criatura estatica
		int celdaID = _celdaLanz.getID();
		int mobID = -1;
		int nivel = -1;
		try {
			mobID = Integer.parseInt(_args.split(";")[0]);
			nivel = Integer.parseInt(_args.split(";")[1]);
		} catch (Exception e) {}
		MobGrado MG = null;
		try {
			MG = MundoDofus.getMobModelo(mobID).getGradoPorNivel(nivel).getCopy();
		} catch (Exception e1) {
			System.out.println("El Mob ID esta mal configurado: " + mobID);
			return;
		}
		if (mobID == -1 || nivel == -1 || MG == null)
			return;
		int idInvocacion = pelea.getSigIDLuchador() - pelea.getNumeroInvos();
		MG.setIdEnPelea(idInvocacion);
		MG.modificarStatsPorInvocador(_lanzador);
		Luchador invocacion = new Luchador(pelea, MG);
		int equipoLanz = _lanzador.getEquipoBin();
		invocacion.setEquipoBin(equipoLanz);
		invocacion.setInvocador(_lanzador);
		Celda nuevaCelda = pelea.getMapaCopia().getCelda(celdaID);
		nuevaCelda.addLuchador(invocacion);
		invocacion.setCeldaPelea(nuevaCelda);
		pelea.addLuchadorEnEquipo(invocacion, equipoLanz);
		String gm = "+" + invocacion.stringGM();
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 185, _lanzador.getID() + "", gm);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_186(ArrayList<Luchador> objetivos, Pelea pelea) { // disminuye los daños %(deshonra)
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_202(ArrayList<Luchador> objetivos, Pelea pelea, ArrayList<Celda> celdas) {// revela todos los
																											// objetos invisibles
		int equipo = _lanzador.getParamEquipoAliado();
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			if (objetivo.esInvisible())
				objetivo.aparecer(_lanzador);
		}
		for (Trampa trampa : pelea.getTrampas()) {
			if (trampa.getParamEquipoDueño() == equipo)
				continue;
			for (Celda celda : celdas) {
				if (celda.getID() == trampa.getCelda().getID()) {
					trampa.esVisibleParaEnemigo();
					trampa.aparecer(equipo);
				}
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_210(ArrayList<Luchador> objetivos, Pelea pelea) {// + resist % tierra
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_211(ArrayList<Luchador> objetivos, Pelea pelea) {// + resist % agua
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_212(ArrayList<Luchador> objetivos, Pelea pelea) {// + resist % aire
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_213(ArrayList<Luchador> objetivos, Pelea pelea) {// + resist % fuego
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_214(ArrayList<Luchador> objetivos, Pelea pelea) {// + resist % neutral
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_215(ArrayList<Luchador> objetivos, Pelea pelea) {// + debilidad % tierra
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_216(ArrayList<Luchador> objetivos, Pelea pelea) {// + debilidad % agua
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_217(ArrayList<Luchador> objetivos, Pelea pelea) {// + debilidad % aire
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_218(ArrayList<Luchador> objetivos, Pelea pelea) {// + debilidad % fuego
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_219(ArrayList<Luchador> objetivos, Pelea pelea) {// + debilidad % neutral
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_220(ArrayList<Luchador> objetivos, Pelea pelea) { // daños devueltos
		if (_turnos < 1)
			return;
		else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_265(ArrayList<Luchador> objetivos, Pelea pelea) { // daños reducidos (armaduras)
		int val = Formulas.getRandomValor(_valores);
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
			val = val2;
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_266(ArrayList<Luchador> objetivos, Pelea pelea) {// robo de suerte
		int val = Formulas.getRandomValor(_valores);
		int vol = 0;
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_REM_SUERTE, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_SUERTE, _lanzador.getID() + "", objetivo.getID()
					+ "," + val + "," + _turnos);
			vol += val;
			val = val2;
		}
		if (vol != 0) {
			_lanzador.addBuff(CentroInfo.STATS_ADD_SUERTE, vol, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_SUERTE, _lanzador.getID() + "", _lanzador.getID()
					+ "," + vol + "," + _turnos);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_267(ArrayList<Luchador> objetivos, Pelea pelea) {// robo de vitalidad
		int val = Formulas.getRandomValor(_valores);
		int vol = 0;
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_REM_VITALIDAD, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_VITALIDAD, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
			vol += val;
			val = val2;
		}
		if (vol == 0)
			return;
		_lanzador.addBuff(CentroInfo.STATS_ADD_VITALIDAD, vol, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_VITALIDAD, _lanzador.getID() + "", _lanzador.getID()
				+ "," + vol + "," + _turnos);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_268(ArrayList<Luchador> objetivos, Pelea pelea) {// robo de agilidad
		int val = Formulas.getRandomValor(_valores);
		int vol = 0;
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_REM_AGILIDAD, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_AGILIDAD, _lanzador.getID() + "", objetivo.getID()
					+ "," + val + "," + _turnos);
			vol += val;
			val = val2;
		}
		if (vol != 0) {
			_lanzador.addBuff(CentroInfo.STATS_ADD_AGILIDAD, vol, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_AGILIDAD, _lanzador.getID() + "",
					_lanzador.getID() + "," + vol + "," + _turnos);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_269(ArrayList<Luchador> objetivos, Pelea pelea) {// robo de inteligencia
		int val = Formulas.getRandomValor(_valores);
		int vol = 0;
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_REM_INTELIGENCIA, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_INTELIGENCIA, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
			vol += val;
			val = val2;
		}
		if (vol == 0) {
			_lanzador.addBuff(CentroInfo.STATS_ADD_INTELIGENCIA, vol, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_INTELIGENCIA, _lanzador.getID() + "",
					_lanzador.getID() + "," + vol + "," + _turnos);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_270(ArrayList<Luchador> objetivos, Pelea pelea) {// robo de sabiduria
		int val = Formulas.getRandomValor(_valores);
		int vol = 0;
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_REM_SABIDURIA, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_SABIDURIA, _lanzador.getID() + "",
					objetivo.getID() + "," + val + "," + _turnos);
			vol += val;
			val = val2;
		}
		if (vol == 0) {
			_lanzador.addBuff(124, vol, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 124, _lanzador.getID() + "", _lanzador.getID() + "," + vol + ","
					+ _turnos);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_271(ArrayList<Luchador> objetivos, Pelea pelea) {// robo de fuerza
		int val = Formulas.getRandomValor(_valores);
		int vol = 0;
		int val2 = val;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			val = getMaxMinHechizo(objetivo, val);
			objetivo.addBuff(CentroInfo.STATS_REM_FUERZA, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_FUERZA, _lanzador.getID() + "", objetivo.getID()
					+ "," + val + "," + _turnos);
			vol += val;
			val = val2;
		}
		if (vol == 0) {
			_lanzador.addBuff(CentroInfo.STATS_ADD_FUERZA, vol, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_FUERZA, _lanzador.getID() + "", _lanzador.getID()
					+ "," + vol + "," + _turnos);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_275(ArrayList<Luchador> objetivos, Pelea pelea) { // daños % vida atacante agua
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				daño = (int) ( (daño / 100.00) * _lanzador.getPDVConBuff());
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_AGUA, daño, false,
						_hechizoID, _veneno);
				if (daño < 0)
					daño = 0;
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (daño > objetivo.getPDVConBuff())
					daño = objetivo.getPDVConBuff();
				objetivo.restarPDV(daño);
				int cura = daño;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				daño = - (daño);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + daño);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_276(ArrayList<Luchador> objetivos, Pelea pelea) {// daños % de la vida del atacante (tierra)
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				daño = (int) ( (daño / 100.00) * _lanzador.getPDVConBuff());
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_TIERRA, daño, false,
						_hechizoID, _veneno);
				if (daño < 0)
					daño = 0;
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (daño > objetivo.getPDVConBuff())
					daño = objetivo.getPDVConBuff();
				objetivo.restarPDV(daño);
				int cura = daño;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				daño = - (daño);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + daño);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_277(ArrayList<Luchador> objetivos, Pelea pelea) {// daños % de la vida del atacante aire
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				daño = (int) ( (daño / 100.00) * _lanzador.getPDVConBuff());
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_AIRE, daño, false,
						_hechizoID, _veneno);
				if (daño < 0)
					daño = 0;
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (daño > objetivo.getPDVConBuff())
					daño = objetivo.getPDVConBuff();
				objetivo.restarPDV(daño);
				int cura = daño;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				daño = - (daño);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + daño);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_278(ArrayList<Luchador> objetivos, Pelea pelea) {// daños % de la vida del atacante fuego
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				daño = (int) ( (daño / 100.00) * _lanzador.getPDVConBuff());
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_FUEGO, daño, false,
						_hechizoID, _veneno);
				if (daño < 0)
					daño = 0;
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (daño > objetivo.getPDVConBuff())
					daño = objetivo.getPDVConBuff();
				objetivo.restarPDV(daño);
				int cura = daño;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				daño = - (daño);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + daño);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_279(ArrayList<Luchador> objetivos, Pelea pelea) {// daños % de la vida del atacante neutral
		if (_turnos <= 0) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo && _hechizoID != 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
					objetivo = _lanzador;
				}
				if (objetivo.tieneBuff(765)) {
					if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
						aplicarEfecto_765B(pelea, objetivo);
						objetivo = objetivo.getBuff(765).getLanzador();
					}
				}
				int daño = Formulas.getRandomValor(_valores);
				daño = getMaxMinHechizo(objetivo, daño);
				daño = (int) ( (daño / 100.00) * _lanzador.getPDVConBuff());
				int dañoFinal = Formulas.calculFinalDaño(pelea, _lanzador, objetivo, CentroInfo.ELEMENTO_NEUTRAL, daño, false,
						_hechizoID, _veneno);
				if (daño < 0)
					daño = 0;
				dañoFinal = aplicarBuffContraGolpe(dañoFinal, objetivo, _lanzador, pelea, _hechizoID);
				if (daño > objetivo.getPDVConBuff())
					daño = objetivo.getPDVConBuff();
				objetivo.restarPDV(daño);
				int cura = daño;
				if (objetivo.tieneBuff(786)) {
					if ( (cura + _lanzador.getPDVConBuff()) > _lanzador.getPDVMaxConBuff())
						cura = _lanzador.getPDVMaxConBuff() - _lanzador.getPDVConBuff();
					_lanzador.restarPDV(-cura);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, objetivo.getID() + "", _lanzador.getID() + ",+" + cura);
				}
				daño = - (daño);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + daño);
				if (objetivo.getPDVConBuff() <= 0)
					pelea.agregarAMuertos(objetivo);
			}
		} else {
			_veneno = true;
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(_efectoID, 0, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_293(Pelea pelea) {// aumenta los daños del hechizo X
		_lanzador.addBuff(_efectoID, _valor, _turnos, 1, false, _hechizoID, _args, _lanzador, _veneno);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_320(ArrayList<Luchador> objetivos, Pelea pelea) { // roba alcance
		int value = 1;
		try {
			value = Integer.parseInt(_args.split(";")[0]);
		} catch (NumberFormatException e) {}
		int num = 0;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(CentroInfo.STATS_REM_ALCANCE, value, _turnos, 0, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_REM_ALCANCE, _lanzador.getID() + "", objetivo.getID()
					+ "," + value + "," + _turnos);
			num += value;
		}
		if (num != 0) {
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, CentroInfo.STATS_ADD_ALCANCE, _lanzador.getID() + "", _lanzador.getID()
					+ "," + num + "," + _turnos);
			_lanzador.addBuff(CentroInfo.STATS_ADD_ALCANCE, num, 1, 0, true, _hechizoID, _args, _lanzador, _veneno);
			if (_lanzador.puedeJugar())
				_lanzador.getTotalStatsConBuff().addUnStat(CentroInfo.STATS_ADD_ALCANCE, num);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_400(Pelea pelea) {// pone una trampa de nivel X
		if (!_celdaLanz.esCaminable(true))
			return;
		if (_celdaLanz.getPrimerLuchador() != null)
			return;
		for (Trampa trampa : pelea.getTrampas())
			if (trampa.getCelda().getID() == _celdaLanz.getID())
				return;
		String[] infos = _args.split(";");
		int hechizoTrampaID = Short.parseShort(infos[0]);
		int nivel = Byte.parseByte(infos[1]);
		String po = MundoDofus.getHechizo(_hechizoID).getStatsPorNivel(_nivelHechizo).getAfectados();
		byte tamaño = (byte) Encriptador.getNumeroPorValorHash(po.charAt(1));
		StatsHechizos ST = MundoDofus.getHechizo(hechizoTrampaID).getStatsPorNivel(nivel);
		Trampa trampa = new Trampa(pelea, _lanzador, _celdaLanz, tamaño, ST, _hechizoID);
		pelea.getTrampas().add(trampa);
		int color = trampa.getColor();
		int equipo = _lanzador.getEquipoBin() + 1;
		String str = "GDZ+" + _celdaLanz.getID() + ";" + tamaño + ";" + color;
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, equipo, 999, _lanzador.getID() + "", str);
		str = "GDC" + _celdaLanz.getID() + ";Haaaaaaaaz3005;";
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, equipo, 999, _lanzador.getID() + "", str);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_401(Pelea pelea) {// pone un glifo nivel X
		if (!_celdaLanz.esCaminable(true))
			return;// Si casilla es caminable
		if (_celdaLanz.getPrimerLuchador() != null)
			return;// Si la casilla esta posicionada por un jugador
		String[] infos = _args.split(";");
		int hechizoGlifoID = Short.parseShort(infos[0]);
		int nivel = Byte.parseByte(infos[1]);
		byte duracion = Byte.parseByte(infos[3]);
		String po = MundoDofus.getHechizo(_hechizoID).getStatsPorNivel(_nivelHechizo).getAfectados();
		byte tamaño = (byte) Encriptador.getNumeroPorValorHash(po.charAt(1));
		StatsHechizos ST = MundoDofus.getHechizo(hechizoGlifoID).getStatsPorNivel(nivel);
		Glifo glifo = new Glifo(pelea, _lanzador, _celdaLanz, tamaño, ST, duracion, _hechizoID);
		pelea.getGlifos().add(glifo);
		int color = glifo.getColor();
		String str = "GDZ+" + _celdaLanz.getID() + ";" + tamaño + ";" + color;
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", str);
		str = "GDC" + _celdaLanz.getID() + ";Haaaaaaaaa3005;";
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", str);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_402(Pelea pelea) {// pone un glifo nivel X
		if (!_celdaLanz.esCaminable(true))
			return;
		String[] infos = _args.split(";");
		int hechizoGlifoID = Short.parseShort(infos[0]);
		int nivel = Byte.parseByte(infos[1]);
		byte duracion = Byte.parseByte(infos[3]);
		String po = MundoDofus.getHechizo(_hechizoID).getStatsPorNivel(_nivelHechizo).getAfectados();
		byte tamaño = (byte) Encriptador.getNumeroPorValorHash(po.charAt(1));
		StatsHechizos ST = MundoDofus.getHechizo(hechizoGlifoID).getStatsPorNivel(nivel);
		Glifo glifo = new Glifo(pelea, _lanzador, _celdaLanz, tamaño, ST, duracion, _hechizoID);
		pelea.getGlifos().add(glifo);
		int color = glifo.getColor();
		String str = "GDZ+" + _celdaLanz.getID() + ";" + tamaño + ";" + color;
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", str);
		str = "GDC" + _celdaLanz.getID() + ";Haaaaaaaaa3005;";
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, _lanzador.getID() + "", str);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_671(ArrayList<Luchador> objetivos, Pelea pelea) {// daños % de la vida del atacante neutral
		float val = Formulas.getRandomValor(_valores) / 100.00f;
		int pdvMax = _lanzador.getPDVMaxConBuff();
		int pdvMedio = pdvMax / 2;
		float porc = 1 - ( (Math.abs(_lanzador.getPDVConBuff() - pdvMedio)) / (float) pdvMedio);
		int daño = (int) (porc * val * pdvMax);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
				objetivo = _lanzador;
			}
			if (objetivo.tieneBuff(765)) {
				if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
					aplicarEfecto_765B(pelea, objetivo);
					objetivo = objetivo.getBuff(765).getLanzador();
				}
			}
			if (objetivo.tieneBuff(105)) {
				daño = daño - objetivo.getBuff(105).getValor();// Immunidad
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 105, _lanzador.getID() + "", objetivo.getID() + ","
						+ objetivo.getBuff(105).getValor());
			}
			Stats totalObjetivo = objetivo.getTotalStatsConBuff();
			int resMasT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_R_NEUTRAL);
			int resPorcT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_ResPorc_NEUTRAL);
			daño -= resMasT;
			int reduc = (int) ( (daño * (float) resPorcT) / 100);
			daño -= reduc;
			if (daño < 1)
				daño = 1;
			int dañoFinal = aplicarBuffContraGolpe(daño, objetivo, _lanzador, pelea, _hechizoID);
			if (dañoFinal > objetivo.getPDVConBuff())
				dañoFinal = objetivo.getPDVConBuff();
			objetivo.restarPDV(dañoFinal);
			dañoFinal = - (dañoFinal);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
			if (objetivo.getPDVConBuff() <= 0)
				pelea.agregarAMuertos(objetivo);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_672(ArrayList<Luchador> objetivos, Pelea pelea) {// daños % de la vida del atacante (neutral)
		float val = Formulas.getRandomValor(_valores) / 100.00f;
		int pdvMax = _lanzador.getPDVMaxConBuff();
		int pdvMedio = pdvMax / 2;
		float porc = 1 - ( (Math.abs(_lanzador.getPDVConBuff() - pdvMedio)) / (float) pdvMedio);
		int daño = (int) (porc * val * pdvMax * 0.95);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			if (objetivo.tieneBuff(106) && objetivo.getValorBuffPelea(106) >= _nivelHechizo) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 106, objetivo.getID() + "", objetivo.getID() + ",1");
				objetivo = _lanzador;
			}
			if (objetivo.tieneBuff(765)) {
				if (objetivo.getBuff(765) != null && !objetivo.getBuff(765).getLanzador().estaMuerto()) {
					aplicarEfecto_765B(pelea, objetivo);
					objetivo = objetivo.getBuff(765).getLanzador();
				}
			}
			if (objetivo.tieneBuff(105)) {
				daño = daño - objetivo.getBuff(105).getValor();// Immunidad
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 105, _lanzador.getID() + "", objetivo.getID() + ","
						+ objetivo.getBuff(105).getValor());
			}
			Stats totalObjetivo = objetivo.getTotalStatsConBuff();
			int resMasT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_R_NEUTRAL);
			int resPorcT = totalObjetivo.getEfecto(CentroInfo.STATS_ADD_ResPorc_NEUTRAL);
			daño -= resMasT;
			int reduc = (int) ( (daño * (float) resPorcT) / 100);
			daño -= reduc;
			if (daño < 1)
				daño = 1;
			int dañoFinal = aplicarBuffContraGolpe(daño, objetivo, _lanzador, pelea, _hechizoID);
			if (dañoFinal > objetivo.getPDVConBuff())
				dañoFinal = objetivo.getPDVConBuff();
			objetivo.restarPDV(dañoFinal);
			dañoFinal = - (dañoFinal);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, _lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
			if (objetivo.getPDVConBuff() <= 0)
				pelea.agregarAMuertos(objetivo);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_750(ArrayList<Luchador> objetivos, Pelea pelea) {}
	
	private void aplicarEfecto_765(ArrayList<Luchador> objetivos, Pelea pelea) { // sacrifio sacrogito
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(_efectoID, 0, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_765B(Pelea pelea, Luchador objetivo) {
		Luchador sacrificado = objetivo.getBuff(765).getLanzador();
		Celda cSacrificado = sacrificado.getCeldaPelea();
		Celda cObjetivo = objetivo.getCeldaPelea();
		cSacrificado.getLuchadores().clear();
		cObjetivo.getLuchadores().clear();
		sacrificado.setCeldaPelea(cObjetivo);
		cObjetivo.addLuchador(sacrificado);
		objetivo.setCeldaPelea(cSacrificado);
		cSacrificado.addLuchador(objetivo);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, objetivo.getID() + "", objetivo.getID() + "," + cSacrificado.getID());
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, sacrificado.getID() + "", sacrificado.getID() + "," + cObjetivo.getID());
		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_776(ArrayList<Luchador> objetivos, Pelea pelea) { // + daños incurables
		int val = Formulas.getRandomValor(_valores);
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(_efectoID, val, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, _efectoID, _lanzador.getID() + "", objetivo.getID() + "," + val + ","
					+ _turnos);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_780(Pelea pelea) { // invoca a un aliado muerto en combate
		Map<Integer, Luchador> muertos = pelea.getListaMuertos();
		Luchador objetivo = null;
		for (Entry<Integer, Luchador> entry : muertos.entrySet()) {
			Luchador muerto = entry.getValue();
			if (muerto.estaRetirado())
				continue;
			if (muerto.getEquipoBin() == _lanzador.getEquipoBin()) {
				if (muerto.esInvocacion())
					if (muerto.getInvocador().estaMuerto())
						continue;
				objetivo = muerto;
			}
		}
		if (objetivo == null)
			return;
		objetivo.setEstaMuerto(false);
		objetivo.setCeldaPelea(_celdaLanz);
		objetivo.getCeldaPelea().addLuchador(objetivo);
		objetivo.getBuffPelea().clear();
		int vida = (100 - _valor) * objetivo.getPDVMaxConBuff() / 100;
		if (!objetivo.esInvocacion()) {
			GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(objetivo.getPersonaje(), vida);
		} else
			pelea.getOrdenJug().add( (pelea.getOrdenJug().indexOf(_lanzador) + 1), objetivo);
		objetivo.restarPDV(-vida);
		pelea.addLuchadorEnEquipo(objetivo, _lanzador.getEquipoBin());
		String gm = "+" + objetivo.stringGM();
		String gtl = pelea.stringOrdenJugadores();
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 780, objetivo.getID() + "", gm);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, objetivo.getID() + "", gtl);
		if (!objetivo.esInvocacion())
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(objetivo.getPersonaje());
		objetivo.setInvocador(_lanzador);
		pelea.borrarUnMuerto(objetivo);
		_lanzador.aumentarInvocaciones();
		ArrayList<Trampa> trampas = new ArrayList<Trampa>();
		trampas.addAll(pelea.getTrampas());
		for (Trampa trampa : trampas) {
			int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), objetivo.getCeldaPelea()
					.getID());
			if (dist <= trampa.getTamaño())
				trampa.activaTrampa(objetivo);
		}
		try {
			Thread.sleep(500);// un tiempo de pausa de un medio de segundo
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_781(ArrayList<Luchador> objetivos, Pelea pelea) {// mala sombra
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(_efectoID, _valor, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_782(ArrayList<Luchador> objetivos, Pelea pelea) {// brokle
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(_efectoID, _valor, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_783(Pelea pelea) {// hace retroceder hasta la casilla objetivo
		Celda celdaLanzador = _lanzador.getCeldaPelea();
		char d = Camino.getDirEntreDosCeldas(celdaLanzador.getID(), _celdaLanz.getID(), pelea.getMapaCopia(), true);
		int idSigCelda = Camino.getSigIDCeldaMismaDir(celdaLanzador.getID(), d, pelea.getMapaCopia(), true);
		Celda sigCelda = pelea.getMapaCopia().getCelda(idSigCelda);
		if (sigCelda == null)
			return;
		if (sigCelda.getLuchadores().isEmpty())
			return;
		Luchador objetivo = sigCelda.getPrimerLuchador();
		if (objetivo.tieneEstado(6))
			return;
		int c1 = idSigCelda;
		int c2 = 0;
		int limite = 0;
		Celda c1celda = pelea.getMapaCopia().getCelda(c1);
		Celda case2 = null;
		ArrayList<Celda> trampas = new ArrayList<Celda>();
		for (Trampa trampa : pelea.getTrampas()) {
			trampas.add(trampa.getCelda());
		}
		while (true) {
			if (Camino.getSigIDCeldaMismaDir(c1, d, pelea.getMapaCopia(), true) == -1)
				return;
			c2 = Camino.getSigIDCeldaMismaDir(c1, d, pelea.getMapaCopia(), true);
			case2 = pelea.getMapaCopia().getCelda(c2);
			if (!case2.esCaminable(true) || pelea.celdaOcupada(c2))
				break;
			if (Camino.getSigIDCeldaMismaDir(c1, d, pelea.getMapaCopia(), true) == _celdaLanz.getID()) {
				c1 = Camino.getSigIDCeldaMismaDir(c1, d, pelea.getMapaCopia(), true);
				c1celda = pelea.getMapaCopia().getCelda(c1);
				break;
			}
			c1 = Camino.getSigIDCeldaMismaDir(c1, d, pelea.getMapaCopia(), true);
			c1celda = pelea.getMapaCopia().getCelda(c1);
			if (c1celda == null)
				return;
			if (trampas.contains(c1celda))
				break;
			limite++;
			if (limite > 50)
				return;
		}
		objetivo.getCeldaPelea().getLuchadores().clear();
		objetivo.setCeldaPelea(c1celda);
		objetivo.getCeldaPelea().addLuchador(objetivo);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 5, _lanzador.getID() + "", objetivo.getID() + "," + c1celda.getID());
		try {
			Thread.sleep(300);
		} catch (Exception e) {}
		for (Trampa trampa : pelea.getTrampas()) {
			int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), c1celda.getID());
			if (dist <= trampa.getTamaño()) {
				trampa.activaTrampa(objetivo);
				break;
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_784(ArrayList<Luchador> objetivos, Pelea pelea) {// teletransporta
		if (_turnos > 1)
			return;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto() || objetivo.esInvocacion())
				continue;
			Celda celda1 = null;
			for (Entry<Integer, Celda> entry : pelea.getPosInicial().entrySet()) {
				if (entry.getKey() == objetivo.getID()) {
					celda1 = entry.getValue();
					break;
				}
			}
			if (celda1.esCaminable(true) && !pelea.celdaOcupada(celda1.getID())) {
				objetivo.getCeldaPelea().getLuchadores().clear();
				objetivo.setCeldaPelea(celda1);
				objetivo.getCeldaPelea().addLuchador(objetivo);
				ArrayList<Trampa> trampas = (new ArrayList<Trampa>());
				trampas.addAll(pelea.getTrampas());
				for (Trampa trampa : trampas) {
					int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), objetivo
							.getCeldaPelea().getID());
					if (dist <= trampa.getTamaño())
						trampa.activaTrampa(objetivo);
				}
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 4, _lanzador.getID() + "", objetivo.getID() + "," + celda1.getID());
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {}
		}
	}
	
	private void aplicarEfecto_786(ArrayList<Luchador> objetivos, Pelea pelea) {// curacion silvestre
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(_efectoID, _valor, _turnos, 1, true, _hechizoID, _args, _lanzador, _veneno);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_787(ArrayList<Luchador> objetivos, Pelea pelea) {
		int hechizoID = -1;
		int hechizoNivel = -1;
		try {
			hechizoID = Integer.parseInt(_args.split(";")[0]);
			hechizoNivel = Integer.parseInt(_args.split(";")[1]);
		} catch (Exception e) {}
		Hechizo hechizo = MundoDofus.getHechizo(hechizoID);
		ArrayList<EfectoHechizo> EH = hechizo.getStatsPorNivel(hechizoNivel).getEfectos();
		for (EfectoHechizo eh : EH) {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				objetivo.addBuff(eh._efectoID, eh._valor, 1, 1, true, eh._hechizoID, eh._args, _lanzador, _veneno);
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_788(ArrayList<Luchador> objetivos, Pelea pelea) { // castigos
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			objetivo.addBuff(_efectoID, _valor, _turnos, 1, true, _hechizoID, _args, objetivo, _veneno);
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_950(ArrayList<Luchador> objetivos, Pelea pelea) {// estatdo X
		int idEstado = -1;
		try {
			idEstado = Integer.parseInt(_args.split(";")[2]);
		} catch (Exception e) {}
		if (idEstado == -1)
			return;
		if (_hechizoID == 1103 || (_hechizoID >= 1107 && _hechizoID <= 1110)) {
			if (_turnos <= 0) {
				if (_lanzador.puedeJugar())
					_lanzador.setEstado(idEstado, _turnos + 1);
				else
					_lanzador.setEstado(idEstado, _turnos);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "", _lanzador.getID() + "," + idEstado
						+ ",1");
			} else {
				if (_lanzador.puedeJugar())
					_lanzador.setEstado(idEstado, _turnos + 1);
				else
					_lanzador.setEstado(idEstado, _turnos);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "", _lanzador.getID() + "," + idEstado
						+ ",1");
				_lanzador.addBuff(_efectoID, idEstado, _turnos, 1, false, _hechizoID, _args, _lanzador, _veneno);
			}
		} else {
			for (Luchador objetivo : objetivos) {
				if (objetivo.estaMuerto())
					continue;
				if (_turnos <= 0) {
					if (objetivo.puedeJugar())
						objetivo.setEstado(idEstado, _turnos + 1);
					else
						objetivo.setEstado(idEstado, _turnos);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "", objetivo.getID() + "," + idEstado
							+ ",1");
				} else {
					if (objetivo.puedeJugar())
						objetivo.setEstado(idEstado, _turnos + 1);
					else
						objetivo.setEstado(idEstado, _turnos);
					GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "", objetivo.getID() + "," + idEstado
							+ ",1");
					objetivo.addBuff(_efectoID, idEstado, _turnos, 1, false, _hechizoID, _args, _lanzador, _veneno);
				}
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
	
	private void aplicarEfecto_951(ArrayList<Luchador> objetivos, Pelea pelea) {// saca del estado X
		int id = -1;
		try {
			id = Integer.parseInt(_args.split(";")[2]);
		} catch (Exception e) {}
		if (id == -1)
			return;
		for (Luchador objetivo : objetivos) {
			if (objetivo.estaMuerto())
				continue;
			if (!objetivo.tieneEstado(id))
				continue;
			objetivo.setEstado(id, 0);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, 950, _lanzador.getID() + "", objetivo.getID() + "," + id + ",0");
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {}
	}
}