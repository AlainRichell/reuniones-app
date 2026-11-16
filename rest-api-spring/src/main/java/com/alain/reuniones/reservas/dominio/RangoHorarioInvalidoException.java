package com.alain.reuniones.reservas.dominio;

import com.alain.reuniones.compartido.dominio.ExcepcionDominio;

public class RangoHorarioInvalidoException extends ExcepcionDominio {

	public RangoHorarioInvalidoException(String mensaje) {
		super("RANGO_HORARIO_INVALIDO", mensaje);
	}

}
