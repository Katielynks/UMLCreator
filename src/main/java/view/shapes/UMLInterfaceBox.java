package view.shapes;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class UMLInterfaceBox extends UMLStandardBox {

    public UMLInterfaceBox(
            DoubleSupplier zoomSupplier,
            Consumer<UMLShapeBox> onSelected) {

        super(
                zoomSupplier,
                onSelected,
                120,
                120,
                180,
                110
        );

        VBox classSection = createSection(true);
        VBox attributeSection = createSection(true);
        VBox operationSection = createSection(false);

        Label interfaceLabel =
                createInterfaceLabel();

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

        classSection.getChildren().addAll(
                interfaceLabel,
                className
        );

        attributeSection.getChildren().add(attribute);

        operationSection.getChildren().add(operation);

        finishLayout(
                classSection,
                attributeSection,
                operationSection
        );
    }

    private Label createInterfaceLabel() {

        Label label =
                new Label("<<Interface>>");

        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);

        label.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 11px;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-font-style: italic;" +
                "-fx-padding: 4 4 0 4;"
        );

        return label;
    }
}