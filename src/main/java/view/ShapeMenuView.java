package view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ShapeMenuView {

    private final VBox shapeMenu;

    public ShapeMenuView() {
        shapeMenu = createShapeMenu();
    }

    public VBox getView() {
        return shapeMenu;
    }

    private VBox createShapeMenu() {
        Button classButton = UIComponentFactory.createMenuButton("Class");
        Button interfaceButton = UIComponentFactory.createMenuButton("Interface");
        Button abstractClassButton = UIComponentFactory.createMenuButton("Abstract Class");
        Button noteButton = UIComponentFactory.createMenuButton("Note");
        Button packageButton = UIComponentFactory.createMenuButton("Package");

        Button associationButton = UIComponentFactory.createMenuButton("Association");
        Button dependencyButton = UIComponentFactory.createMenuButton("Dependency");
        Button inheritanceButton = UIComponentFactory.createMenuButton("Inheritance");
        Button implementationButton = UIComponentFactory.createMenuButton("Implementation");
        Button aggregationButton = UIComponentFactory.createMenuButton("Aggregation");
        Button compositionButton = UIComponentFactory.createMenuButton("Composition");

        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setPrefWidth(180);
        menu.setStyle("-fx-background-color: #13192bff;");

        menu.getChildren().addAll(
                UIComponentFactory.createSectionLabel("Shapes"),
                classButton,
                interfaceButton,
                abstractClassButton,
                noteButton,
                packageButton,

                UIComponentFactory.createSectionLabel("Relationships"),
                associationButton,
                dependencyButton,
                inheritanceButton,
                implementationButton,
                aggregationButton,
                compositionButton
        );

        return menu;
    }
}