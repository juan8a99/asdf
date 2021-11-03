
package variables;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import estaticos.Bustemu;
import estaticos.CentroInfo;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;

public class Casa {
	private int _id;
	private short _mapaIDFuera;
	private int _celdaIDFuera;
	private int _dueñoID;
	private int _precioVenta;
	private int _gremioID;
	private int _derechosGremio;
	private int _acceso;
	private String _clave;
	private int _mapaIDDentro;
	private int _celdaIDDentro;
	private Map<Integer, Boolean> _tieneDerecho = new TreeMap<Integer, Boolean>();
	
	public Casa(int id, short mapaIDFuera, int celdaIDFuera, int dueño, int precio, int gremioID, int acceso, String key,
			int derechosGremio, int mapaIDDentro, int celdaIDDentro) {
		_id = id;
		_mapaIDFuera = mapaIDFuera;
		_celdaIDFuera = celdaIDFuera;
		_dueñoID = dueño;
		_precioVenta = precio;
		_gremioID = gremioID;
		_acceso = acceso;
		_clave = key;
		_derechosGremio = derechosGremio;
		analizarDerechos(derechosGremio);
		_mapaIDDentro = mapaIDDentro;
		_celdaIDDentro = celdaIDDentro;
	}
	
	public int getID() {
		return _id;
	}
	
	public short getMapaIDFuera() {
		return _mapaIDFuera;
	}
	
	public int getCeldaIDFuera() {
		return _celdaIDFuera;
	}
	
	public int getDueñoID() {
		return _dueñoID;
	}
	
	public void setDueñoID(int id) {
		_dueñoID = id;
	}
	
	public int getPrecioVenta() {
		return _precioVenta;
	}
	
	public void setPrecio(int precio) {
		_precioVenta = precio;
	}
	
	public int getGremioID() {
		return _gremioID;
	}
	
	public void setGremioID(int gremioID) {
		_gremioID = gremioID;
	}
	
	public int getDerechosGremio() {
		return _derechosGremio;
	}
	
	public void setDerechosGremio(int derechosGremio) {
		_derechosGremio = derechosGremio;
	}
	
	public int getAcceso() {
		return _acceso;
	}
	
	public void setAcceso(int accesso) {
		_acceso = accesso;
	}
	
	public String getClave() {
		return _clave;
	}
	
	public void setClave(String clave) {
		_clave = clave;
	}
	
	public int getMapaIDDentro() {
		return _mapaIDDentro;
	}
	
	public int getCeldaIDDentro() {
		return _celdaIDDentro;
	}
	
	public static Casa getCasaPorUbicacion(int mapaID, int celdaID) {
		for (Entry<Integer, Casa> casa : MundoDofus.getCasas().entrySet()) {
			if (casa.getValue().getMapaIDFuera() == mapaID && casa.getValue().getCeldaIDFuera() == celdaID) {
				return casa.getValue();
			}
		}
		return null;
	}
	
	public static void cargarCasa(Personaje perso, int nuevoMapaID) {
		for (Entry<Integer, Casa> casa : MundoDofus.getCasas().entrySet()) {
			if (casa.getValue().getMapaIDFuera() == nuevoMapaID) {
				String packet = "P" + casa.getValue().getID() + "|";
				if (casa.getValue().getDueñoID() > 0) {
					Cuenta C = MundoDofus.getCuenta(casa.getValue().getDueñoID());
					if (C == null) {
						packet += "undefined;";
					} else {
						packet += MundoDofus.getCuenta(casa.getValue().getDueñoID()).getApodo() + ";";
					}
				} else {
					packet += ";";
				}
				if (casa.getValue().getPrecioVenta() > 0) {
					packet += "1";
				} else {
					packet += "0";
				}
				if (casa.getValue().getGremioID() > 0) {
					Gremio gremio = MundoDofus.getGremio(casa.getValue().getGremioID());
					String nombreGremio = gremio.getNombre();
					String emblemaGremio = gremio.getEmblema();
					if (gremio.getPjMiembros().size() < 10) {
						GestorSQL.ACTUALIZAR_CASA_GREMIO(casa.getValue(), 0, 0);
					}
					if (perso.getGremio() != null && perso.getGremio().getID() == casa.getValue().getGremioID()
							&& casa.getValue().tieneDerecho(CentroInfo.H_GBLASON) && gremio.getPjMiembros().size() > 9) {
						packet += ";" + nombreGremio + ";" + emblemaGremio;
					} else if (casa.getValue().tieneDerecho(CentroInfo.H_OBLASON) && gremio.getPjMiembros().size() > 9) {
						packet += ";" + nombreGremio + ";" + emblemaGremio;
					}
				}
				GestorSalida.ENVIAR_h_CASA(perso, packet);
				if (casa.getValue().getDueñoID() == perso.getCuentaID()) {
					String packet1 = "L+|" + casa.getValue().getID() + ";" + casa.getValue().getAcceso() + ";";
					if (casa.getValue().getPrecioVenta() <= 0) {
						packet1 += "0;" + casa.getValue().getPrecioVenta();
					} else if (casa.getValue().getPrecioVenta() > 0) {
						packet1 += "1;" + casa.getValue().getPrecioVenta();
					}
					GestorSalida.ENVIAR_h_CASA(perso, packet1);
				}
			}
		}
	}
	
	public void respondeA(Personaje perso) {
		if (perso.getPelea() != null || perso.getConversandoCon() != 0 || perso.getIntercambiandoCon() != 0 || perso.getHaciendoTrabajo() != null
				|| perso.getIntercambio() != null) {
			return;
		}
		Casa casa = perso.getCasa();
		if (casa == null)
			return;
		if (casa.getDueñoID() == perso.getCuentaID()
				|| (perso.getGremio() != null && perso.getGremio().getID() == casa.getGremioID() && tieneDerecho(CentroInfo.H_SINCODIGOGREMIO))) {
			abrirCasa(perso, "-", true);
		} else if (casa.getDueñoID() > 0) {
			GestorSalida.ENVIAR_K_CLAVE(perso, "CK0|8");
		} else if (casa.getDueñoID() == 0) {
			abrirCasa(perso, "-", false);
		} else {
			return;
		}
	}
	
	public static void abrirCasa(Personaje perso, String packet, boolean esHogar) {
		Casa casa = perso.getCasa();
		if ( (!casa.tieneDerecho(CentroInfo.H_ABRIRGREMIO) && (packet.compareTo(casa.getClave()) == 0)) || esHogar) {
			perso.teleport((short) casa.getMapaIDDentro(), casa.getCeldaIDDentro());
			cerrarVentana(perso);
		} else if ( (packet.compareTo(casa.getClave()) != 0) || casa.tieneDerecho(CentroInfo.H_ABRIRGREMIO)) {
			GestorSalida.ENVIAR_K_CLAVE(perso, "KE");
			GestorSalida.ENVIAR_K_CLAVE(perso, "V");
		}
	}
	
	public void comprarEstaCasa(Personaje perso) {
		Casa casa = perso.getCasa();
		if (casa == null)
			return;
		String str = "CK" + casa.getID() + "|" + casa.getPrecioVenta();
		GestorSalida.ENVIAR_h_CASA(perso, str);
	}
	
	public static void comprarCasa(Personaje perso) {
		Casa casa = perso.getCasa();
		if (tieneOtraCasa(perso)) {
			GestorSalida.ENVIAR_Im_INFORMACION(perso, "132;1");
			return;
		}
		if (perso.getKamas() < casa.getPrecioVenta())
			return;
		long nuevasKamas = perso.getKamas() - casa.getPrecioVenta();
		perso.setKamas(nuevasKamas);
		int kamasCofre = 0;
		for (Cofre cofre : Cofre.getCofresPorCasa(casa)) {
			if (casa.getDueñoID() > 0) {
				cofre.moverCofreABanco(MundoDofus.getCuenta(casa.getDueñoID()));
			}
			kamasCofre += cofre.getKamas();
			cofre.setKamas(0);
			cofre.setClave("-");
			cofre.setDueñoID(0);
			GestorSQL.ACTUALIZAR_COFRE(cofre);
		}
		if (casa.getDueñoID() > 0) {
			Cuenta cuentaVendedor = MundoDofus.getCuenta(casa.getDueñoID());
			long bancoKamas = cuentaVendedor.getKamasBanco() + casa.getPrecioVenta() + kamasCofre;
			cuentaVendedor.setKamasBanco(bancoKamas);
			Personaje vendedor = cuentaVendedor.getTempPersonaje();
			if (vendedor != null) {
				GestorSalida.ENVIAR_cs_CHAT_MENSAJE(vendedor, "Una casa ha sido vendida a " + casa.getPrecioVenta() + " kamas.",
						Bustemu.COLOR_MENSAJE);
				GestorSQL.SALVAR_PERSONAJE(vendedor, true);
			}
			GestorSQL.SALVAR_CUENTA(cuentaVendedor);
		}
		casa._dueñoID = perso.getID();
		if (perso.getGremio() != null)
			casa._gremioID = perso.getGremio().getID();
		GestorSQL.SALVAR_PERSONAJE(perso, true);
		GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso);
		GestorSQL.COMPRAR_CASA(perso, casa);
		cerrarVentanaCompra(perso);
		for (Personaje z : perso.getMapa().getPersos()) {
			cargarCasa(z, z.getMapa().getID());
		}
	}
	
	public void venderla(Personaje perso)
	{
		Casa casa = perso.getCasa();
		if (esSuCasa(perso, casa)) {
			String str = "CK" + casa.getID() + "|" + casa.getPrecioVenta();
			GestorSalida.ENVIAR_h_CASA(perso, str);
			return;
		} else {
			return;
		}
	}
	
	public static void precioVenta(Personaje perso, String packet) {
		Casa casa = perso.getCasa();
		int precio = Integer.parseInt(packet);
		if (casa.esSuCasa(perso, casa)) {
			GestorSalida.ENVIAR_h_CASA(perso, "V");
			GestorSalida.ENVIAR_h_CASA(perso, "SK" + casa.getID() + "|" + precio);
			GestorSQL.VENDER_CASA(casa, precio);
			for (Personaje z : perso.getMapa().getPersos()) {
				cargarCasa(z, z.getMapa().getID());
			}
			return;
		} else {
			return;
		}
	}
	
	public boolean esSuCasa(Personaje perso, Casa casa) {
		if (casa.getDueñoID() == perso.getCuentaID())
			return true;
		else
			return false;
	}
	
	public static void cerrarVentana(Personaje perso) {
		GestorSalida.ENVIAR_K_CLAVE(perso, "V");
	}
	
	public static void cerrarVentanaCompra(Personaje perso) {
		GestorSalida.ENVIAR_h_CASA(perso, "V");
	}
	
	public void bloquear(Personaje perso) {
		GestorSalida.ENVIAR_K_CLAVE(perso, "CK1|8");
	}
	
	public static void codificarCasa(Personaje perso, String packet) {
		Casa casa = perso.getCasa();
		if (casa.esSuCasa(perso, casa)) {
			GestorSQL.CODIGO_CASA(perso, casa, packet);
			cerrarVentana(perso);
			return;
		} else {
			cerrarVentana(perso);
			return;
		}
	}
	
	public static String analizarCasaGremio(Personaje perso) {
		boolean primero = true;
		String packet = "+";
		for (Entry<Integer, Casa> entry : MundoDofus.getCasas().entrySet()) {
			Casa casa = entry.getValue();
			if (casa.getGremioID() == perso.getGremio().getID() && casa.getDerechosGremio() > 0) {
				if (primero) {
					packet += entry.getKey() + ";";
					if (MundoDofus.getPersonaje(casa.getDueñoID()) == null)
						packet += "DUEÑO BUGEADO;";
					else
						packet += MundoDofus.getPersonaje(casa.getDueñoID()).getCuenta().getApodo() + ";";
					packet += MundoDofus.getMapa((short) casa.getMapaIDDentro()).getX() + ","
							+ MundoDofus.getMapa((short) casa.getMapaIDDentro()).getY() + ";";
					packet += "0;";
					packet += casa.getDerechosGremio();
					primero = false;
				} else {
					packet += "|";
					packet += entry.getKey() + ";";
					if (MundoDofus.getPersonaje(casa.getDueñoID()) == null)
						packet += "DUEÑO BUGEADO;";
					else
						packet += MundoDofus.getPersonaje(casa.getDueñoID()).getCuenta().getApodo() + ";";
					packet += MundoDofus.getMapa((short) casa.getMapaIDDentro()).getX() + ","
							+ MundoDofus.getMapa((short) casa.getMapaIDDentro()).getY() + ";";
					packet += "0;";
					packet += casa.getDerechosGremio();
				}
			}
		}
		return packet;
	}
	
	public static boolean tieneOtraCasa(Personaje perso) {
		for (Entry<Integer, Casa> casa : MundoDofus.getCasas().entrySet()) {
			if (casa.getValue().getDueñoID() == perso.getCuentaID()) {
				return true;
			}
		}
		return false;
	}
	
	public static void analizarCasaGremio(Personaje perso, String packet) {
		Casa casa = perso.getCasa();
		if (perso.getGremio() == null)
			return;
		if (packet != null) {
			if (packet.charAt(0) == '+') {
				byte maxCasasPorGremio = (byte) Math.floor(perso.getGremio().getNivel() / 10);
				if (casaGremio(perso.getGremio().getID()) >= maxCasasPorGremio)
					return;
				if (perso.getGremio().getPjMiembros().size() < 10)
					return;
				GestorSQL.ACTUALIZAR_CASA_GREMIO(casa, perso.getGremio().getID(), 0);
				analizarCasaGremio(perso, null);
			} else if (packet.charAt(0) == '-') {
				GestorSQL.ACTUALIZAR_CASA_GREMIO(casa, 0, 0);
				analizarCasaGremio(perso, null);
			} else {
				GestorSQL.ACTUALIZAR_CASA_GREMIO(casa, casa.getGremioID(), Integer.parseInt(packet));
				casa.analizarDerechos(Integer.parseInt(packet));
			}
		} else if (packet == null) {
			if (casa.getGremioID() <= 0) {
				GestorSalida.ENVIAR_h_CASA(perso, "G" + casa.getID());
			} else if (casa.getGremioID() > 0) {
				GestorSalida.ENVIAR_h_CASA(perso, "G" + casa.getID() + ";" + perso.getGremio().getNombre() + ";"
						+ perso.getGremio().getEmblema() + ";" + casa.getDerechosGremio());
			}
		}
	}
	
	public static byte casaGremio(int gremioID) {
		byte i = 0;
		for (Entry<Integer, Casa> casa : MundoDofus.getCasas().entrySet()) {
			if (casa.getValue().getGremioID() == gremioID) {
				i++;
			}
		}
		return i;
	}
	
	public boolean tieneDerecho(int derecho) {
		return _tieneDerecho.get(derecho);
	}
	
	public void iniciarDerechos() {
		_tieneDerecho.put(CentroInfo.H_GBLASON, false);
		_tieneDerecho.put(CentroInfo.H_OBLASON, false);
		_tieneDerecho.put(CentroInfo.H_SINCODIGOGREMIO, false);
		_tieneDerecho.put(CentroInfo.H_ABRIRGREMIO, false);
		_tieneDerecho.put(CentroInfo.C_SINCODIGOGREMIO, false);
		_tieneDerecho.put(CentroInfo.C_ABRIRGREMIO, false);
		_tieneDerecho.put(CentroInfo.H_DESCANSOGREMIO, false);
		_tieneDerecho.put(CentroInfo.H_TELEPORTGREMIO, false);
	}
	
	public void analizarDerechos(int total) {
		if (_tieneDerecho.isEmpty()) {
			iniciarDerechos();
		}
		if (total == 1)
			return;
		if (_tieneDerecho.size() > 0)
			_tieneDerecho.clear();
		iniciarDerechos();
		Integer[] mapKey = _tieneDerecho.keySet().toArray(new Integer[_tieneDerecho.size()]);
		while (total > 0) {
			for (int i = _tieneDerecho.size() - 1; i < _tieneDerecho.size(); i--) {
				if (mapKey[i].intValue() <= total) {
					total ^= mapKey[i].intValue();
					_tieneDerecho.put(mapKey[i], true);
					break;
				}
			}
		}
	}
	
	public static void salir(Personaje perso, String packet) {
		Casa casa = perso.getCasa();
		if (!casa.esSuCasa(perso, casa))
			return;
		int Pid = Integer.parseInt(packet);
		Personaje objetivo = MundoDofus.getPersonaje(Pid);
		if (objetivo == null || !objetivo.enLinea() || objetivo.getPelea() != null
				|| objetivo.getMapa().getID() != perso.getMapa().getID())
			return;
		objetivo.teleport(casa.getMapaIDFuera(), casa.getCeldaIDFuera());
		GestorSalida.ENVIAR_Im_INFORMACION(objetivo, "018;" + perso.getNombre());
	}
	
	public static Casa getCasaDePj(Personaje perso) {
		try {
			for (Entry<Integer, Casa> entry : MundoDofus.getCasas().entrySet()) {
				Casa casa = entry.getValue();
				if (casa.getDueñoID() == perso.getCuentaID()) {
					return casa;
				}
			}
		} catch (NullPointerException e) {
			return null;
		}
		return null;
	}
	
	public static void borrarCasaGremio(int gremioID) {
		for (Entry<Integer, Casa> entry : MundoDofus.getCasas().entrySet()) {
			Casa casa = entry.getValue();
			if (casa.getGremioID() == gremioID) {
				casa.setDerechosGremio(0);
				casa.setGremioID(0);
			} else {
				continue;
			}
		}
		GestorSQL.BORRAR_CASA_GREMIO(gremioID);
	}
}