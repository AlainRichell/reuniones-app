package com.alain.reuniones.salas.aplicacion.servicio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alain.reuniones.salas.aplicacion.ObtenerSala;
import com.alain.reuniones.salas.dominio.IdSala;
import com.alain.reuniones.salas.dominio.RepositorioSalas;
import com.alain.reuniones.salas.dominio.Sala;
import com.alain.reuniones.salas.dominio.SalaNoEncontradaException;

@Service
public class ObtenerSalaServicio implements ObtenerSala {

	private final RepositorioSalas repositorio;

	public ObtenerSalaServicio(RepositorioSalas repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	@Transactional(readOnly = true)
	public Sala obtener(IdSala id) {
		return repositorio.buscarPorId(id).orElseThrow(() -> new SalaNoEncontradaException(id));
	}

}
