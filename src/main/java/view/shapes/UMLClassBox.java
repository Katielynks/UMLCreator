package view.shapes;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class UMLClassBox extends UMLStandardBox {

    public UMLClassBox(
            DoubleSupplier zoomSupplier,
            Consumer<UMLShapeBox> onSelected) {

        super(
                zoomSupplier,
                onSelected,
                100,
                100,
                180,
                110
        );

        VBox classSection = createSection(true);
        VBox attributeSection = createSection(true);
        VBox operationSection = createSection(false);

        TextField className =
                createTextField("Class", true);

        TextField attribute =
                createTextField("- attribute", false);

        TextField operation =
                createTextField("+ operation()", false);

        addEnterHandler(
                classSection,
                className,
                true
        );

        addEnterHandler(
                attributeSection,
                attribute,
                false
        );

        addEnterHandler(
                operationSection,
                operation,
                false
        );

        classSection.getChildren().add(className);

        attributeSection.getChildren().add(attribute);

        operationSection.getChildren().add(operation);

        finishLayout(
                classSection,
                attributeSection,
                operationSection
        );

    }

}