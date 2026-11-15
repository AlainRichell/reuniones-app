package com.alain.reuniones.salas.dominio;

import com.alain.reuniones.compartido.dominio.ExcepcionDominio;

public class SalaNoEncontradaException extends ExcepcionDominio {

	public SalaNoEncontradaException(IdSala id) {
		super("SALA_NO_ENCONTRADA", "No existe la sala " + id.valor());
	}

}
