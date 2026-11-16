package com.alain.reuniones.reservas.dominio;

import java.time.Instant;

import com.alain.reuniones.salas.dominio.IdSala;

/** Criterios de búsqueda de reservas, ya resueltos a instantes por la capa de aplicación. */
public record FiltroReservas(IdSala salaId, Instant desde, Instant hasta, EstadoReserva estado) {

}
