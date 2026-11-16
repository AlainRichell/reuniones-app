import { Routes } from '@angular/router';
import { guardiaSesion } from './nucleo/sesion/guardia-sesion';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'salas' },
  {
    path: 'inicio-sesion',
    loadComponent: () =>
      import('./funcionalidades/inicio-sesion/inicio-sesion').then((m) => m.InicioSesion),
  },
  {
    path: 'salas',
    canActivate: [guardiaSesion],
    loadComponent: () => import('./funcionalidades/salas/salas').then((m) => m.Salas),
  },
  {
    path: 'reservas',
    canActivate: [guardiaSesion],
    loadComponent: () => import('./funcionalidades/reservas/reservas').then((m) => m.Reservas),
  },
  { path: '**', redirectTo: 'salas' },
];
