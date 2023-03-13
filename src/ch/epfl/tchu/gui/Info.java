package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class Info {

    private final String playerName;


    /**
     * generate a message about the player
     * @param playerName, the player who is currently playing
     */

    public Info(String playerName){
        this.playerName = playerName;
    }

    /**
     * generate a text which is not specific to a given player
     * return the (french) name of the given card
     * @param card, to make the text
     * @param count, absolute value
     * @return cardName, the name of the card given
     * @throws IllegalArgumentException if the card does not correspond to the
     */
    public static String cardName(Card card, int count) {

        switch (card) {

            case BLACK:
                return StringsFr.BLACK_CARD + StringsFr.plural(count);
            case VIOLET:
                return StringsFr.VIOLET_CARD + StringsFr.plural(count);
            case BLUE:
                return StringsFr.BLUE_CARD + StringsFr.plural(count);
            case GREEN:
                return StringsFr.GREEN_CARD + StringsFr.plural(count);
            case YELLOW:
                return StringsFr.YELLOW_CARD + StringsFr.plural(count);
            case ORANGE:
                return StringsFr.ORANGE_CARD + StringsFr.plural(count);
            case RED:
                return StringsFr.RED_CARD + StringsFr.plural(count);
            case WHITE:
                return StringsFr.WHITE_CARD + StringsFr.plural(count);
            case LOCOMOTIVE:
                return StringsFr.LOCOMOTIVE_CARD + StringsFr.plural(count);

            default:
                throw new IllegalArgumentException();
        }

    }

    /**
     * the text declares that the players finish the game equally-ranked. They both winning points
     * @param playerNames, the name of the two players
     * @param points, the number of points each players win at the end of the game
     * @return draw, return the text that the players are equally ranked with the given points
     */
    public static String draw(List<String> playerNames, int points){
        String names = new String();
        names = String.join(StringsFr.AND_SEPARATOR, playerNames);

        return String.format(StringsFr.DRAW, names, points);
    }

    /**
     * return the text of the player who will play first
     * @return willPlayFirst, the player who plays first
     */
    public String willPlayFirst(){
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * the player kept a certain number of ticket
     * @param count, number of ticket
     * @return keptTickets, the player kept n ticket
     */
    public String keptTickets(int count){
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * the text that indicates that a player can play
     * @return canPlay, a player can play
     */
    public String canPlay(){
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * return a text of the number of Tickets the player draw
     * @param count, number of ticket
     * @return drewTickets, the tickets which the player draw
     */
    public String drewTickets(int count){
        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     *the player drew a blind card on the top deck card
     * @return drewBlindCard, the player drew a blind card
     */
    public String drewBlindCard(){
        return  String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * the player drew the visible card
     * @param card, one out of 5 visible card
     * @return drewVisibleCard, the visible card that the player drew
     */

    public String drewVisibleCard(Card card){
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }


    /**
     * return the claimed route wanted by the player
     * @param route, the route claimed by the player
     * @param cards, the cards to get the route
     * @return claimedRoute, the player claimed the route with his cards
     */
    public String claimedRoute(Route route, SortedBag<Card> cards){
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, getNameRoute(route), getAllCards(cards));
    }

    /**
     * the player attempts to claimed a tunnel with his initial cards
     * @param route, a claimed tunnel
     * @param initialCards, the cards used by the player in attempt to get the tunnel
     * @return attemptsTunnelClaim, the text of the player who attempts to claimed a tunnel with his initial cards
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, getNameRoute(route), getAllCards(initialCards));
    }

    /**
     * a text that the player draw 3 additional cards which can imply an additional cost of
     * number of cards
     * @param drawnCards, the 3 additional cards to see if they are additional cost
     * @param additionalCost, the number of additional cost of cards
     * @return drewAdditionalCards, the 3 additional cards imply an additional cost of cards for the player
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){
        String t1 = new String();
        String t2 = new String();

        t1 = String.format(StringsFr.ADDITIONAL_CARDS_ARE, getAllCards(drawnCards));
        t2 = String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));

        if(additionalCost == 0){
            return t1 + StringsFr.NO_ADDITIONAL_COST;

        } else {
            return t1 + t2;
        }
    }

    /**
     * @param route, the route that the player did not/can't claim
     * @return didNotClaimRoute, a text that declare that the player didn't want/can't claim the route
     */
    public String didNotClaimRoute(Route route){
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, getNameRoute(route));

    }

    /**
     * the text declares that the player only have 2 or less cars and that the last turns begins
     * @param carCount, the number of car (2 or less)
     * @return lastTurnBegins, a message that says that the last turn begins
     */
    public String lastTurnBegins(int carCount){
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     *
     * the player gets the longest trail bonus at the end of the game because he gets the longest trail
     * or because he got one of the longest trail
     * @param longestTrail, the longest trail of the player
     * @return getsLongestTrailBonus, the message says that the player gets the longest trail bonus
     */

    public String getsLongestTrailBonus(Trail longestTrail){
//        String station = longestTrail.toString();

        return String.format(StringsFr.GETS_BONUS, playerName, longestTrail.station1().toString() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2().toString());
    }

    /**
     * return the winner of the game with the points he got and also the number of point of his adversary
     * @param points, the number of points of the player
     * @param loserPoints, the number of point of the loser (player)
     * @return won, the message that declares the player won with the points given
     */
    public String won(int points, int loserPoints){
        return String.format(StringsFr.WINS, playerName,points, StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }


    private static String getNameRoute(Route route){
        return route.station1().toString() + StringsFr.EN_DASH_SEPARATOR + route.station2().toString();
    }


    private static String getAllCards(SortedBag<Card> cards){

        List<String> all = new ArrayList<>();

        for (Card c : cards.toSet()){
            int multiplicity = cards.countOf(c);
            all.add(multiplicity +" "+ cardName(c, multiplicity));
        }

        if (all.size() == 1){
            return all.get(0);
        }else if(all.size() == 2){
            return all.get(0).toString() + StringsFr.AND_SEPARATOR + all.get(1).toString();
        }else{

            List<String> minusOne = all.subList(0, all.size()-1);
            String one = all.get(all.size()-1);

            String text1 = String.join(", ",minusOne);
            String text2 = StringsFr.AND_SEPARATOR+ one;

            return text1 + text2;
        }


    }


}