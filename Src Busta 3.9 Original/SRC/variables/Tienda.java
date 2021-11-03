
package variables;

public class Tienda {
	private int itemId;
	private int precio;
	private int cantidad;
	
	public Tienda(int ItemId, int Precio, int Cantidad) {
		itemId = ItemId;
		precio = Precio;
		cantidad = Cantidad;
	}
	
	public int getPrecio() {
		return precio;
	}
	
	public void setPrecio(int precio) {
		this.precio = precio;
	}
	
	public int getCantidad() {
		return cantidad;
	}
	
	public void setCantidad(int cant) {
		this.cantidad = cant;
	}
	
	public int getItemId() {
		return itemId;
	}
}
