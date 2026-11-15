package com.alain.reuniones.salas.infraestructura.persistencia;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface RepositorioJpaSalas extends JpaRepository<EntidadSala, UUID> {

	@Query("select count(s) from EntidadSala s where lower(s.nombre) = lower(:nombre)")
	long contarPorNombre(@Param("nombre") String nombre);

}
