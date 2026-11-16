package com.alain.reuniones.reservas.aplicacion;

import java.util.UUID;

import com.alain.reuniones.reservas.dominio.Reserva;

/** Obtiene una reserva por su identificador. */
public interface ObtenerReserva {

	Reserva obtener(UUID id);

}
