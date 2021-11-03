
package variables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Timer;

import servidor.EntradaPersonaje.AccionDeJuego;
import variables.Gremio.MiembroGremio;
import variables.Hechizo.StatsHechizos;
import variables.Oficio.StatsOficio;
import variables.Mapa.*;
import variables.Objeto.ObjetoModelo;
import variables.Oficio.*;
import variables.Pelea.*;

import estaticos.Bustemu;
import estaticos.CentroInfo;
import estaticos.Formulas;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import estaticos.MundoDofus.Duo;
import estaticos.MundoDofus.Intercambio;
import estaticos.MundoDofus.InvitarTaller;
import estaticos.MundoDofus.ItemSet;
import estaticos.MundoDofus.Trueque;

public class Personaje {
	private int _ID;
	private String _nombre;
	private int _sexo;
	private int _clase;
	private int _color1;
	private int _color2;
	private int _color3;
	private long _kamas;
	private int _puntosHechizo;
	private int _capital;
	private int _energia;
	private int _nivel;
	private long _experiencia;
	private int _talla;
	private int _gfxID;
	private int _esMercante = 0;
	private int _orientacion = 1;
	private Cuenta _cuenta;
	private int _cuentaID;
	private String _emotes = "7667711";
	private byte _alineacion = 0;
	private int _deshonor = 0;
	private int _honor = 0;
	private boolean _mostrarAlas = false;
	private int _nivelAlineacion = 0;
	private MiembroGremio _miembroGremio;
	private boolean _mostrarConeccionAmigos;
	private String _canales;
	Stats _baseStats;
	private Pelea _pelea;
	private boolean _ocupado;
	private Mapa _mapa;
	private Celda _celda;
	private int _PDV;
	private boolean _estarBanco;
	private int _PDVMAX;
	private boolean _sentado;
	private boolean _listo = false;
	private boolean _enLinea = false;
	private Grupo _grupo;
	private int _intercambioCon = 0;
	private Intercambio _intercambio;
	private InvitarTaller _tallerInvitado;
	private int _conversandoCon = 0;
	private int _invitando = 0;
	private int _dueloID = -1;
	private Map<Integer, StatsHechizos> _hechizos = new TreeMap<Integer, StatsHechizos>();
	private Map<Integer, Character> _lugaresHechizos = new TreeMap<Integer, Character>();
	private Map<Integer, Objeto> _objetos = new TreeMap<Integer, Objeto>();
	private ArrayList<Objeto> _tienda = new ArrayList<Objeto>();
	private Map<Integer, StatsOficio> _statsOficios = new TreeMap<Integer, StatsOficio>();
	private Timer _recuperarVida;
	private String _puntoSalvado;
	private int _exPdv;
	private Cercado _enCercado;
	private int _emoteActivado = 0;
	private AccionTrabajo _haciendoTrabajo;
	private Dragopavo _montura;
	private int _xpDonadaMontura = 0;
	private boolean _montando = false;
	private boolean _enZaaping = false;
	private ArrayList<Short> _zaaps = new ArrayList<Short>();
	private boolean _ausente = false;
	private boolean _invisible = false;
	private Personaje _siguiendo = null;
	private boolean _olvidandoHechizo = false;
	private boolean _esDoble = false;
	private boolean _recaudando = false;
	private boolean _dragopaveando = false;
	private int _recaudandoRecaudadorID = 0;
	private MisionPVP _misionPvp = null;
	private int _titulo = 0;
	private int _esposo = 0;
	private int _esOK = 0;
	private AccionDeJuego _taller = null;
	private Trueque _trueque = null;
	private ArrayList<Integer> _setClase = new ArrayList<Integer>();
	private Map<Integer, Duo<Integer, Integer>> _hechizosSetClase = new TreeMap<Integer, Duo<Integer, Integer>>();
	private boolean _esFantasma = false;
	private Mapa _tempMapaDefPerco = null;
	private Celda _tempCeldaDefPerco = null;
	private Map<Integer, Personaje> _seguidores = new TreeMap<Integer, Personaje>();
	private Cofre _cofre;
	private Casa _casa;
	private Mascota _mascota;
	private int _scrollFuerza = 0;
	private int _scrollInteligencia = 0;
	private int _scrollAgilidad = 0;
	private int _scrollSuerte = 0;
	private int _scrollVitalidad = 0;
	private int _scrollSabiduria = 0;
	private boolean _oficioPublico = false;
	private String _stringOficiosPublicos = "";
	private boolean _listaArtesanos = false;
	private int _bendHechizo = 0;
	private int _bendEfecto = 0;
	private int _bendModif = 0;
	private boolean _cambiarNombre = false;
	private boolean _enKoliseo = false;
	private GrupoKoliseo _koliseo = null;
	private int _categoria = 0;
	private boolean _aceptaKoli = false;
	private int _restriccionesA;
	private int _restriccionesB;
	private boolean _puedeAgredir;
	private boolean _puedeDesafiar;
	private boolean _puedeIntercambiar;
	private boolean _puedeAtacarAMutante;
	private boolean _puedeChatATodos;
	private boolean _puedeMercante;
	private boolean _puedeUsarObjetos;
	private boolean _puedeInteractuarRecaudador;
	private boolean _puedeInteractuarObjetos;
	private boolean _puedeHablarNPC;
	private boolean _puedeAtacarMobsDungCuandoMutante;
	private boolean _puedeMoverTodasDirecciones;
	private boolean _puedeAtacarMobsCualquieraCuandoMutante;
	private boolean _puedeInteractuarPrisma;
	private boolean _puedeSerAgredido;
	private boolean _puedeSerDesafiado;
	private boolean _puedeHacerIntercambio;
	private boolean _puedeSerAtacado;
	private boolean _forzadoCaminar;
	private boolean _esLento;
	private boolean _puedeSwitchModoCriatura;
	private boolean _esTumba;
	private String _forjaEcK;
	private String _ultimaMisionPVP = "";
	private boolean _pescarKuakua = false;
	private Encarnacion _encarnacion;
	private int _idEncarnacion = -1;
	private boolean _reconectado = false;
	private Tutorial _tutorial;
	
	public void setTutorial(Tutorial tuto) {
		_tutorial = tuto;
	}
	
	public Tutorial getTutorial() {
		return _tutorial;
	}
	
	public void setReconectado(boolean recon) {
		_reconectado = recon;
	}
	
	public boolean getReconectado() {
		return _reconectado;
	}
	
	public void setEncarnacion(Encarnacion encarnacion) {
		_encarnacion = encarnacion;
		if (encarnacion != null)
			_idEncarnacion = encarnacion.getID();
		else
			_idEncarnacion = -1;
	}
	
	public int getIDEncarnacion() {
		return _idEncarnacion;
	}
	
	public Encarnacion getEncarnacion() {
		return _encarnacion;
	}
	
	public void setPescarKuakua(boolean pescar) {
		_pescarKuakua = pescar;
	}
	
	public boolean getPescarKuakua() {
		return _pescarKuakua;
	}
	
	public void setUltimaMision(String nombre) {
		_ultimaMisionPVP = nombre;
	}
	
	public String getUltimaMision() {
		return _ultimaMisionPVP;
	}
	
	public void setForjaEcK(String forja) {
		_forjaEcK = forja;
	}
	
	public String getForjaEcK() {
		return _forjaEcK;
	}
	
	public void efectuarRestriccionesA() {
		_puedeAgredir = (_restriccionesA & 1) != 1;
		_puedeDesafiar = (_restriccionesA & 2) != 2;
		_puedeIntercambiar = (_restriccionesA & 4) != 4;
		_puedeAtacarAMutante = (_restriccionesA & 8) == 8;
		_puedeChatATodos = (_restriccionesA & 16) != 16;
		_puedeMercante = (_restriccionesA & 32) != 32;
		_puedeUsarObjetos = (_restriccionesA & 64) != 64;
		_puedeInteractuarRecaudador = (_restriccionesA & 128) != 128;
		_puedeInteractuarObjetos = (_restriccionesA & 256) != 256;
		_puedeHablarNPC = (_restriccionesA & 512) != 512;
		_puedeAtacarMobsDungCuandoMutante = (_restriccionesA & 4096) == 4096;
		_puedeMoverTodasDirecciones = (_restriccionesA & 8192) == 8192;
		_puedeAtacarMobsCualquieraCuandoMutante = (_restriccionesA & 16384) == 16384;
		_puedeInteractuarPrisma = (_restriccionesA & 32768) != 32768;
	}
	
	public String mostrarmeA() {
		_puedeAgredir = (_restriccionesA & 1) != 1;
		_puedeDesafiar = (_restriccionesA & 2) != 2;
		_puedeIntercambiar = (_restriccionesA & 4) != 4;
		_puedeAtacarAMutante = (_restriccionesA & 8) == 8;
		_puedeChatATodos = (_restriccionesA & 16) != 16;
		_puedeMercante = (_restriccionesA & 32) != 32;
		_puedeUsarObjetos = (_restriccionesA & 64) != 64;
		_puedeInteractuarRecaudador = (_restriccionesA & 128) != 128;
		_puedeInteractuarObjetos = (_restriccionesA & 256) != 256;
		_puedeHablarNPC = (_restriccionesA & 512) != 512;
		_puedeAtacarMobsDungCuandoMutante = (_restriccionesA & 4096) == 4096;
		_puedeMoverTodasDirecciones = (_restriccionesA & 8192) == 8192;
		_puedeAtacarMobsCualquieraCuandoMutante = (_restriccionesA & 16384) == 16384;
		_puedeInteractuarPrisma = (_restriccionesA & 32768) != 32768;
		String retorno = "RESTRICCIONES DE A ---------------------------" + _nombre + "\n_puedeAgredir : " + _puedeAgredir
				+ "\n_puedeDesafiar : " + _puedeDesafiar + "\n_puedeIntercambiar : " + _puedeIntercambiar
				+ "\n_puedeAtacarAMutante : " + _puedeAtacarAMutante + "\n_puedeChatATodos : " + _puedeChatATodos
				+ "\n_puedeMercante : " + _puedeMercante + "\n_puedeUsarObjetos : " + _puedeUsarObjetos
				+ "\n_puedeInteractuarRecaudador : " + _puedeInteractuarRecaudador + "\n_puedeInteractuarObjetos : "
				+ _puedeInteractuarObjetos + "\n_puedeHablarNPC : " + _puedeHablarNPC + "\n_puedeAtacarMobsDungCuandoMutante : "
				+ _puedeAtacarMobsDungCuandoMutante + "\n_puedeMoverTodasDirecciones : " + _puedeMoverTodasDirecciones
				+ "\n_puedeAtacarMobsCualquieraCuandoMutante : " + _puedeAtacarMobsCualquieraCuandoMutante
				+ "\n_puedeInteractuarPrisma : " + _puedeInteractuarPrisma;
		return retorno;
	}
	
	public int getRestriccionesA() {
		int restr = 0;
		if (!_puedeAgredir)
			restr += 1;
		if (!_puedeDesafiar)
			restr += 2;
		if (!_puedeIntercambiar)
			restr += 4;
		if (_puedeAtacarAMutante)
			restr += 8;
		if (!_puedeChatATodos)
			restr += 16;
		if (!_puedeMercante)
			restr += 32;
		if (!_puedeUsarObjetos)
			restr += 64;
		if (!_puedeInteractuarRecaudador)
			restr += 128;
		if (!_puedeInteractuarObjetos)
			restr += 256;
		if (!_puedeHablarNPC)
			restr += 512;
		if (_puedeAtacarMobsDungCuandoMutante)
			restr += 4096;
		if (_puedeMoverTodasDirecciones)
			restr += 8192;
		if (_puedeAtacarMobsCualquieraCuandoMutante)
			restr += 16384;
		if (!_puedeInteractuarPrisma)
			restr += 32768;
		_restriccionesA = restr;
		return restr;
	}
	
	public void efectuarRestriccionesB() {
		_puedeSerAgredido = (_restriccionesB & 1) != 1;
		_puedeSerDesafiado = (_restriccionesB & 2) != 2;
		_puedeHacerIntercambio = (_restriccionesB & 4) != 4;
		_puedeSerAtacado = (_restriccionesB & 8) != 8;
		_forzadoCaminar = (_restriccionesB & 16) == 16;
		_esLento = (_restriccionesB & 32) == 32;
		_puedeSwitchModoCriatura = (_restriccionesB & 64) != 64;
		_esTumba = (_restriccionesB & 128) == 128;
	}
	
	public String mostrarmeB() {
		_puedeSerAgredido = (_restriccionesB & 1) != 1;
		_puedeSerDesafiado = (_restriccionesB & 2) != 2;
		_puedeHacerIntercambio = (_restriccionesB & 4) != 4;
		_puedeSerAtacado = (_restriccionesB & 8) != 8;
		_forzadoCaminar = (_restriccionesB & 16) == 16;
		_esLento = (_restriccionesB & 32) == 32;
		_puedeSwitchModoCriatura = (_restriccionesB & 64) != 64;
		_esTumba = (_restriccionesB & 128) == 128;
		String retorno = "RESTRICCIONES DE B ---------------------------" + _nombre + "\n_puedeSerAgredido : "
				+ _puedeSerAgredido + "\n_puedeSerDesafiado : " + _puedeSerDesafiado + "\n_puedeHacerIntercambio : "
				+ _puedeHacerIntercambio + "\n_puedeSerAtacado : " + _puedeSerAtacado + "\n_forzadoCaminar : " + _forzadoCaminar
				+ "\n_esLento : " + _esLento + "\n_puedeSwitchModoCriatura : " + _puedeSwitchModoCriatura + "\n_esTumba : "
				+ _esTumba;
		return retorno;
	}
	
	public int getRestriccionesB() {
		int restr = 0;
		if (!_puedeSerAgredido)
			restr += 1;
		if (!_puedeSerDesafiado)
			restr += 2;
		if (!_puedeHacerIntercambio)
			restr += 4;
		if (!_puedeSerAtacado)
			restr += 8;
		if (_forzadoCaminar)
			restr += 16;
		if (_esLento)
			restr += 32;
		if (!_puedeSwitchModoCriatura)
			restr += 64;
		if (_esTumba)
			restr += 128;
		_restriccionesB = restr;
		return restr;
	}
	
	public Map<Integer, Personaje> getSeguidores() {
		return _seguidores;
	}
	
	public void setMascota(Mascota mascota) {
		_mascota = mascota;
	}
	
	public Mascota getMascota() {
		return _mascota;
	}
	
	public void setGrupoKoliseo(GrupoKoliseo koli) {
		_koliseo = koli;
	}
	
	public GrupoKoliseo getGrupoKoliseo() {
		return _koliseo;
	}
	
	public void setEnKoliseo(boolean koliseo) {
		_enKoliseo = koliseo;
	}
	
	public boolean getEnKoliseo() {
		return _enKoliseo;
	}
	
	public void setCategoria(int categoria) {
		_categoria = categoria;
	}
	
	public int getCategoria() {
		return _categoria;
	}
	
	public Map<Integer, Duo<Integer, Integer>> getHechizosSetClase() {
		return _hechizosSetClase;
	}
	
	public void delHechizosSetClase(int hechizo) {
		if (_hechizosSetClase.containsKey(hechizo)) {
			_hechizosSetClase.remove(hechizo);
		}
	}
	
	public void addHechizosSetClase(int hechizo, int efecto, int modificacion) {
		if (!_hechizosSetClase.containsKey(hechizo)) {
			_hechizosSetClase.put(hechizo, new Duo<Integer, Integer>(efecto, modificacion));
		}
	}
	
	public void setListaArtesanos(boolean viendo) {
		_listaArtesanos = viendo;
	}
	
	public boolean getListaArtesanos() {
		return _listaArtesanos;
	}
	
	public void addScrollFuerza(int scroll) {
		_scrollFuerza += scroll;
	}
	
	public void addScrollAgilidad(int scroll) {
		_scrollAgilidad += scroll;
	}
	
	public void addScrollSuerte(int scroll) {
		_scrollSuerte += scroll;
	}
	
	public void addScrollVitalidad(int scroll) {
		_scrollVitalidad += scroll;
	}
	
	public void addScrollSabiduria(int scroll) {
		_scrollSabiduria += scroll;
	}
	
	public void addScrollInteligencia(int scroll) {
		_scrollInteligencia += scroll;
	}
	
	public int getScrollFuerza() {
		return _scrollFuerza;
	}
	
	public int getScrollAgilidad() {
		return _scrollAgilidad;
	}
	
	public int getScrollSuerte() {
		return _scrollSuerte;
	}
	
	public int getScrollVitalidad() {
		return _scrollVitalidad;
	}
	
	public int getScrollSabiduria() {
		return _scrollSabiduria;
	}
	
	public int getScrollInteligencia() {
		return _scrollInteligencia;
	}
	
	public void setMapaDefPerco(Mapa mapa) {
		_tempMapaDefPerco = mapa;
	}
	
	public void setCeldaDefPerco(Celda celda) {
		_tempCeldaDefPerco = celda;
	}
	
	public Celda getCeldaDefPerco() {
		return _tempCeldaDefPerco;
	}
	
	public Mapa getMapaDefPerco() {
		return _tempMapaDefPerco;
	}
	
	public ArrayList<Integer> getSetClase() {
		return _setClase;
	}
	
	public void setSetClase(ArrayList<Integer> SetClase) {
		_setClase = SetClase;
	}
	
	public void agregarSetClase(int item) {
		if (!_setClase.contains(item))
			_setClase.add(item);
		return;
	}
	
	public void borrarSetClase(int item) {
		if (_setClase.contains(item)) {
			int index = _setClase.indexOf(item);
			_setClase.remove(index);
		}
	}
	
	public int getMercante() {
		return _esMercante;
	}
	
	public void setMercante(int mercante) {
		_esMercante = mercante;
	}
	
	public Trueque getTrueque() {
		return _trueque;
	}
	
	public void setTrueque(Trueque trueque) {
		_trueque = trueque;
	}
	
	public AccionDeJuego getTaller() {
		return _taller;
	}
	
	public void setTaller(AccionDeJuego Taller) {
		_taller = Taller;
	}
	public static class MisionPVP {
		private long _tiempo;
		private Personaje _victimaPVP;
		
		public MisionPVP(long tiempo, Personaje p) {
			_tiempo = tiempo;
			_victimaPVP = p;
		}
		
		public void setPjMision(Personaje tempP) {
			_victimaPVP = tempP;
		}
		
		public Personaje getPjMision() {
			return _victimaPVP;
		}
		
		public long getTiempoPVP() {
			return _tiempo;
		}
		
		public void setTiempoPVP(long tiempo) {
			_tiempo = tiempo;
		}
	}
	public static class Grupo {
		private CopyOnWriteArrayList<Personaje> _personajesGrupo = new CopyOnWriteArrayList<Personaje>();
		private Personaje _liderGrupo;
		
		public Grupo(Personaje p1, Personaje p2) {
			_liderGrupo = p1;
			_personajesGrupo.add(p1);
			_personajesGrupo.add(p2);
		}
		
		public boolean esLiderGrupo(int id) {
			return _liderGrupo.getID() == id;
		}
		
		public void addPerso(Personaje perso) {
			_personajesGrupo.add(perso);
		}
		
		public ArrayList<Integer> getIDsPersos() {
			ArrayList<Integer> lista = new ArrayList<Integer>();
			for (Personaje perso : _personajesGrupo) {
				lista.add(perso.getID());
			}
			return lista;
		}
		
		public int getNumeroPjs() {
			return _personajesGrupo.size();
		}
		
		public int getNivelGrupo() {
			int nivel = 0;
			for (Personaje p : _personajesGrupo) {
				nivel += p.getNivel();
			}
			return nivel;
		}
		
		public CopyOnWriteArrayList<Personaje> getPersos() {
			return _personajesGrupo;
		}
		
		public Personaje getLiderGrupo() {
			return _liderGrupo;
		}
		
		public void dejarGrupo(Personaje p) {
			if (!_personajesGrupo.contains(p))
				return;
			p.setGrupo(null);
			_personajesGrupo.remove(p);
			if (_personajesGrupo.size() == 1) {
				_personajesGrupo.get(0).setGrupo(null);
				if (_personajesGrupo.get(0).getCuenta() == null
						|| _personajesGrupo.get(0).getCuenta().getEntradaPersonaje() == null)
					return;
				GestorSalida.ENVIAR_PV_DEJAR_GRUPO(_personajesGrupo.get(0).getCuenta().getEntradaPersonaje().getOut(), "");
			} else
				GestorSalida.ENVIAR_PM_EXPULSAR_PJ_GRUPO(this, p.getID());
		}
	}
	public static class Stats {
		public Map<Integer, Integer> _statsEfecto = new TreeMap<Integer, Integer>();
		
		public Stats(boolean addBases, Personaje perso) {
			_statsEfecto = new TreeMap<Integer, Integer>();
			if (!addBases)
				return;
			_statsEfecto.put(111, perso.getNivel() < Bustemu.NIVEL_PA1 ? 6 : (6 + Bustemu.CANTIDAD_PA1));
			_statsEfecto.put(CentroInfo.STATS_ADD_PM, 3);
			_statsEfecto.put(176, perso.getClase(false) == CentroInfo.CLASE_ANUTROF ? 140 : 100);
			_statsEfecto.put(158, 1000);
			_statsEfecto.put(182, 1);
			_statsEfecto.put(174, 1);
		}
		
		public Stats(Map<Integer, Integer> stats, boolean addBases, Personaje perso) {
			_statsEfecto = stats;
			if (!addBases)
				return;
			_statsEfecto.put(111, perso.getNivel() < Bustemu.NIVEL_PA1 ? 6 : (6 + Bustemu.CANTIDAD_PA1));
			_statsEfecto.put(CentroInfo.STATS_ADD_PM, 3);
			_statsEfecto.put(176, perso.getClase(false) == CentroInfo.CLASE_ANUTROF ? 140 : 100);
			_statsEfecto.put(158, 1000);
			_statsEfecto.put(182, 1);
			_statsEfecto.put(174, 1);
		}
		
		public Stats(Map<Integer, Integer> stats) {
			_statsEfecto = stats;
		}
		
		public Stats() {
			_statsEfecto = new TreeMap<Integer, Integer>();
		}
		
		public int addUnStat(int stat, int valor) {
			if (_statsEfecto.get(stat) == null)
				_statsEfecto.put(stat, valor);
			else {
				int nuevoValor = (_statsEfecto.get(stat) + valor);
				_statsEfecto.remove(stat);
				_statsEfecto.put(stat, nuevoValor);
			}
			return _statsEfecto.get(stat);
		}
		
		public int especificarStat(int stat, int valor) {
			if (_statsEfecto.get(stat) == null)
				_statsEfecto.put(stat, valor);
			else {
				_statsEfecto.remove(stat);
				_statsEfecto.put(stat, valor);
			}
			return _statsEfecto.get(stat);
		}
		
		public boolean sonStatsIguales(Stats otros) {
			for (Entry<Integer, Integer> entry : _statsEfecto.entrySet()) {
				if (otros.getStatsComoMap().get(entry.getKey()) == null
						|| otros.getStatsComoMap().get(entry.getKey()) != entry.getValue())
					return false;
			}
			for (Entry<Integer, Integer> entry : otros.getStatsComoMap().entrySet()) {
				if (_statsEfecto.get(entry.getKey()) == null || _statsEfecto.get(entry.getKey()) != entry.getValue())
					return false;
			}
			return true;
		}
		
		public int getEfecto(int id) {
			int val;
			if (_statsEfecto.get(id) == null)
				val = 0;
			else
				val = _statsEfecto.get(id);
			switch (id) {
				case 160:
					if (_statsEfecto.get(CentroInfo.STATS_REM_PROB_PERD_PA) != null)
						val -= (int) (getEfecto(CentroInfo.STATS_REM_PROB_PERD_PA));
					if (_statsEfecto.get(124) != null)
						val += (int) (getEfecto(124) / 4);
					break;
				case 161:
					if (_statsEfecto.get(CentroInfo.STATS_REM_PROB_PERD_PM) != null)
						val -= (int) (getEfecto(CentroInfo.STATS_REM_PROB_PERD_PM));
					if (_statsEfecto.get(124) != null)
						val += (int) (getEfecto(124) / 4);
					break;
				case 174:
					if (_statsEfecto.get(CentroInfo.STATS_REM_INIT) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_INIT);
					break;
				case 119:
					if (_statsEfecto.get(CentroInfo.STATS_REM_AGILIDAD) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_AGILIDAD);
					break;
				case 118:
					if (_statsEfecto.get(CentroInfo.STATS_REM_FUERZA) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_FUERZA);
					break;
				case 123:
					if (_statsEfecto.get(CentroInfo.STATS_REM_SUERTE) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_SUERTE);
					break;
				case 126:
					if (_statsEfecto.get(CentroInfo.STATS_REM_INTELIGENCIA) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_INTELIGENCIA);
					break;
				case 111:
					if (_statsEfecto.get(CentroInfo.STATS_ADD_PA2) != null)
						val += _statsEfecto.get(CentroInfo.STATS_ADD_PA2);
					if (_statsEfecto.get(CentroInfo.STATS_REM_PA) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_PA);
					if (_statsEfecto.get(CentroInfo.STATS_REM_PA_NOESQ) != null)// No esquivable
						val -= _statsEfecto.get(CentroInfo.STATS_REM_PA_NOESQ);
					break;
				case 128:
					if (_statsEfecto.get(CentroInfo.STATS_ADD_PM2) != null)
						val += _statsEfecto.get(CentroInfo.STATS_ADD_PM2);
					if (_statsEfecto.get(CentroInfo.STATS_REM_PM) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_PM);
					if (_statsEfecto.get(CentroInfo.STATS_REM_PM_NOESQ) != null)// No esquivable
						val -= _statsEfecto.get(CentroInfo.STATS_REM_PM_NOESQ);
					break;
				case 117:
					if (_statsEfecto.get(CentroInfo.STATS_REM_ALCANCE) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_ALCANCE);
					break;
				case 125:
					if (_statsEfecto.get(110) != null)
						val += _statsEfecto.get(110);
					if (_statsEfecto.get(CentroInfo.STATS_REM_VITALIDAD) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_VITALIDAD);
					break;
				case 112:
					if (_statsEfecto.get(CentroInfo.STATS_REM_DAÑOS) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_DAÑOS);
					break;
				case 158:
					if (_statsEfecto.get(CentroInfo.STATS_REM_PODS) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_PODS);
					break;
				case 176:
					if (_statsEfecto.get(CentroInfo.STATS_REM_PROSPECCION) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_PROSPECCION);
					if (_statsEfecto.get(123) != null)
						val += (_statsEfecto.get(123) / 10);
					break;
				case 242:
					if (_statsEfecto.get(CentroInfo.STATS_REM_R_TIERRA) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_R_TIERRA);
					break;
				case 243:
					if (_statsEfecto.get(CentroInfo.STATS_REM_R_AGUA) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_R_AGUA);
					break;
				case 244:
					if (_statsEfecto.get(CentroInfo.STATS_REM_R_AIRE) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_R_AIRE);
					break;
				case 240:
					if (_statsEfecto.get(CentroInfo.STATS_REM_R_FUEGO) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_R_FUEGO);
					break;
				case 241:
					if (_statsEfecto.get(CentroInfo.STATS_REM_R_NEUTRAL) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_R_NEUTRAL);
					break;
				case 210:
					if (_statsEfecto.get(CentroInfo.STATS_REM_RP_TER) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_RP_TER);
					break;
				case 211:
					if (_statsEfecto.get(CentroInfo.STATS_REM_RP_EAU) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_RP_EAU);
					break;
				case 212:
					if (_statsEfecto.get(CentroInfo.STATS_REM_RP_AIR) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_RP_AIR);
					break;
				case 213:
					if (_statsEfecto.get(CentroInfo.STATS_REM_RP_FEU) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_RP_FEU);
					break;
				case 214:
					if (_statsEfecto.get(CentroInfo.STATS_REM_RP_NEU) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_RP_NEU);
					break;
				case CentroInfo.STATS_ADD_DOMINIO:
					if (_statsEfecto.get(CentroInfo.STATS_ADD_DOMINIO) != null)
						val = _statsEfecto.get(CentroInfo.STATS_ADD_DOMINIO);
					break;
				case 138:
					if (_statsEfecto.get(CentroInfo.STATS_REM_DAÑOS_PORC) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_DAÑOS_PORC);
					break;
				case 178:
					if (_statsEfecto.get(CentroInfo.STATS_REM_CURAS) != null)
						val -= _statsEfecto.get(CentroInfo.STATS_REM_CURAS);
					break;
			}
			return val;
		}
		
		public static Stats acumularStats(Stats s1, Stats s2) {
			TreeMap<Integer, Integer> stats = new TreeMap<Integer, Integer>();
			for (int a = 0; a <= CentroInfo.ID_EFECTO_MAXIMO; a++) {
				if ( (s1._statsEfecto.get(a) == null || s1._statsEfecto.get(a) == 0)
						&& (s2._statsEfecto.get(a) == null || s2._statsEfecto.get(a) == 0))
					continue;
				int som = 0;
				if (s1._statsEfecto.get(a) != null)
					som += s1._statsEfecto.get(a);
				if (s2._statsEfecto.get(a) != null)
					som += s2._statsEfecto.get(a);
				stats.put(a, som);
			}
			return new Stats(stats, false, null);
		}
		
		public Map<Integer, Integer> getStatsComoMap() {
			return _statsEfecto;
		}
		
		public String convertirStatsAString() {
			String str = "";
			for (Entry<Integer, Integer> entry : _statsEfecto.entrySet()) {
				if (str.length() > 0)
					str += ",";
				str += Integer.toHexString(entry.getKey()) + "#" + Integer.toHexString(entry.getValue()) + "#0#0";
			}
			return str;
		}
	}
	
	public Personaje(int id, String nombre, int sexo, int clase, int color1, int color2, int color3, long kamas, int pts,
			int capital, int energia, int nivel, long exp, int talla, int gfxID, byte alineacion, int cuenta,
			Map<Integer, Integer> stats, int mostrarAmigos, byte mostarAlineacion, String canal, short mapa, int celda,
			String inventario, int pdvPorc, String hechizos, String ptoSalvada, String oficios, int xpMontura, int montura,
			int honor, int deshonor, int nivelAlineacion, String zaaps, byte titulo, int esposoId, String tienda, int mercante,
			int ScrollFuerza, int ScrollInteligencia, int ScrollAgilidad, int ScrollSuerte, int ScrollVitalidad,
			int ScrollSabiduria, int restriccionesA, int restriccionesB, int encarnacion) {
		_encarnacion = MundoDofus.getEncarnacion(encarnacion);
		if (_encarnacion != null)
			_idEncarnacion = encarnacion;
		_oficioPublico = false;
		_scrollAgilidad = ScrollAgilidad;
		_scrollFuerza = ScrollFuerza;
		_scrollInteligencia = ScrollInteligencia;
		_scrollSabiduria = ScrollSabiduria;
		_scrollSuerte = ScrollSuerte;
		_scrollVitalidad = ScrollVitalidad;
		_ID = id;
		_nombre = nombre;
		_sexo = sexo;
		_clase = clase;
		_color1 = color1;
		_color2 = color2;
		_color3 = color3;
		_kamas = kamas;
		_puntosHechizo = pts;
		_capital = capital;
		_alineacion = alineacion;
		_honor = honor;
		_deshonor = deshonor;
		_nivelAlineacion = nivelAlineacion;
		_energia = energia;
		_nivel = nivel;
		_experiencia = exp;
		if (montura != -1)
			_montura = MundoDofus.getDragopavoPorID(montura);
		_talla = talla;
		_gfxID = gfxID;
		_xpDonadaMontura = xpMontura;
		_baseStats = new Stats(stats, true, this);
		_cuentaID = cuenta;
		_cuenta = MundoDofus.getCuenta(cuenta);
		_mostrarConeccionAmigos = mostrarAmigos == 1;
		_esposo = esposoId;
		if (getAlineacion() != 0) {
			_mostrarAlas = mostarAlineacion == 1;
		} else {
			_mostrarAlas = false;
		}
		_canales = canal;
		_mapa = MundoDofus.getMapa(mapa);
		_puntoSalvado = ptoSalvada;
		if (_mapa == null) {
			_mapa = MundoDofus.getMapa((short) mapaClase());
			_celda = _mapa.getCelda(311);
		} else if (_mapa != null) {
			_celda = _mapa.getCelda(celda);
			if (_celda == null) {
				_mapa = MundoDofus.getMapa((short) mapaClase());
				_celda = _mapa.getCelda(311);
			}
		}
		_cambiarNombre = false;
		for (String str : zaaps.split(",")) {
			try {
				_zaaps.add(Short.parseShort(str));
			} catch (Exception e) {}
		}
		if (_mapa == null || _celda == null) {
			System.out.println("Mapa o celda invalido del personaje " + _nombre + ", por lo tanto se cierra el server");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {}
			Bustemu.cerrarServer();
			return;
		}
		if (!inventario.equals("")) {
			if (inventario.charAt(inventario.length() - 1) == '|')
				inventario = inventario.substring(0, inventario.length() - 1);
			GestorSQL.CARGAR_OBJETOS(inventario.replace("|", ","));
		}
		for (String item : inventario.split("\\|")) {
			if (item.equals(""))
				continue;
			String[] infos = item.split(":");
			int guid = Integer.parseInt(infos[0]);
			Objeto obj = MundoDofus.getObjeto(guid);
			if (obj == null)
				continue;
			_objetos.put(obj.getID(), obj);
		}
		if (!tienda.equals("")) {
			if (tienda.charAt(tienda.length() - 1) == '|')
				tienda = tienda.substring(0, tienda.length() - 1);
			GestorSQL.CARGAR_OBJETOS(tienda.replace("|", ","));
		}
		for (String item : tienda.split("\\|")) {
			if (item.equals(""))
				continue;
			String[] infos = item.split(":");
			int idObjeto = Integer.parseInt(infos[0]);
			Objeto obj = MundoDofus.getObjeto(idObjeto);
			if (obj == null)
				continue;
			_tienda.add(obj);
		}
		_esMercante = mercante;
		if (_encarnacion != null)
			_PDVMAX = _encarnacion.getPDVMAX();
		else
			_PDVMAX = (nivel - 1) * 5 + (_nivel > 200 ? (_nivel - 200) * (_clase == 11 ? 2 : 1) * 5 : 0)
					+ CentroInfo.getBasePDV(clase) + getTotalStats().getEfecto(125);
		if (pdvPorc > 100)
			_PDV = (_PDVMAX * 100) / 100;
		else
			_PDV = (_PDVMAX * pdvPorc) / 100;
		analizarPosHechizos(hechizos);
		_recuperarVida = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regenerarPuntoAPunto();
			}
		});
		_exPdv = _PDV;
		if (!oficios.equals("")) {
			for (String aJobData : oficios.split(";")) {
				String[] infos = aJobData.split(",");
				try {
					int oficioID = Integer.parseInt(infos[0]);
					long xp = Long.parseLong(infos[1]);
					Oficio oficio = MundoDofus.getOficio(oficioID);
					int pos = aprenderOficio(oficio);
					if (pos == -1)
						continue;
					StatsOficio statsOficio = _statsOficios.get(pos);
					statsOficio.addXP(this, xp);
				} catch (Exception e) {}
			}
		}
		_titulo = titulo;
		_restriccionesA = restriccionesA;
		_restriccionesB = restriccionesB;
		efectuarRestriccionesA();
		efectuarRestriccionesB();
		refrescarSetClase();
		Objeto mascota = getObjPosicion(8);
		if (mascota != null) {
			_mascota = MundoDofus.getMascota(mascota.getID());
		}
	}
	
	// Doble
	public Personaje(int id, String nombre, int sexo, int clase, int color1, int color2, int color3, int nivel, int talla,
			int gfxid, Map<Integer, Integer> stats, Map<Integer, Objeto> objetos, int pdvPorc, byte mostarAlineacion,
			int montura, int nivelAlineacion, byte alineacion) {
		_ID = id;
		_nombre = nombre;
		_sexo = sexo;
		_clase = clase;
		_color1 = color1;
		_color2 = color2;
		_color3 = color3;
		_nivel = nivel;
		_nivelAlineacion = nivelAlineacion;
		_talla = talla;
		_gfxID = gfxid;
		_baseStats = new Stats(stats, true, this);
		_objetos.putAll(objetos);
		if (_encarnacion != null)
			_PDVMAX = _encarnacion.getPDVMAX();
		else
			_PDVMAX = (nivel - 1) * 5 + (_nivel > 200 ? (_nivel - 200) * 5 : 0) + CentroInfo.getBasePDV(clase)
					+ getTotalStats().getEfecto(125);
		_PDV = (_PDVMAX * pdvPorc) / 100;
		_recuperarVida = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regenerarPuntoAPunto();
			}
		});
		_exPdv = _PDV;
		_alineacion = alineacion;
		if (getAlineacion() != 0) {
			_mostrarAlas = mostarAlineacion == 1;
		} else {
			_mostrarAlas = false;
		}
		if (montura != -1)
			_montura = MundoDofus.getDragopavoPorID(montura);
	}
	
	public synchronized static Personaje crearPersonaje(String nombre, int sexo, int clase, int color1, int color2, int color3,
			Cuenta cuenta) {
		String zaaps = "";
		if (Bustemu.USAR_ZAAPS) {
			zaaps = "164,528,844,935,951,1158,1242,1841,2191,3022,3250,4263,4739,5295,6137,6855,6954,7411,8037"
					+ ",8088,8125,8163,8437,8785,9454,10297,10304,10317,10349,10643,11170,11210";
		}
		long kamas = 0;
		String objetos = "";
		int nivel = Bustemu.INICIAR_NIVEL;
		if (cuenta.getPrimeraVez() != 0) {
			Objeto obj = MundoDofus.getObjModelo(1737).crearObjDesdeModelo(20, false);
			Objeto obj2 = MundoDofus.getObjModelo(580).crearObjDesdeModelo(10, false);
			Objeto obj3 = MundoDofus.getObjModelo(548).crearObjDesdeModelo(10, false);
			Objeto obj4 = MundoDofus.getObjModelo(8423).crearObjDesdeModelo(1, false);
			obj.setPosicion(35);
			obj2.setPosicion(36);
			obj3.setPosicion(37);
			MundoDofus.addObjeto(obj, true);
			MundoDofus.addObjeto(obj2, true);
			MundoDofus.addObjeto(obj3, true);
			MundoDofus.addObjeto(obj4, true);
			objetos += obj.getID() + "|" + obj2.getID() + "|" + obj3.getID() + "|" + obj4.getID();
			if (cuenta.getPrimeraVez() == 2) {
				Objeto Obj = MundoDofus.getObjModelo(11023).crearObjDesdeModelo(1, false);
				Objeto Obj2 = MundoDofus.getObjModelo(11017).crearObjDesdeModelo(3, false);
				Objeto Obj3 = MundoDofus.getObjModelo(9233).crearObjDesdeModelo(1, false);
				Objeto Obj4 = MundoDofus.getObjModelo(9234).crearObjDesdeModelo(1, false);
				Objeto Obj5 = MundoDofus.getObjModelo(10275).crearObjDesdeModelo(10, false);
				MundoDofus.addObjeto(Obj, true);
				MundoDofus.addObjeto(Obj2, true);
				MundoDofus.addObjeto(Obj3, true);
				MundoDofus.addObjeto(Obj4, true);
				MundoDofus.addObjeto(Obj5, true);
				objetos += Obj.getID() + "|" + Obj2.getID() + "|" + Obj3.getID() + "|" + Obj4.getID() + "|" + Obj5.getID() + "|";
				nivel = 101;
			}
			kamas = Bustemu.INICIAR_KAMAS;
			cuenta.setPrimeraVez(0);
			GestorSQL.ACTUALIZAR_PRIMERA_VEZ(cuenta);
		}
		Personaje nuevoPersonaje = new Personaje(GestorSQL.getSigIDPersonaje(), nombre, sexo, clase, color1, color2, color3,
				kamas, ( (nivel - 1) * 1), ( (nivel - 1) * 5), 10000, nivel, MundoDofus.getExpMinPersonaje(nivel), 100,
				Integer.parseInt(clase + "" + sexo), (byte) 0, cuenta.getID(), new TreeMap<Integer, Integer>(), 1, (byte) 0,
				"*#%!pi$:?", (short) CentroInfo.getMapaInicio(clase), 314, objetos, 100, "", "7411,311", "", 0, -1, 0, 0, 0,
				zaaps, (byte) 0, 0, "", 0, 0, 0, 0, 0, 0, 0, 8192, 0, -1);
		nuevoPersonaje._hechizos = CentroInfo.getHechizosIniciales(clase);
		for (int a = 1; a <= nuevoPersonaje.getNivel(); a++) {
			CentroInfo.subirNivelAprenderHechizos(nuevoPersonaje, a);
		}
		nuevoPersonaje._lugaresHechizos = CentroInfo.getLugaresHechizosIniciales(clase);
		if (!GestorSQL.AGREGAR_PJ_EN_BD(nuevoPersonaje, objetos))
			return null;
		MundoDofus.addPersonaje(nuevoPersonaje);
		nuevoPersonaje.setEncarnacion(null);
		return nuevoPersonaje;
	}
	
	public void Conectarse() {
		if (_cuenta.getEntradaPersonaje() == null)
			return;
		PrintWriter out = _cuenta.getEntradaPersonaje().getOut();
		_cuenta.setTempPerso(this);
		_enLinea = true;
		if (_esMercante == 1) {
			_mapa.removerMercante(_ID);
			_esMercante = 0;
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_mapa, _ID);
		}
		if (_montura != null)
			GestorSalida.ENVIAR_Re_DETALLES_MONTURA(this, "+", _montura);
		GestorSalida.ENVIAR_Rx_EXP_DONADA_MONTURA(this);
		GestorSalida.ENVIAR_ASK_PERSONAJE_SELECCIONADO(out, this);
		for (int idSet = 1; idSet < MundoDofus.getNumeroItemsSet(); idSet++) {
			int num = getNroObjEquipadosDeSet(idSet);
			if (num == 0)
				continue;
			GestorSalida.ENVIAR_OS_BONUS_SET(this, idSet, num);
		}
		if (_statsOficios.size() > 0) {
			ArrayList<StatsOficio> listaStatOficios = new ArrayList<StatsOficio>();
			listaStatOficios.addAll(_statsOficios.values());
			GestorSalida.ENVIAR_JS_TRABAJO_POR_OFICIO(this, listaStatOficios);
			GestorSalida.ENVIAR_JX_EXPERINENCIA_OFICIO(this, listaStatOficios);
			GestorSalida.ENVIAR_JO_OFICIO_OPCIONES(this, listaStatOficios);
			Objeto obj = getObjPosicion(1);
			if (obj != null) {
				for (StatsOficio statOficio : listaStatOficios) {
					Oficio oficio = statOficio.getOficio();
					if (oficio.herramientaValida(obj.getModelo().getID())) {
						GestorSalida.ENVIAR_OT_OBJETO_HERRAMIENTA(out, oficio.getID());
						String strOficioPub = CentroInfo.trabajosOficioTaller(oficio.getID());
						if (_mapa.esTaller() && _oficioPublico)
							GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(out, "+", _ID, strOficioPub);
						_stringOficiosPublicos = strOficioPub;
					}
				}
			}
		}
		GestorSalida.ENVIAR_ZS_ENVIAR_ALINEACION(out, _alineacion);
		GestorSalida.ENVIAR_cC_ACTIVAR_CANALES(out, _canales + "^" + (_cuenta.getRango() > 1 ? "@¤" : ""));
		if (_miembroGremio != null)
			GestorSalida.GAME_SEND_gS_PACKET(this, _miembroGremio);
		GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
		GestorSalida.GAME_SEND_EMOTE_LIST(this, _emotes, "0");
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
		GestorSalida.ENVIAR_FO_MOSTRAR_CONEXION_AMIGOS(out, _mostrarConeccionAmigos);
		_cuenta.mensajeAAmigos();
		GestorSalida.ENVIAR_Im_INFORMACION(this, "189");
		if (!_cuenta.getUltimaConeccion().equals("") && !_cuenta.getUltimoIP().equals(""))
			GestorSalida.ENVIAR_Im_INFORMACION(this, "0152;" + _cuenta.getUltimaConeccion() + "~" + _cuenta.getUltimoIP());
		GestorSalida.ENVIAR_Im_INFORMACION(this, "0153;" + _cuenta.getActualIP());
		_cuenta.setUltimoIP(_cuenta.getActualIP());
		Date fechaActual = new Date();
		DateFormat dateFormat = new SimpleDateFormat("dd");
		String dia = dateFormat.format(fechaActual);
		dateFormat = new SimpleDateFormat("MM");
		String mes = dateFormat.format(fechaActual);
		dateFormat = new SimpleDateFormat("yyyy");
		String año = dateFormat.format(fechaActual);
		dateFormat = new SimpleDateFormat("HH");
		String hora = dateFormat.format(fechaActual);
		dateFormat = new SimpleDateFormat("mm");
		String min = dateFormat.format(fechaActual);
		_cuenta.setUltimaConeccion(año + "~" + mes + "~" + dia + "~" + hora + "~" + min);
		if (_miembroGremio != null)
			_miembroGremio.setUltConeccion(año + "~" + mes + "~" + dia + "~" + hora + "~" + min);
		GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, Bustemu.MENSAJE_BIENVENIDA_1);
		GestorSalida.ENVIAR_al_ESTADO_ZONA_ALINEACION(out);
		GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(out, Long.toString(getRestriccionesA(), 36));
		GestorSQL.SET_ONLINE(_cuentaID);
		_recuperarVida.start();
		if (_pelea != null) {
			_reconectado = true;
			return;
		}
		GestorSalida.ENVIAR_ILS_TIEMPO_REGENERAR_VIDA(this, 1000);
		ArrayList<Integer> array = new ArrayList<Integer>();
		array.addAll(_objetos.keySet());
		for (Integer idObj : array) {
			Mascota masc = null;
			if ( (masc = MundoDofus.getMascota(idObj)) != null) {
				if (masc.getPDV() < 1)
					continue;
				if (masc.esDevoraAlmas()) {
					GestorSalida.ENVIAR_Im_INFORMACION(this, "025");
					continue;
				}
				if (masc.entreComidas() > 1440) {
					if (masc.getDelgado())
						restarVidaMascota(masc);
					masc.setCorpulencia(2);
					GestorSalida.ENVIAR_Im_INFORMACION(this, "150");
					continue;
				}
				GestorSalida.ENVIAR_Im_INFORMACION(this, "025");
			}
		}
		mostrarRates();
	}
	
	public void crearJuegoPJ() {
		if (_cuenta.getEntradaPersonaje() == null)
			return;
		PrintWriter out = _cuenta.getEntradaPersonaje().getOut();
		GestorSalida.ENVIAR_GCK_CREAR_PANTALLA_PJ(out);// GCK|1|
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);// As xxx
		GestorSalida.ENVIAR_GDM_CAMBIO_DE_MAPA(out, _mapa.getID(), _mapa.getFecha(), _mapa.getCodigo());// GDM|id|fecha|key
		if (_pelea != null)
			return;
		GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(out, _mapa);// fC peleas mapa
		_mapa.addJugador(this);
	}
	
	public void regenerarPuntoAPunto() {
		if (_mapa == null || _pelea != null || _PDV == _PDVMAX)
			return;
		_PDV++;
	}
	
	public void setHechizos(Map<Integer, StatsHechizos> hechizos) {
		_hechizos.clear();
		_lugaresHechizos.clear();
		_hechizos = hechizos;
		_lugaresHechizos = CentroInfo.getLugaresHechizosIniciales(_clase);
	}
	
	public void cambiarSexo() {
		if (_sexo == 1)
			_sexo = 0;
		else
			_sexo = 1;
	}
	
	public void setPtosHechizos(int puntos) {
		_puntosHechizo = puntos;
	}
	
	public boolean enLinea() {
		return _enLinea;
	}
	
	public void setGrupo(Grupo grupo) {
		_grupo = grupo;
	}
	
	public Grupo getGrupo() {
		return _grupo;
	}
	
	public boolean aprenderHechizo(int hechizoID, int nivel, boolean conectando, boolean enviar) {
		if (_encarnacion != null && !conectando)
			return false;
		Hechizo aprender = MundoDofus.getHechizo(hechizoID);
		if (aprender == null || aprender.getStatsPorNivel(nivel) == null) {
			System.out.println("[ERROR]Hechizo " + hechizoID + " nivel " + nivel + " no ubicado.");
			return false;
		}
		_hechizos.remove(hechizoID);
		_hechizos.put(hechizoID, aprender.getStatsPorNivel(nivel));
		if (enviar) {
			GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
			GestorSalida.ENVIAR_Im_INFORMACION(this, "03;" + hechizoID);
		}
		return true;
	}
	
	public String analizarHechizosABD() {
		String hechizos = "";
		if (_hechizos.isEmpty())
			return "";
		for (int key : _hechizos.keySet()) {
			StatsHechizos SH = _hechizos.get(key);
			hechizos += SH.getHechizoID() + ";" + SH.getNivel() + ";";
			if (_lugaresHechizos.get(key) != null)
				hechizos += _lugaresHechizos.get(key);
			else
				hechizos += "_";
			hechizos += ",";
		}
		hechizos = hechizos.substring(0, hechizos.length() - 1);
		return hechizos;
	}
	
	private void analizarPosHechizos(String str) {
		String[] hechizos = str.split(",");
		for (String e : hechizos) {
			try {
				int id = Integer.parseInt(e.split(";")[0]);
				int nivel = Integer.parseInt(e.split(";")[1]);
				char pos = e.split(";")[2].charAt(0);
				aprenderHechizo(id, nivel, true, false);
				_lugaresHechizos.put(id, pos);
			} catch (NumberFormatException e1) {
				continue;
			}
		}
	}
	
	public String getPtoSalvada() {
		return _puntoSalvado;
	}
	
	public void setOficioPublico(boolean publico) {
		_oficioPublico = publico;
	}
	
	public void setStrOficiosPublicos(String oficios) {
		_stringOficiosPublicos = oficios;
	}
	
	public boolean getOficioPublico() {
		return _oficioPublico;
	}
	
	public String getStringOficiosPublicos() {
		return _stringOficiosPublicos;
	}
	
	public int mapaClase() {
		int mapa = 8570;
		switch (_clase) {
			case 1:// feca
				mapa = 7398;
				break;
			case 2:// osamodas
				mapa = 7545;
				break;
			case 3:// anutrof
				mapa = 7442;
				break;
			case 4:// sram
				mapa = 7392;
				break;
			case 5:// xelor
				mapa = 7332;
				break;
			case 6:// zurcarak
				mapa = 7446;
				break;
			case 7:// aniripsa
				mapa = 7361;
				break;
			case 8:// yopuka
				mapa = 7427;
				break;
			case 9:// ocra
				mapa = 7378;
				break;
			case 10:// sadida
				mapa = 7395;
				break;
			case 11:// sacrogito
				mapa = 7336;
				break;
			case 12:// pandawa
				mapa = 8035;
				break;
			default:
				mapa = 7411;
				break;
		}
		return mapa;
	}
	
	public void setSalvarZaap(String savePos) {
		_puntoSalvado = savePos;
	}
	
	public int getIntercambiandoCon() {
		return _intercambioCon;
	}
	
	public void setIntercambiandoCon(int intercambiando) {
		_intercambioCon = intercambiando;
	}
	
	public int getConversandoCon() {
		return _conversandoCon;
	}
	
	public void setConversandoCon(int conversando) {
		_conversandoCon = conversando;
	}
	
	public long getKamas() {
		return _kamas;
	}
	
	public void setKamas(long l) {
		if (l < 0)
			l = 0;
		_kamas = l;
	}
	
	public Cuenta getCuenta() {
		return _cuenta;
	}
	
	public int getPuntosHechizos() {
		return _puntosHechizo;
	}
	
	public Gremio getGremio() {
		if (_miembroGremio == null)
			return null;
		return _miembroGremio.getGremio();
	}
	
	public void setMiembroGremio(MiembroGremio gremio) {
		_miembroGremio = gremio;
	}
	
	public boolean estaListo() {
		return _listo;
	}
	
	public void setListo(boolean listo) {
		_listo = listo;
	}
	
	public int getDueloID() {
		return _dueloID;
	}
	
	public Pelea getPelea() {
		return _pelea;
	}
	
	public void setDueloID(int dueloID) {
		_dueloID = dueloID;
	}
	
	public int getEnergia() {
		return _energia;
	}
	
	public boolean mostrarConeccionAmigo() {
		return _mostrarConeccionAmigos;
	}
	
	public boolean mostrarAlas() {
		return _mostrarAlas;
	}
	
	public String getCanal() {
		return _canales;
	}
	
	public boolean esTumba() {
		return _esTumba;
	}
	
	public void setRestriccionesA(int restr) {
		_restriccionesA = restr;
		efectuarRestriccionesA();
		GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(_cuenta.getEntradaPersonaje().getOut(),
				Integer.toString(_restriccionesA, 36));
	}
	
	public void setRestriccionesB(int restr) {
		_restriccionesB = restr;
		efectuarRestriccionesB();
	}
	
	public void convertirTumba() {
		try {
			_gfxID = _clase * 10 + 3;
			_esFantasma = false;
			_esTumba = true;
			_esFantasma = false;
			_puedeAgredir = false;
			_puedeSerAgredido = false;
			_puedeSerDesafiado = false;
			_puedeHacerIntercambio = false;
			_puedeIntercambiar = false;
			_puedeHablarNPC = false;
			_puedeMercante = false;
			_puedeInteractuarRecaudador = false;
			_puedeInteractuarPrisma = false;
			_puedeUsarObjetos = false;
			_forzadoCaminar = true;
			_esLento = true;
			_ocupado = true;
			_puedeAtacarAMutante = false;
			_puedeDesafiar = false;
			_puedeSerAtacado = false;
			_puedeInteractuarObjetos = false;
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_mapa, this);
			GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(_cuenta.getEntradaPersonaje().getOut(),
					Integer.toString(getRestriccionesA(), 36));
			GestorSalida.ENVIAR_M1_MENSAJE_SERVER(this, "12", "", "");
		} catch (Exception e) {}
	}
	
	public void agregarEnergia(int energia) {
		int exEnergia = _energia;
		_energia += energia;
		if (_energia > 10000)
			_energia = 10000;
		if (_esFantasma && exEnergia <= 0 && _energia > 0) {
			if (_encarnacion != null)
				_gfxID = _encarnacion.getGfx();
			else
				deformar();
			_energia = energia;
			_esTumba = false;
			_esFantasma = false;
			_puedeAgredir = true;
			_puedeSerAgredido = true;
			_puedeSerDesafiado = true;
			_puedeHacerIntercambio = true;
			_puedeIntercambiar = true;
			_puedeHablarNPC = true;
			_puedeMercante = true;
			_puedeInteractuarRecaudador = true;
			_puedeInteractuarPrisma = true;
			_puedeUsarObjetos = true;
			_esLento = false;
			_ocupado = false;
			_forzadoCaminar = false;
			_puedeAtacarAMutante = true;
			_puedeDesafiar = true;
			_puedeSerAtacado = true;
			_puedeInteractuarObjetos = true;
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_mapa, this);
			GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(_cuenta.getEntradaPersonaje().getOut(),
					Long.toString(getRestriccionesA(), 36));
		}
	}
	
	public void restarEnergia(int energia) {
		_energia -= energia;
		if (_energia <= 0) {
			convertirTumba();
		} else if (_energia < 1500) {
			GestorSalida.ENVIAR_M1_MENSAJE_SERVER(this, "11", energia + "", "");
		}
	}
	
	public void setFantasma() {
		_gfxID = 8004;
		_esTumba = false;
		_esFantasma = true;
		_puedeAgredir = false;
		_puedeSerAgredido = false;
		_puedeSerDesafiado = false;
		_puedeHacerIntercambio = false;
		_puedeIntercambiar = false;
		_puedeHablarNPC = false;
		_puedeMercante = false;
		_puedeInteractuarRecaudador = false;
		_puedeInteractuarPrisma = false;
		_puedeUsarObjetos = false;
		_forzadoCaminar = true;
		_esLento = true;
		_ocupado = true;
		_puedeAtacarAMutante = false;
		_puedeDesafiar = false;
		_puedeSerAtacado = false;
		_puedeInteractuarObjetos = false;
		GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_mapa, this);
		GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(_cuenta.getEntradaPersonaje().getOut(),
				Long.toString(getRestriccionesA(), 36));
		teleport((short) 1188, 297);
		GestorSalida.ENVIAR_M1_MENSAJE_SERVER(this, "15", "", "");
	}
	
	public void setRevivir() {
		if (_encarnacion != null)
			_gfxID = _encarnacion.getGfx();
		else
			deformar();
		_energia = 1000;
		_esTumba = false;
		_esFantasma = false;
		_puedeAgredir = true;
		_puedeSerAgredido = true;
		_puedeSerDesafiado = true;
		_puedeHacerIntercambio = true;
		_puedeIntercambiar = true;
		_puedeHablarNPC = true;
		_puedeMercante = true;
		_puedeInteractuarRecaudador = true;
		_puedeInteractuarPrisma = true;
		_puedeUsarObjetos = true;
		_esLento = false;
		_ocupado = false;
		_forzadoCaminar = false;
		_puedeAtacarAMutante = true;
		_puedeDesafiar = true;
		_puedeSerAtacado = true;
		_puedeInteractuarObjetos = true;
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_mapa, this);
		GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(_cuenta.getEntradaPersonaje().getOut(),
				Long.toString(getRestriccionesA(), 36));
	}
	
	public int getNivel() {
		return _nivel;
	}
	
	public void setNivel(int nivel) {
		_nivel = nivel;
	}
	
	public long getExperiencia() {
		return _experiencia;
	}
	
	public Celda getCelda() {
		return _celda;
	}
	
	public void setCelda(Celda celda) {
		_celda.removerPersonaje(_ID);
		_celda = celda;
		celda.addPersonaje(this);
	}
	
	public int getTalla() {
		return _talla;
	}
	
	public void setTalla(int talla) {
		_talla = talla;
	}
	
	public void setPelea(Pelea pelea) {
		_pelea = pelea;
		if (pelea == null)
			return;
		if (_montando && _montura != null) {
			_montura.energiaPerdida(20);
		}
		try {
			if (pelea.getTipoPelea() > 0) {
				for (int i = 20; i < 22; i++) {
					Objeto obj = getObjPosicion(i);
					if (obj == null)
						continue;
					int idObj = obj.getID();
					String stats = obj.convertirStatsAString();
					String[] arg = stats.split(",");
					obj.clearTodo();
					for (String efec : arg) {
						String[] val = efec.split("#");
						if (Integer.parseInt(val[0], 16) == 811) {
							int turnos = Integer.parseInt(val[3], 16);
							if (turnos == 0) {
								borrarObjetoRemove(idObj);
								MundoDofus.eliminarObjeto(idObj);
								GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, idObj);
								if (i == 21) {
									_bendEfecto = 0;
									_bendHechizo = 0;
									_bendModif = 0;
								}
								Thread.sleep(200);
							} else {
								String antiguo = "32b#0#0#" + Integer.toString(turnos, 16);
								String nuevo = "32b#0#0#" + Integer.toString(turnos - 1, 16);
								stats = stats.replace(antiguo, nuevo);
								obj.convertirStringAStats(stats);
								GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(this, obj);
								Thread.sleep(200);
							}
						}
					}
				}
			}
		} catch (InterruptedException e) {}
	}
	
	public int getGfxID() {
		return _gfxID;
	}
	
	public void setGfxID(int gfxid) {
		_gfxID = gfxid;
	}
	
	public void deformar() {
		_gfxID = _clase * 10 + _sexo;
	}
	
	public int getID() {
		return _ID;
	}
	
	public Mapa getMapa() {
		return _mapa;
	}
	
	public String getNombre() {
		return _nombre;
	}
	
	public boolean estaOcupado() {
		return _ocupado;
	}
	
	public void setOcupado(boolean ocupado) {
		_ocupado = ocupado;
	}
	
	public boolean estaSentado() {
		return _sentado;
	}
	
	public int getSexo() {
		return _sexo;
	}
	
	public int getClase(boolean original) {
		if (_encarnacion != null && !original)
			return _encarnacion.getClase();
		return _clase;
	}
	
	public void setClase(int clase) {
		_clase = clase;
	}
	
	public void setExperiencia(long exp) {
		_experiencia = exp;
	}
	
	public int getColor1() {
		return _color1;
	}
	
	public int getColor2() {
		return _color2;
	}
	
	public Stats getBaseStats() {
		return _baseStats;
	}
	
	public int getColor3() {
		return _color3;
	}
	
	public int getCapital() {
		return _capital;
	}
	
	public void resetearStats() {
		_baseStats.addUnStat(125, -_baseStats.getEfecto(125) + _scrollVitalidad);
		_baseStats.addUnStat(124, -_baseStats.getEfecto(124) + _scrollSabiduria);
		_baseStats.addUnStat(118, -_baseStats.getEfecto(118) + _scrollFuerza);
		_baseStats.addUnStat(123, -_baseStats.getEfecto(123) + _scrollSuerte);
		_baseStats.addUnStat(119, -_baseStats.getEfecto(119) + _scrollAgilidad);
		_baseStats.addUnStat(126, -_baseStats.getEfecto(126) + _scrollInteligencia);
	}
	
	public boolean tieneHechizoID(int hechizo) {
		if (_encarnacion != null)
			return _encarnacion.tieneHechizoID(hechizo);
		return _hechizos.get(hechizo) != null;
	}
	
	public boolean boostearHechizo(int hechizoID) {
		if (_encarnacion != null || _hechizos.get(hechizoID) == null) {
			return false;
		}
		int antNivel = _hechizos.get(hechizoID).getNivel();
		if (antNivel == 6)
			return false;
		if (_puntosHechizo >= antNivel && MundoDofus.getHechizo(hechizoID).getStatsPorNivel(antNivel + 1).getReqNivel() <= _nivel) {
			if (aprenderHechizo(hechizoID, antNivel + 1, false, false)) {
				_puntosHechizo -= antNivel;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean olvidarHechizo(int hechizoID) {
		if (_encarnacion != null || _hechizos.get(hechizoID) == null) {
			return false;
		}
		int antNivel = _hechizos.get(hechizoID).getNivel();
		if (antNivel <= 1)
			return false;
		if (aprenderHechizo(hechizoID, 1, false, false)) {
			_puntosHechizo += Formulas.costeHechizo(antNivel);
			GestorSQL.SALVAR_PERSONAJE(this, false);
			return true;
		} else {
			return false;
		}
	}
	
	public String stringListaHechizos() {
		if (_encarnacion != null) {
			return _encarnacion.stringListaHechizos();
		}
		String str = "";
		for (StatsHechizos SH : _hechizos.values()) {
			if (_lugaresHechizos.get(SH.getHechizoID()) == null)
				str += SH.getHechizoID() + "~" + SH.getNivel() + "~_;";
			else
				str += SH.getHechizoID() + "~" + SH.getNivel() + "~" + _lugaresHechizos.get(SH.getHechizoID()) + ";";
		}
		return str;
	}
	
	public void setPosHechizo(int hechizo, char pos, boolean salvar) {
		if (_encarnacion != null) {
			_encarnacion.setPosHechizo(hechizo, pos);
			return;
		}
		reemplazarHechizoEnPos(pos);
		_lugaresHechizos.remove(hechizo);
		_lugaresHechizos.put(hechizo, pos);
		if (salvar)
			GestorSQL.SALVAR_PERSONAJE(this, false);
	}
	
	private void reemplazarHechizoEnPos(char pos) {
		for (int key : _hechizos.keySet()) {
			if (_lugaresHechizos.get(key) != null) {
				if (_lugaresHechizos.get(key).equals(pos)) {
					_lugaresHechizos.remove(key);
				}
			}
		}
	}
	
	public StatsHechizos getStatsHechizo(int hechizoID) {
		if (_encarnacion != null) {
			return _encarnacion.getStatsHechizo(hechizoID);
		}
		return _hechizos.get(hechizoID);
	}
	
	public String stringParaListaPJsServer() {
		String str = "|";
		str += _ID + ";";
		str += _nombre + ";";
		str += _nivel + ";";
		str += _gfxID + ";";
		str += (_color1 != -1 ? Integer.toHexString(_color1) : "-1") + ";";
		str += (_color2 != -1 ? Integer.toHexString(_color2) : "-1") + ";";
		str += (_color3 != -1 ? Integer.toHexString(_color3) : "-1") + ";";
		str += getStringAccesorios() + ";";
		str += _esMercante + ";";// mercante
		str += "1;";
		str += ";";
		str += ";";
		return str;
	}
	
	public void eliminarPersonaje() {
		if (getMiembroGremio() != null)
			GestorSQL.BORRAR_MIEMBRO_GREMIO(_ID);
		GestorSQL.BORRAR_PERSONAJE(this);
		_cuenta.borrarPerso(_ID);
		if (MundoDofus.estaRankingPVP(_ID)) {
			GestorSQL.BORRAR_RANKINGPVP(_ID);
			MundoDofus.delRankingPVP(_ID);
		}
	}
	
	public void mostrarAmigosEnLinea(boolean mostrar) {
		_mostrarConeccionAmigos = mostrar;
	}
	
	public void mostrarRates() {
		GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this, "<b>BIENVENIDO A " + Bustemu.NOMBRE_SERVER + ": \n\nEVENTO : "
				+ Bustemu.EVENTO + "\nKAMAS por : " + Bustemu.RATE_KAMAS + "   \nDROP por : " + Bustemu.RATE_DROP
				+ "\nXP PVM por : " + Bustemu.RATE_XP_PVM + "   \nXP PVP por : " + Bustemu.RATE_XP_PVP + "\nXP OFICIO por : "
				+ Bustemu.RATE_XP_OFICIO + "  \nEXITO FORJAMAGIA por : " + Bustemu.RATE_PORC_FM + "\nCRIANZA DE PAVOS por : "
				+ Bustemu.RATE_CRIANZA_PAVOS + "\nTIEMPO PARIR MONTURA por : " + Bustemu.RATE_TIEMPO_PARIR + " minutos"
				+ " \nLAS MASCOTAS SE ALIMENTARAN Cada " + Bustemu.RATE_TIEMPO_ALIMENTACION + " minutos</b>");
	}
	
	public String analizarFiguraDelPJ() {
		String packetOa = "Oa";
		packetOa += _ID + "|";
		packetOa += getStringAccesorios();
		return packetOa;
	}
	
	public String stringGMmercante() {
		String str = "";
		str += _celda.getID() + ";";// 0
		str += "1;";// 1
		str += "0" + ";";// 2
		str += _ID + ";";// 3
		str += _nombre + ";";// 4
		str += "-5";// 5, hay q poner -5
		str += (getTitulo() > 0 ? ("," + getTitulo() + ";") : (";"));// 5
		str += _gfxID + "^" + _talla + ";";// gfxID^size//6
		str += (_color1 == -1 ? "-1" : Integer.toHexString(_color1)) + ";";// 7
		str += (_color2 == -1 ? "-1" : Integer.toHexString(_color2)) + ";";// 8
		str += (_color3 == -1 ? "-1" : Integer.toHexString(_color3)) + ";";// 9
		str += getStringAccesorios() + ";";// 10,accesorios
		if (_miembroGremio != null && _miembroGremio.getGremio().getPjMiembros().size() > 9) {
			str += _miembroGremio.getGremio().getNombre() + ";"// 11
					+ _miembroGremio.getGremio().getEmblema() + ";";// 12
		} else
			str += ";;";// 11,12
		str += "0";// tipo de gift de mercante, comunmente 0 = todos, 1= armas, 2= pocimas, 3 = recursos
		return str;
	}
	
	public String stringGM() {
		String str = "";
		if (_pelea != null)
			return str;
		str += _celda.getID() + ";";// 0
		str += _orientacion + ";";// 1
		str += "0" + ";";// 2
		str += _ID + ";";// 3
		str += _nombre + ";";// 4
		str += _clase;// 5
		str += (_titulo > 0 ? ("," + _titulo + ";") : (";"));// 5
		str += _gfxID + "^" + _talla + ";";// gfxID^size 6
		str += _sexo + ";";// 7
		str += _alineacion + ",";// 8
		str += getNivelAlineacion() + ","; // 8
		str += (_mostrarAlas ? getNivelAlineacion() : "0") + ",";// 8
		str += _nivel + ",";// 8
		str += (_deshonor > 0 ? 1 : 0) + ";"; // 8
		str += (_color1 == -1 ? "-1" : Integer.toHexString(_color1)) + ";";// 9
		str += (_color2 == -1 ? "-1" : Integer.toHexString(_color2)) + ";";// 10
		str += (_color3 == -1 ? "-1" : Integer.toHexString(_color3)) + ";";// 11
		str += getStringAccesorios() + ";";// 12,accesorios
		if (Bustemu.AURA_ACTIVADA) {
			str += (_nivel > 99 ? (_nivel > 199 ? (getNroObjEquipadosDeSet(130) == 6 ? (3) : (2)) : (1)) : (0)) + ";";// 13
		} else {
			str += "0;";// 13
		}
		str += ";";// Emote 14
		str += ";";// Emote timer 15
		if (_miembroGremio != null && _miembroGremio.getGremio().getPjMiembros().size() > 9) {
			str += _miembroGremio.getGremio().getNombre() + ";"// 16
					+ _miembroGremio.getGremio().getEmblema() + ";";// 17
		} else
			str += ";;";// 16,17 miembro vacio
		str += Integer.toString(getRestriccionesB(), 36) + ";";// 18= RESTRICCIONES getRestriccionesB()
		str += (_montando && _montura != null ? _montura.getStringColor(stringColorDueñoPavo()) : "") + ";"; // 19
		str += ";";// 20
		return str;
	}
	
	public String getStringAccesorios() {// mostrar accesorios
		String str = "";
		str += getModeloObjEnPos(1) + ",";// arma
		str += getModeloObjEnPos(6) + ",";// sombrero
		str += getModeloObjEnPos(7) + ",";// capa
		str += getModeloObjEnPos(8) + ",";// mascota
		str += getModeloObjEnPos(CentroInfo.ITEM_POS_ESCUDO);// escudo
		return str;
	}
	
	public String stringStatsPacket() {
		Stats objEquipStats = getStatsObjEquipados();
		Stats totalStats = getTotalStats();
		Stats boostStats = getStatsBoost();
		Stats benMaldStats = getStatsBendMald();
		refrescarVida();
		String str = "As";
		str += xpString(",") + "|";
		str += _kamas + "|";
		if (_encarnacion != null)
			str += "0|0|";
		else
			str += _capital + "|" + _puntosHechizo + "|";
		str += _alineacion + "~" + _alineacion + "," + _nivelAlineacion + "," + getNivelAlineacion() + "," + _honor + ","
				+ _deshonor + "," + (_mostrarAlas ? "1" : "0") + "|";
		int pdv = getPDV();
		int pdvMax = getPDVMAX();
		if (_pelea != null) {
			Luchador f = _pelea.getLuchadorPorPJ(this);
			if (f != null) {
				pdv = f.getPDVConBuff();
				pdvMax = f.getPDVMaxConBuff();
			}
		}
		str += pdv + "," + pdvMax + "|";
		str += _energia + ",10000|";
		str += getIniciativa() + "|";
		// prospeccion
		str += totalStats.getEfecto(176) + "|";
		// PA
		str += _baseStats.getEfecto(111) + "," + objEquipStats.getEfecto(111) + "," + benMaldStats.getEfecto(111) + ","
				+ boostStats.getEfecto(111) + "," + totalStats.getEfecto(111) + "|";
		// PM
		str += _baseStats.getEfecto(128) + "," + objEquipStats.getEfecto(128) + "," + benMaldStats.getEfecto(128) + ","
				+ boostStats.getEfecto(128) + "," + totalStats.getEfecto(128) + "|";
		// fuerza
		str += _baseStats.getEfecto(118) + "," + objEquipStats.getEfecto(118) + "," + benMaldStats.getEfecto(118) + ","
				+ boostStats.getEfecto(118) + "|";
		// vitalidad
		str += _baseStats.getEfecto(125) + "," + objEquipStats.getEfecto(125) + "," + benMaldStats.getEfecto(125) + ","
				+ boostStats.getEfecto(125) + "|";
		// sabiduria
		str += _baseStats.getEfecto(124) + "," + objEquipStats.getEfecto(124) + "," + benMaldStats.getEfecto(124) + ","
				+ boostStats.getEfecto(124) + "|";
		// suerte
		str += _baseStats.getEfecto(123) + "," + objEquipStats.getEfecto(123) + "," + benMaldStats.getEfecto(123) + ","
				+ boostStats.getEfecto(123) + "|";
		// agilidad
		str += _baseStats.getEfecto(119) + "," + objEquipStats.getEfecto(119) + "," + benMaldStats.getEfecto(119) + ","
				+ boostStats.getEfecto(119) + "|";
		// inteligencia
		str += _baseStats.getEfecto(126) + "," + objEquipStats.getEfecto(126) + "," + benMaldStats.getEfecto(126) + ","
				+ boostStats.getEfecto(126) + "|";
		// alcance
		str += _baseStats.getEfecto(117) + "," + objEquipStats.getEfecto(117) + "," + benMaldStats.getEfecto(117) + ","
				+ boostStats.getEfecto(117) + "|";
		// invocaciones
		str += _baseStats.getEfecto(182) + "," + objEquipStats.getEfecto(182) + "," + benMaldStats.getEfecto(182) + ","
				+ boostStats.getEfecto(182) + "|";
		// daños
		str += _baseStats.getEfecto(112) + "," + objEquipStats.getEfecto(112) + "," + benMaldStats.getEfecto(112) + ","
				+ boostStats.getEfecto(112) + "|";
		// daños fisicos
		str += _baseStats.getEfecto(142) + "," + objEquipStats.getEfecto(142) + "," + benMaldStats.getEfecto(142) + ","
				+ boostStats.getEfecto(142) + "|";
		str += "0,0,0,0|";// Dominio
		// %daños
		str += _baseStats.getEfecto(138) + "," + objEquipStats.getEfecto(138) + "," + benMaldStats.getEfecto(138) + ","
				+ boostStats.getEfecto(138) + "|";
		// curas
		str += _baseStats.getEfecto(178) + "," + objEquipStats.getEfecto(178) + "," + benMaldStats.getEfecto(178) + ","
				+ boostStats.getEfecto(178) + "|";
		// daños trampa
		str += _baseStats.getEfecto(225) + "," + objEquipStats.getEfecto(225) + "," + benMaldStats.getEfecto(225) + ","
				+ boostStats.getEfecto(225) + "|";
		// %daños trampa
		str += _baseStats.getEfecto(226) + "," + objEquipStats.getEfecto(226) + "," + benMaldStats.getEfecto(226) + ","
				+ boostStats.getEfecto(226) + "|";
		// reenvia daños
		str += _baseStats.getEfecto(220) + "," + objEquipStats.getEfecto(220) + "," + benMaldStats.getEfecto(220) + ","
				+ boostStats.getEfecto(220) + "|";
		// golpes criticos
		str += _baseStats.getEfecto(115) + "," + objEquipStats.getEfecto(115) + "," + benMaldStats.getEfecto(115) + ","
				+ boostStats.getEfecto(115) + "|";
		// fallos criticos
		str += _baseStats.getEfecto(122) + "," + objEquipStats.getEfecto(122) + "," + benMaldStats.getEfecto(122) + ","
				+ boostStats.getEfecto(122) + "|";
		// %perdidas PA
		str += _baseStats.getEfecto(160) + "," + objEquipStats.getEfecto(160) + "," + 0 + "," + benMaldStats.getEfecto(160) + ","
				+ boostStats.getEfecto(160) + "|";
		// %perdidas PM
		str += _baseStats.getEfecto(161) + "," + objEquipStats.getEfecto(161) + "," + 0 + "," + benMaldStats.getEfecto(161) + ","
				+ boostStats.getEfecto(161) + "|";
		// +Res Neutral
		str += _baseStats.getEfecto(241) + "," + objEquipStats.getEfecto(241) + "," + 0 + "," + benMaldStats.getEfecto(241) + ","
				+ boostStats.getEfecto(241) + "|";
		// %Res Neutral
		str += _baseStats.getEfecto(214) + "," + objEquipStats.getEfecto(214) + "," + 0 + "," + benMaldStats.getEfecto(214) + ","
				+ boostStats.getEfecto(214) + "|";
		// +Res PVP Neutral
		str += _baseStats.getEfecto(264) + "," + objEquipStats.getEfecto(264) + "," + 0 + "," + benMaldStats.getEfecto(264) + ","
				+ boostStats.getEfecto(264) + "|";
		// %Res PVP Neutral
		str += _baseStats.getEfecto(254) + "," + objEquipStats.getEfecto(254) + "," + 0 + "," + benMaldStats.getEfecto(254) + ","
				+ boostStats.getEfecto(254) + "|";
		str += _baseStats.getEfecto(242) + "," + objEquipStats.getEfecto(242) + "," + 0 + "," + benMaldStats.getEfecto(242) + ","
				+ boostStats.getEfecto(242) + "|";
		str += _baseStats.getEfecto(210) + "," + objEquipStats.getEfecto(210) + "," + 0 + "," + benMaldStats.getEfecto(210) + ","
				+ boostStats.getEfecto(210) + "|";
		str += _baseStats.getEfecto(260) + "," + objEquipStats.getEfecto(260) + "," + 0 + "," + benMaldStats.getEfecto(260) + ","
				+ boostStats.getEfecto(260) + "|";
		str += _baseStats.getEfecto(250) + "," + objEquipStats.getEfecto(250) + "," + 0 + "," + benMaldStats.getEfecto(250) + ","
				+ boostStats.getEfecto(250) + "|";
		str += _baseStats.getEfecto(243) + "," + objEquipStats.getEfecto(243) + "," + 0 + "," + benMaldStats.getEfecto(243) + ","
				+ boostStats.getEfecto(243) + "|";
		str += _baseStats.getEfecto(211) + "," + objEquipStats.getEfecto(211) + "," + 0 + "," + benMaldStats.getEfecto(211) + ","
				+ boostStats.getEfecto(211) + "|";
		str += _baseStats.getEfecto(261) + "," + objEquipStats.getEfecto(261) + "," + 0 + "," + benMaldStats.getEfecto(261) + ","
				+ boostStats.getEfecto(261) + "|";
		str += _baseStats.getEfecto(251) + "," + objEquipStats.getEfecto(251) + "," + 0 + "," + benMaldStats.getEfecto(251) + ","
				+ boostStats.getEfecto(251) + "|";
		str += _baseStats.getEfecto(244) + "," + objEquipStats.getEfecto(244) + "," + 0 + "," + benMaldStats.getEfecto(244) + ","
				+ boostStats.getEfecto(244) + "|";
		str += _baseStats.getEfecto(212) + "," + objEquipStats.getEfecto(212) + "," + 0 + "," + benMaldStats.getEfecto(212) + ","
				+ boostStats.getEfecto(212) + "|";
		str += _baseStats.getEfecto(262) + "," + objEquipStats.getEfecto(262) + "," + 0 + "," + benMaldStats.getEfecto(262) + ","
				+ boostStats.getEfecto(262) + "|";
		str += _baseStats.getEfecto(252) + "," + objEquipStats.getEfecto(252) + "," + 0 + "," + benMaldStats.getEfecto(252) + ","
				+ boostStats.getEfecto(252) + "|";
		str += _baseStats.getEfecto(240) + "," + objEquipStats.getEfecto(240) + "," + 0 + "," + benMaldStats.getEfecto(240) + ","
				+ boostStats.getEfecto(240) + "|";
		str += _baseStats.getEfecto(213) + "," + objEquipStats.getEfecto(213) + "," + 0 + "," + benMaldStats.getEfecto(213) + ","
				+ boostStats.getEfecto(213) + "|";
		str += _baseStats.getEfecto(263) + "," + objEquipStats.getEfecto(263) + "," + 0 + "," + benMaldStats.getEfecto(263) + ","
				+ boostStats.getEfecto(263) + "|";
		str += _baseStats.getEfecto(253) + "," + objEquipStats.getEfecto(253) + "," + 0 + "," + benMaldStats.getEfecto(253) + ","
				+ boostStats.getEfecto(253) + "|";
		return str;
	}
	
	public String xpString(String c) {
		return _experiencia + c + MundoDofus.getExpMinPersonaje(_nivel) + c + MundoDofus.getExpMaxPersonaje(_nivel);
	}
	
	public int emoteActivado() {
		return _emoteActivado;
	}
	
	public void setEmoteActivado(int emoteActivado) {
		_emoteActivado = emoteActivado;
	}
	
	private Stats getStatsObjEquipados() {
		Stats stats = new Stats(false, null);
		ArrayList<Integer> listaSetsEquipados = new ArrayList<Integer>();
		Collection<Objeto> objetos = _objetos.values();
		for (Objeto objeto : objetos) {
			if (objeto.getPosicion() != -1
					&& ( (objeto.getPosicion() >= 0 && objeto.getPosicion() <= 15) || (objeto.getPosicion() >= 20 && objeto
							.getPosicion() <= 27))) {
				stats = Stats.acumularStats(stats, objeto.getStats());
				int setID = objeto.getModelo().getSetID();
				if (setID > 0 && !listaSetsEquipados.contains(setID)) {
					listaSetsEquipados.add(setID);
					ItemSet IS = MundoDofus.getItemSet(setID);
					if (IS != null) {
						stats = Stats.acumularStats(stats, IS.getBonusStatPorNroObj(getNroObjEquipadosDeSet(setID)));
					}
				}
			}
		}
		if (_montando && _montura != null) {
			stats = Stats.acumularStats(stats, _montura.getStats());
		}
		return stats;
	}
	
	private Stats getStatsBoost() {
		Stats stats = new Stats(false, null);
		return stats;
	}
	
	private Stats getStatsBendMald() {
		Stats stats = new Stats(false, null);
		return stats;
	}
	
	public Stats getTotalStats() {
		Stats total = new Stats(false, null);
		total = Stats.acumularStats(total, _baseStats);
		total = Stats.acumularStats(total, getStatsObjEquipados());
		total = Stats.acumularStats(total, getStatsBendMald());
		// PA
		// if (_nivel < 300) {
		// if (total.getEfecto(111) > (_nivel > 200 && _nivel <= 250 ? 14 : 13)) {
		// total.especificarStat(111, 14);
		// } else if (total.getEfecto(111) > 15) {
		// total.especificarStat(111, 15);
		// }
		// } else if (total.getEfecto(111) > 16) {
		// total.especificarStat(111, 16);
		// }
		// // PM
		// if (_nivel < 300) {
		// if (total.getEfecto(128) > (_nivel > 200 ? 7 : 6)) {
		// total.especificarStat(128, 7);
		// }
		// } else if (total.getEfecto(128) > 8) {
		// total.especificarStat(128, 8);
		// }
		// // daños
		// if (_nivel < 300) {
		// if (total.getEfecto(112) > (_nivel > 200 ? 75 : 60)) {
		// total.especificarStat(112, 75);
		// }
		// } else if (total.getEfecto(112) > 90) {
		// total.especificarStat(112, 90);
		// }
		return total;
	}
	
	public int getOrientacion() {
		return _orientacion;
	}
	
	public void setOrientacion(int orientacion) {
		_orientacion = orientacion;
	}
	
	public int getIniciativa() {
		Stats objEquipados = getTotalStats();
		int fact = 4;
		int pvmax = _PDVMAX - CentroInfo.getBasePDV(_clase);
		int pv = _PDV - CentroInfo.getBasePDV(_clase);
		if (_clase == 11)
			fact = 8;
		double coef = pvmax / fact;
		coef += getStatsObjEquipados().getEfecto(174);
		coef += objEquipados.getEfecto(119);
		coef += objEquipados.getEfecto(123);
		coef += objEquipados.getEfecto(126);
		coef += objEquipados.getEfecto(118);
		int init = 1;
		if (pvmax != 0)
			init = (int) (coef * ((double) pv / (double) pvmax));
		if (init < 0)
			init = 0;
		return init;
	}
	
	public int getPodUsados() {
		int pod = 0;
		for (Objeto objeto : _objetos.values()) {
			pod += objeto.getModelo().getPeso() * objeto.getCantidad();
		}
		return pod;
	}
	
	public int getMaxPod() {
		int pods = getTotalStats().getEfecto(158);
		pods += (getTotalStats().getEfecto(118) * 5);
		for (StatsOficio SO : _statsOficios.values()) {
			pods += SO.getNivel() * 5;
			if (SO.getNivel() == 100)
				pods += 1000;
		}
		if (pods < 1000)
			pods = 1000;
		return pods;
	}
	
	public int getPDV() {
		return _PDV;
	}
	
	public void setPDV(int pdv) {
		if (pdv > getPDVMAX())
			pdv = getPDVMAX();
		_PDV = pdv;
		actualizarInfoGrupo();
	}
	
	public int getPDVMAX() {
		if (_encarnacion != null)
			return _encarnacion.getPDVMAX();
		return _PDVMAX;
	}
	
	public void setPDVMAX(int pdvmax) {
		_PDVMAX = pdvmax;
		actualizarInfoGrupo();
	}
	
	public void actualizarInfoGrupo() {
		if (_grupo != null) {
			GestorSalida.ENVIAR_PM_ACTUALIZAR_INFO_PJ_GRUPO(_grupo, this);
		}
	}
	
	public void setSentado(boolean sentado) {
		_sentado = sentado;
		int diferencia = _PDV - _exPdv;
		int tiempo = (sentado ? 500 : 1000);
		_exPdv = _PDV;
		if (_enLinea) {
			GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(this, diferencia);
			GestorSalida.ENVIAR_ILS_TIEMPO_REGENERAR_VIDA(this, tiempo);
		}
		_recuperarVida.setDelay(tiempo);
		if ( (_emoteActivado == 1 || _emoteActivado == 19) && sentado == false)
			_emoteActivado = 0;
	}
	
	public void stopRecuperarVida() {
		_recuperarVida.stop();
	}
	
	public byte getAlineacion() {
		return _alineacion;
	}
	
	public int getPorcPDV() {
		int porcPDV = 100;
		porcPDV = (100 * _PDV) / _PDVMAX;
		if (porcPDV > 100)
			return 100;
		return porcPDV;
	}
	
	public void emote(String str) {
		try {
			int id = Integer.parseInt(str);
			Mapa mapa = _mapa;
			if (_pelea == null)
				GestorSalida.ENVIAR_cS_EMOTICON_MAPA(mapa, _ID, id);
			else
				GestorSalida.ENVIAR_cS_EMOTE_EN_PELEA(_pelea, 7, _ID, id);
		} catch (NumberFormatException e) {
			return;
		}
	}
	
	public void retornoMapa() {
		_pelea = null;
		_ocupado = false;
		_listo = false;
		_dueloID = -1;
		_mapa.addJugador(this);
	}
	
	public void retornoMapaDesPeleaRecau() {
		_pelea = null;
		_ocupado = false;
		_listo = false;
		_dueloID = -1;
		try {
			teleport(_tempMapaDefPerco.getID(), _tempCeldaDefPerco.getID());
		} catch (NullPointerException E) {
			teleport(_mapa.getID(), _celda.getID());
		}
		_tempMapaDefPerco = null;
		_tempCeldaDefPerco = null;
	}
	
	public void retornoPtoSalvadaRecau() {
		_pelea = null;
		_ocupado = false;
		_listo = false;
		_dueloID = -1;
		if (_energia > 0) {
			String[] infos = _puntoSalvado.split(",");
			teleport(Short.parseShort(infos[0]), Integer.parseInt(infos[1]));
		}
		_tempMapaDefPerco = null;
		_tempCeldaDefPerco = null;
		try {
			Thread.sleep(1000);
		} catch (Exception e) {}
		GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(this);
	}
	
	public void retornoPtoSalvada() {
		_pelea = null;
		_ocupado = false;
		_listo = false;
		_dueloID = -1;
		if (_energia > 0) {
			String[] infos = _puntoSalvado.split(",");
			teleport(Short.parseShort(infos[0]), Integer.parseInt(infos[1]));
		}
		try {
			Thread.sleep(1000);
		} catch (Exception e) {}
		GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(this);
	}
	
	public void retornoPtoSalvadaPocima() {
		_pelea = null;
		_ocupado = false;
		_listo = false;
		_dueloID = -1;
		try {
			String[] infos = _puntoSalvado.split(",");
			teleport(Short.parseShort(infos[0]), Integer.parseInt(infos[1]));
		} catch (Exception e) {}
	}
	
	public void boostStat(int stat) {
		int value = 0;
		switch (stat) {
			case 10:// Fuerza
				value = _baseStats.getEfecto(118);
				break;
			case 13:// Suerte
				value = _baseStats.getEfecto(123);
				break;
			case 14:// Agilidad
				value = _baseStats.getEfecto(119);
				break;
			case 15:// Inteligencia
				value = _baseStats.getEfecto(126);
				break;
		}
		int cout = CentroInfo.getRepartoPuntoSegunClase(_clase, stat, value);
		if (cout <= _capital) {
			switch (stat) {
				case 11:// Vitalidad
					if (_clase != 11)
						_baseStats.addUnStat(125, 1);
					else
						_baseStats.addUnStat(125, 2);
					break;
				case 12:// Sabiduria
					_baseStats.addUnStat(124, 1);
					break;
				case 10:// Fuerza
					_baseStats.addUnStat(118, 1);
					break;
				case 13:// suerte
					_baseStats.addUnStat(123, 1);
					break;
				case 14:// Agilidad
					_baseStats.addUnStat(119, 1);
					break;
				case 15:// Inteligencia
					_baseStats.addUnStat(126, 1);
					break;
				default:
					return;
			}
			_capital -= cout;
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		}
	}
	
	public void boostStat2(int stat) {
		int value = 0;
		int capital = _capital;
		int cout = 0;
		while (capital >= cout) {
			switch (stat) {
				case 10:// Fuerza
					value = _baseStats.getEfecto(118);
					break;
				case 13:// Suerte
					value = _baseStats.getEfecto(123);
					break;
				case 14:// Agilidad
					value = _baseStats.getEfecto(119);
					break;
				case 15:// Inteligencia
					value = _baseStats.getEfecto(126);
					break;
			}
			cout = CentroInfo.getRepartoPuntoSegunClase(_clase, stat, value);
			if (cout <= _capital) {
				switch (stat) {
					case 11:// Vitalidad
						if (_clase != 11)
							_baseStats.addUnStat(125, 1);
						else
							_baseStats.addUnStat(125, 2);
						break;
					case 12:// Sabiduria
						_baseStats.addUnStat(124, 1);
						break;
					case 10:// Fuerza
						_baseStats.addUnStat(118, 1);
						break;
					case 13:// suerte
						_baseStats.addUnStat(123, 1);
						break;
					case 14:// Agilidad
						_baseStats.addUnStat(119, 1);
						break;
					case 15:// Inteligencia
						_baseStats.addUnStat(126, 1);
						break;
					default:
						return;
				}
				capital -= cout;
			}
		}
		_capital = capital;
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
	}
	
	public boolean estaMuteado() {
		return _cuenta.estaMuteado();
	}
	
	public void setMapa(Mapa mapa) {
		_mapa = mapa;
	}
	
	public String stringObjetosABD() {
		String str = "";
		for (Entry<Integer, Objeto> entry : _objetos.entrySet()) {
			Objeto obj = entry.getValue();
			str += obj.getID() + "|";
		}
		return str;
	}
	
	public Objeto getObjSimilarInventario(Objeto objeto) {
		ObjetoModelo objModelo = objeto.getModelo();
		if (objModelo.getTipo() == 85 || objModelo.getTipo() == 18 || Bustemu.ARMAS_ENCARNACIONES.contains(objModelo.getID()))
			return null;
		for (Entry<Integer, Objeto> entry : _objetos.entrySet()) {
			Objeto obj = entry.getValue();
			if (obj.getPosicion() == -1 && objeto.getID() != obj.getID() && obj.getModelo().getID() == objModelo.getID()
					&& obj.getStats().sonStatsIguales(objeto.getStats())) {
				return obj;
			}
		}
		return null;
	}
	
	public boolean addObjetoSimilar(Objeto objeto, boolean tieneSimilar, int idAntigua) {
		ObjetoModelo objModelo = objeto.getModelo();
		if (objModelo.getTipo() == 85 || objModelo.getTipo() == 18 || Bustemu.ARMAS_ENCARNACIONES.contains(objModelo.getID())) {
			return false;
		}
		if (tieneSimilar) {
			for (Entry<Integer, Objeto> entry : _objetos.entrySet()) {
				Objeto obj = entry.getValue();
				if (obj.getPosicion() == -1 && obj.getID() != idAntigua && obj.getModelo().getID() == objModelo.getID()
						&& obj.getStats().sonStatsIguales(objeto.getStats())) {
					obj.setCantidad(obj.getCantidad() + objeto.getCantidad());
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
					return true;
				}
			}
		}
		return false;
	}
	
	public void addObjetoPut(Objeto objeto) {
		if (objeto == null)
			return;
		_objetos.put(objeto.getID(), objeto);
	}
	
	public Map<Integer, Objeto> getObjetos() {
		return _objetos;
	}
	
	public String stringPersonajeElegido() {
		String str = "";
		Objeto objeto = getObjPosicion(21);
		if (getObjPosicion(21) != null) {
			String stats = objeto.convertirStatsAString();
			String[] arg = stats.split(",");
			for (String efec : arg) {
				String[] val = efec.split("#");
				int efecto = Integer.parseInt(val[0], 16);
				if (efecto >= 281 || efecto <= 292) {
					_bendEfecto = efecto;
					_bendHechizo = Integer.parseInt(val[1], 16);
					_bendModif = Integer.parseInt(val[3], 16);
				}
			}
		}
		for (Objeto obj : _objetos.values())
			str += obj.stringObjetoConGuiño();
		return str;
	}
	
	public String getObjetosBancoPorID(String splitter) {
		String str = "";
		for (int entry : _cuenta.getBanco().keySet())
			str += entry + splitter;
		return str;
	}
	
	public String getObjetosPersonajePorID(String splitter) {
		String str = "";
		for (int entry : _objetos.keySet()) {
			if (str.length() != 0)
				str += splitter;
			str += entry;
		}
		return str;
	}
	
	public boolean tieneObjetoID(int id) {
		return _objetos.get(id) != null ? _objetos.get(id).getCantidad() > 0 : false;
	}
	
	public void venderObjeto(int id, int cant) {
		if (cant <= 0)
			return;
		Objeto objeto = _objetos.get(id);
		ObjetoModelo objModelo = objeto.getModelo();
		int precioUnitario = objModelo.getPrecio();
		int precioVIP = objModelo.getPrecioVIP();
		if (precioUnitario == 0 && precioVIP > 0) {
			int ptosAconseguir = cant * precioVIP;
			int misPuntos = GestorSQL.getPuntosCuenta(_cuentaID);
			GestorSQL.setPuntoCuenta(ptosAconseguir + misPuntos, _cuentaID);
			int nuevaCant = objeto.getCantidad() - cant;
			if (nuevaCant <= 0) {
				_objetos.remove(id);
				MundoDofus.eliminarObjeto(id);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, id);
			} else {
				objeto.setCantidad(nuevaCant);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objeto);
			}
		} else {
			if (objeto.getCantidad() < cant)
				cant = objeto.getCantidad();
			int precio = cant * (precioUnitario / 10);
			int nuevaCant = objeto.getCantidad() - cant;
			if (nuevaCant <= 0) {
				_objetos.remove(id);
				MundoDofus.eliminarObjeto(id);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, id);
			} else {
				objeto.setCantidad(nuevaCant);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objeto);
			}
			_kamas = _kamas + precio;
		}
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
		GestorSalida.ENVIAR_ESK_VENDIDO(this);
	}
	
	public void borrarObjetoRemove(int id) {
		_objetos.remove(id);
	}
	
	public void borrarObjetoEliminar(int idObjeto, int cantidad, boolean borrarMundoDofus) {
		Objeto obj = _objetos.get(idObjeto);
		if (cantidad > obj.getCantidad())
			cantidad = obj.getCantidad();
		if (obj.getCantidad() >= cantidad) {
			int nuevaCant = obj.getCantidad() - cantidad;
			if (nuevaCant > 0) {
				obj.setCantidad(nuevaCant);
				if (_enLinea)
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
			} else {
				_objetos.remove(obj.getID());
				if (borrarMundoDofus)
					MundoDofus.eliminarObjeto(obj.getID());
				if (_enLinea)
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, obj.getID());
			}
		}
	}
	
	public Objeto getObjPosicion(int pos) {
		if (pos == -1)
			return null;
		for (Objeto objeto : _objetos.values()) {
			if (objeto.getPosicion() == pos)
				return objeto;
		}
		return null;
	}
	
	public void refrescarVida() {
		double actPdvPorc = (100 * (double) _PDV) / (double) _PDVMAX;
		if (_encarnacion != null) {
			_PDVMAX = _encarnacion.getPDVMAX();
		} else
			_PDVMAX = (_nivel - 1) * 5 + (_nivel > 200 ? (_nivel - 200) * (_clase == 11 ? 2 : 1) * 5 : 0)
					+ CentroInfo.getBasePDV(_clase) + getTotalStats().getEfecto(125);
		_PDV = (int) Math.round(_PDVMAX * actPdvPorc / 100);
	}
	
	public void subirNivel(boolean enviar, boolean addXp) {
		if (_nivel == Bustemu.MAX_NIVEL || _encarnacion != null)
			return;
		_nivel++;
		if (_nivel == Bustemu.MAX_NIVEL)
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("Felicitaciones al jugador <b>" + _nombre
					+ "</b> por haber alcanzado el máximo nivel de este servidor. NIVEL 300");
		_capital += 5;
		_PDVMAX += 5;
		if (_nivel > 200)
			_PDVMAX += 5;
		_puntosHechizo++;
		if (_nivel == Bustemu.NIVEL_PA1)
			_baseStats.addUnStat(111, Bustemu.CANTIDAD_PA1);
		CentroInfo.subirNivelAprenderHechizos(this, _nivel);
		if (addXp)
			_experiencia = MundoDofus.getExpNivel(_nivel)._personaje;
		if (enviar && _enLinea) {
			GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(_cuenta.getEntradaPersonaje().getOut(), _nivel);
		}
		_PDV = _PDVMAX;
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
	}
	
	public void addExp(long xp) {
		if (_encarnacion != null) {
			_encarnacion.addExp(xp, this);
			return;
		}
		_experiencia += xp;
		int exNivel = _nivel;
		while (_experiencia >= MundoDofus.getExpMaxPersonaje(_nivel) && _nivel < Bustemu.MAX_NIVEL)
			subirNivel(true, false);
		if (_enLinea) {
			if (exNivel < _nivel) {
				try {
					GestorSalida.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(this, _nivel);
					if (getGremio() != null) {
						getMiembroGremio().setNivel(_nivel);
					}
					GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(this);
					actualizarInfoGrupo();
				} catch (NullPointerException e) {}
			}
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		}
	}
	
	public void addKamas(long kamas) {
		_kamas += kamas;
	}
	
	public void setIntercambio(Intercambio inter) {
		_intercambio = inter;
	}
	
	public Intercambio getIntercambio() {
		return _intercambio;
	}
	
	public void setTallerInvitado(InvitarTaller inter) {
		_tallerInvitado = inter;
	}
	
	public InvitarTaller getTallerInvitado() {
		return _tallerInvitado;
	}
	
	public int aprenderOficio(Oficio oficio) {
		for (Entry<Integer, StatsOficio> entry : _statsOficios.entrySet()) {
			if (entry.getValue().getOficio().getID() == oficio.getID())
				return -1;
		}
		int cantOficios = _statsOficios.size();
		if (cantOficios == 6)
			return -1;
		int pos = -1;
		if (_statsOficios.get(0) == null)
			pos = 0;
		else if (_statsOficios.get(1) == null)
			pos = 1;
		else if (_statsOficios.get(2) == null)
			pos = 2;
		else if (_statsOficios.get(3) == null)
			pos = 3;
		else if (_statsOficios.get(4) == null)
			pos = 4;
		else if (_statsOficios.get(5) == null)
			pos = 5;
		if (pos == -1)
			return -1;
		StatsOficio statOficio = new StatsOficio(pos, oficio, 1, 0);
		_statsOficios.put(pos, statOficio);
		if (_enLinea) {
			ArrayList<StatsOficio> list = new ArrayList<StatsOficio>();
			list.add(statOficio);
			GestorSalida.ENVIAR_Im_INFORMACION(this, "02;" + oficio.getID());
			GestorSalida.ENVIAR_JS_TRABAJO_POR_OFICIO(this, list);
			GestorSalida.ENVIAR_JX_EXPERINENCIA_OFICIO(this, list);
			GestorSalida.ENVIAR_JO_OFICIO_OPCIONES(this, list);
			Objeto obj = getObjPosicion(1);
			if (obj != null)
				if (oficio.herramientaValida(obj.getModelo().getID())) {
					PrintWriter out = _cuenta.getEntradaPersonaje().getOut();
					GestorSalida.ENVIAR_OT_OBJETO_HERRAMIENTA(out, oficio.getID());
					String strOficioPub = CentroInfo.trabajosOficioTaller(oficio.getID());
					if (_mapa.esTaller() && _oficioPublico)
						GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION(out, "+", _ID, strOficioPub);
					_stringOficiosPublicos = strOficioPub;
				}
		}
		return pos;
	}
	
	public void olvidarOficio(int pos) {
		_statsOficios.remove(pos);
	}
	
	public boolean tieneEquipado(int id) {
		for (Entry<Integer, Objeto> entry : _objetos.entrySet()) {
			Objeto objeto = entry.getValue();
			if (objeto.getModelo().getID() == id && objeto.getPosicion() != -1)
				return true;
		}
		return false;
	}
	
	public void setInvitado(int invitando) {
		_invitando = invitando;
	}
	
	public int getInvitado() {
		return _invitando;
	}
	
	public String stringInfoGrupo() {
		String str = _ID + ";";
		str += _nombre + ";";
		str += _gfxID + ";";
		str += _color1 + ";";
		str += _color2 + ";";
		str += _color3 + ";";
		str += getStringAccesorios() + ";";
		str += _PDV + "," + _PDVMAX + ";";
		str += _nivel + ";";
		str += getIniciativa() + ";";
		str += getTotalStats().getEfecto(176) + ";";
		str += "1";
		return str;
	}
	
	public int getNroObjEquipadosDeSet(int setID) {
		int nro = 0;
		for (Objeto objeto : _objetos.values()) {
			if (objeto.getPosicion() == -1)
				continue;
			if (objeto.getModelo().getSetID() == setID)
				nro++;
		}
		return nro;
	}
	
	public void iniciarAccionEnCelda(AccionDeJuego GA) {
		int celdaID = -1;
		int accion = -1;
		try {
			celdaID = Integer.parseInt(GA._args.split(";")[0]);
			accion = Integer.parseInt(GA._args.split(";")[1]);
		} catch (Exception e) {}
		if (celdaID == -1 || accion == -1)
			return;
		if (!_mapa.getCelda(celdaID).puedeHacerAccion(accion, _pescarKuakua))
			return;
		_mapa.getCelda(celdaID).iniciarAccion(this, GA);
	}
	
	public void finalizarAccionEnCelda(AccionDeJuego AJ) {
		int celdaID = -1;
		try {
			celdaID = Integer.parseInt(AJ._args.split(";")[0]);
		} catch (Exception e) {}
		if (celdaID == -1 || AJ == null || _mapa.getCelda(celdaID) == null)
			return;
		_mapa.getCelda(celdaID).finalizarAccion(this, AJ);
	}
	
	public void teleport(short nuevoMapaID, int nuevaCeldaID) {
		if (_tutorial != null)
			return;
		if (!_huir) {
			if ( (System.currentTimeMillis() - _tiempoAgre) > 8000)
				_huir = true;
			else {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, "No puedes teletransportarte, hasta dentro de 10 segundos.");
				return;
			}
		}
		PrintWriter out = null;
		if (_cuenta.getEntradaPersonaje() != null) {
			out = _cuenta.getEntradaPersonaje().getOut();
		}
		Mapa nuevoMapa = MundoDofus.getMapa(nuevoMapaID);
		if (nuevoMapa == null || out == null)
			return;
		if (nuevoMapa.getCelda(nuevaCeldaID) == null)
			return;
		if (nuevoMapa.esTaller()) {
			if (nuevoMapa.getPersos().size() > Bustemu.LIMITE_ARTESANOS_TALLER) {
				GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this,
						"No puedes ingresar al taller porque esta lleno, porafvor regresa más tarde o intenta en otro "
								+ "taller.");
				nuevoMapaID = 951;
				nuevaCeldaID = 340;
				nuevoMapa = MundoDofus.getMapa(nuevoMapaID);
			}
		}
		GestorSalida.ENVIAR_GA2_CARGANDO_MAPA(out, _ID);
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_mapa, _ID);
		_celda.removerPersonaje(_ID);
		_mapa = nuevoMapa;
		_celda = _mapa.getCelda(nuevaCeldaID);
		GestorSalida.ENVIAR_GDM_CAMBIO_DE_MAPA(out, nuevoMapaID, _mapa.getFecha(), _mapa.getCodigo());
		_mapa.addJugador(this);
		if (!_seguidores.isEmpty()) {
			ArrayList<Personaje> seguidores = new ArrayList<Personaje>();
			try {
				seguidores.addAll(_seguidores.values());
			} catch (ConcurrentModificationException e) {}
			for (Personaje seguido : seguidores) {
				if (seguido._enLinea)
					GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(seguido, this);
				else
					_seguidores.remove(seguido.getID());
			}
		}
	}
	private boolean _defendiendo = false;
	
	public boolean getDefendiendo() {
		return _defendiendo;
	}
	
	public void setDefendiendo(boolean def) {
		_defendiendo = def;
	}
	
	public boolean teleportSinTodos(short nuevoMapaID, int nuevaCeldaID) {
		PrintWriter out = null;
		_defendiendo = true;
		if (_cuenta.getEntradaPersonaje() != null) {
			out = _cuenta.getEntradaPersonaje().getOut();
		}
		Mapa nuevoMapa = MundoDofus.getMapa(nuevoMapaID);
		if (nuevoMapa == null || out == null || nuevoMapa.getCelda(nuevaCeldaID) == null || _celda == null)
			return false;
		GestorSalida.ENVIAR_GA2_CARGANDO_MAPA(out, _ID);
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_mapa, _ID);
		_celda.removerPersonaje(_ID);
		_mapa = nuevoMapa;
		_celda = _mapa.getCelda(nuevaCeldaID);
		GestorSalida.ENVIAR_GDM_CAMBIO_DE_MAPA(out, nuevoMapaID, _mapa.getFecha(), _mapa.getCodigo());
		return true;
	}
	private boolean _agresion = false;
	private long _tiempoAgre = 0L;
	private boolean _huir = true;
	
	public void setHuir(boolean huir) {
		_huir = huir;
	}
	
	public boolean getHuir() {
		return _huir;
	}
	
	public long getTiempoAgre() {
		return _tiempoAgre;
	}
	
	public void setTiempoAgre(long tiempo) {
		_tiempoAgre = tiempo;
	}
	
	public void setAgresion(boolean agre) {
		_agresion = agre;
	}
	
	public boolean getAgresion() {
		return _agresion;
	}
	
	public int getCostoAbrirBanco() {
		return _cuenta.getBanco().size();
	}
	
	public String getStringVar(String str) {
		if (str.equals("nombre"))
			return _nombre;
		if (str.equals("costoBanco")) {
			return getCostoAbrirBanco() + "";
		}
		return "";
	}
	
	public void setKamasBanco(long i) {
		_cuenta.setKamasBanco(i);
		GestorSQL.SALVAR_CUENTA(_cuenta);
	}
	
	public long getKamasBanco() {
		return _cuenta.getKamasBanco();
	}
	
	public void setEnBanco(boolean b) {
		_estarBanco = b;
	}
	
	public boolean enBanco() {
		return _estarBanco;
	}
	
	public String stringBanco() {
		String packet = "";
		for (Entry<Integer, Objeto> entry : _cuenta.getBanco().entrySet())
			packet += "O" + entry.getValue().stringObjetoConGuiño();
		if (getKamasBanco() != 0)
			packet += "G" + getKamasBanco();
		return packet;
	}
	
	public void addCapital(int pts) {
		_capital += pts;
	}
	
	public void setCapital(int capital) {
		_capital = capital;
	}
	
	public void addPuntosHechizos(int pts) {
		_puntosHechizo += pts;
	}
	
	public void addObjAlBanco(int id, int cant) {
		Objeto objAGuardar = MundoDofus.getObjeto(id);
		if (_objetos.get(id) == null) {
			return;
		}
		if (objAGuardar.getPosicion() != -1)
			return;
		Objeto objBanco = getSimilarObjetoBanco(objAGuardar);
		int nuevaCant = objAGuardar.getCantidad() - cant;
		if (objBanco == null) {
			if (nuevaCant <= 0) {
				borrarObjetoRemove(objAGuardar.getID());
				_cuenta.getBanco().put(objAGuardar.getID(), objAGuardar);
				String str = "O+" + objAGuardar.getID() + "|" + objAGuardar.getCantidad() + "|" + objAGuardar.getModelo().getID()
						+ "|" + objAGuardar.convertirStatsAString();
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, id);
			} else {
				objAGuardar.setCantidad(nuevaCant);
				objBanco = Objeto.clonarObjeto(objAGuardar, cant);
				MundoDofus.addObjeto(objBanco, true);
				_cuenta.getBanco().put(objBanco.getID(), objBanco);
				String str = "O+" + objBanco.getID() + "|" + objBanco.getCantidad() + "|" + objBanco.getModelo().getID() + "|"
						+ objBanco.convertirStatsAString();
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objAGuardar);
			}
		} else {
			if (nuevaCant <= 0) {
				borrarObjetoRemove(objAGuardar.getID());
				objBanco.setCantidad(objBanco.getCantidad() + objAGuardar.getCantidad());
				String str = "O+" + objBanco.getID() + "|" + objBanco.getCantidad() + "|" + objBanco.getModelo().getID() + "|"
						+ objBanco.convertirStatsAString();
				MundoDofus.eliminarObjeto(objAGuardar.getID());
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, id);
			} else {
				objAGuardar.setCantidad(nuevaCant);
				objBanco.setCantidad(objBanco.getCantidad() + cant);
				String str = "O+" + objBanco.getID() + "|" + objBanco.getCantidad() + "|" + objBanco.getModelo().getID() + "|"
						+ objBanco.convertirStatsAString();
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objAGuardar);
			}
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
		GestorSQL.SALVAR_CUENTA(_cuenta);
	}
	
	private Objeto getSimilarObjetoBanco(Objeto obj) {
		for (Objeto value : _cuenta.getBanco().values()) {
			ObjetoModelo objetoMod = value.getModelo();
			if (objetoMod.getTipo() == 85)
				continue;
			if (objetoMod.getID() == obj.getModelo().getID() && value.getStats().sonStatsIguales(obj.getStats()))
				return value;
		}
		return null;
	}
	
	public void removerDelBanco(int id, int cant) {
		Objeto objBanco = MundoDofus.getObjeto(id);
		if (_cuenta.getBanco().get(id) == null) {
			return;
		}
		Objeto objetoARecibir = getObjSimilarInventario(objBanco);
		int nuevaCant = objBanco.getCantidad() - cant;
		if (objetoARecibir == null) {
			if (nuevaCant <= 0) {
				_cuenta.getBanco().remove(id);
				_objetos.put(id, objBanco);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(this, objBanco);
				String str = "O-" + id;
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
			} else {
				objetoARecibir = Objeto.clonarObjeto(objBanco, cant);
				objBanco.setCantidad(nuevaCant);
				MundoDofus.addObjeto(objetoARecibir, true);
				_objetos.put(objetoARecibir.getID(), objetoARecibir);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(this, objetoARecibir);
				String str = "O+" + objBanco.getID() + "|" + objBanco.getCantidad() + "|" + objBanco.getModelo().getID() + "|"
						+ objBanco.convertirStatsAString();
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
			}
		} else {
			if (nuevaCant <= 0) {
				_cuenta.getBanco().remove(objBanco.getID());
				objetoARecibir.setCantidad(objetoARecibir.getCantidad() + objBanco.getCantidad());
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objetoARecibir);
				MundoDofus.eliminarObjeto(objBanco.getID());
				String str = "O-" + id;
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
			} else {
				objBanco.setCantidad(nuevaCant);
				objetoARecibir.setCantidad(objetoARecibir.getCantidad() + cant);
				GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objetoARecibir);
				String str = "O+" + objBanco.getID() + "|" + objBanco.getCantidad() + "|" + objBanco.getModelo().getID() + "|"
						+ objBanco.convertirStatsAString();
				GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(this, str);
			}
		}
		GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(this);
		GestorSQL.SALVAR_CUENTA(_cuenta);
	}
	
	public void abrirCercado() {
		if (getDeshonor() >= 5) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "183");
			return;
		}
		_enCercado = _mapa.getCercado();
		_ocupado = true;
		String str = analizarListaDrago();
		GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(this, 16, str);
	}
	
	private String analizarListaDrago() {
		String packet = "";
		boolean Primero = false;
		if (_cuenta.getEstablo().size() > 0) {
			for (Dragopavo DD : _cuenta.getEstablo()) {
				if (Primero)
					packet += ";";
				packet += DD.detallesMontura();
				Primero = true;
				continue;
			}
		}
		packet += "~";
		if (_enCercado.getListaCriando().size() > 0) {
			boolean primero = false;
			for (Integer pavo : _enCercado.getListaCriando()) {
				Dragopavo dragopavo = MundoDofus.getDragopavoPorID(pavo);
				if (dragopavo.getDueño() == _ID) {
					if (primero)
						packet += ";";
					packet += dragopavo.detallesMontura();
					primero = true;
					continue;
				}
				if (getMiembroGremio() != null) {
					if (getMiembroGremio().puede(CentroInfo.G_OTRASMONTURAS) && _enCercado.getDueño() != -1) {
						if (primero)
							packet += ";";
						packet += dragopavo.detallesMontura();
						primero = true;
					}
				}
			}
		}
		return packet;
	}
	
	public void salirDeCercado() {
		if (_enCercado == null)
			return;
		_enCercado = null;
	}
	
	public Cercado getEnCercado() {
		return _enCercado;
	}
	
	public void fullPDV() {
		_PDV = _PDVMAX;
	}
	
	public void removerObjetoPorModYCant(int objModeloID, int cantidad) {
		ArrayList<Objeto> lista = new ArrayList<Objeto>();
		lista.addAll(_objetos.values());
		ArrayList<Objeto> listaObjBorrar = new ArrayList<Objeto>();
		int cantTemp = cantidad;
		for (Objeto obj : lista) {
			if (obj.getModelo().getID() != objModeloID)
				continue;
			if (obj.getCantidad() >= cantidad) {
				int nuevaCant = obj.getCantidad() - cantidad;
				if (nuevaCant > 0) {
					obj.setCantidad(nuevaCant);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
				} else {
					_objetos.remove(obj.getID());
					MundoDofus.eliminarObjeto(obj.getID());
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, obj.getID());
				}
				return;
			} else {
				if (obj.getCantidad() >= cantTemp) {
					int nuevaCant = obj.getCantidad() - cantTemp;
					if (nuevaCant > 0) {
						obj.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
					} else
						listaObjBorrar.add(obj);
					for (Objeto objBorrar : listaObjBorrar) {
						_objetos.remove(objBorrar.getID());
						MundoDofus.eliminarObjeto(objBorrar.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, objBorrar.getID());
					}
				} else {
					cantTemp -= obj.getCantidad();
					listaObjBorrar.add(obj);
				}
			}
		}
	}
	
	public void eliminarObjetoPorModelo(int objModeloID) {
		ArrayList<Objeto> list = new ArrayList<Objeto>();
		list.addAll(_objetos.values());
		for (Objeto obj : list) {
			if (obj.getModelo().getID() != objModeloID)
				continue;
			_objetos.remove(obj.getID());
			MundoDofus.eliminarObjeto(obj.getID());
			GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, obj.getID());
		}
		return;
	}
	
	public Map<Integer, StatsOficio> getStatsOficios() {
		return _statsOficios;
	}
	
	public void iniciarAccionOficio(int idTrabajo, ObjetoInteractivo objInterac, AccionDeJuego GA, Celda celda) {
		StatsOficio SO = getOficioPorTrabajo(idTrabajo);
		if (SO == null)
			return;
		SO.iniciarTrabajo(idTrabajo, this, objInterac, GA, celda);
	}
	
	public void finalizarAccionOficio(int idTrabajo, AccionDeJuego GA, Celda celda) {
		StatsOficio SO = getOficioPorTrabajo(idTrabajo);
		if (SO == null)
			return;
		SO.finalizarTrabajo(idTrabajo, this, GA, celda);
	}
	
	public String stringOficios() {
		String str = "";
		for (StatsOficio SO : _statsOficios.values()) {
			if (str.length() > 0)
				str += ";";
			str += SO.getOficio().getID() + "," + SO.getXP();
		}
		return str;
	}
	
	public int totalOficiosBasicos() {
		int i = 0;
		for (StatsOficio SO : _statsOficios.values()) {
			int idOficio = SO.getOficio().getID();
			if (idOficio == 2 || idOficio == 11 || idOficio == 13 || idOficio == 14 || idOficio == 15 || idOficio == 16
					|| idOficio == 17 || idOficio == 18 || idOficio == 19 || idOficio == 20 || idOficio == 24 || idOficio == 25
					|| idOficio == 26 || idOficio == 27 || idOficio == 28 || idOficio == 31 || idOficio == 36 || idOficio == 41
					|| idOficio == 56 || idOficio == 58 || idOficio == 60 || idOficio == 65) {
				i++;
			}
		}
		return i;
	}
	
	public int totalOficiosFM() {
		int i = 0;
		for (StatsOficio SO : _statsOficios.values()) {
			int idOficio = SO.getOficio().getID();
			if (idOficio == 43 || idOficio == 44 || idOficio == 45 || idOficio == 46 || idOficio == 47 || idOficio == 48
					|| idOficio == 49 || idOficio == 50 || idOficio == 62 || idOficio == 63 || idOficio == 64) {
				i++;
			}
		}
		return i;
	}
	
	public void setHaciendoTrabajo(AccionTrabajo JA) {
		_haciendoTrabajo = JA;
	}
	
	public AccionTrabajo getHaciendoTrabajo() {
		return _haciendoTrabajo;
	}
	
	public StatsOficio getOficioPorTrabajo(int trabajoID) {
		for (StatsOficio SO : _statsOficios.values())
			if (SO.esValidoTrabajo(trabajoID))
				return SO;
		return null;
	}
	
	public String analizarListaAmigos(int id) {
		String str = ";";
		str += "?;";
		str += _nombre + ";";
		if (_cuenta.esAmigo(id)) {
			str += _nivel + ";";
			str += _alineacion + ";";
		} else {
			str += "?;";
			str += "-1;";
		}
		str += _clase + ";";
		str += _sexo + ";";
		str += _gfxID;
		return str;
	}
	
	public String analizarListaEnemigos(int id) {
		String str = ";";
		str += "?;";
		str += _nombre + ";";
		if (_cuenta.esEnemigo(id)) {
			str += _nivel + ";";
			str += _alineacion + ";";
		} else {
			str += "?;";
			str += "-1;";
		}
		str += _clase + ";";
		str += _sexo + ";";
		str += _gfxID;
		return str;
	}
	
	public StatsOficio getOficioPorID(int oficio) {
		for (StatsOficio SO : _statsOficios.values())
			if (SO.getOficio().getID() == oficio)
				return SO;
		return null;
	}
	
	public boolean estaMontando() {
		return _montando;
	}
	
	public void bajarMontura() {
		_montando = !_montando;
		GestorSalida.ENVIAR_Rr_ESTADO_MONTADO(this, _montando ? "+" : "-");
	}
	
	public void subirBajarMontura() {
		if (_encarnacion != null) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "134|44");
			return;
		}
		if (_montura.getEnergia() <= 0) {
			GestorSalida.ENVIAR_Im_INFORMACION(this, "1113");
			return;
		}
		_montando = !_montando;
		Objeto mascota = getObjPosicion(8);
		if (_montando && mascota != null) {
			mascota.setPosicion(-1);
			_mascota = null;
			GestorSalida.ENVIAR_OM_MOVER_OBJETO(this, mascota);
		}
		GestorSalida.ENVIAR_Re_DETALLES_MONTURA(this, "+", _montura);
		if (_pelea == null)
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(_mapa, this);
		else if (_pelea.getEstado() == 2)
			GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_PELEA(_pelea, _pelea.getLuchadorPorPJ(this));
		GestorSalida.ENVIAR_Rr_ESTADO_MONTADO(this, _montando ? "+" : "-");
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		_montura.energiaPerdida(15);
	}
	
	public int getXpDonadaMontura() {
		return _xpDonadaMontura;
	}
	
	public Dragopavo getMontura() {
		return _montura;
	}
	
	public void setMontura(Dragopavo DP) {
		_montura = DP;
	}
	
	public void setDonarXPMontura(int xp) {
		_xpDonadaMontura = xp;
	}
	
	public void setEnLinea(boolean linea) {
		_enLinea = linea;
	}
	
	public void resetVariables() {
		_intercambioCon = 0;
		_conversandoCon = 0;
		_ocupado = false;
		_emoteActivado = 0;
		_listo = false;
		_intercambio = null;
		_estarBanco = false;
		_invitando = -1;
		_sentado = false;
		_haciendoTrabajo = null;
		_enZaaping = false;
		_enCercado = null;
		_montando = false;
		_recaudando = false;
		_recaudandoRecaudadorID = 0;
		_esDoble = false;
		_olvidandoHechizo = false;
		_ausente = false;
		_invisible = false;
		_trueque = null;
		_tempMapaDefPerco = null;
		_tempCeldaDefPerco = null;
		_cofre = null;
		_casa = null;
		_listaArtesanos = false;
		_cambiarNombre = false;
		_dragopaveando = false;
		_siguiendo = null;
		_tallerInvitado = null;
		_tutorial = null;
	}
	
	public void addCanal(String canal) {
		if (_canales.indexOf(canal) >= 0)
			return;
		_canales += canal;
		GestorSalida.ENVIAR_cC_SUSCRIBIR_CANAL(this, '+', canal);
	}
	
	public void removerCanal(String canal) {
		_canales = _canales.replace(canal, "");
		GestorSalida.ENVIAR_cC_SUSCRIBIR_CANAL(this, '-', canal);
	}
	
	public void modificarAlineamiento(byte a) {
		_honor = 0;
		_deshonor = 0;
		_alineacion = a;
		_nivelAlineacion = 1;
		GestorSalida.ENVIAR_ZC_CAMBIAR_ALINEACION(this, a);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
	}
	
	public void setDeshonor(int deshonor) {
		_deshonor = deshonor;
	}
	
	public int getDeshonor() {
		return _deshonor;
	}
	
	public boolean estaMostrandoAlas() {
		return _mostrarAlas;
	}
	
	public void setMostrarAlas(boolean mostrarAlas) {
		_mostrarAlas = mostrarAlas;
	}
	
	public int getHonor() {
		return _honor;
	}
	
	public int getNivelAlineacion() {
		if (_alineacion == -1)
			return 1;
		return _nivelAlineacion;
	}
	
	public void botonActDesacAlas(char c) {
		if (_alineacion == -1)
			return;
		int hloose = _honor * 5 / 100;
		switch (c) {
			case '*':
				GestorSalida.GAME_SEND_GIP_PACKET(this, hloose);
				return;
			case '+':
				_mostrarAlas = true;
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
				break;
			case '-':
				_mostrarAlas = false;
				_honor -= hloose;
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
				break;
		}
	}
	
	public void addHonor(int honor) {
		int nivelAntes = _nivelAlineacion;
		_honor = _honor + honor;
		if (_honor < 0)
			_honor = 0;
		else if (_honor >= 25000) {
			_nivelAlineacion = 10;
			_honor = 25000;
		}
		for (int n = 1; n <= 10; n++) {
			if (_honor < MundoDofus.getExpNivel(n)._pvp) {
				_nivelAlineacion = n - 1;
				break;
			}
		}
		if (nivelAntes == _nivelAlineacion)
			return;
		else if (nivelAntes < _nivelAlineacion)
			GestorSalida.ENVIAR_Im_INFORMACION(this, "082;" + _nivelAlineacion);
		else if (nivelAntes > _nivelAlineacion)
			GestorSalida.ENVIAR_Im_INFORMACION(this, "083;" + _nivelAlineacion);
	}
	
	public MiembroGremio getMiembroGremio() {
		return _miembroGremio;
	}
	
	public int getCuentaID() {
		return _cuentaID;
	}
	
	public void setCuenta(Cuenta c) {
		_cuenta = c;
	}
	
	public String stringListaZaap() {
		String map = _mapa.getID() + "";
		try {
			map = _puntoSalvado.split(",")[0];
		} catch (Exception e) {}
		String str = map + "";
		int SubAreaID = _mapa.getSubArea().getArea().getSuperArea().getID();
		for (short i : _zaaps) {
			if (MundoDofus.getMapa(i) == null)
				continue;
			if (MundoDofus.getMapa(i).getSubArea().getArea().getSuperArea().getID() != SubAreaID)
				continue;
			int cost = Formulas.calcularCosteZaap(_mapa, MundoDofus.getMapa(i));
			if (i == _mapa.getID())
				cost = 0;
			str += "|" + i + ";" + cost;
		}
		return str;
	}
	
	public String stringListaPrismas() {
		String map = _mapa.getID() + "";
		String str = map + "";
		int SubAreaID = _mapa.getSubArea().getArea().getSuperArea().getID();
		for (Prisma prisma : MundoDofus.TodosPrismas()) {
			if (prisma.getAlineacion() != _alineacion)
				continue;
			short mapaID = prisma.getMapa();
			if (MundoDofus.getMapa(mapaID) == null)
				continue;
			if (MundoDofus.getMapa(mapaID).getSubArea().getArea().getSuperArea().getID() != SubAreaID)
				continue;
			if (prisma.getEstadoPelea() == 0 || prisma.getEstadoPelea() == -2) {
				str += "|" + mapaID + ";*";
			} else {
				int costo = Formulas.calcularCosteZaap(_mapa, MundoDofus.getMapa(mapaID));
				if (mapaID == _mapa.getID())
					costo = 0;
				str += "|" + mapaID + ";" + costo;
			}
		}
		return str;
	}
	
	public boolean tieneZaap(int mapID) {
		for (int i : _zaaps)
			if (i == mapID)
				return true;
		return false;
	}
	
	public void abrirMenuZaap() {
		if (_pelea == null) {
			if (getDeshonor() >= 3) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "183");
				return;
			}
			_enZaaping = true;
			if (!tieneZaap(_mapa.getID())) {
				_zaaps.add(_mapa.getID());
				GestorSalida.ENVIAR_Im_INFORMACION(this, "024");
				GestorSQL.SALVAR_PERSONAJE(this, false);
			}
			GestorSalida.ENVIAR_WC_MENU_ZAAP(this);
		}
	}
	
	public void abrirMenuPrisma() {
		if (_pelea == null) {
			if (getDeshonor() >= 3) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "183");
				return;
			}
			_enZaaping = true;
			GestorSalida.ENVIAR_Wp_MENU_PRISMA(this);
		}
	}
	
	public void usarZaap(short mapaID) {
		if (!_enZaaping || _pelea != null || !tieneZaap(mapaID))
			return;
		int costo = Formulas.calcularCosteZaap(_mapa, MundoDofus.getMapa(mapaID));
		if (_kamas < costo)
			return;
		int superAreaID = _mapa.getSubArea().getArea().getSuperArea().getID();
		int celdaID = MundoDofus.getCeldaZaapPorMapaID(mapaID);
		Mapa zaapMapa = MundoDofus.getMapa(mapaID);
		if (zaapMapa == null) {
			System.out.println("El mapa " + mapaID + " no esta implantado, Zaap rechazada");
			GestorSalida.GAME_SEND_WUE_PACKET(this);
			return;
		}
		if (zaapMapa.getCelda(celdaID) == null) {
			System.out.println("La celda asociada un zaap " + mapaID + " no esta implatado, Zaap rechazada");
			GestorSalida.GAME_SEND_WUE_PACKET(this);
			return;
		}
		if (!zaapMapa.getCelda(celdaID).esCaminable(true)) {
			System.out.println("La celda asociada a un zaap " + mapaID + " no esta 'walkable', Zaap rechazada");
			GestorSalida.GAME_SEND_WUE_PACKET(this);
			return;
		}
		if (zaapMapa.getSubArea().getArea().getSuperArea().getID() != superAreaID) {
			GestorSalida.GAME_SEND_WUE_PACKET(this);
			return;
		}
		if ( (_alineacion == 2 && mapaID == 4263) || (_alineacion == 1 && mapaID == 5295)) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, "El zaap al que deseas ir, es de alineación enemiga.");
			GestorSalida.GAME_SEND_WUE_PACKET(this);
			return;
		}
		_kamas -= costo;
		teleport(mapaID, celdaID);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		GestorSalida.ENVIAR_WV_CERRAR_ZAAP(this);
		_enZaaping = false;
	}
	
	public String stringZaaps() {
		String str = "";
		boolean primero = false;
		for (int i : _zaaps) {
			if (primero)
				str += ",";
			primero = true;
			str += i + "";
		}
		return str;
	}
	
	public void cerrarZaap() {
		if (!_enZaaping)
			return;
		_enZaaping = false;
		GestorSalida.ENVIAR_WV_CERRAR_ZAAP(this);
	}
	
	public void cerrarPrisma() {
		if (!_enZaaping)
			return;
		_enZaaping = false;
		GestorSalida.ENVIAR_Ww_CERRAR_PRISMA(this);
	}
	
	public void cerrarZaapi() {
		if (!_enZaaping)
			return;
		_enZaaping = false;
		GestorSalida.ENVIAR_Wv_CERRAR_ZAPPI(this);
	}
	
	public void usarZaapi(String packet) {
		Mapa mapa = MundoDofus.getMapa(Short.valueOf(packet.substring(2)));
		short celdaId = 100;
		if (mapa != null) {
			for (Entry<Integer, Celda> entry : mapa.getCeldas().entrySet()) {
				ObjetoInteractivo obj = entry.getValue().getObjetoInterac();
				if (obj != null) {
					if (obj.getID() == 7031 || obj.getID() == 7030) {
						celdaId = (short) (entry.getValue().getID() + 18);
					}
				}
			}
		}
		if (mapa.getSubArea().getArea().getID() == 7 || mapa.getSubArea().getArea().getID() == 11) {
			int price = 20;
			if (getAlineacion() == 1 || getAlineacion() == 2)
				price = 10;
			_kamas -= price;
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
			teleport(Short.valueOf(packet.substring(2)), celdaId);
			GestorSalida.ENVIAR_Wv_CERRAR_ZAPPI(this);
		}
	}
	
	public void usarPrisma(String packet) {
		int celdaID = 340;
		short mapaID = 7411;
		for (Prisma prisma : MundoDofus.TodosPrismas()) {
			if (prisma.getMapa() == Short.valueOf(packet.substring(2))) {
				celdaID = prisma.getCelda();
				mapaID = prisma.getMapa();
				break;
			}
		}
		int costo = Formulas.calcularCosteZaap(_mapa, MundoDofus.getMapa(mapaID));
		if (mapaID == _mapa.getID())
			costo = 0;
		if (_kamas < costo) {
			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, "No tienes las suficientes kamas para realizar esta acción.");
			return;
		}
		_kamas -= costo;
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
		teleport(Short.valueOf(packet.substring(2)), celdaID);
		GestorSalida.ENVIAR_Ww_CERRAR_PRISMA(this);
	}
	
	public boolean tieneObjModeloNoEquip(int id, int cantidad) {
		for (Objeto obj : _objetos.values()) {
			if (obj.getPosicion() != -1 && obj.getPosicion() < 35 || obj.getModelo().getID() != id)
				continue;
			if (obj.getCantidad() >= cantidad)
				return true;
		}
		return false;
	}
	
	public Objeto getObjModeloNoEquip(int id, int cantidad) {
		for (Objeto obj : _objetos.values()) {
			if (obj.getPosicion() != -1 && obj.getPosicion() < 35 || obj.getModelo().getID() != id)
				continue;
			if (obj.getCantidad() >= cantidad)
				return obj;
		}
		return null;
	}
	
	public void setZaaping(boolean zaaping) {
		_enZaaping = zaaping;
	}
	
	public void setOlvidandoHechizo(boolean olvidandoHechizo) {
		_olvidandoHechizo = olvidandoHechizo;
	}
	
	public boolean estaOlvidandoHechizo() {
		return _olvidandoHechizo;
	}
	
	public boolean estaDisponible(Personaje perso) {
		if (_ausente)
			return false;
		if (_invisible) {
			return _cuenta.esAmigo(perso.getCuenta().getID());
		}
		return true;
	}
	
	public void setSiguiendo(Personaje perso) {
		_siguiendo = perso;
	}
	
	public Personaje getSiguiendo() {
		return _siguiendo;
	}
	
	public boolean estaAusente() {
		return _ausente;
	}
	
	public void setEstaAusente(boolean ausente) {
		_ausente = ausente;
	}
	
	public boolean esInvisible() {
		return _invisible;
	}
	
	public boolean esFantasma() {
		return _esFantasma;
	}
	
	public void setEsInvisible(boolean invisible) {
		_invisible = invisible;
	}
	
	public boolean esDoble() {
		return _esDoble;
	}
	
	public void esDoble(boolean esDoble) {
		_esDoble = esDoble;
	}
	
	public boolean getRecaudando() {
		return _recaudando;
	}
	
	public void setRecaudando(boolean recaudando) {
		_recaudando = recaudando;
	}
	
	public void setDragopaveando(boolean recaudando) {
		_dragopaveando = recaudando;
	}
	
	public boolean getMochilaMontura() {
		return _dragopaveando;
	}
	
	public int getRecaudandoRecauID() {
		return _recaudandoRecaudadorID;
	}
	
	public void setRecaudandoRecaudadorID(int recaudadorID) {
		_recaudandoRecaudadorID = recaudadorID;
	}
	
	public void setTitulo(int titulo) {
		_titulo = titulo;
	}
	
	public int getTitulo() {
		return _titulo;
	}
	
	public boolean cambiarNombre() {
		return _cambiarNombre;
	}
	
	public void cambiarNombre(boolean cambiar) {
		_cambiarNombre = cambiar;
	}
	
	public void setNombre(String nombre) {
		_nombre = nombre;
		_cambiarNombre = false;
		GestorSQL.ACTUALIZAR_NOMBRE(this);
		if (getMiembroGremio() != null)
			GestorSQL.ACTUALIZAR_MIEMBRO_GREMIO(getMiembroGremio());
	}
	
	public static Personaje personajeClonado(Personaje perso, int id) {
		TreeMap<Integer, Integer> stats = new TreeMap<Integer, Integer>();
		Stats statsBase = perso.getBaseStats();
		stats.put(125, statsBase.getEfecto(125));
		stats.put(118, statsBase.getEfecto(118));
		stats.put(124, statsBase.getEfecto(124));
		stats.put(126, statsBase.getEfecto(126));
		stats.put(123, statsBase.getEfecto(123));
		stats.put(119, statsBase.getEfecto(119));
		stats.put(111, statsBase.getEfecto(111));
		stats.put(128, statsBase.getEfecto(128));
		stats.put(214, statsBase.getEfecto(214));
		stats.put(210, statsBase.getEfecto(210));
		stats.put(213, statsBase.getEfecto(213));
		stats.put(211, statsBase.getEfecto(211));
		stats.put(212, statsBase.getEfecto(212));
		stats.put(160, statsBase.getEfecto(160));
		stats.put(161, statsBase.getEfecto(161));
		byte mostrarAlas = 0;
		int nivelAlineacion = 0;
		if (perso._alineacion != 0 && perso._mostrarAlas) {
			mostrarAlas = 1;
			nivelAlineacion = perso.getNivelAlineacion();
		}
		int monturaID = -1;
		if (perso._montura != null) {
			monturaID = perso._montura.getID();
		}
		Personaje clon = new Personaje(id, perso._nombre, perso._sexo, perso._clase, perso._color1, perso._color2, perso._color3,
				perso._nivel, 100, perso._gfxID, stats, perso._objetos, 100, mostrarAlas, monturaID, nivelAlineacion,
				perso._alineacion);
		clon.esDoble(true);
		if (perso._montando) {
			clon._montando = true;
		}
		return clon;
	}
	
	public void verificaYCambiaObjPosicion() {
		boolean primerAmuleto = true;
		boolean primerAnillo1 = true;
		boolean primerAnillo2 = true;
		boolean primerArma = true;
		boolean primerBotas = true;
		boolean primerEscudo = true;
		boolean primerCapa = true;
		boolean primerCinturon = true;
		boolean primerSombrero = true;
		boolean primerDofus1 = true;
		boolean primerDofus2 = true;
		boolean primerDofus3 = true;
		boolean primerDofus4 = true;
		boolean primerDofus5 = true;
		boolean primerDofus6 = true;
		boolean primerMascota = true;
		for (Objeto obj : _objetos.values()) {
			if (obj.getPosicion() == -1)
				continue;
			if (obj.getPosicion() == 0) {
				if (primerAmuleto) {
					primerAmuleto = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 2) {
				if (primerAnillo1) {
					primerAnillo1 = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 4) {
				if (primerAnillo2) {
					primerAnillo2 = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 1) {
				if (primerArma) {
					primerArma = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 5) {
				if (primerBotas) {
					primerBotas = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 15) {
				if (primerEscudo) {
					primerEscudo = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 7) {
				if (primerCapa) {
					primerCapa = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 3) {
				if (primerCinturon) {
					primerCinturon = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 6) {
				if (primerSombrero) {
					primerSombrero = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 9) {
				if (primerDofus1) {
					primerDofus1 = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 10) {
				if (primerDofus2) {
					primerDofus2 = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 11) {
				if (primerDofus3) {
					primerDofus3 = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 12) {
				if (primerDofus4) {
					primerDofus4 = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 13) {
				if (primerDofus5) {
					primerDofus5 = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 14) {
				if (primerDofus6) {
					primerDofus6 = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			} else if (obj.getPosicion() == 8) {
				if (primerMascota) {
					primerMascota = false;
				} else {
					obj.setPosicion(-1);
				}
				continue;
			}
		}
	}
	
	public MisionPVP getMisionPVP() {
		return _misionPvp;
	}
	
	public void setMisionPVP(MisionPVP mision) {
		_misionPvp = mision;
	}
	
	public void esposoDe(Personaje esposo) {
		_esposo = esposo.getID();
		GestorSQL.SALVAR_PERSONAJE(this, true);
	}
	
	public String getEsposoListaAmigos() {
		Personaje esposo = MundoDofus.getPersonaje(_esposo);
		String str = "";
		if (esposo != null) {
			str += esposo._nombre + "|" + esposo._clase + esposo._sexo + "|" + esposo._color1 + "|" + esposo._color2 + "|"
					+ esposo._color3 + "|";
			if (!esposo._enLinea) {
				str += "|";
			} else {
				str += esposo.stringUbicEsposo() + "|";
			}
		} else {
			str += "|";
		}
		return str;
	}
	
	public String stringUbicEsposo() {
		int p = 0;
		if (_pelea != null) {
			p = 1;
		}
		return _mapa.getID() + "|" + _nivel + "|" + p;
	}
	
	public void casarse(Personaje perso) {
		if (perso == null)
			return;
		int dist = ( (_mapa.getX() - perso._mapa.getX()) * (_mapa.getX() - perso._mapa.getX()))
				+ ( (_mapa.getY() - perso._mapa.getY()) * (_mapa.getY() - perso._mapa.getY()));
		if (dist > 100) {
			if (perso.getSexo() == 0) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "178");
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "179");
			}
			return;
		}
		int celdaPosicion = CentroInfo.getCeldaIDCercanaNoUsada(perso);
		if (celdaPosicion == 0) {
			if (perso.getSexo() == 0) {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "141");
			} else {
				GestorSalida.ENVIAR_Im_INFORMACION(this, "142");
			}
			return;
		}
		teleport(perso._mapa.getID(), celdaPosicion);
	}
	
	public void divorciar() {
		if (_enLinea)
			GestorSalida.ENVIAR_Im_INFORMACION(this, "047;" + MundoDofus.getPersonaje(_esposo).getNombre());
		_esposo = 0;
		GestorSQL.SALVAR_PERSONAJE(this, true);
	}
	
	public int getEsposo() {
		return _esposo;
	}
	
	public int setEsOK(int ok) {
		return _esOK = ok;
	}
	
	public int getEsOK() {
		return _esOK;
	}
	
	public void cambiarOrientacion(int orientacion) {
		if (_orientacion == 0 || _orientacion == 2 || _orientacion == 4 || _orientacion == 6) {
			setOrientacion(orientacion);
			GestorSalida.ENVIAR_eD_CAMBIAR_ORIENTACION(_mapa, getID(), orientacion);
		}
	}
	
	public void setCofre(Cofre cofre) {
		_cofre = cofre;
	}
	
	public Cofre getCofre() {
		return _cofre;
	}
	
	public void setCasa(Casa casa) {
		_casa = casa;
	}
	
	public Casa getCasa() {
		return _casa;
	}
	
	public String stringColorDueñoPavo() {
		return (_color1 == -1 ? "" : Integer.toHexString(_color1)) + "," + (_color2 == -1 ? "" : Integer.toHexString(_color2))
				+ "," + (_color3 == -1 ? "" : Integer.toHexString(_color3));
	}
	
	public String getModeloObjEnPos(int posiciones) {
		if (posiciones == -1) {
			return null;
		}
		for (Entry<Integer, Objeto> entry : _objetos.entrySet()) {
			Objeto obj = entry.getValue();
			if (obj.getPosicion() == posiciones) {
				if (obj.getObjeviID() != 0) {
					for (Objevivo objevi : MundoDofus.getTodosObjevivos()) {
						if (objevi.getID() == obj.getObjeviID()) {
							String toReturn = Integer.toHexString(objevi.getrealtemplate()) + "~" + objevi.getType() + "~"
									+ objevi.getMascara();
							return toReturn;
						}
					}
				} else {
					return Integer.toHexString(obj.getModelo().getID());
				}
			}
		}
		return null;
	}
	
	public boolean objetoAInvetario(int id) {
		if (this == null || _intercambioCon != _ID)
			return false;
		Objeto objMovido = null;
		for (Objeto objeto : _tienda) {
			if (objeto.getID() == id) {
				objMovido = objeto;
				break;
			}
		}
		if (objMovido == null)
			return false;
		_tienda.remove(objMovido);
		if (addObjetoSimilar(objMovido, true, -1))
			MundoDofus.eliminarObjeto(objMovido.getID());
		else {
			addObjetoPut(objMovido);
			GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(this, objMovido);
		}
		MundoDofus.borrarObjTienda(objMovido.getID());
		return true;
	}
	
	public int contarTienda() {
		int cantidad = _tienda.size();
		return cantidad;
	}
	
	public int maxTienda() {
		int cantidad = (int) _nivel / 10;
		return cantidad;
	}
	
	public String getStringTienda() {
		String str = "";
		for (Objeto objetos : _tienda) {
			str += objetos.getID() + "|";
		}
		return str;
	}
	
	public void agregarObjTienda(Objeto objeto) {
		_tienda.add(objeto);
	}
	
	public void borrarObjTienda(Objeto objeto) {
		if (_tienda.contains(objeto)) {
			_tienda.remove(objeto);
			MundoDofus.borrarObjTienda(objeto.getID());
		}
		GestorSQL.SALVAR_PERSONAJE(this, true);
	}
	
	public String listaTienda() {
		String lista = "";
		boolean esPrimero = true;
		if (_tienda.isEmpty()) {
			return lista;
		}
		for (Objeto objeto : _tienda) {
			if (!esPrimero)
				lista += "|";
			int idobjeto = objeto.getID();
			Tienda tienda = MundoDofus.getObjTienda(idobjeto);
			if (tienda == null)
				continue;
			lista += idobjeto + ";" + tienda.getCantidad() + ";" + objeto.getModelo().getID() + ";"
					+ objeto.convertirStatsAString() + ";" + tienda.getPrecio();
			esPrimero = false;
		}
		return lista;
	}
	
	public ArrayList<Objeto> getTienda() {
		return _tienda;
	}
	
	public void actualizarObjTienda(int itemId, int precio) {
		Tienda tienda = MundoDofus.getObjTienda(itemId);
		tienda.setPrecio(precio);
		GestorSQL.ACTUALIZAR_PRECIO_TIENDA(itemId, precio);
	}
	
	public long precioTotal() {
		long precio = 0;
		for (Objeto obj : _tienda) {
			Tienda tienda = MundoDofus.getObjTienda(obj.getID());
			if (tienda == null)
				return 0;
			precio += tienda.getPrecio();
		}
		return precio;
	}
	
	public void refrescarSetClase() {
		for (int j = 2; j < 8; j++) {
			if (getObjPosicion(j) == null)
				continue;
			Objeto obj = getObjPosicion(j);
			int template = obj.getModelo().getID();
			int set = obj.getModelo().getSetID();
			if ( (set >= 81 && set <= 92) || (set >= 201 && set <= 212)) {
				String[] stats = obj.getModelo().getStringStatsObj().split(",");
				for (String stat : stats) {
					String[] val = stat.split("#");
					int efecto = Integer.parseInt(val[0], 16);
					int hechizo = Integer.parseInt(val[1], 16);
					int modif = Integer.parseInt(val[3], 16);
					String modificacion = efecto + ";" + hechizo + ";" + modif;
					GestorSalida.ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(this, modificacion);
					addHechizosSetClase(hechizo, efecto, modif);
				}
				if (!_setClase.contains(template))
					_setClase.add(template);
			}
		}
	}
	
	public int getModifSetClase(int hechizo, int efecto) {
		int modif = 0;
		if (_bendHechizo == hechizo && _bendEfecto == efecto) {
			modif += _bendModif;
		}
		if (_hechizosSetClase.containsKey(hechizo)) {
			if (_hechizosSetClase.get(hechizo)._primero == efecto) {
				modif += _hechizosSetClase.get(hechizo)._segundo;
				return modif;
			}
		}
		return modif;
	}
	
	public String analizarPrismas() {
		String str = "";
		Prisma prisma = MundoDofus.getPrisma(_mapa.getSubArea().getPrismaID());
		if (prisma == null)
			str = "-3";
		else if (prisma.getEstadoPelea() == 0) {
			str = "0;" + prisma.getTiempoTurno() + ";45000;7";
		} else {
			str = prisma.getEstadoPelea() + "";
		}
		return str;
	}
	private int _objetoIDRomper = 0;
	private boolean _rompiendo = false;
	
	public void setObjetoARomper(int objetoID) {
		_objetoIDRomper = objetoID;
	}
	
	public int getObjetoARomper() {
		return _objetoIDRomper;
	}
	
	public void setRompiendo(boolean romper) {
		_rompiendo = romper;
	}
	
	public boolean getRompiendo() {
		return _rompiendo;
	}
	
	public void restarVidaMascota(Mascota mascota) {
		Objeto masc = null;
		if (mascota == null) {
			masc = getObjPosicion(8);
		} else
			masc = MundoDofus.getObjeto(mascota.getID());
		if (masc != null) {
			int idMascota = masc.getID();
			if (mascota == null)
				mascota = MundoDofus.getMascota(idMascota);
			if (mascota.getPDV() > 1) {
				mascota.setPDV(mascota.getPDV() - 1);
				GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(this, masc);
			} else {
				mascota.setPDV(0);
				GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(this, idMascota);
				int nuevoModelo = CentroInfo.fantasmaMascota(masc.getIDModelo());
				if (nuevoModelo != 0) {
					masc.setPosicion(-1);
					masc.setIDModelo(nuevoModelo);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(this, masc);
					GestorSQL.SALVAR_OBJETO(masc);
					_mascota = null;
				} else {
					borrarObjetoRemove(idMascota);
					MundoDofus.eliminarObjeto(idMascota);
					_mascota = null;
				}
				refrescarVida();
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(this);
				GestorSalida.ENVIAR_Im_INFORMACION(this, "154");
			}
		}
	}
	public static class GrupoKoliseo {
		private Personaje _koli1;
		private Personaje _koli2;
		private Personaje _koli3;
		private int _sumaNivel = 0;
		private int _categoria = 0;
		
		public GrupoKoliseo(Personaje koli1, Personaje koli2, Personaje koli3, int categoria) {
			_koli1 = koli1;
			_koli2 = koli2;
			_koli3 = koli3;
			_koli1._categoria = categoria;
			_koli2._categoria = categoria;
			_koli3._categoria = categoria;
			_sumaNivel = _koli1.getNivel() + _koli2.getNivel() + _koli3.getNivel();
			_categoria = categoria;
		}
		
		public int getSumaNiveles() {
			return _sumaNivel;
		}
		
		public ArrayList<Personaje> getParticipantes() {
			ArrayList<Personaje> grupo = new ArrayList<Personaje>();
			grupo.add(_koli1);
			grupo.add(_koli2);
			grupo.add(_koli3);
			return grupo;
		}
		
		public int getCategoria() {
			return _categoria;
		}
	}
	
	public void setAceptar(boolean aceptar) {
		_aceptaKoli = aceptar;
	}
	
	public boolean getAceptar() {
		return _aceptaKoli;
	}
	
	public long expKoli() {
		switch (_categoria) {
			case 1: // menos lvl 100
				return ( (MundoDofus.getExpNivel(_nivel + 1)._personaje) - (MundoDofus.getExpNivel(_nivel)._personaje)) * 2;
			case 2: // menos lvl 200
				return ( (MundoDofus.getExpNivel(_nivel + 1)._personaje) - (MundoDofus.getExpNivel(_nivel)._personaje));
			case 3: // menos de lvl 300
				if (_nivel == 300)
					return 0;
				return ( (MundoDofus.getExpNivel(_nivel + 1)._personaje) - (MundoDofus.getExpNivel(_nivel)._personaje)) / 6;
		}
		return 0;
	}
	
	public long kamasKoli() {
		switch (_categoria) {
			case 1: // menos lvl 100
				return 20000;
			case 2: // menos lvl 150
				return 50000;
			case 3: // menos de lvl 300
				if (_nivel == 300)
					return 250000;
				return 100000;
		}
		return 0;
	}
}
