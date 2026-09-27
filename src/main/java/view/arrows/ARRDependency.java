package view.arrows;

import java.util.function.Consumer;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class ARRDependency extends ARRRelationship {

    private static final double HANDLE_SIZE = 10;

    private static final double ARROW_LENGTH = 16;
    private static final double ARROW_ANGLE = Math.toRadians(28);

    private final Line hitLine;
    private final Line line;

    private final Line arrowLeft;
    private final Line arrowRight;

    private final Region startHandle;
    private final Region endHandle;

    private double startX = 20;
    private double startY = 50;

    private double endX = 180;
    private double endY = 50;

    public ARRDependency(
            Consumer<ARRRelationship> onSelected) {

        super(onSelected);

        setLayoutX(150);
        setLayoutY(150);

        setPrefSize(220, 120);
        setPickOnBounds(false);

        /*
         * Invisible thick line.
         * This gives the relationship a larger clickable area.
         */
        hitLine = new Line();

        hitLine.setStroke(Color.TRANSPARENT);
        hitLine.setStrokeWidth(14);

        /*
         * Visible dependency line.
         */
        line = new Line();

        line.getStrokeDashArray().addAll(
                10.0,
                7.0
        );

        /*
         * Open arrowhead.
         */
        arrowLeft = new Line();
        arrowRight = new Line();

        /*
         * Endpoint handles.
         */
        startHandle = createHandle();
        endHandle = createHandle();

        /*
         * hitLine goes first so it stays behind
         * the visible relationship.
         */
        getChildren().addAll(
                hitLine,
                line,
                arrowLeft,
                arrowRight,
                startHandle,
                endHandle
        );

        updateLine();

        makeSelectable();

        makeDraggable(
            hitLine,
            line,
            arrowLeft,
            arrowRight
        );

        makeStartHandleDraggable();
        makeEndHandleDraggable();

        setSelected(false);
    }

    private Region createHandle() {

        Region handle = new Region();

        handle.setPrefSize(
                HANDLE_SIZE,
                HANDLE_SIZE
        );

        handle.setMinSize(
                HANDLE_SIZE,
                HANDLE_SIZE
        );

        handle.setMaxSize(
                HANDLE_SIZE,
                HANDLE_SIZE
        );

        handle.setCursor(Cursor.CROSSHAIR);

        return handle;
    }

    private void makeSelectable() {

        /*
         * The invisible hitbox catches clicks
         * near the line.
         */
        hitLine.setOnMousePressed(event -> {
            selectThis();
            event.consume();
        });

        line.setOnMousePressed(event -> {
            selectThis();
            event.consume();
        });

        arrowLeft.setOnMousePressed(event -> {
            selectThis();
            event.consume();
        });

        arrowRight.setOnMousePressed(event -> {
            selectThis();
            event.consume();
        });
    }

    private void makeStartHandleDraggable() {

        startHandle.setOnMousePressed(event -> {
            selectThis();
            event.consume();
        });

        startHandle.setOnMouseDragged(event -> {

            Point2D point = sceneToLocal(
                    event.getSceneX(),
                    event.getSceneY()
            );

            startX = point.getX();
            startY = point.getY();

            updateLine();

            event.consume();
        });
    }

    private void makeEndHandleDraggable() {

        endHandle.setOnMousePressed(event -> {
            selectThis();
            event.consume();
        });

        endHandle.setOnMouseDragged(event -> {

            Point2D point = sceneToLocal(
                    event.getSceneX(),
                    event.getSceneY()
            );

            endX = point.getX();
            endY = point.getY();

            updateLine();

            event.consume();
        });
    }

    private void updateLine() {

        /*
         * Visible dashed line.
         */
        line.setStartX(startX);
        line.setStartY(startY);

        line.setEndX(endX);
        line.setEndY(endY);

        /*
         * Invisible hitbox line follows
         * the exact same coordinates.
         */
        hitLine.setStartX(startX);
        hitLine.setStartY(startY);

        hitLine.setEndX(endX);
        hitLine.setEndY(endY);

        /*
         * Move endpoint handles.
         */
        startHandle.relocate(
                startX - HANDLE_SIZE / 2,
                startY - HANDLE_SIZE / 2
        );

        endHandle.relocate(
                endX - HANDLE_SIZE / 2,
                endY - HANDLE_SIZE / 2
        );

        updateArrowHead();
    }

    private void updateArrowHead() {

        double angle = Math.atan2(
                endY - startY,
                endX - startX
        );

        double leftAngle =
                angle + Math.PI - ARROW_ANGLE;

        double rightAngle =
                angle + Math.PI + ARROW_ANGLE;

        double leftX =
                endX
                + ARROW_LENGTH * Math.cos(leftAngle);

        double leftY =
                endY
                + ARROW_LENGTH * Math.sin(leftAngle);

        double rightX =
                endX
                + ARROW_LENGTH * Math.cos(rightAngle);

        double rightY =
                endY
                + ARROW_LENGTH * Math.sin(rightAngle);

        /*
         * Left side of open arrowhead.
         */
        arrowLeft.setStartX(endX);
        arrowLeft.setStartY(endY);

        arrowLeft.setEndX(leftX);
        arrowLeft.setEndY(leftY);

        /*
         * Right side of open arrowhead.
         */
        arrowRight.setStartX(endX);
        arrowRight.setStartY(endY);

        arrowRight.setEndX(rightX);
        arrowRight.setEndY(rightY);
    }

    @Override
    protected void updateSelectionStyle(
            boolean selected) {

        Color color =
                selected
                        ? Color.web("#4f8cff")
                        : getShapeColor();

        double width =
                selected ? 2 : 1;

        /*
         * Visible dashed line.
         */
        line.setStroke(color);
        line.setStrokeWidth(width);

        /*
         * Arrowhead.
         */
        arrowLeft.setStroke(color);
        arrowLeft.setStrokeWidth(width);

        arrowRight.setStroke(color);
        arrowRight.setStrokeWidth(width);

        /*
         * Endpoint handles only show
         * when selected.
         */
        startHandle.setVisible(selected);
        endHandle.setVisible(selected);

        startHandle.setStyle(
                "-fx-background-color: #4f8cff;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 1;"
        );

        endHandle.setStyle(
                "-fx-background-color: #4f8cff;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 1;"
        );
    }
}