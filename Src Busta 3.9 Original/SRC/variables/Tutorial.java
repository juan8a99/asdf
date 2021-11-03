
package variables;

import java.util.ArrayList;

import estaticos.Bustemu;

public class Tutorial {
	private int _id;
	private ArrayList<Accion> _recompensa = new ArrayList<Accion>(4);
	private Accion _inicio;
	private Accion _final;
	
	public Tutorial(int id, String recompensa, String inicio, String fin) {
		_id = id;
		try {
			for (String str : recompensa.split(",")) {
				if (str.isEmpty()) {
					_recompensa.add(null);
					continue;
				}
				String[] a = str.split("@");
				if (a.length >= 2)
					_recompensa.add(new Accion(Integer.parseInt(a[0]), a[1], ""));
				else
					_recompensa.add(new Accion(Integer.parseInt(a[0]), "", ""));
			}
			if (inicio.isEmpty())
				_inicio = null;
			else {
				String[] b = inicio.split("@");
				if (b.length >= 2)
					_inicio = new Accion(Integer.parseInt(b[0]), b[1], "");
				else
					_inicio = new Accion(Integer.parseInt(b[0]), "", "");
			}
			if (fin.isEmpty())
				_final = null;
			else {
				String[] c = fin.split("@");
				if (c.length >= 2)
					_final = new Accion(Integer.parseInt(c[0]), c[1], "");
				else
					_final = new Accion(Integer.parseInt(c[0]), "", "");
			}
		} catch (Exception e) {
			System.out.println("Ocurrio un error al cargar el tutorial " + id);
			Bustemu.cerrarServer();
		}
	}
	
	public ArrayList<Accion> getRecompensa() {
		return _recompensa;
	}
	
	public Accion getInicio() {
		return _inicio;
	}
	
	public Accion getFin() {
		return _final;
	}
	
	public int getID() {
		return _id;
	}
}
