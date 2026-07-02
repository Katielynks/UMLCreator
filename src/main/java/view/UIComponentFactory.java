package view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class UIComponentFactory {

    private UIComponentFactory() {
        // Prevent creating objects of this helper class.
    }

    public static Button createMenuButton(String text) {
        Button button = new Button(text);

        button.setPrefWidth(150);

        button.setStyle(
                "-fx-background-color: #314172ff;" +
                "-fx-text-fill: #ffffffff;" +
                "-fx-font-size: 13px;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-padding: 8 12 8 12;" +
                "-fx-background-radius: 6;" +
                "-fx-border-color: #000000ff;" +
                "-fx-border-radius: 6;"
        );

        return button;
    }

    public static Button createSmallCanvasButton(String text) {
        Button button = new Button(text);

        button.setStyle(
                "-fx-background-color: #314172ff;" +
                "-fx-text-fill: #ffffffff;" +
                "-fx-font-size: 12px;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-padding: 6 10 6 10;" +
                "-fx-background-radius: 6;" +
                "-fx-border-color: #000000ff;" +
                "-fx-border-radius: 6;"
        );

        return button;
    }

    public static Label createTitleLabel(String text) {
        Label label = new Label(text);

        label.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #cad7e7ff;" +
                "-fx-padding: 0 15 0 0;"
        );

        return label;
    }

    public static Label createSectionLabel(String text) {
        Label label = new Label(text);

        label.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #cad7e7ff;" +
                "-fx-padding: 10 0 5 0;"
        );

        return label;
    }
}