package com.alain.reuniones.seguridad.infraestructura;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alain.reuniones.seguridad.infraestructura.persistencia.EntidadUsuario;
import com.alain.reuniones.seguridad.infraestructura.persistencia.RepositorioJpaUsuarios;

@Service
public class ServicioDetallesUsuario implements UserDetailsService {

	private final RepositorioJpaUsuarios repositorio;

	public ServicioDetallesUsuario(RepositorioJpaUsuarios repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {
		EntidadUsuario encontrado = repositorio.buscarPorUsuario(usuario)
			.orElseThrow(() -> new UsernameNotFoundException("No existe el usuario " + usuario));
		return User.withUsername(encontrado.getUsuario())
			.password(encontrado.getClaveHash())
			.roles(encontrado.getRoles().split(","))
			.build();
	}

}
