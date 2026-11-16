package com.alain.reuniones.salas.aplicacion.servicio;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alain.reuniones.salas.aplicacion.api.BuscadorSalas;
import com.alain.reuniones.salas.dominio.IdSala;
import com.alain.reuniones.salas.dominio.RepositorioSalas;
import com.alain.reuniones.salas.dominio.Sala;
import com.alain.reuniones.salas.dominio.SalaNoEncontradaException;

@Service
public class BuscadorSalasServicio implements BuscadorSalas {

	private final RepositorioSalas repositorio;

	public BuscadorSalasServicio(RepositorioSalas repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existe(IdSala id) {
		return repositorio.buscarPorId(id).isPresent();
	}

	@Override
	@Transactional(readOnly = true)
	public SalaBreve buscar(IdSala id) {
		Sala sala = repositorio.buscarPorId(id).orElseThrow(() -> new SalaNoEncontradaException(id));
		return new SalaBreve(sala.id().valor(), sala.nombre());
	}

	@Override
	@Transactional(readOnly = true)
	public Map<UUID, String> nombresDe(Collection<IdSala> ids) {
		if (ids.isEmpty()) {
			return Map.of();
		}
		return repositorio.buscarPorIds(ids)
			.stream()
			.collect(Collectors.toMap(sala -> sala.id().valor(), Sala::nombre, (primero, segundo) -> primero));
	}

}
