# Secure Tasks API

Proyecto Spring Boot con:
- usuarios y tareas guardados en base de datos H2
- validación de email, contraseña y título de tarea
- contraseñas almacenadas con hash BCrypt
- tests automáticos con MockMvc

## Endpoints

### Healthcheck
- `GET /health`

### Auth
- `POST /auth/register`
- `POST /auth/login`

### Tasks
- `GET /tasks`
- `POST /tasks`

## Ejemplos JSON

### Registro
```json
{
  "email": "celia@email.com",
  "password": "Password1!"
}
```

### Login
```json
{
  "email": "celia@email.com",
  "password": "Password1!"
}
```

### Crear tarea
```json
{
  "title": "Preparar memoria final"
}
```

## Ejecución

### 1. Ejecutar tests
```bash
mvn test
```

### 2. Arrancar la aplicación
```bash
mvn spring-boot:run
```

### 3. Probar la API
- Health: `http://localhost:8080/health`
- Consola H2: `http://localhost:8080/h2-console`

Datos de conexión H2:
- JDBC URL: `jdbc:h2:file:./data/securetasksdb`
- User: `sa`
- Password: dejar vacío

## Qué cambia respecto al proyecto original

Antes:
- usuarios y tareas en listas en memoria
- contraseñas en texto plano
- sin persistencia al reiniciar

Ahora:
- persistencia en base de datos
- contraseñas con hash
- validación automática con Bean Validation
- tests de validación y persistencia
