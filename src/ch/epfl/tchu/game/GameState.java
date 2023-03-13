package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class GameState extends PublicGameState{
    private final Deck<Ticket> tickets;
    private final Map<PlayerId, PlayerState> playerState;
    private final CardState cardState;


    private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);
        this.playerState = Map.copyOf(playerState);
        this.tickets = tickets;
        this.cardState = cardState;


    }

    /**
     *return the initial state of a tCHu game
     * @param tickets, that are contained in a deck
     * @param rng, use to shuffle the deck of cards and the deck of tickets and to choose randomly the current player
     * @return initial, the initial state of a game
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng){
        SortedBag<Card> cards = Constants.ALL_CARDS;
        Deck<Ticket> newTicket = Deck.of(tickets, rng);
        Deck<Card> newCard = Deck.of(cards, rng);

        Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(PlayerId.class);

        PlayerId currentPlayer = PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT));
        PlayerId lastPlayer = currentPlayer.next();


        newPlayerState.put(currentPlayer, PlayerState.initial(newCard.topCards(Constants.INITIAL_CARDS_COUNT)));
        newCard = newCard.withoutTopCards(Constants.INITIAL_CARDS_COUNT);

        newPlayerState.put(lastPlayer, PlayerState.initial(newCard.topCards(Constants.INITIAL_CARDS_COUNT)));
        newCard = newCard.withoutTopCards(Constants.INITIAL_CARDS_COUNT);

        CardState newCardState = CardState.of(newCard);

        GameState state = new GameState(newTicket, newCardState, currentPlayer, newPlayerState, null);

        return state;
    }

    /**
     *return the complete state -not only the public state- of the given player
     * @param playerId, the given player id
     * @return playerState, the complete state of the given player
     */
    @Override
    public PlayerState playerState(PlayerId playerId){
        return playerState.get(playerId);
    }

    /**
     *return the complete state -not only the public state- of the current player
     * @return currentPlayerState, the complete state of the current player
     */
    @Override
    public PlayerState currentPlayerState(){

        return playerState(currentPlayerId());
    }

    /**
     * return the count top tickets of the deck
     * @param count, number of top tickets
     * @return topTickets, the count top tickets of the deck
     * @throws IllegalArgumentException,if count is not between 0 (included) and the size of the deck (included). This exception
     * is checked in the method topCards of PublicGameState (super class)
     */
    public SortedBag<Ticket> topTickets (int count){

        SortedBag<Ticket> top = tickets.topCards(count);

        return top;
    }

    /**
     *the same state to receiver but with count tickets removed from the top of the deck
     * @param count, the number of tickets removed from the deck
     * @return withoutTopTickets, the same state to receiver but with count tickets removed from the top of the deck
     * @throws IllegalArgumentException,if count is not between 0 (included) and the size of the deck (included). This exception
     * is checked in the method withoutTopCards of PublicGameState (super class)
     */
    public GameState withoutTopTickets(int count){
        Deck<Ticket> newTickets = tickets.withoutTopCards(count);

        GameState newState = new GameState(newTickets, this.cardState, this.currentPlayerId(), this.playerState, this.lastPlayer());

        return newState;
    }

    /**
     * return the top card of the deck
     * @return topCard, return the top card of the deck
     * @throws IllegalArgumentException, if the deck is empty
     */
    public Card topCard(){
        return cardState.topDeckCard();
    }

    /**
     * identical state to receiver but without the top card of the deck
     * @return withoutTopCard, identical state to receiver but without the top card of the deck
     * @throws IllegalArgumentException, if the deck is empty
     */
    public GameState withoutTopCard(){
        CardState newCardState = cardState.withoutTopDeckCard();
        GameState newGameState = new GameState(tickets, newCardState, this.currentPlayerId(), playerState, this.lastPlayer());
        return newGameState;
    }

    /**
     * @param discardedCards, cards added to the discard
     * @return withMoreDiscardedCards, the identical state to receiver but with more discarded cards
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
        CardState newCardState = cardState.withMoreDiscardedCards(discardedCards);
        GameState newGameState = new GameState(tickets, newCardState, this.currentPlayerId(), playerState, this.lastPlayer());
        return newGameState;
    }

    /**
     * return an identical state to receiver only if the deck is empty. In this case, the deck is shuffle by a random generator and recreate from the discard
     * @param rng, a random number to shuffle the deck
     * @return withCardsDeckRecreatedIfNeeded
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        if (cardState.isDeckEmpty()){
            CardState newCardState = cardState.withDeckRecreatedFromDiscards(rng);
            GameState recreateGameState = new GameState(tickets, newCardState, this.currentPlayerId(), playerState, this.lastPlayer());
            return recreateGameState;
        }else{
            return this;
        }
    }

    /**
     *an identical state to receiver but chosenTickets are added to the given player's hand of cards
     * @param playerId, given player of the game
     * @param chosenTickets, the tickets chosen by the given player who decided to keep them.
     * @return withInitiallyChosenTickets, an identical state to receiver but chosenTickets are added to the given player's hand of cards
     * @throws IllegalArgumentException, if the deck of tickets is empty
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){
        PlayerState playerIdState = playerState(playerId);
        Preconditions.checkArgument(playerIdState.tickets().isEmpty());
        Map<PlayerId, PlayerState> newMap = new EnumMap<PlayerId, PlayerState>(playerState);
        PlayerState withAddedTickets = newMap.get(playerId).withAddedTickets(chosenTickets);
        newMap.put(playerId, withAddedTickets);

        GameState newGameState = new GameState(tickets, this.cardState, this.currentPlayerId(), newMap, this.lastPlayer());

        return newGameState;
    }
    /**
     * @param drawnTickets, tickets drawn by the player
     * @param chosenTickets, the chosen tickets among the drawn tickets
     * @return withChosenAdditionalTickets, an identical state to receiver but the player has drawn tickets from the top deck
     * and chose to keep some of them.
     * @throws IllegalArgumentException, if the chosen tickets is not included in drawn tickets
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        Deck<Ticket> newTickets = tickets.withoutTopCards(drawnTickets.size());

        Map<PlayerId, PlayerState> newMap = new EnumMap<PlayerId, PlayerState>(playerState);
        PlayerState withAddedTickets = newMap.get(currentPlayerId()).withAddedTickets(chosenTickets);
        newMap.put(currentPlayerId(), withAddedTickets);

        GameState newGameState = new GameState(newTickets, this.cardState, this.currentPlayerId(), newMap, this.lastPlayer());

        return newGameState;

    }

    /**
     * return an identical state to receiver, only that the drawn face up card (at a given index) is put into the current player's hand of cards and was
     * replaced by the card on the top of the deck
     * @param slot, location of the drawn card
     * @return withDrawnFaceUpCard, return an identical state to receiver, only that the drawn face up card (at a given index)
     * is put into the current player's hand of cards
     * @throws IllegalArgumentException, if it's not possible to draw cards, in other words canDrawCards return false
     */
    public GameState withDrawnFaceUpCard(int slot){
        Map<PlayerId, PlayerState> newMap = new EnumMap<PlayerId, PlayerState>(playerState);
        PlayerState newPlayerState = newMap.get(currentPlayerId()).withAddedCard(cardState.faceUpCard(slot));
        newMap.put(currentPlayerId(), newPlayerState);
        CardState newCardState = cardState.withDrawnFaceUpCard(slot);

        GameState newGameState = new GameState(tickets, newCardState, currentPlayerId(), newMap, lastPlayer());

        return newGameState;
    }

    /**
     *return an identical state to receiver, only that the top card of the deck is distributed to the current player's hand.
     * @return withBlindlyDrawnCard, an identical state to receiver, only that the top card of the deck is distributed to the current player's hand
     * @throws IllegalArgumentException, if it's not possible to draw cards.
     */
    public GameState withBlindlyDrawnCard(){


        Map<PlayerId, PlayerState> newMap = new EnumMap<PlayerId, PlayerState>(playerState);
        PlayerState withTopCard = newMap.get(currentPlayerId()).withAddedCard(topCard());
        newMap.put(currentPlayerId(),withTopCard);

        CardState newCards = this.cardState.withoutTopDeckCard();
        GameState newGameState = new GameState(this.tickets, newCards, this.currentPlayerId(), newMap, this.lastPlayer());

        return newGameState;

    }

    /**
     *return an identical state to receiver, only that the given player obtains the claimed route with his given cards
     * @param route, the claimed route of the player
     * @param cards, the cards used by the players to obtain his claimed route
     * @return withClaimedRoute, the identical state to receiver, only that the given player obtains the claimed route with his given cards
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){
        Map<PlayerId, PlayerState> newMap = new EnumMap<>(playerState);

        PlayerState withAddedRoute = newMap.get(currentPlayerId()).withClaimedRoute(route, cards);
        newMap.put(currentPlayerId(), withAddedRoute);

        GameState newGameState = new GameState(tickets, cardState.withMoreDiscardedCards(cards), currentPlayerId(), newMap, lastPlayer());

        return newGameState;
    }

    /**
     *return true if the id of the last player is null and the current player possesses less than two cars
     * @return lastTurnBegins, if the id of the last player is still unknown but the current player possesses less than two cars
     */
    public boolean lastTurnBegins(){
        boolean lastPlayerId = lastPlayer() == null;
        return (lastPlayerId && currentPlayerState().carCount() <= 2);
    }

    /**
     *end the turn of the current player
     * @return forNextTurn, an identical state to receiver, only the current player is becoming the next player
     */
    public GameState forNextTurn(){
        return (lastTurnBegins()) ? new GameState(tickets, this.cardState, currentPlayerId().next(), this.playerState , currentPlayerId())
                : new GameState(tickets, this.cardState, currentPlayerId().next(), this.playerState, this.lastPlayer());

    }

    public GameState withSwitchedRoutes(Route wantedRoute, Route givenRoute){
        Map<PlayerId, PlayerState> newMap = new EnumMap<>(playerState);
        PlayerState one = newMap.get(currentPlayerId()).switchRoutes(wantedRoute, givenRoute);
        PlayerState two = newMap.get(currentPlayerId().next()).switchRoutes(givenRoute, wantedRoute);
        newMap = Map.of(currentPlayerId(), one, currentPlayerId().next(), two);

        GameState newGameState = new GameState(this.tickets, this.cardState, currentPlayerId(), newMap, lastPlayer());

        return newGameState;
    }


}