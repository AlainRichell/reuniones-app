package com.alain.reuniones.reservas.aplicacion.servicio;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alain.reuniones.reservas.aplicacion.ObtenerReserva;
import com.alain.reuniones.reservas.dominio.RepositorioReservas;
import com.alain.reuniones.reservas.dominio.Reserva;
import com.alain.reuniones.reservas.dominio.ReservaNoEncontradaException;

@Service
public class ObtenerReservaServicio implements ObtenerReserva {

	private final RepositorioReservas repositorio;

	public ObtenerReservaServicio(RepositorioReservas repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	@Transactional(readOnly = true)
	public Reserva obtener(UUID id) {
		return repositorio.buscarPorId(id).orElseThrow(() -> new ReservaNoEncontradaException(id));
	}

}
