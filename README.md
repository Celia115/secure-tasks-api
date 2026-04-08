# Secure Tasks API

## Descripción del proyecto

Secure Tasks API es una API REST desarrollada en Java con Spring Boot que permite gestionar tareas de forma sencilla.

El objetivo del proyecto es construir una base funcional aplicando conceptos básicos de desarrollo seguro (S-SDLC), incluyendo autenticación futura, control de acceso, validación de datos y logging.

Actualmente, la API permite crear y consultar tareas en memoria.


## Tecnologías utilizadas

* Java 17
* Spring Boot
* Maven
* Thunder Client / Postman (para pruebas)


## Ejecutar en local

1. Clonar el repositorio:

```bash
git clone https://github.com/Celia115/secure-tasks-api.git
```

2. Entrar en la carpeta del proyecto:

```bash
cd secure-tasks-api
```

3. Ejecutar la aplicación:

```bash
mvn spring-boot:run
```

4. Acceder en el navegador:

```text
http://localhost:8080
```


## Ejecutar tests

Ejecutar los tests con Maven:

```bash
mvn test
```


## Endpoints disponibles

### Healthcheck

* GET `/health`

Comprueba que la API está funcionando correctamente.

Ejemplo de respuesta:

```json
{
  "status": "ok"
}
```

### Obtener tareas

* GET `/tasks`

Devuelve la lista de tareas.

Ejemplo:

```json
[]
```


### Crear tarea

* POST `/tasks`

Permite crear una nueva tarea.

Ejemplo de petición:

```json
{
  "id": 1,
  "title": "Estudiar Spring"
}
```

Ejemplo de respuesta:

```json
{
  "id": 1,
  "title": "Estudiar Spring"
}
```

## Notas

* Las tareas se almacenan en memoria (no base de datos).
* Si se reinicia la aplicación, los datos se pierden.
* Este proyecto es una base inicial para aplicar buenas prácticas de seguridad en futuras fases.
