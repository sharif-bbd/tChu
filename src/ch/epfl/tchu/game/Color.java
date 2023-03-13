package ch.epfl.tchu.game;

import java.util.List;
/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */

public enum Color{

    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    /**
     * ALL : list of all the color
     * list of the size of ALL
     */
    public final static List<Color> ALL = List.of(Color.values());
    public final static int COUNT = ALL.size();


}
