
package variables;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import variables.Objeto.ObjetoModelo;

import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;
import estaticos.MundoDofus.Duo;

public class PuestoMercadillo {
	private class TipoObjetos {
		Map<Integer, Modelo> _modelos = new HashMap<Integer, Modelo>();
		@SuppressWarnings("unused")
		int _tipoObjID;
		
		public TipoObjetos(int categoriaID) {
			_tipoObjID = categoriaID;
		}
		
		public void addModeloVerificacion(ObjetoMercadillo objMerca) {
			int modeloID = objMerca.getObjeto().getModelo().getID();
			Modelo modelo = _modelos.get(modeloID);
			if (modelo == null)
				_modelos.put(modeloID, new Modelo(modeloID, objMerca));
			else
				modelo.addObjMercaConLinea(objMerca);
		}
		
		public boolean borrarObjMercaDeModelo(ObjetoMercadillo objMerca) {
			boolean borrable = false;
			int idModelo = objMerca.getObjeto().getModelo().getID();
			_modelos.get(idModelo).borrarObjMercaDeUnaLinea(objMerca);
			if ( (borrable = _modelos.get(idModelo).estaVacio()))
				borrarModelo(idModelo);
			return borrable;
		}
		
		public Modelo getModelo(int modeloID) {
			return _modelos.get(modeloID);
		}
		
		public ArrayList<ObjetoMercadillo> todoListaObjMercaDeUnTipo() {
			ArrayList<ObjetoMercadillo> listaObjMerca = new ArrayList<ObjetoMercadillo>();
			for (Modelo modelo : _modelos.values()) {
				listaObjMerca.addAll(modelo.todosObjMercaDeUnModelo());
			}
			return listaObjMerca;
		}
		
		public String stringModelo() {
			boolean primero = true;
			String string = "";
			for (int curTemp : _modelos.keySet()) {
				if (!primero)
					string += ";";
				string += curTemp;
				primero = false;
			}
			return string;
		}
		
		public void borrarModelo(int modeloID) {
			_modelos.remove(modeloID);
		}
	}
	private class Modelo {
		int _modeloID;
		Map<Integer, LineaMercadillo> _lineas = new HashMap<Integer, LineaMercadillo>();
		
		public Modelo(int modeloID, ObjetoMercadillo objMercadillo) {
			_modeloID = modeloID;
			addObjMercaConLinea(objMercadillo);
		}
		
		public void addObjMercaConLinea(ObjetoMercadillo objMerca) {
			for (LineaMercadillo linea : _lineas.values()) {
				if (linea.addObjMercaALinea(objMerca))
					return;
			}
			int lineaID = MundoDofus.sigIDLineaMercadillo();
			_lineas.put(lineaID, new LineaMercadillo(lineaID, objMerca));
		}
		
		public LineaMercadillo getLinea(int lineaID) {
			return _lineas.get(lineaID);
		}
		
		public boolean borrarObjMercaDeUnaLinea(ObjetoMercadillo objMerca) {
			boolean borrable = _lineas.get(objMerca.getLineaID()).borrarObjMercaDeLinea(objMerca);
			if (_lineas.get(objMerca.getLineaID()).categoriaVacia()) {
				_lineas.remove(objMerca.getLineaID());
			}
			return borrable;
		}
		
		public ArrayList<ObjetoMercadillo> todosObjMercaDeUnModelo() {
			ArrayList<ObjetoMercadillo> listaObj = new ArrayList<ObjetoMercadillo>();
			for (LineaMercadillo linea : _lineas.values()) {
				listaObj.addAll(linea.todosObjMercaDeUnaLinea());
			}
			return listaObj;
		}
		
		public String analizarParaEHl() {
			String string = _modeloID + "|";
			boolean primero = true;
			for (LineaMercadillo linea : _lineas.values()) {
				if (!primero)
					string += "|";
				string += linea.analizarParaEHl();
				primero = false;
			}
			return string;
		}
		
		public boolean estaVacio() {
			if (_lineas.size() == 0)
				return true;
			return false;
		}
	}
	public class LineaMercadillo {
		private int _lineaID;
		private ArrayList<ArrayList<ObjetoMercadillo>> _categorias = new ArrayList<ArrayList<ObjetoMercadillo>>(3);
		private String _strStats;
		private int _modeloID;
		
		public LineaMercadillo(int lineaID, ObjetoMercadillo objMercadillo) {
			_lineaID = lineaID;
			Objeto objeto = objMercadillo.getObjeto();
			_strStats = objeto.convertirStatsAString();
			_modeloID = objeto.getModelo().getID();
			for (int i = 0; i < 3; i++) {
				_categorias.add(new ArrayList<ObjetoMercadillo>());
			}
			addObjMercaALinea(objMercadillo);
		}
		
		public String getStringStats() {
			return _strStats;
		}
		
		public boolean addObjMercaALinea(ObjetoMercadillo objMerca) {
			if (!categoriaVacia() && !tieneMismoStats(objMerca))
				return false;
			objMerca.setLineaID(_lineaID);
			byte index = (byte) (objMerca.getTipoCantidad(false) - 1);
			_categorias.get(index).add(objMerca);
			ordenar(index);
			return true;
		}
		
		public boolean tieneMismoStats(ObjetoMercadillo objMerca) {
			Objeto objeto = objMerca.getObjeto();
			return _strStats.equalsIgnoreCase(objeto.convertirStatsAString()) && objeto.getModelo().getTipo() != 85;
		}
		
		public ObjetoMercadillo tuTienes(int categoria, int precio) {
			int index = categoria - 1;
			for (int i = 0; i < _categorias.get(index).size(); i++) {
				if (_categorias.get(index).get(i).getPrecio() == precio)
					return _categorias.get(index).get(i);
			}
			return null;
		}
		
		public int[] getPrimeras() {
			int[] aRetornar = new int[3];
			for (int i = 0; i < _categorias.size(); i++) {
				try {
					aRetornar[i] = _categorias.get(i).get(0).getPrecio();
				} catch (IndexOutOfBoundsException e) {
					aRetornar[i] = 0;
				}
			}
			return aRetornar;
		}
		
		public ArrayList<ObjetoMercadillo> todosObjMercaDeUnaLinea() {
			int totalEntradas = _categorias.get(0).size() + _categorias.get(1).size() + _categorias.get(2).size();
			ArrayList<ObjetoMercadillo> todosObjMerca = new ArrayList<ObjetoMercadillo>(totalEntradas);
			for (int cat = 0; cat < _categorias.size(); cat++) {
				todosObjMerca.addAll(_categorias.get(cat));
			}
			return todosObjMerca;
		}
		
		public boolean borrarObjMercaDeLinea(ObjetoMercadillo objMercadillo) {
			byte categoria = (byte) (objMercadillo.getTipoCantidad(false) - 1);
			boolean borrable = _categorias.get(categoria).remove(objMercadillo);
			ordenar(categoria);
			return borrable;
		}
		
		public String analizarParaEHl() {
			String aRetornar = "";
			int[] precio = getPrimeras();
			aRetornar += _lineaID + ";" + _strStats + ";" + (precio[0] == 0 ? "" : precio[0]) + ";"
					+ (precio[1] == 0 ? "" : precio[1]) + ";" + (precio[2] == 0 ? "" : precio[2]);
			return aRetornar;
		}
		
		public String analizarParaEHm() {
			int[] precio = getPrimeras();
			String aRetornar = _lineaID + "|" + _modeloID + "|" + _strStats + "|" + (precio[0] == 0 ? "" : precio[0]) + "|"
					+ (precio[1] == 0 ? "" : precio[1]) + "|" + (precio[2] == 0 ? "" : precio[2]);
			return aRetornar;
		}
		
		public void ordenar(byte categoria) {
			Collections.sort(_categorias.get(categoria));
		}
		
		public boolean categoriaVacia() {
			for (int i = 0; i < _categorias.size(); i++) {
				try {
					if (_categorias.get(i).get(0) != null)
						return false;
				} catch (IndexOutOfBoundsException e) {}
			}
			return true;
		}
	}
	public static class ObjetoMercadillo implements Comparable<ObjetoMercadillo> {
		private int _idDelPuesto;
		private int _precio;
		private int _tipoCantidad;// 1 = 1, 2 = 10, 3 = 100
		private Objeto _objeto;
		private int _lineaID;
		private int _dueño;
		
		public ObjetoMercadillo(int precio, int cant, int dueño, Objeto obj) {
			_precio = precio;
			_tipoCantidad = cant;
			_objeto = obj;
			_dueño = dueño;
		}
		
		public void setIDPuesto(int id) {
			_idDelPuesto = id;
		}
		
		public int getIDDelPuesto() {
			return _idDelPuesto;
		}
		
		public int getPrecio() {
			return _precio;
		}
		
		public int getTipoCantidad(boolean cantidadReal) {
			if (cantidadReal)
				return (int) (Math.pow(10, (double) _tipoCantidad) / 10);
			else
				return _tipoCantidad;
		}
		
		public Objeto getObjeto() {
			return _objeto;
		}
		
		public int getLineaID() {
			return _lineaID;
		}
		
		public void setLineaID(int ID) {
			_lineaID = ID;
		}
		
		public int getDueño() {
			return _dueño;
		}
		
		public String analizarParaEL() {
			int cantidad = getTipoCantidad(true);
			return _lineaID + ";" + cantidad + ";" + _objeto.getModelo().getID() + ";" + _objeto.convertirStatsAString() + ";"
					+ _precio + ";350";
		}
		
		public String analizarParaEmK() {
			int cantidad = getTipoCantidad(true);
			return _objeto.getID() + "|" + cantidad + "|" + _objeto.getModelo().getID() + "|" + _objeto.convertirStatsAString()
					+ "|" + _precio + "|350";
		}
		
		public String analizarObjeto(char separador) {
			int cantidad = getTipoCantidad(true);
			return _lineaID + separador + cantidad + separador + _objeto.getModelo().getID() + separador
					+ _objeto.convertirStatsAString() + separador + _precio + separador + "350";
		}
		
		public int compareTo(ObjetoMercadillo objMercadillo) {
			int otroPrecio = objMercadillo.getPrecio();
			if (otroPrecio > _precio)
				return -1;
			if (otroPrecio == _precio)
				return 0;
			if (otroPrecio < _precio)
				return 1;
			return 0;
		}
	}
	private int _idPuestoMerca;
	private float _porcMercadillo;
	private short _tiempoVenta;
	private short _maxObjCuenta;
	private String _tipoObjPermitidos;
	private short _nivelMax;
	private Map<Integer, TipoObjetos> _listaTipoObj = new HashMap<Integer, TipoObjetos>();
	private Map<Integer, Duo<Integer, Integer>> _path = new HashMap<Integer, Duo<Integer, Integer>>();
	private DecimalFormat _porcentaje = new DecimalFormat("0.0");
	
	public PuestoMercadillo(int mercadilloID, float tasa, short tiempoVenta, short maxObjCuenta, short nivelMax, String tipoObj) {
		_idPuestoMerca = mercadilloID;
		_porcMercadillo = tasa;
		_maxObjCuenta = maxObjCuenta;
		_tipoObjPermitidos = tipoObj;
		_nivelMax = nivelMax;
		int tipoID;
		for (String tipo : tipoObj.split(",")) {
			tipoID = Integer.parseInt(tipo);
			_listaTipoObj.put(tipoID, new TipoObjetos(tipoID));
		}
	}
	
	public int getIDMercadillo() {
		return _idPuestoMerca;
	}
	
	public float getPorcentaje() {
		return _porcMercadillo;
	}
	
	public short getTiempoVenta() {
		return _tiempoVenta;
	}
	
	public short getMaxObjCuenta() {
		return _maxObjCuenta;
	}
	
	public String getTipoObjPermitidos() {
		return _tipoObjPermitidos;
	}
	
	public short getNivelMax() {
		return _nivelMax;
	}
	
	public String analizarParaEHl(int modeloID) {
		int tipo = MundoDofus.getObjModelo(modeloID).getTipo();
		return _listaTipoObj.get(tipo).getModelo(modeloID).analizarParaEHl();
	}
	
	public String stringModelo(int tipoObj) {
		return _listaTipoObj.get(tipoObj).stringModelo();
	}
	
	public String porcentajeImpuesto() {
		return _porcentaje.format(_porcMercadillo).replace(",", ".");
	}
	
	public LineaMercadillo getLinea(int lineaID) {
		try {
			int tipoObj = _path.get(lineaID)._primero;
			int idModelo = _path.get(lineaID)._segundo;
			return _listaTipoObj.get(tipoObj).getModelo(idModelo).getLinea(lineaID);
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	public ArrayList<ObjetoMercadillo> todoListaObjMercaDeUnPuesto() {
		ArrayList<ObjetoMercadillo> listaObjMerca = new ArrayList<ObjetoMercadillo>();
		for (TipoObjetos tipo : _listaTipoObj.values()) {
			listaObjMerca.addAll(tipo.todoListaObjMercaDeUnTipo());
		}
		return listaObjMerca;
	}
	
	public void addObjMercaAlPuesto(ObjetoMercadillo objMerca) {
		if (objMerca.getObjeto() == null)
			return;
		objMerca.setIDPuesto(_idPuestoMerca);
		ObjetoModelo objModelo = objMerca.getObjeto().getModelo();
		int tipoObj = objModelo.getTipo();
		int idModelo = objModelo.getID();
		_listaTipoObj.get(tipoObj).addModeloVerificacion(objMerca);
		_path.put(objMerca.getLineaID(), new Duo<Integer, Integer>(tipoObj, idModelo));
		MundoDofus.addObjMercadillo(objMerca.getDueño(), _idPuestoMerca, objMerca);
	}
	
	public boolean borrarObjMercaDelPuesto(ObjetoMercadillo objMerca) {
		Objeto objeto = objMerca.getObjeto();
		if (objeto == null)
			return false;
		boolean borrable = _listaTipoObj.get(objeto.getModelo().getTipo()).borrarObjMercaDeModelo(objMerca);
		if (borrable) {
			_path.remove(objMerca.getLineaID());
			MundoDofus.borrarObjMercadillo(objMerca.getDueño(), objMerca.getIDDelPuesto(), objMerca);
		}
		return borrable;
	}
	
	public synchronized boolean comprarObjeto(int lineaID, int cant, int precio, Personaje nuevoDueño) {
		boolean posible = true;
		try {
			if (nuevoDueño.getKamas() < precio)
				return false;
			LineaMercadillo linea = getLinea(lineaID);
			ObjetoMercadillo objAComprar = linea.tuTienes(cant, precio);
			nuevoDueño.addKamas(precio * -1);
			if (objAComprar.getDueño() != -1) {
				Cuenta cuenta = MundoDofus.getCuenta(objAComprar.getDueño());
				if (cuenta != null) {
					cuenta.setKamasBanco(cuenta.getKamasBanco() + objAComprar.getPrecio());
				}
			}
			GestorSalida.ENVIAR_As_STATS_DEL_PJ(nuevoDueño);
			Objeto objeto = objAComprar.getObjeto();
			if (nuevoDueño.addObjetoSimilar(objeto, true, -1))
				MundoDofus.eliminarObjeto(objeto.getID());
			else {
				nuevoDueño.addObjetoPut(objeto);
				GestorSalida.ENVIAR_OAKO_APARECER_OBJETO(nuevoDueño, objeto);
			}
			objeto.getModelo().nuevoPrecio(objAComprar.getTipoCantidad(true), precio);
			borrarObjMercaDelPuesto(objAComprar);
			if (MundoDofus.getCuenta(objAComprar.getDueño()) != null
					&& MundoDofus.getCuenta(objAComprar.getDueño()).getTempPersonaje() != null) {
				GestorSalida.ENVIAR_Im_INFORMACION(MundoDofus.getCuenta(objAComprar.getDueño()).getTempPersonaje(), "065;"
						+ precio + "~" + objeto.getModelo().getID() + "~" + objeto.getModelo().getID() + "~1");
			}
			if (objAComprar.getDueño() == -1) {
				GestorSQL.SALVAR_OBJETO(objeto);
			}
			objAComprar = null;
		} catch (NullPointerException e) {
			posible = false;
		}
		return posible;
	}
}
