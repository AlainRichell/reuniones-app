package com.alain.reuniones.salas.dominio;

import com.alain.reuniones.compartido.dominio.ExcepcionDominio;

public class SalaInvalidaException extends ExcepcionDominio {

	public SalaInvalidaException(String mensaje) {
		super("DATOS_INVALIDOS", mensaje);
	}

}
