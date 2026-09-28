
package view.export;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import view.shapes.*;
import view.arrows.*;

public final class DiagramJson {

    private static final int VERSION = 1;

    private DiagramJson() {
    }

    // Data returned after reading and validating a JSON file.
    public record LoadedDiagram(
            List<Node> elements,
            String pattern,
            double zoom,
            int selectedIndex) {
    }

    // Convert the current diagram into JSON.
    public static JsonObject exportDiagram(
            Pane shapeLayer,
            String pattern,
            double zoom) {

        JsonObject root = new JsonObject();

        root.addProperty("format", "umlcreator");
        root.addProperty("version", VERSION);

        JsonObject canvas = new JsonObject();

        canvas.addProperty("pattern", pattern);
        canvas.addProperty("zoom", zoom);

        root.add("canvas", canvas);

        JsonArray elements = new JsonArray();

        int selectedIndex = -1;
        int index = 0;

        for (Node node : shapeLayer.getChildren()) {

            JsonObject element = new JsonObject();

            // Information shared by boxes and arrows.
            element.addProperty(
                    "type",
                    node.getClass().getSimpleName()
            );

            element.addProperty("x", node.getLayoutX());
            element.addProperty("y", node.getLayoutY());

            if (node instanceof UMLShapeBox shape) {

                element.addProperty("kind", "shape");

                element.addProperty(
                        "width",
                        shape.getPrefWidth()
                );

                element.addProperty(
                        "height",
                        shape.getPrefHeight()
                );

                element.addProperty(
                        "color",
                        shape.getDiagramColor().toString()
                );

                element.add(
                        "sections",
                        exportTextSections(shape)
                );

                if (shape.isSelected()) {
                    selectedIndex = index;
                }

            } else if (node instanceof ARRRelationship arrow) {

                element.addProperty("kind", "arrow");

                element.addProperty(
                        "width",
                        arrow.getPrefWidth()
                );

                element.addProperty(
                        "height",
                        arrow.getPrefHeight()
                );

                element.addProperty(
                        "color",
                        arrow.getDiagramColor().toString()
                );

                double[] endpoints = arrow.getEndpoints();

                element.addProperty(
                        "startX",
                        endpoints[0]
                );

                element.addProperty(
                        "startY",
                        endpoints[1]
                );

                element.addProperty(
                        "endX",
                        endpoints[2]
                );

                element.addProperty(
                        "endY",
                        endpoints[3]
                );

                if (arrow.isSelected()) {
                    selectedIndex = index;
                }

            } else {

                // Never silently discard an unsupported element.
                throw new IllegalArgumentException(
                        "Unsupported diagram element: "
                                + node.getClass().getSimpleName()
                );
            }

            elements.add(element);
            index++;
        }

        root.addProperty("selectedIndex", selectedIndex);
        root.add("elements", elements);

        return root;
    }

    // Read JSON and reconstruct the diagram elements.
    public static LoadedDiagram importDiagram(
            JsonObject root,
            DoubleSupplier zoomSupplier,
            Consumer<Object> onSelected) {

        if (!"umlcreator".equals(readString(root, "format"))) {
            throw new IllegalArgumentException(
                    "This is not a UMLCreator diagram."
            );
        }

        if (readInt(root, "version") != VERSION) {
            throw new IllegalArgumentException(
                    "Unsupported diagram file version."
            );
        }

        JsonObject canvas = readObject(root, "canvas");

        String pattern = readString(canvas, "pattern");
        double zoom = readDouble(canvas, "zoom");

        if (!pattern.equals("blank")
                && !pattern.equals("lined")
                && !pattern.equals("dotted")) {

            throw new IllegalArgumentException(
                    "Invalid canvas background."
            );
        }

        if (zoom < 0.4 || zoom > 3.0) {
            throw new IllegalArgumentException(
                    "Invalid canvas zoom."
            );
        }

        JsonArray elements = readArray(root, "elements");

        if (elements.size() > 10000) {
            throw new IllegalArgumentException(
                    "The diagram contains too many elements."
            );
        }

        List<Node> restored = new ArrayList<>();

        for (JsonElement item : elements) {

            if (!item.isJsonObject()) {
                throw new IllegalArgumentException(
                        "Invalid diagram element."
                );
            }

            JsonObject data = item.getAsJsonObject();

            String kind = readString(data, "kind");
            String type = readString(data, "type");

            double x = readDouble(data, "x");
            double y = readDouble(data, "y");

            double width = readDouble(data, "width");
            double height = readDouble(data, "height");

            if (width <= 0 || height <= 0
                    || width > 100000 || height > 100000) {

                throw new IllegalArgumentException(
                        "Invalid element dimensions."
                );
            }

            Color color = Color.web(
                    readString(data, "color")
            );

            Node node;

            if ("shape".equals(kind)) {

                UMLShapeBox shape = createShape(
                        type,
                        zoomSupplier,
                        onSelected
                );

                restoreTextSections(
                        shape,
                        readArray(data, "sections")
                );

                shape.setShapeColor(color);

                // Restore dimensions after adding text lines.
                shape.setPrefWidth(width);
                shape.setPrefHeight(height);

                if (shape instanceof UMLPackage packageBox) {

                    packageBox.restoreDiagramSize(
                            width,
                            height
                    );
                }

                node = shape;

            } else if ("arrow".equals(kind)) {

                ARRRelationship arrow = createArrow(
                        type,
                        onSelected
                );

                arrow.setEndpoints(
                        readDouble(data, "startX"),
                        readDouble(data, "startY"),
                        readDouble(data, "endX"),
                        readDouble(data, "endY")
                );

                arrow.setPrefWidth(width);
                arrow.setPrefHeight(height);

                arrow.setShapeColor(color);

                node = arrow;

            } else {

                throw new IllegalArgumentException(
                        "Unknown diagram element kind: " + kind
                );
            }

            node.setLayoutX(x);
            node.setLayoutY(y);

            restored.add(node);
        }

        int selectedIndex = readInt(
                root,
                "selectedIndex"
        );

        if (selectedIndex < -1
                || selectedIndex >= restored.size()) {

            throw new IllegalArgumentException(
                    "Invalid selected element."
            );
        }

        return new LoadedDiagram(
                restored,
                pattern,
                zoom,
                selectedIndex
        );
    }

    // Create the correct box from its saved type.
    private static UMLShapeBox createShape(
            String type,
            DoubleSupplier zoomSupplier,
            Consumer<Object> onSelected) {

        return switch (type) {

            case "UMLClassBox" ->
                    new UMLClassBox(
                            zoomSupplier,
                            shape -> onSelected.accept(shape)
                    );

            case "UMLInterfaceBox" ->
                    new UMLInterfaceBox(
                            zoomSupplier,
                            shape -> onSelected.accept(shape)
                    );

            case "UMLAbstractClassBox" ->
                    new UMLAbstractClassBox(
                            zoomSupplier,
                            shape -> onSelected.accept(shape)
                    );

            case "UMLNoteBox" ->
                    new UMLNoteBox(
                            zoomSupplier,
                            shape -> onSelected.accept(shape)
                    );

            case "UMLPackage" ->
                    new UMLPackage(
                            zoomSupplier,
                            shape -> onSelected.accept(shape)
                    );

            default -> throw new IllegalArgumentException(
                    "Unknown UML shape: " + type
            );
        };
    }

    // Create the correct arrow from its saved type.
    private static ARRRelationship createArrow(
            String type,
            Consumer<Object> onSelected) {

        return switch (type) {

            case "ARRAssociation" ->
                    new ARRAssociation(
                            arrow -> onSelected.accept(arrow)
                    );

            case "ARRDependency" ->
                    new ARRDependency(
                            arrow -> onSelected.accept(arrow)
                    );

            case "ARRInheritance" ->
                    new ARRInheritance(
                            arrow -> onSelected.accept(arrow)
                    );

            case "ARRImplementation" ->
                    new ARRImplementation(
                            arrow -> onSelected.accept(arrow)
                    );

            case "ARRAggregation" ->
                    new ARRAggregation(
                            arrow -> onSelected.accept(arrow)
                    );

            case "ARRComposition" ->
                    new ARRComposition(
                            arrow -> onSelected.accept(arrow)
                    );

            default -> throw new IllegalArgumentException(
                    "Unknown UML arrow: " + type
            );
        };
    }

    // Save the text in every editable section.
    private static JsonArray exportTextSections(
            UMLShapeBox shape) {

        JsonArray sectionsJson = new JsonArray();

        for (VBox section : findTextSections(shape)) {

            JsonArray lines = new JsonArray();

            for (TextField field : directTextFields(section)) {
                lines.add(field.getText());
            }

            sectionsJson.add(lines);
        }

        return sectionsJson;
    }

    // Restore text and recreate lines added using Enter.
    private static void restoreTextSections(
            UMLShapeBox shape,
            JsonArray savedSections) {

        List<VBox> sections = findTextSections(shape);

        if (sections.size() != savedSections.size()) {

            throw new IllegalArgumentException(
                    "Text section count does not match: "
                            + shape.getClass().getSimpleName()
            );
        }

        for (int i = 0; i < sections.size(); i++) {

            VBox section = sections.get(i);

            JsonElement sectionData = savedSections.get(i);

            if (!sectionData.isJsonArray()) {
                throw new IllegalArgumentException(
                        "Invalid text section."
                );
            }

            JsonArray savedLines = sectionData.getAsJsonArray();

            if (savedLines.size() > 500) {
                throw new IllegalArgumentException(
                        "Too many text lines."
                );
            }

            List<TextField> fields = directTextFields(section);

            if (savedLines.size() < fields.size()) {
                throw new IllegalArgumentException(
                        "The saved section has missing lines."
                );
            }

            // Use the existing Enter handler to add extra fields.
            while (fields.size() < savedLines.size()) {

                TextField lastField =
                        fields.get(fields.size() - 1);

                int previousCount = fields.size();

                Event.fireEvent(
                        lastField,
                        new ActionEvent()
                );

                fields = directTextFields(section);

                if (fields.size() != previousCount + 1) {
                    throw new IllegalArgumentException(
                            "Could not restore a text line."
                    );
                }
            }

            // Restore the actual text in every field.
            for (int j = 0; j < savedLines.size(); j++) {

                JsonElement value = savedLines.get(j);

                if (!value.isJsonPrimitive()
                        || !value.getAsJsonPrimitive().isString()) {

                    throw new IllegalArgumentException(
                            "Invalid text value."
                    );
                }

                String text = value.getAsString();

                if (text.length() > 10000) {
                    throw new IllegalArgumentException(
                            "A text line is too long."
                    );
                }

                fields.get(j).setText(text);
            }
        }
    }

    // Find VBoxes that directly contain editable text fields.
    private static List<VBox> findTextSections(Node root) {

        List<VBox> result = new ArrayList<>();

        collectTextSections(root, result);

        return result;
    }

    private static void collectTextSections(
            Node node,
            List<VBox> result) {

        if (node instanceof VBox box
                && !directTextFields(box).isEmpty()) {

            result.add(box);
        }

        if (node instanceof Parent parent) {

            for (Node child : parent.getChildrenUnmodifiable()) {

                collectTextSections(
                        child,
                        result
                );
            }
        }
    }

    private static List<TextField> directTextFields(
            VBox section) {

        List<TextField> fields = new ArrayList<>();

        for (Node child : section.getChildren()) {

            if (child instanceof TextField field) {
                fields.add(field);
            }
        }

        return fields;
    }

    // JSON validation helpers.
    private static JsonObject readObject(
            JsonObject source,
            String key) {

        JsonElement value = source.get(key);

        if (value == null || !value.isJsonObject()) {

            throw new IllegalArgumentException(
                    "Missing or invalid object: " + key
            );
        }

        return value.getAsJsonObject();
    }

    private static JsonArray readArray(
            JsonObject source,
            String key) {

        JsonElement value = source.get(key);

        if (value == null || !value.isJsonArray()) {

            throw new IllegalArgumentException(
                    "Missing or invalid array: " + key
            );
        }

        return value.getAsJsonArray();
    }

    private static String readString(
            JsonObject source,
            String key) {

        JsonElement value = source.get(key);

        if (value == null
                || !value.isJsonPrimitive()
                || !value.getAsJsonPrimitive().isString()) {

            throw new IllegalArgumentException(
                    "Missing or invalid text: " + key
            );
        }

        return value.getAsString();
    }

    private static double readDouble(
            JsonObject source,
            String key) {

        JsonElement value = source.get(key);

        if (value == null
                || !value.isJsonPrimitive()
                || !value.getAsJsonPrimitive().isNumber()) {

            throw new IllegalArgumentException(
                    "Missing or invalid number: " + key
            );
        }

        double number = value.getAsDouble();

        if (!Double.isFinite(number)) {

            throw new IllegalArgumentException(
                    "Invalid number: " + key
            );
        }

        return number;
    }

    private static int readInt(
            JsonObject source,
            String key) {

        double number = readDouble(source, key);

        if (number != Math.rint(number)
                || number < Integer.MIN_VALUE
                || number > Integer.MAX_VALUE) {

            throw new IllegalArgumentException(
                    "Invalid integer: " + key
            );
        }

        return (int) number;
    }
}
