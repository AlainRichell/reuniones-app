package com.alain.reuniones.reservas.infraestructura.persistencia;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alain.reuniones.reservas.dominio.EstadoReserva;

interface RepositorioJpaReservas extends JpaRepository<EntidadReserva, UUID>, JpaSpecificationExecutor<EntidadReserva> {

	@Query("""
			select count(r) from EntidadReserva r
			where r.salaId = :salaId and r.estado = :estado
			  and r.inicio < :fin and r.fin > :inicio
			""")
	long contarSolapadas(@Param("salaId") UUID salaId, @Param("inicio") Instant inicio, @Param("fin") Instant fin,
			@Param("estado") EstadoReserva estado);

}
