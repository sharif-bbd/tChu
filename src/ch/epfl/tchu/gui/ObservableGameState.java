package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class ObservableGameState {

    private  PublicGameState publicGameState;
    private  PlayerState ownState;
    private final PlayerId ownId;
    private final IntegerProperty ticketsPercentage;
    private final IntegerProperty cardsPercentage;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, ObjectProperty<PlayerId>> claimedRoute;
    private final Map<PlayerId, IntegerProperty> playerTickets;
    private final Map<PlayerId, IntegerProperty> playerCards;
    private final Map<PlayerId, IntegerProperty> playerCars;
    private final Map<PlayerId, IntegerProperty> playerConstructionPoints;
    private final ObservableList<Ticket> tickets;
    private final Map<Card, IntegerProperty> cardsNumber;
    private final Map<Route, BooleanProperty> claimableRoutes;
    private final ObjectProperty<StationConnectivity> connectivityObjectProperty;
    private static final List<List<Route>> LIST_DOUBLE_ROUTES = new ArrayList<>();



    /**
     * Constructs the observable game state
     * @param ownId, my player id
     */
    public ObservableGameState(PlayerId ownId) {

        this.ownId = ownId;
        this.publicGameState = null;
        this.ownState = null;
        this.ticketsPercentage = new SimpleIntegerProperty(0);
        this.cardsPercentage = new SimpleIntegerProperty(0);
        this.faceUpCards = createFaceUpCards();
        this.claimedRoute = createClaimedRoute();
        this.playerTickets = Map.of(PlayerId.PLAYER_1, new SimpleIntegerProperty(0), PlayerId.PLAYER_2, new SimpleIntegerProperty(0));
        this.playerCards = Map.of(PlayerId.PLAYER_1, new SimpleIntegerProperty(0), PlayerId.PLAYER_2, new SimpleIntegerProperty(0));
        this.playerCars = Map.of(PlayerId.PLAYER_1, new SimpleIntegerProperty(0), PlayerId.PLAYER_2, new SimpleIntegerProperty(0));
        this.playerConstructionPoints = Map.of(PlayerId.PLAYER_1, new SimpleIntegerProperty(0), PlayerId.PLAYER_2, new SimpleIntegerProperty(0));
        this.tickets = FXCollections.observableArrayList();
        this.cardsNumber = createCardsNumber();
        this.claimableRoutes = createClaimableRoutes();
        this.connectivityObjectProperty = new SimpleObjectProperty<>(null);
        for(Route r : ChMap.routes()){
            for(Route r2 : ChMap.routes()){
                if(!r.equals(r2) && r.stations().equals(r2.stations())){
                    LIST_DOUBLE_ROUTES.add(List.of(r, r2));
                }
            }
        }
    }

    /**
     * updates all of the properties described on the newPublicGameState and the newOwnState
     * @param newPublicGameState, the public game state
     * @param newOwnState, the complete state of my player
     */
    public void setState(PublicGameState newPublicGameState, PlayerState newOwnState) {

        publicGameState = newPublicGameState;
        ownState = newOwnState;
        connectivityObjectProperty.set(newOwnState.connectivity());


        ticketsPercentage.set(publicGameState.ticketsCount()*100/ChMap.tickets().size());
        cardsPercentage.set(publicGameState.cardState().deckSize()*100/Constants.TOTAL_CARDS_COUNT);
        for(int slot : Constants.FACE_UP_CARD_SLOTS){
            Card newCard = publicGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }
        for (Route r : ChMap.routes()){
            if(newPublicGameState.claimedRoutes().contains(r)){
                claimedRoute.get(r).set((ownState.routes().contains(r)) ? ownId : ownId.next());

            }else{
                claimedRoute.get(r).set(null);
            }
        }
        for(PlayerId id : PlayerId.ALL){
            playerTickets.get(id).set(publicGameState.playerState(id).ticketCount());
            playerCars.get(id).set(publicGameState.playerState(id).carCount());
            playerCards.get(id).set(publicGameState.playerState(id).cardCount());
            if(id.equals(ownId)){
                playerConstructionPoints.get(id).set(ownState.finalPoints());
            }else{
                playerConstructionPoints.get(id).set(publicGameState.playerState(id).claimPoints());
            }
        }

        tickets.setAll(newOwnState.tickets().toList());
        for(Card c : Card.ALL){
            int count = 0;
            for(Card d : ownState.cards()){
                if(c.equals(d)){
                    count += 1;
                }
            }
            cardsNumber.get(c).set(count);
        }

        for(Map.Entry<Route, BooleanProperty> map : claimableRoutes.entrySet()){
            map.getValue().set(ownId.equals(newPublicGameState.currentPlayerId()) && !routeTaken(doubleRoutes(map.getKey()), newPublicGameState) && ownState.canClaimRoute(map.getKey()));
        }



    }

    /**
     * @return canDrawTickets, the update state
     */
    public ReadOnlyBooleanProperty canDrawTickets(){
        return new SimpleBooleanProperty(publicGameState.canDrawTickets());
    }


    /**
     * @return canDrawCards, the boolean if the players can draw cards
     */
    public ReadOnlyBooleanProperty canDrawCards(){
        return new SimpleBooleanProperty(publicGameState.canDrawCards());
    }

    /**
     * @param route, claimed by the player
     * @return possibleClaimCards, the list of all the sets of cards that the player can use to take a route he claims
     */
    public ObservableList<SortedBag<Card>> possibleClaimCards(Route route){
        return FXCollections.observableArrayList(ownState.possibleClaimCards(route));
    }


    private static List<ObjectProperty<Card>> createFaceUpCards(){
        List<ObjectProperty<Card>> faceUpCards = new ArrayList<>();
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            faceUpCards.add(new SimpleObjectProperty<Card>(null));
        }
        return faceUpCards;
    }


    private static Map<Route, ObjectProperty<PlayerId>> createClaimedRoute(){
        Map<Route, ObjectProperty<PlayerId>> map = new HashMap<>();
        for (Route r : ChMap.routes()){
            map.put(r, new SimpleObjectProperty<PlayerId>(null));
        }
        return map;
    }

    private static Map<Card, IntegerProperty> createCardsNumber(){
        Map<Card, IntegerProperty> map = new HashMap<>();
        for (Card c : Card.ALL){
            map.put(c, new SimpleIntegerProperty(0));
        }
        return map;
    }

    private static Map<Route, BooleanProperty> createClaimableRoutes(){
        Map<Route, BooleanProperty> map = new HashMap<>();
        for (Route r : ChMap.routes()){
            map.put(r, new SimpleBooleanProperty(false));
        }
        return map;
    }

    private static List<Route> doubleRoutes(Route r){

        for(List<Route> doubleRoutes : LIST_DOUBLE_ROUTES){
            if(doubleRoutes.contains(r)){
                return doubleRoutes;
            }
        }
        return List.of();
    }

    private static boolean routeTaken(List<Route> routeList, PublicGameState gameState){

        for(PlayerId id : PlayerId.ALL){
            for(Route r : routeList){
                if(gameState.playerState(id).routes().contains(r)){
                    return true;
                }
            }
        }
        return false;
    }

    public ObjectProperty<StationConnectivity> getConnectivityProperty(){
        return connectivityObjectProperty;
    }

    /**
     * @param r, the route
     * @return getOwner, the property of the owner of the route
     */
    public ReadOnlyObjectProperty<PlayerId> getOwner(Route r) {
        return claimedRoute.get(r);
    }

    /**
     * @param id, the id player
     * @return getTicketCount, the getter of the update ticket count of the given player
     */
    public ReadOnlyIntegerProperty getTicketCount(PlayerId id){
        return playerTickets.get(id);
    }

    /**
     * @param id, the id player
     * @return getCardsCount, the getter of the update cards count of the given player
     */
    public ReadOnlyIntegerProperty getCardsCount(PlayerId id){
        return playerCards.get(id);
    }

    /**
     * @param id, the id player
     * @return getCarsCount, the getter of the update (property) cars count of the given player
     */
    public ReadOnlyIntegerProperty getCarsCount(PlayerId id){
        return playerCars.get(id);
    }

    /**
     * @param id, the id player
     * @return getConstructionPoints, the property of the construction points of the given player
     */
    public IntegerProperty getConstructionPoints(PlayerId id){
        return playerConstructionPoints.get(id);
    }

    /**
     * @return getTickets, the getter of the current list of tickets
     */
    public ObservableList<Ticket> getTickets() {
        return tickets;
    }

    /**
     * @param c, card
     * @return getCardsNumber, the property of the card number
     */
    public ReadOnlyIntegerProperty getCardsNumber(Card c) {
        return cardsNumber.get(c);
    }

    /**
     * @param r, the route claimed by the player
     * @return getClaimableRoutes, the property for the claimable route
     */
    public ReadOnlyBooleanProperty getClaimableRoutes(Route r) {
        return claimableRoutes.get(r);
    }

    /**
     * @return getTicketsPercentage, the percentage of the ticket's deck.
     */
    public int getTicketsPercentage() {
        return ticketsPercentage.get();
    }

    /**
     * @return ticketsPercentageProperty, the property of current percentage of the ticket's deck.
     */
    public ReadOnlyIntegerProperty ticketsPercentageProperty() {
        return ticketsPercentage;
    }

    /**
     * @return getCardsPercentage, the percentage of the card's deck.
     */
    public int getCardsPercentage() {
        return cardsPercentage.get();
    }

    /**
     * @return cardsPercentageProperty, the property of current percentage of the ticket's deck.
     */
    public ReadOnlyIntegerProperty cardsPercentageProperty() {
        return cardsPercentage;
    }

    /**
     * @param slot, the position of the face up cards
     * @return getFaceUpCards, the property of the five face up cards
     */
    public ReadOnlyObjectProperty<Card> getFaceUpCards(int slot) {
        return faceUpCards.get(slot);
    }


    public boolean getHasRoute(){
        return ownState.hasRoute();
    }

    public PlayerId getOwnId(){
        return ownId;
    }

}