package com.alain.reuniones.salas.aplicacion;

import com.alain.reuniones.salas.dominio.Sala;

/** Crea una sala siempre que su nombre no esté ocupado. */
public interface CrearSala {

	Sala crear(String nombre, int capacidad, String ubicacion);

}
