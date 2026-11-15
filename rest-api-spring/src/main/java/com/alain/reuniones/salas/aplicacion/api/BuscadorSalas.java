package com.alain.reuniones.salas.aplicacion.api;

import java.util.UUID;

import com.alain.reuniones.salas.dominio.IdSala;

/** Vista mínima de una sala para otros módulos, sin exponer su persistencia. */
public interface BuscadorSalas {

	boolean existe(IdSala id);

	SalaBreve buscar(IdSala id);

	record SalaBreve(UUID id, String nombre) {
	}

}
