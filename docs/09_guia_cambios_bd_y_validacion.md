# Guía paso a paso: pasar de listas a base de datos y añadir validación

## 1. Qué problema tenía el proyecto original

Antes el proyecto guardaba los usuarios y las tareas en listas Java (`ArrayList`). Eso significa:
- los datos se pierden al reiniciar la aplicación
- no hay base de datos real
- la contraseña se guardaba en texto plano
- la validación era muy básica

## 2. Qué se ha cambiado

### 2.1 Dependencias nuevas en `pom.xml`
Se han añadido:
- `spring-boot-starter-data-jpa`: para guardar en base de datos
- `spring-boot-starter-validation`: para validar email, contraseña y título
- `h2`: base de datos ligera
- `spring-security-crypto`: para hashear contraseñas con BCrypt

## 3. Entidades

### 3.1 `User`
Ahora `User` lleva `@Entity`, así Spring crea una tabla `users`.
- `id`: se genera sola
- `email`: único
- `password`: se guarda hasheada

### 3.2 `Task`
Ahora `Task` también es `@Entity` y se guarda en tabla `tasks`.
- `id`: se genera sola
- `title`: texto de la tarea

## 4. Repositories

Se crean:
- `UserRepository`
- `TaskRepository`

Estas interfaces sustituyen a las listas en memoria.
Antes hacías `users.add(...)` o `tasks.add(...)`.
Ahora haces `userRepository.save(...)` o `taskRepository.save(...)`.

## 5. Validación

### 5.1 Registro y login
Se mueven a DTOs:
- `dto/RegisterRequest`
- `dto/LoginRequest`

Validaciones:
- email obligatorio
- email con formato correcto
- contraseña entre 8 y 64 caracteres
- contraseña con mayúscula, minúscula, número y símbolo permitido

### 5.2 Tareas
Se crea `dto/TaskRequest`.
Validaciones:
- título obligatorio
- longitud mínima y máxima
- solo caracteres permitidos

## 6. Protección frente a inyecciones

No existe una única validación mágica “anti inyección”, pero aquí se mejora bastante por dos motivos:

### 6.1 No se construyen consultas SQL a mano
Con JPA y repositorios, Spring genera consultas parametrizadas.
Eso es mucho más seguro que concatenar cadenas tipo:
```java
"SELECT * FROM users WHERE email = '" + email + "'"
```

### 6.2 Se restringen formatos de entrada
Con `@Pattern` y otras validaciones, se limita qué caracteres se aceptan.
Por ejemplo, el título de tarea no acepta `;` ni comillas ni otros caracteres peligrosos.

## 7. AuthController

Antes:
- usaba `List<User>`
- comparaba contraseñas en texto plano

Ahora:
- usa `UserRepository`
- comprueba si el email ya existe en base de datos
- guarda la contraseña con BCrypt
- en login compara con `passwordEncoder.matches(...)`

## 8. TaskController

Antes:
- usaba `List<Task>`

Ahora:
- usa `TaskRepository`
- guarda tareas en base de datos
- valida el título antes de guardar

## 9. Errores de validación

Se ha creado `ApiExceptionHandler` para devolver errores más claros:
- `400 Bad Request`
- mensaje general `Datos inválidos`
- detalles por campo

## 10. Configuración de la base de datos

En `application.properties`:
- se usa H2 en fichero
- los datos quedan guardados en `./data/securetasksdb`
- `ddl-auto=update` crea o actualiza tablas automáticamente
- se activa la consola H2 en `/h2-console`

## 11. Tests

Se mantienen los tests de health y auth y se añade uno de tasks.
Además los tests usan una H2 en memoria para no ensuciar la base real.

## 12. Cómo ejecutarlo

### Ejecutar tests
```bash
mvn test
```

### Arrancar proyecto
```bash
mvn spring-boot:run
```

### Abrir consola H2
URL:
`http://localhost:8080/h2-console`

Datos:
- JDBC URL: `jdbc:h2:file:./data/securetasksdb`
- User: `sa`
- Password: vacío

## 13. Qué tendrás que cambiar tú si quieres seguir ampliándolo

### Si luego quieres relacionar tareas con usuarios
Tendrías que añadir en `Task` una relación `@ManyToOne` con `User`.

### Si luego quieres seguridad real con token
Tendrías que añadir JWT y proteger endpoints con Spring Security.

## 14. Resumen rápido

Antes:
- listas
- sin persistencia
- password en claro
- validación mínima

Ahora:
- base de datos
- persistencia real
- password hasheada
- validación de campos
- mejor protección frente a entradas peligrosas
