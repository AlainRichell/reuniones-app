package com.alain.reuniones.salas.dominio;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.alain.reuniones.compartido.dominio.Pagina;

/** Puerta de salida del agregado {@link Sala}. */
public interface RepositorioSalas {

	Sala guardar(Sala sala);

	Optional<Sala> buscarPorId(IdSala id);

	List<Sala> buscarPorIds(Collection<IdSala> ids);

	boolean existePorNombre(String nombre);

	Pagina<Sala> listar(int pagina, int tamano);

}
