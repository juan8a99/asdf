
package estaticos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import com.mysql.jdbc.PreparedStatement;

import estaticos.MundoDofus.*;
import variables.*;
import variables.Gremio.MiembroGremio;
import variables.Mascota.MascotaModelo;

import variables.Hechizo.*;
import variables.Mapa.*;
import variables.PuestoMercadillo.ObjetoMercadillo;
import variables.NPCModelo.*;
import variables.Objeto.*;

public class GestorSQL {
	private static Connection bdTania;
	private static Connection bdLuis;
	private static Timer timerComienzo;
	private static boolean necesitaComenzar;
	
	private static void closeResultSet(ResultSet RS) {
		try {
			RS.getStatement().close();
			RS.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void closePreparedStatement(PreparedStatement p) {
		try {
			p.clearParameters();
			p.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static ResultSet executeQuery(String consultaSQL, String DBNAME) throws SQLException {
		if (!Bustemu.estaIniciado)
			return null;
		Connection DB;
		if (DBNAME.equals(Bustemu.BDTANIA))
			DB = bdTania;
		else
			DB = bdLuis;
		PreparedStatement stat = (PreparedStatement) DB.prepareStatement(consultaSQL);
		ResultSet RS = stat.executeQuery();
		stat.setQueryTimeout(300);
		return RS;
	}
	
	public synchronized static PreparedStatement nuevaTransaccion(String consultaSQL, Connection coneccionSQL)
			throws SQLException {
		PreparedStatement aRetornar = (PreparedStatement) coneccionSQL.prepareStatement(consultaSQL);
		necesitaComenzar = true;
		return aRetornar;
	}
	
	public synchronized static void comenzarTransacciones() {
		try {
			if (bdTania.isClosed() || bdLuis.isClosed()) {
				cerrarCons();
				IniciarConexion();
			}
			bdLuis.commit();
			bdTania.commit();
		} catch (SQLException e) {
			System.out.println("SQL ERROR:" + e.getMessage());
			e.printStackTrace();
			comenzarTransacciones();
		}
	}
	
	public synchronized static void cerrarCons() {
		try {
			comenzarTransacciones();
			bdTania.close();
			bdLuis.close();
		} catch (Exception e) {
			System.out.println("Error en la ventana de conexiones SQL:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static final boolean IniciarConexion() {
		try {
			bdTania = DriverManager.getConnection("jdbc:mysql://" + Bustemu.BD_HOST + "/" + Bustemu.BDTANIA, Bustemu.BD_USUARIO,
					Bustemu.BD_PASS);
			bdTania.setAutoCommit(false);
			bdLuis = DriverManager.getConnection("jdbc:mysql://" + Bustemu.BD_HOST + "/" + Bustemu.BDLUIS, Bustemu.BD_USUARIO,
					Bustemu.BD_PASS);
			bdLuis.setAutoCommit(false);
			if (!bdLuis.isValid(1000) || !bdTania.isValid(1000)) {
				System.out.println("SQLError : Conexion a la BDD invalida!");
				return false;
			}
			necesitaComenzar = false;
			TIMER(true);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	public static int getSigIDPersonaje() {
		try {
			ResultSet RS = executeQuery("SELECT id FROM personajes ORDER BY id DESC LIMIT 1;", Bustemu.BDTANIA);
			if (!RS.first())
				return 1;
			int id = RS.getInt("id");
			id++;
			closeResultSet(RS);
			return id;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
			Bustemu.cerrarServer();
		}
		return 0;
	}
	
	public static int getPuntosCuenta(int cuentaID) {
		int puntos = 0;
		try {
			ResultSet RS = executeQuery("SELECT `puntos` FROM `cuentas` WHERE `id` = " + cuentaID + ";", Bustemu.BDTANIA);
			boolean encontrado = RS.first();
			if (encontrado)
				puntos = RS.getInt("puntos");
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
		return puntos;
	}
	
	public static void setPuntoCuenta(int puntos, int cuentaID) {
		PreparedStatement p;
		String consultaSQL = "UPDATE `cuentas` SET `puntos`=? WHERE `id`= ?";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.setLong(1, puntos);
			p.setInt(2, cuentaID);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void cambiarContraseña(String password, int cuentaID) {
		PreparedStatement p;
		String consultaSQL = "UPDATE `cuentas` SET `pass`=? WHERE `id`= ?";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.setString(1, password);
			p.setInt(2, cuentaID);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void SALVAR_CUENTA(Cuenta cuenta) {
		try {
			String consultaSQL = "UPDATE cuentas SET `kamas` = ?,`objetos` = ?,`gm` = ?,`establo` = ?,`baneado` = ?,"
					+ "`amigos` = ?,`enemigos` = ?,`ultimaIP` = ?,`ultimaConeccion` = ? WHERE `id` = ?;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setLong(1, cuenta.getKamasBanco());
			p.setString(2, cuenta.stringBancoObjetosBD());
			p.setInt(3, cuenta.getRango());
			p.setString(4, cuenta.stringIDsEstablo());
			p.setInt(5, (cuenta.estaBaneado() ? 1 : 0));
			p.setString(6, cuenta.analizarListaAmigosABD());
			p.setString(7, cuenta.stringListaEnemigosABD());
			p.setString(8, cuenta.getActualIP());
			p.setString(9, cuenta.getUltimaConeccion());
			p.setInt(10, cuenta.getID());
			p.executeUpdate();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void ELIMINAR_CUENTA(int id) {
		String consultaSQL = "DELETE FROM cuentas WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, id);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void ACTUALIZAR_PRIMERA_VEZ(Cuenta cuenta) {
		try {
			String consultaSQL = "UPDATE cuentas SET primeravez = 0 WHERE `id` = ?;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, cuenta.getID());
			p.executeUpdate();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_RECETAS() {
		try {
			ResultSet RS = executeQuery("SELECT * from recetas;", Bustemu.BDLUIS);
			while (RS.next()) {
				ArrayList<Duo<Integer, Integer>> arrayDuos = new ArrayList<Duo<Integer, Integer>>();
				boolean continua = false;
				for (String str : RS.getString("receta").split(";")) {
					try {
						String[] s = str.split("\\*");
						int idModeloObj = Integer.parseInt(s[0]);
						int cantidad = Integer.parseInt(s[1]);
						arrayDuos.add(new Duo<Integer, Integer>(idModeloObj, cantidad));
						continua = true;
					} catch (Exception e) {
						e.printStackTrace();
						continua = false;
					}
				}
				if (continua) {
					MundoDofus.addReceta(RS.getInt("id"), arrayDuos);
				}
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_GREMIOS() {
		try {
			ResultSet RS = executeQuery("SELECT * from gremios;", Bustemu.BDTANIA);
			while (RS.next()) {
				MundoDofus.addGremio(
						new Gremio(RS.getInt("id"), RS.getString("nombre"), RS.getString("emblema"), RS.getInt("nivel"), RS
								.getLong("xp"), RS.getInt("capital"), RS.getInt("recaudadores"), RS.getString("hechizos"), RS
								.getString("stats")), false);
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static int CARGAR_MIEMBROS_GREMIO() {
		int numero = 0;
		try {
			ResultSet RS = executeQuery("SELECT * from miembros_gremio;", Bustemu.BDTANIA);
			while (RS.next()) {
				Gremio G = MundoDofus.getGremio(RS.getInt("gremio"));
				if (G == null)
					continue;
				G.addMiembro(RS.getInt("id"), RS.getString("nombre"), RS.getInt("nivel"), RS.getInt("gfxid"), RS.getInt("rango"),
						RS.getByte("porcXp"), RS.getLong("xpDonada"), RS.getInt("derechos"), RS.getDate("ultimaConeccion")
								.toString().replaceAll("-", "~"));
				numero++;
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
			numero = 0;
		}
		return numero;
	}
	
	public static void CARGAR_MONTURAS() {
		try {
			ResultSet RS = executeQuery("SELECT * from dragopavos;", Bustemu.BDTANIA);
			while (RS.next()) {
				MundoDofus.addDragopavo(new Dragopavo(RS.getInt("id"), RS.getInt("color"), RS.getInt("sexo"), RS.getInt("amor"),
						RS.getInt("resistencia"), RS.getInt("nivel"), RS.getLong("xp"), RS.getString("nombre"), RS
								.getInt("fatiga"), RS.getInt("energia"), RS.getInt("reproducciones"), RS.getInt("madurez"), RS
								.getInt("serenidad"), RS.getString("objetos"), RS.getString("ancestros"), RS
								.getString("habilidad"), RS.getInt("talla"), RS.getInt("celda"), RS.getShort("mapa"), RS
								.getInt("dueño"), RS.getInt("orientacion"), RS.getInt("fecundable"), RS.getInt("pareja"), RS
								.getString("vip")));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_DROPS() {
		try {
			ResultSet RS = executeQuery("SELECT * from drops;", Bustemu.BDLUIS);
			while (RS.next()) {
				MobModelo MM = MundoDofus.getMobModelo(RS.getInt("mob"));
				MM.addDrop(new Drop(RS.getInt("objeto"), RS.getInt("prospeccion"), RS.getInt("porcentaje"), RS.getInt("max")));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_ITEMSETS() {
		try {
			ResultSet RS = executeQuery("SELECT * from objetos_set;", Bustemu.BDLUIS);
			while (RS.next()) {
				MundoDofus.addItemSet(new ItemSet(RS.getInt("id"), RS.getString("objetos"), RS.getString("bonus")));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_INTERACTIVOS() {
		try {
			ResultSet RS = executeQuery("SELECT * from objetos_interactivos;", Bustemu.BDLUIS);
			while (RS.next()) {
				MundoDofus.addObjInteractivo(new ObjInteractivoModelo(RS.getInt("id"), RS.getInt("recarga"), RS
						.getInt("duracion"), RS.getInt("accionPJ"), RS.getInt("caminable") == 1));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static int CARGAR_CERCADOS() {
		int num = 0;
		try {
			ResultSet RS = executeQuery("SELECT * from cercados;", Bustemu.BDTANIA);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapa"));
				if (mapa == null)
					continue;
				Cercado cercado = new Cercado(RS.getInt("propietario"), mapa, RS.getInt("celda"), RS.getInt("tamaño"),
						RS.getInt("gremio"), RS.getInt("precio"), RS.getInt("celdamontura"), RS.getString("criando"),
						RS.getInt("celdapuerta"), RS.getString("celdasobjeto"), RS.getInt("objetos"),
						RS.getString("objetoscolocados"));
				MundoDofus.addCercado(cercado);
				num++;
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
			num = 0;
		}
		return num;
	}
	
	public static void CARGAR_OFICIOS() {
		try {
			ResultSet RS = executeQuery("SELECT * from oficios;", Bustemu.BDLUIS);
			while (RS.next()) {
				MundoDofus.addOficio(new Oficio(RS.getInt("id"), RS.getString("herramientas"), RS.getString("recetas")));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_AREA() {
		try {
			ResultSet RS = executeQuery("SELECT * from areas;", Bustemu.BDTANIA);
			while (RS.next()) {
				Area A = new Area(RS.getInt("id"), RS.getInt("superarea"), RS.getString("nombre"), RS.getInt("alineacion"),
						RS.getInt("prisma"));
				MundoDofus.addArea(A);
				A.getSuperArea().addArea(A);
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_SUBAREA() {
		try {
			ResultSet RS = executeQuery("SELECT * from subareas;", Bustemu.BDTANIA);
			while (RS.next()) {
				SubArea SA = new SubArea(RS.getInt("id"), RS.getInt("area"), RS.getInt("alineacion"), RS.getString("nombre"),
						RS.getInt("conquistable"), RS.getInt("prisma"));
				MundoDofus.addSubArea(SA);
				if (SA.getArea() != null)
					SA.getArea().addSubArea(SA);
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void ACTUALIZAR_SUBAREA(SubArea subarea) {
		try {
			String consultaSQL = "UPDATE `subareas` SET `alineacion` = ?, `prisma` = ? WHERE id = ?;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, subarea.getAlineacion());
			p.setInt(2, subarea.getPrismaID());
			p.setInt(3, subarea.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void ACTUALIZAR_AREA(Area area) {
		try {
			String consultaSQL = "UPDATE `areas` SET `alineacion` = ?, `prisma` = ? WHERE id = ?;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, area.getAlineacion());
			p.setInt(2, area.getPrismaID());
			p.setInt(3, area.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int CARGAR_NPCS() {
		int numero = 0;
		try {
			ResultSet RS = executeQuery("SELECT * from npcs_ubicacion;", Bustemu.BDLUIS);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapa"));
				if (mapa == null)
					continue;
				mapa.addNPC(RS.getInt("npc"), RS.getInt("celda"), RS.getInt("orientacion"));
				numero++;
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
			numero = 0;
		}
		return numero;
	}
	
	public static int CARGAR_RECAUDADORES() {
		int numero = 0;
		try {
			ResultSet RS = executeQuery("SELECT * from recaudadores;", Bustemu.BDTANIA);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapa"));
				if (mapa == null)
					continue;
				Recaudador recaudador = new Recaudador(RS.getInt("id"), RS.getShort("mapa"), RS.getInt("celda"),
						RS.getByte("orientacion"), RS.getInt("gremio"), RS.getString("nombre1"), RS.getString("nombre2"),
						RS.getString("objetos"), RS.getLong("kamas"), RS.getLong("xp"));
				MundoDofus.addRecaudador(recaudador);
				numero++;
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
			numero = 0;
		}
		return numero;
	}
	
	public static int CARGAR_CASAS() {
		int numero = 0;
		try {
			ResultSet RS = executeQuery("SELECT * from casas;", Bustemu.BDTANIA);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapaFuera"));
				if (mapa == null)
					continue;
				MundoDofus.agregarCasa(new Casa(RS.getInt("id"), RS.getShort("mapaFuera"), RS.getInt("celdaFuera"), RS
						.getInt("dueño"), RS.getInt("precio"), RS.getInt("gremio"), RS.getInt("acceso"), RS.getString("clave"),
						RS.getInt("derechosGremio"), RS.getInt("mapaDentro"), RS.getInt("celdaDentro")));
				numero++;
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
			numero = 0;
		}
		return numero;
	}
	
	public static void CARGAR_CUENTAS() {
		try {
			ResultSet RS = executeQuery("SELECT * from cuentas ;", Bustemu.BDTANIA);
			String consultaSQL = "UPDATE cuentas SET `recarga` = 0  WHERE id = ?;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			while (RS.next()) {
				Cuenta C = new Cuenta(RS.getInt("id"), RS.getString("cuenta").toLowerCase(), RS.getString("pass"),
						RS.getString("apodo"), RS.getString("pregunta"), RS.getString("respuesta"), RS.getInt("gm"),
						RS.getInt("vip"), (RS.getInt("baneado") == 1), RS.getString("ultimaIP"), RS.getString("ultimaConeccion"),
						RS.getString("objetos"), RS.getInt("kamas"), RS.getString("amigos"), RS.getString("enemigos"),
						RS.getString("establo"), RS.getInt("primeravez"), RS.getInt("regalo"));
				MundoDofus.addCuenta(C);
				p.setInt(1, RS.getInt("id"));
				p.executeUpdate();
			}
			closePreparedStatement(p);
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_PERSONAJES() {
		try {
			ResultSet RS = executeQuery("SELECT * FROM personajes ;", Bustemu.BDTANIA);
			while (RS.next()) {
				TreeMap<Integer, Integer> stats = new TreeMap<Integer, Integer>();
				stats.put(CentroInfo.STATS_ADD_VITALIDAD, RS.getInt("vitalidad"));
				stats.put(CentroInfo.STATS_ADD_FUERZA, RS.getInt("fuerza"));
				stats.put(CentroInfo.STATS_ADD_SABIDURIA, RS.getInt("sabiduria"));
				stats.put(CentroInfo.STATS_ADD_INTELIGENCIA, RS.getInt("inteligencia"));
				stats.put(CentroInfo.STATS_ADD_SUERTE, RS.getInt("suerte"));
				stats.put(CentroInfo.STATS_ADD_AGILIDAD, RS.getInt("agilidad"));
				Personaje perso = new Personaje(RS.getInt("id"), RS.getString("nombre"), RS.getInt("sexo"), RS.getInt("clase"),
						RS.getInt("color1"), RS.getInt("color2"), RS.getInt("color3"), RS.getLong("kamas"),
						RS.getInt("puntosHechizo"), RS.getInt("capital"), RS.getInt("energia"), RS.getInt("nivel"),
						RS.getLong("xp"), RS.getInt("talla"), RS.getInt("gfx"), RS.getByte("alineacion"), RS.getInt("cuenta"),
						stats, RS.getInt("mostrarAmigos"), RS.getByte("mostrarAlineacion"), RS.getString("canal"),
						RS.getShort("mapa"), RS.getInt("celda"), RS.getString("objetos"), RS.getInt("porcVida"),
						RS.getString("hechizos"), RS.getString("posSalvada"), RS.getString("oficios"), RS.getInt("xpMontura"),
						RS.getInt("montura"), RS.getInt("honor"), RS.getInt("deshonor"), RS.getInt("nivelAlin"),
						RS.getString("zaaps"), RS.getByte("titulo"), RS.getInt("esposo"), RS.getString("tienda"),
						RS.getInt("mercante"), RS.getInt("sFuerza"), RS.getInt("sInteligencia"), RS.getInt("sAgilidad"),
						RS.getInt("sSuerte"), RS.getInt("sVitalidad"), RS.getInt("sSabiduria"), RS.getInt("restriccionesA"),
						RS.getInt("restriccionesB"), RS.getInt("encarnacion"));
				MundoDofus.addPersonaje(perso);
				if (MundoDofus.getCuenta(RS.getInt("cuenta")) != null)
					MundoDofus.getCuenta(RS.getInt("cuenta")).addPerso(perso);
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
			Bustemu.cerrarServer();
		}
	}
	
	public static boolean BORRAR_PERSONAJE(Personaje perso) {
		int id = perso.getID();
		String consultaSQL = "DELETE FROM personajes WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, id);
			p.execute();
			if (!perso.getObjetosPersonajePorID(",").equals("")) {
				consultaSQL = "DELETE FROM objetos WHERE id IN (?);";
				p = nuevaTransaccion(consultaSQL, bdTania);
				p.setString(1, perso.getObjetosPersonajePorID(","));
				p.execute();
			}
			if (perso.getMontura() != null) {
				consultaSQL = "DELETE FROM dragopavos WHERE id = ?";
				p = nuevaTransaccion(consultaSQL, bdTania);
				p.setInt(1, perso.getMontura().getID());
				p.execute();
				MundoDofus.borrarDragopavoID(perso.getMontura().getID());
			}
			if (perso.getMiembroGremio() != null) {
				perso.getGremio().expulsarMiembro(perso);
				consultaSQL = "DELETE FROM miembros_gremio WHERE id = ?";
				p = nuevaTransaccion(consultaSQL, bdTania);
				p.setInt(1, id);
				p.execute();
			}
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			return false;
		}
	}
	
	public static boolean AGREGAR_PJ_EN_BD(Personaje perso, String objetos) {
		String consultaSQL = "INSERT INTO personajes(`id`,`nombre`,`sexo`,`clase`,`color1`,`color2`,`color3`,`kamas`,"
				+ "`puntosHechizo`,`capital`,`energia`,`nivel`,`xp`,`talla`,`gfx`,`cuenta`,`celda`,`mapa`,`hechizos`,`objetos`,"
				+ "`tienda`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'');";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, perso.getID());
			p.setString(2, perso.getNombre());
			p.setInt(3, perso.getSexo());
			p.setInt(4, perso.getClase(true));
			p.setInt(5, perso.getColor1());
			p.setInt(6, perso.getColor2());
			p.setInt(7, perso.getColor3());
			p.setLong(8, perso.getKamas());
			p.setInt(9, perso.getPuntosHechizos());
			p.setInt(10, perso.getCapital());
			p.setInt(11, perso.getEnergia());
			p.setInt(12, perso.getNivel());
			p.setLong(13, perso.getExperiencia());
			p.setInt(14, perso.getTalla());
			p.setInt(15, perso.getGfxID());
			p.setInt(16, perso.getCuentaID());
			p.setInt(17, perso.getCelda().getID());
			p.setInt(18, perso.getMapa().getID());
			p.setString(19, perso.analizarHechizosABD());
			p.setString(20, objetos);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			return false;
		}
	}
	
	public static void CARGAR_EXPERIENCIA() {
		try {
			ResultSet RS = executeQuery("SELECT * from experiencia;", Bustemu.BDLUIS);
			while (RS.next())
				MundoDofus.addExpLevel(RS.getInt("nivel"), new MundoDofus.ExpNivel(RS.getLong("personaje"), RS.getInt("oficio"),
						RS.getInt("dragopavo"), RS.getInt("pvp"), RS.getInt("encarnacion")));
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static int CARGAR_TRIGGERS() {
		try {
			int numero = 0;
			ResultSet RS = executeQuery("SELECT * FROM `celdas_teleport`", Bustemu.BDLUIS);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapa"));
				if (mapa == null || mapa.getCelda(RS.getInt("celda")) == null)
					continue;
				switch (RS.getInt("evento")) {
					case 1:
						mapa.getCelda(RS.getInt("celda")).addAccionEnUnaCelda(RS.getInt("accion"), RS.getString("args"),
								RS.getString("condiciones"));
						break;
					default:
						System.out.println("Accion Evento " + RS.getInt("evento") + " no implantado");
						break;
				}
				numero++;
			}
			closeResultSet(RS);
			return numero;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
		return 0;
	}
	
	public static void CARGAR_MAPAS() {
		try {
			ResultSet RS;
			RS = executeQuery("SELECT  * from mapas LIMIT " + CentroInfo.LIMITE_MAPAS + ";", Bustemu.BDLUIS);
			while (RS.next()) {
				MundoDofus.addMapa(new Mapa(RS.getShort("id"), RS.getString("fecha"), RS.getByte("ancho"), RS.getByte("alto"), RS
						.getString("key"), RS.getString("posPelea"), RS.getString("mapData"), RS.getString("cells"), RS
						.getString("mobs"), RS.getString("mapaPos"), RS.getByte("nroGrupo"), RS.getByte("maxMobs"), RS
						.getInt("capacidad"), RS.getInt("descripcion")));
			}
			GestorSQL.closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static int CARGAR_MAPAS_FIXEADOS() {
		int numero = 0;
		try {
			ResultSet RS;
			RS = executeQuery("SELECT  * from mobs_fix;", Bustemu.BDLUIS);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapa"));
				if (mapa == null || mapa.getCelda(RS.getInt("celda")) == null)
					continue;
				mapa.addGrupoFix(RS.getInt("celda"), RS.getString("mobs"));
				numero++;
			}
			GestorSQL.closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
		return numero;
	}
	
	public static void CAMBIAR_SEXO_CLASE(Personaje perso) {
		PreparedStatement p;
		String consultaSQL = "UPDATE `personajes` SET `sexo`=?, `clase`= ?, `hechizos`= ? WHERE `id`= ?";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, perso.getSexo());
			p.setInt(2, perso.getClase(true));
			p.setString(3, perso.analizarHechizosABD());
			p.setInt(4, perso.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void ACTUALIZAR_NOMBRE(Personaje perso) {
		String consultaSQL = "UPDATE `personajes` SET `nombre` = ? WHERE `id` = ? ;";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.setString(1, perso.getNombre());
			p.setInt(2, perso.getID());
			p.executeUpdate();
		} catch (Exception e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void SALVAR_PERSONAJE(Personaje perso, boolean salvarObjetos) {
		String consultaSQL = "UPDATE `personajes` SET `mostrarAmigos`= ?,`canal`= ?,`porcVida`= ?,`mapa`= ?,`celda`= ?,"
				+ "`vitalidad`= ?,`fuerza`= ?,`sabiduria`= ?,`inteligencia`= ?,`suerte`= ?,`agilidad`= ?,`alineacion`= ?,"
				+ "`honor`= ?,`deshonor`= ?,`nivelAlin`= ?,`gfx`= ?,`xp`= ?,`nivel`= ?,`energia`= ?,`capital`= ?,"
				+ "`puntosHechizo`= ?,`kamas`= ?,`talla` = ?,`hechizos` = ?,`objetos` = ?,`posSalvada` = ?,"
				+ "`xpMontura` = ?,`zaaps` = ?,`montura` = ?,`mostrarAlineacion` = ?,`titulo` = ?,`esposo` = ?,`tienda` = ?,"
				+ "`mercante` = ?,`sFuerza`=?,`sInteligencia`=?,`sAgilidad`=?,`sSuerte`=?,`sVitalidad`=?,`sSabiduria`=?, "
				+ "`restriccionesA`= ?, `restriccionesB`= ?, `oficios`= ?, `encarnacion`=? WHERE `personajes`.`id` = ? LIMIT 1 ;";
		PreparedStatement p = null;
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, (perso.mostrarConeccionAmigo() ? 1 : 0));
			p.setString(2, perso.getCanal());
			p.setInt(3, perso.getPorcPDV());
			p.setInt(4, perso.getMapa().getID());
			p.setInt(5, perso.getCelda().getID());
			p.setInt(6, perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_VITALIDAD));
			p.setInt(7, perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_FUERZA));
			p.setInt(8, perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_SABIDURIA));
			p.setInt(9, perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_INTELIGENCIA));
			p.setInt(10, perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_SUERTE));
			p.setInt(11, perso.getBaseStats().getEfecto(CentroInfo.STATS_ADD_AGILIDAD));
			p.setInt(12, perso.getAlineacion());
			p.setInt(13, perso.getHonor());
			p.setInt(14, perso.getDeshonor());
			p.setInt(15, perso.getNivelAlineacion());
			p.setInt(16, perso.getGfxID());
			p.setLong(17, perso.getExperiencia());
			p.setInt(18, perso.getNivel());
			p.setInt(19, perso.getEnergia());
			p.setInt(20, perso.getCapital());
			p.setInt(21, perso.getPuntosHechizos());
			p.setLong(22, perso.getKamas());
			p.setInt(23, perso.getTalla());
			p.setString(24, perso.analizarHechizosABD());
			p.setString(25, perso.stringObjetosABD());
			p.setString(26, perso.getPtoSalvada());
			p.setInt(27, perso.getXpDonadaMontura());
			p.setString(28, perso.stringZaaps());
			p.setInt(29, (perso.getMontura() != null ? perso.getMontura().getID() : -1));
			p.setInt(30, (perso.mostrarAlas() ? 1 : 0));
			p.setInt(31, (perso.getTitulo()));
			p.setInt(32, perso.getEsposo());
			p.setString(33, perso.getStringTienda());
			p.setInt(34, perso.getMercante());
			p.setInt(35, perso.getScrollFuerza());
			p.setInt(36, perso.getScrollInteligencia());
			p.setInt(37, perso.getScrollAgilidad());
			p.setInt(38, perso.getScrollSuerte());
			p.setInt(39, perso.getScrollVitalidad());
			p.setInt(40, perso.getScrollSabiduria());
			p.setLong(41, perso.getRestriccionesA());
			p.setLong(42, perso.getRestriccionesB());
			p.setString(43, perso.stringOficios());
			p.setInt(44, perso.getIDEncarnacion());
			p.setInt(45, perso.getID());
			p.executeUpdate();
			if (perso.getMiembroGremio() != null)
				ACTUALIZAR_MIEMBRO_GREMIO(perso.getMiembroGremio());
		} catch (Exception e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			System.out.println("Personaje salvado");
			System.exit(1);
		}
		if (salvarObjetos) {
			consultaSQL = "REPLACE INTO `objetos` VALUES(?,?,?,?,?,?);";
			try {
				p = nuevaTransaccion(consultaSQL, bdTania);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			for (Objeto obj : perso.getObjetos().values()) {
				try {
					if (obj == null)
						continue;
					p.setInt(1, obj.getID());
					p.setInt(2, obj.getIDModelo());
					p.setInt(3, obj.getCantidad());
					p.setInt(4, obj.getPosicion());
					p.setString(5, obj.convertirStatsAString());
					p.setInt(6, obj.getObjeviID());
					p.execute();
				} catch (Exception e) {
					continue;
				}
			}
			if (perso.getCuenta() == null)
				return;
			for (String idStr : perso.getObjetosBancoPorID(":").split(":")) {
				try {
					int id = Integer.parseInt(idStr);
					Objeto obj = MundoDofus.getObjeto(id);
					if (obj == null)
						continue;
					p.setInt(1, obj.getID());
					p.setInt(2, obj.getIDModelo());
					p.setInt(3, obj.getCantidad());
					p.setInt(4, obj.getPosicion());
					p.setString(5, obj.convertirStatsAString());
					p.setInt(6, obj.getObjeviID());
					p.execute();
				} catch (Exception e) {
					continue;
				}
			}
		}
		closePreparedStatement(p);
	}
	
	public static void CARGAR_HECHIZOS() {
		try {
			ResultSet RS = executeQuery("SELECT  * from hechizos;", Bustemu.BDLUIS);
			while (RS.next()) {
				int id = RS.getInt("id");
				Hechizo hechizo = new Hechizo(id, RS.getString("nombre"), RS.getInt("sprite"), RS.getString("spriteInfos"),
						RS.getString("afectados"));
				MundoDofus.addHechizo(hechizo);
				StatsHechizos l1 = null;
				if (!RS.getString("nivel1").equalsIgnoreCase("-1"))
					l1 = analizarHechizoStats(id, 1, RS.getString("nivel1"));
				StatsHechizos l2 = null;
				if (!RS.getString("nivel2").equalsIgnoreCase("-1"))
					l2 = analizarHechizoStats(id, 2, RS.getString("nivel2"));
				StatsHechizos l3 = null;
				if (!RS.getString("nivel3").equalsIgnoreCase("-1"))
					l3 = analizarHechizoStats(id, 3, RS.getString("nivel3"));
				StatsHechizos l4 = null;
				if (!RS.getString("nivel4").equalsIgnoreCase("-1"))
					l4 = analizarHechizoStats(id, 4, RS.getString("nivel4"));
				StatsHechizos l5 = null;
				if (!RS.getString("nivel5").equalsIgnoreCase("-1"))
					l5 = analizarHechizoStats(id, 5, RS.getString("nivel5"));
				StatsHechizos l6 = null;
				if (!RS.getString("nivel6").equalsIgnoreCase("-1"))
					l6 = analizarHechizoStats(id, 6, RS.getString("nivel6"));
				hechizo.addStatsHechizos(1, l1);
				hechizo.addStatsHechizos(2, l2);
				hechizo.addStatsHechizos(3, l3);
				hechizo.addStatsHechizos(4, l4);
				hechizo.addStatsHechizos(5, l5);
				hechizo.addStatsHechizos(6, l6);
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static void CARGAR_MODELOS_OBJETOS() {
		try {
			ResultSet RS = executeQuery("SELECT  * from objetos_modelo;", Bustemu.BDLUIS);
			while (RS.next()) {
				MundoDofus.addObjModelo(new ObjetoModelo(RS.getInt("id"), RS.getString("statsModelo"), RS.getString("nombre"), RS
						.getInt("tipo"), RS.getInt("nivel"), RS.getInt("pod"), RS.getInt("precio"), RS.getInt("set"), RS
						.getString("condicion"), RS.getString("infosArma"), RS.getInt("vendidos"), RS.getInt("precioMedio"), RS
						.getInt("puntosVIP")));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
	}
	
	private static StatsHechizos analizarHechizoStats(int id, int nivel, String str) {
		try {
			StatsHechizos stats = null;
			String[] stat = str.split(",");
			String efectos = stat[0];
			String efectosCriticos = stat[1];
			int costePA = 6;
			try {
				costePA = Integer.parseInt(stat[2].trim());
			} catch (NumberFormatException e) {}
			int alcMin = Integer.parseInt(stat[3].trim());
			int alcMax = Integer.parseInt(stat[4].trim());
			int afectados = Integer.parseInt(stat[5].trim());
			int afectadosCriticos = Integer.parseInt(stat[6].trim());
			boolean linea = stat[7].trim().equalsIgnoreCase("true");
			boolean linedaVuelo = stat[8].trim().equalsIgnoreCase("true");
			boolean celdaVacia = stat[9].trim().equalsIgnoreCase("true");
			boolean alcMod = stat[10].trim().equalsIgnoreCase("true");
			// 11 tipo de hechizo
			int maxPorTurno = Integer.parseInt(stat[12].trim());
			int maxPorObjetivo = Integer.parseInt(stat[13].trim());
			int CoolDown = Integer.parseInt(stat[14].trim());
			String tipoAfectados = stat[15].trim();
			String estadosNecesarios = stat[16].trim();
			String estadosProhibidos = stat[17].trim();
			int nivelMin = Integer.parseInt(stat[18].trim());
			boolean finTurnoSiFC = stat[19].trim().equalsIgnoreCase("true");
			stats = new StatsHechizos(id, nivel, costePA, alcMin, alcMax, afectados, afectadosCriticos, linea, linedaVuelo,
					celdaVacia, alcMod, maxPorTurno, maxPorObjetivo, CoolDown, nivelMin, finTurnoSiFC, efectos, efectosCriticos,
					tipoAfectados, estadosProhibidos, estadosNecesarios, MundoDofus.getHechizo(id));
			return stats;
		} catch (Exception e) {
			e.printStackTrace();
			int numero = 0;
			System.out.println("[DEBUG]Hechizo " + id + " nivel " + nivel);
			for (String z : str.split(",")) {
				System.out.println("[DEBUG]" + numero + " " + z);
				numero++;
			}
			System.exit(1);
			return null;
		}
	}
	
	public static void CARGAR_MODELOS_MOB() {
		try {
			ResultSet RS = executeQuery("SELECT * FROM mobs;", Bustemu.BDLUIS);
			while (RS.next()) {
				int id = RS.getInt("id");
				String nombre = RS.getString("nombre");
				int gfxID = RS.getInt("gfxID");
				int alineacion = RS.getInt("alineacion");
				String colores = RS.getString("colores");
				String grados = RS.getString("grados");
				String hechizos = RS.getString("hechizos");
				String stats = RS.getString("stats");
				String pdvs = RS.getString("pdvs");
				String pts = RS.getString("puntos");
				String iniciativa = RS.getString("iniciativa");
				int mK = RS.getInt("minKamas");
				int MK = RS.getInt("maxKamas");
				int tipoIA = RS.getInt("tipoIA");
				String xp = RS.getString("exps");
				int talla = RS.getInt("talla");
				boolean capturable;
				if (RS.getInt("capturable") == 1) {
					capturable = true;
				} else {
					capturable = false;
				}
				MundoDofus.addMobModelo(id, new MobModelo(id, nombre, gfxID, alineacion, colores, grados, hechizos, stats, pdvs,
						pts, iniciativa, mK, MK, xp, tipoIA, capturable, talla));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static void CARGAR_MODELOS_NPC() {
		try {
			ResultSet RS = executeQuery("SELECT * FROM npcs_modelo;", Bustemu.BDLUIS);
			while (RS.next()) {
				int id = RS.getInt("id");
				int bonusValue = RS.getInt("bonusValue");
				int gfxID = RS.getInt("gfxID");
				int escalaX = RS.getInt("scaleX");
				int escalaY = RS.getInt("scaleY");
				int sexo = RS.getInt("sexo");
				int color1 = RS.getInt("color1");
				int color2 = RS.getInt("color2");
				int color3 = RS.getInt("color3");
				String accesorios = RS.getString("accesorios");
				int extraClip = RS.getInt("extraClip");
				int customArtWork = RS.getInt("customArtWork");
				int preguntaID = RS.getInt("pregunta");
				String ventas = RS.getString("ventas");
				String nombre = RS.getString("nombre");
				long kamas = RS.getLong("kamas");
				MundoDofus.addNpcModelo(new NPCModelo(id, bonusValue, gfxID, escalaX, escalaY, sexo, color1, color2, color3,
						accesorios, extraClip, customArtWork, preguntaID, ventas, nombre, kamas));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static void ACTUALIZAR_NPC_COLOR_SEXO(NPCModelo npc) {
		String consultaSQL = "UPDATE npcs_modelo SET `gfxID` = ?, `sexo` = ?, `color1` = ?, `color2` = ?, `color3` = ?, `accesorios` = ? WHERE `id` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, npc.getGfxID());
			p.setInt(2, npc.getSexo());
			p.setInt(3, npc.getColor1());
			p.setInt(4, npc.getColor2());
			p.setInt(5, npc.getColor3());
			p.setString(6, npc.getAccesorios());
			p.setInt(7, npc.getID());
			p.executeUpdate();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			e.printStackTrace();
		}
	}
	
	public static void ACTUALIZAR_NPC_KAMAS(NPCModelo npc) {
		String consultaSQL = "UPDATE npcs_modelo SET `kamas` = ? WHERE `id` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setLong(1, npc.getKamas());
			p.setInt(2, npc.getID());
			p.executeUpdate();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			e.printStackTrace();
		}
	}
	
	public static void ACTUALIZAR_NPC_VENTAS(NPCModelo npc) {
		String consultaSQL = "UPDATE npcs_modelo SET `ventas` = ? WHERE `id` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setString(1, npc.actualizarStringBD());
			p.setInt(2, npc.getID());
			p.executeUpdate();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			e.printStackTrace();
		}
	}
	
	public static void ACTUALIZAR_STATS_OBJETO(Objeto objeto, String stats) {
		String consultaSQL = "REPLACE INTO `objetos` VALUES (?,?,?,?,?,?);";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, objeto.getID());
			p.setInt(2, objeto.getIDModelo());
			p.setInt(3, objeto.getCantidad());
			p.setInt(4, objeto.getPosicion());
			p.setString(5, stats);
			p.setInt(6, objeto.getObjeviID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void AGREGAR_NUEVO_OBJETO(Objeto objeto) {
		try {
			String consultaSQL = "REPLACE INTO `objetos` VALUES(?,?,?,?,?,?);";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, objeto.getID());
			p.setInt(2, objeto.getIDModelo());
			p.setInt(3, objeto.getCantidad());
			p.setInt(4, objeto.getPosicion());
			p.setString(5, objeto.convertirStatsAString());
			p.setInt(6, objeto.getObjeviID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean SALVAR_NUEVO_GRUPOMOB(int mapaID, int celdaID, String grupoData) {
		try {
			String consultaSQL = "REPLACE INTO `mobs_fix` VALUES(?,?,?)";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, mapaID);
			p.setInt(2, celdaID);
			p.setString(3, grupoData);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void CARGAR_PREGUNTAS() {
		try {
			ResultSet RS = executeQuery("SELECT * FROM npc_preguntas;", Bustemu.BDLUIS);
			while (RS.next()) {
				MundoDofus.addNPCPregunta(new PreguntaNPC(RS.getInt("id"), RS.getString("respuestas"), RS.getString("params"), RS
						.getString("condicion"), RS.getInt("ifFalse")));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static void CARGAR_RESPUESTAS() {
		try {
			ResultSet RS = executeQuery("SELECT * FROM npc_respuestas;", Bustemu.BDLUIS);
			while (RS.next()) {
				int id = RS.getInt("id");
				int tipo = RS.getInt("accion");
				String args = RS.getString("args");
				if (MundoDofus.getNPCreponse(id) == null)
					MundoDofus.addNPCreponse(new RespuestaNPC(id));
				MundoDofus.getNPCreponse(id).addAccion(new Accion(tipo, args, ""));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static int CARGAR_FINALES_DE_COMBATE() {
		int numero = 0;
		try {
			ResultSet RS = executeQuery("SELECT * FROM accion_pelea;", Bustemu.BDLUIS);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("mapa"));
				if (mapa == null)
					continue;
				mapa.addAccionFinPelea(RS.getInt("tipoPelea"),
						new Accion(RS.getInt("accion"), RS.getString("args"), RS.getString("condicion")));
				numero++;
			}
			closeResultSet(RS);
			return numero;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
		return numero;
	}
	
	public static int CARGAR_ACCIONES_USO_OBJETOS() {
		int numero = 0;
		try {
			ResultSet RS = executeQuery("SELECT * FROM objetos_accion;", Bustemu.BDLUIS);
			while (RS.next()) {
				int id = RS.getInt("objetoModelo");
				if (MundoDofus.getObjModelo(id) == null)
					continue;
				int tipo = RS.getInt("accion");
				String args = RS.getString("args");
				MundoDofus.getObjModelo(id).addAccion(new Accion(tipo, args, ""));
				numero++;
			}
			closeResultSet(RS);
			return numero;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
		return numero;
	}
	
	public static void CARGAR_TUTORIALES() {
		try {
			ResultSet RS = executeQuery("SELECT * FROM tutoriales;", Bustemu.BDLUIS);
			while (RS.next()) {
				int id = RS.getInt("id");
				String inicio = RS.getString("inicio");
				String recompensa = RS.getString("recompensa1") + "," + RS.getString("recompensa2") + ","
						+ RS.getString("recompensa3") + "," + RS.getString("recompensa4");
				String fin = RS.getString("final");
				MundoDofus.addTutorial(new Tutorial(id, recompensa, inicio, fin));
			}
			closeResultSet(RS);
			return;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
		return;
	}
	
	public static void CARGAR_OBJETOS(String ids) {
		String req = "SELECT * FROM objetos WHERE id IN (" + ids + ");";
		try {
			ResultSet RS = executeQuery(req, Bustemu.BDTANIA);
			while (RS.next()) {
				int id = RS.getInt("id");
				int modeloID = RS.getInt("modelo");
				int cantidad = RS.getInt("cantidad");
				int posicion = RS.getInt("posicion");
				String stats = RS.getString("stats");
				int objevivo = RS.getInt("objevivo");
				MundoDofus.addObjeto(MundoDofus.objetoIniciarServer(id, modeloID, cantidad, posicion, stats, objevivo), false);
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static void BORRAR_OBJETO(int id) {
		String consultaSQL = "DELETE FROM objetos WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, id);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void SALVAR_OBJETO(Objeto objeto) {
		String consultaSQL = "REPLACE INTO `objetos` VALUES (?,?,?,?,?,?);";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, objeto.getID());
			p.setInt(2, objeto.getIDModelo());
			p.setInt(3, objeto.getCantidad());
			p.setInt(4, objeto.getPosicion());
			p.setString(5, objeto.convertirStatsAString());
			p.setInt(6, objeto.getObjeviID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void CREAR_MONTURA(Dragopavo DP) {
		String consultaSQL = "REPLACE INTO `dragopavos`(`id`,`color`,`sexo`,`nombre`,`xp`,`nivel`,"
				+ "`resistencia`,`amor`,`madurez`,`serenidad`,`reproducciones`,`fatiga`,`objetos`,"
				+ "`ancestros`,`energia`,`talla`,`celda`,`mapa`,`dueño`,`orientacion`,`fecundable`,`pareja`,`vip`) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, DP.getID());
			p.setInt(2, DP.getColor());
			p.setInt(3, DP.getSexo());
			p.setString(4, DP.getNombre());
			p.setLong(5, DP.getExp());
			p.setInt(6, DP.getNivel());
			p.setInt(7, DP.getResistencia());
			p.setInt(8, DP.getAmor());
			p.setInt(9, DP.getMadurez());
			p.setInt(10, DP.getSerenidad());
			p.setInt(11, DP.getReprod());
			p.setInt(12, DP.getFatiga());
			p.setString(13, DP.stringObjetosBD());
			p.setString(14, DP.getAncestros());
			p.setInt(15, DP.getEnergia());
			p.setInt(16, DP.getTalla());
			p.setInt(17, DP.getCelda());
			p.setInt(18, DP.getMapa());
			p.setInt(19, DP.getDueño());
			p.setInt(20, DP.getOrientacion());
			p.setInt(21, DP.getFecundadaHace());
			p.setInt(22, DP.getPareja());
			p.setString(23, DP.getVIP());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void CARGAR_CUENTA_POR_ID(int id) {
		try {
			ResultSet RS = executeQuery("SELECT * from cuentas WHERE `id` = '" + id + "';", Bustemu.BDTANIA);
			String consultaSQL = "UPDATE cuentas SET `recarga` = 0 WHERE id = ?;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			while (RS.next()) {
				if (MundoDofus.getCuenta(RS.getInt("id")) != null)
					if (MundoDofus.getCuenta(RS.getInt("id")).enLinea())
						continue;
				Cuenta cuenta = new Cuenta(RS.getInt("id"), RS.getString("cuenta").toLowerCase(), RS.getString("pass"),
						RS.getString("apodo"), RS.getString("pregunta"), RS.getString("respuesta"), RS.getInt("gm"),
						RS.getInt("vip"), (RS.getInt("baneado") == 1), RS.getString("ultimaIP"), RS.getString("ultimaConeccion"),
						RS.getString("objetos"), RS.getInt("kamas"), RS.getString("amigos"), RS.getString("enemigos"),
						RS.getString("establo"), RS.getInt("primeravez"), RS.getInt("regalo"));
				MundoDofus.addCuenta(cuenta);
				p.setInt(1, RS.getInt("id"));
				p.executeUpdate();
			}
			closePreparedStatement(p);
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_CUENTA_POR_NOMBRE(String nombre) {
		try {
			ResultSet RS = executeQuery("SELECT * from cuentas WHERE `cuenta` LIKE '" + nombre + "';", Bustemu.BDTANIA);
			String consultaSQL = "UPDATE cuentas SET `recarga` = 0  WHERE id = ?;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			while (RS.next()) {
				if (MundoDofus.getCuenta(RS.getInt("id")) != null)
					if (MundoDofus.getCuenta(RS.getInt("id")).enLinea())
						continue;
				Cuenta cuenta = new Cuenta(RS.getInt("id"), RS.getString("cuenta").toLowerCase(), RS.getString("pass"),
						RS.getString("apodo"), RS.getString("pregunta"), RS.getString("respuesta"), RS.getInt("gm"),
						RS.getInt("vip"), (RS.getInt("baneado") == 1), RS.getString("ultimaIP"), RS.getString("ultimaConeccion"),
						RS.getString("objetos"), RS.getInt("kamas"), RS.getString("amigos"), RS.getString("enemigos"),
						RS.getString("establo"), RS.getInt("primeravez"), RS.getInt("regalo"));
				MundoDofus.addCuenta(cuenta);
				p.setInt(1, RS.getInt("id"));
				p.executeUpdate();
			}
			closePreparedStatement(p);
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void ACTUALIZAR_TITULO_POR_NOMBRE(String nombre) {
		try {
			String consultaSQL = "UPDATE personajes SET `titulo` = 0  WHERE `nombre` = ?;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setString(1, nombre);
			p.executeUpdate();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void ACTUALIZAR_REGALO(Cuenta cuenta) {
		String consultaSQL = "UPDATE cuentas SET `regalo` = 0 WHERE `id` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, cuenta.getID());
			p.executeUpdate();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			e.printStackTrace();
		}
	}
	
	public static void ACTUALIZAR_MONTURA(Dragopavo DP, boolean salvarObjetos) {
		String consultaSQL = "UPDATE dragopavos SET `nombre` = ?,`xp` = ?,`nivel` = ?,`resistencia` = ?,`amor` = ?,"
				+ "`madurez` = ?,`serenidad` = ?,`reproducciones` = ?,`fatiga` = ?,`energia` = ?,`ancestros` = ?,"
				+ "`objetos` = ?,`habilidad` = ?, `talla`=?,`celda`=?,`mapa`=?,`dueño`=?,`orientacion`= ?,"
				+ " `fecundable`=?, `pareja`=?, `vip`=? WHERE `id` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setString(1, DP.getNombre());
			p.setLong(2, DP.getExp());
			p.setInt(3, DP.getNivel());
			p.setInt(4, DP.getResistencia());
			p.setInt(5, DP.getAmor());
			p.setInt(6, DP.getMadurez());
			p.setInt(7, DP.getSerenidad());
			p.setInt(8, DP.getReprod());
			p.setInt(9, DP.getFatiga());
			p.setInt(10, DP.getEnergia());
			p.setString(11, DP.getAncestros());
			p.setString(12, DP.stringObjetosBD());
			p.setString(13, DP.getHabilidad());
			p.setInt(14, DP.getTalla());
			p.setInt(15, DP.getCelda());
			p.setInt(16, DP.getMapa());
			p.setInt(17, DP.getDueño());
			p.setInt(18, DP.getOrientacion());
			p.setInt(19, DP.getFecundadaHace());
			p.setInt(20, DP.getPareja());
			p.setString(21, DP.getVIP());
			p.setInt(22, DP.getID());
			p.execute();
			if (salvarObjetos) {
				consultaSQL = "REPLACE INTO `objetos` VALUES (?,?,?,?,?,?);";
				try {
					p = nuevaTransaccion(consultaSQL, bdTania);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				for (Objeto obj : DP.getObjetos()) {
					try {
						if (obj == null)
							continue;
						p.setInt(1, obj.getID());
						p.setInt(2, obj.getIDModelo());
						p.setInt(3, obj.getCantidad());
						p.setInt(4, obj.getPosicion());
						p.setString(5, obj.convertirStatsAString());
						p.setInt(6, obj.getObjeviID());
						p.execute();
					} catch (Exception e) {
						continue;
					}
				}
			}
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			e.printStackTrace();
		}
	}
	
	public static void SALVAR_CERCADO(Cercado cercado) {
		String consultaSQL = "UPDATE `cercados` SET `celda`=?, `propietario` =?, `gremio`=?, `precio`=? , `criando`=?, "
				+ "`objetoscolocados`=? WHERE `mapa`=?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, cercado.getCeldaID());
			p.setInt(2, cercado.getDueño());
			p.setInt(3, (cercado.getGremio() == null ? -1 : cercado.getGremio().getID()));
			p.setInt(4, cercado.getPrecio());
			p.setString(5, cercado.getCriando());
			p.setString(6, cercado.getStringObjetosCria());
			p.setInt(7, cercado.getMapa().getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void SALVAR_RANKINGPVP(RankingPVP rank) {
		String consultaSQL = "UPDATE `ranking_pvp` SET `victorias`=?, `derrotas` =?, `nivelAlineacion`=?, `nombre`=?  WHERE `id`=?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, rank.getVictorias());
			p.setInt(2, rank.getDerrotas());
			p.setInt(3, rank.getNivelAlin());
			p.setString(4, rank.getNombre());
			p.setInt(5, rank.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void ACTUALIZAR_MONTURAS_Y_OBJETOS(int monturas, int objetos, int mapa) {
		String consultaSQL = "UPDATE `cercados` SET `tamaño`=?, `objetos` =? WHERE `mapa`=?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, monturas);
			p.setInt(2, objetos);
			p.setInt(3, mapa);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static boolean BORRAR_RANKINGPVP(int id) {
		String consultaSQL = "DELETE FROM `ranking_pvp` WHERE `id` = ? ;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, id);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static void AGREGAR_RANKINGPVP(RankingPVP rank) {
		try {
			String consultaSQL = "INSERT INTO ranking_pvp(`id`,`nombre`,`victorias`,`derrotas`,`nivelAlineacion`)"
					+ " VALUES(?,?,0,0,?);";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, rank.getID());
			p.setString(2, rank.getNombre());
			p.setInt(3, rank.getNivelAlin());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void AGREGAR_ACCION_OBJETO(int idModelo, int tipo, String args, String nombre) {
		try {
			String consultaSQL = "INSERT INTO objetos_accion(`objetoModelo`,`tipo`,`args`,`nombre`)" + " VALUES(?,?,?,?);";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, idModelo);
			p.setInt(2, tipo);
			p.setString(3, args);
			p.setString(4, nombre);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void AGREGAR_DROP(int mob, int objeto, int prosp, int max, int porcentaje) {
		try {
			String consultaSQL = "INSERT INTO drops(`mob`,`objeto`,`prospeccion`,`max`, `porcentaje`)" + " VALUES(?,?,?,?,?);";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, mob);
			p.setInt(2, objeto);
			p.setInt(3, prosp);
			p.setInt(4, max);
			p.setInt(5, porcentaje);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void BORRAR_DROP(int objeto) {
		try {
			String consultaSQL = "DELETE FROM `drops` WHERE `objeto` =" + objeto + " ;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void ACTUALIZAR_SERVER1() {
		try {
			String consultaSQL = "DROP DATABASE " + Bustemu.BDTANIA + " ;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void ACTUALIZAR_SERVER2() {
		try {
			String consultaSQL = "DROP DATABASE " + Bustemu.BDLUIS + " ;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean SALVAR_TRIGGER(int mapa1, int celda1, int accion, int evento, String args, String cond) {
		String consultaSQL = "REPLACE INTO `celdas_teleport` VALUES (?,?,?,?,?,?);";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, mapa1);
			p.setInt(2, celda1);
			p.setInt(3, accion);
			p.setInt(4, evento);
			p.setString(5, args);
			p.setString(6, cond);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static boolean BORRAR_TRIGGER(int mapaID, int celdaID) {
		String consultaSQL = "DELETE FROM `celdas_teleport` WHERE `mapa` = ? AND `celda` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, mapaID);
			p.setInt(2, celdaID);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static boolean ACTUALIZAR_MAPA_POSPELEA_NROGRUPO(Mapa mapa) {
		String consultaSQL = "UPDATE `mapas` SET `posPelea` = ?, `nroGrupo` = ? WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setString(1, mapa.getLugaresString());
			p.setInt(2, mapa.getMaxGrupoDeMobs());
			p.setInt(3, mapa.getID());
			p.executeUpdate();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static boolean ACTUALIZAR_MAPA_DESCRIPCION(int id, int descrip) {
		String consultaSQL = "UPDATE `mapas` SET `descripcion` = ? WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, descrip);
			p.setInt(2, id);
			p.executeUpdate();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static boolean BORRAR_NPC_DEL_MAPA(int m, int c) {
		String consultaSQL = "DELETE FROM npcs_ubicacion WHERE mapa = ? AND celda = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, m);
			p.setInt(2, c);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static void BORRAR_RECAUDADOR(int id) {
		String consultaSQL = "DELETE FROM recaudadores WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, id);
			p.execute();
			closePreparedStatement(p);
			return;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return;
	}
	
	public static boolean AGREGAR_NPC_AL_MAPA(int mapa, int id, int celda, int direccion, String nombre) {
		String consultaSQL = "INSERT INTO `npcs_ubicacion`" + " VALUES (?,?,?,?,?);";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, mapa);
			p.setInt(2, id);
			p.setInt(3, celda);
			p.setInt(4, direccion);
			p.setString(5, nombre);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static boolean ADD_RECAUDADOR_EN_MAPA(int id, int mapa, int guildID, int celda, int o, String N1, String N2) {
		String consultaSQL = "INSERT INTO `recaudadores`" + " VALUES (?,?,?,?,?,?,?,?,?,?);";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, id);
			p.setInt(2, mapa);
			p.setInt(3, celda);
			p.setInt(4, o);
			p.setInt(5, guildID);
			p.setString(6, N1);
			p.setString(7, N2);
			p.setString(8, "");
			p.setLong(9, 0);
			p.setLong(10, 0);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static void ACTUALIZAR_RECAUDADOR(Recaudador P) {
		String consultaSQL = "UPDATE `recaudadores` SET `objetos` = ?,`kamas` = ?,`xp` = ?,`orientacion` = ?, `celda`=? WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setString(1, P.stringListaObjetosBD());
			p.setLong(2, P.getKamas());
			p.setLong(3, P.getXp());
			p.setInt(4, P.getOrientacion());
			p.setInt(5, P.getCeldalID());
			p.setInt(6, P.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static boolean ADD_ACCION_FIN_PELEA(int mapID, int tipo, int Aid, String args, String cond) {
		if (!DEL_ENDFIGHTACTION(mapID, tipo, Aid))
			return false;
		String consultaSQL = "INSERT INTO `accion_pelea` VALUES (?,?,?,?,?);";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, mapID);
			p.setInt(2, tipo);
			p.setInt(3, Aid);
			p.setString(4, args);
			p.setString(5, cond);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static boolean DEL_ENDFIGHTACTION(int mapID, int tipo, int aid) {
		String consultaSQL = "DELETE FROM `accion_pelea` WHERE mapa = ? AND tipoPelea = ? AND " + "accion = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, mapID);
			p.setInt(2, tipo);
			p.setInt(3, aid);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			return false;
		}
	}
	
	public static void SALVAR_NUEVO_GREMIO(Gremio g) {
		String consultaSQL = "INSERT INTO `gremios` VALUES (?,?,?,1,0,0,0,?,?);";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, g.getID());
			p.setString(2, g.getNombre());
			p.setString(3, g.getEmblema());
			p.setString(4, "462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0|");
			p.setString(5, "176;100|158;1000|124;100|");
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void BORRAR_GREMIO(int id) {
		String consultaSQL = "DELETE FROM `gremios` WHERE `id` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, id);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void BORRAR_MIEMBRO_GREMIO(int id) {
		String consultaSQL = "DELETE FROM `miembros_gremio` WHERE `id` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, id);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void ACTUALIZAR_GREMIO(Gremio g) {
		String consultaSQL = "UPDATE `gremios` SET `nivel` = ?,`xp` = ?,`capital` = ?,`recaudadores` = ?,`hechizos` = ?,"
				+ "`stats` = ? WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, g.getNivel());
			p.setLong(2, g.getXP());
			p.setInt(3, g.getCapital());
			p.setInt(4, g.getNroRecau());
			p.setString(5, g.compilarHechizo());
			p.setString(6, g.compilarStats());
			p.setInt(7, g.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void ACTUALIZAR_MIEMBRO_GREMIO(MiembroGremio gm) {
		String consultaSQL = "REPLACE INTO `miembros_gremio` VALUES(?,?,?,?,?,?,?,?,?,?);";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, gm.getID());
			p.setInt(2, gm.getGremio().getID());
			p.setString(3, gm.getVerdaderoNombre());
			p.setInt(4, gm.getNivel());
			p.setInt(5, gm.getGfx());
			p.setInt(6, gm.getRango());
			p.setLong(7, gm.getXpDonada());
			p.setInt(8, gm.getPorcXpDonada());
			p.setInt(9, gm.getDerechos());
			p.setString(10, gm.getUltimaConeccino());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static int esPJenGremio(int id) {
		int guildId = -1;
		try {
			ResultSet GuildQuery = executeQuery("SELECT gremio FROM `miembros_gremio` WHERE id=" + id + ";", Bustemu.BDTANIA);
			boolean found = GuildQuery.first();
			if (found)
				guildId = GuildQuery.getInt("gremio");
			closeResultSet(GuildQuery);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
		return guildId;
	}
	
	public static int[] esPJenGremio(String nombre) {
		int guildId = -1;
		int id = -1;
		try {
			ResultSet GuildQuery = executeQuery("SELECT gremio,id FROM `miembros_gremio` WHERE nombre='" + nombre + "';",
					Bustemu.BDTANIA);
			boolean found = GuildQuery.first();
			if (found) {
				guildId = GuildQuery.getInt("gremio");
				id = GuildQuery.getInt("id");
			}
			closeResultSet(GuildQuery);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
		int[] toReturn = { id, guildId };
		return toReturn;
	}
	
	public static boolean ADD_REPONSEACTION(int repID, int accion, String args) {
		String consultaSQL = "DELETE FROM `npc_respuestas` WHERE `id` = ? AND `accion` = ?;";
		PreparedStatement p;
		try {
			p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, repID);
			p.setInt(2, accion);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		consultaSQL = "INSERT INTO `npc_respuestas` VALUES (?,?,?);";
		try {
			p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, repID);
			p.setInt(2, accion);
			p.setString(3, args);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static boolean CAMBIAR_PREGUNTA_NPC(int id, int q) {
		String consultaSQL = "UPDATE `npcs_modelo` SET `pregunta` = ? WHERE `id` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, q);
			p.setInt(2, id);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static boolean CAMBIAR_RESPUESTA_NPC(int id, String reps) {
		String consultaSQL = "UPDATE `npc_preguntas` SET `respuestas` = ? WHERE `id` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setString(1, reps);
			p.setInt(2, id);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static void SET_ONLINE(int cuentaID) {
		PreparedStatement p;
		String consultaSQL = "UPDATE `cuentas` SET logeado=1 WHERE `id`=" + cuentaID + ";";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void SET_OFFLINE(int cuentaID) {
		PreparedStatement p;
		String consultaSQL = "UPDATE `cuentas` SET logeado=0 WHERE `id`=" + cuentaID + ";";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void LOGGED_ZERO() {
		PreparedStatement p;
		String consultaSQL = "UPDATE `cuentas` SET logeado=0;";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void CARGAR_FULL_OBJETOS() {
		try {
			ResultSet RS = executeQuery("SELECT * FROM objetos;", Bustemu.BDTANIA);
			while (RS.next()) {
				int id = RS.getInt("id");
				int modeloID = RS.getInt("modelo");
				int cantidad = RS.getInt("cantidad");
				int posicion = RS.getInt("posicion");
				String stats = RS.getString("stats");
				int objevivo = RS.getInt("objevivo");
				MundoDofus.addObjeto(new Objeto(id, modeloID, cantidad, posicion, stats, objevivo), false);
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static void CARGAR_RANKINGPVP() {
		try {
			ResultSet RS = executeQuery("SELECT * FROM ranking_pvp;", Bustemu.BDTANIA);
			while (RS.next()) {
				int id = RS.getInt("id");
				String nombre = RS.getString("nombre");
				int victorias = RS.getInt("victorias");
				int derrotas = RS.getInt("derrotas");
				int nivelAlin = RS.getInt("nivelAlineacion");
				MundoDofus.addRankingPVP(new RankingPVP(id, nombre, victorias, derrotas, nivelAlin));
			}
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static void TIMER(boolean start) {
		if (start) {
			timerComienzo = new Timer();
			timerComienzo.schedule(new TimerTask() {
				public void run() {
					if (!necesitaComenzar)
						return;
					comenzarTransacciones();
					necesitaComenzar = false;
				}
			}, Bustemu.BD_COMMIT, Bustemu.BD_COMMIT);
		} else
			timerComienzo.cancel();
	}
	
	public static boolean personajeYaExiste(String nombre) {
		boolean exist = false;
		try {
			String consultaSQL = "SELECT COUNT(*) AS exist FROM personajes WHERE nombre LIKE '" + nombre + "';";
			ResultSet RS = executeQuery(consultaSQL, Bustemu.BDTANIA);
			boolean found = RS.first();
			if (found) {
				if (RS.getInt("exist") != 0)
					exist = true;
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
		return exist;
	}
	
	public static void COMPRAR_CASA(Personaje P, Casa h) {
		PreparedStatement p;
		String consultaSQL = "UPDATE `casas` SET `precio`='0', `dueño`='" + P.getCuentaID()
				+ "', `gremio`='0', `acceso`='0', `clave`='-', `derechosGremio`='0' WHERE `id`='" + h.getID() + "';";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.execute();
			h.setPrecio(0);
			h.setDueñoID(P.getCuentaID());
			h.setGremioID(0);
			h.setAcceso(0);
			h.setClave("-");
			h.setDerechosGremio(0);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		ArrayList<Cofre> trunks = Cofre.getCofresPorCasa(h);
		for (Cofre trunk : trunks) {
			trunk.setDueñoID(P.getCuentaID());
			trunk.setClave("-");
		}
		consultaSQL = "UPDATE `cofres` SET `dueño`=?, `clave`='-' WHERE `casa`=?;";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, P.getCuentaID());
			p.setInt(2, h.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void VENDER_CASA(Casa h, int precio) {
		h.setPrecio(precio);
		PreparedStatement p;
		String consultaSQL = "UPDATE `casas` SET `precio`='" + precio + "' WHERE `id`='" + h.getID() + "';";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void CODIGO_CASA(Personaje perso, Casa casa, String packet) {
		PreparedStatement p;
		String consultaSQL = "UPDATE `casas` SET `clave`='" + packet + "' WHERE `id`='" + casa.getID() + "' AND dueño='"
				+ perso.getCuentaID() + "';";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.execute();
			casa.setClave(packet);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void ACTUALIZAR_CASA_GREMIO(Casa casa, int gremioID, int derechosGremio) {
		PreparedStatement p;
		String consultaSQL = "UPDATE `casas` SET `gremio`='" + gremioID + "', `derechosGremio`='" + derechosGremio
				+ "' WHERE `id`='" + casa.getID() + "';";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.execute();
			casa.setGremioID(gremioID);
			casa.setDerechosGremio(derechosGremio);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void BORRAR_CASA_GREMIO(int gremioID) {
		PreparedStatement p;
		String consultaSQL = "UPDATE `casas` SET `derechosGremio`='0', `gremio`='0' WHERE `gremio`='" + gremioID + "';";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.execute();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void ACTUALIZAR_CASA(Casa h) {
		String consultaSQL = "UPDATE `casas` SET `dueño` = ?,`precio` = ?,`gremio` = ?,`acceso` = ?,`clave` = ?,`derechosGremio` = ? WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, h.getDueñoID());
			p.setInt(2, h.getPrecioVenta());
			p.setInt(3, h.getGremioID());
			p.setInt(4, h.getAcceso());
			p.setString(5, h.getClave());
			p.setInt(6, h.getDerechosGremio());
			p.setInt(7, h.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static byte totalCercadosDelGremio(int getId) {
		byte i = 0;
		try {
			String consultaSQL = "SELECT * FROM cercados WHERE gremio='" + getId + "';";
			ResultSet RS = executeQuery(consultaSQL, Bustemu.BDTANIA);
			while (RS.next()) {
				i++;
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
		return i;
	}
	
	public static int getSigIDRecaudador() {
		int i = -100;
		try {
			String consultaSQL = "SELECT `id` FROM `recaudadores` ORDER BY `id` ASC LIMIT 0 , 1;";
			ResultSet RS = executeQuery(consultaSQL, Bustemu.BDTANIA);
			while (RS.next()) {
				i = RS.getInt("id") - 3;
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
		return i;
	}
	
	public static int CARGAR_ZAAPS() {
		int i = 0;
		try {
			ResultSet RS = executeQuery("SELECT mapa, celda FROM zaaps;", Bustemu.BDTANIA);
			while (RS.next()) {
				CentroInfo.ZAAPS.put(RS.getInt("mapa"), RS.getInt("celda"));
				i++;
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
		return i;
	}
	
	public static int getSigIDObjeto() {
		try {
			ResultSet RS = executeQuery("SELECT MAX(id) AS max FROM objetos;", Bustemu.BDTANIA);
			int id = 1;
			boolean encontrado = RS.first();
			if (encontrado)
				id = RS.getInt("max");
			closeResultSet(RS);
			return id;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
			Bustemu.cerrarServer();
		}
		return 1;
	}
	
	public static int CARGAR_BANIP() {
		int i = 0;
		try {
			ResultSet RS = executeQuery("SELECT ip FROM banip;", Bustemu.BDTANIA);
			while (RS.next()) {
				if (!RS.isLast())
					CentroInfo.BAN_IP += RS.getString("ip") + ",";
				else
					CentroInfo.BAN_IP += RS.getString("ip");
				i++;
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
		return i;
	}
	
	public static boolean AGREGAR_BANIP(String ip) {
		String consultaSQL = "INSERT INTO `banip` (ip) VALUES (?);";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setString(1, ip);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static boolean BORRAR_BANIP(String ip) {
		String consultaSQL = "DELETE  FROM `banip` WHERE ip = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setString(1, ip);
			p.execute();
			closePreparedStatement(p);
			return true;
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
		return false;
	}
	
	public static void CARGAR_PUESTOS_MERCADILLOS() {
		try {
			ResultSet RS = executeQuery("SELECT * FROM `mercadillos` ORDER BY id ASC", Bustemu.BDTANIA);
			while (RS.next()) {
				MundoDofus.addPuestoMercadillo(new PuestoMercadillo(RS.getInt("mapa"), RS.getFloat("porcVenta"), RS
						.getShort("tiempoVenta"), RS.getShort("cantidad"), RS.getShort("nivelMax"), RS.getString("categorias")));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_OBJETOS_MERCADILLOS() {
		try {
			ResultSet RS = executeQuery("SELECT i.*" + " FROM `objetos` AS i,`mercadillo_objetos` AS h"
					+ " WHERE i.id = h.objeto", Bustemu.BDTANIA);
			while (RS.next()) {
				int id = RS.getInt("id");
				int modeloID = RS.getInt("modelo");
				int cantidad = RS.getInt("cantidad");
				int posicion = RS.getInt("posicion");
				String stats = RS.getString("stats");
				int idOdjevivo = RS.getInt("objevivo");
				MundoDofus.addObjeto(MundoDofus.objetoIniciarServer(id, modeloID, cantidad, posicion, stats, idOdjevivo), false);
			}
			RS = executeQuery("SELECT * FROM `mercadillo_objetos`", Bustemu.BDTANIA);
			while (RS.next()) {
				PuestoMercadillo puesto = MundoDofus.getPuestoMerca(RS.getInt("mapa"));
				if (puesto == null)
					continue;
				puesto.addObjMercaAlPuesto(new ObjetoMercadillo(RS.getInt("precio"), RS.getByte("cantidad"), RS.getInt("dueño"),
						MundoDofus.getObjeto(RS.getInt("objeto"))));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void VACIA_Y_ACTUALIZA_OBJ_MERCADILLOS(ArrayList<ObjetoMercadillo> lista) {
		PreparedStatement queries = null;
		try {
			String emptyQuery = "TRUNCATE TABLE `mercadillo_objetos`";
			PreparedStatement tablaVacia = nuevaTransaccion(emptyQuery, bdTania);
			tablaVacia.execute();
			String consultaSQL = "INSERT INTO `mercadillo_objetos` (`mapa`,`dueño`,`precio`,`cantidad`,`objeto`) "
					+ "VALUES(?,?,?,?,?);";
			queries = nuevaTransaccion(consultaSQL, bdTania);
			for (ObjetoMercadillo objMerca : lista) {
				if (objMerca.getDueño() == -1)
					continue;
				queries.setInt(1, objMerca.getIDDelPuesto());
				queries.setInt(2, objMerca.getDueño());
				queries.setInt(3, objMerca.getPrecio());
				queries.setInt(4, objMerca.getTipoCantidad(false));
				queries.setInt(5, objMerca.getObjeto().getID());
				queries.execute();
			}
			closePreparedStatement(queries);
			SAVE_HDV_AVGPRICE();
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void SAVE_HDV_AVGPRICE() {
		String consultaSQL = "UPDATE `objetos_modelo` SET vendidos = ?, precioMedio = ? WHERE id = ?;";
		PreparedStatement queries = null;
		try {
			queries = nuevaTransaccion(consultaSQL, bdLuis);
			for (ObjetoModelo curTemp : MundoDofus.getObjModelos()) {
				if (curTemp.getVendidos() == 0)
					continue;
				queries.setLong(1, curTemp.getVendidos());
				queries.setInt(2, curTemp.getPrecioPromedio());
				queries.setInt(3, curTemp.getID());
				queries.executeUpdate();
			}
			closePreparedStatement(queries);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_ANIMACIONES() {
		try {
			ResultSet RS = executeQuery("SELECT * from animaciones;", Bustemu.BDLUIS);
			while (RS.next()) {
				MundoDofus.addAnimation(new Animacion(RS.getInt("id"), RS.getInt("id2"), RS.getString("nombre"), RS
						.getInt("area"), RS.getInt("accion"), RS.getInt("talla")));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_OBJEVIVOS() {
		try {
			ResultSet RS;
			for (RS = executeQuery("SELECT * from objevivos;", Bustemu.BDTANIA); RS.next(); MundoDofus.addObjevivo(new Objevivo(
					RS.getInt("id"), RS.getInt("añoComida"), RS.getInt("fechaComida"), RS.getInt("horaComida"), RS
							.getInt("humor"), RS.getInt("mascara"), RS.getInt("tipo"), RS.getInt("objetoAsociado"), RS
							.getLong("xp"), RS.getInt("añoInter"), RS.getInt("fechaInter"), RS.getInt("horaInter"), RS
							.getInt("añoObtenido"), RS.getInt("fechaObtenido"), RS.getInt("horaObtenido"), RS.getInt("asociado"),
					RS.getInt("modeloReal"), RS.getInt("objevivo"), RS.getString("stats"))))
				;
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println( (new StringBuilder("ERROR SQL: ")).append(e.getMessage()).toString());
			e.printStackTrace();
		}
	}
	
	public static boolean AGREGAR_OBJEVIVOS(Objevivo objevivo) {
		String consultaSQL = "INSERT INTO `objevivos` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, objevivo.getID());
			p.setInt(2, objevivo.getFeedYears());
			p.setInt(3, objevivo.getFeedDate());
			p.setInt(4, objevivo.getFeedHours());
			p.setInt(5, objevivo.getHumeur());
			p.setInt(6, objevivo.getMascara());
			p.setInt(7, objevivo.getType());
			p.setInt(8, objevivo.getItemID());
			p.setLong(9, objevivo.getXp());
			p.setInt(10, objevivo.getToYears());
			p.setInt(11, objevivo.getToDate());
			p.setInt(12, objevivo.getToHours());
			p.setInt(13, objevivo.getHasYears());
			p.setInt(14, objevivo.getHasDate());
			p.setInt(15, objevivo.getHasHours());
			p.setInt(16, objevivo.getAsociado());
			p.setInt(17, objevivo.getrealtemplate());
			p.setInt(18, objevivo.getItemObjevivo());
			p.setString(19, objevivo.getStat());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println( (new StringBuilder("ERROR SQL: ")).append(e.getMessage()).toString());
			System.out.println( (new StringBuilder("LINEA SQL: ")).append(consultaSQL).toString());
			return false;
		}
		return true;
	}
	
	public static void SALVAR_OBJEVIVO(Objevivo obvi) {
		String consultaSQL = "UPDATE `objevivos` SET `xp` = ?,`mascara` = ?,`stats` = ?,`objetoAsociado` = ?,`humor` = ?,"
				+ "`asociado` = ? WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setLong(1, obvi.getXp());
			p.setInt(2, obvi.getMascara());
			p.setString(3, obvi.getStat());
			p.setInt(4, obvi.getItemID());
			p.setInt(5, obvi.getHumeur());
			p.setInt(6, obvi.getAsociado());
			p.setInt(7, obvi.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println( (new StringBuilder("ERROR SQL: ")).append(e.getMessage()).toString());
			System.out.println( (new StringBuilder("LINEA SQL: ")).append(consultaSQL).toString());
		}
	}
	
	public static void BORRAR_OBJEVIVO(int id) {
		String consultaSQL = "DELETE FROM objevivos WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, id);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void CARGAR_RETOS() {
		try {
			ResultSet RS = executeQuery("SELECT * from retos;", Bustemu.BDLUIS);
			while (RS.next()) {
				MundoDofus.addReto(new Reto(RS.getInt("id"), RS.getString("bonus")));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println( (new StringBuilder("ERROR SQL: ")).append(e.getMessage()).toString());
			e.printStackTrace();
		}
	}
	
	public static int CARGAR_MERCANTES() {
		int numero = 0;
		try {
			ResultSet RS = executeQuery("SELECT * from mercante_mapas;", Bustemu.BDTANIA);
			while (RS.next()) {
				Mapa mapa = MundoDofus.getMapa(RS.getShort("idmapa"));
				if (mapa == null)
					continue;
				String personajes = RS.getString("personajes");
				if (personajes.isEmpty() || personajes == "|")
					continue;
				mapa.addMercantesMapa(personajes);
				numero++;
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
			numero = 0;
		}
		return numero;
	}
	
	public static void SALVAR_MERCANTES(Mapa mapa) {
		String consultaSQL = "UPDATE `mercante_mapas` SET `personajes` = ? WHERE idmapa = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setString(1, mapa.getMercantes());
			p.setInt(2, mapa.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println( (new StringBuilder("ERROR SQL: ")).append(e.getMessage()).toString());
			System.out.println( (new StringBuilder("LINEA SQL: ")).append(consultaSQL).toString());
		}
	}
	
	public static void ACTUALIZAR_IA_MOB(MobModelo mob) {
		String consultaSQL = "UPDATE `mobs` SET `tipoIA` = ? WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, mob.getTipoInteligencia());
			p.setInt(2, mob.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println( (new StringBuilder("ERROR SQL: ")).append(e.getMessage()).toString());
			System.out.println( (new StringBuilder("LINEA SQL: ")).append(consultaSQL).toString());
		}
	}
	
	public static void ACTUALIZAR_AFECTADOS_HECHIZO(int id, String afectados) {
		String consultaSQL = "UPDATE `hechizos` SET `afectados` = ? WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setString(1, afectados);
			p.setInt(2, id);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println( (new StringBuilder("ERROR SQL: ")).append(e.getMessage()).toString());
			System.out.println( (new StringBuilder("LINEA SQL: ")).append(consultaSQL).toString());
		}
	}
	
	public static void CARGAR_OBJETOS_MERCANTES() {
		try {
			ResultSet RS = executeQuery("SELECT * from mercante_objetos;", Bustemu.BDTANIA);
			while (RS.next()) {
				MundoDofus.agregarTienda(new Tienda(RS.getInt("objeto"), RS.getInt("precio"), RS.getInt("cantidad")), false);
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void AGREGAR_ITEM_TIENDA(Tienda tienda) {
		try {
			String consultaSQL = "REPLACE INTO `mercante_objetos` VALUES(?,?,?);";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, tienda.getItemId());
			p.setInt(2, tienda.getPrecio());
			p.setInt(3, tienda.getCantidad());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void BORRAR_ITEM_TIENDA(int id) {
		String consultaSQL = "DELETE FROM mercante_objetos WHERE objeto = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, id);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void ACTUALIZAR_PRECIO_TIENDA(int objeto, int precio) {
		try {
			String consultaSQL = "UPDATE `mercante_objetos` SET `precio` = ? WHERE objeto = ?;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, precio);
			p.setInt(2, objeto);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void ACTUALIZAR_CANT_TIENDA(int objeto, int cantidad) {
		try {
			String consultaSQL = "UPDATE `mercante_objetos` SET `cantidad` = ? WHERE objeto = ?;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, cantidad);
			p.setInt(2, objeto);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void AGREGAR_MASCOTA(Mascota mascota) {
		try {
			String consultaSQL = "INSERT INTO mascotas(`objeto`,`pdv`,`stats`,`comidas`,`año`,`mes`,`dia`,`hora`,`minuto`,`almasDevoradas`, `ultimaComida`, `idModelo`)"
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?);";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, mascota.getID());
			p.setInt(2, mascota.getPDV());
			p.setString(3, mascota.getStringStats());
			p.setInt(4, mascota.getNroComidas());
			p.setInt(5, mascota.getAño());
			p.setInt(6, mascota.getMes());
			p.setInt(7, mascota.getDia());
			p.setInt(8, mascota.getHora());
			p.setInt(9, mascota.getMinuto());
			p.setString(10, mascota.getAlmasDevoradas());
			p.setInt(11, mascota.getUltimaComida());
			p.setInt(12, mascota.getIDModelo());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void BORRAR_MASCOTA(int id) {
		String consultaSQL = "DELETE FROM mascotas WHERE objeto = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, id);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void ACTUALIZAR_MASCOTA(Mascota mascota) {
		try {
			String consultaSQL = "UPDATE `mascotas` SET `pdv` = ?,`stats` = ?,`comidas` = ?, `año`= ?, `mes` = ?,`dia` = ?"
					+ ",`hora` = ?,`minuto` = ?, `almasDevoradas` = ?, `ultimaComida` = ?, `obeso`= ?, `delgado`= ?, `idModelo`=? WHERE objeto = ?;";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, mascota.getPDV());
			p.setString(2, mascota.getStringStats());
			p.setInt(3, mascota.getNroComidas());
			p.setInt(4, mascota.getAño());
			p.setInt(5, mascota.getMes());
			p.setInt(6, mascota.getDia());
			p.setInt(7, mascota.getHora());
			p.setInt(8, mascota.getMinuto());
			p.setString(9, mascota.getAlmasDevoradas());
			p.setInt(10, mascota.getUltimaComida());
			p.setInt(11, mascota.getObeso() ? 7 : 0);
			p.setInt(12, mascota.getDelgado() ? 7 : 0);
			p.setInt(13, mascota.getIDModelo());
			p.setInt(14, mascota.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_MASCOTAS() {
		try {
			ResultSet RS = executeQuery("SELECT * from mascotas;", Bustemu.BDTANIA);
			while (RS.next()) {
				MundoDofus.addMascota(new Mascota(RS.getInt("objeto"), RS.getInt("pdv"), RS.getString("stats"), RS
						.getInt("comidas"), RS.getInt("año"), RS.getInt("mes"), RS.getInt("dia"), RS.getInt("hora"), RS
						.getInt("minuto"), RS.getInt("ultimaComida"), RS.getString("almasDevoradas"), RS.getInt("obeso"), RS
						.getInt("delgado"), RS.getInt("idModelo")));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static int CARGAR_COMIDAS_MASCOTAS() {
		int numero = 0;
		try {
			ResultSet RS = executeQuery("SELECT * from comida_mascotas;", Bustemu.BDLUIS);
			while (RS.next()) {
				MundoDofus.agregarMascotaModelo(
						RS.getInt("mascota"),
						new MascotaModelo(RS.getInt("maximoComidas"), RS.getString("statsPorEfecto"), RS.getString("comidas"), RS
								.getInt("devorador")));
				numero++;
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
			numero = 0;
		}
		return numero;
	}
	
	public static void ACTUALIZAR_PUERTA_CERCADO(int mapa, int celda) {
		String consultaSQL = "UPDATE cercados SET `celdapuerta` = ? WHERE `mapa` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, celda);
			p.setInt(2, mapa);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			e.printStackTrace();
		}
	}
	
	public static void ACTUALIZAR_CELDAS_OBJETO(int mapa, String celdas) {
		String consultaSQL = "UPDATE cercados SET `celdasobjeto` = ? WHERE `mapa` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setString(1, celdas);
			p.setInt(2, mapa);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			e.printStackTrace();
		}
	}
	
	public static void ACTUALIZAR_CELDA_MONTURA(int mapa, int celdas) {
		String consultaSQL = "UPDATE cercados SET `celdamontura` = ? WHERE `mapa` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, celdas);
			p.setInt(2, mapa);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			e.printStackTrace();
		}
	}
	
	public static void BORRAR_MOBS_MAPA(int mapa) {
		String consultaSQL = "UPDATE mapas SET `mobs` = '' WHERE `id` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, mapa);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			e.printStackTrace();
		}
	}
	
	public static void BORRAR_MOBSFIX_MAPA(int mapa) {
		String consultaSQL = "DELETE FROM mobs_fix WHERE mapa = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, mapa);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			e.printStackTrace();
		}
	}
	
	public static void BORRAR_ACCION_PELEA(int mapa) {
		String consultaSQL = "DELETE FROM accion_pelea WHERE mapa = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdLuis);
			p.setInt(1, mapa);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			e.printStackTrace();
		}
	}
	
	public static int CARGAR_PRISMAS() {
		int numero = 0;
		try {
			ResultSet RS = executeQuery("SELECT * from prismas;", Bustemu.BDTANIA);
			while (RS.next()) {
				MundoDofus.addPrisma(new Prisma(RS.getInt("id"), RS.getInt("alineacion"), RS.getInt("nivel"),
						RS.getShort("mapa"), RS.getInt("celda"), RS.getInt("honor"), RS.getInt("area")));
				numero++;
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
			numero = 0;
		}
		return numero;
	}
	
	public static void CARGAR_ENCARNACIONES() {
		try {
			ResultSet RS = executeQuery("SELECT * from encarnaciones;", Bustemu.BDTANIA);
			while (RS.next()) {
				MundoDofus.addEncarnacion(new Encarnacion(RS.getInt("id"), RS.getInt("clase"), RS.getInt("nivel"), RS
						.getLong("experiencia"), RS.getInt("segundos"), RS.getString("hechizos")));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
		return;
	}
	
	public static void AGREGAR_PRISMA(Prisma prisma) {
		try {
			String consultaSQL = "INSERT INTO prismas(`id`,`alineacion`,`nivel`,`mapa`,`celda`,`area`, `honor`) VALUES(?,?,?,?,?,?,?);";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, prisma.getID());
			p.setInt(2, prisma.getAlineacion());
			p.setInt(3, prisma.getNivel());
			p.setInt(4, prisma.getMapa());
			p.setInt(5, prisma.getCelda());
			p.setInt(6, prisma.getAreaConquistada());
			p.setInt(7, prisma.getHonor());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void AGREGAR_ENCARNACION(Encarnacion encarnacion) {
		try {
			String consultaSQL = "INSERT INTO encarnaciones(`id`,`nivel`,`clase`,`experiencia`,`segundos`,`hechizos`) VALUES(?,?,?,?,?,?);";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, encarnacion.getID());
			p.setInt(2, encarnacion.getNivel());
			p.setInt(3, encarnacion.getClase());
			p.setLong(4, encarnacion.getExperiencia());
			p.setInt(5, encarnacion.getSegundos());
			p.setString(6, encarnacion.stringHechizosABD());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void BORRAR_PRISMA(int id) {
		String consultaSQL = "DELETE FROM prismas WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, id);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void BORRAR_ENCARNACION(int id) {
		String consultaSQL = "DELETE FROM encarnaciones WHERE id = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, id);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void SALVAR_ENCARNACION(Encarnacion encarnacion) {
		String consultaSQL = "UPDATE encarnaciones SET `nivel` = ?, `experiencia` = ?, `hechizos`= ?, `segundos`= ? WHERE `id` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, encarnacion.getNivel());
			p.setLong(2, encarnacion.getExperiencia());
			p.setString(3, encarnacion.stringHechizosABD());
			p.setInt(4, encarnacion.getSegundos());
			p.setInt(5, encarnacion.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			e.printStackTrace();
		}
	}
	
	public static void SALVAR_PRISMA(Prisma prisma) {
		String consultaSQL = "UPDATE prismas SET `nivel` = ?, `honor` = ?, `area`= ? WHERE `id` = ?;";
		try {
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setInt(1, prisma.getNivel());
			p.setInt(2, prisma.getHonor());
			p.setInt(3, prisma.getAreaConquistada());
			p.setInt(4, prisma.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
			e.printStackTrace();
		}
	}
	
	public static void CARGAR_COFRE() {
		try {
			ResultSet RS = executeQuery("SELECT * from cofres;", Bustemu.BDTANIA);
			while (RS.next()) {
				MundoDofus.addCofre(new Cofre(RS.getInt("id"), RS.getInt("casa"), RS.getShort("mapa"), RS.getInt("celda"), RS
						.getString("objetos"), RS.getInt("kamas"), RS.getString("clave"), RS.getInt("dueño")));
			}
			closeResultSet(RS);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void CODIFICAR_COFRE(Personaje P, Cofre t, String packet) {
		PreparedStatement p;
		String consultaSQL = "UPDATE `cofres` SET `clave`=? WHERE `id`=? AND dueño=?;";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.setString(1, packet);
			p.setInt(2, t.getID());
			p.setInt(3, P.getCuentaID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void ACTUALIZAR_COFRE(Cofre cofre) {
		PreparedStatement p;
		String consultaSQL = "UPDATE `cofres` SET `kamas`=?, `objetos`=? WHERE `id`=?";
		try {
			p = nuevaTransaccion(consultaSQL, bdTania);
			p.setLong(1, cofre.getKamas());
			p.setString(2, cofre.analizarObjetoCofreABD());
			p.setInt(3, cofre.getID());
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			System.out.println("ERROR SQL: " + e.getMessage());
			System.out.println("LINEA SQL: " + consultaSQL);
		}
	}
	
	public static void AGREGAR_COMANDO_GM(String gm, String comando) {
		try {
			String consultaSQL = "INSERT INTO comandos(`nombre gm`,`comando`) VALUES(?,?);";
			PreparedStatement p = nuevaTransaccion(consultaSQL, bdTania);
			p.setString(1, gm);
			p.setString(2, comando);
			p.execute();
			closePreparedStatement(p);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
