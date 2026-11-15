package com.alain.reuniones.salas.infraestructura.persistencia;

import com.alain.reuniones.salas.dominio.IdSala;
import com.alain.reuniones.salas.dominio.Sala;

final class MapeadorSala {

	private MapeadorSala() {
	}

	static EntidadSala aEntidad(Sala sala) {
		return new EntidadSala(sala.id().valor(), sala.nombre(), sala.capacidad(), sala.ubicacion().orElse(null),
				sala.creadaEn());
	}

	static Sala aDominio(EntidadSala entidad) {
		return Sala.crear(new IdSala(entidad.getId()), entidad.getNombre(), entidad.getCapacidad(),
				entidad.getUbicacion(), entidad.getCreadaEn());
	}

}
