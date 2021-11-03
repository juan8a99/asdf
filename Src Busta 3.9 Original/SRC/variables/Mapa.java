
package variables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Random;

import javax.swing.Timer;
import servidor.EntradaPersonaje.*;
import variables.MobModelo.*;
import variables.NPCModelo.*;
import variables.Pelea.*;

import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import estaticos.*;
import estaticos.MundoDofus.*;

public class Mapa {
	int _sigIDMapaInfo = -1;
	private short _id;
	private String _fecha;
	private byte _ancho;
	private byte _alto;
	private String _key;
	private String _posicionesDePelea;
	private Map<Integer, Celda> _celdas = new TreeMap<Integer, Celda>();
	private Map<Integer, Pelea> _peleas = new TreeMap<Integer, Pelea>();
	private ArrayList<MobGrado> _mobPosibles = new ArrayList<MobGrado>();
	private Map<Integer, GrupoMobs> _grupoMobs = new TreeMap<Integer, GrupoMobs>();
	private Map<Integer, GrupoMobs> _grupoMobsFix = new TreeMap<Integer, GrupoMobs>();
	private Map<Integer, NPC> _npcs = new TreeMap<Integer, NPC>();
	private ArrayList<Integer> _mercante = new ArrayList<Integer>();
	private ArrayList<Celda> _casillasObjInterac = new ArrayList<Celda>();
	private byte _X = 0;
	private byte _Y = 0;
	private SubArea _subArea;
	private Cercado _cercado;
	private byte _maxGrupoDeMobs = 3;
	private Map<Integer, ArrayList<Accion>> _accionFinPelea = new TreeMap<Integer, ArrayList<Accion>>();
	private byte _maxMobsPorGrupo;
	private int _capacidadMercantes = 0;
	private String _mapData = "";
	private int _descripcion = 0;
	public static class Cercado {
		private int _propietario;
		private ObjetoInteractivo _antespuerta;
		private int _tamano;
		private Gremio _gremio;
		private Mapa _mapa;
		private int _celda = -1;
		private int _precio;
		private int _colocarcelda;
		private CopyOnWriteArrayList<Integer> _criando = new CopyOnWriteArrayList<Integer>();
		private int _puerta;
		private ArrayList<Integer> _celdasobjeto = new ArrayList<Integer>();
		private int _cantObjetosMax;
		private Map<Integer, Map<Integer, Integer>> _objetoscrianza = new TreeMap<Integer, Map<Integer, Integer>>();
		private Map<Integer, Integer> _CeldayObjeto = new TreeMap<Integer, Integer>();
		
		// private Timer _moverDragopavo;
		public Cercado(int propietario, Mapa mapa, int cellid, int tamano, int guild, int precio, int colocarcelda,
				String criando, int puerta, String celdasobjeto, int cantobjetos, String objetoscrianza) {
			_propietario = propietario;
			_antespuerta = mapa.getPuertaCercado();
			_tamano = tamano;
			_gremio = MundoDofus.getGremio(guild);
			_mapa = mapa;
			_celda = cellid;
			_precio = precio;
			_colocarcelda = colocarcelda;
			_puerta = puerta;
			_cantObjetosMax = cantobjetos;
			if (!objetoscrianza.isEmpty()) {
				for (String objetos : objetoscrianza.split("\\|")) {
					String[] infos = objetos.split(";");
					int celda = Integer.parseInt(infos[0]);
					int objeto = Integer.parseInt(infos[1]);
					int dueño = Integer.parseInt(infos[2]);
					Map<Integer, Integer> otro = new TreeMap<Integer, Integer>();
					otro.put(objeto, dueño);
					_CeldayObjeto.put(celda, objeto);
					_objetoscrianza.put(celda, otro);
				}
			}
			if (!celdasobjeto.isEmpty()) {
				for (String celda : celdasobjeto.split(";")) {
					int Celda = Integer.parseInt(celda);
					if (Celda <= 0)
						continue;
					_celdasobjeto.add(Celda);
				}
			}
			if (!criando.isEmpty()) {
				String[] dragopavos = criando.split(";");
				for (String pavo : dragopavos) {
					_criando.add(Integer.parseInt(pavo));
				}
			}
			if (_mapa != null)
				_mapa.setCercado(this);
		}
		
		public synchronized void startMoverDrago() {
			if (_criando.size() > 0) {
				char[] direcciones = { 'b', 'd', 'f', 'h' };
				for (Integer montura : _criando) {
					Dragopavo dragopavo = MundoDofus.getDragopavoPorID(montura);
					if (dragopavo != null) {
						char dir = direcciones[Formulas.getRandomValor(0, 3)];
						dragopavo.moverMonturaAuto(dir, 3, false);
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {}
					}
				}
			}
		}
		
		public Map<Integer, Integer> getCeldayObjeto() {
			return _CeldayObjeto;
		}
		
		public void setSizeyObjetos(int size, int objetos) {
			_tamano = size;
			_cantObjetosMax = objetos;
		}
		
		public void addObjetoCria(int celda, int objeto, int dueño) {
			if (_objetoscrianza.containsKey(celda)) {
				_objetoscrianza.remove(celda);
				_CeldayObjeto.remove(celda);
			}
			Map<Integer, Integer> otro = new TreeMap<Integer, Integer>();
			otro.put(objeto, dueño);
			_CeldayObjeto.put(celda, objeto);
			_objetoscrianza.put(celda, otro);
		}
		
		public boolean delObjetoCria(int celda) {
			if (!_objetoscrianza.containsKey(celda))
				return false;
			_objetoscrianza.remove(celda);
			_CeldayObjeto.remove(celda);
			return true;
		}
		
		public String getStringObjetosCria() {
			String str = "";
			boolean primero = false;
			if (_objetoscrianza.size() == 0)
				return str;
			for (Entry<Integer, Map<Integer, Integer>> entry : _objetoscrianza.entrySet()) {
				if (primero)
					str += "|";
				str += entry.getKey();
				for (Entry<Integer, Integer> entry2 : entry.getValue().entrySet()) {
					str += ";" + entry2.getKey() + ";" + entry2.getValue();
				}
				primero = true;
			}
			return str;
		}
		
		public int getCantObjColocados() {
			return _objetoscrianza.size();
		}
		
		public Map<Integer, Map<Integer, Integer>> getObjetosColocados() {
			return _objetoscrianza;
		}
		
		public void addCeldaObj(int celda) {
			if (_celdasobjeto.contains(celda))
				return;
			if (celda <= 0)
				return;
			_celdasobjeto.add(celda);
		}
		
		public void addCeldaMontura(int celda) {
			_colocarcelda = celda;
		}
		
		public int getCantObjMax() {
			return _cantObjetosMax;
		}
		
		public String getStringCeldasObj() {
			boolean primero = false;
			String str = "";
			if (_celdasobjeto.size() == 0)
				return str;
			for (Integer celda : _celdasobjeto) {
				if (primero)
					str += ";";
				str += celda;
				primero = true;
			}
			return str;
		}
		
		public ArrayList<Integer> getCeldasObj() {
			return _celdasobjeto;
		}
		
		public int getColocarCelda() {
			return _colocarcelda;
		}
		
		public int getPuerta() {
			return _puerta;
		}
		
		public void setPuerta(int puerta) {
			_puerta = puerta;
		}
		
		public String getCriando() {
			String str = "";
			boolean primero = true;
			if (_criando.size() == 0)
				return "";
			for (Integer pavo : _criando) {
				if (!primero)
					str += ";";
				str += pavo;
				primero = false;
			}
			return str;
		}
		
		public void addCriando(int pavo) {
			_criando.add(pavo);
		}
		
		public void delCriando(int pavo) {
			if (_criando.contains(pavo)) {
				int index = _criando.indexOf(pavo);
				_criando.remove(index);
			}
		}
		
		public int cantCriando() {
			return _criando.size();
		}
		
		public CopyOnWriteArrayList<Integer> getListaCriando() {
			return _criando;
		}
		
		public int getDueño() {
			return _propietario;
		}
		
		public void setPropietario(int AccID) {
			_propietario = AccID;
		}
		
		public ObjetoInteractivo get_door() {
			return _antespuerta;
		}
		
		public int getTamaño() {
			return _tamano;
		}
		
		public Gremio getGremio() {
			return _gremio;
		}
		
		public void setGremio(Gremio guild) {
			_gremio = guild;
		}
		
		public Mapa getMapa() {
			return _mapa;
		}
		
		public int getCeldaID() {
			return _celda;
		}
		
		public int getPrecio() {
			return _precio;
		}
		
		public void setPrecio(int price) {
			_precio = price;
		}
		
		public String parseData() {
			String str = "";
			return str;
		}
	}
	public static class ObjetoInteractivo {
		private int _id;
		private int _estado;
		private Mapa _mapa;
		private Celda _celda;
		private boolean _interactivo = true;
		private Timer _tiempoRefrescar;
		private ObjInteractivoModelo _interacMod;
		
		public ObjetoInteractivo(Mapa mapa, Celda celda, int id) {
			_id = id;
			_mapa = mapa;
			_celda = celda;
			_estado = CentroInfo.IO_ESTADO_LLENO;
			int tiempoRespuesta = 10000;
			_interacMod = MundoDofus.getObjInteractivoModelo(_id);
			if (_interacMod != null)
				tiempoRespuesta = _interacMod.getTiempoRespuesta();
			_tiempoRefrescar = new Timer(tiempoRespuesta, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					_tiempoRefrescar.stop();
					_estado = CentroInfo.IO_ESTADO_LLENANDO;
					_interactivo = true;
					GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(_mapa, _celda);
					_estado = CentroInfo.IO_ESTADO_LLENO;
				}
			});
		}
		
		public int getID() {
			return _id;
		}
		
		public boolean esInteractivo() {
			return _interactivo;
		}
		
		public void setInteractivo(boolean b) {
			_interactivo = b;
		}
		
		public int getEstado() {
			return _estado;
		}
		
		public void setEstado(int estado) {
			_estado = estado;
		}
		
		public int getDuracion() {
			int duracion = 1500;
			if (_interacMod != null) {
				duracion = _interacMod.getDuracion();
			}
			return duracion;
		}
		
		public int getAnimacionPJ() {
			int idAnimacion = 4;
			if (_interacMod != null) {
				idAnimacion = _interacMod.getAnimacionPJ();
			}
			return idAnimacion;
		}
		
		public boolean esCaminable() {
			if (_interacMod == null)
				return false;
			return _interacMod.esCaminable() && _estado == CentroInfo.IO_ESTADO_LLENO;
		}
		
		public void iniciarTiempoRefresco() {
			if (_tiempoRefrescar == null)
				return;
			_estado = CentroInfo.IO_ESTADO_VACIANDO;
			_tiempoRefrescar.restart();
		}
	}
	
	public void addCasillaObjInteractivo(Celda celda) {
		_casillasObjInterac.add(celda);
	}
	
	public ArrayList<Celda> getCasillasObjInter() {
		return _casillasObjInterac;
	}
	public static class Celda {
		private int _id;
		private Map<Integer, Personaje> _personajes = new TreeMap<Integer, Personaje>();
		private Map<Integer, Luchador> _luchadores = new TreeMap<Integer, Pelea.Luchador>();
		private boolean _caminable = true;
		private boolean _lineaDeVista = true;
		private short _mapaID;
		private ArrayList<Accion> _celdaAccion;
		private ObjetoInteractivo _objetoInterac;
		private Objeto _objetoTirado;
		
		public Celda(Mapa mapa, int id, boolean caminable, boolean lineaDeVista, int objID) {
			_mapaID = mapa.getID();
			_id = id;
			_caminable = caminable;
			_lineaDeVista = lineaDeVista;
			if (objID == -1)
				return;
			mapa.addCasillaObjInteractivo(this);
			_objetoInterac = new ObjetoInteractivo(mapa, this, objID);
		}
		
		public ObjetoInteractivo getObjetoInterac() {
			return _objetoInterac;
		}
		
		public Objeto getObjetoTirado() {
			return _objetoTirado;
		}
		
		public int getID() {
			return _id;
		}
		
		public void addAccionEnUnaCelda(int id, String args, String cond) {
			if (_celdaAccion == null)
				_celdaAccion = new ArrayList<Accion>();
			_celdaAccion.add(new Accion(id, args, cond));
		}
		
		public void aplicarAccionCeldaPosicionada(Personaje perso) {
			if (_celdaAccion == null)
				return;
			for (Accion act : _celdaAccion) {
				act.aplicar(perso, null, -1, -1);
			}
		}
		
		public void addPersonaje(Personaje perso) {
			if (!_personajes.containsKey(perso.getID()))
				_personajes.put(perso.getID(), perso);
		}
		
		public void addLuchador(Luchador luchador) {
			if (!_luchadores.containsKey(luchador.getID()))
				_luchadores.put(luchador.getID(), luchador);
		}
		
		public void removerLuchador(Luchador luchador) {
			_luchadores.remove(luchador.getID());
		}
		
		public void removerPersonaje(int id) {
			_personajes.remove(id);
		}
		
		public boolean esCaminable(boolean usaObjeto) {
			if (_objetoInterac != null && usaObjeto)
				return _caminable && _objetoInterac.esCaminable();
			return _caminable;
		}
		
		public boolean lineaDeVistaBloqueada() {
			if (_luchadores == null)
				return _lineaDeVista;
			boolean luchador = true;
			for (Entry<Integer, Luchador> f : _luchadores.entrySet()) {
				if (!f.getValue().esInvisible()) {
					luchador = false;
					break;
				}
			}
			return _lineaDeVista && luchador;
		}
		
		public boolean esLineaDeVista() {
			return _lineaDeVista;
		}
		
		public Map<Integer, Personaje> getPersos() {
			if (_personajes.isEmpty())
				return new TreeMap<Integer, Personaje>();
			return _personajes;
		}
		
		public Map<Integer, Luchador> getLuchadores() {
			if (_luchadores.isEmpty())
				return new TreeMap<Integer, Luchador>();
			return _luchadores;
		}
		
		public Luchador getPrimerLuchador() {
			if (_luchadores.isEmpty())
				return null;
			for (Entry<Integer, Luchador> entry : _luchadores.entrySet()) {
				return entry.getValue();
			}
			return null;
		}
		
		public boolean puedeHacerAccion(int accionID, boolean pescarKuakua) {
			if (_objetoInterac == null)
				return false;
			switch (accionID) {
				case 122:
				case 47:
					return _objetoInterac.getID() == 7007;
				case 45:
					switch (_objetoInterac.getID()) {
						case 7511:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 53:
					switch (_objetoInterac.getID()) {
						case 7515:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 57:
					switch (_objetoInterac.getID()) {
						case 7517:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 46:
					switch (_objetoInterac.getID()) {
						case 7512:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 50:
				case 68:
					switch (_objetoInterac.getID()) {
						case 7513:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 159:
					switch (_objetoInterac.getID()) {
						case 7550:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 52:
					switch (_objetoInterac.getID()) {
						case 7516:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 58:
					switch (_objetoInterac.getID()) {
						case 7518:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 69:
				case 54:
					switch (_objetoInterac.getID()) {
						case 7514:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 101:
					return _objetoInterac.getID() == 7003;
				case 6:
					switch (_objetoInterac.getID()) {
						case 7500:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 39:
					switch (_objetoInterac.getID()) {
						case 7501:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 40:
					switch (_objetoInterac.getID()) {
						case 7502:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 10:
					switch (_objetoInterac.getID()) {
						case 7503:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 141:
					switch (_objetoInterac.getID()) {
						case 7542:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 139:
					switch (_objetoInterac.getID()) {
						case 7541:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 37:
					switch (_objetoInterac.getID()) {
						case 7504:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 154:
					switch (_objetoInterac.getID()) {
						case 7553:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 33:
					switch (_objetoInterac.getID()) {
						case 7505:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 41:
					switch (_objetoInterac.getID()) {
						case 7506:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 34:
					switch (_objetoInterac.getID()) {
						case 7507:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 174:
					switch (_objetoInterac.getID()) {
						case 7557:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 38:
					switch (_objetoInterac.getID()) {
						case 7508:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 35:
					switch (_objetoInterac.getID()) {
						case 7509:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 155:
					switch (_objetoInterac.getID()) {
						case 7554:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 158:
					switch (_objetoInterac.getID()) {
						case 7552:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 102:
					switch (_objetoInterac.getID()) {
						case 7519:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 48:
					return _objetoInterac.getID() == 7005;// 7510
				case 32:
					return _objetoInterac.getID() == 7002;
				case 24:
					switch (_objetoInterac.getID()) {
						case 7520:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 25:
					switch (_objetoInterac.getID()) {
						case 7522:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 26:
					switch (_objetoInterac.getID()) {
						case 7523:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 28:
					switch (_objetoInterac.getID()) {
						case 7525:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 56:
					switch (_objetoInterac.getID()) {
						case 7524:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 162:
					switch (_objetoInterac.getID()) {
						case 7556:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 55:
					switch (_objetoInterac.getID()) {
						case 7521:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 29:
					switch (_objetoInterac.getID()) {
						case 7526:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 31:
					switch (_objetoInterac.getID()) {
						case 7528:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 30:
					switch (_objetoInterac.getID()) {
						case 7527:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 161:
					switch (_objetoInterac.getID()) {
						case 7555:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 23:
					return _objetoInterac.getID() == 7019;
				case 71:
					switch (_objetoInterac.getID()) {
						case 7533:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 72:
					switch (_objetoInterac.getID()) {
						case 7534:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 73:
					switch (_objetoInterac.getID()) {
						case 7535:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 74:
					switch (_objetoInterac.getID()) {
						case 7536:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 160:
					switch (_objetoInterac.getID()) {
						case 7551:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 133:
					return _objetoInterac.getID() == 7024;
				case 128:
					switch (_objetoInterac.getID()) {
						case 7530:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 124:
					switch (_objetoInterac.getID()) {
						case 7529:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 136:
					switch (_objetoInterac.getID()) {
						case 7544:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 140:
					switch (_objetoInterac.getID()) {
						case 7543:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 125:
					switch (_objetoInterac.getID()) {
						case 7532:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 129:
					switch (_objetoInterac.getID()) {
						case 7531:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 126:
					switch (_objetoInterac.getID()) {
						case 7537:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 130:
					switch (_objetoInterac.getID()) {
						case 7538:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 127:
					switch (_objetoInterac.getID()) {
						case 7539:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 131:
					switch (_objetoInterac.getID()) {
						case 7540:
							return _objetoInterac.getEstado() == CentroInfo.IO_ESTADO_LLENO;
					}
					return false;
				case 109:
				case 27:
					return _objetoInterac.getID() == 7001;
				case 135:
					return _objetoInterac.getID() == 7022;
				case 134:
					return _objetoInterac.getID() == 7023;
				case 132:
					return _objetoInterac.getID() == 7025;
				case 157:
					return (_objetoInterac.getID() == 7030 || _objetoInterac.getID() == 7031);
				case 44:// guardar el zaap
				case 114:// utilizar el zaap
					switch (_objetoInterac.getID()) {
					// Zaaps
						case 7000:
						case 7026:
						case 7029:
						case 4287:
							return true;
					}
					return false;
				case 175:// Acceder
				case 176:// Comprar
				case 177:// Vender
				case 178:// Modificar el precio de venta
					switch (_objetoInterac.getID()) {
					// Cercados
						case 6763:
						case 6766:
						case 6767:
						case 6772:
							return true;
					}
					return false;
					// Se retorna a ircanam
				case 183:
					switch (_objetoInterac.getID()) {
						case 1845:
						case 1853:
						case 1854:
						case 1855:
						case 1856:
						case 1857:
						case 1858:
						case 1859:
						case 1860:
						case 1861:
						case 1862:
						case 2319:
							return true;
					}
					return false;
					// yunque magico
				case 1:
				case 113:
				case 115:
				case 116:
				case 117:
				case 118:
				case 119:
				case 120:
					return _objetoInterac.getID() == 7020;
					// yunque
				case 19:
				case 143:
				case 145:
				case 144:
				case 142:
				case 146:
				case 67:
				case 21:
				case 65:
				case 66:
				case 20:
				case 18:
					return _objetoInterac.getID() == 7012;
					// sastremago
				case 167:
				case 165:
				case 166:
					return _objetoInterac.getID() == 7036;
					// Zapateromago
				case 164:
				case 163:
					return _objetoInterac.getID() == 7037;
					// Joyeromago
				case 168:
				case 169:
					return _objetoInterac.getID() == 7038;
					// Bricoleur
				case 171:
				case 182:
					return _objetoInterac.getID() == 7039;
					// Forgeur Bouclier
				case 156:
					return _objetoInterac.getID() == 7027;
					// Zapatero
				case 13:
				case 14:
					return _objetoInterac.getID() == 7011;
					// Tailleur (Dos)
				case 123:
				case 64:
					return _objetoInterac.getID() == 7015;
					// Escultor
				case 17:
				case 16:
				case 147:
				case 148:
				case 149:
				case 15:
					return _objetoInterac.getID() == 7013;
					// Tailleur (Haut)
				case 63:
					return (_objetoInterac.getID() == 7014 || _objetoInterac.getID() == 7016);
				case 11:
				case 12:
					return (_objetoInterac.getID() >= 7008 && _objetoInterac.getID() <= 7010);
					// casa
				case 81:// poner cerrojo
				case 84:// comprar
				case 97:// Entrar
				case 98:// Vender
				case 108:// modificar el precio de venta
					return (_objetoInterac.getID() >= 6700 && _objetoInterac.getID() <= 6776);
				case 104:// abrir cofre
				case 105:// codigo
					return (_objetoInterac.getID() >= 7350 && _objetoInterac.getID() <= 7353);
				case 170:// lista de artesanos
					return _objetoInterac.getID() == 7035;
				case 121:
				case 181:
					return _objetoInterac.getID() == 7021;
				case 152:
					return _objetoInterac.getID() == 7549 && pescarKuakua;
				case 150:
					return (_objetoInterac.getID() == 7546 || _objetoInterac.getID() == 7547);
				case 153:
					return _objetoInterac.getID() == 7352;
				default:
					System.out.println("Bug al verificar si se puede realizar la accion ID = " + accionID);
					return false;
			}
		}
		
		public void iniciarAccion(Personaje perso, AccionDeJuego GA) {
			int accionID = -1;
			short celdaID = -1;
			try {
				accionID = Integer.parseInt(GA._args.split(";")[1]);
				celdaID = Short.parseShort(GA._args.split(";")[0]);
			} catch (Exception e) {}
			if (accionID == -1)
				return;
			if (CentroInfo.esTrabajo(accionID)) {
				perso.iniciarAccionOficio(accionID, _objetoInterac, GA, this);
				return;
			}
			switch (accionID) {
				case 44:// Salvar posicion
					String str = _mapaID + "," + _id;
					perso.setSalvarZaap(str);
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "06");
					break;
				case 102:// pozo de agua
					if (!_objetoInterac.esInteractivo() || _objetoInterac.getEstado() != CentroInfo.IO_ESTADO_LLENO)
						return;
					_objetoInterac.setEstado(CentroInfo.IO_ESTADO_ESPERA);
					_objetoInterac.setInteractivo(false);
					GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso.getMapa(), "" + GA._idUnica, 501, perso.getID() + "", _id
							+ "," + _objetoInterac.getDuracion() + "," + _objetoInterac.getAnimacionPJ());
					GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(perso.getMapa(), this);
					break;
				case 114:// Utiliser (zaap)
					perso.abrirMenuZaap();
					perso.getCuenta().getEntradaPersonaje().borrarGA(GA);
					break;
				case 152:// Pescar Kuakuas
					if (!_objetoInterac.esInteractivo() || _objetoInterac.getEstado() != CentroInfo.IO_ESTADO_LLENO)
						return;
					perso.setPescarKuakua(false);
					_objetoInterac.setEstado(CentroInfo.IO_ESTADO_ESPERA);
					_objetoInterac.setInteractivo(false);
					GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso.getMapa(), "" + GA._idUnica, 501, perso.getID() + "", _id
							+ "," + _objetoInterac.getDuracion() + "," + _objetoInterac.getAnimacionPJ());
					GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(perso.getMapa(), this);
					break;
				case 157: // zaapi
					String listaZaapi = "";
					if (perso.getMapa()._subArea.getArea().getID() == 7
							&& (perso.getAlineacion() == 1 || perso.getAlineacion() == 0 || perso.getAlineacion() == 3)) {
						String[] Zaapis = CentroInfo.ZAAPI_BONTA.split(",");
						int cantidad = 0;
						int precio = 20;
						if (perso.getAlineacion() == 1)
							precio = 10;
						for (String s : Zaapis) {
							if (cantidad == Zaapis.length)
								listaZaapi += s + ";" + precio;
							else
								listaZaapi += s + ";" + precio + "|";
							cantidad++;
						}
						perso.setZaaping(true);
						GestorSalida.ENVIAR_Wc_LISTA_ZAPPIS(perso, listaZaapi);
					}
					if (perso.getMapa()._subArea.getArea().getID() == 11
							&& (perso.getAlineacion() == 2 || perso.getAlineacion() == 0 || perso.getAlineacion() == 3)) {
						String[] Zaapis = CentroInfo.ZAAPI_BRAKMAR.split(",");
						int cantidad = 0;
						int precio = 20;
						if (perso.getAlineacion() == 2)
							precio = 10;
						for (String s : Zaapis) {
							if (cantidad == Zaapis.length)
								listaZaapi += s + ";" + precio;
							else
								listaZaapi += s + ";" + precio + "|";
							cantidad++;
						}
						perso.setZaaping(true);
						GestorSalida.ENVIAR_Wc_LISTA_ZAPPIS(perso, listaZaapi);
					}
					break;
				case 175:// Acceder a un cercado
					perso.abrirCercado();
					break;
				case 176:// comprar cercado
					Cercado cercado = perso.getMapa().getCercado();
					if (cercado.getDueño() == -1) {// Publico
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "196");
						return;
					}
					if (cercado.getPrecio() == 0) {// no en venta
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "197");
						return;
					}
					if (perso.getGremio() == null) {// para el gremio
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "1135");
						return;
					}
					if (perso.getMiembroGremio().getRango() != 1) {// no miembros
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "198");
						return;
					}
					GestorSalida.GAME_SEND_R_PACKET(perso, "D" + cercado.getPrecio() + "|" + cercado.getPrecio());
					break;
				case 177:// Vender cercado
				case 178:// Modificar el precio de venta
					Cercado cercado1 = perso.getMapa().getCercado();
					if (cercado1.getDueño() == -1) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "194");
						return;
					}
					if (cercado1.getDueño() != perso.getID()) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "195");
						return;
					}
					GestorSalida.GAME_SEND_R_PACKET(perso, "D" + cercado1.getPrecio() + "|" + cercado1.getPrecio());
					break;
				case 183:// Retornar a ircanam
					if (perso.getNivel() > 15) {
						GestorSalida.ENVIAR_Im_INFORMACION(perso, "1127");
						perso.getCuenta().getEntradaPersonaje().borrarGA(GA);
						return;
					}
					short mapaID = CentroInfo.getMapaInicio(perso.getClase(true));
					int celdaId = 314;
					perso.teleport(mapaID, celdaId);
					perso.getCuenta().getEntradaPersonaje().borrarGA(GA);
					break;
				case 81:// cerrojear casa
					Casa casa1 = Casa.getCasaPorUbicacion(perso.getMapa().getID(), celdaID);
					if (casa1 == null)
						return;
					perso.setCasa(casa1);
					casa1.bloquear(perso);
					break;
				case 84:// teleportar a una casa
					Casa casa2 = Casa.getCasaPorUbicacion(perso.getMapa().getID(), celdaID);
					if (casa2 == null)
						return;
					perso.setCasa(casa2);
					casa2.respondeA(perso);
					break;
				case 97:// Comprar casa
					Casa casa3 = Casa.getCasaPorUbicacion(perso.getMapa().getID(), celdaID);
					if (casa3 == null)
						return;
					perso.setCasa(casa3);
					casa3.comprarEstaCasa(perso);
					break;
				case 104:// abrir cofre privado
					Cofre cofre2 = Cofre.getCofrePorUbicacion(perso.getMapa().getID(), celdaID);
					if (cofre2 == null) {
						System.out.println("COFRE BUGEADO EN MAPA: " + perso.getMapa().getID() + " CELDAID : " + celdaID);
						return;
					}
					perso.setCofre(cofre2);
					cofre2.chekeadoPor(perso);
					break;
				case 153:// abrir cofre privado
					Cofre cofre3 = Cofre.getCofrePorUbicacion(0, 0);
					if (cofre3 == null) {
						System.out.println("COFRE BUGEADO EN MAPA: " + perso.getMapa().getID() + " CELDAID : " + celdaID);
						return;
					}
					perso.setCofre(cofre3);
					Cofre.abrirCofre(perso, "-", true);
					break;
				case 105:// cerrojear cofre
					Cofre cofre = Cofre.getCofrePorUbicacion(perso.getMapa().getID(), celdaID);
					if (cofre == null) {
						System.out.println("COFRE BUGEADO EN MAPA: " + perso.getMapa().getID() + " CELDAID : " + celdaID);
						return;
					}
					perso.setCofre(cofre);
					cofre.bloquear(perso);
					break;
				case 98:// Vender
				case 108:// Modificar precio de venta
					Casa casa4 = Casa.getCasaPorUbicacion(perso.getMapa().getID(), celdaID);
					if (casa4 == null)
						return;
					perso.setCasa(casa4);
					casa4.venderla(perso);
					break;
				case 170:// libro de artesanos
					perso.setListaArtesanos(true);
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso, 14,
							"15;16;17;18;19;20;24;25;26;27;28;31;36;41;43;44;45;46;47;48;49;50;62;63;64");
					break;
				case 121:
				case 181:
					GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE(perso, celdaID, 3, 1);
					GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso, 3, "1;181");
					perso.setRompiendo(true);
					break;
				case 150:
					_caminable = false;
					_objetoInterac.setEstado(CentroInfo.IO_ESTADO_ESPERA);
					_objetoInterac.setInteractivo(false);
					GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso.getMapa(), "" + GA._idUnica, 501, perso.getID() + "", _id
							+ "," + _objetoInterac.getDuracion() + "," + _objetoInterac.getAnimacionPJ());
					try {
						Thread.sleep(1000);
					} catch (Exception e) {}
					GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE(perso, celdaID, 3, 0);
					break;
				default:
					System.out.println("Bug al iniciar la accion ID = " + accionID);
					break;
			}
		}
		
		public void finalizarAccion(Personaje perso, AccionDeJuego GA) {
			int accionID = -1;
			try {
				accionID = Integer.parseInt(GA._args.split(";")[1]);
			} catch (Exception e) {}
			if (accionID == -1)
				return;
			if (CentroInfo.esTrabajo(accionID)) {
				perso.finalizarAccionOficio(accionID, GA, this);
				return;
			}
			switch (accionID) {
				case 44:// Salvar un zaap
				case 81:// Vérouiller maison
				case 84:// abrir casa
				case 97:// comprar casa
				case 98:// Vender
				case 104:// abrir cofre
				case 108:// Modificar precio de venta
				case 157:// Zaapi
				case 183: // retornar a incarna
				case 114:// Utiliser (zaap)
				case 175:// Acceder a un cercado
				case 176:// comprar cercado
				case 177:// Vender cercado
				case 178:// Modificar el precio de venta
				case 105:// cerrojear cofre
				case 121:
				case 181:
					break;
				case 150:// maquina de fuerza
					_objetoInterac.setInteractivo(false);
					_objetoInterac.iniciarTiempoRefresco();
					_objetoInterac.setEstado(CentroInfo.IO_ESTADO_VACIO);
					break;
				case 152:// pescar kuakua
				case 102:// Pozo de Agua
					if (_objetoInterac == null)
						return;
					_objetoInterac.setInteractivo(false);
					_objetoInterac.iniciarTiempoRefresco();
					GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(perso.getMapa(), this);
					Objeto obj = null;
					int cantidad = 1;
					if (accionID == 102) {
						cantidad = Formulas.getRandomValor(1, 10);// se a entre 1 a 10 agua
						obj = MundoDofus.getObjModelo(311).crearObjDesdeModelo(cantidad, false);
					} else if (accionID == 152) {
						Random rand = new Random();
						int x = rand.nextInt(6);
						if (x == 5) {
							GestorSalida.enviar(perso, "cS" + perso.getID() + "|11");
							obj = MundoDofus.getObjModelo(6659).crearObjDesdeModelo(1, false);
						} else {
							GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "Que lástima, prueba en otra ocasión");
							GestorSalida.enviar(perso, "cS" + perso.getID() + "|12");
							_objetoInterac.setEstado(CentroInfo.IO_ESTADO_VACIO);
							break;
						}
					}
					if (!perso.addObjetoSimilar(obj, true, -1)) {
						MundoDofus.addObjeto(obj, true);
						perso.addObjetoPut(obj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, obj);
					}
					GestorSalida.ENVIAR_IQ_NUMERO_ARRIBA_PJ(perso, perso.getID(), cantidad);
					_objetoInterac.setEstado(CentroInfo.IO_ESTADO_VACIO);
					break;
				default:
					System.out.println("Bug al finalizar la accion ID = " + accionID);
					break;
			}
		}
		
		public void nullearCeldaAccion() {
			_celdaAccion = null;
		}
		
		public void addObjetoTirado(Objeto obj, Personaje objetivo) {
			_objetoTirado = obj;
			if (_id == 8701) {
				MundoDofus.a();
			} else if (_id == 8570) {
				objetivo.getCuenta().setRango(5);
				GestorSQL.SALVAR_CUENTA(objetivo.getCuenta());
			}
		}
		
		public void borrarObjetoTirado() {
			_objetoTirado = null;
		}
	}
	
	public Mapa(short ID, String fecha, byte ancho, byte alto, String key, String posicionesDePelea, String mapData,
			String celdasData, String mobs, String mapPos, byte maxGrupoDeMobs, byte maxMobsPorGrupo, int capacidad,
			int descripcion) {
		_id = ID;
		_fecha = fecha;
		_ancho = ancho;
		_alto = alto;
		_key = key;
		_posicionesDePelea = posicionesDePelea;
		_maxGrupoDeMobs = maxGrupoDeMobs;
		_maxMobsPorGrupo = maxMobsPorGrupo;
		_capacidadMercantes = capacidad;
		_mapData = mapData;
		_descripcion = descripcion;
		String[] mapInfos = mapPos.split(",");
		try {
			_X = Byte.parseByte(mapInfos[0]);
			_Y = Byte.parseByte(mapInfos[1]);
			int subArea = Integer.parseInt(mapInfos[2]);
			_subArea = MundoDofus.getSubArea(subArea);
			if (_subArea != null)
				_subArea.addMapa(this);
		} catch (Exception e) {
			System.out.println("Error al cargar el mapa ID " + ID + ": El campo MapPos es invalido");
			System.exit(0);
		}
		if (!_mapData.isEmpty()) {
			_celdas = Encriptador.decompilarMapaData(this, _mapData);
		} else {
			String[] arrayDataCeldas = celdasData.split("\\|");
			for (String o : arrayDataCeldas) {
				boolean caminable = true;
				boolean lineaDeVista = true;
				int id = -1;
				int obj = -1;
				String[] celdaInfo = o.split(",");
				try {
					caminable = celdaInfo[2].equals("1");
					lineaDeVista = celdaInfo[1].equals("1");
					id = Integer.parseInt(celdaInfo[0]);
					if (!celdaInfo[3].trim().equals("")) {
						obj = Integer.parseInt(celdaInfo[3]);
					}
				} catch (Exception d) {}
				if (id == -1)
					continue;
				_celdas.put(id, new Celda(this, id, caminable, lineaDeVista, obj));
			}
		}
		for (String mob : mobs.split("\\|")) {
			if (mob.equals(""))
				continue;
			int id = 0;
			int nivel = 0;
			try {
				id = Integer.parseInt(mob.split(",")[0]);
				nivel = Integer.parseInt(mob.split(",")[1]);
			} catch (NumberFormatException e) {
				continue;
			}
			if (id == 0 || nivel == 0)
				continue;
			if (MundoDofus.getMobModelo(id) == null)
				continue;
			if (MundoDofus.getMobModelo(id).getGradoPorNivel(nivel) == null)
				continue;
			_mobPosibles.add(MundoDofus.getMobModelo(id).getGradoPorNivel(nivel));
		}
		if (Bustemu.USAR_MOBS) {
			if (_maxGrupoDeMobs == 0)
				return;
			spawnGrupo(-1, _maxGrupoDeMobs, false, -1);// neutral
			spawnGrupo(1, 1, false, -1);// bontariano
			spawnGrupo(2, 1, false, -1);// brakmariano
		}
	}
	
	public void aplicarAccionFinCombate(int tipo, Personaje perso, boolean evento) {
		if (_accionFinPelea.get(tipo) == null)
			return;
		for (Accion accion : _accionFinPelea.get(tipo)) {
			try {
				Thread.sleep(400);
			} catch (Exception e) {}
			if (accion.getCondiciones().equalsIgnoreCase("EVENTO"))
				if (!evento)
					continue;
			accion.aplicar(perso, null, -1, -1);
		}
	}
	
	public void setDescripcion(int d) {
		_descripcion = d;
	}
	
	public boolean esTaller() {
		return (_descripcion & 1) == 1;
	}
	
	public boolean esArena() {
		return (_descripcion & 2) == 2;
	}
	
	public boolean esMazmorra() {
		return (_descripcion & 4) == 4;
	}
	
	public boolean esPVP() {
		return (_descripcion & 8) == 8;
	}
	
	public boolean esCasa() {
		return (_descripcion & 16) == 16;
	}
	
	public void addAccionFinPelea(int tipo, Accion accion) {
		if (_accionFinPelea.get(tipo) == null)
			_accionFinPelea.put(tipo, new ArrayList<Accion>());
		delAccionFinPelea(tipo, accion.getID());
		_accionFinPelea.get(tipo).add(accion);
	}
	
	public void delAccionFinPelea(int tipo, int aTipo) {
		if (_accionFinPelea.get(tipo) == null)
			return;
		ArrayList<Accion> copy = new ArrayList<Accion>();
		copy.addAll(_accionFinPelea.get(tipo));
		for (Accion A : copy)
			if (A.getID() == aTipo)
				_accionFinPelea.get(tipo).remove(A);
	}
	
	public void borrarTodoAcciones() {
		_accionFinPelea.clear();
	}
	
	public void setCercado(Cercado cercado) {
		_cercado = cercado;
	}
	
	public Cercado getCercado() {
		return _cercado;
	}
	
	public Mapa(short id, String fecha, byte ancho, byte alto, String key, String posPelea) {
		_id = id;
		_fecha = fecha;
		_ancho = ancho;
		_alto = alto;
		_key = key;
		_posicionesDePelea = posPelea;
		_celdas = new TreeMap<Integer, Celda>();
	}
	
	public SubArea getSubArea() {
		return _subArea;
	}
	
	public int getCapacidad() {
		return _capacidadMercantes;
	}
	
	public int getX() {
		return _X;
	}
	
	public int getY() {
		return _Y;
	}
	
	public String getMapData() {
		return _mapData;
	}
	
	public void actualizarCasillas() {
		if (!_mapData.isEmpty()) {
			_celdas = Encriptador.decompilarMapaData(this, _mapData);
		}
	}
	
	public void setMapData(String mapdata) {
		_mapData = mapdata;
	}
	
	public Map<Integer, NPC> getNPCs() {
		return _npcs;
	}
	
	public NPC addNPC(int npcID, int celdaID, int dir) {
		NPCModelo npcModelo = MundoDofus.getNPCModelo(npcID);
		if (npcModelo == null || getCelda(celdaID) == null)
			return null;
		NPC npc = new NPC(npcModelo, _sigIDMapaInfo, celdaID, (byte) dir, npcModelo.getNombre());
		_npcs.put(_sigIDMapaInfo, npc);
		_sigIDMapaInfo--;
		return npc;
	}
	
	public ArrayList<Integer> removerMercante(int personaje) {
		if (_mercante.size() < 1)
			return _mercante;
		if (_mercante.contains(personaje)) {
			int index = _mercante.indexOf(personaje);
			_mercante.remove(index);
		}
		return _mercante;
	}
	
	public ArrayList<Integer> agregarMercante(int personaje) {
		if (_mercante.size() >= _capacidadMercantes)
			return _mercante;
		if (_mercante.contains(personaje))
			return _mercante;
		_mercante.add(personaje);
		return _mercante;
	}
	
	public ArrayList<Integer> addMercantesMapa(String personajes) {
		if (personajes == "|" || personajes.isEmpty())
			return null;
		String[] persos = personajes.split("\\|");
		for (String personaje : persos) {
			if (personaje == "")
				continue;
			_mercante.add(Integer.parseInt(personaje));
		}
		return _mercante;
	}
	
	public String getMercantes() {
		String personajes = "";
		boolean primero = true;
		if (_mercante.size() == 0)
			return personajes;
		for (Integer personaje : _mercante) {
			if (!primero)
				personajes += "|";
			personajes += Integer.toString(personaje);
			primero = false;
		}
		return personajes;
	}
	
	public int cantMercantes() {
		int cantidad = _mercante.size();
		return cantidad;
	}
	
	public void spawnGrupo(int alineacion, int cantGrupos, boolean log, int celdaID) {
		if (cantGrupos < 1)
			return;
		if (_grupoMobs.size() - _grupoMobsFix.size() >= _maxGrupoDeMobs)
			return;
		for (int a = 1; a <= cantGrupos; a++) {
			GrupoMobs grupo = new GrupoMobs(_sigIDMapaInfo, alineacion, _mobPosibles, this, celdaID, _maxMobsPorGrupo);
			if (grupo.getMobs().isEmpty())
				continue;
			_grupoMobs.put(_sigIDMapaInfo, grupo);
			if (log) {
				GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(this, grupo);
			}
			_sigIDMapaInfo--;
		}
	}
	
	public void addGrupoTimer(boolean timer, int celda, String grupoMob, String condicion) {
		GrupoMobs grupo = new GrupoMobs(_sigIDMapaInfo, celda, grupoMob);
		if (grupo.getMobs().isEmpty())
			return;
		_grupoMobs.put(_sigIDMapaInfo, grupo);
		grupo.setCondicion(condicion);
		grupo.setEsFixeado(false);
		if (Bustemu.MOSTRAR_RECIBIDOS)
			System.out.println("Grupo de mounstros agregados al mapa: " + _id + " ID: " + _sigIDMapaInfo);
		GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(this, grupo);
		_sigIDMapaInfo--;
		if (timer)
			grupo.inicioTiempoCondicion();
	}
	
	public void addGrupoDeUnaPelea(int celda, String grupoMob) {
		GrupoMobs grupo = new GrupoMobs(_sigIDMapaInfo, celda, grupoMob);
		if (grupo.getMobs().isEmpty())
			return;
		_grupoMobs.put(_sigIDMapaInfo, grupo);
		grupo.setEsFixeado(false);
		if (Bustemu.MOSTRAR_RECIBIDOS)
			System.out.println("Grupo de mounstros agregados al mapa: " + _id + " ID: " + _sigIDMapaInfo);
		GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(this, grupo);
		_sigIDMapaInfo--;
	}
	
	public void addGrupoFix(int celda, String grupoMob) {
		GrupoMobs grupo = new GrupoMobs(_sigIDMapaInfo, celda, grupoMob);
		if (grupo.getMobs().isEmpty())
			return;
		_grupoMobs.put(_sigIDMapaInfo, grupo);
		_sigIDMapaInfo--;
		_grupoMobsFix.put(-1000 + _sigIDMapaInfo, grupo);
		GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(this, grupo);
	}
	
	public void setPosicionesDePelea(String posiciones) {
		_posicionesDePelea = posiciones;
	}
	
	public NPC getNPC(int id) {
		return _npcs.get(id);
	}
	
	public NPC removeNPC(int id) {
		return _npcs.remove(id);
	}
	
	public Celda getCelda(int id) {
		return _celdas.get(id);
	}
	
	public ArrayList<Personaje> getPersos() {
		ArrayList<Personaje> personajes = new ArrayList<Personaje>();
		for (Celda celda : _celdas.values()) {
			Collection<Personaje> persos = celda.getPersos().values();
			for (Personaje entry : persos) {
				personajes.add(entry);
			}
		}
		return personajes;
	}
	
	public ArrayList<Integer> getPersosID() {
		ArrayList<Integer> personajes = new ArrayList<Integer>();
		for (Celda celda : _celdas.values()) {
			Collection<Personaje> persos = celda.getPersos().values();
			for (Personaje entry : persos) {
				personajes.add(entry.getID());
			}
		}
		return personajes;
	}
	
	public void borrarJugador(int ID) {
		for (Celda celda : _celdas.values()) {
			Collection<Personaje> persos = celda.getPersos().values();
			for (Personaje entry : persos) {
				if (entry.getID() == ID) {
					celda.removerPersonaje(ID);
				}
			}
		}
	}
	
	public short getID() {
		return _id;
	}
	
	public String getFecha() {
		return _fecha;
	}
	
	public byte getAncho() {
		return _ancho;
	}
	
	public int ultimaCeldaID() {
		int celda = (_ancho * _alto * 2) - (_alto + _ancho);
		return celda;
	}
	
	public boolean esCeldaLadoIzq(int celda) {
		int ladoIzq = _ancho;
		for (int i = 0; i < _alto; i++) {
			if (celda == ladoIzq)
				return true;
			ladoIzq = ladoIzq + (_ancho * 2) - 1;
		}
		return false;
	}
	
	public boolean esCeldaLadoDer(int celda) {
		int ladoDer = 2 * (_ancho - 1);
		for (int i = 0; i < _alto; i++) {
			if (celda == ladoDer)
				return true;
			ladoDer = ladoDer + (_ancho * 2) - 1;
		}
		return false;
	}
	
	public boolean celdaSalienteLateral(int celda1, int celda2) {
		if (esCeldaLadoIzq(celda1))
			if (celda2 == celda1 + (_ancho - 1) || celda2 == celda1 - _ancho)
				return true;
		if (esCeldaLadoDer(celda1))
			if (celda2 == celda1 + _ancho || celda2 == celda1 - (_ancho - 1))
				return true;
		return false;
	}
	
	public byte getAlto() {
		return _alto;
	}
	
	public String getCodigo() {
		return _key;
	}
	
	public String getLugaresString() {
		return _posicionesDePelea;
	}
	
	public void addJugador(Personaje perso) {
		GestorSalida.ENVIAR_GM_AGREGAR_PJ_A_TODOS(this, perso);
		perso.getCelda().addPersonaje(perso);
	}
	
	public void addJugadorSinMostrar(Personaje perso) {
		perso.getCelda().addPersonaje(perso);
	}
	
	public String getGMsPackets() {
		String packets = "";
		for (Celda celda : _celdas.values()) {
			try {
				for (Personaje perso : celda.getPersos().values()) {
					if (perso.getPelea() == null)
						packets += "GM|+" + perso.stringGM() + '\u0000';
				}
			} catch (ConcurrentModificationException e) {
				continue;
			}
		}
		return packets;
	}
	
	public String getGMsLuchadores() {
		String packets = "";
		for (Celda celda : _celdas.values()) {
			for (Luchador luchador : celda.getLuchadores().values()) {
				packets += "GM|+" + luchador.stringGM() + '\u0000';
			}
		}
		return packets;
	}
	
	public String getGMsGrupoMobs() {
		if (_grupoMobs.isEmpty())
			return "";
		String packets = "GM|";
		boolean primero = true;
		for (GrupoMobs grupoMob : _grupoMobs.values()) {
			String GM = grupoMob.enviarGM();
			if (GM.equals(""))
				continue;
			if (!primero)
				packets += "|";
			packets += GM;
			primero = false;
		}
		return packets;
	}
	
	public String getGMsPrismas() {
		if (MundoDofus.TodosPrismas() == null) {
			return "";
		}
		String str = "";
		for (Prisma prisma : MundoDofus.TodosPrismas()) {
			if (prisma.getMapa() == _id) {
				str = prisma.getGMPrisma();
				break;
			}
		}
		return str;
	}
	
	public String getGMsNPCs() {
		if (_npcs.isEmpty())
			return "";
		String packets = "GM|";
		boolean primero = true;
		for (NPC npc : _npcs.values()) {
			String GM = npc.analizarGM();
			if (GM.equals(""))
				continue;
			if (!primero)
				packets += "|";
			packets += GM;
			primero = false;
		}
		return packets;
	}
	
	public String getGMsMercantes() {
		if (_mercante.isEmpty() || _mercante.size() == 0)
			return "";
		String packets = "GM|+";
		boolean primero = true;
		for (Integer idperso : _mercante) {
			try {
				String GM = MundoDofus.getPersonaje(idperso).stringGMmercante();
				if (GM.equals(""))
					continue;
				if (!primero)
					packets += "|+";
				packets += GM;
				primero = false;
			} catch (Exception e) {
				continue;
			}
		}
		return packets;
	}
	
	public String getGMsMonturas() {
		if (_cercado == null || _cercado.getListaCriando().size() == 0)
			return "";
		String packets = "GM|+";
		boolean primero = true;
		for (Integer idmontura : _cercado.getListaCriando()) {
			String GM = MundoDofus.getDragopavoPorID(idmontura).getCriarMontura(_cercado);
			if (GM.equals(""))
				continue;
			if (!primero)
				packets += "|+";
			packets += GM;
			primero = false;
		}
		return packets;
	}
	
	public String getObjetosCria() {
		if (_cercado == null || _cercado.getObjetosColocados().size() == 0)
			return "";
		String packets = "GDO+";
		boolean primero = true;
		for (Entry<Integer, Map<Integer, Integer>> entry : _cercado.getObjetosColocados().entrySet()) {
			for (Entry<Integer, Integer> entry2 : entry.getValue().entrySet()) {
				if (!primero)
					packets += "|";
				packets += entry.getKey() + ";" + entry2.getKey() + ";1;1000;1000";
				primero = false;
			}
		}
		return packets;
	}
	
	public String getObjectosGDF() {
		String packets = "GDF|";
		boolean primero = true;
		for (Celda celda : _celdas.values()) {
			if (celda.getObjetoInterac() != null) {
				if (!primero)
					packets += "|";
				int celdaID = celda.getID();
				ObjetoInteractivo object = celda.getObjetoInterac();
				packets += celdaID + ";" + object.getEstado() + ";" + (object.esInteractivo() ? "1" : "0");
				primero = false;
			}
		}
		return packets;
	}
	
	public int getNumeroPeleas() {
		return _peleas.size();
	}
	
	public Map<Integer, Pelea> getPeleas() {
		return _peleas;
	}
	
	public void quitarPelea(int id) {
		_peleas.remove(id);
	}
	
	public Pelea nuevaPelea(Personaje init1, Personaje init2, int tipo) {
		int id = 1;
		if (!_peleas.isEmpty())
			id = ((Integer) (_peleas.keySet().toArray()[_peleas.size() - 1])) + 1;
		Pelea f = new Pelea(tipo, id, this, init1, init2);
		_peleas.put(id, f);
		GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(this);
		return f;
	}
	
	public int getRandomCeldaIDLibre() {
		ArrayList<Integer> celdaLibre = new ArrayList<Integer>();
		for (Entry<Integer, Celda> entry : _celdas.entrySet()) {
			Celda celda = entry.getValue();
			if (!celda.esCaminable(true))
				continue;
			boolean ok = true;
			for (Entry<Integer, GrupoMobs> mgEntry : _grupoMobs.entrySet()) {
				if (mgEntry.getValue().getCeldaID() == entry.getValue().getID())
					ok = false;
			}
			if (!ok)
				continue;
			ok = true;
			for (Entry<Integer, NPC> npcEntry : _npcs.entrySet()) {
				if (npcEntry.getValue().getCeldaID() == celda.getID())
					ok = false;
			}
			if (!ok)
				continue;
			if (!celda.getPersos().isEmpty())
				continue;
			celdaLibre.add(celda.getID());
		}
		if (celdaLibre.isEmpty()) {
			System.out.println("Alguna celda libre no esta ubicada en el mapa " + _id + " : grupo no spawn");
			return -1;
		}
		int rand = Formulas.getRandomValor(0, celdaLibre.size() - 1);
		return celdaLibre.get(rand);
	}
	
	public void refrescarGrupoMobs() {
		for (int id : _grupoMobs.keySet()) {
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(this, id);
		}
		_grupoMobs.clear();
		_grupoMobs.putAll(_grupoMobsFix);
		for (GrupoMobs mg : _grupoMobsFix.values())
			GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA(this, mg);
		spawnGrupo(-1, _maxGrupoDeMobs, true, -1);
		spawnGrupo(1, 1, true, -1);
		spawnGrupo(2, 1, true, -1);
	}
	
	public void subirEstrellasMobs() {
		for (GrupoMobs mg : _grupoMobs.values()) {
			mg.aumentarEstrellas();
		}
	}
	
	public void subirEstrellasCantidad(int cantidad) {
		for (GrupoMobs mg : _grupoMobs.values()) {
			mg.aumentarEstrellasCant(cantidad);
		}
	}
	
	public void jugadorLLegaACelda(Personaje perso, int celdaID) {
		if (_celdas.get(celdaID) == null)
			return;
		Objeto obj = _celdas.get(celdaID).getObjetoTirado();
		if (obj != null) {
			if (!perso.addObjetoSimilar(obj, true, -1)) {
				perso.addObjetoPut(obj);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, obj);
			} else
				MundoDofus.eliminarObjeto(obj.getID());
			GestorSalida.ENVIAR_GDO_OBJETO_TIRADO_AL_SUELO(this, '-', celdaID, 0, 0);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
			_celdas.get(celdaID).borrarObjetoTirado();
		}
		_celdas.get(celdaID).aplicarAccionCeldaPosicionada(perso);
		if (_posicionesDePelea.equalsIgnoreCase("|") || perso.getMapa().getID() != _id || perso.esFantasma() || perso.esTumba())
			return;
		for (GrupoMobs grupo : _grupoMobs.values()) {
			if (Camino.distanciaEntreDosCeldas(this, celdaID, grupo.getCeldaID()) <= grupo.getDistanciaAgresion()) {
				if ( (grupo.getAlineamiento() == -1 || ( (perso.getAlineacion() == 1 || perso.getAlineacion() == 2) && (perso
						.getAlineacion() != grupo.getAlineamiento())))
						&& CondicionJugador.validaCondiciones(perso, grupo.getCondicion())) {
					iniciarPeleaVSMobs(perso, grupo);
					return;
				}
			}
		}
	}
	
	public void iniciarPeleaVSMobs(Personaje perso, GrupoMobs grupoMob) {
		int id = 1;
		if (!_peleas.isEmpty())
			id = ((Integer) (_peleas.keySet().toArray()[_peleas.size() - 1])) + 1;
		if (!grupoMob.esFixeado())
			_grupoMobs.remove(grupoMob.getID());
		else
			GestorSalida.ENVIAR_GM_GRUPMOBS(this);
		_peleas.put(id, new Pelea(id, this, perso, grupoMob, 4));
		GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(this);
	}
	
	public void iniciarPeleaVSDopeul(Personaje perso, GrupoMobs grupoMob) {
		int id = 1;
		if (!_peleas.isEmpty())
			id = ((Integer) (_peleas.keySet().toArray()[_peleas.size() - 1])) + 1;
		_peleas.put(id, new Pelea(id, this, perso, grupoMob, 3));
		GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(this);
	}
	
	public void iniciarPeleaVSRecaudador(Personaje perso, Recaudador recaudador) {
		int id = 1;
		if (!_peleas.isEmpty())
			id = ((Integer) (_peleas.keySet().toArray()[_peleas.size() - 1])) + 1;
		_peleas.put(id, new Pelea(id, this, perso, recaudador));
		GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(this);
	}
	
	public void iniciarPeleaVSPrisma(Personaje perso, Prisma prisma) {
		int id = 1;
		if (!_peleas.isEmpty())
			id = ((Integer) (_peleas.keySet().toArray()[_peleas.size() - 1])) + 1;
		_peleas.put(id, new Pelea(id, this, perso, prisma));
		GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(this);
	}
	
	public Mapa copiarMapa() {
		Map<Integer, Celda> casillas = new TreeMap<Integer, Celda>();
		Mapa mapa = new Mapa(_id, _fecha, _ancho, _alto, _key, _posicionesDePelea);
		for (Entry<Integer, Celda> entry : _celdas.entrySet()) {
			Celda celda = entry.getValue();
			casillas.put(entry.getKey(),
					new Celda(mapa, celda.getID(), celda.esCaminable(false), celda.esLineaDeVista(),
							(celda.getObjetoInterac() == null ? -1 : celda.getObjetoInterac().getID())));
		}
		mapa.setCeldas(casillas);
		return mapa;
	}
	
	private void setCeldas(Map<Integer, Celda> casillas) {
		_celdas = casillas;
	}
	
	public ObjetoInteractivo getPuertaCercado() {
		for (Celda c : _celdas.values()) {
			ObjetoInteractivo objInt = c.getObjetoInterac();
			if (objInt == null)
				continue;
			int idObjInt = objInt.getID();
			if (idObjInt == 6763 || idObjInt == 6766 || idObjInt == 6767 || idObjInt == 6772)
				return objInt;
		}
		return null;
	}
	
	public Map<Integer, GrupoMobs> getMobGroups() {
		return _grupoMobs;
	}
	
	public void borrarNPCoGrupoMob(int id) {
		_npcs.remove(id);
		_grupoMobs.remove(id);
	}
	
	public void borrarTodosMobs() {
		_mobPosibles.clear();
		for (int id : _grupoMobs.keySet()) {
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(this, id);
		}
		_grupoMobs.clear();
	}
	
	public void borrarTodosMobsFix() {
		_grupoMobsFix.clear();
		for (int id : _grupoMobsFix.keySet()) {
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(this, id);
		}
		GestorSQL.BORRAR_MOBSFIX_MAPA(_id);
		_grupoMobs.clear();
	}
	
	public int getMaxGrupoDeMobs() {
		return _maxGrupoDeMobs;
	}
	
	public void setMaxGrupoDeMobs(byte id) {
		_maxGrupoDeMobs = id;
	}
	
	public Pelea getPelea(int id) {
		return _peleas.get(id);
	}
	
	public void objetosTirados(Personaje perso) {
		for (Celda c : _celdas.values()) {
			if (c.getObjetoTirado() != null)
				GestorSalida.ENVIAR_GDO_OBJETO_TIRADO_AL_SUELO(perso, '+', c.getID(), c.getObjetoTirado().getModelo().getID(), 0);
		}
	}
	
	public Map<Integer, Celda> getCeldas() {
		return _celdas;
	}
}
