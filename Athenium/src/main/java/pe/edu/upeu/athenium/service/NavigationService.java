package pe.edu.upeu.athenium.service;

import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;

@Service
public class NavigationService {

    // callback que el DashboardController registrará para que otros controladores puedan pedir navegación
    private BiConsumer<String, Object> navigateCallback;

    public void registerNavigateCallback(BiConsumer<String, Object> cb) {
        this.navigateCallback = cb;
    }

    public void navigate(String view, Object data) {
        if (navigateCallback != null) {
            navigateCallback.accept(view, data);
        } else {
            System.out.println("Navigation callback no registrado: " + view);
        }
    }

}

