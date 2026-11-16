package com.alain.reuniones.reservas.dominio;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.alain.reuniones.salas.dominio.IdSala;

/** Reserva de una sala en un rango horario; nace activa y solo puede cancelarse una vez. */
public final class Reserva {

	private static final int LONGITUD_MINIMA_RESPONSABLE = 2;
	private static final int LONGITUD_MAXIMA_RESPONSABLE = 120;
	private static final int LONGITUD_MAXIMA_CORREO = 180;
	private static final String PATRON_CORREO = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

	private final UUID id;
	private final IdSala salaId;
	private final String nombreResponsable;
	private final String correoResponsable;
	private final RangoHorario rango;
	private final EstadoReserva estado;
	private final Instant canceladaEn;
	private final Instant creadaEn;

	private Reserva(UUID id, IdSala salaId, String nombreResponsable, String correoResponsable, RangoHorario rango,
			EstadoReserva estado, Instant canceladaEn, Instant creadaEn) {
		this.id = id;
		this.salaId = salaId;
		this.nombreResponsable = nombreResponsable;
		this.correoResponsable = correoResponsable;
		this.rango = rango;
		this.estado = estado;
		this.canceladaEn = canceladaEn;
		this.creadaEn = creadaEn;
	}

	public static Reserva programar(UUID id, IdSala salaId, String nombreResponsable, String correoResponsable,
			RangoHorario rango, Instant creadaEn) {
		validar(id, salaId, nombreResponsable, correoResponsable, rango, creadaEn);
		return new Reserva(id, salaId, nombreResponsable.trim(), correoResponsable.trim(), rango, EstadoReserva.ACTIVA,
				null, creadaEn);
	}

	public static Reserva reconstruir(UUID id, IdSala salaId, String nombreResponsable, String correoResponsable,
			RangoHorario rango, EstadoReserva estado, Instant canceladaEn, Instant creadaEn) {
		validar(id, salaId, nombreResponsable, correoResponsable, rango, creadaEn);
		Objects.requireNonNull(estado, "el estado es obligatorio");
		return new Reserva(id, salaId, nombreResponsable.trim(), correoResponsable.trim(), rango, estado, canceladaEn,
				creadaEn);
	}

	private static void validar(UUID id, IdSala salaId, String nombreResponsable, String correoResponsable,
			RangoHorario rango, Instant creadaEn) {
		Objects.requireNonNull(id, "el id de la reserva es obligatorio");
		Objects.requireNonNull(salaId, "la sala es obligatoria");
		Objects.requireNonNull(rango, "el rango horario es obligatorio");
		Objects.requireNonNull(creadaEn, "la fecha de creación es obligatoria");

		String nombre = nombreResponsable == null ? "" : nombreResponsable.trim();
		if (nombre.length() < LONGITUD_MINIMA_RESPONSABLE || nombre.length() > LONGITUD_MAXIMA_RESPONSABLE) {
			throw new ReservaInvalidaException("El nombre del responsable debe tener entre 2 y 120 caracteres");
		}
		String correo = correoResponsable == null ? "" : correoResponsable.trim();
		if (correo.length() > LONGITUD_MAXIMA_CORREO || !correo.matches(PATRON_CORREO)) {
			throw new ReservaInvalidaException("El correo del responsable no es válido");
		}
	}

	public Reserva cancelar(Instant ahora) {
		if (estado == EstadoReserva.CANCELADA) {
			throw new ReservaYaCanceladaException(id);
		}
		Objects.requireNonNull(ahora, "la fecha de cancelación es obligatoria");
		return new Reserva(id, salaId, nombreResponsable, correoResponsable, rango, EstadoReserva.CANCELADA, ahora,
				creadaEn);
	}

	public UUID id() {
		return id;
	}

	public IdSala salaId() {
		return salaId;
	}

	public String nombreResponsable() {
		return nombreResponsable;
	}

	public String correoResponsable() {
		return correoResponsable;
	}

	public RangoHorario rango() {
		return rango;
	}

	public EstadoReserva estado() {
		return estado;
	}

	public Optional<Instant> canceladaEn() {
		return Optional.ofNullable(canceladaEn);
	}

	public Instant creadaEn() {
		return creadaEn;
	}

}
