package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2;


    /**
     * return the list of the player
     */
    public final static List<PlayerId> ALL = List.of(PlayerId.values());
    /**
     * return the number of player
     */
    public final static int COUNT = ALL.size();

    /**
     *return the next player ID
     *@return next, the next player ID
     */
    public PlayerId next() {
        return this.equals(PlayerId.PLAYER_1) ? PlayerId.PLAYER_2 : PlayerId.PLAYER_1;
    }


}