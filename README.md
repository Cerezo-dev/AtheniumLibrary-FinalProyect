# CRUDlibrary

CRUD de un sistema de biblioteca universitario

### Descripción
Sistema de gestión de asistencia para eventos universitarios. Permite registrar participantes, eventos y controlar la asistencia.

### Tecnologías
- **Java**
- **Spring Boot**
- **Maven**

### Estructura de Carpetas

- `control/`: Controladores REST para manejar las peticiones.
- `dto/`: Objetos de transferencia de datos entre capas.
- `modelo/`: Entidades del dominio.
- `repositorio/`: Interfaces para acceso a datos (JPA).
- `servicio/`: Lógica de negocio y servicios.
- `componente/validacion/`: Validaciones personalizadas (ejemplo: DNI).
- `enums/`: Enumeraciones para tipos y estados.
- `utils/`: Utilidades varias.

### Ejecución

1. Instala dependencias con Maven:  
   `mvn clean install`
2. Ejecuta la aplicación:  
   `mvn spring-boot:run`

### Controladores Principales

- `AsistenciaController`: Gestiona la asistencia de participantes.
- `EventoController`: CRUD de eventos.
- `ParticipanteController`: CRUD de participantes.

### DTOs

- `PersonaDto`: Datos de personas.
- `AsistenciaDetalleDto`: Detalles de asistencia.
- `ComboBoxOption`: Opciones para combos en la interfaz.

### Servicios

- Interfaces y sus implementaciones para la lógica de negocio.

### Recursos

- Archivos FXML para la interfaz gráfica.
- Archivos CSS para estilos.

---

Puedes ampliar cada sección según lo que necesites documentar (por ejemplo, endpoints, ejemplos de uso, etc.).