package view;

import java.util.function.Consumer;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class ARRComposition extends ARRRelationship {

    private static final double HANDLE_SIZE = 10;

    private static final double DIAMOND_LENGTH = 22;
    private static final double DIAMOND_WIDTH = 9;

    private final Line hitLine;
    private final Line line;

    // Filled diamond for composition
    private final Polygon diamond;

    private final Region startHandle;
    private final Region endHandle;

    private double startX = 20;
    private double startY = 50;

    private double endX = 180;
    private double endY = 50;

    public ARRComposition(
            Consumer<ARRRelationship> onSelected) {

        super(onSelected);

        setLayoutX(150);
        setLayoutY(150);

        setPrefSize(220, 120);
        setPickOnBounds(false);

        /*
         * Larger invisible hitbox.
         */
        hitLine = new Line();
        hitLine.setStroke(Color.TRANSPARENT);
        hitLine.setStrokeWidth(14);

        /*
         * Visible solid line.
         */
        line = new Line();

        /*
         * Filled composition diamond.
         */
        diamond = new Polygon();

        /*
         * Endpoint handles.
         */
        startHandle = createHandle();
        endHandle = createHandle();

        getChildren().addAll(
                hitLine,
                line,
                diamond,
                startHandle,
                endHandle
        );

        updateLine();

        /*
         * Dragging the line or diamond
         * moves the whole relationship.
         */
        makeDraggable(
                hitLine,
                line,
                diamond
        );

        /*
         * Dragging either square changes
         * only that endpoint.
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

        double angle = Math.atan2(
                endY - startY,
                endX - startX
        );

        /*
         * Back of the diamond.
         */
        double backX =
                endX - DIAMOND_LENGTH * Math.cos(angle);

        double backY =
                endY - DIAMOND_LENGTH * Math.sin(angle);

        /*
         * Visible line stops at the back
         * of the diamond.
         */
        line.setStartX(startX);
        line.setStartY(startY);

        line.setEndX(backX);
        line.setEndY(backY);

        /*
         * Invisible hitbox covers the whole
         * relationship.
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

        updateDiamond();
    }

    private void updateDiamond() {

        double angle = Math.atan2(
                endY - startY,
                endX - startX
        );

        /*
         * Front tip of diamond.
         */
        double tipX = endX;
        double tipY = endY;

        /*
         * Back point of diamond.
         */
        double backX =
                endX - DIAMOND_LENGTH * Math.cos(angle);

        double backY =
                endY - DIAMOND_LENGTH * Math.sin(angle);

        /*
         * Center of diamond.
         */
        double centerX =
                endX
                - (DIAMOND_LENGTH / 2)
                * Math.cos(angle);

        double centerY =
                endY
                - (DIAMOND_LENGTH / 2)
                * Math.sin(angle);

        /*
         * Perpendicular direction.
         */
        double perpendicularX =
                -Math.sin(angle);

        double perpendicularY =
                Math.cos(angle);

        /*
         * Side points.
         */
        double leftX =
                centerX
                + DIAMOND_WIDTH * perpendicularX;

        double leftY =
                centerY
                + DIAMOND_WIDTH * perpendicularY;

        double rightX =
                centerX
                - DIAMOND_WIDTH * perpendicularX;

        double rightY =
                centerY
                - DIAMOND_WIDTH * perpendicularY;

        diamond.getPoints().setAll(
                tipX, tipY,
                leftX, leftY,
                backX, backY,
                rightX, rightY
        );
    }

    @Override
    protected void updateSelectionStyle(
            boolean selected) {

        Color color =
                selected
                        ? Color.web("#4f8cff")
                        : Color.WHITE;

        double width =
                selected ? 2 : 1;

        /*
         * Solid line.
         */
        line.setStroke(color);
        line.setStrokeWidth(width);

        /*
         * FILLED diamond.
         */
        diamond.setFill(color);
        diamond.setStroke(color);
        diamond.setStrokeWidth(width);

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