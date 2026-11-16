import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { AlmacenSesion } from './nucleo/sesion/almacen-sesion';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ToastModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly sesion = inject(AlmacenSesion);
  private readonly router = inject(Router);

  protected cerrarSesion(): void {
    this.sesion.cerrar();
    this.router.navigate(['/inicio-sesion']);
  }
}
