package view;

import java.util.function.Consumer;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public abstract class UMLShapeBox extends VBox {

    private boolean selected = false;
    private Color shapeColor = Color.WHITE;
    private final Consumer<UMLShapeBox> onSelected;

    public UMLShapeBox(Consumer<UMLShapeBox> onSelected) {
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

    protected abstract void updateSelectionStyle(boolean selected);


    public void setShapeColor(Color color) {
        this.shapeColor = color;
        updateSelectionStyle(isSelected());
    }

    protected String getShapeColorCss() {
        return String.format(
                "#%02X%02X%02X",
                Math.round(shapeColor.getRed() * 255),
                Math.round(shapeColor.getGreen() * 255),
                Math.round(shapeColor.getBlue() * 255)
        );
    }
}