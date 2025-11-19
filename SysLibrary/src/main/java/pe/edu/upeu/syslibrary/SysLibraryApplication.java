package pe.edu.upeu.syslibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SysLibraryApplication extends Application {
    private ConfigurableApplicationContext context;
    private Parent parent;

	public static void main(String[] args) {
        ///SpringApplication.run(SysLibraryApplication.class, args);
        launch(args);
	}
    @Override
    public void init() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(SysLibraryApplication.class);
        builder.application().setWebApplicationType(WebApplicationType.NONE);
        context=builder.run(getParameters().getRaw().toArray(new String[0]));
        // CORRECCIÓN CLAVE: Usar .load() para cargar el FXML
        try {
            // CORRECCIÓN CLAVE: Usar .load() para cargar el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/maingui_view.fxml")); // Usaré /view/Login.fxml como ejemplo
            loader.setControllerFactory(context::getBean);
            parent = loader.load(); // <-- Usa .load() para cargar y devolver el Parent

        } catch (Exception e) {
            // Es vital atrapar la excepción de carga del FXML para diagnosticar.
            System.err.println("FATAL ERROR AL CARGAR EL FXML:");
            e.printStackTrace();
            throw new RuntimeException("Fallo al cargar la vista FXML.", e);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        // 1. Crear la Scene y guardar la referencia en una variable local
        Scene scene = new Scene(parent, bounds.getWidth(), bounds.getHeight() - 100);

        // 2. Aplicar la hoja de estilos usando la ruta correcta
        // Usamos /css/styles.css porque está dentro de la carpeta 'css'
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        // 3. Asignar la Scene al Stage y mostrar
        stage.setScene(scene);
        stage.setTitle("SysLibrary");
        stage.show();
    }
}
