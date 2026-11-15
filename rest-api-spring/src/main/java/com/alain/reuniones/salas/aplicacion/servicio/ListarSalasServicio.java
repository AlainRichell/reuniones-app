package com.alain.reuniones.salas.aplicacion.servicio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alain.reuniones.compartido.dominio.Pagina;
import com.alain.reuniones.salas.aplicacion.ListarSalas;
import com.alain.reuniones.salas.dominio.RepositorioSalas;
import com.alain.reuniones.salas.dominio.Sala;

@Service
public class ListarSalasServicio implements ListarSalas {

	private final RepositorioSalas repositorio;

	public ListarSalasServicio(RepositorioSalas repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<Sala> listar(int pagina, int tamano) {
		return repositorio.listar(pagina, tamano);
	}

}
