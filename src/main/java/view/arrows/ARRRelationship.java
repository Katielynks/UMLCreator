package view.arrows;

import java.util.function.Consumer;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public abstract class ARRRelationship extends Pane {

    private boolean selected = false;

    private final Consumer<ARRRelationship> onSelected;

    private double startMouseX;
    private double startMouseY;

    private double startLayoutX;
    private double startLayoutY;

    private Color shapeColor = Color.WHITE;

    public ARRRelationship(
            Consumer<ARRRelationship> onSelected) {

        this.onSelected = onSelected;
    }

    protected void selectThis() {
        onSelected.accept(this);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateSelectionStyle(selected);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setShapeColor(Color color) {
        this.shapeColor = color;
        updateSelectionStyle(isSelected());
    }

    protected Color getShapeColor() {
        return shapeColor;
    }

    /*
     * Allows the whole relationship to be dragged.
     *
     * Pass in the parts of the arrow that should
     * allow dragging, such as the hit line,
     * visible line, and arrowhead.
     */
    protected void makeDraggable(Node... dragTargets) {

        for (Node target : dragTargets) {

            target.setOnMousePressed(event -> {

                selectThis();

                Point2D point =
                        getParent().sceneToLocal(
                                event.getSceneX(),
                                event.getSceneY()
                        );

                startMouseX = point.getX();
                startMouseY = point.getY();

                startLayoutX = getLayoutX();
                startLayoutY = getLayoutY();

                event.consume();
            });

            target.setOnMouseDragged(event -> {

                Point2D point =
                        getParent().sceneToLocal(
                                event.getSceneX(),
                                event.getSceneY()
                        );

                double deltaX =
                        point.getX() - startMouseX;

                double deltaY =
                        point.getY() - startMouseY;

                setLayoutX(
                        startLayoutX + deltaX
                );

                setLayoutY(
                        startLayoutY + deltaY
                );

                event.consume();
            });
        }
    }

    protected abstract void updateSelectionStyle(
            boolean selected);
}