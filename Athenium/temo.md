# Temo — Explicación muy corta y simple

Este archivo explica, de forma muy simple y lineal, los elementos principales del proyecto Athenium. Está pensado para que cualquiera (incluso un niño) pueda entenderlo.

---

## Usuario
- ¿Qué es? Una persona que usa la app (estudiante, docente, bibliotecario, admin).
- ¿Qué puede hacer ahora? Registrarse, iniciar sesión (login) y usar funciones según su rol.
- ¿Cómo funciona el registro? El usuario escribe su nombre, email y contraseña.
- ¿Qué pasa con la contraseña? Nunca se guarda en texto plano. Antes de guardar, la contraseña se convierte en un "código difícil" (hash) usando BCrypt. Eso protege la contraseña si alguien mira la base de datos.
- ¿Cómo inicia sesión? El usuario escribe su email y contraseña. El sistema busca al usuario por email y compara la contraseña escrita con el hash usando BCrypt.matches. Si coincide, entra.
- Futuro: bloqueo tras varios intentos fallidos, recuperación por email y autenticación en dos pasos (2FA).

---

## Perfil
- ¿Qué es? El papel o rol que tiene un usuario (por ejemplo: Estudiante, Docente, Bibliotecario, Administrador).
- ¿Para qué sirve? Limita o permite acciones: sólo bibliotecarios pueden validar devoluciones, administradores ven reportes, etc.
- Futuro: permisos más finos (ej.: solo algunos bibliotecarios pueden eliminar registros).

---

## Libro
- ¿Qué es? La información del libro: título, autor, ISBN, año. Es el concepto del libro.
- ¿Para qué sirve? Mostrar datos al usuario y buscar en el catálogo.
- Futuro: metadatos enriquecidos (portada, descripciones, recomendaciones).

---

## Ejemplar
- ¿Qué es? Una copia física de un libro (cada ejemplar tiene su código y ubicación).
- Estados: Disponible, Prestado, Reservado, En reparación.
- ¿Cómo se usa? Al reservar o prestar, cambiamos el estado del ejemplar.
- Futuro: escaneo por lector (QR/Barcode) para agilizar préstamos y devoluciones.

---

## Reserva
- ¿Qué es? Cuando un usuario pide apartar un ejemplar para recogerlo después.
- ¿Cómo funciona ahora? Se crea un registro que marca el ejemplar como "Reservado".
- Futuro: notificaciones automáticas (email/SMS) cuando el ejemplar esté listo.

---

## Préstamo
- ¿Qué es? Cuando se presta un ejemplar a un usuario (se registra quién lo tiene y hasta cuándo).
- ¿Cómo funciona ahora? El sistema crea una entrada de préstamo y actualiza el estado del ejemplar a "Prestado".
- Futuro: reglas de renovación, multas automáticas y recordatorios.

---

## Sesión (SessionManager)
- ¿Qué hace? Guarda datos de la sesión del usuario en memoria (id, nombre, perfil) mientras la app está abierta.
- ¿Por qué es útil? Para saber qué usuario está usando la app y qué permisos tiene.

---

## Navegación y UI (Dashboard / Sidebar)
- ¿Cómo se muestra todo? Hay un `Dashboard` con una barra lateral (icons/VBoxes) y un contenido central que cambia según la opción seleccionada.
- ¿Cómo se cambian páginas? El `DashboardController` carga archivos FXML dentro del centro (manteniendo la barra lateral fija).
- Futuro: que los íconos sean botones funcionales que abran páginas completas con su propia barra lateral.

---

## Seguridad de contraseñas — en palabras simples
1. Usuario escribe contraseña.
2. Antes de guardar, la contraseña se transforma con BCrypt (es como ponerla en una caja cerrada con llave irreversible).
3. En login, no se "deshace" la caja; en su lugar se comprueba si la caja creada desde lo que el usuario escribe coincide con la que está guardada.
4. Resultado: nadie puede leer la contraseña original desde la base de datos.


---

## Advertencias importantes (corto)
- Nunca comparar contraseñas en SQL. Buscar por email y comparar el hash en Java. 💡
- Algunos FXML usan iconos externos (FontAwesome). Si falta la librería, la pantalla no carga: añadir la dependencia o usar iconos simples. ⚠️
- Evitar usar Map.of(...) con valores null en constructores de beans: causa NPE al iniciar Spring. ⚠️
- Si hay dos repositorios con el mismo nombre (bean duplicado), Spring falla. Renombrar o limitar paquetes escaneados. ⚠️

---

## Qué se implementará a futuro (muy corto)
- Notificaciones automáticas (email/push) para reservas y vencimientos. 🔔
- Renovaciones y multas automáticas. 💳
- Búsqueda tolerante a errores (fuzzy) — encuentra libros aunque se escriba mal. 🔎
- Mejor control de concurrencia para evitar que dos personas reserven el último ejemplar al mismo tiempo. 🤝
- Migración a servidor de base de datos (Postgres/MySQL) para producción. 🛡️

---

## Final (una frase)
Athenium organiza personas (Usuarios), roles (Perfiles) y objetos físicos (Ejemplares) para que la biblioteca funcione mejor, con reglas claras para seguridad (BCrypt) y una interfaz visual donde cada parte tiene su lugar.

Si quieres que lo haga aún más sencillo (dibujos o pasos con iconos), lo preparo y lo agrego aquí.
