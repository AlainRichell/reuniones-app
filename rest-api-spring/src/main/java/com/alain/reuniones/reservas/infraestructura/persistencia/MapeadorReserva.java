package com.alain.reuniones.reservas.infraestructura.persistencia;

import com.alain.reuniones.reservas.dominio.Reserva;
import com.alain.reuniones.salas.dominio.IdSala;

final class MapeadorReserva {

	private MapeadorReserva() {
	}

	static EntidadReserva aEntidad(Reserva reserva) {
		return new EntidadReserva(reserva.id(), reserva.salaId().valor(), reserva.nombreResponsable(),
				reserva.correoResponsable(), reserva.rango().inicio(), reserva.rango().fin(), reserva.estado(),
				reserva.canceladaEn().orElse(null), reserva.creadaEn());
	}

	static Reserva aDominio(EntidadReserva entidad) {
		return Reserva.reconstruir(entidad.getId(), new IdSala(entidad.getSalaId()), entidad.getNombreResponsable(),
				entidad.getCorreoResponsable(),
				new com.alain.reuniones.reservas.dominio.RangoHorario(entidad.getInicio(), entidad.getFin()),
				entidad.getEstado(), entidad.getCanceladaEn(), entidad.getCreadaEn());
	}

}
