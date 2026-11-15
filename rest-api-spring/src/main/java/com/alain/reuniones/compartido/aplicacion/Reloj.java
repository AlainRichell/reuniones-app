package com.alain.reuniones.compartido.aplicacion;

import java.time.Instant;

/** Fuente de tiempo del sistema, sustituible en las pruebas. */
public interface Reloj {

	Instant ahora();

}
