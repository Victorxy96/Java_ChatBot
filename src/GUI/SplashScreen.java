package GUI;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


public class SplashScreen extends Preloader{

    private Stage stage;
    private AnchorPane root;
    private ProgressIndicator progress;

    @Override
    public void init() throws Exception {
        Platform.runLater(() -> {
            root = new AnchorPane();
            root.setPrefWidth(400);
            root.setPrefHeight(600);

            ImageView splashImage = new ImageView(new Image("img/splash.png"));
            splashImage.setFitWidth(400);
            splashImage.setFitHeight(600);

            progress = new ProgressIndicator(0.1);
            progress.setLayoutX(185);
            progress.setLayoutY(400);

            root.getChildren().addAll(splashImage, progress);
        });
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.getIcons().addAll(new Image("img/chat.png"));
        stage.setMinWidth(400);
        stage.setMinHeight(600);
        stage.setScene(new Scene(root, 400, 600));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        progress.setProgress(0.2);
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        // Handle application notification in this point (see MyApplication#init).
        if (info instanceof ProgressNotification) {
            double progressValue = ((ProgressNotification) info).getProgress();
            progress.setProgress(progressValue);
            if (progressValue >= 0.8) {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), root);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0.3);
                fadeOut.setCycleCount(1);
                fadeOut.play();
                fadeOut.setOnFinished(e -> root.getScene().getWindow().hide());
            }
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        // Handle state change notifications.
        StateChangeNotification.Type type = info.getType();
        switch (type) {
            case BEFORE_START:
                stage.hide();
        }
    }

}
