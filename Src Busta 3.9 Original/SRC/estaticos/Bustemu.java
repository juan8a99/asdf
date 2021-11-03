
package estaticos;

import java.io.BufferedReader;
// import java.io.File;
// import java.io.Console;

import java.io.FileReader;

import java.net.InetAddress;
import java.util.ArrayList;
// import java.util.Arrays;

import real.ServidorGeneral;
import servidor.ServidorPersonaje;

public class Bustemu {
	private static final String CONFIG_ARCHIVO = "GavConfig.txt";
	public static boolean estaIniciado = false;
	public static ServidorPersonaje servidorPersonaje;
	public static ServidorGeneral servidorGeneral;
	public static boolean Corriendo = false;
	public static boolean Salvando = false;
	public static String IP_PC_SERVER = "localhost";
	public static String BD_HOST = "localhost";
	public static String BD_USUARIO = "root";
	public static String BD_PASS = "";
	public static String BDLUIS = "";
	public static String BDTANIA = "";
	public static String NOMBRE_SERVER = "";
	public static boolean IP_LOCALHOST = false;
	public static String IP_ENCRIPTADA;
	public static String EVENTO = "";
	public static int MAPA_LAG = 7411;
	public static boolean EXPULSAR = false;
	public static long TIEMPO_COMERCIO_RECLUTA = 45000;
	public static String MENSAJE_BIENVENIDA_1 = "";
	public static String MENSAJE_BIENVENIDA_2 = "SERVER CREADO CON BUSTEMU";
	// public static String MENSAJE_BIENVENIDA_2 = "BIENVENIDOS A BUSTOFUS, DISCULPEN LA AUSENCIA, SE SIGUE MEJORANDO";
	public static String COLOR_MENSAJE = "";
	public static boolean MOSTRAR_RECIBIDOS = false;
	public static boolean MOSTRAR_ENVIOS_SOS = false;
	public static boolean MOSTRAR_ENVIOS_STD = false;
	public static boolean CONFIG_POLICIA = true;
	public static ArrayList<Integer> TIPO_RECURSOS = new ArrayList<Integer>();
	public static ArrayList<Integer> OBJETOS_NO_PERMITIDOS = new ArrayList<Integer>();
	public static ArrayList<Integer> NO_MERCADILLOS_EN = new ArrayList<Integer>();
	public static ArrayList<Integer> ALIMENTOS_MONTURA = new ArrayList<Integer>();
	public static ArrayList<Integer> ARMAS_ENCARNACIONES = new ArrayList<Integer>();
	public static ArrayList<String> PUBLICIDAD = new ArrayList<String>();
	public static String ARMAS = "9544,9545,9546,9547,9548,10125,10126,10127,10133";
	public static int PUERTO_SERVIDOR = 444;
	public static int PUERTO_JUEGO = 5555;
	public static int MAX_PJS_POR_CUENTA = 5;
	public static int MAX_MULTI_CUENTAS = 3;
	public static boolean PERMITIR_MULTICUENTA = true;
	public static int TIEMPO_MOVERSE_PAVOS = 90000;
	public static int MAX_NIVEL_OFICIO = 100;
	public static int TIEMPO_ARENA = 10 * 60 * 1000;
	public static int BD_COMMIT = 30 * 1000;
	public static int SERVER_ID = 1;
	public static int TIEMPO_PELEA = 30 * 1000;
	public static int TIMEPO_SALVAR = 60 * 60 * 1000;
	public static int RECARGA_ESTRELLAS_MOBS = 15 * 60 * 1000;
	public static int LIMITE_JUGADORES = 100;
	public static int INICIAR_NIVEL = 1;
	public static int INICIAR_KAMAS = 0;
	public static int MAX_NIVEL = 200;
	public static int NIVEL_PA1 = 100;
	public static int CANTIDAD_PA1 = 1;
	public static int NIVEL_PM1 = 200;
	public static int CANTIDAD_PM1 = 1;
	public static boolean USAR_ZAAPS = false;
	public static boolean PERMITIR_PVP = false;
	public static boolean USAR_MOBS = false;
	public static boolean USAR_IP_CRIPTO = false;
	public static boolean AURA_ACTIVADA = false;
	public static int LIMITE_ARTESANOS_TALLER = 5;
	public static int CANT_DROP = 1;
	public static int CHAPAS_MISION = 1;
	public static int RATE_DROP = 1;
	public static float RATE_XP_PVP = 10;
	public static float RATE_XP_PVM = 1;
	public static float RATE_KAMAS = 1;
	public static float RATE_HONOR = 1;
	public static float RATE_XP_OFICIO = 1;
	public static float RATE_PORC_FM = 1;
	public static float RATE_CRIANZA_PAVOS = 1;
	public static int RATE_TIEMPO_ALIMENTACION = 5;
	public static int RATE_TIEMPO_PARIR = 1;
	private static float DEFECTO_XP_PVM;
	private static float DEFECTO_XP_PVP;
	private static float DEFECTO_XP_OFICIO;
	private static float DEFECTO_XP_HONOR;
	private static float DEFECTO_PORC_FM;
	private static int DEFECTO_DROP;
	private static float DEFECTO_KAMAS;
	private static float DEFECTO_CRIANZA_PAVOS;
	private static int DEFECTO_TIEMPO_ALIMENTACION;
	private static int DEFECTO_TIEMPO_PARIR;
	private static String DEFECTO_EVENTO;
	public static boolean CONTRA_DDOS = false;
	
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Bustemu.cerrarServer();
			}
		});
		System.out.println("\t                     ELBUSTEMU " + CentroInfo.VERSION_SERVIDOR + " - PERU ");
		System.out.println("\t               Creado Por " + CentroInfo.CREADOR);
		System.out.println("\t               Editado Por " + CentroInfo.CREADOR + " solo para Dofus " + CentroInfo.CLIENT_VERSION);
		System.out.println("\t               Gracias Elbusta Por Crear Este Emulador!");
		System.out.println("\t               Gracias a Gavril Por Subirlo");
		/* Console consola = System.console(); if (consola != null) { boolean autorizado = false;
		 * System.out.println("Verificando Autentificacion"); while (!autorizado) { char[] clave2 =
		 * consola.readPassword("Ingresa la clave de ELBUSTEMU :  "); String clave = "bustofusforever"; if
		 * (String.valueOf(clave2).equals(clave)) { autorizado = true; Arrays.fill(clave2, ' ');
		 * System.out.println("-------- EXCELENTE, ARRANCANDO EMULADOR --------"); } else System.out.println("Clave Incorrecta");
		 * } } */
		System.out.println("Cargando la configuración");
		cargarConfiguracion();
		estaIniciado = true;
		System.out.print("Conexión a la base de datos :");
		if (GestorSQL.IniciarConexion())
			System.out.println(" Conexión OK");
		else {
			System.out.println("Conexión invalida");
			Bustemu.cerrarServer();
			return;
		}
		System.out.println("Creación del Server");
		MundoDofus.crearServer();
		Corriendo = true;
		System.out.println("Lanzamiento del Server, PUERTO JUEGO " + PUERTO_JUEGO);
		String Ip = "";
		try {
			Ip = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {}
			System.exit(1);
		}
		Ip = IP_PC_SERVER;
		servidorGeneral = new ServidorGeneral();
		servidorPersonaje = new ServidorPersonaje(Ip);
		System.out.println("Lanzamiento del Server, PUERTO SERVIDOR " + PUERTO_SERVIDOR);
		if (USAR_IP_CRIPTO)
			System.out.println("Ip del server " + IP_PC_SERVER + " criptografiada " + IP_ENCRIPTADA);
		System.out.println("Atento a las conexiones");
	}
	
	private static void cargarConfiguracion() {
		try {
			BufferedReader config = new BufferedReader(new FileReader(CONFIG_ARCHIVO));
			String linea = "";
			while ( (linea = config.readLine()) != null) {
				if (linea.split("=").length == 1)
					continue;
				String param = linea.split("=")[0].trim();
				String value = linea.split("=")[1].trim();
				if (param.equalsIgnoreCase("ENVIADOS_SOS")) {
					if (value.equalsIgnoreCase("true")) {
						MOSTRAR_ENVIOS_SOS = true;
					}
				} else if (param.equalsIgnoreCase("ENVIADOS_STD")) {
					if (value.equalsIgnoreCase("true")) {
						MOSTRAR_ENVIOS_STD = true;
					}
				} else if (param.equalsIgnoreCase("INICIO_KAMAS")) {
					INICIAR_KAMAS = Integer.parseInt(value);
					if (INICIAR_KAMAS < 0)
						INICIAR_KAMAS = 0;
					if (INICIAR_KAMAS > 1000000000)
						INICIAR_KAMAS = 1000000000;
				} else if (param.equalsIgnoreCase("INICIO_LEVEL")) {
					INICIAR_NIVEL = Integer.parseInt(value);
					if (INICIAR_NIVEL < 1)
						INICIAR_NIVEL = 1;
					if (INICIAR_NIVEL > 200)
						INICIAR_NIVEL = 200;
				} else if (param.equalsIgnoreCase("TIEMPO_PARA_SALVAR")) {
					TIMEPO_SALVAR = Integer.parseInt(value) * 60 * 1000;
				} else if (param.equalsIgnoreCase("KAMAS")) {
					RATE_KAMAS = Float.parseFloat(value);
					DEFECTO_KAMAS = RATE_KAMAS;
				} else if (param.equalsIgnoreCase("HONOR")) {
					RATE_HONOR = Float.parseFloat(value);
					DEFECTO_XP_HONOR = RATE_HONOR;
				} else if (param.equalsIgnoreCase("XP_OFICIO")) {
					RATE_XP_OFICIO = Float.parseFloat(value);
					DEFECTO_XP_OFICIO = RATE_XP_OFICIO;
				} else if (param.equalsIgnoreCase("XP_PVM")) {
					RATE_XP_PVM = Float.parseFloat(value);
					DEFECTO_XP_PVM = RATE_XP_PVM;
				} else if (param.equalsIgnoreCase("XP_PVP")) {
					RATE_XP_PVP = Float.parseFloat(value);
					DEFECTO_XP_PVP = RATE_XP_PVP;
				} else if (param.equalsIgnoreCase("DROP")) {
					RATE_DROP = Integer.parseInt(value);
					DEFECTO_DROP = RATE_DROP;
				} else if (param.equalsIgnoreCase("PORC_FM")) {
					RATE_PORC_FM = Float.parseFloat(value);
					DEFECTO_PORC_FM = RATE_PORC_FM;
				} else if (param.equalsIgnoreCase("CRIANZA_PAVOS")) {
					RATE_CRIANZA_PAVOS = Float.parseFloat(value);
					DEFECTO_CRIANZA_PAVOS = RATE_CRIANZA_PAVOS;
				} else if (param.equalsIgnoreCase("ZAAP")) {
					if (value.equalsIgnoreCase("true")) {
						USAR_ZAAPS = true;
					}
				} else if (param.equalsIgnoreCase("ENCRIPTAR_IP")) {
					if (value.equalsIgnoreCase("true")) {
						USAR_IP_CRIPTO = true;
					}
				} else if (param.equalsIgnoreCase("MENSAJE_BIENVENIDA")) {
					MENSAJE_BIENVENIDA_1 = linea.split("=", 2)[1];
				} else if (param.equalsIgnoreCase("EVENTO")) {
					EVENTO = value;
					DEFECTO_EVENTO = EVENTO;
				} else if (param.equalsIgnoreCase("MENSAJE_COLOR")) {
					COLOR_MENSAJE = value;
				} else if (param.equalsIgnoreCase("GAME_PORT")) {
					PUERTO_JUEGO = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("REALM_PORT")) {
					PUERTO_SERVIDOR = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("IP_SERVER")) {
					IP_PC_SERVER = value;
				} else if (param.equalsIgnoreCase("BD_HOST")) {
					BD_HOST = value;
				} else if (param.equalsIgnoreCase("BD_USUARIO")) {
					BD_USUARIO = value;
				} else if (param.equalsIgnoreCase("BD_PASS")) {
					BD_PASS = value;
				} else if (param.equalsIgnoreCase("BD_LUIS")) {
					BDLUIS = value;
				} else if (param.equalsIgnoreCase("BD_TANIA")) {
					BDTANIA = value;
				} else if (param.equalsIgnoreCase("NOMBRE_SERVER")) {
					NOMBRE_SERVER = value;
				} else if (param.equalsIgnoreCase("TIEMPO_COMERCIO")) {
					TIEMPO_COMERCIO_RECLUTA = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("USAR_MOBS")) {
					USAR_MOBS = value.equalsIgnoreCase("true");
				} else if (param.equalsIgnoreCase("MAX_PERSO_POR_CUENTA")) {
					MAX_PJS_POR_CUENTA = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("MAX_CUENTAS_POR_IP")) {
					MAX_MULTI_CUENTAS = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("PERMITIR_MULTICUENTA")) {
					PERMITIR_MULTICUENTA = value.equalsIgnoreCase("true");
				} else if (param.equalsIgnoreCase("LIMITE_JUGADORES")) {
					LIMITE_JUGADORES = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("ARENA_TIMER")) {
					TIEMPO_ARENA = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("ACTIVAR_AURA")) {
					AURA_ACTIVADA = value.equalsIgnoreCase("true");
				} else if (param.equalsIgnoreCase("CONTRA_DDOS")) {
					CONTRA_DDOS = value.equalsIgnoreCase("true");
				} else if (param.equalsIgnoreCase("PERMITIR_PVP")) {
					PERMITIR_PVP = value.equalsIgnoreCase("true");
				} else if (param.equalsIgnoreCase("MAX_LEVEL")) {
					MAX_NIVEL = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("LEVEL_PA1")) {
					NIVEL_PA1 = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("PA1")) {
					CANTIDAD_PA1 = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("LEVEL_PM1")) {
					NIVEL_PM1 = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("PM1")) {
					CANTIDAD_PM1 = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("SERVER_ID")) {
					SERVER_ID = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("TIEMPO_PELEA")) {
					TIEMPO_PELEA = Integer.parseInt(value) * 1000;
				} else if (param.equalsIgnoreCase("CANT_DROP")) {
					CANT_DROP = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("CHAPAS_MISION")) {
					if (Integer.parseInt(value) > 0)
						CHAPAS_MISION = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("TIEMPO_ALIMENTACION")) {
					RATE_TIEMPO_ALIMENTACION = Integer.parseInt(value);
					DEFECTO_TIEMPO_ALIMENTACION = RATE_TIEMPO_ALIMENTACION;
				} else if (param.equalsIgnoreCase("TIEMPO_PARIR")) {
					RATE_TIEMPO_PARIR = Integer.parseInt(value);
					DEFECTO_TIEMPO_PARIR = RATE_TIEMPO_PARIR;
				} else if (param.equalsIgnoreCase("TIEMPO_MOVERSE_PAVOS")) {
					TIEMPO_MOVERSE_PAVOS = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("PUBLICIDAD_1")) {
					PUBLICIDAD.add(value);
				} else if (param.equalsIgnoreCase("PUBLICIDAD_2")) {
					PUBLICIDAD.add(value);
				} else if (param.equalsIgnoreCase("PUBLICIDAD_3")) {
					PUBLICIDAD.add(value);
				} else if (param.equalsIgnoreCase("PUBLICIDAD_4")) {
					PUBLICIDAD.add(value);
				} else if (param.equalsIgnoreCase("PUBLICIDAD_5")) {
					PUBLICIDAD.add(value);
				} else if (param.equalsIgnoreCase("RECIBIDOS")) {
					if (value.equalsIgnoreCase("true")) {
						MOSTRAR_RECIBIDOS = true;
					}
				} else if (param.equalsIgnoreCase("TIPO_RECURSOS")) {
					for (String str : value.split(",")) {
						TIPO_RECURSOS.add(Integer.parseInt(str));
					}
				} else if (param.equalsIgnoreCase("OBJ_NO_PERMITIDOS")) {
					for (String str : value.split(",")) {
						OBJETOS_NO_PERMITIDOS.add(Integer.parseInt(str));
					}
				} else if (param.equalsIgnoreCase("NO_MERCADILLOS_EN")) {
					for (String str : value.split(",")) {
						NO_MERCADILLOS_EN.add(Integer.parseInt(str));
					}
				} else if (param.equalsIgnoreCase("TIPO_ALIMENTO_MONTURA")) {
					for (String str : value.split(",")) {
						ALIMENTOS_MONTURA.add(Integer.parseInt(str));
					}
				}
			}
			if (BDLUIS == null || BDTANIA == null || BD_HOST == null || BD_PASS == null || BD_USUARIO == null) {
				throw new Exception();
			}
			for (String str : ARMAS.split(",")) {
				ARMAS_ENCARNACIONES.add(Integer.parseInt(str));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Ficha de la configuracion no existe o ilegible");
			System.out.println("Cerrando el server");
			System.exit(1);
		}
	}
	
	public static void cerrarServer() {
		System.out.println("Interrupcion del server ...");
		if (Corriendo) {
			Corriendo = false;
			MundoDofus.salvarServidor(null);
			Bustemu.servidorPersonaje.cerrarServidor();
			GestorSQL.cerrarCons();
		}
		System.out.println("Interrupcion del server: OK");
		Corriendo = false;
	}
	
	public static void resetRates() {
		RATE_XP_PVM = DEFECTO_XP_PVM;
		RATE_XP_PVP = DEFECTO_XP_PVP;
		RATE_XP_OFICIO = DEFECTO_XP_OFICIO;
		RATE_PORC_FM = DEFECTO_PORC_FM;
		RATE_HONOR = DEFECTO_XP_HONOR;
		RATE_DROP = DEFECTO_DROP;
		RATE_KAMAS = DEFECTO_KAMAS;
		RATE_TIEMPO_ALIMENTACION = DEFECTO_TIEMPO_ALIMENTACION;
		RATE_CRIANZA_PAVOS = DEFECTO_CRIANZA_PAVOS;
		RATE_TIEMPO_PARIR = DEFECTO_TIEMPO_PARIR;
		EVENTO = DEFECTO_EVENTO;
	}
}
