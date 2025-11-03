package pe.edu.upeu.athenium.common.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

// Importaciones de Componentes y Servicios de Athenium
import pe.edu.upeu.athenium.common.components.StageManager;
import pe.edu.upeu.athenium.common.components.Toast;
import pe.edu.upeu.athenium.common.dto.MenuMenuItenTO;
import pe.edu.upeu.athenium.common.dto.SessionManager;
import pe.edu.upeu.athenium.common.service.IMenuMenuItemDao;
import pe.edu.upeu.athenium.common.service.NavigationService;
import pe.edu.upeu.athenium.common.utils.UtilsX;
import pe.edu.upeu.athenium.usuario.entity.Usuario;
import pe.edu.upeu.athenium.usuario.service.IUsuarioService;

// Importaciones de Java
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.prefs.Preferences;

@Controller
public class mMainMenuController {

    // Campos Inyectados por Spring (Servicios)
    @Autowired
    private ApplicationContext context;
    @Autowired
    private NavigationService navigationService;
    @Autowired
    IMenuMenuItemDao mmiDao;
    @Autowired
    IUsuarioService usuarioService; //Para la parte de arriba dinamica del usuario

    // Campos FXML (Componentes de la UI - Raíz y Centro)
    @FXML private BorderPane bp;
    @FXML private TabPane tabPaneFx;

    // Campos de Utilidad y Estado
    Preferences userPrefs = Preferences.userRoot();
    UtilsX util = new UtilsX();
    Properties myresources = new Properties();
    List<MenuMenuItenTO> lista;
    Stage stage;
    private Popup rolePopup;


    // CAMPOS FXML - SECCIÓN ARRIBA (TOP)
    @FXML private Label lblHeaderTitle;
    @FXML private Label lblUserInfo;
    @FXML private Label lblDate;


    // CAMPOS FXML - SECCIÓN SIDEBAR (LEFT)

    @FXML private Circle circleUsuario;
    @FXML private Node iconBars;
    @FXML private Node iconHome;
    @FXML private Node iconAnalytics;
    @FXML private Node iconGallery;
    @FXML private Node iconSettings;
    @FXML private Node iconAdd;



    // PARTE 1: LÓGICA COMÚN E INICIALIZACIÓN


    // inicializacion y Navegación Central
    @FXML
    public void initialize() {
        try {
            // 1. Obtener el Stage de forma segura
            setupStageListener();

            // 2. Registrar el callback de navegación (si existe el servicio)
            setupNavigationCallback();

            // 3. Configurar menús (basado en lógica de DAO)
            graficarMenus();

            // 4. Cargar datos dinámicos del header (Usuario y Fecha)
            loadHeaderData();

            // 5. Cargar la vista inicial
            bp.setCenter(tabPaneFx); // Asegura que el TabPane esté en el centro
            abrirPaginaEnContenido("/view/pMenus/mHome/mHome.fxml", "Inicio");

        } catch (Exception e) {
            System.out.println("[Dashboard] Exception in initialize: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Helper para cargar FXML en el centro del BorderPane.
     * Esta es lógica de NAVEGACIÓN (UI).
     */
    private void abrirPaginaEnContenido(String fxmlPath, String titulo) {
        System.out.println("[Dashboard] abrirPaginaEnContenido: " + fxmlPath + " -> resource: " + getClass().getResource(fxmlPath));
        try {
            URL res = getClass().getResource(fxmlPath);
            if (res == null) {
                System.out.println("[Dashboard] ERROR: recurso no encontrado: " + fxmlPath);
                showNotification("Página no encontrada: " + fxmlPath, 3000);
                return;
            }

            FXMLLoader loader = new FXMLLoader(res);
            if (context != null) loader.setControllerFactory(context::getBean);
            Parent content = loader.load();
            if (bp != null) bp.setCenter(content);

            // **NUEVO: Actualizar el título del header**
            if (lblHeaderTitle != null) {
                lblHeaderTitle.setText(titulo);
            }

        } catch (IOException ex) {
            System.out.println("[Dashboard] Error al cargar la página: " + fxmlPath + " -> " + ex);
            ex.printStackTrace();
        }
    }

    /**
     * Lógica para cargar la vista de detalle del libro (separada por claridad).
     */
    private void loadBookDetail(Object data) {
        try {
            URL res = getClass().getResource("/view/page_book_detail.fxml");
            if (res == null) {
                System.out.println("[Dashboard] ERROR: resource /view/page_book_detail.fxml not found");
                return;
            }
            FXMLLoader loader = new FXMLLoader(res);
            if (context != null) loader.setControllerFactory(context::getBean);
            Parent content = loader.load();

            // Pasar el ID al controlador de detalle
            Object ctrl = loader.getController();
            if (ctrl != null && data != null) {
                Long idLong = null;
                if (data instanceof Integer) idLong = ((Integer) data).longValue();
                else if (data instanceof Long) idLong = (Long) data;
                else if (data instanceof String) {
                    try { idLong = Long.parseLong((String) data); } catch (Exception ignored) {}
                }
                if (idLong != null) {
                    try {
                        ctrl.getClass().getMethod("setBookId", Long.class).invoke(ctrl, idLong);
                    } catch (NoSuchMethodException nsme) {
                        try { ctrl.getClass().getMethod("setBookId", int.class).invoke(ctrl, idLong.intValue()); } catch (Exception ex) { ex.printStackTrace(); }
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            }
            if (bp != null) bp.setCenter(content);
        } catch (IOException ex) {
            System.out.println("[Dashboard] Error loading book_detail: " + ex);
            ex.printStackTrace();
        }
    }

    // Métodos de Configuración y Helpers (Comunes)
    private void setupStageListener() {
        tabPaneFx.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    stage = (Stage) newScene.getWindow();
                    StageManager.setPrimaryStage(stage);
                    // System.out.println("[Dashboard] Stage obtenido.");
                });
            }
        });

        if (tabPaneFx.getScene() != null) {
            Platform.runLater(() -> {
                stage = (Stage) tabPaneFx.getScene().getWindow();
                StageManager.setPrimaryStage(stage);
                // System.out.println("[Dashboard] Stage obtenido (temprano).");
            });
        }
    }

    private void setupNavigationCallback() {
        if (navigationService == null) {
            System.out.println("[Dashboard] ADVERTENCIA: navigationService es null.");
            return;
        }

        navigationService.registerNavigateCallback((view, data) -> {
            Platform.runLater(() -> {
                System.out.println("[Dashboard] navigate request: " + view + " data=" + data);
                switch (String.valueOf(view)) {
                    case "home": abrirPaginaEnContenido("/view/pMenus/mHome/mHome.fxml", "Home"); break;
                    case "book_detail": loadBookDetail(data); break;
                    case "dashboard": abrirPaginaEnContenido("/view/pMenus/mHome/mHome.fxml", "Home"); break;
                    case "analytics": abrirPaginaEnContenido("/view/mMainMenu/MainMenu/mMainMenu.fxml", "Main"); break;
                    case "gallery": abrirPaginaEnContenido("/view/main_devolucione.fxml", "devolciones"); break;
                    case "settings": abrirPaginaEnContenido("/view/page_settings.fxml", "Configuración"); break;
                    case "admin": abrirPaginaEnContenido("/view/page_add.fxml", "a"); break;
                    default: abrirPaginaEnContenido("/view/pMenus/mHome/mHome.fxml", "e"); break;
                }
            });
        });
    }

    /**
     * Configura el estado visual del menú (Lógica de UI).
     */
    private void graficarMenus() {
        if (mmiDao == null) {
            System.out.println("[Dashboard] graficarMenus: mmiDao es null");
            return;
        }
        lista = listaAccesos();
        System.out.println("Menus cargados (no renderizados): " + (lista != null ? lista.size() : 0));
        //if (bp != null) bp.setTop(null); // Limpiar lo que esta arriba (TODO: implementar renderizado real)
    }

    /**
     * Carga la lista de accesos desde el DAO (Delegación).
     */
    private List<MenuMenuItenTO> listaAccesos() {
        if (mmiDao == null) {
            System.out.println("[Dashboard] listaAccesos: mmiDao es null");
            return Collections.emptyList();
        }
        myresources = util.detectLanguage(userPrefs.get("IDIOMAX", "es"));
        return mmiDao.listaAccesos(SessionManager.getInstance().getUserPerfil(), myresources);
    }

    /**
     * Muestra una notificación Toast (Lógica de UI).
     */
    public void showNotification(String message, int durationMillis) {
        Stage st = StageManager.getPrimaryStage();
        if (st != null) {
            Toast.showToast(st, message, durationMillis, st.getWidth() / 2, st.getHeight() / 2);
        } else {
            System.out.println("NOTIF: " + message);
        }
    }

    /**
     * Helper de conteo (Lógica de utilidad, aceptable en controller).
     */
    public int[] contarMenuMunuItem(List<MenuMenuItenTO> data) {
        int menui = 0, menuitem = 0;
        String menuN = "";
        for (MenuMenuItenTO mmi : data) {
            if (!mmi.getMenunombre().equals(menuN)) {
                menuN = mmi.getMenunombre();
                menui++;
            }
            if (!mmi.getMenuitemnombre().equals("")) {
                menuitem++;
            }
        }
        return new int[]{menui, menuitem};
    }

    // =================================================================
    // PARTE 2: LÓGICA DE LA BARRA SUPERIOR (ARRIBA)
    // =================================================================

    /**
     * Carga los datos que no cambian (Usuario y Fecha) al iniciar.
     */
    private void loadHeaderData() {
        setUserInfo();
        setCurrentDate();
    }

    /**
     * Obtiene el usuario de la sesión y actualiza el saludo.
     */
    private void setUserInfo() {
        try {
            // 1. Obtener el email del SessionManager
            String userEmail = SessionManager.getInstance().getUserName();
            if (userEmail == null || userEmail.isEmpty()) {
                lblUserInfo.setText("Hola, Invitado");
                return;
            }

            // 2. Buscar el usuario en la BD usando el servicio
            // Nota: Tu servicio usa buscarUsuario(email)
            Usuario usuario = usuarioService.buscarUsuario(userEmail);

            if (usuario != null && usuario.getNombre() != null) {
                lblUserInfo.setText("Hola, " + usuario.getNombre() + ", bienvenido de vuelta");
            } else {
                lblUserInfo.setText("Hola, " + userEmail);
            }
        } catch (Exception e) {
            lblUserInfo.setText("Hola, (Error al cargar)");
            e.printStackTrace();
        }
    }

    /**
     * Obtiene y formatea la fecha actual para Lima, Perú.
     */
    private void setCurrentDate() {
        try {
            // 1. Definir la Zona Horaria
            ZoneId zonaLima = ZoneId.of("America/Lima");

            // 2. Obtener la fecha y hora actual en esa zona
            ZonedDateTime fechaActual = ZonedDateTime.now(zonaLima);

            // 3. Formatear
            // Formato: "02 nov 2025" (Ajusta el patrón si prefieres otro)
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("dd MMM yyyy", new Locale("es", "PE"));

            lblDate.setText(fechaActual.format(formatter));

        } catch (Exception e) {
            lblDate.setText("Fecha no disponible");
            e.printStackTrace();
        }
    }

    // =================================================================
    // PARTE 3: LÓGICA DE LA BARRA LATERAL (SIDEBAR)
    // =================================================================

    // Handlers de Eventos de la UI
    @FXML
    public void onIconHomeClicked(MouseEvent e) {
        abrirPaginaEnContenido("/view/pMenus/mHome/mHome.fxml", "Inicio");
    }

    @FXML
    public void onIconAnalyticsClicked(MouseEvent e) {
        abrirPaginaEnContenido("/view/main_libro.fxml", "Gestionar Libros");
    }

    @FXML
    public void onIconGalleryClicked(MouseEvent e) {
        abrirPaginaEnContenido("/view/main_devoluciones.fxml", "Libros Devueltos");
    }

    @FXML
    public void onIconSettingsClicked(MouseEvent e) {
        abrirPaginaEnContenido("/view/page_settings.fxml", "Configuración");
    }

    @FXML
    public void onIconAddClicked(MouseEvent e) {
        abrirPaginaEnContenido("/view/page_add.fxml", "Añadir Nuevo");
    }

    @FXML
    public void onUserCircleClicked(MouseEvent e) {
        if (rolePopup == null) {
            try {
                // 1. Cargar el FXML del Pop-up
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pMenus/mMainMenu/roleSelector.fxml"));
                loader.setControllerFactory(context::getBean);
                Parent content = loader.load();

                // 2. Crear el Pop-up
                rolePopup = new Popup();
                rolePopup.getContent().add(content);

                // 3. Pasar referencia al controlador del pop-up
                RoleSimulatorController controller = loader.getController();
                if (controller != null) {
                    controller.setParentController(this);
                }

            } catch (IOException ex) {
                System.out.println("[Dashboard] Error al cargar simular_login_popup.fxml: " + ex);
                ex.printStackTrace();
                return;
            }
        }

        if (rolePopup.isShowing()) {
            rolePopup.hide();
        } else {
            // 4. Calcular y mostrar posición anclada al círculo
            Bounds bounds = circleUsuario.localToScreen(circleUsuario.getBoundsInLocal());
            rolePopup.show(
                    circleUsuario,
                    bounds.getMinX() + bounds.getWidth(),
                    bounds.getMaxY() - rolePopup.getContent().get(0).getBoundsInLocal().getHeight()
            );
        }
    }

    // Lógica de Simulación (UI/Prototipado)
    /**
     * Callback llamado por RoleSimulatorController para cambiar el perfil de sesión.
     */
    public void simularLogin(String perfil) {
        // 1. Simular el login (Lógica de estado de sesión)
        if (SessionManager.getInstance() != null) {
            SessionManager.getInstance().setUserPerfil(perfil);
        }

        // 2. Ocultar el pop-up
        if (rolePopup != null) {
            rolePopup.hide();
        }

        // 3. Recargar menús y la vista principal (Refrescar UI)
        graficarMenus();
        onIconHomeClicked(null); // Pasa 'null' ya que el MouseEvent no es necesario aquí

        showNotification("Sesión simulada como: " + perfil, 3000);
    }

    /// PARTE 4: LÓGICA DEL CONTENIDO CENTRAL (CENTER)

    // La lógica del contenido central es manejada por el método común 'abrirPaginaEnContenido',
    // que reemplaza el nodo en la región <center> del BorderPane.
    // Los controladores específicos (ej. LibroController, HomeContentController)
    // manejan la lógica interna de cada vista cargada.
}