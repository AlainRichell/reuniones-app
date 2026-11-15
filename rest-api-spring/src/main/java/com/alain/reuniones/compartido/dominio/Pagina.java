package com.alain.reuniones.compartido.dominio;

import java.util.List;

/** Página de resultados independiente del framework de persistencia. */
public record Pagina<T>(List<T> contenido, int pagina, int tamano, long totalElementos) {

	public Pagina {
		contenido = List.copyOf(contenido);
	}

	public int totalPaginas() {
		return tamano <= 0 ? 0 : (int) Math.ceil((double) totalElementos / tamano);
	}

}
