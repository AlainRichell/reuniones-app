package com.alain.reuniones.compartido.infraestructura.tiempo;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.alain.reuniones.compartido.aplicacion.Reloj;

@Component
public class RelojSistema implements Reloj {

	@Override
	public Instant ahora() {
		return Instant.now();
	}

}
