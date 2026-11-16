package com.alain.reuniones.reservas.infraestructura.persistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.alain.reuniones.compartido.dominio.Pagina;
import com.alain.reuniones.reservas.dominio.EstadoReserva;
import com.alain.reuniones.reservas.dominio.FiltroReservas;
import com.alain.reuniones.reservas.dominio.OrdenReserva;
import com.alain.reuniones.reservas.dominio.RangoHorario;
import com.alain.reuniones.reservas.dominio.RepositorioReservas;
import com.alain.reuniones.reservas.dominio.Reserva;
import com.alain.reuniones.reservas.dominio.SalaNoDisponibleException;
import com.alain.reuniones.salas.dominio.IdSala;

import jakarta.persistence.criteria.Predicate;

@Repository
public class AdaptadorPersistenciaReservas implements RepositorioReservas {

	private final RepositorioJpaReservas repositorio;

	public AdaptadorPersistenciaReservas(RepositorioJpaReservas repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Reserva guardar(Reserva reserva) {
		try {
			return MapeadorReserva.aDominio(repositorio.save(MapeadorReserva.aEntidad(reserva)));
		}
		catch (DataIntegrityViolationException excepcion) {
			throw new SalaNoDisponibleException(
					"La sala " + reserva.salaId().valor() + " ya está ocupada en ese horario");
		}
	}

	@Override
	public Optional<Reserva> buscarPorId(UUID id) {
		return repositorio.findById(id).map(MapeadorReserva::aDominio);
	}

	@Override
	public boolean existeSolapada(IdSala salaId, RangoHorario rango) {
		return repositorio.contarSolapadas(salaId.valor(), rango.inicio(), rango.fin(), EstadoReserva.ACTIVA) > 0;
	}

	@Override
	public Pagina<Reserva> buscar(FiltroReservas filtro, OrdenReserva orden, int pagina, int tamano) {
		Page<EntidadReserva> resultado = repositorio.findAll(filtro(filtro),
				PageRequest.of(pagina, tamano, orden(orden)));
		return new Pagina<>(resultado.getContent().stream().map(MapeadorReserva::aDominio).toList(), pagina, tamano,
				resultado.getTotalElements());
	}

	private Specification<EntidadReserva> filtro(FiltroReservas filtro) {
		return (raiz, consulta, constructor) -> {
			List<Predicate> predicados = new ArrayList<>();
			if (filtro.salaId() != null) {
				predicados.add(constructor.equal(raiz.get("salaId"), filtro.salaId().valor()));
			}
			if (filtro.estado() != null) {
				predicados.add(constructor.equal(raiz.get("estado"), filtro.estado()));
			}
			if (filtro.desde() != null) {
				predicados.add(constructor.greaterThan(raiz.get("fin"), filtro.desde()));
			}
			if (filtro.hasta() != null) {
				predicados.add(constructor.lessThan(raiz.get("inicio"), filtro.hasta()));
			}
			return constructor.and(predicados.toArray(Predicate[]::new));
		};
	}

	private Sort orden(OrdenReserva orden) {
		String campo = orden == OrdenReserva.CREADA_EN ? "creadaEn" : "inicio";
		return Sort.by(Sort.Direction.ASC, campo).and(Sort.by("id"));
	}

}
