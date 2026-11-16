import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DatePickerModule } from 'primeng/datepicker';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { SelectModule } from 'primeng/select';
import { TagModule } from 'primeng/tag';
import { mensajeDeError } from '../../nucleo/api/errores';
import { ServicioReservas } from '../../nucleo/api/servicio-reservas';
import { ServicioSalas } from '../../nucleo/api/servicio-salas';
import { aFechaLocal } from '../../nucleo/fechas';
import { EstadoReserva, Reserva, Sala } from '../../nucleo/modelos';

@Component({
  selector: 'app-reservas',
  imports: [
    DatePipe,
    FormsModule,
    ButtonModule,
    ConfirmDialogModule,
    DatePickerModule,
    DialogModule,
    InputTextModule,
    PaginatorModule,
    SelectModule,
    TagModule,
  ],
  providers: [ConfirmationService],
  templateUrl: './reservas.html',
})
export class Reservas {
  private readonly servicioReservas = inject(ServicioReservas);
  private readonly servicioSalas = inject(ServicioSalas);
  private readonly mensajes = inject(MessageService);
  private readonly confirmacion = inject(ConfirmationService);

  protected readonly reservas = signal<Reserva[]>([]);
  protected readonly salas = signal<Sala[]>([]);
  protected readonly total = signal(0);
  protected readonly cargando = signal(false);
  protected readonly guardando = signal(false);

  protected readonly estados: { etiqueta: string; valor: EstadoReserva }[] = [
    { etiqueta: 'Activas', valor: 'ACTIVA' },
    { etiqueta: 'Canceladas', valor: 'CANCELADA' },
  ];

  protected filtroSala: string | null = null;
  protected filtroFecha: Date | null = null;
  protected filtroEstado: EstadoReserva | null = null;

  protected tamano = 10;
  protected pagina = 0;
  protected dialogoVisible = false;
  protected nueva = {
    salaId: null as string | null,
    nombreResponsable: '',
    correoResponsable: '',
    inicio: null as Date | null,
    fin: null as Date | null,
  };

  constructor() {
    this.servicioSalas.listar(0, 100).subscribe({
      next: (resultado) => this.salas.set(resultado.contenido),
    });
    this.buscar();
  }

  protected buscar(): void {
    this.cargar(0);
  }

  protected cargar(pagina: number): void {
    this.pagina = pagina;
    this.cargando.set(true);

    this.servicioReservas
      .buscar({
        salaId: this.filtroSala ?? undefined,
        fecha: this.filtroFecha ? aFechaLocal(this.filtroFecha) : undefined,
        estado: this.filtroEstado ?? undefined,
        pagina,
        tamano: this.tamano,
      })
      .subscribe({
        next: (resultado) => {
          this.reservas.set(resultado.contenido);
          this.total.set(resultado.totalElementos);
          this.cargando.set(false);
        },
        error: (error: HttpErrorResponse) => {
          this.cargando.set(false);
          this.mensajes.add({
            severity: 'error',
            summary: 'No se pudieron cargar las reservas',
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
    const inicio = new Date();
    inicio.setMinutes(0, 0, 0);
    inicio.setHours(inicio.getHours() + 1);
    const fin = new Date(inicio.getTime() + 60 * 60 * 1000);

    this.nueva = {
      salaId: this.filtroSala,
      nombreResponsable: '',
      correoResponsable: '',
      inicio,
      fin,
    };
    this.dialogoVisible = true;
  }

  protected crear(): void {
    if (!this.nueva.salaId || !this.nueva.inicio || !this.nueva.fin) {
      this.mensajes.add({
        severity: 'warn',
        summary: 'Faltan datos',
        detail: 'Elige la sala y el horario de la reserva.',
      });
      return;
    }

    this.guardando.set(true);
    this.servicioReservas
      .crear({
        salaId: this.nueva.salaId,
        nombreResponsable: this.nueva.nombreResponsable.trim(),
        correoResponsable: this.nueva.correoResponsable.trim(),
        inicio: this.nueva.inicio.toISOString(),
        fin: this.nueva.fin.toISOString(),
      })
      .subscribe({
        next: () => {
          this.guardando.set(false);
          this.dialogoVisible = false;
          this.mensajes.add({ severity: 'success', summary: 'Reserva creada' });
          this.buscar();
        },
        error: (error: HttpErrorResponse) => {
          this.guardando.set(false);
          this.mensajes.add({
            severity: 'error',
            summary: 'No se pudo crear la reserva',
            detail: mensajeDeError(error),
          });
        },
      });
  }

  protected confirmarCancelacion(reserva: Reserva): void {
    this.confirmacion.confirm({
      header: 'Cancelar reserva',
      message: `¿Cancelar ${reserva.sala.nombre} de ${this.formatear(reserva.inicio)} a ${this.formatear(reserva.fin)}?`,
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí, cancelar',
      rejectLabel: 'Volver',
      accept: () => this.cancelar(reserva.id),
    });
  }

  private cancelar(id: string): void {
    this.servicioReservas.cancelar(id).subscribe({
      next: () => {
        this.mensajes.add({ severity: 'success', summary: 'Reserva cancelada' });
        this.buscar();
      },
      error: (error: HttpErrorResponse) => {
        this.mensajes.add({
          severity: 'error',
          summary: 'No se pudo cancelar la reserva',
          detail: mensajeDeError(error),
        });
      },
    });
  }

  private formatear(iso: string): string {
    return new Date(iso).toLocaleString('es-BO', { dateStyle: 'short', timeStyle: 'short' });
  }
}
