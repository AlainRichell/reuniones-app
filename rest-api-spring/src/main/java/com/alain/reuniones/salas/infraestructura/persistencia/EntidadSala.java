package com.alain.reuniones.salas.infraestructura.persistencia;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "salas")
public class EntidadSala {

	@Id
	private UUID id;

	@Column(nullable = false, length = 80)
	private String nombre;

	@Column(nullable = false)
	private int capacidad;

	@Column(length = 120)
	private String ubicacion;

	@Column(name = "creada_en", nullable = false)
	private Instant creadaEn;

	protected EntidadSala() {
	}

	public EntidadSala(UUID id, String nombre, int capacidad, String ubicacion, Instant creadaEn) {
		this.id = id;
		this.nombre = nombre;
		this.capacidad = capacidad;
		this.ubicacion = ubicacion;
		this.creadaEn = creadaEn;
	}

	public UUID getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public Instant getCreadaEn() {
		return creadaEn;
	}

}
