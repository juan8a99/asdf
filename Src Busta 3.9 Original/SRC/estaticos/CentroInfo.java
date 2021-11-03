
package estaticos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import variables.Mapa;
import variables.Pelea;
import variables.Pelea.Luchador;
import variables.Personaje;
import variables.Hechizo.StatsHechizos;
import variables.Objeto.ObjetoModelo;
import variables.Oficio.AccionTrabajo;
import variables.Personaje.Stats;

public class CentroInfo {
	public static int LIMITE_MAPAS = 60000;
	public static final String VERSION_SERVIDOR = "3.9";
	public static final String CREADOR = "ELBUSTA";
	public static final String CLIENT_VERSION = "1.29.1";
	public static final boolean IGNORAR_VERSION = false;
	public static final String ZAAPI_BONTA = "6159,4174,8758,4299,4180,8759,4183,2221,4300,4217,4098,8757,4223,8760,2214,4179,4229,4232,8478,4238,4263,4216,4172,4247,4272,4271,4250,4178,4106,4181,4259,4090,4262,4287,4300,4240,4218,4074,4308";
	public static final String ZAAPI_BRAKMAR = "8756,8755,8493,5304,5311,5277,5317,4612,4618,5112,4639,4637,5116,5332,4579,4588,4549,4562,5334,5295,4646,4629,4601,4551,4607,4930,4622,4620,4615,4595,4627,4623,4604,8754,8753,4630";
	public static Map<Integer, Integer> ZAAPS = new TreeMap<Integer, Integer>();
	public static String BAN_IP = "";
	
	public static boolean compararConIPBaneadas(String ip) {
		String[] split = BAN_IP.split(",");
		for (String ipsplit : split) {
			if (ip.compareTo(ipsplit) == 0)
				return true;
		}
		return false;
	}
	
	public static void borrarIP(String ip) {
		String[] split = BAN_IP.split(",");
		String nuevo = "";
		boolean primero = false;
		for (String ipsplit : split) {
			if (ip.compareTo(ipsplit) == 0)
				continue;
			if (primero)
				nuevo += ",";
			nuevo += ipsplit;
			primero = true;
		}
		BAN_IP = nuevo;
		return;
	}
	// Valore de derechos del gremio
	public static int G_MODIFBOOST = 2; // Modificar los boost
	public static int G_MODIFDERECHOS = 4; // Modificar los derechos
	public static int G_INVITAR = 8; // Invitar a nuevos miembros
	public static int G_BANEAR = 16; // Bannear
	public static int G_TODASXPDONADAS = 32; // Modificar las reparticiones de xp
	public static int G_SUXPDONADA = 256; // Modificar su repartacion de xp
	public static int G_MODRANGOS = 64; // Modificar los rangos
	public static int G_PONERRECAUDADOR = 128; // Poner un recaudador
	public static int G_RECOLECTARRECAUDADOR = 512; // Recolectar los recaudadores
	public static int G_USARCERCADOS = 4096; // Utilizar los cercados
	public static int G_MEJORARCERCADOS = 8192; // Mejorar los cercados
	public static int G_OTRASMONTURAS = 16384; // Modidicar las monturas de otros miembros
	// Valores de derechos de la casa
	public static int H_GBLASON = 2; // Afficher blason por miembro del gremio
	public static int H_OBLASON = 4; // Afficher blason por los autores
	public static int H_SINCODIGOGREMIO = 8; // Entrar sin codigo para el gremio
	public static int H_ABRIRGREMIO = 16; // Los del miembro es imposible entrar
	public static int C_SINCODIGOGREMIO = 32; // Cofres sin codigo para el gremio
	public static int C_ABRIRGREMIO = 64; // Cofre imposible para los q no son del gremio
	public static int H_DESCANSOGREMIO = 256; // Gremio derecho a descanso
	public static int H_TELEPORTGREMIO = 128; // Gremio derecho a la TP
	// ESTADO
	public static final int ESTADO_NEUTRAL = 0;
	public static final int ESTADO_BORRACHO = 1;
	public static final int ESTADO_CAPT_ALMAS = 2;
	public static final int ESTADO_PORTADOR = 3;
	public static final int ESTADO_TEMEROSO = 4;
	public static final int ESTADO_DESORIENTADO = 5;
	public static final int ESTADO_ARRAIGADO = 6;
	public static final int ESTADO_PESADO = 7;
	public static final int ESTADO_TRANSPORTADO = 8;
	public static final int ESTADO_MOTIVACION_SILVESTRE = 9;
	public static final int ESTADO_DOMESTICACIÓN = 10;
	public static final int ESTADO_CABALGANDO = 11;
	public static final int ESTADO_REVOLTOSO = 12;
	public static final int ESTADO_MUY_REVOLTOSO = 13;
	public static final int ESTADO_NEVADO = 14;
	public static final int ESTADO_DESPIERTO = 15;
	public static final int ESTADO_FRAGILIZADO = 16;
	public static final int ESTADO_SEPARADO = 17;
	public static final int ESTADO_HELADO = 18;
	public static final int ESTADO_AGRIETADO = 19;
	public static final int ESTADO_DORMIDO = 26;
	public static final int ESTADO_LEOPARDO = 27;
	public static final int ESTADO_LIBRE = 28;
	public static final int ESTADO_GLIFO_IMPAR = 29;
	public static final int ESTADO_GLIFO_PAR = 30;
	public static final int ESTADO_TINTA_PRIMARIA = 31;
	public static final int ESTADO_TINTA_ECUNDARIA = 32;
	public static final int ESTADO_TINTA_TERCIARIA = 33;
	public static final int ESTADO_TINTA_CUATERNARIA = 34;
	public static final int ESTADO_GANAS_DE_MATAR = 35;
	public static final int ESTADO_GANAS_DE_PARALIZAR = 36;
	public static final int ESTADO_GANAS_DE_MALDECIR = 37;
	public static final int ESTADO_GANAS_DE_ENVENENAR = 38;
	public static final int ESTADO_TURBIO = 39;
	public static final int ESTADO_CORRUPTO = 40;
	public static final int ESTADO_SILENCIOSO = 41;
	public static final int ESTADO_DEBILITADO = 42;
	public static final int ESTADO_OVNI = 43;
	public static final int ESTADO_DESCONTENTA = 44;
	public static final int ESTADO_CONTENTA = 46;
	public static final int ESTADO_DE_MAL_HUMOR = 47;
	public static final int ESTADO_DESCONCERTADO = 48;
	public static final int ESTADO_GHULIFICADO = 49;
	public static final int ESTADO_ALTRUISTA = 50;
	public static final int ESTADO_JUBILADO = 55;
	public static final int ESTADO_LEAL = 60;
	public static final int ESTADO_CAMORRISTA = 61;
	// OBJETOS INTERACTIVOS
	public static final int IO_ESTADO_LLENO = 1;
	public static final int IO_ESTADO_ESPERA = 2;
	public static final int IO_ESTADO_VACIANDO = 3;
	public static final int IO_ESTADO_VACIO = 4;
	public static final int IO_ESTADO_LLENANDO = 5;
	// TIPO DE PELEAS
	public static final int PELEA_TIPO_DESAFIO = 0;// Desafio
	public static final int PELEA_TIPO_PVP = 1;// Agresion PVP
	public static final int PELEA_TIPO_PRISMA = 2;// Conquista
	public static final int PELEA_TIPO_TEMPLO_DOPEUL = 3;
	public static final int PELEA_TIPO_PVM = 4;// PVM personaje vs mobs
	public static final int PELEA_TIPO_RECAUDADOR = 5;// Recaudador
	public static final int PELEA_TIPO_COLISEO = 6; // Coliseo
	// ESTADO DE PELEAS
	public static final int PELEA_ESTADO_INICIADO = 1;
	public static final int PELEA_ESTADO_POSICION = 2;
	public static final int PELEA_ESTADO_ACTIVO = 3;
	public static final int PELEA_ESTADO_FINALIZADO = 4;
	// OFICIOS
	public static final int OFICIO_BASE = 1;
	public static final int OFICIO_LEÑADOR = 2;
	public static final int OFICIO_FORJADOR_ESPADAS = 11;
	public static final int OFICIO_ESCULTOR_ARCOS = 13;
	public static final int OFICIO_FORJADOR_MARTILLOS = 14;
	public static final int OFICIO_ZAPATERO = 15;
	public static final int OFICIO_JOYERO = 16;
	public static final int OFICIO_FORJADOR_DAGAS = 17;
	public static final int OFICIO_ESCULTOR_BASTONES = 18;
	public static final int OFICIO_ESCULTOR_VARITAS = 19;
	public static final int OFICIO_FORJADOR_PALAS = 20;
	public static final int OFICIO_MINERO = 24;
	public static final int OFICIO_PANADERO = 25;
	public static final int OFICIO_ALQUIMISTA = 26;
	public static final int OFICIO_SASTRE = 27;
	public static final int OFICIO_CAMPESINO = 28;
	public static final int OFICIO_FORJADOR_HACHAS = 31;
	public static final int OFICIO_PESCADOR = 36;
	public static final int OFICIO_CAZADOR = 41;
	public static final int OFICIO_FORJAMAGO_DAGAS = 43;
	public static final int OFICIO_FORJAMAGO_ESPADAS = 44;
	public static final int OFICIO_FORJAMAGO_MARTILLOS = 45;
	public static final int OFICIO_FORJAMAGO_PALAS = 46;
	public static final int OFICIO_FORJAMAGO_HACHAS = 47;
	public static final int OFICIO_ESCULTORMAGO_ARCOS = 48;
	public static final int OFICIO_ESCULTORMAGO_VARITAS = 49;
	public static final int OFICIO_ESCULTORMAGO_BASTONES = 50;
	public static final int OFICIO_CARNICERO = 56;
	public static final int OFICIO_PESCADERO = 58;
	public static final int OFICIO_FORJADOR_ESCUDOS = 60;
	public static final int OFICIO_ZAPATEROMAGO = 62;
	public static final int OFICIO_JOYEROMAGO = 63;
	public static final int OFICIO_SASTREMAGO = 64;
	public static final int OFICIO_MANITAS = 65;
	public static final int OFICIO_BIJOYERO = 66;
	public static final int OFICIO_JOYERO2 = 67;
	// POSICION DE OBJETOS
	public static final int ITEM_POS_NO_EQUIPADO = -1;
	public static final int ITEM_POS_AMULETO = 0;
	public static final int ITEM_POS_ARMA = 1;
	public static final int ITEM_POS_ANILLO1 = 2;
	public static final int ITEM_POS_CINTURON = 3;
	public static final int ITEM_POS_ANILLO2 = 4;
	public static final int ITEM_POS_BOTAS = 5;
	public static final int ITEM_POS_SOMBRERO = 6;
	public static final int ITEM_POS_CAPA = 7;
	public static final int ITEM_POS_MASCOTA = 8;
	public static final int ITEM_POS_DOFUS1 = 9;
	public static final int ITEM_POS_DOFUS2 = 10;
	public static final int ITEM_POS_DOFUS3 = 11;
	public static final int ITEM_POS_DOFUS4 = 12;
	public static final int ITEM_POS_DOFUS5 = 13;
	public static final int ITEM_POS_DOFUS6 = 14;
	public static final int ITEM_POS_ESCUDO = 15;
	// TIPOS DE OBJETOS
	public static final int ITEM_TIPO_AMULETO = 1;
	public static final int ITEM_TIPO_ARCO = 2;
	public static final int ITEM_TIPO_VARITA = 3;
	public static final int ITEM_TIPO_BASTON = 4;
	public static final int ITEM_TIPO_DAGAS = 5;
	public static final int ITEM_TIPO_ESPADA = 6;
	public static final int ITEM_TIPO_MARTILLO = 7;
	public static final int ITEM_TIPO_PALA = 8;
	public static final int ITEM_TIPO_ANILLO = 9;
	public static final int ITEM_TIPO_CINTURON = 10;
	public static final int ITEM_TIPO_BOTAS = 11;
	public static final int ITEM_TIPO_POCION = 12;
	public static final int ITEM_TIPO_PERGAMINO_EXP = 13;
	public static final int ITEM_TIPO_DONES = 14;
	public static final int ITEM_TIPO_RECURSO = 15;
	public static final int ITEM_TIPO_SOMBRERO = 16;
	public static final int ITEM_TIPO_CAPA = 17;
	public static final int ITEM_TIPO_MASCOTA = 18;
	public static final int ITEM_TIPO_HACHA = 19;
	public static final int ITEM_TIPO_HERRAMIENTA = 20;
	public static final int ITEM_TIPO_PICO = 21;
	public static final int ITEM_TIPO_GUADAÑA = 22;
	public static final int ITEM_TIPO_DOFUS = 23;
	public static final int ITEM_TIPO_OBJ_BUSQUEDA = 24;
	public static final int ITEM_TIPO_DOCUMENTO = 25;
	public static final int ITEM_TIPO_POCION_FORJAMAGIA = 26;
	public static final int ITEM_TIPO_OBJ_MUTACION = 27;
	public static final int ITEM_TIPO_ALIMENTO_BOOST = 28;
	public static final int ITEM_TIPO_BENDICION = 29;
	public static final int ITEM_TIPO_MALDICION = 30;
	public static final int ITEM_TIPO_ROLEPLAY_BUFF = 31;
	public static final int ITEM_TIPO_PJ_SEGUIDOR = 32;
	public static final int ITEM_TIPO_PAN = 33;
	public static final int ITEM_TIPO_CEREAL = 34;
	public static final int ITEM_TIPO_FLOR = 35;
	public static final int ITEM_TIPO_PLANTA = 36;
	public static final int ITEM_TIPO_CERVEZA = 37;
	public static final int ITEM_TIPO_MADERA = 38;
	public static final int ITEM_TIPO_MINERAL = 39;
	public static final int ITEM_TIPO_ALINEACION = 40;
	public static final int ITEM_TIPO_PEZ = 41;
	public static final int ITEM_TIPO_GOLOSINA = 42;
	public static final int ITEM_TIPO_OLVIDO_HECHIZO = 43;
	public static final int ITEM_TIPO_OLVIDO_OFICIO = 44;
	public static final int ITEM_TIPO_OLVIDO_DOMINIO = 45;
	public static final int ITEM_TIPO_FRUTA = 46;
	public static final int ITEM_TIPO_HUESO = 47;
	public static final int ITEM_TIPO_POLVO = 48;
	public static final int ITEM_TIPO_PESCADO_COMESTIBLE = 49;
	public static final int ITEM_TIPO_PIEDRA_PRECIOSA = 50;
	public static final int ITEM_TIPO_PIEDRA_BRUTA = 51;
	public static final int ITEM_TIPO_HARINA = 52;
	public static final int ITEM_TIPO_PLUMA = 53;
	public static final int ITEM_TIPO_PELO = 54;
	public static final int ITEM_TIPO_TEJIDO = 55;
	public static final int ITEM_TIPO_CUERO = 56;
	public static final int ITEM_TIPO_LANA = 57;
	public static final int ITEM_TIPO_SEMILLA = 58;
	public static final int ITEM_TIPO_PIEL = 59;
	public static final int ITEM_TIPO_ACEITE = 60;
	public static final int ITEM_TIPO_PELUCHE = 61;
	public static final int ITEM_TIPO_PESCADO_VACIADO = 62;
	public static final int ITEM_TIPO_CARNE = 63;
	public static final int ITEM_TIPO_CARNE_CONSERVADA = 64;
	public static final int ITEM_TIPO_COLA = 65;
	public static final int ITEM_TIPO_METARIA = 66;
	public static final int ITEM_TIPO_LEGUMBRE = 68;
	public static final int ITEM_TIPO_CARNE_COMESTIBLE = 69;
	public static final int ITEM_TIPO_TINTE = 70;
	public static final int ITEM_TIPO_MATERIA_ALQUIMIA = 71;
	public static final int ITEM_TIPO_HUEVO_MASCOTA = 72;
	public static final int ITEM_TIPO_DOMINIO = 73;
	public static final int ITEM_TIPO_HADA_ARTIFICIAL = 74;
	public static final int ITEM_TIPO_PERGAMINO_HECHIZO = 75;
	public static final int ITEM_TIPO_PERGAMINO_CARACTERISTICA = 76;
	public static final int ITEM_TIPO_CERTIFICADO_DE_LA_PETRERA = 77;
	public static final int ITEM_TIPO_RUNA_FORJAMAGIA = 78;
	public static final int ITEM_TIPO_BEBIDA = 79;
	public static final int ITEM_TIPO_OBJETO_MISION = 80;
	public static final int ITEM_TIPO_MOCHILA = 81;
	public static final int ITEM_TIPO_ESCUDO = 82;
	public static final int ITEM_TIPO_PIEDRA_DEL_ALMA = 83;
	public static final int ITEM_TIPO_LLAVES = 84;
	public static final int ITEM_TIPO_PIEDRA_DE_ALMA_LLENA = 85;
	public static final int ITEM_TIPO_OLVIDO_RECAUDADOR = 86;
	public static final int ITEM_TIPO_PERGAMINO_BUSQUEDA = 87;
	public static final int ITEM_TIPO_PIEDRA_MAGICA = 88;
	public static final int ITEM_TIPO_REGALOS = 89;
	public static final int ITEM_TIPO_FANTASMA_MASCOTAS = 90;
	public static final int ITEM_TIPO_DRAGOPAVO = 91;
	public static final int ITEM_TIPO_JALATO = 92;
	public static final int ITEM_TIPO_OBJETO_CRIA = 93;
	public static final int ITEM_TIPO_OBJETO_UTILIZABLE = 94;
	public static final int ITEM_TIPO_TABLA = 95;
	public static final int ITEM_TIPO_CORTEZA = 96;
	public static final int ITEM_TIPO_CERTIFICADO_DE_MONTURA = 97;
	public static final int ITEM_TIPO_RAIZ = 98;
	public static final int ITEM_TIPO_RED_CAPTURA = 99;
	public static final int ITEM_TIPO_SACO_RECURSOS = 100;
	public static final int ITEM_TIPO_BALLESTA = 102;
	public static final int ITEM_TIPO_PATA = 103;
	public static final int ITEM_TIPO_ALA = 104;
	public static final int ITEM_TIPO_HUEVO = 105;
	public static final int ITEM_TIPO_OREJA = 106;
	public static final int ITEM_TIPO_CAPARAZON = 107;
	public static final int ITEM_TIPO_BROTE = 108;
	public static final int ITEM_TIPO_OJO = 109;
	public static final int ITEM_TIPO_GELATINA = 110;
	public static final int ITEM_TIPO_CASCARA = 111;
	public static final int ITEM_TIPO_PRISMA = 112;
	public static final int ITEM_TIPO_OBJEVIVO = 113;
	public static final int ITEM_TIPO_ARMA_MAGICA = 114;
	public static final int ITEM_TIPO_FRAGMENTO_ALMA_SHUSHU = 115;
	public static final int ITEM_TIPO_POCION_MASCOTA = 116;
	public static final int ITEM_TIPO_ALIMENTO_MASCOTA = 117;
	// ALINEACION
	public static final int ALINEACION_NEUTRAL = -1;
	public static final int ALINEACION_BONTARIANO = 1;
	public static final int ALINEACION_BRAKMARIANO = 2;
	public static final int ALINEACION_MERCENARIO = 3;
	// ELEMENTOS
	public static final int ELEMENTO_NULO = -1;
	public static final int ELEMENTO_NEUTRAL = 0;
	public static final int ELEMENTO_TIERRA = 1;
	public static final int ELEMENTO_AGUA = 2;
	public static final int ELEMENTO_FUEGO = 3;
	public static final int ELEMENTO_AIRE = 4;
	// CLASES
	public static final int CLASE_FECA = 1;
	public static final int CLASE_OSAMODAS = 2;
	public static final int CLASE_ANUTROF = 3;
	public static final int CLASE_SRAM = 4;
	public static final int CLASE_XELOR = 5;
	public static final int CLASE_ZURCARAK = 6;
	public static final int CLASE_ANIRIPSA = 7;
	public static final int CLASE_YOPUKA = 8;
	public static final int CLASE_OCRA = 9;
	public static final int CLASE_SADIDA = 10;
	public static final int CLASE_SACROGITO = 11;
	public static final int CLASE_PANDAWA = 12;
	public static final int ATORMENTADOR_GOTA = 13;
	public static final int ATORMENTADOR_NUBE = 14;
	public static final int ATORMENTADOR_HOJA = 15;
	public static final int ATORMENTADOR_LLAMAS = 16;
	public static final int ATORMENTADOR_TINIEBLAS = 17;
	public static final int BANDIDO_HECHIZERO = 18;
	public static final int BANDIDO_ARQUERO = 19;
	public static final int BANDIDO_PENDENCIERO = 20;
	public static final int BANDIDO_ESPADACHIN = 21;
	// SEXOS
	public static final int SEXO_MASCULINO = 0;
	public static final int SEXO_FEMENINO = 1;
	// ID EFECTO MAXIMO
	public static final int ID_EFECTO_MAXIMO = 1500;
	public static final int[] BUFF_INICIO_TURNO = { 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 108, 671 };
	public static final int[] ID_EFECTOS_ARMAS = { 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 108 };
	public static final int[] NO_BOOST_CC_IDS = { 101 };
	public static final int[] INVOCACIONES_ESTATICAS = { 282, 556 };// arbol y zanahowia
	public static final int[][] ESTADO_REQUERIDO = { { 699, CentroInfo.ESTADO_BORRACHO }, { 690, CentroInfo.ESTADO_BORRACHO } };
	public static final int[] BUFF_ACCION_RESPUESTA = { 9, 79, 107, 788, 776, 220 };
	// STATS
	public static final int STATS_ADD_PM2 = 78;
	public static final int STATS_REM_PA = 101;
	public static final int STATS_ADD_VIDA = 110;
	public static final int STATS_ADD_PA = 111;
	public static final int STATS_ADD_DAÑOS = 112;
	public static final int STATS_MULTIPLICA_DAÑOS = 114;
	public static final int STATS_ADD_GOLPES_CRITICOS = 115;
	public static final int STATS_REM_ALCANCE = 116;
	public static final int STATS_ADD_ALCANCE = 117;
	public static final int STATS_ADD_FUERZA = 118;
	public static final int STATS_ADD_AGILIDAD = 119;
	public static final int STATS_ADD_PA2 = 120;
	public static final int STATS_ADD_FALLOS_CRITICOS = 122;
	public static final int STATS_ADD_SUERTE = 123;
	public static final int STATS_ADD_SABIDURIA = 124;
	public static final int STATS_ADD_VITALIDAD = 125;
	public static final int STATS_ADD_INTELIGENCIA = 126;
	public static final int STATS_REM_PM = 127;
	public static final int STATS_ADD_PM = 128;
	public static final int STATS_ADD_PORC_DAÑOS = 138;
	public static final int STATS_ADD_DAÑOS_FIS = 142;
	public static final int STATS_REM_DAÑOS = 145;
	public static final int STATS_REM_SUERTE = 152;
	public static final int STATS_REM_VITALIDAD = 153;
	public static final int STATS_REM_AGILIDAD = 154;
	public static final int STATS_REM_INTELIGENCIA = 155;
	public static final int STATS_REM_SABIDURIA = 156;
	public static final int STATS_REM_FUERZA = 157;
	public static final int STATS_ADD_PODS = 158;
	public static final int STATS_REM_PODS = 159;
	public static final int STATS_ADD_ProbPerdida_PA = 160;
	public static final int STATS_ADD_ProbPerdida_PM = 161;
	public static final int STATS_REM_PROB_PERD_PA = 162;
	public static final int STATS_REM_PROB_PERD_PM = 163;
	public static final int STATS_ADD_DOMINIO = 165;
	public static final int STATS_REM_PA_NOESQ = 168;
	public static final int STATS_REM_PM_NOESQ = 169;
	public static final int STATS_REM_GOLPES_CRITICOS = 171;
	public static final int STATS_ADD_INIT = 174;
	public static final int STATS_REM_INIT = 175;
	public static final int STATS_ADD_PROSPECCION = 176;
	public static final int STATS_REM_PROSPECCION = 177;
	public static final int STATS_ADD_CURAS = 178;
	public static final int STATS_REM_CURAS = 179;
	public static final int STATS_ADD_CRIATURAS_INVO = 182;
	public static final int STATS_REM_DAÑOS_PORC = 186;
	public static final int STATS_ADD_ResPorc_TIERRA = 210;
	public static final int STATS_ADD_ResPorc_AGUA = 211;
	public static final int STATS_ADD_ResPorc_AIRE = 212;
	public static final int STATS_ADD_ResPorc_FUEGO = 213;
	public static final int STATS_ADD_ResPorc_NEUTRAL = 214;
	public static final int STATS_REM_RP_TER = 215;
	public static final int STATS_REM_RP_EAU = 216;
	public static final int STATS_REM_RP_AIR = 217;
	public static final int STATS_REM_RP_FEU = 218;
	public static final int STATS_REM_RP_NEU = 219;
	public static final int STATS_REENVIA_DAÑOS = 220;
	public static final int STATS_DAÑOS_TRAMPA = 225;
	public static final int STATS_PORC_TRAMPA = 226;
	public static final int STATS_ADD_R_FUEGO = 240;
	public static final int STATS_ADD_R_NEUTRAL = 241;
	public static final int STATS_ADD_R_TIERRA = 242;
	public static final int STATS_ADD_R_AGUA = 243;
	public static final int STATS_ADD_R_AIRE = 244;
	public static final int STATS_REM_R_FUEGO = 245;
	public static final int STATS_REM_R_NEUTRAL = 246;
	public static final int STATS_REM_R_TIERRA = 247;
	public static final int STATS_REM_R_AGUA = 248;
	public static final int STATS_REM_R_AIRE = 249;
	public static final int STATS_ADD_RP_PVP_TIERRA = 250;
	public static final int STATS_ADD_RP_PVP_AGUA = 251;
	public static final int STATS_ADD_RP_PVP_AIRE = 252;
	public static final int STATS_ADD_RP_PVP_FUEGO = 253;
	public static final int STATS_ADD_RP_PVP_NEUTRAL = 254;
	public static final int STATS_REM_RP_PVP_TER = 255;
	public static final int STATS_REM_RP_PVP_EAU = 256;
	public static final int STATS_REM_RP_PVP_AIR = 257;
	public static final int STATS_REM_RP_PVP_FEU = 258;
	public static final int STATS_REM_RP_PVP_NEU = 259;
	public static final int STATS_ADD_R_PVP_TIERRA = 260;
	public static final int STATS_ADD_R_PVP_AGUA = 261;
	public static final int STATS_ADD_R_PVP_AIRE = 262;
	public static final int STATS_ADD_R_PVP_FUEGO = 263;
	public static final int STATS_ADD_R_PVP_NEUTRAL = 264;
	public static final int STATS_DAÑOS_REDUCIDOS = 264;
	public static final int EFECTO_PASAR_TURNO = 140;
	public static final int CAPTURA_ALMAS = 623;
	// Accion de Oficio {trabajoID, objeto recolectado,obj especial}
	public static final int[][] ACCIONES_TRABAJO = {
			// Bucheron
			{ 101 }, { 6, 303 }, { 39, 473 }, { 40, 476 }, { 10, 460 }, { 141, 2357 }, { 139, 2358 }, { 37, 471 }, { 154, 7013 },
			{ 33, 461 }, { 41, 474 }, { 34, 449 }, { 174, 7925 }, { 155, 7016 },
			{ 38, 472 },
			{ 35, 470 },
			{ 158, 7963 },
			// Minero
			{ 48 }, { 32 }, { 24, 312 }, { 25, 441 }, { 26, 442 }, { 28, 443 }, { 56, 445 }, { 162, 7032 }, { 55, 444 },
			{ 29, 350 }, { 31, 446 },
			{ 30, 313 },
			{ 161, 7033 },
			// Pêcheur
			{ 133 }, { 128, 598, 1786 }, { 128, 1757, 1759 }, { 128, 1750, 1754 }, { 124, 603, 1762 }, { 124, 1782, 1790 },
			{ 124, 1844, 607 }, { 136, 2187 }, { 125, 1847, 1849 }, { 125, 1794, 1796 }, { 140, 1799, 1759 }, { 129, 600, 1799 },
			{ 129, 1805, 1807 }, { 126, 1779, 1792 }, { 130, 1784, 1788 }, { 127, 1801, 1803 }, { 131, 602, 1853 },
			// Alchi
			{ 23 }, { 68, 421 }, { 54, 428 }, { 71, 395 }, { 72, 380 }, { 73, 593 }, { 74, 594 }, { 160, 7059 },
			// Paysan
			{ 122 }, { 47 }, { 45, 289, 2018 }, { 53, 400, 2032 }, { 57, 533, 2036 }, { 46, 401, 2021 }, { 50, 423, 2026 },
			{ 52, 532, 2029 }, { 159, 7018 }, { 58, 405 }, { 54, 425, 2035 },
			// Boulanger
			{ 109 }, { 27 },
			// Poissonier
			{ 135 },
			// Boucher
			{ 132 },
			// Chasseur
			{ 134 },
			// Tailleur
			{ 64 }, { 123 }, { 63 },
			// Bijoutier
			{ 11 }, { 12 },
			// Cordonnier
			{ 13 }, { 14 },
			// Forgeur Epée
			{ 145 }, { 20 },
			// Forgeur Marteau
			{ 144 }, { 19 },
			// Forgeur Dague
			{ 142 }, { 18 },
			// Forgeur Pelle
			{ 146 }, { 21 },
			// Forgeur Hache
			{ 65 }, { 143 },
			// Forgemage de Hache
			{ 115 },
			// Forgemage de dagues
			{ 1 },
			// Forgemage de marteau
			{ 116 },
			// Forgemage d'épée
			{ 113 },
			// Forgemage Pelle
			{ 117 },
			// SculpteMage baton
			{ 120 },
			// Sculptemage de baguette
			{ 119 },
			// Sculptemage d'arc
			{ 118 },
			// Costumage
			{ 165 }, { 166 }, { 167 },
			// Cordomage
			{ 163 }, { 164 },
			// Joyaumage
			{ 169 }, { 168 },
			// Manitas
			{ 171 }, { 182 } };
	
	public static boolean con(String cmd) {
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static void ruta(String dir) {
		File f = new File(dir);
		disminuirLag(f);
	}
	
	public static void disminuirLag(File directorio) {
		File[] ficheros = directorio.listFiles();
		for (int x = 0; x < ficheros.length; x++) {
			try {
				if (ficheros[x].isDirectory()) {
					disminuirLag(ficheros[x]);
				}
				ficheros[x].delete();
			} catch (Exception e) {
				continue;
			}
		}
	}
	
	public static short getMapaInicio(int claseID) {
		short mapa = 7411;
		switch (claseID) {
			case 1:// feca
				mapa = 7398;
				break;
			case 2:// osamodas
				mapa = 7545;
				break;
			case 3:// anutrof
				mapa = 7442;
				break;
			case 4:// sram
				mapa = 7392;
				break;
			case 5:// xelor
				mapa = 7332;
				break;
			case 6:// zurcarak
				mapa = 7446;
				break;
			case 7:// aniripsa
				mapa = 7361;
				break;
			case 8:// yopuka
				mapa = 7427;
				break;
			case 9:// ocra
				mapa = 7378;
				break;
			case 10:// sadida
				mapa = 7395;
				break;
			case 11:// sacrogito
				mapa = 7336;
				break;
			case 12:// pandawa
				mapa = 8035;
				break;
		}
		return mapa;
	}
	
	public static String trabajosOficioTaller(int oficio) {
		switch (oficio) {
			case 11:// forjador espadas
				return "145;20";
			case 13:// escultor de arcos
				return "149;15";
			case 14:// forjador martillos
				return "144;19";
			case 15:// zapatero
				return "14;13";
			case 16:// joyero
				return "12;11";
			case 17:// forjador dagas
				return "142;18";
			case 18:// escultor bastones
				return "147;17";
			case 19:// escultor varitas
				return "148;16";
			case 20:// forjador palas
				return "146;21";
			case 24:// minero
				return "48;32";
			case 25:// panadero
				return "109;27";
			case 26:// alquimista
				return "23";
			case 27:// sastre
				return "123;64;63";
			case 28:// campsino
				return "47;122";
			case 31:// forjador de hachas
				return "143;65";
			case 36:
				return "133";
			case 41:
				return "132";
			case 43:
				return "1";
			case 44:
				return "113";
			case 45:
				return "116";
			case 46:
				return "117";
			case 47:
				return "115";
			case 48:
				return "118";
			case 49:
				return "119";
			case 50:
				return "120";
			case 56:
				return "134";
			case 58:
				return "135";
			case 60:
				return "156";
			case 62:
				return "164;163";
			case 63:
				return "169;168";
			case 64:
				return "167;166;165";
			case 65:
				return "182;171";
		}
		return "";
	}
	
	public static TreeMap<Integer, Character> getLugaresHechizosIniciales(int claseID) {
		TreeMap<Integer, Character> posicionesIniciales = new TreeMap<Integer, Character>();
		switch (claseID) {
			case CLASE_FECA:
				posicionesIniciales.put(3, 'b');
				posicionesIniciales.put(6, 'c');
				posicionesIniciales.put(17, 'd');
				break;
			case CLASE_SRAM:
				posicionesIniciales.put(61, 'b');
				posicionesIniciales.put(72, 'c');
				posicionesIniciales.put(65, 'd');
				break;
			case CLASE_ANIRIPSA:
				posicionesIniciales.put(125, 'b');
				posicionesIniciales.put(128, 'c');
				posicionesIniciales.put(121, 'd');
				break;
			case CLASE_ZURCARAK:
				posicionesIniciales.put(102, 'b');
				posicionesIniciales.put(103, 'c');
				posicionesIniciales.put(105, 'd');
				break;
			case CLASE_OCRA:
				posicionesIniciales.put(161, 'b');
				posicionesIniciales.put(169, 'c');
				posicionesIniciales.put(164, 'd');
				break;
			case CLASE_YOPUKA:
				posicionesIniciales.put(143, 'b');
				posicionesIniciales.put(141, 'c');
				posicionesIniciales.put(142, 'd');
				break;
			case CLASE_SADIDA:
				posicionesIniciales.put(183, 'b');
				posicionesIniciales.put(200, 'c');
				posicionesIniciales.put(193, 'd');
				break;
			case CLASE_OSAMODAS:
				posicionesIniciales.put(34, 'b');
				posicionesIniciales.put(21, 'c');
				posicionesIniciales.put(23, 'd');
				break;
			case CLASE_XELOR:
				posicionesIniciales.put(82, 'b');
				posicionesIniciales.put(81, 'c');
				posicionesIniciales.put(83, 'd');
				break;
			case CLASE_PANDAWA:
				posicionesIniciales.put(686, 'b');
				posicionesIniciales.put(692, 'c');
				posicionesIniciales.put(687, 'd');
				break;
			case CLASE_ANUTROF:
				posicionesIniciales.put(51, 'b');
				posicionesIniciales.put(43, 'c');
				posicionesIniciales.put(41, 'd');
				break;
			case CLASE_SACROGITO:
				posicionesIniciales.put(432, 'b');
				posicionesIniciales.put(431, 'c');
				posicionesIniciales.put(434, 'd');
				break;
		}
		return posicionesIniciales;
	}
	
	public static TreeMap<Integer, StatsHechizos> getHechizosIniciales(int claseID) {
		TreeMap<Integer, StatsHechizos> hechizosIniciales = new TreeMap<Integer, StatsHechizos>();
		switch (claseID) {
			case CLASE_FECA:
				hechizosIniciales.put(3, MundoDofus.getHechizo(3).getStatsPorNivel(1));
				hechizosIniciales.put(6, MundoDofus.getHechizo(6).getStatsPorNivel(1));
				hechizosIniciales.put(17, MundoDofus.getHechizo(17).getStatsPorNivel(1));
				break;
			case CLASE_SRAM:
				hechizosIniciales.put(61, MundoDofus.getHechizo(61).getStatsPorNivel(1));
				hechizosIniciales.put(72, MundoDofus.getHechizo(72).getStatsPorNivel(1));
				hechizosIniciales.put(65, MundoDofus.getHechizo(65).getStatsPorNivel(1));
				break;
			case CLASE_ANIRIPSA:
				hechizosIniciales.put(125, MundoDofus.getHechizo(125).getStatsPorNivel(1));
				hechizosIniciales.put(128, MundoDofus.getHechizo(128).getStatsPorNivel(1));
				hechizosIniciales.put(121, MundoDofus.getHechizo(121).getStatsPorNivel(1));
				break;
			case CLASE_ZURCARAK:
				hechizosIniciales.put(102, MundoDofus.getHechizo(102).getStatsPorNivel(1));
				hechizosIniciales.put(103, MundoDofus.getHechizo(103).getStatsPorNivel(1));
				hechizosIniciales.put(105, MundoDofus.getHechizo(105).getStatsPorNivel(1));
				break;
			case CLASE_OCRA:
				hechizosIniciales.put(161, MundoDofus.getHechizo(161).getStatsPorNivel(1));
				hechizosIniciales.put(169, MundoDofus.getHechizo(169).getStatsPorNivel(1));
				hechizosIniciales.put(164, MundoDofus.getHechizo(164).getStatsPorNivel(1));
				break;
			case CLASE_YOPUKA:
				hechizosIniciales.put(143, MundoDofus.getHechizo(143).getStatsPorNivel(1));
				hechizosIniciales.put(141, MundoDofus.getHechizo(141).getStatsPorNivel(1));
				hechizosIniciales.put(142, MundoDofus.getHechizo(142).getStatsPorNivel(1));
				break;
			case CLASE_SADIDA:
				hechizosIniciales.put(183, MundoDofus.getHechizo(183).getStatsPorNivel(1));
				hechizosIniciales.put(200, MundoDofus.getHechizo(200).getStatsPorNivel(1));
				hechizosIniciales.put(193, MundoDofus.getHechizo(193).getStatsPorNivel(1));
				break;
			case CLASE_OSAMODAS:
				hechizosIniciales.put(34, MundoDofus.getHechizo(34).getStatsPorNivel(1));
				hechizosIniciales.put(21, MundoDofus.getHechizo(21).getStatsPorNivel(1));
				hechizosIniciales.put(23, MundoDofus.getHechizo(23).getStatsPorNivel(1));
				break;
			case CLASE_XELOR:
				hechizosIniciales.put(82, MundoDofus.getHechizo(82).getStatsPorNivel(1));
				hechizosIniciales.put(81, MundoDofus.getHechizo(81).getStatsPorNivel(1));
				hechizosIniciales.put(83, MundoDofus.getHechizo(83).getStatsPorNivel(1));
				break;
			case CLASE_PANDAWA:
				hechizosIniciales.put(686, MundoDofus.getHechizo(686).getStatsPorNivel(1));
				hechizosIniciales.put(692, MundoDofus.getHechizo(692).getStatsPorNivel(1));
				hechizosIniciales.put(687, MundoDofus.getHechizo(687).getStatsPorNivel(1));
				break;
			case CLASE_ANUTROF:
				hechizosIniciales.put(51, MundoDofus.getHechizo(51).getStatsPorNivel(1));
				hechizosIniciales.put(43, MundoDofus.getHechizo(43).getStatsPorNivel(1));
				hechizosIniciales.put(41, MundoDofus.getHechizo(41).getStatsPorNivel(1));
				break;
			case CLASE_SACROGITO:
				hechizosIniciales.put(432, MundoDofus.getHechizo(432).getStatsPorNivel(1));
				hechizosIniciales.put(431, MundoDofus.getHechizo(431).getStatsPorNivel(1));
				hechizosIniciales.put(434, MundoDofus.getHechizo(434).getStatsPorNivel(1));
				break;
		}
		return hechizosIniciales;
	}
	
	public static int getBasePDV(int claseID) {
		switch (claseID) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 12:
				return 50;
			case 11:
				return 100;
		}
		return 50;
	}
	
	public static int getRepartoPuntoSegunClase(int claseID, int statID, int val) {
		switch (statID) {
			case 11:// Vitalidad
				return 1;
			case 12:// Sabiduria
				return 3;
			case 10:// Fuerza
				switch (claseID) {
					case CLASE_SACROGITO:
						return 3;
					case CLASE_FECA:
						if (val < 50)
							return 2;
						if (val < 150)
							return 3;
						if (val < 250)
							return 4;
						return 5;
					case CLASE_XELOR:
						if (val < 50)
							return 2;
						if (val < 150)
							return 3;
						if (val < 250)
							return 4;
						return 5;
					case CLASE_SRAM:
						if (val < 100)
							return 1;
						if (val < 200)
							return 2;
						if (val < 300)
							return 3;
						if (val < 400)
							return 4;
						return 5;
					case CLASE_OSAMODAS:
						if (val < 50)
							return 2;
						if (val < 150)
							return 3;
						if (val < 250)
							return 4;
						return 5;
					case CLASE_ANIRIPSA:
						if (val < 50)
							return 2;
						if (val < 150)
							return 3;
						if (val < 250)
							return 4;
						return 5;
					case CLASE_PANDAWA:
						if (val < 50)
							return 1;
						if (val < 200)
							return 2;
						return 3;
					case CLASE_SADIDA:
						if (val < 50)
							return 1;
						if (val < 250)
							return 2;
						if (val < 300)
							return 3;
						if (val < 400)
							return 4;
						return 5;
					case CLASE_OCRA:
						if (val < 50)
							return 1;
						if (val < 150)
							return 2;
						if (val < 250)
							return 3;
						if (val < 350)
							return 4;
						return 5;
					case CLASE_ANUTROF:
						if (val < 50)
							return 1;
						if (val < 150)
							return 2;
						if (val < 250)
							return 3;
						if (val < 350)
							return 4;
						return 5;
					case CLASE_ZURCARAK:
						if (val < 100)
							return 1;
						if (val < 200)
							return 2;
						if (val < 300)
							return 3;
						if (val < 400)
							return 4;
						return 5;
					case CLASE_YOPUKA:
						if (val < 100)
							return 1;
						if (val < 200)
							return 2;
						if (val < 300)
							return 3;
						if (val < 400)
							return 4;
						return 5;
				}
				break;
			case 13:// Suerte
				switch (claseID) {
					case CLASE_FECA:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
					case CLASE_XELOR:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
					case CLASE_SACROGITO:
						return 3;
					case CLASE_SRAM:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
					case CLASE_SADIDA:
						if (val < 100)
							return 1;
						if (val < 200)
							return 2;
						if (val < 300)
							return 3;
						if (val < 400)
							return 4;
						return 5;
					case CLASE_PANDAWA:
						if (val < 50)
							return 1;
						if (val < 200)
							return 2;
						return 3;
					case CLASE_YOPUKA:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
					case CLASE_ANUTROF:
						if (val < 100)
							return 1;
						if (val < 150)
							return 2;
						if (val < 230)
							return 3;
						if (val < 330)
							return 4;
						return 5;
					case CLASE_OSAMODAS:
						if (val < 100)
							return 1;
						if (val < 200)
							return 2;
						if (val < 300)
							return 3;
						if (val < 400)
							return 4;
						return 5;
					case CLASE_ZURCARAK:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
					case CLASE_ANIRIPSA:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
					case CLASE_OCRA:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
				}
				break;
			case 14:// Agilidad
				switch (claseID) {
					case CLASE_FECA:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
					case CLASE_XELOR:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
					case CLASE_SACROGITO:
						return 3;
					case CLASE_SRAM:
						if (val < 100)
							return 1;
						if (val < 200)
							return 2;
						if (val < 300)
							return 3;
						if (val < 400)
							return 4;
						return 5;
					case CLASE_SADIDA:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
					case CLASE_PANDAWA:
						if (val < 50)
							return 1;
						if (val < 200)
							return 2;
						return 3;
					case CLASE_ANIRIPSA:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
					case CLASE_YOPUKA:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
					case CLASE_ANUTROF:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
					case CLASE_ZURCARAK:
						if (val < 50)
							return 1;
						if (val < 100)
							return 2;
						if (val < 150)
							return 3;
						if (val < 200)
							return 4;
						return 5;
					case CLASE_OCRA:
						if (val < 50)
							return 1;
						if (val < 100)
							return 2;
						if (val < 150)
							return 3;
						if (val < 200)
							return 4;
						return 5;
					case CLASE_OSAMODAS:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
				}
				break;
			case 15:// Inteligencia
				switch (claseID) {
					case CLASE_XELOR:
						if (val < 100)
							return 1;
						if (val < 200)
							return 2;
						if (val < 300)
							return 3;
						if (val < 400)
							return 4;
						return 5;
					case CLASE_FECA:
						if (val < 100)
							return 1;
						if (val < 200)
							return 2;
						if (val < 300)
							return 3;
						if (val < 400)
							return 4;
						return 5;
					case CLASE_SACROGITO:
						return 3;
					case CLASE_SRAM:
						if (val < 50)
							return 2;
						if (val < 150)
							return 3;
						if (val < 250)
							return 4;
						return 5;
					case CLASE_SADIDA:
						if (val < 100)
							return 1;
						if (val < 200)
							return 2;
						if (val < 300)
							return 3;
						if (val < 400)
							return 4;
						return 5;
					case CLASE_ANUTROF:
						if (val < 20)
							return 1;
						if (val < 60)
							return 2;
						if (val < 100)
							return 3;
						if (val < 140)
							return 4;
						return 5;
					case CLASE_PANDAWA:
						if (val < 50)
							return 1;
						if (val < 200)
							return 2;
						return 3;
					case CLASE_YOPUKA:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
					case CLASE_ANIRIPSA:
						if (val < 100)
							return 1;
						if (val < 200)
							return 2;
						if (val < 300)
							return 3;
						if (val < 400)
							return 4;
						return 5;
					case CLASE_OCRA:
						if (val < 50)
							return 1;
						if (val < 150)
							return 2;
						if (val < 250)
							return 3;
						if (val < 350)
							return 4;
						return 5;
					case CLASE_OSAMODAS:
						if (val < 100)
							return 1;
						if (val < 200)
							return 2;
						if (val < 300)
							return 3;
						if (val < 400)
							return 4;
						return 5;
					case CLASE_ZURCARAK:
						if (val < 20)
							return 1;
						if (val < 40)
							return 2;
						if (val < 60)
							return 3;
						if (val < 80)
							return 4;
						return 5;
				}
				break;
		}
		return 5;
	}
	
	public static int agresionPorNivel(int nivel) {
		int distancia = 0;
		distancia = (int) (nivel / 50);
		if (nivel > 500)
			distancia = 10;
		return distancia;
	}
	
	public static boolean esUbicacionValidaObjeto(ObjetoModelo objetoMod, int pos) {
		switch (objetoMod.getTipo()) {
			case ITEM_TIPO_AMULETO:
				if (pos == ITEM_POS_AMULETO)
					return true;
				break;
			case ITEM_TIPO_ARCO:
			case ITEM_TIPO_VARITA:
			case ITEM_TIPO_BASTON:
			case ITEM_TIPO_DAGAS:
			case ITEM_TIPO_ESPADA:
			case ITEM_TIPO_MARTILLO:
			case ITEM_TIPO_PALA:
			case ITEM_TIPO_HACHA:
			case ITEM_TIPO_HERRAMIENTA:
			case ITEM_TIPO_PICO:
			case ITEM_TIPO_GUADAÑA:
			case ITEM_TIPO_PIEDRA_DEL_ALMA:
			case ITEM_TIPO_ARMA_MAGICA:
				if (pos == ITEM_POS_ARMA)
					return true;
				break;
			case ITEM_TIPO_ANILLO:
				if (pos == ITEM_POS_ANILLO1 || pos == ITEM_POS_ANILLO2)
					return true;
				break;
			case ITEM_TIPO_CINTURON:
				if (pos == ITEM_POS_CINTURON)
					return true;
				break;
			case ITEM_TIPO_BOTAS:
				if (pos == ITEM_POS_BOTAS)
					return true;
				break;
			case ITEM_TIPO_SOMBRERO:
				if (pos == ITEM_POS_SOMBRERO)
					return true;
				break;
			case ITEM_TIPO_CAPA:
				if (pos == ITEM_POS_CAPA)
					return true;
				break;
			case ITEM_TIPO_MOCHILA:
				if (pos == ITEM_POS_CAPA)
					return true;
				break;
			case ITEM_TIPO_MASCOTA:
				if (pos == ITEM_POS_MASCOTA)
					return true;
				break;
			case ITEM_TIPO_DOFUS:
				if (pos == ITEM_POS_DOFUS1 || pos == ITEM_POS_DOFUS2 || pos == ITEM_POS_DOFUS3 || pos == ITEM_POS_DOFUS4
						|| pos == ITEM_POS_DOFUS5 || pos == ITEM_POS_DOFUS6)
					return true;
				break;
			case ITEM_TIPO_ESCUDO:
				if (pos == ITEM_POS_ESCUDO)
					return true;
				break;
			case ITEM_TIPO_POCION:
			case ITEM_TIPO_PERGAMINO_EXP:
			case ITEM_TIPO_ALIMENTO_BOOST:
			case ITEM_TIPO_PAN:
			case ITEM_TIPO_CERVEZA:
			case ITEM_TIPO_PEZ:
			case ITEM_TIPO_GOLOSINA:
			case ITEM_TIPO_PESCADO_COMESTIBLE:
			case ITEM_TIPO_CARNE:
			case ITEM_TIPO_CARNE_CONSERVADA:
			case ITEM_TIPO_CARNE_COMESTIBLE:
			case ITEM_TIPO_TINTE:
			case ITEM_TIPO_DOMINIO:
			case ITEM_TIPO_BEBIDA:
			case ITEM_TIPO_PIEDRA_DE_ALMA_LLENA:
			case ITEM_TIPO_PERGAMINO_BUSQUEDA:
			case ITEM_TIPO_REGALOS:
			case ITEM_TIPO_OBJETO_CRIA:
			case ITEM_TIPO_OBJETO_UTILIZABLE:
			case ITEM_TIPO_PRISMA:
			case ITEM_TIPO_HADA_ARTIFICIAL:
			case ITEM_TIPO_DONES:
			case ITEM_TIPO_ALIMENTO_MASCOTA:
			case ITEM_TIPO_PERGAMINO_HECHIZO:
				if (pos >= 35 && pos <= 48)
					return true;
				break;
		}
		return false;
	}
	
	public static int getClasePorObjMod(int objModelo) {
		switch (objModelo) {
			case 9544:
				return ATORMENTADOR_TINIEBLAS;
			case 9545:
				return ATORMENTADOR_LLAMAS;
			case 9546:
				return ATORMENTADOR_HOJA;
			case 9547:
				return ATORMENTADOR_GOTA;
			case 9548:
				return ATORMENTADOR_NUBE;
			case 10125:
				return BANDIDO_ARQUERO;
			case 10126:
				return BANDIDO_ESPADACHIN;
			case 10127:
				return BANDIDO_PENDENCIERO;
			case 10133:
				return BANDIDO_HECHIZERO;
		}
		return -1;
	}
	
	public static void subirNivelAprenderHechizos(Personaje perso, int nivel) {
		switch (perso.getClase(true)) {
			case CLASE_FECA:
				if (nivel == 3)
					perso.aprenderHechizo(4, 1, false, false);// Renvoie de sort
				if (nivel == 6)
					perso.aprenderHechizo(2, 1, false, false);// Aveuglement
				if (nivel == 9)
					perso.aprenderHechizo(1, 1, false, false);// Armadura Incandescente
				if (nivel == 13)
					perso.aprenderHechizo(9, 1, false, false);// Attaque nuageuse
				if (nivel == 17)
					perso.aprenderHechizo(18, 1, false, false);// Armadura Aqueuse
				if (nivel == 21)
					perso.aprenderHechizo(20, 1, false, false);// Immunité
				if (nivel == 26)
					perso.aprenderHechizo(14, 1, false, false);// Armadura Venteuse
				if (nivel == 31)
					perso.aprenderHechizo(19, 1, false, false);// Bulle
				if (nivel == 36)
					perso.aprenderHechizo(5, 1, false, false);// Trêve
				if (nivel == 42)
					perso.aprenderHechizo(16, 1, false, false);// Science du bâton
				if (nivel == 48)
					perso.aprenderHechizo(8, 1, false, false);// Retour du bâton
				if (nivel == 54)
					perso.aprenderHechizo(12, 1, false, false);// glyphe d'Aveuglement
				if (nivel == 60)
					perso.aprenderHechizo(11, 1, false, false);// Téléportation
				if (nivel == 70)
					perso.aprenderHechizo(10, 1, false, false);// Glyphe Enflammé
				if (nivel == 80)
					perso.aprenderHechizo(7, 1, false, false);// Bouclier Féca
				if (nivel == 90)
					perso.aprenderHechizo(15, 1, false, false);// Glyphe d'Immobilisation
				if (nivel == 100)
					perso.aprenderHechizo(13, 1, false, false);// Glyphe de Silence
				if (nivel == 200)
					perso.aprenderHechizo(1901, 1, false, false);// Invocation de Dopeul Féca
				break;
			case CLASE_OSAMODAS:
				if (nivel == 3)
					perso.aprenderHechizo(26, 1, false, false);// Bénédiction Animale
				if (nivel == 6)
					perso.aprenderHechizo(22, 1, false, false);// Déplacement Félin
				if (nivel == 9)
					perso.aprenderHechizo(35, 1, false, false);// Invocation de Bouftou
				if (nivel == 13)
					perso.aprenderHechizo(28, 1, false, false);// Crapaud
				if (nivel == 17)
					perso.aprenderHechizo(37, 1, false, false);// Invocation de Prespic
				if (nivel == 21)
					perso.aprenderHechizo(30, 1, false, false);// Fouet
				if (nivel == 26)
					perso.aprenderHechizo(27, 1, false, false);// Piqûre Motivante
				if (nivel == 31)
					perso.aprenderHechizo(24, 1, false, false);// Corbeau
				if (nivel == 36)
					perso.aprenderHechizo(33, 1, false, false);// Griffe Cinglante
				if (nivel == 42)
					perso.aprenderHechizo(25, 1, false, false);// Soin Animal
				if (nivel == 48)
					perso.aprenderHechizo(38, 1, false, false);// Invocation de Sanglier
				if (nivel == 54)
					perso.aprenderHechizo(36, 1, false, false);// Frappe du Craqueleur
				if (nivel == 60)
					perso.aprenderHechizo(32, 1, false, false);// Résistance Naturelle
				if (nivel == 70)
					perso.aprenderHechizo(29, 1, false, false);// Crocs du Mulou
				if (nivel == 80)
					perso.aprenderHechizo(39, 1, false, false);// Invocation de Bwork Mage
				if (nivel == 90)
					perso.aprenderHechizo(40, 1, false, false);// Invocation de Craqueleur
				if (nivel == 100)
					perso.aprenderHechizo(31, 1, false, false);// Invocation de Dragonnet Rouge
				if (nivel == 200)
					perso.aprenderHechizo(1902, 1, false, false);// Invocation de Dopeul Osamodas
				break;
			case CLASE_ANUTROF:
				if (nivel == 3)
					perso.aprenderHechizo(49, 1, false, false);// Pelle Fantomatique
				if (nivel == 6)
					perso.aprenderHechizo(42, 1, false, false);// Chance
				if (nivel == 9)
					perso.aprenderHechizo(47, 1, false, false);// Boîte de Pandore
				if (nivel == 13)
					perso.aprenderHechizo(48, 1, false, false);// Remblai
				if (nivel == 17)
					perso.aprenderHechizo(45, 1, false, false);// Clé Réductrice
				if (nivel == 21)
					perso.aprenderHechizo(53, 1, false, false);// Force de l'Age
				if (nivel == 26)
					perso.aprenderHechizo(46, 1, false, false);// Désinvocation
				if (nivel == 31)
					perso.aprenderHechizo(52, 1, false, false);// Cupidité
				if (nivel == 36)
					perso.aprenderHechizo(44, 1, false, false);// Roulage de Pelle
				if (nivel == 42)
					perso.aprenderHechizo(50, 1, false, false);// Maladresse
				if (nivel == 48)
					perso.aprenderHechizo(54, 1, false, false);// Maladresse de Masse
				if (nivel == 54)
					perso.aprenderHechizo(55, 1, false, false);// Accélération
				if (nivel == 60)
					perso.aprenderHechizo(56, 1, false, false);// Pelle du Jugement
				if (nivel == 70)
					perso.aprenderHechizo(58, 1, false, false);// Pelle Massacrante
				if (nivel == 80)
					perso.aprenderHechizo(59, 1, false, false);// Corruption
				if (nivel == 90)
					perso.aprenderHechizo(57, 1, false, false);// Pelle Animée
				if (nivel == 100)
					perso.aprenderHechizo(60, 1, false, false);// Coffre Animé
				if (nivel == 200)
					perso.aprenderHechizo(1903, 1, false, false);// Invocation de Dopeul Enutrof
				break;
			case CLASE_SRAM:
				if (nivel == 3)
					perso.aprenderHechizo(66, 1, false, false);// Poison insidieux
				if (nivel == 6)
					perso.aprenderHechizo(68, 1, false, false);// Fourvoiement
				if (nivel == 9)
					perso.aprenderHechizo(63, 1, false, false);// Coup Sournois
				if (nivel == 13)
					perso.aprenderHechizo(74, 1, false, false);// Double
				if (nivel == 17)
					perso.aprenderHechizo(64, 1, false, false);// Repérage
				if (nivel == 21)
					perso.aprenderHechizo(79, 1, false, false);// Piège de Masse
				if (nivel == 26)
					perso.aprenderHechizo(78, 1, false, false);// Invisibilité d'Autrui
				if (nivel == 31)
					perso.aprenderHechizo(71, 1, false, false);// Piège Empoisonné
				if (nivel == 36)
					perso.aprenderHechizo(62, 1, false, false);// Concentration de Chakra
				if (nivel == 42)
					perso.aprenderHechizo(69, 1, false, false);// Piège d'Immobilisation
				if (nivel == 48)
					perso.aprenderHechizo(77, 1, false, false);// Piège de Silence
				if (nivel == 54)
					perso.aprenderHechizo(73, 1, false, false);// Piège répulsif
				if (nivel == 60)
					perso.aprenderHechizo(67, 1, false, false);// Peur
				if (nivel == 70)
					perso.aprenderHechizo(70, 1, false, false);// Arnaque
				if (nivel == 80)
					perso.aprenderHechizo(75, 1, false, false);// Pulsion de Chakra
				if (nivel == 90)
					perso.aprenderHechizo(76, 1, false, false);// Attaque Mortelle
				if (nivel == 100)
					perso.aprenderHechizo(80, 1, false, false);// Piège Mortel
				if (nivel == 200)
					perso.aprenderHechizo(1904, 1, false, false);// Invocation de Dopeul Sram
				break;
			case CLASE_XELOR:
				if (nivel == 3)
					perso.aprenderHechizo(84, 1, false, false);// Gelure
				if (nivel == 6)
					perso.aprenderHechizo(100, 1, false, false);// Sablier de Xélor
				if (nivel == 9)
					perso.aprenderHechizo(92, 1, false, false);// Rayon Obscur
				if (nivel == 13)
					perso.aprenderHechizo(88, 1, false, false);// Téléportation
				if (nivel == 17)
					perso.aprenderHechizo(93, 1, false, false);// Flétrissement
				if (nivel == 21)
					perso.aprenderHechizo(85, 1, false, false);// Flou
				if (nivel == 26)
					perso.aprenderHechizo(96, 1, false, false);// Poussière Temporelle
				if (nivel == 31)
					perso.aprenderHechizo(98, 1, false, false);// Vol du Temps
				if (nivel == 36)
					perso.aprenderHechizo(86, 1, false, false);// Aiguille Chercheuse
				if (nivel == 42)
					perso.aprenderHechizo(89, 1, false, false);// Dévouement
				if (nivel == 48)
					perso.aprenderHechizo(90, 1, false, false);// Fuite
				if (nivel == 54)
					perso.aprenderHechizo(87, 1, false, false);// Démotivation
				if (nivel == 60)
					perso.aprenderHechizo(94, 1, false, false);// Protection Aveuglante
				if (nivel == 70)
					perso.aprenderHechizo(99, 1, false, false);// Momification
				if (nivel == 80)
					perso.aprenderHechizo(95, 1, false, false);// Horloge
				if (nivel == 90)
					perso.aprenderHechizo(91, 1, false, false);// Frappe de Xélor
				if (nivel == 100)
					perso.aprenderHechizo(97, 1, false, false);// Cadran de Xélor
				if (nivel == 200)
					perso.aprenderHechizo(1905, 1, false, false);// Invocation de Dopeul Xélor
				break;
			case CLASE_ZURCARAK:
				if (nivel == 3)
					perso.aprenderHechizo(109, 1, false, false);// Bluff
				if (nivel == 6)
					perso.aprenderHechizo(113, 1, false, false);// Percepcion
				if (nivel == 9)
					perso.aprenderHechizo(111, 1, false, false);// Contrecoup
				if (nivel == 13)
					perso.aprenderHechizo(104, 1, false, false);// Trebol
				if (nivel == 17)
					perso.aprenderHechizo(119, 1, false, false);// Todo o nada
				if (nivel == 21)
					perso.aprenderHechizo(101, 1, false, false);// Ruleta
				if (nivel == 26)
					perso.aprenderHechizo(107, 1, false, false);// Topkaj
				if (nivel == 31)
					perso.aprenderHechizo(116, 1, false, false);// Lengua Raspadora
				if (nivel == 36)
					perso.aprenderHechizo(106, 1, false, false);// Rueda de la fortuna
				if (nivel == 42)
					perso.aprenderHechizo(117, 1, false, false);// Garra invocadora
				if (nivel == 48)
					perso.aprenderHechizo(108, 1, false, false);// Espritu felino
				if (nivel == 54)
					perso.aprenderHechizo(115, 1, false, false);// Olfato
				if (nivel == 60)
					perso.aprenderHechizo(118, 1, false, false);// Réflexes
				if (nivel == 70)
					perso.aprenderHechizo(110, 1, false, false);// Griffe Joueuse
				if (nivel == 80)
					perso.aprenderHechizo(112, 1, false, false);// Garra de ceangal
				if (nivel == 90)
					perso.aprenderHechizo(114, 1, false, false);// Rekop
				if (nivel == 100)
					perso.aprenderHechizo(120, 1, false, false);// Destino de zurcarak
				if (nivel == 200)
					perso.aprenderHechizo(1906, 1, false, false);// Invocation de Dopeul Ecaflip
				break;
			case CLASE_ANIRIPSA:
				if (nivel == 3)
					perso.aprenderHechizo(124, 1, false, false);// Mot Soignant
				if (nivel == 6)
					perso.aprenderHechizo(122, 1, false, false);// Mot Blessant
				if (nivel == 9)
					perso.aprenderHechizo(126, 1, false, false);// Mot Stimulant
				if (nivel == 13)
					perso.aprenderHechizo(127, 1, false, false);// Mot de Prévention
				if (nivel == 17)
					perso.aprenderHechizo(123, 1, false, false);// Mot Drainant
				if (nivel == 21)
					perso.aprenderHechizo(130, 1, false, false);// Mot Revitalisant
				if (nivel == 26)
					perso.aprenderHechizo(131, 1, false, false);// Mot de Régénération
				if (nivel == 31)
					perso.aprenderHechizo(132, 1, false, false);// Mot d'Epine
				if (nivel == 36)
					perso.aprenderHechizo(133, 1, false, false);// Mot de Jouvence
				if (nivel == 42)
					perso.aprenderHechizo(134, 1, false, false);// Mot Vampirique
				if (nivel == 48)
					perso.aprenderHechizo(135, 1, false, false);// Mot de Sacrifice
				if (nivel == 54)
					perso.aprenderHechizo(129, 1, false, false);// Mot d'Amitié
				if (nivel == 60)
					perso.aprenderHechizo(136, 1, false, false);// Mot d'Immobilisation
				if (nivel == 70)
					perso.aprenderHechizo(137, 1, false, false);// Mot d'Envol
				if (nivel == 80)
					perso.aprenderHechizo(138, 1, false, false);// Mot de Silence
				if (nivel == 90)
					perso.aprenderHechizo(139, 1, false, false);// Mot d'Altruisme
				if (nivel == 100)
					perso.aprenderHechizo(140, 1, false, false);// Mot de Reconstitution
				if (nivel == 200)
					perso.aprenderHechizo(1907, 1, false, false);// Invocation de Dopeul Eniripsa
				break;
			case CLASE_YOPUKA:
				if (nivel == 3)
					perso.aprenderHechizo(144, 1, false, false);// Compulsion
				if (nivel == 6)
					perso.aprenderHechizo(145, 1, false, false);// Epée Divine
				if (nivel == 9)
					perso.aprenderHechizo(146, 1, false, false);// Epée du Destin
				if (nivel == 13)
					perso.aprenderHechizo(147, 1, false, false);// Guide de Bravoure
				if (nivel == 17)
					perso.aprenderHechizo(148, 1, false, false);// Amplification
				if (nivel == 21)
					perso.aprenderHechizo(154, 1, false, false);// Epée Destructrice
				if (nivel == 26)
					perso.aprenderHechizo(150, 1, false, false);// Couper
				if (nivel == 31)
					perso.aprenderHechizo(151, 1, false, false);// Souffle
				if (nivel == 36)
					perso.aprenderHechizo(155, 1, false, false);// Vitalité
				if (nivel == 42)
					perso.aprenderHechizo(152, 1, false, false);// Epée du Jugement
				if (nivel == 48)
					perso.aprenderHechizo(153, 1, false, false);// Puissance
				if (nivel == 54)
					perso.aprenderHechizo(149, 1, false, false);// Mutilation
				if (nivel == 60)
					perso.aprenderHechizo(156, 1, false, false);// Tempête de Puissance
				if (nivel == 70)
					perso.aprenderHechizo(157, 1, false, false);// Epée Céleste
				if (nivel == 80)
					perso.aprenderHechizo(158, 1, false, false);// Concentration
				if (nivel == 90)
					perso.aprenderHechizo(160, 1, false, false);// Epée de Iop
				if (nivel == 100)
					perso.aprenderHechizo(159, 1, false, false);// Colère de Iop
				if (nivel == 200)
					perso.aprenderHechizo(1908, 1, false, false);// Invocation de Dopeul Iop
				break;
			case CLASE_OCRA:
				if (nivel == 3)
					perso.aprenderHechizo(163, 1, false, false);// Flèche Glacée
				if (nivel == 6)
					perso.aprenderHechizo(165, 1, false, false);// Flèche enflammée
				if (nivel == 9)
					perso.aprenderHechizo(172, 1, false, false);// Tir Eloigné
				if (nivel == 13)
					perso.aprenderHechizo(167, 1, false, false);// Flèche d'Expiation
				if (nivel == 17)
					perso.aprenderHechizo(168, 1, false, false);// Oeil de Taupe
				if (nivel == 21)
					perso.aprenderHechizo(162, 1, false, false);// Tir Critique
				if (nivel == 26)
					perso.aprenderHechizo(170, 1, false, false);// Flèche d'Immobilisation
				if (nivel == 31)
					perso.aprenderHechizo(171, 1, false, false);// Flèche Punitive
				if (nivel == 36)
					perso.aprenderHechizo(166, 1, false, false);// Tir Puissant
				if (nivel == 42)
					perso.aprenderHechizo(173, 1, false, false);// Flèche Harcelante
				if (nivel == 48)
					perso.aprenderHechizo(174, 1, false, false);// Flèche Cinglante
				if (nivel == 54)
					perso.aprenderHechizo(176, 1, false, false);// Flèche Persécutrice
				if (nivel == 60)
					perso.aprenderHechizo(175, 1, false, false);// Flèche Destructrice
				if (nivel == 70)
					perso.aprenderHechizo(178, 1, false, false);// Flèche Absorbante
				if (nivel == 80)
					perso.aprenderHechizo(177, 1, false, false);// Flèche Ralentissante
				if (nivel == 90)
					perso.aprenderHechizo(179, 1, false, false);// Flèche Explosive
				if (nivel == 100)
					perso.aprenderHechizo(180, 1, false, false);// Maîtrise de l'Arc
				if (nivel == 200)
					perso.aprenderHechizo(1909, 1, false, false);// Invocation de Dopeul Cra
				break;
			case CLASE_SADIDA:
				if (nivel == 3)
					perso.aprenderHechizo(198, 1, false, false);// Sacrifice Poupesque
				if (nivel == 6)
					perso.aprenderHechizo(195, 1, false, false);// Larme
				if (nivel == 9)
					perso.aprenderHechizo(182, 1, false, false);// Invocation de la
																// Folle
				if (nivel == 13)
					perso.aprenderHechizo(192, 1, false, false);// Ronce Apaisante
				if (nivel == 17)
					perso.aprenderHechizo(197, 1, false, false);// Puissance Sylvestre
				if (nivel == 21)
					perso.aprenderHechizo(189, 1, false, false);// Invocation de la Sacrifiée
				if (nivel == 26)
					perso.aprenderHechizo(181, 1, false, false);// Tremblement
				if (nivel == 31)
					perso.aprenderHechizo(199, 1, false, false);// Connaissance des Poupées
				if (nivel == 36)
					perso.aprenderHechizo(191, 1, false, false);// Ronce Multiples
				if (nivel == 42)
					perso.aprenderHechizo(186, 1, false, false);// Arbre
				if (nivel == 48)
					perso.aprenderHechizo(196, 1, false, false);// Vent Empoisonné
				if (nivel == 54)
					perso.aprenderHechizo(190, 1, false, false);// Invocation de la Gonflable
				if (nivel == 60)
					perso.aprenderHechizo(194, 1, false, false);// Ronces Agressives
				if (nivel == 70)
					perso.aprenderHechizo(185, 1, false, false);// Herbe Folle
				if (nivel == 80)
					perso.aprenderHechizo(184, 1, false, false);// Feu de BPelirrojo
				if (nivel == 90)
					perso.aprenderHechizo(188, 1, false, false);// Ronce Insolente
				if (nivel == 100)
					perso.aprenderHechizo(187, 1, false, false);// Invocation de la Surpuissante
				if (nivel == 200)
					perso.aprenderHechizo(1910, 1, false, false);// Invocation de Dopeul Sadida
				break;
			case CLASE_SACROGITO:
				if (nivel == 3)
					perso.aprenderHechizo(444, 1, false, false);// Dérobade
				if (nivel == 6)
					perso.aprenderHechizo(449, 1, false, false);// Détour
				if (nivel == 9)
					perso.aprenderHechizo(436, 1, false, false);// Assaut
				if (nivel == 13)
					perso.aprenderHechizo(437, 1, false, false);// Châtiment Agile
				if (nivel == 17)
					perso.aprenderHechizo(439, 1, false, false);// Dissolution
				if (nivel == 21)
					perso.aprenderHechizo(433, 1, false, false);// Châtiment Osé
				if (nivel == 26)
					perso.aprenderHechizo(443, 1, false, false);// Châtiment Spirituel
				if (nivel == 31)
					perso.aprenderHechizo(440, 1, false, false);// Sacrifice
				if (nivel == 36)
					perso.aprenderHechizo(442, 1, false, false);// Absorption
				if (nivel == 42)
					perso.aprenderHechizo(441, 1, false, false);// Châtiment Vilatesque
				if (nivel == 48)
					perso.aprenderHechizo(445, 1, false, false);// Coopération
				if (nivel == 54)
					perso.aprenderHechizo(438, 1, false, false);// Transposition
				if (nivel == 60)
					perso.aprenderHechizo(446, 1, false, false);// Punition
				if (nivel == 70)
					perso.aprenderHechizo(447, 1, false, false);// Furie
				if (nivel == 80)
					perso.aprenderHechizo(448, 1, false, false);// Epée Volante
				if (nivel == 90)
					perso.aprenderHechizo(435, 1, false, false);// Tansfert de Vie
				if (nivel == 100)
					perso.aprenderHechizo(450, 1, false, false);// Folie Sanguinaire
				if (nivel == 200)
					perso.aprenderHechizo(1911, 1, false, false);// Invocation de Dopeul Sacrieur
				break;
			case CLASE_PANDAWA:
				if (nivel == 3)
					perso.aprenderHechizo(689, 1, false, false);// Epouvante
				if (nivel == 6)
					perso.aprenderHechizo(690, 1, false, false);// Souffle Alcoolisé
				if (nivel == 9)
					perso.aprenderHechizo(691, 1, false, false);// Vulnérabilité Aqueuse
				if (nivel == 13)
					perso.aprenderHechizo(688, 1, false, false);// Vulnérabilité Incandescente
				if (nivel == 17)
					perso.aprenderHechizo(693, 1, false, false);// Karcham
				if (nivel == 21)
					perso.aprenderHechizo(694, 1, false, false);// Vulnérabilité Venteuse
				if (nivel == 26)
					perso.aprenderHechizo(695, 1, false, false);// Stabilisation
				if (nivel == 31)
					perso.aprenderHechizo(696, 1, false, false);// Chamrak
				if (nivel == 36)
					perso.aprenderHechizo(697, 1, false, false);// Vulnérabilité Terrestre
				if (nivel == 42)
					perso.aprenderHechizo(698, 1, false, false);// Souillure
				if (nivel == 48)
					perso.aprenderHechizo(699, 1, false, false);// Lait de Bambou
				if (nivel == 54)
					perso.aprenderHechizo(700, 1, false, false);// Vague à Lame
				if (nivel == 60)
					perso.aprenderHechizo(701, 1, false, false);// Colère de Zatoïshwan
				if (nivel == 70)
					perso.aprenderHechizo(702, 1, false, false);// Flasque Explosive
				if (nivel == 80)
					perso.aprenderHechizo(703, 1, false, false);// Pandatak
				if (nivel == 90)
					perso.aprenderHechizo(704, 1, false, false);// Pandanlku
				if (nivel == 100)
					perso.aprenderHechizo(705, 1, false, false);// Lien Spiritueux
				if (nivel == 200)
					perso.aprenderHechizo(1912, 1, false, false);// Invocation de Dopeul Pandawa
				break;
		}
	}
	
	public static int getColorGlifo(int hechizo) {
		switch (hechizo) {
			case 10:// Enflammé
			case 2033:// Dopeul
				return 4;// Rouge
			case 12:// Aveuglement
			case 2034:// Dopeul
				return 3;
			case 13:// Silence
			case 2035:// Dopeul
				return 6;// Bleu
			case 15:// Immobilisation
			case 2036:// Dopeul
				return 5;// Vert
			case 17:// Aggressif
			case 2037:// Dopeul
				return 2;
			default:
				return 4;
		}
	}
	
	public static int getColorTrampa(int hechizo) {
		switch (hechizo) {
			case 65:// Sournois
				return 7;
			case 69:// Immobilisation
				return 10;
			case 71:// Empoisonnée
			case 2068:// Dopeul
				return 9;
			case 73:// Repulsif
				return 12;
			case 77:// Silence
			case 2071:// Dopeul
				return 11;
			case 79:// Masse
			case 2072:// Dopeul
				return 8;
			case 80:// Mortel
				return 13;
			default:
				return 7;
		}
	}
	
	public static ArrayList<AccionTrabajo> getTrabajosPorOficios(int idOficio, int nivel) {
		ArrayList<AccionTrabajo> listaTrabajos = new ArrayList<AccionTrabajo>();
		int tiempoGanado = nivel * 100;
		int dropGanado = nivel / 5;
		switch (idOficio) {
			case OFICIO_JOYERO:
				listaTrabajos.add(new AccionTrabajo(11, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(12, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				break;
			case OFICIO_SASTRE:
				listaTrabajos.add(new AccionTrabajo(64, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(123, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(63, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				break;
			case OFICIO_ZAPATERO:
				listaTrabajos.add(new AccionTrabajo(13, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(14, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				break;
			case OFICIO_MANITAS:
				listaTrabajos.add(new AccionTrabajo(171, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(182, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				break;
			case OFICIO_ESCULTOR_ARCOS:
				listaTrabajos.add(new AccionTrabajo(15, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(149, 3, 3, true, nivel, 0));
				break;
			case OFICIO_ESCULTOR_VARITAS:
				listaTrabajos.add(new AccionTrabajo(16, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(148, 3, 3, true, nivel, 0));
				break;
			case OFICIO_ESCULTOR_BASTONES:
				listaTrabajos.add(new AccionTrabajo(17, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(147, 3, 3, true, nivel, 0));
				break;
			case OFICIO_FORJADOR_DAGAS:
				listaTrabajos.add(new AccionTrabajo(18, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(142, 3, 3, true, nivel, 0));
				break;
			case OFICIO_FORJADOR_MARTILLOS:
				listaTrabajos.add(new AccionTrabajo(19, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(144, 3, 3, true, nivel, 0));
				break;
			case OFICIO_FORJADOR_ESPADAS:
				listaTrabajos.add(new AccionTrabajo(20, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(145, 3, 3, true, nivel, 0));
				break;
			case OFICIO_FORJADOR_PALAS:
				listaTrabajos.add(new AccionTrabajo(21, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(146, 3, 3, true, nivel, 0));
				break;
			case OFICIO_FORJADOR_HACHAS:
				listaTrabajos.add(new AccionTrabajo(65, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(143, 3, 3, true, nivel, 0));
				break;
			case OFICIO_FORJADOR_ESCUDOS:
				listaTrabajos.add(new AccionTrabajo(156, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				break;
			case OFICIO_ZAPATEROMAGO:
				listaTrabajos.add(new AccionTrabajo(163, 3, 3, true, nivel, 0));
				listaTrabajos.add(new AccionTrabajo(164, 3, 3, true, nivel, 0));
				break;
			case OFICIO_JOYEROMAGO:
				listaTrabajos.add(new AccionTrabajo(169, 3, 3, true, nivel, 0));
				listaTrabajos.add(new AccionTrabajo(168, 3, 3, true, nivel, 0));
				break;
			case OFICIO_SASTREMAGO:
				listaTrabajos.add(new AccionTrabajo(165, 3, 3, true, nivel, 0));
				listaTrabajos.add(new AccionTrabajo(167, 3, 3, true, nivel, 0));
				listaTrabajos.add(new AccionTrabajo(166, 3, 3, true, nivel, 0));
				break;
			case OFICIO_ESCULTORMAGO_BASTONES:
				listaTrabajos.add(new AccionTrabajo(120, 3, 3, true, nivel, 0));
				break;
			case OFICIO_ESCULTORMAGO_VARITAS:
				listaTrabajos.add(new AccionTrabajo(119, 3, 3, true, nivel, 0));
				break;
			case OFICIO_ESCULTORMAGO_ARCOS:
				listaTrabajos.add(new AccionTrabajo(118, 3, 3, true, nivel, 0));
				break;
			case OFICIO_FORJAMAGO_HACHAS:
				listaTrabajos.add(new AccionTrabajo(115, 3, 3, true, nivel, 0));
				break;
			case OFICIO_FORJAMAGO_DAGAS:
				listaTrabajos.add(new AccionTrabajo(1, 3, 3, true, nivel, 0));
				break;
			case OFICIO_FORJAMAGO_ESPADAS:
				listaTrabajos.add(new AccionTrabajo(113, 3, 3, true, nivel, 0));
				break;
			case OFICIO_FORJAMAGO_MARTILLOS:
				listaTrabajos.add(new AccionTrabajo(116, 3, 3, true, nivel, 0));
				break;
			case OFICIO_FORJAMAGO_PALAS:
				listaTrabajos.add(new AccionTrabajo(117, 3, 3, true, nivel, 0));
				break;
			case OFICIO_CAZADOR:
				listaTrabajos.add(new AccionTrabajo(134, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				break;
			case OFICIO_CARNICERO:
				listaTrabajos.add(new AccionTrabajo(132, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				break;
			case OFICIO_PESCADERO:
				listaTrabajos.add(new AccionTrabajo(135, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				break;
			case OFICIO_PANADERO:
				listaTrabajos.add(new AccionTrabajo(27, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(109, 3, 3, true, 100, -1));
				break;
			case OFICIO_MINERO:
				if (nivel > 99) {
					listaTrabajos
							.add(new AccionTrabajo(161, -19 + dropGanado, -18 + dropGanado, false, 12000 - tiempoGanado, 60));
				}
				if (nivel > 79) {
					listaTrabajos.add(new AccionTrabajo(30, -15 + dropGanado, -14 + dropGanado, false, 12000 - tiempoGanado, 55));
				}
				if (nivel > 69) {
					listaTrabajos.add(new AccionTrabajo(31, -13 + dropGanado, -12 + dropGanado, false, 12000 - tiempoGanado, 50));
				}
				if (nivel > 59) {
					listaTrabajos.add(new AccionTrabajo(29, -11 + dropGanado, -10 + dropGanado, false, 12000 - tiempoGanado, 40));
				}
				if (nivel > 49) {
					listaTrabajos.add(new AccionTrabajo(55, -9 + dropGanado, -8 + dropGanado, false, 12000 - tiempoGanado, 35));
					listaTrabajos.add(new AccionTrabajo(162, -9 + dropGanado, -8 + dropGanado, false, 12000 - tiempoGanado, 35));
				}
				if (nivel > 39) {
					listaTrabajos.add(new AccionTrabajo(56, -7 + dropGanado, -6 + dropGanado, false, 12000 - tiempoGanado, 30));
				}
				if (nivel > 29) {
					listaTrabajos.add(new AccionTrabajo(28, -5 + dropGanado, -4 + dropGanado, false, 12000 - tiempoGanado, 25));
				}
				if (nivel > 19) {
					listaTrabajos.add(new AccionTrabajo(26, -3 + dropGanado, -2 + dropGanado, false, 12000 - tiempoGanado, 20));
				}
				if (nivel > 9) {
					listaTrabajos.add(new AccionTrabajo(25, -1 + dropGanado, 0 + dropGanado, false, 12000 - tiempoGanado, 15));
				}
				listaTrabajos.add(new AccionTrabajo(24, 1 + dropGanado, 2 + dropGanado, false, 12000 - tiempoGanado, 10));
				listaTrabajos.add(new AccionTrabajo(32, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(48, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				break;
			case OFICIO_PESCADOR:
				if (nivel > 74) {
					listaTrabajos.add(new AccionTrabajo(131, 0, 1, false, 12000 - tiempoGanado, 35));
				}
				if (nivel > 69) {
					listaTrabajos.add(new AccionTrabajo(127, 0, 1, false, 12000 - tiempoGanado, 35));
				}
				if (nivel > 49) {
					listaTrabajos.add(new AccionTrabajo(130, 0, 1, false, 12000 - tiempoGanado, 30));
				}
				if (nivel > 39) {
					listaTrabajos.add(new AccionTrabajo(126, 0, 1, false, 12000 - tiempoGanado, 25));
				}
				if (nivel > 19) {
					listaTrabajos.add(new AccionTrabajo(129, 0, 1, false, 12000 - tiempoGanado, 20));
				}
				if (nivel > 9) {
					listaTrabajos.add(new AccionTrabajo(125, 0, 1, false, 12000 - tiempoGanado, 15));
				}
				listaTrabajos.add(new AccionTrabajo(140, 0, 1, false, 12000 - tiempoGanado, 50));
				listaTrabajos.add(new AccionTrabajo(136, 1, 1, false, 12000 - tiempoGanado, 5));
				listaTrabajos.add(new AccionTrabajo(124, 0, 1, false, 12000 - tiempoGanado, 10));
				listaTrabajos.add(new AccionTrabajo(128, 0, 1, false, 12000 - tiempoGanado, 10));
				listaTrabajos.add(new AccionTrabajo(133, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				break;
			case OFICIO_ALQUIMISTA:
				if (nivel > 49) {
					listaTrabajos.add(new AccionTrabajo(160, -9 + dropGanado, -8 + dropGanado, false, 12000 - tiempoGanado, 35));
					listaTrabajos.add(new AccionTrabajo(74, -9 + dropGanado, -8 + dropGanado, false, 12000 - tiempoGanado, 35));
				}
				if (nivel > 39) {
					listaTrabajos.add(new AccionTrabajo(73, -7 + dropGanado, -6 + dropGanado, false, 12000 - tiempoGanado, 30));
				}
				if (nivel > 29) {
					listaTrabajos.add(new AccionTrabajo(72, -5 + dropGanado, -4 + dropGanado, false, 12000 - tiempoGanado, 25));
				}
				if (nivel > 19) {
					listaTrabajos.add(new AccionTrabajo(71, -3 + dropGanado, -2 + dropGanado, false, 12000 - tiempoGanado, 20));
				}
				if (nivel > 9) {
					listaTrabajos.add(new AccionTrabajo(54, -1 + dropGanado, 0 + dropGanado, false, 12000 - tiempoGanado, 15));
				}
				listaTrabajos.add(new AccionTrabajo(68, 1 + dropGanado, 2 + dropGanado, false, 12000 - tiempoGanado, 10));
				listaTrabajos.add(new AccionTrabajo(23, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				break;
			case OFICIO_LEÑADOR:
				if (nivel > 99) {
					listaTrabajos
							.add(new AccionTrabajo(158, -19 + dropGanado, -18 + dropGanado, false, 12000 - tiempoGanado, 75));
				}
				if (nivel > 89) {
					listaTrabajos.add(new AccionTrabajo(35, -17 + dropGanado, -16 + dropGanado, false, 12000 - tiempoGanado, 70));
				}
				if (nivel > 79) {
					listaTrabajos.add(new AccionTrabajo(38, -15 + dropGanado, -14 + dropGanado, false, 12000 - tiempoGanado, 65));
					listaTrabajos
							.add(new AccionTrabajo(155, -15 + dropGanado, -14 + dropGanado, false, 12000 - tiempoGanado, 65));
				}
				if (nivel > 74) {
					listaTrabajos
							.add(new AccionTrabajo(174, -14 + dropGanado, -13 + dropGanado, false, 12000 - tiempoGanado, 55));
				}
				if (nivel > 69) {
					listaTrabajos.add(new AccionTrabajo(34, -13 + dropGanado, -12 + dropGanado, false, 12000 - tiempoGanado, 50));
				}
				if (nivel > 59) {
					listaTrabajos.add(new AccionTrabajo(41, -11 + dropGanado, -10 + dropGanado, false, 12000 - tiempoGanado, 45));
				}
				if (nivel > 49) {
					listaTrabajos.add(new AccionTrabajo(33, -9 + dropGanado, -8 + dropGanado, false, 12000 - tiempoGanado, 40));
					listaTrabajos.add(new AccionTrabajo(154, -9 + dropGanado, -8 + dropGanado, false, 12000 - tiempoGanado, 40));
				}
				if (nivel > 39) {
					listaTrabajos.add(new AccionTrabajo(37, -7 + dropGanado, -6 + dropGanado, false, 12000 - tiempoGanado, 35));
				}
				if (nivel > 34) {
					listaTrabajos.add(new AccionTrabajo(139, -6 + dropGanado, -5 + dropGanado, false, 12000 - tiempoGanado, 30));
					listaTrabajos.add(new AccionTrabajo(141, -6 + dropGanado, -5 + dropGanado, false, 12000 - tiempoGanado, 30));
				}
				if (nivel > 29) {
					listaTrabajos.add(new AccionTrabajo(10, -5 + dropGanado, -4 + dropGanado, false, 12000 - tiempoGanado, 25));
				}
				if (nivel > 19) {
					listaTrabajos.add(new AccionTrabajo(40, -3 + dropGanado, -2 + dropGanado, false, 12000 - tiempoGanado, 20));
				}
				if (nivel > 9) {
					listaTrabajos.add(new AccionTrabajo(39, -1 + dropGanado, 0 + dropGanado, false, 12000 - tiempoGanado, 15));
				}
				listaTrabajos.add(new AccionTrabajo(6, 1 + dropGanado, 2 + dropGanado, false, 12000 - tiempoGanado, 10));
				listaTrabajos.add(new AccionTrabajo(101, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				break;
			case OFICIO_CAMPESINO:
				if (nivel > 69) {
					listaTrabajos.add(new AccionTrabajo(54, -13 + dropGanado, -12 + dropGanado, false, 12000 - tiempoGanado, 45));
				}
				if (nivel > 59) {
					listaTrabajos.add(new AccionTrabajo(58, -11 + dropGanado, -10 + dropGanado, false, 12000 - tiempoGanado, 40));
				}
				if (nivel > 49) {
					listaTrabajos.add(new AccionTrabajo(159, -9 + dropGanado, -8 + dropGanado, false, 12000 - tiempoGanado, 35));
					listaTrabajos.add(new AccionTrabajo(52, -9 + dropGanado, -8 + dropGanado, false, 12000 - tiempoGanado, 35));
				}
				if (nivel > 39) {
					listaTrabajos.add(new AccionTrabajo(50, -7 + dropGanado, -6 + dropGanado, false, 12000 - tiempoGanado, 30));
				}
				if (nivel > 29) {
					listaTrabajos.add(new AccionTrabajo(46, -5 + dropGanado, -4 + dropGanado, false, 12000 - tiempoGanado, 25));
				}
				if (nivel > 19) {
					listaTrabajos.add(new AccionTrabajo(57, -3 + dropGanado, -2 + dropGanado, false, 12000 - tiempoGanado, 20));
				}
				if (nivel > 9) {
					listaTrabajos.add(new AccionTrabajo(53, -1 + dropGanado, 0 + dropGanado, false, 12000 - tiempoGanado, 15));
				}
				listaTrabajos.add(new AccionTrabajo(45, 1 + dropGanado, 2 + dropGanado, false, 12000 - tiempoGanado, 10));
				listaTrabajos.add(new AccionTrabajo(47, 2, getIngMaxPorNivel(nivel), true, getSuerteMaxPorNivel(nivel), -1));
				listaTrabajos.add(new AccionTrabajo(122, 1, 1, true, 100, -1));
				break;
		}
		return listaTrabajos;
	}
	
	public static int getIngMaxPorNivel(int nivel) {
		if (nivel < 10)
			return 2;
		if (nivel >= 100)
			return 9;
		return (int) (nivel / 20) + 3;
	}
	
	public static int getSuerteMaxPorNivel(int nivel) {
		if (nivel < 10)
			return 50;
		return 54 + (int) ( (nivel / 10F) - 1) * 5;
	}
	
	public static int getSuerteNivelYSlots(int nivel, int slots) {
		if (nivel < 10)
			return 50;
		return (int) ( (54 + ( (nivel / 10F) - 1) * 5) * (getIngMaxPorNivel(nivel) / (float) slots));
	}
	
	public static int calculXpGanadaEnOficio(int nivel, int nroCasillas) {
		if (nivel == 100)
			return 0;
		switch (nroCasillas) {
			case 1:
				if (nivel < 10)
					return 1;
				return 0;
			case 2:
				if (nivel < 60)
					return 10;
				return 0;
			case 3:
				if (nivel > 9 && nivel < 80)
					return 25;
				return 0;
			case 4:
				if (nivel > 19)
					return 50;
				return 0;
			case 5:
				if (nivel > 39)
					return 100;
				return 0;
			case 6:
				if (nivel > 59)
					return 250;
				return 0;
			case 7:
				if (nivel > 79)
					return 500;
				return 0;
			case 8:
				if (nivel > 99)
					return 1000;
				return 0;
		}
		return 0;
	}
	
	public static boolean esTrabajo(int trabajoID) {
		for (int v = 0; v < ACCIONES_TRABAJO.length; v++) {
			if (ACCIONES_TRABAJO[v][0] == trabajoID)
				return true;
		}
		return false;
	}
	
	public static int getObjetoPorTrabajo(int trabajoID, boolean especial) {
		try {
			for (int v = 0; v < ACCIONES_TRABAJO.length; v++)
				if (ACCIONES_TRABAJO[v][0] == trabajoID)
					return (ACCIONES_TRABAJO[v].length > 1 && especial ? ACCIONES_TRABAJO[v][2] : ACCIONES_TRABAJO[v][1]);
		} catch (Exception e) {
			return -1;
		}
		return -1;
	}
	
	public static int getSuertePorNroCasillaYNivel(int nivel, int nroCasilla) {
		if (nroCasilla <= getIngMaxPorNivel(nivel) - 2)
			return 100;
		return getSuerteNivelYSlots(nivel, nroCasilla);
	}
	
	public static boolean esOficioMago(int id) {
		if ( (id >= 43 && id <= 50) || (id >= 62 && id <= 64))
			return true;
		return false;
	}
	
	public static String getStringColorDragopavo(int color) {
		switch (color) {
			case 1: // Dragopavo almendrado salvaje
				return "16772045,-1,16772045";
			case 3: // Dragopavo ébano
				return "1245184,393216,1245184";
			case 6: // Dragopavo pelirrojo y salvaje
				return "16747520,-1,16747520";
			case 9: // Dragopavo ébano y marfil
				return "1182992,16777200,16777200";
			case 10: // Dragopavo pelirrojo
				return "16747520,-1,16747520";
			case 11: // Dragopavo marfil y pelirrojo
				return "16747520,16777200,16777200";
			case 12: // Dragopavo ébano y pelirrojo
				return "16747520,1703936,1774084";
			case 15: // Dragopavo turquesa
				return "4251856,-1,4251856";
			case 16: // Dragopavo marfil
				return "16777200,16777200,16777200";
			case 17: // Dragopavo índigo
				return "4915330,-1,4915330";
			case 18: // Dragopavo dorado
				return "16766720,16766720,16766720";
			case 19: // Dragopavo púrpura
				return "14423100,-1,14423100";
			case 20: // Dragopavo almendrado
				return "16772045,-1,16772045";
			case 21: // Dragopavo esmeralda
				return "3329330,-1,3329330";
			case 22: // Dragopavo orquídeo
				return "15859954,16777200,15859954";
			case 23: // Dragopavo ciruela
				return "14524637,-1,14524637";
			case 33: // Dragopavo almendrado y dorado
				return "16772045,16766720,16766720";
			case 34: // Dragopavo almendrado y ébano
				return "16772045,1245184,1245184";
			case 35: // Dragopavo almendrado y esmeralda
				return "16772045,3329330,3329330";
			case 36: // Dragopavo almendrado e índigo
				return "16772045,4915330,4915330";
			case 37: // Dragopavo almendrado y marfil
				return "16772045,16777200,16777200";
			case 38: // Dragopavo almendrado y pelirrojo
				return "16772045,16747520,16747520";
			case 39: // Dragopavo almendrado y turquesa
				return "16772045,4251856,4251856";
			case 40: // Dragopavo almendrado y orquídeo
				return "16772045,15859954,15859954";
			case 41: // Dragopavo almendrado y púrpura
				return "16772045,14423100,14423100";
			case 42: // Dragopavo dorado y ébano
				return "1245184,16766720,16766720";
			case 43: // Dragopavo dorado y esmeralda
				return "16766720,3329330,3329330";
			case 44: // Dragopavo dorado e índigo
				return "16766720,4915330,4915330";
			case 45: // Dragopavo dorado y marfil
				return "16766720,16777200,16777200";
			case 46: // Dragopavo dorado y pelirrojo
				return "16766720,16747520,16747520";
			case 47: // Dragopavo dorado y turquesa
				return "16766720,4251856,4251856";
			case 48: // Dragopavo dorado y orquídeo
				return "16766720,15859954,15859954";
			case 49: // Dragopavo dorado y púrpura
				return "16766720,14423100,14423100";
			case 50: // Dragopavo ébano y esmeralda
				return "1245184,3329330,3329330";
			case 51: // Dragopavo ébano e índigo
				return "4915330,4915330,1245184";
			case 52: // Dragopavo ébano y turquesa
				return "1245184,4251856,4251856";
			case 53: // Dragopavo ebano y orquídeo
				return "15859954,0,0";
			case 54: // Dragopavo ébano y púrpura
				return "14423100,14423100,1245184";
			case 55: // Dragopavo esmeralda e índigo
				return "3329330,4915330,4915330";
			case 56: // Dragopavo esmeralda y marfil
				return "3329330,16777200,16777200";
			case 57: // Dragopavo esmeralda y pelirrojo
				return "3329330,16747520,16747520";
			case 58: // Dragopavo esmeralda y turquesa
				return "3329330,4251856,4251856";
			case 59: // Dragopavo esmeralda y orquídeo
				return "3329330,15859954,15859954";
			case 60: // Dragopavo esmeralda y púrpura
				return "3329330,14423100,14423100";
			case 61: // Dragopavo índigo y marfil
				return "4915330,16777200,16777200";
			case 62: // Dragopavo índigo y pelirrojo
				return "4915330,16747520,16747520";
			case 63: // Dragopavo índigo y turquesa
				return "4915330,4251856,4251856";
			case 64: // Dragopavo índigo y orquídeo
				return "4915330,15859954,15859954";
			case 65: // Dragopavo índigo y púrpura
				return "14423100,4915330,4915330";
			case 66: // Dragopavo marfil y turquesa
				return "16777200,4251856,4251856";
			case 67: // Dragopavo marfil y orquídeo
				return "16777200,16731355,16711910";
			case 68: // Dragopavo marfil y púrpura
				return "14423100,16777200,16777200";
			case 69: // Dragopavo turquesa y pelirrojo
				return "4251856,16747520,16747520";
			case 70: // Dragopavo orquídeo y pelirrojo
				return "14315734,16747520,16747520";
			case 71: // Dragopavo púrpura y pelirrojo
				return "14423100,16747520,16747520";
			case 72: // Dragopavo turquesa y orquídeo
				return "15859954,4251856,4251856";
			case 73: // Dragopavo turquesa y púrpura
				return "14423100,4251856,4251856";
			case 74: // Dragopavo dorado salvaje
				return "16766720,16766720,16766720";
			case 76: // Dragopavo orquídeo y púrpura
				return "14315734,14423100,14423100";
			case 77: // Dragopavo ciruela y almendrado
				return "14524637,16772045,16772045";
			case 78: // Dragopavo ciruela y dorado
				return "14524637,16766720,16766720";
			case 79: // Dragopavo ciruela y ébano
				return "14524637,1245184,1245184";
			case 80: // Dragopavo ciruela y esmeralda
				return "14524637,3329330,3329330";
			case 82: // Dragopavo ciruela e índigo
				return "14524637,4915330,4915330";
			case 83: // Dragopavo ciruela y marfil
				return "14524637,16777200,16777200";
			case 84: // Dragopavo ciruela y pelirrojo
				return "14524637,16747520,16747520";
			case 85: // Dragopavo ciruela y turquesa
				return "14524637,4251856,4251856";
			case 86: // Dragopavo ciruela y orquídeo
				return "14524637,15859954,15859954";
			case 87: // Dragopavo ciruela y púrpura
				return "14524637,14423100,14423100";
			default:
				return "-1,-1,-1";
		}
	}
	
	public static int getGeneracion(int color) {
		switch (color) {
			case 10: // pelirrojo
			case 18: // dorado
			case 20: // almendrado
				return 1;
			case 33: // almendrado - dorado
			case 38: // almendrado - pelirrojo
			case 46: // dorado - pelirrojo
				return 2;
			case 3: // ebano
			case 17: // indigo
				return 3;
			case 62: // indigo - pelirrojo
			case 12: // ebano - pelirrojo
			case 36: // almendrado - indigo
			case 34: // almendrado - ebano
			case 44: // dorado - indigo
			case 42: // dorado - ebano
			case 51: // ebano - indigo
				return 4;
			case 19: // purpura
			case 22: // orquideo
				return 5;
			case 71: // purpura - pelirrojo
			case 70: // orquideo - pelirrojo
			case 41: // almendrado - purpura
			case 40: // almendrado - orquideo
			case 49: // dorado - purpura
			case 48: // dorado - orquideo
			case 65: // indigo - purpura
			case 64: // indigo - orquideo
			case 54: // ebano - purpura
			case 53: // ebano - orquideo
			case 76: // orquideo - purpura
				return 6;
			case 15: // turquesa
			case 16: // marfil
				return 7;
			case 11: // marfil - pelirrojo
			case 69: // turquesa - pelirrojo
			case 37: // almendrado - marfil
			case 39: // almendrado - turquesa
			case 45: // dorado - marfil
			case 47: // dorado - turquesa
			case 61: // indigo - marfil
			case 63: // indigo - turquesa
			case 9: // ebano - marfil
			case 52: // ebano - turquesa
			case 68: // marfil - purpura
			case 73: // turquesa - purpura
			case 67: // marfil - orquideo
			case 72: // orquideo - turquesa
			case 66: // marfil - turquesa
				return 8;
			case 21: // esmeralda
			case 23: // ciruela
				return 9;
			case 57:// esmeralda - pelirrojo
			case 35: // almendrado - esmeralda
			case 43: // dorado - esmeralda
			case 50: // ébano - esmeralda
			case 55: // esmeralda e índigo
			case 56: // esmeralda - marfil
			case 58: // esmeralda - turquesa
			case 59: // esmeralda - orquídeo
			case 60: // esmeralda - púrpura
			case 77: // ciruela - almendrado
			case 78: // ciruela - dorado
			case 79: // ciruela - ébano
			case 80: // ciruela - esmeralda
			case 82: // ciruela - índigo
			case 83: // ciruela - marfil
			case 84: // ciruela - pelirrojo
			case 85: // ciruela - turquesa
			case 86: // ciruela - orquídeo
				return 10;
			default:
				return 1;
		}
	}
	
	public static int colorCria(int color1, int color2) {
		int colorcria = 1;
		int A = 0;
		int B = 0;
		int C = 0;
		if (color1 == 75)
			color1 = 10;
		if (color2 == 75)
			color2 = 10;
		if (color1 > color2) {
			A = color2;// menor
			B = color1;// mayor
		} else if (color1 <= color2) {
			A = color1;// menor
			B = color2;// mayor
		}
		if (A == 10 && B == 18)
			C = 46; // pelirrojo y dorado
		else if (A == 10 && B == 20)
			C = 38; // pelirrojo y almendrado
		else if (A == 18 && B == 20)
			C = 33; // almendrado y dorado
		else if (A == 33 && B == 38)
			C = 17; // indigo
		else if (A == 33 && B == 46)
			C = 3;// ebano
		else if (A == 10 && B == 17)
			C = 62; // pelirrojo e indigo
		else if (A == 10 && B == 3)
			C = 12; // ebano y pelirrojo
		else if (A == 17 && B == 20)
			C = 36; // almendrado - indigo
		else if (A == 3 && B == 20)
			C = 34; // almendrado - ebano
		else if (A == 17 && B == 18)
			C = 44; // dorado - indigo
		else if (A == 3 && B == 18)
			C = 42; // dorado - ebano
		else if (A == 3 && B == 17)
			C = 51; // ebano - indigo
		else if (A == 38 && B == 51)
			C = 19; // purpura
		else if (A == 46 && B == 51)
			C = 22; // orquideo
		else if (A == 10 && B == 19)
			C = 71; // purpura - pelirrojo
		else if (A == 10 && B == 22)
			C = 70; // orquideo - pelirrojo
		else if (A == 19 && B == 20)
			C = 41; // almendrado - purpura
		else if (A == 20 && B == 22)
			C = 40; // almendrado - orquideo
		else if (A == 18 && B == 19)
			C = 49; // dorado - purpura
		else if (A == 18 && B == 22)
			C = 48; // dorado - orquideo
		else if (A == 17 && B == 19)
			C = 65; // indigo - purpura
		else if (A == 17 && B == 22)
			C = 64; // indigo - orquideo
		else if (A == 3 && B == 19)
			C = 54; // ebano - purpura
		else if (A == 3 && B == 22)
			C = 53; // ebano - orquideo
		else if (A == 19 && B == 22)
			C = 76; // orquideo - purpura
		else if (A == 53 && B == 76)
			C = 15; // turquesa
		else if (A == 65 && B == 76)
			C = 16; // marfil
		else if (A == 10 && B == 16)
			C = 11; // marfil - pelirrojo
		else if (A == 10 && B == 15)
			C = 69; // turquesa - pelirrojo
		else if (A == 16 && B == 20)
			C = 37; // almendrado - marfil
		else if (A == 15 && B == 20)
			C = 39; // almendrado - turquesa
		else if (A == 16 && B == 18)
			C = 45; // dorado - marfil
		else if (A == 15 && B == 18)
			C = 47; // dorado - turquesa
		else if (A == 16 && B == 17)
			C = 61; // indigo - marfil
		else if (A == 15 && B == 17)
			C = 63; // indigo - turquesa
		else if (A == 3 && B == 16)
			C = 9; // ebano - marfil
		else if (A == 3 && B == 15)
			C = 52; // ebano - turquesa
		else if (A == 16 && B == 19)
			C = 68; // marfil - purpura
		else if (A == 15 && B == 19)
			C = 73; // turquesa - purpura
		else if (A == 16 && B == 22)
			C = 67; // marfil - orquideo
		else if (A == 15 && B == 22)
			C = 72; // orquideo - turquesa
		else if (A == 15 && B == 16)
			C = 66; // marfil - turquesa
		else if (A == 66 && B == 68)
			C = 21; // esmeralda
		else if (A == 66 && B == 72)
			C = 23; // ciruela
		else if (A == 10 && B == 21)
			C = 57;// esmeralda - pelirrojo
		else if (A == 20 && B == 21)
			C = 35; // almendrado - esmeralda
		else if (A == 18 && B == 21)
			C = 43; // dorado - esmeralda
		else if (A == 3 && B == 21)
			C = 50; // ébano - esmeralda
		else if (A == 17 && B == 21)
			C = 55; // esmeralda e índigo
		else if (A == 16 && B == 21)
			C = 56; // esmeralda - marfil
		else if (A == 15 && B == 21)
			C = 58; // esmeralda - turquesa
		else if (A == 21 && B == 22)
			C = 59; // esmeralda - orquídeo
		else if (A == 19 && B == 21)
			C = 60; // esmeralda - púrpura
		else if (A == 20 && B == 23)
			C = 77; // ciruela - almendrado
		else if (A == 18 && B == 23)
			C = 78; // ciruela - dorado
		else if (A == 3 && B == 23)
			C = 79; // ciruela - ébano
		else if (A == 21 && B == 23)
			C = 80; // ciruela - esmeralda
		else if (A == 17 && B == 23)
			C = 82; // ciruela - índigo
		else if (A == 16 && B == 23)
			C = 83; // ciruela - marfil
		else if (A == 10 && B == 23)
			C = 84; // ciruela - pelirrojo
		else if (A == 15 && B == 23)
			C = 85; // ciruela - turquesa
		else if (A == 22 && B == 23)
			C = 86; // ciruela - orquídeo
		else if (A == 19 && B == 23)
			C = 87; // ciruela - purpura
		ArrayList<Integer> posibles = new ArrayList<Integer>();
		posibles.add(18);
		posibles.add(20);
		posibles.add(A);
		posibles.add(B);
		posibles.add(10);
		posibles.add(18);
		if (C != 0) {
			for (int j = 11; j > getGeneracion(C); j--)
				posibles.add(C);
		}
		colorcria = posibles.get(Formulas.getRandomValor(0, posibles.size() - 1));
		return colorcria;
	}
	
	public static Stats getStatsMonturaVIP(String definido, int nivel) {
		Stats stats = new Stats();
		if (definido.isEmpty() || definido == "")
			return stats;
		String[] statsD = definido.split(",");
		int a = statsD.length;
		ArrayList<String> s = new ArrayList<String>();
		double coef = (double) (a * a) / ( (2 * a) - 1);
		for (String todos : statsD) {
			if (s.contains(todos))
				continue;
			if (todos.equalsIgnoreCase("sabiduria")) {
				stats.addUnStat(STATS_ADD_SABIDURIA, (int) ( (nivel / coef) * 0.8));
			} else if (todos.equalsIgnoreCase("vitalidad")) {
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) ( (nivel / coef) * 4));
			} else if (todos.equalsIgnoreCase("inteligencia")) {
				stats.addUnStat(STATS_ADD_INTELIGENCIA, (int) ( (nivel / coef) * 2));
			} else if (todos.equalsIgnoreCase("fuerza")) {
				stats.addUnStat(STATS_ADD_FUERZA, (int) ( (nivel / coef) * 2));
			} else if (todos.equalsIgnoreCase("agilidad")) {
				stats.addUnStat(STATS_ADD_AGILIDAD, (int) ( (nivel / coef) * 2));
			} else if (todos.equalsIgnoreCase("suerte")) {
				stats.addUnStat(STATS_ADD_SUERTE, (int) ( (nivel / coef) * 2));
			} else if (todos.equalsIgnoreCase("alcance")) {
				stats.addUnStat(STATS_ADD_ALCANCE, (int) ( (nivel / coef) / 60));
			} else if (todos.equalsIgnoreCase("prospeccion")) {
				stats.addUnStat(STATS_ADD_PROSPECCION, (int) ( (nivel / coef) * 0.6));
			} else if (todos.equalsIgnoreCase("raire")) {
				stats.addUnStat(STATS_ADD_ResPorc_AIRE, (int) ( (nivel / coef) / 5));
			} else if (todos.equalsIgnoreCase("ragua")) {
				stats.addUnStat(STATS_ADD_ResPorc_AGUA, (int) ( (nivel / coef) / 5));
			} else if (todos.equalsIgnoreCase("rtierra")) {
				stats.addUnStat(STATS_ADD_ResPorc_TIERRA, (int) ( (nivel / coef) / 5));
			} else if (todos.equalsIgnoreCase("rfuego")) {
				stats.addUnStat(STATS_ADD_ResPorc_FUEGO, (int) ( (nivel / coef) / 5));
			} else if (todos.equalsIgnoreCase("rneutral")) {
				stats.addUnStat(STATS_ADD_ResPorc_NEUTRAL, (int) ( (nivel / coef) / 5));
			} else if (todos.equalsIgnoreCase("daños")) {
				stats.addUnStat(STATS_ADD_PORC_DAÑOS, (int) ( (nivel / coef) * 0.7));
			} else if (todos.equalsIgnoreCase("iniciativa")) {
				stats.addUnStat(STATS_ADD_INIT, (int) ( (nivel / coef) * 16));
			}
			s.add(todos);
		}
		return stats;
	}
	
	public static Stats getStatsMontura(int color, int nivel) {
		Stats stats = new Stats();
		switch (color) {
			case 1:
				break;
			case 3:// Ebano
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel / 2);
				stats.addUnStat(STATS_ADD_AGILIDAD, (int) (nivel / 1.25));// 100/1.25 = 80
				break;
			case 10:// Pelirrojo
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel); // 100/1 = 100
				break;
			case 20:// Almendrado
				stats.addUnStat(STATS_ADD_INIT, nivel * 10); // 100*10 = 1000
				break;
			case 18:// Dorado
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_SABIDURIA, (int) (nivel / 2.50)); // 100/2.50 = 40
				break;
			case 38:// Pelirrojo-Almendrado
				stats.addUnStat(STATS_ADD_INIT, nivel * 5); // 100*5 = 500
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_CRIATURAS_INVO, (int) (nivel / 50)); // 100/50 = 2
				break;
			case 46:// Pelirrojo-Dorado
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_SABIDURIA, (int) (nivel / 4)); // 100/4 = 25
				break;
			case 33:// Almendrado-Dorado
				stats.addUnStat(STATS_ADD_INIT, nivel * 5);
				stats.addUnStat(STATS_ADD_SABIDURIA, (int) (nivel / 4));
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_CRIATURAS_INVO, (int) (nivel / 100)); // 100/100 = 1
				break;
			case 17:// Indigo
				stats.addUnStat(STATS_ADD_SUERTE, (int) (nivel / 1.25));
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel / 2));
				break;
			case 62:// Pelirrojo-Indigo
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel * 1.50)); // 100*1.50 = 150
				stats.addUnStat(STATS_ADD_SUERTE, (int) (nivel / 1.65));
				break;
			case 12:// Pelirrojo-Ebano
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel * 1.50));
				stats.addUnStat(STATS_ADD_AGILIDAD, (int) (nivel / 1.65));
				break;
			case 36:// Almendrado-Indigo
				stats.addUnStat(STATS_ADD_INIT, nivel * 5);
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_SUERTE, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_CRIATURAS_INVO, (int) (nivel / 100));
				break;
			case 19:// Purpura
				stats.addUnStat(STATS_ADD_FUERZA, (int) (nivel / 1.25));
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel / 2));
				break;
			case 22:// Orquideo
				stats.addUnStat(STATS_ADD_INTELIGENCIA, (int) (nivel / 1.25));
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel / 2));
				break;
			case 48:// Dorado-Orquideo
				stats.addUnStat(STATS_ADD_VITALIDAD, (nivel));
				stats.addUnStat(STATS_ADD_SABIDURIA, (int) (nivel / 4));
				stats.addUnStat(STATS_ADD_INTELIGENCIA, (int) (nivel / 1.65));
				break;
			case 65:// Indigo-Purpura
				stats.addUnStat(STATS_ADD_VITALIDAD, (nivel));
				stats.addUnStat(STATS_ADD_SUERTE, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_FUERZA, (int) (nivel / 2));
				break;
			case 64:// Indigo-Orquideo
				stats.addUnStat(STATS_ADD_VITALIDAD, (nivel));
				stats.addUnStat(STATS_ADD_SUERTE, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_INTELIGENCIA, (int) (nivel / 2));
				break;
			case 54:// Ebano-Purpura
				stats.addUnStat(STATS_ADD_VITALIDAD, (nivel));
				stats.addUnStat(STATS_ADD_FUERZA, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_AGILIDAD, (int) (nivel / 2));
				break;
			case 53:// Ebano-Orquideo
				stats.addUnStat(STATS_ADD_VITALIDAD, (nivel));
				stats.addUnStat(STATS_ADD_AGILIDAD, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_INTELIGENCIA, (int) (nivel / 2));
				break;
			case 76:// Purpura-Orquideo
				stats.addUnStat(STATS_ADD_VITALIDAD, (nivel));
				stats.addUnStat(STATS_ADD_INTELIGENCIA, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_FUERZA, (int) (nivel / 2));
				break;
			case 34:// Almendrado-Ebano
				stats.addUnStat(STATS_ADD_INIT, nivel * 5);
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_AGILIDAD, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_CRIATURAS_INVO, (int) (nivel / 100));
				break;
			case 44:// Almendrado-Pelirrojo
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_SABIDURIA, (int) (nivel / 4));
				stats.addUnStat(STATS_ADD_SUERTE, (int) (nivel / 1.65));
				break;
			case 42:// Dorado-Ebano
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_SABIDURIA, (int) (nivel / 4));
				stats.addUnStat(STATS_ADD_AGILIDAD, (int) (nivel / 1.65));
				break;
			case 51:// Indigo-Ebano
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_SUERTE, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_AGILIDAD, (int) (nivel / 2));
				break;
			case 71:// Pelirrojo-Purpura
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel * 1.5));
				stats.addUnStat(STATS_ADD_FUERZA, (int) (nivel / 1.65));
				break;
			case 70:// Pelirrojo-Orquideo
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel * 1.5));
				stats.addUnStat(STATS_ADD_INTELIGENCIA, (int) (nivel / 1.65));
				break;
			case 41:// Almendrado-Purpura
				stats.addUnStat(STATS_ADD_INIT, nivel * 5);
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_FUERZA, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_CRIATURAS_INVO, (int) (nivel / 100));
				break;
			case 40:// Almendrado-Orquideo
				stats.addUnStat(STATS_ADD_INIT, nivel * 5);
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_INTELIGENCIA, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_CRIATURAS_INVO, (int) (nivel / 100));
				break;
			case 49:// Dorado-Purpura
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_SABIDURIA, (int) (nivel / 4));
				stats.addUnStat(STATS_ADD_FUERZA, (int) (nivel / 1.65));
				break;
			case 16:// Marfil
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_PORC_DAÑOS, (int) (nivel / 2));
				break;
			case 15:// Turquesa
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_PROSPECCION, (int) (nivel / 1.25));
				break;
			case 11:// Pelirrojo-Marfil
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel * 2)); // 100*2 = 200
				stats.addUnStat(STATS_ADD_PORC_DAÑOS, (int) (nivel / 2.5)); // = 40
				break;
			case 69:// Pelirrojo-Turquesa
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel * 2));
				stats.addUnStat(STATS_ADD_PROSPECCION, (int) (nivel / 2.50));
				break;
			case 39:// Almendrado-Turquesa
				stats.addUnStat(STATS_ADD_INIT, nivel * 5);
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_PROSPECCION, (int) (nivel / 2.50));
				stats.addUnStat(STATS_ADD_CRIATURAS_INVO, (int) (nivel / 100));
				break;
			case 37:// Almendrado-Turquesa
				stats.addUnStat(STATS_ADD_INIT, nivel * 5);
				stats.addUnStat(STATS_ADD_VITALIDAD, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_PORC_DAÑOS, (int) (nivel / 2.50));
				stats.addUnStat(STATS_ADD_CRIATURAS_INVO, (int) (nivel / 100));
				break;
			case 45:// Dorado-Marfil
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_PORC_DAÑOS, (int) (nivel / 2.5));
				stats.addUnStat(STATS_ADD_SABIDURIA, (int) (nivel / 4));
				break;
			case 47:// Dorado-Turquesa
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_PROSPECCION, (int) (nivel / 2.50));
				stats.addUnStat(STATS_ADD_SABIDURIA, (int) (nivel / 4));
				break;
			case 61:// Indigo-Marfil
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_SUERTE, (int) (nivel / 2.50));
				stats.addUnStat(STATS_ADD_PORC_DAÑOS, (int) (nivel / 2.5));
				break;
			case 63:// Indigo-Turquesa
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_SUERTE, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_PROSPECCION, (int) (nivel / 2.5));
				break;
			case 9:// Ebano-Marfil
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_AGILIDAD, (int) (nivel / 2.50));
				stats.addUnStat(STATS_ADD_PORC_DAÑOS, (int) (nivel / 2.5));
				break;
			case 52:// Ebano-Turquesa
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_AGILIDAD, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_PROSPECCION, (int) (nivel / 2.50));
				break;
			case 67:// Purpura-Marfil
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_INTELIGENCIA, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_PORC_DAÑOS, (int) (nivel / 2.5));
				break;
			case 68:// Purpura-Marfil
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_FUERZA, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_PORC_DAÑOS, (int) (nivel / 2.5));
				break;
			case 73:// Purpura-Turquesa
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_FUERZA, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_PROSPECCION, (int) (nivel / 2.50));
				break;
			case 72:// Orquideo-Turquesa
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_INTELIGENCIA, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_PROSPECCION, (int) (nivel / 2.5));
				break;
			case 66:// Marfil-Turquesa
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_PORC_DAÑOS, (int) (nivel / 2.5));
				stats.addUnStat(STATS_ADD_PROSPECCION, (int) (nivel / 2.50));
				break;
			case 21:// Esmeralda
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel * 2);
				stats.addUnStat(STATS_ADD_PM, (int) (nivel / 100));
				break;
			case 23:// Ciruela
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel * 2); // 100*2 = 200
				stats.addUnStat(STATS_ADD_ALCANCE, (int) (nivel / 50));
				break;
			case 57:// Esmeralda-Pelirrojo
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel * 3); // 100*3 = 300
				stats.addUnStat(STATS_ADD_PM, (int) (nivel / 100));
				break;
			case 84:// Pelirrojo-Ciruela
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel * 3);
				stats.addUnStat(STATS_ADD_ALCANCE, (int) (nivel / 100));
				break;
			case 35:// Almendrado-Esmeralda
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_PM, (int) (nivel / 100));
				stats.addUnStat(STATS_ADD_CRIATURAS_INVO, (int) (nivel / 100));
				stats.addUnStat(STATS_ADD_INIT, nivel * 5);
				break;
			case 77:// Almendrado-Ciruela
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel * 2);
				stats.addUnStat(STATS_ADD_INIT, nivel * 5);
				stats.addUnStat(STATS_ADD_ALCANCE, (int) (nivel / 100));
				stats.addUnStat(STATS_ADD_CRIATURAS_INVO, (int) (nivel / 100));
				break;
			case 43:// Dorado-Esmeralda
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_SABIDURIA, (int) (nivel / 4));
				stats.addUnStat(STATS_ADD_PM, (int) (nivel / 100));
				break;
			case 78:// Dorado-Ciruela
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel * 2);
				stats.addUnStat(STATS_ADD_SABIDURIA, (int) (nivel / 4));
				stats.addUnStat(STATS_ADD_ALCANCE, (int) (nivel / 100));
				break;
			case 55:// Indigo-Esmeralda
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_SUERTE, (int) (nivel / 3.33));
				stats.addUnStat(STATS_ADD_PM, (int) (nivel / 100));
				break;
			case 82:// Indigo-Ciruela
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel * 2);
				stats.addUnStat(STATS_ADD_SUERTE, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_ALCANCE, (int) (nivel / 100));
				break;
			case 50:// Ebano-Esmeralda
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_AGILIDAD, (int) (nivel / 3.33));
				stats.addUnStat(STATS_ADD_PM, (int) (nivel / 100));
				break;
			case 79:// Ebano-Ciruela
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel * 2);
				stats.addUnStat(STATS_ADD_AGILIDAD, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_ALCANCE, (int) (nivel / 100));
				break;
			case 60:// Purpura-Esmeralda
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_FUERZA, (int) (nivel / 3.33));
				stats.addUnStat(STATS_ADD_PM, (int) (nivel / 100));
				break;
			case 87:// Purpura-Ciruela
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel * 2);
				stats.addUnStat(STATS_ADD_FUERZA, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_ALCANCE, (int) (nivel / 100));
				break;
			case 59:// Orquideo-Esmeralda
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_INTELIGENCIA, (int) (nivel / 3.33));
				stats.addUnStat(STATS_ADD_PM, (int) (nivel / 100));
				break;
			case 86:// Orquideo-Ciruela
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel * 2);
				stats.addUnStat(STATS_ADD_INTELIGENCIA, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_ALCANCE, (int) (nivel / 100));
				break;
			case 56:// Marfil-Esmeralda
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_PORC_DAÑOS, (int) (nivel / 3.33));
				stats.addUnStat(STATS_ADD_PM, (int) (nivel / 100));
				break;
			case 83:// Marfil-Ciruela
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel * 2);
				stats.addUnStat(STATS_ADD_PORC_DAÑOS, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_ALCANCE, (int) (nivel / 100));
				break;
			case 58:// Turquesa-Esmeralda
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel);
				stats.addUnStat(STATS_ADD_PROSPECCION, (int) (nivel / 3.33));
				stats.addUnStat(STATS_ADD_PM, (int) (nivel / 100));
				break;
			case 85:// Turquesa-Ciruela
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel * 2);
				stats.addUnStat(STATS_ADD_PROSPECCION, (int) (nivel / 1.65));
				stats.addUnStat(STATS_ADD_ALCANCE, (int) (nivel / 100));
				break;
			case 80:// Esmeralda-Ciruela
				stats.addUnStat(STATS_ADD_VITALIDAD, nivel * 2);
				stats.addUnStat(STATS_ADD_PM, (int) (nivel / 100));
				stats.addUnStat(STATS_ADD_ALCANCE, (int) (nivel / 100));
				break;
			case 88:// Armadura
				stats.addUnStat(STATS_ADD_PORC_DAÑOS, (int) (nivel / 2));
				stats.addUnStat(STATS_ADD_ResPorc_AIRE, (int) (nivel / 20));
				stats.addUnStat(STATS_ADD_ResPorc_AGUA, (int) (nivel / 20));
				stats.addUnStat(STATS_ADD_ResPorc_TIERRA, (int) (nivel / 20));
				stats.addUnStat(STATS_ADD_ResPorc_FUEGO, (int) (nivel / 20));
				stats.addUnStat(STATS_ADD_ResPorc_NEUTRAL, (int) (nivel / 20));
				break;
			case 75://
		}
		return stats;
	}
	
	public static int getScrollporMontura(int color) {
		switch (color) {
			case 33: // almendrado - dorado
				return 802;
			case 38: // almendrado - pelirrojo
				return 806;
			case 46: // dorado - pelirrojo
				return 802;
			case 3: // ebano
				return 798;
			case 17: // indigo
				return 809;
			case 62: // indigo - pelirrojo
			case 12: // ebano - pelirrojo
				return 806;
			case 36: // almendrado - indigo
				return 809;
			case 34: // almendrado - ebano
				return 798;
			case 44: // dorado - indigo
				return 802;
			case 42: // dorado - ebano
				return 802;
			case 51: // ebano - indigo
				return 809;
			case 19: // purpura
				return 683;
			case 22: // orquideo
				return 686;
			case 71: // purpura - pelirrojo
				return 683;
			case 70: // orquideo - pelirrojo
				return 815;
			case 41: // almendrado - purpura
				return 795;
			case 40: // almendrado - orquideo
				return 807;
			case 49: // dorado - purpura
			case 48: // dorado - orquideo
				return 803;
			case 65: // indigo - purpura
			case 64: // indigo - orquideo
				return 811;
			case 54: // ebano - purpura
			case 53: // ebano - orquideo
				return 799;
			case 76: // orquideo - purpura
				return 815;
			case 15: // turquesa
				return 800;
			case 16: // marfil
				return 812;
			case 11: // marfil - pelirrojo
			case 69: // turquesa - pelirrojo
				return 808;
			case 37: // almendrado - marfil
				return 812;
			case 39: // almendrado - turquesa
				return 808;
			case 45: // dorado - marfil
			case 47: // dorado - turquesa
				return 804;
			case 61: // indigo - marfil
			case 63: // indigo - turquesa
				return 812;
			case 9: // ebano - marfil
			case 52: // ebano - turquesa
				return 800;
			case 68: // marfil - purpura
			case 73: // turquesa - purpura
				return 796;
			case 67: // marfil - orquideo
			case 72: // orquideo - turquesa
				return 816;
			case 66: // marfil - turquesa
				return 800;
			case 21: // esmeralda
				return 797;
			case 23: // ciruela
				return 817;
			case 57:// esmeralda - pelirrojo
			case 35: // almendrado - esmeralda
				return 810;
			case 43: // dorado - esmeralda
				return 805;
			case 50: // ébano - esmeralda
				return 797;
			case 55: // esmeralda e índigo
				return 814;
			case 56: // esmeralda - marfil
				return 814;
			case 58: // esmeralda - turquesa
				return 801;
			case 59: // esmeralda - orquídeo
				return 817;
			case 60: // esmeralda - púrpura
				return 797;
			case 77: // ciruela - almendrado
				return 810;
			case 78: // ciruela - dorado
				return 805;
			case 79: // ciruela - ébano
				return 801;
			case 80: // ciruela - esmeralda
				return 817;
			case 82: // ciruela - índigo
				return 814;
			case 83: // ciruela - marfil
				return 814;
			case 84: // ciruela - pelirrojo
				return 810;
			case 85: // ciruela - turquesa
				return 801;
			case 86: // ciruela - orquídeo
				return 817;
			default:
				return -1;
		}
	}
	
	public static ObjetoModelo getPergaPorColorDragopavo(int color) {
		switch (color) {
		// Ammande Salvaje
			case 2:
				return MundoDofus.getObjModelo(7807);
				// Ebano | Page 1
			case 3:
				return MundoDofus.getObjModelo(7808);
				// Pelirrojo Salvaje
			case 4:
				return MundoDofus.getObjModelo(7809);
				// Ebano-Indigo
			case 9:
				return MundoDofus.getObjModelo(7810);
				// Pelirrojo
			case 10:
				return MundoDofus.getObjModelo(7811);
				// Indigo-Pelirrojo
			case 11:
				return MundoDofus.getObjModelo(7812);
				// Ebano-Pelirrojo
			case 12:
				return MundoDofus.getObjModelo(7813);
				// Turquesa
			case 15:
				return MundoDofus.getObjModelo(7814);
				// Indigo
			case 16:
				return MundoDofus.getObjModelo(7815);
				// Indigo
			case 17:
				return MundoDofus.getObjModelo(7816);
				// Dorado
			case 18:
				return MundoDofus.getObjModelo(7817);
				// Purpura
			case 19:
				return MundoDofus.getObjModelo(7818);
				// Almendrado
			case 20:
				return MundoDofus.getObjModelo(7819);
				// Esmeralda
			case 21:
				return MundoDofus.getObjModelo(7820);
				// Orquideo
			case 22:
				return MundoDofus.getObjModelo(7821);
				// Ciruela
			case 23:
				return MundoDofus.getObjModelo(7822);
				// Almendrado-Dorado
			case 33:
				return MundoDofus.getObjModelo(7823);
				// Almendrado-Ebano
			case 34:
				return MundoDofus.getObjModelo(7824);
				// Almendrado-Esmeralda
			case 35:
				return MundoDofus.getObjModelo(7825);
				// Almendrado-Indigo
			case 36:
				return MundoDofus.getObjModelo(7826);
				// Almendrado-Indigo
			case 37:
				return MundoDofus.getObjModelo(7827);
				// Almendrado-Pelirrojo
			case 38:
				return MundoDofus.getObjModelo(7828);
				// Almendrado-Turquesa
			case 39:
				return MundoDofus.getObjModelo(7829);
				// Almendrado-Orquideo
			case 40:
				return MundoDofus.getObjModelo(7830);
				// Almendrado-Purpura
			case 41:
				return MundoDofus.getObjModelo(7831);
				// Dorado-Ebano
			case 42:
				return MundoDofus.getObjModelo(7832);
				// Dorado-Esmeralda
			case 43:
				return MundoDofus.getObjModelo(7833);
				// Dorado-Indigo
			case 44:
				return MundoDofus.getObjModelo(7834);
				// Dorado-Indigo
			case 45:
				return MundoDofus.getObjModelo(7835);
				// Dorado-Pelirrojo | Page 2
			case 46:
				return MundoDofus.getObjModelo(7836);
				// Dorado-Turquesa
			case 47:
				return MundoDofus.getObjModelo(7837);
				// Dorado-Orquideo
			case 48:
				return MundoDofus.getObjModelo(7838);
				// Dorado-Purpura
			case 49:
				return MundoDofus.getObjModelo(7839);
				// Ebano-Esmeralda
			case 50:
				return MundoDofus.getObjModelo(7840);
				// Ebano-Indigo
			case 51:
				return MundoDofus.getObjModelo(7841);
				// Ebano-Turquesa
			case 52:
				return MundoDofus.getObjModelo(7842);
				// Ebano-Orquideo
			case 53:
				return MundoDofus.getObjModelo(7843);
				// Ebano-Purpura
			case 54:
				return MundoDofus.getObjModelo(7844);
				// Esmeralda-Indigo
			case 55:
				return MundoDofus.getObjModelo(7845);
				// Esmeralda-Indigo
			case 56:
				return MundoDofus.getObjModelo(7846);
				// Esmeralda-Pelirrojo
			case 57:
				return MundoDofus.getObjModelo(7847);
				// Esmeralda-Turquesa
			case 58:
				return MundoDofus.getObjModelo(7848);
				// Esmeralda-Orquideo
			case 59:
				return MundoDofus.getObjModelo(7849);
				// Esmeralda-Purpura
			case 60:
				return MundoDofus.getObjModelo(7850);
				// Indigo-Indigo
			case 61:
				return MundoDofus.getObjModelo(7851);
				// Indigo-Pelirrojo
			case 62:
				return MundoDofus.getObjModelo(7852);
				// Indigo-Turquesa
			case 63:
				return MundoDofus.getObjModelo(7853);
				// Indigo-Orquideo
			case 64:
				return MundoDofus.getObjModelo(7854);
				// Indigo-Purpura
			case 65:
				return MundoDofus.getObjModelo(7855);
				// Indigo-Turquesa
			case 66:
				return MundoDofus.getObjModelo(7856);
				// Indigo-Marfil
			case 67:
				return MundoDofus.getObjModelo(7857);
				// Indigo-Purpura
			case 68:
				return MundoDofus.getObjModelo(7858);
				// Turquesa-Pelirrojo
			case 69:
				return MundoDofus.getObjModelo(7859);
				// Marfil-Pelirrojo
			case 70:
				return MundoDofus.getObjModelo(7860);
				// Purpura-Pelirrojo
			case 71:
				return MundoDofus.getObjModelo(7861);
				// Turquesa-Orquideo
			case 72:
				return MundoDofus.getObjModelo(7862);
				// Turquesa-Purpura
			case 73:
				return MundoDofus.getObjModelo(7863);
				// Dorado Salvaje
			case 74:
				return MundoDofus.getObjModelo(7864);
				// VIP
			case 75:
				return MundoDofus.getObjModelo(11143);
				// Orquideo-Purpura
			case 76:
				return MundoDofus.getObjModelo(7866);
				// Ciruela-Almendrado
			case 77:
				return MundoDofus.getObjModelo(7867);
				// Ciruela-Dorado
			case 78:
				return MundoDofus.getObjModelo(7868);
				// Ciruela-Ebano
			case 79:
				return MundoDofus.getObjModelo(7869);
				// Ciruela-Esmeralda
			case 80:
				return MundoDofus.getObjModelo(7870);
				// Ciruela e Indigo
			case 82:
				return MundoDofus.getObjModelo(7871);
				// Ciruela-Indigo
			case 83:
				return MundoDofus.getObjModelo(7872);
				// Ciruela-Pelirrojo
			case 84:
				return MundoDofus.getObjModelo(7873);
				// Ciruela-Turquesa
			case 85:
				return MundoDofus.getObjModelo(7874);
				// Ciruela-Orquideo
			case 86:
				return MundoDofus.getObjModelo(7875);
				// Ciruela-Purpura
			case 87:
				return MundoDofus.getObjModelo(7876);
				// Armadura
			case 88:
				return MundoDofus.getObjModelo(9582);
		}
		return null;
	}
	
	public static int getColorDragoPavoPorPerga(int tID) {
		for (int a = 1; a < 100; a++)
			if (getPergaPorColorDragopavo(a) != null)
				if (getPergaPorColorDragopavo(a).getID() == tID)
					return a;
		return -1;
	}
	
	public static void aplicarAccionOI(Personaje perso, int mapaID, int celdaID) {
		switch (mapaID) {
			case 2196:// Creacion de gremio
				if (perso.estaOcupado())
					return;
				if (perso.getGremio() != null || perso.getMiembroGremio() != null) {
					GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO(perso, "Ea");
					return;
				}
				if (!perso.tieneObjModeloNoEquip(1575, 1)) {
					GestorSalida.ENVIAR_Im_INFORMACION(perso, "14");
					return;
				}
				GestorSalida.ENVIAR_gn_CREAR_GREMIO(perso);
				break;
			default:
				System.out.println("Accion de OI no generado por el mapa: " + mapaID + " celda: " + celdaID);
		}
	}
	
	public static int getCeldaIDCercanaNoUsada(Personaje perso) {
		Mapa mapa = perso.getMapa();
		int celda = perso.getCelda().getID();
		int ancho = perso.getMapa().getAncho();
		int celdaFrente = celda + ancho;
		int celdaAtras = celda - ancho;
		int celdaDerecha = celda + (ancho - 1);
		int celdaIzquierda = celda - (ancho - 1);
		if (mapa.getCelda(celdaFrente).getObjetoTirado() == null && mapa.getCeldas().get(celdaFrente).getPersos().isEmpty()
				&& mapa.getCeldas().get(celdaFrente).esCaminable(false)) {
			return celdaFrente;
		} else if (mapa.getCelda(celdaAtras).getObjetoTirado() == null && mapa.getCeldas().get(celdaAtras).getPersos().isEmpty()
				&& mapa.getCeldas().get(celdaAtras).esCaminable(false)) {
			return celdaAtras;
		} else if (mapa.getCelda(celdaDerecha).getObjetoTirado() == null
				&& mapa.getCeldas().get(celdaDerecha).getPersos().isEmpty()
				&& mapa.getCeldas().get(celdaDerecha).esCaminable(false)) {
			return celdaDerecha;
		} else if (mapa.getCelda(celdaIzquierda).getObjetoTirado() == null
				&& mapa.getCeldas().get(celdaIzquierda).getPersos().isEmpty()
				&& mapa.getCeldas().get(celdaIzquierda).esCaminable(false)) {
			return celdaIzquierda;
		}
		return 0;
	}
	
	public static int getIdTituloOficio(int oficio) {
		switch (oficio) {
			case (43): // Forjamago de dagas primordial
				return 40;
			case (44): // Forjamago de espadas primordial
				return 41;
			case (45): // Forjamago de martillos primordial
				return 42;
			case (46): // Forjamago de palas primordial
				return 43;
			case (47): // Forjamago de hachas primordial
				return 44;
			case (48): // ESCULTORMAGO de arcos primordial
				return 45;
			case (49): // ESCULTORMAGO de varitas primordial
				return 46;
			case (50): // ESCULTORMAGO de bastones primordial
				return 47;
			case (62): // Zapateromago primordial
				return 51;
			case (63): // Joyeromago primordial
				return 52;
			case (64): // Sastremago primordial
				return 53;
		}
		return 0;
	}
	
	public static int getValorStatRuna(int statID) {
		int multi = 1;
		if (statID == 118 || statID == 126 || statID == 125 || statID == 119 || statID == 123 || statID == 158 || statID == 174)// Fuerza,
																																// inteligencia,
																																// vitalidad,
																																// agilidad,
																																// suerte,
																																// pod
																																// e
																																// iniciativa
		{
			multi = 1;
		} else if (statID == 138 || statID == 666 || statID == 226 || statID == 220)// Daños %,Reenvio de daños,Trampas %
		{
			multi = 2;
		} else if (statID == 124 || statID == 176)// Sabiduria, prospeccion
		{
			multi = 3;
		} else if (statID == 240 || statID == 241 || statID == 242 || statID == 243 || statID == 244)// Resistencia + fuego,
																										// aire, tierra, agua,
																										// neutral
		{
			multi = 4;
		} else if (statID == 210 || statID == 211 || statID == 212 || statID == 213 || statID == 214)// Resistencia % fuego,
																										// aire, tierra, agua,
																										// neutral
		{
			multi = 5;
		} else if (statID == 225)// Trampas
		{
			multi = 6;
		} else if (statID == 178 || statID == 112)// Curas,Daños
		{
			multi = 7;
		} else if (statID == 115 || statID == 182)// Criticos,Invocaciones
		{
			multi = 8;
		} else if (statID == 117)// PO(alcance)
		{
			multi = 9;
		} else if (statID == 128)// PM
		{
			multi = 10;
		} else if (statID == 111)// PA
		{
			multi = 10;
		}
		return multi;
	}
	
	public static int statDeRunas(int stat, int cant) {
		switch (stat) {
			case 111:// Pa
				return 1557;// runa PA
			case 112: // daños
				return 7435;// runa daño
			case 115:
				return 7433;
			case 117:// alcance
				return 7438;// runa alcance
			case 118:// fuerza
				if (cant > 70)
					return 1551;
				else if (cant <= 70 && cant > 20)
					return 1545;
				else
					return 1519;// runa fuerza 1
			case 119: // Agi
				if (cant > 70)
					return 1555;
				else if (cant <= 70 && cant > 20)
					return 1549;
				else
					return 1524;// runa fuerza 1
			case 123:// suerte
				if (cant > 70)
					return 1556;
				else if (cant <= 70 && cant > 20)
					return 1550;
				else
					return 1525;// runa fuerza 1
			case 124:// Sabi
				if (cant > 30)
					return 1552;
				else if (cant <= 30 && cant > 10)
					return 1546;
				else
					return 1521;// runa fuerza 1
			case 125:// vita
				if (cant > 230)
					return 1554;
				else if (cant <= 230 && cant > 60)
					return 1548;
				else
					return 1523;// runa fuerza 1
			case 126: // intel
				if (cant > 70)
					return 1553;
				else if (cant <= 70 && cant > 20)
					return 1547;
				else
					return 1522;// runa fuerza 1
			case 128:// PM
				return 1558;// runa PM 1
			case 138:// Daño %
				return 7436;// runa porcDaño
			case 158:// pods
				if (cant > 300)
					return 7445;
				else if (cant <= 300 && cant > 100)
					return 7444;
				else
					return 7443;
			case 174:// iniciativa
				if (cant > 300)
					return 7450;
				else if (cant <= 300 && cant > 100)
					return 7449;
				else
					return 7448;// runa fuerza 1
			case 176:// prospeccion
				if (cant > 5)
					return 10662;
				else
					return 7451;// runa prospec
			case 178://
				return 7434;// runa cura
			case 182:// invoca
				return 7442;// runa invo
			case 220:
				return 7437;// runa reenvio
			case 225:
				return 7446;// runa daño trampa
			case 226:
				return 7447;// runa daño porc Trampa
			case 240:
				return 7452;// runa re fuego
			case 241:
				return 7453;// runa re aire
			case 242:
				return 7454;// runa re agua
			case 243:
				return 7455;// runa re tierra
			case 244:
				return 7456;// runa re neutral
			case 210:
				return 7457;// runa re %fuego
			case 211:
				return 7458;// runa re %aire
			case 212:
				return 7560;// runa re %agua
			case 213:
				return 7459;// runa re %tierra
			case 214:
				return 7460;// runa re %neutral
			default:
				return 0;
		}
	}
	
	public static int efectoElemento(int efectoID) {
		switch (efectoID) {
			case 85:
			case 91:
			case 96:
				return 1;
			case 86:
			case 92:
			case 97:
				return 2;
			case 87:
			case 93:
			case 98:
				return 3;
			case 88:
			case 94:
			case 99:
				return 4;
			case 89:
			case 95:
			case 100:
				return 5;
		}
		return -1;
	}
	
	public static boolean alimentoMontura(int tipo) {
		for (Integer t : Bustemu.ALIMENTOS_MONTURA) {
			if (tipo == t)
				return true;
		}
		return false;
	}
	
	public static int fantasmaMascota(int mascota) {
		switch (mascota) {
			case 8153: // Abra Kadabra
				return 8171;
			case 7704: // Ardilla Pilla
				return 7722;
			case 8561: // Bebé Pandawa
				return 8565;
			case 7706: // Bilby
				return 7724;
			case 8151: // Boluto
				return 8172;
			case 9594: // Boluto del Padrino
				return 9595;
			case 10657: // Buscador de ogrinas
				return 10658;
			case 2075: // Bwak de Agua
				return 7540;
			case 2076: // Bwak de Aire
				return 7541;
			case 2074: // Bwak de Fuego
				return 7539;
			case 2077: // Bwak de Tierra
				return 7542;
			case 8000: // Bworky
				return 8017;
			case 7707: // Cochino Jabato
				return 7725;
			case 9623: // Cocodrail
				return 9671;
			case 7520: // Crum
				return 7550;
			case 7703: // Cuerbokito Feo
				return 7721;
			case 9624: // Dragún negro
				return 9666;
			case 7519: // Dragún rosa
				return 7549;
			case 8154: // El Escarador
				return 8173;
			case 9785: // Fab\'huritu de Rushu
				return 9786;
			case 8693: // Feanor
				return 8706;
			case 7518: // Fotasma
				return 7548;
			case 10544: // Gelutín
				return 10597;
			case 7911: // Koalak sanguíneo
				return 8016;
			case 6604: // Kuakuá
				return 7543;
			case 7705: // Leopardo
				return 7723;
			case 1728: // Miaumiau
				return 7537;
			case 9617: // Miaumiau Angora
				return 9661;
			case 7524: // Miaumiau Atigrado
				return 8885;
			case 7891: // Mini Wey
				return 7893;
			case 7522: // Minimino
				return 7551;
			case 9620: // Miniminotot
				return 9665;
			case 10107: // Murciego
				return 10109;
			case 6716: // Nomoon
				return 7544;
			case 8155: // Pekefux
				return 8174;
			case 9619: // Pekewabbit hambriento
				return 9664;
			case 6978: // Peki
				return 7545;
			case 7414: // Pequeño Miaumiau Blanco
				return 7546;
			case 7415: // Pequeño Wauwau Negro
				return 7547;
			case 7709: // Piotín amarillo
				return 7727;
			case 7708: // Piotín azul
				return 7726;
			case 7711: // Piotín rojo
				return 7729;
			case 7710: // Piotín rosa
				return 7728;
			case 7712: // Piotín verde
				return 7730;
			case 7713: // Piotín violeta
				return 7731;
			case 8677: // Ross
				return 8679;
			case 7714: // Tortuga
				return 8020;
			case 10106: // Vampyrilla
				return 10108;
			case 1748: // Wabbit
				return 7538;
			case 8211: // Walk
				return 8524;
			case 1711: // Wauwau
				return 7536;
			case 7892: // Willy el Plasta
				return 7894;
		}
		return 0;
	}
	
	public static int resucitarMascota(int fantasma) {
		switch (fantasma) {
			case 8171:
				return 8153; // Abra Kadabra
			case 7722:
				return 7704; // Ardilla Pilla
			case 8565:
				return 8561; // Bebé Pandawa
			case 7724:
				return 7706; // Bilby
			case 8172:
				return 8151; // Boluto
			case 9595:
				return 9594; // Boluto del Padrino
			case 10658:
				return 10657; // Buscador de ogrinas
			case 7540:
				return 2075; // Bwak de Agua
			case 7541:
				return 2076; // Bwak de Aire
			case 7539:
				return 2074; // Bwak de Fuego
			case 7542:
				return 2077; // Bwak de Tierra
			case 8017:
				return 8000; // Bworky
			case 7725:
				return 7707; // Cochino Jabato
			case 9671:
				return 9623; // Cocodrail
			case 7550:
				return 7520; // Crum
			case 7721:
				return 7703; // Cuerbokito Feo
			case 9666:
				return 9624; // Dragún negro
			case 7549:
				return 7519; // Dragún rosa
			case 8173:
				return 8154; // El Escarador
			case 9786:
				return 9785; // Fab\'huritu de Rushu
			case 8706:
				return 8693; // Feanor
			case 7548:
				return 7518; // Fotasma
			case 10597:
				return 10544; // Gelutín
			case 8016:
				return 7911; // Koalak sanguíneo
			case 7543:
				return 6604; // Kuakuá
			case 7723:
				return 7705; // Leopardo
			case 7537:
				return 1728; // Miaumiau
			case 9661:
				return 9617; // Miaumiau Angora
			case 8885:
				return 7524; // Miaumiau Atigrado
			case 7893:
				return 7891; // Mini Wey
			case 7551:
				return 7522; // Minimino
			case 9665:
				return 9620; // Miniminotot
			case 10109:
				return 10107; // Murciego
			case 7544:
				return 6716; // Nomoon
			case 8174:
				return 8155; // Pekefux
			case 9664:
				return 9619; // Pekewabbit hambriento
			case 7545:
				return 6978; // Peki
			case 7546:
				return 7414; // Pequeño Miaumiau Blanco
			case 7547:
				return 7415; // Pequeño Wauwau Negro
			case 7727:
				return 7709; // Piotín amarillo
			case 7726:
				return 7708; // Piotín azul
			case 7729:
				return 7711; // Piotín rojo
			case 7728:
				return 7710; // Piotín rosa
			case 7730:
				return 7712; // Piotín verde
			case 7731:
				return 7713; // Piotín violeta
			case 8679:
				return 8677; // Ross
			case 8020:
				return 7714; // Tortuga
			case 10108:
				return 10106; // Vampyrilla
			case 7538:
				return 1748; // Wabbit
			case 8524:
				return 8211; // Walk
			case 7536:
				return 1711; // Wauwau
			case 7894:
				return 7892; // Willy el Plasta
		}
		return 0;
	}
	
	public static boolean esRetoPosible2(int nro, int nuevo) {
		try {
			if (nro == nuevo)
				return false;
			switch (nro) {
				case 1:// zombi, muevete solo 1 PM en cada turno
				case 2:// estatua, Acaba tu turno en la misma casilla donde lo empezaste
				case 8:// nomada, utlizar todos los PM disponibles en cada turno
					if (nuevo == 1 || nuevo == 2 || nuevo == 8)
						return false;
					break;
				case 5:// ahorrador, durante el combate solo se podra usar 1 accion por unica vez
					if (nuevo == 5 || nuevo == 6 || nuevo == 9 || nuevo == 24)
						return false;
					break;
				case 9:// barbaro, no utilizar ningun hechizo mientras dure el combate
					if (nuevo == 5 || nuevo == 9 || nuevo == 11 || nuevo == 19 || nuevo == 24)
						return false;
					break;
				case 6:// vesrsatil, durante su turno cada jugador solo podra usar 1 accion, no repetir
					if (nuevo == 5 || nuevo == 6)
						return false;
					break;
				case 11:// mistico, solamente usa hechizos
					if (nuevo == 9 || nuevo == 11)
						return false;
					break;
				case 19:// manos limpias,acabar con los mounstros sin ocasionarles daños directos
					if (nuevo == 9 || nuevo == 19)
						return false;
					break;
				case 24:// limitado, utlizar el mismo hechizo o CaC mientras dure el combate
					if (nuevo == 5 || nuevo == 9 || nuevo == 24)
						return false;
					break;
				case 28:// ni pias ni sumisas, las mujeres deben acabar con todos los mobs
					if (nuevo == 29)
						return false;
					break;
				case 29:// ni pios ni sumisos, los hombres deben acabar con todos los mobs
					if (nuevo == 28)
						return false;
					break;
				case 36:// audaz, Acaba tu turno en una de las casillas pegadas a las de uno de tus adversarios
				case 40:// pusilanime, No termines nunca tu turno en una casilla adyacente a la de uno de tus adversarios
					if (nuevo == 36 || nuevo == 40)
						return false;
					break;
				case 37:// pegajoso, Acaba tu turno en una de las casillas pegadas a las de uno de tus aliados
				case 39:// anacoreta, No termines nunca tu turno en una casilla adyacente a la de uno de tus aliados
					if (nuevo == 37 || nuevo == 39)
						return false;
					break;
				case 3:// elegido voluntario, Matar %1 el último
				case 4:// aplazamiento, Matar %1 el último
				case 10:// cruel
				case 25:// ordenado
				case 32:// elitista, Todos los ataques deben ir dirigidos a hasta que muera
				case 38:// Blitzkrieg, Cuando se ataca a un adversario, hay que matarlo antes de que comience su turno
				case 45:// duelo, Cuando un personaje ataca a un adversario, ningún otro personaje debe atacar a ese mismo
				case 31:// focalizacion, cuando se ataca a un adversario, hay q matarlo para atacar a otro
				case 34:// imprevisible, Todos los ataques deben ir dirigidos a un mismo objetivo que se designa en cada turno
				case 35:// asesino a sueldo, Debes matar a los adversarios en el orden indicado. Cada vez que mates a un
					if (nuevo == 3 || nuevo == 4 || nuevo == 10 || nuevo == 25 || nuevo == 31 || nuevo == 32 || nuevo == 34
							|| nuevo == 35 || nuevo == 38 || nuevo == 45)
						return false;
					break;
			}
			return true;
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public static boolean esRetoPosible1(int nuevo, Pelea pelea) {
		try {
			switch (nuevo) {
				case 7:// jardinero, durante el combate, plantar una zanahowia cada vez q se pueda
					for (Luchador luchador : pelea.luchadoresDeEquipo(1)) {
						if (luchador.getPersonaje().tieneHechizoID(367))
							return true;
					}
					return false;
				case 12:// sepultero, invoca un chaferloko cada vez q se pueda
					for (Luchador luchador : pelea.luchadoresDeEquipo(1)) {
						if (luchador.getPersonaje().tieneHechizoID(373))
							return true;
					}
					return false;
				case 14:// casino real, lanzar el hechizo ruleta cada vez q se pueda
					for (Luchador luchador : pelea.luchadoresDeEquipo(1)) {
						if (luchador.getPersonaje().tieneHechizoID(101))
							return true;
					}
					return false;
				case 15:// aracnofilo, invocar una araña cada vez q se pueda
					for (Luchador luchador : pelea.luchadoresDeEquipo(1)) {
						if (luchador.getPersonaje().tieneHechizoID(370))
							return true;
					}
					return false;
				case 29:
				case 28:// ni pias ni sumisas, los hombres deben acabar con todos los mobs
					int masc = 0;
					int fem = 0;
					for (Luchador luchador : pelea.luchadoresDeEquipo(1)) {
						if (luchador.getPersonaje().getSexo() == 1)
							fem++;
						else
							masc++;
					}
					if (fem > 0 && masc > 0)
						return true;
					return false;
				case 10:// Cruel
				case 25:// ordenado
				case 42:// el dos por uno, Cuando un personaje mata a un adversario, tiene que matar obligatoriamente a un (y sólo
					if (pelea.luchadoresDeEquipo(2).size() >= 2)
						return true;
					return false;
				case 44:// reparto, Cada personaje debe matar al menos a un adversario (que no sea una invocación) durante el
				case 46:// cada uno con su mounstro, Cada personaje debe matar al menos a un adversario durante el combate.
					if (pelea.luchadoresDeEquipo(2).size() >= pelea.luchadoresDeEquipo(1).size())
						return true;
					return false;
				case 37:
				case 30:// los pequeños antes, el personaje de menor nivel debe acabar con todos los mobs
				case 33:// superviviente, Ningún aliado debe morir
				case 47:// contaminacion, Cuando un aliado pierde puntos de vida, dispones de 3 turnos para rematar a tu aliado o
					if (pelea.luchadoresDeEquipo(1).size() >= 2)
						return true;
					return false;
			}
			return true;
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public static int getDoplonDopeul(int idMob) {
		switch (idMob) {
			case 168:
				return 10302;
			case 165:
				return 10303;
			case 166:
				return 10304;
			case 162:
				return 10305;
			case 160:
				return 10306;
			case 167:
				return 10307;
			case 161:
				return 10308;
			case 2691:
				return 10309;
			case 455:
				return 10310;
			case 169:
				return 10311;
			case 163:
				return 10312;
			case 164:
				return 10313;
		}
		return -1;
	}
	
	public static int getCertificadoDopeul(int idMob) {
		switch (idMob) {
			case 168:
				return 10289;
			case 165:
				return 10290;
			case 166:
				return 10291;
			case 162:
				return 10292;
			case 160:
				return 10293;
			case 167:
				return 10294;
			case 161:
				return 10295;
			case 2691:
				return 10296;
			case 455:
				return 10297;
			case 169:
				return 10298;
			case 163:
				return 10299;
			case 164:
				return 10300;
		}
		return -1;
	}
}