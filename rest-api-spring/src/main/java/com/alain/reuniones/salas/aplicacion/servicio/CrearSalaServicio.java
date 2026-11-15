package com.alain.reuniones.salas.aplicacion.servicio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alain.reuniones.compartido.aplicacion.GeneradorId;
import com.alain.reuniones.compartido.aplicacion.Reloj;
import com.alain.reuniones.salas.aplicacion.CrearSala;
import com.alain.reuniones.salas.dominio.IdSala;
import com.alain.reuniones.salas.dominio.NombreSalaDuplicadoException;
import com.alain.reuniones.salas.dominio.RepositorioSalas;
import com.alain.reuniones.salas.dominio.Sala;

@Service
public class CrearSalaServicio implements CrearSala {

	private final RepositorioSalas repositorio;
	private final Reloj reloj;
	private final GeneradorId generadorId;

	public CrearSalaServicio(RepositorioSalas repositorio, Reloj reloj, GeneradorId generadorId) {
		this.repositorio = repositorio;
		this.reloj = reloj;
		this.generadorId = generadorId;
	}

	@Override
	@Transactional
	public Sala crear(String nombre, int capacidad, String ubicacion) {
		if (repositorio.existePorNombre(nombre == null ? "" : nombre.trim())) {
			throw new NombreSalaDuplicadoException(nombre);
		}
		Sala sala = Sala.crear(new IdSala(generadorId.nuevo()), nombre, capacidad, ubicacion, reloj.ahora());
		return repositorio.guardar(sala);
	}

}
