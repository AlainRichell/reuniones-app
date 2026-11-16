package com.alain.reuniones.reservas.dominio;

import java.util.Optional;
import java.util.UUID;

import com.alain.reuniones.compartido.dominio.Pagina;
import com.alain.reuniones.salas.dominio.IdSala;

/** Puerta de salida del agregado {@link Reserva}. */
public interface RepositorioReservas {

	Reserva guardar(Reserva reserva);

	Optional<Reserva> buscarPorId(UUID id);

	boolean existeSolapada(IdSala salaId, RangoHorario rango);

	Pagina<Reserva> buscar(FiltroReservas filtro, OrdenReserva orden, int pagina, int tamano);

}
