
package variables;

import java.io.PrintWriter;
import java.util.ArrayList;

import variables.Mapa.Cercado;
import variables.MobModelo.GrupoMobs;
import variables.NPCModelo.PreguntaNPC;
import variables.Objeto.ObjetoModelo;
import variables.Oficio.StatsOficio;
import variables.Personaje.MisionPVP;

import estaticos.Bustemu;
import estaticos.CentroInfo;
import estaticos.CondicionJugador;
import estaticos.Formulas;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import estaticos.MundoDofus.*;

public class Accion {
	private int _ID;
	private String _args;
	private String _cond;
	
	public Accion(int id, String args, String cond) {
		_ID = id;
		_args = args;
		_cond = cond;
	}
	
	public String getCondiciones() {
		return _cond;
	}
	
	public void aplicar(Personaje perso, Personaje objetivo, int objUsadoID, int celda) {
		if (perso == null)
			return;
		if (perso.getCuenta().getEntradaPersonaje() == null)
			return;
		if (!_cond.equalsIgnoreCase("") && !_cond.equalsIgnoreCase("-1") && !CondicionJugador.validaCondiciones(perso, _cond)) {
			GestorSalida.ENVIAR_Im_INFORMACION(perso, "119");
			return;
		}
		PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
		switch (_ID) {
			case -2:// crear gremio
				try {
					if (perso.estaOcupado())
						return;
					if (perso.getGremio() != null || perso.getMiembroGremio() != null) {
						GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(perso, "Ea");
						return;
					}
					GestorSalida.ENVIAR_gn_CREAR_GREMIO(perso);
				} catch (Exception e) {}
				break;
			case -1:// abrir banco
				try {
					GestorSQL.SALVAR_PERSONAJE(perso, true);
					if (perso.getDeshonor() >= 1) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "183");
						return;
					}
					int costo = perso.getCostoAbrirBanco();
					if (costo > 0) {
						long nKamas = perso.getKamas() - costo;
						if (nKamas < 0) {
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "1128;" + costo);
							GestorSalida.ENVIAR_M1_MENSAJE_SERVER(perso, "10", "", "");
							return;
						}
						perso.setKamas(nKamas);
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "020;" + costo);
					}
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso, 5, "");
					GestorSalida.ENVIAR_EL_LISTA_OBJETOS_BANCO(perso);
					perso.setOcupado(true);
					perso.setEnBanco(true);
				} catch (Exception e) {}
				break;
			case 0:// Teleportar
				try {
					short nuevoMapaID = Short.parseShort(_args.split(",", 2)[0]);
					int nuevaCeldaID = Integer.parseInt(_args.split(",", 2)[1]);
					perso.teleport(nuevoMapaID, nuevaCeldaID);
				} catch (Exception e) {
					return;
				}
				break;
			case 1:// hablar NPC
				try {
					if (_args.equalsIgnoreCase("DV")) {
						GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(out);
						perso.setConversandoCon(0);
					} else {
						int qID = -1;
						try {
							qID = Integer.parseInt(_args);
						} catch (NumberFormatException e) {}
						PreguntaNPC quest = MundoDofus.getNPCPregunta(qID);
						if (quest == null) {
							GestorSalida.ENVIAR_DV_FINALIZAR_DIALOGO(out);
							perso.setConversandoCon(0);
							return;
						}
						GestorSalida.ENVIAR_DQ_DIALOGO_PREGUNTA(out, quest.stringArgParaDialogo(perso));
					}
				} catch (Exception e) {}
				break;
			case 2: // agrega objetos al azar
				try {
					String quitar = _args.split(";")[0];
					String[] azar = _args.split(";")[1].split("\\|");
					int id = Integer.parseInt(quitar.split(",")[0]);
					int cant = Integer.parseInt(quitar.split(",")[1]);
					if (cant < 0) {
						perso.removerObjetoPorModYCant(id, -cant);
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + -cant + "~" + id);
					}
					String objetoazar = azar[Formulas.getRandomValor(0, azar.length - 1)];
					int ID = Integer.parseInt(objetoazar.split(",")[0]);
					int cantidad = Integer.parseInt(objetoazar.split(",")[1]);
					boolean enviar = true;
					if (objetoazar.split(",").length > 2)
						enviar = objetoazar.split(",")[2].equals("1");
					if (cantidad > 0) {
						ObjetoModelo OM = MundoDofus.getObjModelo(ID);
						if (OM == null)
							return;
						Objeto obj = OM.crearObjDesdeModelo(cantidad, false);
						if (!perso.addObjetoSimilar(obj, true, -1)) {
							MundoDofus.addObjeto(obj, true);
							perso.addObjetoPut(obj);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, obj);
						}
					} else {
						perso.removerObjetoPorModYCant(ID, -cantidad);
					}
					if (perso.enLinea()) {
						if (enviar) {
							if (cantidad >= 0) {
								GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;" + cantidad + "~" + ID);
							} else if (cantidad < 0) {
								GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + -cantidad + "~" + ID);
							}
						}
					}
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
				} catch (Exception e) {}
				break;
			case 4:// agregar o quitar Kamas
				try {
					long cant = Integer.parseInt(_args);
					long tempKamas = perso.getKamas();
					long nuevasKamas = tempKamas + cant;
					if (nuevasKamas < 0)
						nuevasKamas = 0;
					perso.setKamas(nuevasKamas);
					if (perso.enLinea())
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				} catch (Exception e) {}
				break;
			case 5:// agregar o quitar objeto
				try {
					int id = Integer.parseInt(_args.split(",")[0]);
					int cant = Integer.parseInt(_args.split(",")[1]);
					boolean send = true;
					if (_args.split(",").length > 2)
						send = _args.split(",")[2].equals("1");
					if (cant > 0) {
						ObjetoModelo OM = MundoDofus.getObjModelo(id);
						if (OM == null)
							return;
						Objeto obj = OM.crearObjDesdeModelo(cant, false);
						if (!perso.addObjetoSimilar(obj, true, -1)) {
							MundoDofus.addObjeto(obj, true);
							perso.addObjetoPut(obj);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, obj);
						}
					} else {
						perso.removerObjetoPorModYCant(id, -cant);
					}
					if (perso.enLinea()) {
						GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
						if (send) {
							if (cant >= 0) {
								GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;" + cant + "~" + id);
							} else if (cant < 0) {
								GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + -cant + "~" + id);
							}
						}
					}
				} catch (Exception e) {}
				break;
			case 6:// Aprender un oficio
				try {
					int mID = Integer.parseInt(_args);
					if (MundoDofus.getOficio(mID) == null)
						return;
					if (mID == 2 || mID == 11 || mID == 13 || mID == 14 || mID == 15 || mID == 16 || mID == 17 || mID == 18
							|| mID == 19 || mID == 20 || mID == 24 || mID == 25 || mID == 26 || mID == 27 || mID == 28
							|| mID == 31 || mID == 36 || mID == 41 || mID == 56 || mID == 58 || mID == 60 || mID == 65) {
						if (perso.getOficioPorID(mID) != null) {
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "111");
							return;
						}
						if (perso.totalOficiosBasicos() > 2) {
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "19");
							return;
						} else {
							perso.aprenderOficio(MundoDofus.getOficio(mID));
							return;
						}
					}
					if (mID == 43 || mID == 44 || mID == 45 || mID == 46 || mID == 47 || mID == 48 || mID == 49 || mID == 50
							|| mID == 62 || mID == 63 || mID == 64) {
						if (perso.getOficioPorID(17) != null && perso.getOficioPorID(17).getNivel() >= 65 && mID == 43
								|| perso.getOficioPorID(11) != null && perso.getOficioPorID(11).getNivel() >= 65 && mID == 44
								|| perso.getOficioPorID(14) != null && perso.getOficioPorID(14).getNivel() >= 65 && mID == 45
								|| perso.getOficioPorID(20) != null && perso.getOficioPorID(20).getNivel() >= 65 && mID == 46
								|| perso.getOficioPorID(31) != null && perso.getOficioPorID(31).getNivel() >= 65 && mID == 47
								|| perso.getOficioPorID(13) != null && perso.getOficioPorID(13).getNivel() >= 65 && mID == 48
								|| perso.getOficioPorID(19) != null && perso.getOficioPorID(19).getNivel() >= 65 && mID == 49
								|| perso.getOficioPorID(18) != null && perso.getOficioPorID(18).getNivel() >= 65 && mID == 50
								|| perso.getOficioPorID(15) != null && perso.getOficioPorID(15).getNivel() >= 65 && mID == 62
								|| perso.getOficioPorID(16) != null && perso.getOficioPorID(16).getNivel() >= 65 && mID == 63
								|| perso.getOficioPorID(27) != null && perso.getOficioPorID(27).getNivel() >= 65 && mID == 64) {
							if (perso.getOficioPorID(mID) != null) {
								GestorSalida.ENVIAR_Im_INFORMACION(perso, "111");
								return;
							}
							if (perso.totalOficiosFM() > 2) {
								GestorSalida.ENVIAR_Im_INFORMACION(perso, "19");
								return;
							} else {
								perso.aprenderOficio(MundoDofus.getOficio(mID));
								// perso.getOficioPorID(mID).addXp(perso, 234001);// Level 61 directo
								return;
							}
						} else
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "12");
					}
				} catch (Exception e) {}
				break;
			case 7:// retornar al punto de salvada
				if (perso.getPelea() == null)
					perso.retornoPtoSalvadaPocima();
				break;
			case 8:// Agregar un stat
				try {
					int statID = Integer.parseInt(_args.split(",", 2)[0]);
					int cantidad = Integer.parseInt(_args.split(",", 2)[1]);
					int mensajeID = 0;
					switch (statID) {
						case 124:// sabiduria
							if (perso.getScrollSabiduria() >= 500) {
								GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
										"Llegaste al máximo de scroll en esta característica.");
								return;
							}
							perso.addScrollSabiduria(cantidad);
							mensajeID = 9;
							break;
						case 118:// fuerza
							if (perso.getScrollFuerza() >= 500) {
								GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
										"Llegaste al máximo de scroll en esta característica.");
								return;
							}
							perso.addScrollFuerza(cantidad);
							mensajeID = 10;
							break;
						case 123:// suerte
							if (perso.getScrollSuerte() >= 500) {
								GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
										"Llegaste al máximo de scroll en esta característica.");
								return;
							}
							perso.addScrollSuerte(cantidad);
							mensajeID = 11;
							break;
						case 119:// agilidad
							if (perso.getScrollAgilidad() >= 500) {
								GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
										"Llegaste al máximo de scroll en esta característica.");
								return;
							}
							perso.addScrollAgilidad(cantidad);
							mensajeID = 12;
							break;
						case 125:// vitalidad
							if (perso.getScrollVitalidad() >= 500) {
								GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
										"Llegaste al máximo de scroll en esta característica.");
								return;
							}
							perso.addScrollVitalidad(cantidad);
							mensajeID = 13;
							break;
						case 126:// inteligencia
							if (perso.getScrollInteligencia() >= 500) {
								GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
										"Llegaste al máximo de scroll en esta característica.");
								return;
							}
							perso.addScrollInteligencia(cantidad);
							mensajeID = 14;
							break;
					}
					perso.getBaseStats().addUnStat(statID, cantidad);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
					if (mensajeID > 0)
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "0" + mensajeID + ";" + cantidad);
				} catch (Exception e) {}
				break;
			case 9:// Aprender un hechizo
				try {
					int sID = Integer.parseInt(_args);
					if (MundoDofus.getHechizo(sID) == null)
						return;
					perso.aprenderHechizo(sID, 1, false, true);
				} catch (Exception e) {}
				break;
			case 10:// Curar
				try {
					int min = Integer.parseInt(_args.split(",", 2)[0]);
					int max = Integer.parseInt(_args.split(",", 2)[1]);
					if (max == 0)
						max = min;
					int val = Formulas.getRandomValor(min, max);
					if (objetivo != null) {
						if (objetivo.getPDV() + val > objetivo.getPDVMAX())
							val = objetivo.getPDVMAX() - objetivo.getPDV();
						objetivo.setPDV(objetivo.getPDV() + val);
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(objetivo);
					} else {
						if (perso.getPDV() + val > perso.getPDVMAX())
							val = perso.getPDVMAX() - perso.getPDV();
						perso.setPDV(perso.getPDV() + val);
						GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
						if (perso.getPelea() != null) {
							GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES(perso.getPelea(), perso);
						}
					}
				} catch (Exception e) {}
				break;
			case 11:// Definir una alineacion
				try {
					byte nuevaAlin = Byte.parseByte(_args.split(",", 2)[0]);
					boolean remplaza = Integer.parseInt(_args.split(",", 2)[1]) == 1;
					if (perso.getAlineacion() != CentroInfo.ALINEACION_NEUTRAL && !remplaza)
						return;
					perso.modificarAlineamiento(nuevaAlin);
				} catch (Exception e) {}
				break;
			case 12:// Spawn de un grupo de mounstros(lanzar una piedra del alma)
				try {
					boolean delObj = _args.split(",")[0].equals("true");
					boolean enArena = _args.split(",")[1].equals("true");
					if (enArena && !perso.getMapa().esArena()) {
						GestorSalida.ENVIAR_Im_INFORMACION(out, "113");
						return;
					}
					PiedraDeAlma piedraAlma = (PiedraDeAlma) MundoDofus.getObjeto(objUsadoID);
					String grupoMobs = piedraAlma.analizarGrupo();
					String condicion = "MiS = " + perso.getID();
					perso.getMapa().addGrupoTimer(true, perso.getCelda().getID(), grupoMobs, condicion);
					if (delObj) {
						perso.borrarObjetoEliminar(objUsadoID, 1, true);
					}
				} catch (Exception e) {}
				break;
			case 13: // Resetear caracteristicas
				try {
					perso.resetearStats();
					perso.addCapital( (perso.getNivel() - 1) * 5 - perso.getCapital());
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				} catch (Exception e) {}
				break;
			case 14:// Olvidar y recuperar los puntos de un hechizo
				try {
					perso.setOlvidandoHechizo(true);
					GestorSalida.GAME_SEND_FORGETSPELL_INTERFACE('+', perso);
				} catch (Exception e) {}
				break;
			case 15:// Teleportar a una mazmorra con llave
				try {
					short nuevoMapaID = Short.parseShort(_args.split(",")[0]);
					int nuevaCeldaID = Integer.parseInt(_args.split(",")[1]);
					int objNecesario = Integer.parseInt(_args.split(",")[2]);
					int mapaNecesario = Integer.parseInt(_args.split(",")[3]);
					if (objNecesario == 0) {
						perso.teleport(nuevoMapaID, nuevaCeldaID);
						return;
					} else if (objNecesario > 0) {
						if (perso.tieneObjModeloNoEquip(objNecesario, 1)) {
							perso.removerObjetoPorModYCant(objNecesario, 1);
							GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
							if (mapaNecesario == 0) {
								perso.teleport(nuevoMapaID, nuevaCeldaID);
							} else if (mapaNecesario > 0) {
								if (perso.getMapa().getID() == mapaNecesario) {
									perso.teleport(nuevoMapaID, nuevaCeldaID);
								} else if (perso.getMapa().getID() != mapaNecesario) {
									GestorSalida.ENVIAR_Im_INFORMACION(perso, "113");
								}
							}
							return;
						} else {
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "14|45");
							return;
						}
					}
				} catch (Exception e) {}
				break;
			case 16:// Agregar honor
				if (perso.getAlineacion() != 0) {
					int addHonor = Integer.parseInt(_args);
					perso.addHonor(addHonor);
				}
				break;
			case 17:// Xp de oficio
				int JobID = Integer.parseInt(_args.split(",")[0]);
				int XpValue = Integer.parseInt(_args.split(",")[1]);
				if (perso.getOficioPorID(JobID) != null) {
					perso.getOficioPorID(JobID).addXP(perso, XpValue);
				}
				break;
			case 18:
				if (Casa.tieneOtraCasa(perso)) {
					Objeto obj = MundoDofus.getObjeto(objUsadoID);
					if (perso.tieneObjModeloNoEquip(obj.getModelo().getID(), 1)) {
						perso.removerObjetoPorModYCant(obj.getModelo().getID(), 1);
						Casa h = Casa.getCasaDePj(perso);
						if (h == null)
							return;
						perso.teleport((short) h.getMapaIDDentro(), h.getCeldaIDDentro());
					}
				}
				break;
			case 19:// Teleport a la casa del gremio (abrir desde el panel de gremio)
				GestorSalida.GAME_SEND_GUILDHOUSE_PACKET(perso);
				break;
			case 20:// + Puntos de hechizo
				int pts = Integer.parseInt(_args);
				if (pts < 1)
					return;
				perso.addPuntosHechizos(pts);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				break;
			case 21:// + Energia
				int energia = Integer.parseInt(_args);
				if (energia < 1)
					return;
				perso.agregarEnergia(energia);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				break;
			case 22:// + Xp
				long expAgregar = Integer.parseInt(_args);
				if (expAgregar < 1)
					return;
				long totalXp = perso.getExperiencia() + expAgregar;
				perso.setExperiencia(totalXp);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				break;
			case 23:// Olvidar oficio
				int oficio = Integer.parseInt(_args);
				if (oficio < 1)
					return;
				StatsOficio statsOficio = perso.getOficioPorID(oficio);
				if (statsOficio == null)
					return;
				int pos = statsOficio.getPosicion();
				perso.olvidarOficio(pos);
				GestorSalida.ENVIAR_JR_OLVIDAR_OFICIO(perso, oficio);
				GestorSQL.SALVAR_PERSONAJE(perso, false);
				break;
			case 24:// te da otro gfx
				int gfxID = Integer.parseInt(_args);
				if (gfxID < 0)
					return;
				perso.setGfxID(gfxID);
				GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
				GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(perso.getMapa(), perso);
				break;
			case 25:// regresar al gfx original
				int gfxOriginal = perso.getClase(true) * 10 + perso.getSexo();
				perso.setGfxID(gfxOriginal);
				GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
				GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(perso.getMapa(), perso);
				break;
			case 26:// Teleportar al cercado del gremio
				GestorSalida.GAME_SEND_GUILDENCLO_PACKET(perso);
				break;
			case 27:// lanza una pelea contra un determinado mob
				try {
					String mobGrupo = "";
					for (String mobYNivel : _args.split("\\|")) {
						int mobID = -1;
						int mobNivel = -1;
						String[] mobONivel = mobYNivel.split(",");
						mobID = Integer.parseInt(mobONivel[0]);
						mobNivel = Integer.parseInt(mobONivel[1]);
						if (MundoDofus.getMobModelo(mobID) == null
								|| MundoDofus.getMobModelo(mobID).getGradoPorNivel(mobNivel) == null) {
							System.out.println("MobGrupo invalido mobID:" + mobID + " mobNivel:" + mobNivel);
							continue;
						}
						mobGrupo += mobID + "," + mobNivel + "," + mobNivel + ";";
					}
					if (mobGrupo.isEmpty())
						return;
					GrupoMobs grupo = new GrupoMobs(perso.getMapa()._sigIDMapaInfo, perso.getCelda().getID(), mobGrupo);
					perso.getMapa().iniciarPeleaVSMobs(perso, grupo);
				} catch (Exception e) {}
				break;
			case 28: // subir / baja montura
				try {
					if (perso.getMontura() != null) {
						if (perso.getMontura().esMontable() == 1)
							perso.subirBajarMontura();
						else
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "1176");
					} else
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "MOUNT_NO_EQUIP");
				} catch (Exception e) {}
				break;
			case 29: // puntos de sala de entrenamiento
				try {
					int mapa = perso.getMapa().getID();
					switch (mapa) {
						case 13105:
							if (perso.getBaseStats().getEfecto(118) < 21)
								perso.getBaseStats().addUnStat(118, 1);
							break;
						case 13125:
							if (perso.getBaseStats().getEfecto(126) < 21)
								perso.getBaseStats().addUnStat(126, 1);
							break;
						case 13145:
							if (perso.getBaseStats().getEfecto(119) < 21)
								perso.getBaseStats().addUnStat(119, 1);
							break;
						case 13165:
							if (perso.getBaseStats().getEfecto(123) < 21)
								perso.getBaseStats().addUnStat(123, 1);
							break;
						case 13110:
							if (perso.getBaseStats().getEfecto(118) < 41)
								perso.getBaseStats().addUnStat(118, 1);
							break;
						case 13130:
							if (perso.getBaseStats().getEfecto(126) < 41)
								perso.getBaseStats().addUnStat(126, 1);
							break;
						case 13150:
							if (perso.getBaseStats().getEfecto(119) < 41)
								perso.getBaseStats().addUnStat(119, 1);
							break;
						case 13170:
							if (perso.getBaseStats().getEfecto(123) < 41)
								perso.getBaseStats().addUnStat(123, 1);
							break;
						case 13115:
							if (perso.getBaseStats().getEfecto(118) < 81)
								perso.getBaseStats().addUnStat(118, 1);
							break;
						case 13135:
							if (perso.getBaseStats().getEfecto(126) < 81)
								perso.getBaseStats().addUnStat(126, 1);
							break;
						case 13155:
							if (perso.getBaseStats().getEfecto(119) < 81)
								perso.getBaseStats().addUnStat(119, 1);
							break;
						case 13175:
							if (perso.getBaseStats().getEfecto(123) < 81)
								perso.getBaseStats().addUnStat(123, 1);
							break;
						case 13120:
							if (perso.getBaseStats().getEfecto(118) < 101)
								perso.getBaseStats().addUnStat(118, 1);
							break;
						case 13140:
							if (perso.getBaseStats().getEfecto(126) < 101)
								perso.getBaseStats().addUnStat(126, 1);
							break;
						case 13160:
							if (perso.getBaseStats().getEfecto(119) < 101)
								perso.getBaseStats().addUnStat(119, 1);
							break;
						case 13180:
							if (perso.getBaseStats().getEfecto(123) < 101)
								perso.getBaseStats().addUnStat(123, 1);
							break;
					}
					perso.teleport((short) 6954, 268);
				} catch (Exception e) {}
				break;
			case 30: // refrescar mobs
				perso.getMapa().refrescarGrupoMobs();
				break;
			case 31:// cambiar clase
				try {
					if (perso.getEncarnacion() != null)
						return;
					int clase = Integer.parseInt(_args);
					if (clase == perso.getClase(true)) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "Usted ya pertenece a esta clase.");
						return;
					}
					int nivel = perso.getNivel();
					perso.setClase(clase);
					perso.resetearStats();
					Thread.sleep(150);
					perso.setCapital(0);
					perso.setPtosHechizos(0);
					perso.setHechizos(CentroInfo.getHechizosIniciales(clase));
					Thread.sleep(150);
					perso.setNivel(1);
					while (perso.getNivel() < nivel) {
						perso.subirNivel(false, false);
					}
					perso.deformar();
					GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(perso.getMapa(), perso);
					GestorSalida.ENVIAR_ASK_PERSONAJE_SELECCIONADO(out, perso);
					GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(perso);
					Thread.sleep(150);
					GestorSQL.CAMBIAR_SEXO_CLASE(perso);
				} catch (Exception e) {}
				break;
			case 32: // cambiar sexo
				try {
					perso.cambiarSexo();
					Thread.sleep(300);
					perso.deformar();
					GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(perso.getMapa(), perso);
					GestorSQL.CAMBIAR_SEXO_CLASE(perso);
				} catch (Exception e) {}
				break;
			case 33: // caramelo boost
				int objBoost = Integer.parseInt(_args);
				Objeto obje = perso.getObjPosicion(20);
				if (obje != null) {
					int idObj = obje.getID();
					perso.borrarObjetoRemove(idObj);
					MundoDofus.eliminarObjeto(idObj);
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(out, idObj);
				}
				Objeto nuevo = MundoDofus.getObjModelo(objBoost).crearObjPosDesdeModelo(1, 20, false);
				if (!perso.addObjetoSimilar(nuevo, true, -1)) {
					MundoDofus.addObjeto(nuevo, true);
					perso.addObjetoPut(nuevo);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, nuevo);
				}
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				break;
			case 34:// perfecciona un objeto
				int posAMover = Integer.parseInt(_args);
				Objeto objetoPos = perso.getObjPosicion(posAMover);
				if (objetoPos != null) {
					String maxStats = ObjetoModelo.generarStatsModeloDB(objetoPos.getModelo().getStringStatsObj(), true);
					objetoPos.clearTodo();
					objetoPos.convertirStringAStats(maxStats);
					perso.borrarObjetoEliminar(objUsadoID, 1, true);
					GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(out, objetoPos);
					GestorSQL.SALVAR_OBJETO(objetoPos);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				} else {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "No se encontró un objeto en dicha posición.");
				}
				break;
			case 35:// pescar Kuakua
				try {
					int kamasApostar = Integer.parseInt(_args);
					long tempKamas = perso.getKamas();
					if (tempKamas < kamasApostar) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "182");
						return;
					}
					perso.setKamas(tempKamas - kamasApostar);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
					perso.setPescarKuakua(true);
				} catch (Exception e) {}
				break;
			case 36:// precio por tutorial
				try {
					long precio = Integer.parseInt(_args.split(";")[0]);
					int tutorial = Integer.parseInt(_args.split(";")[1]);
					if (tutorial == 30) {
						int aleatorio = Formulas.getRandomValor(1, 200);
						if (aleatorio == 100)
							tutorial = 31;
					}
					Tutorial tuto = MundoDofus.getTutorial(tutorial);
					if (tuto == null)
						return;
					if (perso.getKamas() >= precio) {
						if (precio != 0) {
							perso.setKamas(perso.getKamas() - precio);
							GestorSalida.ENVIAR_Im_INFORMACION(perso, "046;" + precio);
						}
						try {
							tuto.getInicio().aplicar(perso, null, -1, -1);
						} catch (Exception e) {}
						Thread.sleep(1500);
						GestorSalida.enviar(perso, "TC" + tutorial + "|7001010000");
						perso.setTutorial(tuto);
						perso.setOcupado(true);
						return;
					} else {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "182");
						return;
					}
				} catch (Exception e) {}
				break;
			case 37:// dar un objeto al azar, por un objeto necesario
				try {
					String[] strs = _args.split("\\|");
					String[] strs2 = strs[1].split(",");
					int objNecesario = Integer.parseInt(strs[0]);
					if (perso.tieneObjModeloNoEquip(objNecesario, 1)) {
						perso.removerObjetoPorModYCant(objNecesario, 1);
						int objNuevo = Integer.parseInt(strs2[Formulas.getRandomValor(0, strs2.length - 1)]);
						Objeto nuevoObj = MundoDofus.getObjModelo(objNuevo).crearObjDesdeModelo(1, false);
						if (!perso.addObjetoSimilar(nuevoObj, true, -1)) {
							MundoDofus.addObjeto(nuevoObj, true);
							perso.addObjetoPut(nuevoObj);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, nuevoObj);
						}
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + 1 + "~" + objNecesario);
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;" + 1 + "~" + objNuevo);
					} else {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "134|43");
					}
				} catch (Exception e) {}
				break;
			case 38:// trasladar de un lado a otro a un pj
				try {
					int nuevaCelda = Integer.parseInt(_args);
					Mapa mapa = perso.getMapa();
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapa, perso.getID());
					perso.setCelda(mapa.getCelda(nuevaCelda));
					GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(perso.getMapa(), perso);
				} catch (Exception e) {}
				break;
			case 39:// ganar ruleta jalato
				try {
					NPCModelo npcModelo = MundoDofus.getNPCModelo(408);
					perso.setKamas(perso.getKamas() + npcModelo.getKamas());
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
					npcModelo.setKamas(100000);
				} catch (Exception e) {}
				break;
			case 40:// paga al npc de jalato
				try {
					NPCModelo npcModelo = MundoDofus.getNPCModelo(408);
					npcModelo.setKamas(npcModelo.getKamas() + 1000);
				} catch (Exception e) {}
				break;
			case 41:// realizar pelea dopeul
				try {
					String mobGrupo = "";
					int mobID = Integer.parseInt(_args);
					int mobNivel = ( ( (perso.getNivel() - 1) / 20) + 1) * 20;
					if (MundoDofus.getMobModelo(mobID) == null
							|| MundoDofus.getMobModelo(mobID).getGradoPorNivel(mobNivel) == null) {
						System.out.println("MobGrupo invalido mobID:" + mobID + " mobNivel:" + mobNivel);
						return;
					}
					mobGrupo += mobID + "," + mobNivel + "," + mobNivel + ";";
					GrupoMobs grupo = new GrupoMobs(perso.getMapa()._sigIDMapaInfo, perso.getCelda().getID(), mobGrupo);
					perso.getMapa().iniciarPeleaVSDopeul(perso, grupo);
				} catch (Exception e) {}
				break;
			case 50:// Agresion Mision
				if (perso.getAlineacion() == 0 || perso.getAlineacion() == 3) {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "134");
					return;
				}
				if (perso.getMisionPVP() == null) {
					MisionPVP mision = new MisionPVP(0, null);
					perso.setMisionPVP(mision);
				}
				if (System.currentTimeMillis() - perso.getMisionPVP().getTiempoPVP() > 600000
						|| perso.getMisionPVP().getTiempoPVP() == 0) {
					Personaje tempP = null;
					ArrayList<Personaje> victimas = new ArrayList<Personaje>();
					for (Personaje victima : MundoDofus.getPJsEnLinea()) {
						if (victima == null || victima == perso)
							continue;
						if (victima.getAlineacion() == perso.getAlineacion() || victima.getAlineacion() == 0
								|| victima.getAlineacion() == 3 || !victima.mostrarAlas())
							continue;
						if (victima.getCuenta().getActualIP().compareTo(perso.getCuenta().getActualIP()) == 0)
							continue;
						if (victima.getNombre().equalsIgnoreCase(perso.getUltimaMision()))
							continue;
						if ( ( (perso.getNivel() + 20) >= victima.getNivel()) && ( (perso.getNivel() - 20) <= victima.getNivel()))
							victimas.add(victima);
					}
					if (victimas.size() == 0) {
						GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
								"<b>[Thomas Sacre]</b> No hemos encontrado un objetivo a tu altura. Porfavor vuelve más tarde.",
								"000000");
						break;
					}
					tempP = victimas.get(Formulas.getRandomValor(0, victimas.size() - 1));
					String nombreVict = tempP.getNombre();
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "<b>[Thomas Sacre]</b> Usted esta ahora a la caza de "
							+ nombreVict + ".", "000000");
					perso.setUltimaMision(nombreVict);
					perso.getMisionPVP().setPjMision(tempP);
					perso.getMisionPVP().setTiempoPVP(System.currentTimeMillis());
					ObjetoModelo objModelo = MundoDofus.getObjModelo(10085);
					Objeto nuevoObj = objModelo.crearObjDesdeModelo(20, false);
					nuevoObj.addTextoStat(989, nombreVict);
					nuevoObj.addTextoStat(961, "" + Integer.toHexString(tempP.getNivelAlineacion()));
					nuevoObj.addTextoStat(962, "" + Integer.toHexString(tempP.getNivel()));
					Objeto pergamino = perso.getObjModeloNoEquip(10085, 1);
					if (pergamino != null) {
						pergamino.setCantidad(20);
						pergamino.convertirStringAStats(nuevoObj.convertirStatsAString());
						GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(out, pergamino);
					} else {
						MundoDofus.addObjeto(nuevoObj, true);
						perso.addObjetoPut(nuevoObj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, nuevoObj);
					}
				} else {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							"<b>[Thomas Sacre]</b> Usted acaba de terminar un contrato, por ahora debes descansar 10 minutos.",
							"000000");
				}
				break;
			case 51:// objetivo en la geoposicion(del jugador a cazar)
				if (perso.getPelea() != null || perso.esFantasma()) {
					return;
				}
				String nombreCazar = "";
				nombreCazar = MundoDofus.getObjeto(objUsadoID).getNombreMision();
				if (nombreCazar == null) {
					return;
				}
				Personaje victima = MundoDofus.getPjPorNombre(nombreCazar);
				try {
					if (victima == null || !victima.enLinea()) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "1211");
						return;
					}
					if (victima.esFantasma()) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "El objetivo es fantasma / L'objectif es fantome");
						return;
					}
					if (!victima.estaMostrandoAlas()) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "1195");
						return;
					}
					if (victima.getPelea() != null) {
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
								"El objetivo esta en una pelea / L'objectif est dans un combat");
						return;
					}
					long tiempo = System.currentTimeMillis();
					if (!victima.getHuir()) {
						if ( (tiempo - victima.getTiempoAgre()) > 10000)
							victima.setHuir(true);
						else {
							GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
									"El objetivo esta en una pelea PVP / L'objectif est dans un combat PVP");
							return;
						}
					}
					Short[] mapas = { 4422, 7810, 952, 1887, 833 };
					short mapa = mapas[Formulas.getRandomValor(0, 4)];
					perso.teleport(mapa, 399);
					victima.teleport(mapa, 194);
					perso.setHuir(false);
					victima.setHuir(false);
					perso.setTiempoAgre(tiempo);
					victima.setTiempoAgre(tiempo);
				}
				// GestorSalida.GAME_SEND_FLAG_PACKET(perso, persoGeo);
				catch (NullPointerException e) {
					return;
				}
				break;
			case 52:// recompensar por traque(persecucion)
				if (perso.getMisionPVP() != null && perso.getMisionPVP().getTiempoPVP() == -2) {
					int xp = Formulas.getXPMision(perso.getNivel());
					perso.addExp(xp);
					perso.setMisionPVP(null);
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso, "Recibes " + xp + " puntos de experiencia.", "000000");
				} else {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(perso,
							" <b>[Thomas Sacre]</b> Regresa a mi cuando hayas abatido al enemigo.", "000000");
				}
				break;
			case 53:// propone intercambio desde un NPC
				try {
					String objetodar = _args.split(";")[0];
					String objetopedir = _args.split(";")[1];
					Trueque trueque = new Trueque(perso, objetopedir, objetodar);
					perso.setTrueque(trueque);
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(out, 9, "");
				} catch (Exception e) {}
				break;
			case 54:// hechar automaticamente puntos caracteristica
				try {
					if (_args.isEmpty())
						return;
					int Args = Integer.parseInt(_args);
					if (Args == 10 || Args == 11 || Args == 12 || Args == 13 || Args == 14 || Args == 15)
						perso.boostStat2(Args);
				} catch (Exception e) {}
				break;
			case 55:// pagar para ejecutar una accion
				try {
					if (_args.isEmpty())
						return;
					long precio = Integer.parseInt(_args.split(";")[0]);
					int accion = Integer.parseInt(_args.split(";")[1]);
					if (perso.getKamas() >= precio) {
						perso.setKamas(perso.getKamas() - precio);
						(new Accion(accion, "", "")).aplicar(perso, null, -1, -1);
						break;
					} else {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "182");
						return;
					}
				} catch (Exception e) {}
				break;
			case 56: // kita y agrega objeto
				try {
					String quitar = _args.split(";")[0];
					String agregar = _args.split(";")[1];
					int objModelo = Integer.parseInt(quitar.split(",")[0]);
					int cant = Integer.parseInt(quitar.split(",")[1]);
					int objNuevo = Integer.parseInt(agregar.split(",")[0]);
					int cantNuevo = Integer.parseInt(agregar.split(",")[1]);
					if (perso.tieneObjModeloNoEquip(objModelo, cant)) {
						perso.removerObjetoPorModYCant(objModelo, cant);
						Objeto nuevoObj = MundoDofus.getObjModelo(objNuevo).crearObjDesdeModelo(cantNuevo, false);
						if (!perso.addObjetoSimilar(nuevoObj, true, -1)) {
							MundoDofus.addObjeto(nuevoObj, true);
							perso.addObjetoPut(nuevoObj);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, nuevoObj);
						}
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + cant + "~" + objModelo);
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;" + cantNuevo + "~" + objNuevo);
					} else {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "134|43");
					}
				} catch (Exception e) {}
				break;
			case 57:// cambiar de nombre
				try {
					GestorSalida.ENVIAR_AlE_CAMBIAR_NOMBRE(out, "r");
					perso.cambiarNombre(true);
				} catch (Exception e) {}
				break;
			case 58: // agrega varios objetos, y kitar solo 1
				try {
					String quitar = _args.split(";")[0];
					String[] azar = _args.split(";")[1].split("\\|");
					int id = Integer.parseInt(quitar.split(",")[0]);
					int cant = Integer.parseInt(quitar.split(",")[1]);
					if (cant < 0) {
						perso.removerObjetoPorModYCant(id, -cant);
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + -cant + "~" + id);
					}
					for (String objetoazar : azar) {
						int ID = Integer.parseInt(objetoazar.split(",")[0]);
						int cantidad = Integer.parseInt(objetoazar.split(",")[1]);
						boolean enviar = true;
						if (objetoazar.split(",").length > 2)
							enviar = objetoazar.split(",")[2].equals("1");
						if (cantidad > 0) {
							ObjetoModelo OM = MundoDofus.getObjModelo(ID);
							if (OM == null)
								return;
							Objeto obj = OM.crearObjDesdeModelo(cantidad, false);
							if (!perso.addObjetoSimilar(obj, true, -1)) {
								MundoDofus.addObjeto(obj, true);
								perso.addObjetoPut(obj);
								GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(out, obj);
							}
						} else {
							perso.removerObjetoPorModYCant(ID, -cantidad);
						}
						if (perso.enLinea()) {
							if (enviar) {
								if (cantidad >= 0) {
									GestorSalida.ENVIAR_Im_INFORMACION(perso, "021;" + cantidad + "~" + ID);
								} else if (cantidad < 0) {
									GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + -cantidad + "~" + ID);
								}
							}
						}
					}
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
				} catch (Exception e) {}
				break;
			case 100:// dar habilidad
				if (perso.getMontura() != null) {
					int habilidad = 0;
					if (_args.split(",").length == 2)
						habilidad = Formulas.getRandomValor(Integer.parseInt(_args.split(",")[0]),
								Integer.parseInt(_args.split(",")[1]));
					else
						habilidad = Integer.parseInt(_args);
					Dragopavo montura = perso.getMontura();
					montura.setHabilidad(habilidad + "");
					perso.setMontura(montura);
					GestorSalida.ENVIAR_Re_DETALLES_MONTURA(perso, "+", MundoDofus.getDragopavoPorID(montura.getID()));
					GestorSQL.ACTUALIZAR_MONTURA(montura, false);
				}
				break;
			case 101:// llegar a la casilla de mi esposo(pregunta del sacerdote)
				if ( (perso.getSexo() == 0 && perso.getCelda().getID() == 282)
						|| (perso.getSexo() == 1 && perso.getCelda().getID() == 297)) {
					MundoDofus.addEsposo(perso.getSexo(), perso);
				} else {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "1102");
				}
				break;
			case 102:// Preguntar si desean casarse
				MundoDofus.discursoSacerdote(perso, perso.getMapa(), perso.getConversandoCon());
				break;
			case 103:// Divorciar
				if (perso.getKamas() < 50000) {
					return;
				} else {
					perso.setKamas(perso.getKamas() - 50000);
					Personaje wife = MundoDofus.getPersonaje(perso.getEsposo());
					wife.divorciar();
					perso.divorciar();
				}
				break;
			case 104:// ganar energia (caramelo)
				int energy = Integer.parseInt(_args);
				perso.agregarEnergia(energy);
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
				break;
			case 200:// poner un objeto en el mapa
				Mapa mapa = perso.getMapa();
				// int durabilidad = Integer.parseInt(args);
				int idModelo = MundoDofus.getObjeto(objUsadoID).getModelo().getID();
				if (mapa.getCercado() == null)
					return;
				Cercado cercado = mapa.getCercado();
				if (!perso.getNombre().equalsIgnoreCase("Elbusta")) {
					if (perso.getGremio() == null) {
						GestorSalida.ENVIAR_BN_NADA(perso);
						return;
					}
					if (!perso.getMiembroGremio().puede(8192)) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "193");
						return;
					}
					if (cercado.getCeldasObj().size() == 0 || !cercado.getCeldasObj().contains(celda)) {
						GestorSalida.ENVIAR_BN_NADA(perso);
						return;
					}
				}
				if (cercado.getCantObjColocados() < cercado.getCantObjMax()) {
					cercado.addObjetoCria(celda, idModelo, perso.getID());
					GestorSalida.ENVIAR_GDO_PONER_OBJETO_CRIA(mapa, celda + ";" + idModelo + ";1;1000;1000");
					perso.borrarObjetoEliminar(objUsadoID, 1, true);
				} else
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso,
							"Ya llegaste al máximo de objetos de crianza en este cercado");
				break;
			case 201:// colocar un primsa
				try {
					int celdapj = perso.getCelda().getID();
					Mapa tMapa = perso.getMapa();
					SubArea subarea = tMapa.getSubArea();
					Area area = subarea.getArea();
					int alineacion = perso.getAlineacion();
					if (celdapj <= 0) {
						return;
					}
					if (perso.getNivelAlineacion() < 3) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "155");
						return;
					}
					if (alineacion == 0 || alineacion == 3) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "134|43");
						return;
					}
					if (!perso.mostrarAlas()) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "1148");
						return;
					}
					if (tMapa.esArena() || tMapa.esCasa() || tMapa.esTaller() || tMapa.getID() > 13000
							|| (tMapa.getAncho() != 15 && tMapa.getAncho() != 19)) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "1146");
						return;
					}
					if (subarea.getAlineacion() != 0 || !subarea.getConquistable()) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "1149");
						return;
					}
					Prisma prisma = new Prisma(MundoDofus.getSigIDPrisma(), alineacion, 1, tMapa.getID(), celdapj, 0, -1);
					subarea.setAlineacion(alineacion);
					subarea.setPrismaID(prisma.getID());
					for (Personaje z : MundoDofus.getPJsEnLinea()) {
						if (z.getAlineacion() == 0) {
							GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z, subarea.getID() + "|" + alineacion + "|1");
							if (area.getAlineacion() == 0)
								GestorSalida.ENVIAR_aM_MENSAJE_ALINEACION_AREA(z, area.getID() + "|" + alineacion);
							continue;
						}
						GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z, subarea.getID() + "|" + alineacion + "|0");
						if (area.getAlineacion() == 0)
							GestorSalida.ENVIAR_aM_MENSAJE_ALINEACION_AREA(z, area.getID() + "|" + alineacion);
					}
					if (area.getAlineacion() == 0) {
						area.setPrismaID(prisma.getID());
						area.setAlineacion(alineacion);
						prisma.setAreaConquistada(area.getID());
					}
					MundoDofus.addPrisma(prisma);
					GestorSQL.AGREGAR_PRISMA(prisma);
					GestorSalida.ENVIAR_GM_PRISMA_A_MAPA(tMapa, prisma);
				} catch (Exception e) {}
				break;
			case 228:// Realizar animacion de juegos artificiales
				int animacionID = Integer.parseInt(_args);
				Animacion animacion = MundoDofus.getAnimacion(animacionID);
				if (perso.getPelea() != null)
					return;
				if (perso.getMapa().getID() == Bustemu.MAPA_LAG && Bustemu.EXPULSAR) {
					perso.getCuenta().getEntradaPersonaje().salir();
					return;
				}
				perso.cambiarOrientacion(1);
				GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso.getMapa(), "0", 228, perso.getID() + ";" + celda + ","
						+ Animacion.preparaAGameAccion(animacion), "");
				break;
			default:
				System.out.println("Accion ID = " + _ID + " no implantado");
				break;
		}
	}
	
	public int getID() {
		return _ID;
	}
}
