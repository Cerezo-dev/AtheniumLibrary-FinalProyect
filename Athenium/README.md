# Athenium — Documentación técnica y guía de integración

Este README describe cómo está organizado el proyecto, cómo se interconectan sus piezas (Spring Boot + JavaFX), advertencias y problemas habituales, y pasos prácticos para ejecutar, depurar y extender la aplicación.

Última actualización: 2025-10-31

---

## 1. Resumen rápido
Athenium es una aplicación de escritorio basada en JavaFX que arranca un contexto Spring Boot para acceder a servicios, repositorios y componentes (patrón híbrido UI desktop + backend embebido). La UI está construida con FXML y los controladores JavaFX pueden ser administrados por Spring cuando se carga el FXML con un `ControllerFactory`.

Objetivo de este README: explicar cómo las piezas encajan, documentar problemas conocidos y proporcionar pasos reproducibles para desarrollar y depurar.

---

## 2. Visión general de la arquitectura y componentes

- Spring Boot (contexto) — maneja servicios (`@Service`), repositorios (`Spring Data JPA`), configuración y acceso a la base de datos (SQLite en desarrollo).
- JavaFX (UI) — vistas en `src/main/resources/view/*.fxml`. Controladores JavaFX en `src/main/java/pe/edu/upeu/athenium/controller`.
- Integración Spring+JavaFX — FXML se carga con `FXMLLoader` y, cuando conviene, se usa `loader.setControllerFactory(context::getBean)` para que Spring inyecte dependencias en el controlador.
- Recursos UI — CSS en `src/main/resources/css/`, imágenes en `src/main/resources/img/`.
- Persistencia — JPA/Hibernate con SQLite (`data/athenium.db`) por defecto; repositorios en `repository`.
- Utilidades y componentes: `StageManager`, `Toast`, `NavigationService`, `SessionManager` (almacena sesión del usuario), `ILibroService` y sus implementaciones.

---

## 3. Cómo se interconectan las piezas (flujo típico)

1. Inicio de la app
   - `RunJavaFx` o `AtheniumApplication` inicia JavaFX y a su vez inicializa Spring via `SpringApplicationBuilder`.
   - Spring crea beans (servicios, repositorios, etc.).

2. Login
   - El FXML de login usa `LoginController`. Al autenticar, `LoginController` llama a `IUsuarioService` (o `IUsuarioService.loginUsuario`) para validar credenciales.
   - Al autenticarse correctamente, se guarda información en `SessionManager` y se carga `mDashboard.fxml` con `FXMLLoader`.
   - Cuando `FXMLLoader` usa `controllerFactory(context::getBean)`, los controladores dentro del dashboard obtienen inyección de Spring.

3. Navegación interna
   - `DashboardController` mantiene la estructura (Sidebar + BorderPane central) y usa `abrirPaginaEnContenido(String fxmlPath)` para cargar sub-páginas en el centro manteniendo la barra lateral.
   - Para navegación desde servicios u otros controllers se puede usar `NavigationService` que notifica al `DashboardController` para el cambio de vista.

4. Operaciones de negocio
   - Los controladores mandan peticiones a servicios (`@Service`) que a su vez usan repositorios (`Spring Data JPA`) para persistencia.

---

## 4. Banderas de desarrollo y propiedades útiles
En `src/main/resources/application.properties` puedes encontrar y/o agregar:

- `athenium.auth.enabled=false` — desactiva inicialización de `AuthService` si causa problemas de arranque (útil para debug).
- `athenium.ui.forceFallback=true` — fuerza al `LoginController` a cargar las vistas sin `controllerFactory` (modo fallback) para mostrar UI aunque la inyección Spring falle; útil para ver la UI mientras depuras beans.
- `spring.main.allow-bean-definition-overriding=true` — (opcional / temporal) permite sobrescribir beans si hay nombres duplicados; usar sólo como parche.

---

## 5. Problemas conocidos y advertencias (y cómo arreglarlos)
Aquí listo los problemas que han aparecido y la solución práctica:

1. `FontAwesomeIconView is not a valid type`
   - Causa: FXML usa `FontAwesomeIconView` pero la librería FontAwesomeFX no está en classpath o falta `<?import ...?>` en el FXML.
   - Solución rápida: agregar la import en el FXML `<?import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView?>` o cambiar a `FontIcon` con Ikonli.
   - Solución robusta: agregar dependencia Ikonli o FontAwesomeFX en `pom.xml` (recomendado Ikonli) y reconstruir.

2. Excepción al crear bean `authService` (NPE con Map.of(...))
   - Causa: uso de `Map.of` con valores null; `Map.of` no permite nulls y lanza NPE durante construcción del bean.
   - Solución: inicializar con `new HashMap<>()` y evitar `requireNonNull` con valores null. Alternativa: activar `athenium.auth.enabled=false` temporalmente y corregir constructor.

3. `BeanDefinitionOverrideException` para `prestamoRepository`
   - Causa: dos interfaces de repositorio con el mismo nombre de bean en paquetes distintos.
   - Solución: consolidar/renombrar interfaces o restringir `@EnableJpaRepositories(basePackages=...)` para evitar escaneo doble. Como parche temporal, `spring.main.allow-bean-definition-overriding=true`.

4. Login que compara password en SQL (consulta nativa)
   - Causa: el repositorio hace `WHERE email=:email AND password=:password` — esto no funciona si usas BCrypt hashed passwords.
   - Solución: buscar usuario por email y luego usar `PasswordEncoder.matches(raw, encoded)` en servicio.

5. Problemas de JavaFX/OpenJFX al usar Maven
   - Causa: dependencias `org.openjfx:...:windows-x86_64` no encontradas en Maven central según classifier. El `os-maven-plugin` detecta classifier; si falla, la build puede no descargar.
   - Solución: ejecutar desde IDE con VM options apuntando al JavaFX SDK, o forzar classifier correcto y actualizar la configuración del `os-maven-plugin`.

6. NullPointerException en `DashboardController.abrirPaginaEnContenido`
   - Causa: `tabPaneFx` o `bp` no enlazados por FXML o controlador instanciado sin Spring.
   - Solución: comprobar `fx:id` en FXML (`tabPaneFx`, `bp`) y asegurarse de que el controlador se cree con `controllerFactory` si requiere beans; se añadieron checks null en el controlador para mitigar.

---

## 6. Cómo ejecutar localmente (recomendado: IDE)

Requisitos mínimos:
- JDK 17+
- Maven 3.8+
- Para ejecutar JavaFX desde Maven puede ser necesario tener las plataformas JavaFX (SDK) o usar el plugin `javafx-maven-plugin`.

Ejecutar en IDE (más fiable durante desarrollo):
1. Importa como proyecto Maven en IntelliJ.
2. Configura VM options si tu JDK no incluye JavaFX (ajusta la ruta):

```bash
--module-path "C:\ruta\a\javafx-sdk-21.0.2\lib" --add-modules=javafx.controls,javafx.fxml
```

3. Ejecuta `RunJavaFx` o `AtheniumApplication` desde el IDE.

Ejecutar con maven (puede fallar por JavaFX classifier):

```bash
# limpia y compila (sin tests)
mvnw.cmd clean package -DskipTests

# arrancar la app usando Spring Boot (no inicia JavaFX correctamente en algunos entornos)
mvnw.cmd spring-boot:run
```

Si `mvnw` falla por OpenJFX classifier, ejecuta desde IDE como se indicó.

---

## 7. Depuración y logs — pasos rápidos
1. Observa `logs/athenium.log` y la salida de la consola del IDE. Las líneas con prefijos `[Login]` o `[Dashboard]` fueron añadidas para diagnóstico.
2. Problemas comunes y cómo detectar:
   - FXML no encontrado: busca `resource: null` en logs y revisa rutas en `getResource("/view/...")`.
   - Error `FontAwesomeIconView`: revisa que el FXML importe la clase y que la dependencia exista en `pom.xml`.
   - Bean creation errors: mira el stacktrace completo para ver qué bean y en qué constructor ocurre la excepción.
3. Para reproducir errores de inyección: activa `athenium.ui.forceFallback=false` y observa fallos al cargar con `controllerFactory`.

---

## 8. Cómo agregar una nueva página (pasos mínimos)
1. Crear `src/main/resources/view/page_nueva.fxml` (diseño UI).
2. Crear controlador `src/main/java/.../controller/PageNuevaController.java` y anotarlo con `@Controller` si quieres inyección Spring.
3. Si el controlador usa beans, carga el FXML con `loader.setControllerFactory(context::getBean)`.
4. Para navegación: desde `DashboardController` o `NavigationService` invoca `abrirPaginaEnContenido("/view/page_nueva.fxml")`.
5. Añadir icono o botón en `mMainMenu.fxml` o `mDashboard.fxml` y enlazar el handler en `DashboardController`.

---

## 9. Recomendaciones para producción y siguientes pasos
- Migrar a Postgres/MySQL y activar `Flyway` para migraciones controladas.
- Encapsular la inicialización JavaFX/Spring para que la UI y el backend se puedan testear por separado.
- Añadir tests unitarios e integración (login, reservas, concurrencia en préstamos).
- Eliminar banderas de fallback (`athenium.ui.forceFallback`, `athenium.auth.enabled`) una vez resueltas las causas raíz.

---

## 10. Contacto y cómo contribuir
- Para proponer cambios, crea un branch, añade tests y abre un PR con descripción clara del problema y la solución.
- Si encuentras un error nuevo, pega la traza completa y los últimos logs generados (archivo `logs/athenium.log`).

---

Si quieres, aplico ahora cambios adicionales al `pom.xml` (por ejemplo añadir Ikonli) o actualizo uno de los FXML problemáticos para sustituir `FontAwesomeIconView` por una alternativa (FontIcon), y pruebo la compilación. ¿Qué prefieres que haga a continuación?
