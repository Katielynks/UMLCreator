
package view.export;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;

import view.arrows.ARRRelationship;
import view.shapes.UMLShapeBox;

public class DiagramSave {

    private static final double PADDING = 40;
    private static final int MAX_IMAGE_SIZE = 2400;

    private final StackPane canvasArea;
    private final Canvas canvas;
    private final Pane shapeLayer;
    private final Scale shapeScale;

    public DiagramSave(
            StackPane canvasArea,
            Canvas canvas,
            Pane shapeLayer,
            Scale shapeScale) {

        this.canvasArea = canvasArea;
        this.canvas = canvas;
        this.shapeLayer = shapeLayer;
        this.shapeScale = shapeScale;
    }

    // Open the save dialog and export the diagram.
    public void saveDiagramAsJpeg(String canvasPattern) {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Save UML Diagram");
        fileChooser.setInitialFileName("uml-diagram.jpg");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "JPEG Image",
                        "*.jpg",
                        "*.jpeg"
                )
        );

        File file = fileChooser.showSaveDialog(
                canvasArea.getScene().getWindow()
        );

        if (file == null) {
            return;
        }

        // Add .jpg if the user did not provide an extension.
        String filename = file.getName().toLowerCase(Locale.ROOT);

        if (!filename.endsWith(".jpg")
                && !filename.endsWith(".jpeg")) {

            file = new File(
                    file.getParentFile(),
                    file.getName() + ".jpg"
            );
        }

        try {

            WritableImage image = captureDiagram(canvasPattern);

            writeJpeg(image, file);

        } catch (IOException | RuntimeException exception) {

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Save Failed");
            alert.setHeaderText("Could not save the diagram.");
            alert.setContentText(exception.getMessage());

            alert.showAndWait();
        }
    }

    // Capture the diagram without selection highlights.
    private WritableImage captureDiagram(String canvasPattern) {

        double oldScaleX = shapeScale.getX();
        double oldScaleY = shapeScale.getY();

        List<Node> previouslySelected = new ArrayList<>();

        try {

            // Export at normal zoom.
            shapeScale.setX(1.0);
            shapeScale.setY(1.0);

            // Temporarily remove selection highlights and handles.
            for (Node node : shapeLayer.getChildren()) {

                if (node instanceof UMLShapeBox shape
                        && shape.isSelected()) {

                    previouslySelected.add(node);
                    shape.setSelected(false);

                } else if (node instanceof ARRRelationship relationship
                        && relationship.isSelected()) {

                    previouslySelected.add(node);
                    relationship.setSelected(false);
                }
            }

            shapeLayer.applyCss();
            shapeLayer.layout();

            // Include at least the visible canvas.
            double[] bounds = {
                    0,
                    0,
                    Math.max(1, canvas.getWidth()),
                    Math.max(1, canvas.getHeight())
            };

            // Extend the image to include all components.
            for (Node node : shapeLayer.getChildren()) {

                includeBounds(node, bounds);

                // Include arrow endpoints outside their parent pane.
                if (node instanceof ARRRelationship relationship) {

                    for (Node part : relationship.getChildren()) {
                        includeBounds(part, bounds);
                    }
                }
            }

            double left = bounds[0];
            double top = bounds[1];

            int width = (int) Math.ceil(bounds[2] - left);
            int height = (int) Math.ceil(bounds[3] - top);

            if ((long) width * height > 20_000_000L) {

                throw new IllegalArgumentException(
                        "The diagram is too large to export."
                );
            }

            // Capture the shapes and arrows.
            SnapshotParameters parameters = new SnapshotParameters();

            parameters.setFill(Color.TRANSPARENT);

            parameters.setViewport(
                    new Rectangle2D(
                            left,
                            top,
                            width,
                            height
                    )
            );

            WritableImage shapesImage =
                    shapeLayer.snapshot(parameters, null);

            // Create a separate canvas for the JPEG.
            Canvas exportCanvas = new Canvas(width, height);

            GraphicsContext gc =
                    exportCanvas.getGraphicsContext2D();

            drawExportBackground(
                    gc,
                    width,
                    height,
                    canvasPattern
            );

            // Draw the shapes and arrows over the background.
            gc.drawImage(shapesImage, 0, 0);

            return exportCanvas.snapshot(null, null);

        } finally {

            // Restore the editor zoom.
            shapeScale.setX(oldScaleX);
            shapeScale.setY(oldScaleY);

            // Restore the previously selected object.
            for (Node node : previouslySelected) {

                if (node instanceof UMLShapeBox shape) {

                    shape.setSelected(true);

                } else if (node instanceof ARRRelationship relationship) {

                    relationship.setSelected(true);
                }
            }
        }
    }

    // Expand the capture area to include a component.
    private void includeBounds(Node node, double[] bounds) {

        if (!node.isVisible()) {
            return;
        }

        Bounds nodeBounds = shapeLayer.sceneToLocal(
                node.localToScene(node.getBoundsInLocal())
        );

        bounds[0] = Math.min(
                bounds[0],
                nodeBounds.getMinX() - PADDING
        );

        bounds[1] = Math.min(
                bounds[1],
                nodeBounds.getMinY() - PADDING
        );

        bounds[2] = Math.max(
                bounds[2],
                nodeBounds.getMaxX() + PADDING
        );

        bounds[3] = Math.max(
                bounds[3],
                nodeBounds.getMaxY() + PADDING
        );
    }

    // Draw the selected background pattern.
    private void drawExportBackground(
            GraphicsContext gc,
            int width,
            int height,
            String canvasPattern) {

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        double spacing = 25;

        if ("lined".equals(canvasPattern)) {

            gc.setStroke(Color.rgb(70, 70, 70));
            gc.setLineWidth(1);

            for (double x = 0; x < width; x += spacing) {
                gc.strokeLine(x, 0, x, height);
            }

            for (double y = 0; y < height; y += spacing) {
                gc.strokeLine(0, y, width, y);
            }

        } else if ("dotted".equals(canvasPattern)) {

            gc.setFill(Color.rgb(90, 90, 90));

            for (double x = 0; x < width; x += spacing) {

                for (double y = 0; y < height; y += spacing) {

                    gc.fillOval(x, y, 3, 3);
                }
            }
        }
    }

    // Convert the captured image to JPEG.
    private void writeJpeg(
            WritableImage image,
            File file) throws IOException {

        int sourceWidth = (int) image.getWidth();
        int sourceHeight = (int) image.getHeight();

        BufferedImage original = new BufferedImage(
                sourceWidth,
                sourceHeight,
                BufferedImage.TYPE_INT_RGB
        );

        // Copy pixels from JavaFX to BufferedImage.
        for (int y = 0; y < sourceHeight; y++) {

            for (int x = 0; x < sourceWidth; x++) {

                int color = image.getPixelReader().getArgb(x, y);

                original.setRGB(x, y, color);
            }
        }

        // Limit the longest side to 2400 pixels.
        double scale = Math.min(
                1.0,
                (double) MAX_IMAGE_SIZE
                        / Math.max(sourceWidth, sourceHeight)
        );

        int outputWidth = Math.max(
                1,
                (int) Math.round(sourceWidth * scale)
        );

        int outputHeight = Math.max(
                1,
                (int) Math.round(sourceHeight * scale)
        );

        BufferedImage output = new BufferedImage(
                outputWidth,
                outputHeight,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics = output.createGraphics();

        try {

            graphics.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );

            graphics.drawImage(
                    original,
                    0,
                    0,
                    outputWidth,
                    outputHeight,
                    null
            );

        } finally {

            graphics.dispose();
        }

        if (!ImageIO.write(output, "jpg", file)) {

            throw new IOException(
                    "No JPEG writer is available."
            );
        }
    }
}
