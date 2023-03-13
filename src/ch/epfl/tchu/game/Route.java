package ch.epfl.tchu.game;



import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class Route {

    /**
     * represents the two different types of routes.
     */
    public enum Level {OVERGROUND, UNDERGROUND;}

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;
    /**
     * construct a route
     * @param id, identification of each station
     * @param station1, a station (start)
     * @param station2, a station (end)
     * @param length of the routes
     * @param level of the route
     * @param color of the route
     *
     * @throws IllegalArgumentException if the station1 and the station2 are equals
     * or if the length is not between the standards limits
     *
     * @throws NullPointerException if the id, one of the station or the level are null.
     *
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {

        Preconditions.checkArgument((!station1.equals(station2)) && (length>= Constants.MIN_ROUTE_LENGTH) && (length <= Constants.MAX_ROUTE_LENGTH));
        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.length =length;
        this.level = level;
        this.color= color;
        Objects.requireNonNull(id);
        Objects.requireNonNull(station1);
        Objects.requireNonNull(station2);
        Objects.requireNonNull(level);

    }

    /**
     * return the identification of the route
     * @return id, the identification of the route
     */
    public String id(){return id;}

    /**
     * return the first station of the route
     * @return station1, the first station
     */
    public Station station1(){return station1;}

    /**
     * return the second station of the route
     * @return station2, the second station
     */
    public Station station2(){return station2;}

    /**
     *return the length of the route
     * @return length, the length of the route
     */
    public int length(){return length;}

    /**
     * return the level at which the route is
     * @return level, level of the route
     */
    public Level level(){return level;}

    /**
     *return the color of the route or null if the road is a neutral color (light grey)
     * @return color, color of the route
     */
    public Color color(){return color;}

    /**
     * return the list of the two stations of the route in the order they were placed in the constructor
     * @return stations, the list of the 2 stations of the route
     */
    public List<Station> stations(){
        List<Station> stations = List.of(station1, station2);

        return stations;
    }

    /**
     * return the station of the route that is not one given
     * @param station, a station
     * @return stationOpposite, the station of the route that is not given
     * @throws IllegalArgumentException if the station is neither the first or the second station of the route
     */
    public Station stationOpposite(Station station){
        Preconditions.checkArgument( (station.equals(station1)) || (station.equals(station2)));
        if (station.equals(station1)){
            return station2;
        } else if (station.equals(station2)){
            return station1;
        }else{
            return null;
        }
    }

    /**
     * return the list of all the sets of cards which can be play or tempted to get a route.
     * The list is sorted in ascending order of the locomotive card then by color.
     * @return possibleClaimCards, the list of all the sets of card that can be used to get/attempt to get a route
     */
    /*
    ArrayList<SortedBag<Card>> au début de la méthode, et lui ajouter les SortedBag qui contiennent les cartes jouables (chaque SortedBag doit avoir la même taille que la Route).
    Je vous conseille d'utiliser ces deux méthodes statiques, elles permettent de créer les SortedBags corrects en une seule ligne.
    SortedBag<E> of(int n, E e)
    SortedBag<E> of(int n1, E e1, int n2, E e2)
     */
    public List<SortedBag<Card>> possibleClaimCards(){

        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();


        if((level == Level.UNDERGROUND) && (color == null)){
            for (int i = 0; i < length ; ++i){
                for(Card c : Card.CARS){
                    possibleClaimCards.add(SortedBag.of(length - i, c, i, Card.LOCOMOTIVE));
                }
            }

            possibleClaimCards.add(SortedBag.of(length, Card.LOCOMOTIVE));
            return possibleClaimCards;

        } else if ((level == Level.UNDERGROUND) && (color != null)){
            int index = color.ordinal() ;

            for (int i = 0; i <= length; ++i){

                possibleClaimCards.add(SortedBag.of(length - i, Card.ALL.get(index), i, Card.LOCOMOTIVE));

            }
            return possibleClaimCards;

        } else if ((level == Level.OVERGROUND) && (color == null)){
            for (Card c : Card.CARS){
                possibleClaimCards.add(SortedBag.of(length, c));
            }
            return possibleClaimCards;

        } else if((level == Level.OVERGROUND) && (color != null)){
            possibleClaimCards.add(SortedBag.of(length, Card.of(color)));
            return possibleClaimCards;
        }else{
            return null;
        }
    }

    /**
     * return the number of additional cards to put to get the route knowing that the player already put the claimCards
     * @param claimCards, the cards claims by the player to get the route
     * @param drawnCards, the cards drawn in the deck
     * @return additionalClaimCardsCount, the additional number of cards the player should play to get the underground route
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards){
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS && this.level == Level.UNDERGROUND);

        int additionalClaimCardsCount = 0;

        for(Card c1 : drawnCards){
            if(claimCards.contains(c1) || c1 == Card.LOCOMOTIVE){
                ++additionalClaimCardsCount;
            }
        }
        return additionalClaimCardsCount;

    }

    /**
     *
     * @return claimPoints, the number of points the player gets when he obtains the route
     */
    public int claimPoints(){

        return ((length >= Constants.MIN_ROUTE_LENGTH)&&(length <= Constants.MAX_ROUTE_LENGTH)) ? Constants.ROUTE_CLAIM_POINTS.get(length)
                : Constants.ROUTE_CLAIM_POINTS.get(0);

    }

    @Override
    public String toString(){
        return station1.name() + " - " + station2.name();
    }




}

