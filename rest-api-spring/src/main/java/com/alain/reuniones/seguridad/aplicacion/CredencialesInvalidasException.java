package com.alain.reuniones.seguridad.aplicacion;

import com.alain.reuniones.compartido.dominio.ExcepcionDominio;

public class CredencialesInvalidasException extends ExcepcionDominio {

	public CredencialesInvalidasException() {
		super("NO_AUTENTICADO", "El usuario o la clave no son correctos");
	}

}
