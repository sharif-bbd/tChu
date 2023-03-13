package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
final class InfoViewCreator {


    /**
     * @param ownId, the player Id that corresponds to the interface
     * @param playerNames, the Map of the player's names
     * @param gameState, the observable game state
     * @param textList, an observable list that contains the information of the current game
     * @return createInfoView, creates the information view of the game
     */
    public static Node createInfoView(PlayerId ownId, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> textList){
        VBox infoView = new VBox();
        infoView.getStylesheets().add("info.css");
        infoView.getStylesheets().add("colors.css");
        VBox insideVbox = new VBox();
        insideVbox.setId("player-stats");

        for(PlayerId id : PlayerId.ALL){
            TextFlow player = new TextFlow();
            player.getStyleClass().add(id.name());
            Circle circle = new Circle(5);
            circle.getStyleClass().add("filled");
            StringExpression stats =  Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(id),gameState.getTicketCount(id), gameState.getCardsCount(id), gameState.getCarsCount(id), gameState.getConstructionPoints(id));
            Text text = new Text();
            text.textProperty().bind(stats);
            player.getChildren().addAll(circle, text);
            insideVbox.getChildren().add(player);
        }

        Separator separator = new Separator();
        TextFlow information = new TextFlow();
        information.setId("game-info");
        infoView.getChildren().addAll(insideVbox, separator, information);
        for (int i = 0; i < 5; i++) {
            Text message = new Text();
            information.getChildren().add(message);
        }
        Bindings.bindContent(information.getChildren(), textList);
        return infoView;
    }
}