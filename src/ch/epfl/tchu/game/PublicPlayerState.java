package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */

public class PublicPlayerState {
    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;


    /**
     * construct the public player state who owns a certain number of tickets, cards and grab the routes he claimed
     * @param ticketCount, the number of tickets the players have
     * @param cardCount, the number of cards the player have
     * @param routes, which belong to the player
     * @throws IllegalArgumentException, if ticketCount and cardCount is strictly inferior to 0
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        Preconditions.checkArgument((ticketCount>=0)&&(cardCount>=0));
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
    }

    /**
     *return the number of the player's tickets
     * @return ticketCount, the number of tickets
     */
    public int ticketCount(){
        return ticketCount;
    }

    /**
     *return the number of the player's cards
     * @return cardCount, the number of cards
     */
    public int cardCount(){
        return cardCount;
    }

    /**
     *return the routes that belong to the player
     * @return routes, that the player have obtained
     */
    public List<Route> routes(){
        return routes;
    }

    /**
     * return the player's amount of cars
     * @return carCount, the number of cars that belong to the player
     */
    public int carCount(){
        int totalSize = 0;
        for(Route allRoutes : routes){
            totalSize += allRoutes.length();
        }
        return Constants.INITIAL_CAR_COUNT - totalSize;
    }

    /**
     * return the claim points obtained by the player
     * @return claimPoints, obtained by the player
     */
    public int claimPoints(){
        int totalPoints = 0;
        for(Route allRoutes : routes){
            totalPoints += allRoutes.claimPoints();
        }
        return totalPoints;
    }

    public boolean hasRoute(){
        return !routes.isEmpty();
    }
}