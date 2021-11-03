
package real;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import variables.Cuenta;

import estaticos.Bustemu;
import estaticos.CentroInfo;
import estaticos.GestorSQL;
import estaticos.GestorSalida;
import estaticos.MundoDofus;

public class EntradaGeneral implements Runnable {
	private BufferedReader _in;
	private Thread _thread;
	public PrintWriter _out;
	private Socket _socketCuenta;
	private String _codigoLlave;
	private int _packetNum = 0;
	private String _nombreCuenta;
	private String _clave;
	private Cuenta _cuenta;
	
	public EntradaGeneral(Socket socket) {
		try {
			_socketCuenta = socket;
			_in = new BufferedReader(new InputStreamReader(_socketCuenta.getInputStream()));
			_out = new PrintWriter(_socketCuenta.getOutputStream());
			_thread = new Thread(this);
			_thread.setDaemon(true);
			_thread.start();
		} catch (IOException e) {
			try {
				if (!_socketCuenta.isClosed())
					_socketCuenta.close();
			} catch (IOException e1) {}
		} finally {
			if (_cuenta != null) {
				_cuenta.setEntradaGeneral(null);
				_cuenta.setEntradaPersonaje(null);
				_cuenta.setTempIP("");
			}
		}
	}
	
	public void run() {
		try {
			String packet = "";
			char charCur[] = new char[1];
			if (Bustemu.CONFIG_POLICIA)
				GestorSalida.ENVIAR_XML_POLICIA(_out);
			_codigoLlave = GestorSalida.ENVIAR_HC_CODIGO_LLAVE(_out);
			while (_in.read(charCur, 0, 1) != -1 && Bustemu.Corriendo) {
				if (charCur[0] != '\u0000' && charCur[0] != '\n' && charCur[0] != '\r') {
					packet += charCur[0];
				} else if (!packet.isEmpty()) {
					_packetNum++;
					analizar_Packet_Real(packet);
					packet = "";
				}
			}
		} catch (IOException e) {
			try {
				_in.close();
				_out.close();
				if (_cuenta != null) {
					_cuenta.setTempPerso(null);
					_cuenta.setEntradaPersonaje(null);
					_cuenta.setEntradaGeneral(null);
					_cuenta.setTempIP("");
				}
				if (!_socketCuenta.isClosed())
					_socketCuenta.close();
				_thread.interrupt();
			} catch (IOException e1) {}
		} finally {
			try {
				_in.close();
				_out.close();
				if (_cuenta != null) {
					_cuenta.setTempPerso(null);
					_cuenta.setEntradaPersonaje(null);
					_cuenta.setEntradaGeneral(null);
					_cuenta.setTempIP("");
				}
				if (!_socketCuenta.isClosed())
					_socketCuenta.close();
				_thread.interrupt();
			} catch (IOException e1) {}
		}
	}
	
	private void analizar_Packet_Real(String packet) {
		switch (_packetNum) {
			case 1:// Version
				if (!packet.equalsIgnoreCase(CentroInfo.CLIENT_VERSION) && !CentroInfo.IGNORAR_VERSION) {
					GestorSalida.ENVIAR_AlEv_VERSION_DEL_CLIENTE(_out);
					try {
						_socketCuenta.close();
					} catch (IOException e) {}
				}
				break;
			case 2:// nombre de cuenta
				_nombreCuenta = packet.toLowerCase();
				break;
			case 3:// HashPass
				if (!packet.substring(0, 2).equalsIgnoreCase("#1")) {
					try {
						_socketCuenta.close();
					} catch (IOException e) {}
				}
				_clave = packet;
				if (Cuenta.cuentaLogin(_nombreCuenta, _clave, _codigoLlave)) {
					_cuenta = MundoDofus.getCuentaPorNombre(_nombreCuenta);
					if (_cuenta.enLinea() && _cuenta.getEntradaPersonaje() != null) {
						_cuenta.getEntradaPersonaje().closeSocket();
					} else if (_cuenta.enLinea() && _cuenta.getEntradaPersonaje() == null) {
						GestorSalida.ENVIAR_AlEc_MISMA_CUENTA_CONECTADA(_out);
						GestorSalida.ENVIAR_AlEc_MISMA_CUENTA_CONECTADA(_cuenta.getEntradaGeneral()._out);
						return;
					}
					if (_cuenta.estaBaneado()) {
						GestorSalida.ENVIAR_AlEb_CUENTA_BANEADA(_out);
						try {
							_socketCuenta.close();
						} catch (IOException e) {}
						return;
					}
					if (Bustemu.LIMITE_JUGADORES != -1
							&& Bustemu.LIMITE_JUGADORES <= Bustemu.servidorPersonaje.nroJugadoresLinea()) {
						if (_cuenta.getRango() == 0 && _cuenta.getVIP() == 0) {
							GestorSalida.REALM_SEND_TOO_MANY_PLAYER_ERROR(_out);
							try {
								_socketCuenta.close();
							} catch (IOException e) {}
							return;
						}
					}
					if (MundoDofus.getGmAcceso() > _cuenta.getRango()) {
						GestorSalida.REALM_SEND_TOO_MANY_PLAYER_ERROR(_out);
						return;
					}
					String ip = _socketCuenta.getInetAddress().getHostAddress();
					if (CentroInfo.compararConIPBaneadas(ip)) {
						GestorSalida.ENVIAR_AlEb_CUENTA_BANEADA(_out);
						return;
					}
					if (!Bustemu.PERMITIR_MULTICUENTA) {
						if (MundoDofus.usandoIP(ip)) {
							GestorSalida.REALM_SEND_TOO_MANY_PLAYER_ERROR(_out);
							try {
								_socketCuenta.close();
							} catch (IOException e) {}
							return;
						}
					}
					if (MundoDofus.cuentasIP(ip) >= Bustemu.MAX_MULTI_CUENTAS) {
						GestorSalida.REALM_SEND_TOO_MANY_PLAYER_ERROR(_out);
						try {
							_socketCuenta.close();
						} catch (IOException e) {}
						return;
					}
					_cuenta.setEntradaGeneral(this);
					_cuenta.setTempIP(ip);
					ServidorGeneral._totalAbonodos++;
					_cuenta._posicion = ServidorGeneral._totalAbonodos;
					GestorSalida.ENVIAR_Ad_Ac_AH_AlK_AQ_INFO_CUENTA_Y_SERVER(_out, _cuenta.getApodo(),
							(_cuenta.getRango() > 0 ? (1) : (0)), _cuenta.getPregunta());
				} else {
					GestorSQL.CARGAR_CUENTA_POR_NOMBRE(_nombreCuenta);
					if (Cuenta.cuentaLogin(_nombreCuenta, _clave, _codigoLlave)) {
						_cuenta = MundoDofus.getCuentaPorNombre(_nombreCuenta);
						if (_cuenta.enLinea() && _cuenta.getEntradaPersonaje() != null) {
							_cuenta.getEntradaPersonaje().closeSocket();
						} else if (_cuenta.enLinea() && _cuenta.getEntradaPersonaje() == null) {
							GestorSalida.ENVIAR_AlEc_MISMA_CUENTA_CONECTADA(_out);
							GestorSalida.ENVIAR_AlEc_MISMA_CUENTA_CONECTADA(_cuenta.getEntradaGeneral()._out);
							return;
						}
						if (_cuenta.estaBaneado()) {
							GestorSalida.ENVIAR_AlEb_CUENTA_BANEADA(_out);
							try {
								_socketCuenta.close();
							} catch (IOException e) {}
							return;
						}
						if (Bustemu.LIMITE_JUGADORES != -1
								&& Bustemu.LIMITE_JUGADORES <= Bustemu.servidorPersonaje.nroJugadoresLinea()) {
							if (_cuenta.getRango() == 0 && _cuenta.getVIP() == 0) {
								GestorSalida.REALM_SEND_TOO_MANY_PLAYER_ERROR(_out);
								try {
									_socketCuenta.close();
								} catch (IOException e) {}
								return;
							}
						}
						if (MundoDofus.getGmAcceso() > _cuenta.getRango()) {
							GestorSalida.REALM_SEND_TOO_MANY_PLAYER_ERROR(_out);
							return;
						}
						String ip = _socketCuenta.getInetAddress().getHostAddress();
						if (CentroInfo.compararConIPBaneadas(ip)) {
							GestorSalida.ENVIAR_AlEb_CUENTA_BANEADA(_out);
							return;
						}
						if (!Bustemu.PERMITIR_MULTICUENTA) {
							if (MundoDofus.usandoIP(ip)) {
								GestorSalida.REALM_SEND_TOO_MANY_PLAYER_ERROR(_out);
								try {
									_socketCuenta.close();
								} catch (IOException e) {}
								return;
							}
						}
						if (MundoDofus.cuentasIP(ip) >= Bustemu.MAX_MULTI_CUENTAS) {
							GestorSalida.REALM_SEND_TOO_MANY_PLAYER_ERROR(_out);
							try {
								_socketCuenta.close();
							} catch (IOException e) {}
							return;
						}
						_cuenta.setTempIP(ip);
						_cuenta.setEntradaGeneral(this);
						ServidorGeneral._totalAbonodos++;
						_cuenta._posicion = ServidorGeneral._totalAbonodos;
						GestorSalida.ENVIAR_Ad_Ac_AH_AlK_AQ_INFO_CUENTA_Y_SERVER(_out, _cuenta.getApodo(),
								(_cuenta.getRango() > 0 ? (1) : (0)), _cuenta.getPregunta());
					} else {
						GestorSalida.ENVIAR_AlEf_LOGIN_ERROR(_out);
						try {
							_socketCuenta.close();
						} catch (IOException e) {}
					}
				}
				break;
			default:
				if (packet.substring(0, 2).equals("Af")) {
					_packetNum--;
					Pendiente.EnEspera(_cuenta);
				} else if (packet.substring(0, 2).equals("Ax")) {
					if (_cuenta == null)
						return;
					GestorSalida.ENVIAR_AxK_TIEMPO_ABONADO_NRO_PJS(_out, _cuenta.getNumeroPersonajes());
				} else if (packet.equals("AX" + Bustemu.SERVER_ID)) {
					Bustemu.servidorPersonaje.addEsperandoCuenta(_cuenta);
					String ip = _cuenta.getActualIP();
					GestorSalida.ENVIAR_AXK_O_AYK_IP_SERVER(_out, _cuenta.getID(), ip.equals("127.0.0.1"));
				}
				break;
		}
	}
}
