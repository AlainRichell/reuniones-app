package com.alain.reuniones.salas.aplicacion;

import com.alain.reuniones.compartido.dominio.Pagina;
import com.alain.reuniones.salas.dominio.Sala;

/** Lista las salas de forma paginada. */
public interface ListarSalas {

	Pagina<Sala> listar(int pagina, int tamano);

}
