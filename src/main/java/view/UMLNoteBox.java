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

public class UMLNoteBox extends UMLShapeBox {

    private static final double MIN_WIDTH = 120;
    private static final double MIN_HEIGHT = 70;
    private static final double LINE_HEIGHT = 28;

    private double startMouseX;
    private double startMouseY;
    private double startLayoutX;
    private double startLayoutY;

    private double startWidth;
    private double startHeight;

    private final DoubleSupplier zoomSupplier;
    private final Polygon resizeHandle;

    private final VBox textSection;

    public UMLNoteBox(
            DoubleSupplier zoomSupplier,
            Consumer<UMLShapeBox> onSelected) {

        super(onSelected);

        this.zoomSupplier = zoomSupplier;

        setLayoutX(160);
        setLayoutY(160);

        setPrefWidth(180);
        setPrefHeight(90);

        setMinWidth(MIN_WIDTH);
        setMinHeight(MIN_HEIGHT);

        /*
         * This VBox stores all of the lines of text.
         */
        textSection = new VBox();

        TextField firstLine = createTextField("Note");

        addEnterHandler(firstLine);

        textSection.getChildren().add(firstLine);

        /*
         * Spacer pushes the resize handle to the bottom.
         */
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        /*
         * Create resize handle.
         */
        resizeHandle = createResizeHandle();

        HBox resizeRow = new HBox(resizeHandle);

        resizeRow.setAlignment(Pos.BOTTOM_RIGHT);

        resizeRow.setMinHeight(12);
        resizeRow.setPrefHeight(12);
        resizeRow.setMaxHeight(12);

        /*
         * Add everything into the note box.
         */
        getChildren().addAll(
                textSection,
                spacer,
                resizeRow
        );

        /*
         * Start unselected.
         */
        setSelected(false);

        /*
         * Clicking the note selects it.
         */
        setOnMouseClicked(event -> {
            selectThis();
            event.consume();
        });

        makeDraggable();
    }

    /*
     * Creates one editable line in the note.
     */
    private TextField createTextField(String text) {

        TextField textField = new TextField(text);

        textField.setMinHeight(LINE_HEIGHT);
        textField.setPrefHeight(LINE_HEIGHT);
        textField.setMaxWidth(Double.MAX_VALUE);

        textField.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-padding: 4 6 4 6;" +
                "-fx-border-color: transparent;"
        );

        /*
         * Clicking inside the text also selects the note.
         */
        textField.setOnMousePressed(event -> {
            selectThis();
        });

        return textField;
    }

    /*
     * Pressing Enter finishes the current line
     * and creates another line underneath it.
     */
    private void addEnterHandler(TextField currentTextField) {

        currentTextField.setOnAction(event -> {

            TextField newTextField = createTextField("");

            addEnterHandler(newTextField);

            int currentIndex =
                    textSection.getChildren().indexOf(currentTextField);

            textSection.getChildren().add(
                    currentIndex + 1,
                    newTextField
            );

            /*
             * Increase the height of the note.
             */
            setPrefHeight(
                    getPrefHeight() + LINE_HEIGHT
            );

            newTextField.requestFocus();

            event.consume();
        });

        /*
         * If an empty line is backspaced,
         * remove that line.
         */
        currentTextField.setOnKeyPressed(event -> {

            if (event.getCode() == KeyCode.BACK_SPACE
                    && currentTextField.getText().isEmpty()) {

                /*
                 * Always keep at least one line.
                 */
                if (textSection.getChildren().size() > 1) {

                    int currentIndex =
                            textSection.getChildren()
                                    .indexOf(currentTextField);

                    textSection.getChildren()
                            .remove(currentTextField);

                    /*
                     * Reduce the box height again.
                     */
                    setPrefHeight(
                            Math.max(
                                    MIN_HEIGHT,
                                    getPrefHeight() - LINE_HEIGHT
                            )
                    );

                    /*
                     * Focus the line above.
                     */
                    if (currentIndex > 0) {

                        textSection.getChildren()
                                .get(currentIndex - 1)
                                .requestFocus();

                    } else if (!textSection.getChildren().isEmpty()) {

                        textSection.getChildren()
                                .get(0)
                                .requestFocus();
                    }

                    event.consume();
                }
            }
        });
    }

    /*
     * Creates the resize triangle in the bottom-right.
     */
    private Polygon createResizeHandle() {

        Polygon handle = new Polygon();

        handle.getPoints().addAll(
                0.0, 10.0,
                10.0, 10.0,
                10.0, 0.0
        );

        handle.setFill(Color.WHITE);

        handle.setCursor(
                Cursor.SE_RESIZE
        );

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

            double zoom =
                    zoomSupplier.getAsDouble();

            double deltaX =
                    (event.getSceneX() - startMouseX)
                            / zoom;

            double deltaY =
                    (event.getSceneY() - startMouseY)
                            / zoom;

            double newWidth =
                    Math.max(
                            MIN_WIDTH,
                            startWidth + deltaX
                    );

            double newHeight =
                    Math.max(
                            MIN_HEIGHT,
                            startHeight + deltaY
                    );

            setPrefWidth(newWidth);
            setPrefHeight(newHeight);

            event.consume();
        });

        return handle;
    }

    /*
     * Allows the note to be dragged around the canvas.
     */
    private void makeDraggable() {

        setOnMousePressed(event -> {

            /*
             * Clicking text should edit the text,
             * not drag the note.
             */
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

            /*
             * Do not drag while interacting
             * directly with the text field.
             */
            if (event.getTarget() instanceof TextField) {
                return;
            }

            double zoom =
                    zoomSupplier.getAsDouble();

            double deltaX =
                    (event.getSceneX() - startMouseX)
                            / zoom;

            double deltaY =
                    (event.getSceneY() - startMouseY)
                            / zoom;

            setLayoutX(
                    startLayoutX + deltaX
            );

            setLayoutY(
                    startLayoutY + deltaY
            );

            event.consume();
        });
    }

    /*
     * Selected = blue border.
     * Unselected = white border.
     */
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
                    "-fx-border-color: white;" +
                    "-fx-border-width: 1;"
            );
        }
    }
}