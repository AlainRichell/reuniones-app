package com.alain.reuniones.salas.dominio;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/** Sala reservable: nombre único sin distinguir mayúsculas, capacidad entre 1 y 100. */
public final class Sala {

	private static final int LONGITUD_MINIMA_NOMBRE = 3;
	private static final int LONGITUD_MAXIMA_NOMBRE = 80;
	private static final int CAPACIDAD_MINIMA = 1;
	private static final int CAPACIDAD_MAXIMA = 100;
	private static final int LONGITUD_MAXIMA_UBICACION = 120;

	private final IdSala id;
	private final String nombre;
	private final int capacidad;
	private final String ubicacion;
	private final Instant creadaEn;

	private Sala(IdSala id, String nombre, int capacidad, String ubicacion, Instant creadaEn) {
		this.id = id;
		this.nombre = nombre;
		this.capacidad = capacidad;
		this.ubicacion = ubicacion;
		this.creadaEn = creadaEn;
	}

	public static Sala crear(IdSala id, String nombre, int capacidad, String ubicacion, Instant creadaEn) {
		Objects.requireNonNull(id, "el id de la sala es obligatorio");
		Objects.requireNonNull(creadaEn, "la fecha de creación es obligatoria");

		String nombreLimpio = limpiar(nombre);
		if (nombreLimpio.length() < LONGITUD_MINIMA_NOMBRE || nombreLimpio.length() > LONGITUD_MAXIMA_NOMBRE) {
			throw new SalaInvalidaException("El nombre debe tener entre 3 y 80 caracteres");
		}
		if (capacidad < CAPACIDAD_MINIMA || capacidad > CAPACIDAD_MAXIMA) {
			throw new SalaInvalidaException("La capacidad debe estar entre 1 y 100");
		}

		String ubicacionLimpia = limpiar(ubicacion);
		if (ubicacionLimpia.length() > LONGITUD_MAXIMA_UBICACION) {
			throw new SalaInvalidaException("La ubicación no puede superar 120 caracteres");
		}

		return new Sala(id, nombreLimpio, capacidad, ubicacionLimpia.isEmpty() ? null : ubicacionLimpia, creadaEn);
	}

	private static String limpiar(String valor) {
		return valor == null ? "" : valor.trim();
	}

	public IdSala id() {
		return id;
	}

	public String nombre() {
		return nombre;
	}

	public int capacidad() {
		return capacidad;
	}

	public Optional<String> ubicacion() {
		return Optional.ofNullable(ubicacion);
	}

	public Instant creadaEn() {
		return creadaEn;
	}

}
