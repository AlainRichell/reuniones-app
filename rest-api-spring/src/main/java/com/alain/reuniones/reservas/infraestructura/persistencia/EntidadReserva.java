package com.alain.reuniones.reservas.infraestructura.persistencia;

import java.time.Instant;
import java.util.UUID;

import com.alain.reuniones.reservas.dominio.EstadoReserva;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservas")
public class EntidadReserva {

	@Id
	private UUID id;

	@Column(name = "sala_id", nullable = false)
	private UUID salaId;

	@Column(name = "nombre_responsable", nullable = false, length = 120)
	private String nombreResponsable;

	@Column(name = "correo_responsable", nullable = false, length = 180)
	private String correoResponsable;

	@Column(nullable = false)
	private Instant inicio;

	@Column(nullable = false)
	private Instant fin;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private EstadoReserva estado;

	@Column(name = "cancelada_en")
	private Instant canceladaEn;

	@Column(name = "creada_en", nullable = false)
	private Instant creadaEn;

	protected EntidadReserva() {
	}

	public EntidadReserva(UUID id, UUID salaId, String nombreResponsable, String correoResponsable, Instant inicio,
			Instant fin, EstadoReserva estado, Instant canceladaEn, Instant creadaEn) {
		this.id = id;
		this.salaId = salaId;
		this.nombreResponsable = nombreResponsable;
		this.correoResponsable = correoResponsable;
		this.inicio = inicio;
		this.fin = fin;
		this.estado = estado;
		this.canceladaEn = canceladaEn;
		this.creadaEn = creadaEn;
	}

	public UUID getId() {
		return id;
	}

	public UUID getSalaId() {
		return salaId;
	}

	public String getNombreResponsable() {
		return nombreResponsable;
	}

	public String getCorreoResponsable() {
		return correoResponsable;
	}

	public Instant getInicio() {
		return inicio;
	}

	public Instant getFin() {
		return fin;
	}

	public EstadoReserva getEstado() {
		return estado;
	}

	public Instant getCanceladaEn() {
		return canceladaEn;
	}

	public Instant getCreadaEn() {
		return creadaEn;
	}

}
