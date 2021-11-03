
package real;

import java.io.IOException;
import java.net.ServerSocket;

import estaticos.Bustemu;

public class ServidorGeneral implements Runnable {
	private ServerSocket _serverSocket;
	private Thread _thread;
	public static int _totalNoAbonados = 0;// Total de conecciones no abonadas
	public static int _totalAbonodos = 0;// Total de connecciones abonadas
	public static int _nroColaID = -1;// Numéro de la cola ID
	public static int _subscribe = 1;// Fila de no abonados (0) o abonados (1)
	
	public ServidorGeneral() {
		try {
			_serverSocket = new ServerSocket(Bustemu.PUERTO_SERVIDOR);
			_thread = new Thread(this);
			_thread.setDaemon(true);
			_thread.start();
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			Bustemu.cerrarServer();
		}
	}
	
	public void run() {
		while (Bustemu.Corriendo) {
			try {
				new EntradaGeneral(_serverSocket.accept());
			} catch (IOException e) {
				System.out.println("IOException: " + e.getMessage());
				try {
					System.out.println("Cierre del server de conexion");
					if (!_serverSocket.isClosed())
						_serverSocket.close();
				} catch (IOException e1) {}
			}
		}
	}
	
	public void cerrarServidorGeneral() {
		try {
			_serverSocket.close();
		} catch (IOException e) {}
	}
	
	public Thread getThread() {
		return _thread;
	}
}
