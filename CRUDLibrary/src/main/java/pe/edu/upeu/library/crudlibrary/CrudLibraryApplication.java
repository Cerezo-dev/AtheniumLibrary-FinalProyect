package pe.edu.upeu.library.crudlibrary;

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
public class CrudLibraryApplication extends Application {

    private ConfigurableApplicationContext context;
    private Parent parent;

    public static void main(String[] args) {
        //SpringApplication.run(CrudLibraryApplication.class, args);
        launch(args);
    }
    @Override
    public void init() throws Exception {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(CrudLibraryApplication.class);
        builder.application().setWebApplicationType(WebApplicationType.NONE);
        context = builder.run(getParameters().getRaw().toArray(new String[0]));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/main_Menu.fxml"));
        loader.setControllerFactory(context::getBean);
        parent = loader.load();
    }
    @Override
    public void start(Stage stage) throws Exception {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(parent, bounds.getWidth(), bounds.getHeight()-100);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Crud Library");
        stage.show();
    }
}