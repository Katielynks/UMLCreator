import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import view.MainView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();

        Scene scene = new Scene(mainView.getRoot(), 1000, 700);

        scene.getStylesheets().add(
            getClass().getResource("/styles.css").toExternalForm()
        );

        primaryStage.setTitle("UML Diagram Creator");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(
            new Image(
                    getClass().getResourceAsStream("/icon.png")
            )
        );
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}