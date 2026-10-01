# UML Diagram Creator

A desktop UML diagram editor built with JavaFX.
Ccreate, edit, customize, save, and reopen UML diagrams using an interactive canvas.

## Features

### UML Shapes

The editor supports:

- Class
- Interface
- Abstract Class
- Note
- Package

Each shape can be:

- Moved around the canvas
- Resized
- Selected and deselected
- Customized using the color picker
- Edited directly through text fields

Class-based elements support separate sections for:
- Class name
- Attributes
- Operations

New text lines can also be added dynamically.


### UML Relationships

The editor supports:

- Association
- Dependency
- Inheritance
- Implementation
- Aggregation
- Composition

Relationships can be:

- Selected and highlighted
- Moved as a complete relationship
- Resized by dragging either endpoint
- Recolored using the color picker

Each relationship uses its corresponding UML notation, including:

- Dashed dependency lines
- Hollow inheritance triangles
- Hollow aggregation diamonds
- Filled composition diamonds

### Selection

Only one diagram element is selected at a time.

When selected, an element is highlighted in blue. Selecting another shape or relationship automatically deselects the previous one.

### Color Picker

The color picker can be used to customize:

- Shape borders
- Relationship lines
- Arrowheads
- Diamonds

Each diagram element can have its own color.

### Canvas

The canvas supports three background styles:

- Blank
- Lined
- Dotted

The canvas also supports zooming using the mouse wheel.


## File Operations

### New

Clears the current diagram and resets the canvas.

### Open

Opens a previously exported UML diagram from a JSON file. The JSON file restores all diagram information.

### Save

Saves the current UML diagram as a JPEG image.

### Export

Exports the current diagram as a JSON file so that it can be opened and edited again later.

## Preview

![UML Diagram Creator](src/main/resources/uml-diagram-creator.png)

## Requirements

### Running from Source

To build and run the project from source, the following must be installed:

- Java 21 or newer
- Apache Maven

### Windows Download

Download the Windows ZIP from the **Releases** section, extract it, and run `UML Diagram Creator.exe`