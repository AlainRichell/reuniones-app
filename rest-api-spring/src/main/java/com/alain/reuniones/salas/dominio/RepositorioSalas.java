package com.alain.reuniones.salas.dominio;

import java.util.Optional;

import com.alain.reuniones.compartido.dominio.Pagina;

/** Puerta de salida del agregado {@link Sala}. */
public interface RepositorioSalas {

	Sala guardar(Sala sala);

	Optional<Sala> buscarPorId(IdSala id);

	boolean existePorNombre(String nombre);

	Pagina<Sala> listar(int pagina, int tamano);

}
