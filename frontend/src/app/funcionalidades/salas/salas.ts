import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { mensajeDeError } from '../../nucleo/api/errores';
import { ServicioSalas } from '../../nucleo/api/servicio-salas';
import { Sala } from '../../nucleo/modelos';

@Component({
  selector: 'app-salas',
  imports: [
    DatePipe,
    FormsModule,
    ButtonModule,
    DialogModule,
    InputNumberModule,
    InputTextModule,
    PaginatorModule,
  ],
  templateUrl: './salas.html',
})
export class Salas {
  private readonly servicio = inject(ServicioSalas);
  private readonly mensajes = inject(MessageService);

  protected readonly salas = signal<Sala[]>([]);
  protected readonly total = signal(0);
  protected readonly cargando = signal(false);
  protected readonly guardando = signal(false);

  protected tamano = 10;
  protected pagina = 0;
  protected dialogoVisible = false;
  protected nueva = { nombre: '', capacidad: 8, ubicacion: '' };

  constructor() {
    this.cargar(0);
  }

  protected cargar(pagina: number): void {
    this.pagina = pagina;
    this.cargando.set(true);

    this.servicio.listar(pagina, this.tamano).subscribe({
      next: (resultado) => {
        this.salas.set(resultado.contenido);
        this.total.set(resultado.totalElementos);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.mensajes.add({
          severity: 'error',
          summary: 'No se pudieron cargar las salas',
          detail: mensajeDeError(error),
        });
      },
    });
  }

  protected cambiarPagina(evento: PaginatorState): void {
    this.tamano = evento.rows ?? this.tamano;
    this.cargar(evento.page ?? 0);
  }

  protected abrirDialogo(): void {
    this.nueva = { nombre: '', capacidad: 8, ubicacion: '' };
    this.dialogoVisible = true;
  }

  protected crear(): void {
    this.guardando.set(true);
    const solicitud = {
      nombre: this.nueva.nombre.trim(),
      capacidad: this.nueva.capacidad,
      ubicacion: this.nueva.ubicacion.trim() || null,
    };

    this.servicio.crear(solicitud).subscribe({
      next: () => {
        this.guardando.set(false);
        this.dialogoVisible = false;
        this.mensajes.add({ severity: 'success', summary: 'Sala creada' });
        this.cargar(0);
      },
      error: (error: HttpErrorResponse) => {
        this.guardando.set(false);
        this.mensajes.add({
          severity: 'error',
          summary: 'No se pudo crear la sala',
          detail: mensajeDeError(error),
        });
      },
    });
  }
}
