
package variables;

public class Animacion {
	private int ID;
	private int animacionID;
	private String nombreAnimacion;
	private int areaAnimacion;
	private int accionAnimacion;
	private int tamañoAnimacion;
	
	public Animacion(int Id, int AnimId, String Name, int Area, int Action, int Size) {
		this.ID = Id;
		this.animacionID = AnimId;
		this.nombreAnimacion = Name;
		this.areaAnimacion = Area;
		this.accionAnimacion = Action;
		this.tamañoAnimacion = Size;
	}
	
	public int getId() {
		return ID;
	}
	
	public String getNombre() {
		return nombreAnimacion;
	}
	
	public int getArea() {
		return areaAnimacion;
	}
	
	public int getAccion() {
		return accionAnimacion;
	}
	
	public int getTamaño() {
		return tamañoAnimacion;
	}
	
	public int getAnimacionId() {
		return animacionID;
	}
	
	public static String preparaAGameAccion(Animacion animacion) {
		String Packet;
		Packet = animacion.getAnimacionId() + "," + animacion.getArea() + "," + animacion.getAccion() + ","
				+ animacion.getTamaño();
		return Packet;
		// String hechizo = sHechizo.getHechizoID() + "," + celdaID + "," + sHechizo.getSpriteID() + ","
		// + sHechizo.getNivel() + "," + sHechizo.getSpriteInfos();
	}
}