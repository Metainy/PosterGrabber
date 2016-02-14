package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Smoothing the font
        System.setProperty("prism.lcdtext", "false");


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        Parent root = loader.load();

        // Passing Stage instance to the controller
        Controller controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Poster Grabber");
        primaryStage.setScene(new Scene(root, 850, 550));

        // Setting minimum size
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(555);

        // Adding TaskBar icon
        primaryStage.getIcons().addAll(new Image("/icons/ic_main_32.png"), new Image("/icons/ic_main_48.png"), new Image("/icons/ic_main_64.png"));

        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}