
package estaticos;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import estaticos.MundoDofus.ItemSet;

import servidor.ServidorPersonaje;
import variables.Cofre;
import variables.Dragopavo;
import variables.Gremio;
import variables.Mapa;
import variables.PuestoMercadillo;
import variables.Objeto;
import variables.Pelea;
import variables.Personaje;
import variables.Prisma;
import variables.Recaudador;
import variables.Gremio.MiembroGremio;
import variables.Mapa.Celda;
import variables.Mapa.ObjetoInteractivo;
import variables.Mapa.Cercado;
import variables.PuestoMercadillo.ObjetoMercadillo;
import variables.MobModelo.GrupoMobs;
import variables.NPCModelo.NPC;
import variables.Objeto.ObjetoModelo;
import variables.Oficio.StatsOficio;
import variables.Pelea.Luchador;
import variables.Personaje.Grupo;

public class GestorSalida {
	public static void enviar(Personaje perso, String packet) {
		if (perso == null || !perso.enLinea() || perso.getCuenta() == null || perso.getCuenta().getEntradaPersonaje() == null)
			return;
		PrintWriter out = perso.getCuenta().getEntradaPersonaje().getOut();
		if (out != null && !packet.equals("") && !packet.equals("" + (char) 0x00)) {
			packet = Encriptador.aUTF(packet);
			out.print( (packet) + (char) 0x00);
			out.flush();
		}
	}
	
	public static void enviar(PrintWriter out, String packet) {
		if (out != null && !packet.equals("") && !packet.equals("" + (char) 0x00)) {
			packet = Encriptador.aUTF(packet);
			out.print( (packet) + (char) 0x00);
			out.flush();
		}
	}
	
	public static void ENVIAR_HG_SALUDO_JUEGO_GENERAL(PrintWriter out) {
		String packet = "HG";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("SALUDO JUEGO: OUT>>  " + packet);
	}
	
	public static String ENVIAR_HC_CODIGO_LLAVE(PrintWriter out) {
		String alfabeto = "abcdefghijklmnopqrstuvwxyz";
		Random rand = new Random();
		String codigoLlave = "";
		for (int i = 0; i < 32; i++) {
			codigoLlave = codigoLlave + alfabeto.charAt(rand.nextInt(alfabeto.length()));
		}
		String packet = "HC" + codigoLlave;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CODIGO LLAVE: OUT>>" + packet);
		return codigoLlave;
	}
	
	public static void ENVIAR_AlEv_VERSION_DEL_CLIENTE(PrintWriter out) {
		String packet = "AlEv" + CentroInfo.CLIENT_VERSION;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("VERSION CLIENTE: CONEXION>>" + packet);
	}
	
	public static void ENVIAR_AlEf_LOGIN_ERROR(PrintWriter out) {
		String packet = "AlEf";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LOGIN ERROR: CONEXION>>" + packet);
	}
	
	public static void ENVIAR_Af_ABONADOS_POSCOLA(PrintWriter out, int posicion, int totalAbo, int totalNonAbo, String subscribe,
			int colaID) {
		String packet = "Af" + posicion + "|" + totalAbo + "|" + totalNonAbo + "|" + subscribe + "|" + colaID;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MULTIPAQUETES: CONEXION>>" + packet);
	}
	
	public static void ENVIAR_Ad_Ac_AH_AlK_AQ_INFO_CUENTA_Y_SERVER(PrintWriter out, String apodo, int nivel, String pregunta) {
		String packet = "Ad" + apodo + (char) 0x00;
		packet += "Ac4" + (char) 0x00;
		packet += "AH" + Bustemu.SERVER_ID + ";" + MundoDofus.getEstado() + ";110;1" + (char) 0x00;
		packet += "AlK" + nivel + (char) 0x00;
		packet += "AQ" + pregunta.replace(" ", "+");
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CONEXION: OUT>>" + packet);
	}
	
	public static void ENVIAR_AlEb_CUENTA_BANEADA(PrintWriter out) {
		String packet = "AlEb";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CUENTA BANEADA: CONEXION>>" + packet);
	}
	
	public static void ENVIAR_AlEc_MISMA_CUENTA_CONECTADA(PrintWriter out) {
		String packet = "AlEc";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MISMA CUENTA CONECTADA: CONEXION>>" + packet);
	}
	
	public static void ENVIAR_XML_POLICIA(PrintWriter out) {
		String packet = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><cross-domain-policy>"
				+ "<allow-access-from domain=\"*\" to-ports=\"*\" secure=\"false\" />"
				+ "<site-control permitted-cross-domain-policies=\"master-only\" /></cross-domain-policy>";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("XML POLICIA: OUT>>  " + packet);
	}
	
	public static void ENVIAR_AxK_TIEMPO_ABONADO_NRO_PJS(PrintWriter out, int nroPersonajes) {
		String packet = "AxK31536000000";// Tiempo de abono
		if (nroPersonajes > 0)
			packet += "|" + Bustemu.SERVER_ID + "," + nroPersonajes;// Server ID
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("TIEMPO ABON NRO PJS: OUT>>" + packet);
	}
	
	public static void ENVIAR_AXK_O_AYK_IP_SERVER(PrintWriter out, int cuentaID, boolean esHost) {
		String packet = "A";
		if (Bustemu.USAR_IP_CRIPTO) {
			String ip = Bustemu.IP_LOCALHOST && esHost ? Encriptador.encriptarIP("127.0.0.1")
					+ Encriptador.encriptarPuerto(Bustemu.PUERTO_JUEGO) : Bustemu.IP_ENCRIPTADA;
			packet += "XK" + ip + cuentaID;// ip encriptada
		} else {
			String ip = Bustemu.IP_LOCALHOST && esHost ? "127.0.0.1" : Bustemu.IP_PC_SERVER;
			packet += "YK" + ip + ":" + Bustemu.PUERTO_JUEGO + ";" + cuentaID;// ip sin encriptar
		}
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CONEXION IP SERVER: OUT>>" + packet);
	}
	
	public static void GAME_SEND_ATTRIBUTE_FAILED(PrintWriter out) {
		String packet = "ATE";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_ATTRIBUTE_SUCCESS(PrintWriter out) {
		String packet = "ATK0";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_AV_VERSION_REGIONAL(PrintWriter out) {
		String packet = "AV0";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("VERSION DE REGION: OUT>>  " + packet);
	}
	
	public static void ENVIAR_APE2_GENERAR_NOMBRE_RANDOM(PrintWriter out) {
		String packet = "APE2";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GENERAR NOMBRE: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ALK_LISTA_DE_PERSONAJES(PrintWriter out, Map<Integer, Personaje> persos) {
		String packet = "ALK31536000000|" + persos.size();
		for (Entry<Integer, Personaje> entry : persos.entrySet()) {
			packet += entry.getValue().stringParaListaPJsServer();
		}
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LISTA DE PJS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_AAEa_NOMBRE_YA_EXISTENTE(PrintWriter out) {
		String packet = "AAEa";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ERROR NOMBRE EXIST: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_AAEf_MAXIMO_PJS_CREADOS(PrintWriter out) {
		String packet = "AAEf";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ERROR MAX PJS CREADOS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_AAK_CREACION_PJ(PrintWriter out) {
		String packet = "AAK";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CREAR PJ OK: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ADE_ERROR_BORRAR_PJ(PrintWriter out) {
		String packet = "ADE";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ERROR BORRAR PJ: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_AAEF_ERROR_CREAR_PJ(PrintWriter out) {
		String packet = "AAEF";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ERROR CREAR PJ: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ASE_SELECCION_PERSONAJE_FALLIDA(PrintWriter out) {
		String packet = "ASE";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ERROR SELECCION PJ: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_As_STATS_DEL_PJ(Personaje perso) {
		String packet = perso.stringStatsPacket();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("STATS PJ: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Rx_EXP_DONADA_MONTURA(Personaje perso) {
		String packet = "Rx" + perso.getXpDonadaMontura();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("XP DONADA MONTURA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Rn_CAMBIO_NOMBRE_MONTURA(Personaje perso, String nombre) {
		String packet = "Rn" + nombre;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CAMBIO NOMBRE MONTURA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Re_DETALLES_MONTURA(Personaje perso, String simbolo, Dragopavo dragopavo) {
		String packet = "Re" + simbolo;
		if (simbolo.equals("+"))
			packet += dragopavo.detallesMontura();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("DETALLE MONTURA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ASK_PERSONAJE_SELECCIONADO(PrintWriter out, Personaje perso) {
		String packet = "ASK|" + perso.getID() + "|" + perso.getNombre() + "|" + perso.getNivel() + "|" + perso.getClase(false)
				+ "|" + perso.getSexo() + "|" + perso.getGfxID() + "|"
				+ (perso.getColor1() == -1 ? "-1" : Integer.toHexString(perso.getColor1())) + "|"
				+ (perso.getColor2() == -1 ? "-1" : Integer.toHexString(perso.getColor2())) + "|"
				+ (perso.getColor3() == -1 ? "-1" : Integer.toHexString(perso.getColor3())) + "|"
				+ perso.stringPersonajeElegido();
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("PERSONAJE SELECCIONADO: PERSO>>  " + packet);
		// de ser correcto retorna un GC, para crear el ambiente para el personaje
	}
	
	public static void ENVIAR_ASE_DESCONECTAR_DEL_SERVIDOR(PrintWriter out) {
		String packet = "ASE";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("DESCONECTAR PERSONAJE: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ZS_ENVIAR_ALINEACION(PrintWriter out, int alineacionID) {
		String packet = "ZS" + alineacionID;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ENVIAR ALINEACION: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_cC_ACTIVAR_CANALES(PrintWriter out, String canal) {
		String packet = "cC+" + canal;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACTIVAR CANAL: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_al_ESTADO_ZONA_ALINEACION(PrintWriter out) {
		String packet = "al|" + MundoDofus.getAlineacionTodasSubareas();
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("SUBAREAS ALINEACION: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_SLo_MOSTRAR_TODO_HECHIZOS(PrintWriter out, boolean mostrar) {
		String packet = "SLo" + (mostrar ? "+" : "-");
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOSTRAR MAS HECHIZOS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_AR_RESTRICCIONES_PERSONAJE(PrintWriter out, String str) {
		String packet = "AR" + str;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("RESTRICCIONES: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Ow_PODS_DEL_PJ(Personaje perso) {
		String packet = "Ow" + perso.getPodUsados() + "|" + perso.getMaxPod();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PODS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_FO_MOSTRAR_CONEXION_AMIGOS(PrintWriter out, boolean mostrar) {
		String packet = "FO" + (mostrar ? "+" : "-");
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOSTRAR AMIGOS CONEX: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GCK_CREAR_PANTALLA_PJ(PrintWriter out) {
		String packet = "GCK|1|";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CREAR PANTALLA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_BT_TIEMPO_SERVER(PrintWriter out) {
		String packet = ServidorPersonaje.getTiempoServer();
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("TIEMPO SERVER: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_BD_FECHA_SERVER(PrintWriter out) {
		String packet = ServidorPersonaje.getFechaServer();
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("FECHA SERVER: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GDM_CAMBIO_DE_MAPA(PrintWriter out, int id, String fecha, String key) {
		String packet = "GDM|" + id + "|" + fecha + "|" + key;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CAMBIO MAPA: PERSO>>  " + packet);
	}// retorna un GI
	
	public static void ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(Personaje perso, String str) {
		String packet = "GDE|" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("FRAME OBJ EXT: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(Mapa mapa, String str) {
		String packet = "GDE|" + str;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("FRAME OBJ EXT: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GDK_CARGAR_MAPA(PrintWriter out) {
		String packet = "GDK";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CARGAR MAPA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GM_GRUPOMOBS(PrintWriter out, Mapa mapa) {
		String packet = mapa.getGMsGrupoMobs();
		if (packet.equals(""))
			return;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM GRUPOMOBS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GDO_OBJETOS_CRIAS_EN_MAPA(PrintWriter out, Mapa mapa) {// actualiza una celda
		String packet = mapa.getObjetosCria();
		if (packet.equals(""))
			return;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("OBJ CRIANZA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GM_NPCS(PrintWriter out, Mapa mapa) {
		String packet = mapa.getGMsNPCs();
		if (packet.equals("") && packet.length() < 4)
			return;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM NPC: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GM_RECAUDADORES_EN_MAPA(PrintWriter out, Mapa mapa) {
		String packet = Recaudador.enviarGMDeRecaudador(mapa);
		if (packet.equals("") || packet.length() < 4)
			return;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM RECAUDADOR: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GM_PERSONAJES(PrintWriter out, Mapa mapa) {
		String packet = mapa.getGMsPackets();
		if (packet == "")
			return;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM PERSONAJES: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GM_MERCANTES(PrintWriter out, Mapa mapa) {
		String packet = mapa.getGMsMercantes();
		if (packet == "")
			return;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM MERCANTE: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GM_PRISMAS_EN_MAPA(PrintWriter out, Mapa mapa) {
		String packet = mapa.getGMsPrismas();
		if (packet == "")
			return;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM PRISMA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GM_MONTURAS_EN_MAPA(PrintWriter out, Mapa mapa) {
		String packet = mapa.getGMsMonturas();
		if (packet == "")
			return;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM DRAGOPAVO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GM_BORRAR_PJ_A_TODOS(Mapa mapa, int id) {
		String packet = "GM|-" + id;
		for (Personaje z : mapa.getPersos()) {
			enviar(z, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("BORRAR PJ: MAPA ID " + mapa.getID() + ": MAPA>>" + packet);
	}
	
	public static void ENVIAR_GM_BORRAR_LUCHADOR(Pelea pelea, int id, int equipos) {
		String packet = "GM|-" + id;
		for (Luchador luchador : pelea.luchadoresDeEquipo(equipos)) {
			Personaje perso = luchador.getPersonaje();
			if (perso == null || perso.getID() == id)
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("BORRRA LUCH: PELEA ID " + pelea.getID() + ": PELEA>>" + packet);
	}
	
	public static void ENVIAR_GM_AGREGAR_PJ_A_TODOS(Mapa mapa, Personaje perso) {
		String packet = "GM|+" + perso.stringGM();
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("AGREGAR PJ: MAPA ID " + mapa.getID() + ": MAPA>>" + packet);
	}
	
	public static void ENVIAR_GM_GRUPMOBS(Mapa mapa) {
		String packet = mapa.getGMsGrupoMobs();
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM GRUPOMOBS: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GM_GRUPOMOB_A_MAPA(Mapa mapa, GrupoMobs grupoMobs) {
		String packet = "GM|";
		packet += grupoMobs.enviarGM();
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("GM GRUPOMOB: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GM_PERSONAJE_A_MAPA(Mapa mapa, Personaje perso) {
		String packet = mapa.getGMsPackets();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM PERSONAJE: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GM_DRAGOPAVO_A_MAPA(Mapa mapa, Dragopavo dragopavo) {
		String packet = dragopavo.getCriarMontura(mapa.getCercado());
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM DRAGOPAVO: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GM_MERCANTE_A_MAPA(Mapa mapa, String packet) {
		if (packet == "")
			return;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM MERCANTE: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GM_PRISMA_A_MAPA(Mapa mapa, Prisma prisma) {
		String packet = prisma.getGMPrisma();
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM PRISMA: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GA903_UNIRSE_PELEA_Y_ESTAR_OCUPADO(PrintWriter out, int id) {
		String packet = "GA;903;" + id + ";o";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("UNIR PELEA OCUPADO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GA903_UNIRSE_PELEA_Y_OPONENTE_OCUPADO(PrintWriter out, int id) {
		String packet = "GA;903;" + id + ";z";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("UNIR PELEA OPON OCUPADO: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_GA900_DESAFIAR(Mapa mapa, int id, int id2) {
		String packet = "GA;900;" + id + ";" + id2;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("DESAFIAR: MAPA ID " + mapa.getID() + ": MAPA>>" + packet);
	}
	
	public static void ENVIAR_GA902_RECHAZAR_DESAFIO(Mapa mapa, int id, int id2) {
		String packet = "GA;902;" + id + ";" + id2;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("RECHAZAR DESAFIO: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GA901_ACEPTAR_DESAFIO(Mapa mapa, int id, int id2) {
		String packet = "GA;901;" + id + ";" + id2;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACEPTAR DESAFIO: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_fC_CANTIDAD_DE_PELEAS(PrintWriter out, Mapa mapa) {
		String packet = "fC" + mapa.getNumeroPeleas();
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CANTIDAD PELEAS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_fC_CANTIDAD_DE_PELEAS(Mapa mapa) {
		String packet = "fC" + mapa.getNumeroPeleas();
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CANTIDAD PELEAS: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GJK_UNIRSE_PELEA(Personaje perso, int estado, boolean botonCancelar, boolean mostrarBotones,
			boolean espectador, int tiempo, int tipoPelea) {
		String packet = "GJK" + estado + "|" + (botonCancelar ? 1 : 0) + "|" + (mostrarBotones ? 1 : 0) + "|"
				+ (espectador ? 1 : 0) + "|" + tiempo + "|" + tipoPelea;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("UNIRSE PELEA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GJK_UNIRSE_PELEA(Pelea pelea, int equipos, int estado, boolean botonCancelar,
			boolean mostrarBotones, boolean espectador, int tiempo, int tipoPelea) {
		String packet = "GJK" + estado + "|" + (botonCancelar ? 1 : 0) + "|" + (mostrarBotones ? 1 : 0) + "|"
				+ (espectador ? 1 : 0) + "|" + tiempo + "|" + tipoPelea;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("UNIRSE PELEA: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GP_POSICIONES_PELEA(PrintWriter out, String posiciones, int equipo) {
		String packet = "GP" + posiciones + "|" + equipo;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("POSICIONES PELEA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GP_POSICIONES_PELEA(Pelea pelea, int equipos, String posiciones, int equipo) {
		String packet = "GP" + posiciones + "|" + equipo;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("POSICIONES PELEA: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(Mapa mapa, int arg1, int id1, int id2, int cell1, String str1,
			int cell2, String str2) {
		String packet = "Gc+" + id1 + ";" + arg1 + "|" + id1 + ";" + cell1 + ";" + str1 + "|" + id2 + ";" + cell2 + ";" + str2;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOSTRAR BANDERA: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(Personaje perso, int arg1, int id1, int id2, int celda1, String str1,
			int celda2, String str2) {
		String packet = "Gc+" + id1 + ";" + arg1 + "|" + id1 + ";" + celda1 + ";" + str1 + "|" + id2 + ";" + celda2 + ";" + str2;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOSTRAR BANDERA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Gc_BORRAR_BANDERA_EN_MAPA(Mapa mapa, int id) {
		String packet = "Gc-" + id;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("BORRAR BANDERA: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(Mapa mapa, int idInit1, Luchador luchador) {
		String packet = "Gt" + idInit1 + "|+" + luchador.getID() + ";" + luchador.getNombreLuchador() + ";" + luchador.getNivel();
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("AGREGAR NOMBRE ESPADA: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(Personaje perso, int idInit1, String str) {
		String packet = "Gt" + idInit1 + "|+" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("AGREGAR NOMBRE ESPADA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(Mapa mapa, int idInit1, Luchador luchador) {
		String packet = "Gt" + idInit1 + "|-" + luchador.getID();
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("BORRAR NOMBRE ESPADA: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GDO_PONER_OBJETO_CRIA(Mapa mapa, String str) {// actualiza una celda
		String packet = "GDO+" + str;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PONER OBJ CRIA: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_Oa_CAMBIAR_ROPA(Mapa mapa, Personaje perso) {
		String packet = perso.analizarFiguraDelPJ();
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CAMBIAR ROPA: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_Oa_CAMBIAR_ROPA_PELEA(Personaje perso, Pelea pelea) {
		String packet = perso.analizarFiguraDelPJ();
		for (Luchador luchador : pelea.luchadoresDeEquipo(3)) {
			if (luchador.estaRetirado())
				continue;
			Personaje perso1 = luchador.getPersonaje();
			if (perso1 == null || !perso1.enLinea())
				continue;
			enviar(perso1, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CAMBIAR ROPA: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GIC_CAMBIAR_POS_PELEA(Pelea pelea, int equipos, Mapa mapa, int id, int celda) {
		String packet = "GIC|" + id + ";" + celda;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CAMBIAR POS PELEA: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_Go_BOTON_ESPEC_AYUDA_CERRADO(Mapa mapa, char s, char opcion, int id) {
		String packet = "Go" + s + opcion + id;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_GR_TODOS_LUCHADORES_LISTOS(Pelea pelea, int equipos, int id, boolean b) {
		String packet = "GR" + (b ? "1" : "0") + id;
		if (pelea.getEstado() != 2)
			return;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LUCHADORES LISTO: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_Im_INFORMACION_A_TODOS(String str) {
		String packet = "Im" + str;
		for (Personaje perso : MundoDofus.getPJsEnLinea())
			enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INFORMACION: TODOS>>  " + packet);
	}
	
	public static void ENVIAR_Im_INFORMACION(PrintWriter out, String str) {
		String packet = "Im" + str;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INFORMACION: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Im_INFORMACION(Personaje perso, String str) {
		String packet = "Im" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INFORMACION: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ILS_TIEMPO_REGENERAR_VIDA(Personaje perso, int tiempoRegen) {
		String packet = "ILS" + tiempoRegen;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("TIEMPO REGEN VIDA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ILF_CANTIDAD_DE_VIDA(Personaje perso, int cantidad) {
		String packet = "ILF" + cantidad; // si 0, se estanca la regeneracion
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("CANT VIDA REGENERADA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ILS_TIEMPO_REGENERAR_VIDA(PrintWriter out, int tiempoRegen) {
		String packet = "ILS" + tiempoRegen;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("TIEMPO REGEN VIDA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ILF_CANTIDAD_DE_VIDA(PrintWriter out, int cantidad) {
		String packet = "ILF" + cantidad; // si 0, se estanca la regeneracion
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("CANT VIDA REGENERADA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Im_INFORMACION_A_MAPA(Mapa mapa, String id) {
		String packet = "Im" + id;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INFORMACION: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_eUK_EMOTE_MAPA(Mapa mapa, int id, int emote, String tiempo) {
		String packet = "eUK" + id + "|" + emote + "|" + tiempo;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("EMOTE: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_Im_INFORMACION_A_PELEA(Pelea pelea, int equipos, String msj) {
		String packet = "Im" + msj;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INFORMACION: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_cs_CHAT_MENSAJE(Personaje perso, String msj, String color) {
		String packet = "cs<font color='#" + color + "'>" + msj + "</font>";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CHAT: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_cs_CHAT_MENSAJE_A_MAPA(Mapa mapa, String msj, String color) {
		String packet = "cs<font color='#" + color + "'>" + msj + "</font>";
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CHAT: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GA903_ERROR_PELEA(PrintWriter out, char c) {
		String packet = "GA;903;;" + c;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ERROR JUEGO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GIC_UBICACION_LUCHADORES_INICIAR(Pelea pelea, int equipos) {
		String packet = "GIC|";
		for (Luchador p : pelea.luchadoresDeEquipo(3)) {
			if (p.getCeldaPelea() == null)
				continue;
			packet += p.getID() + ";" + p.getCeldaPelea().getID() + "|";
		}
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("UBIC LUCH INICIAR: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES(Pelea pelea, int equipos, Luchador luchador) {
		String packet = "GIC|" + luchador.getID() + ";" + luchador.getCeldaPelea().getID() + "|";
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("APARECER LUCH INVI: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES(Pelea pelea, Luchador luchador, Personaje perso) {
		String packet = "GIC|" + luchador.getID() + ";" + luchador.getCeldaPelea().getID() + "|";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("APARECER LUCH INVI: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GS_EMPEZAR_COMBATE_EQUIPOS(Pelea pelea, int equipos) {
		String packet = "GS";
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INICIAR PELEA: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GS_EMPEZAR_COMBATE(Personaje perso) {
		String packet = "GS";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INICIO PELEA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GTL_ORDEN_JUGADORES(Pelea pelea, int equipos) {
		String packet = pelea.stringOrdenJugadores();
		for (Luchador l : pelea.luchadoresDeEquipo(equipos)) {
			if (l.estaRetirado())
				continue;
			Personaje perso = l.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ORDEN LUCH: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GTL_ORDEN_JUGADORES(Personaje perso, Pelea pelea) {
		String packet = pelea.stringOrdenJugadores();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ORDEN LUCH: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES(Pelea pelea, int equipos) {
		String packet = "GTM";
		for (Luchador luchador : pelea.luchadoresDeEquipo(3)) {
			packet += "|" + luchador.getID() + ";";
			if (luchador.estaMuerto()) {
				packet += "1";
				continue;
			} else {
				packet += "0;" + luchador.getPDVConBuff() + ";";
				int PA = luchador.getPAConBuff();
				if (PA < 0)
					packet += 0 + ";" + luchador.getPMConBuff() + ";";
				else
					packet += PA + ";" + luchador.getPMConBuff() + ";";
				if (luchador.getCeldaPelea() == null)
					packet += "-1";
				else
					packet += (luchador.esInvisible() ? "-1" : luchador.getCeldaPelea().getID());
				packet += ";;" + luchador.getPDVMaxConBuff();
			}
		}
		for (Luchador luchador : pelea.luchadoresDeEquipo(equipos)) {
			if (luchador.estaRetirado())
				continue;
			Personaje perso = luchador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INFO STATS LUCH: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES(Pelea pelea, Personaje perso) {
		String packet = "GTM";
		for (Luchador luchador : pelea.luchadoresDeEquipo(3)) {
			packet += "|" + luchador.getID() + ";";
			if (luchador.estaMuerto()) {
				packet += "1";
				continue;
			} else {
				packet += "0;" + luchador.getPDVConBuff() + ";";
				int PA = luchador.getPAConBuff();
				if (PA < 0)
					packet += 0 + ";" + luchador.getPMConBuff() + ";";
				else
					packet += PA + ";" + luchador.getPMConBuff() + ";";
				if (luchador.getCeldaPelea() == null)
					packet += "-1";
				else
					packet += (luchador.esInvisible() ? "-1" : luchador.getCeldaPelea().getID());
				packet += ";;" + luchador.getPDVMaxConBuff();
			}
		}
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INFO STATS LUCH: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GTS_INICIO_TURNO_PELEA(Pelea pelea, int equipos, int id, int tiempo) {
		String packet = "GTS" + id + "|" + tiempo;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INICIO TURNO: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GTS_INICIO_TURNO_PELEA(Personaje perso, int id, int tiempo) {
		String packet = "GTS" + id + "|" + tiempo;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INICIO TURNO: PERSO>>  " + packet);
	}
	
	// retorna un GC
	public static void ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(Personaje perso) {
		String packet = "GV";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("RESETEAR PANTALLA JUEGO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_pong(PrintWriter out) {
		String packet = "pong";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("DOFUS PONG: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_qpong(PrintWriter out) {
		String packet = "qpong";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("DOFUS QPONG: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GAS_INICIO_DE_ACCION(Pelea pelea, int equipos, int id) {
		String packet = "GAS" + id;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INICIO ACCION: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GA_ACCION_PELEA(Pelea pelea, int equipos, int accionID, String s1, String s2) {
		String packet = "GA;" + accionID + ";" + s1;
		if (!s2.equals(""))
			packet += ";" + s2;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACCION PELEA: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GA_ACCION_DE_JUEGO(PrintWriter out, String respuestaID, String s0, String s1, String s2) {
		String packet = "GA" + respuestaID + ";" + s0;
		if (!s1.equals(""))
			packet += ";" + s1;
		if (!s2.equals(""))
			packet += ";" + s2;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACCION DE JUEGO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GA_ACCION_PELEA_CON_RESPUESTA(Pelea pelea, int equipos, int respuestaID, String s1, String s2,
			String s3) {
		String packet = "GA" + respuestaID + ";" + s1 + ";" + s2 + ";" + s3;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACCION PELEA CON RESP.: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GAMEACTION_A_PELEA(Pelea pelea, int equipos, String packet) {
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAMEACTION: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GAF_FINALIZAR_ACCION(Pelea pelea, int equipos, int i1, int id) {
		String packet = "GAF" + i1 + "|" + id;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("FINALIZAR ACCION: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_BN_NADA(PrintWriter out) {
		String packet = "BN";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("NADA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_BN_NADA(Personaje perso) {
		String packet = "BN";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("NADA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GTF_FIN_DE_TURNO(Pelea pelea, int equipos, int id) {
		String packet = "GTF" + id;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("FIN TURNO: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GTR_TURNO_LISTO(Pelea pelea, int equipos, int id) {
		String packet = "GTR" + id;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("TURNO LISTO: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_cS_EMOTICON_MAPA(Mapa mapa, int id, int pid) {
		String packet = "cS" + id + "|" + pid;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("EMOTE: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_SUE_SUBIR_NIVEL_HECHIZO_ERROR(PrintWriter out) {
		String packet = "SUE";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_SUK_SUBIR_NIVEL_HECHIZO(PrintWriter out, int hechizoID, int nivel) {
		String packet = "SUK" + hechizoID + "~" + nivel;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("SUBIR NIVEL HECHIZOS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_SL_LISTA_HECHIZOS(Personaje perso) {// lanza a la pantalla todos los hechizos actualizados
		String packet = "SL" + perso.stringListaHechizos();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LISTA HECHIZOS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GA103_JUGADOR_MUERTO(Pelea pelea, int equipos, int id) {
		String packet = "GA;103;" + id + ";" + id;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LUCH. MUERTO: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GE_PANEL_RESULTADOS_PELEA(Pelea pelea, int equipos, String packet) {
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PANEL RESULTADOS: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GE_PANEL_RESULTADOS_PELEA(PrintWriter out, Pelea pelea, int ganador) {
		String packet = pelea.getPanelResultados(ganador);
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PANEL RESULTADOS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GIE_EFECTO_HECHIZO(Pelea pelea, int equipos, int tipo, int objetivo, int mParam1, String mParam2,
			String mParam3, String mParam4, int turnos, int hechizoID) {
		String packet = "GIE" + tipo + ";" + objetivo + ";" + mParam1 + ";" + mParam2 + ";" + mParam3 + ";" + mParam4 + ";"
				+ turnos + ";" + hechizoID;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("EFECTO HECHIZO: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GM_LUCHADOR_A_TODA_PELEA(Pelea pelea, int equipos, Mapa mapa) {
		String packet = mapa.getGMsLuchadores();
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM LUCHADOR ENTRA PELEA: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GM_LUCHADORES(Pelea pelea, Mapa mapa, Personaje perso) {
		String packet = mapa.getGMsLuchadores();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM LUCHADORES: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GM_JUGADO_UNIRSE_PELEA(Pelea pelea, int equipos, Luchador luchador) {
		String packet = "GM|+" + luchador.stringGM();
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador != luchador) {
				Personaje perso = peleador.getPersonaje();
				if (perso == null || !perso.enLinea())
					continue;
				enviar(perso, packet);
			}
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LUCH UNIR PELEA: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_fL_LISTA_PELEAS(PrintWriter out, Mapa mapa) {
		String packet = "fL";
		boolean primero = true;
		try {
			for (Entry<Integer, Pelea> entry : mapa.getPeleas().entrySet()) {
				if (!primero) {
					packet += "|";
				}
				String info = entry.getValue().infoPeleasEnMapa();
				if (!info.isEmpty()) {
					packet += info;
					primero = false;
				}
			}
		} catch (ConcurrentModificationException e) {}
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LISTA PELEA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(Personaje perso, String sufijo, int id, String nombre, String msj) {
		String packet = "cMK" + sufijo + "|" + id + "|" + nombre + "|" + msj;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CHAT: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_cMK_CHAT_MENSAJE_MAPA(Mapa mapa, String sufijo, int id, String nombre, String msj) {
		String packet = "cMK" + sufijo + "|" + id + "|" + nombre + "|" + msj;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CHAT: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_cMK_CHAT_MENSAJE_GREMIO(Gremio gremio, String sufijo, int id, String nombre, String msj) {
		String packet = "cMK" + sufijo + "|" + id + "|" + nombre + "|" + msj;
		for (Personaje perso : gremio.getPjMiembros()) {
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CHAT: GREMIO>" + packet);
	}
	
	public static void ENVIAR_cMK_CHAT_MENSAJE_TODOS(String sufijo, int id, String nombre, String msj) {
		String packet = "cMK" + sufijo + "|" + id + "|" + nombre + "|" + msj;
		for (Personaje perso : MundoDofus.getPJsEnLinea())
			enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CHAT: TODOS>>" + packet);
	}
	
	public static void ENVIAR_cMK_CHAT_MENSAJE_ALINEACION(String sufijo, int id, String nombre, String msj, Personaje perso) {
		String packet = "cMK" + sufijo + "|" + id + "|" + nombre + "|" + msj;
		for (Personaje z : MundoDofus.getPJsEnLinea()) {
			if (z.getAlineacion() == perso.getAlineacion()) {
				enviar(z, packet);
			}
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CHAT: ALINEACION>>" + packet);
	}
	
	public static void ENVIAR_cMK_CHAT_MENSAJE_ADMINS(String sufijo, int id, String nombre, String msj) {
		String packet = "cMK" + sufijo + "|" + id + "|" + nombre + "|" + msj;
		for (Personaje perso : MundoDofus.getPJsEnLinea())
			if (perso.enLinea())
				if (perso.getCuenta() != null)
					if (perso.getCuenta().getRango() > 0)
						enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CHAT: ADMINS>>" + packet);
	}
	
	public static void ENVIAR_cMK_CHAT_MENSAJE_PELEA(Pelea pelea, int equipos, String sufijo, int id, String nombre, String msj) {
		String packet = "cMK" + sufijo + "|" + id + "|" + nombre + "|" + msj;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CHAT: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GDZ_ACTUALIZA_ZONA_EN_PELEA(Pelea pelea, int equipos, String sufijo, int celda, int tamaño,
			int color) {
		String packet = "GDZ" + sufijo + celda + ";" + tamaño + ";" + color;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACTUALIZA ZONA: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GDC_ACTUALIZAR_CELDA_EN_PELEA(Pelea pelea, int equipos, int celda) {
		String packet = "GDC" + celda;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACTUALIZAR CELDA: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GDC_AUTORIZAR(Mapa mapa, int celda) {
		String packet = "GDC" + celda + ";aaWaaaaaaa800;1";
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACTUALIZAR CELDA: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GA2_CARGANDO_MAPA(PrintWriter out, int id) {
		String packet = "GA;2;" + id + ";";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CARGANDO MAPA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_cMEf_CHAT_ERROR(PrintWriter out, String nombre) {
		String packet = "cMEf" + nombre;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CHAT ERROR: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_eD_CAMBIAR_ORIENTACION(Mapa mapa, int id, int dir) {
		String packet = "eD" + id + "|" + dir;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CAMBIAR ORIENTACION: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_TB_CINEMA_INICIO_JUEGO(Personaje perso) {
		String packet = "TB";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CINEMA INICIO JUEGO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_TC_CARGAR_TUTORIAL(PrintWriter out, int tutorial) {
		String packet = "TC" + tutorial + "|7001010000";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CARGAR TUTORIAL: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_TT_MOSTRAR_TIP(PrintWriter out, int tutorial) {
		String packet = "TT" + tutorial;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOSTRAR TIP: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(Personaje perso, int tipo, String str) {
		String packet = "ECK" + tipo;
		if (!str.equals(""))
			packet += "|" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PANEL INTERCAMBIOS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(PrintWriter out, int tipo, String str) {
		String packet = "ECK" + tipo;
		if (!str.equals(""))
			packet += "|" + str;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PANEL INTERCAMBIOS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EL_LISTA_OBJETOS_NPC(PrintWriter out, NPC npc) {
		String packet = "EL" + npc.getModeloBD().stringObjetosAVender();
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("LISTA OBJ NPC: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EL_LISTA_OBJETOS_RECAUDADOR(PrintWriter out, Recaudador recau) {
		String packet = "EL" + recau.getListaObjRecaudador();
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LISTA OBJ RECAUDADOR: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EL_LISTA_OBJETOS_DRAGOPAVO(PrintWriter out, Dragopavo drago) {
		String packet = "EL" + drago.getListaObjDragopavo();
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LISTA MOCHILA DP: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EL_LISTA_TIENDA_PERSONAJE(PrintWriter out, Personaje perso) {// lista de objetos de la tienda
		String packet = "EL" + perso.listaTienda();
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LISTA TIENDA PJ: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EV_CERRAR_VENTANAS(PrintWriter out) {
		String packet = "EV";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CERRAR VENTANA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_DCK_CREAR_DIALOGO(PrintWriter out, int id) {
		String packet = "DCK" + id;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CREAR DIALOGO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_DQ_DIALOGO_PREGUNTA(PrintWriter out, String str) {
		String packet = "DQ" + str;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("DIALOGO PREGUNTA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_DV_FINALIZAR_DIALOGO(PrintWriter out) {
		String packet = "DV";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CERRAR DIALOGO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_BAT2_CONSOLA(PrintWriter out, String str) {
		String packet = "BAT2" + str;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CONSOLA COMANDOS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EBE_ERROR_DE_COMPRA(PrintWriter out) {
		String packet = "EBE";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ERROR COMPRA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ESE_ERROR_VENTA(PrintWriter out) {
		String packet = "ESE";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ERROR VENTA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EBK_COMPRADO(PrintWriter out) {
		String packet = "EBK";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("COMPRADO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ESK_VENDIDO(Personaje perso) {
		String packet = "ESK";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("VENDIDO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(Personaje perso, Objeto obj) {
		String packet = "OQ" + obj.getID() + "|" + obj.getCantidad();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CAMBIA CANT OBJETO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_OAKO_APARECER_OBJETO(Personaje perso, Objeto objeto) {
		String packet = "OAKO" + objeto.stringObjetoConGuiño();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("APARECER OBJETO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_OAKO_APARECER_OBJETO(PrintWriter out, Objeto objeto) {
		String packet = "OAKO" + objeto.stringObjetoConGuiño();
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("APARECER OBJETO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_OR_ELIMINAR_OBJETO(Personaje perso, int id) {
		String packet = "OR" + id;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ELIMINAR OBJETO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_OR_ELIMINAR_OBJETO(PrintWriter out, int id) {
		String packet = "OR" + id;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ELIMINAR OBJETO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_OdE_ERROR_ELIMINAR_OBJETO(PrintWriter out) {
		String packet = "OdE";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ERROR ELIMINAR OBJETO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_OM_MOVER_OBJETO(Personaje perso, Objeto obj) {
		String packet = "OM" + obj.getID() + "|";
		if (obj.getPosicion() != -1)
			packet += obj.getPosicion();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOVER OBJETO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_cS_EMOTE_EN_PELEA(Pelea pelea, int equipos, int id, int id2) {
		String packet = "cS" + id + "|" + id2;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("EMOTE PELEA: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_OAEL_ERROR_AGREGAR_OBJETO(PrintWriter out) {
		String packet = "OAEL";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ERROR AGREGAR OBJETO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_AN_MENSAJE_NUEVO_NIVEL(PrintWriter out, int nivel) {
		String packet = "AN" + nivel;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("SUBIO NIVEL: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_AN_MENSAJE_NUEVO_NIVEL(Personaje perso, int nivel) {
		String packet = "AN" + nivel;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("SUBIO NIVEL: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_MENSAJE_A_TODOS_CHAT_COLOR(String msj, String color) {
		String packet = "cs<font color='#" + color + "'>" + msj + "</font>";
		for (Personaje P : MundoDofus.getPJsEnLinea()) {
			enviar(P, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MSJ CHAT COLOR: TODOS>>" + packet);
	}
	
	public static void ENVIAR_OCK_ACTUALIZA_OBJETO(PrintWriter out, Objeto objeto) {
		String packet = "OCK" + objeto.stringObjetoConGuiño();
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACTUALIZA OBJETO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_OCK_ACTUALIZA_OBJETO(Personaje perso, Objeto objeto) {
		String packet = "OCK" + objeto.stringObjetoConGuiño();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACTUALIZA OBJETO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ERK_CONSULTA_INTERCAMBIO(PrintWriter out, int id, int idT, int tipo) {
		String packet = "ERK" + id + "|" + idT + "|" + tipo;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CONSULTA INTERCAMBIO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ERE_ERROR_CONSULTA(PrintWriter out, char c) {
		String packet = "ERE" + c;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CONSULTA ERROR: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EMK_MOVER_OBJETO_LOCAL(PrintWriter out, char tipoOG, String signo, String s1) {
		String packet = "EMK" + tipoOG + signo;
		if (!s1.equals(""))
			packet += s1;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOVER OBJ LOCAL: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EmK_MOVER_OBJETO_DISTANTE(PrintWriter out, char tipoOG, String signo, String s1) {
		String packet = "EmK" + tipoOG + signo;
		if (!s1.equals(""))
			packet += s1;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOVER OBJ DISTANTE: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EmE_ERROR_MOVER_OBJETO_DISTANTE(PrintWriter out, char tipoOG, String signo, String s1) {
		String packet = "EmE" + tipoOG + signo;
		if (!s1.equals(""))
			packet += s1;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOVER OBJ DISTANTE: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EiK_MOVER_OBJETO_TIENDA(PrintWriter out, char tipo, String signo, String s1) {
		String packet = "EiK" + tipo + signo;
		if (!s1.equals(""))
			packet += s1;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOVER OBJ TIENDA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(PrintWriter out, int tipo, String objKama, String signo, String s1) {
		String packet = "Ep" + tipo + "K" + objKama + signo + s1;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PAGO POR TRABAJO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ErK_RESULTADO_TRABAJO(Personaje perso, String objKama, String signo, String s1) {
		String packet = "ErK" + objKama + signo + s1;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("RESULTADO TRABAJO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EK_CHECK_OK_INTERCAMBIO(PrintWriter out, boolean ok, int id) {
		String packet = "EK" + (ok ? "1" : "0") + id;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACEPTAR INTER: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EV_INTERCAMBIO_EFECTUADO(PrintWriter out, char c) {
		String packet = "EV" + c;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INTER EFECTUADO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_PIE_ERROR_INVITACION_GRUPO(PrintWriter out, String s) {
		String packet = "PIE" + s;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ERROR INVIT GRUPO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_PIK_INVITAR_GRUPO(PrintWriter out, String n1, String n2) {
		String packet = "PIK" + n1 + "|" + n2;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INVITAR AL GRUPO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_PCK_CREAR_GRUPO(PrintWriter out, Grupo grupo) {
		String packet = "PCK" + grupo.getLiderGrupo().getNombre();
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CREAR GRUPO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_PL_LIDER_GRUPO(PrintWriter out, Grupo grupo) {
		String packet = "PL" + grupo.getLiderGrupo().getID();
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LIDER GRUPO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_PR_RECHAZAR_INVITACION_GRUPO(Personaje perso) {
		String packet = "PR";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("RECHAZ INVIT GRUPO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_PV_DEJAR_GRUPO(PrintWriter out, String s) {
		String packet = "PV" + s;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("DEJAR GRUPO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_PM_TODOS_MIEMBROS_GRUPO(PrintWriter out, Grupo grupo) {
		String packet = "PM+";
		boolean primero = true;
		for (Personaje p : grupo.getPersos()) {
			if (!primero)
				packet += "|";
			packet += p.stringInfoGrupo();
			primero = false;
		}
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MIEMBROS GRUPO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_PM_AGREGAR_PJ_GRUPO(Grupo grupo, Personaje perso) {
		String packet = "PM+" + perso.stringInfoGrupo();
		for (Personaje P : grupo.getPersos())
			enviar(P, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("AGREGAR PJ GRUPO: GRUPO>>  " + packet);
	}
	
	public static void ENVIAR_PM_ACTUALIZAR_INFO_PJ_GRUPO(Grupo grupo, Personaje perso) {
		String packet = "PM~" + perso.stringInfoGrupo();
		for (Personaje P : grupo.getPersos())
			enviar(P, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACTUALIZAR INFO GRUPO: GRUPO>>  " + packet);
	}
	
	public static void ENVIAR_PM_EXPULSAR_PJ_GRUPO(Grupo grupo, int id) {
		String packet = "PM-" + id;
		for (Personaje P : grupo.getPersos())
			enviar(P, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("EXPULSAR PJ GRUPO: GRUPO>>  " + packet);
	}
	
	public static void ENVIAR_cMK_MENSAJE_CHAT_GRUPO(Grupo grupo, String str, int id, String nombre, String msj) {
		String packet = "cMK" + str + "|" + id + "|" + nombre + "|" + msj + "|";
		for (Personaje P : grupo.getPersos())
			enviar(P, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MSJ CHAT GRUPO: GRUPO>>  " + packet);
	}
	
	public static void ENVIAR_fD_DETALLES_PELEA(PrintWriter out, Pelea pelea) {
		if (pelea == null)
			return;
		String packet = "fD" + pelea.getID() + "|";
		for (Luchador peleador : pelea.luchadoresDeEquipo(1)) {
			if (peleador.esInvocacion())
				continue;
			packet += peleador.getNombreLuchador() + "~" + peleador.getNivel() + ";";
		}
		packet += "|";
		for (Luchador peleador : pelea.luchadoresDeEquipo(2)) {
			if (peleador.esInvocacion())
				continue;
			packet += peleador.getNombreLuchador() + "~" + peleador.getNivel() + ";";
		}
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_IQ_NUMERO_ARRIBA_PJ(Personaje perso, int idPerso, int numero) {
		String packet = "IQ" + idPerso + "|" + numero;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("NUMERO ARRIBA PJ: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_JN_OFICIO_NIVEL(Personaje perso, int oficioID, int nivel) {
		String packet = "JN" + oficioID + "|" + nivel;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("OFICIO NIVEL: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GDF_OBJETOS_INTERACTIVOS(PrintWriter out, Mapa mapa) {
		String packet = mapa.getObjectosGDF();
		if (packet == "")
			return;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("OBJ INTERACTIVOS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(Mapa mapa, Celda celda) {
		ObjetoInteractivo objInteract = celda.getObjetoInterac();
		String packet = "GDF|" + celda.getID() + ";" + objInteract.getEstado() + ";" + (objInteract.esInteractivo() ? "1" : "0");
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ESTADO OBJ INTERACTIVO: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GDF_FORZADO_MAPA(Mapa mapa, String str) {
		String packet = "GDF|" + str;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("EST OBJ INTER FORZADO: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GDF_FORZADO_PERSONAJE(Personaje perso, int celda, int frame, int esInteractivo) {
		String packet = "GDF|" + celda + ";" + frame + ";" + esInteractivo;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("EST OBJ INTER FORZADO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GA_ACCION_JUEGO_AL_MAPA(Mapa mapa, String idUnica, int idAccionModelo, String s1, String s2) {
		String packet = "GA" + idUnica + ";" + idAccionModelo + ";" + s1;
		if (!s2.equals(""))
			packet += ";" + s2;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACCION JUEGO: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_EL_LISTA_OBJETOS_BANCO(Personaje perso) {
		String packet = "EL" + perso.stringBanco();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LISTA OBJ BANCO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_JX_EXPERINENCIA_OFICIO(Personaje perso, ArrayList<StatsOficio> statsOficios) {
		String packet = "JX";
		for (StatsOficio statOficio : statsOficios) {
			packet += "|" + statOficio.getOficio().getID() + ";" + statOficio.getNivel() + ";" + statOficio.getXpString(";")
					+ ";";
		}
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("EXPERIENCIA OFICIO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_JO_OFICIO_OPCIONES(Personaje perso, ArrayList<StatsOficio> statsOficios) {
		for (StatsOficio sm : statsOficios) {
			String packet = "JO" + sm.getPosicion() + "|" + sm.getOpcionBin() + "|" + sm.getSlotsPublico();
			enviar(perso, packet);
			if (Bustemu.MOSTRAR_ENVIOS_SOS)
				System.out.println("OFICIO OPCIONES: PERSO>>  " + packet);
		}
	}
	
	public static void ENVIAR_JO_OFICIO_OPCIONES(Personaje perso, StatsOficio sm) {
		String packet = "JO" + sm.getPosicion() + "|" + sm.getOpcionBin() + "|" + sm.getSlotsPublico();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("OFICIO OPCIONES: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EJ_DESCRIPCION_LIBRO_ARTESANO(Personaje perso, String str) {
		String packet = "EJ" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("DESCRIP LIBRO ARTESANO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Ej_AGREGAR_LIBRO_ARTESANO(Personaje perso, String str) {
		String packet = "Ej" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("AGREG LIBRO ARTESANO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_JS_TRABAJO_POR_OFICIO(Personaje perso, ArrayList<StatsOficio> statsOficios) {
		String packet = "JS";
		for (StatsOficio sm : statsOficios) {
			packet += sm.analizarTrabajolOficio();
		}
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_JR_OLVIDAR_OFICIO(Personaje perso, int id) {
		String packet = "JR" + id;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("OLVIDAR OFICIO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(Personaje perso, String str) {
		String packet = "EsK" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOVER OBJ: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Gf_MOSTRAR_CASILLA_EN_PELEA(Pelea pelea, int equipos, int id, int celdaID) {
		String packet = "Gf" + id + "|" + celdaID;
		for (Luchador peleador : pelea.luchadoresDeEquipo(equipos)) {
			if (peleador.estaRetirado())
				continue;
			Personaje perso = peleador.getPersonaje();
			if (perso == null || !perso.enLinea())
				continue;
			enviar(perso, packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOSTRAR CASILLA: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_Ea_TERMINO_RECETAS(Personaje perso, String str) {
		String packet = "Ea" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("TERMINOS RECETAS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EA_TURNO_RECETA(Personaje perso, String str) {
		String packet = "EA" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("TURNO RECETA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Ec_INICIAR_RECETA(Personaje perso, String str) {
		String packet = "Ec" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INICIAR RECETA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_IO_ICONO_OBJ_INTERACTIVO(Mapa mapa, int id, String str) {
		String packet = "IO" + id + "|" + str;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ICONO OBJ INTERACTIVO: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_FL_LISTA_DE_AMIGOS(Personaje perso) {// lista de amigos
		String packet = "FL" + perso.getCuenta().stringListaAmigos();
		enviar(perso, packet);
		if (perso.getEsposo() != 0) {
			String packet2 = "FS" + perso.getEsposoListaAmigos();
			enviar(perso, packet2);
			if (Bustemu.MOSTRAR_ENVIOS_SOS)
				System.out.println("GAME: ENVIAR>>  " + packet2);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("AMIGOS LINEA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Im0143_AMIGO_CONECTADO(Personaje amigo, Personaje perso) {
		String packet = "Im0143;" + amigo.getCuenta().getApodo() + " (<b><a href='asfunction:onHref,ShowPlayerPopupMenu,"
				+ amigo.getNombre() + "'>" + amigo.getNombre() + "</a></b>)";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MENSAJE AMIGO CONECTADO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_FA_AGREGAR_AMIGO(Personaje perso, String str) {
		String packet = "FA" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("AGREGAR AMIGO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_FD_BORRAR_AMIGO(Personaje perso, String str) {
		String packet = "FD" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("BORRAR AMIGO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_iA_AGREGAR_ENEMIGO(Personaje perso, String str) {
		String packet = "iA" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("AGREGAR ENEMIGO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_iD_BORRAR_ENEMIGO(Personaje perso, String str) {
		String packet = "iD" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("BORRAR ENEMIGO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_iL_LISTA_ENEMIGOS(Personaje perso) {
		String packet = "iL" + perso.getCuenta().stringListaEnemigos();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LISTA ENEMIGOS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Rp_INFORMACION_CERCADO(Personaje perso, Cercado cercado) {
		String packet = "";
		if (cercado == null)
			return;
		packet = "Rp" + cercado.getDueño() + ";" + cercado.getPrecio() + ";" + cercado.getTamaño() + ";"
				+ cercado.getCantObjMax() + ";";
		Gremio G = cercado.getGremio();
		if (G != null) {
			packet += G.getNombre() + ";" + G.getEmblema();
		} else {
			packet += ";";
		}
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INFO CERCADO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_OS_BONUS_SET(Personaje perso, int setID, int numero) {
		String packet = "OS";
		int num = 0;
		if (numero != -1)
			num = numero;
		else
			num = perso.getNroObjEquipadosDeSet(setID);
		if (num == 0)
			packet += "-" + setID;
		else {
			packet += "+" + setID + "|";
			ItemSet IS = MundoDofus.getItemSet(setID);
			if (IS != null) {
				String objetos = "";
				for (ObjetoModelo OM : IS.getObjetosModelos()) {
					if (perso.tieneEquipado(OM.getID())) {
						if (objetos.length() > 0)
							objetos += ";";
						objetos += OM.getID();
					}
				}
				packet += objetos + "|" + IS.getBonusStatPorNroObj(num).convertirStatsAString();
			}
		}
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("BONUS SET: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Rd_DESCRIPCION_MONTURA(Personaje perso, Dragopavo dragopavo) {
		String packet = "Rd" + dragopavo.detallesMontura();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("DESCRIPCION MONTURA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Rr_ESTADO_MONTADO(Personaje perso, String montado) {
		String packet = "Rr" + montado;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ESTADO MONTADO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(Mapa mapa, Personaje perso) {
		String packet = "GM|~" + perso.stringGM();
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("REFRESCAR PJ: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GM_REFRESCAR_PJ_EN_PELEA(Pelea pelea, Luchador luch) {
		String packet = "GM|~" + luch.stringGM();
		for (Luchador peleador : pelea.luchadoresDeEquipo(3)) {
			if (peleador.getPersonaje() == null)
				continue;
			enviar(peleador.getPersonaje(), packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("REFRESCAR PJ: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_Ee_MONTURA_A_ESTABLO(Personaje perso, char c, String s) {
		String packet = "Ee" + c + s;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PANEL MONTURA A ESTABLO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Ef_MONTURA_A_CRIAR(Personaje perso, char c, String s) {
		String packet = "Ef" + c + s;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PANEL MONTURA A CRIAR: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_cC_SUSCRIBIR_CANAL(Personaje perso, char c, String s) {
		String packet = "cC" + c + s;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("SUSCRIBIR CANAL: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GM_AGREGAR_NPC_AL_MAPA(Mapa mapa, NPC npc) {
		String packet = "GM|" + npc.analizarGM();
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM AGREGAR NPC: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GM_AGREGAR_RECAUDADOR_AL_MAPA(Mapa mapa) {
		String str = Recaudador.enviarGMDeRecaudador(mapa);
		if (str.length() < 4)
			return;
		String packet = "GM|" + str;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GM AGREGAR RECAUDADOR: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GDO_OBJETO_TIRADO_AL_SUELO(Mapa mapa, char agre_borr, int celda, int idObjetoMod, int i) {
		String packet = "GDO" + agre_borr + celda + ";" + idObjetoMod + ";" + i;
		for (Personaje z : mapa.getPersos())
			enviar(z, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("OBJ TIRADO SUELO: MAPA>>  " + packet);
	}
	
	public static void ENVIAR_GDO_OBJETO_TIRADO_AL_SUELO(Personaje perso, char agre_borr, int celda, int idObjetoMod, int i) {
		String packet = "GDO" + agre_borr + celda + ";" + idObjetoMod + ";" + i;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("OBJ TIRADO SUELO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_ZC_CAMBIAR_ALINEACION(Personaje perso, int a) {
		String packet = "ZC" + a;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_GIP_PACKET(Personaje perso, int a) {
		String packet = "GIP" + a;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_gn_CREAR_GREMIO(Personaje perso) {
		String packet = "gn";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CREAR GREMIO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_gC_CREAR_PANEL_GREMIO(Personaje perso, String s) {
		String packet = "gC" + s;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CREAR PANEL GREMIO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_gV_CERRAR_PANEL_GREMIO(Personaje perso) {
		String packet = "gV";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CERRAR PANEL GREMIO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_gIM_INFO_MIEMBROS_GREMIO(Personaje perso, Gremio g, char c) {
		String packet = "gIM" + c;
		switch (c) {
			case '+':
				try {
					packet += g.analizarMiembrosGM();
				} catch (NullPointerException e) {}
				break;
			case '-':
				try {
					packet += g.analizarMiembrosGM();
				} catch (NullPointerException e) {}
				break;
		}
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INFO MIEMBROS GREMIO: PERSO>>  " + packet);
	}
	
	public static void GAME_SEND_gIB_PACKET(Personaje perso, String infos) {
		String packet = "gIB" + infos;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_gIH_PACKET(Personaje perso, String infos) {
		String packet = "gIH" + infos;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_gS_PACKET(Personaje perso, MiembroGremio gm) {
		String packet = "gS" + gm.getGremio().getNombre() + "|" + gm.getGremio().getEmblema().replace(',', '|') + "|"
				+ gm.analizarDerechos();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_gJ_PACKET(Personaje perso, String str) {
		String packet = "gJ" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_gK_PACKET(Personaje perso, String str) {
		String packet = "gK" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_gIG_PACKET(Personaje perso, Gremio g) {
		if (g == null)
			return;
		long xpMin = MundoDofus.getExpNivel(g.getNivel())._gremio;
		long xpMax;
		if (MundoDofus.getExpNivel(g.getNivel() + 1) == null) {
			xpMax = -1;
		} else {
			xpMax = MundoDofus.getExpNivel(g.getNivel() + 1)._gremio;
		}
		String packet = "gIG" + (g.getSize() > 9 ? 1 : 0) + "|" + g.getNivel() + "|" + xpMin + "|" + g.getXP() + "|" + xpMax;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_WC_MENU_ZAAP(Personaje perso) {
		String packet = "WC" + perso.stringListaZaap();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MENU ZAAP: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Wp_MENU_PRISMA(Personaje perso) {
		String packet = "Wp" + perso.stringListaPrismas();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MENU PRISMA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_WV_CERRAR_ZAAP(Personaje perso) {
		String packet = "WV";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CERRAR ZAAP: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Ww_CERRAR_PRISMA(Personaje perso) {
		String packet = "Ww";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CERRAR PRISMA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Wc_LISTA_ZAPPIS(Personaje perso, String lista) {
		String packet = "Wc" + perso.getMapa().getID() + "|" + lista;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MENU ZAAPIS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Wv_CERRAR_ZAPPI(Personaje perso) {
		String packet = "Wv";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CERRAR ZAAPIS: PERSO>>  " + packet);
	}
	
	public static void GAME_SEND_WUE_PACKET(Personaje perso) {
		String packet = "WUE";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_EMOTE_LIST(Personaje perso, String s, String s1) {
		String packet = "eL" + s + "|" + s1;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_NO_EMOTE(Personaje perso) {
		String packet = "eUE";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void REALM_SEND_TOO_MANY_PLAYER_ERROR(PrintWriter out) {
		String packet = "AlEw";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void REALM_SEND_REQUIRED_APK(PrintWriter out) {
		String chars = "abcdefghijklmnopqrstuvwxyz";
		String pass = "";
		for (int x = 0; x < 5; x++) {
			int i = (int) Math.floor(Math.random() * 26);
			pass += chars.charAt(i);
		}
		String packet = "APK" + pass;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_BWK(Personaje perso, String str) {
		String packet = "BWK" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_K_CLAVE(Personaje perso, String str) {
		String packet = "K" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_h_CASA(Personaje perso, String str) {
		String packet = "h" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_FORGETSPELL_INTERFACE(char sign, Personaje perso) {
		String packet = "SF" + sign;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_R_PACKET(Personaje perso, String str) {
		String packet = "R" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_gIF_PACKET(Personaje perso, String str) {// info de cercado
		String packet = "gIF" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_gITM_INFO_RECAUDADOR(Personaje perso, String str) {// info de movimiento de recaudador
		String packet = "gITM" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_gITp_INFO_ATACANTES_RECAUDADOR(Personaje perso, String str) {// info de recaudador atacado
		String packet = "gITp" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(Personaje perso, String str) {// info de recaudador player
		String packet = "gITP" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_CP_INFO_DEFENSORES_PRISMA(Personaje perso, String str) {// info de recaudador player
		String packet = "CP" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_Cp_INFO_ATACANTES_PRISMA(Personaje perso, String str) {// info de recaudador player
		String packet = "Cp" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_IH_COORDINAS_UBICACION(Personaje perso, String str) {
		String packet = "IH" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("COORD UBIC: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(Personaje perso, Personaje objetivo) {
		String packet = "IC" + objetivo.getMapa().getX() + "|" + objetivo.getMapa().getY();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PJ BAND COMPAS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_IC_BORRAR_BANDERA_COMPAS(Personaje perso) {
		String packet = "IC|";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("BORRAR BAND COMPAS: PERSO>>  " + packet);
	}
	
	public static void GAME_SEND_gT_PACKET(Personaje perso, String str) {
		String packet = "gT" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_GUILDHOUSE_PACKET(Personaje perso) {
		String packet = "gUT";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_GUILDENCLO_PACKET(Personaje perso) {
		String packet = "gUF";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_EHm_PACKET(Personaje perso, String sign, String str) {
		String packet = "EHm" + sign + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_EHM_PACKET(Personaje perso, String sign, String str) {
		String packet = "EHM" + sign + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_EHP_PACKET(Personaje perso, int modeloID) {
		String packet = "EHP" + modeloID + "|" + MundoDofus.getObjModelo(modeloID).getPrecioPromedio();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_EHl(Personaje perso, PuestoMercadillo mercadillo, int modeloID) {
		String packet = "EHl" + mercadillo.analizarParaEHl(modeloID);
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_EHL_PACKET(Personaje perso, int categ, String modelos) {
		String packet = "EHL" + categ + "|" + modelos;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_EHL_PACKET(Personaje perso, String objetos) { // paquete de lista de objetos en venta
		String packet = "EHL" + objetos;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_HDVITEM_SELLING(Personaje perso) {
		String packet = "EL";
		ObjetoMercadillo[] entries = perso.getCuenta().getObjMercaDePuesto(Math.abs(perso.getIntercambiandoCon()));
		boolean isFirst = true;
		for (ObjetoMercadillo curEntry : entries) {
			if (curEntry == null)
				break;
			if (!isFirst)
				packet += "|";
			packet += curEntry.analizarParaEL();
			isFirst = false;
		}
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void GAME_SEND_WEDDING(Mapa mapa, int accion, int hombre, int mujer, int sacerdote) {
		String packet = "GA;" + accion + ";" + hombre + ";" + hombre + "," + mujer + "," + sacerdote;
		Personaje perso = MundoDofus.getPersonaje(hombre);
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("GAME: ENVIAR>>  " + packet);
	}
	
	public static void ENVIAR_Oa_ACTUALIZAR_FIGURA_PJ(Personaje perso) {
		String packet = "Oa" + perso.getID() + "|" + perso.getStringAccesorios();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("ACTUALIZAR FIGURA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Gd_RETO_A_LOS_LUCHADORES(Pelea pelea, String reto) {
		String packet = "Gd" + reto;
		for (Luchador peleador : pelea.luchadoresDeEquipo(1)) {
			if (peleador.estaRetirado())
				continue;
			enviar(peleador.getPersonaje(), packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOSTRAR RETOS: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_Gd_RETO_A_PERSONAJE(Personaje perso, String reto) { // manda el reto a la pantalla
		String packet = "Gd" + reto;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MOSTRAR RETOS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GdaK_RETO_REALIZADO(Personaje perso, int reto) { // reto ganado
		String packet = "GdaK" + reto;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("RETO GANADO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GdaO_RETO_PERDIDO(Personaje perso, int reto) { // reto perdido
		String packet = "GdaO" + reto;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("RETO PERDIDO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GdaK_RETO_REALIZADO(Pelea pelea, int reto) { // reto ganado
		String packet = "GdaK" + reto;
		for (Luchador peleador : pelea.luchadoresDeEquipo(5)) {
			if (peleador.estaRetirado() || peleador.esInvocacion())
				continue;
			enviar(peleador.getPersonaje(), packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("RETO GANADO: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_GdaO_RETO_PERDIDO(Pelea pelea, int reto) { // reto perdido
		String packet = "GdaO" + reto;
		for (Luchador peleador : pelea.luchadoresDeEquipo(5)) {
			if (peleador.estaRetirado() || peleador.esInvocacion())
				continue;
			enviar(peleador.getPersonaje(), packet);
		}
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("RETO PERDIDO: PELEA>>  " + packet);
	}
	
	public static void ENVIAR_Eq_PREGUNTAR_MERCANTE(Personaje perso, int todoItems, int tasa, long precioPagar) {
		String packet = "Eq" + todoItems + "|" + tasa + "|" + precioPagar;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PREG. MERCANTE: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(Personaje perso, String modificacion) {
		String packet = "SB" + modificacion;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("HECHIZO BOOST: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_M1_MENSAJE_SERVER(Personaje perso, String id, String msj, String nombre) {
		String packet = "M1" + id + "|" + msj + "|" + nombre;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MSJ SERVER: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Im1223_MENSAJE_IMBORRABLE(Personaje perso, String str) {
		String packet = "Im1223;" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("Im1223: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Im1223_MENSAJE_IMBORRABLE(PrintWriter out, String str) {
		String packet = "Im1223;" + str;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("Im1223: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(String str) {
		String packet = "Im1223;" + str;
		for (Personaje perso : MundoDofus.getPJsEnLinea())
			enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("Im1223: TODOS>>  " + packet);
	}
	
	public static void ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(Personaje perso, String str) {
		String packet = "gA" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MSJ SOBRE RECAU: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_am_MENSAJE_ALINEACION_SUBAREA(Personaje perso, String str) {
		String packet = "am" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("MSJ ALIN SUBAREA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_aM_MENSAJE_ALINEACION_AREA(Personaje perso, String str) {
		String packet = "aM" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_STD)
			System.out.println("MSJ ALIN AREA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_CA_MENSAJE_ATAQUE_PRISMA(Personaje perso, String str) {
		String packet = "CA" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MSJ ATAQ PRISMA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_CS_MENSAJE_SOBREVIVIO_PRISMA(Personaje perso, String str) {
		String packet = "CS" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MSJ SOBREVIVIO PRISMA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_CD_MENSAJE_MURIO_PRISMA(Personaje perso, String str) {
		String packet = "CD" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MSJ MURIO PRISMA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_PF_SEGUIR_PERSONAJE(Personaje perso, String str) {
		String packet = "PF" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("SEGUIR PERSO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EL_LISTA_OBJETOS_COFRE(Personaje perso, Cofre cofre) {
		String packet = "EL" + cofre.analizarCofre();
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LISTA OBJ COFRE: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_OT_OBJETO_HERRAMIENTA(PrintWriter out, int id) {
		String packet = "OT";
		if (id > 0)
			packet += id;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("OBJ HERRAMIENTA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EW_OFICIO_MODO_PUBLICO(PrintWriter out, String signo) {
		String packet = "EW" + signo;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MODO PUBLICO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_EW_OFICIO_MODO_INVITACION(PrintWriter out, String signo, int idPerso, String idOficios) {
		String packet = "EW" + signo + idPerso + "|" + idOficios;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("INVITAR TALLER: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Cb_BALANCE_CONQUISTA(Personaje perso, String str) {
		String packet = "Cb" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("BALANCE CONQUISTA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_CB_BONUS_CONQUISTA(Personaje perso, String str) {
		String packet = "CB" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("BONUS CONQUISTA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_CW_INFO_MUNDO_CONQUISTA(Personaje perso, String str) {
		String packet = "CW" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("MUNDO CONQUISTA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_CIJ_INFO_UNIRSE_PRISMA(Personaje perso, String str) {
		String packet = "CIJ" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("UNIRSE PRISMA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_CIV_CERRAR_INFO_CONQUISTA(Personaje perso) {
		String packet = "CIV";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CERRAR INFO CONQUISTA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Ag_LISTA_REGALOS(PrintWriter out, int idObjeto, String codObjeto) {
		String packet = "Ag1|" + idObjeto
				+ "|Regalo Dofus Peruano|Bueno amigos, no llene este campo, ya que me parecía un poco absurdo hacerlo, "
				+ "espero que les guste esta nueva forma de recibir regalos. La hice para entregar premios a los "
				+ "jugadores ganadores de Torneos o Eventos que realizaré más adelante. GRACIAS, ATTE ELBUSTA  xD|SIN FOTO|"
				+ codObjeto;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LISTA REGALOS: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_AG_SIGUIENTE_REGALO(PrintWriter out) {
		String packet = "AGK";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("SIGUIENTE REGALO: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_AlE_CAMBIAR_NOMBRE(PrintWriter out, String letra) {
		String packet = "AlE" + letra;
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("CAMBIAR NOMBRE: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_M145_MENSAJE_PANEL_INFORMACION(Personaje perso, String str) {
		String packet = "M145|" + str;
		enviar(perso, packet);
	}
	
	public static void ENVIAR_M145_MENSAJE_PANEL_INFORMACION(PrintWriter out, String str) {
		String packet = "M145|" + str;
		enviar(out, packet);
	}
	
	public static void ENVIAR_M145_MENSAJE_PANEL_INFORMACION_TODOS(String str) {
		String packet = "M145|" + str;
		for (Personaje perso : MundoDofus.getPJsEnLinea())
			enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PANEL INFORMACION: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_BAIO_MENSAJE_PANEL(Personaje perso, String str) {
		String packet = "BAIO" + str;
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PANEL INFORMACION: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_Ew_PODS_MONTURA(Personaje perso, int pods) {
		String packet = "Ew" + pods + ";1000";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("PODS MONTURA: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_QL_LISTA_MISIONES(Personaje perso, String str) {
		String packet = "QL|442;1;5|220;0;1|185;0;3";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("LISTA MISIONES: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_QS_PASOS_RECOMPENSA_MISION(Personaje perso, String str) {
		String packet = "QS220|253|2857,0;2858,0|||";
		enviar(perso, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("RECOMPENSA MISION: PERSO>>  " + packet);
	}
	
	public static void ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(PrintWriter out) {
		String packet = "GV";
		enviar(out, packet);
		if (Bustemu.MOSTRAR_ENVIOS_SOS)
			System.out.println("RESETEAR PANTALLA JUEGO: PERSO>>  " + packet);
	}
}
