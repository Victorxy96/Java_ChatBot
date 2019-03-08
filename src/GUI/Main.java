package GUI;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private Parent root;

    static Application application;

    public Main() {
        this.application = this;
    }

    @Override
    public void init() throws Exception {
        LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(0.3));
        root = FXMLLoader.load(getClass().getResource("ChatWindow.fxml"));
        LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(0.85));
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(0.9));
        primaryStage.setTitle("Chat with Stu-SB");
        primaryStage.getIcons().addAll(new Image("img/chat.png"));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(400);
        primaryStage.setScene(new Scene(root, 400, 600));
        LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(1));
        primaryStage.show();
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class, SplashScreen.class, args);
    }

}
