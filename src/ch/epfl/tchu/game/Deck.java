package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class Deck<C extends Comparable<C>> {
    private final List<C> cards;

    private Deck(List<C> cards) {
        this.cards = cards;

    }


    /**
     * return a deck with the same cards of the SortedBag cards randomly shuffle
     * @param cards, which constitute the deck
     * @param rng, a random number to shuffle the deck
     * @param <C>, a type argument to form a deck of cards
     * @return of, a deck with the same cards but randomly shuffle
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){

        ArrayList<C> shuffle = new ArrayList<C>(cards.toList());

        Collections.shuffle(shuffle, rng);

        return new Deck<C>(shuffle);
    }

    /**
     * return the size of the deck (number of the cards contains)
     * @return size, the size of the deck which depends of the type argument
     */
    public int size(){
        return cards.size();
    }

    /**
     *return true if the list of cards is empty
     * @return isEmpty, true and it is the list of cards
     */
    public boolean isEmpty(){
        return cards.isEmpty();
    }

    /**
     * return the top card of the deck
     * @return topCard, the type argument to form a deck of cards
     * @throws IllegalArgumentException when the Deck is empty or is null
     */
    public C topCard(){
        Preconditions.checkArgument(!cards.isEmpty() && cards != null);
        return cards.get(cards.size()-1);
    }

    /**
     *return a new list of the deck but without the top card
     * @return withoutTopCard, a list of deck without the top card
     * @throws IllegalArgumentException when the Deck is empty or is null
     */
    public Deck<C> withoutTopCard(){
        Preconditions.checkArgument(!cards.isEmpty() && cards != null);
        List<C> deckWithoutTopCard = new ArrayList<>(cards.subList(0, this.size() - 1));

        return new Deck<C>(deckWithoutTopCard);
    }

    /**
     * return a sortedBag which contains the count cards on the top of the deck
     * @param count, the number of cards on the top of the deck
     * @return topCards of the deck
     * @throws IllegalArgumentException if count is not between 0 (included) and the size of the deck (included)
     */
    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument( ( 0 <= count) && ( count <= this.size()));
        List<C> topCards = new ArrayList<>(cards.subList(this.size() - count, this.size()));
        SortedBag<C> top = SortedBag.of(topCards);

        return top;
    }

    /**
     *return a new list of the deck but without the top card (count cards)
     * @param count, the number of cards on the top of the deck
     * @return withoutTopCards, a deck of card without the top cards
     * @throws IllegalArgumentException if count is not between 0 (included) and the size of the deck (included)
     */
    public Deck<C> withoutTopCards(int count){
        Preconditions.checkArgument( ( 0<= count) && ( count <= this.size()));
        List<C> deckWithoutTopCards = new ArrayList<>(cards.subList(0, this.size() - count));
        Deck<C> newDeck = new Deck<C>(deckWithoutTopCards);


        return newDeck;
    }
}