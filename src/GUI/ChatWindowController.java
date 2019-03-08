package GUI;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iflytek.cloud.speech.*;
import com.sun.javafx.application.LauncherImpl;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ChatWindowController implements Initializable {
    public ObservableList<Messages> messageList = FXCollections.observableArrayList();
    @FXML
    private Pane voicePane;
    @FXML
    private TextField input;
    @FXML
    private ListView<Messages> output;
    @FXML
    private ImageView voice;
    @FXML
    private ImageView send;
    @FXML
    private Rectangle voiceBar2;
    @FXML
    private Rectangle voiceBar3;
    @FXML
    private Rectangle voiceBar4;
    @FXML
    private Rectangle voiceBar5;
    private Chat chatSession;
    private Timeline voiceBarAnimation;
    private SpeechRecognize speech;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String basePath = new File("src").getAbsolutePath();
        Bot bot = new Bot("Stu-SB", basePath);
        LauncherImpl.notifyPreloader(Main.application, new Preloader.ProgressNotification(0.75));
        this.speech = new SpeechRecognize();
        this.chatSession = new Chat(bot);
        this.output.setItems(messageList);
        this.output.setCellFactory(e -> new MesListViewCell());
        loadVoiceBarTimeline();
        send.setOnMouseClicked(e -> {
            String inputMessage = input.getText();
            sendTextMessage(inputMessage, inputMessage);
        });
        input.setOnAction(e -> {
            String inputMessage = input.getText();
            sendTextMessage(inputMessage, inputMessage);
        });
        voice.setOnMouseClicked(e -> {
            if (!voicePane.isVisible()) {
                speech.start();
                voicePane.setVisible(true);
                voiceBarAnimation.play();
            } else {
                voiceBarAnimation.stop();
                voicePane.setVisible(false);
                speech.stop();
                String inputMessage = input.getText();
                sendTextMessage(inputMessage, inputMessage);
            }
        });
    }

    public void sendTextMessage(String textToShow, String textToRespond) {
        String outputMessage = chatSession.multisentenceRespond(textToRespond);
        System.out.println(outputMessage);
        Messages message = new Messages(textToShow, null);
        messageList.add(message);
        message = new Messages(null, outputMessage);
        messageList.add(message);
        output.scrollTo(message);
        input.clear();
    }

    public void loadVoiceBarTimeline() {
        voiceBarAnimation = new Timeline();
        voicePane.setVisible(false);
        voiceBarAnimation.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(0), new KeyValue(voiceBar2.visibleProperty(), false)),
                new KeyFrame(Duration.millis(0), new KeyValue(voiceBar3.visibleProperty(), false)),
                new KeyFrame(Duration.millis(0), new KeyValue(voiceBar4.visibleProperty(), false)),
                new KeyFrame(Duration.millis(0), new KeyValue(voiceBar5.visibleProperty(), false)),
                new KeyFrame(Duration.millis(1000), new KeyValue(voiceBar2.visibleProperty(), true)),
                new KeyFrame(Duration.millis(2000), new KeyValue(voiceBar3.visibleProperty(), true)),
                new KeyFrame(Duration.millis(3000), new KeyValue(voiceBar4.visibleProperty(), true)),
                new KeyFrame(Duration.millis(4000), new KeyValue(voiceBar5.visibleProperty(), true)),
                new KeyFrame(Duration.millis(5000), new KeyValue(voiceBar5.visibleProperty(), false)));

        voiceBarAnimation.setCycleCount(voiceBarAnimation.INDEFINITE);
    }

    public class SpeechRecognize {

        private SpeechRecognizer recognizer;
        private String APPID = "5bf61011";

        private String sentence = "";
        public RecognizerListener mRecoListener = new RecognizerListener() {
            public void onResult(RecognizerResult results, boolean isLast) {
                JsonParser parser = new JsonParser();
                JsonObject object = (JsonObject) parser.parse(results.getResultString());
                JsonArray wsArray = object.get("ws").getAsJsonArray();
                for (int i = 0; i < wsArray.size(); i++) {
                    JsonArray cwArray = wsArray.get(i).getAsJsonObject().get("cw").getAsJsonArray();
                    for (int j = 0; j < cwArray.size(); j++) {
                        String word = cwArray.get(j).getAsJsonObject().get("w").getAsString();
                        sentence = sentence + word;
                    }
                }
                System.out.printf("from class %s\n", sentence);
                input.setText(sentence);
            }

            public void onBeginOfSpeech() {
            }

            public void onVolumeChanged(int volume) {
            }

            public void onEndOfSpeech() {
            }

            public void onEvent(int eventType, int arg1, int arg2, String msg) {
            }

            @Override
            public void onError(SpeechError error) {
                error.getErrorDescription(true);
            }
        };

        public SpeechRecognize() {
            SpeechUtility.createUtility("appid=" + APPID);
            recognizer = SpeechRecognizer.createRecognizer();
            recognizer.setParameter(SpeechConstant.LANGUAGE, "en_us");
            recognizer.setParameter(SpeechConstant.DOMAIN, "iat");
        }

        public String getSentence() {
            return sentence;
        }

        public void start() {
            sentence = "";
            recognizer.startListening(mRecoListener);
        }

        public void stop() {
            recognizer.stopListening();
        }
    }


    public class MesListViewCell extends ListCell<Messages> {
        @FXML
        private Label humanMes;
        @FXML
        private Label botMes;
        @FXML
        private GridPane botBox;
        @FXML
        private GridPane humanBox;
        @FXML
        private Pagination pagination;
        @FXML
        private AnchorPane pagePane;

        private FXMLLoader fxmlLoader;

        @Override
        public void updateItem(Messages messages, boolean empty) {
            super.updateItem(messages, empty);
            if (empty || messages == null) {
                setText(null);
                setGraphic(null);
            } else {
                if (fxmlLoader == null) {
                    fxmlLoader = new FXMLLoader(getClass().getResource("MessageFrame.fxml"));
                    fxmlLoader.setController(this);
                    try {
                        fxmlLoader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (messages.getHumanMessage() == null && messages.getCard() != null) {
                    pagination.setPageCount(messages.getCard().size());
                    pagination.setPageFactory(new Callback<Integer, Node>() {
                        @Override
                        public Node call(Integer param) {
                            VBox box = new VBox();
                            ImageView imageView = new ImageView(new Image(messages.getCard().get(param).getImgURL()));
                            imageView.setFitHeight(185);
                            imageView.setFitWidth(330);
                            GridPane btnGrid = new GridPane();
                            ArrayList<Button> buttons = new ArrayList<>();
                            for (int i = 0; i < messages.getCard().get(param).getChoiceButton().size(); i++) {
                                String btnName = messages.getCard().get(param).getChoiceButton().get(i).getText();
                                String btnContent = messages.getCard().get(param).getChoiceButton().get(i).getPostBack();
                                String btnUrl = messages.getCard().get(param).getChoiceButton().get(i).getUrl();
                                buttons.add(new Button(btnName));
                                buttons.get(i).setOnMouseClicked(e -> {
                                    if (btnUrl != null) {
                                        try {
                                            Desktop.getDesktop().browse(new URI(btnUrl));
                                        } catch (IOException | URISyntaxException ex) {
                                            ex.printStackTrace();
                                        }
                                    } else {
                                        sendTextMessage(btnName, btnContent);
                                    }
                                });
                                buttons.get(i).setPrefWidth(150);
                                btnGrid.add(buttons.get(i), i % 2, i / 2);
                            }
                            btnGrid.setAlignment(Pos.CENTER);
                            box.getChildren().addAll(imageView, btnGrid);
                            box.setAlignment(Pos.CENTER);
                            return box;
                        }
                    });
                    setGraphic(pagePane);
                } else if (messages.getHumanMessage() == null) {
                    botMes.setText(messages.getBotMessage());
                    setGraphic(botBox);
                } else {
                    humanMes.setText(messages.getHumanMessage());
                    setGraphic(humanBox);
                }
            }
        }
    }
}
