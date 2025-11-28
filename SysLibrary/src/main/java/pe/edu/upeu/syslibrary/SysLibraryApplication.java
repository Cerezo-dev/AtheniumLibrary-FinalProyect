package pe.edu.upeu.syslibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import pe.edu.upeu.syslibrary.model.Perfil;
import pe.edu.upeu.syslibrary.repositorio.PerfilRepository;

@SpringBootApplication
public class SysLibraryApplication extends Application {

    private ConfigurableApplicationContext context;
    private Parent parent;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(SysLibraryApplication.class);
        builder.application().setWebApplicationType(WebApplicationType.NONE);
        context = builder.run(getParameters().getRaw().toArray(new String[0]));

        try {
            // 🔵 CAMBIO: Ahora la vista inicial es el Login de ESTUDIANTES
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/login_student.fxml"));
            loader.setControllerFactory(context::getBean);
            parent = loader.load();
        } catch (Exception e) {
            System.err.println("ERROR AL CARGAR LOGIN ESTUDIANTE:");
            e.printStackTrace();
            context.close();
            throw new RuntimeException("Fallo al cargar la vista FXML.", e);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(parent);
        try {
            if (getClass().getResource("/css/styles.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            }
        } catch (Exception e) { }

        stage.setScene(scene);
        stage.setTitle("SysLibrary - Campus Virtual"); // Título actualizado
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() {
        context.close();
        System.exit(0);
    }

    @Bean
    public CommandLineRunner initData(PerfilRepository perfilRepository) {
        return args -> {
            crearPerfilSiNoExiste(perfilRepository, "ADMINISTRADOR");
            crearPerfilSiNoExiste(perfilRepository, "BIBLIOTECARIO");
            crearPerfilSiNoExiste(perfilRepository, "ESTUDIANTE"); // Aseguramos que exista este rol
        };
    }

    private void crearPerfilSiNoExiste(PerfilRepository repo, String nombre) {
        if (repo.findByNombre(nombre) == null) {
            Perfil p = new Perfil();
            p.setNombre(nombre);
            repo.save(p);
            System.out.println("✅ Rol " + nombre + " creado automáticamente.");
        }
    }
}