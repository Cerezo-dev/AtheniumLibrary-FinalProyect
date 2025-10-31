package pe.edu.upeu.athenium.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.athenium.components.StageManager;
import pe.edu.upeu.athenium.components.Toast;
import pe.edu.upeu.athenium.dto.MenuMenuItenTO;
import pe.edu.upeu.athenium.dto.SessionManager;
import pe.edu.upeu.athenium.service.IMenuMenuItemDao;
import pe.edu.upeu.athenium.service.NavigationService;
import pe.edu.upeu.athenium.utils.UtilsX;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;

@Controller
public class DashboardController {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private NavigationService navigationService;
    Preferences userPrefs = Preferences.userRoot();
    UtilsX util = new UtilsX();
    Properties myresources = new Properties();
    @Autowired
    IMenuMenuItemDao mmiDao;
    @FXML
    private TabPane tabPaneFx;
    List<MenuMenuItenTO> lista;
    @FXML
    private BorderPane bp;
    Stage stage;

    // New sidebar icon fx:ids
    @FXML
    private javafx.scene.Node iconBars;
    @FXML
    private javafx.scene.Node iconHome;
    @FXML
    private javafx.scene.Node iconAnalytics;
    @FXML
    private javafx.scene.Node iconGallery;
    @FXML
    private javafx.scene.Node iconSettings;
    @FXML
    private javafx.scene.Node iconAdd;

    @FXML
    public void initialize() {
        try {
            System.out.println("[Dashboard] initialize() invoked");
            // Asegura obtener el Stage solo cuando la Scene esté disponible
            tabPaneFx.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    Platform.runLater(() -> {
                        stage = (Stage) newScene.getWindow();
                        StageManager.setPrimaryStage(stage);
                        System.out.println("[Dashboard] El título del stage es: " + (stage != null ? stage.getTitle() : "Stage nulo"));
                    });
                }
            });

            // Caso en que la Scene ya esté presente (ejecución temprana)
            if (tabPaneFx.getScene() != null) {
                Platform.runLater(() -> {
                    stage = (Stage) tabPaneFx.getScene().getWindow();
                    StageManager.setPrimaryStage(stage);
                    System.out.println("[Dashboard] El título del stage es: " + (stage != null ? stage.getTitle() : "Stage nulo"));
                });
            }

            // Register navigation callback
            if (navigationService != null) {
                navigationService.registerNavigateCallback((view, data) -> {
                    Platform.runLater(() -> {
                        System.out.println("[Dashboard] navigate request: " + view + " data=" + data);
                        switch (String.valueOf(view)) {
                            case "home": abrirPaginaEnContenido("/view/mHome.fxml"); break;
                            case "book_detail": {
                                try {
                                    System.out.println("[Dashboard] Cargando book_detail FXML");
                                    java.net.URL res = getClass().getResource("/view/page_book_detail.fxml");
                                    System.out.println("[Dashboard] resource for page_book_detail: " + res);
                                    if (res == null) {
                                        System.out.println("[Dashboard] ERROR: resource /view/page_book_detail.fxml not found");
                                        return;
                                    }
                                    FXMLLoader loader = new FXMLLoader(res);
                                    if (context != null) loader.setControllerFactory(context::getBean); else System.out.println("[Dashboard] ADVERTENCIA: ApplicationContext es null; cargando controlador sin inyección Spring");
                                    Parent content = loader.load();
                                    // set id in controller if provided
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
                                                // Try Long method first
                                                ctrl.getClass().getMethod("setBookId", Long.class).invoke(ctrl, idLong);
                                            } catch (NoSuchMethodException nsme) {
                                                try { ctrl.getClass().getMethod("setBookId", int.class).invoke(ctrl, idLong.intValue()); } catch (Exception ex) { ex.printStackTrace(); }
                                            } catch (Exception ex) { ex.printStackTrace(); }
                                        }
                                    }
                                    if (bp != null) bp.setCenter(content); else System.out.println("[Dashboard] ERROR: bp (BorderPane) es null; no se puede setCenter");
                                } catch (IOException ex) {
                                    System.out.println("[Dashboard] Error loading book_detail: " + ex);
                                    ex.printStackTrace();
                                }
                                break;
                            }
                            case "dashboard": abrirPaginaEnContenido("/view/mHome.fxml"); break;
                            case "admin": abrirPaginaEnContenido("/view/page_add.fxml"); break;
                            default: abrirPaginaEnContenido("/view/mHome.fxml"); break;
                        }
                    });
                });
            } else {
                System.out.println("[Dashboard] ADVERTENCIA: navigationService es null; no se registrará callback de navegación.");
            }

            // No construimos más un MenuBar con MenuItems: navegación solo por sidebar
            graficarMenus();
            // Asegurarse de no usar tabPaneFx antes de que esté en la escena, pero centrarlo aquí está bien
            bp.setCenter(tabPaneFx);

            // Abrir la página inicial en el centro
            abrirPaginaEnContenido("/view/mHome.fxml");
        } catch (Exception e) {
            System.out.println("[Dashboard] Exception in initialize: " + e);
            e.printStackTrace();
        }
    }

    // handlers for sidebar icons
    public void onIconHomeClicked(MouseEvent e) {
        abrirPaginaEnContenido("/view/mHome.fxml");
    }
    public void onIconAnalyticsClicked(MouseEvent e) {
        abrirPaginaEnContenido("/view/page_analytics.fxml");
    }
    public void onIconGalleryClicked(MouseEvent e) {
        abrirPaginaEnContenido("/view/page_gallery.fxml");
    }
    public void onIconSettingsClicked(MouseEvent e) {
        abrirPaginaEnContenido("/view/page_settings.fxml");
    }
    public void onIconAddClicked(MouseEvent e) {
        abrirPaginaEnContenido("/view/page_add.fxml");
    }

    // helper para mostrar notificaciones usando Toast
    public void showNotification(String message, int durationMillis) {
        Stage st = StageManager.getPrimaryStage();
        if (st != null) {
            Toast.showToast(st, message, durationMillis, st.getWidth() / 2, st.getHeight() / 2);
        } else {
            System.out.println("NOTIF: " + message);
        }
    }


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

    private List<MenuMenuItenTO> listaAccesos() {
        if (mmiDao == null) {
            System.out.println("[Dashboard] listaAccesos: mmiDao es null, devolviendo lista vacía");
            return java.util.Collections.emptyList();
        }
        myresources = util.detectLanguage(userPrefs.get("IDIOMAX", "es"));
        return mmiDao.listaAccesos(SessionManager.getInstance().getUserPerfil(), myresources);
    }

    private void graficarMenus() {
        // Evitar ejecutar lógica que requiere beans inyectados si no están presentes
        if (mmiDao == null) {
            System.out.println("[Dashboard] graficarMenus: mmiDao es null, omitiendo construcción de menús");
            return;
        }
        // Solo obtenemos la lista si necesitas control interno, pero no creamos MenuBar
        lista = listaAccesos();
        // Si quieres, puedes usar `lista` para mostrar badges o permisos, pero la navegación
        // principal ahora proviene exclusivamente de los icons del sidebar.
        System.out.println("Menus cargados (no renderizados): " + (lista != null ? lista.size() : 0));
        // Asegurar que no haya un MenuBar en la parte superior
        if (bp != null) bp.setTop(null);
    }

    // new helper to load simple content pages into the center while keeping sidebar
    private void abrirPaginaEnContenido(String fxmlPath) {
        System.out.println("[Dashboard] abrirPaginaEnContenido: " + fxmlPath + " -> resource: " + getClass().getResource(fxmlPath));
        try {
            java.net.URL res = getClass().getResource(fxmlPath);
            if (res == null) {
                System.out.println("[Dashboard] ERROR: recurso no encontrado: " + fxmlPath);
                // show a toast if stage available
                Stage st = StageManager.getPrimaryStage();
                if (st != null) Toast.showToast(st, "Página no encontrada: " + fxmlPath, 3000, st.getWidth()/2, st.getHeight()/2);
                return;
            }
            FXMLLoader loader = new FXMLLoader(res);
            if (context != null) loader.setControllerFactory(context::getBean); else System.out.println("[Dashboard] ADVERTENCIA: ApplicationContext es null; cargando página sin controllerFactory");
            Parent content = loader.load();
            if (bp != null) bp.setCenter(content); else System.out.println("[Dashboard] ERROR: BorderPane bp es null, no se puede asignar contenido");
         } catch (IOException ex) {
             System.out.println("[Dashboard] Error al cargar la página: " + fxmlPath + " -> " + ex);
             ex.printStackTrace();
         }
     }


}
