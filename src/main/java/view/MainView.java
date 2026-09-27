package view;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class MainView {

    private final BorderPane root;

    public MainView() {
        root = new BorderPane();

        CanvasView canvasView = new CanvasView();

        root.setTop(new TopMenuView().getView());
        root.setLeft(new ShapeMenuView(
            canvasView::addClassBox,
            canvasView::addInterfaceBox,
            canvasView::addAbstractClassBox,
            canvasView::addNoteBox,
            canvasView::addPackage,
            canvasView::addAssociation,
            canvasView::addDependency,
            canvasView::addInheritance,
            canvasView::addImplementation,
            canvasView::addAggregation,
            canvasView::addComposition,
            canvasView::changeSelectedColor
        ).getView());
        
        root.setCenter(canvasView.getView());
    }

    public Parent getRoot() {
        return root;
    }
}