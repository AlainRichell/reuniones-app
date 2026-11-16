package com.alain.reuniones.reservas.dominio;

import java.util.UUID;

import com.alain.reuniones.compartido.dominio.ExcepcionDominio;

public class ReservaNoEncontradaException extends ExcepcionDominio {

	public ReservaNoEncontradaException(UUID id) {
		super("RESERVA_NO_ENCONTRADA", "No existe la reserva " + id);
	}

}
