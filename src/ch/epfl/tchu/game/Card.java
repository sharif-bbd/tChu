package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */

public enum Card {
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);


    public final static List<Card> ALL = List.of(Card.values());
    public final static int COUNT = ALL.size();
    public final static List<Card> CARS = List.of(
            BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);
    private final Color color;


    /**
     *
     *Construct the color of the card
     * @param c
     */

    Card(Color c) {
        color = c;
    }

    /**
     *Each cars correspond to a color
     * @param color
     * @return the color of the car
     */

    public static Card of(Color color){
        for(Card cars : CARS){
            if(cars.color == color)
                return cars;
        }
        return null;
    }


    /**
     *
     * @return the color of the card and null if it is a locomotive
     */
    public Color color(){
        return color;
    }


}

