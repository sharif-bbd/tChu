package ch.epfl.tchu.net;


import java.util.List;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public enum MessageId {

    /**
     * the server sends this type of messages to the clients
     */
    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS,
    CLAIM_WANTED_ROUTE,
    GIVEN_ROUTE,
    ACCEPT_SWITCH_ROUTES,
    CHOOSE_NAME,
    SET_PLAYER_NAME;

    public static List<MessageId> ALL = List.of(MessageId.values());

}