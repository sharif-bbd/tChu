package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;
import javafx.util.Duration;


import javax.swing.*;
import java.util.*;

import static javafx.application.Platform.setImplicitExit;


/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class GraphicalPlayer {

    private final static int TEXT_LIST_MAX_SIZE = 5;
    private final static int FIRST_TEXT = 0;
    private final PlayerId ownId;
    private final Map<PlayerId, String> playerNames;
    private final ObservableGameState observableGameState;
    private final ObservableList<Text> textList;
    private final Stage mainStage;
    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHandler;
    private final ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandler;
    private final ObjectProperty<ActionHandlers.DrawCardHandler> drawCardsHandler;
    private final ObjectProperty<ActionHandlers.SwitchRoutesHandler> switchRouteHandler;
    private Color currentTextColor = null;

    private static Random rng = new Random();
    private static Timeline t = new Timeline();
    private static List<String> randomNames = Arrays.asList("Chuggington", "Thomas le petit train", "Mistral", "Orient-Express", "Maroc Express", "El Chepe", "Golden Montain Express");

    //load images for the animation
    private final static javafx.scene.image.Image TGV_3 = new javafx.scene.image.Image(GraphicalPlayer.class.getResource("/tgv3.png").toString());
    private final static javafx.scene.image.Image TGV_4 = new javafx.scene.image.Image(GraphicalPlayer.class.getResource("/tgv4.png").toString());
    private final static javafx.scene.image.Image TGV_5 = new javafx.scene.image.Image(GraphicalPlayer.class.getResource("/tgv5.png").toString());
    private final static javafx.scene.image.Image TGV_6 = new javafx.scene.image.Image(GraphicalPlayer.class.getResource("/tgv6.png").toString());

    //create a group node for the train
    private static Group train;

    /**
     * constructs the interface graphic of the player
     * @param ownId, my player id
     * @param playerNames, the names of the player
     */
    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> playerNames, ActionHandlers.ChooseGivenRouteHandler chooseGivenRouteHandler){
        this.ownId = ownId;
        this.playerNames = playerNames;
        observableGameState = new ObservableGameState(ownId);
        textList = FXCollections.observableArrayList();

        drawTicketsHandler = new SimpleObjectProperty<>();
        claimRouteHandler = new SimpleObjectProperty<>();
        drawCardsHandler = new SimpleObjectProperty<>();
        switchRouteHandler = new SimpleObjectProperty<>();


        //main window
        Node mapview = MapViewCreator.createMapView(observableGameState, claimRouteHandler, this::chooseClaimCards, switchRouteHandler, chooseGivenRouteHandler);
        Node cardsView = DecksViewCreator.createCardsView(observableGameState, drawTicketsHandler, drawCardsHandler);
        Node handView = DecksViewCreator.createHandView(observableGameState);
        Node infoView = InfoViewCreator.createInfoView(ownId, playerNames, observableGameState, textList);

        BorderPane mainPane = new BorderPane(mapview, null, cardsView, handView, infoView);
        Scene principalScene = new Scene(mainPane, playerColor(ownId));
        mainStage = new Stage();
        Image icon = new Image("subway.png");
        mainStage.getIcons().add(icon);
        mainStage.setTitle("tChu - " + playerNames.get(ownId));
        mainStage.setScene(principalScene);
        mainStage.show();
    }

    /**
     *set the state of my player
     * @param newPublicGameState, the current public game state
     * @param newOwnState, my new player state
     */
    public void setState(PublicGameState newPublicGameState, PlayerState newOwnState){
        assert Platform.isFxApplicationThread();
        observableGameState.setState(newPublicGameState, newOwnState);
    }

    public static void choosePlayerName(ActionHandlers.ChooseNameHandler handler) {
        assert Platform.isFxApplicationThread();
        setImplicitExit(false);

        String family = "Helvetica";
        Image icon = new Image("subway.png");
        Stage stage = new Stage();
        List<String> randomNames = Arrays.asList(
                "Chuggington",
                "Thomas le petit train",
                "Mistral",
                "Orient-Express",
                "Maroc Express",
                "El Chepe",
                "Golden Montain Express",
                "Bumbay Express",
                "le Train Bleu",
                "California Zephyr");

        //Creation de la fenetre
        Label chooseName = new Label(StringsFr.CHOOSE_NAME);
        Text blank = new Text();
        blank.setFont(Font.font("Arial", 7));
        Text blank1 = new Text();
        blank1.setFont(Font.font("Arial", 7));
        TextField textField = new TextField();
        Button submitButton = new Button(StringsFr.SUBMIT);
        GridPane grid = new GridPane();
        Label welcome = new Label("Bienvenue dans tCHu !!");
        welcome.getStyleClass().add("label-welcome");

        //TODO: ajout
        Hyperlink link = new Hyperlink("règles du jeu");
        link.setOnAction(e -> {
            Window.display();
        });

        //images for the train animation
        final ImageView tgv3 = new ImageView(TGV_3);
        final ImageView tgv4 = new ImageView(TGV_4);
        final ImageView tgv5 = new ImageView(TGV_5);
        final ImageView tgv6 = new ImageView(TGV_6);
        train = new Group(tgv3);

        //put the train at the specified coordinates
        train.setTranslateX(95);
        train.setTranslateY(350);

        //Add the images in the timeline
        loopTime(train, tgv4, 200);
        loopTime(train, tgv5, 400);
        loopTime(train, tgv6, 600);
        t.play();

        //Mise en page
        grid.add(welcome, 0, 0);
        grid.add(chooseName, 0, 1);
        grid.add(textField, 0, 2);
        grid.add(blank, 0, 3);
        grid.add(submitButton, 0, 4);
        grid.add(blank1, 0, 5);
        grid.add(link, 0, 6);
        GridPane.setHalignment(submitButton, HPos.CENTER);
        GridPane.setHalignment(chooseName, HPos.CENTER);
        GridPane.setHalignment(textField, HPos.CENTER);
        GridPane.setHalignment(link, HPos.CENTER);

        grid.getChildren().add(train);
        Scene scene = new Scene(grid, 420, 500, Color.ROYALBLUE);
        scene.getStylesheets().add("background.css");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(StringsFr.WELCOME);
        stage.setOnCloseRequest(Event::consume);
        stage.show();

        submitButton.setOnAction(e -> {
            stage.hide();

            if ((textField.getText().isEmpty()) || (textField.getText().isBlank())) {
                handler.onChooseName(getRandomItems(randomNames));
            } else {
                handler.onChooseName(textField.getText());
            }

        });
    }

    /**
     * the player receives the information of the current game
     *
     * @param message, the information of the current game
     */
    public void receiveInfo(String message) {
        assert Platform.isFxApplicationThread();
        Text text = new Text(message);
        if (message.contains(playerNames.get(ownId))) currentTextColor = playerColor(ownId);
        else if (message.contains(playerNames.get(ownId.next()))) currentTextColor = playerColor(ownId.next());
        text.setFill(currentTextColor);
        textList.add(text);
        if (textList.size() > TEXT_LIST_MAX_SIZE) {
            textList.remove(FIRST_TEXT);
        }
    }

    /**
     * when the turn starts, the player can choose one action
     * @param drawTicketsH, action to draw tickets
     * @param drawCardH, action to draw a card
     * @param claimRouteH, action to claim a route
     */
    public void startTurn(ActionHandlers.DrawTicketsHandler drawTicketsH, ActionHandlers.DrawCardHandler drawCardH, ActionHandlers.ClaimRouteHandler claimRouteH, ActionHandlers.SwitchRoutesHandler switchRoutesH){
        assert Platform.isFxApplicationThread();


        if (observableGameState.canDrawTickets().get()) {
            ActionHandlers.DrawTicketsHandler t = () -> {
                setNull();
                drawTicketsH.onDrawTickets();
            };

            drawTicketsHandler.set(t);
        }

        if(observableGameState.canDrawCards().get()) {
            ActionHandlers.DrawCardHandler c = (int i) -> {
                setNull();
                drawCardH.onDrawCard(i);
                drawCard(drawCardH);
            };
            drawCardsHandler.set(c);
        }

        ActionHandlers.ClaimRouteHandler claimR = (route, cards) -> {
            setNull();
            claimRouteH.onClaimRoute(route, cards);
        };
        claimRouteHandler.set(claimR);

        if(observableGameState.getHasRoute()){

            ActionHandlers.SwitchRoutesHandler switchR = (wRoute)  -> {
                setNull();
                switchRoutesH.onSwitchRoutes(wRoute);
            };
            switchRouteHandler.set(switchR);
        }

    }

    /**
     *open a window where the player can choose tickets (at the start and during the game)
     * @param chooseTickets, the sorted bag of the tickets, the player can choose
     * @param chooseTicketsHandler, the action to choose ticket(s)
     */
    public void chooseTickets(SortedBag<Ticket> chooseTickets, ActionHandlers.ChooseTicketsHandler chooseTicketsHandler){
        assert Platform.isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);

        Button chooseTicketsButton = new Button(StringsFr.CHOOSE);
        String chooseMessage = new String();
        chooseMessage = String.format(StringsFr.CHOOSE_TICKETS, chooseTickets.size() - Constants.DISCARDABLE_TICKETS_COUNT, StringsFr.plural(chooseTickets.size() - Constants.DISCARDABLE_TICKETS_COUNT));
        List<Ticket> listTicket = new ArrayList<>(chooseTickets.toList());

        ObservableList<Ticket> observableList = FXCollections.observableArrayList(listTicket);
        ListView<Ticket> listView = new ListView<>(observableList);

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        createWindow(stage, StringsFr.TICKETS_CHOICE, chooseTicketsButton, chooseMessage, listView);

        chooseTicketsButton.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).
                lessThan(chooseTickets.size() - Constants.DISCARDABLE_TICKETS_COUNT));

        chooseTicketsButton.setOnAction( e -> {
            stage.hide();
            chooseTicketsHandler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));
        });
    }

    /*public void chooseFirstRoute(ActionHandlers.ChooseFirstRouteHandler chooseFirstRouteH){

        ActionHandlers.ChooseFirstRouteHandler firstRouteH = () ->{
            setNull();
            chooseFirstRouteH.onChooseRoute();
        };
        switchRouteHandler.set
    }

     */

    public void viewRequest(Route wantedRoute, Route givenRoute, ActionHandlers.AcceptHandler acceptH){
        //une fois qu'on appuye sur le bouton de confirmation on appelera le handler passé en paramètre


        Stage stage = new Stage();


        Button confirm = new Button("Accepter");
        Button refuser = new Button("Refuser");
        refuser.getStyleClass().add("button-refuser");
        confirm.getStyleClass().add("button-accept");
        GridPane grid = new GridPane();



        TextFlow textFlow = new TextFlow();
        Text text = new Text("Souhaitez-vous échanger votre route " + wantedRoute.toString() + " contre la route " + givenRoute.toString());
        textFlow.getChildren().add(text);
        Text blank = new Text();
        Text blank1 = new Text();
        blank.setFont(Font.font("Arial", 7));
        blank1.setFont(Font.font("Arial", 7));

        grid.add(text, 0,0);
        grid.add(blank,0,1);
        grid.add(confirm, 0,2);
        grid.add(blank1,0,3);
        grid.add(refuser, 0,4);
        GridPane.setHalignment(text, HPos.CENTER);
        GridPane.setHalignment(confirm, HPos.CENTER);
        GridPane.setHalignment(refuser, HPos.CENTER);

        Scene scene = new Scene(grid);
        scene.getStylesheets().add("chooser.css");
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Demande d'échange ");
        stage.setOnCloseRequest(Event::consume);
        stage.show();

        confirm.setOnAction(e -> {
            stage.hide();
            acceptH.onAnswer(true);
        });

        refuser.setOnAction(e -> {
            stage.hide();
            acceptH.onAnswer(false);
        });

    }


    /**
     * the player can choose to draw a card among the top card of the deck or the face up cards
     * @param drawCardHandler, action to draw a card
     */
    public void drawCard(ActionHandlers.DrawCardHandler drawCardHandler){
        assert Platform.isFxApplicationThread();

        ActionHandlers.DrawCardHandler cardHandler = (int i) -> {
            setNull();
            drawCardHandler.onDrawCard(i);
        };

        drawCardsHandler.set(cardHandler);
    }

    /**
     *the player chooses the cards in order to obtain his claimed route
     * @param listCards, the initial cards used by the player to claimed a route
     * @param chooseCardsHandler, action to choose cards
     */
    public void chooseClaimCards(List<SortedBag<Card>> listCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler){
        assert Platform.isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);
        Button chooseClaimCardButton = new Button(StringsFr.CHOOSE);

        ObservableList<SortedBag<Card>> observableList = FXCollections.observableArrayList(listCards);
        ListView<SortedBag<Card>> listView = new ListView<>(observableList);
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));

        SortedBag<Card> choiceOfCards = listView.getSelectionModel().getSelectedItem();

        createWindow(stage, StringsFr.CARDS_CHOICE ,chooseClaimCardButton, StringsFr.CHOOSE_CARDS,listView);

        chooseClaimCardButton.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).lessThan(1));
        chooseClaimCardButton.setOnAction( e-> {
            stage.hide();
            chooseCardsHandler.onChooseCards(listView.getSelectionModel().getSelectedItem());
        });

    }

    /**
     *
     * @param listCards, the additional cards the player can uses to obtain an underground route
     * @param chooseCardsHandler, action to choose cards
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> listCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler){
        assert Platform.isFxApplicationThread();
        Stage stage = new Stage(StageStyle.UTILITY);
        Button chooseClaimCardButton = new Button(StringsFr.CHOOSE);


        ObservableList<SortedBag<Card>> observableList = FXCollections.observableArrayList(listCards);
        ListView<SortedBag<Card>> listView = new ListView<>(observableList);
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));

        SortedBag<Card> choiceOfCards = listView.getSelectionModel().getSelectedItem();
        createWindow(stage, StringsFr.CARDS_CHOICE ,chooseClaimCardButton, StringsFr.CHOOSE_ADDITIONAL_CARDS,listView);

        chooseClaimCardButton.setOnAction( e -> {
            stage.hide();
            chooseCardsHandler.onChooseCards(listView.getSelectionModel().getSelectedItem() == null ? SortedBag.of() : listView.getSelectionModel().getSelectedItem());
        });

    }



    //Private methods
    private <E> void createWindow(Stage stage, String windowTitle, Button chooseButton, String introText, ListView<E> node) {

        VBox vBox = new VBox();
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");

        TextFlow textFlow = new TextFlow();
        Text text = new Text(introText);
        textFlow.getChildren().add(text);

        vBox.getChildren().addAll(textFlow, node, chooseButton);

        stage.setScene(scene);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(windowTitle);
        stage.setOnCloseRequest(Event::consume);
        stage.show();

    }

    private void setNull(){
        drawCardsHandler.set(null);
        drawTicketsHandler.set(null);
        claimRouteHandler.set(null);
        switchRouteHandler.set(null);
    }

    /**
     * @param id, the id of the player
     * @return playerColor, return the corresponding text color of each of the player
     */
    private static Color playerColor(PlayerId id) {
        if (id == PlayerId.PLAYER_1) return  Color.TEAL;
        else return Color.SALMON;
    }

    private static String getRandomItems(List<String> list){
        int index = rng.nextInt(list.size());
        return list.get(index);
    }

    private static void loopTime(Group group, ImageView image, double ms){
        //Animate the train in a loop
        t.setCycleCount(Timeline.INDEFINITE);
        //Add the images in the timeline
        t.getKeyFrames().add(new KeyFrame(
                Duration.millis(ms),
                (ActionEvent event) -> {
                    group.getChildren().setAll(image);
                }
        ));
    }

}