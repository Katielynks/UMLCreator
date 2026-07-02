package view;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class MainView {

    private final BorderPane root;

    public MainView() {
        root = new BorderPane();

        root.setTop(new TopMenuView().getView());
        root.setLeft(new ShapeMenuView().getView());
        root.setCenter(new CanvasView().getView());
    }

    public Parent getRoot() {
        return root;
    }
}