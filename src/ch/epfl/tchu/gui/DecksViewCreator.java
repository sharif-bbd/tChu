package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
final class DecksViewCreator {

    /**
     * @param gameState, the observable game state
     * @return createHandView, the view of the player hand
     */
    public static Node createHandView(ObservableGameState gameState){
        HBox hBox = new HBox();
        hBox.getStylesheets().add("decks.css");
        hBox.getStylesheets().add("colors.css");
        hBox.setId("hand-pane");
        ListView<Ticket> ticketsView = new ListView<>(gameState.getTickets());
        ticketsView.setId("tickets");
        ObjectProperty<StationConnectivity> connectivity = gameState.getConnectivityProperty();
        connectivity.addListener((p,o,n) -> ticketsView.setCellFactory(v -> new TextFieldListCell<>(new TicketStringConverter(n))));
        hBox.getChildren().add(ticketsView);


        for (Card c : Card.ALL) {
            ReadOnlyIntegerProperty count = gameState.getCardsNumber(c);
            StackPane hand = new StackPane();
            hand.visibleProperty().bind(Bindings.greaterThan(count, 0));

            hand.getStyleClass().add((c != Card.LOCOMOTIVE) ? c.name() : "NEUTRAL");
            hand.getStyleClass().add("card");

            Rectangle outsideRectangle = new Rectangle(60d, 90d);
            outsideRectangle.getStyleClass().add("outside");
            hand.getChildren().add(outsideRectangle);

            Rectangle insideRectangle = new Rectangle(40d, 70d);
            insideRectangle.getStyleClass().add("filled");
            insideRectangle.getStyleClass().add("inside");
            hand.getChildren().add(insideRectangle);

            Rectangle trainImageRectangle = new Rectangle(40d, 70d);
            trainImageRectangle.getStyleClass().add("train-image");
            hand.getChildren().add(trainImageRectangle);

            Text counter = new Text(String.valueOf(count.get()));
            counter.getStyleClass().add("count");
            counter.textProperty().bind(Bindings.convert(count));
            counter.visibleProperty().bind(Bindings.greaterThan(count, 1));
            hand.getChildren().add(counter);

            hBox.getChildren().add(hand);

        }
        return hBox;
    }

    /**
     * @param gameState, the observable game state
     * @param drawTickets, property that contains a Handler to draw tickets
     * @param drawCards, property that contains a Handler to draw cards
     * @return createCardsView, the view of the deck of cards and the deck of tickets
     */
    public static Node createCardsView(ObservableGameState gameState, ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTickets, ObjectProperty<ActionHandlers.DrawCardHandler> drawCards){
        VBox vBox = new VBox();
        vBox.getStylesheets().add("decks.css");
        vBox.getStylesheets().add("colors.css");
        vBox.setId("card-pane");

        Button ticketsButton = new Button("Billets");
        ticketsButton.getStyleClass().add("gauged");
        ticketsButton.disableProperty().bind(drawTickets.isNull());

        Group ticketsButtonGroup = new Group();

        Rectangle ticketsBackground = new Rectangle(50,5);
        ticketsBackground.getStyleClass().add("background");
        ticketsButtonGroup.getChildren().add(ticketsBackground);

        Rectangle ticketsForeground = new Rectangle(50, 5);
        ReadOnlyIntegerProperty percentageOfTickets = gameState.ticketsPercentageProperty();
        ticketsForeground.widthProperty().bind(percentageOfTickets.multiply(50).divide(100));
        ticketsForeground.getStyleClass().add("foreground");
        ticketsButtonGroup.getChildren().add(ticketsForeground);

        ticketsButton.setGraphic(ticketsButtonGroup);

        ticketsButton.setOnMouseClicked(e -> drawTickets.get().onDrawTickets());

        vBox.getChildren().add(ticketsButton);



        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            StackPane card = new StackPane();
            card.getStyleClass().add((Card.ALL.get(slot) != Card.LOCOMOTIVE) ? Card.ALL.get(slot).name() : "NEUTRAL");
            gameState.getFaceUpCards(slot).addListener((p,o,n) ->  card.getStyleClass().setAll(((n != Card.LOCOMOTIVE) ? n.name() : "NEUTRAL"), "card", "faceUpCards"));
            card.getStyleClass().add("card");
            card.getStyleClass().add("faceUpCards");

            vBox.getChildren().add(card);

            Rectangle outsideRectangle = new Rectangle(60d, 90d);
            outsideRectangle.getStyleClass().add("outside");
            card.getChildren().add(outsideRectangle);

            Rectangle insideRectangle = new Rectangle(40d, 70d);
            insideRectangle.getStyleClass().add("filled");
            insideRectangle.getStyleClass().add("inside");
            card.getChildren().add(insideRectangle);

            Rectangle trainImageRectangle = new Rectangle(40d, 70d);
            trainImageRectangle.getStyleClass().add("train-image");
            card.getChildren().add(trainImageRectangle);

            card.disableProperty().bind(drawCards.isNull());
            card.setOnMouseClicked(e -> drawCards.get().onDrawCard(slot));
        }
        Button cardsButton = new Button("Cartes");
        cardsButton.getStyleClass().add("gauged");
        cardsButton.disableProperty().bind(drawCards.isNull());

        Group cardsButtonGroup = new Group();

        Rectangle cardsBackground = new Rectangle(50, 5);
        cardsBackground.getStyleClass().add("background");
        cardsButtonGroup.getChildren().add(cardsBackground);

        Rectangle cardsForeground = new Rectangle(50, 5);
        ReadOnlyIntegerProperty percentageOfCards = gameState.cardsPercentageProperty();
        cardsForeground.widthProperty().bind(percentageOfCards.multiply(50).divide(100));
        cardsForeground.getStyleClass().add("foreground");
        cardsButtonGroup.getChildren().add(cardsForeground);

        cardsButton.setGraphic(cardsButtonGroup);

        cardsButton.setOnMouseClicked(e -> {
            drawCards.get().onDrawCard(-1);
        });

        vBox.getChildren().add(cardsButton);


        Hyperlink link = new Hyperlink("règles du jeu");
        link.setOnAction(e -> {
            Window.display();
        });

        vBox.getChildren().add(link);


        return vBox;
    }

}