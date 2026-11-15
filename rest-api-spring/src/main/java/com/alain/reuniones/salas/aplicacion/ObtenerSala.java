package com.alain.reuniones.salas.aplicacion;

import com.alain.reuniones.salas.dominio.IdSala;
import com.alain.reuniones.salas.dominio.Sala;

/** Obtiene una sala por su identificador. */
public interface ObtenerSala {

	Sala obtener(IdSala id);

}
