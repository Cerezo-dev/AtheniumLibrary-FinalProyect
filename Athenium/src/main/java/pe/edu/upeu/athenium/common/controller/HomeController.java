package pe.edu.upeu.athenium.common.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.athenium.common.components.Toast;
import pe.edu.upeu.athenium.common.components.StageManager;
import pe.edu.upeu.athenium.common.service.NavigationService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private NavigationService navigationService;

    @FXML
    private TextField txtSearch;
    @FXML
    private ListView<String> lstResults;
    @FXML
    private Button btnRefresh;

    // Mock DB simple
    private final List<String> mockBooks = new ArrayList<>();

    public void initialize() {
        mockBooks.add("Cálculo Diferencial - James Stewart");
        mockBooks.add("Clean Code - Robert C. Martin");
        mockBooks.add("Introducción a los Algoritmos - Cormen");
        mockBooks.add("Don Quijote de la Mancha - Miguel de Cervantes");
        mockBooks.add("Inteligencia Artificial - Stuart Russell");
        lstResults.getItems().addAll(mockBooks);

        txtSearch.textProperty().addListener((obs, oldV, newV) -> filter(newV));
        btnRefresh.setOnAction(e -> doRefresh());
    }

    private void filter(String term) {
        lstResults.getItems().setAll(mockBooks.stream().filter(s -> s.toLowerCase().contains(term.toLowerCase())).collect(Collectors.toList()));
    }

    private void doRefresh() {
        StageManager.getPrimaryStage();
        Toast.showToast(StageManager.getPrimaryStage(), "Datos actualizados", 1500, StageManager.getPrimaryStage().getWidth() / 2, StageManager.getPrimaryStage().getHeight() / 2);
    }

}

