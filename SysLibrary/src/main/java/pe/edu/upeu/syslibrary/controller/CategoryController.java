package pe.edu.upeu.syslibrary.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Categoria;
import pe.edu.upeu.syslibrary.service.ICategoriaService;
import pe.edu.upeu.syslibrary.model.Categoria;
import pe.edu.upeu.syslibrary.service.ICategoriaService;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.controlsfx.glyphfont.Glyph;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
// Asumiendo una clase de modelo para Categoría, por ejemplo, Category.java
// import pe.edu.upeu.syslibrary.model.Category;

@Controller
public class CategoryController {

    @FXML
    private VBox mainCategoryViewRoot;

    @FXML
    private FlowPane categoriesContainer;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ICategoriaService categoriaService;

    @FXML
    public void initialize() {
        System.out.println("Inicializando categorías...");
        loadCategoryCards();
    }

    /** Cargar dinámicamente las tarjetas de categorías */
    private void loadCategoryCards() {
        categoriesContainer.getChildren().clear();

        try {
            var categorias = categoriaService.findAll();
            if (categorias.isEmpty()) {
                categoriesContainer.getChildren().add(new Label("No hay categorías registradas."));
                return;
            }

            for (Categoria categoria : categorias) {
                VBox card = createCategoryCard(categoria);
                categoriesContainer.getChildren().add(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
            categoriesContainer.getChildren().add(new Label("Error al cargar las categorías."));
        }
    }

    /** Crear una tarjeta visual para cada categoría */
    private VBox createCategoryCard(Categoria categoria) {
        VBox card = new VBox();
        card.getStyleClass().add("category-card");
        card.setPrefWidth(300);

        // Encabezado: ícono + nombre
        Glyph icon = new Glyph("FontAwesome", "BOOKMARK");
        icon.getStyleClass().add("category-icon-violet");

        Label lblNombre = new Label(categoria.getNombre());
        lblNombre.getStyleClass().add("card-category-title");

        Label lblDescripcion = new Label(
                categoria.getDescripcion() != null ? categoria.getDescripcion() : "Sin descripción"
        );
        lblDescripcion.getStyleClass().add("card-category-description");
        lblDescripcion.setWrapText(true);

        HBox header = new HBox(10, icon, new VBox(lblNombre, lblDescripcion));

        // Espaciador
        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Botones de acción
        Button btnEdit = new Button("Editar");
        btnEdit.getStyleClass().add("secondary-button-light");
        btnEdit.setOnAction(e -> handleEditCategory(categoria));

        Button btnDelete = new Button("Eliminar");
        btnDelete.getStyleClass().add("danger-button-light");
        btnDelete.setOnAction(e -> handleDeleteCategory(categoria));

        HBox actions = new HBox(10, btnEdit, btnDelete);
        actions.setAlignment(javafx.geometry.Pos.CENTER);

        card.getChildren().addAll(header, spacer, actions);
        return card;
    }

    // --- Acciones de los botones de cada tarjeta ---
    private void handleEditCategory(Categoria categoria) {
        System.out.println("Editando categoría: " + categoria.getNombre());
        // mainguiController.showCategoryEditForm(categoria);
    }

    private void handleDeleteCategory(Categoria categoria) {
        System.out.println("Eliminando categoría: " + categoria.getNombre());
        try {
            categoriaService.deleteById(categoria.getIdCategoria());
            loadCategoryCards(); // recarga la vista
        } catch (Exception e) {
            System.err.println("Error al eliminar la categoría: " + e.getMessage());
        }
    }

    @FXML
    private void handleNewCategory(ActionEvent event) {
        System.out.println("Nueva Categoría...");
        MainguiController main = applicationContext.getBean(MainguiController.class);
        // main.showCategoryRegistrationForm();
    }
}