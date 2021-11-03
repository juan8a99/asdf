
package variables;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.ArrayList;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Timer;

import servidor.EntradaPersonaje.AccionDeJuego;
import variables.Hechizo.StatsHechizos;
import variables.Mapa.Celda;
import variables.MobModelo.GrupoMobs;
import variables.MobModelo.MobGrado;
import variables.Objeto.ObjetoModelo;
import variables.Personaje.Grupo;
import variables.Personaje.Stats;

import estaticos.*;
import estaticos.MundoDofus.*;

public class Pelea {
	public static class Trampa {
		private Luchador _lanzador;
		private Celda _celda;
		private byte _tamaño;
		private int _hechizo;
		private StatsHechizos _trampaHechizo;
		private Pelea _pelea;
		private int _color;
		private boolean _visible = true;
		private int _paramEquipoDueño = -1;
		
		public Trampa(Pelea pelea, Luchador lanzador, Celda celda, byte tamaño, StatsHechizos trampaHechizo, int hechizo) {
			_pelea = pelea;
			_lanzador = lanzador;
			_celda = celda;
			_hechizo = hechizo;
			_tamaño = tamaño;
			_trampaHechizo = trampaHechizo;
			_color = CentroInfo.getColorTrampa(hechizo);
			_paramEquipoDueño = lanzador.getParamEquipoAliado();
		}
		
		public Celda getCelda() {
			return _celda;
		}
		
		public int getParamEquipoDueño() {
			return _paramEquipoDueño;
		}
		
		public byte getTamaño() {
			return _tamaño;
		}
		
		public Luchador getLanzador() {
			return _lanzador;
		}
		
		public void esVisibleParaEnemigo() {
			_visible = true;
		}
		
		public boolean esVisible() {
			return _visible;
		}
		
		public int getColor() {
			return _color;
		}
		
		public void desaparecer() {
			String str = "GDZ-" + _celda.getID() + ";" + _tamaño + ";" + _color;
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, _paramEquipoDueño, 999, _lanzador.getID() + "", str);
			str = "GDC" + _celda.getID();
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, _paramEquipoDueño, 999, _lanzador.getID() + "", str);
			if (_visible) {
				int equipo2 = _lanzador.getParamEquipoEnemigo();
				String str2 = "GDZ-" + _celda.getID() + ";" + _tamaño + ";" + _color;
				GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, equipo2, 999, _lanzador.getID() + "", str2);
				str2 = "GDC" + _celda.getID();
				GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, equipo2, 999, _lanzador.getID() + "", str2);
			}
		}
		
		public void aparecer(int equipo) {
			String str = "GDZ+" + _celda.getID() + ";" + _tamaño + ";" + _color;
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, equipo, 999, _lanzador.getID() + "", str);
			str = "GDC" + _celda.getID() + ";Haaaaaaaaz3005;";
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, equipo, 999, _lanzador.getID() + "", str);
		}
		
		public void activaTrampa(Luchador trampeado) {
			if (trampeado._estaMuerto)
				return;
			_pelea.getTrampas().remove(this);
			desaparecer();
			String str = _hechizo + "," + _celda.getID() + ",0,1,1," + _lanzador.getID();
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 307, trampeado.getID() + "", str);
			ArrayList<Celda> celdas = new ArrayList<Celda>();
			celdas.add(_celda);
			for (int a = 0; a < _tamaño; a++) {
				char[] dirs = { 'b', 'd', 'f', 'h' };
				ArrayList<Celda> cases2 = new ArrayList<Celda>();
				cases2.addAll(celdas);
				for (Celda aCell : cases2) {
					for (char d : dirs) {
						Celda celda = _pelea.getMapaCopia().getCelda(
								Camino.getSigIDCeldaMismaDir(aCell.getID(), d, _pelea.getMapaCopia(), true));
						if (celda == null)
							continue;
						if (!celdas.contains(celda)) {
							celdas.add(celda);
						}
					}
				}
			}
			Luchador trampaLanzador;
			if (_lanzador.getPersonaje() == null)
				trampaLanzador = new Luchador(_pelea, _lanzador.getMob());
			else
				trampaLanzador = new Luchador(_pelea, _lanzador.getPersonaje());
			trampaLanzador.setCeldaPelea(_celda);
			if (_trampaHechizo.getHechizoID() == 1688)
				_trampaHechizo.aplicaTrampaAPelea(_pelea, trampaLanzador, _celda, celdas, false);
			else
				_trampaHechizo.aplicaTrampaAPelea(_pelea, trampaLanzador, trampeado.getCeldaPelea(), celdas, false);
			_pelea.verificaSiAcaboPelea();
		}
	}
	public static class Luchador {
		private int _id = 0;
		private boolean _puedeJugar = false;
		private Pelea _pelea;
		private int _tipo = 0;
		private MobGrado _mob = null;
		private Personaje _perso = null;
		private int _equipoBin = -2;
		private Celda _celda;
		private ArrayList<EfectoHechizo> _buffsPelea = new ArrayList<EfectoHechizo>();
		private Luchador _invocador;
		private int _PDVMAX;
		private int _PDV;
		private boolean _estaMuerto;
		private boolean _estaRetirado;
		private int _gfxID;
		private Map<Integer, Integer> _estados = new TreeMap<Integer, Integer>();
		private Luchador _transportado;
		private Luchador _transportadoPor;
		private Recaudador _recaudador = null;
		private Prisma _prisma = null;
		private Personaje _doble = null;
		private ArrayList<HechizoLanzado> _hechizosLanzados = new ArrayList<HechizoLanzado>();
		private Luchador _objetivoDestZurca = null;
		private float _bonusAlineacion = 0;
		private Stats _totalStats;
		private Map<Integer, Integer> _bonusCastigo = new TreeMap<Integer, Integer>();
		private int _nroInvocaciones = 0;
		private int _idHechiLanzReto = -1;
		private int _idCeldaIniTurnoReto;
		private ArrayList<Integer> _hechiLanzadosReto = new ArrayList<Integer>();
		private ArrayList<Integer> _mobMatadosReto = new ArrayList<Integer>();
		private boolean _intocable = false;
		private boolean _contaminacion = false;
		private boolean _contaminado = false;
		private int _turnosParaMorir = 0;
		private int _pjAtacante = 0;
		private int _prospeccionTemporal = 0;
		private boolean _desconectado = false;
		private int _turnosRestantes = 20;
		
		public void setPjAtacante(int id) {
			_pjAtacante = id;
		}
		
		public int getPjAtacante() {
			return _pjAtacante;
		}
		
		public void setBonusCastigo(int bonus, int stat) {
			_bonusCastigo.put(stat, bonus);
		}
		
		public int getBonusCastigo(int stat) {
			int bonus = 0;
			if (_bonusCastigo.containsKey(stat))
				bonus = _bonusCastigo.get(stat);
			return bonus;
		}
		
		public Luchador getObjetivoDestZurca() {
			return _objetivoDestZurca;
		}
		
		public void setObjetivoDestZurca(Luchador objetivo) {
			_objetivoDestZurca = objetivo;
		}
		
		public int getTipo() {
			return _tipo;
		}
		
		public Luchador(Pelea pelea, MobGrado mob) {
			_pelea = pelea;
			_tipo = 2;
			_mob = mob;
			_id = mob.getIdEnPelea();
			_PDVMAX = mob.getPDVMAX();
			_PDV = mob.getPDV();
			_gfxID = getGfxDefecto();
			_totalStats = mob.getStats();
		}
		
		public Luchador(Pelea pelea, Personaje perso) {
			_pelea = pelea;
			if (perso.esDoble()) {
				_tipo = 10;
				_doble = perso;
			} else {
				_tipo = 1;
				_perso = perso;
			}
			_id = perso.getID();
			_PDVMAX = perso.getPDVMAX();
			_PDV = perso.getPDV();
			_gfxID = getGfxDefecto();
			_totalStats = perso.getTotalStats();
		}
		
		public Luchador(Pelea pelea, Recaudador recaudador) {
			_pelea = pelea;
			_tipo = 5;
			_recaudador = recaudador;
			_id = -1;
			Gremio gremio = MundoDofus.getGremio(recaudador.getGremioID());
			_PDVMAX = gremio.getNivel() * 100;
			_PDV = gremio.getNivel() * 100;
			_gfxID = 6000;
			_totalStats = gremio.getStatsPelea();
		}
		
		public Luchador(Pelea pelea, Prisma prisma) {
			_pelea = pelea;
			_tipo = 7;
			_prisma = prisma;
			_id = -1;
			_PDVMAX = prisma.getNivel() * 10000;
			_PDV = prisma.getNivel() * 10000;
			_gfxID = prisma.getAlineacion() == 1 ? 8101 : 8100;
			prisma.actualizarStats();
			_totalStats = prisma.getStats();
		}
		
		public ArrayList<HechizoLanzado> getHechizosLanzados() {
			return _hechizosLanzados;
		}
		
		public void actualizaHechizoLanzado() {
			ArrayList<HechizoLanzado> copia = new ArrayList<HechizoLanzado>();
			copia.addAll(_hechizosLanzados);
			int i = 0;
			for (HechizoLanzado HL : copia) {
				HL.actuSigLanzamiento();
				if (HL.getSigLanzamiento() <= 0) {
					_hechizosLanzados.remove(i);
					i--;
				}
				i++;
			}
		}
		
		public void addHechizoLanzado(Luchador objetivo, StatsHechizos sort, Luchador lanzador) {
			HechizoLanzado lanzado = new HechizoLanzado(objetivo, sort, lanzador);
			_hechizosLanzados.add(lanzado);
		}
		
		public int getID() {
			return _id;
		}
		
		public Luchador getTransportando() {
			return _transportado;
		}
		
		public void setTransportado(Luchador transportado) {
			_transportado = transportado;
		}
		
		public Luchador getTransportadoPor() {
			return _transportadoPor;
		}
		
		public void setTransportadoPor(Luchador transportadoPor) {
			_transportadoPor = transportadoPor;
		}
		
		public int getGfxID() {
			return _gfxID;
		}
		
		public void setGfxID(int gfxID) {
			_gfxID = gfxID;
		}
		
		public ArrayList<EfectoHechizo> getBuffPelea() {
			return _buffsPelea;
		}
		
		public boolean esInvisible() {
			return tieneBuff(150);
		}
		
		public Celda getCeldaPelea() {
			return _celda;
		}
		
		public void setCeldaPelea(Celda celda) {
			_celda = celda;
		}
		
		public void setEquipoBin(int i) {
			_equipoBin = i;
		}
		
		public boolean estaMuerto() {
			return _estaMuerto;
		}
		
		public void setMuerto(boolean estaMuerto) {
			_estaMuerto = estaMuerto;
		}
		
		public boolean estaRetirado() {
			return _estaRetirado;
		}
		
		public Personaje getPersonaje() {
			if (_tipo == 1)
				return _perso;
			return null;
		}
		
		public Recaudador getRecau() {
			if (_tipo == 5)
				return _recaudador;
			return null;
		}
		
		public Prisma getPrisma() {
			if (_tipo == 7)
				return _prisma;
			return null;
		}
		
		public boolean calculaSiGC(int porcGC) {
			if (porcGC < 2)
				return false;
			Stats statsConBuff = getTotalStatsConBuff();
			int agi = statsConBuff.getEfecto(CentroInfo.STATS_ADD_AGILIDAD);
			if (agi < 0)
				agi = 0;
			porcGC -= statsConBuff.getEfecto(CentroInfo.STATS_ADD_GOLPES_CRITICOS);
			porcGC = (int) ( (porcGC * 2.9901) / Math.log(agi + 12));
			if (porcGC < 2)
				porcGC = 2;
			int jet = Formulas.getRandomValor(1, porcGC);
			return (jet == porcGC);
		}
		
		public boolean testSiEsGC(int porcGC, StatsHechizos sHechizo, Luchador luchador) {
			Personaje perso = luchador.getPersonaje();
			if (porcGC < 2)
				return false;
			Stats statsConBuff = getTotalStatsConBuff();
			int agi = statsConBuff.getEfecto(CentroInfo.STATS_ADD_AGILIDAD);
			if (agi < 0)
				agi = 0;
			porcGC -= statsConBuff.getEfecto(CentroInfo.STATS_ADD_GOLPES_CRITICOS);
			if (luchador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
				int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 287);
				porcGC -= modi;
			}
			porcGC = (int) ( (porcGC * 2.9901) / Math.log(agi + 12));
			if (porcGC < 2)
				porcGC = 2;
			int jet = Formulas.getRandomValor(1, porcGC);
			return (jet == porcGC);
		}
		
		public ArrayList<EfectoHechizo> getBuffsPorEfectoID(int efectotID) {
			ArrayList<EfectoHechizo> buffs = new ArrayList<EfectoHechizo>();
			for (EfectoHechizo buff : _buffsPelea) {
				if (buff.getEfectoID() == efectotID)
					buffs.add(buff);
			}
			return buffs;
		}
		
		public Stats getTotalStatsSinBuff() {
			Stats stats = new Stats(new TreeMap<Integer, Integer>());
			stats = _totalStats;
			return stats;
		}
		
		public Stats getTotalStatsConBuff() {
			Stats stats = new Stats(new TreeMap<Integer, Integer>());
			stats = _totalStats;
			stats = Stats.acumularStats(stats, getBuffsStatsPelea());
			return stats;
		}
		
		private Stats getBuffsStatsPelea() {
			Stats stats = new Stats();
			for (EfectoHechizo entry : _buffsPelea) {
				stats.addUnStat(entry.getEfectoID(), entry.getValor());
			}
			return stats;
		}
		
		public String stringGM() {
			String str = "";
			str += _celda.getID() + ";";
			str += 1 + ";";
			str += "0;";
			str += getID() + ";";
			str += getNombreLuchador() + ";";
			switch (_tipo) {
				case 1:// Perso
					Stats totalStats = getTotalStatsConBuff();
					str += _perso.getClase(false) + ";";
					str += _perso.getGfxID() + "^" + _perso.getTalla() + ";";
					str += _perso.getSexo() + ";";
					str += _perso.getNivel() + ";";
					str += _perso.getAlineacion() + ",";
					str += "0,";
					str += (_perso.estaMostrandoAlas() ? _perso.getNivelAlineacion() : "0") + ",";
					str += _perso.getID() + ";";
					str += (_perso.getColor1() == -1 ? "-1" : Integer.toHexString(_perso.getColor1())) + ";";
					str += (_perso.getColor2() == -1 ? "-1" : Integer.toHexString(_perso.getColor2())) + ";";
					str += (_perso.getColor3() == -1 ? "-1" : Integer.toHexString(_perso.getColor3())) + ";";
					str += _perso.getStringAccesorios() + ";";
					str += getPDVConBuff() + ";";
					str += totalStats.getEfecto(CentroInfo.STATS_ADD_PA) + ";";
					str += totalStats.getEfecto(CentroInfo.STATS_ADD_PM) + ";";
					str += totalStats.getEfecto(CentroInfo.STATS_ADD_ResPorc_NEUTRAL) + ";";
					str += totalStats.getEfecto(CentroInfo.STATS_ADD_ResPorc_TIERRA) + ";";
					str += totalStats.getEfecto(CentroInfo.STATS_ADD_ResPorc_FUEGO) + ";";
					str += totalStats.getEfecto(CentroInfo.STATS_ADD_ResPorc_AGUA) + ";";
					str += totalStats.getEfecto(CentroInfo.STATS_ADD_ResPorc_AIRE) + ";";
					str += totalStats.getEfecto(CentroInfo.STATS_ADD_ProbPerdida_PA) + ";";
					str += totalStats.getEfecto(CentroInfo.STATS_ADD_ProbPerdida_PM) + ";";
					str += _equipoBin + ";";
					if (_perso.estaMontando() && _perso.getMontura() != null)
						str += _perso.getMontura().getStringColor(_perso.stringColorDueñoPavo());
					str += ";";
					break;
				case 2:// Mob
					str += "-2;";
					str += _mob.getModelo().getGfxID() + "^" + _mob.getModelo().getTalla() + ";";
					str += _mob.getGrado() + ";";
					str += _mob.getModelo().getColores().replace(",", ";") + ";";
					str += "0,0,0,0;";
					str += getPDVMaxConBuff() + ";";
					str += _mob.getPA() + ";";
					str += _mob.getPM() + ";";
					str += _equipoBin;
					break;
				case 5:// Recaudador
					str += "-6;";// Recaudador
					str += "6000^100;";// GFXID^Size
					Gremio G = MundoDofus.getGremio(Recaudador.getIDGremioPorMapaID(_pelea._mapaReal.getID()));
					str += G.getNivel() + ";";
					str += "1;";
					str += "2;4;";
					str += (int) Math.floor(G.getNivel() / 2) + ";" + (int) Math.floor(G.getNivel() / 2) + ";"
							+ (int) Math.floor(G.getNivel() / 2) + ";" + (int) Math.floor(G.getNivel() / 2) + ";"
							+ (int) Math.floor(G.getNivel() / 2) + ";" + (int) Math.floor(G.getNivel() / 2) + ";"
							+ (int) Math.floor(G.getNivel() / 2) + ";";// Resistencias
					str += _equipoBin;
					break;
				case 7:// Prisma
					str += "-2;";
					str += (_prisma.getAlineacion() == 1 ? 8101 : 8100) + "^100;";
					str += _prisma.getNivel() + ";";
					str += "-1;-1;-1;";
					str += "0,0,0,0;";
					str += getPDVMaxConBuff() + ";";
					str += 0 + ";";
					str += 0 + ";";
					str += _equipoBin;
					break;
				case 10:// Doble
					Stats totalStats2 = getTotalStatsConBuff();
					str += _doble.getClase(false) + ";";
					str += _doble.getGfxID() + "^" + _doble.getTalla() + ";";
					str += _doble.getSexo() + ";";
					str += _doble.getNivel() + ";";
					str += _doble.getAlineacion() + ",";
					str += "0,";
					str += (_doble.estaMostrandoAlas() ? _doble.getNivelAlineacion() : "0") + ",";
					str += _doble.getID() + ";";
					str += (_doble.getColor1() == -1 ? "-1" : Integer.toHexString(_doble.getColor1())) + ";";
					str += (_doble.getColor2() == -1 ? "-1" : Integer.toHexString(_doble.getColor2())) + ";";
					str += (_doble.getColor3() == -1 ? "-1" : Integer.toHexString(_doble.getColor3())) + ";";
					str += _doble.getStringAccesorios() + ";";
					str += getPDVConBuff() + ";";
					str += totalStats2.getEfecto(CentroInfo.STATS_ADD_PA) + ";";
					str += totalStats2.getEfecto(CentroInfo.STATS_ADD_PM) + ";";
					str += totalStats2.getEfecto(CentroInfo.STATS_ADD_ResPorc_NEUTRAL) + ";";
					str += totalStats2.getEfecto(CentroInfo.STATS_ADD_ResPorc_TIERRA) + ";";
					str += totalStats2.getEfecto(CentroInfo.STATS_ADD_ResPorc_FUEGO) + ";";
					str += totalStats2.getEfecto(CentroInfo.STATS_ADD_ResPorc_AGUA) + ";";
					str += totalStats2.getEfecto(CentroInfo.STATS_ADD_ResPorc_AIRE) + ";";
					str += totalStats2.getEfecto(CentroInfo.STATS_ADD_ProbPerdida_PA) + ";";
					str += totalStats2.getEfecto(CentroInfo.STATS_ADD_ProbPerdida_PM) + ";";
					str += _equipoBin + ";";
					if (_doble.estaMontando() && _doble.getMontura() != null)
						str += _doble.getMontura().getStringColor(_doble.stringColorDueñoPavo());
					str += ";";
					break;
			}
			return str;
		}
		
		public void setEstado(int id, int estado) {
			_estados.remove(id);
			if (estado != 0)
				_estados.put(id, estado);
		}
		
		public boolean tieneEstado(int id) {
			if (_estados.get(id) == null)
				return false;
			return _estados.get(id) != 0;
		}
		
		public void disminuirEstados() {
			Map<Integer, Integer> copia = new TreeMap<Integer, Integer>();
			for (Entry<Integer, Integer> est : _estados.entrySet()) {
				if (est.getKey() <= 0)
					continue;
				int nVal = est.getValue() - 1;
				if (nVal == 0) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 950, getID() + "", getID() + "," + est.getKey() + ",0");
					continue;
				}
				copia.put(est.getKey(), nVal);
			}
			_estados.clear();
			_estados.putAll(copia);
		}
		
		public int getPDVMaxConBuff() {
			return _PDVMAX + getValorBuffPelea(125);
		}
		
		public int getPDVConBuff() {
			return _PDV + getValorBuffPelea(125);
		}
		
		public int getPDVMax() {
			return _PDVMAX;
		}
		
		public int getPDV() {
			return _PDV;
		}
		
		public void restarPDV(int pdv) {
			_PDV -= pdv;
			if (_intocable && pdv > 0) {
				_pelea._retos.remove(17);
				_pelea._retos.put(17, 2);
				GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(_pelea, 17);
				for (Luchador luch : _pelea._inicioLucEquipo1) {
					luch._intocable = false;
				}
			}
			if (_contaminacion && pdv > 0) {
				_contaminado = true;
			}
		}
		
		public void setPDV(int pdv) {
			_PDV = pdv;
		}
		
		public void setPDVMAX(int pdv) {
			_PDVMAX = pdv;
		}
		
		public int getValorBuffPelea(int id) {
			int valor = 0;
			for (EfectoHechizo entry : _buffsPelea) {
				if (entry.getEfectoID() == id)
					valor += entry.getValor();
			}
			return valor;
		}
		
		public void aplicarBuffInicioTurno(Pelea pelea) {
			synchronized (_buffsPelea) {
				for (int efectoID : CentroInfo.BUFF_INICIO_TURNO) {
					ArrayList<EfectoHechizo> buffs = new ArrayList<EfectoHechizo>();
					buffs.addAll(_buffsPelea);
					for (EfectoHechizo entry : buffs) {
						if (entry.getEfectoID() == efectoID) {
							entry.aplicarBuffDeInicioTurno(pelea, this);
						}
					}
				}
			}
		}
		
		public EfectoHechizo getBuff(int id) {
			for (EfectoHechizo entry : _buffsPelea) {
				if (entry.getEfectoID() == id && entry.getDuracion() > 0) {
					return entry;
				}
			}
			return null;
		}
		
		public boolean tieneBuff(int id) {
			for (EfectoHechizo entry : _buffsPelea) {
				if (entry.getEfectoID() == id && entry.getDuracion() > 0) {
					return true;
				}
			}
			return false;
		}
		
		public int getDañoDominio(int id) {
			int value = 0;
			for (EfectoHechizo entry : _buffsPelea) {
				if (entry.getHechizoID() == id)
					value += entry.getValor();
			}
			return value;
		}
		
		public boolean tieneBuffHechizoID(int id) {
			for (EfectoHechizo entry : _buffsPelea) {
				if (entry.getHechizoID() == id)
					return true;
			}
			return false;
		}
		
		public void actualizarBuffsPelea() {
			ArrayList<EfectoHechizo> efectos = new ArrayList<EfectoHechizo>();
			disminuirEstados();
			for (EfectoHechizo buff : _buffsPelea) {
				if (buff.disminuirDuracion() > 0) {
					efectos.add(buff);
				} else {
					switch (buff.getEfectoID()) {
						case 125:
							int valor = buff.getValor();
							if (buff.getHechizoID() == 441) {
								_PDVMAX = (_PDVMAX - valor);
								int pdv = 0;
								if (_PDV - valor <= 0) {
									pdv = 0;
									_pelea.agregarAMuertos(this);
									continue;
								} else
									pdv = (_PDV - valor);
								_PDV = pdv;
							}
							break;
						case 150:
							GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 150, buff.getLanzador().getID() + "", getID() + ",0");
							break;
					}
				}
			}
			_buffsPelea.clear();
			_buffsPelea.addAll(efectos);
		}
		
		public void addBuff(int efectoId, int valor, int turnos, int duracion, boolean desbufeable, int hechizoID, String args,
				Luchador lanzador, boolean veneno) {
			if (hechizoID == 99 || hechizoID == 5 || hechizoID == 20 || hechizoID == 127 || hechizoID == 89 || hechizoID == 126
					|| hechizoID == 115 || hechizoID == 192 || hechizoID == 4 || hechizoID == 1 || hechizoID == 6
					|| hechizoID == 14 || hechizoID == 18 || hechizoID == 7 || hechizoID == 284 || hechizoID == 197) {
				desbufeable = true;
			}
			if (hechizoID == 431 || hechizoID == 433 || hechizoID == 437 || hechizoID == 443)
				desbufeable = false;
			_buffsPelea.add(new EfectoHechizo(efectoId, valor, (_puedeJugar ? turnos + 1 : turnos), duracion, desbufeable,
					lanzador, args, hechizoID, veneno));
			switch (efectoId) {
				case 106:// Renvio de hechizo
					GestorSalida.ENVIAR_GIE_EFECTO_HECHIZO(_pelea, 7, efectoId, getID(), -1, valor + "", "10", "", turnos,
							hechizoID);
					break;
				case 950:// Estados
					GestorSalida.ENVIAR_GIE_EFECTO_HECHIZO(_pelea, 7, efectoId, getID(), -1, "", valor + "", "", turnos,
							hechizoID);
					break;
				case 79:// Suerte de zurcarak
					valor = Integer.parseInt(args.split(";")[0]);
					String valMax = args.split(";")[1];
					String suerte = args.split(";")[2];
					GestorSalida.ENVIAR_GIE_EFECTO_HECHIZO(_pelea, 7, efectoId, getID(), valor, valMax, suerte, "", turnos,
							hechizoID);
					break;
				case 788:// Pone de manifiesto mensaje el tiempo de concluido Sacrificio de X sobre Y turnos
					valor = Integer.parseInt(args.split(";")[1]);
					String valMax2 = args.split(";")[2];
					if (Integer.parseInt(args.split(";")[0]) == 108)
						return;
					GestorSalida.ENVIAR_GIE_EFECTO_HECHIZO(_pelea, 7, efectoId, getID(), valor, "" + valor, "" + valMax2, "",
							turnos, hechizoID);
					break;
				case 96: // daños agua
				case 97: // daños tierra
				case 98: // daños aire
				case 99: // daños fuego
				case 100: // daños neutral
				case 107: // reenvio de daños
				case 108: // curar
				case 165: // Aumenta los daños % de armas
				case 781: // minimiza los efectos aleatorios
				case 782: // maximiza los efectos aleatorios
					valor = Integer.parseInt(args.split(";")[0]);
					String valMax1 = args.split(";")[1];
					if (valMax1.compareTo("-1") == 0 || hechizoID == 82 || hechizoID == 94 || hechizoID == 132) {
						GestorSalida
								.ENVIAR_GIE_EFECTO_HECHIZO(_pelea, 7, efectoId, getID(), valor, "", "", "", turnos, hechizoID);
					} else if (valMax1.compareTo("-1") != 0) {
						GestorSalida.ENVIAR_GIE_EFECTO_HECHIZO(_pelea, 7, efectoId, getID(), valor, valMax1, "", "", turnos,
								hechizoID);
					}
					break;
				default:
					GestorSalida.ENVIAR_GIE_EFECTO_HECHIZO(_pelea, 7, efectoId, getID(), valor, "", "", "", turnos, hechizoID);
					break;
			}
		}
		
		public int getIniciativa() {
			if (_tipo == 1)
				return _perso.getIniciativa();
			if (_tipo == 2)
				return _mob.getIniciativa();
			if (_tipo == 5)
				return MundoDofus.getGremio(_recaudador.getGremioID()).getNivel();
			if (_tipo == 7)
				return 0;
			if (_tipo == 10)
				return _doble.getIniciativa();
			return 0;
		}
		
		public int getNivel() {
			if (_tipo == 1)
				return _perso.getNivel();
			if (_tipo == 2)
				return _mob.getNivel();
			if (_tipo == 5)
				return MundoDofus.getGremio(_recaudador.getGremioID()).getNivel();
			if (_tipo == 7)
				return _prisma.getNivel();
			if (_tipo == 10)
				return _doble.getNivel();
			return 0;
		}
		
		public String xpString(String str) {
			if (_perso != null) {
				int max = _perso.getNivel() + 1;
				if (max > Bustemu.MAX_NIVEL)
					max = Bustemu.MAX_NIVEL;
				return MundoDofus.getExpNivel(_perso.getNivel())._personaje + str + _perso.getExperiencia() + str
						+ MundoDofus.getExpNivel(max)._personaje;
			}
			return "0" + str + "0" + str + "0";
		}
		
		public String getNombreLuchador() {
			if (_tipo == 1)
				return _perso.getNombre();
			if (_tipo == 2)
				return _mob.getModelo().getID() + "";
			if (_tipo == 5)
				return (_recaudador.getN1() + "," + _recaudador.getN2());
			if (_tipo == 7)
				return (_prisma.getAlineacion() == 1 ? 1111 : 1112) + "";
			if (_tipo == 10)
				return _doble.getNombre();
			return "";
		}
		
		public MobGrado getMob() {
			if (_tipo == 2)
				return _mob;
			return null;
		}
		
		public int getEquipoBin() {
			return _equipoBin;
		}
		
		public int getParamEquipoAliado() {
			return _pelea.getParamEquipo(_id);
		}
		
		public int getParamEquipoEnemigo() {
			return _pelea.getIDEquipoEnemigo(_id);
		}
		
		public boolean puedeJugar() {
			return _puedeJugar;
		}
		
		public void setPuedeJugar(boolean b) {
			_puedeJugar = b;
		}
		
		public int getPAConBuff() {
			return getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_PA);
		}
		
		public int getPMConBuff() {
			return getTotalStatsConBuff().getEfecto(CentroInfo.STATS_ADD_PM);
		}
		
		public int getTempPA(Pelea pelea) {
			return pelea._tempLuchadorPA;
		}
		
		public int getTempPM(Pelea pelea) {
			return pelea._tempLuchadorPM;
		}
		
		public void addTempPM(Pelea pelea, int pm) {
			pelea._tempLuchadorPM += pm;
		}
		
		public void addTempPA(Pelea pelea, int pa) {
			pelea._tempLuchadorPA += pa;
		}
		
		public void setInvocador(Luchador invocador) {
			_invocador = invocador;
		}
		
		public Luchador getInvocador() {
			return _invocador;
		}
		
		public void aumentarInvocaciones() {
			_nroInvocaciones++;
		}
		
		public int getNroInvocaciones() {
			return _nroInvocaciones;
		}
		
		public boolean esInvocacion() {
			return (_invocador != null);
		}
		
		public boolean esRecaudador() {
			return (_recaudador != null);
		}
		
		public boolean esPrisma() {
			return (_prisma != null);
		}
		
		public boolean esDoble() {
			return (_doble != null);
		}
		
		public synchronized void desbuffear() {
			ArrayList<EfectoHechizo> nuevosBuffs = new ArrayList<EfectoHechizo>();
			for (EfectoHechizo EH : _buffsPelea) {
				if (!EH.esDesbufeable())
					nuevosBuffs.add(EH);
				switch (EH.getEfectoID()) {
					case CentroInfo.STATS_ADD_PA:
					case CentroInfo.STATS_ADD_PA2:
						GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 101, getID() + "", getID() + ",-" + EH.getValor());
						break;
					case CentroInfo.STATS_ADD_PM:
					case CentroInfo.STATS_ADD_PM2:
						GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 127, getID() + "", getID() + ",-" + EH.getValor());
						break;
				}
			}
			_buffsPelea.clear();
			_buffsPelea.addAll(nuevosBuffs);
			if (_perso != null && !_estaRetirado)
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
		}
		
		public void fullPDV() {
			_PDV = _PDVMAX;
		}
		
		public void setEstaMuerto(boolean b) {
			_estaMuerto = b;
		}
		
		public void hacerseVisible(int turnos) {
			if (turnos >= 1) {
				return;
			}
			ArrayList<EfectoHechizo> buffs = new ArrayList<EfectoHechizo>();
			buffs.addAll(getBuffPelea());
			for (EfectoHechizo EH : buffs) {
				if (EH.getEfectoID() == 150)
					getBuffPelea().remove(EH);
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 150, getID() + "", getID() + ",0");
			GestorSalida.ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES(_pelea, 7, this);
		}
		
		public void aparecer(Luchador mostrar) {
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 150, getID() + "", getID() + ",0");
			GestorSalida.ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES(_pelea, 7, this);
		}
		
		public int getPDVMAXFueraPelea() {
			if (_perso != null)
				return _perso.getPDVMAX();
			if (_mob != null)
				return _mob.getPDVMAX();
			return 0;
		}
		
		public int getGfxDefecto() {
			if (_perso != null)
				return _perso.getGfxID();
			if (_mob != null)
				return _mob.getModelo().getGfxID();
			return 0;
		}
		
		public long getXpGive() {
			if (_mob != null)
				return _mob.getBaseXp();
			return 0;
		}
	}
	public static class Glifo {
		private Luchador _lanzador;
		private Celda _celda;
		private byte _tamaño;
		private int _hechizos;
		private StatsHechizos _glifoHechizo;
		private byte _duracion;
		private Pelea _pelea;
		private int _color;
		
		public Glifo(Pelea pelea, Luchador lanzador, Celda celda, byte tamaño, StatsHechizos glifoHechizo, byte duracion,
				int hechizo) {
			_pelea = pelea;
			_lanzador = lanzador;
			_celda = celda;
			_hechizos = hechizo;
			_tamaño = tamaño;
			_glifoHechizo = glifoHechizo;
			_duracion = duracion;
			_color = CentroInfo.getColorGlifo(hechizo);
		}
		
		public Celda getCelda() {
			return _celda;
		}
		
		public byte getTamaño() {
			return _tamaño;
		}
		
		public Luchador getLanzador() {
			return _lanzador;
		}
		
		public byte getDuracion() {
			return _duracion;
		}
		
		public int disminuirDuracion() {
			_duracion--;
			return _duracion;
		}
		
		public void activarGlifo(Luchador glifeado) {
			String str = _hechizos + "," + _celda.getID() + ",0,1,1," + _lanzador.getID();
			GestorSalida.ENVIAR_GA_ACCION_PELEA(_pelea, 7, 307, glifeado.getID() + "", str);
			_glifoHechizo.aplicaHechizoAPelea(_pelea, _lanzador, glifeado.getCeldaPelea(), false);
			_pelea.verificaSiAcaboPelea();
		}
		
		public void desaparecer() {
			GestorSalida.ENVIAR_GDZ_ACTUALIZA_ZONA_EN_PELEA(_pelea, 7, "-", _celda.getID(), _tamaño, _color);
			GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_EN_PELEA(_pelea, 7, _celda.getID());
		}
		
		public int getColor() {
			return _color;
		}
	}
	public static class HechizoLanzado {
		private int _hechizoId = 0;
		private int _sigLanzamiento = 0;
		private Luchador _objetivo = null;
		
		public HechizoLanzado(Luchador objetivo, StatsHechizos sHechizo, Luchador lanzador) {
			_objetivo = objetivo;
			_hechizoId = sHechizo.getHechizoID();
			if (lanzador.getTipo() == 1 && lanzador.getPersonaje().getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
				int modi = lanzador.getPersonaje().getModifSetClase(sHechizo.getHechizoID(), 286);
				_sigLanzamiento = sHechizo.getSigLanzamiento() - modi;
			} else
				_sigLanzamiento = sHechizo.getSigLanzamiento();
		}
		
		public void actuSigLanzamiento() {
			_sigLanzamiento--;
		}
		
		public int getSigLanzamiento() {
			return _sigLanzamiento;
		}
		
		public int getID() {
			return _hechizoId;
		}
		
		public Luchador getObjetivo() {
			return _objetivo;
		}
		
		public static boolean poderSigLanzamiento(Luchador lanzador, int id) {
			for (HechizoLanzado HL : lanzador.getHechizosLanzados()) {
				if (HL._hechizoId == id && HL.getSigLanzamiento() > 0)
					return false;
			}
			return true;
		}
		
		public static int getNroLanzamientos(Luchador lanzador, int id) {
			int nro = 0;
			for (HechizoLanzado HL : lanzador.getHechizosLanzados()) {
				if (HL._hechizoId == id)
					nro++;
			}
			return nro;
		}
		
		public static int getNroLanzPorObjetivo(Luchador lanzador, Luchador objetivo, int id) {
			if (objetivo == null)
				return 0;
			int nro = 0;
			for (HechizoLanzado HL : lanzador.getHechizosLanzados()) {
				if (HL._objetivo == null)
					continue;
				if (HL._hechizoId == id && HL._objetivo.getID() == objetivo.getID())
					nro++;
			}
			return nro;
		}
	}
	private int _id;
	private Map<Integer, Luchador> _equipo1 = new TreeMap<Integer, Luchador>();
	private Map<Integer, Luchador> _equipo2 = new TreeMap<Integer, Luchador>();
	private Map<Integer, Luchador> _listaMuertos = new TreeMap<Integer, Luchador>();
	private Map<Integer, Personaje> _espectadores = new TreeMap<Integer, Personaje>();
	private Mapa _mapaCopia;
	private Mapa _mapaReal;
	private Luchador _luchInit1;
	private Luchador _luchInit2;
	private int _idLuchInit1;
	private int _idLuchInit2;
	private ArrayList<Celda> _celdasPos1 = new ArrayList<Celda>();
	private ArrayList<Celda> _celdasPos2 = new ArrayList<Celda>();
	private int _estadoPelea = 0;
	private int _gremioID = -1;
	private int _tipo = -1;
	private boolean _cerrado1 = false;
	private boolean _soloGrupo1 = false;
	private boolean _cerrado2 = false;
	private boolean _soloGrupo2 = false;
	private boolean _espectadorOk = true;
	private boolean _ayuda1 = false;
	private boolean _ayuda2 = false;
	private int _celdaColor2;
	private int _celdaColor1;
	private int _nroOrdenLuc;
	private int _tempLuchadorPA;
	private int _tempLuchadorPM;
	private int _tempLuchadorPAusados;
	private int _tempLuchadorPMusados;
	private String _tempAccion = "";
	private List<Luchador> _ordenJugadores = new ArrayList<Luchador>();
	private Timer _tiempoTurno;
	private List<Glifo> _glifos = new ArrayList<Glifo>();
	private List<Trampa> _trampas = new ArrayList<Trampa>();
	private GrupoMobs _mobGrupo;
	private ArrayList<Luchador> _capturadores = new ArrayList<Luchador>(8);
	private boolean _esCapturable = false;
	private int _capturadorGanador = -1;
	private PiedraDeAlma _piedraAlma;
	private Recaudador _Recaudador;
	private Prisma _Prisma;
	private ArrayList<Integer> _mobsMuertosReto = new ArrayList<Integer>();
	private ArrayList<Integer> _muertesLuchInic1 = new ArrayList<Integer>();
	private ArrayList<Integer> _muertesLuchInic2 = new ArrayList<Integer>();
	private int _cantLucEquipo1 = 0;
	private int _cantLucEquipo2 = 1;
	public List<Luchador> _inicioLucEquipo1 = new ArrayList<Luchador>();
	public List<Luchador> _inicioLucEquipo2 = new ArrayList<Luchador>();
	private int _numeroInvos = 0;
	private Map<Integer, Celda> _posinicial = new TreeMap<Integer, Celda>();
	private String _listadefensores = "";
	private Map<Integer, String> _stringReto = new TreeMap<Integer, String>();
	private int _idMobReto = 0;
	private int _luchMenorNivelReto = 0;
	private int _elementoReto = 0;
	private int _cantMobsMuerto = 0;
	private Map<Integer, Integer> _ordenNivelMobs = new TreeMap<Integer, Integer>();
	private Map<Integer, Luchador> _ordenLuchMobs = new TreeMap<Integer, Luchador>();
	private Map<Integer, Integer> _retos = new TreeMap<Integer, Integer>();
	private int _estrellas = 0;
	private long _tiempoInicio = 0;
	private long _tiempoInicioTurno = 0L;
	private int _misionPVP = 0;
	private boolean _evento = false;
	private int _cantUltAfec = 0;
	
	public void setUltAfec(int afec) {
		_cantUltAfec = afec;
	}
	
	public void setEvento(boolean even) {
		_evento = even;
	}
	
	public Map<Integer, Integer> getRetos() {
		return _retos;
	}
	
	public synchronized void tiempoTurno() {
		if (_tiempoInicio == 0) {
			long tiempoRestante = 44000L - (System.currentTimeMillis() - _tiempoInicioTurno);
			if (tiempoRestante <= 0) {
				try {
					iniciarPelea();
				} catch (Exception e) {
					GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_ADMINS("@", 0, "BUG-PELEA", "El mapa " + _mapaReal.getID()
							+ " no inicia la pelea, ir a debugearla.");
				}
				if (_Recaudador != null)
					_Recaudador.setTiempoTurno(45000);
				else if (_Prisma != null)
					_Prisma.setTiempoTurno(45000);
			} else {
				if (_Recaudador != null)
					_Recaudador.setTiempoTurno((int) tiempoRestante);
				else if (_Prisma != null)
					_Prisma.setTiempoTurno((int) tiempoRestante);
			}
		} else {
			long system = System.currentTimeMillis();
			if (system - _tiempoInicioTurno >= 27000L) {
				_tiempoInicioTurno = system;
				try {
					finTurno();
				} catch (Exception e) {
					GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_ADMINS("@", 0, "BUG-PELEA", "El mapa " + _mapaReal.getID()
							+ " tiene los turnos bugeados, ir a debugearla, USAR COMANDO: CHECKTURNO.");
				}
			}
		}
	}
	
	public synchronized void turnoDebug() {
		if (_tiempoInicio != 0) {
			long system = System.currentTimeMillis();
			if (system - _tiempoInicioTurno >= 40000L) {
				_tiempoInicioTurno = system;
				try {
					finTurno();
				} catch (Exception e) {
					GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_ADMINS("@", 0, "BUG-PELEA", "El mapa " + _mapaReal.getID()
							+ " tiene los turnos bugeados, ir a debugearla, USAR COMANDO: CHECKTURNO.");
				}
			}
		}
	}
	
	public void putStringReto(int id, String reto) {
		_stringReto.put(id, reto);
	}
	
	public void setListaDefensores(String str) {
		_listadefensores = str;
	}
	
	public String getListaDefensores() {
		return _listadefensores;
	}
	
	public Map<Integer, Celda> getPosInicial() {
		return _posinicial;
	}
	
	public int getNumeroInvos() {
		return _numeroInvos;
	}
	
	public void setIDMobReto(int mob) {
		_idMobReto = mob;
	}
	
	public int getIDMobReto() {
		return _idMobReto;
	}
	
	public synchronized void finAccion() {
		notifyAll();
	}
	
	public Pelea(int tipo, int id, Mapa mapa, Personaje init1, Personaje init2) {
		try {
			_tipo = tipo;
			_id = id;
			_mapaCopia = mapa.copiarMapa();
			_mapaReal = mapa;
			int id1 = init1.getID();
			int id2 = init2.getID();
			_luchInit1 = new Luchador(this, init1);
			_luchInit2 = new Luchador(this, init2);
			_idLuchInit1 = _luchInit1.getID();
			_idLuchInit2 = _luchInit2.getID();
			_equipo1.put(id1, _luchInit1);
			_equipo2.put(id2, _luchInit2);
			_tiempoInicioTurno = System.currentTimeMillis();
			GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(this, 3, 2, _tipo == 0, true, false, _tipo == 0 ? 0 : 45000, _tipo);
			GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(init1, 0);
			GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(init2, 0);
			if (_tipo != 0) {
				_tiempoTurno = new Timer(1000, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (_tiempoInicio == 0)
							tiempoTurno();
						else
							_tiempoTurno.stop();
					}
				});
				_tiempoTurno.start();
			}
			Random equipos = new Random();
			if (equipos.nextBoolean()) {
				_celdasPos1 = analizarPosiciones(0);
				_celdasPos2 = analizarPosiciones(1);
				GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 0);
				GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 2, _mapaCopia.getLugaresString(), 1);
				_celdaColor1 = 0;
				_celdaColor2 = 1;
			} else {
				_celdasPos1 = analizarPosiciones(1);
				_celdasPos2 = analizarPosiciones(0);
				_celdaColor1 = 1;
				_celdaColor2 = 0;
				GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 1);
				GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 2, _mapaCopia.getLugaresString(), 0);
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 3 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id2 + "", id2 + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id2 + "", id2 + "," + 3 + ",0");
			_luchInit1.setCeldaPelea(getCeldaRandom(_celdasPos1));
			_luchInit2.setCeldaPelea(getCeldaRandom(_celdasPos2));
			init1.getCelda().removerPersonaje(id1);
			init2.getCelda().removerPersonaje(id2);
			_luchInit1.getCeldaPelea().addLuchador(_luchInit1);
			_luchInit2.getCeldaPelea().addLuchador(_luchInit2);
			init1.setPelea(this);
			_luchInit1.setEquipoBin(0);
			init2.setPelea(this);
			_luchInit2.setEquipoBin(1);
			Mapa mapaTemp = init1.getMapa();
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, id1);
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, id2);
			if (_tipo == 1) {
				GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(mapaTemp, 0, id1, id2, init1.getCelda().getID(),
						"0;" + init1.getAlineacion(), init2.getCelda().getID(), "0;" + init2.getAlineacion());
			} else {
				GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(mapaTemp, 0, id1, id2, init1.getCelda().getID(), "0;-1", init2
						.getCelda().getID(), "0;-1");
			}
			GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, id1, _luchInit1);
			GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, id2, _luchInit2);
			GestorSalida.ENVIAR_GM_LUCHADOR_A_TODA_PELEA(this, 7, _mapaCopia);
			setEstado(2);
		} catch (Exception e) {
			return;
		}
	}
	
	public Pelea(int id, Mapa mapa, Personaje perso, Prisma prisma) {
		prisma.setEstadoPelea(0);
		prisma.setPeleaID(id);
		prisma.setPelea(this);
		_tipo = 2;
		_id = id;
		_mapaCopia = mapa.copiarMapa();
		_mapaReal = mapa;
		_tiempoInicioTurno = System.currentTimeMillis();
		_luchInit1 = new Luchador(this, perso);
		_idLuchInit1 = _luchInit1.getID();
		_Prisma = prisma;
		_idLuchInit2 = _Prisma.getID();
		int id1 = perso.getID();
		_equipo1.put(id1, _luchInit1);
		Luchador lPrisma = new Luchador(this, prisma);
		_equipo2.put(-1, lPrisma);
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(this, 1, 2, false, true, false, 45000, _tipo);
		GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0);
		_tiempoTurno = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_tiempoInicio == 0)
					tiempoTurno();
				else
					_tiempoTurno.stop();
			}
		});
		_tiempoTurno.start();
		Random equipos = new Random();
		if (equipos.nextBoolean()) {
			_celdasPos1 = analizarPosiciones(0);
			_celdasPos2 = analizarPosiciones(1);
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 0);
			_celdaColor1 = 0;
			_celdaColor2 = 1;
		} else {
			_celdasPos1 = analizarPosiciones(1);
			_celdasPos2 = analizarPosiciones(0);
			_celdaColor1 = 1;
			_celdaColor2 = 0;
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 1);
		}
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 8 + ",0");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 3 + ",0");
		List<Entry<Integer, Luchador>> equipo2 = new ArrayList<Entry<Integer, Luchador>>();
		equipo2.addAll(_equipo2.entrySet());
		for (Entry<Integer, Luchador> entry : equipo2) {
			Luchador lprisma = entry.getValue();
			Celda celdaRandom = getCeldaRandom(_celdasPos2);
			if (celdaRandom == null) {
				_equipo2.remove(lprisma.getID());
				continue;
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, lprisma.getID() + "", lprisma.getID() + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, lprisma.getID() + "", lprisma.getID() + "," + 3 + ",0");
			lprisma.setCeldaPelea(celdaRandom);
			lprisma.getCeldaPelea().addLuchador(lprisma);
			lprisma.setEquipoBin(1);
			lprisma.fullPDV();
		}
		_luchInit1.setCeldaPelea(getCeldaRandom(_celdasPos1));
		perso.getCelda().removerPersonaje(id1);
		_luchInit1.getCeldaPelea().addLuchador(_luchInit1);
		perso.setPelea(this);
		_luchInit1.setEquipoBin(0);
		Mapa mapaTemp = perso.getMapa();
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, id1);
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, prisma.getID());
		GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(mapaTemp, 0, id1, prisma.getID(), perso.getCelda().getID(),
				"0;" + perso.getAlineacion(), prisma.getCelda(), "0;" + prisma.getAlineacion());
		GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, id1, _luchInit1);
		for (Luchador f : _equipo2.values()) {
			GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, prisma.getID(), f);
		}
		GestorSalida.ENVIAR_GM_LUCHADOR_A_TODA_PELEA(this, 7, _mapaCopia);
		setEstado(2);
		String str = "";
		if (_Prisma != null)
			str = prisma.getMapa() + "|" + prisma.getX() + "|" + prisma.getY();
		for (Personaje z : MundoDofus.getPJsEnLinea()) {
			if (z == null)
				continue;
			if (z.getAlineacion() != prisma.getAlineacion())
				continue;
			GestorSalida.ENVIAR_CA_MENSAJE_ATAQUE_PRISMA(z, str);
		}
	}
	
	public Pelea(int id, Mapa mapa, Personaje perso, GrupoMobs grupoMob, int tipo) {
		_tipo = tipo;
		_id = id;
		_mapaCopia = mapa.copiarMapa();
		_mapaReal = mapa;
		_tiempoInicioTurno = System.currentTimeMillis();
		int id1 = perso.getID();
		_luchInit1 = new Luchador(this, perso);
		_idLuchInit1 = _luchInit1.getID();
		_mobGrupo = grupoMob;
		_idLuchInit2 = _mobGrupo.getID();
		_equipo1.put(id1, _luchInit1);
		for (Entry<Integer, MobGrado> entry : grupoMob.getMobs().entrySet()) {
			entry.getValue().setIdEnPelea(entry.getKey());
			Luchador mob = new Luchador(this, entry.getValue());
			_equipo2.put(entry.getKey(), mob);
		}
		if (tipo == 4) {
			_estrellas = grupoMob.getEstrellas();
			grupoMob.setEstrellas(0);
		}
		_tiempoTurno = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_tiempoInicio == 0)
					tiempoTurno();
				else
					_tiempoTurno.stop();
			}
		});
		_tiempoTurno.start();
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(this, 1, 2, false, true, false, 45000, _tipo);
		GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0);
		if (_equipo2.size() <= 2) {
			_celdasPos1 = analizarPosiciones(0);
			_celdasPos2 = analizarPosiciones(1);
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 0);
			_celdaColor1 = 0;
			_celdaColor2 = 1;
		} else {
			Random equipos = new Random();
			if (equipos.nextBoolean()) {
				_celdasPos1 = analizarPosiciones(0);
				_celdasPos2 = analizarPosiciones(1);
				GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 0);
				_celdaColor1 = 0;
				_celdaColor2 = 1;
			} else {
				_celdasPos1 = analizarPosiciones(1);
				_celdasPos2 = analizarPosiciones(0);
				_celdaColor1 = 1;
				_celdaColor2 = 0;
				GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 1);
			}
		}
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 8 + ",0");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 3 + ",0");
		List<Entry<Integer, Luchador>> equipo2 = new ArrayList<Entry<Integer, Luchador>>();
		equipo2.addAll(_equipo2.entrySet());
		for (Entry<Integer, Luchador> entry : equipo2) {
			Luchador mob = entry.getValue();
			Celda celdaRandom = getCeldaRandom(_celdasPos2);
			if (celdaRandom == null) {
				_equipo2.remove(mob.getID());
				continue;
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, mob.getID() + "", mob.getID() + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, mob.getID() + "", mob.getID() + "," + 3 + ",0");
			mob.setCeldaPelea(celdaRandom);
			mob.getCeldaPelea().addLuchador(mob);
			mob.setEquipoBin(1);
			mob.fullPDV();
		}
		_luchInit1.setCeldaPelea(getCeldaRandom(_celdasPos1));
		perso.getCelda().removerPersonaje(id1);
		_luchInit1.getCeldaPelea().addLuchador(_luchInit1);
		perso.setPelea(this);
		_luchInit1.setEquipoBin(0);
		Mapa mapaTemp = perso.getMapa();
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, id1);
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, grupoMob.getID());
		if (tipo == 4) {
			GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(mapaTemp, 4, id1, grupoMob.getID(), perso.getCelda().getID(), "0;-1",
					grupoMob.getCeldaID() - 1, "1;-1");
			GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, id1, _luchInit1);
			for (Luchador luchador : _equipo2.values()) {
				GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, grupoMob.getID(), luchador);
			}
		}
		GestorSalida.ENVIAR_GM_LUCHADOR_A_TODA_PELEA(this, 7, _mapaCopia);
		setEstado(2);
	}
	
	public Pelea(int id, Mapa mapa, Personaje perso, Recaudador recaudador) {
		setGremioID(recaudador.getGremioID());
		recaudador.setEstadoPelea(1);
		recaudador.setPeleaID(id);
		recaudador.setPelea(this);
		_tipo = 5;
		_id = id;
		_mapaCopia = mapa.copiarMapa();
		_mapaReal = mapa;
		_tiempoInicioTurno = System.currentTimeMillis();
		_luchInit1 = new Luchador(this, perso);
		_idLuchInit1 = _luchInit1.getID();
		_Recaudador = recaudador;
		_idLuchInit2 = _Recaudador.getID();
		int id1 = perso.getID();
		_equipo1.put(id1, _luchInit1);
		Luchador lRecaudador = new Luchador(this, recaudador);
		_equipo2.put(-1, lRecaudador);
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(this, 1, 2, false, true, false, 45000, _tipo);
		GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0);
		_tiempoTurno = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_tiempoInicio == 0)
					tiempoTurno();
				else
					_tiempoTurno.stop();
			}
		});
		_tiempoTurno.start();
		Random equipos = new Random();
		if (equipos.nextBoolean()) {
			_celdasPos1 = analizarPosiciones(0);
			_celdasPos2 = analizarPosiciones(1);
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 0);
			_celdaColor1 = 0;
			_celdaColor2 = 1;
		} else {
			_celdasPos1 = analizarPosiciones(1);
			_celdasPos2 = analizarPosiciones(0);
			_celdaColor1 = 1;
			_celdaColor2 = 0;
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 1);
		}
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 8 + ",0");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, id1 + "", id1 + "," + 3 + ",0");
		List<Entry<Integer, Luchador>> equipo2 = new ArrayList<Entry<Integer, Luchador>>();
		equipo2.addAll(_equipo2.entrySet());
		for (Entry<Integer, Luchador> entry : equipo2) {
			Luchador recau = entry.getValue();
			Celda celdaRandom = getCeldaRandom(_celdasPos2);
			if (celdaRandom == null) {
				_equipo2.remove(recau.getID());
				continue;
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, recau.getID() + "", recau.getID() + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, recau.getID() + "", recau.getID() + "," + 3 + ",0");
			recau.setCeldaPelea(celdaRandom);
			recau.getCeldaPelea().addLuchador(recau);
			recau.setEquipoBin(1);
			recau.fullPDV();
		}
		_luchInit1.setCeldaPelea(getCeldaRandom(_celdasPos1));
		perso.getCelda().removerPersonaje(id1);
		_luchInit1.getCeldaPelea().addLuchador(_luchInit1);
		perso.setPelea(this);
		_luchInit1.setEquipoBin(0);
		Mapa mapaTemp = perso.getMapa();
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, _idLuchInit1);
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, recaudador.getID());
		GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(mapaTemp, 5, id1, recaudador.getID(), perso.getCelda().getID(), "0;-1",
				recaudador.getCeldalID(), "3;-1");
		GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, id1, _luchInit1);
		for (Luchador luchador : _equipo2.values()) {
			GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaTemp, recaudador.getID(), luchador);
		}
		GestorSalida.ENVIAR_GM_LUCHADOR_A_TODA_PELEA(this, 7, _mapaCopia);
		setEstado(2);
		String str = "";
		if (_Recaudador != null)
			str = "A" + _Recaudador.getN1() + "," + _Recaudador.getN2() + "|.|" + _Recaudador.getMapaID() + "|"
					+ _Recaudador.getCeldalID();
		for (Personaje z : MundoDofus.getGremio(_gremioID).getPjMiembros()) {
			if (z == null)
				continue;
			if (z.enLinea()) {
				GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z, Recaudador.analizarRecaudadores(z.getGremio().getID()));
				Recaudador.analizarAtaque(z, _gremioID);
				Recaudador.analizarDefensa(z, _gremioID);
				GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(z, str);
			}
		}
	}
	
	public Pelea(int id, Mapa mapa, ArrayList<Personaje> grupo1, ArrayList<Personaje> grupo2) {
		_tipo = CentroInfo.PELEA_TIPO_COLISEO;
		_id = id;
		_mapaCopia = mapa.copiarMapa();
		_mapaReal = mapa;
		_tiempoInicioTurno = System.currentTimeMillis();
		for (Personaje persos : grupo1) {
			persos.setPelea(this);
			Luchador pj1 = new Luchador(this, persos);
			_equipo1.put(persos.getID(), pj1);
			GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(persos, 0);
		}
		for (Personaje persos : grupo2) {
			persos.setPelea(this);
			Luchador pj2 = new Luchador(this, persos);
			_equipo2.put(persos.getID(), pj2);
			GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(persos, 0);
		}
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(this, 3, 2, false, true, false, 45000, _tipo);
		_tiempoTurno = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_tiempoInicio == 0)
					tiempoTurno();
				else
					_tiempoTurno.stop();
			}
		});
		_tiempoTurno.start();
		List<Entry<Integer, Luchador>> equipo1 = new ArrayList<Entry<Integer, Luchador>>();
		equipo1.addAll(_equipo1.entrySet());
		List<Entry<Integer, Luchador>> equipo2 = new ArrayList<Entry<Integer, Luchador>>();
		equipo2.addAll(_equipo2.entrySet());
		Random equipos = new Random();
		if (equipos.nextBoolean()) {
			_celdasPos1 = analizarPosiciones(0);
			_celdasPos2 = analizarPosiciones(1);
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 0);
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 2, _mapaCopia.getLugaresString(), 1);
			_celdaColor1 = 0;
			_celdaColor2 = 1;
		} else {
			_celdasPos1 = analizarPosiciones(1);
			_celdasPos2 = analizarPosiciones(0);
			_celdaColor1 = 1;
			_celdaColor2 = 0;
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, _mapaCopia.getLugaresString(), 1);
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 2, _mapaCopia.getLugaresString(), 0);
		}
		for (Entry<Integer, Luchador> entry : equipo2) {
			Luchador lucha = entry.getValue();
			Celda celdaRandom = getCeldaRandom(_celdasPos2);
			if (celdaRandom == null) {
				_equipo2.remove(lucha.getID());
				continue;
			}
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, lucha.getID() + "", lucha.getID() + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, lucha.getID() + "", lucha.getID() + "," + 3 + ",0");
			lucha.setCeldaPelea(celdaRandom);
			lucha.getCeldaPelea().addLuchador(lucha);
			lucha.setEquipoBin(1);
		}
		for (Personaje persos : grupo1) {
			Mapa mapaTemp = persos.getMapa();
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, persos.getID());
		}
		for (Personaje persos : grupo2) {
			Mapa mapaTemp = persos.getMapa();
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(mapaTemp, persos.getID());
		}
		int grupoID1 = _equipo1.get(0).getID();
		int grupoID2 = _equipo2.get(0).getID();
		for (Luchador luchador : _equipo1.values()) {
			GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapa, grupoID1, luchador);
		}
		for (Luchador luchador : _equipo2.values()) {
			GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapa, grupoID2, luchador);
		}
		GestorSalida.ENVIAR_GM_LUCHADOR_A_TODA_PELEA(this, 7, _mapaCopia);
		setEstado(2);
	}
	
	public Mapa getMapaCopia() {
		return _mapaCopia;
	}
	
	public List<Trampa> getTrampas() {
		return _trampas;
	}
	
	public List<Glifo> getGlifos() {
		return _glifos;
	}
	
	private Celda getCeldaRandom(List<Celda> celdas) {
		Random rand = new Random();
		Celda celda;
		CopyOnWriteArrayList<Celda> celdas2 = new CopyOnWriteArrayList<Celda>();
		celdas2.addAll(celdas);
		if (celdas2.isEmpty())
			return null;
		int limit = 0;
		if (celdas2.size() == 1) {
			celda = celdas2.get(0);
			if (celda == null || !celda.getLuchadores().isEmpty())
				return null;
			return celda;
		}
		do {
			int size = celdas2.size();
			if (size == 1)
				celda = celdas2.get(0);
			else {
				int id = rand.nextInt(size - 1);
				celda = celdas2.get(id);
				if (!celda.getLuchadores().isEmpty()) {
					celdas2.remove(id);
				}
			}
			limit++;
		} while ( (celda == null || !celda.getLuchadores().isEmpty()) && limit < 80);
		if (limit == 80) {
			return null;
		}
		return celda;
	}
	
	private ArrayList<Celda> analizarPosiciones(int num) {
		return Encriptador.analizarInicioCelda(_mapaCopia, num);
	}
	
	public int getID() {
		return _id;
	}
	
	public ArrayList<Luchador> luchadoresDeEquipo(int equipos) {
		ArrayList<Luchador> luchadores = new ArrayList<Luchador>();
		// 5 = espectador y equipo 1
		// 6 = espectador y equipo 2
		// 7 = todos
		if (equipos - 4 >= 0) {// 4 = espectador
			for (Entry<Integer, Personaje> entry : _espectadores.entrySet()) {
				luchadores.add(new Luchador(this, entry.getValue()));
			}
			equipos -= 4;
		}
		if (equipos - 2 >= 0) {// 3 = ambos equipos, 2 = equipo 2 (atacante, mob, recaudador)
			for (Entry<Integer, Luchador> entry : _equipo2.entrySet()) {
				luchadores.add(entry.getValue());
			}
			equipos -= 2;
		}
		if (equipos - 1 >= 0) {// 1 = equipo 1
			for (Entry<Integer, Luchador> entry : _equipo1.entrySet()) {
				luchadores.add(entry.getValue());
			}
		}
		return luchadores;
	}
	
	public synchronized void cambiarLugar(Personaje perso, int celda) {
		Luchador luchador = getLuchadorPorPJ(perso);
		int equipo = getParamEquipo(perso.getID()) - 1;
		if (luchador == null)
			return;
		if (getEstado() != 2 || celdaOcupada(celda) || perso.estaListo()
				|| (equipo == 0 && !grupoCeldasContiene(_celdasPos1, celda))
				|| (equipo == 1 && !grupoCeldasContiene(_celdasPos2, celda)))
			return;
		if (_mapaCopia.getCelda(celda) == null)
			return;
		luchador.getCeldaPelea().getLuchadores().clear();
		luchador.setCeldaPelea(_mapaCopia.getCelda(celda));
		_mapaCopia.getCelda(celda).addLuchador(luchador);
		GestorSalida.ENVIAR_GIC_CAMBIAR_POS_PELEA(this, 3, _mapaCopia, perso.getID(), celda);
	}
	
	public boolean celdaOcupada(int celda) {
		Celda celd = _mapaCopia.getCelda(celda);
		if (celd == null)
			return true;
		return celd.getLuchadores().size() > 0;
	}
	
	private boolean grupoCeldasContiene(ArrayList<Celda> celdas, int celda) {
		for (int a = 0; a < celdas.size(); a++) {
			if (celdas.get(a).getID() == celda)
				return true;
		}
		return false;
	}
	
	public void verificaTodosListos() {
		boolean val = true;
		for (int a = 0; a < _equipo1.size(); a++) {
			if (!_equipo1.get(_equipo1.keySet().toArray()[a]).getPersonaje().estaListo())
				val = false;
		}
		if (_tipo != 4 && _tipo != 5 && _tipo != 2 && _tipo != 3) {
			for (int a = 0; a < _equipo2.size(); a++) {
				if (!_equipo2.get(_equipo2.keySet().toArray()[a]).getPersonaje().estaListo())
					val = false;
			}
		}
		if (_tipo == 5 || _tipo == 2)
			val = false;
		if (val) {
			iniciarPelea();
		}
	}
	
	private void iniciarPelea() {
		if (_estadoPelea >= 3)
			return;
		_estadoPelea = 3;
		_tiempoInicio = System.currentTimeMillis();
		_tiempoInicioTurno = 0;
		if (_tipo == 5) {
			_Recaudador.setEstadoPelea(2);
			for (Personaje z : MundoDofus.getGremio(_gremioID).getPjMiembros()) {
				if (z == null)
					continue;
				if (z.enLinea()) {
					GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z, Recaudador.analizarRecaudadores(z.getGremio().getID()));
					Recaudador.analizarAtaque(z, _gremioID);
					Recaudador.analizarDefensa(z, _gremioID);
				}
			}
		} else if (_tipo == 2) {
			_Prisma.setEstadoPelea(-2);
			for (Personaje z : MundoDofus.getPJsEnLinea()) {
				if (z == null)
					continue;
				if (z.getAlineacion() == _Prisma.getAlineacion()) {
					Prisma.analizarAtaque(z);
					Prisma.analizarDefensa(z);
				}
			}
		} else if (_tipo == 1) {
			if (_equipo1.size() == 1) {
				Personaje init1 = _luchInit1.getPersonaje();
				if (init1 != null && init1.getMisionPVP() != null) {
					String victima = "";
					try {
						victima = init1.getMisionPVP().getPjMision().getNombre();
					} catch (NullPointerException e) {
						victima = "";
					}
					for (Luchador luchador : _equipo2.values()) {
						if (luchador.getPersonaje().getNombre().equalsIgnoreCase(victima))
							_misionPVP = 1;
					}
				}
			}
			if (_equipo2.size() == 1) {
				Personaje init2 = _luchInit2.getPersonaje();
				if (init2 != null && init2.getMisionPVP() != null) {
					String victima = "";
					try {
						victima = init2.getMisionPVP().getPjMision().getNombre();
					} catch (NullPointerException e) {
						victima = "";
					}
					for (Luchador luchador : _equipo1.values()) {
						if (luchador.getPersonaje().getNombre().equalsIgnoreCase(victima))
							_misionPVP = 2;
					}
				}
			}
		} else if (_tipo == 4) {
			int alineacion = -1;
			if (_equipo2.size() > 0) {
				_equipo2.get(_equipo2.keySet().toArray()[0]).getMob().getModelo().getAlineacion();
			}
			if (!_mobGrupo.esFixeado())
				_mapaReal.spawnGrupo(alineacion, 1, true, _mobGrupo.getCeldaID());
		}
		GestorSalida.ENVIAR_Gc_BORRAR_BANDERA_EN_MAPA(_mapaReal, _idLuchInit1);
		GestorSalida.ENVIAR_GIC_UBICACION_LUCHADORES_INICIAR(this, 7);
		GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE_EQUIPOS(this, 7);
		iniciarOrdenLuchadores();
		_nroOrdenLuc = -1;
		GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(this, 7);
		GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES(this, 7);
		if (_tipo == 4 || _tipo == 3) {
			ArrayList<Integer> retosPosibles = new ArrayList<Integer>();
			for (int i = 1; i < 51; i++) {
				if (i == 13 || i == 16 || i == 19 || i == 26 || i == 27 || i == 38 || i == 48 || i == 49)
					continue;
				if (CentroInfo.esRetoPosible1(i, this))
					retosPosibles.add(i);
			}
			int idReto = retosPosibles.get(Formulas.getRandomValor(0, retosPosibles.size() - 1));
			_retos.put(idReto, 0);
			GestorSalida.ENVIAR_Gd_RETO_A_LOS_LUCHADORES(this, MundoDofus.getReto(idReto).getDetalleReto(this));
			if (_mapaReal.esArena() || _mapaReal.esMazmorra()) {
				idReto = retosPosibles.get(Formulas.getRandomValor(0, retosPosibles.size() - 1));
				boolean repetir = true;
				while (repetir) {
					repetir = false;
					idReto = retosPosibles.get(Formulas.getRandomValor(0, retosPosibles.size() - 1));
					for (Integer nro : _retos.keySet()) {
						if (CentroInfo.esRetoPosible2(nro, idReto) && !repetir)
							repetir = false;
						else
							repetir = true;
					}
				}
				_retos.put(idReto, 0);
				GestorSalida.ENVIAR_Gd_RETO_A_LOS_LUCHADORES(this, MundoDofus.getReto(idReto).getDetalleReto(this));
			}
		}
		for (Luchador luchador : luchadoresDeEquipo(3)) {
			_posinicial.put(luchador.getID(), luchador.getCeldaPelea());
			Personaje perso = luchador.getPersonaje();
			if (perso == null)
				continue;
			if (perso.estaMontando())
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, perso.getID() + "", perso.getID() + ","
						+ CentroInfo.ESTADO_CABALGANDO + ",1");
			if (perso.getPDV() != luchador.getPDVConBuff()) {
				luchador.setPDV(perso.getPDV());
				luchador.setPDVMAX(perso.getPDVMAX());
			}
			luchador._totalStats = perso.getTotalStats();
		}
		_cantLucEquipo1 = luchadoresDeEquipo(1).size();
		_cantLucEquipo2 = luchadoresDeEquipo(2).size();
		_inicioLucEquipo1.addAll(luchadoresDeEquipo(1));
		_inicioLucEquipo2.addAll(luchadoresDeEquipo(2));
		try {
			Thread.sleep(200);
		} catch (Exception e) {}
		if (_tipo == 4 || _tipo == 3) {
			Map<Integer, Integer> copiaRetos = new TreeMap<Integer, Integer>();
			copiaRetos.putAll(_retos);
			for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
				int reto = entry.getKey();
				int exitoReto = entry.getValue();
				if (exitoReto != 0)
					continue;
				int idLuch = 0;
				int size2 = _equipo2.size();
				int nivel = 10000;
				Map<Integer, Integer> ordenNivelMobs = new TreeMap<Integer, Integer>();
				Map<Integer, Luchador> ordenLuchMobs = new TreeMap<Integer, Luchador>();
				switch (reto) {
					case 30:// los pequeños antes
						for (Luchador luch : _equipo1.values()) {
							if (luch.getNivel() < nivel) {
								_luchMenorNivelReto = luch.getID();
								nivel = luch.getNivel();
							}
						}
						break;
					case 10:// cruel
						while (ordenNivelMobs.size() < size2) {
							nivel = 10000;
							for (Luchador luch : _equipo2.values()) {
								if (luch.getNivel() < nivel && !ordenNivelMobs.containsKey(luch.getID())) {
									idLuch = luch.getID();
									nivel = luch.getNivel();
								}
							}
							ordenNivelMobs.put(idLuch, nivel);
						}
						_ordenNivelMobs.putAll(ordenNivelMobs);
						break;
					case 17: // intocable
						for (Luchador luch : _equipo1.values()) {
							luch._intocable = true;
						}
						break;
					case 25:// ordenado
						while (ordenNivelMobs.size() < size2) {
							nivel = 0;
							for (Luchador luch : _equipo2.values()) {
								if (luch.getNivel() > nivel && !ordenNivelMobs.containsKey(luch.getID())) {
									idLuch = luch.getID();
									nivel = luch.getNivel();
								}
							}
							ordenNivelMobs.put(idLuch, nivel);
						}
						_ordenNivelMobs.putAll(ordenNivelMobs);
						break;
					case 35: // asesino a sueldo
						ArrayList<Luchador> temporal = new ArrayList<Pelea.Luchador>();
						temporal.addAll(_equipo2.values());
						while (ordenLuchMobs.size() < size2) {
							Luchador l = temporal.get(Formulas.getRandomValor(0, temporal.size() - 1));
							temporal.remove(l);
							ordenLuchMobs.put(l.getID(), l);
						}
						_ordenLuchMobs.putAll(ordenLuchMobs);
						for (Entry<Integer, Luchador> e : _ordenLuchMobs.entrySet()) {
							GestorSalida.ENVIAR_Gf_MOSTRAR_CASILLA_EN_PELEA(this, 5, e.getKey(), e.getValue().getCeldaPelea()
									.getID());
							break;
						}
						break;
					case 47:// contaminacion
						for (Luchador luch : _equipo1.values()) {
							luch._contaminacion = true;
						}
						break;
				}
			}
		}
		inicioTurno();
	}
	
	private void inicioTurno() {
		if (!verificaSiQuedaUno()) {
			verificaSiAcaboPelea();
			return;
		}
		if (_estadoPelea >= 4)
			return;
		_nroOrdenLuc++;
		_tempAccion = "";
		_cantMobsMuerto = 0;
		if (_nroOrdenLuc >= _ordenJugadores.size())
			_nroOrdenLuc = 0;
		if (_ordenJugadores.get(_nroOrdenLuc) == null)
			return;
		Luchador luchador = _ordenJugadores.get(_nroOrdenLuc);
		if (luchador._estaMuerto) {
			finTurno();
			return;
		}
		GestorSalida.ENVIAR_GTS_INICIO_TURNO_PELEA(this, 7, luchador.getID(), 31000);
		luchador.aplicarBuffInicioTurno(this);
		if (luchador._estaMuerto) {
			finTurno();
			return;
		}
		ArrayList<Glifo> glifos = new ArrayList<Glifo>();
		glifos.addAll(_glifos);
		for (Glifo g : glifos) {
			if (_estadoPelea >= 4)
				return;
			if (g.getLanzador().getID() == luchador.getID()) {
				if (g.disminuirDuracion() == 0) {
					_glifos.remove(g);
					g.desaparecer();
					continue;
				}
			}
			int dist = Camino.distanciaEntreDosCeldas(_mapaCopia, luchador.getCeldaPelea().getID(), g.getCelda().getID());
			if (dist <= g.getTamaño() && g._hechizos != 476) {
				g.activarGlifo(luchador);
			}
		}
		if (luchador._estaMuerto) {
			finTurno();
			return;
		}
		luchador._bonusCastigo.clear();
		_tempLuchadorPA = luchador.getPAConBuff();
		_tempLuchadorPM = luchador.getPMConBuff();
		_tempLuchadorPAusados = 0;
		_tempLuchadorPMusados = 0;
		luchador.actualizaHechizoLanzado();
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {}
		if (luchador.getPersonaje() != null) {
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(luchador.getPersonaje());
		}
		if (luchador.tieneBuff(CentroInfo.EFECTO_PASAR_TURNO)) {
			finTurno();
			return;
		}
		if (luchador.getPDVConBuff() <= 0) {
			agregarAMuertos(luchador);
			return;
		}
		if (luchador._desconectado) {
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "1182;" + luchador.getNombreLuchador() + "~"
					+ luchador._turnosRestantes);
			luchador._turnosRestantes--;
			if (luchador._turnosRestantes == 0)
				agregarAMuertos(luchador);
			else
				finTurno();
			return;
		}
		// if (luchador.getTipo() == 2) {
		// int x = 0;
		// int y = 0;
		// int equipo_enemigo = luchador.getParamEquipoAliado() == 1 ? 2 : 1;
		// for (Luchador enemigo : luchadoresDeEquipo(equipo_enemigo)) {
		// if (enemigo._estaRetirado || enemigo._estaMuerto || enemigo.esDoble() || enemigo.esInvocacion())
		// continue;
		// y++;
		// if (enemigo.getValorBuffPelea(149) == 8005)
		// x++;
		// }
		// if (x == y) {
		// finTurno();
		// return;
		// }
		// }
		_tiempoInicioTurno = System.currentTimeMillis();
		luchador.setPuedeJugar(true);
		if ( (_tipo == 4 || _tipo == 3) && luchador.getPersonaje() != null) {
			Map<Integer, Integer> copiaRetos = new TreeMap<Integer, Integer>();
			copiaRetos.putAll(_retos);
			for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
				int reto = entry.getKey();
				int exitoReto = entry.getValue();
				if (exitoReto != 0)
					continue;
				switch (reto) {
					case 2:// estatua
						luchador._idCeldaIniTurnoReto = luchador._celda.getID();
						break;
					case 6:// versatil
						luchador._hechiLanzadosReto.clear();
						break;
					case 34:// imprevisible
						ArrayList<Luchador> mobsVivos = new ArrayList<Luchador>();
						for (Luchador luch : _inicioLucEquipo2) {
							if (luch.estaMuerto())
								continue;
							mobsVivos.add(luch);
						}
						Luchador x = mobsVivos.get(Formulas.getRandomValor(0, mobsVivos.size() - 1));
						_idMobReto = x.getID();
						GestorSalida.ENVIAR_Gf_MOSTRAR_CASILLA_EN_PELEA(this, 5, _idMobReto, x.getCeldaPelea().getID());
						break;
					case 47:// contaminacion
						if (luchador._contaminado) {
							luchador._turnosParaMorir++;
							if (luchador._turnosParaMorir > 3) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
								exitoReto = 2;
							}
						}
						break;
				}
				if (exitoReto != 0) {
					_retos.remove(reto);
					_retos.put(reto, exitoReto);
				}
			}
		}
		if (luchador.getPersonaje() == null || luchador._doble != null || luchador._recaudador != null
				|| luchador._prisma != null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {}
			new IntelArtificial.IAThread(luchador, this);
		}
	}
	
	public void finTurno() {
		while (_tempAccion != "") {
			// GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 2, luchador.getID());
			try {
				Thread.sleep(500);
			} catch (Exception e) {}
		}
		if (_estadoPelea >= 4 || _nroOrdenLuc < 0)
			return;
		_tiempoInicioTurno = 0;
		if (_nroOrdenLuc >= _ordenJugadores.size())
			_nroOrdenLuc = 0;
		Luchador luchador = _ordenJugadores.get(_nroOrdenLuc);
		try {
			luchador.setPuedeJugar(false);
			GestorSalida.ENVIAR_GTF_FIN_DE_TURNO(this, 7, luchador.getID());
			try {
				Thread.sleep(250);
			} catch (Exception e) {}
			if (!luchador._estaRetirado) {
				_tempAccion = "";
				if (!luchador._estaMuerto) {
					for (EfectoHechizo EH : luchador.getBuffsPorEfectoID(131)) {
						int cadaCuantosPA = EH.getValor();
						int val = -1;
						try {
							val = Integer.parseInt(EH.getArgs().split(";")[1]);
						} catch (Exception e) {}
						if (val == -1)
							continue;
						int nroPAusados = (int) Math.floor((double) _tempLuchadorPAusados / (double) cadaCuantosPA);
						int dañoFinTurno = val * nroPAusados;
						Stats totalLanz = EH.getLanzador().getTotalStatsConBuff();
						int inte = 0;
						if (EH.getHechizoID() == 200) {// Si veneno paralizante
							inte = totalLanz.getEfecto(126);
							if (inte < 0)
								inte = 0;
						}
						int pDaños = totalLanz.getEfecto(CentroInfo.STATS_ADD_PORC_DAÑOS);
						if (pDaños < 0)
							pDaños = 0;
						int daños = totalLanz.getEfecto(CentroInfo.STATS_ADD_DAÑOS);
						dañoFinTurno = (int) ( ( (100 + inte + pDaños) / 100) * dañoFinTurno) + daños;
						if (luchador.tieneBuff(105)) {
							GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 105, luchador.getID() + "", luchador.getID() + ","
									+ luchador.getBuff(105).getValor());
							dañoFinTurno = dañoFinTurno - luchador.getBuff(105).getValor();// Immunidad
						}
						if (dañoFinTurno <= 0)
							continue;
						if (dañoFinTurno > luchador.getPDVConBuff())
							dañoFinTurno = luchador.getPDVConBuff();
						luchador.restarPDV(dañoFinTurno);
						dañoFinTurno = - (dañoFinTurno);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 100, EH.getLanzador().getID() + "", luchador.getID() + ","
								+ dañoFinTurno);
					}
					ArrayList<Glifo> glifos = new ArrayList<Glifo>();
					glifos.addAll(_glifos);
					for (Glifo glifo : glifos) {
						if (_estadoPelea >= 4)
							return;
						int dist = Camino.distanciaEntreDosCeldas(_mapaCopia, luchador.getCeldaPelea().getID(), glifo.getCelda()
								.getID());
						if (dist <= glifo.getTamaño() && glifo._hechizos == 476) {
							glifo.activarGlifo(luchador);
						}
					}
					if (luchador.getPDVConBuff() <= 0)
						agregarAMuertos(luchador);
					// retos
					Personaje perso = luchador.getPersonaje();
					if ( (_tipo == 4 || _tipo == 3) && perso != null) {
						Map<Integer, Integer> copiaRetos = new TreeMap<Integer, Integer>();
						copiaRetos.putAll(_retos);
						for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
							int reto = entry.getKey();
							int exitoReto = entry.getValue();
							if (exitoReto != 0)
								continue;
							switch (reto) {
								case 1:// zombi
									if (_tempLuchadorPMusados == 0) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 2:// estatua
									if (luchador._idCeldaIniTurnoReto != luchador._celda.getID()) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 7: // jardinero
									if (hechizoDisponible(luchador, 367)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 8:// nomada
									if (_tempLuchadorPM > 0) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 12: // sepultero
									if (hechizoDisponible(luchador, 373)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 14: // casino real
									if (hechizoDisponible(luchador, 101)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 15: // jardinero
									if (hechizoDisponible(luchador, 370)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 36:// audaz
									if (!Camino.hayAlrededor(_mapaCopia, this, luchador, false)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 37:// Pegajoso
									if (!Camino.hayAlrededor(_mapaCopia, this, luchador, true)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 39: // anacorea
									if (Camino.hayAlrededor(_mapaCopia, this, luchador, true)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 40: // pusilanime
									if (Camino.hayAlrededor(_mapaCopia, this, luchador, false)) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
								case 41: // impetuoso
									if (_tempLuchadorPA > 0) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
									break;
							}
							if (exitoReto != 0) {
								_retos.remove(reto);
								_retos.put(reto, exitoReto);
							}
						}
					}
					// reset de valores
					_tempLuchadorPAusados = 0;
					_tempLuchadorPMusados = 0;
					Stats statsConBuff = luchador.getTotalStatsConBuff();
					_tempLuchadorPA = statsConBuff.getEfecto(CentroInfo.STATS_ADD_PA);
					_tempLuchadorPM = statsConBuff.getEfecto(CentroInfo.STATS_ADD_PM);
					luchador.actualizarBuffsPelea();
					if (perso != null)
						if (perso.enLinea())
							GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
					GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES(this, 7);
					GestorSalida.ENVIAR_GTR_TURNO_LISTO(this, 7, _nroOrdenLuc == _ordenJugadores.size() ? _ordenJugadores.get(0)
							.getID() : luchador.getID());
				}
			}
			inicioTurno();
		} catch (NullPointerException e) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {}
			finTurno();
		}
	}
	
	public boolean hechizoDisponible(Luchador luchador, int idHechizo) {
		boolean ver = false;
		if (luchador.getPersonaje().tieneHechizoID(idHechizo)) {
			ver = true;
			for (HechizoLanzado HL : luchador.getHechizosLanzados()) {
				if (HL._hechizoId == idHechizo && HL.getSigLanzamiento() > 0) {
					ver = false;
				}
			}
		}
		return ver;
	}
	
	private void iniciarOrdenLuchadores() {
		int j = 0;
		int k = 0;
		int empieza0 = 0;
		int empieza1 = 0;
		int curMaxIni0 = 0;
		int curMaxIni1 = 0;
		Luchador curMax0 = null;
		Luchador curMax1 = null;
		boolean team1_ready = false;
		boolean team2_ready = false;
		do {
			if (!team1_ready) {
				team1_ready = true;
				Map<Integer, Luchador> _Team0 = _equipo1;
				for (Entry<Integer, Luchador> entry : _Team0.entrySet()) {
					if (_ordenJugadores.contains(entry.getValue()))
						continue;
					team1_ready = false;
					if (entry.getValue().getIniciativa() >= curMaxIni0) {
						curMaxIni0 = entry.getValue().getIniciativa();
						curMax0 = entry.getValue();
					}
					if (curMaxIni0 > empieza0) {
						empieza0 = curMaxIni0;
					}
				}
			}
			if (!team2_ready) {
				team2_ready = true;
				for (Entry<Integer, Luchador> entry : _equipo2.entrySet()) {
					if (_ordenJugadores.contains(entry.getValue()))
						continue;
					team2_ready = false;
					if (entry.getValue().getIniciativa() >= curMaxIni1) {
						curMaxIni1 = entry.getValue().getIniciativa();
						curMax1 = entry.getValue();
					}
					if (curMaxIni1 > empieza1) {
						empieza1 = curMaxIni1;
					}
				}
			}
			if ( (curMax1 == null) && (curMax0 == null))
				return;
			if (empieza0 > empieza1) {
				if (luchadoresDeEquipo(1).size() > j) {
					_ordenJugadores.add(curMax0);
					j++;
				}
				if (luchadoresDeEquipo(2).size() > k) {
					_ordenJugadores.add(curMax1);
					k++;
				}
			} else {
				if (luchadoresDeEquipo(2).size() > j) {
					_ordenJugadores.add(curMax1);
					j++;
				}
				if (luchadoresDeEquipo(1).size() > k) {
					_ordenJugadores.add(curMax0);
					k++;
				}
			}
			curMaxIni0 = 0;
			curMaxIni1 = 0;
			curMax0 = null;
			curMax1 = null;
		} while (_ordenJugadores.size() != luchadoresDeEquipo(3).size());
	}
	
	public void unirsePelea(Personaje perso, int idOtroPerso) {
		PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
		if (_estadoPelea > 2) {
			GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'l');
			return;
		}
		Luchador jugadorUnirse = null;
		int tiempoRestante = (int) (45000 - (System.currentTimeMillis() - _tiempoInicioTurno));
		if (_equipo1.containsKey(idOtroPerso)) {
			Celda celda = getCeldaRandom(_celdasPos1);
			if (celda == null)
				return;
			if (_equipo1.size() >= 8) {
				GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 't');
				return;
			}
			if (_soloGrupo1) {
				Grupo g = _luchInit1.getPersonaje().getGrupo();
				if (g != null) {
					if (!g.getPersos().contains(perso)) {
						GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'f');
						return;
					}
				}
			}
			if (_tipo == 1) {
				if (perso.getAlineacion() == -1) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				} else if (_luchInit1.getPersonaje().getAlineacion() != perso.getAlineacion()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				}
				perso.botonActDesacAlas('+');
			}
			if (_tipo == 2) {
				if (perso.getAlineacion() == -1) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				} else if (_luchInit1.getPersonaje().getAlineacion() != perso.getAlineacion()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				}
				perso.botonActDesacAlas('+');
			}
			if (_gremioID > -1 && perso.getGremio() != null) {
				if (getGremioID() == perso.getGremio().getID()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'g');
					return;
				}
			}
			if (_cerrado1) {
				GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'f');
				return;
			}
			if (_tipo == 0) {
				GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, 2, true, true, false, 0, _tipo);
			} else {
				GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, 2, false, true, false, tiempoRestante, _tipo);
			}
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(out, _mapaCopia.getLugaresString(), _celdaColor1);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, perso.getID() + "", perso.getID() + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, perso.getID() + "", perso.getID() + "," + 3 + ",0");
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
			Luchador luchador = new Luchador(this, perso);
			luchador.setEquipoBin(0);
			_equipo1.put(perso.getID(), luchador);
			perso.setPelea(this);
			luchador.setCeldaPelea(celda);
			luchador.getCeldaPelea().addLuchador(luchador);
			jugadorUnirse = luchador;
		} else if (_equipo2.containsKey(idOtroPerso)) {
			Celda celda = getCeldaRandom(_celdasPos2);
			if (celda == null)
				return;
			if (_equipo2.size() >= 8) {
				GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 't');
				return;
			}
			if (_soloGrupo2) {
				Grupo g = _luchInit2.getPersonaje().getGrupo();
				if (g != null) {
					if (!g.getPersos().contains(perso)) {
						GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'f');
						return;
					}
				}
			}
			if (_tipo == 1) {
				if (perso.getAlineacion() == -1) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				}
				if (_luchInit2.getPersonaje().getAlineacion() != perso.getAlineacion()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				}
				perso.botonActDesacAlas('+');
			}
			if (_tipo == 2) {
				if (perso.getAlineacion() == -1) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				}
				if (_luchInit2.getPrisma().getAlineacion() != perso.getAlineacion()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'a');
					return;
				}
				perso.botonActDesacAlas('+');
			}
			if (_gremioID > -1 && perso.getGremio() != null) {
				if (getGremioID() == perso.getGremio().getID()) {
					GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'g');
					return;
				}
			}
			if (_cerrado2) {
				GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'f');
				return;
			}
			if (_tipo == 0) {
				GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, 2, true, true, false, 0, _tipo);
			} else {
				GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, 2, false, true, false, tiempoRestante, _tipo);
			}
			GestorSalida.ENVIAR_GP_POSICIONES_PELEA(out, _mapaCopia.getLugaresString(), _celdaColor2);
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, perso.getID() + "", perso.getID() + "," + 8 + ",0");
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, perso.getID() + "", perso.getID() + "," + 3 + ",0");
			GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
			Luchador luchador = new Luchador(this, perso);
			luchador.setEquipoBin(1);
			_equipo2.put(perso.getID(), luchador);
			perso.setPelea(this);
			luchador.setCeldaPelea(celda);
			luchador.getCeldaPelea().addLuchador(luchador);
			jugadorUnirse = luchador;
		}
		GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0);
		perso.getCelda().removerPersonaje(perso.getID());
		GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso.getMapa(), (jugadorUnirse.getEquipoBin() == 0 ? _luchInit1
				: _luchInit2).getID(), jugadorUnirse);
		GestorSalida.ENVIAR_GM_JUGADO_UNIRSE_PELEA(this, 7, jugadorUnirse);
		GestorSalida.ENVIAR_GM_LUCHADORES(this, _mapaCopia, perso);
		if (_Recaudador != null) {
			for (Personaje z : MundoDofus.getGremio(_gremioID).getPjMiembros()) {
				if (z.enLinea()) {
					Recaudador.analizarAtaque(z, _gremioID);
					Recaudador.analizarDefensa(z, _gremioID);
				}
			}
		}
		if (_Prisma != null) {
			for (Personaje z : MundoDofus.getPJsEnLinea()) {
				if (z == null || z.getAlineacion() != _Prisma.getAlineacion())
					continue;
				Prisma.analizarAtaque(perso);
			}
		}
	}
	
	public boolean unirsePeleaRecaudador(Personaje perso, int recauID, short mapaID, int celdaID) {
		PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
		if (_tiempoInicio != 0L) {
			GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'l');
			return false;
		}
		Luchador jugadorAUnirse = null;
		Celda celda = getCeldaRandom(_celdasPos2);
		if (celda == null)
			return false;
		if (perso.getMapa().getID() != mapaID) {
			perso.setMapaDefPerco(perso.getMapa());
			perso.setCeldaDefPerco(perso.getCelda());
			try {
				if (!perso.teleportSinTodos(mapaID, celdaID))
					return false;
				Thread.sleep(500);
			} catch (Exception e) {
				return false;
			}
		}
		int tiempoRecaudador = 0;
		if (_Recaudador != null)
			tiempoRecaudador = _Recaudador.getTiempoTurno();
		int idPerso = perso.getID();
		try {
			Thread.sleep(500);
		} catch (Exception e) {}
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, 2, false, true, false, tiempoRecaudador, _tipo);
		GestorSalida.ENVIAR_GP_POSICIONES_PELEA(out, _mapaCopia.getLugaresString(), _celdaColor2);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, idPerso + "", idPerso + "," + 8 + ",0");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, idPerso + "", idPerso + "," + 3 + ",0");
		Luchador luchador = new Luchador(this, perso);
		jugadorAUnirse = luchador;
		luchador.setEquipoBin(1);
		_equipo2.put(idPerso, luchador);
		perso.setPelea(this);
		luchador.setCeldaPelea(celda);
		celda.addLuchador(luchador);
		GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0);
		GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso.getMapa(), recauID, jugadorAUnirse);
		GestorSalida.ENVIAR_GM_JUGADO_UNIRSE_PELEA(this, 7, jugadorAUnirse);
		try {
			Thread.sleep(300);
		} catch (Exception e) {}
		GestorSalida.ENVIAR_GM_LUCHADORES(this, _mapaCopia, perso);
		try {
			Thread.sleep(300);
		} catch (Exception e) {}
		return true;
	}
	
	public boolean unirsePeleaPrisma(Personaje perso, int prismaID, short mapaID, int celdaID) {
		PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
		if (_tiempoInicio != 0L) {
			GestorSalida.ENVIAR_GA903_ERROR_PELEA(out, 'l');
			return false;
		}
		Luchador jugadorAUnirse = null;
		Celda celda = getCeldaRandom(_celdasPos2);
		if (celda == null)
			return false;
		if (perso.getMapa().getID() != mapaID) {
			perso.setMapaDefPerco(perso.getMapa());
			perso.setCeldaDefPerco(perso.getCelda());
			try {
				if (!perso.teleportSinTodos(mapaID, celdaID))
					return false;
				Thread.sleep(500);
			} catch (Exception e) {
				return false;
			}
		}
		int tiempoPrisma = 0;
		if (_Prisma != null)
			tiempoPrisma = _Prisma.getTiempoTurno();
		int idPerso = perso.getID();
		try {
			Thread.sleep(500);
		} catch (Exception e) {}
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, 2, false, true, false, tiempoPrisma, _tipo);
		GestorSalida.ENVIAR_GP_POSICIONES_PELEA(out, _mapaCopia.getLugaresString(), _celdaColor2);
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, idPerso + "", idPerso + "," + 8 + ",0");
		GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 3, 950, idPerso + "", idPerso + "," + 3 + ",0");
		Luchador luchador = new Luchador(this, perso);
		jugadorAUnirse = luchador;
		luchador.setEquipoBin(1);
		_equipo2.put(idPerso, luchador);
		perso.setPelea(this);
		luchador.setCeldaPelea(celda);
		celda.addLuchador(luchador);
		GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0);
		GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso.getMapa(), prismaID, jugadorAUnirse);
		GestorSalida.ENVIAR_GM_JUGADO_UNIRSE_PELEA(this, 7, jugadorAUnirse);
		try {
			Thread.sleep(300);
		} catch (Exception e) {}
		GestorSalida.ENVIAR_GM_LUCHADORES(this, _mapaCopia, perso);
		try {
			Thread.sleep(300);
		} catch (Exception e) {}
		return true;
	}
	
	public void unirseEspectador(Personaje perso) {
		if (_tiempoInicio == 0L)
			return;
		if (perso != null && perso.esFantasma()) {
			GestorSalida.ENVIAR_Im_INFORMACION(perso, "1116");
			return;
		}
		if (!_espectadorOk || _estadoPelea != 3) {
			GestorSalida.ENVIAR_Im_INFORMACION(perso, "157");
			return;
		}
		int tiempoRestante = (int) (29000 - (System.currentTimeMillis() - _tiempoInicioTurno));
		perso.setPelea(this);
		perso.getCelda().removerPersonaje(perso.getID());
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, _estadoPelea, false, false, true, 0, _tipo);
		GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE(perso);
		GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(perso.getMapa(), perso.getID());
		GestorSalida.ENVIAR_GM_LUCHADORES(this, _mapaCopia, perso);
		GestorSalida.ENVIAR_GTS_INICIO_TURNO_PELEA(perso, _ordenJugadores.get(_nroOrdenLuc).getID(), tiempoRestante);
		GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(perso, this);
		try {
			Thread.sleep(200);
		} catch (Exception e) {}
		if (_tipo == 4 || _tipo == 3) {
			for (Entry<Integer, Integer> entry : _retos.entrySet()) {
				String str = _stringReto.get(entry.getKey());
				if (!str.isEmpty()) {
					GestorSalida.ENVIAR_Gd_RETO_A_PERSONAJE(perso, str);
					if (entry.getValue() == 1)
						GestorSalida.ENVIAR_GdaK_RETO_REALIZADO(perso, entry.getKey());
					else if (entry.getValue() == 2)
						GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(perso, entry.getKey());
				}
			}
		}
		_espectadores.put(perso.getID(), perso);
		GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "036;" + perso.getNombre());
	}
	
	public void desconectarLuchador(Personaje perso) {
		Luchador luchador = getLuchadorPorPJ(perso);
		if (luchador == null)
			return;
		luchador._desconectado = true;
		if (luchador.puedeJugar()) {
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "1182;" + luchador.getNombreLuchador() + "~"
					+ luchador._turnosRestantes);
			luchador._turnosRestantes--;
			finTurno();
		}
	}
	
	public void reconectarLuchador(Personaje perso) {
		int tiempoRestante = (int) (29000 - (System.currentTimeMillis() - _tiempoInicioTurno));
		perso.getCelda().removerPersonaje(perso.getID());
		GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, _estadoPelea, false, true, false, 0, _tipo);
		GestorSalida.ENVIAR_GM_LUCHADORES(this, _mapaCopia, perso);
		GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE(perso);
		GestorSalida.ENVIAR_GTS_INICIO_TURNO_PELEA(perso, _ordenJugadores.get(_nroOrdenLuc).getID(), tiempoRestante);
		GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(perso, this);
		GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES(this, perso);
		try {
			Thread.sleep(200);
		} catch (Exception e) {}
		if (_tipo == 4 || _tipo == 3) {
			for (Entry<Integer, Integer> entry : _retos.entrySet()) {
				String str = _stringReto.get(entry.getKey());
				if (!str.isEmpty()) {
					GestorSalida.ENVIAR_Gd_RETO_A_PERSONAJE(perso, str);
					if (entry.getValue() == 1)
						GestorSalida.ENVIAR_GdaK_RETO_REALIZADO(perso, entry.getKey());
					else if (entry.getValue() == 2)
						GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(perso, entry.getKey());
				}
			}
		}
		getLuchadorPorPJ(perso)._desconectado = false;
		GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "1184;" + perso.getNombre());
	}
	
	public void botonBloquearMasJug(int id) {
		if (_luchInit1 != null && _idLuchInit1 == id) {
			_cerrado1 = !_cerrado1;
			GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _cerrado1 ? '+' : '-', 'A', id);
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 1, _cerrado1 ? "095" : "096");
		} else if (_luchInit2 != null && _idLuchInit2 == id) {
			_cerrado2 = !_cerrado2;
			GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _cerrado2 ? '+' : '-', 'A', id);
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 2, _cerrado2 ? "095" : "096");
		}
	}
	
	public synchronized void botonSoloGrupo(int id) {
		if (_luchInit1 != null && _idLuchInit1 == id) {
			_soloGrupo1 = !_soloGrupo1;
			if (_soloGrupo1) {
				ArrayList<Integer> lista = new ArrayList<Integer>();
				ArrayList<Integer> expulsar = new ArrayList<Integer>();
				lista.addAll(_luchInit1.getPersonaje().getGrupo().getIDsPersos());
				for (Entry<Integer, Luchador> entry : _equipo1.entrySet()) {
					int expulsadoID = entry.getKey();
					Luchador luch = entry.getValue();
					if (!lista.contains(expulsadoID)) {
						expulsar.add(expulsadoID);
						GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, expulsadoID, 3);
						GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luch.getPersonaje());
						luch.getPersonaje().retornoMapa();
						luch._celda.removerLuchador(luch);
						GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(_mapaReal, _idLuchInit1, luch);
					}
				}
				for (Integer ID : expulsar) {
					_equipo1.remove(ID);
				}
			}
			GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _soloGrupo1 ? '+' : '-', 'P', id);
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 1, _soloGrupo1 ? "093" : "094");
		} else if (_luchInit2 != null && _idLuchInit2 == id) {
			_soloGrupo2 = !_soloGrupo2;
			if (_soloGrupo2) {
				ArrayList<Integer> lista = new ArrayList<Integer>();
				ArrayList<Integer> expulsar = new ArrayList<Integer>();
				lista.addAll(_luchInit2.getPersonaje().getGrupo().getIDsPersos());
				for (Entry<Integer, Luchador> entry : _equipo2.entrySet()) {
					int expulsadoID = entry.getKey();
					Luchador luch = entry.getValue();
					if (!lista.contains(expulsadoID)) {
						expulsar.add(expulsadoID);
						GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, expulsadoID, 3);
						GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luch.getPersonaje());
						luch.getPersonaje().retornoMapa();
						luch._celda.removerLuchador(luch);
						GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(_mapaReal, _idLuchInit2, luch);
					}
				}
				for (Integer ID : expulsar) {
					_equipo2.remove(ID);
				}
			}
			GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _soloGrupo2 ? '+' : '-', 'P', id);
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 2, _soloGrupo2 ? "095" : "096");
		}
	}
	
	public synchronized void botonBloquearEspect(int id) {
		if ( (_luchInit1 != null && _idLuchInit1 == id) || (_luchInit2 != null && _idLuchInit2 == id)) {
			_espectadorOk = !_espectadorOk;
			if (_idLuchInit1 == id)
				GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _espectadorOk ? '+' : '-', 'S', _idLuchInit1);
			else
				GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _espectadorOk ? '+' : '-', 'S', _idLuchInit2);
			GestorSalida.ENVIAR_Im_INFORMACION_A_MAPA(_mapaCopia, _espectadorOk ? "039" : "040");
		}
		if (_espectadores.size() > 0) {
			for (Personaje espec : _espectadores.values()) {
				GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(espec);
				espec.retornoMapa();
			}
			_espectadores.clear();
		}
	}
	
	public void botonAyuda(int id) {
		if (_luchInit1 != null && _idLuchInit1 == id) {
			_ayuda1 = !_ayuda1;
			GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _ayuda1 ? '+' : '-', 'H', id);
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 1, _ayuda1 ? "0103" : "0104");
		} else if (_luchInit2 != null && _idLuchInit2 == id) {
			_ayuda2 = !_ayuda2;
			GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(_mapaReal, _ayuda2 ? '+' : '-', 'H', id);
			GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 2, _ayuda2 ? "0103" : "0104");
		}
	}
	
	private void setEstado(int estado) {
		_estadoPelea = estado;
	}
	
	private void setGremioID(int gremioID) {
		_gremioID = gremioID;
	}
	
	public int getEstado() {
		return _estadoPelea;
	}
	
	public int getGremioID() {
		return _gremioID;
	}
	
	public int getTipoPelea() {
		return _tipo;
	}
	
	public List<Luchador> getOrdenJug() {
		return _ordenJugadores;
	}
	
	public boolean puedeMoverseLuchador(Luchador movedor, AccionDeJuego GA) {
		String path = GA._args;
		if (path.equals("") || _ordenJugadores.size() <= 0 || _nroOrdenLuc < 0)
			return false;
		if (_nroOrdenLuc >= _ordenJugadores.size())
			_nroOrdenLuc = 0;
		Luchador luchador = _ordenJugadores.get(_nroOrdenLuc);
		if (luchador == null)
			return false;
		if (!_tempAccion.equals("") || luchador.getID() != movedor.getID() || _estadoPelea != 3) {
			return false;
		}
		Personaje perso = movedor.getPersonaje();
		Luchador tacleador = Camino.getEnemigoAlrededor(movedor.getCeldaPelea().getID(), _mapaCopia, this);
		if (tacleador != null && !movedor.tieneEstado(6) && !movedor.tieneEstado(8)) {
			int porcEsquiva = Formulas.getPorcTacleo(movedor, tacleador);
			int rand = Formulas.getRandomValor(0, 99);
			if (rand > porcEsquiva) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA_CON_RESPUESTA(this, 7, GA._idUnica, "104", movedor.getID() + ";", "");
				int pierdePA = _tempLuchadorPA * porcEsquiva / 100;
				if (pierdePA < 0)
					pierdePA = -pierdePA;
				if (_tempLuchadorPM < 0)
					_tempLuchadorPM = 0;
				GestorSalida.ENVIAR_GA_ACCION_PELEA_CON_RESPUESTA(this, 7, GA._idUnica, "129", movedor.getID() + "",
						movedor.getID() + ",-" + _tempLuchadorPM);
				GestorSalida.ENVIAR_GA_ACCION_PELEA_CON_RESPUESTA(this, 7, GA._idUnica, "102", movedor.getID() + "",
						movedor.getID() + ",-" + pierdePA);
				_tempLuchadorPM = 0;
				_tempLuchadorPA -= pierdePA;
				return false;
			}
		}
		AtomicReference<String> pathRef = new AtomicReference<String>(path);
		int nroCeldasMov = Camino.caminoValido(_mapaCopia, movedor.getCeldaPelea().getID(), pathRef, this);
		String nuevoPath = pathRef.get();
		if (nroCeldasMov > _tempLuchadorPM || nroCeldasMov == -1000) {
			return false;
		}
		_tempLuchadorPM -= nroCeldasMov;
		_tempLuchadorPMusados += nroCeldasMov;
		// retos
		if ( (_tipo == 4 || _tipo == 3) && movedor.getPersonaje() != null) {
			Map<Integer, Integer> copiaRetos = new TreeMap<Integer, Integer>();
			copiaRetos.putAll(_retos);
			for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
				int reto = entry.getKey();
				int exitoReto = entry.getValue();
				if (exitoReto != 0)
					continue;
				switch (reto) {
					case 1:// zombi
						if (_tempLuchadorPMusados != 1) {
							GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
							exitoReto = 2;
						}
						break;
				}
				if (exitoReto != 0) {
					_retos.remove(reto);
					_retos.put(reto, exitoReto);
				}
			}
		}
		int sigCeldaID = Encriptador.celdaCodigoAID(nuevoPath.substring(nuevoPath.length() - 2));
		if (_mapaCopia.getCelda(sigCeldaID) == null)
			return false;
		if (perso != null)
			GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(this, 7, movedor.getID());
		if (!movedor.esInvisible())
			GestorSalida.ENVIAR_GA_ACCION_PELEA_CON_RESPUESTA(this, 7, GA._idUnica, "1", movedor.getID() + "",
					"a" + Encriptador.celdaIDACodigo(movedor.getCeldaPelea().getID()) + nuevoPath);
		else {
			if (perso != null) {
				PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
				if (out != null)
					GestorSalida.ENVIAR_GA_ACCION_DE_JUEGO(out, GA._idUnica + "", "1", movedor.getID() + "",
							"a" + Encriptador.celdaIDACodigo(movedor.getCeldaPelea().getID()) + nuevoPath);
			}
		}
		Luchador portador = movedor.getTransportadoPor();
		if (portador != null && movedor.tieneEstado(8) && portador.tieneEstado(3)) {
			if (sigCeldaID != portador.getCeldaPelea().getID()) {
				portador.getCeldaPelea().removerLuchador(movedor);
				portador.setEstado(3, 0);
				movedor.setEstado(8, 0);
				portador.setTransportado(null);
				movedor.setTransportadoPor(null);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 950, portador.getID() + "", portador.getID() + "," + 3 + ",0");
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 950, movedor.getID() + "", movedor.getID() + "," + 8 + ",0");
			} else
				movedor.getCeldaPelea().getLuchadores().clear();
		} else
			movedor.getCeldaPelea().getLuchadores().clear();
		movedor.setCeldaPelea(_mapaCopia.getCelda(sigCeldaID));
		movedor.getCeldaPelea().addLuchador(movedor);
		if (nroCeldasMov < 0) {
			nroCeldasMov = nroCeldasMov * (-1);
		}
		_tempAccion = "GA;129;" + movedor.getID() + ";" + movedor.getID() + ",-" + nroCeldasMov;
		Luchador tranportado = movedor.getTransportando();
		if (tranportado != null && movedor.tieneEstado(3) && tranportado.tieneEstado(8)) {
			tranportado.setCeldaPelea(movedor.getCeldaPelea());
			movedor.getCeldaPelea().addLuchador(tranportado);
		}
		if (perso == null) {
			GestorSalida.ENVIAR_GAMEACTION_A_PELEA(this, 7, _tempAccion);
			try {
				if (nroCeldasMov > 3)
					Thread.sleep(600 * nroCeldasMov);
				else
					Thread.sleep(1000 * nroCeldasMov);
			} catch (InterruptedException e) {}
			_tempAccion = "";
			ArrayList<Trampa> trampas = new ArrayList<Trampa>();
			trampas.addAll(_trampas);
			for (Trampa t : trampas) {
				int dist = Camino.distanciaEntreDosCeldas(_mapaCopia, t.getCelda().getID(), movedor.getCeldaPelea().getID());
				if (dist <= t.getTamaño())
					t.activaTrampa(movedor);
			}
			return true;
		}
		perso.getCuenta().getEntradaPersonaje().addGA(GA);
		return true;
	}
	
	public void finalizarMovimiento(Personaje perso) {
		if (_nroOrdenLuc < 0 || _ordenJugadores.size() == 0)
			return;
		if (_nroOrdenLuc >= _ordenJugadores.size())
			_nroOrdenLuc = 0;
		int idLuchador = _ordenJugadores.get(_nroOrdenLuc).getID();
		if (_tempAccion.equals("") || idLuchador != perso.getID() || _estadoPelea != 3)
			return;
		GestorSalida.ENVIAR_GAMEACTION_A_PELEA(this, 7, _tempAccion);
		GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 2, idLuchador);
		ArrayList<Trampa> trampas = new ArrayList<Trampa>();
		trampas.addAll(_trampas);
		for (Trampa trampa : trampas) {
			if (_estadoPelea == 4)
				break;
			Luchador F = getLuchadorPorPJ(perso);
			int dist = Camino.distanciaEntreDosCeldas(_mapaCopia, trampa.getCelda().getID(), F.getCeldaPelea().getID());
			if (dist <= trampa.getTamaño())
				trampa.activaTrampa(F);
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {}
		_tempAccion = "";
		finAccion();
	}
	
	public void pasarTurno(Personaje perso) {
		if (!_tempAccion.equals(""))
			return;
		Luchador luchador = getLuchadorPorPJ(perso);
		if (luchador == null || !luchador.puedeJugar()) {
			return;
		}
		finTurno();
	}
	
	public int intentarLanzarHechizo(Luchador lanzador, StatsHechizos sHechizo, int celdaID) { // lanzamientos de los hechizos
		Personaje perso = lanzador.getPersonaje();
		if (!_tempAccion.equals("") && perso != null)
			return 10;
		if (sHechizo == null)
			return 10;
		Celda celda = _mapaCopia.getCelda(celdaID);
		_tempAccion = "casting";
		if (puedeLanzarHechizo(lanzador, sHechizo, celda, -1)) {
			if (perso != null)
				GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
			if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
				int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 285);
				_tempLuchadorPA -= sHechizo.getCostePA() - modi;
				_tempLuchadorPAusados += sHechizo.getCostePA() - modi;
			} else {
				_tempLuchadorPA -= sHechizo.getCostePA();
				_tempLuchadorPAusados += sHechizo.getCostePA();
			}
			GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(this, 7, lanzador.getID());
			boolean esFC = sHechizo.getPorcFC() != 0 && Formulas.getRandomValor(1, sHechizo.getPorcFC()) == sHechizo.getPorcFC();
			if (esFC) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 302, lanzador.getID() + "", sHechizo.getHechizoID() + "");
			} else {
				if ( (_tipo == 4 || _tipo == 3) && lanzador.getPersonaje() != null) {
					Map<Integer, Integer> copiaRetos = new TreeMap<Integer, Integer>();
					copiaRetos.putAll(_retos);
					for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
						int reto = entry.getKey();
						int exitoReto = entry.getValue();
						if (exitoReto != 0)
							continue;
						switch (reto) {
							case 5: // Ahorrador
								if (lanzador._hechiLanzadosReto.contains(sHechizo.getHechizoID())) {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
									exitoReto = 2;
								} else
									lanzador._hechiLanzadosReto.add(sHechizo.getHechizoID());
								break;
							case 6: // versatil
								if (lanzador._hechiLanzadosReto.contains(sHechizo.getHechizoID())) {
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
									exitoReto = 2;
								} else
									lanzador._hechiLanzadosReto.add(sHechizo.getHechizoID());
								break;
							case 9: // barbaro
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
								exitoReto = 2;
								break;
							case 18: // incurable
								for (EfectoHechizo efecto : sHechizo.getEfectos()) {
									if (efecto.getEfectoID() == 108) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
								}
								break;
							case 20: // elemental
								for (EfectoHechizo efecto : sHechizo.getEfectos()) {
									int efectoID = efecto.getEfectoID();
									if (efectoID >= 85 && efectoID <= 100 && efectoID != 90) {
										if (_elementoReto == 0) {
											_elementoReto = CentroInfo.efectoElemento(efectoID);
										} else {
											if (CentroInfo.efectoElemento(efectoID) != _elementoReto) {
												GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
												exitoReto = 2;
											}
										}
									}
								}
								break;
							case 24: // limitado
								int hechizoID = sHechizo.getHechizoID();
								if (lanzador._idHechiLanzReto == -1) {
									lanzador._idHechiLanzReto = hechizoID;
								} else {
									if (lanzador._idHechiLanzReto != hechizoID) {
										GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
										exitoReto = 2;
									}
								}
								break;
						}
						if (exitoReto != 0) {
							_retos.remove(reto);
							_retos.put(reto, exitoReto);
						}
					}
				}
				boolean esGC = lanzador.testSiEsGC(sHechizo.getPorcGC(), sHechizo, lanzador);
				String hechizoStr = sHechizo.getHechizoID() + "," + celdaID + "," + sHechizo.getSpriteID() + ","
						+ sHechizo.getNivel() + "," + sHechizo.getSpriteInfos();
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 300, lanzador.getID() + "", hechizoStr);
				if (esGC) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 301, lanzador.getID() + "", hechizoStr);
				}
				if (lanzador.getPersonaje() == null) {
					try {
						Thread.sleep(750);
					} catch (InterruptedException e) {}
				}
				sHechizo.aplicaHechizoAPelea(this, lanzador, celda, esGC);
			}
			if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
				int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 285);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, lanzador.getID() + "",
						lanzador.getID() + ",-" + (sHechizo.getCostePA() - modi));
			} else {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, lanzador.getID() + "",
						lanzador.getID() + ",-" + sHechizo.getCostePA());
			}
			GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 0, lanzador.getID());
			if (!esFC)
				lanzador.addHechizoLanzado(celda.getPrimerLuchador(), sHechizo, lanzador);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			if ( (esFC && sHechizo.esFinTurnoSiFC()) || sHechizo.getHechizoID() == 1735) {
				_tempAccion = "";
				if (lanzador.getMob() != null || lanzador.esInvocacion())
					return 5;
				else {
					_tempAccion = "";
					finTurno();
					return 5;
				}
			}
			if (!verificaSiAcaboPelea()) {
				_tempAccion = "";
				return 10;
			}
		} else if (lanzador.getMob() != null || (lanzador.esInvocacion() && lanzador.getPersonaje() == null)) {
			_tempAccion = "";
			return 10;
		}
		_tempAccion = "";
		return 0;
	}
	
	public void intentarCaC(Personaje perso, int celdaID) { // lanzamientos cuerpo a cuerpo CAC
		Luchador lanzador = getLuchadorPorPJ(perso);
		if (lanzador == null)
			return;
		if (_ordenJugadores.get(_nroOrdenLuc).getID() != lanzador.getID())
			return;
		// retos
		if (_tipo == 4 || _tipo == 3) {
			Map<Integer, Integer> copiaRetos = new TreeMap<Integer, Integer>();
			copiaRetos.putAll(_retos);
			for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
				int reto = entry.getKey();
				int exitoReto = entry.getValue();
				if (exitoReto != 0)
					continue;
				switch (reto) {
					case 5:// Ahorrador
						if (lanzador._hechiLanzadosReto.contains(0)) {
							GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
							exitoReto = 2;
						} else
							lanzador._hechiLanzadosReto.add(0);
						break;
					case 6:// versatil
						if (lanzador._hechiLanzadosReto.contains(0)) {
							GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
							exitoReto = 2;
						} else
							lanzador._hechiLanzadosReto.add(0);
						break;
					case 11:// mistico
						GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
						exitoReto = 2;
						break;
					case 24: // limitado
						int hechizoID = 0;
						if (lanzador._idHechiLanzReto == -1) {
							lanzador._idHechiLanzReto = hechizoID;
						} else {
							if (lanzador._idHechiLanzReto != hechizoID) {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
								exitoReto = 2;
							}
						}
						break;
				}
				if (exitoReto != 0) {
					_retos.remove(reto);
					_retos.put(reto, exitoReto);
				}
			}
		}
		if (perso.getObjPosicion(CentroInfo.ITEM_POS_ARMA) == null) {
			if (_tempLuchadorPA < 4)
				return;
			GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(this, 7, perso.getID());
			if (lanzador.esInvisible())
				lanzador.hacerseVisible(-1);
			Luchador objetivo = _mapaCopia.getCelda(celdaID).getPrimerLuchador();
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 303, perso.getID() + "", celdaID + "");
			if (objetivo != null) {
				int daño = Formulas.getRandomValor("1d5+0");
				int dañoFinal = Formulas.calculFinalDaño(this, lanzador, objetivo, CentroInfo.ELEMENTO_NEUTRAL, daño, true, -1,
						false);
				dañoFinal = EfectoHechizo.aplicarBuffContraGolpe(dañoFinal, objetivo, lanzador, this, -1);
				if (dañoFinal > objetivo.getPDVConBuff())
					dañoFinal = objetivo.getPDVConBuff();
				objetivo.restarPDV(dañoFinal);
				dañoFinal = - (dañoFinal);
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 100, lanzador.getID() + "", objetivo.getID() + "," + dañoFinal);
			}
			_tempLuchadorPA -= 4;
			GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, perso.getID() + "", perso.getID() + ",-4");
			GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 0, perso.getID());
			if (objetivo.getPDVConBuff() <= 0)
				agregarAMuertos(objetivo);
			verificaSiAcaboPelea();
		} else {
			Objeto arma = perso.getObjPosicion(CentroInfo.ITEM_POS_ARMA);
			if (arma.getModelo().getTipo() == 83) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 305, perso.getID() + "", "");
				GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 0, perso.getID());
				finTurno();
			}
			int costePA = arma.getModelo().getCostePA();
			if (_tempLuchadorPA < costePA) {
				return;
			}
			GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(this, 7, perso.getID());
			boolean esFC = arma.getModelo().getPorcFC() != 0
					&& Formulas.getRandomValor(1, arma.getModelo().getPorcFC()) == arma.getModelo().getPorcFC();
			if (esFC) {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 305, perso.getID() + "", "");
				GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 0, perso.getID());
				finTurno();
			} else {
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 303, perso.getID() + "", celdaID + "");
				boolean esGC = lanzador.calculaSiGC(arma.getModelo().getPorcGC());
				if (esGC) {
					GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 301, perso.getID() + "", "0");
				}
				if (lanzador.esInvisible())
					lanzador.hacerseVisible(-1);
				ArrayList<EfectoHechizo> efectos = arma.getEfectos();
				if (esGC) {
					efectos = arma.getEfectosCriticos();
				}
				for (EfectoHechizo EH : efectos) {
					if (_estadoPelea != 3)
						break;
					ArrayList<Luchador> objetivos = Camino.getObjetivosZonaArma(this, arma.getModelo().getTipo(),
							_mapaCopia.getCelda(celdaID), lanzador.getCeldaPelea().getID());
					EH.setTurnos(0);
					EH.aplicarAPelea(this, lanzador, objetivos, true, null);
				}
				_tempLuchadorPA -= costePA;
				GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 102, perso.getID() + "", perso.getID() + ",-" + costePA);
				GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(this, 7, 0, perso.getID());
				verificaSiAcaboPelea();
			}
		}
	}
	
	public boolean puedeLanzarHechizo(Luchador lanzador, StatsHechizos sHechizo, Celda celda, int celdaObjetivo) {
		int celdaDondeLanza;
		if (celdaObjetivo <= -1)
			celdaDondeLanza = lanzador.getCeldaPelea().getID();
		else
			celdaDondeLanza = celdaObjetivo;
		if (_ordenJugadores == null || _nroOrdenLuc < 0)
			return false;
		if (_nroOrdenLuc >= _ordenJugadores.size())
			_nroOrdenLuc = 0;
		Luchador tempLuchador = _ordenJugadores.get(_nroOrdenLuc);
		Personaje perso = lanzador.getPersonaje();
		if (sHechizo == null) {
			if (perso != null) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1169");
			}
			return false;
		}
		for (int estado : sHechizo.getEstadosProhi()) {
			if (lanzador.tieneEstado(estado))
				return false;
		}
		for (int estado : sHechizo.getEstadosNeces()) {
			if (estado == -1)
				break;
			if (!lanzador.tieneEstado(estado))
				return false;
		}
		if (tempLuchador == null || tempLuchador.getID() != lanzador.getID()) {
			if (perso != null) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1175");
			}
			return false;
		}
		int gastarPA = 0;
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 285);
			gastarPA = sHechizo.getCostePA() - modi;
		} else {
			gastarPA = sHechizo.getCostePA();
		}
		if (_tempLuchadorPA < gastarPA) {
			if (perso != null) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1170;" + _tempLuchadorPA + "~" + sHechizo.getCostePA());
			}
			return false;
		}
		if (celda == null) {
			if (perso != null) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1172");
			}
			return false;
		}
		// lanzar en linea
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 288);
			boolean modif = modi == 1;
			if (sHechizo.esLanzLinea() && !modif
					&& !Camino.siCeldasEstanEnMismaLinea(_mapaCopia, celdaDondeLanza, celda.getID(), 'z')) {
				if (perso != null) {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "1173");
				}
				return false;
			}
		} else if (sHechizo.esLanzLinea() && !Camino.siCeldasEstanEnMismaLinea(_mapaCopia, celdaDondeLanza, celda.getID(), 'z')) {
			if (perso != null) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1173");
			}
			return false;
		}
		// linea de vista
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 289);
			boolean modif = modi == 1;
			if (sHechizo.tieneLineaVuelo() && !Camino.checkearLineaDeVista(_mapaCopia, celdaDondeLanza, celda.getID(), lanzador)
					&& !modif) {
				if (perso != null) {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "1174");
				}
				return false;
			}
		} else if (sHechizo.tieneLineaVuelo()
				&& !Camino.checkearLineaDeVista(_mapaCopia, celdaDondeLanza, celda.getID(), lanzador)) {
			if (perso != null) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1174");
			}
			return false;
		}
		int dist = Camino.distanciaEntreDosCeldas(_mapaCopia, celdaDondeLanza, celda.getID());
		int maxAlc = sHechizo.getMaxAlc();
		int minAlc = sHechizo.getMinAlc();
		// + alcance
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 281);
			maxAlc = maxAlc + modi;
		}
		// alcance modificable
		Stats statsConBuff = lanzador.getTotalStatsConBuff();
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 282);
			boolean modif = modi == 1;
			if (sHechizo.esModifAlc() || modif) {
				maxAlc += statsConBuff.getEfecto(117);
				if (maxAlc < minAlc)
					maxAlc = minAlc;
			}
		} else if (sHechizo.esModifAlc()) {
			maxAlc += statsConBuff.getEfecto(117);
			if (maxAlc < minAlc)
				maxAlc = minAlc;
		}
		if (dist < minAlc || dist > maxAlc) {
			if (perso != null) {
				GestorSalida.ENVIAR_Im_INFORMACION(perso, "1171;" + minAlc + "~" + maxAlc + "~" + dist);
			}
			return false;
		}
		if (!HechizoLanzado.poderSigLanzamiento(lanzador, sHechizo.getHechizoID()))
			return false;
		int nroLanzTurno = sHechizo.getMaxLanzPorTurno();
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 290);
			nroLanzTurno = sHechizo.getMaxLanzPorTurno() + modi;
		}
		if (nroLanzTurno - HechizoLanzado.getNroLanzamientos(lanzador, sHechizo.getHechizoID()) <= 0 && nroLanzTurno > 0)
			return false;
		Luchador objetivo = celda.getPrimerLuchador();
		int nroLanzObjetivo = sHechizo.getMaxLanzPorJugador();
		if (lanzador.getTipo() == 1 && perso.getHechizosSetClase().containsKey(sHechizo.getHechizoID())) {
			int modi = perso.getModifSetClase(sHechizo.getHechizoID(), 291);
			nroLanzObjetivo = sHechizo.getMaxLanzPorJugador() + modi;
		}
		if (nroLanzObjetivo - HechizoLanzado.getNroLanzPorObjetivo(lanzador, objetivo, sHechizo.getHechizoID()) <= 0
				&& nroLanzObjetivo > 0)
			return false;
		return true;
	}
	
	public String getPanelResultados(int equipoGanador) {
		long tiempo = System.currentTimeMillis() - _tiempoInicio;
		int initID = _idLuchInit1;
		int tipoX = 0;
		if (_tipo == 1 || _tipo == 2 || _tipo == CentroInfo.PELEA_TIPO_COLISEO)
			tipoX = 1;
		boolean exito = false;
		if (_tipo == 4 || _tipo == 3) {
			Map<Integer, Integer> copiaRetos = new TreeMap<Integer, Integer>();
			copiaRetos.putAll(_retos);
			if (equipoGanador == 1) {
				for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
					int reto = entry.getKey();
					int exitoReto = entry.getValue();
					switch (reto) {
						case 33: // superviviente
							for (Luchador luchador : _inicioLucEquipo1) {
								if (luchador._estaMuerto) {
									exitoReto = 2;
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
									break;
								}
							}
							break;
						case 44:// reparto
						case 46: // cada uno con su mousntro
							for (Luchador luchador : _inicioLucEquipo1) {
								if (luchador._mobMatadosReto.size() > 0) {
									exitoReto = 2;
									GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
									break;
								}
							}
							break;
					}
					if (exitoReto == 2)
						continue;
					if (exitoReto == 0) {
						exitoReto = 1;
						GestorSalida.ENVIAR_GdaK_RETO_REALIZADO(this, reto);
					}
					exito = true;
					_retos.remove(reto);
					_retos.put(reto, exitoReto);
				}
			} else {
				for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
					int reto = entry.getKey();
					int exitoReto = entry.getValue();
					if (exitoReto != 0)
						continue;
					exitoReto = 2;
					GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
					_retos.remove(reto);
					_retos.put(reto, exitoReto);
				}
			}
		}
		String packet = "GE";
		if (_tipo == 4)
			packet += tiempo + ";" + _estrellas + "|" + initID + "|" + tipoX + "|";
		else
			packet += tiempo + "|" + initID + "|" + tipoX + "|";
		ArrayList<Luchador> ganadores = new ArrayList<Luchador>();
		ArrayList<Luchador> perdedores = new ArrayList<Luchador>();
		if (equipoGanador == 1) {
			ganadores.addAll(_equipo1.values());
			perdedores.addAll(_equipo2.values());
		} else {
			ganadores.addAll(_equipo2.values());
			perdedores.addAll(_equipo1.values());
		}
		Personaje pj1 = null;
		long exp = 0;
		for (Luchador luchador : ganadores) {
			pj1 = luchador.getPersonaje();
			if (luchador.esInvocacion() || luchador.getMob() != null)
				continue;
			if (_tipo == 1 && pj1 != null) {
				if (!MundoDofus.estaRankingPVP(pj1.getID())) {
					RankingPVP rank = new RankingPVP(pj1.getID(), pj1.getNombre(), 0, 0, pj1.getAlineacion());
					MundoDofus.addRankingPVP(rank);
					GestorSQL.AGREGAR_RANKINGPVP(rank);
					rank.aumentarVictoria();
				} else {
					MundoDofus.getRanking(pj1.getID()).aumentarVictoria();
				}
			}
			if (_tipo == 1 && pj1 != null && _misionPVP > 0) {
				if (_equipo1.containsValue(luchador) && _misionPVP == 1) {
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1,
							"Thomas Sacre : Contrato finalizado, ven a zaap pueblo para reclamar tu recompensa.", "000000");
					pj1.getMisionPVP().setPjMision(null);
					pj1.getMisionPVP().setTiempoPVP(-2);
					if (pj1.tieneObjModeloNoEquip(10085, 1))
						pj1.eliminarObjetoPorModelo(10085);
					Objeto nuevo = MundoDofus.getObjModelo(10275).crearObjDesdeModelo(Bustemu.CHAPAS_MISION, false);
					if (!pj1.addObjetoSimilar(nuevo, true, -1)) {
						MundoDofus.addObjeto(nuevo, true);
						pj1.addObjetoPut(nuevo);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pj1, nuevo);
					}
					exp = (long) (Formulas.getXPMision(pj1.getNivel()) * Bustemu.RATE_XP_PVP);
					pj1.addExp(exp);
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, "Recibes " + exp + " puntos de experiencia.", "000000");
				} else {
					GestorSalida
							.ENVIAR_cs_CHAT_MENSAJE(pj1, "Thomas Sacre : Has recibido la recompensa de tu cazador.", "000000");
					exp = (long) (Formulas.getXPMision(pj1.getNivel()) * Bustemu.RATE_XP_PVP);
					pj1.addExp(exp);
					GestorSalida.ENVIAR_cs_CHAT_MENSAJE(pj1, "Recibes " + exp + " puntos de experiencia.", "000000");
					Objeto nuevo = MundoDofus.getObjModelo(10275).crearObjDesdeModelo(Bustemu.CHAPAS_MISION, false);
					if (!pj1.addObjetoSimilar(nuevo, true, -1)) {
						MundoDofus.addObjeto(nuevo, true);
						pj1.addObjetoPut(nuevo);
						GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pj1, nuevo);
					}
				}
			} else if (_tipo == 4 && pj1 != null) {
				float porc = MundoDofus.getBalanceMundo(pj1.getAlineacion()) / 100;
				float porcN = (float) Math.rint(pj1.getNivelAlineacion() / 2.5);
				luchador._bonusAlineacion = porc * porcN;
			} else if (_tipo == 6 && pj1 != null) {
				if (pj1.tieneObjModeloNoEquip(11158, 1))
					pj1.eliminarObjetoPorModelo(11158);
				Objeto nuevo = MundoDofus.getObjModelo(11158).crearObjDesdeModelo(1, false);
				if (!pj1.addObjetoSimilar(nuevo, true, -1)) {
					MundoDofus.addObjeto(nuevo, true);
					pj1.addObjetoPut(nuevo);
					GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pj1, nuevo);
				}
				pj1.setEnKoliseo(false);
			}
		}
		for (Luchador luchador : perdedores) {
			if (luchador.esInvocacion())
				continue;
			Personaje pj2 = luchador.getPersonaje();
			if (_tipo == 6 && pj2 != null) {
				pj1.setEnKoliseo(false);
			} else if (_tipo == 1 && pj2 != null && _misionPVP > 0) {
				if (_equipo2.containsValue(luchador) && _misionPVP == 2) {
					pj2.getMisionPVP().setPjMision(null);
					pj2.getMisionPVP().setTiempoPVP(-2);
					if (pj2.tieneObjModeloNoEquip(10085, 1))
						pj2.eliminarObjetoPorModelo(10085);
				}
			}
			if (_tipo == 1 && pj2 != null) {
				if (!MundoDofus.estaRankingPVP(pj2.getID())) {
					RankingPVP rank = new RankingPVP(pj2.getID(), pj2.getNombre(), 0, 0, pj2.getAlineacion());
					MundoDofus.addRankingPVP(rank);
					GestorSQL.AGREGAR_RANKINGPVP(rank);
					rank.aumentarDerrota();
				} else {
					MundoDofus.getRanking(pj2.getID()).aumentarDerrota();
				}
			}
		}
		int grupoPP = 0, minkamas = 0, maxkamas = 0, grupoPPIntacta = 0;
		int PPreto = 0, XPreto = 0;
		float coefEstrella = 0;
		if (_estrellas != 0)
			coefEstrella = (float) _estrellas / 100;
		int estrellaPP = 0;
		int estrellaXP = 0;
		long totalXP = 0;
		ArrayList<Luchador> ordenLuchMasAMenosPP = new ArrayList<Luchador>();
		Luchador lucConMaxPP = null;
		ArrayList<Drop> posibleDrops = new ArrayList<Drop>();
		if ( (_tipo == 4 || _tipo == 5) && equipoGanador == 1) {
			for (Luchador ganador : ganadores) {
				if (!ganador.esInvocacion() || (ganador.getMob() != null && ganador.getMob().getModelo().getID() == 285)) {
					int prosp = ganador.getTotalStatsConBuff().getEfecto(176);
					ganador._prospeccionTemporal = prosp;
					Personaje pjGanador = ganador.getPersonaje();
					if (pjGanador != null) {
						if (pjGanador.getAlineacion() == 1 || pjGanador.getAlineacion() == 2) {
							if (_mapaReal.getSubArea().getAlineacion() == pjGanador.getAlineacion()) {
								ganador._prospeccionTemporal = (int) (prosp + (prosp * ganador._bonusAlineacion));
							} else {
								ganador._prospeccionTemporal = (int) (prosp - (prosp * ganador._bonusAlineacion));
							}
						}
						if (_tipo == 4) {
							Mascota mascota = pjGanador.getMascota();
							if (mascota != null) {
								if (mascota.esDevoraAlmas()) {
									boolean comio = false;
									for (Entry<Integer, Integer> entry : _mobGrupo.getAlmasMobs().entrySet()) {
										if (mascota.esComestible(entry.getKey())) {
											comio = true;
											mascota.comerAlma(entry.getKey(), entry.getValue());
										}
									}
									if (comio) {
										GestorSalida
												.ENVIAR_OCK_ACTUALIZA_OBJETO(pjGanador, MundoDofus.getObjeto(mascota.getID()));
									}
								}
							}
						}
					}
					grupoPP += ganador._prospeccionTemporal;
				}
			}
			grupoPPIntacta = grupoPP;
			if (grupoPP < 0)
				grupoPP = 0;
			if (exito) {
				for (Entry<Integer, Integer> entry : _retos.entrySet()) {
					int reto = entry.getKey();
					int exitoReto = entry.getValue();
					if (exitoReto == 2)
						continue;
					Reto retox = MundoDofus.getReto(reto);
					PPreto += retox.bonusPP();
					XPreto += retox.bonusXP();
				}
			}
			if (coefEstrella > 0)
				estrellaPP = (int) (grupoPP * coefEstrella);
			grupoPP = grupoPP + ( (grupoPP * PPreto / 100) + estrellaPP);
			for (Luchador perdedor : perdedores) {
				MobGrado mob = perdedor.getMob();
				if (perdedor.esInvocacion() || mob == null)
					continue;
				MobModelo mobModelo = mob.getModelo();
				minkamas += mobModelo.getMinKamas();
				maxkamas += mobModelo.getMaxKamas();
				for (Drop drop : mobModelo.getDrops()) {
					int prospReq = (int) (drop.getProspReq() * Formulas.PROSP_REQ);
					if (prospReq <= grupoPP) {
						int nuevaProbabilidad = drop.getProbabilidad() + Bustemu.RATE_DROP;
						int cant = (int) (drop.getDropMax() * Math.sqrt(drop.getProbabilidad())) * Bustemu.CANT_DROP;
						posibleDrops.add(new Drop(drop.getObjetoID(), prospReq, nuevaProbabilidad, cant));
					}
				}
			}
			Map<Integer, Luchador> todosConPP = new TreeMap<Integer, Luchador>();
			for (Luchador ganador : ganadores) {
				int prosp = ganador.getTotalStatsConBuff().getEfecto(176);
				while (todosConPP.containsKey(prosp))
					prosp += 1;
				todosConPP.put(prosp, ganador);
			}
			while (ordenLuchMasAMenosPP.size() < ganadores.size()) {
				int tempPP = -1;
				for (Entry<Integer, Luchador> entry : todosConPP.entrySet()) {
					if (entry.getKey() > tempPP && !ordenLuchMasAMenosPP.contains(entry.getValue())) {
						lucConMaxPP = entry.getValue();
						tempPP = entry.getKey();
					}
				}
				ordenLuchMasAMenosPP.add(lucConMaxPP);
			}
			ganadores.clear();
			ganadores.addAll(ordenLuchMasAMenosPP);
			for (Luchador perdedor : perdedores) {
				if (perdedor.esInvocacion() || perdedor.getMob() == null)
					continue;
				totalXP += perdedor.getMob().getBaseXp();
			}
			if (coefEstrella > 0)
				estrellaXP = (int) (totalXP * coefEstrella);
			totalXP = totalXP + (totalXP * XPreto / 100) + estrellaXP;
			boolean mobCapturable = true;
			for (Luchador perdedor : perdedores) {
				try {
					mobCapturable &= perdedor.getMob().getModelo().esCapturable();
				} catch (Exception e) {
					mobCapturable = false;
					break;
				}
			}
			_esCapturable |= mobCapturable;
			if (_esCapturable) {
				boolean primero = false;
				int maxNivel = 0;
				String piedraStats = "";
				for (Luchador perdedor : perdedores) {
					if (primero)
						piedraStats += ",";
					piedraStats += "26f#" + Integer.toHexString(perdedor.getNivel()) + "#0#"
							+ Integer.toHexString(perdedor.getMob().getModelo().getID());
					primero = true;
					if (perdedor.getNivel() > maxNivel)
						maxNivel = perdedor.getNivel();
				}
				_piedraAlma = new PiedraDeAlma(MundoDofus.getSigIDObjeto(), 1, 7010, -1, piedraStats);
				for (Luchador ganador : ganadores) {
					if (!ganador.esInvocacion() && ganador.tieneEstado(CentroInfo.ESTADO_CAPT_ALMAS)) {
						_capturadores.add(ganador);
					}
				}
				if (_capturadores.size() > 0 && !_mapaReal.esArena()) {
					for (int i = 0; i < _capturadores.size(); i++) {
						try {
							Luchador capt = _capturadores.get(Formulas.getRandomValor(0, _capturadores.size() - 1));
							Personaje capturador = capt.getPersonaje();
							Objeto objPos1 = capturador.getObjPosicion(1);
							if (objPos1.getModelo().getTipo() != 83) {
								_capturadores.remove(capt);
								continue;
							}
							Duo<Integer, Integer> piedraJug = Formulas.decompilarPiedraAlma(objPos1);
							if (piedraJug._segundo < maxNivel) {
								_capturadores.remove(capt);
								continue;
							}
							int suerteCaptura = Formulas.totalPorcCaptura(piedraJug._primero, capturador);
							if (Formulas.getRandomValor(1, 100) <= suerteCaptura) {
								int piedra = objPos1.getID();
								capturador.borrarObjetoRemove(piedra);
								GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO(capturador, piedra);
								_capturadorGanador = capt._id;
								break;
							}
						} catch (NullPointerException e) {
							continue;
						}
					}
				}
			}
		}
		for (Luchador ganador : ganadores) {
			if (ganador._estaRetirado || ganador._doble != null)
				continue;
			Personaje pjGanador = ganador.getPersonaje();
			if (tipoX == 0) {
				if (ganador.esInvocacion() && pjGanador == null)
					if (ganador.getMob().getModelo().getID() != 285)
						continue;
				long xpGanada = Formulas.getXpGanadaPVM(ganador, ganadores, perdedores, totalXP);
				long xpParaGremio = Formulas.getXPDonadaGremio(ganador, xpGanada);
				xpGanada -= (xpParaGremio * 10);
				long xpParaDragopavo = 0;
				if (pjGanador != null && pjGanador.estaMontando()) {
					xpParaDragopavo = Formulas.getXPDonadaDragopavo(ganador, xpGanada);
					pjGanador.getMontura().addXp(xpParaDragopavo);
					GestorSalida.ENVIAR_Re_DETALLES_MONTURA(pjGanador, "+", pjGanador.getMontura());
				}
				xpGanada -= (xpParaDragopavo * 100);
				long kamasGanadas = Formulas.getKamasGanadas(ganador, minkamas, maxkamas);
				if (pjGanador != null) {
					if (pjGanador.getAlineacion() == 1 || pjGanador.getAlineacion() == 2) {
						if (_mapaReal.getSubArea().getAlineacion() == pjGanador.getAlineacion()) {
							xpGanada = (long) (xpGanada + (xpGanada * ganador._bonusAlineacion));
							kamasGanadas = (long) (kamasGanadas + (kamasGanadas * ganador._bonusAlineacion));
						} else {
							xpGanada = (long) (xpGanada - (xpGanada * ganador._bonusAlineacion));
							kamasGanadas = (long) (kamasGanadas - (kamasGanadas * ganador._bonusAlineacion));
						}
						if (xpGanada < 0)
							xpGanada = 0;
						if (kamasGanadas < 0)
							kamasGanadas = 0;
					}
				}
				String drops = "";
				ArrayList<Drop> tempDrops = new ArrayList<Drop>();
				tempDrops.addAll(posibleDrops);
				Map<Integer, Integer> objetosGanados = new TreeMap<Integer, Integer>();
				int veces = 0;
				int parteCorresponde = 100;
				int maximo = 0;
				int canDrops = 0;
				if (_tipo == 4 || _tipo == 5) {
					if (pjGanador != null)
						parteCorresponde = grupoPPIntacta
								/ (ganador._prospeccionTemporal <= 0 ? 1 : ganador._prospeccionTemporal);
					if (_tipo == 4) {
						for (Drop dro : tempDrops) {
							canDrops += dro.getDropMax();
						}
						maximo = canDrops / (parteCorresponde <= 0 ? 1000 : parteCorresponde);
					}
					if (pjGanador == null)
						maximo = 3;
				}
				if (_tipo == 5 && pjGanador != null) {
					if (pjGanador.getGremio() == null || (_Recaudador.getGremioID() != pjGanador.getGremio().getID())) {
						boolean primero = false;
						Collection<Objeto> objRecaudador = null;
						ArrayList<Integer> idObjetos = new ArrayList<Integer>();
						objRecaudador = _Recaudador.getObjetos().values();
						if (objRecaudador.size() > 0) {
							maximo = objRecaudador.size() / parteCorresponde;
							for (Objeto objeto : objRecaudador) {
								if (objeto == null)
									continue;
								if (primero)
									drops += ",";
								drops += objeto.getModelo().getID() + "~" + objeto.getCantidad();
								if (pjGanador.addObjetoSimilar(objeto, true, -1))
									MundoDofus.eliminarObjeto(objeto.getID());
								else {
									pjGanador.addObjetoPut(objeto);
									GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pjGanador, objeto);
								}
								idObjetos.add(objeto.getID());
								primero = true;
								veces++;
								if (veces >= maximo)
									break;
							}
							for (Integer integer : idObjetos) {
								_Recaudador.borrarObjeto(integer);
							}
						}
					}
				} else {
					for (Drop drop : tempDrops) {
						int porc = drop.getProbabilidad();
						if (porc >= 110) {
							ObjetoModelo OM = MundoDofus.getObjModelo(drop.getObjetoID());
							if (OM == null)
								continue;
							veces++;
							int id = OM.getID();
							Random random = new Random();
							int cant = random.nextInt(drop.getDropMax()) + 1;
							objetosGanados.put(id, (objetosGanados.get(id) == null ? 0 : objetosGanados.get(id)) + cant);
							drop.setDropMax(drop.getDropMax() - cant);
							if (drop.getDropMax() <= 0)
								posibleDrops.remove(drop);
							if (veces >= maximo)
								break;
						} else {
							Random rand = new Random();
							int jet = rand.nextInt(110);
							if (jet < porc) {
								ObjetoModelo OM = MundoDofus.getObjModelo(drop.getObjetoID());
								if (OM == null)
									continue;
								veces++;
								int id = OM.getID();
								Random random = new Random();
								int cant = random.nextInt(drop.getDropMax()) + 1;
								objetosGanados.put(id, (objetosGanados.get(id) == null ? 0 : objetosGanados.get(id)) + cant);
								drop.setDropMax(drop.getDropMax() - cant);
								if (drop.getDropMax() <= 0)
									posibleDrops.remove(drop);
								if (veces >= maximo)
									break;
							}
						}
					}
					if (_tipo == 3) {
						for (Luchador perdedor : perdedores) {
							MobGrado mob = perdedor.getMob();
							if (perdedor.esInvocacion() || mob == null)
								continue;
							int idMob = mob.getModelo().getID();
							if (CentroInfo.getDoplonDopeul(idMob) != -1) {
								objetosGanados.put(CentroInfo.getDoplonDopeul(idMob), 1);
								objetosGanados.put(CentroInfo.getCertificadoDopeul(idMob), 1);
							}
						}
					} else if (ganador._id == _capturadorGanador && _piedraAlma != null) {
						if (drops.length() > 0)
							drops += ",";
						drops += 7010 + "~" + 1;
						if (!pjGanador.addObjetoSimilar(_piedraAlma, false, -1)) {
							MundoDofus.addObjeto(_piedraAlma, true);
							pjGanador.addObjetoPut(_piedraAlma);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pjGanador, _piedraAlma);
						}
					}
					for (Entry<Integer, Integer> entry : objetosGanados.entrySet()) {
						ObjetoModelo OM = MundoDofus.getObjModelo(entry.getKey());
						if (OM == null)
							continue;
						if (drops.length() > 0)
							drops += ",";
						drops += entry.getKey() + "~" + entry.getValue();
						Objeto obj = OM.crearObjDesdeModelo(entry.getValue(), false);
						if (pjGanador == null) {
							Personaje invocador = ganador.getInvocador().getPersonaje();
							if (!invocador.addObjetoSimilar(obj, true, -1)) {
								MundoDofus.addObjeto(obj, true);
								invocador.addObjetoPut(obj);
								GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(invocador, obj);
							}
						} else if (!pjGanador.addObjetoSimilar(obj, true, -1)) {
							MundoDofus.addObjeto(obj, true);
							pjGanador.addObjetoPut(obj);
							GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(pjGanador, obj);
						}
					}
				}
				if (xpGanada != 0 && pjGanador != null)
					pjGanador.addExp(xpGanada);
				if (kamasGanadas != 0 && pjGanador != null)
					pjGanador.addKamas(kamasGanadas);
				if (xpParaGremio > 0 && pjGanador.getMiembroGremio() != null)
					pjGanador.getMiembroGremio().darXpAGremio(xpParaGremio);
				packet += "2;" + ganador.getID() + ";" + ganador.getNombreLuchador() + ";" + ganador.getNivel() + ";"
						+ (ganador._estaMuerto ? "1" : "0") + ";";
				packet += ganador.xpString(";") + ";";
				packet += (xpGanada == 0 ? "" : xpGanada) + ";";
				packet += (xpParaGremio == 0 ? "" : xpParaGremio) + ";";
				packet += (xpParaDragopavo == 0 ? "" : xpParaDragopavo) + ";";
				packet += drops + ";";
				packet += (kamasGanadas == 0 ? "" : kamasGanadas) + "|";
				if (pjGanador != null) {
					GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(pjGanador);
				}
			} else {
				if (ganador.esInvocacion() && ganador.getPersonaje() == null)
					continue;
				int honor = 0;
				int deshonor = 0;
				if (tipoX == 1) {
					if (_tipo == 6) {
						packet += "2;" + ganador.getID() + ";" + ganador.getNombreLuchador() + ";" + ganador.getNivel() + ";"
								+ (ganador._estaMuerto ? "1" : "0") + ";";
						packet += (pjGanador.getAlineacion() != -1 ? MundoDofus.getExpNivel(pjGanador.getNivelAlineacion())._pvp
								: 0) + ";";
						packet += pjGanador.getHonor() + ";";
						int maxHonor = MundoDofus.getExpNivel(pjGanador.getNivelAlineacion() + 1)._pvp;
						if (maxHonor == -1)
							maxHonor = MundoDofus.getExpNivel(pjGanador.getNivelAlineacion())._pvp;
						packet += (pjGanador.getAlineacion() != -1 ? maxHonor : 0) + ";";
						packet += honor + ";";
						packet += pjGanador.getNivelAlineacion() + ";";
						packet += pjGanador.getDeshonor() + ";";
						packet += deshonor + ";";
						packet += "11158~1;";
						long kamasKoli = pjGanador.kamasKoli();
						long expKoli = pjGanador.expKoli();
						packet += kamasKoli + ";";
						packet += ganador.xpString(";") + ";";
						packet += expKoli + "|";
					} else if (_tipo == 1) {
						if (pjGanador == null)
							continue;
						if (_luchInit2.getPersonaje().getAlineacion() != 0 && _luchInit1.getPersonaje().getAlineacion() != 0) {
							if (_luchInit2.getPersonaje().getCuenta().getActualIP()
									.compareTo(_luchInit1.getPersonaje().getCuenta().getActualIP()) != 0
									&& Bustemu.PERMITIR_PVP) {
								honor = Formulas.calcularHonorGanado(ganadores, perdedores, ganador);
							}
							if (pjGanador.getDeshonor() > 0)
								deshonor = -1;
						}
						if (honor < 0)
							honor = 0;
						pjGanador.addHonor(honor);
						pjGanador.setDeshonor(pjGanador.getDeshonor() + deshonor);
						packet += "2;" + ganador.getID() + ";" + ganador.getNombreLuchador() + ";" + ganador.getNivel() + ";"
								+ (ganador._estaMuerto ? "1" : "0") + ";";
						packet += (pjGanador.getAlineacion() != -1 ? MundoDofus.getExpNivel(pjGanador.getNivelAlineacion())._pvp
								: 0) + ";";
						packet += pjGanador.getHonor() + ";";
						int maxHonor = MundoDofus.getExpNivel(pjGanador.getNivelAlineacion() + 1)._pvp;
						if (maxHonor == -1)
							maxHonor = MundoDofus.getExpNivel(pjGanador.getNivelAlineacion())._pvp;
						packet += (pjGanador.getAlineacion() != -1 ? maxHonor : 0) + ";";
						packet += honor + ";";
						packet += pjGanador.getNivelAlineacion() + ";";
						packet += pjGanador.getDeshonor() + ";";
						packet += deshonor + ";";
						if (_misionPVP > 0)
							packet += "10275~" + Bustemu.CHAPAS_MISION;
						packet += ";0;";
						packet += pjGanador.xpString(";") + ";";
						packet += exp + "|";
					} else if (_tipo == 2) {
						honor = Formulas.calcularHonorGanado(ganadores, perdedores, ganador);
						Personaje P = ganador.getPersonaje();
						if (P != null) {
							if (honor < 0)
								honor = 0;
							P.addHonor(honor);
							if (P.getDeshonor() - deshonor < 0)
								deshonor = 0;
							P.setDeshonor(P.getDeshonor() - deshonor);
							packet += "2;" + ganador.getID() + ";" + ganador.getNombreLuchador() + ";" + ganador.getNivel() + ";"
									+ (ganador._estaMuerto ? "1" : "0") + ";";
							packet += (P.getAlineacion() != -1 ? MundoDofus.getExpNivel(P.getNivelAlineacion())._pvp : 0) + ";";
							packet += P.getHonor() + ";";
							int maxHonor = MundoDofus.getExpNivel(P.getNivelAlineacion() + 1)._pvp;
							if (maxHonor == -1)
								maxHonor = MundoDofus.getExpNivel(P.getNivelAlineacion())._pvp;
							packet += (P.getAlineacion() != -1 ? maxHonor : 0) + ";";
							packet += honor + ";";
							packet += P.getNivelAlineacion() + ";";
							packet += P.getDeshonor() + ";";
							packet += deshonor;
							packet += ";;0;0;0;0;0|";
						} else {
							Prisma prisma = ganador.getPrisma();
							honor = honor * 5;
							if (prisma.getHonor() + honor < 0)
								honor = -prisma.getHonor();
							honor *= 3;
							prisma.addHonor(honor);
							packet += "2;" + ganador.getID() + ";" + ganador.getNombreLuchador() + ";" + ganador.getNivel() + ";"
									+ (ganador._estaMuerto ? "1" : "0") + ";";
							packet += MundoDofus.getExpNivel(prisma.getNivel())._pvp + ";";
							packet += prisma.getHonor() + ";";
							int maxHonor = MundoDofus.getExpNivel(prisma.getNivel() + 1)._pvp;
							if (maxHonor == -1)
								maxHonor = MundoDofus.getExpNivel(prisma.getNivel())._pvp;
							packet += maxHonor + ";";
							packet += honor + ";";
							packet += prisma.getNivel() + ";";
							packet += "0;0;;0;0;0;0;0|";
						}
					}
				}
			}
		}
		for (Luchador perdedor : perdedores) {// equipo perdedor
			if (perdedor._doble != null)
				continue;
			Personaje pjPerdedor = perdedor.getPersonaje();
			if (perdedor.esInvocacion() && pjPerdedor == null)
				continue;
			if (tipoX == 0) {
				if (perdedor.getPDVConBuff() == 0 || perdedor._estaRetirado) {
					packet += "0;" + perdedor.getID() + ";" + perdedor.getNombreLuchador() + ";" + perdedor.getNivel() + ";1"
							+ ";" + perdedor.xpString(";") + ";;;;|";
				} else {
					packet += "0;" + perdedor.getID() + ";" + perdedor.getNombreLuchador() + ";" + perdedor.getNivel() + ";0"
							+ ";" + perdedor.xpString(";") + ";;;;|";
				}
			} else {
				int honor = 0;
				int deshonor = 0;
				if (_tipo == 6) {
					packet += "0;" + perdedor.getID() + ";" + perdedor.getNombreLuchador() + ";" + perdedor.getNivel() + ";"
							+ (perdedor._estaMuerto ? "1" : "0") + ";";
					packet += (pjPerdedor.getAlineacion() != -1 ? MundoDofus.getExpNivel(pjPerdedor.getNivelAlineacion())._pvp
							: 0) + ";";
					packet += pjPerdedor.getHonor() + ";";
					int maxHonor = MundoDofus.getExpNivel(pjPerdedor.getNivelAlineacion() + 1)._pvp;
					if (maxHonor == -1)
						maxHonor = MundoDofus.getExpNivel(pjPerdedor.getNivelAlineacion())._pvp;
					packet += (pjPerdedor.getAlineacion() != -1 ? maxHonor : 0) + ";";
					packet += honor + ";";
					packet += pjPerdedor.getNivelAlineacion() + ";";
					packet += pjPerdedor.getDeshonor() + ";";
					packet += deshonor + ";";
					packet += "11158~1;";
					long kamasKoli = pjPerdedor.kamasKoli();
					long expKoli = pjPerdedor.expKoli();
					packet += kamasKoli + ";";
					packet += perdedor.xpString(";") + ";";
					packet += expKoli + "|";
				} else if (_tipo == 1) {
					if (pjPerdedor == null)
						continue;
					if (_luchInit2.getPersonaje().getAlineacion() != 0 && _luchInit1.getPersonaje().getAlineacion() != 0) {
						if (_luchInit2.getPersonaje().getCuenta().getActualIP()
								.compareTo(_luchInit1.getPersonaje().getCuenta().getActualIP()) != 0
								|| Bustemu.PERMITIR_PVP) {
							honor = Formulas.calcularHonorGanado(ganadores, perdedores, perdedor);
						}
					}
					Personaje P = pjPerdedor;
					if (P.getHonor() < 0 || honor > 0)
						honor = 0;
					P.addHonor(honor);
					if (P.getDeshonor() - deshonor < 0)
						deshonor = 0;
					P.setDeshonor(P.getDeshonor() - deshonor);
					packet += "0;" + perdedor.getID() + ";" + perdedor.getNombreLuchador() + ";" + perdedor.getNivel() + ";"
							+ (perdedor._estaMuerto ? "1" : "0") + ";";
					packet += (P.getAlineacion() != -1 ? MundoDofus.getExpNivel(P.getNivelAlineacion())._pvp : 0) + ";";
					packet += P.getHonor() + ";";
					int maxHonor = MundoDofus.getExpNivel(P.getNivelAlineacion() + 1)._pvp;
					if (maxHonor == -1)
						maxHonor = MundoDofus.getExpNivel(P.getNivelAlineacion())._pvp;
					packet += (P.getAlineacion() != -1 ? maxHonor : 0) + ";";
					packet += honor + ";";
					packet += P.getNivelAlineacion() + ";";
					packet += P.getDeshonor() + ";";
					packet += deshonor + ";";
					packet += ";0;";
					packet += pjPerdedor.xpString(";") + ";";
					packet += "0|";
				} else if (_tipo == 2) {
					honor = Formulas.calcularHonorGanado(ganadores, perdedores, perdedor);
					if (pjPerdedor != null) {
						if (pjPerdedor.getHonor() < 0 || honor > 0)
							honor = 0;
						pjPerdedor.addHonor(honor);
						if (pjPerdedor.getDeshonor() - deshonor < 0)
							deshonor = 0;
						pjPerdedor.setDeshonor(pjPerdedor.getDeshonor() - deshonor);
						packet += "0;" + perdedor.getID() + ";" + perdedor.getNombreLuchador() + ";" + perdedor.getNivel() + ";"
								+ (perdedor._estaMuerto ? "1" : "0") + ";";
						packet += (pjPerdedor.getAlineacion() != -1 ? MundoDofus.getExpNivel(pjPerdedor.getNivelAlineacion())._pvp
								: 0)
								+ ";";
						packet += pjPerdedor.getHonor() + ";";
						int maxHonor = MundoDofus.getExpNivel(pjPerdedor.getNivelAlineacion() + 1)._pvp;
						if (maxHonor == -1)
							maxHonor = MundoDofus.getExpNivel(pjPerdedor.getNivelAlineacion())._pvp;
						packet += (pjPerdedor.getAlineacion() != -1 ? maxHonor : 0) + ";";
						packet += honor + ";";
						packet += pjPerdedor.getNivelAlineacion() + ";";
						packet += pjPerdedor.getDeshonor() + ";";
						packet += deshonor;
						packet += ";;0;0;0;0;0|";
					} else {
						Prisma prisma = perdedor.getPrisma();
						if (prisma.getHonor() + honor < 0)
							honor = -prisma.getHonor();
						prisma.addHonor(honor);
						packet += "0;" + perdedor.getID() + ";" + perdedor.getNombreLuchador() + ";" + perdedor.getNivel() + ";"
								+ (perdedor._estaMuerto ? "1" : "0") + ";";
						packet += MundoDofus.getExpNivel(prisma.getNivel())._pvp + ";";
						packet += prisma.getHonor() + ";";
						int maxHonor = MundoDofus.getExpNivel(prisma.getNivel() + 1)._pvp;
						if (maxHonor == -1)
							maxHonor = MundoDofus.getExpNivel(prisma.getNivel())._pvp;
						packet += maxHonor + ";";
						packet += honor + ";";
						packet += prisma.getNivel() + ";";
						packet += "0;0;;0;0;0;0;0|";
					}
				}
			}
		}
		if (_mapaCopia == null)
			return packet;
		if (MundoDofus.getRecauPorMapaID(_mapaCopia.getID()) != null && _tipo == 4) {
			Recaudador recau = MundoDofus.getRecauPorMapaID(_mapaCopia.getID());
			long xpGanada = Formulas.getXpGanadaRecau(recau, totalXP) / 1000;
			long winkamas = Formulas.getKamasGanadaRecau(minkamas, maxkamas) / 100;
			recau.addXp(xpGanada);
			recau.setKamas(recau.getKamas() + winkamas);
			packet += "5;" + recau.getID() + ";" + recau.getN1() + "," + recau.getN2() + ";"
					+ MundoDofus.getGremio(recau.getGremioID()).getNivel() + ";0;";
			Gremio gremio = MundoDofus.getGremio(recau.getGremioID());
			packet += gremio.getNivel() + ";";
			packet += gremio.getXP() + ";";
			packet += MundoDofus.getXPMaxGremio(gremio.getNivel()) + ";";
			packet += ";";// Xp Ganada
			packet += xpGanada + ";";// Xp Gremio
			packet += ";";// Montura
			String drops = "";
			if (gremio.getStats().get(158) >= recau.getPodsActuales()) {
				ArrayList<Drop> tempDrops = new ArrayList<Drop>();
				tempDrops.addAll(posibleDrops);
				Map<Integer, Integer> objGanados = new TreeMap<Integer, Integer>();
				int veces = 0;
				int maximo = gremio.getStats(176) / 75;
				for (Drop drop : tempDrops) {
					int porc = drop.getProbabilidad();
					if (porc >= 110) {
						ObjetoModelo OT = MundoDofus.getObjModelo(drop.getObjetoID());
						if (OT == null)
							continue;
						veces++;
						int id = OT.getID();
						objGanados.put(id, (objGanados.get(id) == null ? 0 : objGanados.get(id)) + 1);
						drop.setDropMax(drop.getDropMax() - 1);
						if (drop.getDropMax() == 0)
							posibleDrops.remove(drop);
						if (veces >= maximo)
							break;
					} else {
						Random rand = new Random();
						int jet = rand.nextInt(110);
						if (jet < porc) {
							ObjetoModelo OT = MundoDofus.getObjModelo(drop.getObjetoID());
							if (OT == null)
								continue;
							veces++;
							int id = OT.getID();
							objGanados.put(id, (objGanados.get(id) == null ? 0 : objGanados.get(id)) + 1);
							drop.setDropMax(drop.getDropMax() - 1);
							if (drop.getDropMax() == 0)
								posibleDrops.remove(drop);
							if (veces >= maximo)
								break;
						}
					}
				}
				for (Entry<Integer, Integer> entry : objGanados.entrySet()) {
					ObjetoModelo OT = MundoDofus.getObjModelo(entry.getKey());
					if (OT == null)
						continue;
					if (drops.length() > 0)
						drops += ",";
					drops += entry.getKey() + "~" + entry.getValue();
					Objeto obj = OT.crearObjDesdeModelo(entry.getValue(), false);
					MundoDofus.addObjeto(obj, true);
					recau.addObjeto(obj);
				}
			}
			packet += drops + ";";
			packet += winkamas + "|";
			GestorSQL.ACTUALIZAR_RECAUDADOR(recau);
		}
		return packet;
	}
	
	public void agregarAMuertos(Luchador victima) {
		if (victima._estaMuerto)
			return;
		victima._estaMuerto = true;
		int idVictima = victima.getID();
		if (!victima._estaRetirado)
			_listaMuertos.put(idVictima, victima);// se agrega el jugador a la lista de cadaveres
		if (_tipo == 4 || _tipo == 3) {
			if (!victima.esInvocacion() && _equipo2.values().contains(victima)) {
				_mobsMuertosReto.add(idVictima);// agrega el mob a la lista negra
			}
		}
		if (_nroOrdenLuc < 0) {
			System.out.println("victima : " + victima.getNombreLuchador() + " , id: " + victima.getID());
			System.out.println("_ordenJugadores : " + _ordenJugadores.size());
			System.out.println("_nroOrdenLuc : " + _nroOrdenLuc);
			System.out.println("mapa ID : " + _mapaCopia.getID());
			return;
		}
		if (_nroOrdenLuc >= _ordenJugadores.size())
			_nroOrdenLuc = 0;
		// retos
		Luchador asesino = _ordenJugadores.get(_nroOrdenLuc);
		if ( (_tipo == 4 || _tipo == 3) && victima.getMob() != null && !victima.esInvocacion()) {
			Map<Integer, Integer> copiaRetos = new TreeMap<Integer, Integer>();
			copiaRetos.putAll(_retos);
			for (Entry<Integer, Integer> entry : copiaRetos.entrySet()) {
				int reto = entry.getKey();
				int exitoReto = entry.getValue();
				if (exitoReto != 0)
					continue;
				int cant2 = 0;
				int nivelV = victima.getNivel();
				switch (reto) {
					case 3:// elegido voluntario
						if (_mobsMuertosReto.size() > 0) {
							if (_mobsMuertosReto.get(0) == _idMobReto) {
								GestorSalida.ENVIAR_GdaK_RETO_REALIZADO(this, reto);
								exitoReto = 1;
							} else {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
								exitoReto = 2;
							}
						}
						break;
					case 4:// aplazamiento
						cant2 = _inicioLucEquipo2.size();
						if (_mobsMuertosReto.size() == cant2) {
							if (_mobsMuertosReto.get(cant2 - 1) == _idMobReto) {
								GestorSalida.ENVIAR_GdaK_RETO_REALIZADO(this, reto);
								exitoReto = 1;
							} else {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
								exitoReto = 2;
							}
						}
						break;
					case 10:// cruel
					case 25:// ordenado
						for (Entry<Integer, Integer> e : _ordenNivelMobs.entrySet()) {
							if (e.getValue() == nivelV) {
								if (e.getKey() == idVictima) {
									_ordenNivelMobs.remove(idVictima);
									break;
								}
							} else {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
								exitoReto = 2;
								break;
							}
						}
						break;
					case 28:// ni pias ni sumisas
						if (asesino.getPersonaje() == null)
							continue;
						if (asesino.getPersonaje().getSexo() == 0) {
							GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
							exitoReto = 2;
						}
						break;
					case 29: // ni pios ni sumisos
						if (asesino.getPersonaje() == null)
							continue;
						if (asesino.getPersonaje().getSexo() == 1) {
							GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
							exitoReto = 2;
						}
						break;
					case 30:// los pequeños antes
						if (asesino.getPersonaje() == null)
							continue;
						if (asesino.getID() != _luchMenorNivelReto) {
							GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
							exitoReto = 2;
						}
						break;
					case 31:// focalizacion
						if (_idMobReto == 0) {
							_idMobReto = idVictima;
						} else if (_mobsMuertosReto.contains(_idMobReto)) {
							_idMobReto = 0;
						}
						break;
					case 32:// elitista
						if (_idMobReto == idVictima) {
							GestorSalida.ENVIAR_GdaK_RETO_REALIZADO(this, reto);
							exitoReto = 1;
						} else {
							GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
							exitoReto = 2;
						}
						break;
					case 35:// asesino a sueldo
						boolean siguiente = false;
						for (Entry<Integer, Luchador> e : _ordenLuchMobs.entrySet()) {
							if (e.getKey() == idVictima) {
								_ordenLuchMobs.remove(idVictima);
								siguiente = true;
								break;
							} else {
								GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
								exitoReto = 2;
								break;
							}
						}
						if (siguiente) {
							for (Entry<Integer, Luchador> e : _ordenLuchMobs.entrySet()) {
								GestorSalida.ENVIAR_Gf_MOSTRAR_CASILLA_EN_PELEA(this, 5, e.getKey(), e.getValue().getCeldaPelea()
										.getID());
								break;
							}
						}
						break;
					case 42: // el dos por uno
						_cantMobsMuerto++;
						if (_cantMobsMuerto > 2) {
							GestorSalida.ENVIAR_GdaO_RETO_PERDIDO(this, reto);
							exitoReto = 2;
						}
						break;
					case 44:// reparto
					case 46:// cada uno con su monstruo
						if (asesino.getPersonaje() == null)
							continue;
						asesino._mobMatadosReto.add(idVictima);
						break;
				}
				if (exitoReto != 0) {
					_retos.remove(reto);
					_retos.put(reto, exitoReto);
				}
			}
		}
		if (_inicioLucEquipo1.contains(victima))
			if (!victima.esInvocacion())
				if (!_muertesLuchInic1.contains(idVictima))
					_muertesLuchInic1.add(idVictima);
		if (_inicioLucEquipo2.contains(victima))
			if (!victima.esInvocacion())
				if (!_muertesLuchInic2.contains(idVictima))
					_muertesLuchInic2.add(idVictima);
		GestorSalida.ENVIAR_GA103_JUGADOR_MUERTO(this, 7, idVictima);
		victima.getCeldaPelea().getLuchadores().clear();
		if (victima.getEquipoBin() == 0) {
			TreeMap<Integer, Luchador> team = new TreeMap<Integer, Luchador>();
			team.putAll(_equipo1);
			for (Entry<Integer, Luchador> entry : team.entrySet()) {
				Luchador invocacion = entry.getValue();
				if (invocacion._estaMuerto || invocacion._estaRetirado)
					continue;
				if (invocacion.esInvocacion()) {
					if (invocacion.getInvocador().getID() == idVictima) {
						try {
							Thread.sleep(150);
						} catch (Exception E) {}
						agregarAMuertos(invocacion);
					}
				}
			}
		} else if (victima.getEquipoBin() == 1) {
			TreeMap<Integer, Luchador> team = new TreeMap<Integer, Luchador>();
			team.putAll(_equipo2);
			for (Entry<Integer, Luchador> entry : team.entrySet()) {
				Luchador invocacion = entry.getValue();
				if (invocacion._estaMuerto || invocacion._estaRetirado)
					continue;
				if (invocacion.esInvocacion()) {
					if (invocacion.getInvocador().getID() == idVictima) {
						try {
							Thread.sleep(150);
						} catch (Exception E) {}
						agregarAMuertos(invocacion);
					}
				}
			}
		}
		if (victima.getMob() != null) {
			try {
				boolean esEstatico = false;
				for (int id : CentroInfo.INVOCACIONES_ESTATICAS)
					if (id == victima.getMob().getModelo().getID())
						esEstatico = true;
				if (victima.esInvocacion() && !esEstatico) {
					victima.getInvocador()._nroInvocaciones--;
					if (!_ordenJugadores.isEmpty()) {
						int index = _ordenJugadores.indexOf(victima);
						if (index != -1) {
							if (_nroOrdenLuc > index && _nroOrdenLuc > 0) {
								_nroOrdenLuc--;
							}
							_ordenJugadores.remove(index);
						}
						if (_nroOrdenLuc < 0) {
							System.out.println("index : " + index + " , victima id: " + victima.getID());
							System.out.println("_nroOrdenLuc : " + _nroOrdenLuc);
							return;
						}
						if (_equipo1.containsKey(idVictima))
							_equipo1.remove(idVictima);
						else if (_equipo2.containsKey(idVictima))
							_equipo2.remove(idVictima);
						GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 999, idVictima + "", stringOrdenJugadores());
						if (victima.puedeJugar() && asesino.getID() == idVictima) {
							_tempAccion = "";
							finTurno();
						}
						Thread.sleep(500);
					}
				}
			} catch (Exception e) {}
		}
		ArrayList<Glifo> glifos = new ArrayList<Glifo>();
		glifos.addAll(_glifos);
		for (Glifo glifo : glifos) {
			if (glifo.getLanzador().getID() == idVictima) {
				int celdaID = glifo.getCelda().getID();
				GestorSalida.ENVIAR_GDZ_ACTUALIZA_ZONA_EN_PELEA(this, 7, "-", celdaID, glifo.getTamaño(), 4);
				GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_EN_PELEA(this, 7, celdaID);
				_glifos.remove(glifo);
			}
		}
		ArrayList<Trampa> trampas = new ArrayList<Trampa>();
		trampas.addAll(_trampas);
		for (Trampa trampa : trampas) {
			if (trampa.getLanzador().getID() == idVictima) {
				trampa.desaparecer();
				_trampas.remove(trampa);
			}
		}
		if (_tipo == 5) {
			if (victima.esRecaudador()) {
				todosMuertosPara5y2(false);
				return;
			}
		} else if (_tipo == 2) {
			if (victima.esPrisma()) {
				todosMuertosPara5y2(false);
				return;
			}
		}
		if (victima.puedeJugar()) {
			if ( (victima.getMob() != null && !victima.esInvocacion()) || victima.getMob() == null) {
				try {
					Thread.sleep(300);
				} catch (Exception E) {}
				_tempAccion = "";
				finTurno();
			}
		}
		if (_muertesLuchInic1.size() == _cantLucEquipo1 || _muertesLuchInic2.size() == _cantLucEquipo2) {
			try {
				Thread.sleep(300);
			} catch (Exception E) {}
			verificaSiAcaboPelea();
		}
	}
	
	public boolean verificaSiAcaboPelea() {
		if (_estadoPelea == 4)
			return false;
		boolean equipo1Muerto = true;
		boolean equipo2Muerto = true;
		for (Entry<Integer, Luchador> entry : _equipo1.entrySet()) {
			if (entry.getValue().esInvocacion())
				continue;
			if (!entry.getValue()._estaMuerto) {
				equipo1Muerto = false;
				break;
			}
		}
		for (Entry<Integer, Luchador> entry : _equipo2.entrySet()) {
			if (entry.getValue().esInvocacion())
				continue;
			if (!entry.getValue()._estaMuerto) {
				equipo2Muerto = false;
				break;
			}
		}
		if (equipo1Muerto || equipo2Muerto || !verificaSiQuedaUno()) {
			_estadoPelea = 4;
			int equipoGanador = equipo1Muerto ? 2 : 1;
			for (Luchador luchador : luchadoresDeEquipo(7)) {
				Personaje perso = luchador.getPersonaje();
				if (perso != null) {
					if (luchador._desconectado) {
						luchador._estaRetirado = true;
						luchador._estaMuerto = true;
						perso.resetVariables();
						GestorSQL.SALVAR_PERSONAJE(perso, true);
						MundoDofus.desconectarPerso(perso);
					}
					perso.setPelea(null);
					perso.setOcupado(false);
					perso.setDueloID(-1);
					perso.setListo(false);
				}
			}
			_mapaReal.quitarPelea(_id);
			String packet = getPanelResultados(equipoGanador);
			try {
				Thread.sleep(1100 * (_cantUltAfec + 1));
			} catch (Exception e) {}
			GestorSalida.ENVIAR_GE_PANEL_RESULTADOS_PELEA(this, 7, packet);
			for (Personaje perso : _espectadores.values()) {
				perso.retornoMapa();
			}
			GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(_mapaReal);
			ArrayList<Luchador> ganadores = new ArrayList<Luchador>();
			ArrayList<Luchador> perdedores = new ArrayList<Luchador>();
			if (equipo1Muerto) {
				perdedores.addAll(_equipo1.values());
				ganadores.addAll(_equipo2.values());
			} else {
				ganadores.addAll(_equipo1.values());
				perdedores.addAll(_equipo2.values());
			}
			try {
				Thread.sleep(1500);
			} catch (Exception E) {}
			String str = "";
			if (_Recaudador != null)
				str = "S" + _Recaudador.getN1() + "," + _Recaudador.getN2() + "|.|" + _Recaudador.getMapaID() + "|"
						+ _Recaudador.getCeldalID();
			if (_Prisma != null)
				str = _Prisma.getMapa() + "|" + _Prisma.getX() + "|" + _Prisma.getY();
			for (Luchador ganador : ganadores) {
				if (ganador._recaudador != null) {
					for (Personaje pj : MundoDofus.getGremio(_gremioID).getPjMiembros()) {
						if (pj == null)
							continue;
						if (pj.enLinea()) {
							GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(pj, Recaudador.analizarRecaudadores(pj.getGremio().getID()));
							GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(pj, str);
						}
					}
					ganador._recaudador.setEstadoPelea(0);
					ganador._recaudador.setPeleaID(-1);
					ganador._recaudador.setPelea(null);
					for (Personaje z : MundoDofus.getMapa((short) ganador._recaudador.getMapaID()).getPersos()) {
						if (z == null || z.getCuenta().getEntradaPersonaje() == null)
							continue;
						PrintWriter out = z.getCuenta().getEntradaPersonaje().getOut();
						if (out == null)
							continue;
						GestorSalida.ENVIAR_GM_RECAUDADORES_EN_MAPA(out, z.getMapa());
					}
				} else if (ganador._prisma != null) {
					for (Personaje z : MundoDofus.getPJsEnLinea()) {
						if (z == null || z.getAlineacion() != _Prisma.getAlineacion())
							continue;
						GestorSalida.ENVIAR_CS_MENSAJE_SOBREVIVIO_PRISMA(z, str);
					}
					ganador._prisma.setEstadoPelea(-1);
					ganador._prisma.setPeleaID(-1);
					ganador._prisma.setPelea(null);
					for (Personaje z : MundoDofus.getMapa((short) ganador._prisma.getMapa()).getPersos()) {
						if (z == null || z.getCuenta().getEntradaPersonaje() == null)
							continue;
						PrintWriter out = z.getCuenta().getEntradaPersonaje().getOut();
						if (out == null)
							continue;
						GestorSalida.ENVIAR_GM_PRISMAS_EN_MAPA(out, z.getMapa());
					}
				}
				Personaje pjGanador = ganador.getPersonaje();
				if (ganador._estaRetirado || pjGanador == null || ganador.esInvocacion() || !pjGanador.enLinea())
					continue;
				if (_tipo == 5 && pjGanador != null) {
					if (_Recaudador != null)
						GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(pjGanador, getListaDefensores());
				} else if (_tipo == 2 && pjGanador != null) {
					if (_Prisma != null)
						GestorSalida.ENVIAR_CP_INFO_DEFENSORES_PRISMA(pjGanador, getListaDefensores());
				}
				if (_tipo != 0) {
					if (ganador.getPDV() < 1) {
						pjGanador.setPDV(1);
					} else {
						pjGanador.setPDV(ganador.getPDV());
					}
				}
				if ( (_tipo == 5 || _tipo == 2) && pjGanador.getMapaDefPerco() != null) {
					try {
						Thread.sleep(400);
					} catch (Exception E) {}
					pjGanador.retornoMapaDesPeleaRecau();
				} else if (_tipo == CentroInfo.PELEA_TIPO_COLISEO) {
					pjGanador.setEnKoliseo(false);
					pjGanador.retornoPtoSalvada();
				} else {
					pjGanador.retornoMapa();
				}
				try {
					Thread.sleep(500);
				} catch (Exception E) {}
				pjGanador.getMapa().aplicarAccionFinCombate(_tipo, pjGanador, _evento);
			}
			if (_Recaudador != null)
				str = "D" + _Recaudador.getN1() + "," + _Recaudador.getN2() + "|.|" + _Recaudador.getMapaID() + "|"
						+ _Recaudador.getCeldalID();
			for (Luchador perdedor : perdedores) {
				if (perdedor._recaudador != null) {
					for (Personaje z : MundoDofus.getGremio(_gremioID).getPjMiembros()) {
						if (z == null)
							continue;
						if (z.enLinea()) {
							GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z, Recaudador.analizarRecaudadores(z.getGremio().getID()));
							GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(z, str);
						}
					}
					_mapaReal.removeNPC(perdedor._recaudador.getID());
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_mapaReal, perdedor._recaudador.getID());
					_Recaudador.borrarRecaudador(perdedor._recaudador.getID());
				}
				if (perdedor._prisma != null) {
					SubArea subarea = _mapaReal.getSubArea();
					for (Personaje z : MundoDofus.getPJsEnLinea()) {
						if (z == null)
							continue;
						if (z.getAlineacion() == 0) {
							GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z, subarea.getID() + "|0|1");
							continue;
						}
						if (z.getAlineacion() == _Prisma.getAlineacion())
							GestorSalida.ENVIAR_CD_MENSAJE_MURIO_PRISMA(z, str);
						GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z, subarea.getID() + "|-1|0");
						GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z, subarea.getID() + "|0|1");
						if (_Prisma.getAreaConquistada() != -1) {
							GestorSalida.ENVIAR_aM_MENSAJE_ALINEACION_AREA(z, subarea.getArea().getID() + "|-1");
							subarea.getArea().setPrismaID(0);
							subarea.getArea().setAlineacion(0);
						}
					}
					int prismaID = perdedor._prisma.getID();
					subarea.setPrismaID(0);
					subarea.setAlineacion(0);
					_mapaReal.removeNPC(prismaID);
					GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_mapaReal, prismaID);
					MundoDofus.borrarPrisma(prismaID);
					GestorSQL.BORRAR_PRISMA(prismaID);
				}
				Personaje pjPerdedor = perdedor.getPersonaje();
				if (perdedor._estaRetirado || pjPerdedor == null || perdedor.esInvocacion() || !pjPerdedor.enLinea())
					continue;
				if (_tipo == 5 && pjPerdedor != null) {
					if (_Recaudador != null)
						GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(pjPerdedor, getListaDefensores());
				} else if (_tipo == 2 && pjPerdedor != null) {
					if (_Prisma != null)
						GestorSalida.ENVIAR_CP_INFO_DEFENSORES_PRISMA(pjPerdedor, getListaDefensores());
				}
				if (_tipo != 0) {
					try {
						Thread.sleep(700);
					} catch (Exception E) {}
					if (_tipo != CentroInfo.PELEA_TIPO_COLISEO) {
						int energiaAPerder = 5 * pjPerdedor.getNivel();
						if (_tipo == 5)
							energiaAPerder += 500;
						pjPerdedor.restarEnergia(energiaAPerder);
						GestorSalida.ENVIAR_Im_INFORMACION(pjPerdedor, "034;" + energiaAPerder);
					} else {
						pjPerdedor.setEnKoliseo(false);
					}
					if ( (_tipo == 5 || _tipo == 2) && pjPerdedor.getMapaDefPerco() != null) {
						try {
							Thread.sleep(400);
						} catch (Exception E) {}
						pjPerdedor.retornoPtoSalvadaRecau();
					} else {
						pjPerdedor.retornoPtoSalvada();
					}
					pjPerdedor.setPDV(1);
					pjPerdedor.restarVidaMascota(null);
				} else {
					pjPerdedor.retornoMapa();
				}
			}
			return false;
		}
		return true;
	}
	
	public void todosMuertosPara5y2(boolean equipo1Muerto) {
		if (_estadoPelea == 4)
			return;
		_estadoPelea = 4;
		int nroEquipoGanador = equipo1Muerto ? 2 : 1;
		_tiempoInicioTurno = 0;
		_nroOrdenLuc = -1;
		for (Luchador luchador : luchadoresDeEquipo(7)) {
			Personaje perso = luchador.getPersonaje();
			if (perso != null) {
				if (luchador._desconectado) {
					luchador._estaRetirado = true;
					luchador._estaMuerto = true;
					perso.resetVariables();
					GestorSQL.SALVAR_PERSONAJE(perso, true);
					MundoDofus.desconectarPerso(perso);
				}
				perso.setPelea(null);
				perso.setOcupado(false);
				perso.setDueloID(-1);
				perso.setListo(false);
			}
		}
		_mapaReal.quitarPelea(_id);
		String packet = getPanelResultados(nroEquipoGanador);
		try {
			Thread.sleep(1000 * (_cantUltAfec + 1));
		} catch (Exception e) {}
		GestorSalida.ENVIAR_GE_PANEL_RESULTADOS_PELEA(this, 7, packet);
		for (Personaje perso : _espectadores.values()) {
			perso.retornoMapa();
		}
		ArrayList<Luchador> ganadores = new ArrayList<Luchador>();
		ArrayList<Luchador> perdedores = new ArrayList<Luchador>();
		if (equipo1Muerto) {
			perdedores.addAll(_equipo1.values());
			ganadores.addAll(_equipo2.values());
		} else {
			ganadores.addAll(_equipo1.values());
			perdedores.addAll(_equipo2.values());
		}
		try {
			Thread.sleep(1500);
		} catch (Exception E) {}
		String str = "";
		if (_Recaudador != null)
			str = "S" + _Recaudador.getN1() + "," + _Recaudador.getN2() + "|.|" + _Recaudador.getMapaID() + "|"
					+ _Recaudador.getCeldalID();
		if (_Prisma != null)
			str = _Prisma.getMapa() + "|" + _Prisma.getX() + "|" + _Prisma.getY();
		for (Luchador ganador : ganadores) {
			if (ganador._recaudador != null) {
				for (Personaje z : MundoDofus.getGremio(_gremioID).getPjMiembros()) {
					if (z == null)
						continue;
					if (z.enLinea()) {
						GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z, Recaudador.analizarRecaudadores(z.getGremio().getID()));
						GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(z, str);
					}
				}
				ganador._recaudador.setEstadoPelea(0);
				ganador._recaudador.setPeleaID(-1);
				ganador._prisma.setPelea(null);
				for (Personaje z : MundoDofus.getMapa((short) ganador._recaudador.getMapaID()).getPersos()) {
					if (z == null || z.getCuenta().getEntradaPersonaje() == null)
						continue;
					PrintWriter out = z.getCuenta().getEntradaPersonaje().getOut();
					if (out == null)
						continue;
					GestorSalida.ENVIAR_GM_RECAUDADORES_EN_MAPA(out, z.getMapa());
				}
			}
			if (ganador._prisma != null) {
				for (Personaje z : MundoDofus.getPJsEnLinea()) {
					if (z == null)
						continue;
					if (z.getAlineacion() != _Prisma.getAlineacion())
						continue;
					GestorSalida.ENVIAR_CS_MENSAJE_SOBREVIVIO_PRISMA(z, str);
				}
				ganador._prisma.setEstadoPelea(-1);
				ganador._prisma.setPeleaID(-1);
				ganador._prisma.setPelea(null);
				for (Personaje z : MundoDofus.getMapa((short) ganador._prisma.getMapa()).getPersos()) {
					if (z == null || z.getCuenta().getEntradaPersonaje() == null)
						continue;
					PrintWriter out = z.getCuenta().getEntradaPersonaje().getOut();
					if (out == null)
						continue;
					GestorSalida.ENVIAR_GM_PRISMAS_EN_MAPA(out, z.getMapa());
				}
			}
			Personaje pjGanador = ganador.getPersonaje();
			if (ganador._estaRetirado || pjGanador == null || ganador.esInvocacion() || !pjGanador.enLinea())
				continue;
			if (_tipo == 5 && pjGanador != null) {
				if (_Recaudador != null)
					GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(pjGanador, getListaDefensores());
			}
			if (_tipo == 2 && pjGanador != null) {
				if (_Prisma != null)
					GestorSalida.ENVIAR_CP_INFO_DEFENSORES_PRISMA(pjGanador, getListaDefensores());
			}
			if (_tipo != 0) {
				if (ganador.getPDVConBuff() <= 0) {
					pjGanador.setPDV(1);
				} else {
					pjGanador.setPDV(ganador.getPDVConBuff());
				}
			}
			if ( (_tipo == 5 || _tipo == 2) && pjGanador.getMapaDefPerco() != null) {
				try {
					Thread.sleep(400);
				} catch (Exception E) {}
				pjGanador.retornoPtoSalvadaRecau();
			} else if (_tipo == CentroInfo.PELEA_TIPO_COLISEO) {
				pjGanador.setEnKoliseo(false);
				pjGanador.retornoPtoSalvada();
			} else {
				pjGanador.retornoMapa();
			}
			try {
				Thread.sleep(500);
			} catch (Exception E) {}
			pjGanador.getMapa().aplicarAccionFinCombate(_tipo, pjGanador, _evento);
		}
		if (_Recaudador != null)
			str = "D" + _Recaudador.getN1() + "," + _Recaudador.getN2() + "|.|" + _Recaudador.getMapaID() + "|"
					+ _Recaudador.getCeldalID();
		for (Luchador perdedor : perdedores) {
			if (perdedor._recaudador != null) {
				for (Personaje z : MundoDofus.getGremio(_gremioID).getPjMiembros()) {
					if (z == null)
						continue;
					if (z.enLinea()) {
						GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z, Recaudador.analizarRecaudadores(z.getGremio().getID()));
						GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(z, str);
					}
				}
				_mapaReal.removeNPC(perdedor._recaudador.getID());
				GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_mapaReal, perdedor._recaudador.getID());
				_Recaudador.borrarRecaudador(perdedor._recaudador.getID());
			}
			if (perdedor._prisma != null) {
				SubArea subarea = _mapaReal.getSubArea();
				for (Personaje z : MundoDofus.getPJsEnLinea()) {
					if (z == null)
						continue;
					if (z.getAlineacion() == 0) {
						GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z, subarea.getID() + "|-1|1");
						continue;
					}
					if (z.getAlineacion() == _Prisma.getAlineacion())
						GestorSalida.ENVIAR_CD_MENSAJE_MURIO_PRISMA(z, str);
					GestorSalida.ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(z, subarea.getID() + "|-1|0");
					if (_Prisma.getAreaConquistada() != -1) {
						GestorSalida.ENVIAR_aM_MENSAJE_ALINEACION_AREA(z, subarea.getArea().getID() + "|-1");
						subarea.getArea().setPrismaID(0);
						subarea.getArea().setAlineacion(0);
					}
				}
				int prismaID = perdedor._prisma.getID();
				subarea.setPrismaID(0);
				subarea.setAlineacion(0);
				_mapaReal.removeNPC(prismaID);
				GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_mapaReal, prismaID);
				MundoDofus.borrarPrisma(prismaID);
				GestorSQL.BORRAR_PRISMA(prismaID);
			}
			Personaje pjPerdedor = perdedor.getPersonaje();
			if (perdedor._estaRetirado || pjPerdedor == null || perdedor.esInvocacion() || !pjPerdedor.enLinea())
				continue;
			if (_tipo == 5 && pjPerdedor != null) {
				if (_Recaudador != null)
					GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(pjPerdedor, getListaDefensores());
			} else if (_tipo == 2 && pjPerdedor != null) {
				if (_Prisma != null)
					GestorSalida.ENVIAR_CP_INFO_DEFENSORES_PRISMA(pjPerdedor, getListaDefensores());
			}
			if (_tipo != 0) {
				try {
					Thread.sleep(700);
				} catch (Exception E) {}
				if (_tipo != CentroInfo.PELEA_TIPO_COLISEO) {
					int energiaAPerder = 5 * pjPerdedor.getNivel();
					if (_tipo == 5)
						energiaAPerder += 500;
					pjPerdedor.restarEnergia(energiaAPerder);
					GestorSalida.ENVIAR_Im_INFORMACION(pjPerdedor, "034;" + energiaAPerder);
				} else {
					pjPerdedor.setEnKoliseo(false);
				}
				if ( (_tipo == 5 || _tipo == 2) && pjPerdedor.getMapaDefPerco() != null) {
					try {
						Thread.sleep(400);
					} catch (Exception E) {}
					pjPerdedor.retornoPtoSalvadaRecau();
				} else
					pjPerdedor.retornoPtoSalvada();
				pjPerdedor.setPDV(1);
				pjPerdedor.restarVidaMascota(null);
			} else {
				pjPerdedor.retornoMapa();
			}
		}
	}
	
	public int getParamEquipo(int id) {
		if (_equipo1.containsKey(id))
			return 1;
		if (_equipo2.containsKey(id))
			return 2;
		if (_espectadores.containsKey(id))
			return 4;
		return -1;
	}
	
	public int getIDEquipoEnemigo(int id) {
		if (_equipo1.containsKey(id))
			return 2;
		if (_equipo2.containsKey(id))
			return 1;
		return -1;
	}
	
	public Luchador getLuchadorPorPJ(Personaje perso) {
		Luchador luchador = null;
		if (_equipo1.get(perso.getID()) != null)
			luchador = _equipo1.get(perso.getID());
		else if (_equipo2.get(perso.getID()) != null)
			luchador = _equipo2.get(perso.getID());
		return luchador;
	}
	
	public Luchador getLuchadorTurno() {
		return _ordenJugadores.get(_nroOrdenLuc);
	}
	
	public void actualizarInfoJugadores() {
		Stats statConBuff = _ordenJugadores.get(_nroOrdenLuc).getTotalStatsConBuff();
		_tempLuchadorPA = statConBuff.getEfecto(CentroInfo.STATS_ADD_PA) - _tempLuchadorPAusados;
		if (_tempLuchadorPA < 0)
			_tempLuchadorPA = 0;
		_tempLuchadorPM = statConBuff.getEfecto(CentroInfo.STATS_ADD_PM) - _tempLuchadorPMusados;
		if (_tempLuchadorPM < 0)
			_tempLuchadorPM = 0;
	}
	
	public void retirarsePelea(Personaje retirador, Personaje expulsado) {// jugador marchado
		if (retirador == null || _ordenJugadores == null || _nroOrdenLuc < 0)
			return;
		if (_nroOrdenLuc >= _ordenJugadores.size())
			_nroOrdenLuc = 0;
		_cantUltAfec = 1;
		Luchador lucRetirador = getLuchadorPorPJ(retirador);
		Luchador lucExpulsado = null;
		if (expulsado != null) {
			lucExpulsado = getLuchadorPorPJ(expulsado);
		}
		if (lucRetirador != null) {
			switch (_tipo) {
				case 0:// Desafio
				case 1:// PVP
				case 2: // prisma
				case 3: // templo dopeul
				case 4:// PVM
				case 5:// Recaudador
				case CentroInfo.PELEA_TIPO_COLISEO:
					if (_estadoPelea == 3) {
						if (lucRetirador.puedeJugar() && cuantosQuedanDelEquipo(lucRetirador.getID()) > 1) {
							finTurno();
						}
						agregarAMuertos(lucRetirador);
						if (_estadoPelea == 4)
							return;
						GestorSalida.ENVIAR_GM_BORRAR_PJ_A_TODOS(_mapaCopia, lucRetirador.getID());
						if (_tipo != 0 && _tipo != CentroInfo.PELEA_TIPO_COLISEO) {
							retirador.restarEnergia(1500);
							if (retirador.enLinea())
								GestorSalida.ENVIAR_Im_INFORMACION(retirador, "034;" + 1500);
							if (_tipo == 1 || _tipo == 2) {
								retirador.addHonor(-500);
								if (retirador.enLinea())
									GestorSalida.ENVIAR_Im_INFORMACION(retirador, "076;" + (-500));
								retirador.retornoPtoSalvadaRecau();
							} else {
								retirador.retornoPtoSalvada();
							}
							retirador.setPDV(1);
							retirador.restarVidaMascota(null);
						} else if (_tipo == CentroInfo.PELEA_TIPO_COLISEO) {
							retirador.retornoPtoSalvada();
							retirador.setPDV(1);
							retirador.restarVidaMascota(null);
							retirador.setEnKoliseo(false);
						} else if (retirador.enLinea()) {
							retirador.retornoMapa();
						}
						lucRetirador._estaRetirado = true;
					} else if (_estadoPelea == 2) {
						boolean puedeExpulsar = false;
						if (_tipo == 4 || _tipo == 3) {
							if (_luchInit1 != null && _luchInit1.getPersonaje() != null) {
								if (retirador.getID() != _luchInit1.getPersonaje().getID()) {
									return;
								}
							}
						}
						if (_luchInit1 != null && _luchInit1.getPersonaje() != null) {
							if (retirador.getID() == _luchInit1.getPersonaje().getID()) {
								puedeExpulsar = true;
							}
						}
						if (_luchInit2 != null && _luchInit2.getPersonaje() != null) {
							if (retirador.getID() == _luchInit2.getPersonaje().getID()) {
								puedeExpulsar = true;
							}
						}
						if (lucExpulsado != null && puedeExpulsar) {// si expulsa a otro jugador
							int idLucExpuls = lucExpulsado.getID();
							if (lucExpulsado.getEquipoBin() == lucRetirador.getEquipoBin()) {
								if (idLucExpuls != lucRetirador.getID()) {
									GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, expulsado.getID(), 3);
									if (_tipo != 0)
										GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, expulsado.getID(), 3);
									if (expulsado.enLinea()) {
										expulsado.retornoMapa();
									}
									if (_equipo1.containsKey(lucExpulsado.getID())) {
										lucExpulsado._celda.removerLuchador(lucExpulsado);
										_equipo1.remove(lucExpulsado.getID());
									} else if (_equipo2.containsKey(lucExpulsado.getID())) {
										lucExpulsado._celda.removerLuchador(lucExpulsado);
										_equipo2.remove(lucExpulsado.getID());
									}
									GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(_mapaReal,
											_equipo1.containsKey(lucExpulsado.getID()) ? _idLuchInit1 : _idLuchInit2,
											lucExpulsado);
								}
							}
						} else if (puedeExpulsar && _tipo != CentroInfo.PELEA_TIPO_COLISEO) {// se expulsa asi mismo
							_tiempoInicioTurno = 0;
							for (Luchador luch : luchadoresDeEquipo(lucRetirador.getParamEquipoAliado())) {
								Personaje perso = luch.getPersonaje();
								if (retirador.getID() != perso.getID() || _tipo == 0) {// si es desafio
									if (perso.enLinea()) {
										GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(perso);
										perso.retornoMapa();
									}
								} else {// sino el lider se ve afectado
									perso.restarEnergia(1500);
									if (perso.enLinea())
										GestorSalida.ENVIAR_Im_INFORMACION(perso, "034;" + 1500);
									if (_tipo == 1 || _tipo == 2) {
										perso.addHonor(-500);
										if (perso.enLinea())
											GestorSalida.ENVIAR_Im_INFORMACION(perso, "076;" + (-500));
									}
									if (_tipo == 5 || _tipo == 2) {
										perso.retornoPtoSalvadaRecau();
									} else {
										perso.retornoPtoSalvada();
									}
									perso.restarVidaMascota(null);
									perso.setPDV(1);
								}
							}
							if (_tipo != 4) {// para el enemigo restaura puntos
								for (Luchador luch : luchadoresDeEquipo(lucRetirador.getParamEquipoEnemigo())) {
									Personaje perso = luch.getPersonaje();
									if (perso == null)
										continue;
									if (perso.enLinea()) {
										GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(perso);
										perso.retornoMapa();
									}
								}
							}
							_estadoPelea = 4;
							_mapaReal.quitarPelea(_id);
							GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(_mapaReal);
							GestorSalida.ENVIAR_Gc_BORRAR_BANDERA_EN_MAPA(_mapaReal, _idLuchInit1);
							if (_tipo == 5) {
								String str = "S" + _Recaudador.getN1() + "," + _Recaudador.getN2() + "|.|"
										+ _Recaudador.getMapaID() + "|" + _Recaudador.getCeldalID();
								for (Personaje z : MundoDofus.getGremio(_gremioID).getPjMiembros()) {
									if (z == null)
										continue;
									if (z.enLinea()) {
										GestorSalida.ENVIAR_gITM_INFO_RECAUDADOR(z,
												Recaudador.analizarRecaudadores(z.getGremio().getID()));
										GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(z, str);
									}
								}
								if (retirador != null) {
									if (_Recaudador != null)
										GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(retirador, getListaDefensores());
								}
								_Recaudador.setEstadoPelea(0);
								_Recaudador.setPeleaID(-1);
								_Recaudador.setPelea(null);
								for (Personaje z : MundoDofus.getMapa((short) _Recaudador.getMapaID()).getPersos()) {
									if (z == null || z.getCuenta().getEntradaPersonaje() == null)
										continue;
									PrintWriter out = z.getCuenta().getEntradaPersonaje().getOut();
									if (out == null)
										continue;
									GestorSalida.ENVIAR_GM_RECAUDADORES_EN_MAPA(out, z.getMapa());
								}
							}
							if (_tipo == 2) {
								String str = _Prisma.getMapa() + "|" + _Prisma.getX() + "|" + _Prisma.getY();
								for (Personaje z : MundoDofus.getPJsEnLinea()) {
									if (z == null || z.getAlineacion() != _Prisma.getAlineacion())
										continue;
									GestorSalida.ENVIAR_CS_MENSAJE_SOBREVIVIO_PRISMA(z, str);
								}
								_Prisma.setEstadoPelea(-1);
								_Prisma.setPeleaID(-1);
								_Prisma.setPelea(null);
								if (retirador != null) {
									if (_Prisma != null)
										GestorSalida.ENVIAR_CP_INFO_DEFENSORES_PRISMA(retirador, getListaDefensores());
								}
								for (Personaje z : MundoDofus.getMapa((short) _Prisma.getMapa()).getPersos()) {
									if (z == null || z.getCuenta().getEntradaPersonaje() == null)
										continue;
									PrintWriter out = z.getCuenta().getEntradaPersonaje().getOut();
									if (out == null)
										continue;
									GestorSalida.ENVIAR_GM_PRISMAS_EN_MAPA(out, z.getMapa());
								}
							}
							if (_tipo == 4) {
								int alineacion = -1;
								if (_equipo2.size() > 0) {
									_equipo2.get(_equipo2.keySet().toArray()[0]).getMob().getModelo().getAlineacion();
								}
								if (!_mobGrupo.esFixeado())
									_mapaReal.spawnGrupo(alineacion, 1, true, _mobGrupo.getCeldaID());
							}
						} else {// si se retira asi mismo y no es lider
							GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, retirador.getID(), 3);
							if (_tipo != 0) {
								if (_tipo != CentroInfo.PELEA_TIPO_COLISEO) {
									retirador.restarEnergia(1500);
									if (retirador.enLinea())
										GestorSalida.ENVIAR_Im_INFORMACION(retirador, "034;" + 1500);
								} else {
									retirador.setEnKoliseo(false);
								}
								if (_tipo == 1 || _tipo == 2) {
									retirador.addHonor(-500);
									if (retirador.enLinea())
										GestorSalida.ENVIAR_Im_INFORMACION(retirador, "076;" + (-500));
								}
								if (_tipo == 5 || _tipo == 2) {
									retirador.retornoPtoSalvadaRecau();
								} else {
									retirador.retornoPtoSalvada();
								}
								retirador.setPDV(1);
								retirador.restarVidaMascota(null);
							} else if (retirador.enLinea()) {
								GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(retirador);
								retirador.retornoMapa();
							}
							if (_equipo1.containsKey(lucRetirador.getID())) {
								lucRetirador._celda.removerLuchador(lucRetirador);
								_equipo1.remove(lucRetirador.getID());
							} else if (_equipo2.containsKey(lucRetirador.getID())) {
								lucRetirador._celda.removerLuchador(lucRetirador);
								_equipo2.remove(lucRetirador.getID());
							}
							GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(_mapaReal,
									_equipo1.containsKey(lucRetirador.getID()) ? _idLuchInit1 : _idLuchInit2, lucRetirador);
						}
					} else {
						System.out.println("ERROR, estado de combate: " + _estadoPelea + " tipo de combate:" + _tipo
								+ " LuchadorExp:" + lucExpulsado + " LuchadorRet:" + lucRetirador);
					}
					break;
				default:
					System.out.println("Tipo de combate no generado, tipo de combate:" + _tipo + " LuchadorExp:" + lucExpulsado
							+ " LuchadorRet:" + lucRetirador);
					break;
			}
		} else {
			_espectadores.remove(retirador.getID());
			GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(retirador);
			retirador.retornoMapa();
		}
	}
	
	public String stringOrdenJugadores() {
		String packet = "GTL";
		for (Luchador luchador : _ordenJugadores) {
			packet += "|" + luchador.getID();
		}
		return packet + (char) 0x00;
	}
	
	public int getSigIDLuchador() {
		int g = -1;
		for (Luchador luchador : luchadoresDeEquipo(3)) {
			if (luchador.getID() < g)
				g = luchador.getID();
		}
		_numeroInvos++;
		g--;
		return g;
	}
	
	public void addLuchadorEnEquipo(Luchador luchador, int equipo) {
		if (equipo == 0)
			_equipo1.put(luchador.getID(), luchador);
		else if (equipo == 1)
			_equipo2.put(luchador.getID(), luchador);
	}
	
	public String infoPeleasEnMapa() {
		if (_estadoPelea >= 4) {
			_mapaReal.quitarPelea(_id);
			return "";
		}
		String infos = _id + ";";
		Date actDate = new Date();
		long tiempo = (actDate.getTime() + 0x36ee80L) - (System.currentTimeMillis() - _tiempoInicio);
		infos += (_tiempoInicio == 0 ? "-1" : tiempo) + ";";
		int jugEquipo0 = 0;
		int jugEquipo1 = 0;
		for (Luchador l : _equipo1.values()) {
			if (l == null)
				continue;
			if (l.esInvocacion())
				continue;
			jugEquipo0++;
		}
		for (Luchador l : _equipo2.values()) {
			if (l == null)
				continue;
			if (l.esInvocacion())
				continue;
			jugEquipo1++;
		}
		infos += "0,";
		switch (_tipo) {
			case 0:
				infos += "0,";
				infos += jugEquipo0 + ";";
				infos += "0,";
				infos += "0,";
				infos += jugEquipo1 + ";";
				break;
			case 1:
				infos += _luchInit1.getPersonaje().getAlineacion() + ",";
				infos += jugEquipo0 + ";";
				infos += "0,";
				infos += _luchInit2.getPersonaje().getAlineacion() + ",";
				infos += jugEquipo1 + ";";
				break;
			case 2:
				infos += _luchInit1.getPersonaje().getAlineacion() + ",";
				infos += jugEquipo0 + ";";
				infos += "0,";
				infos += _Prisma.getAlineacion() + ",";
				infos += jugEquipo1 + ";";
				break;
			case 3:
				infos += "0,";
				infos += jugEquipo0 + ";";
				infos += "1,";
				infos += _equipo2.get(_equipo2.keySet().toArray()[0]).getMob().getModelo().getAlineacion() + ",";
				infos += jugEquipo1 + ";";
				break;
			case 4:
				infos += "0,";
				infos += jugEquipo0 + ";";
				infos += "1,";
				infos += _equipo2.get(_equipo2.keySet().toArray()[0]).getMob().getModelo().getAlineacion() + ",";
				infos += jugEquipo1 + ";";
				break;
			case 5:
				infos += "0,";
				infos += jugEquipo0 + ";";
				infos += "3,";
				infos += "0,";
				infos += jugEquipo1 + ";";
				break;
		}
		return infos;
	}
	
	public boolean verificaSiQuedaUno() {
		for (Luchador luchador : _equipo1.values()) {
			if (luchador._estaMuerto || luchador.esInvocacion())
				continue;
			return true;
		}
		for (Luchador luchador : _equipo2.values()) {
			if (luchador._estaMuerto || luchador.esInvocacion())
				continue;
			return true;
		}
		return false;
	}
	
	public int cuantosQuedanDelEquipo(int id) {
		int num = 0;
		if (_equipo1.containsKey(id))
			for (Luchador luchador : _equipo1.values()) {
				if (luchador._estaMuerto || luchador.esInvocacion())
					continue;
				num++;
			}
		else if (_equipo2.containsKey(id))
			for (Luchador luchador : _equipo2.values()) {
				if (luchador._estaMuerto || luchador.esInvocacion())
					continue;
				num++;
			}
		return num;
	}
	
	public boolean acabaPeleaSiSeVa(int id) {
		boolean acaba = true;
		if (_equipo1.containsKey(id))
			for (Luchador luchador : _equipo1.values()) {
				if (luchador._estaMuerto || luchador.esInvocacion() || luchador._id == id)
					continue;
				acaba = false;
			}
		else if (_equipo2.containsKey(id))
			for (Luchador luchador : _equipo2.values()) {
				if (luchador._estaMuerto || luchador.esInvocacion() || luchador._id == id)
					continue;
				acaba = false;
			}
		else if (_espectadores.containsKey(id))
			return true;
		return acaba;
	}
	
	public static void agregarEspadaDePelea(Mapa mapa, Personaje perso) {
		for (Entry<Integer, Pelea> peleas : mapa.getPeleas().entrySet()) {
			Pelea pelea = peleas.getValue();
			if (pelea._estadoPelea == 2) {
				Personaje persoInit1 = pelea._luchInit1.getPersonaje();
				int id1 = pelea._idLuchInit1;
				int id2 = pelea._idLuchInit2;
				String enviar1 = "";
				String enviar2 = "";
				boolean primero1 = true;
				boolean primero2 = true;
				Personaje persoInit2 = null;
				switch (pelea._tipo) {
					case 0:
						persoInit2 = pelea._luchInit2.getPersonaje();
						if (persoInit2 == null)
							continue;
						GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(perso, 0, id1, id2, persoInit1.getCelda().getID(),
								"0;-1", persoInit2.getCelda().getID(), "0;-1");
						break;
					case 1:
						persoInit2 = pelea._luchInit2.getPersonaje();
						if (persoInit2 == null)
							continue;
						GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(perso, 0, id1, id2, persoInit1.getCelda().getID(), "0;"
								+ persoInit1.getAlineacion(), persoInit2.getCelda().getID(), "0;" + persoInit2.getAlineacion());
						break;
					case 2:
						GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(perso, 0, id1, pelea._Prisma.getID(), persoInit1
								.getCelda().getID(), "0;" + persoInit1.getAlineacion(), pelea._Prisma.getCelda(), "0;"
								+ pelea._Prisma.getAlineacion());
						break;
					case 4:
						GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(perso, 4, id1, pelea._mobGrupo.getID(), persoInit1
								.getCelda().getID(), "0;-1", pelea._mobGrupo.getCeldaID() - 1, "1;-1");
						break;
					case 5:
						GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(perso, 5, id1, pelea._Recaudador.getID(), persoInit1
								.getCelda().getID(), "0;-1", pelea._Recaudador.getCeldalID(), "3;-1");
						break;
				}
				for (Entry<Integer, Luchador> entry : pelea._equipo1.entrySet()) {
					Luchador luchador = entry.getValue();
					if (!primero1)
						enviar1 += "|+";
					enviar1 += luchador.getID() + ";" + luchador.getNombreLuchador() + ";" + luchador.getNivel();
					primero1 = false;
				}
				GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso, id1, enviar1);
				for (Entry<Integer, Luchador> entry : pelea._equipo2.entrySet()) {
					Luchador luchador = entry.getValue();
					if (!primero2)
						enviar2 += "|+";
					enviar2 += luchador.getID() + ";" + luchador.getNombreLuchador() + ";" + luchador.getNivel();
					primero2 = false;
				}
				GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso, id2, enviar2);
			}
		}
	}
	
	public static int getPeleaIDPorLuchador(Mapa mapa, int id) {
		for (Entry<Integer, Pelea> pelea : mapa.getPeleas().entrySet()) {
			for (Entry<Integer, Luchador> F : pelea.getValue()._equipo1.entrySet()) {
				if (F.getValue().getPersonaje() != null && F.getValue().getID() == id) {
					return pelea.getValue().getID();
				}
			}
		}
		return 0;
	}
	
	public Map<Integer, Luchador> getListaMuertos() {
		return _listaMuertos;
	}
	
	public void borrarUnMuerto(Luchador objetivo) {
		_listaMuertos.remove(objetivo.getID());
	}
}
