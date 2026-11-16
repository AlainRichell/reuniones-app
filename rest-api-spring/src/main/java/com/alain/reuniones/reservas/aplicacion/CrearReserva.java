package com.alain.reuniones.reservas.aplicacion;

import java.time.Instant;
import java.util.UUID;

import com.alain.reuniones.reservas.dominio.Reserva;

/** Crea una reserva activa si la sala existe y está libre en el rango pedido. */
public interface CrearReserva {

	Reserva crear(UUID salaId, String nombreResponsable, String correoResponsable, Instant inicio, Instant fin);

}
