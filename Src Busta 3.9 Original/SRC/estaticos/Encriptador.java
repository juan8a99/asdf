
package estaticos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import variables.*;
import variables.Mapa.Celda;

public class Encriptador {
	public static String encriptarPassword(String codigoLlave, String Password) {
		char[] HASH = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
				'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
				'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_' };
		String encriptado = "#1";
		for (int i = 0; i < Password.length(); i++) {
			char PPass = Password.charAt(i);
			char PKey = codigoLlave.charAt(i);
			int APass = (int) PPass / 16;
			int AKey = (int) PPass % 16;
			int ANB = (APass + (int) PKey) % HASH.length;
			int ANB2 = (AKey + (int) PKey) % HASH.length;
			encriptado += HASH[ANB];
			encriptado += HASH[ANB2];
		}
		return encriptado;
	}
	
	public static String desencriptarPassword(String pass, String key) {
		int l1, l2, l3, l4, l5;
		String l7 = "";
		String Chaine = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
		for (l1 = 0; l1 <= (pass.length() - 1); l1 += 2) {
			l3 = (int) key.charAt( (l1 / 2));
			l2 = Chaine.indexOf(pass.charAt(l1));
			l4 = (64 + l2) - l3;
			int l11 = l1 + 1;
			l2 = Chaine.indexOf(pass.charAt(l11));
			l5 = (64 + l2) - l3;
			if (l5 < 0)
				l5 = 64 + l5;
			l7 = l7 + (char) (16 * l4 + l5);
		}
		return l7;
	}
	
	public static String encriptarIP(String IP) {
		String[] Splitted = IP.split("\\.");
		String Encrypted = "";
		int Count = 0;
		for (int i = 0; i < 50; i++) {
			for (int o = 0; o < 50; o++) {
				if ( ( (i & 15) << 4 | o & 15) == Integer.parseInt(Splitted[Count])) {
					Character A = (char) (i + 48);
					Character B = (char) (o + 48);
					Encrypted += A.toString() + B.toString();
					i = 0;
					o = 0;
					Count++;
					if (Count == 4)
						return Encrypted;
				}
			}
		}
		return "DD";
	}
	
	public static String encriptarPuerto(int configGamePort) {
		char[] HASH = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
				'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
				'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_' };
		int P = configGamePort;
		String nbr64 = "";
		for (int a = 2; a >= 0; a--) {
			nbr64 += HASH[(int) (P / (java.lang.Math.pow(64, a)))];
			P = (int) (P % (int) (java.lang.Math.pow(64, a)));
		}
		return nbr64;
	}
	
	public static String celdaIDACodigo(int celdaID) {
		char[] HASH = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
				'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
				'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_' };
		int char1 = celdaID / 64, char2 = celdaID % 64;
		return HASH[char1] + "" + HASH[char2];
	}
	
	public static int celdaCodigoAID(String celdaCodigo) {
		char[] HASH = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
				'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
				'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_' };
		char char1 = celdaCodigo.charAt(0), char2 = celdaCodigo.charAt(1);
		int code1 = 0, code2 = 0, a = 0;
		while (a < HASH.length) {
			if (HASH[a] == char1) {
				code1 = a * 64;
			}
			if (HASH[a] == char2) {
				code2 = a;
			}
			a++;
		}
		return (code1 + code2);
	}
	
	public static int getNumeroPorValorHash(char c) {
		char[] HASH = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
				'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
				'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_' };
		for (int a = 0; a < HASH.length; a++) {
			if (HASH[a] == c) {
				return a;
			}
		}
		return -1;
	}
	
	public static char getValorHashPorNumero(int c) {
		char[] hash = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
				'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
				'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_' };
		return hash[c];
	}
	
	public static ArrayList<Celda> analizarInicioCelda(Mapa mapa, int num) {
		ArrayList<Celda> listaCeldas = null;
		String infos = null;
		if (!mapa.getLugaresString().equalsIgnoreCase("-1")) {
			infos = mapa.getLugaresString().split("\\|")[num];
			int a = 0;
			listaCeldas = new ArrayList<Celda>();
			while (a < infos.length()) {
				listaCeldas.add(mapa.getCelda( (getNumeroPorValorHash(infos.charAt(a)) << 6)
						+ getNumeroPorValorHash(infos.charAt(a + 1))));
				a = a + 2;
			}
		}
		return listaCeldas;
	}
	
	public static Map<Integer, Celda> decompilarMapaData(Mapa mapa, String dData) {
		Map<Integer, Celda> celdas = new TreeMap<Integer, Celda>();
		for (int f = 0; f < dData.length(); f += 10) {
			String CellData = dData.substring(f, f + 10);
			List<Byte> celdaInfo = new ArrayList<Byte>();
			for (int i = 0; i < CellData.length(); i++)
				celdaInfo.add((byte) getNumeroPorValorHash(CellData.charAt(i)));
			int caminable = (celdaInfo.get(2) & 56) >> 3;// 0 = no, 1 = medio, 4 = si
			boolean lineaDeVista = (celdaInfo.get(0) & 1) != 0;
			int layerObject2 = ( (celdaInfo.get(0) & 2) << 12) + ( (celdaInfo.get(7) & 1) << 12) + (celdaInfo.get(8) << 6)
					+ celdaInfo.get(9);
			boolean layerObjeto2Interac = ( (celdaInfo.get(7) & 2) >> 1) != 0;
			int objeto = (layerObjeto2Interac ? layerObject2 : -1);
			
			celdas.put(f / 10, new Celda(mapa, f / 10, caminable != 0, lineaDeVista, objeto));
		}
		return celdas;
	}
	
	public static String aUTF(String entrada) {
		String _out = "";
		try {
			_out = new String(entrada.getBytes("UTF8"));
		} catch (Exception e) {
			System.out.println("Conversion en UTF-8 fallida! : " + e.getMessage());
		}
		return _out;
	}
	
	public static String aUnicode(String entrada) {
		String _out = "";
		try {
			_out = new String(entrada.getBytes(), "UTF8");
		} catch (Exception e) {
			System.out.println("Conversion en UNICODE fallida! : " + e.getMessage());
		}
		return _out;
	}
}
