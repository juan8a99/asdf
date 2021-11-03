
package variables;

import java.util.ArrayList;

import estaticos.CondicionJugador;
import estaticos.GestorSQL;
import estaticos.MundoDofus;

import variables.Objeto.ObjetoModelo;

public class NPCModelo {
	private int _id;
	private String _nombre;
	private int _bonusValue;
	private int _gfxID;
	private int _escalaX;
	private int _escalaY;
	private int _sexo;
	private int _color1;
	private int _color2;
	private int _color3;
	private String _accesorios;
	private int _extraClip;
	private int _customArtWork;
	private int _preguntaID;
	private ArrayList<ObjetoModelo> _objVender = new ArrayList<ObjetoModelo>();
	private long _kamas;
	public static class NPC {
		private NPCModelo _modeloBD;
		private int _celdaID;
		private int _id;
		private byte _orientacion;
		private String _nombre;
		
		public NPC(NPCModelo npcModelo, int id, int celda, byte o, String nombre) {
			_modeloBD = npcModelo;
			_id = id;
			_celdaID = celda;
			_orientacion = o;
			_nombre = nombre;
		}
		
		public NPCModelo getModeloBD() {
			return _modeloBD;
		}
		
		public String getNombre() {
			return _nombre;
		}
		
		public int getCeldaID() {
			return _celdaID;
		}
		
		public int getID() {
			return _id;
		}
		
		public int getOrientacion() {
			return _orientacion;
		}
		
		public String analizarGM() {
			String sock = "";
			sock += "+";
			sock += _celdaID + ";";
			sock += _orientacion + ";";
			sock += "0" + ";";
			sock += _id + ";";
			sock += _modeloBD.getID() + ";";
			sock += "-4" + ";";// tipo = NPC
			String talla = "";
			if (_modeloBD.getTallaX() == _modeloBD.getTallaY()) {
				talla = "" + _modeloBD.getTallaY();
			} else {
				talla = _modeloBD.getTallaX() + "x" + _modeloBD.getTallaY();
			}
			sock += _modeloBD.getGfxID() + "^" + talla + ";";
			sock += _modeloBD.getSexo() + ";";
			sock += (_modeloBD.getColor1() != -1 ? Integer.toHexString(_modeloBD.getColor1()) : "-1") + ";";
			sock += (_modeloBD.getColor2() != -1 ? Integer.toHexString(_modeloBD.getColor2()) : "-1") + ";";
			sock += (_modeloBD.getColor3() != -1 ? Integer.toHexString(_modeloBD.getColor3()) : "-1") + ";";
			sock += _modeloBD.getAccesorios() + ";";
			sock += (_modeloBD.getExtraClip() != -1 ? (_modeloBD.getExtraClip()) : ("")) + ";";
			sock += _modeloBD.getCustomArtWork();
			return sock;
		}
		
		public void setCeldaID(int id) {
			_celdaID = id;
		}
		
		public void setOrientacion(byte o) {
			_orientacion = o;
		}
	}
	public static class PreguntaNPC {
		private int _id;
		private String _respuestas;
		private String _args;
		private String _condicion;
		private int _falsaPregunta;
		
		public PreguntaNPC(int id, String respuestas, String args, String cond, int falsaPregunta) {
			_id = id;
			_respuestas = respuestas;
			_args = args;
			_condicion = cond;
			_falsaPregunta = falsaPregunta;
		}
		
		public int getID() {
			return _id;
		}
		
		public String stringArgParaDialogo(Personaje perso) {
			if (!CondicionJugador.validaCondiciones(perso, _condicion))
				return MundoDofus.getNPCPregunta(_falsaPregunta).stringArgParaDialogo(perso);
			String str = _id + "";
			if (!_args.equals(""))
				str += ";" + analizarArgumentos(_args, perso);
			if (!_respuestas.isEmpty())
				str += "|" + _respuestas;
			return str;
		}
		
		public String stringGremio(Personaje perso, Gremio gremio) {
			String str = _id + "";
			if (!_args.equals(""))
				str += ";" + analizarArgumentosGremio(_args, gremio);
			return str;
		}
		
		public String getRespuestas() {
			return _respuestas;
		}
		
		private String analizarArgumentos(String args, Personaje perso) {
			String arg = args;
			arg = arg.replace("[nombre]", perso.getStringVar("nombre"));
			arg = arg.replace("[costoBanco]", perso.getStringVar("costoBanco"));
			arg = arg.replace("[lider]", MundoDofus.liderRanking);
			arg = arg.replace("[npcKamas]", MundoDofus.getNPCModelo(408).getKamas() + "");
			return arg;
		}
		
		private String analizarArgumentosGremio(String args, Gremio gremio) {
			String arg = args;
			arg = arg.replace("[gremio]", gremio.getInfoGremio());
			return arg;
		}
		
		public void setRespuestas(String respuestas) {
			_respuestas = respuestas;
		}
	}
	public static class RespuestaNPC {
		private int _id;
		private ArrayList<Accion> _acciones = new ArrayList<Accion>();
		
		public RespuestaNPC(int id) {
			_id = id;
		}
		
		public int getID() {
			return _id;
		}
		
		public void addAccion(Accion accion) {
			ArrayList<Accion> c = new ArrayList<Accion>();
			c.addAll(_acciones);
			for (Accion a : c)
				if (a.getID() == accion.getID())
					_acciones.remove(a);
			_acciones.add(accion);
		}
		
		public void aplicar(Personaje perso) {
			for (Accion accion : _acciones)
				accion.aplicar(perso, null, -1, -1);
		}
		
		public boolean esOtroDialogo() {
			for (Accion accion : _acciones) {
				if (accion.getID() == 1)
					return true;
			}
			return false;
		}
	}
	
	public NPCModelo(int id, int bonusValue, int gfxid, int escalaX, int escalaY, int sexo, int color1, int color2, int color3,
			String accesorios, int clip, int artWork, int preguntaID, String objVender, String nombre, long kamas) {
		super();
		_id = id;
		_bonusValue = bonusValue;
		_gfxID = gfxid;
		_escalaX = escalaX;
		_escalaY = escalaY;
		_sexo = sexo;
		_color1 = color1;
		_color2 = color2;
		_color3 = color3;
		_accesorios = accesorios;
		_extraClip = clip;
		_customArtWork = artWork;
		_preguntaID = preguntaID;
		_nombre = nombre;
		_kamas = kamas;
		if (objVender.equals(""))
			return;
		for (String obj : objVender.split("\\,")) {
			try {
				int idModelo = Integer.parseInt(obj);
				ObjetoModelo objModelo = MundoDofus.getObjModelo(idModelo);
				if (objModelo == null)
					continue;
				_objVender.add(objModelo);
			} catch (NumberFormatException e) {
				continue;
			}
		}
		actualizarObjetosAVender();
	}
	
	public int getID() {
		return _id;
	}
	
	public long getKamas() {
		return _kamas;
	}
	
	public void setKamas(long kamas) {
		_kamas = kamas;
	}
	
	public String getNombre() {
		return _nombre;
	}
	
	public int getBonusValor() {
		return _bonusValue;
	}
	
	public int getGfxID() {
		return _gfxID;
	}
	
	public int getTallaX() {
		return _escalaX;
	}
	
	public int getTallaY() {
		return _escalaY;
	}
	
	public int getSexo() {
		return _sexo;
	}
	
	public int getColor1() {
		return _color1;
	}
	
	public int getColor2() {
		return _color2;
	}
	
	public int getColor3() {
		return _color3;
	}
	
	public String getAccesorios() {
		return _accesorios;
	}
	
	public int getExtraClip() {
		return _extraClip;
	}
	
	public int getCustomArtWork() {
		return _customArtWork;
	}
	
	public int getPreguntaID() {
		return _preguntaID;
	}
	
	public void configurarNPC(int gfxid, int sexo, int color1, int color2, int color3, String accesorios) {
		_gfxID = gfxid;
		_sexo = sexo;
		_color1 = color1;
		_color2 = color2;
		_color3 = color3;
		_accesorios = accesorios;
		GestorSQL.ACTUALIZAR_NPC_COLOR_SEXO(this);
	}
	
	public void actualizarObjetosAVender() {
		String objetos = "";
		if (_objVender.isEmpty())
			_listaObjetos = objetos;
		for (ObjetoModelo obj : _objVender) {
			objetos += obj.stringDeStatsParaTienda() + "|";
		}
		_listaObjetos = objetos;
	}
	
	public String actualizarStringBD() {
		String objetos = "";
		if (_objVender.isEmpty())
			return objetos;
		for (ObjetoModelo obj : _objVender) {
			objetos += obj.getID() + ",";
		}
		return objetos;
	}
	private String _listaObjetos = "";
	
	public String stringObjetosAVender() {
		return _listaObjetos;
	}
	
	public boolean addObjetoAVender(ObjetoModelo objModelo) {
		if (_objVender.contains(objModelo))
			return false;
		_objVender.add(objModelo);
		actualizarObjetosAVender();
		GestorSQL.ACTUALIZAR_NPC_VENTAS(this);
		return true;
	}
	
	public boolean borrarObjetoAVender(int idModelo) {
		ArrayList<ObjetoModelo> nuevosObj = new ArrayList<ObjetoModelo>();
		boolean remove = false;
		for (ObjetoModelo OM : _objVender) {
			if (OM.getID() == idModelo) {
				remove = true;
				continue;
			}
			nuevosObj.add(OM);
		}
		_objVender = nuevosObj;
		actualizarObjetosAVender();
		GestorSQL.ACTUALIZAR_NPC_VENTAS(this);
		return remove;
	}
	
	public void setPreguntaID(int pregunta) {
		_preguntaID = pregunta;
	}
	
	public boolean tieneObjeto(int idModelo) {
		for (ObjetoModelo OM : _objVender) {
			if (OM.getID() == idModelo)
				return true;
		}
		return false;
	}
}
