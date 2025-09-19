package pe.edu.upeu.library.crudlibrary.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Map;

@Controller
public class controllerMainMenu {

    @FXML
    private BorderPane bp;
    @FXML
    private BorderPane menui1;
    @FXML
    private TabPane tabPane;
    @FXML
    private MenuItem menuItem1, menuItem2, menuItem3, menuItem4, menuItem5, menuItem6, menuItemc;
    @Autowired
    private ApplicationContext context;

    @FXML
    public void initialize() {
        MenuItemListener mIL = new MenuItemListener();
        menuItem1.setOnAction(mIL::handle);
        menuItem2.setOnAction(mIL::handle);
        menuItem3.setOnAction(mIL::handle);
        menuItem4.setOnAction(mIL::handle);
        menuItem5.setOnAction(mIL::handle);
        menuItem6.setOnAction(mIL::handle);
        menuItemc.setOnAction(mIL::handle);
    }

    class MenuItemListener {
        Map<String, String[]> menuconfig = Map.of(
                "menuItem1", new String[]{"/FXML/main_principal.fxml", "Reg. Principal", "T"},
                "menuItem2", new String[]{"/FXML/main_prestamos.fxml", "Prestamos", "T"},
                "menuItem3", new String[]{"/FXML/main_devoluciones.fxml", "Devoluciones", "T"},
                "menuItem4", new String[]{"/FXML/main_usuarios.fxml", "Usuarios", "T"},
                "menuItem5", new String[]{"/FXML/main_libros.fxml", "Libros", "T"},
                "menuItem6", new String[]{"/FXML/main_reportes.fxml", "Reportes", "T"},
                "menuItemc", new String[]{"/FXML/login.fxml", "Salir", "S"} // Corregido aquí
        );

        public void handle(ActionEvent e) {
            String id = ((MenuItem) e.getSource()).getId();
            System.out.println("Menu seleccionado: " + id);
            if (menuconfig.containsKey(id)) {
                String[] cfg = menuconfig.get(id);
                if (cfg[2].equals("S")) {
                    Platform.exit();
                    System.exit(0);
                } else {
                    abrirTabConFXML(cfg[0], cfg[1]);
                }
            }
        }

        private void abrirTabConFXML(String fxmlPath, String tituloTab) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                loader.setControllerFactory(context::getBean); // Inyección con Spring
                Parent root = loader.load();

                ScrollPane scrollPane = new ScrollPane(root);
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);

                Tab newTab = new Tab(tituloTab, scrollPane);
                tabPane.getTabs().clear();
                tabPane.getTabs().add(newTab);

            } catch (IOException e) {
                throw new RuntimeException("Error al cargar FXML: " + fxmlPath, e);
            }
        }
    }

    class MenuListener {
        public void menuSelected(Event e) {
        }
    }
}
