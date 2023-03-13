package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public class PublicCardState {

    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardSize;

    /**
     * build a public state of the cards which are known by the players
     * @param faceUpCards, which are the 5 cards visible
     * @param deckSize, is the size of the deck
     * @param discardsSize, is the size of the discards
     * @throws IllegalArgumentException if faceUpCards is not exactly equals to 5 and if
     * the deckSize or the discardsSize are negatives
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        Preconditions.checkArgument((faceUpCards.size() == 5) && (deckSize>=0) && (discardsSize>=0));
        this.deckSize = deckSize;
        this.discardSize = discardsSize;
        this.faceUpCards = List.copyOf(faceUpCards);

    }

    /**
     * size of the deck (cards which don't belong to the players)
     * @return totalSize, the number of cards which are not owned by the players
     */
    /*public int totalSize(){
        int nonOwnedCards = this.deckSize() + this.discardsSize() + this.faceUpCards.size();
        return nonOwnedCards;
    }

     */

    /**
     * return the 5 face up cards into a list containing 5 elements
     * @return faceUpCards, the 5 face up cards
     */
    public List<Card> faceUpCards(){

        return faceUpCards;

    }

    /**
     *return the face up card in a given index
     * @param slot, index
     * @return faceUpCard, the face up card indicated by the index
     * @throws IndexOutOfBoundsException if the index is not between 0 (included) and 5 (excluded)
     */
    public Card faceUpCard(int slot){

        return faceUpCards.get(Objects.checkIndex(slot,5));

    }

    /**
     * return the size of the deck
     * @return deckSize, the size of the deck
     */
    public int deckSize(){
        return deckSize;
    }

    /**
     *return true if the deck is empty
     * @return isDeckEmpty, true if it's empty
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     * return the size of the discards
     * @return discardsSize, the size of the discards
     */
    public int discardsSize(){
        return discardSize;
    }

}