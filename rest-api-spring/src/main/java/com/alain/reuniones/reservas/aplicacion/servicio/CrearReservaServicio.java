package com.alain.reuniones.reservas.aplicacion.servicio;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alain.reuniones.compartido.aplicacion.GeneradorId;
import com.alain.reuniones.compartido.aplicacion.Reloj;
import com.alain.reuniones.reservas.aplicacion.CrearReserva;
import com.alain.reuniones.reservas.dominio.RangoHorario;
import com.alain.reuniones.reservas.dominio.RepositorioReservas;
import com.alain.reuniones.reservas.dominio.Reserva;
import com.alain.reuniones.reservas.dominio.SalaNoDisponibleException;
import com.alain.reuniones.salas.aplicacion.api.BuscadorSalas;
import com.alain.reuniones.salas.dominio.IdSala;
import com.alain.reuniones.salas.dominio.SalaNoEncontradaException;

@Service
public class CrearReservaServicio implements CrearReserva {

	private final RepositorioReservas repositorio;

	private final BuscadorSalas buscadorSalas;

	private final Reloj reloj;

	private final GeneradorId generadorId;

	public CrearReservaServicio(RepositorioReservas repositorio, BuscadorSalas buscadorSalas, Reloj reloj,
			GeneradorId generadorId) {
		this.repositorio = repositorio;
		this.buscadorSalas = buscadorSalas;
		this.reloj = reloj;
		this.generadorId = generadorId;
	}

	@Override
	@Transactional
	public Reserva crear(UUID salaId, String nombreResponsable, String correoResponsable, Instant inicio,
			Instant fin) {
		IdSala sala = new IdSala(salaId);
		if (!buscadorSalas.existe(sala)) {
			throw new SalaNoEncontradaException(sala);
		}

		RangoHorario rango = new RangoHorario(inicio, fin);
		if (repositorio.existeSolapada(sala, rango)) {
			throw new SalaNoDisponibleException("La sala " + salaId + " ya está reservada de " + formatear(inicio)
					+ " a " + formatear(fin));
		}

		Reserva reserva = Reserva.programar(generadorId.nuevo(), sala, nombreResponsable, correoResponsable, rango,
				reloj.ahora());
		return repositorio.guardar(reserva);
	}

	private static String formatear(Instant instante) {
		return DateTimeFormatter.ISO_INSTANT.format(instante);
	}

}
