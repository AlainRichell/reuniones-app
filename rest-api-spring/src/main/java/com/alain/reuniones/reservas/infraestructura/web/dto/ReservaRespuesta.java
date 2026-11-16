package com.alain.reuniones.reservas.infraestructura.web.dto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.alain.reuniones.reservas.dominio.EstadoReserva;
import com.alain.reuniones.reservas.dominio.Reserva;

public record ReservaRespuesta(UUID id, SalaResumen sala, String nombreResponsable, String correoResponsable,
		Instant inicio, Instant fin, EstadoReserva estado, Instant canceladaEn, Instant creadaEn) {

	public record SalaResumen(UUID id, String nombre) {
	}

	public static ReservaRespuesta de(Reserva reserva, String nombreSala) {
		return new ReservaRespuesta(reserva.id(), new SalaResumen(reserva.salaId().valor(), nombreSala),
				reserva.nombreResponsable(), reserva.correoResponsable(),
				reserva.rango().inicio().truncatedTo(ChronoUnit.MILLIS),
				reserva.rango().fin().truncatedTo(ChronoUnit.MILLIS), reserva.estado(),
				reserva.canceladaEn().map(instante -> instante.truncatedTo(ChronoUnit.MILLIS)).orElse(null),
				reserva.creadaEn().truncatedTo(ChronoUnit.MILLIS));
	}

}
