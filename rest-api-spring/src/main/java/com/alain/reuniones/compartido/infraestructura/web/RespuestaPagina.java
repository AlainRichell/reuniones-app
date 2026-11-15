package com.alain.reuniones.compartido.infraestructura.web;

import java.util.List;
import java.util.function.Function;

import com.alain.reuniones.compartido.dominio.Pagina;

/** Envoltorio de paginación de la API, independiente del formato interno de Spring. */
public record RespuestaPagina<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {

	public static <D, R> RespuestaPagina<R> de(Pagina<D> pagina, Function<D, R> mapeador) {
		return new RespuestaPagina<>(pagina.contenido().stream().map(mapeador).toList(), pagina.pagina(),
				pagina.tamano(), pagina.totalElementos(), pagina.totalPaginas());
	}

}
