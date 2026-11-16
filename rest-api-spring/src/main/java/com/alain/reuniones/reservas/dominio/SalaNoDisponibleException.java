package com.alain.reuniones.reservas.dominio;

import com.alain.reuniones.compartido.dominio.ExcepcionDominio;

public class SalaNoDisponibleException extends ExcepcionDominio {

	public SalaNoDisponibleException(String mensaje) {
		super("SALA_NO_DISPONIBLE", mensaje);
	}

}
