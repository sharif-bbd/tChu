package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.tchu.gui.Info.cardName;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class CardBagStringConverter extends StringConverter<SortedBag<Card>> {


    /**
     * @param object, sorted bag of cards
     * @return toString, convert the sorted bag of cards to a string
     */
    @Override
    public String toString(SortedBag<Card> object) {
        List<String> all = new ArrayList<>();

        for (Card c : object.toSet()) {
            int multiplicity = object.countOf(c);
            all.add(multiplicity + " " + cardName(c, multiplicity));
        }
        if (all.size() == 1) {
            return all.get(0);
        } else if (all.size() == 2) {
            return all.get(0).toString() + StringsFr.AND_SEPARATOR + all.get(1).toString();
        } else {

            List<String> minusOne = all.subList(0, all.size() - 1);
            String one = all.get(all.size() - 1);

            String text1 = String.join(", ", minusOne);
            String text2 = StringsFr.AND_SEPARATOR + one;

            return text1 + text2;
        }
    }

    /**
     * @param string, string converter
     * @return fromString, convert the string to a sorted bag of cards
     * @throws UnsupportedOperationException, because we never use this method
     */
    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }

}