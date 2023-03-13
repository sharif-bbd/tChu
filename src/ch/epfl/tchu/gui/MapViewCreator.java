package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.util.List;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
final class MapViewCreator {
    /**
     *when the player have to choose cards in order to get the route he claimed
     */
    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler handler);
    }

    /**
     * creates the view of the map tChu
     * @param observableGameState, the observable game state
     * @param claimRouteHandlerObjectProperty, a property that contains a Handler used when the player claims a route
     * @param cardChooser, the cards choose by the player in order to get the route he claimed
     * @return createMapView, creates the view of the map tChu
     */
    public static Node createMapView(ObservableGameState observableGameState, ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandlerObjectProperty, CardChooser cardChooser, ObjectProperty<ActionHandlers.SwitchRoutesHandler> switchRoutesHandlerObjectProperty, ActionHandlers.ChooseGivenRouteHandler chooseGivenRouteHandler){
        ImageView imageView = new ImageView();
        Pane mapView = new Pane();
        mapView.getStylesheets().add("map.css");
        mapView.getStylesheets().add("colors.css");
        mapView.getChildren().add(imageView);
        BooleanProperty inSwitch = new SimpleBooleanProperty();

        for (Route r : ChMap.routes()){
            Group routeGroup = new Group();
            routeGroup.setId(r.id());
            routeGroup.getStyleClass().add("route");
            routeGroup.getStyleClass().add(r.level().name());
            routeGroup.getStyleClass().add((r.color() != null) ? r.color().name() : "NEUTRAL");

            routeGroup.disableProperty().bind((claimRouteHandlerObjectProperty.isNull()
                    .or(observableGameState.getClaimableRoutes(r).not()
                            .and(observableGameState.getOwner(r).isEqualTo(observableGameState.getOwnId().next()).not()
                                    .or(switchRoutesHandlerObjectProperty.isNull()))))
                    .and(inSwitch.not()
                            .or(observableGameState.getOwner(r).isEqualTo(observableGameState.getOwnId()).not())));




            routeGroup.setOnMouseClicked(e -> {
                if(!inSwitch.get()){
                    if(observableGameState.getOwner(r).get() == null){

                        List<SortedBag<Card>> possibleClaimCards = observableGameState.possibleClaimCards(r);
                        ActionHandlers.ClaimRouteHandler routeHandler = claimRouteHandlerObjectProperty.get();
                        ActionHandlers.ChooseCardsHandler chooseCardsH = chosenCard -> routeHandler.onClaimRoute(r, chosenCard);
                        if(possibleClaimCards.size() == 1){
                            routeHandler.onClaimRoute(r, possibleClaimCards.get(0));
                        }else if(possibleClaimCards.size() > 1){
                            cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                        }
                    }else{
                        switchRoutesHandlerObjectProperty.get().onSwitchRoutes(r);
                        inSwitch.set(true);
                    }
                }else{
                    chooseGivenRouteHandler.onChooseRoute(r);
                    inSwitch.set(false);
                }
            });

            observableGameState.getOwner(r).addListener((p,o,n) -> {
                routeGroup.getStyleClass().clear();

                routeGroup.getStyleClass().add(n.name());
                routeGroup.getStyleClass().add("route");
                routeGroup.getStyleClass().add(r.level().name());
                routeGroup.getStyleClass().add((r.color() != null) ? r.color().name() : "NEUTRAL");
            });
            for (int i = 1; i <= r.length(); i++) {
                Group caseGroup = new Group();
                caseGroup.setId(r.id() + "_" + String.valueOf(i));
                routeGroup.getChildren().add(caseGroup);
                Rectangle block = new Rectangle(36d, 12d);
                block.getStyleClass().add("track");
                block.getStyleClass().add("filled");
                caseGroup.getChildren().add(block);
                Group carGroup = new Group();
                carGroup.getStyleClass().add("car");
                caseGroup.getChildren().add(carGroup);
                Rectangle car = new Rectangle(36d, 12d);
                car.getStyleClass().add("filled");
                carGroup.getChildren().add(car);
                Circle circle1 = new Circle(12, 6, 3);
                carGroup.getChildren().add(circle1);
                Circle circle2 = new Circle(24, 6, 3);
                carGroup.getChildren().add(circle2);
            }
            mapView.getChildren().add(routeGroup);
        }

        return mapView;
    }


}