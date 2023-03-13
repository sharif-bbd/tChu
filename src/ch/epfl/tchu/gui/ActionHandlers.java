package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

import java.util.List;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */

public interface ActionHandlers {

    /**
     * abstract method onDrawTickets
     * which is called when the player wants to draw tickets
     */
    @FunctionalInterface
    interface DrawTicketsHandler{
        void onDrawTickets();
    }

    /**
     *abstract method onDrawCard
     * which is called when the player wants to draw a card in a given position
     */
    @FunctionalInterface
    interface DrawCardHandler{
        void onDrawCard(int slot);
    }

    /**
     * abstract method onClaimRoute
     * which is called when the player claims a route given his (initial) cards
     */
    @FunctionalInterface
    interface ClaimRouteHandler{
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    /**
     * abstract method onChooseTickets
     * which is called when the player chooses the draw tickets
     */
    @FunctionalInterface
    interface ChooseTicketsHandler{
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    /**
     * abstract method onChooseCards
     * which is called when the player choose the cards to get a route
     */
    @FunctionalInterface
    interface ChooseCardsHandler{
        void onChooseCards(SortedBag<Card> cards);
    }

    @FunctionalInterface
    interface SwitchRoutesHandler{
        void onSwitchRoutes(Route wantedRoute);
    }

    @FunctionalInterface
    interface ChooseGivenRouteHandler{
        void onChooseRoute(Route givenRoute);
    }

    @FunctionalInterface
    interface AcceptHandler{
        void onAnswer(boolean accept);
    }

    @FunctionalInterface
    interface ChooseNameHandler{
        void onChooseName(String string);
    }
}