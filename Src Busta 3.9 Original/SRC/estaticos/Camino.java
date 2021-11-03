
package estaticos;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import variables.*;
import variables.Mapa.Celda;
import variables.Pelea.Luchador;
import variables.Pelea.Trampa;

public class Camino {
	private static Integer _nSteps = new Integer(0);
	
	public static int caminoValido(Mapa mapa, int celdaID, AtomicReference<String> pathRef, Pelea pelea) {
		synchronized (_nSteps) {
			_nSteps = 0;
			int nuevaCelda = celdaID;
			int Steps = 0;
			String path = pathRef.get();
			String nuevoPath = "";
			for (int i = 0; i < path.length(); i += 3) {
				if (path.length() < (i + 3))
					return Steps;
				String miniPath = path.substring(i, i + 3);
				char dir = miniPath.charAt(0);
				int celdaIDDeco = Encriptador.celdaCodigoAID(miniPath.substring(1));
				_nSteps = 0;
				if (pelea != null && i != 0 && getEnemigoAlrededor(nuevaCelda, mapa, pelea) != null) {
					pathRef.set(nuevoPath);
					return Steps;
				}
				if (pelea != null && i != 0) {
					for (Trampa p : pelea.getTrampas()) {
						int dist = distanciaEntreDosCeldas(mapa, p.getCelda().getID(), nuevaCelda);
						if (dist <= p.getTamaño()) {
							pathRef.set(nuevoPath);
							return Steps;
						}
					}
				}
				String[] aPathInfos = ValidSinglePath(nuevaCelda, miniPath, mapa, pelea).split(":");
				if (aPathInfos[0].equalsIgnoreCase("stop")) {
					nuevaCelda = Integer.parseInt(aPathInfos[1]);
					Steps += _nSteps;
					nuevoPath += dir + Encriptador.celdaIDACodigo(nuevaCelda);
					pathRef.set(nuevoPath);
					return -Steps;
				} else if (aPathInfos[0].equalsIgnoreCase("ok")) {
					nuevaCelda = celdaIDDeco;
					Steps += _nSteps;
				} else {
					pathRef.set(nuevoPath);
					return -1000;
				}
				nuevoPath += dir + Encriptador.celdaIDACodigo(nuevaCelda);
			}
			pathRef.set(nuevoPath);
			return Steps;
		}
	}
	
	public static Luchador getEnemigoAlrededor(int celdaID, Mapa mapa, Pelea pelea) {
		char[] dirs = { 'b', 'd', 'f', 'h' };
		for (char dir : dirs) {
			Celda sigCelda = mapa.getCelda(getSigIDCeldaMismaDir(celdaID, dir, mapa, false));
			if (sigCelda == null)
				continue;
			Luchador luchador = sigCelda.getPrimerLuchador();
			if (luchador != null) {
				if (luchador.getEquipoBin() != pelea.getLuchadorTurno().getEquipoBin())
					return luchador;
			}
		}
		return null;
	}
	
	public static boolean hayAlrededor(Mapa mapa, Pelea pelea, Luchador l, boolean amigo) {
		char[] dirs = { 'b', 'd', 'f', 'h' };
		for (char dir : dirs) {
			Celda sigCelda = mapa.getCelda(getSigIDCeldaMismaDir(l.getCeldaPelea().getID(), dir, mapa, false));
			if (sigCelda == null)
				continue;
			Luchador luchador = sigCelda.getPrimerLuchador();
			if (luchador != null) {
				if (amigo) {
					if (luchador.getEquipoBin() == l.getEquipoBin())
						return true;
				} else {
					if (luchador.getEquipoBin() != l.getEquipoBin())
						return true;
				}
			}
		}
		return false;
	}
	
	public static boolean esSiguienteA(int celda1, int celda2, Mapa mapa) {
		int ancho = mapa.getAncho();
		if (celda1 + (ancho - 1) == celda2 || celda1 + (ancho) == celda2 || celda1 - (ancho - 1) == celda2
				|| celda1 - ancho == celda2)
			return true;
		else
			return false;
	}
	
	public static String ValidSinglePath(int celdaID, String Path, Mapa mapa, Pelea pelea) {
		_nSteps = 0;
		char dir = Path.charAt(0);
		int celdaIDDeco = Encriptador.celdaCodigoAID(Path.substring(1));
		if (pelea != null && pelea.celdaOcupada(celdaIDDeco))
			return "no:";
		int ultimaCelda = celdaID;
		for (_nSteps = 1; _nSteps <= 64; _nSteps++) {
			if (getSigIDCeldaMismaDir(ultimaCelda, dir, mapa, pelea != null) == celdaIDDeco) {
				if (pelea != null && pelea.celdaOcupada(celdaIDDeco))
					return "stop:" + ultimaCelda;
				if (mapa.getCelda(celdaIDDeco).esCaminable(true))
					return "ok:";
				else {
					_nSteps--;
					return ("stop:" + ultimaCelda);
				}
			} else
				ultimaCelda = getSigIDCeldaMismaDir(ultimaCelda, dir, mapa, pelea != null);
			if (pelea != null && pelea.celdaOcupada(ultimaCelda)) {
				return "no:";
			}
			if (pelea != null) {
				if (getEnemigoAlrededor(ultimaCelda, mapa, pelea) != null) {
					return "stop:" + ultimaCelda;
				}
				for (Trampa p : pelea.getTrampas()) {
					int dist = distanciaEntreDosCeldas(mapa, p.getCelda().getID(), ultimaCelda);
					if (dist <= p.getTamaño()) {
						return "stop:" + ultimaCelda;
					}
				}
			}
		}
		return "no:";
	}
	
	public static int getSigIDCeldaMismaDir(int celdaID, char direccion, Mapa mapa, boolean combate) {
		switch (direccion) {
			case 'a':
				return combate ? -1 : celdaID + 1;
			case 'b':
				return celdaID + mapa.getAncho(); // diagonal derecha abajo
			case 'c':
				return combate ? -1 : celdaID + (mapa.getAncho() * 2 - 1);
			case 'd':
				return celdaID + (mapa.getAncho() - 1); // diagonal izquierda abajo
			case 'e':
				return combate ? -1 : celdaID - 1;
			case 'f':
				return celdaID - mapa.getAncho(); // diagonal izquierda arriba
			case 'g':
				return combate ? -1 : celdaID - (mapa.getAncho() * 2 - 1);
			case 'h':
				return celdaID - mapa.getAncho() + 1;// diagonal derecha arriba
		}
		return -1;
	}
	
	public static int getSigIDCeldaMismaDir(int celdaID, char direccion, short mapaID) {
		Mapa mapa = MundoDofus.getMapa(mapaID);
		if (mapa == null)
			return -1;
		switch (direccion) {
			case 'b':
				return celdaID + mapa.getAncho(); // diagonal derecha abajo
			case 'd':
				return celdaID + (mapa.getAncho() - 1); // diagonal izquierda abajo
			case 'f':
				return celdaID - mapa.getAncho(); // diagonal izquierda arriba
			case 'h':
				return celdaID - mapa.getAncho() + 1;// diagonal derecha arriba
		}
		return -1;
	}
	
	public static int distanciaEntreDosCeldas(Mapa mapa, int id1, int id2) {
		if (id1 == id2)
			return 0;
		if (mapa == null)
			return 0;
		int diffX = Math.abs(getCeldaCoordenadaX(mapa, id1) - getCeldaCoordenadaX(mapa, id2));
		int diffY = Math.abs(getCeldaCoordenadaY(mapa, id1) - getCeldaCoordenadaY(mapa, id2));
		return (diffX + diffY);
	}
	
	public static char getDirAleatorio() {
		char[] direcciones = { 'b', 'd', 'f', 'h' };
		int aleatorio = Formulas.getRandomValor(0, 3);
		return direcciones[aleatorio];
	}
	
	public static int getNuevaCeldaDespuesGolpe(Mapa mapa, Celda celdaInicio, Celda celdaObjetivo, int valor, Pelea pelea,
			Luchador objetivo) {
		if (celdaInicio.getID() == celdaObjetivo.getID())
			return 0;
		char c = getDirEntreDosCeldas(celdaInicio.getID(), celdaObjetivo.getID(), mapa, true);
		int id = celdaObjetivo.getID();
		if (valor < 0) {
			c = getDireccionOpuesta(c);
			valor = -valor;
		}
		for (int a = 0; a < valor; a++) {
			int sigCelda = getSigIDCeldaMismaDir(id, c, mapa, true);
			if (mapa.getCelda(sigCelda) != null && mapa.getCelda(sigCelda).esCaminable(true)
					&& mapa.getCelda(sigCelda).getLuchadores().isEmpty()) {
				id = sigCelda;
				for (Trampa trampa : pelea.getTrampas()) {
					int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), id);
					if (dist <= trampa.getTamaño()) {
						return id;
					}
				}
			} else
				return - (valor - a);
		}
		if (id == celdaObjetivo.getID())
			id = 0;
		return id;
	}
	
	public static int getCeldaDespEmpujon(Mapa mapa, Celda celdaLanz, Celda celdaObje, int valor, Pelea pelea,
			Luchador objetivo) {
		if (celdaLanz.getID() == celdaObje.getID())
			return 0;
		char c = getDirEntreDosCeldas(celdaLanz.getID(), celdaObje.getID(), mapa, true);
		int id = celdaObje.getID();
		if (valor < 0) {
			c = getDireccionOpuesta(c);
			valor = -valor;
		}
		for (int a = 0; a < valor; a++) {
			int sigCelda = getSigIDCeldaMismaDir(id, c, mapa, true);
			if (mapa.getCelda(sigCelda) != null && mapa.getCelda(sigCelda).esCaminable(true)
					&& mapa.getCelda(sigCelda).getLuchadores().isEmpty()) {
				id = sigCelda;
				for (Trampa trampa : pelea.getTrampas()) {
					int dist = Camino.distanciaEntreDosCeldas(pelea.getMapaCopia(), trampa.getCelda().getID(), id);
					if (dist <= trampa.getTamaño()) {
						return id;
					}
				}
			} else
				return - (valor - a);
		}
		if (id == celdaObje.getID())
			id = 0;
		return id;
	}
	
	public static char getDireccionOpuesta(char c) {
		switch (c) {
			case 'a':
				return 'e';
			case 'b':
				return 'f';
			case 'c':
				return 'g';
			case 'd':
				return 'h';
			case 'e':
				return 'a';
			case 'f':
				return 'b';
			case 'g':
				return 'c';
			case 'h':
				return 'd';
		}
		return 0x00;
	}
	
	public static boolean siCeldasEstanEnMismaLinea(Mapa map, int c1, int c2, char dir) {
		if (c1 == c2)
			return true;
		if (dir != 'z') {
			for (int a = 0; a < 70; a++) {
				if (getSigIDCeldaMismaDir(c1, dir, map, true) == c2)
					return true;
				if (getSigIDCeldaMismaDir(c1, dir, map, true) == -1)
					break;
				c1 = getSigIDCeldaMismaDir(c1, dir, map, true);
			}
		} else {
			char[] dirs = { 'b', 'd', 'f', 'h' };
			for (char d : dirs) {
				int c = c1;
				for (int a = 0; a < 70; a++) {
					if (getSigIDCeldaMismaDir(c, d, map, true) == c2)
						return true;
					c = getSigIDCeldaMismaDir(c, d, map, true);
				}
			}
		}
		return false;
	}
	
	public static ArrayList<Luchador> getObjetivosZonaArma(Pelea pelea, int tipo, Celda celda, int celdaIDLanzador) {
		ArrayList<Luchador> objetivos = new ArrayList<Luchador>();
		char c = getDirEntreDosCeldas(celdaIDLanzador, celda.getID(), pelea.getMapaCopia(), true);
		if (c == 0) {
			if (celda.getPrimerLuchador() != null)
				objetivos.add(celda.getPrimerLuchador());
			return objetivos;
		}
		switch (tipo) {
			case CentroInfo.ITEM_TIPO_MARTILLO:
				Luchador f = getFighter2CellBefore(celdaIDLanzador, c, pelea.getMapaCopia());
				if (f != null)
					objetivos.add(f);
				Luchador g = getPrimerLuchadorMismaDireccion(pelea.getMapaCopia(), celdaIDLanzador, (char) (c - 1));
				if (g != null)
					objetivos.add(g);// Agregar casilla a izquierda
				Luchador h = getPrimerLuchadorMismaDireccion(pelea.getMapaCopia(), celdaIDLanzador, (char) (c + 1));
				if (h != null)
					objetivos.add(h);// Agregar casilla a derecha
				Luchador i = celda.getPrimerLuchador();
				if (i != null)
					objetivos.add(i);
				break;
			case CentroInfo.ITEM_TIPO_BASTON:
				Luchador j = getPrimerLuchadorMismaDireccion(pelea.getMapaCopia(), celdaIDLanzador, (char) (c - 1));
				if (j != null)
					objetivos.add(j);// Agregar casilla a izquierda
				Luchador k = getPrimerLuchadorMismaDireccion(pelea.getMapaCopia(), celdaIDLanzador, (char) (c + 1));
				if (k != null)
					objetivos.add(k);// Agregar casilla a derecha
				Luchador l = celda.getPrimerLuchador();
				if (l != null)
					objetivos.add(l);// Agregar casilla objetivo
				break;
			case CentroInfo.ITEM_TIPO_PICO:
			case CentroInfo.ITEM_TIPO_ESPADA:
			case CentroInfo.ITEM_TIPO_GUADAÑA:
			case CentroInfo.ITEM_TIPO_DAGAS:
			case CentroInfo.ITEM_TIPO_VARITA:
			case CentroInfo.ITEM_TIPO_PALA:
			case CentroInfo.ITEM_TIPO_ARCO:
			case CentroInfo.ITEM_TIPO_HACHA:
				Luchador m = celda.getPrimerLuchador();
				if (m != null)
					objetivos.add(m);
				break;
		}
		return objetivos;
	}
	
	private static Luchador getPrimerLuchadorMismaDireccion(Mapa mapa, int id, char dir) {
		if (dir == (char) ('a' - 1))
			dir = 'h';
		if (dir == (char) ('h' + 1))
			dir = 'a';
		return mapa.getCelda(getSigIDCeldaMismaDir(id, dir, mapa, false)).getPrimerLuchador();
	}
	
	private static Luchador getFighter2CellBefore(int celdaID, char dir, Mapa mapa) {
		int nueva2CeldaID = getSigIDCeldaMismaDir(getSigIDCeldaMismaDir(celdaID, dir, mapa, false), dir, mapa, false);
		return mapa.getCelda(nueva2CeldaID).getPrimerLuchador();
	}
	
	public static char getCharDir(int direccion) {
		switch (direccion) {
			case 0:
				return 'a';
			case 1:
				return 'b';
			case 2:
				return 'c';
			case 3:
				return 'd';
			case 4:
				return 'e';
			case 5:
				return 'f';
			case 6:
				return 'g';
			case 7:
				return 'h';
		}
		return 'b';
	}
	
	public static char getDirEntreDosCeldas(Mapa mapa, int id1, int id2) {
		if (id1 == id2)
			return 0;
		if (mapa == null)
			return 0;
		int difX = (getCeldaCoordenadaX(mapa, id1) - getCeldaCoordenadaX(mapa, id2));
		int difY = (getCeldaCoordenadaY(mapa, id1) - getCeldaCoordenadaY(mapa, id2));
		int difXabs = Math.abs(difX);
		int difYabs = Math.abs(difY);
		if (difXabs > difYabs) {
			if (difX > 0)
				return 'f';
			else
				return 'b';
		} else {
			if (difY > 0)
				return 'h';
			else
				return 'd';
		}
	}
	
	public static char getDirEntreDosCeldas(int celdaID1, int celdaID2, Mapa mapa, boolean combate) {
		ArrayList<Character> direcciones = new ArrayList<Character>();
		direcciones.add('b');
		direcciones.add('d');
		direcciones.add('f');
		direcciones.add('h');
		if (!combate) {
			direcciones.add('a');
			direcciones.add('b');
			direcciones.add('c');
			direcciones.add('d');
		}
		for (char c : direcciones) {
			int celda = celdaID1;
			for (int i = 0; i <= 64; i++) {
				if (getSigIDCeldaMismaDir(celda, c, mapa, combate) == celdaID2)
					return c;
				celda = getSigIDCeldaMismaDir(celda, c, mapa, combate);
			}
		}
		return 0;
	}
	
	public static ArrayList<Celda> getCeldasAfectadasEnElArea(Mapa mapa, int celdaID, int celdaIDLanzador, String afectados,
			int posTipoAlcance, boolean esGC) {
		ArrayList<Celda> cases = new ArrayList<Celda>();
		if (mapa == null)
			return cases;
		if (mapa.getCelda(celdaID) == null)
			return cases;
		cases.add(mapa.getCelda(celdaID));
		int tamaño = Encriptador.getNumeroPorValorHash(afectados.charAt(posTipoAlcance + 1));
		switch (afectados.charAt(posTipoAlcance)) {
			case 'C':// Circulo
				for (int a = 0; a < tamaño; a++) {
					char[] dirs = { 'b', 'd', 'f', 'h' };
					ArrayList<Celda> cases2 = new ArrayList<Celda>();
					cases2.addAll(cases);
					for (Celda aCell : cases2) {
						for (char d : dirs) {
							Celda cell = mapa.getCelda(Camino.getSigIDCeldaMismaDir(aCell.getID(), d, mapa, true));
							if (cell == null)
								continue;
							if (!cases.contains(cell))
								cases.add(cell);
						}
					}
				}
				break;
			case 'X':// Cruz
				char[] dirs = { 'b', 'd', 'f', 'h' };
				for (char d : dirs) {
					int cID = celdaID;
					for (int a = 0; a < tamaño; a++) {
						cases.add(mapa.getCelda(getSigIDCeldaMismaDir(cID, d, mapa, true)));
						cID = getSigIDCeldaMismaDir(cID, d, mapa, true);
					}
				}
				break;
			case 'L':// Linea
				char dir = Camino.getDirEntreDosCeldas(celdaIDLanzador, celdaID, mapa, true);
				for (int a = 0; a < tamaño; a++) {
					cases.add(mapa.getCelda(getSigIDCeldaMismaDir(celdaID, dir, mapa, true)));
					celdaID = getSigIDCeldaMismaDir(celdaID, dir, mapa, true);
				}
				break;
			case 'P':// Jugador?
			case 'T':// Jugador?
				break;
			default:
				System.out.println("[FIXME]Tipo de alcance no reconocido: " + afectados.charAt(0));
				break;
		}
		return cases;
	}
	
	public static int getCeldaCoordenadaX(Mapa mapa, int celdaID) {
		if (mapa == null)
			return 0;
		int w = mapa.getAncho();
		return ( (celdaID - (w - 1) * getCeldaCoordenadaY(mapa, celdaID)) / w);
	}
	
	public static int getCeldaCoordenadaY(Mapa mapa, int celdaID) {
		int w = mapa.getAncho();
		int loc5 = (int) (celdaID / ( (w * 2) - 1));
		int loc6 = celdaID - loc5 * ( (w * 2) - 1);
		int loc7 = loc6 % w;
		return (loc5 - loc7);
	}
	
	public static boolean checkearLineaDeVista(Mapa mapa, int celda1, int celda2, Luchador luchador) {
		if (luchador.getPersonaje() != null)
			return true;
		int dist = distanciaEntreDosCeldas(mapa, celda1, celda2);
		ArrayList<Integer> los = new ArrayList<Integer>();
		if (dist > 2)
			los = getLineaDeVista(celda1, celda2, mapa);
		if (los != null && dist > 2) {
			for (int i : los) {
				if (i != celda1 && i != celda2 && !mapa.getCelda(i).lineaDeVistaBloqueada())
					return false;
			}
		}
		if (dist > 2) {
			int cell = getCeldaMasCercanaAlrededor(mapa, celda2, celda1, null);
			if (cell != -1 && !mapa.getCelda(cell).lineaDeVistaBloqueada())
				return false;
		}
		return true;
	}
	
	public static int getCeldaMasCercanaAlrededor(Mapa mapa, int celdaInicio, int celdaFinal, ArrayList<Celda> celdasProhibidas) {
		int dist = 1000;
		int celdaID = celdaInicio;
		if (celdasProhibidas == null)
			celdasProhibidas = new ArrayList<Celda>();
		char[] dirs = { 'b', 'd', 'f', 'h' };
		for (char d : dirs) {
			int sigCelda = Camino.getSigIDCeldaMismaDir(celdaInicio, d, mapa, true);
			Celda C = mapa.getCelda(sigCelda);
			if (C == null)
				break;
			int dis = Camino.distanciaEntreDosCeldas(mapa, celdaFinal, sigCelda);
			if (dis < dist && C.esCaminable(true) && C.getPrimerLuchador() == null && !celdasProhibidas.contains(C)) {
				dist = dis;
				celdaID = sigCelda;
			}
		}
		return celdaID == celdaInicio ? -1 : celdaID;
	}
	
	public static int getCeldaMasCercanaAlrededor2(Mapa mapa, int celdaInicio, int celdaFinal, int alcanceMin, int alcanceMax) {
		int dist = 1000;
		int celdaID = celdaInicio;
		char d = getDirEntreDosCeldas(mapa, celdaInicio, celdaFinal);
		int celdaInicio2 = celdaInicio;
		int sigCelda = 0;
		int i = 0;
		while (i < alcanceMax) {
			sigCelda = Camino.getSigIDCeldaMismaDir(celdaInicio2, d, mapa, true);
			celdaInicio2 = sigCelda;
			i++;
			if (i > alcanceMin) {
				Celda C = mapa.getCelda(sigCelda);
				if (C == null)
					continue;
				if (C.esCaminable(true) && C.getPrimerLuchador() == null)
					break;
			}
		}
		Celda C = mapa.getCelda(sigCelda);
		if (C == null)
			return -1;
		int dis = Camino.distanciaEntreDosCeldas(mapa, celdaFinal, sigCelda);
		if (dis < dist && C.esCaminable(true) && C.getPrimerLuchador() == null) {
			dist = dis;
			celdaID = sigCelda;
		}
		return celdaID == celdaInicio ? -1 : celdaID;
	}
	
	public static ArrayList<Celda> pathMasCortoEntreDosCeldas(Mapa mapa, int inicio, int destino, int distMax) {
		ArrayList<Celda> tempPath = new ArrayList<Celda>();
		ArrayList<Celda> tempPath2 = new ArrayList<Celda>();
		ArrayList<Celda> celdasCerradas = new ArrayList<Celda>();
		int limite = 1000;
		Celda tempCelda = mapa.getCelda(inicio);
		int stepNum = 0;
		boolean stop = false;
		while (!stop && stepNum++ <= limite) {
			int celdaMasCercana = getCeldaMasCercanaAlrededor(mapa, tempCelda.getID(), destino, celdasCerradas);
			if (celdaMasCercana == -1) {
				celdasCerradas.add(tempCelda);
				if (tempPath.size() > 0) {
					tempPath.remove(tempPath.size() - 1);
					if (tempPath.size() > 0)
						tempCelda = tempPath.get(tempPath.size() - 1);
					else
						tempCelda = mapa.getCelda(inicio);
				} else {
					tempCelda = mapa.getCelda(inicio);
				}
			} else if (distMax == 0 && celdaMasCercana == destino) {
				tempPath.add(mapa.getCelda(destino));
				break;
			} else if (distMax > Camino.distanciaEntreDosCeldas(mapa, celdaMasCercana, destino)) {
				tempPath.add(mapa.getCelda(destino));
				break;
			} else {
				tempCelda = mapa.getCelda(celdaMasCercana);
				celdasCerradas.add(tempCelda);
				tempPath.add(tempCelda);
			}
		}
		tempCelda = mapa.getCelda(inicio);
		celdasCerradas.clear();
		if (!tempPath.isEmpty()) {
			celdasCerradas.add(tempPath.get(0));
		}
		while (!stop && stepNum++ <= limite) {
			int nearestCell = getCeldaMasCercanaAlrededor(mapa, tempCelda.getID(), destino, celdasCerradas);
			if (nearestCell == -1) {
				celdasCerradas.add(tempCelda);
				if (tempPath2.size() > 0) {
					tempPath2.remove(tempPath2.size() - 1);
					if (tempPath2.size() > 0)
						tempCelda = tempPath2.get(tempPath2.size() - 1);
					else
						tempCelda = mapa.getCelda(inicio);
				} else {
					tempCelda = mapa.getCelda(inicio);
				}
			} else if (distMax == 0 && nearestCell == destino) {
				tempPath2.add(mapa.getCelda(destino));
				break;
			} else if (distMax > Camino.distanciaEntreDosCeldas(mapa, nearestCell, destino)) {
				tempPath2.add(mapa.getCelda(destino));
				break;
			} else {
				tempCelda = mapa.getCelda(nearestCell);
				celdasCerradas.add(tempCelda);
				tempPath2.add(tempCelda);
			}
		}
		if ( (tempPath2.size() < tempPath.size() && tempPath2.size() > 0) || tempPath.isEmpty())
			tempPath = tempPath2;
		return tempPath;
	}
	
	public static ArrayList<Integer> listaCeldasDesdeLuchador(Pelea pelea, Luchador luchador) {
		ArrayList<Integer> celdas = new ArrayList<Integer>();
		int celdaInicio = luchador.getCeldaPelea().getID();
		int[] tempPath;
		int i = 0;
		if (luchador.getTempPM(pelea) > 0)
			tempPath = new int[luchador.getTempPM(pelea)];
		else
			return null;
		if (tempPath.length == 0)
			return null;
		while (tempPath[0] != 5) {
			tempPath[i]++;
			if (tempPath[i] == 5 && i != 0) {
				tempPath[i] = 0;
				i--;
			} else {
				int tempCelda = getCeldaDesdePath(celdaInicio, tempPath, pelea.getMapaCopia());
				Celda celdaTemp = pelea.getMapaCopia().getCelda(tempCelda);
				if (celdaTemp == null)
					continue;
				if (celdaTemp.esCaminable(true) && celdaTemp.getPrimerLuchador() == null) {
					if (!celdas.contains(tempCelda)) {
						celdas.add(tempCelda);
						if (i < tempPath.length - 1)
							i++;
					}
				}
			}
		}
		return listaCeldasPorDistancia(pelea, luchador, celdas);
	}
	
	public static int getCeldaDesdePath(int inicio, int[] path, Mapa mapa) {
		int celda = inicio, i = 0;
		int ancho = mapa.getAncho();
		while (i < path.length) {
			if (path[i] == 1)
				celda -= ancho;
			if (path[i] == 2)
				celda -= (ancho - 1);
			if (path[i] == 3)
				celda += ancho;
			if (path[i] == 4)
				celda += (ancho - 1);
			i++;
		}
		return celda;
	}
	
	public static ArrayList<Integer> listaCeldasPorDistancia(Pelea pelea, Luchador luchador, ArrayList<Integer> celdas) {
		ArrayList<Integer> celdasPelea = new ArrayList<Integer>();
		ArrayList<Integer> copiaCeldas = celdas;
		int dist = 100;
		int tempCelda = 0;
		int tempIndex = 0;
		while (copiaCeldas.size() > 0) {
			dist = 200;
			for (int celda : copiaCeldas) {
				int d = distanciaEntreDosCeldas(pelea.getMapaCopia(), luchador.getCeldaPelea().getID(), celda);
				if (dist > d) {
					dist = d;
					tempCelda = celda;
					tempIndex = copiaCeldas.indexOf(celda);
				}
			}
			celdasPelea.add(tempCelda);
			copiaCeldas.remove(tempIndex);
		}
		return celdasPelea;
	}
	
	public static boolean esBorde1(int id) {
		int[] bords = { 1, 30, 59, 88, 117, 146, 175, 204, 233, 262, 291, 320, 349, 378, 407, 436, 465, 15, 44, 73, 102, 131,
				160, 189, 218, 247, 276, 305, 334, 363, 392, 421, 450, 479 };
		ArrayList<Integer> test = new ArrayList<Integer>();
		for (int i : bords) {
			test.add(i);
		}
		if (test.contains(id))
			return true;
		else
			return false;
	}
	
	public static boolean esBorde2(int id) {
		int[] bords = { 16, 45, 74, 103, 132, 161, 190, 219, 248, 277, 306, 335, 364, 393, 422, 451, 29, 58, 87, 116, 145, 174,
				203, 232, 261, 290, 319, 348, 377, 406, 435, 464 };
		ArrayList<Integer> test = new ArrayList<Integer>();
		for (int i : bords) {
			test.add(i);
		}
		if (test.contains(id))
			return true;
		else
			return false;
	}
	
	public static ArrayList<Integer> getLineaDeVista(int celda1, int celda2, Mapa mapa) {
		ArrayList<Integer> lineasDeVista = new ArrayList<Integer>();
		int celda = celda1;
		boolean siguiente = false;
		int ancho = mapa.getAncho();
		int alto = mapa.getAlto();
		int ultCelda = mapa.ultimaCeldaID();
		int[] dir1 = { 1, -1, (ancho + alto), - (ancho + alto), ancho, (ancho - 1), - (ancho), - (ancho - 1) };
		for (int i : dir1) {
			lineasDeVista.clear();
			celda = celda1;
			lineasDeVista.add(celda);
			siguiente = false;
			while (!siguiente) {
				celda += i;
				lineasDeVista.add(celda);
				if (esBorde2(celda) || esBorde1(celda) || celda <= 0 || celda >= ultCelda)
					siguiente = true;
				if (celda == celda2) {
					return lineasDeVista;
				}
			}
		}
		return null;
	}
	
	public static int celdaMovPerco(Mapa mapa, int celda) {
		ArrayList<Integer> celdasPosibles = new ArrayList<Integer>();
		int ancho = mapa.getAncho();
		int[] dir = { - (ancho), - (ancho - 1), (ancho - 1), ancho };
		for (int i = 0; i < dir.length; i++) {
			if (celda + dir[i] > 14 || celda + dir[i] < 464) {
				if (mapa.getCelda(celda + dir[i]).esCaminable(false)) {
					celdasPosibles.add(celda + dir[i]);
				}
			}
		}
		if (celdasPosibles.size() <= 0)
			return -1;
		int celda_mov = celdasPosibles.get(Formulas.getRandomValor(0, celdasPosibles.size() - 1));
		return celda_mov;
	}
}
