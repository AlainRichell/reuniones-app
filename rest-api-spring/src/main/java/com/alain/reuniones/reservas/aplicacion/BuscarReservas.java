package com.alain.reuniones.reservas.aplicacion;

import java.time.LocalDate;
import java.util.UUID;

import com.alain.reuniones.compartido.dominio.Pagina;
import com.alain.reuniones.reservas.dominio.EstadoReserva;
import com.alain.reuniones.reservas.dominio.OrdenReserva;
import com.alain.reuniones.reservas.dominio.Reserva;

/** Busca reservas por sala, día o estado, con paginación y orden estable. */
public interface BuscarReservas {

	Pagina<Reserva> buscar(UUID salaId, LocalDate fecha, EstadoReserva estado, OrdenReserva orden, int pagina,
			int tamano);

}
