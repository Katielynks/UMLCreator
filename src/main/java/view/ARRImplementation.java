package view;

import java.util.function.Consumer;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class ARRImplementation extends ARRRelationship {

    private static final double HANDLE_SIZE = 10;

    private static final double ARROW_LENGTH = 18;
    private static final double ARROW_WIDTH = 10;

    private final Line hitLine;
    private final Line line;

    // Hollow triangle
    private final Polygon arrowHead;

    private final Region startHandle;
    private final Region endHandle;

    private double startX = 20;
    private double startY = 50;

    private double endX = 180;
    private double endY = 50;

    public ARRImplementation(
            Consumer<ARRRelationship> onSelected) {

        super(onSelected);

        setLayoutX(150);
        setLayoutY(150);

        setPrefSize(220, 120);
        setPickOnBounds(false);

        /*
         * Invisible thick line for easier clicking.
         */
        hitLine = new Line();

        hitLine.setStroke(Color.TRANSPARENT);
        hitLine.setStrokeWidth(14);

        /*
         * Visible dashed implementation line.
         */
        line = new Line();

        line.getStrokeDashArray().addAll(
                10.0,
                7.0
        );

        /*
         * Hollow triangle arrowhead.
         */
        arrowHead = new Polygon();

        arrowHead.setFill(Color.TRANSPARENT);

        /*
         * Endpoint handles.
         */
        startHandle = createHandle();
        endHandle = createHandle();

        getChildren().addAll(
                hitLine,
                line,
                arrowHead,
                startHandle,
                endHandle
        );

        updateLine();

        /*
         * Dragging the line or arrowhead
         * moves the entire relationship.
         */
        makeDraggable(
                hitLine,
                line,
                arrowHead
        );

        /*
         * Dragging the little squares
         * moves only that endpoint.
         */
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
         * Invisible hitbox follows
         * the same coordinates.
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

        /*
         * Center of the back of the triangle.
         */
        double baseX =
                endX - ARROW_LENGTH * Math.cos(angle);

        double baseY =
                endY - ARROW_LENGTH * Math.sin(angle);

        /*
         * Perpendicular direction.
         */
        double perpendicularX =
                -Math.sin(angle);

        double perpendicularY =
                Math.cos(angle);

        /*
         * Left corner.
         */
        double leftX =
                baseX
                + ARROW_WIDTH * perpendicularX;

        double leftY =
                baseY
                + ARROW_WIDTH * perpendicularY;

        /*
         * Right corner.
         */
        double rightX =
                baseX
                - ARROW_WIDTH * perpendicularX;

        double rightY =
                baseY
                - ARROW_WIDTH * perpendicularY;

        /*
         * Hollow triangle.
         */
        arrowHead.getPoints().setAll(
                endX, endY,
                leftX, leftY,
                rightX, rightY
        );
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
         * Dashed line.
         */
        line.setStroke(color);
        line.setStrokeWidth(width);

        /*
         * Hollow triangle.
         */
        arrowHead.setFill(Color.TRANSPARENT);
        arrowHead.setStroke(color);
        arrowHead.setStrokeWidth(width);

        /*
         * Endpoint handles.
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