package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class PlayerState extends PublicPlayerState{

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;
    public final StationConnectivity connectivity;


    /**
     * construct the player state who has a certain type and number of tickets, cards and routes
     * @param tickets, that belong to the player
     * @param cards, that belong to the player
     * @param routes, that belong to the player
     */
    public PlayerState (SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes){
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
        this.routes = routes;
        this.connectivity = connectivity();



    }

    /**
     * return the initial state of the player which the initial cards were distributed.
     * the player doesn't own any tickets and didn't take any routes
     * @param initialCards, of the player
     * @return initial,
     * @throws IllegalArgumentException, if the amount of initial cards is different to 4
     */
    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument(initialCards.size() == 4);
        SortedBag<Ticket> initTicket = SortedBag.of();
        List<Route> initRoute = new ArrayList<>();

        return new PlayerState(initTicket, initialCards, initRoute);
    }

    /**
     *return the tickets of the player
     * @return tickets, that belong to the player
     */
    public SortedBag<Ticket> tickets(){
        return tickets;
    }

    /**
     * return the player state, with added tickets
     * @param newTickets, the new tickets that are added to the player's state
     * @return withAddedTickets, the player state with added tickets
     */
    //TODO: check the method
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets){
        SortedBag<Ticket> addedTickets = this.tickets.union(newTickets);
        return new PlayerState(addedTickets, this.cards, this.routes);
    }

    /**
     * return the cards that belong to the player
     * @return cards, of the player
     */
    public SortedBag<Card> cards(){
        return cards;
    }

    /**
     * return the player's state with some new added card
     * @param card, the added card for the player
     * @return withAddedCard, the additional card in the player's state
     */
    public PlayerState withAddedCard(Card card){
        SortedBag<Card> cardToAdd = SortedBag.of(card);
        SortedBag<Card> addedCard = this.cards.union(cardToAdd);
        return new PlayerState(this.tickets, addedCard, this.routes);
    }

    /**
     * return the player's state with some new added card
     * @param additionalCards, the added cards for the player
     * @return withAddedCards, the additional cards in the player's state
     */
    /*public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        SortedBag<Card> addedCards = this.cards.union(additionalCards);
        return new PlayerState(this.tickets, addedCards, this.routes);
    }

     */

    /**
     *
     *return true if the players has enough cars and if he owns the correct cards
     * @param route, the route that the player claims
     * @return canClaimRoute, the player get the route he claims
     */
    public boolean canClaimRoute(Route route){

        return (carCount() >= route.length() && (possibleClaimCards(route).size() >= 1)) ? true : false;


    }

    /**
     * return the list of all the sets of cards that the player can use to take a route he claims
     * @param route, claimed by the player
     * @return possibleClaimCards, the list of every sets of cards that the player can use to take a route
     * @throws IllegalArgumentException, if the player doesn't have enough cars to take a route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        Preconditions.checkArgument(route.length() <= carCount());
        List<SortedBag<Card>> allOptions = route.possibleClaimCards();
        List<SortedBag<Card>> playerOptions = new ArrayList<>();

        for (SortedBag<Card> s : allOptions){
            if (cards.contains(s)){
                playerOptions.add(s);
            }
        }

        return playerOptions;
    }

    /**
     * @param additionalCardsCount, that the player is forced to add to get the route he claims
     * @param initialCards, that belong to the player
     * @return possibleAdditionalCards, the list of all the set of cards that the player can use to get an underground route
     * knowing that he already puts his initial cards
     * @throws IllegalArgumentException, if the additional cards is not between 1 and 3(included), if the set of cards is empty
     * or contains more than 2 different types of cards or if the set of cards drawn does not contains exactly 3 cards.
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards){

        Preconditions.checkArgument((additionalCardsCount>= 1)&&(additionalCardsCount<=3)&&(!initialCards.isEmpty()));
        if(initialCards.size() > 2){

            Set<Card> initial = initialCards.toSet();
            Preconditions.checkArgument(initial.size() <= 2);
        }

        SortedBag<Card> remainingCards = cards.difference(initialCards);
        SortedBag.Builder<Card> usefulCards = new SortedBag.Builder<>();

        for (Card c: remainingCards){
            if (initialCards.contains(c) || c == Card.LOCOMOTIVE ){
                usefulCards.add(c);
            }
        }

        SortedBag<Card> possibleCards = usefulCards.build();
        if(possibleCards.size() >= additionalCardsCount){

            Set<SortedBag<Card>> maxSubsets = possibleCards.subsetsOfSize(additionalCardsCount);
            List<SortedBag<Card>> possibleAdditionalCards = new LinkedList<>(maxSubsets);
            possibleAdditionalCards.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

            return possibleAdditionalCards;

        }else{
            return List.of();
        }
    }

    /**
     * return an identical state to the receiver, only that the player get the route he claimed due to his cards
     * @param route, possessed by the player
     * @param claimCards, that the player used to obtained his route
     * @return withClaimedRoute, the player state with the claimed route that the player succeeded to possesses
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> addRoute = new ArrayList<>(routes);
        addRoute.add(route);
        SortedBag<Card> newCards = cards.difference(claimCards);

        return new PlayerState(this.tickets, newCards, addRoute);
    }

    /**
     *returns the number of points — possibly negative — obtained by the player due to his tickets
     * @return ticketPoints, number of points obtained by the ticket's player
     */
    public int ticketPoints(){

        int max = max();

        StationPartition.Builder builder = new StationPartition.Builder(max + 1);
        for(Route r: routes){
            builder.connect(r.station1(), r.station2());
        }

        StationPartition stationPartition = builder.build();
        int allPoints = 0;
        for(Ticket t : tickets){
            allPoints += t.points(stationPartition);
        }

        return allPoints;
    }

    /**
     *returns the total points obtained by the player at the end of the game
     * @return finalPoints, obtained by the player
     */
    public int finalPoints(){
        PublicPlayerState points = new PublicPlayerState(tickets.size(), cards.size(), this.routes);
        return ticketPoints() + points.claimPoints();

    }

    private int max(){
        int max = 0;

        for(int i = 0; i < routes.size(); ++i){
            if( routes.get(i).station1().id() > max ){
                max = routes.get(i).station1().id();
            }
            if(routes.get(i).station2().id() > max){
                max = routes.get(i).station2().id();
            }
        }
        return max;
    }

    public StationConnectivity connectivity(){
        int max = max();

        StationPartition.Builder builder = new StationPartition.Builder(max + 1);
        for(Route r: routes){
            builder.connect(r.station1(), r.station2());
        }

        StationPartition stationPartition = builder.build();

        return stationPartition;
    }



    public PlayerState switchRoutes(Route wantedRoute, Route givenRoute){
        List<Route> newRoute = new ArrayList<>(routes);
        newRoute.remove(givenRoute);
        newRoute.add(wantedRoute);
        PlayerState playerState = new PlayerState(this.tickets, this.cards, newRoute);
        return playerState;
    }


}