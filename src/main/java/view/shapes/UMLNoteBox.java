package view.shapes;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class UMLNoteBox extends UMLStandardBox {

    public UMLNoteBox(
            DoubleSupplier zoomSupplier,
            Consumer<UMLShapeBox> onSelected) {

        super(
                zoomSupplier,
                onSelected,
                160,
                160,
                180,
                90
        );

        VBox textSection = createSection(false);

        TextField firstLine =
                createTextField("Note", false);

        addEnterHandler(
                textSection,
                firstLine,
                false
        );

        textSection.getChildren().add(firstLine);

        finishLayout(textSection);
    }
}