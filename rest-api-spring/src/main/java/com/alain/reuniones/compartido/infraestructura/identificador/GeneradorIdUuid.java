package com.alain.reuniones.compartido.infraestructura.identificador;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.alain.reuniones.compartido.aplicacion.GeneradorId;

@Component
public class GeneradorIdUuid implements GeneradorId {

	@Override
	public UUID nuevo() {
		return UUID.randomUUID();
	}

}
