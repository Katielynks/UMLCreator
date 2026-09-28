package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class TopMenuView {

    private final HBox topMenu;

    private final Runnable onSaveClicked;
    private final Runnable onNewClicked;
    private final Runnable onOpenClicked;
    private final Runnable onExportClicked;

    public TopMenuView(
        Runnable onNewClicked,
        Runnable onSaveClicked,
        Runnable onOpenClicked,
        Runnable onExportClicked
        ) {
        this.onSaveClicked = onSaveClicked;
        this.onNewClicked = onNewClicked;
        this.onOpenClicked = onOpenClicked;
        this.onExportClicked = onExportClicked;
        topMenu = createTopMenu();
    }

    public HBox getView() {
        return topMenu;
    }

    private HBox createTopMenu() {
        Button newButton = UIComponentFactory.createMenuButton("New");
        newButton.setOnAction(event -> onNewClicked.run());
        Button openButton = UIComponentFactory.createMenuButton("Open");
        openButton.setOnAction(event -> onOpenClicked.run());
        Button saveButton = UIComponentFactory.createMenuButton("Save");
        saveButton.setOnAction(event -> onSaveClicked.run());
        Button exportButton = UIComponentFactory.createMenuButton("Export");
        exportButton.setOnAction(event -> onExportClicked.run());

        HBox menu = new HBox(10);
        menu.setPadding(new Insets(10));
        menu.setAlignment(Pos.CENTER_LEFT);
        menu.setStyle("-fx-background-color: #13192bff;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        menu.getChildren().addAll(
                UIComponentFactory.createTitleLabel("UML Diagram Creator"),
                spacer,
                newButton,
                openButton,
                saveButton,
                exportButton
        );

        return menu;
    }
}