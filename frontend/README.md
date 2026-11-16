# Frontend

Aplicación Angular 22 con PrimeNG para consumir la API de reserva de salas.

## Requisitos

- Node 22.22 o superior.
- La API en `http://localhost:8080` (el proxy de desarrollo se encarga de `/api`).

## Arranque

```bash
npm ci
npm start
```

La aplicación queda en http://localhost:4200. El proxy está en `proxy.conf.json` y se aplica desde `angular.json`.

## Compilación

```bash
npm run build
```

El resultado queda en `dist/frontend/browser`; en Docker lo sirve nginx con `nginx.conf`, que además proxea `/api` al contenedor de la API.

## Pantallas

- `/inicio-sesion`: formulario que guarda el token en `localStorage`.
- `/salas`: listado paginado y alta de salas.
- `/reservas`: filtros por sala, día y estado, alta de reservas y cancelación con confirmación.

Los errores se leen del campo `codigo` del Problem Details y se muestran como mensajes legibles.
