package pe.edu.upeu.athenium.common.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javafx.event.ActionEvent;

// Aquí inyectarías tu servicio de negocio para libros
// import pe.edu.upeu.athenium.libro.service.impl.ILibroService;

@Controller
public class HomeContentController {

    // --- Elementos FXML de la Interfaz ---

    // Filtros
    @FXML private TextField txtSearch;
    @FXML private CheckBox chkAvailable;
    @FXML private TextField txtAnioPublicacion;

    // Contenido
    @FXML private FlowPane resultsFlowPane; // Contenedor FlowPane para las tarjetas

    // Inyección de servicio real
    // @Autowired
    // private ILibroService libroService;

    @FXML
    public void initialize() {
        // Inicializa la búsqueda al cargar la vista
        buscarLibros();
    }

    /**
     * Manejador para el botón "Aplicar Filtros".
     */
    @FXML
    private void handleAplicarFiltros(ActionEvent event) {
        buscarLibros();
    }

    /**
     * Lógica principal de búsqueda y renderización de tarjetas.
     */
    private void buscarLibros() {
        // Aquí obtendrías los parámetros
        String query = txtSearch.getText();
        boolean disponible = chkAvailable.isSelected();
        String anio = txtAnioPublicacion.getText();

        // --- 1. Lógica de Backend (a implementar) ---
        // List<Libro> resultados = libroService.buscar(query, disponible, anio);

        // --- 2. Limpiar y Renderizar ---
        resultsFlowPane.getChildren().clear();

        // Por ahora, solo simulamos la carga de tarjetas
        System.out.println("Buscando con: Query=" + query + ", Disponible=" + disponible);

        // En un proyecto real, iterarías sobre 'resultados'
        // for (Libro libro : resultados) {
        //     Node card = cargarCardDesdeFXML(libro);
        //     resultsFlowPane.getChildren().add(card);
        // }
    }

    // El método cargarCardDesdeFXML usaría FXMLLoader y un controlador específico para la tarjeta
    // Es complejo y lo desarrollarías después de tener la lógica del modelo.
}