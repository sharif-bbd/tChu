package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class CardState extends PublicCardState{

    private final Deck<Card> deck;
    private final SortedBag<Card> discard;
    private final List<Card> faceUpCards;


    private CardState(List<Card> faceUpCards, SortedBag<Card> discard, Deck<Card> deck) {
        super(faceUpCards, deck.size(), discard.size());
        this.deck = deck;
        this.discard = discard;
        this.faceUpCards = List.copyOf(faceUpCards);

    }

    /**
     * returns the deck minus the five top Cards
     * the discard is empty
     * @param deck, deck of cards Card
     * @return an instance of CardState with a new Deck where the 5 top cards have been removed
     * @throws IllegalArgumentException if the deck contains less than 5 cards
     */
    public static CardState of(Deck<Card> deck){
        Preconditions.checkArgument(deck.size() >= 5);
        List<Card> newFaceUpCards = new ArrayList<>(deck.topCards(5).toList());
        Deck<Card> newDeck = deck.withoutTopCards(5);
        SortedBag<Card> newDiscard = SortedBag.of();


        return new CardState(newFaceUpCards, newDiscard, newDeck);
    }

    /**
     * returns a set of identical cards to the receiver
     * @param slot, index of the cards
     * @return withDrawnFaceUpCard, a set of identical cards to the receiver
     * @throws IndexOutOfBoundsException, if the index given is not between 0 (included) and 5 (excluded)
     * @throws IllegalArgumentException, if the deck is empty
     */
    public CardState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(!this.deck.isEmpty());
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        List<Card> newFaceUpCards = new LinkedList<>(this.faceUpCards);
        newFaceUpCards.remove(slot);
        newFaceUpCards.add(slot, this.deck.topCard());
        Deck<Card> newDeck = this.deck.withoutTopCard();

        return new CardState(newFaceUpCards, this.discard, newDeck);
    }

    /**
     *return the card on the top of the deck
     * @return topDeckCard, a Card located on the top of the deck
     * @throws IllegalArgumentException if the deck is empty
     */
    public Card topDeckCard(){
        Preconditions.checkArgument(!this.deck.isEmpty());
        return this.deck.topCard();
    }

    /**
     *returns a set of identical cards to the receiver (this) but without the top deck card
     * @return withoutTopDeckCard, a set of identical cards but without the top deck card
     * @throws IllegalArgumentException, if the deck is empty
     */
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(!deck.isEmpty());
        return new CardState(this.faceUpCards, this.discard, this.deck.withoutTopCard());
    }

    /**
     *returns a set of identical cards to the receiver (this) if only the discards are randomly shuffle and
     * the deck is recreated from discards
     * @param rng, to randomly shuffle the discards
     * @return withDeckRecreatedFromDiscards, the discards are randomly shuffle to recreate a new deck
     * @throws IllegalArgumentException, if the deck of the receiver is not empty
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(deck.isEmpty());
        Deck<Card> resetDeck = Deck.of(this.discard, rng);
        SortedBag<Card> resetDiscard = SortedBag.of();
        return new CardState(this.faceUpCards, resetDiscard, resetDeck);
    }

    /**
     *returns a set of identical cards to the receiver (this) with additional discarded cards
     * @param additionalDiscards, the additional cards for the discarded cards
     * @return withMoreDiscardedCards, the set of identical cards to the receiver but with more discarded cards
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        SortedBag<Card> biggerDiscards = this.discard.union(additionalDiscards);
        return new CardState(this.faceUpCards, biggerDiscards, this.deck);
    }


}