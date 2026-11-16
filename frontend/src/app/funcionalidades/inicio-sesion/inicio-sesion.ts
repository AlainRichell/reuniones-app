import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { mensajeDeError } from '../../nucleo/api/errores';
import { ServicioSesion } from '../../nucleo/api/servicio-sesion';
import { AlmacenSesion } from '../../nucleo/sesion/almacen-sesion';

@Component({
  selector: 'app-inicio-sesion',
  imports: [FormsModule, ButtonModule, InputTextModule],
  templateUrl: './inicio-sesion.html',
})
export class InicioSesion {
  private readonly servicio = inject(ServicioSesion);
  private readonly almacen = inject(AlmacenSesion);
  private readonly router = inject(Router);

  protected usuario = '';
  protected clave = '';
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);

  protected iniciar(): void {
    this.cargando.set(true);
    this.error.set(null);
    this.servicio.iniciar(this.usuario.trim(), this.clave).subscribe({
      next: (respuesta) => {
        this.almacen.guardar(respuesta.tokenAcceso);
        this.router.navigate(['/salas']);
      },
      error: (error: HttpErrorResponse) => {
        this.error.set(mensajeDeError(error));
        this.cargando.set(false);
      },
    });
  }
}
