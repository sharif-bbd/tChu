package ch.epfl.tchu.gui;


import ch.epfl.tchu.SortedBag;


import ch.epfl.tchu.game.*;
import javafx.application.Platform;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;



/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class GraphicalPlayerAdapter implements Player {


    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<SortedBag<Ticket>> initChosenTickets = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Route> claimedRoute = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<TurnKind> turnKinds = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Integer> slot = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Card>> claimedCards = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Card>> addClaimedCards = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Route> wantedRoute = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Route> givenRoute = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<String> playerNamesBlockingQueue = new ArrayBlockingQueue<>(1);

    /**
     *
     */
    public GraphicalPlayerAdapter(){}


    /**
     * At the beginning of the game, to communicate the own Id of the player and the different names of the player
     * @param ownId,  the Id player
     * @param playerNames, the players names
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        BlockingQueue<GraphicalPlayer> graphicalPlayerBlockingQueue = new ArrayBlockingQueue<>(1);
        Platform.runLater(() -> {
            throwExceptionForPut(graphicalPlayerBlockingQueue, new GraphicalPlayer(ownId, playerNames, (r) ->{
                throwExceptionForPut(givenRoute, r);
            }));
            this.graphicalPlayer = throwExceptionForTake(graphicalPlayerBlockingQueue);
        });
    }

    /**
     * called when an information has to be communicate to the player during the game.
     * @param info, the info for the player
     */
    @Override
    public void receiveInfo(String info) {
        Platform.runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * called everytime to make an update state of the game
     *inform the player of the new public state and his own state
     * @param newState, new public state
     * @param ownState, the state of the player
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        Platform.runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     *When the game starts, the method tells the player the five tickets he got
     * @param tickets, the player's tickets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

        Platform.runLater(() -> graphicalPlayer.chooseTickets(tickets, (ticketChoice) -> {
                    throwExceptionForPut(initChosenTickets, ticketChoice);
                }
        ));

    }

    /**
     *  when the game starts, the player chooses three initial tickets
     * @return chooseInitialTickets, three initial tickets chosen by the player
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return throwExceptionForTake(initChosenTickets);
    }



    /**
     * When the turn of the player starts, he chooses which action he wants to play.
     * @return nextTurn, the action chosen by the player
     */
    @Override
    public TurnKind nextTurn() {
        Platform.runLater(() -> graphicalPlayer.startTurn(

                () -> throwExceptionForPut(turnKinds, TurnKind.DRAW_TICKETS),

                (s) -> {
                    //TODO: lance excpetion pour chacun ou le faire dans le meme try catch ? lancer pour chacun :)
                    throwExceptionForPut(turnKinds, TurnKind.DRAW_CARDS);
                    throwExceptionForPut(slot,s);
                },

                (r, cards) -> {
                    throwExceptionForPut(turnKinds, TurnKind.CLAIM_ROUTE);
                    throwExceptionForPut(claimedRoute, r);
                    throwExceptionForPut(claimedCards, cards);
                },

                (wRoute) -> {
                    throwExceptionForPut(turnKinds, TurnKind.SWITCH_ROUTE);
                    throwExceptionForPut(wantedRoute, wRoute);
                }

        ));

        return throwExceptionForTake(turnKinds);
    }

    /**
     * called when the player draws additional tickets during the game in order to communicate the tickets drawn and to
     * know which one the player keep
     * @param options, the list of tickets
     * @return chooseTickets, he player choose the tickets he wants to keep.
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }


    /**
     * when the player decide to draw some cards, either from the deck or from the 5 visible face up cards.
     * @return drawSlot, when the player decide to draw some cards, either from the deck or from the 5 visible face up cards.
     */
    @Override
    public int drawSlot() {
        if (slot.isEmpty()) {
            Platform.runLater(() -> graphicalPlayer.drawCard(
                    (s) -> throwExceptionForPut(slot,s)));
        }
        return throwExceptionForTake(slot);

    }
    /**
     * called when a player claimed a route in order to know the route
     * @return claimedRoute, the route claimed by a player
     */
    @Override
    public Route claimedRoute() {
        return throwExceptionForTake(claimedRoute);
    }

    /**
     * called when a player claimed a route in order to know which cards he decides to play
     * @return initialClaimCards, the player's initial cards in order to get the route he desired
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return throwExceptionForTake(claimedCards);
    }

    /**
     * When the player has decided to get a tunnel and given the additional cards, in order to know which possibilities
     * of cards he can use. If the sorted bag is empty, it means that the player doesn't/can't
     * get the route with his cards
     * @param options, the options the player can use to get his route
     * @return chooseAdditionalCards, the cards the player can use to obtain his route
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        Platform.runLater(()-> graphicalPlayer.chooseAdditionalCards(options, (chosenC) -> {
            throwExceptionForPut(addClaimedCards, chosenC);
        }));
        return throwExceptionForTake(addClaimedCards);
    }


    //TODO : est ce que mes methodes marchent ?
    private <E> void throwExceptionForPut(BlockingQueue<E> q, E param){
        try {
            q.put(param);
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    private <E> E throwExceptionForTake(BlockingQueue<E> q) {
        try {
            return q.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public Route claimWantedRoute() {
        return throwExceptionForTake(wantedRoute);
    }

    @Override
    public Route givenRoute() {
        return throwExceptionForTake(givenRoute);
    }

    @Override
    public Boolean acceptSwitchRoutes(Route wantedRoute, Route givenRoute) {
        BlockingQueue<Boolean> booleanBlockingQueue = new ArrayBlockingQueue<Boolean>(1);
        Platform.runLater(() -> {
            graphicalPlayer.viewRequest(wantedRoute, givenRoute, (b) -> throwExceptionForPut(booleanBlockingQueue, b));
        });
        return throwExceptionForTake(booleanBlockingQueue);
    }

    @Override
    public String choosePlayerName() {
        return throwExceptionForTake(playerNamesBlockingQueue);
    }

    @Override
    public void setPlayerNames() {
        Platform.runLater(() -> GraphicalPlayer.choosePlayerName(s -> throwExceptionForPut(playerNamesBlockingQueue, s)));
    }

}