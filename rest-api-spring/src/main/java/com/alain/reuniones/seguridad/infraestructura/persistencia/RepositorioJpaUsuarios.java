package com.alain.reuniones.seguridad.infraestructura.persistencia;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositorioJpaUsuarios extends JpaRepository<EntidadUsuario, UUID> {

	@Query("select u from EntidadUsuario u where lower(u.usuario) = lower(:usuario)")
	Optional<EntidadUsuario> buscarPorUsuario(@Param("usuario") String usuario);

}
