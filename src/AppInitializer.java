import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("view/LoginForm.fxml"));

        Scene scene = new Scene(parent);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");


        Image img = new Image("images/marvel clock.png");

        primaryStage.getIcons().add(img);
        primaryStage.centerOnScreen();
        primaryStage.show();


    }
}
