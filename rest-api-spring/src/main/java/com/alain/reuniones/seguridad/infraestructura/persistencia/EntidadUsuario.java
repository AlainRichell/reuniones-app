package com.alain.reuniones.seguridad.infraestructura.persistencia;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class EntidadUsuario {

	@Id
	private UUID id;

	@Column(nullable = false, length = 60)
	private String usuario;

	@Column(name = "clave_hash", nullable = false, length = 100)
	private String claveHash;

	@Column(nullable = false, length = 120)
	private String roles;

	@Column(name = "creado_en", nullable = false)
	private Instant creadoEn;

	protected EntidadUsuario() {
	}

	public UUID getId() {
		return id;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getClaveHash() {
		return claveHash;
	}

	public String getRoles() {
		return roles;
	}

	public Instant getCreadoEn() {
		return creadoEn;
	}

}
