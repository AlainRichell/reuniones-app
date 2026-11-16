package com.alain.reuniones.seguridad.aplicacion;

import java.util.List;

/** Emite el token que acredita a un usuario autenticado. */
public interface EmisorToken {

	TokenEmitido emitir(String usuario, List<String> roles);

	record TokenEmitido(String valor, long expiraEnSegundos) {
	}

}
