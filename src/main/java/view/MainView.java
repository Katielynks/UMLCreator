package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.input.ScrollEvent;


public class MainView {

    private final BorderPane root;
    private final Canvas canvas;
    private String canvasPattern = "blank";
    private double zoomLevel = 1.0;

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
        topMenu.setStyle("-fx-background-color: #13192bff;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topMenu.getChildren().addAll(
                createTitleLabel("UML Diagram Creator"),
                spacer,
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
        shapeMenu.setStyle("-fx-background-color: #13192bff;");

        shapeMenu.getChildren().addAll(
                createSectionLabel("Shapes"),
                classButton,
                interfaceButton,
                abstractClassButton,
                noteButton,
                packageButton,

                createSectionLabel("Relationships"),
                associationButton,
                dependencyButton,
                inheritanceButton,
                implementationButton,
                aggregationButton,
                compositionButton
        );

        return shapeMenu;
    }

    private StackPane createCanvasArea() {
        StackPane canvasArea = new StackPane();
        canvasArea.setStyle("-fx-background-color: black;");

        canvas.widthProperty().bind(canvasArea.widthProperty());
        canvas.heightProperty().bind(canvasArea.heightProperty());

        canvasArea.getChildren().add(canvas);

        Button blankButton = createSmallCanvasButton("Blank");
        Button linedButton = createSmallCanvasButton("Lined");
        Button dottedButton = createSmallCanvasButton("Dotted");

        blankButton.setOnAction(event -> {
            canvasPattern = "blank";
            drawCanvasBackground();
        });

        linedButton.setOnAction(event -> {
            canvasPattern = "lined";
            drawCanvasBackground();
        });

        dottedButton.setOnAction(event -> {
            canvasPattern = "dotted";
            drawCanvasBackground();
        });

        HBox canvasButtons = new HBox(8);
        canvasButtons.setPadding(new Insets(10));
        canvasButtons.setAlignment(Pos.CENTER);
        canvasButtons.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        canvasButtons.getChildren().addAll(blankButton, linedButton, dottedButton);

        canvasArea.getChildren().add(canvasButtons);

        StackPane.setAlignment(canvasButtons, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(canvasButtons, new Insets(0, 15, 15, 0));

        canvasArea.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() > 0) {
                zoomLevel *= 1.1;
            } else {
                zoomLevel *= 0.9;
            }

            if (zoomLevel < 0.4) {
                zoomLevel = 0.4;
            }

            if (zoomLevel > 3.0) {
                zoomLevel = 3.0;
            }

            drawCanvasBackground();
            event.consume();
        });

        canvas.widthProperty().addListener((observable, oldValue, newValue) -> drawCanvasBackground());
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> drawCanvasBackground());

        drawCanvasBackground();

        return canvasArea;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);

        button.setPrefWidth(150);

        button.setStyle(
            "-fx-background-color: #314172ff;" +
            "-fx-text-fill: #ffffffff;" +
            "-fx-font-size: 13px;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-padding: 8 12 8 12;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: #000000ff;" +
            "-fx-border-radius: 6;"
        );

        return button;
    }


    private Label createTitleLabel(String text) {
        Label label = new Label(text);

        label.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #cad7e7ff;" +
                "-fx-padding: 0 15 0 0;"
        );

        return label;
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);

        label.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #cad7e7ff;" +
                "-fx-padding: 10 0 5 0;"
        );

        return label;
    }

    private Button createSmallCanvasButton(String text) {
        Button button = new Button(text);

        button.setStyle(
                "-fx-background-color: #314172ff;" +
                "-fx-text-fill: #ffffffff;" +
                "-fx-font-size: 12px;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-padding: 6 10 6 10;" +
                "-fx-background-radius: 6;" +
                "-fx-border-color: #000000ff;" +
                "-fx-border-radius: 6;"
        );

        return button;
    }

    private void drawCanvasBackground() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.clearRect(0, 0, width, height);

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        if (canvasPattern.equals("lined")) {
            drawLinedBackground(gc, width, height);
        } else if (canvasPattern.equals("dotted")) {
            drawDottedBackground(gc, width, height);
        }
    }

    private void drawLinedBackground(GraphicsContext gc, double width, double height) {
        gc.setStroke(Color.rgb(70, 70, 70));
        gc.setLineWidth(1);

        double spacing = 25 * zoomLevel;

        for (double x = 0; x < width; x += spacing) {
            gc.strokeLine(x, 0, x, height);
        }

        for (double y = 0; y < height; y += spacing) {
            gc.strokeLine(0, y, width, y);
        }
    }

    private void drawDottedBackground(GraphicsContext gc, double width, double height) {
        gc.setFill(Color.rgb(90, 90, 90));

        double spacing = 25 * zoomLevel;
        double dotSize = 3;

        for (double x = 0; x < width; x += spacing) {
            for (double y = 0; y < height; y += spacing) {
                gc.fillOval(x, y, dotSize, dotSize);
            }
        }
    }

}