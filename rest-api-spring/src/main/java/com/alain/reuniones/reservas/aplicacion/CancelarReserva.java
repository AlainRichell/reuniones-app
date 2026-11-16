package com.alain.reuniones.reservas.aplicacion;

import java.util.UUID;

import com.alain.reuniones.reservas.dominio.Reserva;

/** Cancela una reserva activa; una reserva ya cancelada es un conflicto, no un no-op. */
public interface CancelarReserva {

	Reserva cancelar(UUID id);

}
