package view.export;

import javafx.scene.layout.Pane;

public class DiagramNew {

    private final Pane shapeLayer;

    public DiagramNew(Pane shapeLayer) {
        this.shapeLayer = shapeLayer;
    }

    // Remove all UML boxes and arrows.
    public void clearDiagram() {
        shapeLayer.getChildren().clear();
    }
}