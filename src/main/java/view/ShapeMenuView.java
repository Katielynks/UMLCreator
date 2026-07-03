package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ShapeMenuView {

    private final ScrollPane shapeMenu;

    public ShapeMenuView() {
        shapeMenu = createShapeMenu();
    }

    public ScrollPane getView() {
        return shapeMenu;
    }

    private ScrollPane createShapeMenu() {
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

        HBox colorPicker = createColorPickerControl();

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
                compositionButton,

                UIComponentFactory.createSectionLabel("Color"),
                colorPicker
        );

        ScrollPane scrollPane = new ScrollPane(menu);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(180);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane.getStyleClass().add("shape-menu-scroll");

        return scrollPane;
    }

private HBox createColorPickerControl() {
    ColorPicker colorPicker = new ColorPicker(Color.web("#000000ff"));

    Rectangle colorPreview = new Rectangle(110, 14);
    colorPreview.setFill(colorPicker.getValue());
    colorPreview.setStroke(Color.web("#cad7e7"));
    colorPreview.setArcWidth(3);
    colorPreview.setArcHeight(3);

    Label arrowLabel = new Label("⌄");
    arrowLabel.setStyle(
            "-fx-text-fill: #cad7e7;" +
            "-fx-font-size: 13px;"
    );

    HBox colorControl = new HBox(8);
    colorControl.setAlignment(Pos.CENTER);
    colorControl.setPrefWidth(150);
    colorControl.setStyle(
            "-fx-background-color: #314172ff;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: #000000ff;" +
            "-fx-border-radius: 6;" +
            "-fx-padding: 6 8 6 8;"
    );

    colorPicker.setOpacity(0);
    colorPicker.setMinSize(1, 1);
    colorPicker.setPrefSize(1, 1);
    colorPicker.setMaxSize(1, 1);

    colorControl.getChildren().addAll(colorPreview, arrowLabel, colorPicker);

    colorControl.setOnMouseClicked(event -> {
        colorPicker.show();
    });

    colorPicker.setOnAction(event -> {
        colorPreview.setFill(colorPicker.getValue());
    });

    return colorControl;
}

}