package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;
/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public interface Player {

    public enum TurnKind{
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE,
        SWITCH_ROUTE;

        /**
         * ALL: list of all the 3 possibilities a player can play during his turn
         */
        public final static List<TurnKind> ALL = List.of(TurnKind.values());
    }

    /**
     * At the beginning of the game, to communicate the own Id of the player and the different names of the player
     * @param ownId,  the Id player
     * @param playerNames, the players names
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * called when an information has to be communicate to the player during the game.
     * @param info, the info for the player
     */
    void receiveInfo(String info);

    /**
     * called everytime to make an update state of the game
     *inform the player of the new public state and his own state
     * @param newState, new public state
     * @param ownState, the state of the player
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     *When the game starts, the method tells the player the five tickets he got
     * @param tickets, the player's tickets
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     *  when the game starts, the player chooses three initial tickets
     * @return chooseInitialTickets, three initial tickets chosen by the player
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * When the turn of the player starts, he chooses which action he wants to play.
     * @return nextTurn, the action chosen by the player
     */
    TurnKind nextTurn();

    /**
     * called when the player draws additional tickets during the game in order to communicate the tickets drawn and to
     * know which one the player keep
     * @param options, the list of tickets
     * @return chooseTickets, he player choose the tickets he wants to keep.
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * when the player decide to draw some cards, either from the deck or from the 5 visible face up cards.
     * @return drawSlot, when the player decide to draw some cards, either from the deck or from the 5 visible face up cards.
     */
    int drawSlot();

    /**
     * called when a player claimed a route in order to know the route
     * @return claimedRoute, the route claimed by a player
     */
    Route claimedRoute();

    /**
     * called when a player claimed a route in order to know which cards he decides to play
     * @return initialClaimCards, the player's initial cards in order to get the route he desired
     */
    SortedBag<Card> initialClaimCards();

    /**
     * When the player has decided to get a tunnel and given the additional cards, in order to know which possibilities
     * of cards he can use. If the sorted bag is empty, it means that the player doesn't/can't
     * get the route with his cards
     * @param options, the options the player can use to get his route
     * @return chooseAdditionalCards, the cards the player can use to obtain his route
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    Route claimWantedRoute();

    Route givenRoute();

    Boolean acceptSwitchRoutes(Route wantedRoute, Route givenRoute);

    String choosePlayerName();

    void setPlayerNames();

}