
package variables;

public class RankingPVP {
	private int _id;
	private String _nombre;
	private int _victorias = 0;
	private int _derrotas = 0;
	private int _nivelAlineacion = 1;
	
	public RankingPVP(int id, String nombre, int victorias, int derrotas, int nivelAlineacion) {
		_id = id;
		_nombre = nombre;
		_victorias = victorias;
		_derrotas = derrotas;
		_nivelAlineacion = nivelAlineacion;
	}
	
	public int getVictorias() {
		return _victorias;
	}
	
	public int getDerrotas() {
		return _derrotas;
	}
	
	public int getID() {
		return _id;
	}
	
	public void aumentarVictoria() {
		_victorias++;
	}
	
	public void aumentarDerrota() {
		_derrotas++;
	}
	
	public void setNivelAlin(int nivel) {
		_nivelAlineacion = nivel;
	}
	
	public String getNombre() {
		return _nombre;
	}
	
	public int getNivelAlin() {
		return _nivelAlineacion;
	}
}
