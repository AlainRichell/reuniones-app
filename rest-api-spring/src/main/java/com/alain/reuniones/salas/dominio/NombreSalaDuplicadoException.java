package com.alain.reuniones.salas.dominio;

import com.alain.reuniones.compartido.dominio.ExcepcionDominio;

public class NombreSalaDuplicadoException extends ExcepcionDominio {

	public NombreSalaDuplicadoException(String nombre) {
		super("NOMBRE_SALA_OCUPADO", "Ya existe una sala con el nombre " + nombre);
	}

}
