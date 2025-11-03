package pe.edu.upeu.athenium;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AtheniumApplication extends Application {

    private ConfigurableApplicationContext applicationContext;
    private Parent root;


    public static void main(String[] args) {
        //SpringApplication.run(SysVentasApplication.class, args);
        launch(args);
    }

    @Override
    public void init() throws Exception {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(AtheniumApplication.class);
        builder.application().setWebApplicationType(WebApplicationType.NONE);
        applicationContext=builder.run(getParameters().getRaw().toArray(new String[0]));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pMenus/usr-authAccess/login.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        root = loader.load();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(root);
        // Cargar la hoja de estilos principal (styles.back)
        scene.getStylesheets().add(getClass().getResource("/css/Themes/default/styles.css").toExternalForm());
        stage.setScene(scene);

        stage.setTitle("Athenium - EGBD");

        stage.show();
    }
}