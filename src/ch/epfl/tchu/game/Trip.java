package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class Trip {
    private final  Station from;
    private final Station to;
    private final int points;

    /**
     *
     * @param from the starting station of the trip
     * @param to   the station representing the destination of the trip
     * @param points the number of points this trip gives
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points>0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;

    }

    /**
     *
     * @param from a list of all the starting stations
     * @param to a list of all the destination stations
     * @param points the number of point a trip gives
     * @return a list of all possible trips
     */
    public static List<Trip> all (List<Station> from, List<Station> to, int points){
        Preconditions.checkArgument((from != null) && (to != null) && (points >= 0) && (!from.isEmpty()) && (!to.isEmpty()));
        ArrayList<Trip> trips = new ArrayList<>();
        for (Station start: from){
            for (Station end : to){
                trips.add(new Trip(start, end, points));
            }
        }

        return trips;
    }

    /**
     *
     * @return getter for the starting station of a trip
     */
    public Station from() {
        return from;
    }

    /**
     *
     * @return getter for the end station of a trip
     */
    public Station to() {
        return to;
    }

    /**
     *
     * @return getter for the number of points of a trip
     */
    public int points() {
        return points;
    }

    /**
     *
     * @param connectivity gives us info on whether or
     *                     not the stations of the trip are linked
     * @return the number of point that has been won or lost
     */
    public int points(StationConnectivity connectivity){
        if (connectivity.connected(from, to)){
            return points;
        }else{
            return -points;
        }
    }
}
