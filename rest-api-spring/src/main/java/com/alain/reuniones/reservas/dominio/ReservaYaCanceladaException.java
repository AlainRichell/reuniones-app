package com.alain.reuniones.reservas.dominio;

import java.util.UUID;

import com.alain.reuniones.compartido.dominio.ExcepcionDominio;

public class ReservaYaCanceladaException extends ExcepcionDominio {

	public ReservaYaCanceladaException(UUID id) {
		super("RESERVA_YA_CANCELADA", "La reserva " + id + " ya está cancelada");
	}

}
