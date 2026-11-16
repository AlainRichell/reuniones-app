import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AlmacenSesion } from './almacen-sesion';

export const guardiaSesion: CanActivateFn = () => {
  if (inject(AlmacenSesion).autenticado()) {
    return true;
  }
  return inject(Router).createUrlTree(['/inicio-sesion']);
};
