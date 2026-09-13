package view;

import java.util.function.Consumer;

import javafx.scene.layout.VBox;

public abstract class UMLShapeBox extends VBox {

    private boolean selected = false;

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
}