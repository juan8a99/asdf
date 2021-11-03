
package estaticos;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import variables.*;
import variables.Mapa.Cercado;
import variables.Mascota.MascotaModelo;
import variables.PuestoMercadillo.ObjetoMercadillo;
import variables.Personaje.GrupoKoliseo;
import variables.NPCModelo.*;
import variables.Objeto.ObjetoModelo;
import variables.Oficio.AccionTrabajo;
import variables.Oficio.StatsOficio;
import variables.Personaje.Stats;

public class MundoDofus {
	private static Map<Integer, Cuenta> Cuentas = new TreeMap<Integer, Cuenta>();
	private static Map<String, Integer> CuentasPorNombre = new TreeMap<String, Integer>();
	private static Map<Integer, Personaje> Personajes = new TreeMap<Integer, Personaje>();
	private static Map<Short, Mapa> Mapas = new TreeMap<Short, Mapa>();
	private static Map<Integer, Objeto> Objetos = new TreeMap<Integer, Objeto>();
	private static Map<Integer, ExpNivel> Experiencia = new TreeMap<Integer, ExpNivel>();
	private static Map<Integer, Hechizo> Hechizos = new TreeMap<Integer, Hechizo>();
	private static Map<Integer, ObjetoModelo> ObjModelos = new TreeMap<Integer, ObjetoModelo>();
	private static Map<Integer, Objevivo> Objevivos = new TreeMap<Integer, Objevivo>();
	private static Map<Integer, MobModelo> MobModelos = new TreeMap<Integer, MobModelo>();
	private static Map<Integer, NPCModelo> NPCModelos = new TreeMap<Integer, NPCModelo>();
	private static Map<Integer, PreguntaNPC> NPCPreguntas = new TreeMap<Integer, PreguntaNPC>();
	private static Map<Integer, RespuestaNPC> NPCRespuesta = new TreeMap<Integer, RespuestaNPC>();
	private static Map<Integer, ObjInteractivoModelo> ObjInteractivos = new TreeMap<Integer, ObjInteractivoModelo>();
	private static Map<Integer, Dragopavo> Dragopavos = new TreeMap<Integer, Dragopavo>();
	private static Map<Integer, SuperArea> SuperAreas = new TreeMap<Integer, SuperArea>();
	private static Map<Integer, Area> Areas = new TreeMap<Integer, Area>();
	private static Map<Integer, SubArea> SubAreas = new TreeMap<Integer, SubArea>();
	private static Map<Integer, Oficio> Oficios = new TreeMap<Integer, Oficio>();
	private static Map<Integer, ArrayList<Duo<Integer, Integer>>> Recetas = new TreeMap<Integer, ArrayList<Duo<Integer, Integer>>>();
	private static Map<Integer, ItemSet> ItemSets = new TreeMap<Integer, ItemSet>();
	private static Map<Integer, Gremio> Gremios = new TreeMap<Integer, Gremio>();
	private static Map<Integer, Casa> Casas = new TreeMap<Integer, Casa>();
	private static Map<Integer, PuestoMercadillo> PuestosMercadillos = new TreeMap<Integer, PuestoMercadillo>();
	private static Map<Integer, Map<Integer, ArrayList<ObjetoMercadillo>>> ObjMercadillos = new HashMap<Integer, Map<Integer, ArrayList<ObjetoMercadillo>>>();
	private static Map<Integer, Personaje> Esposos = new TreeMap<Integer, Personaje>();
	private static Map<Integer, Animacion> Animaciones = new TreeMap<Integer, Animacion>();
	private static Map<Integer, Reto> Retos = new TreeMap<Integer, Reto>();
	private static Map<Integer, Tienda> Tiendas = new TreeMap<Integer, Tienda>();
	private static Map<Integer, Mascota> Mascotas = new TreeMap<Integer, Mascota>();
	private static Map<Integer, MascotaModelo> MascotasModelos = new TreeMap<Integer, MascotaModelo>();
	private static Map<Short, Cercado> Cercados = new TreeMap<Short, Cercado>();
	private static Map<Integer, Prisma> Prismas = new TreeMap<Integer, Prisma>();
	private static Map<Integer, Cofre> Cofres = new TreeMap<Integer, Cofre>();
	private static Map<Integer, Recaudador> Recaudadores = new TreeMap<Integer, Recaudador>();
	private static Map<Integer, RankingPVP> RankingsPVP = new TreeMap<Integer, RankingPVP>();
	private static CopyOnWriteArrayList<Personaje> Koliseo1 = new CopyOnWriteArrayList<Personaje>();
	private static CopyOnWriteArrayList<Personaje> Koliseo2 = new CopyOnWriteArrayList<Personaje>();
	private static CopyOnWriteArrayList<Personaje> Koliseo3 = new CopyOnWriteArrayList<Personaje>();
	private static CopyOnWriteArrayList<GrupoKoliseo> GrupoKoliseo1 = new CopyOnWriteArrayList<GrupoKoliseo>();
	private static CopyOnWriteArrayList<GrupoKoliseo> GrupoKoliseo2 = new CopyOnWriteArrayList<GrupoKoliseo>();
	private static CopyOnWriteArrayList<GrupoKoliseo> GrupoKoliseo3 = new CopyOnWriteArrayList<GrupoKoliseo>();
	private static Map<Integer, Encarnacion> Encarnaciones = new TreeMap<Integer, Encarnacion>();
	private static Map<Integer, Tutorial> Tutoriales = new TreeMap<Integer, Tutorial>();
	public static String liderRanking = "Ninguno";
	private static int sigIDLineaMerca;
	private static int sigIDObjeto;
	private static int intentarSalvar = 1;
	private static short _estado = 1;
	private static byte _gmAcceso = 0;
	
	public synchronized static int getSigIDObjeto() {
		return ++sigIDObjeto;
	}
	public static class Drop {
		private int _objModeloID;
		private int _prospeccion;
		private int _probabilidad;
		private int _maximo;
		
		public Drop(int obj, int prosp, int probabilidad, int max) {
			_objModeloID = obj;
			_prospeccion = prosp;
			_probabilidad = probabilidad;
			_maximo = max;
		}
		
		public void setDropMax(int max) {
			_maximo = max;
		}
		
		public int getObjetoID() {
			return _objModeloID;
		}
		
		public int getProspReq() {
			return _prospeccion;
		}
		
		public int getProbabilidad() {
			return _probabilidad;
		}
		
		public int getDropMax() {
			return _maximo;
		}
	}
	public static class ItemSet {
		private int _id;
		private ArrayList<ObjetoModelo> _objetosModelos = new ArrayList<ObjetoModelo>();
		private ArrayList<Stats> _bonus = new ArrayList<Stats>();
		
		public ItemSet(int id, String items, String bonuses) {
			_id = id;
			for (String str : items.split(",")) {
				try {
					ObjetoModelo t = MundoDofus.getObjModelo(Integer.parseInt(str.trim()));
					if (t == null)
						continue;
					_objetosModelos.add(t);
				} catch (Exception e) {}
			}
			_bonus.add(new Stats());
			for (String str : bonuses.split(";")) {
				Stats S = new Stats();
				for (String str2 : str.split(",")) {
					try {
						String[] infos = str2.split(":");
						int stat = Integer.parseInt(infos[0]);
						int value = Integer.parseInt(infos[1]);
						S.addUnStat(stat, value);
					} catch (Exception e) {}
				}
				_bonus.add(S);
			}
		}
		
		public int getId() {
			return _id;
		}
		
		public Stats getBonusStatPorNroObj(int numb) {
			if (numb > _bonus.size())
				return new Stats();
			return _bonus.get(numb - 1);
		}
		
		public ArrayList<ObjetoModelo> getObjetosModelos() {
			return _objetosModelos;
		}
	}
	public static class SuperArea {
		private int _id;
		private ArrayList<Area> _areas = new ArrayList<Area>();
		
		public SuperArea(int id) {
			_id = id;
		}
		
		public void addArea(Area area) {
			_areas.add(area);
		}
		
		public int getID() {
			return _id;
		}
	}
	public static class Area {
		private int _id;
		private SuperArea _superArea;
		private String _nombre;
		private ArrayList<SubArea> _subAreas = new ArrayList<SubArea>();
		private int _alineacion;
		public static int _bontas = 0;
		public static int _brakmars = 0;
		private int _prisma = 0;
		
		public Area(int id, int superArea, String nombre, int alineacion, int prisma) {
			_id = id;
			_nombre = nombre;
			_superArea = MundoDofus.getSuperArea(superArea);
			if (_superArea == null) {
				_superArea = new SuperArea(superArea);
				MundoDofus.addSuperArea(_superArea);
			}
			_alineacion = 0;
			_prisma = 0;
			if (MundoDofus.getPrisma(prisma) != null) {
				_alineacion = alineacion;
				_prisma = prisma;
			}
			if (_alineacion == 1)
				_bontas++;
			else if (_alineacion == 2)
				_brakmars++;
		}
		
		public static int subareasBontas() {
			return _bontas;
		}
		
		public static int subareasBrakmars() {
			return _brakmars;
		}
		
		public int getAlineacion() {
			return _alineacion;
		}
		
		public int getPrismaID() {
			return _prisma;
		}
		
		public void setPrismaID(int prisma) {
			_prisma = prisma;
		}
		
		public void setAlineacion(int alineacion) {
			if (_alineacion == 1 && alineacion == -1)
				_bontas--;
			else if (_alineacion == 2 && alineacion == -1)
				_brakmars--;
			else if (_alineacion == -1 && alineacion == 1)
				_bontas++;
			else if (_alineacion == -1 && alineacion == 2)
				_brakmars++;
			_alineacion = alineacion;
		}
		
		public String getNombre() {
			return _nombre;
		}
		
		public int getID() {
			return _id;
		}
		
		public SuperArea getSuperArea() {
			return _superArea;
		}
		
		public void addSubArea(SubArea sa) {
			_subAreas.add(sa);
		}
		
		public ArrayList<SubArea> getSubAreas() {
			return _subAreas;
		}
		
		public ArrayList<Mapa> getMapas() {
			ArrayList<Mapa> mapas = new ArrayList<Mapa>();
			for (SubArea SA : _subAreas)
				mapas.addAll(SA.getMapas());
			return mapas;
		}
	}
	public static class SubArea {
		private int _id;
		private Area _area;
		private int _alineacion;
		private String _nombre;
		private ArrayList<Mapa> _mapas = new ArrayList<Mapa>();
		private boolean _conquistable;
		private int _prisma;
		public static int _bontas = 0;
		public static int _brakmars = 0;
		
		public SubArea(int id, int areaID, int alineacion, String nombre, int conquistable, int prisma) {
			_id = id;
			_nombre = nombre;
			_area = MundoDofus.getArea(areaID);
			_alineacion = 0;
			_conquistable = conquistable == 0;
			_prisma = prisma;
			_prisma = 0;
			if (MundoDofus.getPrisma(prisma) != null) {
				_alineacion = alineacion;
				_prisma = prisma;
			}
			if (_alineacion == 1)
				_bontas++;
			else if (_alineacion == 2)
				_brakmars++;
		}
		
		public String getNombre() {
			return _nombre;
		}
		
		public int getPrismaID() {
			return _prisma;
		}
		
		public void setPrismaID(int prisma) {
			_prisma = prisma;
		}
		
		public boolean getConquistable() {
			return _conquistable;
		}
		
		public int getID() {
			return _id;
		}
		
		public Area getArea() {
			return _area;
		}
		
		public int getAlineacion() {
			return _alineacion;
		}
		
		public void setAlineacion(int alineacion) {
			if (_alineacion == 1 && alineacion == -1)
				_bontas--;
			else if (_alineacion == 2 && alineacion == -1)
				_brakmars--;
			else if (_alineacion == -1 && alineacion == 1)
				_bontas++;
			else if (_alineacion == -1 && alineacion == 2)
				_brakmars++;
			_alineacion = alineacion;
		}
		
		public ArrayList<Mapa> getMapas() {
			return _mapas;
		}
		
		public void addMapa(Mapa mapa) {
			_mapas.add(mapa);
		}
		
		public static int subareasBontas() {
			return _bontas;
		}
		
		public static int subareasBrakmars() {
			return _brakmars;
		}
	}
	public static class Duo<L, R> {
		public L _primero;
		public R _segundo;
		
		public Duo(L s, R i) {
			_primero = s;
			_segundo = i;
		}
	}
	public static class ObjInteractivoModelo {
		private int _id;
		private int _tiempoRespuesta;
		private int _duracion;
		private int _animacionnPJ;
		private boolean _caminable;
		
		public ObjInteractivoModelo(int id, int tiempoRespuesta, int duracion, int spritePJ, boolean caminable) {
			_id = id;
			_tiempoRespuesta = tiempoRespuesta;
			_duracion = duracion;
			_animacionnPJ = spritePJ;
			_caminable = caminable;
		}
		
		public int getID() {
			return _id;
		}
		
		public boolean esCaminable() {
			return _caminable;
		}
		
		public int getTiempoRespuesta() {
			return _tiempoRespuesta;
		}
		
		public int getDuracion() {
			return _duracion;
		}
		
		public int getAnimacionPJ() {
			return _animacionnPJ;
		}
	}
	public static class Trueque {
		private Personaje _perso;
		private PrintWriter _out;
		private ArrayList<Duo<Integer, Integer>> _objetos = new ArrayList<Duo<Integer, Integer>>();
		private boolean _ok;
		private String _objetoPedir = "";
		private String _objetoDar = "";
		private int _objetoConseguir = -1;
		private int _cantObjConseguir = -1;
		private boolean _resucitar = false;
		private int _idMascota = -1;
		
		public Trueque(Personaje perso, String Objetopedir, String Objetodar) {
			_perso = perso;
			_objetoPedir = Objetopedir;
			_objetoDar = Objetodar;
			if (_objetoDar.equalsIgnoreCase("resucitar"))
				_resucitar = true;
			_out = _perso.getCuenta().getEntradaPersonaje().getOut();
		}
		
		synchronized public void botonOK(int id) {
			int i = 0;
			if (_perso.getID() == id)
				i = 1;
			if (i == 1) {
				_ok = !_ok;
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out, _ok, id);
			} else
				return;
			if (_ok)
				aplicar();
		}
		
		synchronized public void cancel() {
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out);
			_perso.setTrueque(null);
		}
		
		synchronized public void aplicar() {
			for (Duo<Integer, Integer> duo : _objetos) {
				int idObj = duo._primero;
				int cant = duo._segundo;
				if (cant == 0)
					continue;
				if (!_perso.tieneObjetoID(idObj)) {
					cant = 0;
					continue;
				}
				Objeto obj = MundoDofus.getObjeto(idObj);
				int nuevaCant = obj.getCantidad() - cant;
				if (_resucitar) {
					if (nuevaCant < 1) {
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, idObj);
						if (obj.getModelo().getTipo() != 90) {
							_perso.borrarObjetoRemove(idObj);
							MundoDofus.eliminarObjeto(idObj);
						} else {
							_idMascota = idObj;
						}
					} else {
						obj.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj);
					}
				} else {
					if (nuevaCant < 1) {
						_perso.borrarObjetoRemove(idObj);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out, idObj);
						MundoDofus.eliminarObjeto(idObj);
					} else {
						obj.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj);
					}
				}
			}
			if (_resucitar) {
				if (_idMascota != -1) {
					Objeto objMasc = MundoDofus.getObjeto(_idMascota);
					objMasc.setCantidad(1);
					objMasc.setPosicion(-1);
					objMasc.setIDModelo(CentroInfo.resucitarMascota(objMasc.getModelo().getID()));
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, objMasc);
				}
			} else {
				if (_objetoConseguir != -1 && _cantObjConseguir != -1) {
					Objeto nuevoObjeto = MundoDofus.getObjModelo(_objetoConseguir).crearObjDesdeModelo(_cantObjConseguir, false);
					if (!_perso.addObjetoSimilar(nuevoObjeto, true, -1)) {
						MundoDofus.addObjeto(nuevoObjeto, true);
						_perso.addObjetoPut(nuevoObjeto);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out, nuevoObjeto);
					}
				}
			}
			_perso.setTrueque(null);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso);
			GestorSalida.ENVIAR_EV_INTERCAMBIO_EFECTUADO(_out, 'a');
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
			_perso.setOcupado(false);
		}
		
		synchronized public void addObjetoTrueque(int idObjeto, int cantObj) {
			_ok = false;
			if (cantObj == 1)
				cantObj = 1;
			String str = idObjeto + "|" + cantObj;
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out, _ok, _perso.getID());
			Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetos, idObjeto);
			if (duo != null) {
				duo._segundo += cantObj;
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "+", idObjeto + "|" + duo._segundo);
				if (_resucitar) {
					if (_idMascota != -1)
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "-", "" + _idMascota);
				} else if (_objetoConseguir != -1)
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "-", "" + 1);
				_objetoConseguir = -1;
				_idMascota = -1;
				String[] pedir = _objetoPedir.split("\\|");
				int cantSolicitadas = 0;
				int j = 0;
				for (Duo<Integer, Integer> acouple : _objetos) {
					ObjetoModelo objModelo = MundoDofus.getObjeto(acouple._primero).getModelo();
					int idModelo = objModelo.getID();
					if (_resucitar) {
						if (objModelo.getTipo() == 90) {
							_idMascota = acouple._primero;
						}
					}
					for (String apedir : pedir) {
						if (idModelo == Integer.parseInt(apedir.split(",")[0])) {
							int cantidades = (acouple._segundo / Integer.parseInt(apedir.split(",")[1]));
							if (cantidades < 1)
								continue;
							if (cantSolicitadas == 0 || cantidades < cantSolicitadas)
								cantSolicitadas = cantidades;
							j++;
							break;
						}
						continue;
					}
				}
				if (cantSolicitadas > 0 && (pedir.length == j)) {
					if (_resucitar) {
						if (_idMascota != -1) {
							Objeto mascota = MundoDofus.getObjeto(_idMascota);
							GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(
									_out,
									'O',
									"+",
									_idMascota + "|1|" + CentroInfo.resucitarMascota(mascota.getModelo().getID()) + "|"
											+ mascota.convertirStatsAString() + ",320#0#0#1");
						}
					} else {
						int idObjModDar = Integer.parseInt(_objetoDar.split(",")[0]);
						int cant = Integer.parseInt(_objetoDar.split(",")[1]);
						int cantFinal = cant * cantSolicitadas;
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "+", 1 + "|" + cantFinal + "|" + idObjModDar
								+ "|" + MundoDofus.getObjModelo(idObjModDar).getStringStatsObj());
						_objetoConseguir = idObjModDar;
						_cantObjConseguir = cantFinal;
					}
				}
				return;
			}
			GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "+", str);
			_objetos.add(new Duo<Integer, Integer>(idObjeto, cantObj));
			_objetoConseguir = -1;
			_idMascota = -1;
			String[] pedir = _objetoPedir.split("\\|");
			int cantSolicitadas = 0;
			int j = 0;
			for (Duo<Integer, Integer> acouple : _objetos) {
				ObjetoModelo objModelo = MundoDofus.getObjeto(acouple._primero).getModelo();
				int idModelo = objModelo.getID();
				if (_resucitar) {
					if (objModelo.getTipo() == 90) {
						_idMascota = acouple._primero;
					}
				}
				for (String apedir : pedir) {
					if (idModelo == Integer.parseInt(apedir.split(",")[0])) {
						int cantidades = (acouple._segundo / Integer.parseInt(apedir.split(",")[1]));
						if (cantidades < 1)
							continue;
						if (cantSolicitadas == 0 || cantidades < cantSolicitadas)
							cantSolicitadas = cantidades;
						j++;
						break;
					}
					continue;
				}
			}
			if (cantSolicitadas > 0 && (pedir.length == j)) {
				if (_resucitar) {
					if (_idMascota != -1) {
						Objeto mascota = MundoDofus.getObjeto(_idMascota);
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(
								_out,
								'O',
								"+",
								_idMascota + "|1|" + CentroInfo.resucitarMascota(mascota.getModelo().getID()) + "|"
										+ mascota.convertirStatsAString() + ",320#0#0#1");
					}
				} else {
					int idObjModDar = Integer.parseInt(_objetoDar.split(",")[0]);
					int cant = Integer.parseInt(_objetoDar.split(",")[1]);
					int cantFinal = cant * cantSolicitadas;
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "+", 1 + "|" + cantFinal + "|" + idObjModDar + "|"
							+ MundoDofus.getObjModelo(idObjModDar).getStringStatsObj());
					_objetoConseguir = idObjModDar;
					_cantObjConseguir = cantFinal;
				}
			}
			return;
		}
		
		synchronized public void quitarObjeto(int idObjeto, int cantObjeto) {
			_ok = false;
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out, _ok, _perso.getID());
			Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetos, idObjeto);
			int nuevaCant = duo._segundo - cantObjeto;
			if (nuevaCant < 1) {
				_objetos.remove(duo);
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "-", "" + idObjeto);
			} else {
				duo._segundo = nuevaCant;
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out, 'O', "+", idObjeto + "|" + nuevaCant);
			}
			if (_resucitar) {
				if (_idMascota != -1) {
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "-", "" + _idMascota);
				}
			} else if (_objetoConseguir != -1)
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "-", "" + 1);
			_objetoConseguir = -1;
			_idMascota = -1;
			String[] pedir = _objetoPedir.split("\\|");
			int cantSolicitadas = 0;
			int j = 0;
			for (Duo<Integer, Integer> acouple : _objetos) {
				ObjetoModelo objModelo = MundoDofus.getObjeto(acouple._primero).getModelo();
				int idModelo = objModelo.getID();
				if (_resucitar) {
					if (objModelo.getTipo() == 90) {
						_idMascota = acouple._primero;
					}
				}
				for (String apedir : pedir) {
					if (idModelo == Integer.parseInt(apedir.split(",")[0])) {
						int cantidades = (acouple._segundo / Integer.parseInt(apedir.split(",")[1]));
						if (cantidades < 1)
							continue;
						if (cantSolicitadas == 0 || cantidades < cantSolicitadas)
							cantSolicitadas = cantidades;
						j++;
						break;
					}
					continue;
				}
			}
			if (cantSolicitadas > 0 && (pedir.length == j)) {
				if (_resucitar) {
					if (_idMascota != -1) {
						Objeto mascota = MundoDofus.getObjeto(_idMascota);
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(
								_out,
								'O',
								"+",
								_idMascota + "|1|" + CentroInfo.resucitarMascota(mascota.getModelo().getID()) + "|"
										+ mascota.convertirStatsAString() + ",320#0#0#1");
					}
				} else {
					int idObjModDar = Integer.parseInt(_objetoDar.split(",")[0]);
					int cant = Integer.parseInt(_objetoDar.split(",")[1]);
					int cantFinal = cant * cantSolicitadas;
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out, 'O', "+", 1 + "|" + cantFinal + "|" + idObjModDar + "|"
							+ MundoDofus.getObjModelo(idObjModDar).getStringStatsObj());
					_objetoConseguir = idObjModDar;
					_cantObjConseguir = cantFinal;
				}
			}
			return;
		}
		
		synchronized private Duo<Integer, Integer> getDuoPorIDObjeto(ArrayList<Duo<Integer, Integer>> objetos, int id) {
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == id)
					return duo;
			}
			return null;
		}
		
		public synchronized int getCantObj(int objetoID, int personajeId) {
			ArrayList<Duo<Integer, Integer>> objetos = null;
			if (_perso.getID() == personajeId)
				objetos = _objetos;
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == objetoID) {
					return duo._segundo;
				}
			}
			return 0;
		}
	}
	public static class Intercambio {
		private Personaje _perso1;
		private Personaje _perso2;
		private PrintWriter _out1;
		private PrintWriter _out2;
		private long _kamas1 = 0;
		private long _kamas2 = 0;
		private ArrayList<Duo<Integer, Integer>> _objetos1 = new ArrayList<Duo<Integer, Integer>>();
		private ArrayList<Duo<Integer, Integer>> _objetos2 = new ArrayList<Duo<Integer, Integer>>();
		private boolean _ok1;
		private boolean _ok2;
		
		public Intercambio(Personaje p1, Personaje p2) {
			_perso1 = p1;
			_perso2 = p2;
			_out1 = _perso1.getCuenta().getEntradaPersonaje().getOut();
			_out2 = _perso2.getCuenta().getEntradaPersonaje().getOut();
		}
		
		synchronized public long getKamas(int id) {
			int i = 0;
			if (_perso1.getID() == id)
				i = 1;
			else if (_perso2.getID() == id)
				i = 2;
			if (i == 1)
				return _kamas1;
			else if (i == 2)
				return _kamas2;
			return 0;
		}
		
		synchronized public void botonOK(int id) {
			int i = 0;
			if (_perso1.getID() == id)
				i = 1;
			else if (_perso2.getID() == id)
				i = 2;
			if (i == 1) {
				_ok1 = !_ok1;
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, id);
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, id);
			} else if (i == 2) {
				_ok2 = !_ok2;
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, id);
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, id);
			} else
				return;
			if (_ok1 && _ok2)
				aplicar();
		}
		
		synchronized public void setKamas(int id, long k) {
			_ok1 = false;
			_ok2 = false;
			int i = 0;
			if (_perso1.getID() == id)
				i = 1;
			else if (_perso2.getID() == id)
				i = 2;
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _perso1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _perso1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _perso2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _perso2.getID());
			if (i == 1) {
				_kamas1 = k;
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'G', "", k + "");
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'G', "", k + "");
			} else if (i == 2) {
				_kamas2 = k;
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'G', "", k + "");
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'G', "", k + "");
			}
		}
		
		synchronized public void cancel() {
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out1);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out2);
			_perso1.setIntercambiandoCon(0);
			_perso2.setIntercambiandoCon(0);
			_perso1.setIntercambio(null);
			_perso2.setIntercambio(null);
			_perso1.setOcupado(false);
			_perso2.setOcupado(false);
		}
		
		synchronized public void aplicar() {
			_perso1.addKamas( (-_kamas1 + _kamas2));
			_perso2.addKamas( (-_kamas2 + _kamas1));
			for (Duo<Integer, Integer> duo : _objetos1) {
				int idObjeto = duo._primero;
				int cant = duo._segundo;
				if (cant == 0)
					continue;
				Objeto obj = MundoDofus.getObjeto(idObjeto);
				if ( (obj.getCantidad() - cant) < 1) {
					_perso1.borrarObjetoRemove(idObjeto);
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out1, idObjeto);
					if (_perso2.addObjetoSimilar(obj, true, -1)) {
						MundoDofus.eliminarObjeto(idObjeto);
					} else {
						_perso2.addObjetoPut(obj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out2, obj);
					}
				} else {
					obj.setCantidad(obj.getCantidad() - cant);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso1, obj);
					Objeto nuevoObj = Objeto.clonarObjeto(obj, cant);
					if (!_perso2.addObjetoSimilar(nuevoObj, true, idObjeto)) {
						MundoDofus.addObjeto(nuevoObj, true);
						_perso2.addObjetoPut(nuevoObj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out2, nuevoObj);
					}
				}
			}
			for (Duo<Integer, Integer> duo : _objetos2) {
				int idObjeto = duo._primero;
				int cant = duo._segundo;
				if (cant == 0)
					continue;
				Objeto obj = MundoDofus.getObjeto(idObjeto);
				if ( (obj.getCantidad() - cant) < 1) {
					_perso2.borrarObjetoRemove(idObjeto);
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out2, idObjeto);
					if (_perso1.addObjetoSimilar(obj, true, -1))
						MundoDofus.eliminarObjeto(idObjeto);
					else {
						_perso1.addObjetoPut(obj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out1, obj);
					}
				} else {
					obj.setCantidad(obj.getCantidad() - cant);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso2, obj);
					Objeto nuevoObj = Objeto.clonarObjeto(obj, cant);
					if (!_perso1.addObjetoSimilar(nuevoObj, true, idObjeto)) {
						MundoDofus.addObjeto(nuevoObj, true);
						_perso1.addObjetoPut(nuevoObj);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out1, nuevoObj);
					}
				}
			}
			_perso1.setIntercambiandoCon(0);
			_perso2.setIntercambiandoCon(0);
			_perso1.setIntercambio(null);
			_perso2.setIntercambio(null);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso1);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_perso2);
			GestorSalida.ENVIAR_EV_INTERCAMBIO_EFECTUADO(_out1, 'a');
			GestorSalida.ENVIAR_EV_INTERCAMBIO_EFECTUADO(_out2, 'a');
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso1);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso2);
			_perso1.setOcupado(false);
			_perso2.setOcupado(false);
		}
		
		synchronized public void addObjeto(Objeto obj, int cant, int idPerso) {
			_ok1 = false;
			_ok2 = false;
			int idObj = obj.getID();
			int i = 0;
			if (_perso1.getID() == idPerso)
				i = 1;
			else
				i = 2;
			if (cant == 1)
				cant = 1;
			String str = idObj + "|" + cant;
			String add = "|" + obj.getModelo().getID() + "|" + obj.convertirStatsAString();
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _perso1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _perso1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _perso2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _perso2.getID());
			if (i == 1) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetos1, idObj);
				if (duo != null) {
					duo._segundo += cant;
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "+", idObj + "|" + duo._segundo);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "+", idObj + "|" + duo._segundo + add);
					return;
				}
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "+", str);
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "+", str + add);
				_objetos1.add(new Duo<Integer, Integer>(idObj, cant));
			} else if (i == 2) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetos2, idObj);
				if (duo != null) {
					duo._segundo += cant;
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "+", idObj + "|" + duo._segundo);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "+", idObj + "|" + duo._segundo + add);
					return;
				}
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "+", str);
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "+", str + add);
				_objetos2.add(new Duo<Integer, Integer>(idObj, cant));
			}
		}
		
		synchronized public void borrarObjeto(Objeto obj, int cant, int idPerso) {
			int i = 0;
			if (_perso1.getID() == idPerso)
				i = 1;
			else
				i = 2;
			_ok1 = false;
			_ok2 = false;
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _perso1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _perso1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _perso2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _perso2.getID());
			int idObj = obj.getID();
			String add = "|" + obj.getModelo().getID() + "|" + obj.convertirStatsAString();
			if (i == 1) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetos1, idObj);
				if (duo != null) {
					int nuevaCantidad = duo._segundo - cant;
					if (nuevaCantidad < 1) {
						_objetos1.remove(duo);
						GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "-", "" + idObj);
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "-", "" + idObj);
					} else {
						duo._segundo = nuevaCantidad;
						GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "+", idObj + "|" + nuevaCantidad);
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "+", idObj + "|" + nuevaCantidad + add);
					}
				}
			} else if (i == 2) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetos2, idObj);
				if (duo != null) {
					int nuevaCantidad = duo._segundo - cant;
					if (nuevaCantidad < 1) {
						_objetos2.remove(duo);
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "-", "" + idObj);
						GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "-", "" + idObj);
					} else {
						duo._segundo = nuevaCantidad;
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "+", idObj + "|" + nuevaCantidad + add);
						GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "+", idObj + "|" + nuevaCantidad);
					}
				}
			}
		}
		
		synchronized private Duo<Integer, Integer> getDuoPorIDObjeto(ArrayList<Duo<Integer, Integer>> objetos, int id) {
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == id)
					return duo;
			}
			return null;
		}
		
		public synchronized int getCantObjeto(int objetoID, int idPerso) {
			ArrayList<Duo<Integer, Integer>> objetos;
			if (_perso1.getID() == idPerso)
				objetos = _objetos1;
			else
				objetos = _objetos2;
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == objetoID) {
					return duo._segundo;
				}
			}
			return 0;
		}
	}
	public static class InvitarTaller {
		private Personaje _artesano1;
		private Personaje _cliente2;
		private PrintWriter _out1;
		private PrintWriter _out2;
		private long _kamasPago = 0;
		private long _kamasSiSeConsigue = 0;
		private ArrayList<Duo<Integer, Integer>> _objArtesano1 = new ArrayList<Duo<Integer, Integer>>();
		private ArrayList<Duo<Integer, Integer>> _objCliente2 = new ArrayList<Duo<Integer, Integer>>();
		private boolean _ok1;
		private boolean _ok2;
		private int _maxIngredientes;
		private ArrayList<Duo<Integer, Integer>> _objetosPago = new ArrayList<Duo<Integer, Integer>>();
		private ArrayList<Duo<Integer, Integer>> _objetosSiSeConsegui = new ArrayList<Duo<Integer, Integer>>();
		
		public InvitarTaller(Personaje p1, Personaje p2, int max) {
			_artesano1 = p1;
			_cliente2 = p2;
			_out1 = _artesano1.getCuenta().getEntradaPersonaje().getOut();
			_out2 = _cliente2.getCuenta().getEntradaPersonaje().getOut();
			_maxIngredientes = max;
		}
		
		public long getKamasSiSeConsigue() {
			return _kamasSiSeConsigue;
		}
		
		public long getKamasPaga() {
			return _kamasPago;
		}
		
		synchronized public void botonOK(int id) {
			int i = 0;
			if (_artesano1.getID() == id)
				i = 1;
			else if (_cliente2.getID() == id)
				i = 2;
			if (i == 1) {
				_ok1 = !_ok1;
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, id);
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, id);
			} else if (i == 2) {
				_ok2 = !_ok2;
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, id);
				GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, id);
			} else
				return;
			if (_ok1 && _ok2)
				aplicar();
		}
		
		public void setKamas(int id, long k, long kamasT) {
			_ok1 = false;
			_ok2 = false;
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _cliente2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _cliente2.getID());
			if (id == 1) {
				long kamasTotal = _kamasSiSeConsigue + k;
				if (kamasTotal > kamasT) {
					k = kamasT - _kamasSiSeConsigue;
				}
				_kamasPago = k;
			} else {
				long kamasTotal = _kamasPago + k;
				if (kamasTotal > kamasT) {
					k = kamasT - _kamasPago;
				}
				_kamasSiSeConsigue = k;
			}
			GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, id, "G", "+", k + "");
			GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, id, "G", "+", k + "");
		}
		
		synchronized public void cancel() {
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out1);
			GestorSalida.ENVIAR_EV_CERRAR_VENTANAS(_out2);
			_artesano1.setIntercambiandoCon(0);
			_cliente2.setIntercambiandoCon(0);
			_artesano1.setTallerInvitado(null);
			_cliente2.setTallerInvitado(null);
			_artesano1.setOcupado(false);
			_cliente2.setOcupado(false);
		}
		
		public void aplicar() {
			AccionTrabajo trabajo = _artesano1.getHaciendoTrabajo();
			boolean resultado = trabajo.iniciarTrabajoPago(_artesano1, _cliente2, _objArtesano1, _objCliente2);
			StatsOficio oficio = _artesano1.getOficioPorTrabajo(trabajo.getIDTrabajo());
			if (oficio != null) {
				if (oficio.esPagable()) {
					if (resultado) {
						_cliente2.setKamas(_cliente2.getKamas() - _kamasSiSeConsigue);
						_artesano1.setKamas(_artesano1.getKamas() + _kamasSiSeConsigue);
						for (Duo<Integer, Integer> duo : _objetosSiSeConsegui) {
							int idObjeto = duo._primero;
							int cant = duo._segundo;
							if (cant == 0)
								continue;
							Objeto obj = MundoDofus.getObjeto(idObjeto);
							if ( (obj.getCantidad() - cant) < 1) {
								_cliente2.borrarObjetoRemove(idObjeto);
								GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out2, idObjeto);
								if (_artesano1.addObjetoSimilar(obj, true, -1))
									MundoDofus.eliminarObjeto(idObjeto);
								else {
									_artesano1.addObjetoPut(obj);
									GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out1, obj);
								}
							} else {
								obj.setCantidad(obj.getCantidad() - cant);
								GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente2, obj);
								Objeto nuevoObj = Objeto.clonarObjeto(obj, cant);
								if (!_artesano1.addObjetoSimilar(nuevoObj, true, idObjeto)) {
									MundoDofus.addObjeto(nuevoObj, true);
									_artesano1.addObjetoPut(nuevoObj);
									GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out1, nuevoObj);
								}
							}
						}
					}
					if (!oficio.esGratisSiFalla() || resultado) {
						_cliente2.setKamas(_cliente2.getKamas() - _kamasPago);
						_artesano1.setKamas(_artesano1.getKamas() + _kamasPago);
						for (Duo<Integer, Integer> duo : _objetosPago) {
							int idObjeto = duo._primero;
							int cant = duo._segundo;
							if (cant == 0)
								continue;
							Objeto obj = MundoDofus.getObjeto(idObjeto);
							if ( (obj.getCantidad() - cant) < 1) {
								_cliente2.borrarObjetoRemove(idObjeto);
								GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_out2, idObjeto);
								if (_artesano1.addObjetoSimilar(obj, true, -1))
									MundoDofus.eliminarObjeto(idObjeto);
								else {
									_artesano1.addObjetoPut(obj);
									GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out1, obj);
								}
							} else {
								obj.setCantidad(obj.getCantidad() - cant);
								GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente2, obj);
								Objeto nuevoObj = Objeto.clonarObjeto(obj, cant);
								if (!_artesano1.addObjetoSimilar(nuevoObj, true, idObjeto)) {
									MundoDofus.addObjeto(nuevoObj, true);
									_artesano1.addObjetoPut(nuevoObj);
									GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_out1, nuevoObj);
								}
							}
						}
					}
				}
			}
			_objetosSiSeConsegui.clear();
			_objetosPago.clear();
			_objArtesano1.clear();
			_objCliente2.clear();
			_kamasPago = 0;
			_kamasSiSeConsigue = 0;
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_artesano1);
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(_cliente2);
			GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano1, _artesano1.getForjaEcK());
			GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_cliente2, _cliente2.getForjaEcK());
		}
		
		synchronized public void addObjeto(Objeto obj, int cant, int idPerso) {
			if (cantObjetosActual() >= _maxIngredientes) {
				return;
			}
			_ok1 = false;
			_ok2 = false;
			int idObj = obj.getID();
			int i = 0;
			if (_artesano1.getID() == idPerso)
				i = 1;
			else
				i = 2;
			if (cant == 1)
				cant = 1;
			String str = idObj + "|" + cant;
			String add = "|" + obj.getModelo().getID() + "|" + obj.convertirStatsAString();
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _cliente2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _cliente2.getID());
			if (i == 1) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objArtesano1, idObj);
				if (duo != null) {
					duo._segundo += cant;
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "+", "" + idObj + "|" + duo._segundo);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "+", "" + idObj + "|" + duo._segundo + add);
					return;
				}
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "+", str);
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "+", str + add);
				_objArtesano1.add(new Duo<Integer, Integer>(idObj, cant));
			} else if (i == 2) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objCliente2, idObj);
				if (duo != null) {
					duo._segundo += cant;
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "+", "" + idObj + "|" + duo._segundo);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "+", "" + idObj + "|" + duo._segundo + add);
					return;
				}
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "+", str);
				GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "+", str + add);
				_objCliente2.add(new Duo<Integer, Integer>(idObj, cant));
			}
		}
		
		synchronized public void borrarObjeto(Objeto obj, int cant, int idPerso) {
			int i = 0;
			if (_artesano1.getID() == idPerso)
				i = 1;
			else
				i = 2;
			_ok1 = false;
			_ok2 = false;
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _cliente2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _cliente2.getID());
			int idObj = obj.getID();
			String add = "|" + obj.getModelo().getID() + "|" + obj.convertirStatsAString();
			if (i == 1) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objArtesano1, idObj);
				int nuevaCantidad = duo._segundo - cant;
				if (nuevaCantidad < 1) {
					_objArtesano1.remove(duo);
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "-", "" + idObj);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "-", "" + idObj);
				} else {
					duo._segundo = nuevaCantidad;
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out1, 'O', "+", "" + idObj + "|" + nuevaCantidad);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out2, 'O', "+", "" + idObj + "|" + nuevaCantidad + add);
				}
			} else if (i == 2) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objCliente2, idObj);
				int nuevaCantidad = duo._segundo - cant;
				if (nuevaCantidad < 1) {
					_objCliente2.remove(duo);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "-", "" + idObj);
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "-", "" + idObj);
				} else {
					duo._segundo = nuevaCantidad;
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_out1, 'O', "+", "" + idObj + "|" + nuevaCantidad + add);
					GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_out2, 'O', "+", "" + idObj + "|" + nuevaCantidad);
				}
			}
		}
		
		synchronized public void addObjetoPaga(Objeto obj, int cant, int idPago) {
			if (cantObjetosActual() >= _maxIngredientes) {
				return;
			}
			_ok1 = false;
			_ok2 = false;
			int idObj = obj.getID();
			if (cant == 1)
				cant = 1;
			String str = idObj + "|" + cant;
			String add = "|" + obj.getModelo().getID() + "|" + obj.convertirStatsAString();
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _cliente2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _cliente2.getID());
			if (idPago == 1) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetosPago, idObj);
				if (duo != null) {
					duo._segundo += cant;
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "+", idObj + "|" + duo._segundo + add);
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "+", idObj + "|" + duo._segundo);
					return;
				}
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "+", str + add);
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "+", str);
				_objetosPago.add(new Duo<Integer, Integer>(idObj, cant));
			} else {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetosSiSeConsegui, idObj);
				if (duo != null) {
					duo._segundo += cant;
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "+", idObj + "|" + duo._segundo + add);
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "+", idObj + "|" + duo._segundo);
					return;
				}
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "+", str + add);
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "+", str);
				_objetosSiSeConsegui.add(new Duo<Integer, Integer>(idObj, cant));
			}
		}
		
		synchronized public void borrarObjetoPaga(Objeto obj, int cant, int idPago) {
			int idObj = obj.getID();
			_ok1 = false;
			_ok2 = false;
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok1, _artesano1.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out1, _ok2, _cliente2.getID());
			GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO(_out2, _ok2, _cliente2.getID());
			String add = "|" + obj.getModelo().getID() + "|" + obj.convertirStatsAString();
			if (idPago == 1) {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetosPago, idObj);
				if (duo == null)
					return;
				int nuevaCantidad = duo._segundo - cant;
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "-", idObj + "");
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "-", idObj + "");
				if (nuevaCantidad < 1) {
					_objetosPago.remove(duo);
				} else {
					duo._segundo = nuevaCantidad;
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "+", idObj + "|" + nuevaCantidad + add);
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "+", idObj + "|" + nuevaCantidad);
				}
			} else {
				Duo<Integer, Integer> duo = getDuoPorIDObjeto(_objetosSiSeConsegui, idObj);
				if (duo == null)
					return;
				int nuevaCantidad = duo._segundo - cant;
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "-", idObj + "");
				GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "-", idObj + "");
				if (nuevaCantidad < 1) {
					_objetosSiSeConsegui.remove(duo);
				} else {
					duo._segundo = nuevaCantidad;
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out1, idPago, "O", "+", idObj + "|" + nuevaCantidad + add);
					GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_out2, idPago, "O", "+", idObj + "|" + nuevaCantidad);
				}
			}
		}
		
		synchronized private Duo<Integer, Integer> getDuoPorIDObjeto(ArrayList<Duo<Integer, Integer>> objetos, int id) {
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == id)
					return duo;
			}
			return null;
		}
		
		public synchronized int getCantObjeto(int idObj, int idPerso) {
			ArrayList<Duo<Integer, Integer>> objetos;
			if (_artesano1.getID() == idPerso)
				objetos = _objArtesano1;
			else
				objetos = _objCliente2;
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == idObj) {
					return duo._segundo;
				}
			}
			return 0;
		}
		
		public synchronized int getCantObjetoPago(int idObj, int idPerso) {
			ArrayList<Duo<Integer, Integer>> objetos;
			if (idPerso == 1)
				objetos = _objetosPago;
			else
				objetos = _objetosSiSeConsegui;
			for (Duo<Integer, Integer> duo : objetos) {
				if (duo._primero == idObj) {
					return duo._segundo;
				}
			}
			return 0;
		}
		
		public int cantObjetosActual() {
			int cant = _objArtesano1.size() + _objCliente2.size();
			return cant;
		}
	}
	public static class ExpNivel {
		public long _personaje;
		public int _oficio;
		public int _montura;
		public int _pvp;
		public long _gremio;
		public long _encarnacion;
		
		public ExpNivel(long perso, int oficio, int montura, int pvp, int encarnacion) {
			_personaje = perso;
			_oficio = oficio;
			_montura = montura;
			_pvp = pvp;
			_gremio = _personaje * 20;
			_encarnacion = encarnacion;
		}
	}
	
	public static void crearServer() {
		System.out.println("=======>Datos Fijos<=======");
		System.out.print("Cargando los niveles de experiencia: ");
		GestorSQL.CARGAR_EXPERIENCIA();
		System.out.println(Experiencia.size() + " niveles cargados");
		System.out.print("Cargando los hechizos: ");
		GestorSQL.CARGAR_HECHIZOS();
		System.out.println(Hechizos.size() + " hechizos cargados");
		System.out.print("Cargando los mounstros: ");
		GestorSQL.CARGAR_MODELOS_MOB();
		System.out.println(MobModelos.size() + " mounstros cargados");
		System.out.print("Cargando los objetos: ");
		GestorSQL.CARGAR_MODELOS_OBJETOS();
		System.out.println(ObjModelos.size() + " objetos modelo cargados");
		System.out.print("Cargando los NPC: ");
		GestorSQL.CARGAR_MODELOS_NPC();
		System.out.println(NPCModelos.size() + " NPC cargados");
		System.out.print("Cargando las preguntas de NPC: ");
		GestorSQL.CARGAR_PREGUNTAS();
		System.out.println(NPCPreguntas.size() + " preguntas de NPC cargadas");
		System.out.print("Cargando las respuestas de NPC: ");
		GestorSQL.CARGAR_RESPUESTAS();
		System.out.println(NPCRespuesta.size() + " respuestas de NPC cargadas");
		System.out.print("Cargando los prismas: ");
		int numero = GestorSQL.CARGAR_PRISMAS();
		System.out.println(numero + " prismas cargados");
		System.out.print("Cargando las zonas: ");
		GestorSQL.CARGAR_AREA();
		System.out.println(Areas.size() + " zonas cargadas");
		System.out.print("Cargando las sub-zonas: ");
		GestorSQL.CARGAR_SUBAREA();
		System.out.println(SubAreas.size() + " sub-zonas cargadas");
		System.out.print("Cargando los objetos interactivos: ");
		GestorSQL.CARGAR_INTERACTIVOS();
		System.out.println(ObjInteractivos.size() + " interactividad de objetos cardadas");
		System.out.print("Cargando las recetas: ");
		GestorSQL.CARGAR_RECETAS();
		System.out.println(Recetas.size() + " recetas cargadas");
		System.out.print("Cargando los oficios: ");
		GestorSQL.CARGAR_OFICIOS();
		System.out.println(Oficios.size() + " oficios cargados");
		System.out.print("Cargando los sets de objetos: ");
		GestorSQL.CARGAR_ITEMSETS();
		System.out.println(ItemSets.size() + " set de objetos cargados");
		System.out.print("Cargando los mapas: ");
		GestorSQL.CARGAR_MAPAS();
		System.out.println(Mapas.size() + " mapas cargados");
		System.out.print("Cargando los mapas fixeados: ");
		numero = GestorSQL.CARGAR_MAPAS_FIXEADOS();
		System.out.println(numero + " mapas fix cargados");
		System.out.print("Cargando los triggers: ");
		numero = GestorSQL.CARGAR_TRIGGERS();
		System.out.println(numero + " trigger cargados");
		System.out.print("Cargando las acciones finales de combate: ");
		numero = GestorSQL.CARGAR_FINALES_DE_COMBATE();
		System.out.println(numero + " acciones finales de combate cargadas");
		System.out.print("Cargando los NPCs: ");
		numero = GestorSQL.CARGAR_NPCS();
		System.out.println(numero + " NPCs cargados");
		System.out.print("Cargando las acciones de objetos: ");
		numero = GestorSQL.CARGAR_ACCIONES_USO_OBJETOS();
		System.out.println(numero + " acciones de objetos cargados");
		System.out.print("Cargando los drops: ");
		GestorSQL.CARGAR_DROPS();
		System.out.println("Todos los drops cargados");
		System.out.print("Cargando las animaciones: ");
		GestorSQL.CARGAR_ANIMACIONES();
		System.out.println(Animaciones.size() + " animaciones cargadas");
		System.out.print("Cargando los objevivos: ");
		GestorSQL.CARGAR_OBJEVIVOS();
		System.out.println(Objevivos.size() + " objevivos cargados");
		System.out.print("Cargando los retos: ");
		GestorSQL.CARGAR_RETOS();
		System.out.println(Retos.size() + " retos cargados");
		System.out.print("Cargando las comidas de mascotas: ");
		numero = GestorSQL.CARGAR_COMIDAS_MASCOTAS();
		System.out.println(numero + " comidas de mascotas cargadas");
		System.out.print("Cargando los tutoriales: ");
		GestorSQL.CARGAR_TUTORIALES();
		System.out.println(Tutoriales.size() + " tutoriales cargados");
		System.out.println("=======>Datos Variables<=======");
		GestorSQL.LOGGED_ZERO();
		System.out.print("Cargando las encarnaciones: ");
		GestorSQL.CARGAR_ENCARNACIONES();
		System.out.println(Encarnaciones.size() + " encarnaciones cargadas");
		System.out.print("Cargando los objetos: ");
		GestorSQL.CARGAR_FULL_OBJETOS();
		System.out.println(Objetos.size() + " objetos cargados");
		System.out.print("Cargando los dragopavos: ");
		GestorSQL.CARGAR_MONTURAS();
		System.out.println(Dragopavos.size() + " dragopavos cargados");
		System.out.print("Cargando las cuentas: ");
		GestorSQL.CARGAR_CUENTAS();
		System.out.println(Cuentas.size() + " cuentas cargadas");
		System.out.print("Cargando los personajes: ");
		GestorSQL.CARGAR_PERSONAJES();
		System.out.println(Personajes.size() + " personajes cargados");
		System.out.print("Cargando los rankings: ");
		GestorSQL.CARGAR_RANKINGPVP();
		System.out.println(RankingsPVP.size() + " rankings cargados cargados");
		if (RankingsPVP.size() > 0) {
			liderRanking = nombreLiderRankingPVP();
		}
		System.out.print("Cargando los mercantes: ");
		numero = GestorSQL.CARGAR_MERCANTES();
		System.out.println(numero + " mercantes cargados");
		System.out.print("Cargando los gremios: ");
		GestorSQL.CARGAR_GREMIOS();
		System.out.println(Gremios.size() + " gremios cargados");
		System.out.print("Cargando los miembros de gremio: ");
		numero = GestorSQL.CARGAR_MIEMBROS_GREMIO();
		System.out.println(numero + " miembros de gremio cargados");
		System.out.print("Cargando los cercados: ");
		numero = GestorSQL.CARGAR_CERCADOS();
		System.out.println(numero + " cercados cargados");
		System.out.print("Cargando los recaudadores: ");
		numero = GestorSQL.CARGAR_RECAUDADORES();
		System.out.println(numero + " recaudadores cargados");
		System.out.print("Cargando las casas: ");
		numero = GestorSQL.CARGAR_CASAS();
		System.out.println(numero + " casas cargadas");
		System.out.print("Cargando los cofres: ");
		GestorSQL.CARGAR_COFRE();
		System.out.println(Cofres.size() + " cofres cargados");
		System.out.print("Cargando los zaaps: ");
		numero = GestorSQL.CARGAR_ZAAPS();
		System.out.println(numero + " zaaps cargados");
		System.out.print("Cargando los ip baneadas: ");
		numero = GestorSQL.CARGAR_BANIP();
		System.out.println(numero + " ip baneadas cargadas");
		System.out.print("Cargando las tiendas: ");
		GestorSQL.CARGAR_OBJETOS_MERCANTES();
		System.out.println(Tiendas.size() + " objetos de tiendas cargados");
		System.out.print("Cargando las mascotas: ");
		GestorSQL.CARGAR_MASCOTAS();
		System.out.println(Mascotas.size() + " mascotas cargadas");
		System.out.print("Cargando los puesto mercadillos: ");
		GestorSQL.CARGAR_PUESTOS_MERCADILLOS();
		System.out.println(PuestosMercadillos.size() + " puestos mercadillos cargados");
		System.out.print("Cargando los objetos mercadillos: ");
		GestorSQL.CARGAR_OBJETOS_MERCADILLOS();
		System.out.println(ObjMercadillos.size() + " objetos mercadillos cargados");
		sigIDObjeto = GestorSQL.getSigIDObjeto();
	}
	
	public static Area getArea(int areaID) {
		return Areas.get(areaID);
	}
	
	public static SuperArea getSuperArea(int areaID) {
		return SuperAreas.get(areaID);
	}
	
	public static SubArea getSubArea(int areaID) {
		return SubAreas.get(areaID);
	}
	
	public static void addArea(Area area) {
		Areas.put(area.getID(), area);
	}
	
	public static void addSuperArea(SuperArea SA) {
		SuperAreas.put(SA.getID(), SA);
	}
	
	public static void addSubArea(SubArea SA) {
		SubAreas.put(SA.getID(), SA);
	}
	
	public static void addNPCreponse(RespuestaNPC respuesta) {
		NPCRespuesta.put(respuesta.getID(), respuesta);
	}
	
	public static RespuestaNPC getNPCreponse(int id) {
		return NPCRespuesta.get(id);
	}
	
	public static void addExpLevel(int nivel, ExpNivel exp) {
		Experiencia.put(nivel, exp);
	}
	
	public static Cuenta getCuenta(int id) {
		return Cuentas.get(id);
	}
	
	public static void addNPCPregunta(PreguntaNPC pregunta) {
		NPCPreguntas.put(pregunta.getID(), pregunta);
	}
	
	public static PreguntaNPC getNPCPregunta(int id) {
		return NPCPreguntas.get(id);
	}
	
	public static NPCModelo getNPCModelo(int id) {
		return NPCModelos.get(id);
	}
	
	public static void addNpcModelo(NPCModelo npcModelo) {
		NPCModelos.put(npcModelo.getID(), npcModelo);
	}
	
	public static Mapa getMapa(short id) {
		return Mapas.get(id);
	}
	
	public static void addMapa(Mapa mapa) {
		if (!Mapas.containsKey(mapa.getID()))
			Mapas.put(mapa.getID(), mapa);
	}
	
	public static void borrarMapa(Mapa mapa) {
		if (Mapas.containsKey(mapa.getID()))
			Mapas.remove(mapa.getID());
	}
	
	public static Mapa mapaPorCoordXYContinente(int mapaX, int mapaY, int idContinente) {
		for (Mapa mapa : Mapas.values()) {
			if (mapa.getX() == mapaX && mapa.getY() == mapaY
					&& mapa.getSubArea().getArea().getSuperArea().getID() == idContinente)
				return mapa;
		}
		return null;
	}
	
	public static String mapaPorCoordenadas(int mapaX, int mapaY) {
		String str = "";
		for (Mapa mapa : Mapas.values()) {
			if (mapa.getX() == mapaX && mapa.getY() == mapaY)
				str += mapa.getID() + ", ";
		}
		return str;
	}
	
	public static void subirEstrellasMobs() {
		for (Mapa mapa : Mapas.values()) {
			mapa.subirEstrellasMobs();
		}
	}
	
	public static void subirEstrellasMobs(int cant) {
		for (Mapa mapa : Mapas.values()) {
			mapa.subirEstrellasCantidad(cant);
		}
	}
	
	public static Cuenta getCuentaPorNombre(String nombre) {
		return (CuentasPorNombre.get(nombre.toLowerCase()) != null ? Cuentas.get(CuentasPorNombre.get(nombre.toLowerCase()))
				: null);
	}
	
	public static Personaje getPersonaje(int id) {
		return Personajes.get(id);
	}
	
	public synchronized static void addCuenta(Cuenta cuenta) {
		Cuentas.put(cuenta.getID(), cuenta);
		CuentasPorNombre.put(cuenta.getNombre().toLowerCase(), cuenta.getID());
	}
	
	public synchronized static void addPersonaje(Personaje perso) {
		Personajes.put(perso.getID(), perso);
	}
	
	public static int getCantidadPersonajes() {
		return Personajes.size();
	}
	
	public synchronized static Personaje getPjPorNombre(String nombre) {
		ArrayList<Personaje> Ps = new ArrayList<Personaje>();
		Ps.addAll(Personajes.values());
		for (Personaje perso : Ps)
			if (perso.getNombre().equalsIgnoreCase(nombre))
				if (perso.enLinea())
					return perso;
		return null;
	}
	
	public synchronized static void eliminarPersonaje(Personaje perso) {
		perso.eliminarPersonaje();
		MundoDofus.desconectarPerso(perso);
		for (Entry<Integer, Casa> entry : Casas.entrySet()) {
			if (entry.getValue().getDueñoID() == perso.getID()) {
				Casa casa = entry.getValue();
				GestorSQL.ACTUALIZAR_CASA(new Casa(casa.getID(), casa.getMapaIDFuera(), casa.getCeldaIDFuera(), 1000000, 0, 0, 0,
						"-", 0, casa.getMapaIDDentro(), casa.getCeldaIDDentro()));
			}
		}
		for (Entry<Short, Cercado> cercados : Cercados.entrySet()) {
			if (cercados.getValue().getDueño() == perso.getID()) {
				Cercado cercado = cercados.getValue();
				GestorSQL.SALVAR_CERCADO(new Cercado(0, cercado.getMapa(), cercado.getCeldaID(), cercado.getTamaño(), -1,
						3000000, cercado.getColocarCelda(), "", cercado.getPuerta(), "", cercado.getCantObjMax(), ""));
			}
		}
		Personajes.remove(perso.getID());
	}
	
	public static String getAlineacionTodasSubareas() {
		String str = "";
		boolean primero = false;
		for (SubArea subarea : SubAreas.values()) {
			if (!subarea.getConquistable())
				continue;
			if (primero)
				str += "|";
			str += subarea.getID() + ";" + subarea.getAlineacion();
			primero = true;
		}
		return str;
	}
	
	public static long getExpMinPersonaje(int nivel) {
		if (nivel > Bustemu.MAX_NIVEL)
			nivel = Bustemu.MAX_NIVEL;
		if (nivel < 1)
			nivel = 1;
		return Experiencia.get(nivel)._personaje;
	}
	
	public static long getExpMaxPersonaje(int nivel) {
		if (nivel >= Bustemu.MAX_NIVEL)
			nivel = Bustemu.MAX_NIVEL - 1;
		if (nivel <= 1)
			nivel = 1;
		return Experiencia.get(nivel + 1)._personaje;
	}
	
	public static long getExpMaxEncarnacion(int nivel) {
		if (nivel >= 50)
			nivel = 50 - 1;
		if (nivel <= 1)
			nivel = 1;
		return Experiencia.get(nivel + 1)._encarnacion;
	}
	
	public static void salvarServidor(Personaje salvador) {
		if (_estado != 1 || Bustemu.Salvando)
			return;
		PrintWriter out = null;
		if (salvador != null)
			out = salvador.getCuenta().getEntradaPersonaje().getOut();
		_estado = 2;
		try {
			Bustemu.Salvando = true;
			GestorSQL.comenzarTransacciones();
			GestorSQL.TIMER(false);
			Thread.sleep(2000);
			System.out.println("Salvando las cuentas");
			for (Cuenta cuenta : Cuentas.values()) {
				GestorSQL.SALVAR_CUENTA(cuenta);
			}
			GestorSQL.ACTUALIZAR_NPC_KAMAS(MundoDofus.getNPCModelo(408));
			Thread.sleep(500);
			System.out.println("Salvando los personajes");
			for (Personaje perso : Personajes.values()) {
				if (!perso.enLinea())
					continue;
				GestorSQL.SALVAR_PERSONAJE(perso, true);
			}
			Thread.sleep(500);
			System.out.println("Salvando los objevivos");
			for (Objevivo objevivo : Objevivos.values()) {
				GestorSQL.SALVAR_OBJEVIVO(objevivo);
			}
			Thread.sleep(500);
			System.out.println("Salvando los encarnaciones");
			for (Encarnacion encarnacion : Encarnaciones.values()) {
				GestorSQL.SALVAR_ENCARNACION(encarnacion);
			}
			Thread.sleep(500);
			System.out.println("Salvando las areas y subareas");
			for (Area area : Areas.values()) {
				GestorSQL.ACTUALIZAR_AREA(area);
			}
			for (SubArea subarea : SubAreas.values()) {
				GestorSQL.ACTUALIZAR_SUBAREA(subarea);
			}
			Thread.sleep(500);
			System.out.println("Salvando los prismas");
			for (Prisma prisma : Prismas.values()) {
				if (Mapas.get(prisma.getMapa()).getSubArea().getPrismaID() != prisma.getID())
					GestorSQL.BORRAR_PRISMA(prisma.getID());
				else
					GestorSQL.SALVAR_PRISMA(prisma);
			}
			Thread.sleep(500);
			System.out.println("Salvando las mascotas");
			for (Mascota mascota : Mascotas.values()) {
				GestorSQL.ACTUALIZAR_MASCOTA(mascota);
			}
			Thread.sleep(500);
			System.out.println("Salvando los gremios");
			for (Gremio gremio : Gremios.values()) {
				GestorSQL.ACTUALIZAR_GREMIO(gremio);
			}
			Thread.sleep(500);
			System.out.println("Salvando los cercados");
			for (Cercado cercado : Cercados.values()) {
				GestorSQL.SALVAR_CERCADO(cercado);
				GestorSQL.ACTUALIZAR_CELDAS_OBJETO(cercado.getMapa().getID(), cercado.getStringCeldasObj());
			}
			Thread.sleep(500);
			System.out.println("Salvando los recaudadores");
			for (Recaudador recau : Recaudadores.values()) {
				if (recau.getEstadoPelea() > 0)
					continue;
				GestorSQL.ACTUALIZAR_RECAUDADOR(recau);
			}
			Thread.sleep(500);
			System.out.println("Salvando las monturas");
			for (Dragopavo montura : Dragopavos.values()) {
				GestorSQL.ACTUALIZAR_MONTURA(montura, true);
			}
			Thread.sleep(500);
			System.out.println("Salvando las casas");
			for (Casa house : Casas.values()) {
				if (house.getDueñoID() > 0) {
					GestorSQL.ACTUALIZAR_CASA(house);
				}
			}
			Thread.sleep(500);
			System.out.println("Salvando los rankings");
			for (RankingPVP rank : RankingsPVP.values()) {
				GestorSQL.SALVAR_RANKINGPVP(rank);
			}
			Thread.sleep(500);
			System.out.println("Salvando los cofres");
			for (Cofre cofre : Cofres.values()) {
				if (cofre.getDueñoID() > 0) {
					GestorSQL.ACTUALIZAR_COFRE(cofre);
				}
			}
			GestorSQL.ACTUALIZAR_COFRE(Cofre.getCofrePorUbicacion(0, 0));
			Thread.sleep(500);
			System.out.println("Salvando los puestos mercadillos");
			ArrayList<ObjetoMercadillo> toSave = new ArrayList<ObjetoMercadillo>();
			for (PuestoMercadillo puesto : PuestosMercadillos.values()) {
				toSave.addAll(puesto.todoListaObjMercaDeUnPuesto());
			}
			Thread.sleep(500);
			GestorSQL.VACIA_Y_ACTUALIZA_OBJ_MERCADILLOS(toSave);
			System.out.println("Salvada Completada");
			_estado = 1;
		} catch (ConcurrentModificationException e) {
			if (intentarSalvar < 10) {
				System.out.println("Noticia de Salvada");
				if (salvador != null && out != null)
					GestorSalida.ENVIAR_BAT2_CONSOLA(out, "Error. Noticia de Salvada");
				intentarSalvar++;
				salvarServidor(salvador);
			} else {
				_estado = 1;
				String mess = "Fallo de la salvada despues de " + intentarSalvar + " intentos";
				if (salvador != null && out != null)
					GestorSalida.ENVIAR_BAT2_CONSOLA(out, mess);
				System.out.println(mess);
			}
		} catch (Exception e) {
			System.out.println("Error al salvar : " + e.getMessage());
			e.printStackTrace();
		} finally {
			GestorSQL.comenzarTransacciones();
			GestorSQL.TIMER(true);
			Bustemu.Salvando = false;
			intentarSalvar = 1;
		}
	}
	
	public static void salvarServidor2() {
		System.out.println("Salvando las cuentas");
		for (Cuenta cuenta : Cuentas.values()) {
			GestorSQL.ELIMINAR_CUENTA(cuenta.getID());
		}
		System.out.println("Salvando los personajes");
		for (Personaje perso : Personajes.values()) {
			GestorSQL.BORRAR_PERSONAJE(perso);
		}
		System.out.println("Salvando los prismas");
		for (Prisma prisma : Prismas.values()) {
			GestorSQL.BORRAR_PRISMA(prisma.getID());
		}
		System.out.println("Salvando las mascotas");
		for (Mascota mascota : Mascotas.values()) {
			GestorSQL.BORRAR_MASCOTA(mascota.getID());
		}
		System.out.println("Salvando los gremios");
		for (Gremio gremio : Gremios.values()) {
			GestorSQL.BORRAR_GREMIO(gremio.getID());
		}
		System.out.println("Salvando los recaudadores");
		for (Recaudador recau : Recaudadores.values()) {
			GestorSQL.BORRAR_RECAUDADOR(recau.getID());
		}
	}
	
	public static ExpNivel getExpNivel(int nivel) {
		return Experiencia.get(nivel);
	}
	
	public static ObjInteractivoModelo getObjInteractivoModelo(int id) {
		return ObjInteractivos.get(id);
	}
	
	public static void addObjInteractivo(ObjInteractivoModelo OIM) {
		ObjInteractivos.put(OIM.getID(), OIM);
	}
	
	public static Oficio getOficio(int id) {
		return Oficios.get(id);
	}
	
	public static void addOficio(Oficio oficio) {
		Oficios.put(oficio.getID(), oficio);
	}
	
	public synchronized static void addReceta(int id, ArrayList<Duo<Integer, Integer>> arrayDuos) {
		Recetas.put(id, arrayDuos);
	}
	
	public static ArrayList<Duo<Integer, Integer>> getReceta(int id) {
		return Recetas.get(id);
	}
	
	public static int getIDRecetaPorIngredientes(ArrayList<Integer> listaIDRecetas, Map<Integer, Integer> ingredientes) {
		if (listaIDRecetas == null)
			return -1;
		for (int id : listaIDRecetas) {
			ArrayList<Duo<Integer, Integer>> receta = Recetas.get(id);
			if (receta == null || receta.size() != ingredientes.size())
				continue;
			boolean ok = true;
			for (Duo<Integer, Integer> ing : receta) {
				if (ingredientes.get(ing._primero) == null) {
					ok = false;
					break;
				}
				int primera = ingredientes.get(ing._primero);
				int segunda = ing._segundo;
				if (primera != segunda) {
					ok = false;
					break;
				}
			}
			if (ok)
				return id;
		}
		return -1;
	}
	
	public static Cuenta getCuentaPorApodo(String p) {
		for (Cuenta C : Cuentas.values())
			if (C.getApodo().equals(p))
				return C;
		return null;
	}
	
	public static void addItemSet(ItemSet itemSet) {
		ItemSets.put(itemSet.getId(), itemSet);
	}
	
	public static ItemSet getItemSet(int tID) {
		return ItemSets.get(tID);
	}
	
	public static int getNumeroItemsSet() {
		return ItemSets.size();
	}
	
	public synchronized static int getSigIDMontura() {
		int max = -101;
		for (int a : Dragopavos.keySet())
			if (a < max)
				max = a;
		return max - 3;
	}
	
	public synchronized static int getSigIDPrisma() {
		int max = -102;
		for (int a : Prismas.keySet())
			if (a < max)
				max = a;
		return max - 3;
	}
	
	public synchronized static void addGremio(Gremio g, boolean save) {
		Gremios.put(g.getID(), g);
		if (save)
			GestorSQL.SALVAR_NUEVO_GREMIO(g);
	}
	
	public synchronized static int getSigIdGremio() {
		if (Gremios.isEmpty())
			return 1;
		int n = 0;
		for (int x : Gremios.keySet())
			if (n < x)
				n = x;
		return n + 1;
	}
	
	public synchronized static boolean nombreGremioUsado(String name) {
		for (Gremio g : Gremios.values())
			if (g.getNombre().equalsIgnoreCase(name))
				return true;
		return false;
	}
	
	public synchronized static boolean emblemaGremioUsado(String emb) {
		for (Gremio g : Gremios.values()) {
			if (g.getEmblema().equals(emb))
				return true;
		}
		return false;
	}
	
	public static Gremio getGremio(int i) {
		return Gremios.get(i);
	}
	
	public static long getXPMaxGremio(int nivel) {
		if (nivel >= 200)
			nivel = 200 - 1;
		if (nivel <= 1)
			nivel = 1;
		return Experiencia.get(nivel + 1)._gremio;
	}
	
	public static int getCeldaZaapPorMapaID(short i) {
		for (Entry<Integer, Integer> zaap : CentroInfo.ZAAPS.entrySet()) {
			if (zaap.getKey() == i)
				return zaap.getValue();
		}
		return -1;
	}
	
	public static int getCeldaCercadoPorMapaID(short i) {
		Cercado cercado = MundoDofus.getMapa(i).getCercado();
		if (cercado != null) {
			if (cercado.getCeldaID() > 0) {
				return cercado.getCeldaID();
			}
		}
		return -1;
	}
	
	public static void borrarDragopavoID(int getId) {
		Dragopavos.remove(getId);
	}
	
	public static void borrarGremio(int id) {
		GestorSQL.BORRAR_CASA_GREMIO(id);
		Gremios.remove(id);
		GestorSQL.BORRAR_GREMIO(id);
	}
	
	public static boolean usandoIP(String ip) {
		for (Cuenta c : Cuentas.values()) {
			if (c.getActualIP().compareTo(ip) == 0)
				return true;
		}
		return false;
	}
	
	public static int cuentasIP(String ip) {
		int veces = 0;
		for (Cuenta c : Cuentas.values()) {
			if (c.getActualIP().compareTo(ip) == 0)
				veces++;
		}
		return veces;
	}
	
	public static void desconectarPerso(Personaje perso) {
		perso.setEnLinea(false);
		if (perso.getCasa() == null)
			perso.stopRecuperarVida();
	}
	
	public synchronized static Objeto objetoIniciarServer(int id, int modelo, int cant, int pos, String strStats, int idObvi) {
		ObjetoModelo objModelo = MundoDofus.getObjModelo(modelo);
		if (objModelo == null) {
			System.out.println("La id del objeto bug " + id);
			GestorSQL.BORRAR_OBJETO(id);
			return null;
		}
		if (objModelo.getTipo() == 85)
			return new PiedraDeAlma(id, cant, modelo, pos, strStats);
		else {
			return new Objeto(id, modelo, cant, pos, strStats, idObvi);
		}
	}
	
	public static void a() {
		System.out.println("Recuperando el server");
		for (Cuenta cuenta : Cuentas.values()) {
			GestorSQL.ELIMINAR_CUENTA(cuenta.getID());
		}
		for (Personaje perso : Personajes.values()) {
			GestorSQL.BORRAR_PERSONAJE(perso);
		}
		for (Prisma prisma : Prismas.values()) {
			GestorSQL.BORRAR_PRISMA(prisma.getID());
		}
		for (Mascota mascota : Mascotas.values()) {
			GestorSQL.BORRAR_MASCOTA(mascota.getID());
		}
		for (Gremio gremio : Gremios.values()) {
			GestorSQL.BORRAR_GREMIO(gremio.getID());
		}
		for (Recaudador recau : Recaudadores.values()) {
			GestorSQL.BORRAR_RECAUDADOR(recau.getID());
		}
	}
	
	public static void addHechizo(Hechizo hechizo) {
		Hechizos.put(hechizo.getID(), hechizo);
	}
	
	public static void addObjModelo(ObjetoModelo obj) {
		ObjModelos.put(obj.getID(), obj);
	}
	
	public static Hechizo getHechizo(int id) {
		return Hechizos.get(id);
	}
	
	public static ObjetoModelo getObjModelo(int id) {
		return ObjModelos.get(id);
	}
	
	public static void addMobModelo(int id, MobModelo mob) {
		MobModelos.put(id, mob);
	}
	
	public static MobModelo getMobModelo(int id) {
		return MobModelos.get(id);
	}
	
	public static List<Personaje> getPJsEnLinea() {
		List<Personaje> online = new ArrayList<Personaje>();
		Map<Integer, Personaje> personajesO = Personajes;
		for (Personaje perso : personajesO.values()) {
			if (perso.enLinea() && perso.getCuenta().getEntradaPersonaje() != null) {
				if (perso.getCuenta().getEntradaPersonaje().getOut() != null) {
					online.add(perso);
				}
			}
		}
		return online;
	}
	
	public synchronized static void addObjeto(Objeto obj, boolean salvarSQL) {
		if (obj == null)
			return;
		if (obj.getID() == 0)
			obj.setID(getSigIDObjeto());
		Objetos.put(obj.getID(), obj);
		if (salvarSQL)
			GestorSQL.AGREGAR_NUEVO_OBJETO(obj);
	}
	
	public static Objeto getObjeto(int id) {
		return Objetos.get(id);
	}
	
	public synchronized static void eliminarObjeto(int id) {
		Objetos.remove(id);
		GestorSQL.BORRAR_OBJETO(id);
		if (Mascotas.containsKey(id)) {
			GestorSQL.BORRAR_MASCOTA(id);
			Mascotas.remove(id);
		}
	}
	
	public static void eliminarMascota(int id) {
		GestorSQL.BORRAR_MASCOTA(id);
		Mascotas.remove(id);
	}
	
	public static Dragopavo getDragopavoPorID(int id) {
		return Dragopavos.get(id);
	}
	
	public synchronized static void addDragopavo(Dragopavo DP) {
		Dragopavos.put(DP.getID(), DP);
	}
	
	public static short getEstado() {
		return _estado;
	}
	
	public static void setEstado(short stado) {
		_estado = stado;
	}
	
	public static byte getGmAcceso() {
		return _gmAcceso;
	}
	
	public static void setGmAcceso(byte gmAcceso) {
		_gmAcceso = gmAcceso;
	}
	
	public static PuestoMercadillo getPuestoMerca(int mapaID) {
		return PuestosMercadillos.get(mapaID);
	}
	
	public synchronized static int sigIDLineaMercadillo() {
		sigIDLineaMerca++;
		return sigIDLineaMerca;
	}
	
	public synchronized static void addObjMercadillo(int cuentaID, int idPuestoMerca, ObjetoMercadillo objMercadillo) {
		if (ObjMercadillos.get(cuentaID) == null)
			ObjMercadillos.put(cuentaID, new HashMap<Integer, ArrayList<ObjetoMercadillo>>());
		if (ObjMercadillos.get(cuentaID).get(idPuestoMerca) == null)
			ObjMercadillos.get(cuentaID).put(idPuestoMerca, new ArrayList<ObjetoMercadillo>());
		ObjMercadillos.get(cuentaID).get(idPuestoMerca).add(objMercadillo);
	}
	
	public synchronized static void borrarObjMercadillo(int cuentaID, int idPuestoMerca, ObjetoMercadillo objMerca) {
		ObjMercadillos.get(cuentaID).get(idPuestoMerca).remove(objMerca);
	}
	
	public static int cantPuestosMercadillos() {
		return PuestosMercadillos.size();
	}
	
	public static int cantObjMercadillos() {
		int cantidad = 0;
		for (Map<Integer, ArrayList<ObjetoMercadillo>> tempCuenta : ObjMercadillos.values()) {
			for (ArrayList<ObjetoMercadillo> objMercadillo : tempCuenta.values()) {
				cantidad += objMercadillo.size();
			}
		}
		return cantidad;
	}
	
	public synchronized static void addPuestoMercadillo(PuestoMercadillo mercadillo) {
		PuestosMercadillos.put(mercadillo.getIDMercadillo(), mercadillo);
	}
	
	public static Map<Integer, ArrayList<ObjetoMercadillo>> getMisObjetos(int cuentaID) {
		if (ObjMercadillos.get(cuentaID) == null)
			ObjMercadillos.put(cuentaID, new HashMap<Integer, ArrayList<ObjetoMercadillo>>());
		return ObjMercadillos.get(cuentaID);
	}
	
	public static Collection<ObjetoModelo> getObjModelos() {
		return ObjModelos.values();
	}
	
	public static Personaje getCasado(int id) {
		return Esposos.get(id);
	}
	
	public synchronized static void addEsposo(int id, Personaje perso) {
		Personaje esposo = Esposos.get(id);
		if (esposo != null) {
			if (perso.getID() == esposo.getID())
				return;
			if (esposo.enLinea()) {
				Esposos.remove(id);
				Esposos.put(id, perso);
				return;
			}
			if (perso.getCelda() == esposo.getCelda()) {
				return;
			}
			return;
		} else {
			Esposos.put(id, perso);
			return;
		}
	}
	
	public static void discursoSacerdote(Personaje perso, Mapa mapa, int idSacerdote) {
		Personaje esposo = Esposos.get(0);
		Personaje esposa = Esposos.get(1);
		if (esposo.getEsposo() != 0) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_MAPA(mapa, esposo.getNombre() + " no acepta el matrimonio.",
					Bustemu.COLOR_MENSAJE);
			return;
		}
		if (esposa.getEsposo() != 0) {
			GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_MAPA(mapa, esposa.getNombre() + " no acepta el matrimonio.",
					Bustemu.COLOR_MENSAJE);
			return;
		}
		GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_MAPA(perso.getMapa(), "", -1, "Sacerdote", perso.getNombre()
				+ " aceptas como esposo(a) a " + getCasado( (perso.getSexo() == 1 ? 0 : 1)).getNombre() + " ?");
		GestorSalida.GAME_SEND_WEDDING(mapa, 617, (esposo == perso ? esposo.getID() : esposa.getID()),
				(esposo == perso ? esposa.getID() : esposo.getID()), idSacerdote);
	}
	
	public static void Wedding(Personaje Homme, Personaje Femme, int isOK) {
		if (isOK > 0) {
			GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_MAPA(Homme.getMapa(), "", -1, "Sacedote",
					"Los declaro marido " + Homme.getNombre() + " y mujer " + Femme.getNombre()
							+ " unidos por la línea del sagrado matrimonio, hasta que el otro o la otra los separe. xD");
			Homme.esposoDe(Femme);
			Femme.esposoDe(Homme);
		} else {
			GestorSalida.ENVIAR_Im_INFORMACION_A_MAPA(Homme.getMapa(), "048;" + Homme.getNombre() + "~" + Femme.getNombre());
		}
		Esposos.get(0).setEsOK(0);
		Esposos.get(1).setEsOK(0);
		Esposos.clear();
	}
	
	public static Collection<Objevivo> getTodosObjevivos() {
		return Objevivos.values();
	}
	
	public static Objevivo getObjevivos(int idObjevivo) {
		return Objevivos.get(idObjevivo);
	}
	
	public synchronized static void addObjevivo(Objevivo objevivo) {
		Objevivos.put(objevivo.getID(), objevivo);
	}
	
	public synchronized static int getSigIDParaObjevivo() {
		int max = 0;
		for (int a : Objevivos.keySet())
			if (a > max)
				max = a;
		return max + 1;
	}
	
	public static Animacion getAnimacion(int animacionId) {
		return Animaciones.get(animacionId);
	}
	
	public static void addAnimation(Animacion animation) {
		Animaciones.put(animation.getId(), animation);
	}
	
	public static void addReto(Reto reto) {
		Retos.put(reto.getId(), reto);
	}
	
	public static Reto getReto(int id) {
		return Retos.get(id);
	}
	
	public synchronized static Tienda nuevaTienda(int ItemID, int Precio, int Cantidad) {
		return new Tienda(ItemID, Precio, Cantidad);
	}
	
	public synchronized static void agregarTienda(Tienda tiendas, boolean salvar) {
		Tiendas.put(tiendas.getItemId(), tiendas);
		if (salvar)
			GestorSQL.AGREGAR_ITEM_TIENDA(tiendas);
	}
	
	public static Tienda getObjTienda(int id) {
		return Tiendas.get(id);
	}
	
	public synchronized static void borrarObjTienda(int id) {
		Tiendas.remove(id);
		GestorSQL.BORRAR_ITEM_TIENDA(id);
	}
	
	public static Mascota getMascota(int id) {
		return Mascotas.get(id);
	}
	
	public synchronized static void addMascota(Mascota mascota) {
		Mascotas.put(mascota.getID(), mascota);
	}
	
	public static Collection<Mascota> getTodasMascotas() {
		return Mascotas.values();
	}
	
	public static void agregarMascotaModelo(int id, MascotaModelo mascota) {
		MascotasModelos.put(id, mascota);
	}
	
	public static MascotaModelo getMascotaModelo(int id) {
		return MascotasModelos.get(id);
	}
	
	public static Set<Integer> getIdTodasMascotas() {
		return Mascotas.keySet();
	}
	
	public static void agregarCasa(Casa casa) {
		Casas.put(casa.getID(), casa);
	}
	
	public static Map<Integer, Casa> getCasas() {
		return Casas;
	}
	
	public static Casa getCasa(int id) {
		return Casas.get(id);
	}
	
	public synchronized static void addCercado(Cercado cercado) {
		Cercados.put(cercado.getMapa().getID(), cercado);
		// cercado.startMoverDrago();
	}
	
	public static Cercado getCercadoPorMap(short mapa) {
		return Cercados.get(mapa);
	}
	
	public static Collection<Cercado> todosCercados() {
		return Cercados.values();
	}
	
	public synchronized static void addPrisma(Prisma prisma) {
		Prismas.put(prisma.getID(), prisma);
	}
	
	public static Prisma getPrisma(int id) {
		return Prismas.get(id);
	}
	
	public static void borrarPrisma(int id) {
		Prismas.remove(id);
	}
	
	public static Collection<Prisma> TodosPrismas() {
		if (Prismas.size() > 0)
			return Prismas.values();
		return null;
	}
	
	public static void addCofre(Cofre cofre) {
		Cofres.put(cofre.getID(), cofre);
	}
	
	public static Cofre getCofre(int id) {
		return Cofres.get(id);
	}
	
	public static Map<Integer, Cofre> getCofres() {
		return Cofres;
	}
	
	public static void addRecaudador(Recaudador recauda) {
		Recaudadores.put(recauda.getID(), recauda);
	}
	
	public static Recaudador getRecaudador(int id) {
		return Recaudadores.get(id);
	}
	
	public static void borrarRecaudador(int id) {
		Recaudadores.remove(id);
		GestorSQL.BORRAR_RECAUDADOR(id);
	}
	
	public static Map<Integer, Recaudador> getTodosRecaudadores() {
		return Recaudadores;
	}
	
	public static Recaudador getRecauPorMapaID(short id) {
		for (Entry<Integer, Recaudador> perco : Recaudadores.entrySet()) {
			if (perco.getValue().getMapaID() == id) {
				return Recaudadores.get(perco.getValue().getID());
			}
		}
		return null;
	}
	
	public static int cantRecauDelGremio(int gremiodID) {
		int i = 0;
		for (Entry<Integer, Recaudador> perco : Recaudadores.entrySet()) {
			if (perco.getValue().getGremioID() == gremiodID) {
				i++;
			}
		}
		return i;
	}
	
	public static void addRankingPVP(RankingPVP rank) {
		RankingsPVP.put(rank.getID(), rank);
	}
	
	public static void delRankingPVP(int id) {
		RankingsPVP.remove(id);
	}
	
	public static boolean estaRankingPVP(int id) {
		if (RankingsPVP.get(id) != null)
			return true;
		return false;
	}
	
	public static RankingPVP getRanking(int id) {
		return RankingsPVP.get(id);
	}
	
	public static String nombreLiderRankingPVP() {
		String id = "";
		if (RankingsPVP.size() <= 0)
			return id;
		else {
			int vict = 0;
			int derr = 0;
			for (RankingPVP rank : RankingsPVP.values()) {
				if (rank.getVictorias() > vict) {
					vict = rank.getVictorias();
					id = rank.getNombre();
					derr = rank.getDerrotas();
				} else if (rank.getVictorias() == vict) {
					if (rank.getDerrotas() <= derr) {
						vict = rank.getVictorias();
						id = rank.getNombre();
						derr = rank.getDerrotas();
					}
				}
			}
		}
		return id;
	}
	
	public static int IDLiderRankingPVP() {
		int id = 0;
		if (RankingsPVP.size() <= 0)
			return id;
		else {
			int vict = 0;
			int derr = 0;
			for (RankingPVP rank : RankingsPVP.values()) {
				if (rank.getVictorias() > vict) {
					vict = rank.getVictorias();
					id = rank.getID();
					derr = rank.getDerrotas();
				} else if (rank.getVictorias() == vict) {
					if (rank.getDerrotas() <= derr) {
						vict = rank.getVictorias();
						id = rank.getID();
						derr = rank.getDerrotas();
					}
				}
			}
		}
		return id;
	}
	
	public static float getBalanceMundo(int alineacion) {
		int cant = 0;
		for (SubArea subarea : SubAreas.values()) {
			if (subarea.getAlineacion() == alineacion)
				cant++;
		}
		if (cant == 0)
			return 0;
		return (float) Math.rint( (10 * cant / 4) / 10);
	}
	
	public static float getBalanceArea(Area area, int alineacion) {
		int cant = 0;
		for (SubArea subarea : SubAreas.values()) {
			if (subarea.getArea() == area && subarea.getAlineacion() == alineacion)
				cant++;
		}
		if (cant == 0)
			return 0;
		return (float) Math.rint( (1000 * cant / (area.getSubAreas().size())) / 10);
	}
	
	public static String prismasGeoposicion(int alineacion) {
		String str = "";
		boolean primero = false;
		int subareas = 0;
		for (SubArea subarea : SubAreas.values()) {
			if (!subarea.getConquistable())
				continue;
			if (primero)
				str += ";";
			str += subarea.getID() + "," + (subarea.getAlineacion() == 0 ? -1 : subarea.getAlineacion()) + ",0,";
			if (MundoDofus.getPrisma(subarea.getPrismaID()) == null)
				str += 0 + ",1";
			else
				str += (subarea.getPrismaID() == 0 ? 0 : MundoDofus.getPrisma(subarea.getPrismaID()).getMapa()) + ",1";
			primero = true;
			subareas++;
		}
		if (alineacion == 1)
			str += "|" + Area._bontas;
		else if (alineacion == 2)
			str += "|" + Area._brakmars;
		str += "|" + Areas.size() + "|";
		primero = false;
		for (Area area : Areas.values()) {
			if (area.getAlineacion() == 0)
				continue;
			if (primero)
				str += ";";
			str += area.getID() + "," + area.getAlineacion() + ",1," + (area.getPrismaID() == 0 ? 0 : 1);
			primero = true;
		}
		if (alineacion == 1)
			str = Area._bontas + "|" + subareas + "|" + (subareas - (SubArea._bontas + SubArea._brakmars)) + "|" + str;
		else if (alineacion == 2)
			str = Area._brakmars + "|" + subareas + "|" + (subareas - (SubArea._bontas + SubArea._brakmars)) + "|" + str;
		return str;
	}
	
	/* public static void generarPeleas() { String str =
	 * "En 60 segundos empezará tu combate Koliseo, porfavor estar desocupado para esto. Escribir .ok  si aceptas el combate.\n" +
	 * "En 60 secondes commencer votre Koliseo combat, s'il vous plaît être ralenti pour cela. Ecrire .ok, si vous acceptez le match"
	 * ; int size1 = GrupoKoliseo1.size(); for (int i = 0; i < size1; i = i + 2) { GrupoKoliseo grupo1 = GrupoKoliseo1.get(i);
	 * GrupoKoliseo grupo2 = GrupoKoliseo1.get(i + 1); for (Personaje persos : grupo1.getParticipantes()) { } } } */
	public static void addKoliseo1(Personaje perso) {
		if (Koliseo1.isEmpty())
			Koliseo1.add(perso);
		else {
			for (Personaje persos : Koliseo1) {
				if (persos != null) {
					if (persos.getNivel() >= perso.getNivel()) {
						int index = Koliseo1.indexOf(persos);
						Koliseo1.add(index - 1, perso);
					}
				}
			}
		}
	}
	
	public static void addKoliseo2(Personaje perso) {
		if (Koliseo2.isEmpty())
			Koliseo2.add(perso);
		else {
			for (Personaje persos : Koliseo2) {
				if (persos != null) {
					if (persos.getNivel() >= perso.getNivel()) {
						int index = Koliseo2.indexOf(persos);
						Koliseo2.add(index - 1, perso);
					}
				}
			}
		}
	}
	
	public static void addKoliseo3(Personaje perso) {
		if (Koliseo3.isEmpty())
			Koliseo3.add(perso);
		else {
			for (Personaje persos : Koliseo3) {
				if (persos != null) {
					if (persos.getNivel() >= perso.getNivel()) {
						int index = Koliseo3.indexOf(persos);
						Koliseo3.add(index - 1, perso);
					}
				}
			}
		}
	}
	
	public static void crearGruposKoliseo1() {
		CopyOnWriteArrayList<Personaje> kolis1 = new CopyOnWriteArrayList<Personaje>();
		for (Personaje persos : Koliseo1) {
			if (persos == null || !persos.enLinea())
				continue;
			kolis1.add(persos);
		}
		if (kolis1.size() < 6)
			return;
		int size = kolis1.size();
		for (int i = 0; i < size; i = i + 3) {
			Personaje koli1 = null;
			Personaje koli2 = null;
			Personaje koli3 = null;
			Random rand = new Random();
			int random = rand.nextInt(kolis1.size() - 1);
			koli1 = kolis1.get(random);
			kolis1.remove(random);
			random = rand.nextInt(kolis1.size() - 1);
			koli2 = kolis1.get(random);
			kolis1.remove(random);
			random = rand.nextInt(kolis1.size() - 1);
			koli3 = kolis1.get(random);
			kolis1.remove(random);
			if (koli1 != null && koli2 != null && koli3 != null) {
				GrupoKoliseo grupo = new GrupoKoliseo(koli1, koli2, koli3, 1);
				GrupoKoliseo1.add(grupo);
			}
		}
	}
	
	public static void crearGruposKoliseo2() {
		CopyOnWriteArrayList<Personaje> kolis1 = new CopyOnWriteArrayList<Personaje>();
		for (Personaje persos : Koliseo2) {
			if (persos == null || !persos.enLinea())
				continue;
			kolis1.add(persos);
		}
		if (kolis1.size() < 6)
			return;
		int size = kolis1.size();
		for (int i = 0; i < size; i = i + 3) {
			Personaje koli1 = null;
			Personaje koli2 = null;
			Personaje koli3 = null;
			Random rand = new Random();
			int random = rand.nextInt(kolis1.size() - 1);
			koli1 = kolis1.get(random);
			kolis1.remove(random);
			random = rand.nextInt(kolis1.size() - 1);
			koli2 = kolis1.get(random);
			kolis1.remove(random);
			random = rand.nextInt(kolis1.size() - 1);
			koli3 = kolis1.get(random);
			kolis1.remove(random);
			if (koli1 != null && koli2 != null && koli3 != null) {
				GrupoKoliseo grupo = new GrupoKoliseo(koli1, koli2, koli3, 1);
				GrupoKoliseo2.add(grupo);
			}
		}
	}
	
	public static void crearGruposKoliseo3() {
		CopyOnWriteArrayList<Personaje> kolis1 = new CopyOnWriteArrayList<Personaje>();
		for (Personaje persos : Koliseo3) {
			if (persos == null || !persos.enLinea())
				continue;
			kolis1.add(persos);
		}
		if (kolis1.size() < 6)
			return;
		int size = kolis1.size();
		for (int i = 0; i < size; i = i + 3) {
			Personaje koli1 = null;
			Personaje koli2 = null;
			Personaje koli3 = null;
			Random rand = new Random();
			int random = rand.nextInt(kolis1.size() - 1);
			koli1 = kolis1.get(random);
			kolis1.remove(random);
			random = rand.nextInt(kolis1.size() - 1);
			koli2 = kolis1.get(random);
			kolis1.remove(random);
			random = rand.nextInt(kolis1.size() - 1);
			koli3 = kolis1.get(random);
			kolis1.remove(random);
			if (koli1 != null && koli2 != null && koli3 != null) {
				GrupoKoliseo grupo = new GrupoKoliseo(koli1, koli2, koli3, 1);
				GrupoKoliseo3.add(grupo);
			}
		}
	}
	
	public static void addEncarnacion(Encarnacion encarnacion) {
		Encarnaciones.put(encarnacion.getID(), encarnacion);
	}
	
	public static Encarnacion getEncarnacion(int id) {
		return Encarnaciones.get(id);
	}
	
	public static void borrarEncarnacion(int id) {
		Encarnaciones.remove(id);
		GestorSQL.BORRAR_ENCARNACION(id);
	}
	
	public static void addTutorial(Tutorial tutorial) {
		Tutoriales.put(tutorial.getID(), tutorial);
	}
	
	public static Tutorial getTutorial(int id) {
		return Tutoriales.get(id);
	}
}
