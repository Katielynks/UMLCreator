package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class CanvasView {

    private final StackPane canvasArea;
    private final Canvas canvas;

    private String canvasPattern = "blank";
    private double zoomLevel = 1.0;

    public CanvasView() {
        canvas = new Canvas(800, 600);
        canvasArea = createCanvasArea();
    }

    public StackPane getView() {
        return canvasArea;
    }

    private StackPane createCanvasArea() {
        StackPane area = new StackPane();
        area.setStyle("-fx-background-color: black;");

        canvas.widthProperty().bind(area.widthProperty());
        canvas.heightProperty().bind(area.heightProperty());

        area.getChildren().add(canvas);

        HBox canvasButtons = createCanvasPatternButtons();
        area.getChildren().add(canvasButtons);

        StackPane.setAlignment(canvasButtons, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(canvasButtons, new Insets(0, 15, 15, 0));

        area.addEventFilter(ScrollEvent.SCROLL, event -> {
            handleZoom(event);
            event.consume();
        });

        canvas.widthProperty().addListener((observable, oldValue, newValue) -> drawCanvasBackground());
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> drawCanvasBackground());

        drawCanvasBackground();

        return area;
    }

    private HBox createCanvasPatternButtons() {
        Button blankButton = UIComponentFactory.createSmallCanvasButton("Blank");
        Button linedButton = UIComponentFactory.createSmallCanvasButton("Lined");
        Button dottedButton = UIComponentFactory.createSmallCanvasButton("Dotted");

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

        HBox buttons = new HBox(8);
        buttons.setPadding(new Insets(10));
        buttons.setAlignment(Pos.CENTER);
        buttons.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        buttons.getChildren().addAll(blankButton, linedButton, dottedButton);

        return buttons;
    }

    private void handleZoom(ScrollEvent event) {
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