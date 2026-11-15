package com.alain.reuniones.salas.dominio;

import java.util.Objects;
import java.util.UUID;

/** Identificador de una sala. */
public record IdSala(UUID valor) {

	public IdSala {
		Objects.requireNonNull(valor, "el id de la sala es obligatorio");
	}

}
