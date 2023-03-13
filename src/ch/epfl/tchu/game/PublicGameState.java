package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public class PublicGameState {

    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;


    /**
     * build the public state of a game state
     * @param ticketsCount, number of tickets contained in the deck
     * @param cardState, public state of the cards (cars/locomotive)
     * @param currentPlayerId, the current player
     * @param playerState, public state of the players
     * @param lastPlayer, the id of the last player (null if this id is still unknown)
     * @throws IllegalArgumentException, if the deck size is strictly negative or if playerState does not contain exactly two pairs of keys
     * @throws NullPointerException, if either one of the arguments is null (except for lastPlayer)
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer){
        Preconditions.checkArgument(ticketsCount >= 0);
        Preconditions.checkArgument(playerState.size() == 2);
        if( (cardState.equals(null)) || (currentPlayerId.equals(null)) || (playerState.isEmpty())){
            throw new NullPointerException();
        }
        this.ticketsCount = ticketsCount;
        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.playerState =playerState;
        this.lastPlayer = lastPlayer;
    }

    /**
     *the size of ticket's deck
     * @return ticketsCount, the size of ticket's deck
     */
    public int ticketsCount(){
        return ticketsCount;
    }

    /**
     * return true if and only if it's possible to draw the tickets cards
     * @return canDrawTickets, true if the deck is not empty
     */
    public boolean canDrawTickets(){
        return ticketsCount > 0;
    }

    /**
     *return the public state of the cards
     * @return cardState, is the public state
     */
    public PublicCardState cardState(){
        return cardState;
    }

    /**
     *return true if and only if the player can draw cards
     * @return canDrawCards, true if the deck and the discard contain at least 5 cards
     */
    public boolean canDrawCards(){
        return cardState.deckSize() + cardState.discardsSize() >= 5;
    }

    /**
     * return the current player id
     * @return currentPlayerId, the current player id
     */
    public PlayerId currentPlayerId(){
        return currentPlayerId;
    }

    /**
     * return the public state of the given player id
     * @param playerId, the given player id
     * @return playerState, the public state of the given player id
     */
    public PublicPlayerState playerState(PlayerId playerId){
        return playerState.get(playerId);
    }

    /**
     *return the public state of the current player
     * @return currentPlayerState, return the public state of the current player
     */
    public PublicPlayerState currentPlayerState(){
        return playerState(currentPlayerId);
    }

    /**
     *return all the routes claimed by either one of the player
     * @return claimedRoutes, all the routes obtained by a player
     */
    public List<Route> claimedRoutes(){
        List<Route> claimedRoutes = new ArrayList<>(playerState.get(currentPlayerId).routes());
        claimedRoutes.addAll(playerState.get(currentPlayerId.next()).routes());
        return claimedRoutes;
    }

    /**
     * return the id of the last player or null if we don't know the id of the last player because the game didn't start
     * @return lastPlayer, the id of the last player
     */
    public PlayerId lastPlayer(){
        return lastPlayer;
    }
}