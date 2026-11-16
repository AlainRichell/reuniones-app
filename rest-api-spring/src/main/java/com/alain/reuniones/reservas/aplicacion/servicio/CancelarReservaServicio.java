package com.alain.reuniones.reservas.aplicacion.servicio;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alain.reuniones.compartido.aplicacion.Reloj;
import com.alain.reuniones.reservas.aplicacion.CancelarReserva;
import com.alain.reuniones.reservas.dominio.RepositorioReservas;
import com.alain.reuniones.reservas.dominio.Reserva;
import com.alain.reuniones.reservas.dominio.ReservaNoEncontradaException;

@Service
public class CancelarReservaServicio implements CancelarReserva {

	private final RepositorioReservas repositorio;

	private final Reloj reloj;

	public CancelarReservaServicio(RepositorioReservas repositorio, Reloj reloj) {
		this.repositorio = repositorio;
		this.reloj = reloj;
	}

	@Override
	@Transactional
	public Reserva cancelar(UUID id) {
		Reserva reserva = repositorio.buscarPorId(id).orElseThrow(() -> new ReservaNoEncontradaException(id));
		return repositorio.guardar(reserva.cancelar(reloj.ahora()));
	}

}
