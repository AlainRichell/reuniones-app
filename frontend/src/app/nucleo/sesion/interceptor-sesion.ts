import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AlmacenSesion } from './almacen-sesion';

export const interceptorSesion: HttpInterceptorFn = (peticion, siguiente) => {
  const token = inject(AlmacenSesion).tokenActual();
  if (!token) {
    return siguiente(peticion);
  }
  return siguiente(peticion.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
