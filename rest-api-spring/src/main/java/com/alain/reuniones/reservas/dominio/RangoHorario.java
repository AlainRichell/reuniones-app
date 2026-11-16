package com.alain.reuniones.reservas.dominio;

import java.time.Instant;
import java.util.Objects;

/** Intervalo semiabierto {@code [inicio, fin)}: dos rangos adyacentes no se solapan. */
public record RangoHorario(Instant inicio, Instant fin) {

	public RangoHorario {
		Objects.requireNonNull(inicio, "el inicio es obligatorio");
		Objects.requireNonNull(fin, "el fin es obligatorio");
		if (!fin.isAfter(inicio)) {
			throw new RangoHorarioInvalidoException("El fin debe ser posterior al inicio");
		}
	}

	public boolean seSolapaCon(RangoHorario otro) {
		return inicio.isBefore(otro.fin) && otro.inicio.isBefore(fin);
	}

	public boolean intersecta(Instant desde, Instant hasta) {
		return inicio.isBefore(hasta) && desde.isBefore(fin);
	}

}
