package com.alain.reuniones.compartido.dominio;

/** Base de los errores de negocio: lleva un código estable que viaja al cliente HTTP. */
public abstract class ExcepcionDominio extends RuntimeException {

	private final String codigo;

	protected ExcepcionDominio(String codigo, String mensaje) {
		super(mensaje);
		this.codigo = codigo;
	}

	public String codigo() {
		return codigo;
	}

}
