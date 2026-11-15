package com.alain.reuniones.salas.infraestructura.persistencia;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.alain.reuniones.compartido.dominio.Pagina;
import com.alain.reuniones.salas.dominio.IdSala;
import com.alain.reuniones.salas.dominio.NombreSalaDuplicadoException;
import com.alain.reuniones.salas.dominio.RepositorioSalas;
import com.alain.reuniones.salas.dominio.Sala;

@Repository
public class AdaptadorPersistenciaSalas implements RepositorioSalas {

	private static final Sort ORDEN_POR_DEFECTO = Sort.by(Sort.Direction.ASC, "nombre").and(Sort.by("id"));

	private final RepositorioJpaSalas repositorio;

	public AdaptadorPersistenciaSalas(RepositorioJpaSalas repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Sala guardar(Sala sala) {
		try {
			return MapeadorSala.aDominio(repositorio.save(MapeadorSala.aEntidad(sala)));
		}
		catch (DataIntegrityViolationException excepcion) {
			throw new NombreSalaDuplicadoException(sala.nombre());
		}
	}

	@Override
	public Optional<Sala> buscarPorId(IdSala id) {
		return repositorio.findById(id.valor()).map(MapeadorSala::aDominio);
	}

	@Override
	public boolean existePorNombre(String nombre) {
		return repositorio.contarPorNombre(nombre) > 0;
	}

	@Override
	public Pagina<Sala> listar(int pagina, int tamano) {
		Page<EntidadSala> resultado = repositorio.findAll(PageRequest.of(pagina, tamano, ORDEN_POR_DEFECTO));
		return new Pagina<>(resultado.getContent().stream().map(MapeadorSala::aDominio).toList(), pagina, tamano,
				resultado.getTotalElements());
	}

}
