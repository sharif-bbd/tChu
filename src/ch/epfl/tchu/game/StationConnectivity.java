package ch.epfl.tchu.game;
/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public interface StationConnectivity {
    /**
     *
     * @param s1 a station
     * @param s2 a station
     * @return true if the stations are connected by the player and false otherwise
     */
    boolean connected(Station s1, Station s2);
}
