
package variables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.Timer;

import servidor.EntradaPersonaje.AccionDeJuego;
import variables.Mapa.Celda;
import variables.Mapa.ObjetoInteractivo;
import variables.Objeto.ObjetoModelo;
import estaticos.*;
import estaticos.MundoDofus.Duo;

public class Oficio {
	public static class StatsOficio {
		private int _posicion;
		private Oficio _oficio;
		private int _nivel;
		private long _exp;
		private ArrayList<AccionTrabajo> _trabajosPoderRealizar = new ArrayList<AccionTrabajo>();
		private boolean _esPagable;
		private boolean _gratisSiFalla;
		private boolean _noProporcRecurso;
		private int _slotsPublico;
		private int _adicional = 0;
		private AccionTrabajo _tempTrabajo;
		
		public StatsOficio(int posicion, Oficio oficio, int nivel, long xp) {
			_posicion = posicion;
			_oficio = oficio;
			_nivel = nivel;
			_exp = xp;
			_trabajosPoderRealizar = CentroInfo.getTrabajosPorOficios(oficio.getID(), nivel);
			_slotsPublico = CentroInfo.getIngMaxPorNivel(nivel);
		}
		
		public int getAdicional() {
			return _adicional;
		}
		
		public int getNivel() {
			return _nivel;
		}
		
		public boolean esPagable() {
			return _esPagable;
		}
		
		public boolean esGratisSiFalla() {
			return _gratisSiFalla;
		}
		
		public boolean noProveerRecuerso() {
			return _noProporcRecurso;
		}
		
		public void setSlotsPublico(int slots) {
			_slotsPublico = slots;
		}
		
		public int getSlotsPublico() {
			return _slotsPublico;
		}
		
		public void subirNivel(Personaje perso) {
			_nivel++;
			_trabajosPoderRealizar = CentroInfo.getTrabajosPorOficios(_oficio.getID(), _nivel);
			_adicional = (int) Math.sqrt(_nivel * 2) + (_nivel / 20);
		}
		
		public String analizarTrabajolOficio() {
			String str = "|" + _oficio.getID() + ";";
			boolean primero = true;
			for (AccionTrabajo AT : _trabajosPoderRealizar) {
				if (!primero)
					str += ",";
				else
					primero = false;
				str += AT.getIDTrabajo() + "~" + AT.getCasillasMax() + "~";
				if (AT.esReceta())
					str += "0~0~" + AT.getSuerte();
				else
					str += AT.getCasillasMin() + "~0~" + AT.getTiempo();
			}
			return str;
		}
		
		public long getXP() {
			return _exp;
		}
		
		public void iniciarTrabajo(int idTrabajo, Personaje perso, ObjetoInteractivo OI, AccionDeJuego AJ, Celda celda) {
			for (AccionTrabajo AT : _trabajosPoderRealizar) {
				if (AT.getIDTrabajo() == idTrabajo) {
					_tempTrabajo = AT;
					AT.iniciarAccionTrabajo(perso, OI, AJ, celda);
					return;
				}
			}
		}
		
		public void finalizarTrabajo(int idTrabajo, Personaje perso, AccionDeJuego AJ, Celda celda) {
			if (_tempTrabajo == null)
				return;
			_tempTrabajo.finalizarAccionTrabajo(perso, AJ, celda);
			addXP(perso, (int) (_tempTrabajo.getXpGanada() * Bustemu.RATE_XP_OFICIO));
		}
		
		public void addXP(Personaje perso, long xp) {
			if (_nivel > Bustemu.MAX_NIVEL_OFICIO - 1)
				return;
			int exNivel = _nivel;
			_exp += xp;
			while (_exp >= MundoDofus.getExpNivel(_nivel + 1)._oficio && _nivel < Bustemu.MAX_NIVEL_OFICIO)
				subirNivel(perso);
			ArrayList<StatsOficio> list = new ArrayList<StatsOficio>();
			list.add(this);
			if (perso.enLinea()) {
				if (_nivel >= Bustemu.MAX_NIVEL_OFICIO) {
					perso.setTitulo(CentroInfo.getIdTituloOficio(this._oficio._id));
					GestorSQL.SALVAR_PERSONAJE(perso, false);
					if (perso.getPelea() == null)
						GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(perso.getMapa(), perso);
				}
				if (_nivel > exNivel) {
					GestorSalida.ENVIAR_JS_TRABAJO_POR_OFICIO(perso, list);
					GestorSalida.ENVIAR_JN_OFICIO_NIVEL(perso, _oficio.getID(), _nivel);
					GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
					GestorSalida.ENVIAR_JO_OFICIO_OPCIONES(perso, list);
				}
				GestorSalida.ENVIAR_JX_EXPERINENCIA_OFICIO(perso, list);
			}
		}
		
		public String getXpString(String s) {
			String str = MundoDofus.getExpNivel(_nivel)._oficio + s;
			str += _exp + s;
			str += MundoDofus.getExpNivel( (_nivel < Bustemu.MAX_NIVEL_OFICIO ? _nivel + 1 : _nivel))._oficio;
			return str;
		}
		
		public Oficio getOficio() {
			return _oficio;
		}
		
		public boolean esValidoTrabajo(int id) {
			for (AccionTrabajo AT : _trabajosPoderRealizar)
				if (AT.getIDTrabajo() == id)
					return true;
			return false;
		}
		
		public int getOpcionBin() {
			int nro = 0;
			nro += (_noProporcRecurso ? 4 : 0);
			nro += (_gratisSiFalla ? 2 : 0);
			nro += (_esPagable ? 1 : 0);
			return nro;
		}
		
		public void setOpciones(int bin) {
			_noProporcRecurso = (bin & 4) == 4;
			_gratisSiFalla = (bin & 2) == 2;
			_esPagable = (bin & 1) == 1;
		}
		
		public int getPosicion() {
			return _posicion;
		}
	}
	public static class AccionTrabajo {
		private int _idTrabajoMod;
		private int _casillasMin = 1;
		private int _casillasMax = 1;
		private boolean _esReceta;
		private int _suerte = 100;
		private int _tiempo = 0;
		private int _xpGanada = 0;
		private long _iniciarTiempo;
		private Map<Integer, Integer> _ingredientes = new TreeMap<Integer, Integer>();
		private Map<Integer, Integer> _ultimoTrabajo = new TreeMap<Integer, Integer>();
		private Timer _tiempoTrabajo;
		private Personaje _artesano;
		private boolean _interrumpir = false;
		private Personaje _cliente;
		private int _veces = -1;
		public static float _tolerNormal = 1.3f;
		public static float _tolerVIP = 2.8f;
		private String _datos = "";
		
		public AccionTrabajo(int idTrabajo, int min, int max, boolean esReceta, int nSuerteTiempo, int xpGanada) {
			_idTrabajoMod = idTrabajo;
			_casillasMin = min;
			_casillasMax = max;
			_esReceta = esReceta;
			if (esReceta)
				_suerte = nSuerteTiempo;
			else
				_tiempo = nSuerteTiempo;
			_xpGanada = xpGanada;
			_tiempoTrabajo = new Timer(800, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					iniciarReceta();
					_tiempoTrabajo.stop();
				}
			});
		}
		
		public void finalizarAccionTrabajo(Personaje perso, AccionDeJuego AJ, Celda celda) {
			ObjetoInteractivo OI = celda.getObjetoInterac();
			if (_esReceta) {
				GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE(perso, celda.getID(), 1, 1);
			} else if (OI != null) {
				if (_iniciarTiempo - System.currentTimeMillis() > 500)
					return;
				if (OI != null) {
					OI.iniciarTiempoRefresco();
					GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(perso.getMapa(), celda);
					OI.setEstado(CentroInfo.IO_ESTADO_VACIO);
				}
				boolean especial = Formulas.getRandomValor(0, 99) == 0;
				int cant = _casillasMax > _casillasMin ? Formulas.getRandomValor(_casillasMin, _casillasMax) : _casillasMin;
				int idObjModelo = CentroInfo.getObjetoPorTrabajo(_idTrabajoMod, especial);
				ObjetoModelo OM = MundoDofus.getObjModelo(idObjModelo);
				if (OM == null)
					return;
				Objeto objeto = OM.crearObjDesdeModelo(cant, false);
				if (!perso.addObjetoSimilar(objeto, true, -1)) {
					MundoDofus.addObjeto(objeto, true);
					perso.addObjetoPut(objeto);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(perso, objeto);
				}
				GestorSalida.ENVIAR_IQ_NUMERO_ARRIBA_PJ(perso, perso.getID(), cant);
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(perso);
			}
		}
		
		public void iniciarAccionTrabajo(Personaje perso, ObjetoInteractivo OI, AccionDeJuego AJ, Celda celda) {
			_artesano = perso;
			if (_esReceta) {
				perso.setOcupado(true);
				perso.setHaciendoTrabajo(this);
				GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso, 3, _casillasMax + ";" + _idTrabajoMod);
				GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE(perso, celda.getID(), 2, 1);
			} else {
				OI.setInteractivo(false);
				OI.setEstado(CentroInfo.IO_ESTADO_ESPERA);
				GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(perso.getMapa(), "" + AJ._idUnica, 501, perso.getID() + "",
						celda.getID() + "," + _tiempo);
				GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(perso.getMapa(), celda);
				_iniciarTiempo = System.currentTimeMillis() + _tiempo;
			}
		}
		
		public int getIDTrabajo() {
			return _idTrabajoMod;
		}
		
		public int getCasillasMin() {
			return _casillasMin;
		}
		
		public int getCasillasMax() {
			return _casillasMax;
		}
		
		public int getXpGanada() {
			return _xpGanada;
		}
		
		public int getSuerte() {
			return _suerte;
		}
		
		public int getTiempo() {
			return _tiempo;
		}
		
		public boolean esReceta() {
			return _esReceta;
		}
		private boolean _primero = false;
		
		public void modificarIngrediente(PrintWriter out, int id, int cant) {
			int c = _ingredientes.get(id) == null ? 0 : _ingredientes.get(id);
			_ingredientes.remove(id);
			c += cant;
			if (c > 0) {
				_ingredientes.put(id, c);
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(out, 'O', "+", id + "|" + c);
			} else
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(out, 'O', "-", id + "");
		}
		
		public void iniciarReceta() {
			if (!_esReceta)
				return;
			boolean firmado = false;
			if (_idTrabajoMod == 1 || _idTrabajoMod == 113 || _idTrabajoMod == 115 || _idTrabajoMod == 116
					|| _idTrabajoMod == 117 || _idTrabajoMod == 118 || _idTrabajoMod == 119 || _idTrabajoMod == 120
					|| (_idTrabajoMod >= 163 && _idTrabajoMod <= 169)) {
				recetaForjaMagia();
				return;
			}
			try {
				PrintWriter out = _artesano.getCuenta().getEntradaPersonaje().getOut();
				Map<Integer, Integer> ingredPorModelo = new TreeMap<Integer, Integer>();
				for (Entry<Integer, Integer> ingrediente : _ingredientes.entrySet()) {
					int idObjeto = ingrediente.getKey();
					int cantObjeto = ingrediente.getValue();
					if (!_artesano.tieneObjetoID(idObjeto)) {
						GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
						return;
					}
					Objeto obj = MundoDofus.getObjeto(idObjeto);
					if (obj == null || obj.getCantidad() < cantObjeto) {
						GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
						return;
					}
					if (_primero)
						GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(out, 'O', "+", idObjeto + "|" + cantObjeto);
					int nuevaCant = obj.getCantidad() - cantObjeto;
					if (nuevaCant == 0) {
						_artesano.borrarObjetoRemove(idObjeto);
						MundoDofus.eliminarObjeto(idObjeto);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, idObjeto);
					} else {
						obj.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, obj);
					}
					ingredPorModelo.put(obj.getModelo().getID(), cantObjeto);
				}
				_primero = true;
				if (ingredPorModelo.containsKey(7508))
					firmado = true;
				ingredPorModelo.remove(7508);
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano);
				StatsOficio SO = _artesano.getOficioPorTrabajo(_idTrabajoMod);
				int idReceta = MundoDofus.getIDRecetaPorIngredientes(SO.getOficio().listaRecetaPorTrabajo(_idTrabajoMod),
						ingredPorModelo);
				if (idReceta == -1 || !SO.getOficio().puedeReceta(_idTrabajoMod, idReceta)) {
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
					GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-");
					_ingredientes.clear();
					return;
				}
				int suerte = CentroInfo.getSuertePorNroCasillaYNivel(SO.getNivel(), _ingredientes.size());
				int jet = Formulas.getRandomValor(1, 100);
				boolean exito = suerte >= jet;
				if (!exito) {
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EF");
					GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-" + idReceta);
					GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0118");
				} else {
					Objeto objCreado = MundoDofus.getObjModelo(idReceta).crearObjDesdeModelo(1, false);
					if (firmado)
						objCreado.addTextoStat(988, _artesano.getNombre());
					Objeto igual = null;
					if ( (igual = _artesano.getObjSimilarInventario(objCreado)) == null) {
						MundoDofus.addObjeto(objCreado, true);
						_artesano.addObjetoPut(objCreado);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_artesano, objCreado);
					} else {
						igual.setCantidad(igual.getCantidad() + 1);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, igual);
						objCreado = igual;
					}
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano);
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_artesano.getCuenta().getEntradaPersonaje().getOut(), 'O', "+",
							objCreado.stringObjetoConPalo(1));
					GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "+" + idReceta);
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "K;" + idReceta);
				}
				int xpGanada = (int) (CentroInfo.calculXpGanadaEnOficio(SO.getNivel(), _ingredientes.size()) * Bustemu.RATE_XP_OFICIO);
				SO.addXP(_artesano, xpGanada);
				ArrayList<StatsOficio> statsOficios = new ArrayList<StatsOficio>();
				statsOficios.add(SO);
				GestorSalida.ENVIAR_JX_EXPERINENCIA_OFICIO(_artesano, statsOficios);
				_ultimoTrabajo.clear();
				_ultimoTrabajo.putAll(_ingredientes);
				_ingredientes.clear();
				try {
					Thread.sleep(200);
				} catch (Exception e) {}
			} catch (Exception e) {
				return;
			}
		}
		
		private void recetaForjaMagia() {
			boolean firmado = false;
			Objeto objAMaguear = null, objRunaFirma = null, objModificador = null;
			int nivelRunaElemento = 0, statAgre = -1, nivelRunaStats = 0, agregar = 0, idABorrar = -1;
			int runa = 0;
			boolean vip = false;
			String statAMaguear = "-1";
			for (int idIngrediente : _ingredientes.keySet()) {
				Objeto ing = MundoDofus.getObjeto(idIngrediente);
				if (ing == null || !_artesano.tieneObjetoID(idIngrediente)) {
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
					GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-");
					_ingredientes.clear();
					return;
				}
				int idModelo = ing.getModelo().getID();
				switch (idModelo) {
					case 1333:// pocion chispa
						statAgre = 99;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1335:// pocion llovisna
						statAgre = 96;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1337:// pocion de corriente de airee
						statAgre = 98;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1338:// pocion de sacudida
						statAgre = 97;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1340:// pocion derrumbamiento
						statAgre = 97;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1341:// pocion chaparron
						statAgre = 96;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1342:// pocion de rafaga
						statAgre = 98;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1343:// pocion de Flameacion
						statAgre = 99;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1345:// pocion Incendio
						statAgre = 99;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1346:// pocion Tsunami
						statAgre = 96;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1347:// pocion huracan
						statAgre = 98;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1348:// pocion de seismo
						statAgre = 97;
						nivelRunaElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1519:// Fuerza
						objModificador = ing;
						statAMaguear = "76";
						agregar = 1;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1521:// Sabiduria
						objModificador = ing;
						statAMaguear = "7c";
						agregar = 1;
						runa = 6;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1522:// Inteligencia
						objModificador = ing;
						statAMaguear = "7e";
						agregar = 1;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1523:// Vita
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 3;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1524:// Agi
						objModificador = ing;
						statAMaguear = "77";
						agregar = 1;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1525:// suerte
						objModificador = ing;
						statAMaguear = "7b";
						agregar = 1;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1545:// Pa fuerza
						objModificador = ing;
						statAMaguear = "76";
						agregar = 3;
						runa = 3;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1546:// Pa Sabiduria
						objModificador = ing;
						statAMaguear = "7c";
						agregar = 3;
						runa = 18;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1547:// Pa Inteligencia
						objModificador = ing;
						statAMaguear = "7e";
						agregar = 3;
						runa = 3;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1548:// Pa Vitalidad
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 10;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1549:// Pa agilidad
						objModificador = ing;
						statAMaguear = "77";
						agregar = 3;
						runa = 3;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1550:// Pa suerte
						objModificador = ing;
						statAMaguear = "7b";
						agregar = 3;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1551:// Ra Fuerza
						objModificador = ing;
						statAMaguear = "76";
						agregar = 10;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1552:// Ra Sabiduria
						objModificador = ing;
						statAMaguear = "7c";
						agregar = 10;
						runa = 50;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1553:// Ra Iniciativa
						objModificador = ing;
						statAMaguear = "7e";
						agregar = 10;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1554:// Ra Vida
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 30;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1555:// Ra Agilidad
						objModificador = ing;
						statAMaguear = "77";
						agregar = 10;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1556:// Ra suerte
						objModificador = ing;
						statAMaguear = "7b";
						agregar = 10;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1557:// Ga PA
						objModificador = ing;
						statAMaguear = "6f";
						agregar = 1;
						runa = 100;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 1558:// Ga PM
						objModificador = ing;
						statAMaguear = "80";
						agregar = 1;
						runa = 90;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7433:// Criticos
						objModificador = ing;
						statAMaguear = "73";
						agregar = 1;
						runa = 30;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7434:// curas
						objModificador = ing;
						statAMaguear = "b2";
						agregar = 1;
						runa = 20;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7435:// daños
						objModificador = ing;
						statAMaguear = "70";
						agregar = 1;
						runa = 20;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7436:// daños %
						objModificador = ing;
						statAMaguear = "8a";
						agregar = 1;
						runa = 2;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7437:// daños reenvio
						objModificador = ing;
						statAMaguear = "dc";
						agregar = 1;
						runa = 2;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7438:// alcance
						objModificador = ing;
						statAMaguear = "75";
						agregar = 1;
						runa = 50;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7442:// invocaciones
						objModificador = ing;
						statAMaguear = "b6";
						agregar = 1;
						runa = 30;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7443:// Pod
						objModificador = ing;
						statAMaguear = "9e";
						agregar = 10;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7444:// Pa pod
						objModificador = ing;
						statAMaguear = "9e";
						agregar = 30;
						runa = 1; // ?
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7445:// Ra pod
						objModificador = ing;
						statAMaguear = "9e";
						agregar = 100;
						runa = 1; // ?
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7446:// trampa
						objModificador = ing;
						statAMaguear = "e1";
						agregar = 1;
						runa = 15;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7447:// trampa %
						objModificador = ing;
						statAMaguear = "e2";
						agregar = 1;
						runa = 2;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7448:// iniciativa
						objModificador = ing;
						statAMaguear = "ae";
						agregar = 10;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7449:// Pa iniciativa
						objModificador = ing;
						statAMaguear = "ae";
						agregar = 30;
						runa = 3;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7450:// Ra iniciativa
						objModificador = ing;
						statAMaguear = "ae";
						agregar = 100;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7451:// Prospeccion
						objModificador = ing;
						statAMaguear = "b0";
						agregar = 1;
						runa = 5;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7452:// resistencia fuego
						objModificador = ing;
						statAMaguear = "f3";
						agregar = 1;
						runa = 4;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7453:// resistencia aire
						objModificador = ing;
						statAMaguear = "f2";
						agregar = 1;
						runa = 4;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7454:// resistencia agua
						objModificador = ing;
						statAMaguear = "f1";
						agregar = 1;
						runa = 4;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7455:// resistencia tierra
						objModificador = ing;
						statAMaguear = "f0";
						agregar = 1;
						runa = 4;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7456:// resistencia neutral
						objModificador = ing;
						statAMaguear = "f4";
						agregar = 1;
						runa = 4;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7457:// resistencia % fuego
						objModificador = ing;
						statAMaguear = "d5";
						agregar = 1;
						runa = 5;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7458:// resistencia % aire
						objModificador = ing;
						statAMaguear = "d4";
						agregar = 1;
						runa = 5;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7459:// resistencia % tierra
						objModificador = ing;
						statAMaguear = "d2";
						agregar = 1;
						runa = 5;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7460:// resistencia % neutral
						objModificador = ing;
						statAMaguear = "d6";
						agregar = 1;
						runa = 5;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7560:// resistencia % agua
						objModificador = ing;
						statAMaguear = "d3";
						agregar = 1;
						runa = 5;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 8379:// runa vida
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 10;
						runa = 10;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 10662:// runa ra prospe
						objModificador = ing;
						statAMaguear = "b0";
						agregar = 3;
						runa = 15;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 7508:// Runa de Firma
						firmado = true;
						objRunaFirma = ing;
						break;
					case 11118:// Fuerza
						vip = true;
						objModificador = ing;
						statAMaguear = "76";
						agregar = 15;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11119:// Sabiduria
						vip = true;
						objModificador = ing;
						statAMaguear = "7c";
						agregar = 15;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11120:// Inteligencia
						vip = true;
						objModificador = ing;
						statAMaguear = "7e";
						agregar = 15;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11121:// Vita
						vip = true;
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 45;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11122:// Agi
						vip = true;
						objModificador = ing;
						statAMaguear = "77";
						agregar = 15;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11123:// suerte
						vip = true;
						objModificador = ing;
						statAMaguear = "7b";
						agregar = 15;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11124:// Prospeccion
						vip = true;
						objModificador = ing;
						statAMaguear = "b0";
						agregar = 10;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11125:// Criticos
						vip = true;
						objModificador = ing;
						statAMaguear = "73";
						agregar = 3;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11126:// curas
						vip = true;
						objModificador = ing;
						statAMaguear = "b2";
						agregar = 5;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11127:// daños
						vip = true;
						objModificador = ing;
						statAMaguear = "70";
						agregar = 5;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11128:// daños %
						vip = true;
						objModificador = ing;
						statAMaguear = "8a";
						agregar = 10;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					case 11129:// daños reenvio
						vip = true;
						objModificador = ing;
						statAMaguear = "dc";
						agregar = 5;
						runa = 1;
						nivelRunaStats = ing.getModelo().getNivel();
						break;
					default:
						int tipo = ing.getModelo().getTipo();
						if ( (tipo >= 1 && tipo <= 11) || (tipo >= 16 && tipo <= 22) || tipo == 81 || tipo == 102 || tipo == 114
								|| ing.getModelo().getCostePA() > 0) {
							objAMaguear = ing;
							GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_artesano.getCuenta().getEntradaPersonaje().getOut(), 'O',
									"+", objAMaguear.getID() + "|" + 1);
							idABorrar = idIngrediente;
							Objeto nuevoObj = Objeto.clonarObjeto(objAMaguear, 1);
							if (objAMaguear.getCantidad() > 1) {
								int nuevaCant = objAMaguear.getCantidad() - 1;
								objAMaguear.setCantidad(nuevaCant);
								GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objAMaguear);
								break;
							} else {
								MundoDofus.eliminarObjeto(idIngrediente);
								_artesano.borrarObjetoRemove(idIngrediente);
								GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, idIngrediente);
							}
							objAMaguear = nuevoObj;
						}
				}
			}
			StatsOficio oficio = _artesano.getOficioPorTrabajo(_idTrabajoMod);
			oficio.addXP(_artesano, (int) (Bustemu.RATE_XP_OFICIO + 9.0 / 10.0) * 10);
			if (oficio == null || objAMaguear == null || objModificador == null) {
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-");
				_ingredientes.clear();
				return;
			}
			if (idABorrar != -1) {
				_ingredientes.remove(idABorrar);
			}
			ObjetoModelo objModelo = objAMaguear.getModelo();
			int suerte = 0;
			int nivelOficio = oficio.getNivel();
			int objModeloID = objModelo.getID();
			String statStringObj = objAMaguear.convertirStatsAString();
			if (nivelRunaElemento > 0 && nivelRunaStats == 0) {// si cambia de elemento
				suerte = Formulas.calculoPorcCambioElenemto(nivelOficio, objModelo.getNivel(), nivelRunaElemento);
				if (suerte > 100 - (nivelOficio / 20))
					suerte = 100 - (nivelOficio / 20);
				if (suerte < (nivelOficio / 20))
					suerte = (nivelOficio / 20);
			} else if (nivelRunaStats > 0 && nivelRunaElemento == 0) {// si cambia de stats
				int pesoTotalActual = 1;
				int pesoActualStat = 1;
				if (!statStringObj.isEmpty()) {
					pesoTotalActual = pesoTotalActualObj(statStringObj, objAMaguear);
					pesoActualStat = pesoStatActual(objAMaguear, statAMaguear);
				}
				int pesoTotalBase = pesoTotalBaseObj(objModeloID);
				if (pesoTotalBase < 0) {
					pesoTotalBase = 0;
				}
				if (pesoActualStat < 0) {
					pesoActualStat = 0;
				}
				if (pesoTotalActual < 0) {
					pesoTotalActual = 0;
				}
				float coef = 1;
				int tieneStatBase = statBaseObjeto(objAMaguear, statAMaguear);
				int tieneStatActual = statActualObjeto(objAMaguear, statAMaguear);
				if (tieneStatBase == 1 && tieneStatActual == 1 || tieneStatBase == 1 && tieneStatActual == 0) {
					coef = 1.0f;
				} else if (tieneStatBase == 2 && tieneStatActual == 2) {
					coef = 0.50f;
				} else if (tieneStatBase == 0 && tieneStatActual == 0 || tieneStatBase == 0 && tieneStatActual == 1) {
					coef = 0.25f;
				}
				float tolerancia = vip ? _tolerVIP : _tolerNormal;
				int diferencia = (int) (pesoTotalBase * tolerancia) - pesoTotalActual;
				suerte = (int) Formulas.suerteFM(pesoTotalBase, pesoTotalActual, pesoActualStat, runa, diferencia, coef);
				suerte = suerte + oficio.getAdicional();
				if (vip)
					suerte += 30;
				if (suerte <= 0)
					suerte = 1;
				else if (suerte > 100)
					suerte = 100;
				// GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_artesano, "TU EXITO / VOUS SUCCES : " + suerte + "%", "FF0000");
			}
			int jet = Formulas.getRandomValor(1, 100);
			boolean exito = suerte >= jet;
			if (!exito) { // falla magueo
				int romper = Formulas.getRandomValor(1, 100);
				if (objRunaFirma != null) {
					int nuevaCant = objRunaFirma.getCantidad() - 1;
					if (nuevaCant <= 0) {
						_artesano.borrarObjetoRemove(objRunaFirma.getID());
						MundoDofus.eliminarObjeto(objRunaFirma.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, objRunaFirma.getID());
					} else {
						objRunaFirma.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objRunaFirma);
					}
				}
				if (objModificador != null) {
					int nuevaCant = objModificador.getCantidad() - 1;
					if (nuevaCant <= 0) {
						_artesano.borrarObjetoRemove(objModificador.getID());
						MundoDofus.eliminarObjeto(objModificador.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, objModificador.getID());
					} else {
						objModificador.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objModificador);
					}
				}
				if (romper == 50) {
					_interrumpir = true;
				} else {
					MundoDofus.addObjeto(objAMaguear, false);
					_artesano.addObjetoPut(objAMaguear);
					if (!statStringObj.isEmpty()) {
						String statsStr = objAMaguear.stringStatsFCForja(objAMaguear, runa);
						objAMaguear.clearTodo();
						objAMaguear.convertirStringAStats(statsStr);
					}
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_artesano, objAMaguear);
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano);
					String datos = objAMaguear.stringObjetoConPalo(1);
					if (_veces != 0)
						GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_artesano.getCuenta().getEntradaPersonaje().getOut(), 'O',
								"+", datos);
					_datos = datos;
				}
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-" + objModeloID);
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EF");
				GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0183");
				if (_interrumpir)
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_artesano,
							"El magueo ha fallado y el objeto ha sido destruido. Le mage failed, et objet deleted.");
			} else {
				int coef = 0;
				if (nivelRunaElemento == 1)
					coef = 50;
				else if (nivelRunaElemento == 25)
					coef = 65;
				else if (nivelRunaElemento == 50)
					coef = 85;
				if (firmado) {
					objAMaguear.addTextoStat(985, _artesano.getNombre());
				}
				if (nivelRunaElemento > 0 && nivelRunaStats == 0) {
					for (EfectoHechizo EH : objAMaguear.getEfectos()) {
						if (EH.getEfectoID() != 100)
							continue;
						String[] infos = EH.getArgs().split(";");
						try {
							int min = Integer.parseInt(infos[0], 16);
							int max = Integer.parseInt(infos[1], 16);
							int nuevoMin = (int) ( (min * coef) / 100);
							int nuevoMax = (int) ( (max * coef) / 100);
							if (nuevoMin == 0)
								nuevoMin = 1;
							String nuevoRango = "1d" + (nuevoMax - nuevoMin + 1) + "+" + (nuevoMin - 1);
							String nuevosArgs = Integer.toHexString(nuevoMin) + ";" + Integer.toHexString(nuevoMax) + ";-1;-1;0;"
									+ nuevoRango;
							EH.setArgs(nuevosArgs);
							EH.setEfectoID(statAgre);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else if (nivelRunaStats > 0 && nivelRunaElemento == 0) {// Si se modifica los stats(runa)
					boolean negativo = false;
					int actualStat = statActualObjeto(objAMaguear, statAMaguear);
					if (actualStat == 2) {// los stats existentes actual son negativos
						if (statAMaguear.compareTo("7b") == 0) {
							statAMaguear = "98";
							negativo = true;
						}
						if (statAMaguear.compareTo("77") == 0) {
							statAMaguear = "9a";
							negativo = true;
						}
						if (statAMaguear.compareTo("7e") == 0) {
							statAMaguear = "9b";
							negativo = true;
						}
						if (statAMaguear.compareTo("76") == 0) {
							statAMaguear = "9d";
							negativo = true;
						}
						if (statAMaguear.compareTo("7c") == 0) {
							statAMaguear = "9c";
							negativo = true;
						}
						if (statAMaguear.compareTo("7d") == 0) {
							statAMaguear = "99";
							negativo = true;
						}
					}
					if (actualStat == 1 || actualStat == 2) {
						String statsStr = objAMaguear.convertirStatsAStringFM(statAMaguear, objAMaguear, agregar, negativo);
						objAMaguear.clearTodo();
						objAMaguear.convertirStringAStats(statsStr);
					} else {
						if (statStringObj.isEmpty()) {
							String statsStr = statAMaguear + "#" + Integer.toHexString(agregar) + "#0#0#0d0+" + agregar;
							objAMaguear.clearTodo();
							objAMaguear.convertirStringAStats(statsStr);
						} else {
							String statsStr = objAMaguear.convertirStatsAStringFM(statAMaguear, objAMaguear, agregar, negativo)
									+ "," + statAMaguear + "#" + Integer.toHexString(agregar) + "#0#0#0d0+" + agregar;
							objAMaguear.clearTodo();
							objAMaguear.convertirStringAStats(statsStr);
						}
					}
				}
				if (objRunaFirma != null) {
					int nuevaCant = objRunaFirma.getCantidad() - 1;
					if (nuevaCant <= 0) {
						_artesano.borrarObjetoRemove(objRunaFirma.getID());
						MundoDofus.eliminarObjeto(objRunaFirma.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, objRunaFirma.getID());
					} else {
						objRunaFirma.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objRunaFirma);
					}
				}
				if (objModificador != null) {
					int nuevaCant = objModificador.getCantidad() - 1;
					if (nuevaCant <= 0) {
						_artesano.borrarObjetoRemove(objModificador.getID());
						MundoDofus.eliminarObjeto(objModificador.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, objModificador.getID());
					} else {
						objModificador.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objModificador);
					}
				}
				MundoDofus.addObjeto(objAMaguear, false);
				_artesano.addObjetoPut(objAMaguear);
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano);// Pods
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_artesano, objAMaguear);
				String datos = objAMaguear.stringObjetoConPalo(1);
				if (_veces != 0)
					GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_artesano.getCuenta().getEntradaPersonaje().getOut(), 'O', "+",
							datos);
				_datos = datos;
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "+" + objModeloID);
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "K;" + objModeloID);// Exito
			}
			_ultimoTrabajo.clear();
			_ultimoTrabajo.putAll(_ingredientes);
			_ultimoTrabajo.put(objAMaguear.getID(), 1);
			_ingredientes.clear();
			try {
				Thread.sleep(200);
			} catch (Exception e) {}
		}
		
		public void unaMagueada() {
			_primero = false;
			_tiempoTrabajo.start();
			enviarEmK(_datos);
		}
		
		public void variasMagueadas(int tiempo, Personaje perso) {
			_tiempoTrabajo.stop();
			_ultimoTrabajo.clear();
			_ultimoTrabajo.putAll(_ingredientes);
			for (int a = tiempo; a >= 0; a--) {
				_ingredientes.clear();
				if (_interrumpir) {
					GestorSalida.ENVIAR_Ea_TERMINO_RECETAS(perso, "2");
					enviarEmK(_datos);
					return;
				}
				_veces = a;
				GestorSalida.ENVIAR_EA_TURNO_RECETA(perso, a + "");
				_ingredientes.putAll(_ultimoTrabajo);
				iniciarReceta();
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {}
			}
			GestorSalida.ENVIAR_Ea_TERMINO_RECETAS(perso, "1");
			enviarEmK(_datos);
		}
		
		private void enviarEmK(String str) {
			if (!str.isEmpty())
				GestorSalida
						.ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_artesano.getCuenta().getEntradaPersonaje().getOut(), 'O', "+", str);
		}
		
		public void ponerIngredUltRecet() {
			if (_artesano == null || _ultimoTrabajo == null || !_ingredientes.isEmpty())
				return;
			_ingredientes.clear();
			_ingredientes.putAll(_ultimoTrabajo);
			for (Entry<Integer, Integer> e : _ingredientes.entrySet()) {
				Objeto objeto = MundoDofus.getObjeto(e.getKey());
				if (objeto == null)
					continue;
				if (objeto.getCantidad() < e.getValue())
					continue;
				GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL(_artesano.getCuenta().getEntradaPersonaje().getOut(), 'O', "+",
						e.getKey() + "|" + e.getValue());
			}
		}
		
		public void resetReceta() {
			_ingredientes.clear();
			_ultimoTrabajo.clear();
		}
		
		public boolean iniciarTrabajoPago(Personaje artesano, Personaje cliente, ArrayList<Duo<Integer, Integer>> objArtesano,
				ArrayList<Duo<Integer, Integer>> objCliente) {
			if (!_esReceta)
				return false;
			_artesano = artesano;
			_cliente = cliente;
			boolean firmado = false;
			for (Duo<Integer, Integer> duo : objArtesano) {
				_ingredientes.put(duo._primero, duo._segundo);
			}
			for (Duo<Integer, Integer> duo : objCliente) {
				_ingredientes.put(duo._primero, duo._segundo);
			}
			if (_idTrabajoMod == 1 || _idTrabajoMod == 113 || _idTrabajoMod == 115 || _idTrabajoMod == 116
					|| _idTrabajoMod == 117 || _idTrabajoMod == 118 || _idTrabajoMod == 119 || _idTrabajoMod == 120
					|| (_idTrabajoMod >= 163 && _idTrabajoMod <= 169)) {
				boolean resultado = trabajoPagoFM();
				return resultado;
			}
			Map<Integer, Integer> ingredPorModelo = new TreeMap<Integer, Integer>();
			for (Entry<Integer, Integer> ingrediente : _ingredientes.entrySet()) {
				int idObjeto = ingrediente.getKey();
				int cantObjeto = ingrediente.getValue();
				Objeto obj = MundoDofus.getObjeto(idObjeto);
				if (obj == null || obj.getCantidad() < cantObjeto) {
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_cliente, "EI");
					return false;
				}
				int nuevaCant = obj.getCantidad() - cantObjeto;
				if (nuevaCant == 0) {
					if (_artesano.tieneObjetoID(idObjeto)) {
						_artesano.borrarObjetoRemove(idObjeto);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, idObjeto);
					} else {
						_cliente.borrarObjetoRemove(idObjeto);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_cliente, idObjeto);
					}
					MundoDofus.eliminarObjeto(idObjeto);
				} else {
					obj.setCantidad(nuevaCant);
					if (_artesano.tieneObjetoID(idObjeto)) {
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, obj);
					} else {
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente, obj);
					}
				}
				int idModelo = obj.getIDModelo();
				if (ingredPorModelo.get(idModelo) == null)
					ingredPorModelo.put(idModelo, cantObjeto);
				else {
					int nueva = ingredPorModelo.get(idModelo) + cantObjeto;
					ingredPorModelo.remove(idModelo);
					ingredPorModelo.put(idModelo, nueva);
				}
			}
			if (ingredPorModelo.containsKey(7508))
				firmado = true;
			ingredPorModelo.remove(7508);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_artesano);
			GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_cliente);
			StatsOficio SO = _artesano.getOficioPorTrabajo(_idTrabajoMod);
			int idReceta = MundoDofus.getIDRecetaPorIngredientes(SO.getOficio().listaRecetaPorTrabajo(_idTrabajoMod),
					ingredPorModelo);
			if (idReceta == -1 || !SO.getOficio().puedeReceta(_idTrabajoMod, idReceta)) {
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-");
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_cliente, "EI");
				_ingredientes.clear();
				return false;
			}
			int suerte = CentroInfo.getSuertePorNroCasillaYNivel(SO.getNivel(), _ingredientes.size());
			int jet = Formulas.getRandomValor(1, 100);
			boolean exito = suerte >= jet;
			if (!exito) {
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EF");
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-" + idReceta);
				GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0118");
				GestorSalida.ENVIAR_Im_INFORMACION(_cliente, "0118");
			} else {
				Objeto nuevoObj = MundoDofus.getObjModelo(idReceta).crearObjDesdeModelo(1, false);
				if (firmado)
					nuevoObj.addTextoStat(988, _artesano.getNombre());
				Objeto igual = null;
				if ( (igual = _cliente.getObjSimilarInventario(nuevoObj)) == null) {
					MundoDofus.addObjeto(nuevoObj, true);
					_cliente.addObjetoPut(nuevoObj);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_cliente, nuevoObj);
				} else {
					igual.setCantidad(igual.getCantidad() + 1);
					GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente, igual);
					nuevoObj = igual;
				}
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_cliente);
				String statsNuevoObj = nuevoObj.convertirStatsAString();
				String todaInfo = nuevoObj.stringObjetoConPalo(1);
				GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_artesano, "O", "+", todaInfo);
				GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_cliente, "O", "+", todaInfo);
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "K;" + idReceta + ";T" + _cliente.getNombre() + ";"
						+ statsNuevoObj);
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_cliente, "K;" + idReceta + ";B" + _artesano.getNombre() + ";"
						+ statsNuevoObj);
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "+" + idReceta);
			}
			int xpGanada = (int) (CentroInfo.calculXpGanadaEnOficio(SO.getNivel(), _ingredientes.size()) * Bustemu.RATE_XP_OFICIO);
			SO.addXP(_artesano, xpGanada);
			ArrayList<StatsOficio> statsOficios = new ArrayList<StatsOficio>();
			statsOficios.add(SO);
			GestorSalida.ENVIAR_JX_EXPERINENCIA_OFICIO(_artesano, statsOficios);
			_ingredientes.clear();
			return exito;
		}
		
		private boolean trabajoPagoFM() {
			boolean firmado = false;
			Objeto objAMaguear = null, objRunaFirma = null, objModificador = null;
			int esCambioElemento = 0, statAgre = -1, esCambiadoStats = 0, agregar = 0, idABorrar = -1;
			int runa = 0;
			boolean vip = false;
			String statAMaguear = "-1";
			for (int idIngrediente : _ingredientes.keySet()) {
				Objeto ing = MundoDofus.getObjeto(idIngrediente);
				if (ing == null) {
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
					GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_cliente, "EI");
					GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-");
					_ingredientes.clear();
					return false;
				}
				int idModelo = ing.getModelo().getID();
				switch (idModelo) {
					case 1333:// pocion chispa
						statAgre = 99;
						esCambioElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1335:// pocion llovisna
						statAgre = 96;
						esCambioElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1337:// pocion de corriente de airee
						statAgre = 98;
						esCambioElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1338:// pocion de sacudida
						statAgre = 97;
						esCambioElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1340:// pocion derrumbamiento
						statAgre = 97;
						esCambioElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1341:// pocion chaparron
						statAgre = 96;
						esCambioElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1342:// pocion de rafaga
						statAgre = 98;
						esCambioElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1343:// pocion de Flameacion
						statAgre = 99;
						esCambioElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1345:// pocion Incendio
						statAgre = 99;
						esCambioElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1346:// pocion Tsunami
						statAgre = 96;
						esCambioElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1347:// pocion huracan
						statAgre = 98;
						esCambioElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1348:// pocion de seismo
						statAgre = 97;
						esCambioElemento = ing.getModelo().getNivel();
						objModificador = ing;
						break;
					case 1519:// Fuerza
						objModificador = ing;
						statAMaguear = "76";
						agregar = 1;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1521:// Sabiduria
						objModificador = ing;
						statAMaguear = "7c";
						agregar = 1;
						runa = 6;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1522:// Inteligencia
						objModificador = ing;
						statAMaguear = "7e";
						agregar = 1;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1523:// Vita
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 3;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1524:// Agi
						objModificador = ing;
						statAMaguear = "77";
						agregar = 1;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1525:// suerte
						objModificador = ing;
						statAMaguear = "7b";
						agregar = 1;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1545:// Pa fuerza
						objModificador = ing;
						statAMaguear = "76";
						agregar = 3;
						runa = 3;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1546:// Pa Sabiduria
						objModificador = ing;
						statAMaguear = "7c";
						agregar = 3;
						runa = 18;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1547:// Pa Inteligencia
						objModificador = ing;
						statAMaguear = "7e";
						agregar = 3;
						runa = 3;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1548:// Pa Vitalidad
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 10;
						runa = 10;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1549:// Pa agilidad
						objModificador = ing;
						statAMaguear = "77";
						agregar = 3;
						runa = 3;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1550:// Pa suerte
						objModificador = ing;
						statAMaguear = "7b";
						agregar = 3;
						runa = 10;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1551:// Ra Fuerza
						objModificador = ing;
						statAMaguear = "76";
						agregar = 10;
						runa = 10;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1552:// Ra Sabiduria
						objModificador = ing;
						statAMaguear = "7c";
						agregar = 10;
						runa = 50;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1553:// Ra Iniciativa
						objModificador = ing;
						statAMaguear = "7e";
						agregar = 10;
						runa = 10;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1554:// Ra Vida
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 30;
						runa = 10;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1555:// Ra Agilidad
						objModificador = ing;
						statAMaguear = "77";
						agregar = 10;
						runa = 10;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1556:// Ra suerte
						objModificador = ing;
						statAMaguear = "7b";
						agregar = 10;
						runa = 10;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1557:// Ga PA
						objModificador = ing;
						statAMaguear = "6f";
						agregar = 1;
						runa = 100;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 1558:// Ga PM
						objModificador = ing;
						statAMaguear = "80";
						agregar = 1;
						runa = 90;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7433:// Criticos
						objModificador = ing;
						statAMaguear = "73";
						agregar = 1;
						runa = 30;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7434:// curas
						objModificador = ing;
						statAMaguear = "b2";
						agregar = 1;
						runa = 20;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7435:// daños
						objModificador = ing;
						statAMaguear = "70";
						agregar = 1;
						runa = 20;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7436:// daños %
						objModificador = ing;
						statAMaguear = "8a";
						agregar = 1;
						runa = 2;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7437:// daños reenvio
						objModificador = ing;
						statAMaguear = "dc";
						agregar = 1;
						runa = 2;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7438:// alcance
						objModificador = ing;
						statAMaguear = "75";
						agregar = 1;
						runa = 50;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7442:// invocaciones
						objModificador = ing;
						statAMaguear = "b6";
						agregar = 1;
						runa = 30;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7443:// Pod
						objModificador = ing;
						statAMaguear = "9e";
						agregar = 10;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7444:// Pa pod
						objModificador = ing;
						statAMaguear = "9e";
						agregar = 30;
						runa = 1; // ?
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7445:// Ra pod
						objModificador = ing;
						statAMaguear = "9e";
						agregar = 100;
						runa = 1; // ?
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7446:// trampa
						objModificador = ing;
						statAMaguear = "e1";
						agregar = 1;
						runa = 15;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7447:// trampa %
						objModificador = ing;
						statAMaguear = "e2";
						agregar = 1;
						runa = 2;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7448:// iniciativa
						objModificador = ing;
						statAMaguear = "ae";
						agregar = 10;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7449:// Pa iniciativa
						objModificador = ing;
						statAMaguear = "ae";
						agregar = 30;
						runa = 3;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7450:// Ra iniciativa
						objModificador = ing;
						statAMaguear = "ae";
						agregar = 100;
						runa = 10;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7451:// Prospeccion
						objModificador = ing;
						statAMaguear = "b0";
						agregar = 1;
						runa = 5;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7452:// resistencia fuego
						objModificador = ing;
						statAMaguear = "f3";
						agregar = 1;
						runa = 4;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7453:// resistencia aire
						objModificador = ing;
						statAMaguear = "f2";
						agregar = 1;
						runa = 4;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7454:// resistencia agua
						objModificador = ing;
						statAMaguear = "f1";
						agregar = 1;
						runa = 4;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7455:// resistencia tierra
						objModificador = ing;
						statAMaguear = "f0";
						agregar = 1;
						runa = 4;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7456:// resistencia neutral
						objModificador = ing;
						statAMaguear = "f4";
						agregar = 1;
						runa = 4;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7457:// resistencia % fuego
						objModificador = ing;
						statAMaguear = "d5";
						agregar = 1;
						runa = 5;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7458:// resistencia % aire
						objModificador = ing;
						statAMaguear = "d4";
						agregar = 1;
						runa = 5;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7459:// resistencia % tierra
						objModificador = ing;
						statAMaguear = "d2";
						agregar = 1;
						runa = 5;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7460:// resistencia % neutral
						objModificador = ing;
						statAMaguear = "d6";
						agregar = 1;
						runa = 5;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7560:// resistencia % agua
						objModificador = ing;
						statAMaguear = "d3";
						agregar = 1;
						runa = 5;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 8379:// runa vida
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 10;
						runa = 10;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 10662:// runa ra prospe
						objModificador = ing;
						statAMaguear = "b0";
						agregar = 3;
						runa = 15;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 7508:// Runa de Firma
						firmado = true;
						objRunaFirma = ing;
						break;
					case 11118:// Fuerza
						vip = true;
						objModificador = ing;
						statAMaguear = "76";
						agregar = 15;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 11119:// Sabiduria
						vip = true;
						objModificador = ing;
						statAMaguear = "7c";
						agregar = 15;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 11120:// Inteligencia
						vip = true;
						objModificador = ing;
						statAMaguear = "7e";
						agregar = 15;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 11121:// Vita
						vip = true;
						objModificador = ing;
						statAMaguear = "7d";
						agregar = 45;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 11122:// Agi
						vip = true;
						objModificador = ing;
						statAMaguear = "77";
						agregar = 15;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 11123:// suerte
						vip = true;
						objModificador = ing;
						statAMaguear = "7b";
						agregar = 15;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 11124:// Prospeccion
						vip = true;
						objModificador = ing;
						statAMaguear = "b0";
						agregar = 10;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 11125:// Criticos
						vip = true;
						objModificador = ing;
						statAMaguear = "73";
						agregar = 3;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 11126:// curas
						vip = true;
						objModificador = ing;
						statAMaguear = "b2";
						agregar = 5;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 11127:// daños
						vip = true;
						objModificador = ing;
						statAMaguear = "70";
						agregar = 5;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 11128:// daños %
						vip = true;
						objModificador = ing;
						statAMaguear = "8a";
						agregar = 10;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					case 11129:// daños reenvio
						vip = true;
						objModificador = ing;
						statAMaguear = "dc";
						agregar = 5;
						runa = 1;
						esCambiadoStats = ing.getModelo().getNivel();
						break;
					default:
						int tipo = ing.getModelo().getTipo();
						if ( (tipo >= 1 && tipo <= 11) || (tipo >= 16 && tipo <= 22) || tipo == 81 || tipo == 102 || tipo == 114
								|| ing.getModelo().getCostePA() > 0) {
							objAMaguear = ing;
							if (objAMaguear.getCantidad() > 1) {
								Personaje modificado = _artesano.tieneObjetoID(idIngrediente) ? _artesano : _cliente;
								int nuevaCant = objAMaguear.getCantidad() - 1;
								Objeto nuevoObj = Objeto.clonarObjeto(objAMaguear, nuevaCant);
								objAMaguear.setCantidad(1);
								GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado, objAMaguear);
								MundoDofus.addObjeto(nuevoObj, true);
								GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(modificado, nuevoObj);
								break;
							}
						}
				}
			}
			StatsOficio oficio = _artesano.getOficioPorTrabajo(_idTrabajoMod);
			oficio.addXP(_artesano, (int) (Bustemu.RATE_XP_OFICIO + 9.0 / 10.0) * 10);
			if (oficio == null || objAMaguear == null || objModificador == null) {
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_artesano, "EI");
				GestorSalida.ENVIAR_Ec_INICIAR_RECETA(_cliente, "EI");
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-");
				_ingredientes.clear();
				return false;
			}
			if (idABorrar != -1) {
				_ingredientes.remove(idABorrar);
			}
			ObjetoModelo objModeloMaguear = objAMaguear.getModelo();
			int idObjMaguear = objAMaguear.getID();
			int suerte = 0;
			int nivelOficio = oficio.getNivel();
			int idObjModMaguear = objModeloMaguear.getID();
			String statStringObj = objAMaguear.convertirStatsAString();
			if (esCambioElemento > 0 && esCambiadoStats == 0) {// si cambia de elemento
				suerte = Formulas.calculoPorcCambioElenemto(nivelOficio, objModeloMaguear.getNivel(), esCambioElemento);
				if (suerte > 100 - (nivelOficio / 20))
					suerte = 100 - (nivelOficio / 20);
				if (suerte < (nivelOficio / 20))
					suerte = (nivelOficio / 20);
			} else if (esCambiadoStats > 0 && esCambioElemento == 0) {// si cambia de stats
				int pesoTotalActual = 1;
				int pesoActualStat = 1;
				if (!statStringObj.isEmpty()) {
					pesoTotalActual = pesoTotalActualObj(statStringObj, objAMaguear);
					pesoActualStat = pesoStatActual(objAMaguear, statAMaguear);
				}
				int pesoTotalBase = pesoTotalBaseObj(idObjModMaguear);
				if (pesoTotalBase < 0) {
					pesoTotalBase = 0;
				}
				if (pesoActualStat < 0) {
					pesoActualStat = 0;
				}
				if (pesoTotalActual < 0) {
					pesoTotalActual = 0;
				}
				float coef = 1;
				int tieneStatBase = statBaseObjeto(objAMaguear, statAMaguear);
				int tieneStatActual = statActualObjeto(objAMaguear, statAMaguear);
				if (tieneStatBase == 1 && tieneStatActual == 1 || tieneStatBase == 1 && tieneStatActual == 0) {
					coef = 1.0f;
				} else if (tieneStatBase == 2 && tieneStatActual == 2) {
					coef = 0.50f;
				} else if (tieneStatBase == 0 && tieneStatActual == 0 || tieneStatBase == 0 && tieneStatActual == 1) {
					coef = 0.25f;
				}
				float tolerancia = vip ? _tolerVIP : _tolerNormal;
				int diferencia = (int) (pesoTotalBase * tolerancia) - pesoTotalActual;
				suerte = (int) Formulas.suerteFM(pesoTotalBase, pesoTotalActual, pesoActualStat, runa, diferencia, coef);
				suerte = suerte + oficio.getAdicional();
				if (vip)
					suerte += 30;
				if (suerte <= 0)
					suerte = 1;
				else if (suerte > 100)
					suerte = 100;
				// GestorSalida.ENVIAR_cs_CHAT_MENSAJE(_artesano, "TU EXITO / VOUS SUCCES : " + suerte + "%", "FF0000");
			}
			int jet = Formulas.getRandomValor(1, 100);
			boolean exito = suerte >= jet;
			if (!exito) { // falla magueo
				if (objRunaFirma != null) {
					Personaje modificado = _artesano.tieneObjetoID(objRunaFirma.getID()) ? _artesano : _cliente;
					int nuevaCant = objRunaFirma.getCantidad() - 1;
					if (nuevaCant <= 0) {
						modificado.borrarObjetoRemove(objRunaFirma.getID());
						MundoDofus.eliminarObjeto(objRunaFirma.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(modificado, objRunaFirma.getID());
					} else {
						objRunaFirma.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano, objRunaFirma);
					}
				}
				if (objModificador != null) {
					Personaje modificado = _artesano.tieneObjetoID(objModificador.getID()) ? _artesano : _cliente;
					int nuevaCant = objModificador.getCantidad() - 1;
					if (nuevaCant <= 0) {
						modificado.borrarObjetoRemove(objModificador.getID());
						MundoDofus.eliminarObjeto(objModificador.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(modificado, objModificador.getID());
					} else {
						objModificador.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado, objModificador);
					}
				}
				int romper = Formulas.getRandomValor(1, 100);
				if (romper == 50) {
					_interrumpir = true;
					Personaje modificado = _artesano.tieneObjetoID(idObjMaguear) ? _artesano : _cliente;
					MundoDofus.eliminarObjeto(idObjMaguear);
					modificado.borrarObjetoRemove(idObjMaguear);
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(modificado, idObjMaguear);
				} else {
					if (_artesano.tieneObjetoID(idObjMaguear)) {
						_artesano.borrarObjetoRemove(idObjMaguear);
						_cliente.addObjetoPut(objAMaguear);
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, idObjMaguear);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_cliente, objAMaguear);
					}
					if (!statStringObj.isEmpty()) {
						String statsStr = objAMaguear.stringStatsFCForja(objAMaguear, runa);
						objAMaguear.clearTodo();
						objAMaguear.convertirStringAStats(statsStr);
					}
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_cliente);
					GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_cliente, objAMaguear);
					String todaInfo = objAMaguear.stringObjetoConPalo(1);
					GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_artesano, "O", "+", todaInfo);
					GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_cliente, "O", "+", todaInfo);
				}
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "-" + idObjModMaguear);
				GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "0183");
				GestorSalida.ENVIAR_Im_INFORMACION(_cliente, "0183");
				if (_interrumpir) {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_artesano,
							"El magueo ha fallado y el objeto ha sido destruido. Le mage failed, et objet deleted.");
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(_cliente,
							"El magueo ha fallado y el objeto ha sido destruido. Le mage failed, et objet deleted.");
				}
				_artesano.setForjaEcK("EF");
				_cliente.setForjaEcK("EF");
			} else {
				int coef = 0;
				if (esCambioElemento == 1)
					coef = 50;
				else if (esCambioElemento == 25)
					coef = 65;
				else if (esCambioElemento == 50)
					coef = 85;
				if (firmado) {
					objAMaguear.addTextoStat(985, _artesano.getNombre());
				}
				if (esCambioElemento > 0 && esCambiadoStats == 0) {
					for (EfectoHechizo EH : objAMaguear.getEfectos()) {
						if (EH.getEfectoID() != 100)
							continue;
						String[] infos = EH.getArgs().split(";");
						try {
							int min = Integer.parseInt(infos[0], 16);
							int max = Integer.parseInt(infos[1], 16);
							int nuevoMin = (int) ( (min * coef) / 100);
							int nuevoMax = (int) ( (max * coef) / 100);
							if (nuevoMin == 0)
								nuevoMin = 1;
							String nuevoRango = "1d" + (nuevoMax - nuevoMin + 1) + "+" + (nuevoMin - 1);
							String nuevosArgs = Integer.toHexString(nuevoMin) + ";" + Integer.toHexString(nuevoMax) + ";-1;-1;0;"
									+ nuevoRango;
							EH.setArgs(nuevosArgs);
							EH.setEfectoID(statAgre);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else if (esCambiadoStats > 0 && esCambioElemento == 0) {// Si se modifica los stats(runa)
					boolean negativo = false;
					int actualStat = statActualObjeto(objAMaguear, statAMaguear);
					if (actualStat == 2) {// los stats existentes actual son negativos
						if (statAMaguear.compareTo("7b") == 0) {
							statAMaguear = "98";
							negativo = true;
						}
						if (statAMaguear.compareTo("77") == 0) {
							statAMaguear = "9a";
							negativo = true;
						}
						if (statAMaguear.compareTo("7e") == 0) {
							statAMaguear = "9b";
							negativo = true;
						}
						if (statAMaguear.compareTo("76") == 0) {
							statAMaguear = "9d";
							negativo = true;
						}
						if (statAMaguear.compareTo("7c") == 0) {
							statAMaguear = "9c";
							negativo = true;
						}
						if (statAMaguear.compareTo("7d") == 0) {
							statAMaguear = "99";
							negativo = true;
						}
					}
					if (actualStat == 1 || actualStat == 2) {
						String statsStr = objAMaguear.convertirStatsAStringFM(statAMaguear, objAMaguear, agregar, negativo);
						objAMaguear.clearTodo();
						objAMaguear.convertirStringAStats(statsStr);
					} else {
						if (statStringObj.isEmpty()) {
							String statsStr = statAMaguear + "#" + Integer.toHexString(agregar) + "#0#0#0d0+" + agregar;
							objAMaguear.clearTodo();
							objAMaguear.convertirStringAStats(statsStr);
						} else {
							String statsStr = objAMaguear.convertirStatsAStringFM(statAMaguear, objAMaguear, agregar, negativo)
									+ "," + statAMaguear + "#" + Integer.toHexString(agregar) + "#0#0#0d0+" + agregar;
							objAMaguear.clearTodo();
							objAMaguear.convertirStringAStats(statsStr);
						}
					}
				}
				if (objRunaFirma != null) {
					Personaje modificado = _artesano.tieneObjetoID(objRunaFirma.getID()) ? _artesano : _cliente;
					int nuevaCant = objRunaFirma.getCantidad() - 1;
					if (nuevaCant <= 0) {
						modificado.borrarObjetoRemove(objRunaFirma.getID());
						MundoDofus.eliminarObjeto(objRunaFirma.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(modificado, objRunaFirma.getID());
					} else {
						objRunaFirma.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado, objRunaFirma);
					}
				}
				if (objModificador != null) {
					Personaje modificado = _artesano.tieneObjetoID(objModificador.getID()) ? _artesano : _cliente;
					int nuevaCant = objModificador.getCantidad() - 1;
					if (nuevaCant <= 0) {
						modificado.borrarObjetoRemove(objModificador.getID());
						MundoDofus.eliminarObjeto(objModificador.getID());
						GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(modificado, objModificador.getID());
					} else {
						objModificador.setCantidad(nuevaCant);
						GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado, objModificador);
					}
				}
				if (_artesano.tieneObjetoID(idObjMaguear)) {
					_artesano.borrarObjetoRemove(idObjMaguear);
					_cliente.addObjetoPut(objAMaguear);
					GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(_artesano, idObjMaguear);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(_cliente, objAMaguear);
				}
				GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(_cliente);// Pods
				GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_cliente, objAMaguear);
				String statsNuevoObj = objAMaguear.convertirStatsAString();
				String todaInfo = objAMaguear.stringObjetoConPalo(1);
				GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano.getMapa(), _artesano.getID(), "+" + idObjModMaguear);
				GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_artesano, "O", "+", todaInfo);
				GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO(_cliente, "O", "+", todaInfo);
				_artesano.setForjaEcK("K;" + idObjModMaguear + ";T" + _cliente.getNombre() + ";" + statsNuevoObj);
				_cliente.setForjaEcK("K;" + idObjModMaguear + ";B" + _artesano.getNombre() + ";" + statsNuevoObj);
			}
			_ingredientes.clear();
			return exito;
		}
	}
	private int _id;
	private ArrayList<Integer> _herramientas = new ArrayList<Integer>();
	private Map<Integer, ArrayList<Integer>> _recetas = new TreeMap<Integer, ArrayList<Integer>>();
	
	public Oficio(int id, String herramientas, String recetas) {
		_id = id;
		if (!herramientas.equals("")) {
			for (String str : herramientas.split(",")) {
				try {
					int herramienta = Integer.parseInt(str);
					_herramientas.add(herramienta);
				} catch (Exception e) {
					continue;
				}
			}
		}
		if (!recetas.equals("")) {
			for (String str : recetas.split("\\|")) {
				try {
					int trabajoID = Integer.parseInt(str.split(";")[0]);
					ArrayList<Integer> list = new ArrayList<Integer>();
					for (String str2 : str.split(";")[1].split(","))
						list.add(Integer.parseInt(str2));
					_recetas.put(trabajoID, list);
				} catch (Exception e) {
					continue;
				}
			}
		}
	}
	
	public ArrayList<Integer> listaRecetaPorTrabajo(int trabajo) {
		return _recetas.get(trabajo);
	}
	
	public boolean puedeReceta(int trabajo, int modelo) {
		if (_recetas.get(trabajo) != null)
			for (int a : _recetas.get(trabajo))
				if (a == modelo)
					return true;
		return false;
	}
	
	public int getID() {
		return _id;
	}
	
	public boolean herramientaValida(int t) {
		for (int a : _herramientas)
			if (t == a)
				return true;
		return false;
	}
	
	public static byte statActualObjeto(Objeto obj, String statsObjeto) {// 0 = no tiene, 1 = tiene, 2 = negativo
		if (!obj.convertirStatsAString().isEmpty()) {
			for (Entry<Integer, Integer> entry : obj.getStats().getStatsComoMap().entrySet()) {
				String hex = Integer.toHexString(entry.getKey());
				if (hex.compareTo(statsObjeto) > 0) {
					if (hex.compareTo("98") == 0 && statsObjeto.compareTo("7b") == 0) {
						return 2;
					} else if (hex.compareTo("9a") == 0 && statsObjeto.compareTo("77") == 0) {
						return 2;
					} else if (hex.compareTo("9b") == 0 && statsObjeto.compareTo("7e") == 0) {
						return 2;
					} else if (hex.compareTo("9d") == 0 && statsObjeto.compareTo("76") == 0) {
						return 2;
					} else if (hex.compareTo("9c") == 0 && statsObjeto.compareTo("7c") == 0) {
						return 2;
					} else if (hex.compareTo("99") == 0 && statsObjeto.compareTo("7d") == 0) {
						return 2;
					} else {
						continue;
					}
				} else if (hex.compareTo(statsObjeto) == 0) {
					return 1;
				}
			}
			return 0;
		} else {
			return 0;
		}
	}
	
	public static byte statBaseObjeto(Objeto obj, String stat) {
		String[] splitted = obj.getModelo().getStringStatsObj().split(",");
		for (String s : splitted) {
			String[] stats = s.split("#");
			if (stats[0].compareTo(stat) > 0) {
				if (stats[0].compareTo("98") == 0 && stat.compareTo("7b") == 0) {
					return 2;
				} else if (stats[0].compareTo("9a") == 0 && stat.compareTo("77") == 0) {
					return 2;
				} else if (stats[0].compareTo("9b") == 0 && stat.compareTo("7e") == 0) {
					return 2;
				} else if (stats[0].compareTo("9d") == 0 && stat.compareTo("76") == 0) {
					return 2;
				} else if (stats[0].compareTo("9c") == 0 && stat.compareTo("7c") == 0) {
					return 2;
				} else if (stats[0].compareTo("99") == 0 && stat.compareTo("7d") == 0) {
					return 2;
				} else {
					continue;
				}
			} else if (stats[0].compareTo(stat) == 0) {
				return 1;
			}
		}
		return 0;
	}
	
	public static int getStatBaseMax(ObjetoModelo objMod, String statsModif) {
		String[] splitted = objMod.getStringStatsObj().split(",");
		for (String s : splitted) {
			String[] stats = s.split("#");
			if (stats[0].compareTo(statsModif) > 0) {
				continue;
			} else if (stats[0].compareTo(statsModif) == 0) {
				int max = Integer.parseInt(stats[2], 16);
				if (max == 0)
					max = Integer.parseInt(stats[1], 16);
				return max;
			}
		}
		return 0;
	}
	
	public static int pesoStatActual(Objeto obj, String statsModif) {
		for (Entry<Integer, Integer> entry : obj.getStats().getStatsComoMap().entrySet()) {
			if (Integer.toHexString(entry.getKey()).compareTo(statsModif) > 0) {
				continue;
			} else if (Integer.toHexString(entry.getKey()).compareTo(statsModif) == 0) {
				int statID = entry.getKey();
				int multi = 1;
				if (statID == 125 || statID == 158 || statID == 174)// Fuerza, inteligencia, vitalidad, agilidad, suerte, pod e
				// iniciativa
				{
					multi = 1;
				} else if (statID == 118 || statID == 126 || statID == 119 || statID == 123)// Fuerza, inteligencia, vitalidad,
				// agilidad, suerte, pod e iniciativa
				{
					multi = 2;
				} else if (statID == 138 || statID == 666 || statID == 226 || statID == 220)// Daños %,Reenvio de daños,Trampas %
				{
					multi = 3;
				} else if (statID == 124 || statID == 176)// Sabiduria, prospeccion
				{
					multi = 5;
				} else if (statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244)// Resistencia +
																												// fuego,
				// aire, tierra, agua,
				// neutral
				{
					multi = 7;
				} else if (statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214)// Resistencia %
																												// fuego,
				// aire, tierra, agua,
				// neutral
				{
					multi = 8;
				} else if (statID == 225)// Trampas
				{
					multi = 15;
				} else if (statID == 178 || statID == 112)// Curas,Daños
				{
					multi = 20;
				} else if (statID == 115 || statID == 182)// Criticos,Invocaciones
				{
					multi = 30;
				} else if (statID == 117)// PO(alcance)
				{
					multi = 50;
				} else if (statID == 128)// PM
				{
					multi = 90;
				} else if (statID == 111)// PA
				{
					multi = 100;
				}
				int peso = entry.getValue() * multi; // peso de la caracteristica
				return peso;
			}
		}
		return 0;
	}
	
	public static int pesoTotalActualObj(String statsTemplate, Objeto obj) {
		int peso = 0;
		int suma = 0;
		String[] splitted = statsTemplate.split(",");
		for (String s : splitted) {
			String[] stats = s.split("#");
			int statID = Integer.parseInt(stats[0], 16);
			boolean siguiente = false;
			for (int a : CentroInfo.ID_EFECTOS_ARMAS)
				if (a == statID)
					siguiente = true;
			if (siguiente)
				continue;
			String jet = "";
			int cantidad = 1;
			try {
				jet = stats[4];
				cantidad = Formulas.getRandomValor(jet);
				try {
					int min = Integer.parseInt(stats[1], 16);
					int max = Integer.parseInt(stats[2], 16);
					cantidad = min;
					if (max != 0)
						cantidad = max;
				} catch (Exception e) {
					cantidad = Formulas.getRandomValor(jet);
				}
			} catch (Exception e) {}
			int multi = 1;
			int coef = 1;
			int tieneStatBase = statBaseObjeto(obj, stats[0]);
			if (tieneStatBase == 2) {
				coef = 2;
			} else if (tieneStatBase == 0) {
				coef = 4;
			}
			if (statID == 125 || statID == 158 || statID == 174)// Fuerza, inteligencia, vitalidad, agilidad, suerte, pod e
																// iniciativa
			{
				multi = 1;
			} else if (statID == 118 || statID == 126 || statID == 119 || statID == 123)// Fuerza, inteligencia, vitalidad,
																						// agilidad, suerte, pod e iniciativa
			{
				multi = 2;
			} else if (statID == 138 || statID == 666 || statID == 226 || statID == 220)// Daños %,Reenvio de daños,Trampas %
			{
				multi = 3;
			} else if (statID == 124 || statID == 176)// Sabiduria, prospeccion
			{
				multi = 5;
			} else if (statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244)// Resistencia + fuego,
																											// aire, tierra, agua,
																											// neutral
			{
				multi = 7;
			} else if (statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214)// Resistencia % fuego,
																											// aire, tierra, agua,
																											// neutral
			{
				multi = 8;
			} else if (statID == 225)// Trampas
			{
				multi = 15;
			} else if (statID == 178 || statID == 112)// Curas,Daños
			{
				multi = 20;
			} else if (statID == 115 || statID == 182)// Criticos,Invocaciones
			{
				multi = 30;
			} else if (statID == 117)// PO(alcance)
			{
				multi = 50;
			} else if (statID == 128)// PM
			{
				multi = 90;
			} else if (statID == 111)// PA
			{
				multi = 100;
			}
			peso = cantidad * multi * coef; // peso de la caracteristica
			suma += peso;
		}
		return suma;
	}
	
	public static int pesoTotalBaseObj(int idObjModelo) {
		int peso = 0;
		int suma = 0;
		String NaturalStatsItem = "";
		NaturalStatsItem = MundoDofus.getObjModelo(idObjModelo).getStringStatsObj();
		if (NaturalStatsItem == null || NaturalStatsItem.isEmpty())
			return 0;
		String[] splitted = NaturalStatsItem.split(",");
		for (String s : splitted) {
			String[] stats = s.split("#");
			int statID = Integer.parseInt(stats[0], 16);
			boolean follow = true;
			for (int a : CentroInfo.ID_EFECTOS_ARMAS)
				if (a == statID)
					follow = false;
			if (!follow)
				continue;
			String jet = "";
			int valor = 1;
			try {
				jet = stats[4];
				valor = Formulas.getRandomValor(jet);
				try {
					// se pone el jet maximo
					int min = Integer.parseInt(stats[1], 16);
					int max = Integer.parseInt(stats[2], 16);
					valor = min;
					if (max != 0)
						valor = max;
				} catch (Exception e) {
					valor = Formulas.getRandomValor(jet);
				}
			} catch (Exception e) {}
			int multi = 1;
			if (statID == 125 || statID == 158 || statID == 174)// Fuerza, inteligencia, vitalidad, agilidad, suerte, pod e
			// iniciativa
			{
				multi = 1;
			} else if (statID == 118 || statID == 126 || statID == 119 || statID == 123)// Fuerza, inteligencia, vitalidad,
			// agilidad, suerte, pod e iniciativa
			{
				multi = 2;
			} else if (statID == 138 || statID == 666 || statID == 226 || statID == 220)// Daños %,Reenvio de daños,Trampas %
			{
				multi = 3;
			} else if (statID == 124 || statID == 176)// Sabiduria, prospeccion
			{
				multi = 5;
			} else if (statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244)// Resistencia + fuego,
			// aire, tierra, agua,
			// neutral
			{
				multi = 7;
			} else if (statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214)// Resistencia % fuego,
			// aire, tierra, agua,
			// neutral
			{
				multi = 8;
			} else if (statID == 225)// Trampas
			{
				multi = 15;
			} else if (statID == 178 || statID == 112)// Curas,Daños
			{
				multi = 20;
			} else if (statID == 115 || statID == 182)// Criticos,Invocaciones
			{
				multi = 30;
			} else if (statID == 117)// PO(alcance)
			{
				multi = 50;
			} else if (statID == 128)// PM
			{
				multi = 90;
			} else if (statID == 111)// PA
			{
				multi = 100;
			}
			peso = valor * multi; // peso de la caracteristica
			suma += peso;
		}
		return suma;
	}
}
