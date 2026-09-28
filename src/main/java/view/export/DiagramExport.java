
package view.export;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

public class DiagramExport {

    private final StackPane canvasArea;
    private final Pane shapeLayer;

    private final Supplier<String> patternSupplier;
    private final DoubleSupplier zoomSupplier;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public DiagramExport(
            StackPane canvasArea,
            Pane shapeLayer,
            Supplier<String> patternSupplier,
            DoubleSupplier zoomSupplier) {

        this.canvasArea = canvasArea;
        this.shapeLayer = shapeLayer;
        this.patternSupplier = patternSupplier;
        this.zoomSupplier = zoomSupplier;
    }

    public void exportJson() {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Export UML Diagram");
        fileChooser.setInitialFileName("uml-diagram.json");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "UML Diagram JSON",
                        "*.json"
                )
        );

        File file = fileChooser.showSaveDialog(
                canvasArea.getScene().getWindow()
        );

        if (file == null) {
            return;
        }

        // Ensure the file has the correct extension.
        if (!file.getName()
                .toLowerCase(Locale.ROOT)
                .endsWith(".json")) {

            file = new File(
                    file.getParentFile(),
                    file.getName() + ".json"
            );
        }

        try {

            JsonObject diagram = DiagramJson.exportDiagram(
                    shapeLayer,
                    patternSupplier.get(),
                    zoomSupplier.getAsDouble()
            );

            try (Writer writer = Files.newBufferedWriter(
                    file.toPath(),
                    StandardCharsets.UTF_8)) {

                gson.toJson(diagram, writer);
            }

        } catch (IOException | RuntimeException exception) {

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Export Failed");
            alert.setHeaderText(
                    "Could not export the UML diagram."
            );

            alert.setContentText(
                    exception.getMessage()
            );

            alert.showAndWait();
        }
    }
}
