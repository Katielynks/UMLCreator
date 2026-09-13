package view;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class UMLAbstractClassBox extends UMLShapeBox {

    private static final double MIN_WIDTH = 120;
    private static final double MIN_HEIGHT = 90;
    private static final double LINE_HEIGHT = 28;

    private double startMouseX;
    private double startMouseY;
    private double startLayoutX;
    private double startLayoutY;

    private double startWidth;
    private double startHeight;

    private boolean selected = false;

    private final DoubleSupplier zoomSupplier;
    private final Polygon resizeHandle;

    public UMLAbstractClassBox(
        DoubleSupplier zoomSupplier,
        Consumer<UMLShapeBox> onSelected) {

        super(onSelected);
        this.zoomSupplier = zoomSupplier;

        setLayoutX(140);
        setLayoutY(140);
        setPrefWidth(180);
        setPrefHeight(135);
        setMinWidth(MIN_WIDTH);
        setMinHeight(MIN_HEIGHT);

        VBox classSection = createSection(true);
        VBox attributeSection = createSection(true);
        VBox operationSection = createSection(false);

        Label abstractLabel = createAbstractLabel();
        TextField className = createTextField("Class", true);
        TextField attribute = createTextField("- attribute", false);
        TextField operation = createTextField("+ operation()", false);

        addEnterHandler(classSection, className, true);
        addEnterHandler(attributeSection, attribute, false);
        addEnterHandler(operationSection, operation, false);

        classSection.getChildren().addAll(abstractLabel, className);
        attributeSection.getChildren().add(attribute);
        operationSection.getChildren().add(operation);

        resizeHandle = createResizeHandle();
        setSelected(false);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox resizeRow = new HBox(resizeHandle);
        resizeRow.setAlignment(Pos.BOTTOM_RIGHT);
        resizeRow.setMinHeight(12);
        resizeRow.setPrefHeight(12);
        resizeRow.setMaxHeight(12);

        getChildren().addAll(
                classSection,
                attributeSection,
                operationSection,
                spacer,
                resizeRow
        );

        setOnMouseClicked(event -> {
            selectThis();
            event.consume();
        });

        makeDraggable();
    }

    private VBox createSection(boolean hasBottomBorder) {
        VBox section = new VBox();

        if (hasBottomBorder) {
            section.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-border-color: transparent transparent white transparent;" +
                    "-fx-border-width: 0 0 1 0;"
            );
        } else {
            section.setStyle("-fx-background-color: transparent;");
        }

        return section;
    }

    private Label createAbstractLabel() {
        Label label = new Label("<<Abstract>>");

        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);

        label.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 11px;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-font-style: italic;" +
                "-fx-padding: 4 4 0 4;"
        );

        return label;
    }

    private TextField createTextField(String text, boolean centered) {
        TextField textField = new TextField(text);

        textField.setMinHeight(LINE_HEIGHT);
        textField.setPrefHeight(LINE_HEIGHT);
        textField.setMaxWidth(Double.MAX_VALUE);

        if (centered) {
            textField.setAlignment(Pos.CENTER);
        }

        textField.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-padding: 4;" +
                "-fx-border-color: transparent;"
        );

        textField.setOnMousePressed(event -> {
            selectThis();
        });

        return textField;
    }

    private void addEnterHandler(VBox section, TextField currentTextField, boolean centered) {
        currentTextField.setOnAction(event -> {
            TextField newTextField = createTextField("", centered);

            addEnterHandler(section, newTextField, centered);

            int currentIndex = section.getChildren().indexOf(currentTextField);
            section.getChildren().add(currentIndex + 1, newTextField);

            setPrefHeight(getPrefHeight() + LINE_HEIGHT);

            newTextField.requestFocus();

            event.consume();
        });

        currentTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE && currentTextField.getText().isEmpty()) {
                if (section.getChildren().size() > 1) {
                    int currentIndex = section.getChildren().indexOf(currentTextField);

                    section.getChildren().remove(currentTextField);

                    setPrefHeight(Math.max(MIN_HEIGHT, getPrefHeight() - LINE_HEIGHT));

                    if (currentIndex > 0) {
                        section.getChildren().get(currentIndex - 1).requestFocus();
                    } else if (!section.getChildren().isEmpty()) {
                        section.getChildren().get(0).requestFocus();
                    }

                    event.consume();
                }
            }
        });
    }

    private Polygon createResizeHandle() {
        Polygon handle = new Polygon();

        handle.getPoints().addAll(
                0.0, 10.0,
                10.0, 10.0,
                10.0, 0.0
        );

        handle.setFill(Color.WHITE);
        handle.setCursor(Cursor.SE_RESIZE);
        handle.setVisible(false);

        handle.setOnMousePressed(event -> {
            selectThis();

            startMouseX = event.getSceneX();
            startMouseY = event.getSceneY();

            startWidth = getWidth();
            startHeight = getHeight();

            event.consume();
        });

        handle.setOnMouseDragged(event -> {
            double zoom = zoomSupplier.getAsDouble();

            double deltaX = (event.getSceneX() - startMouseX) / zoom;
            double deltaY = (event.getSceneY() - startMouseY) / zoom;

            double newWidth = Math.max(MIN_WIDTH, startWidth + deltaX);
            double newHeight = Math.max(MIN_HEIGHT, startHeight + deltaY);

            setPrefWidth(newWidth);
            setPrefHeight(newHeight);

            event.consume();
        });

        return handle;
    }

    private void makeDraggable() {
        setOnMousePressed(event -> {
            if (event.getTarget() instanceof TextField) {
                selectThis();
                return;
            }

            startMouseX = event.getSceneX();
            startMouseY = event.getSceneY();

            startLayoutX = getLayoutX();
            startLayoutY = getLayoutY();

            selectThis();

            event.consume();
        });

        setOnMouseDragged(event -> {
            if (event.getTarget() instanceof TextField) {
                return;
            }

            double zoom = zoomSupplier.getAsDouble();

            double deltaX = (event.getSceneX() - startMouseX) / zoom;
            double deltaY = (event.getSceneY() - startMouseY) / zoom;

            setLayoutX(startLayoutX + deltaX);
            setLayoutY(startLayoutY + deltaY);

            event.consume();
        });
    }

    @Override
    protected void updateSelectionStyle(boolean selected) {

        resizeHandle.setVisible(selected);

        if (selected) {
            setStyle(
                    "-fx-background-color: rgba(255,255,255,0.06);" +
                    "-fx-border-color: #4f8cff;" +
                    "-fx-border-width: 2;"
            );
        } else {
            setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-border-color: grey;" +
                    "-fx-border-width: 1;"
            );
        }
    }
}