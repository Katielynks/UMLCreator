package view;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class UMLPackage extends UMLShapeBox {

    private static final double MIN_WIDTH = 140;
    private static final double MIN_HEIGHT = 80;
    private static final double LINE_HEIGHT = 24;
    private static final double TAB_WIDTH = 45;
    private static final double TAB_HEIGHT = 16;

    private double startMouseX;
    private double startMouseY;
    private double startLayoutX;
    private double startLayoutY;

    private double startWidth;
    private double startHeight;

    private final DoubleSupplier zoomSupplier;
    private final Polygon resizeHandle;

    private final Region tab;
    private final VBox body;
    private final VBox textSection;

    public UMLPackage(
        DoubleSupplier zoomSupplier,
        Consumer<UMLShapeBox> onSelected) {

        super(onSelected);
        this.zoomSupplier = zoomSupplier;

        setLayoutX(140);
        setLayoutY(140);
        setPrefWidth(180);
        setPrefHeight(100);
        setMinWidth(MIN_WIDTH);
        setMinHeight(MIN_HEIGHT);
        setSpacing(0);

        tab = createTab();
        body = createBody();
        textSection = new VBox();

        TextField optionalText = createTextField("<<subsystem>>", true, true);
        TextField titleField = createTextField("Package", true, false);

        addEnterHandler(textSection, titleField, true);

        textSection.getChildren().addAll(optionalText, titleField);
        textSection.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        resizeHandle = createResizeHandle();

        HBox resizeRow = new HBox(resizeHandle);
        resizeRow.setAlignment(Pos.BOTTOM_RIGHT);
        resizeRow.setMinHeight(12);
        resizeRow.setPrefHeight(12);
        resizeRow.setMaxHeight(12);

        body.getChildren().addAll(textSection, spacer, resizeRow);

        HBox topRow = new HBox();
        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        topRow.getChildren().addAll(tab, topSpacer);

        getChildren().addAll(topRow, body);

        setSelected(false);

        setOnMouseClicked(event -> {
            selectThis();
            event.consume();
        });

        makeDraggable();
    }

    private Region createTab() {
        Region packageTab = new Region();
        packageTab.setPrefSize(TAB_WIDTH, TAB_HEIGHT);
        packageTab.setMinSize(TAB_WIDTH, TAB_HEIGHT);
        packageTab.setMaxSize(TAB_WIDTH, TAB_HEIGHT);
        return packageTab;
    }

    private VBox createBody() {
        VBox packageBody = new VBox();
        packageBody.setPrefHeight(getPrefHeight() - TAB_HEIGHT);
        packageBody.setMinHeight(MIN_HEIGHT - TAB_HEIGHT);
        return packageBody;
    }

    private TextField createTextField(String text, boolean centered, boolean italic) {
        TextField textField = new TextField(text);

        textField.setMinHeight(LINE_HEIGHT);
        textField.setPrefHeight(LINE_HEIGHT);
        textField.setMaxWidth(Double.MAX_VALUE);

        if (centered) {
            textField.setAlignment(Pos.CENTER);
        }

        String style =
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-padding: 2 4 2 4;" +
                "-fx-border-color: transparent;";

        if (italic) {
            style += "-fx-font-style: italic;";
        }

        textField.setStyle(style);

        textField.setOnMousePressed(event -> {
            selectThis();
        });

        return textField;
    }

    private void addEnterHandler(VBox section, TextField currentTextField, boolean centered) {
        currentTextField.setOnAction(event -> {
            TextField newTextField = createTextField("", centered, false);

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

        handle.setFill(Color.GRAY);
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

            body.setPrefWidth(newWidth);
            body.setPrefHeight(newHeight - TAB_HEIGHT);

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

        String borderColor = selected ? "#4f8cff" : getShapeColorCss();
        String borderWidth = selected ? "2" : "1";

        tab.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: " + borderWidth + ";" +
                "-fx-border-radius: 0;" +
                "-fx-background-radius: 0;"
        );

        body.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: " + borderWidth + ";" +
                "-fx-border-radius: 0;" +
                "-fx-background-radius: 0;"
        );
    }
}