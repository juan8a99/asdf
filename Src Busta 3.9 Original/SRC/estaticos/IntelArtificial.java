
package estaticos;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;

import servidor.EntradaPersonaje.AccionDeJuego;
import variables.*;
import variables.Hechizo.StatsHechizos;
import variables.Mapa.Celda;
import variables.Pelea.Luchador;
import variables.Pelea.*;

public class IntelArtificial {
	public static class IAThread implements Runnable {
		private Pelea _pelea;
		private Luchador _atacante;
		private static boolean stop = false;
		private Thread _t;
		
		public IAThread(Luchador atacante, Pelea pelea) {
			_atacante = atacante;
			_pelea = pelea;
			_t = new Thread(this);
			_t.setDaemon(true);
			_t.start();
		}
		
		public void run() {
			stop = false;
			if (_atacante.getMob() == null) {
				if (_atacante.esDoble()) {
					tipo_5(_atacante, _pelea);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
					_pelea.finTurno();
				} else if (_atacante.esRecaudador()) {
					tipo_Recaudador(_atacante, _pelea);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
					_pelea.finTurno();
				} else if (_atacante.esPrisma()) {
					tipo_Prisma(_atacante, _pelea);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
					_pelea.finTurno();
				} else {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
					_pelea.finTurno();
				}
			} else if (_atacante.getMob().getModelo() == null) {
				_pelea.finTurno();
			} else {
				switch (_atacante.getMob().getModelo().getTipoInteligencia()) {
					case 0:// no realiza nada
						tipo_0(_atacante, _pelea);
						break;
					case 1:// general
						tipo_1(_atacante, _pelea);
						break;
					case 2:// esfera xelor
						tipo_2(_atacante, _pelea);
						break;
					case 3:// mobs sala de entrenamiento
						tipo_3(_atacante, _pelea);
						break;
					case 4:// tofu,prespic
						tipo_4(_atacante, _pelea);
						break;
					case 5:// bloqueadora
						tipo_5(_atacante, _pelea);
						break;
					case 6:// hinchable, conejo
						tipo_6(_atacante, _pelea);
						break;
					case 7:// gatake, ataca y solo ataca
						tipo_7(_atacante, _pelea);
						break;
					case 8:// mochila animada
						tipo_8(_atacante, _pelea);
						break;
					case 9:// cofre animado, arbol de la vida
						tipo_9(_atacante, _pelea);
						break;
					case 10:// cascara explosiva
						tipo_10(_atacante, _pelea);
						break;
					case 11:// chaferloko, y lancero
						tipo_11(_atacante, _pelea);
						break;
					case 12:// kralamar gigante
						tipo_12(_atacante, _pelea);
						break;
					case 13:// vasija
						tipo_13(_atacante, _pelea);
						break;
					case 14:// como 1 pero no invoca
						tipo_14(_atacante, _pelea);
						break;
					case 15:// como 1 pero no buffea
						tipo_15(_atacante, _pelea);
						break;
					case 16:// como 1 pero no cura
						tipo_16(_atacante, _pelea);
						break;
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {}
				if (!_atacante.estaMuerto()) {
					_pelea.finTurno();
				}
			}
		}
		
		private static void tipo_0(Luchador lanzador, Pelea pelea) {
			stop = true;
		}
		
		private static void tipo_1(Luchador lanzador, Pelea pelea) {
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				int porcPDV = (lanzador.getPDVConBuff() * 100) / lanzador.getPDVMaxConBuff();
				Luchador enemigo = enemigoMasCercano(pelea, lanzador); // Enemigos
				Luchador amigo = amigoMasCercano(pelea, lanzador); // Amigos
				if (enemigo == null) {
					mueveLoMasLejosPosible(pelea, lanzador);
					return;
				}
				if (porcPDV > 15) {
					int ataque = atacaSiEsPosible1(pelea, lanzador);
					while (ataque == 0 && !stop) {
						if (ataque == 5)
							stop = true;
						ataque = atacaSiEsPosible1(pelea, lanzador);
					}
					if (!moverYAtacarSiEsPosible(pelea, lanzador)) {// se mueve y trata de atacar
						if (!buffeaSiEsPosible1(pelea, lanzador, lanzador)) {// auto-buff
							if (!curaSiEsPosible(pelea, lanzador, true)) {// cura aliada
								if (!buffeaSiEsPosible1(pelea, lanzador, amigo)) {// boost aliado
									enemigo = enemigoMasCercano(pelea, lanzador);
									if (enemigo == null) {
										mueveLoMasLejosPosible(pelea, lanzador);
										return;
									}
									if (!acercarseA(pelea, lanzador, enemigo)) {// avanzar
										if (!invocarSiEsPosible1(pelea, lanzador)) {// invocar
											stop = true;
										}
									}
								}
							}
						}
					} else {
						ataque = atacaSiEsPosible1(pelea, lanzador);
						while (ataque == 0 && !stop) {
							if (ataque == 5)
								stop = true;
							ataque = atacaSiEsPosible1(pelea, lanzador);
						}
					}
				} else {
					if (!curaSiEsPosible(pelea, lanzador, true)) {// auto-cura
						int ataque = atacaSiEsPosible1(pelea, lanzador);
						while (ataque == 0 && !stop) {
							if (ataque == 5)
								stop = true;
							ataque = atacaSiEsPosible1(pelea, lanzador);
						}
						if (!buffeaSiEsPosible1(pelea, lanzador, lanzador)) {// auto-buff
							if (!buffeaSiEsPosible1(pelea, lanzador, amigo)) {// buff aliados
								if (!invocarSiEsPosible1(pelea, lanzador)) {
									enemigo = enemigoMasCercano(pelea, lanzador);
									if (enemigo == null) {
										mueveLoMasLejosPosible(pelea, lanzador);
										return;
									}
									if (!acercarseA(pelea, lanzador, enemigo)) {
										stop = true;
									}
								}
							}
						}
					}
				}
			}
		}
		
		private static void tipo_2(Luchador lanzador, Pelea pelea) { // esfera xelor
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				Luchador enemigo = enemigoMasCercano(pelea, lanzador);
				if (enemigo == null)
					return;
				int ataque = atacaSiEsPosible2(pelea, lanzador);
				while (ataque == 0 && !stop) {
					if (ataque == 5)
						stop = true;
					ataque = atacaSiEsPosible2(pelea, lanzador);
				}
				stop = true;
			}
		}
		
		private static void tipo_3(Luchador lanzador, Pelea pelea) { // mobs salas de entrenamiento
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				Luchador enemigo = enemigoMasCercano(pelea, lanzador);
				if (enemigo == null) {
					mueveLoMasLejosPosible(pelea, lanzador);
					return;
				}
				int ataque = atacaSiEsPosible2(pelea, lanzador);
				while (ataque == 0 && !stop) {
					if (ataque == 5)
						stop = true;
					ataque = atacaSiEsPosible2(pelea, lanzador);
				}
				enemigo = enemigoMasCercano(pelea, lanzador);
				if (enemigo == null) {
					mueveLoMasLejosPosible(pelea, lanzador);
					return;
				}
				if (!acercarseA(pelea, lanzador, enemigo))
					stop = true;
			}
		}
		
		private static void tipo_4(Luchador lanzador, Pelea pelea) { // IA propia de tofu, prespic
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				Luchador enemigo = enemigoMasCercano(pelea, lanzador);
				if (enemigo == null) {
					mueveLoMasLejosPosible(pelea, lanzador);
					return;
				}
				int ataque = atacaSiEsPosible2(pelea, lanzador);
				if (ataque == 0 && !stop) {
					while (ataque == 0 && !stop) {
						if (ataque == 5)
							stop = true;
						ataque = atacaSiEsPosible2(pelea, lanzador);
					}
				} else if (moverYAtacarSiEsPosible(pelea, lanzador)) {
					ataque = atacaSiEsPosible2(pelea, lanzador);
					if (ataque == 0 && !stop) {
						while (ataque == 0 && !stop) {
							if (ataque == 5)
								stop = true;
							ataque = atacaSiEsPosible2(pelea, lanzador);
						}
					}
				} else {
					enemigo = enemigoMasCercano(pelea, lanzador);
					if (enemigo == null) {
						mueveLoMasLejosPosible(pelea, lanzador);
						return;
					}
					if (acercarseA(pelea, lanzador, enemigo)) {
						ataque = atacaSiEsPosible2(pelea, lanzador);
						while (ataque == 0 && !stop) {
							if (ataque == 5)
								stop = true;
							ataque = atacaSiEsPosible2(pelea, lanzador);
						}
					}
				}
				mueveLoMasLejosPosible(pelea, lanzador);
				stop = true;
			}
		}
		
		private static void tipo_5(Luchador lanzador, Pelea pelea) { // IA para bloqueadora
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				Luchador enemigo = enemigoMasCercano(pelea, lanzador);
				if (enemigo == null) {
					mueveLoMasLejosPosible(pelea, lanzador);
					return;
				}
				if (!acercarseA(pelea, lanzador, enemigo)) {
					stop = true;
				}
			}
		}
		
		private static void tipo_6(Luchador lanzador, Pelea pelea) {// la hinchable, conejo
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				Luchador amigo = amigoMasCercano(pelea, lanzador);
				if (!acercarseA(pelea, lanzador, amigo)) {
					while (buffeaSiEsPosible2(pelea, lanzador, amigo)) {}
					while (buffeaSiEsPosible2(pelea, lanzador, lanzador)) {}
					stop = true;
				}
			}
		}
		
		private static void tipo_7(Luchador lanzador, Pelea pelea) { // gatake, pala animada, jabali
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				Luchador enemigo = enemigoMasCercano(pelea, lanzador);
				if (enemigo == null) {
					mueveLoMasLejosPosible(pelea, lanzador);
					return;
				}
				int ataque = atacaSiEsPosible2(pelea, lanzador);
				while (ataque == 0 && !stop) {
					if (ataque == 5)
						stop = true;
					ataque = atacaSiEsPosible2(pelea, lanzador);
				}
				enemigo = enemigoMasCercano(pelea, lanzador);
				if (enemigo == null) {
					mueveLoMasLejosPosible(pelea, lanzador);
					return;
				}
				if (!acercarseA(pelea, lanzador, enemigo)) {
					stop = true;
				}
			}
		}
		
		private static void tipo_8(Luchador lanzador, Pelea pelea) { // mochila animada
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				Luchador amigo = amigoMasCercano(pelea, lanzador);
				if (amigo == null)
					return;
				if (!acercarseA(pelea, lanzador, amigo)) {
					while (buffeaSiEsPosible2(pelea, lanzador, amigo)) {}
					stop = true;
				}
			}
		}
		
		private static void tipo_9(Luchador lanzador, Pelea pelea) { // cofre animado, arbol de vida
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				while (buffeaSiEsPosible2(pelea, lanzador, lanzador)) {}
				stop = true;
			}
		}
		
		private static void tipo_10(Luchador lanzador, Pelea pelea) {// cascara explosiva
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				int ataque = atacaSiEsPosible2(pelea, lanzador);
				while (ataque == 0 && !stop) {
					if (ataque == 5)
						stop = true;
					ataque = atacaSiEsPosible2(pelea, lanzador);
				}
				while (buffeaSiEsPosible2(pelea, lanzador, lanzador)) {}
				stop = true;
			}
		}
		
		private static void tipo_11(Luchador lanzador, Pelea pelea) { // chafer y chaferloko
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				Luchador enemigo = enemigoMasCercano(pelea, lanzador); // Enemigos
				if (enemigo == null) {
					mueveLoMasLejosPosible(pelea, lanzador);
					return;
				}
				int ataque = atacaSiEsPosible3(pelea, lanzador);
				while (ataque == 0 && !stop) {
					if (ataque == 5)
						stop = true;
					ataque = atacaSiEsPosible3(pelea, lanzador);
				}
				while (buffeaSiEsPosible1(pelea, lanzador, lanzador)) {}// auto-buff
				enemigo = enemigoMasCercano(pelea, lanzador);
				if (enemigo == null) {
					mueveLoMasLejosPosible(pelea, lanzador);
					return;
				}
				if (!acercarseA(pelea, lanzador, enemigo)) {
					stop = true;
				}
			}
		}
		
		private static void tipo_12(Luchador lanzador, Pelea pelea) {// kralamar
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				int ataque = 0;
				if (!invocarSiEsPosible2(pelea, lanzador)) {
					if (!buffeaKralamar(pelea, lanzador, lanzador)) {
						ataque = atacaSiEsPosible1(pelea, lanzador);
						while (ataque == 0 && !stop) {
							if (ataque == 5)
								stop = true;
							ataque = atacaSiEsPosible1(pelea, lanzador);
						}
						stop = true;
					} else {
						ataque = atacaSiEsPosible1(pelea, lanzador);
						while (ataque == 0 && !stop) {
							if (ataque == 5)
								stop = true;
							ataque = atacaSiEsPosible1(pelea, lanzador);
						}
						stop = true;
					}
				}
			}
		}
		
		private static void tipo_13(Luchador lanzador, Pelea pelea) {// vasija
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				if (!buffeaSiEsPosible2(pelea, lanzador, lanzador)) {// auto boost
					int ataque = atacaSiEsPosible2(pelea, lanzador);
					while (ataque == 0 && !stop) {
						if (ataque == 5)
							stop = true;
						ataque = atacaSiEsPosible2(pelea, lanzador);
					}
					stop = true;
				}
			}
		}
		
		private static void tipo_14(Luchador lanzador, Pelea pelea) {
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				int porcPDV = (lanzador.getPDVConBuff() * 100) / lanzador.getPDVMaxConBuff();
				Luchador enemigo = enemigoMasCercano(pelea, lanzador); // Enemigos
				Luchador amigo = amigoMasCercano(pelea, lanzador); // Amigos
				if (enemigo == null) {
					mueveLoMasLejosPosible(pelea, lanzador);
					return;
				}
				if (porcPDV > 15) {
					int ataque = atacaSiEsPosible1(pelea, lanzador);
					while (ataque == 0 && !stop) {
						if (ataque == 5)
							stop = true;
						ataque = atacaSiEsPosible1(pelea, lanzador);
					}
					if (!moverYAtacarSiEsPosible(pelea, lanzador)) {// se mueve y trata de atacar
						if (!buffeaSiEsPosible1(pelea, lanzador, lanzador)) {// auto-buff
							if (!curaSiEsPosible(pelea, lanzador, false)) {// cura aliada
								if (!buffeaSiEsPosible1(pelea, lanzador, amigo)) {// boost aliado
									enemigo = enemigoMasCercano(pelea, lanzador);
									if (enemigo == null) {
										mueveLoMasLejosPosible(pelea, lanzador);
										return;
									}
									if (!acercarseA(pelea, lanzador, enemigo)) {// avanzar
										stop = true;
									}
								}
							}
						}
					} else {
						ataque = atacaSiEsPosible1(pelea, lanzador);
						while (ataque == 0 && !stop) {
							if (ataque == 5)
								stop = true;
							ataque = atacaSiEsPosible1(pelea, lanzador);
						}
					}
				} else {
					if (!curaSiEsPosible(pelea, lanzador, true)) {// auto-cura
						int ataque = atacaSiEsPosible1(pelea, lanzador);
						while (ataque == 0 && !stop) {
							if (ataque == 5)
								stop = true;
							ataque = atacaSiEsPosible1(pelea, lanzador);
						}
						if (!buffeaSiEsPosible1(pelea, lanzador, lanzador)) {// auto-buff
							if (!buffeaSiEsPosible1(pelea, lanzador, amigo)) {// buff aliados
								enemigo = enemigoMasCercano(pelea, lanzador);
								if (enemigo == null) {
									mueveLoMasLejosPosible(pelea, lanzador);
									return;
								}
								if (!acercarseA(pelea, lanzador, enemigo)) {
									stop = true;
								}
							}
						}
					}
				}
			}
		}
		
		private static void tipo_15(Luchador lanzador, Pelea pelea) {
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				int porcPDV = (lanzador.getPDVConBuff() * 100) / lanzador.getPDVMaxConBuff();
				Luchador enemigo = enemigoMasCercano(pelea, lanzador); // Enemigos
				if (enemigo == null) {
					mueveLoMasLejosPosible(pelea, lanzador);
					return;
				}
				if (porcPDV > 15) {
					int ataque = atacaSiEsPosible1(pelea, lanzador);
					while (ataque == 0 && !stop) {
						if (ataque == 5)
							stop = true;
						ataque = atacaSiEsPosible1(pelea, lanzador);
					}
					if (!moverYAtacarSiEsPosible(pelea, lanzador)) {// se mueve y trata de atacar
						if (!curaSiEsPosible(pelea, lanzador, false)) {// cura aliada
							enemigo = enemigoMasCercano(pelea, lanzador);
							if (enemigo == null) {
								mueveLoMasLejosPosible(pelea, lanzador);
								return;
							}
							if (!acercarseA(pelea, lanzador, enemigo)) {// avanzar
								if (!invocarSiEsPosible1(pelea, lanzador)) {// invocar
									stop = true;
								}
							}
						}
					} else {
						ataque = atacaSiEsPosible1(pelea, lanzador);
						while (ataque == 0 && !stop) {
							if (ataque == 5)
								stop = true;
							ataque = atacaSiEsPosible1(pelea, lanzador);
						}
					}
				} else {
					if (!curaSiEsPosible(pelea, lanzador, true)) {// auto-cura
						int ataque = atacaSiEsPosible1(pelea, lanzador);
						while (ataque == 0 && !stop) {
							if (ataque == 5)
								stop = true;
							ataque = atacaSiEsPosible1(pelea, lanzador);
						}
						if (!invocarSiEsPosible1(pelea, lanzador)) {
							enemigo = enemigoMasCercano(pelea, lanzador);
							if (enemigo == null) {
								mueveLoMasLejosPosible(pelea, lanzador);
								return;
							}
							if (!acercarseA(pelea, lanzador, enemigo)) {
								stop = true;
							}
						}
					}
				}
			}
		}
		
		private static void tipo_16(Luchador lanzador, Pelea pelea) {
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				Luchador enemigo = enemigoMasCercano(pelea, lanzador); // Enemigos
				Luchador amigo = amigoMasCercano(pelea, lanzador); // Amigos
				if (enemigo == null) {
					mueveLoMasLejosPosible(pelea, lanzador);
					return;
				}
				int ataque = atacaSiEsPosible1(pelea, lanzador);
				while (ataque == 0 && !stop) {
					if (ataque == 5)
						stop = true;
					ataque = atacaSiEsPosible1(pelea, lanzador);
				}
				if (!moverYAtacarSiEsPosible(pelea, lanzador)) {// se mueve y trata de atacar
					if (!buffeaSiEsPosible1(pelea, lanzador, lanzador)) {// auto-buff
						if (!buffeaSiEsPosible1(pelea, lanzador, amigo)) {// boost aliado
							enemigo = enemigoMasCercano(pelea, lanzador);
							if (enemigo == null) {
								mueveLoMasLejosPosible(pelea, lanzador);
								return;
							}
							if (!acercarseA(pelea, lanzador, enemigo)) {// avanzar
								if (!invocarSiEsPosible1(pelea, lanzador)) {// invocar
									stop = true;
								}
							}
						}
					}
				} else {
					ataque = atacaSiEsPosible1(pelea, lanzador);
					while (ataque == 0 && !stop) {
						if (ataque == 5)
							stop = true;
						ataque = atacaSiEsPosible1(pelea, lanzador);
					}
				}
			}
		}
		
		private static void tipo_Prisma(Luchador lanzador, Pelea pelea) {
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				Luchador amigo = amigoMasCercano(pelea, lanzador); // Amigos
				if (amigo != null) {
					if (!curaSiEsPosiblePrisma(pelea, lanzador, false)) {// cura aliada
						if (!buffeaSiEsPosiblePrisma(pelea, lanzador, amigo)) {// boost aliado
							if (!buffeaSiEsPosiblePrisma(pelea, lanzador, lanzador)) {// auto boost
								int ataque = atacaSiEsPosiblePrisma(pelea, lanzador);
								while (ataque == 0 && !stop) {
									if (ataque == 5)
										stop = true;
									ataque = atacaSiEsPosiblePrisma(pelea, lanzador);
								}
								stop = true;
							}
						}
					}
				} else {
					int ataque = atacaSiEsPosiblePrisma(pelea, lanzador);
					while (ataque == 0 && !stop) {
						if (ataque == 5)
							stop = true;
						ataque = atacaSiEsPosiblePrisma(pelea, lanzador);
					}
					stop = true;
				}
			}
		}
		
		private static void tipo_Recaudador(Luchador lanzador, Pelea pelea) { // IA propia del recaudador
			int veces = 0;
			while (!stop && lanzador.puedeJugar()) {
				if (++veces >= 8)
					stop = true;
				if (veces > 15)
					return;
				int porcPDV = (lanzador.getPDVConBuff() * 100) / lanzador.getPDVMaxConBuff();
				Luchador amigo = amigoMasCercano(pelea, lanzador);
				Luchador enemigo = enemigoMasCercano(pelea, lanzador);
				if (porcPDV > 15) {
					int ataque = atacaSiEsPosibleRecau(pelea, lanzador);
					while (ataque == 0 && !stop) {
						if (ataque == 5)
							stop = true;
						ataque = atacaSiEsPosibleRecau(pelea, lanzador);
					}
					if (!curaSiEsPosibleRecau(pelea, lanzador, false)) {
						if (!buffeaSiEsPosibleRecau(pelea, lanzador, amigo)) {
							enemigo = enemigoMasCercano(pelea, lanzador);
							if (enemigo == null) {
								mueveLoMasLejosPosible(pelea, lanzador);
								return;
							}
							if (!acercarseA(pelea, lanzador, enemigo)) {
								stop = true;
							}
						}
					}
				} else {
					if (!curaSiEsPosibleRecau(pelea, lanzador, true)) {
						int ataque = atacaSiEsPosibleRecau(pelea, lanzador);
						while (ataque == 0 && !stop) {
							if (ataque == 5)
								stop = true;
							ataque = atacaSiEsPosibleRecau(pelea, lanzador);
						}
						if (!mueveLoMasLejosPosible(pelea, lanzador)) {
							stop = true;
						}
					}
				}
			}
		}
		
		private static boolean mueveLoMasLejosPosible(Pelea pelea, Luchador lanzador) {
			if (lanzador.getTempPM(pelea) <= 0)
				return false;
			int celdaIDLanzador = lanzador.getCeldaPelea().getID();
			Mapa mapa = pelea.getMapaCopia();
			int dist[] = { 1000, 11000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 }, celda[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0 };
			for (int i = 0; i < 10; i++) {
				for (Luchador blanco : pelea.luchadoresDeEquipo(3)) {
					if (blanco.estaMuerto())
						continue;
					if (blanco == lanzador || blanco.getParamEquipoAliado() == lanzador.getParamEquipoAliado())
						continue;
					int celdaEnemigo = blanco.getCeldaPelea().getID();
					if (celdaEnemigo == celda[0] || celdaEnemigo == celda[1] || celdaEnemigo == celda[2]
							|| celdaEnemigo == celda[3] || celdaEnemigo == celda[4] || celdaEnemigo == celda[5]
							|| celdaEnemigo == celda[6] || celdaEnemigo == celda[7] || celdaEnemigo == celda[8]
							|| celdaEnemigo == celda[9])
						continue;
					int d = 0;
					d = Camino.distanciaEntreDosCeldas(mapa, celdaIDLanzador, celdaEnemigo);
					if (d == 0)
						continue;
					if (d < dist[i]) {
						dist[i] = d;
						celda[i] = celdaEnemigo;
					}
					if (dist[i] == 1000) {
						dist[i] = 0;
						celda[i] = celdaIDLanzador;
					}
				}
			}
			if (dist[0] == 0)
				return false;
			int dist2[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			int ancho = mapa.getAncho();
			int PM = lanzador.getTempPM(pelea);
			int celdaInicio = celdaIDLanzador;
			int celdaDestino = celdaIDLanzador;
			int ultCelda = mapa.ultimaCeldaID();
			Random rand = new Random();
			int valor = rand.nextInt(3);
			int[] movidas;
			if (valor == 0)
				movidas = new int[] { 0, 1, 2, 3 };
			else if (valor == 1)
				movidas = new int[] { 1, 2, 3, 0 };
			else if (valor == 1)
				movidas = new int[] { 2, 3, 0, 1 };
			else
				movidas = new int[] { 3, 0, 1, 2 };
			for (int i = 0; i <= PM; i++) {
				if (celdaDestino > 0)
					celdaInicio = celdaDestino;
				int celdaTemporal = celdaInicio;
				int infl = 0, inflF = 0;
				for (Integer x : movidas) {
					switch (x) {
						case 0:
							celdaTemporal = celdaTemporal + ancho;
							break;
						case 1:
							celdaTemporal = celdaInicio + (ancho - 1);
							break;
						case 2:
							celdaTemporal = celdaInicio - ancho;
							break;
						case 3:
							celdaTemporal = celdaInicio - (ancho - 1);
							break;
					}
					infl = 0;
					for (int a = 0; a < 10 && dist[a] != 0; a++) {
						dist2[a] = Camino.distanciaEntreDosCeldas(mapa, celdaTemporal, celda[a]);
						if (dist2[a] > dist[a])
							infl++;
					}
					if (infl > inflF && celdaTemporal > 0 && celdaTemporal < ultCelda
							&& !mapa.celdaSalienteLateral(celdaDestino, celdaTemporal)
							&& mapa.getCelda(celdaTemporal).esCaminable(false)) {
						inflF = infl;
						celdaDestino = celdaTemporal;
					}
				}
			}
			if (celdaDestino < 0 || celdaDestino > ultCelda || celdaDestino == celdaIDLanzador
					|| !mapa.getCelda(celdaDestino).esCaminable(false))
				return false;
			ArrayList<Celda> path = Camino.pathMasCortoEntreDosCeldas(mapa, celdaIDLanzador, celdaDestino, 0);
			if (path == null)
				return false;
			ArrayList<Celda> finalPath = new ArrayList<Celda>();
			for (int a = 0; a < lanzador.getTempPM(pelea); a++) {
				if (path.size() == a)
					break;
				finalPath.add(path.get(a));
			}
			String pathstr = "";
			try {
				int tempCeldaID = celdaIDLanzador;
				int tempDir = 0;
				for (Celda c : finalPath) {
					char d = Camino.getDirEntreDosCeldas(tempCeldaID, c.getID(), mapa, true);
					if (d == 0)
						return false;
					if (tempDir != d) {
						if (finalPath.indexOf(c) != 0)
							pathstr += Encriptador.celdaIDACodigo(tempCeldaID);
						pathstr += d;
					}
					tempCeldaID = c.getID();
				}
				if (tempCeldaID != celdaIDLanzador)
					pathstr += Encriptador.celdaIDACodigo(tempCeldaID);
			} catch (Exception e) {
				e.printStackTrace();
			}
			AccionDeJuego GA = new AccionDeJuego(0, 1, "");
			GA._args = pathstr;
			boolean resultado = pelea.puedeMoverseLuchador(lanzador, GA);
			return resultado;
		}
		
		private static boolean acercarseA(Pelea pelea, Luchador lanzador, Luchador objetivo) {
			Mapa mapa = pelea.getMapaCopia();
			if (lanzador.getTempPM(pelea) <= 0)
				return false;
			if (objetivo == null) {
				objetivo = enemigoMasCercano(pelea, lanzador);
			}
			if (objetivo == null)
				return false;
			int celdaID = -1;
			try {
				if (Camino.esSiguienteA(lanzador.getCeldaPelea().getID(), objetivo.getCeldaPelea().getID(), mapa))
					return false;
				celdaID = Camino.getCeldaMasCercanaAlrededor(mapa, objetivo.getCeldaPelea().getID(), lanzador.getCeldaPelea()
						.getID(), null);
			} catch (NullPointerException e) {
				return false;
			}
			if (celdaID == -1) {
				ArrayList<Luchador> enemigos = listaEnemigosMenosPDV(pelea, lanzador);
				for (Luchador enemigo : enemigos) {
					int celdaID2 = Camino.getCeldaMasCercanaAlrededor(mapa, enemigo.getCeldaPelea().getID(), lanzador
							.getCeldaPelea().getID(), null);
					if (celdaID2 != -1) {
						celdaID = celdaID2;
						break;
					}
				}
			}
			ArrayList<Celda> path = Camino.pathMasCortoEntreDosCeldas(mapa, lanzador.getCeldaPelea().getID(), celdaID, 0);
			if (path == null || path.isEmpty())
				return false;
			ArrayList<Celda> finalPath = new ArrayList<Celda>();
			for (int a = 0; a < lanzador.getTempPM(pelea); a++) {
				if (path.size() == a)
					break;
				finalPath.add(path.get(a));
			}
			String pathstr = "";
			try {
				int tempCeldaID = lanzador.getCeldaPelea().getID();
				int tempDir = 0;
				for (Celda c : finalPath) {
					char d = Camino.getDirEntreDosCeldas(tempCeldaID, c.getID(), mapa, true);
					if (d == 0)
						return false;
					if (tempDir != d) {
						if (finalPath.indexOf(c) != 0)
							pathstr += Encriptador.celdaIDACodigo(tempCeldaID);
						pathstr += d;
					}
					tempCeldaID = c.getID();
				}
				if (tempCeldaID != lanzador.getCeldaPelea().getID())
					pathstr += Encriptador.celdaIDACodigo(tempCeldaID);
			} catch (Exception e) {
				e.printStackTrace();
			}
			AccionDeJuego GA = new AccionDeJuego(0, 1, "");
			GA._args = pathstr;
			boolean resultado = pelea.puedeMoverseLuchador(lanzador, GA);
			return resultado;
		}
		
		private static boolean invocarSiEsPosible1(Pelea pelea, Luchador invocador) {
			if (invocador.getNroInvocaciones() >= invocador.getTotalStatsConBuff().getEfecto(182))
				return false;
			Luchador enemigoCercano = enemigoMasCercano(pelea, invocador);
			if (enemigoCercano == null)
				return false;
			int celdaMasCercana = Camino.getCeldaMasCercanaAlrededor(pelea.getMapaCopia(), invocador.getCeldaPelea().getID(),
					enemigoCercano.getCeldaPelea().getID(), null);
			if (celdaMasCercana == -1)
				return false;
			StatsHechizos hechizo = hechizoInvocacion(pelea, invocador, celdaMasCercana);
			if (hechizo == null)
				return false;
			int invoc = pelea.intentarLanzarHechizo(invocador, hechizo, celdaMasCercana);
			if (invoc != 0)
				return false;
			return true;
		}
		
		private static boolean invocarSiEsPosible2(Pelea pelea, Luchador invocador) {
			if (invocador.getNroInvocaciones() >= invocador.getTotalStatsConBuff().getEfecto(182))
				return false;
			Luchador enemigoCercano = enemigoMasCercano(pelea, invocador);
			if (enemigoCercano == null)
				return false;
			int invoc = hechizoInvocacion2(pelea, invocador, enemigoCercano);
			if (invoc != 0)
				return false;
			return true;
		}
		
		private static StatsHechizos hechizoInvocacion(Pelea pelea, Luchador invocador, int celdaCercana) {
			if (invocador.getMob() == null)
				return null;
			for (Entry<Integer, StatsHechizos> SH : invocador.getMob().getHechizos().entrySet()) {
				if (!pelea.puedeLanzarHechizo(invocador, SH.getValue(), pelea.getMapaCopia().getCelda(celdaCercana), -1))
					continue;
				for (EfectoHechizo EH : SH.getValue().getEfectos()) {
					if (EH.getEfectoID() == 181 || EH.getEfectoID() == 185)
						return SH.getValue();
				}
			}
			return null;
		}
		
		private static int hechizoInvocacion2(Pelea pelea, Luchador invocador, Luchador enemigoCercano) {
			if (invocador.getMob() == null)
				return 5;
			ArrayList<StatsHechizos> hechizos = new ArrayList<StatsHechizos>();
			StatsHechizos SH = null;
			int celdaMasCercana = -1;
			try {
				for (Entry<Integer, StatsHechizos> SS : invocador.getMob().getHechizos().entrySet()) {
					StatsHechizos hechi = SS.getValue();
					boolean paso = false;
					for (EfectoHechizo EH : hechi.getEfectos()) {
						if (paso)
							continue;
						if (EH.getEfectoID() == 181 || EH.getEfectoID() == 185) {
							celdaMasCercana = Camino.getCeldaMasCercanaAlrededor2(pelea.getMapaCopia(), invocador.getCeldaPelea()
									.getID(), enemigoCercano.getCeldaPelea().getID(), hechi.getMinAlc(), hechi.getMaxAlc());
							if (celdaMasCercana == -1)
								continue;
							if (!pelea.puedeLanzarHechizo(invocador, hechi, pelea.getMapaCopia().getCelda(celdaMasCercana), -1))
								continue;
							hechizos.add(hechi);
							paso = true;
						}
					}
				}
			} catch (NullPointerException e) {
				return 5;
			}
			if (hechizos.size() <= 0)
				return 5;
			if (hechizos.size() == 1)
				SH = hechizos.get(0);
			else
				SH = hechizos.get(Formulas.getRandomValor(0, hechizos.size() - 1));
			int invoca = pelea.intentarLanzarHechizo(invocador, SH, celdaMasCercana);
			return invoca;
		}
		
		private static boolean curaSiEsPosible(Pelea pelea, Luchador lanzador, boolean autoCura) {
			if (autoCura && (lanzador.getPDVConBuff() * 100) / lanzador.getPDVMaxConBuff() > 95)
				return false;
			Luchador objetivo = null;
			StatsHechizos SH = null;
			if (autoCura) {
				objetivo = lanzador;
				SH = mejorHechizoCuracion(pelea, lanzador, objetivo);
			} else {
				Luchador tempObjetivo = null;
				int porcPDVmin = 100;
				StatsHechizos tempSH = null;
				for (Luchador blanco : pelea.luchadoresDeEquipo(3)) {
					if (blanco.estaMuerto() || blanco == lanzador)
						continue;
					if (blanco.getParamEquipoAliado() == lanzador.getParamEquipoAliado()) {
						int porcPDV = 0;
						int PDVMAX = blanco.getPDVMaxConBuff();
						if (PDVMAX == 0)
							porcPDV = 0;
						else
							porcPDV = (blanco.getPDVConBuff() * 100) / PDVMAX;
						if (porcPDV < porcPDVmin && porcPDV < 95) {
							int infl = 0;
							for (Entry<Integer, StatsHechizos> ss : lanzador.getMob().getHechizos().entrySet()) {
								int infCura = calculaInfluenciaCura(ss.getValue());
								if (infl < infCura && infCura != 0
										&& pelea.puedeLanzarHechizo(lanzador, ss.getValue(), blanco.getCeldaPelea(), -1)) {
									infl = infCura;
									tempSH = ss.getValue();
								}
							}
							if (tempSH != SH && tempSH != null) {
								tempObjetivo = blanco;
								SH = tempSH;
								porcPDVmin = porcPDV;
							}
						}
					}
				}
				objetivo = tempObjetivo;
			}
			if (objetivo == null)
				return false;
			if (SH == null)
				return false;
			int cura = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
			if (cura != 0)
				return false;
			return true;
		}
		
		private static boolean curaSiEsPosiblePrisma(Pelea pelea, Luchador prisma, boolean autoCura) {
			if (autoCura && (prisma.getPDVConBuff() * 100) / prisma.getPDVMaxConBuff() > 95)
				return false;
			Luchador objetivo = null;
			Hechizo hechizo = MundoDofus.getHechizo(124);
			StatsHechizos SH = hechizo.getStatsPorNivel(6);
			if (autoCura) {
				objetivo = prisma;
			} else {
				Luchador curado = null;
				int porcPDVmin = 100;
				for (Luchador blanco : pelea.luchadoresDeEquipo(3)) {
					if (blanco.estaMuerto() || blanco == prisma)
						continue;
					if (blanco.getParamEquipoAliado() == prisma.getParamEquipoAliado()) {
						int porcPDV = (blanco.getPDVConBuff() * 100) / blanco.getPDVMaxConBuff();
						if (porcPDV < porcPDVmin && porcPDV < 95) {
							curado = blanco;
							porcPDVmin = porcPDV;
						}
					}
				}
				objetivo = curado;
			}
			if (objetivo == null)
				return false;
			if (SH == null)
				return false;
			int cura = pelea.intentarLanzarHechizo(prisma, SH, objetivo.getCeldaPelea().getID());
			if (cura != 0)
				return false;
			return true;
		}
		
		private static boolean curaSiEsPosibleRecau(Pelea pelea, Luchador recaudador, boolean autoCura) {
			if (autoCura && (recaudador.getPDVConBuff() * 100) / recaudador.getPDVMaxConBuff() > 95)
				return false;
			Luchador objetivo = null;
			StatsHechizos SH = null;
			if (autoCura) {
				objetivo = recaudador;
				SH = mejorHechizoCuracionRecaudador(pelea, recaudador, objetivo);
			} else {
				Luchador tempObjetivo = null;
				int porcPDVmin = 100;
				StatsHechizos tempSH = null;
				if (pelea.luchadoresDeEquipo(recaudador.getParamEquipoAliado()).size() <= 1)
					return false;
				for (Luchador blanco : pelea.luchadoresDeEquipo(3)) {
					if (blanco.estaMuerto() || blanco == recaudador)
						continue;
					if (blanco.getParamEquipoAliado() == recaudador.getParamEquipoAliado()) {
						int porcPDV = (blanco.getPDVConBuff() * 100) / blanco.getPDVMaxConBuff();
						if (porcPDV < porcPDVmin && porcPDV < 95) {
							int infl = 0;
							for (Entry<Integer, StatsHechizos> sh : MundoDofus.getGremio(recaudador.getRecau().getGremioID())
									.getHechizos().entrySet()) {
								if (sh.getValue() == null)
									continue;
								int infCura = calculaInfluenciaCura(sh.getValue());
								if (infl < infCura && infCura != 0
										&& pelea.puedeLanzarHechizo(recaudador, sh.getValue(), blanco.getCeldaPelea(), -1)) {
									infl = infCura;
									tempSH = sh.getValue();
								}
							}
							if (tempSH != SH && tempSH != null) {
								tempObjetivo = blanco;
								SH = tempSH;
								porcPDVmin = porcPDV;
							}
						}
					}
				}
				objetivo = tempObjetivo;
			}
			if (objetivo == null)
				return false;
			if (SH == null)
				return false;
			int cura = pelea.intentarLanzarHechizo(recaudador, SH, objetivo.getCeldaPelea().getID());
			if (cura != 0)
				return false;
			return true;
		}
		
		private static boolean buffeaSiEsPosible1(Pelea pelea, Luchador lanzador, Luchador objetivo) {
			if (objetivo == null)
				return false;
			try {
				StatsHechizos SH = mejorBuff1(pelea, lanzador, objetivo);
				if (SH == null)
					return false;
				int buff = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
				if (buff != 0)
					return false;
				return true;
			} catch (NullPointerException e) {
				return false;
			}
		}
		
		private static boolean buffeaSiEsPosible2(Pelea pelea, Luchador lanzador, Luchador objetivo) {
			if (objetivo == null)
				return false;
			try {
				StatsHechizos SH = mejorBuff2(pelea, lanzador, objetivo);
				if (SH == null)
					return false;
				int buff = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
				if (buff != 0)
					return false;
				return true;
			} catch (NullPointerException e) {
				return false;
			}
		}
		
		private static boolean buffeaKralamar(Pelea pelea, Luchador lanzador, Luchador objetivo) {
			if (objetivo == null)
				return false;
			Hechizo hechizo = MundoDofus.getHechizo(1106);
			StatsHechizos SH = hechizo.getStatsPorNivel(1);
			if (SH == null)
				return false;
			int buff = 5;
			try {
				buff = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
			} catch (NullPointerException e) {
				return false;
			}
			if (buff != 0)
				return false;
			return true;
		}
		
		private static boolean buffeaSiEsPosiblePrisma(Pelea pelea, Luchador lanzador, Luchador objetivo) {
			if (objetivo == null)
				return false;
			StatsHechizos SH = mejorBuffPrisma(pelea, lanzador);
			if (SH == null)
				return false;
			int buff = 5;
			try {
				buff = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
			} catch (NullPointerException e) {
				return false;
			}
			if (buff != 0)
				return false;
			return true;
		}
		
		private static boolean buffeaSiEsPosibleRecau(Pelea pelea, Luchador recaudador, Luchador objetivo) {
			if (objetivo == null)
				return false;
			try {
				StatsHechizos SH = mejorBuffRecaudador(pelea, recaudador, objetivo);
				if (SH == null)
					return false;
				int buff = pelea.intentarLanzarHechizo(recaudador, SH, objetivo.getCeldaPelea().getID());
				if (buff != 0)
					return false;
				return true;
			} catch (NullPointerException e) {
				return false;
			}
		}
		
		private static StatsHechizos mejorBuffPrisma(Pelea pelea, Luchador lanzador) {
			Hechizo hechizo = MundoDofus.getHechizo(153);
			StatsHechizos hechizoStats = hechizo.getStatsPorNivel(6);
			return hechizoStats;
		}
		
		private static StatsHechizos mejorBuff1(Pelea pelea, Luchador lanzador, Luchador objetivo) {
			int infl = 0;
			StatsHechizos sh = null;
			if (objetivo == null)
				return null;
			try {
				for (Entry<Integer, StatsHechizos> SH : lanzador.getMob().getHechizos().entrySet()) {
					int infDaño = calculaInfluenciaDaño(SH.getValue(), lanzador, objetivo);
					if (infl < infDaño && infDaño > 0
							&& pelea.puedeLanzarHechizo(lanzador, SH.getValue(), objetivo.getCeldaPelea(), -1)) {
						infl = infDaño;
						sh = SH.getValue();
					}
				}
			} catch (NullPointerException e) {
				return null;
			}
			return sh;
		}
		
		private static StatsHechizos mejorBuff2(Pelea pelea, Luchador lanzador, Luchador objetivo) {
			ArrayList<StatsHechizos> hechizos = new ArrayList<StatsHechizos>();
			StatsHechizos sh = null;
			if (objetivo == null)
				return null;
			try {
				Celda celdaObj = objetivo.getCeldaPelea();
				for (Entry<Integer, StatsHechizos> SH : lanzador.getMob().getHechizos().entrySet()) {
					if (pelea.puedeLanzarHechizo(lanzador, SH.getValue(), celdaObj, -1))
						hechizos.add(SH.getValue());
				}
				if (hechizos.size() <= 0)
					return null;
				if (hechizos.size() == 1)
					return hechizos.get(0);
				sh = hechizos.get(Formulas.getRandomValor(0, hechizos.size() - 1));
				return sh;
			} catch (NullPointerException e) {
				return null;
			}
		}
		
		private static StatsHechizos mejorBuffRecaudador(Pelea pelea, Luchador lanzador, Luchador objetivo) {
			int infl = 0;
			StatsHechizos sh = null;
			if (objetivo == null)
				return null;
			try {
				for (Entry<Integer, StatsHechizos> SH : MundoDofus.getGremio(lanzador.getRecau().getGremioID()).getHechizos()
						.entrySet()) {
					if (SH.getValue() == null)
						continue;
					int infDaños = calculaInfluenciaDaño(SH.getValue(), lanzador, objetivo);
					if (infl < infDaños && infDaños > 0
							&& pelea.puedeLanzarHechizo(lanzador, SH.getValue(), objetivo.getCeldaPelea(), -1)) {
						infl = infDaños;
						sh = SH.getValue();
					}
				}
			} catch (NullPointerException e) {
				return null;
			}
			return sh;
		}
		
		private static StatsHechizos mejorHechizoCuracion(Pelea pelea, Luchador lanzador, Luchador objetivo) {
			int infl = 0;
			StatsHechizos sh = null;
			if (objetivo == null)
				return null;
			try {
				for (Entry<Integer, StatsHechizos> SH : lanzador.getMob().getHechizos().entrySet()) {
					int infCura = calculaInfluenciaCura(SH.getValue());
					if (infl < infCura && infCura != 0
							&& pelea.puedeLanzarHechizo(lanzador, SH.getValue(), objetivo.getCeldaPelea(), -1)) {
						infl = infCura;
						sh = SH.getValue();
					}
				}
			} catch (NullPointerException e) {
				return null;
			}
			return sh;
		}
		
		private static StatsHechizos mejorHechizoCuracionRecaudador(Pelea pelea, Luchador lanzador, Luchador objetivo) {
			int infl = 0;
			StatsHechizos sh = null;
			if (objetivo == null)
				return null;
			try {
				for (Entry<Integer, StatsHechizos> SH : MundoDofus.getGremio(lanzador.getRecau().getGremioID()).getHechizos()
						.entrySet()) {
					if (SH.getValue() == null)
						continue;
					int infCura = calculaInfluenciaCura(SH.getValue());
					if (infl < infCura && infCura != 0
							&& pelea.puedeLanzarHechizo(lanzador, SH.getValue(), objetivo.getCeldaPelea(), -1)) {
						infl = infCura;
						sh = SH.getValue();
					}
				}
			} catch (NullPointerException e) {
				return null;
			}
			return sh;
		}
		
		private static Luchador amigoMasCercano(Pelea pelea, Luchador lanzador) {
			int dist = 1000;
			Luchador tempObjetivo = null;
			for (Luchador objetivo : pelea.luchadoresDeEquipo(lanzador.getParamEquipoAliado())) {
				if (objetivo.estaMuerto() || objetivo == lanzador)
					continue;
				int d = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(), objetivo
						.getCeldaPelea().getID());
				if (d < dist) {
					dist = d;
					tempObjetivo = objetivo;
				}
			}
			return tempObjetivo;
		}
		
		private static Luchador enemigoMasCercano(Pelea pelea, Luchador lanzador) {
			int dist = 1000;
			Luchador tempObjetivo = null;
			for (Luchador objetivo : pelea.luchadoresDeEquipo(lanzador.getParamEquipoEnemigo())) {
				if (objetivo.estaMuerto() || objetivo.esInvisible())
					continue;
				int d = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(), objetivo
						.getCeldaPelea().getID());
				if (d < dist) {
					dist = d;
					tempObjetivo = objetivo;
				}
			}
			return tempObjetivo;
		}
		
		private static Luchador luchadorMasCercano(Pelea pelea, Luchador lanzador) {
			int dist = 1000;
			Luchador tempObjetivo = null;
			for (Luchador objetivo : pelea.luchadoresDeEquipo(3)) {
				if (objetivo.estaMuerto() || objetivo == lanzador)
					continue;
				int d = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(), objetivo
						.getCeldaPelea().getID());
				if (d < dist) {
					dist = d;
					tempObjetivo = objetivo;
				}
			}
			return tempObjetivo;
		}
		
		private static ArrayList<Luchador> listaTodoEnemigos(Pelea pelea, Luchador lanzador) {
			ArrayList<Luchador> listaEnemigos = new ArrayList<Luchador>();
			ArrayList<Luchador> enemigosNoInvo = new ArrayList<Luchador>();
			ArrayList<Luchador> enemigosInvo = new ArrayList<Luchador>();
			for (Luchador objetivo : pelea.luchadoresDeEquipo(lanzador.getParamEquipoEnemigo())) {
				if (objetivo.estaMuerto() || objetivo.esInvisible())
					continue;
				if (objetivo.esInvocacion()) {
					enemigosInvo.add(objetivo);
				} else {
					enemigosNoInvo.add(objetivo);
				}
			}
			Random rand = new Random();
			if (rand.nextBoolean()) {
				listaEnemigos.addAll(enemigosInvo);
				listaEnemigos.addAll(enemigosNoInvo);
			} else {
				listaEnemigos.addAll(enemigosNoInvo);
				listaEnemigos.addAll(enemigosInvo);
			}
			return listaEnemigos;
		}
		
		private static ArrayList<Luchador> listaEnemigosMenosPDV(Pelea pelea, Luchador lanzador) {
			ArrayList<Luchador> listaEnemigos = new ArrayList<Luchador>();
			ArrayList<Luchador> enemigosNoInvo = new ArrayList<Luchador>();
			ArrayList<Luchador> enemigosInvo = new ArrayList<Luchador>();
			for (Luchador objetivo : pelea.luchadoresDeEquipo(lanzador.getParamEquipoEnemigo())) {
				if (objetivo.estaMuerto() || objetivo.esInvisible())
					continue;
				if (objetivo.esInvocacion())
					enemigosInvo.add(objetivo);
				else
					enemigosNoInvo.add(objetivo);
			}
			int i = 0;
			int tempPDV;
			Random rand = new Random();
			if (rand.nextBoolean()) {
				try {
					int i3 = enemigosNoInvo.size(), i2 = enemigosInvo.size();
					while (i < i2) {
						tempPDV = 200000;
						int index = 0;
						for (Luchador invo : enemigosInvo) {
							if (invo.getPDVConBuff() <= tempPDV) {
								tempPDV = invo.getPDVConBuff();
								index = enemigosInvo.indexOf(invo);
							}
						}
						Luchador test = enemigosInvo.get(index);
						if (test != null)
							listaEnemigos.add(test);
						enemigosInvo.remove(index);
						i++;
					}
					i = 0;
					while (i < i3) {
						tempPDV = 200000;
						int index = 0;
						for (Luchador invo : enemigosNoInvo) {
							if (invo.getPDVConBuff() <= tempPDV) {
								tempPDV = invo.getPDVConBuff();
								index = enemigosNoInvo.indexOf(invo);
							}
						}
						Luchador test = enemigosNoInvo.get(index);
						if (test != null)
							listaEnemigos.add(test);
						enemigosNoInvo.remove(index);
						i++;
					}
				} catch (NullPointerException e) {
					return listaEnemigos;
				}
			} else
				try {
					int i2 = enemigosNoInvo.size(), i3 = enemigosInvo.size();
					while (i < i2) {
						tempPDV = 200000;
						int index = 0;
						for (Luchador invo : enemigosNoInvo) {
							if (invo.getPDVConBuff() <= tempPDV) {
								tempPDV = invo.getPDVConBuff();
								index = enemigosNoInvo.indexOf(invo);
							}
						}
						Luchador test = enemigosNoInvo.get(index);
						if (test != null)
							listaEnemigos.add(test);
						enemigosNoInvo.remove(index);
						i++;
					}
					i = 0;
					while (i < i3) {
						tempPDV = 200000;
						int index = 0;
						for (Luchador invo : enemigosInvo) {
							if (invo.getPDVConBuff() <= tempPDV) {
								tempPDV = invo.getPDVConBuff();
								index = enemigosInvo.indexOf(invo);
							}
						}
						Luchador test = enemigosInvo.get(index);
						if (test != null)
							listaEnemigos.add(test);
						enemigosInvo.remove(index);
						i++;
					}
				} catch (NullPointerException e) {
					return listaEnemigos;
				}
			return listaEnemigos;
		}
		
		private static ArrayList<Luchador> listaTodoLuchadores(Pelea pelea, Luchador lanzador) {
			Luchador enemigoMasCercano = luchadorMasCercano(pelea, lanzador);
			ArrayList<Luchador> listaEnemigos = new ArrayList<Luchador>();
			ArrayList<Luchador> enemigosNoInvo = new ArrayList<Luchador>();
			ArrayList<Luchador> enemigosInvo = new ArrayList<Luchador>();
			for (Luchador objetivo : pelea.luchadoresDeEquipo(3)) {
				if (objetivo.estaMuerto())
					continue;
				if (objetivo.esInvocacion())
					enemigosInvo.add(objetivo);
				else
					enemigosNoInvo.add(objetivo);
			}
			if (enemigoMasCercano != null)
				listaEnemigos.add(enemigoMasCercano);
			int i = 0;
			int tempPDV;
			Random rand = new Random();
			if (rand.nextBoolean()) {
				try {
					int i3 = enemigosNoInvo.size(), i2 = enemigosInvo.size();
					while (i < i2) {
						tempPDV = 200000;
						int index = 0;
						for (Luchador invo : enemigosInvo) {
							if (invo.getPDVConBuff() <= tempPDV) {
								tempPDV = invo.getPDVConBuff();
								index = enemigosInvo.indexOf(invo);
							}
						}
						Luchador test = enemigosInvo.get(index);
						if (test != null)
							listaEnemigos.add(test);
						enemigosInvo.remove(index);
						i++;
					}
					i = 0;
					while (i < i3) {
						tempPDV = 200000;
						int index = 0;
						for (Luchador invo : enemigosNoInvo) {
							if (invo.getPDVConBuff() <= tempPDV) {
								tempPDV = invo.getPDVConBuff();
								index = enemigosNoInvo.indexOf(invo);
							}
						}
						Luchador test = enemigosNoInvo.get(index);
						if (test != null)
							listaEnemigos.add(test);
						enemigosNoInvo.remove(index);
						i++;
					}
				} catch (NullPointerException e) {
					return listaEnemigos;
				}
			} else
				try {
					int i2 = enemigosNoInvo.size(), i3 = enemigosInvo.size();
					while (i < i2) {
						tempPDV = 200000;
						int index = 0;
						for (Luchador invo : enemigosNoInvo) {
							if (invo.getPDVConBuff() <= tempPDV) {
								tempPDV = invo.getPDVConBuff();
								index = enemigosNoInvo.indexOf(invo);
							}
						}
						Luchador test = enemigosNoInvo.get(index);
						if (test != null)
							listaEnemigos.add(test);
						enemigosNoInvo.remove(index);
						i++;
					}
					i = 0;
					while (i < i3) {
						tempPDV = 200000;
						int index = 0;
						for (Luchador invo : enemigosInvo) {
							if (invo.getPDVConBuff() <= tempPDV) {
								tempPDV = invo.getPDVConBuff();
								index = enemigosInvo.indexOf(invo);
							}
						}
						Luchador test = enemigosInvo.get(index);
						if (test != null)
							listaEnemigos.add(test);
						enemigosInvo.remove(index);
						i++;
					}
				} catch (NullPointerException e) {
					return listaEnemigos;
				}
			return listaEnemigos;
		}
		
		private static int atacaSiEsPosibleRecau(Pelea pelea, Luchador recaudador) {
			ArrayList<Luchador> listaEnemigos = objetivosMasCercanos(pelea, recaudador);
			StatsHechizos SH = null;
			Luchador objetivo = null;
			for (Luchador blanco : listaEnemigos) {
				SH = mejorHechizoRecau(pelea, recaudador, blanco);
				if (SH != null) {
					objetivo = blanco;
					break;
				}
			}
			if (objetivo == null || SH == null)
				return 666;
			int attack = pelea.intentarLanzarHechizo(recaudador, SH, objetivo.getCeldaPelea().getID());
			if (attack != 0)
				return attack;
			return 0;
		}
		
		private static int atacaSiEsPosiblePrisma(Pelea pelea, Luchador lanzador) {
			ArrayList<Luchador> listaEnemigos = listaEnemigosMenosPDV(pelea, lanzador);
			StatsHechizos SH = null;
			Luchador objetivo = null;
			for (Luchador blanco : listaEnemigos) {
				SH = mejorHechizoPrisma(pelea, lanzador, blanco);
				if (SH != null) {
					objetivo = blanco;
					break;
				}
			}
			if (objetivo == null || SH == null)
				return 666;
			int ataque = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
			if (ataque != 0)
				return ataque;
			return 0;
		}
		
		private static int atacaSiEsPosible1(Pelea pelea, Luchador lanzador) {
			ArrayList<Luchador> listaEnemigos = objetivosMasCercanos(pelea, lanzador);
			StatsHechizos SH = null;
			Luchador objetivo = null;
			for (Luchador blanco : listaEnemigos) {
				SH = mejorHechizo1(pelea, lanzador, blanco);
				if (SH != null) {
					objetivo = blanco;
					break;
				}
			}
			if (objetivo == null || SH == null)
				return 666;
			int ataque = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
			if (ataque != 0)
				return ataque;
			return 0;
		}
		
		private static int atacaSiEsPosible2(Pelea pelea, Luchador lanzador) {
			ArrayList<Luchador> listaEnemigos = objetivosMasCercanos(pelea, lanzador);
			StatsHechizos SH = null;
			Luchador objetivo = null;
			for (Luchador blanco : listaEnemigos) {
				SH = mejorHechizo2(pelea, lanzador, blanco);
				if (SH != null) {
					objetivo = blanco;
					break;
				}
			}
			if (objetivo == null || SH == null)
				return 666;
			int ataque = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
			if (ataque != 0)
				return ataque;
			return 0;
		}
		
		private static int atacaSiEsPosible3(Pelea pelea, Luchador lanzador) {
			ArrayList<Luchador> listaEnemigos = listaTodoLuchadores(pelea, lanzador);
			StatsHechizos SH = null;
			Luchador objetivo = null;
			for (Luchador blanco : listaEnemigos) {
				SH = mejorHechizo2(pelea, lanzador, blanco);
				if (SH != null) {
					objetivo = blanco;
					break;
				}
			}
			if (objetivo == null || SH == null)
				return 666;
			int ataque = pelea.intentarLanzarHechizo(lanzador, SH, objetivo.getCeldaPelea().getID());
			if (ataque != 0)
				return ataque;
			return 0;
		}
		
		private static boolean moverYAtacarSiEsPosible(Pelea pelea, Luchador lanzador) {
			ArrayList<Integer> celdas = Camino.listaCeldasDesdeLuchador(pelea, lanzador);
			if (celdas == null) {
				return false;
			}
			Luchador enemigo = enemigoMasCercano(pelea, lanzador);
			if (enemigo == null) {
				return false;
			}
			StatsHechizos hechizo;
			int distMin = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(), enemigo
					.getCeldaPelea().getID());
			ArrayList<StatsHechizos> hechizos = hechizosLanzables(lanzador, pelea, distMin);
			if (hechizos == null || hechizos.isEmpty()) {
				return false;
			}
			if (hechizos.size() == 1)
				hechizo = hechizos.get(0);
			else
				hechizo = hechizos.get(Formulas.getRandomValor(0, hechizos.size() - 1));
			ArrayList<Luchador> objetivos = objetivosMasCercanosAlHechizo(pelea, lanzador, hechizo);
			if (objetivos == null) {
				return false;
			}
			int celdaDestino = 0;
			Luchador objetivo = null;
			boolean encontrado = false;
			for (int celda : celdas) {
				for (Luchador O : objetivos) {
					if (pelea.puedeLanzarHechizo(lanzador, hechizo, O.getCeldaPelea(), celda)) {
						celdaDestino = celda;
						objetivo = O;
						encontrado = true;
					}
					if (encontrado)
						break;
				}
				if (encontrado)
					break;
			}
			if (celdaDestino == 0) {
				return false;
			}
			ArrayList<Celda> path = Camino.pathMasCortoEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(),
					celdaDestino, 0);
			if (path == null) {
				return false;
			}
			String pathStr = "";
			try {
				int tempCeldaID = lanzador.getCeldaPelea().getID();
				int tempDir = 0;
				for (Celda c : path) {
					char dir = Camino.getDirEntreDosCeldas(tempCeldaID, c.getID(), pelea.getMapaCopia(), true);
					if (dir == 0) {
						return false;
					}
					if (tempDir != dir) {
						if (path.indexOf(c) != 0)
							pathStr += Encriptador.celdaIDACodigo(tempCeldaID);
						pathStr += dir;
					}
					tempCeldaID = c.getID();
				}
				if (tempCeldaID != lanzador.getCeldaPelea().getID())
					pathStr += Encriptador.celdaIDACodigo(tempCeldaID);
			} catch (Exception e) {
				e.printStackTrace();
			}
			AccionDeJuego GA = new AccionDeJuego(0, 1, "");
			GA._args = pathStr;
			boolean resultado = pelea.puedeMoverseLuchador(lanzador, GA);
			if (resultado && objetivo != null && hechizo != null) {
				pelea.intentarLanzarHechizo(lanzador, hechizo, objetivo.getCeldaPelea().getID());
			}
			return resultado;
		}
		
		private static ArrayList<StatsHechizos> hechizosLanzables(Luchador lanzador, Pelea pelea, int distMin) {
			ArrayList<StatsHechizos> hechizos = new ArrayList<StatsHechizos>();
			if (lanzador.getMob() == null)
				return null;
			for (Entry<Integer, StatsHechizos> SH : lanzador.getMob().getHechizos().entrySet()) {
				StatsHechizos hechizo = SH.getValue();
				if (hechizo.getCostePA() > lanzador.getTempPA(pelea))
					continue;
				if (!HechizoLanzado.poderSigLanzamiento(lanzador, hechizo.getHechizoID()))
					continue;
				if (hechizo.getMaxLanzPorTurno() - HechizoLanzado.getNroLanzamientos(lanzador, hechizo.getHechizoID()) <= 0
						&& hechizo.getMaxLanzPorTurno() > 0)
					continue;
				if (calculaInfluenciaDaño(hechizo, lanzador, lanzador) >= 0)
					continue;
				hechizos.add(hechizo);
			}
			ArrayList<StatsHechizos> hechizosFinales = hechizosMasAMenosDaños(lanzador, hechizos);
			return hechizosFinales;
		}
		
		private static ArrayList<StatsHechizos> hechizosMasAMenosDaños(Luchador lanzador, ArrayList<StatsHechizos> hechizos) {
			if (hechizos == null)
				return null;
			ArrayList<StatsHechizos> hechizosFinales = new ArrayList<StatsHechizos>();
			Map<Integer, StatsHechizos> copia = new TreeMap<Integer, StatsHechizos>();
			for (StatsHechizos SH : hechizos) {
				copia.put(SH.getHechizoID(), SH);
			}
			int tempInfluencia = 0;
			int tempID = 0;
			while (copia.size() > 0) {
				tempInfluencia = 0;
				tempID = 0;
				for (Entry<Integer, StatsHechizos> SH : copia.entrySet()) {
					int influencia = -calculaInfluenciaDaño(SH.getValue(), lanzador, lanzador);
					if (influencia > tempInfluencia) {
						tempID = SH.getValue().getHechizoID();
						tempInfluencia = influencia;
					}
				}
				if (tempID == 0 || tempInfluencia == 0)
					break;
				hechizosFinales.add(copia.get(tempID));
				copia.remove(tempID);
			}
			return hechizosFinales;
		}
		
		private static ArrayList<Luchador> objetivosMasCercanosAlHechizo(Pelea pelea, Luchador lanzador, StatsHechizos hechizo) {
			ArrayList<Luchador> objetivos = new ArrayList<Luchador>();
			ArrayList<Luchador> objetivos1 = new ArrayList<Luchador>();
			int distMax = hechizo.getMaxAlc();
			distMax += lanzador.getTempPM(pelea);
			ArrayList<Luchador> objetivosP = listaTodoEnemigos(pelea, lanzador);
			for (Luchador entry : objetivosP) {
				Luchador objetivo = entry;
				int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(), objetivo
						.getCeldaPelea().getID());
				if (dist < distMax)
					objetivos.add(objetivo);
			}
			while (objetivos.size() > 0) {
				int index = 0;
				int dista = 1000;
				for (Luchador objetivo : objetivos) {
					int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(), objetivo
							.getCeldaPelea().getID());
					if (dist < dista) {
						dista = dist;
						index = objetivos.indexOf(objetivo);
					}
				}
				objetivos1.add(objetivos.get(index));
				objetivos.remove(index);
			}
			return objetivos1;
		}
		
		private static ArrayList<Luchador> objetivosMasCercanos(Pelea pelea, Luchador lanzador) {
			ArrayList<Luchador> objetivos = new ArrayList<Luchador>();
			ArrayList<Luchador> objetivos1 = listaTodoEnemigos(pelea, lanzador);
			while (objetivos.size() > 0) {
				int index = 0;
				int dista = 1000;
				for (Luchador objetivo : objetivos) {
					int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), lanzador.getCeldaPelea().getID(), objetivo
							.getCeldaPelea().getID());
					if (dist < dista) {
						dista = dist;
						index = objetivos.indexOf(objetivo);
					}
				}
				objetivos1.add(objetivos.get(index));
				objetivos.remove(index);
			}
			return objetivos1;
		}
		
		private static StatsHechizos mejorHechizoRecau(Pelea pelea, Luchador recaudador, Luchador objetivo) {
			int influenciaMax = 0;
			StatsHechizos sh = null;
			Map<Integer, StatsHechizos> hechiRecau = MundoDofus.getGremio(recaudador.getRecau().getGremioID()).getHechizos();
			if (objetivo == null)
				return null;
			for (Entry<Integer, StatsHechizos> SH1 : hechiRecau.entrySet()) {
				StatsHechizos hechizo1 = SH1.getValue();
				if (hechizo1 == null)
					continue;
				int tempInfluencia = 0, influencia1 = 0, influencia2 = 0;
				int PA = 6;
				int costePA[] = { 0, 0 };
				if (!pelea.puedeLanzarHechizo(recaudador, hechizo1, objetivo.getCeldaPelea(), -1))
					continue;
				tempInfluencia = calculaInfluenciaDaño(hechizo1, recaudador, objetivo);
				if (tempInfluencia == 0)
					continue;
				if (tempInfluencia > influenciaMax) {
					sh = hechizo1;
					costePA[0] = sh.getCostePA();
					influencia1 = tempInfluencia;
					influenciaMax = influencia1;
				}
				for (Entry<Integer, StatsHechizos> SH2 : hechiRecau.entrySet()) {
					StatsHechizos hechizo2 = SH2.getValue();
					if (hechizo2 == null)
						continue;
					if ( (PA - costePA[0]) < hechizo2.getCostePA())
						continue;
					if (!pelea.puedeLanzarHechizo(recaudador, hechizo2, objetivo.getCeldaPelea(), -1))
						continue;
					tempInfluencia = calculaInfluenciaDaño(hechizo2, recaudador, objetivo);
					if (tempInfluencia == 0)
						continue;
					if ( (influencia1 + tempInfluencia) > influenciaMax) {
						sh = hechizo2;
						costePA[1] = hechizo2.getCostePA();
						influencia2 = tempInfluencia;
						influenciaMax = influencia1 + influencia2;
					}
					for (Entry<Integer, StatsHechizos> SH3 : hechiRecau.entrySet()) {
						StatsHechizos hechizo3 = SH3.getValue();
						if (hechizo3 == null)
							continue;
						if ( (PA - costePA[0] - costePA[1]) < hechizo3.getCostePA())
							continue;
						if (!pelea.puedeLanzarHechizo(recaudador, hechizo3, objetivo.getCeldaPelea(), -1))
							continue;
						tempInfluencia = calculaInfluenciaDaño(hechizo3, recaudador, objetivo);
						if (tempInfluencia == 0)
							continue;
						if ( (tempInfluencia + influencia1 + influencia2) > influenciaMax) {
							sh = hechizo3;
							influenciaMax = tempInfluencia + influencia1 + influencia2;
						}
					}
				}
			}
			return sh;
		}
		
		private static StatsHechizos mejorHechizoPrisma(Pelea pelea, Luchador lanzador, Luchador objetivo) {
			StatsHechizos sh = null;
			ArrayList<StatsHechizos> posibles = new ArrayList<StatsHechizos>();
			if (objetivo == null)
				return null;
			try {
				for (Entry<Integer, StatsHechizos> SH : lanzador.getPrisma().getHechizos().entrySet()) {
					StatsHechizos statsH = SH.getValue();
					if (!pelea.puedeLanzarHechizo(lanzador, statsH, objetivo.getCeldaPelea(), -1))
						continue;
					posibles.add(statsH);
				}
			} catch (NullPointerException e) {
				return null;
			}
			if (posibles.isEmpty())
				return sh;
			if (posibles.size() == 1)
				return posibles.get(0);
			sh = posibles.get(Formulas.getRandomValor(0, posibles.size() - 1));
			return sh;
		}
		
		private static StatsHechizos mejorHechizo1(Pelea pelea, Luchador lanzador, Luchador objetivo) {
			int influenciaMax = 0;
			StatsHechizos sh = null;
			Map<Integer, StatsHechizos> hechiMob = lanzador.getMob().getHechizos();
			if (objetivo == null)
				return null;
			for (Entry<Integer, StatsHechizos> SH : hechiMob.entrySet()) {
				int tempInfluencia = 0, influencia1 = 0, influencia2 = 0;
				int PA = lanzador.getTempPA(pelea);
				int costePA[] = { 0, 0 };
				StatsHechizos hechizo1 = SH.getValue();
				if (!pelea.puedeLanzarHechizo(lanzador, hechizo1, objetivo.getCeldaPelea(), -1))
					continue;
				tempInfluencia = calculaInfluenciaDaño(hechizo1, lanzador, objetivo);
				if (tempInfluencia == 0)
					continue;
				if (tempInfluencia > influenciaMax) {
					sh = hechizo1;
					costePA[0] = sh.getCostePA();
					influencia1 = tempInfluencia;
					influenciaMax = influencia1;
				}
				for (Entry<Integer, StatsHechizos> SH2 : hechiMob.entrySet()) {
					StatsHechizos hechizo2 = SH2.getValue();
					if ( (PA - costePA[0]) < hechizo2.getCostePA())
						continue;
					if (!pelea.puedeLanzarHechizo(lanzador, hechizo2, objetivo.getCeldaPelea(), -1))
						continue;
					tempInfluencia = calculaInfluenciaDaño(hechizo2, lanzador, objetivo);
					if (tempInfluencia == 0)
						continue;
					if ( (influencia1 + tempInfluencia) > influenciaMax) {
						sh = hechizo2;
						costePA[1] = hechizo2.getCostePA();
						influencia2 = tempInfluencia;
						influenciaMax = influencia1 + influencia2;
					}
					for (Entry<Integer, StatsHechizos> SH3 : hechiMob.entrySet()) {
						StatsHechizos hechizo3 = SH3.getValue();
						if ( (PA - costePA[0] - costePA[1]) < hechizo3.getCostePA())
							continue;
						if (!pelea.puedeLanzarHechizo(lanzador, hechizo3, objetivo.getCeldaPelea(), -1))
							continue;
						tempInfluencia = calculaInfluenciaDaño(hechizo3, lanzador, objetivo);
						if (tempInfluencia == 0)
							continue;
						if ( (tempInfluencia + influencia1 + influencia2) > influenciaMax) {
							sh = hechizo3;
							influenciaMax = tempInfluencia + influencia1 + influencia2;
						}
					}
				}
			}
			return sh;
		}
		
		private static StatsHechizos mejorHechizo2(Pelea pelea, Luchador lanzador, Luchador objetivo) {
			StatsHechizos sh = null;
			ArrayList<StatsHechizos> posibles = new ArrayList<StatsHechizos>();
			if (objetivo == null)
				return null;
			try {
				for (Entry<Integer, StatsHechizos> SH : lanzador.getMob().getHechizos().entrySet()) {
					StatsHechizos hechizo = SH.getValue();
					if (!pelea.puedeLanzarHechizo(lanzador, hechizo, objetivo.getCeldaPelea(), -1))
						continue;
					posibles.add(hechizo);
				}
			} catch (NullPointerException e) {
				return null;
			}
			if (posibles.isEmpty())
				return sh;
			if (posibles.size() == 1)
				return posibles.get(0);
			sh = posibles.get(Formulas.getRandomValor(0, posibles.size() - 1));
			return sh;
		}
		
		/* private static int getMejorZonaAtaq(Pelea pelea, Luchador lanzador, StatsHechizos hechizo, int celdaLanzada) { if
		 * (hechizo.getAfectados().isEmpty() || (hechizo.getAfectados().charAt(0) == 'P' && hechizo.getAfectados().charAt(1) ==
		 * 'a')) { return 0; } ArrayList<Casilla> posibleLanzamientos = new ArrayList<Casilla>(); int celdaLanzamiento = -1; if
		 * (hechizo.getMaxAlc() != 0) { char arg1 = 'a'; if (hechizo.esLanzLinea()) { arg1 = 'X'; } else { arg1 = 'C'; } char[]
		 * tabla = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v'
		 * }; char arg2 = 'a'; if (hechizo.getMaxAlc() > 20) { arg2 = 'u'; } else { arg2 = tabla[hechizo.getMaxAlc()]; } String
		 * args = Character.toString(arg1) + Character.toString(arg2); posibleLanzamientos =
		 * Camino.getCeldasAfectadasEnElArea(pelea.getMapaCopia(), celdaLanzada, celdaLanzada, args, 0, false); } else {
		 * posibleLanzamientos.add(pelea.getMapaCopia().getCasilla(celdaLanzada)); } if (posibleLanzamientos == null) { return -1;
		 * } int nroObjetivos = 0; for (Casilla celda : posibleLanzamientos) { try { if (!pelea.puedeLanzarHechizo(lanzador,
		 * hechizo, celda, celdaLanzada)) continue; int num = 0; int tempObjetivos = 0; ArrayList<EfectoHechizo> test = new
		 * ArrayList<EfectoHechizo>(); test.addAll(hechizo.getEfectos()); for (EfectoHechizo SE : test) { try { if (SE == null)
		 * continue; if (SE.getValor() == -1) continue; int POnum = num * 2; ArrayList<Casilla> celdas =
		 * Camino.getCeldasAfectadasEnElArea(pelea.getMapaCopia(), celda.getID(), celdaLanzada, hechizo.getAfectados(), POnum,
		 * false); for (Casilla c : celdas) { if (c.getPrimerLuchador() == null) continue; if
		 * (c.getPrimerLuchador().getParamEquipo() != lanzador.getParamEquipo()) tempObjetivos++; } } catch (Exception e) {}
		 * num++; } if (tempObjetivos > nroObjetivos) { nroObjetivos = tempObjetivos; celdaLanzamiento = celda.getID(); } } catch
		 * (Exception E) {} } if (nroObjetivos > 0 && celdaLanzamiento != -1) return celdaLanzamiento + nroObjetivos * 1000; else
		 * return 0; } */
		private static int calculaInfluenciaCura(StatsHechizos SH) {
			int inf = 0;
			for (EfectoHechizo SE : SH.getEfectos()) {
				int efectoID = SE.getEfectoID();
				if (efectoID == 108 || efectoID == 81)
					inf += 100 * Formulas.getMaxValor(SE.getValores());
			}
			return inf;
		}
		
		private static int calculaInfluenciaDaño(StatsHechizos SH, Luchador lanzador, Luchador objetivo) {
			int influenciaTotal = 0;
			for (EfectoHechizo SE : SH.getEfectos()) {
				int inf = 0;
				switch (SE.getEfectoID()) {
					case 5:// empuja de X casillas
						inf = 500 * Formulas.getMaxValor(SE.getValores());
						break;
					case 77:// robo de PM
						inf = 1500 * Formulas.getMaxValor(SE.getValores());
						break;
					case 84:// robo de PA
						inf = 1500 * Formulas.getMaxValor(SE.getValores());
						break;
					case 89:// Daños % vida neutral
						inf = 200 * Formulas.getMaxValor(SE.getValores());
						break;
					case 91:// robo de vida Agua
						inf = 150 * Formulas.getMaxValor(SE.getValores());
						break;
					case 92:// robo de vida Tierra
						inf = 150 * Formulas.getMaxValor(SE.getValores());
						break;
					case 93:// robo de vida Aire
						inf = 150 * Formulas.getMaxValor(SE.getValores());
						break;
					case 94:// robo de vida fuego
						inf = 150 * Formulas.getMaxValor(SE.getValores());
						break;
					case 95:// robo de vida neutral
						inf = 150 * Formulas.getMaxValor(SE.getValores());
						break;
					case 96:// Daños Eau
						inf = 100 * Formulas.getMaxValor(SE.getValores());
						break;
					case 97:// Daños Tierra
						inf = 100 * Formulas.getMaxValor(SE.getValores());
						break;
					case 98:// Daños Aire
						inf = 100 * Formulas.getMaxValor(SE.getValores());
						break;
					case 99:// Daños fuego
						inf = 100 * Formulas.getMaxValor(SE.getValores());
						break;
					case 100:// Daños neutral
						inf = 100 * Formulas.getMaxValor(SE.getValores());
						break;
					case 101:// menos PA
						inf = 1000 * Formulas.getMaxValor(SE.getValores());
						break;
					case 111:// + PA
						inf = -1000 * Formulas.getMaxValor(SE.getValores());
						break;
					case 117:// + alcance
						inf = -500 * Formulas.getMaxValor(SE.getValores());
						break;
					case 121:// + Daños
						inf = -100 * Formulas.getMaxValor(SE.getValores());
						break;
					case 122:// + fallos criticos
						inf = 200 * Formulas.getMaxValor(SE.getValores());
						break;
					case 123:// + suerte
						inf = -200 * Formulas.getMaxValor(SE.getValores());
						break;
					case 124:// + sabiduria
						inf = -200 * Formulas.getMaxValor(SE.getValores());
						break;
					case 125:// + vitalidad
						inf = -200 * Formulas.getMaxValor(SE.getValores());
						break;
					case 126:// + inteligencia
						inf = -200 * Formulas.getMaxValor(SE.getValores());
						break;
					case 127:// menos PM
						inf = 1000 * Formulas.getMaxValor(SE.getValores());
						break;
					case 128:// + PM
						inf = -1000 * Formulas.getMaxValor(SE.getValores());
						break;
					case 131:// veneno X pdv por PA
						inf = 300 * Formulas.getMaxValor(SE.getValores());
						break;
					case 132:// desechiza
						inf = 2000;
						break;
					case 138:// + % Daños
						inf = -50 * Formulas.getMaxValor(SE.getValores());
						break;
					case 150:// invisibilidad
						inf = -2000; // amigos
						break;
					case 168:// -PA no esquivable
						inf = 1000 * Formulas.getMaxValor(SE.getValores());
						break;
					case 169:// -PM no esquivable
						inf = 1000 * Formulas.getMaxValor(SE.getValores());
						break;
					case 210:// resistencia
						inf = -300 * Formulas.getMaxValor(SE.getValores());
						break;
					case 211:// resistencia
						inf = -300 * Formulas.getMaxValor(SE.getValores());
						break;
					case 212:// resistencia
						inf = -300 * Formulas.getMaxValor(SE.getValores());
						break;
					case 213:// resistencia
						inf = -300 * Formulas.getMaxValor(SE.getValores());
						break;
					case 214:// resistencia
						inf = -300 * Formulas.getMaxValor(SE.getValores());
						break;
					case 215:// debilidad
						inf = 300 * Formulas.getMaxValor(SE.getValores());
						break;
					case 216:// debilidad
						inf = 300 * Formulas.getMaxValor(SE.getValores());
						break;
					case 217:// debilidad
						inf = 300 * Formulas.getMaxValor(SE.getValores());
						break;
					case 218:// debilidad
						inf = 300 * Formulas.getMaxValor(SE.getValores());
						break;
					case 219:// debilidad
						inf = 300 * Formulas.getMaxValor(SE.getValores());
						break;
					case 265:// Reduccion a los Daños
						inf = -250 * Formulas.getMaxValor(SE.getValores());
						break;
				}
				if (objetivo == null)
					continue;
				if (lanzador.getParamEquipoAliado() == objetivo.getParamEquipoAliado())
					influenciaTotal -= (inf);
				else
					influenciaTotal += (inf);
			}
			return influenciaTotal;
		}
	}
}
