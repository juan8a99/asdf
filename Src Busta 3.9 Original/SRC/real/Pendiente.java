package real;

import estaticos.GestorSalida;

import variables.Cuenta;

public class Pendiente {

	public static void EnEspera(Cuenta C) {
		if (C == null)
			return;
		if (C._posicion <= 1) {
			try {
				Thread.sleep(750);
				if (C == null || C.getEntradaGeneral() == null || C.getEntradaGeneral()._out == null)
					return;
				GestorSalida.ENVIAR_Af_ABONADOS_POSCOLA(C.getEntradaGeneral()._out, 1,
						ServidorGeneral._totalAbonodos, ServidorGeneral._totalNoAbonados,
						"" + 1, ServidorGeneral._nroColaID);
				C._posicion = -1;
				ServidorGeneral._totalAbonodos--;
			} catch (InterruptedException e) {
				GestorSalida
						.ENVIAR_AlEc_MISMA_CUENTA_CONECTADA(C.getEntradaGeneral()._out);
				System.out.println("Error : " + e.getMessage());
			}
		} else {
			try {
				Thread.sleep(750 * C._posicion);
				if (C == null || C.getEntradaGeneral() == null || C.getEntradaGeneral()._out == null)
					return;
				GestorSalida.ENVIAR_Af_ABONADOS_POSCOLA(C.getEntradaGeneral()._out, 1,
						ServidorGeneral._totalAbonodos, ServidorGeneral._totalNoAbonados,
						"" + 1, ServidorGeneral._nroColaID);
				C._posicion = -1;
				ServidorGeneral._totalAbonodos--;
			} catch (InterruptedException e) {
				GestorSalida
						.ENVIAR_AlEc_MISMA_CUENTA_CONECTADA(C.getEntradaGeneral()._out);
				System.out.println("Error : " + e.getMessage());
			}
		}
	}
}