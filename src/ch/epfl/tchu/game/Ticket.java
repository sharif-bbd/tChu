package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.HashSet;
import java.util.List;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class Ticket implements Comparable<Ticket> {

    private final List<Trip> trips;
    private final String smt;

    /**
     *
     * @param trips list of all the trips from one starting stations
     */
    public Ticket(List<Trip> trips) {

        Preconditions.checkArgument((trips != null)&&(!trips.isEmpty()));
        for (int i = 0; i < trips.size(); ++i){
            Preconditions.checkArgument(trips.get(0).from().name() == trips.get(i).from().name());
        }


        this.trips = List.copyOf(trips);
        smt = computeText(trips);


    }

    /**
     *
     * @param from starting station of the trip
     * @param to arriving station of the trip
     * @param points number of points of the trip
     */
    public Ticket(Station from, Station to, int points){
        this(List.of(new Trip(from, to, points)));


    }

    /**
     *
     * @return textual representation of the ticket
     */
    public String text(){
        return smt;
    }

    /**
     *
     * @param connectivity determines if the two stations are connected by the player or not
     * @return the maximum points of the trip
     */
    public int points(StationConnectivity connectivity){
        int max =  Integer.MIN_VALUE;
        for (int i = 0; i < this.trips.size(); ++i){
            if(this.trips.get(i).points(connectivity) > max){
                max = this.trips.get(i).points(connectivity);
            }
        }
        return max;
    }

    /**
     *
     * @param trips list of all the trips from one starting station
     * @return the textual representation of the ticket
     */
    private static String computeText(List<Trip> trips){
        String text;
        HashSet<String> stationTo = new HashSet<>();

        for(Trip to : trips){
            stationTo.add( to.to().toString() + " (" + to.points() +")");
        }

        if(trips.size() == 1){
            text = String.format("%s - %s", trips.get(0).from().name(), String.join(", ",stationTo));
        } else {
            text = String.format("%s - {%s}", trips.get(0).from().name(), String.join(", ",stationTo));
        }
        return text;
    }


    /**
     *
     * @param that a thicket
     * @return an integer depending on the alphabetical order of the two tickets compared
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text().compareTo(that.text());
    }


    /**
     *
     * @return textual representation of the ticket
     */
    @Override
    public String toString(){
        return smt;
    }

    public String toString(StationConnectivity connectivity){
        int points = points(connectivity);

        return (points >= 0) ? smt + " vous fais gagner : " + points : smt + " vous fais perdre : " + points;
    }
}
