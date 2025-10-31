package pe.edu.upeu.athenium.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.athenium.service.NavigationService;

@Controller
public class AdminController {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private NavigationService navigationService;

    @FXML
    private Button btnDummy;

    public void initialize() {
        // placeholder
    }

}

