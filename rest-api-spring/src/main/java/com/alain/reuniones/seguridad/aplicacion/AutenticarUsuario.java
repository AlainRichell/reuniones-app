package com.alain.reuniones.seguridad.aplicacion;

import java.util.List;

/** Verifica las credenciales de un usuario y devuelve sus roles. */
public interface AutenticarUsuario {

	UsuarioAutenticado autenticar(String usuario, String clave);

	record UsuarioAutenticado(String usuario, List<String> roles) {
	}

}
