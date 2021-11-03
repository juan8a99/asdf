
package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import variables.Cuenta;
import variables.Mapa.Cercado;

import variables.Personaje;

import estaticos.*;

public class ServidorPersonaje implements Runnable {
	private ServerSocket _serverSocket;
	private Thread _thread;
	private ArrayList<EntradaPersonaje> _clientes = new ArrayList<EntradaPersonaje>();
	private ArrayList<Cuenta> _esperando = new ArrayList<Cuenta>();
	private Timer _tiempoSalvada;
	private Timer _subirEstrellas;
	private Timer _rankingPVP;
	private Timer _moverPavos;
	private Timer _publicidad;
	private long _tiempoInicio;
	private int _recordJugadores = 0;
	private String _primeraIp = "";
	private String _segundaIp = "";
	private String _terceraIp = "";
	private int _alterna = 0;
	private long _tiempoBan1 = 0;
	private long _tiempoBan2 = 0;
	private boolean _ban = true;
	private int _i = 0;
	
	public ServidorPersonaje(String Ip) {
		try {
			_tiempoSalvada = new Timer();
			_tiempoSalvada.schedule(new TimerTask() {
				public void run() {
					if (!Bustemu.Salvando) {
						Thread t = new Thread(new salvarServidorPersonaje());
						t.start();
					}
					System.out.println("El servidor ha sido guardado satisfactoriamente");
				}
			}, Bustemu.TIMEPO_SALVAR, Bustemu.TIMEPO_SALVAR);
			_subirEstrellas = new Timer();
			_subirEstrellas.schedule(new TimerTask() {
				public void run() {
					MundoDofus.subirEstrellasMobs();
				}
			}, Bustemu.RECARGA_ESTRELLAS_MOBS, Bustemu.RECARGA_ESTRELLAS_MOBS); // 20 minutos
			_moverPavos = new Timer();
			_moverPavos.schedule(new TimerTask() {
				public void run() {
					for (Cercado cercado : MundoDofus.todosCercados()) {
						cercado.startMoverDrago();
					}
				}
			}, Bustemu.TIEMPO_MOVERSE_PAVOS, Bustemu.TIEMPO_MOVERSE_PAVOS); // 20 minutos
			_publicidad = new Timer();
			_publicidad.schedule(new TimerTask() {
				public void run() {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(Bustemu.PUBLICIDAD.get(_i));
					_i++;
					if (_i >= Bustemu.PUBLICIDAD.size())
						_i = 0;
				}
			}, 1800000, 1800000); // 30 minutos
			_rankingPVP = new Timer();
			_rankingPVP.schedule(new TimerTask() {
				public void run() {
					String antiguoLider = MundoDofus.liderRanking;
					Personaje liderViejo = MundoDofus.getPjPorNombre(antiguoLider);
					if (liderViejo != null)
						liderViejo.setTitulo(0);
					GestorSQL.ACTUALIZAR_TITULO_POR_NOMBRE(antiguoLider);
					int idPerso = MundoDofus.IDLiderRankingPVP();
					Personaje perso = MundoDofus.getPersonaje(idPerso);
					if (perso != null) {
						perso.setTitulo(8);
						MundoDofus.getNPCModelo(1350).configurarNPC(perso.getGfxID(), perso.getSexo(), perso.getColor1(),
								perso.getColor2(), perso.getColor3(), perso.getStringAccesorios());
					}
					MundoDofus.liderRanking = MundoDofus.nombreLiderRankingPVP();
				}
			}, 3600000, 3600000);
			Timer mensaje = new Timer();
			mensaje.schedule(new TimerTask() {
				public void run() {
					GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(Bustemu.MENSAJE_BIENVENIDA_1);
				}
			}, 4500000, 4500000);
			_serverSocket = new ServerSocket(Bustemu.PUERTO_JUEGO);
			if (Bustemu.USAR_IP_CRIPTO)
				Bustemu.IP_ENCRIPTADA = Encriptador.encriptarIP(Ip) + Encriptador.encriptarPuerto(Bustemu.PUERTO_JUEGO);
			_tiempoInicio = System.currentTimeMillis();
			_thread = new Thread(this);
			_thread.start();// runnea el thread para especificar el se
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			Bustemu.cerrarServer();
		}
	}
	public static class salvarServidorPersonaje implements Runnable {
		public synchronized void run() {
			if (!Bustemu.Salvando) {
				GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1164");
				MundoDofus.salvarServidor(null);
				GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1165");
			}
		}
	}
	
	public ArrayList<EntradaPersonaje> getClientes() {
		return _clientes;
	}
	
	public long getTiempoInicio() {
		return _tiempoInicio;
	}
	
	public int getRecordJugadores() {
		return _recordJugadores;
	}
	
	public int nroJugadoresLinea() {
		return _clientes.size();
	}
	
	public void run() {
		while (Bustemu.Corriendo) {
			try {
				EntradaPersonaje gestor = new EntradaPersonaje(_serverSocket.accept());
				if (Bustemu.CONTRA_DDOS) {
					_alterna++;
					String ipTemporal = gestor.getSock().getInetAddress().getHostAddress();
					if (!gestor.poderEntrar()) {
						continue;
					}
					if (_alterna == 1) {
						_primeraIp = ipTemporal;
						if (_ban) {
							_tiempoBan1 = System.currentTimeMillis();
						} else
							_tiempoBan2 = System.currentTimeMillis();
						_ban = !_ban;
					} else if (_alterna == 2) {
						_segundaIp = ipTemporal;
						if (_ban) {
							_tiempoBan1 = System.currentTimeMillis();
						} else
							_tiempoBan2 = System.currentTimeMillis();
						_ban = !_ban;
					} else {
						_terceraIp = ipTemporal;
						_alterna = 0;
						if (_ban) {
							_tiempoBan1 = System.currentTimeMillis();
						} else
							_tiempoBan2 = System.currentTimeMillis();
						_ban = !_ban;
					}
					if ( (_primeraIp.compareTo(ipTemporal) == 0) && (_segundaIp.compareTo(ipTemporal) == 0)
							&& (_terceraIp.compareTo(ipTemporal) == 0) && (Math.abs(_tiempoBan1 - _tiempoBan2)) < 1000) {
						CentroInfo.BAN_IP += "," + ipTemporal;
						GestorSQL.AGREGAR_BANIP(ipTemporal);
						System.out.println("IP BANEADA : " + ipTemporal);
						GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("La IP " + ipTemporal
								+ " fue baneada por atacar el servidor, este mensaje es automatico, y "
								+ "programado por Elbusta, funciona las 24 horas, sin la presencia del creador.");
						gestor.getSock().close();
						continue;
					}
				}
				_clientes.add(gestor);
				if (_clientes.size() > _recordJugadores)
					_recordJugadores = _clientes.size();
			} catch (IOException e) {
				System.out.println("IOException: " + e.getMessage());
				try {
					if (!_serverSocket.isClosed())
						_serverSocket.close();
					Bustemu.cerrarServer();
				} catch (IOException e1) {}
			}
		}
	}
	
	public void cerrarServidor() {
		try {
			_serverSocket.close();
		} catch (IOException e) {}
		ArrayList<EntradaPersonaje> c = new ArrayList<EntradaPersonaje>();
		c.addAll(_clientes);
		for (EntradaPersonaje GE : c) {
			try {
				GE.closeSocket();
			} catch (Exception e) {}
		}
	}
	
	public void delGestorCliente(EntradaPersonaje gameThread) {
		_clientes.remove(gameThread);
		if (_clientes.size() > _recordJugadores)
			_recordJugadores = _clientes.size();
	}
	
	public synchronized Cuenta getEsperandoCuenta(int id) {
		for (int i = 0; i < _esperando.size(); i++) {
			if (_esperando.get(i).getID() == id)
				return _esperando.get(i);
		}
		return null;
	}
	
	public synchronized void delEsperandoCuenta(Cuenta cuenta) {
		_esperando.remove(cuenta);
	}
	
	public synchronized void addEsperandoCuenta(Cuenta cuenta) {
		_esperando.add(cuenta);
	}
	
	public static String getTiempoServer() {
		Date actDate = new Date();
		return "BT" + (actDate.getTime() + 3600000);
	}
	
	public static String getFechaServer() {
		Date actDate = new Date();
		DateFormat fecha = new SimpleDateFormat("dd");
		String dia = Integer.parseInt(fecha.format(actDate)) + "";
		while (dia.length() < 2) {
			dia = "0" + dia;
		}
		fecha = new SimpleDateFormat("MM");
		String mes = (Integer.parseInt(fecha.format(actDate)) - 1) + "";
		while (mes.length() < 2) {
			mes = "0" + mes;
		}
		fecha = new SimpleDateFormat("yyyy");
		String año = (Integer.parseInt(fecha.format(actDate)) - 1370) + "";
		return "BD" + año + "|" + mes + "|" + dia;
	}
	
	public Thread getThread() {
		return _thread;
	}
}
