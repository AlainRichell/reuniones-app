package com.alain.reuniones.reservas.dominio;

import com.alain.reuniones.compartido.dominio.ExcepcionDominio;

public class ReservaInvalidaException extends ExcepcionDominio {

	public ReservaInvalidaException(String mensaje) {
		super("DATOS_INVALIDOS", mensaje);
	}

}
