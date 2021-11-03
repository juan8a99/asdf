
package variables;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import variables.Objeto.ObjetoModelo;

import estaticos.Bustemu;
import estaticos.MundoDofus;
import estaticos.MundoDofus.Duo;

public class Mascota {
	private int _objID;
	private int _nroComidas;
	private int _pdv;
	private String _stringStats;
	private String _fechaUltComida;
	private int _año;
	private int _mes;
	private int _dia;
	private int _hora;
	private int _minuto;
	private MascotaModelo _mascModelo;
	private int _idModelo;
	private boolean _esDevoraAlmas;
	private int _ultimaComida;
	private String _almasDevoradas;
	private int _obeso;
	private int _delgado;
	public static class Comida {
		private int _idComida;
		private int _cant;
		private int _idStat;
		
		public Comida(int idModelo, int cant, int idStat) {
			_idComida = idModelo;
			_cant = cant;
			_idStat = idStat;
		}
		
		public int getIDComida() {
			return _idComida;
		}
		
		public int getCantidad() {
			return _cant;
		}
		
		public int getIDStat() {
			return _idStat;
		}
	}
	public static class MascotaModelo {
		private int _maxStats;
		private ArrayList<Duo<Integer, Integer>> _statsPorEfecto = new ArrayList<Duo<Integer, Integer>>();
		private ArrayList<Comida> _comidas = new ArrayList<Comida>();
		private boolean _esDevorador;
		
		public MascotaModelo(int maxStas, String statsPorEfecto, String comidas, int devorador) {
			_maxStats = maxStas;
			if (!comidas.isEmpty()) {
				for (String comida : comidas.split("\\|")) {
					if (comida.isEmpty())
						continue;
					String[] str = comida.split(";");
					try {
						Comida comi = new Comida(Integer.parseInt(str[0]), Integer.parseInt(str[1]), Integer.parseInt(str[2]));
						_comidas.add(comi);
					} catch (Exception e) {
						continue;
					}
				}
			}
			String[] stats = statsPorEfecto.split("\\|");
			for (String s : stats) {
				try {
					_statsPorEfecto.add(new Duo<Integer, Integer>(Integer.parseInt(s.split(";")[0]), Integer.parseInt(s
							.split(";")[1])));
				} catch (Exception e) {
					continue;
				}
			}
			if (devorador == 1)
				_esDevorador = true;
			else
				_esDevorador = false;
		}
		
		public int getMaxStats() {
			return _maxStats;
		}
		
		public int getStatsPorEfecto(int stat) {
			for (Duo<Integer, Integer> duo : _statsPorEfecto) {
				if (duo._primero == stat) {
					return duo._segundo;
				}
			}
			return 0;
		}
	}
	
	public Mascota(int objetoID, int PDV, String stats, int nrComidas, int año, int mes, int dia, int hora, int minuto,
			int ultComida, String almasDevoradas, int obeso, int delgado, int idModelo) {
		_idModelo = idModelo;
		_objID = objetoID;
		_nroComidas = nrComidas;
		_pdv = PDV;
		_stringStats = stats;
		_año = año;
		_mes = mes;
		_dia = dia;
		_hora = hora;
		_minuto = minuto;
		_almasDevoradas = almasDevoradas;
		_ultimaComida = ultComida;
		_obeso = obeso;
		_delgado = delgado;
		_fechaUltComida = "328#" + Integer.toHexString(_año) + "#" + Integer.toHexString( (_mes - 1) * 100 + _dia) + "#"
				+ Integer.toHexString(_hora * 100 + _minuto);
		_mascModelo = MundoDofus.getMascotaModelo(_idModelo);
		_esDevoraAlmas = _mascModelo._esDevorador;
	}
	
	public int getUltimaComida() {
		return _ultimaComida;
	}
	
	public int getIDModelo() {
		return _idModelo;
	}
	
	public int getID() {
		return _objID;
	}
	
	public int getAño() {
		return _año;
	}
	
	public int getMes() {
		return _mes;
	}
	
	public int getDia() {
		return _dia;
	}
	
	public int getHora() {
		return _hora;
	}
	
	public int getMinuto() {
		return _minuto;
	}
	
	public int getNroComidas() {
		return _nroComidas;
	}
	
	public int getPDV() {
		return _pdv;
	}
	
	public void setPDV(int pdv) {
		_pdv = pdv;
	}
	
	public int getMinutosDia() {
		int total = 0;
		total += ( (_mes - 1) * 43200);
		total += ( (_dia - 1) * 1440);
		total += (_hora * 60);
		total += _minuto;
		return total;
	}
	
	public void setFecha(int Año, int Mes, int Dia, int Hora, int Minuto) {
		_año = Año;
		_mes = Mes;
		_dia = Dia;
		_hora = Hora;
		_minuto = Minuto;
	}
	
	public boolean esDevoraAlmas() {
		return _esDevoraAlmas;
	}
	
	public String getStringStats() {
		return _stringStats;
	}
	
	public String getAlmasDevoradas() {
		return _almasDevoradas;
	}
	
	public boolean esComestible(int idModComida) {
		if (idModComida == 11045) {
			return true;
		}
		ArrayList<Comida> comidas = _mascModelo._comidas;
		for (Comida comi : comidas) {
			if (comi.getIDComida() == idModComida)
				return true;
		}
		return false;
	}
	
	public boolean getDelgado() {
		return _delgado == 7;
	}
	
	public boolean getObeso() {
		return _obeso == 7;
	}
	
	public void setCorpulencia(int numero) {
		if (numero == 0) {
			_obeso = 0;
			_delgado = 0;
		} else if (numero == 1) {
			_obeso = 7;
			_delgado = 0;
		} else if (numero == 2) {
			_obeso = 0;
			_delgado = 7;
		}
	}
	
	public void comerAlma(int idModeloMob, int cantAlmasDevor) {
		Comida comida = null;
		for (Comida comi : _mascModelo._comidas) {
			if (comi.getIDComida() == idModeloMob) {
				comida = comi;
				break;
			}
		}
		if (comida == null)
			return;
		int cantNecesaria = comida.getCantidad();
		int valorTemp = 0;
		Map<Integer, Integer> almas = new TreeMap<Integer, Integer>();
		if (!_almasDevoradas.isEmpty()) {
			for (String stati : _almasDevoradas.split(";")) {
				int statID = Integer.parseInt(stati.split(",")[0]);
				int valor = Integer.parseInt(stati.split(",")[1]);
				almas.put(statID, valor);
			}
		}
		if (almas.containsKey(idModeloMob)) {
			valorTemp = almas.get(idModeloMob);
			int nuevo = valorTemp + cantAlmasDevor;
			almas.remove(idModeloMob);
			almas.put(idModeloMob, nuevo);
		} else {
			almas.put(idModeloMob, cantAlmasDevor);
		}
		int valorNuevo = valorTemp + cantAlmasDevor;
		String almasDevoradas = "";
		boolean primero = true;
		if (almas.size() != 0) {
			for (Entry<Integer, Integer> entry : almas.entrySet()) {
				if (!primero)
					almasDevoradas += ";";
				almasDevoradas += entry.getKey() + "," + entry.getValue();
				primero = false;
			}
			_almasDevoradas = almasDevoradas;
		}
		int efecto = comida.getIDStat();
		int maximo = 0;
		int maxPorStat = 0;
		if (!_stringStats.isEmpty()) {
			String[] stat = _stringStats.split(",");
			for (String Stat : stat) {
				String[] a = Stat.split("#");
				int statId = Integer.parseInt(a[0], 16);
				if (statId == efecto) {
					maxPorStat = Integer.parseInt(a[4].split("\\+")[1]);
					maximo += maxPorStat;
				} else if (statId >= 210 && statId <= 214)
					maximo += (Integer.parseInt(a[4].split("\\+")[1]) * 6);
				else
					maximo += Integer.parseInt(a[4].split("\\+")[1]);
			}
		}
		boolean puedeAumentar = maxPorStat < _mascModelo.getStatsPorEfecto(efecto);
		boolean puede = maximo < _mascModelo._maxStats;
		if (!puedeAumentar || !puede) {
			return;
		}
		if ( (valorNuevo / cantNecesaria) > (valorTemp / cantNecesaria)) {
			Map<Integer, Integer> stasitos = new TreeMap<Integer, Integer>();
			if (!_stringStats.isEmpty()) {
				for (String stati : _stringStats.split(",")) {
					int statID = Integer.parseInt(stati.split("#")[0], 16);
					int valor = Integer.parseInt(stati.split("#")[4].split("\\+")[1]);
					stasitos.put(statID, valor);
				}
			}
			if (stasitos.containsKey(efecto)) {
				int nuevo = stasitos.get(efecto);
				if (efecto == 158)
					nuevo += 10;
				else
					nuevo += 1;
				stasitos.remove(efecto);
				stasitos.put(efecto, nuevo);
			} else {
				if (efecto == 158)
					stasitos.put(efecto, 10);
				else
					stasitos.put(efecto, 1);
			}
			String statsfinal = "";
			boolean esPrimero = true;
			if (stasitos.size() != 0) {
				for (Entry<Integer, Integer> entry : stasitos.entrySet()) {
					if (!esPrimero)
						statsfinal += ",";
					statsfinal += Integer.toHexString(entry.getKey()) + "#" + Integer.toHexString(entry.getValue()) + "#0#0#0d0+"
							+ entry.getValue();
					esPrimero = false;
				}
				_stringStats = statsfinal;
			}
		}
	}
	
	public void comerComida(int idModComida) {
		_nroComidas++;
		_ultimaComida = idModComida;
		if (_nroComidas == 3) {
			_nroComidas = 0;
			Comida comida = null;
			for (Comida comi : _mascModelo._comidas) {
				if (comi.getIDComida() < 0) {
					ObjetoModelo objMod = MundoDofus.getObjModelo(idModComida);
					if (Math.abs(comi.getIDComida()) == objMod.getTipo()) {
						comida = comi;
						break;
					}
				} else {
					if (comi.getIDComida() == idModComida) {
						comida = comi;
						break;
					}
				}
			}
			if (comida == null)
				return;
			int efecto = comida.getIDStat();
			int maximo = 0;
			int maxPorStat = 0;
			if (!_stringStats.isEmpty()) {
				String[] stat = _stringStats.split(",");
				for (String Stat : stat) {
					String[] a = Stat.split("#");
					int statId = Integer.parseInt(a[0], 16);
					if (statId == efecto) {// iniciativa
						maxPorStat = Integer.parseInt(a[4].split("\\+")[1]);
						maximo += maxPorStat;
					} else if (statId >= 210 && statId <= 214)
						maximo += (Integer.parseInt(a[4].split("\\+")[1]) * 6);
					else
						maximo += Integer.parseInt(a[4].split("\\+")[1]);
				}
			}
			boolean puedeAumentar = maxPorStat < _mascModelo.getStatsPorEfecto(efecto);
			boolean puede = maximo < _mascModelo._maxStats;
			if (!puedeAumentar || !puede) {
				return;
			}
			Map<Integer, Integer> stasitos = new TreeMap<Integer, Integer>();
			if (!_stringStats.isEmpty()) {
				for (String stati : _stringStats.split(",")) {
					int statID = Integer.parseInt(stati.split("#")[0], 16);
					int valor = Integer.parseInt(stati.split("#")[4].split("\\+")[1]);
					stasitos.put(statID, valor);
				}
			}
			if (stasitos.containsKey(efecto)) {
				int nuevo = stasitos.get(efecto);
				if (efecto == 158)
					nuevo += 10;
				else
					nuevo += 1;
				stasitos.remove(efecto);
				stasitos.put(efecto, nuevo);
			} else {
				if (efecto == 158)
					stasitos.put(efecto, 10);
				else
					stasitos.put(efecto, 1);
			}
			String statsfinal = "";
			boolean esPrimero = true;
			if (stasitos.size() != 0) {
				for (Entry<Integer, Integer> entry : stasitos.entrySet()) {
					if (!esPrimero)
						statsfinal += ",";
					statsfinal += Integer.toHexString(entry.getKey()) + "#" + Integer.toHexString(entry.getValue()) + "#0#0#0d0+"
							+ entry.getValue();
					esPrimero = false;
				}
				_stringStats = statsfinal;
			}
		}
		return;
	}
	
	public boolean horaComer() {
		Calendar actual = Calendar.getInstance();
		int bmes = actual.get(Calendar.MONTH) + 1;
		int bdia = actual.get(Calendar.DAY_OF_MONTH);
		int bhora = actual.get(Calendar.HOUR_OF_DAY);
		int bminuto = actual.get(Calendar.MINUTE);
		long total = 0;
		total += ( (bmes - 1) * 43200);
		total += ( (bdia - 1) * 1440);
		total += (bhora * 60);
		total += bminuto;
		long resta = total - getMinutosDia();
		if (resta >= Bustemu.RATE_TIEMPO_ALIMENTACION) {
			setFecha(2012, bmes, bdia, bhora, bminuto);
			_fechaUltComida = "328#" + Integer.toHexString(_año) + "#" + Integer.toHexString( (_mes - 1) * 100 + _dia) + "#"
					+ Integer.toHexString(_hora * 100 + _minuto);
			setCorpulencia(0);
			return true;
		}
		return false;
	}
	
	public long entreComidas() {
		Calendar actual = Calendar.getInstance();
		int bmes = actual.get(Calendar.MONTH) + 1;
		int bdia = actual.get(Calendar.DAY_OF_MONTH);
		int bhora = actual.get(Calendar.HOUR_OF_DAY);
		int bminuto = actual.get(Calendar.MINUTE);
		int total = 0;
		total += ( (bmes - 1) * 43200);
		total += ( (bdia - 1) * 1440);
		total += (bhora * 60);
		total += bminuto;
		return (total - getMinutosDia());
	}
	
	public String analizarStatsMascota() {
		String stats = "320#0#0#" + Integer.toHexString(getPDV()) + "," + _fechaUltComida + ",326#0#" + _obeso + "#" + _delgado;
		if (_ultimaComida != -1)
			stats += ",327#0#0#" + Integer.toHexString(_ultimaComida);
		if (!_almasDevoradas.isEmpty()) {
			String str = "";
			boolean primero = false;
			for (String almas : _almasDevoradas.split(";")) {
				String[] detalle = almas.split(",");
				if (primero)
					str += ",";
				int idReal = Integer.parseInt(detalle[0]);
				String nombreMob = MundoDofus.getMobModelo(idReal).getNombre();
				str += "2cd#" + Integer.toHexString(idReal) + "#0#" + Integer.toHexString(Integer.parseInt(detalle[1])) + "#"
						+ nombreMob;
				primero = true;
			}
			stats += "," + str;
		}
		if (!_stringStats.isEmpty())
			stats += "," + _stringStats;
		return stats;
	}
	
	public String analizarStatsFantasma() {
		String stats = _fechaUltComida + ",326#0#" + _obeso + "#" + _delgado;
		if (_ultimaComida != -1)
			stats += ",327#0#0#" + Integer.toHexString(_ultimaComida);
		if (!_almasDevoradas.isEmpty()) {
			String str = "";
			boolean primero = false;
			for (String almas : _almasDevoradas.split(";")) {
				String[] detalle = almas.split(",");
				if (primero)
					str += ",";
				int idReal = Integer.parseInt(detalle[0]);
				String nombreMob = MundoDofus.getMobModelo(idReal).getNombre();
				str += "2cd#" + Integer.toHexString(idReal) + "#0#" + Integer.toHexString(Integer.parseInt(detalle[1])) + "#"
						+ nombreMob;
				primero = true;
			}
			stats += "," + str;
		}
		if (!_stringStats.isEmpty())
			stats += "," + _stringStats;
		return stats;
	}
}
