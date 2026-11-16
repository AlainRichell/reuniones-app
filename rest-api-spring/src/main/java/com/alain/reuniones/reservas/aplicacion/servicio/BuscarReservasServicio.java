package com.alain.reuniones.reservas.aplicacion.servicio;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alain.reuniones.compartido.dominio.Pagina;
import com.alain.reuniones.reservas.aplicacion.BuscarReservas;
import com.alain.reuniones.reservas.dominio.EstadoReserva;
import com.alain.reuniones.reservas.dominio.FiltroReservas;
import com.alain.reuniones.reservas.dominio.OrdenReserva;
import com.alain.reuniones.reservas.dominio.RepositorioReservas;
import com.alain.reuniones.reservas.dominio.Reserva;
import com.alain.reuniones.salas.dominio.IdSala;

@Service
public class BuscarReservasServicio implements BuscarReservas {

	private final RepositorioReservas repositorio;

	private final ZoneId zonaHoraria;

	public BuscarReservasServicio(RepositorioReservas repositorio, ZoneId zonaHoraria) {
		this.repositorio = repositorio;
		this.zonaHoraria = zonaHoraria;
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<Reserva> buscar(UUID salaId, LocalDate fecha, EstadoReserva estado, OrdenReserva orden, int pagina,
			int tamano) {
		FiltroReservas filtro = new FiltroReservas(salaId == null ? null : new IdSala(salaId), inicioDelDia(fecha),
				inicioDelDiaSiguiente(fecha), estado);
		return repositorio.buscar(filtro, orden, pagina, tamano);
	}

	private java.time.Instant inicioDelDia(LocalDate fecha) {
		return fecha == null ? null : fecha.atStartOfDay(zonaHoraria).toInstant();
	}

	private java.time.Instant inicioDelDiaSiguiente(LocalDate fecha) {
		return fecha == null ? null : fecha.plusDays(1).atStartOfDay(zonaHoraria).toInstant();
	}

}
