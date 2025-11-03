package pe.edu.upeu.athenium.common.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
// Nota: Ya no necesitamos StageManager o Toast aquí
import pe.edu.upeu.athenium.libro.entity.Libro;
import pe.edu.upeu.athenium.libro.service.ILibroService;

import java.io.IOException;
import java.util.List;

@Controller
public class HomeContentController {

    // --- FXML UI Elements ---
    @FXML private TextField txtSearch;
    @FXML private CheckBox chkAvailable;
    @FXML private TextField txtAnioPublicacion;
    @FXML private FlowPane resultsFlowPane;
    @FXML private Button btnPrevPage;
    @FXML private Label lblPaginationStatus;
    @FXML private Button btnNextPage;

    // --- Inyección de Servicios ---
    @Autowired
    private ILibroService libroService;
    @Autowired
    private ApplicationContext context; // Necesario para la inyección de Spring

    // --- Estado de Paginación y Filtros ---
    private int currentPage = 0;
    private final int pageSize = 9;
    private String currentSearchFilter = "";
    private ObservableList<Libro> allBooksCache = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadBooksFromService();

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            currentSearchFilter = newVal.trim();
            currentPage = 0;
            loadBooksFromService();
        });

        chkAvailable.setDisable(true);
        txtAnioPublicacion.setDisable(true);
    }

    @FXML
    private void handleAplicarFiltros() {
        currentPage = 0;
        loadBooksFromService();
    }

    /**
     * 1. Llama al Servicio (Backend)
     */
    private void loadBooksFromService() {
        new Thread(() -> {
            List<Libro> results;
            if (currentSearchFilter.isEmpty()) {
                results = libroService.findAll();
            } else {
                results = libroService.filtrarLibros(currentSearchFilter);
            }

            Platform.runLater(() -> {
                allBooksCache.setAll(results);
                updateBookCardsView();
            });
        }).start();
    }

    /**
     * 2. Renderiza la paginación (Frontend)
     */
    private void updateBookCardsView() {
        resultsFlowPane.getChildren().clear();

        int totalBooks = allBooksCache.size();
        int totalPages = (int) Math.ceil((double) totalBooks / pageSize);
        if (totalPages == 0) totalPages = 1;

        if (currentPage >= totalPages && totalPages > 0) {
            currentPage = totalPages - 1;
        } else if (totalPages == 0) {
            currentPage = 0;
        }

        int startIndex = currentPage * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalBooks);

        if (startIndex < totalBooks) {
            List<Libro> booksForPage = allBooksCache.subList(startIndex, endIndex);

            // --- CAMBIO CLAVE AQUÍ ---
            for (Libro libro : booksForPage) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/pMenus/mHome/book_card.fxml"));

                    // 1. Usar el contexto de Spring para crear el BookCardController (prototype)
                    loader.setControllerFactory(context::getBean);

                    VBox bookCard = loader.load();

                    // 2. Obtener la instancia del controlador de la tarjeta
                    BookCardController cardController = loader.getController();

                    // 3. Pasar los datos y el callback de refresco
                    cardController.setData(libro, this::loadBooksFromService);

                    resultsFlowPane.getChildren().add(bookCard);
                } catch (IOException e) {
                    System.err.println("Error al cargar la tarjeta de libro (book_card.fxml): " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        updatePaginationStatus(totalPages);
    }

    /**
     * 3. (Eliminado) populateBookCard() ya no es necesario.
     * El BookCardController se encarga de eso.
     */

    /**
     * 4. Actualiza los botones de paginación
     */
    private void updatePaginationStatus(int totalPages) {
        lblPaginationStatus.setText("Página " + (currentPage + 1) + " de " + totalPages);
        btnPrevPage.setDisable(currentPage == 0);
        btnNextPage.setDisable(currentPage >= totalPages - 1 || totalPages == 0);

        if (totalPages <= 1 && allBooksCache.isEmpty()) {
            lblPaginationStatus.setText("No hay resultados");
        } else if (totalPages <= 1) {
            btnPrevPage.setDisable(true);
            btnNextPage.setDisable(true);
        }
    }

    // --- Handlers de Paginación ---

    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            updateBookCardsView();
        }
    }

    @FXML
    private void handleNextPage() {
        int totalBooks = allBooksCache.size();
        int totalPages = (int) Math.ceil((double) totalBooks / pageSize);

        if (currentPage < totalPages - 1) {
            currentPage++;
            updateBookCardsView();
        }
    }

    /**
     * 5. (Eliminado) handleReservarLibro() ya no es necesario aquí.
     * El BookCardController se encarga de eso.
     */
}