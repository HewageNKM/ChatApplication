package lk.ijse.chatapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lk.ijse.chatapplication.sever.Server;

import java.io.IOException;
import java.util.Objects;

public class AppInitializer extends Application {
    @Override
    public void start(Stage primaryStage) {
      new Thread(()->{
          Server.getInstance().run();
        }).start();
          try {
            primaryStage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/LoginForm.fxml")))));
            primaryStage.setTitle("Login Form");
            primaryStage.getIcons().add(new Image("/asset/login.png"));
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
