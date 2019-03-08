package GUI;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.util.ArrayList;

public class Messages {
    private String botMessage;
    private String humanMessage;
    private ArrayList<Card> card;

    public Messages(String humanMessage, String botMessage) {
        this.humanMessage = humanMessage;
        this.botMessage = botMessage;
        if (botMessage != null) parseBotMessage();
    }

    public void parseBotMessage() {
        InputSource source = new InputSource(new StringReader(botMessage));
        if (botMessage.trim().startsWith("<carousel>") || botMessage.trim().startsWith("<card>")) {
            try {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);

                NodeList cardNodes = doc.getElementsByTagName("card");
                ArrayList<Card> cardArrayList = new ArrayList<>();
                for (int i = 0; i < cardNodes.getLength(); i++) {
                    Element cardElement = (Element) cardNodes.item(i);
                    NodeList imgNode = cardElement.getElementsByTagName("image");
                    String imgURL = getParsedStringFromNode(imgNode);
                    NodeList titleNode = cardElement.getElementsByTagName("title");
                    String titleStr = getParsedStringFromNode(titleNode);
                    ArrayList<ChoiceButton> choiceButton = new ArrayList<>();
                    NodeList buttonNode = cardElement.getElementsByTagName("button");
                    for (int j = 0; j < buttonNode.getLength(); j ++) {
                        Element button = (Element) buttonNode.item(j);
                        NodeList textNode = button.getElementsByTagName("text");
                        String textStr = getParsedStringFromNode(textNode);
                        NodeList postBackNode = button.getElementsByTagName("postback");
                        String postBackStr = getParsedStringFromNode(postBackNode);
                        if (postBackStr != null) postBackStr = postBackStr.replaceAll("\\p{Punct}","").trim();
                        NodeList urlNode = button.getElementsByTagName("url");
                        String urlStr = getParsedStringFromNode(urlNode);
                        choiceButton.add(new ChoiceButton(textStr, postBackStr, urlStr));
                    }
                    cardArrayList.add(new Card(imgURL, titleStr, choiceButton));
                }
                this.card = cardArrayList;

            } catch (ParserConfigurationException | org.xml.sax.SAXException | java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getParsedStringFromNode(NodeList node) {
        String target = null;
        if (node.getLength() > 0) {
            Element element = (Element) node.item(0);
            Node child = element.getFirstChild();
            if (child instanceof CharacterData) {
                CharacterData cd = (CharacterData) child;
                target = cd.getData();
            }
        }
        return target;
    }

    public String getBotMessage() {
        return botMessage;
    }

    public String getHumanMessage() {
        return humanMessage;
    }

    public ArrayList<Card> getCard() {
        return card;
    }

    public class Card {
        private String imgURL;
        private String title;
        private ArrayList<ChoiceButton> choiceButton;

        public Card(String imgURL, String title, ArrayList<ChoiceButton> choiceButton) {
            this.imgURL = imgURL;
            this.title = title;
            this.choiceButton = choiceButton;
        }

        public String getImgURL() {
            return imgURL;
        }

        public void setImgURL(String imgURL) {
            this.imgURL = imgURL;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public ArrayList<ChoiceButton> getChoiceButton() {
            return choiceButton;
        }

        public void setChoiceButton(ArrayList<ChoiceButton> choiceButton) {
            this.choiceButton = choiceButton;
        }
    }


    public class ChoiceButton {
        private String text;
        private String postBack;
        private String url;

        public ChoiceButton(String text, String postBack, String url) {
            this.text = text;
            this.postBack = postBack;
            this.url = url;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getPostBack() {
            return postBack;
        }

        public void setPostBack(String postBack) {
            this.postBack = postBack;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }


}
