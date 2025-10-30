# Athenium — Ecosistema de Gestión Bibliográfica Digital (EGBD)


## Disclaimer: este es un readme temporal generado por copilot para el proyecto Athenium.
## Resumen corto
Athenium es una aplicación Java (Spring Boot + JavaFX) diseñada para gestionar recursos bibliográficos de una universidad: catálogo, ejemplares físicos, préstamos, reservas, usuarios y roles. El objetivo principal del proyecto es ofrecer una plataforma que facilite las operaciones de la biblioteca y que evolucione hacia "Athenium 2.0": un asistente proactivo que ayude al descubrimiento del conocimiento.

---

## Índice
- Visión general del proyecto
- Arquitectura y tecnologías
- Estructura del repositorio
- Entidades principales (modelo de dominio)
- Flujos principales (casos de uso críticos)
- Base de datos y migraciones
- Seguridad de contraseñas y por qué BCrypt
- Desarrollo local: cómo correr el proyecto
- Notas sobre SQLite vs MySQL (cambio futuro)
- Problemas comunes y soluciones rápidas
- Qué falta (Backlog prioritario / Plus)
- Cómo contribuir

---

## Visión general del proyecto
Athenium nace como una aplicación para digitalizar y automatizar tareas rutinarias de una biblioteca universitaria (préstamos, devoluciones, reservas, gestión de inventario, control de usuarios). A mediano plazo, se planea convertirlo en un "ecosistema" que incluya recomendaciones, búsqueda avanzada y notificaciones multicanal.

Valor para el usuario:
- Estudiantes y docentes podrán encontrar, reservar y pedir préstamos de forma sencilla.
- Bibliotecarios tendrán herramientas para administrar inventarios y validar operaciones.
- Administradores obtendrán reportes y métricas para la toma de decisiones.

---

## Arquitectura y tecnologías
- Backend: Spring Boot (Spring Data JPA, Spring Security)
- Persistencia: SQLite en desarrollo (archivo `data/athenium.db`), planeado soporte para BD relacionales (MySQL/Postgres)
- UI: JavaFX (FXML) como cliente de escritorio (la app arranca como una aplicación JavaFX que también instancia contexto Spring)
- ORM: Hibernate (con dialecto SQLite vía `hibernate-community-dialects`)
- Build: Maven

Motivación:
- JavaFX ofrece UI rica para escritorio y facilita prototipado del cliente.
- Spring Boot facilita estructura modular, inyección de dependencias y manejo de JPA.

---

## Estructura del repositorio (alto nivel)
Resumen de carpetas importantes:
- `src/main/java/pe/edu/upeu/athenium`: código fuente principal
  - `controller`: controladores JavaFX/REST
  - `service`: lógica de negocio
  - `repository`: interfaces Spring Data JPA
  - `model`: entidades JPA (Usuario, Libro, Ejemplar, Reserva, Perfil, etc.)
  - `config` y `security`: configuración de Spring y seguridad
  - `components`: utilidades UI (StageManager, Toast, etc.)
- `src/main/resources`:
  - `application.properties`: configuración (DB, JPA, logging)
  - `view/`: archivos FXML (UI)
  - `css/`, `img/`: recursos para la UI
- `data/athenium.db`: base SQLite (archivo para pruebas locales)
- `pom.xml`: dependencias y plugins Maven

---

## Entidades principales (modelo de dominio)
Explicación conceptual de las entidades más relevantes:

- Usuario
  - Representa a cualquier actor con cuenta: estudiante, docente, bibliotecario, administrador.
  - Atributos clave: `id`, `email`, `nombre`, `apellido`, `password` (hash), `estado`, `perfil`.
  - `perfil` permite RBAC (roles y permisos).

- Perfil
  - Define el rol del usuario en el sistema: `ESTUDIANTE`, `DOCENTE`, `BIBLIOTECARIO`, `ADMINISTRADOR`.
  - Usado para autorizar acciones y mostrar interfaces específicas.

- Libro
  - Representa la obra o título (metadatos): `titulo`, `autor`, `isbn`, `anioPublicacion`, `genero`.
  - No representa stock — es la entidad conceptual.

- Ejemplar
  - Copia física de un `Libro`. A cada ejemplar se le asigna un `codigoEjemplar` único.
  - Tiene `estado` (`DISPONIBLE`, `PRESTADO`, `RESERVADO`, `EN_REPARACION`) y `ubicacion`.
  - Relación: muchos `Ejemplar` -> 1 `Libro`.

- Reserva
  - Conecta `Usuario` con un `Ejemplar` para reservarlo. Tiene `fechaReserva`, `vencimiento`, `estado`.

- Prestamo (expected)
  - Similar a `Reserva` pero representa el acto de prestar un ejemplar. Debe ser implementada si no existe aún.

Notas:
- La diferenciación entre `Libro` y `Ejemplar` es importante para gestionar stock y disponibilidad en tiempo real.
- Para control de concurrencia (reservas/préstamos simultáneos) se recomienda usar `@Version` (optimistic locking) o transacciones con aislamiento adecuado.

---

## Flujos principales (user journeys)
1. Buscar libro
   - Usuario escribe en la barra de búsqueda -> backend busca en catálogo por título/autor/isbn -> muestra listado con estados de ejemplares.

2. Reservar libro
   - Usuario seleccionado un `Ejemplar` -> sistema valida disponibilidad -> crea `Reserva` (estado PENDIENTE/CONFIRMADA) -> marca `Ejemplar` como `RESERVADO`.
   - Control de concurrencia: si dos usuarios intentan reservar el último ejemplar, solo el primero (por commit DB / lock) debe triunfar.

3. Prestar / Devolver
   - Bibliotecario valida y registra el préstamo (crea `Prestamo`, cambia estado del ejemplar a `PRESTADO`).
   - Al devolver, se cierra `Prestamo` y se actualiza estado del `Ejemplar`.

4. Registro y login
   - Registro crea un `Usuario` con `Perfil` predeterminado (p. ej. ESTUDIANTE) y almacena `password` como hash BCrypt.
   - Login verifica hash con `PasswordEncoder` de Spring Security.

---

## Base de datos y migraciones
- Actualmente el proyecto usa SQLite y un archivo `data/athenium.db` para pruebas.
- `application.properties` apunta a `jdbc:sqlite:./data/athenium.db` y `spring.jpa.hibernate.ddl-auto=update`.
- Para producción es recomendable usar migraciones controladas con Flyway o Liquibase y una BD tipo Postgres/MySQL.

Consejo para migraciones:
- Mantener scripts SQL en `src/main/resources/db/migration` y activar Flyway en `application.properties` cuando se quiera que controle las migraciones.

---

## Seguridad de contraseñas — por qué BCrypt
- Las contraseñas deben almacenarse solo como hashes. BCrypt es una opción segura porque incorpora salt y es adaptativo (puedes aumentar el work factor).
- En este proyecto, si las consultas nativas usan `WHERE u.password = :password` (comparando texto plano), el login fallará al usar hashes. En lugar de filtrar por password en SQL, debe buscarse por email y luego verificar la contraseña con `PasswordEncoder.matches(raw, encoded)`.

Ejemplo correcto de login en `UsuarioRepository`/servicio:
1. `Usuario u = usuarioRepository.buscarUsuario(email);`  // busca por email
2. `if (u != null && passwordEncoder.matches(rawPassword, u.getPassword())) { /* login ok */ }`

Problema observado en el proyecto: hay una consulta nativa `loginUsuario` que compara la contraseña en SQL. Eso solo funciona si guardas contraseña en texto plano (poco seguro). Cambiar:
```java
@Query(value = "SELECT u.* FROM athenium_usuario u WHERE u.email = :email AND u.password = :password", nativeQuery = true)
Usuario loginUsuario(@Param("email") String email, @Param("password") String password);
```
por la estrategia segura descrita arriba.

---

## Desarrollo local: cómo ejecutar
Requisitos:
- JDK 17+ (propiedad `java.version` está en 21 en `pom.xml`, puedes usar 17 si lo prefieres).
- Maven 3.8+

Comandos básicos:

```bash
# Construir
mvn clean package -DskipTests

# Ejecutar la app (modo Spring Boot) — desde la raíz del proyecto
mvn spring-boot:run
```

Para ejecutar desde IDE, importa como proyecto Maven y ejecuta la clase `RunJavaFx` o `AtheniumApplication` según cómo esté organizada la inicialización de JavaFX+Spring.

Si usas SQLite asegúrate de que `data/athenium.db` sea accesible y el driver `sqlite-jdbc` esté en el classpath (ya está en `pom.xml`).

---

## Notas sobre SQLite vs MySQL/Postgres
- SQLite es excelente para desarrollo local y prototipos porque no requiere servidor.
- Desventajas para producción: concurrencia limitada, motores o consultas específicas (por ejemplo, Hibernate puede intentar usar `information_schema` y fallar con queries adaptadas a MySQL/MariaDB). Si migras a MySQL/Postgres en producción:
  - Actualiza `spring.datasource.url`, `driver-class-name`, y `spring.jpa.database-platform` en `application.properties`.
  - Ajusta los dialectos Hibernate y ejecuta migraciones con Flyway.
  - Prueba consultas nativas; algunas consultas que funcionan en SQLite pueden requerir cambios en SQL nativo.

En resumen: usar SQLite ahora no debería complicar el cambio posterior, pero hay que tener cuidado con las consultas nativas y el dialecto.

---

## Problemas comunes detectados en este repositorio y soluciones rápidas
1. "Not a managed type" para entidad: revisar que la clase esté en el paquete escaneado por Spring Boot (paquete base en `@SpringBootApplication`).
2. `Encoded password does not look like BCrypt`: ocurre cuando el `PasswordEncoder` recibe un hash que no tiene formato BCrypt. Solución: almacenar contraseñas con BCrypt y verificar con `matches()` en el servicio.
3. Spring Data query derivada que falla (PropertyReferenceException): los nombres de método deben coincidir con propiedades y caminos del modelo. Ejemplo errado: `findByUsuarioIdUsuario(Long)` — Spring interpreta `usuario.idUsuario` y falla si la estructura no coincide. Use `findByUsuarioId(Long id)` o `findByUsuario(User usuario)` según la propiedad.
4. Consultas nativas para `login` que comparan password: cambiar a buscar por email y comparar hashes en Java.

---

## Qué falta / backlog prioritario (Plus)
Se prioriza lo que hace que el proyecto sea profesional y seguro:

1. Seguridad crítica:
   - Quitar consultas que comparen password en SQL. Implementar login usando `PasswordEncoder.matches`.
   - Implementar bloqueo por intentos fallidos (5 intentos) y notificaciones.
2. Concurrencia y fiabilidad:
   - Añadir control de concurrencia para reservas/préstamos (`@Version` o estrategias DB).
   - Integrar backups (scripts o servicio) programados cada 24h.
3. UX / búsqueda:
   - Implementar motor de búsqueda tolerante a errores (ElasticSearch o Algolia) o biblioteca de fuzzy search.
4. Notificaciones multicanal (email + push)
5. Tests automáticos y CI: pipeline con build, tests unitarios e integración.
6. Documentación y UML: diagramas de clases y secuencia para el caso "Reservar libro".
7. Migración a BD de producción: pruebas con MySQL/Postgres y Flyway.

---

## Issue
[EPIC] Backlog Prioritario — Seguridad, Concurrencia, Búsqueda y Notificaciones (Athenium 2.0)
```

Etiquetas recomendadas: `epic`, `prioridad-alta`, `security`, `backend`, `infra`

Cuerpo del issue (copy/paste):

```
Resumen
------
Este epic agrupa las tareas prioritarias necesarias para llevar Athenium a un estado robusto y seguro, listo para pruebas de integración y despliegue. Se centra en seguridad de credenciales, consistencia y concurrencia de reservas/préstamos, un motor de búsqueda tolerante a errores y la infraestructura de notificaciones.

Objetivos
--------
- Eliminar riesgos de seguridad (no almacenar ni comparar contraseñas en texto plano).
- Garantizar integridad y consistencia en operaciones críticas (reservas, préstamos) bajo concurrencia.
- Mejorar experiencia de búsqueda para usuarios finales (fuzzy search, filtros).
- Habilitar notificaciones multicanal (email, push) para recordatorios.

Checklist (tareado por sub-issues)
----------------------------------
- [ ] Seguridad: Refactor login y repositorios para usar `PasswordEncoder.matches` y eliminar queries que comparen `password` en SQL.
- [ ] Seguridad: Implementar bloqueo por intentos fallidos (5 intentos) y notificación al usuario (email) al bloquear.
- [ ] Concurrencia: Añadir control de concurrencia a `Reserva`/`Prestamo` (optimistic locking con `@Version` o estrategia DB) y tests de carga simulando contención en un mismo ejemplar.
- [ ] Backups: Crear scripts y/o configuración para backups automáticos de la BD (cada 24h) y documentar el procedimiento de restore.
- [ ] Búsqueda: Integrar motor de búsqueda (ElasticSearch o alternativa) o biblioteca de fuzzy-search y crear endpoints de búsqueda avanzada con filtros.
- [ ] Notificaciones: Implementar envío de emails y notificaciones push (p.ej. Firebase) para vencimientos y confirmaciones.
- [ ] Tests: Añadir pruebas de integración para flujos críticos (reserva, préstamo, login) y configurar CI para ejecutar tests.
- [ ] Infra: Probar migración a Postgres/MySQL con Flyway y documentar pasos.

Criterios de aceptación
----------------------
- Login ya no compara contraseñas en SQL; todas las contraseñas en BD son hashes BCrypt.
- Simulación de 50 usuarios concurrentes intentando reservar el mismo ejemplar: sólo 1 reserva confirmada; los demás reciben "no disponible".
- Búsqueda devuelve resultados relevantes con tolerancia a errores tipográficos (ej. "Calculo diferenial" → "Cálculo Diferencial").
- Backups automáticos demostrados con restore en entorno de staging.

Sub-issues propuestos
---------------------
Crear un issue por cada checkbox en la checklist anterior, asignar `owner` y estimación (puntos o horas). Ejemplo de sub-issue: "Refactor login para BCrypt y eliminar consulta nativa de password".

Notas técnicas
--------------
- Priorizar no romper la base actual de datos SQLite; las pruebas de integración pueden ejecutarse con H2 o Postgres en contenedores.
- Para la concurrencia, si usamos SQLite en dev, tener en cuenta sus limitaciones; realizar pruebas con Postgres/MySQL para resultados realistas.

Asignación inicial recomendada
-----------------------------
- Owner: @equipo-backend
- Reviewers: @equipo-security, @equipo-infra
- Labels: `epic`, `security`, `high-priority`, `needs-discussion`

```


---
