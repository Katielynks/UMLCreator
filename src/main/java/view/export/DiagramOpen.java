
package view.export;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

public class DiagramOpen {

    private static final long MAX_FILE_SIZE = 10_000_000;

    private final StackPane canvasArea;
    private final Pane shapeLayer;

    private final DoubleSupplier zoomSupplier;
    private final Consumer<Object> onSelected;

    private final BiConsumer<String, Double> onSettingsLoaded;

    public DiagramOpen(
            StackPane canvasArea,
            Pane shapeLayer,
            DoubleSupplier zoomSupplier,
            Consumer<Object> onSelected,
            BiConsumer<String, Double> onSettingsLoaded) {

        this.canvasArea = canvasArea;
        this.shapeLayer = shapeLayer;
        this.zoomSupplier = zoomSupplier;
        this.onSelected = onSelected;
        this.onSettingsLoaded = onSettingsLoaded;
    }

    public void openJson() {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Open UML Diagram");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "UML Diagram JSON",
                        "*.json"
                )
        );

        File file = fileChooser.showOpenDialog(
                canvasArea.getScene().getWindow()
        );

        if (file == null) {
            return;
        }

        try {

            if (Files.size(file.toPath()) > MAX_FILE_SIZE) {

                throw new IllegalArgumentException(
                        "The diagram file is too large."
                );
            }

            JsonElement json;

            // Read the selected JSON file.
            try (Reader reader = Files.newBufferedReader(
                    file.toPath(),
                    StandardCharsets.UTF_8)) {

                json = JsonParser.parseReader(reader);
            }

            if (!json.isJsonObject()) {

                throw new IllegalArgumentException(
                        "Invalid UML diagram file."
                );
            }

            JsonObject root = json.getAsJsonObject();

            // Reconstruct the new diagram first.
            // The current canvas is not cleared yet.
            DiagramJson.LoadedDiagram loaded =
                    DiagramJson.importDiagram(
                            root,
                            zoomSupplier,
                            onSelected
                    );

            // Replace the current diagram.
            shapeLayer.getChildren().setAll(
                    loaded.elements()
            );

            // Restore background and zoom.
            onSettingsLoaded.accept(
                    loaded.pattern(),
                    loaded.zoom()
            );

            // Restore the selected component, if any.
            int selectedIndex = loaded.selectedIndex();

            Node selectedNode = selectedIndex >= 0
                    ? loaded.elements().get(selectedIndex)
                    : null;

            onSelected.accept(selectedNode);

        } catch (IOException | RuntimeException exception) {

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Open Failed");
            alert.setHeaderText(
                    "Could not open the UML diagram."
            );

            alert.setContentText(
                    exception.getMessage()
            );

            alert.showAndWait();
        }
    }
}
