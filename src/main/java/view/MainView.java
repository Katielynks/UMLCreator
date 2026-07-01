package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MainView {

    private final BorderPane root;
    private final Canvas canvas;

    public MainView() {
        root = new BorderPane();
        canvas = new Canvas(800, 600);

        root.setTop(createTopMenu());
        root.setLeft(createShapeMenu());
        root.setCenter(createCanvasArea());
    }

    public Parent getRoot() {
        return root;
    }

    private HBox createTopMenu() {
        Button newButton = createMenuButton("New");
        Button openButton = createMenuButton("Open");
        Button saveButton = createMenuButton("Save");
        Button exportButton = createMenuButton("Export");

        HBox topMenu = new HBox(10);
        topMenu.setPadding(new Insets(10));
        topMenu.setAlignment(Pos.CENTER_LEFT);
        topMenu.setStyle("-fx-background-color: #eeeeee;");

        topMenu.getChildren().addAll(
                new Label("UML Diagram Creator"),
                newButton,
                openButton,
                saveButton,
                exportButton
        );

        return topMenu;
    }

    private VBox createShapeMenu() {
        Button classButton = createMenuButton("Class");
        Button abstractClassButton = createMenuButton("Abstract Class");
        Button interfaceButton = createMenuButton("Interface");
        Button packageButton = createMenuButton("Package");        
        Button noteButton = createMenuButton("Note");


        Button associationButton = createMenuButton("Association");
        Button dependencyButton = createMenuButton("Dependency");
        Button inheritanceButton = createMenuButton("Inheritance");
        Button implementationButton = createMenuButton("Implementation");
        Button aggregationButton = createMenuButton("Aggregation");
        Button compositionButton = createMenuButton("Composition");

        VBox shapeMenu = new VBox(10);
        shapeMenu.setPadding(new Insets(10));
        shapeMenu.setPrefWidth(180);
        shapeMenu.setStyle("-fx-background-color: #f7f7f7;");

        shapeMenu.getChildren().addAll(
                new Label("Shapes"),
                classButton,
                interfaceButton,
                abstractClassButton,
                noteButton,
                packageButton,

                new Label("Relationships"),
                associationButton,
                dependencyButton,
                inheritanceButton,
                implementationButton,
                aggregationButton,
                compositionButton
        );

        return shapeMenu;
    }

    private Pane createCanvasArea() {
        Pane canvasPane = new Pane();
        canvasPane.setPadding(new Insets(10));
        canvasPane.setStyle("-fx-background-color: white;");

        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        canvasPane.getChildren().add(canvas);

        return canvasPane;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);

        button.setPrefWidth(150);

        button.setStyle(
            "-fx-background-color: #ffffff;" +
            "-fx-text-fill: #333333;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 8 12 8 12;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: #cccccc;" +
            "-fx-border-radius: 6;"
        );

        return button;
    }

}