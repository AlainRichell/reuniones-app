package com.alain.reuniones.salas.infraestructura.web.dto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.alain.reuniones.salas.dominio.Sala;

public record SalaRespuesta(UUID id, String nombre, int capacidad, String ubicacion, Instant creadaEn) {

	public static SalaRespuesta de(Sala sala) {
		return new SalaRespuesta(sala.id().valor(), sala.nombre(), sala.capacidad(), sala.ubicacion().orElse(null),
				sala.creadaEn().truncatedTo(ChronoUnit.MILLIS));
	}

}
