package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static java.nio.charset.StandardCharsets.US_ASCII;
/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class RemotePlayerProxy implements Player {

    private final Socket socket;
    private BufferedWriter w;
    private BufferedReader r;
    /**
     * Construct a proxy of a distant player
     * @param socket , the proxy uses the socket to communicate with the client through the server by
     *               exchanging textual messages
     * @throws IOException, signals that an I/O exception of some sort has occurred
     */
    public RemotePlayerProxy(Socket socket) throws IOException {
        this.socket = socket;
        try {
            r = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), US_ASCII ));
            w = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), US_ASCII));
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    /**
     *initialise the players by giving them their own id and the different names of each player
     * @param ownId,  the Id player
     * @param playerNames, the players names
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        List<String> stringList = List.copyOf(playerNames.values());
        send(MessageId.INIT_PLAYERS,Serdes.PLAYER_ID_SERDE.serialize(ownId),
               Serdes.STRING_LIST_SERDE.serialize(stringList));
    }

    /**
     *when an information has to be communicate to the player during the game
     * @param info, the info for the player
     */
    @Override
    public void receiveInfo(String info) {
        send(MessageId.RECEIVE_INFO, Serdes.STRING_SERDE.serialize(info));
    }

    /**
     * inform the player of the new public state and his own state
     * @param newState, new public state
     * @param ownState, the state of the player
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        send(MessageId.UPDATE_STATE,
                Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState),
                Serdes.PLAYER_STATE_SERDE.serialize(ownState));
    }

    /**
     * tells the player the five tickets he received
     * @param tickets, the player's tickets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        send(MessageId.SET_INITIAL_TICKETS, Serdes.TICKET_SORTED_BAG_SERDE.serialize(tickets));
    }

    /**
     *the player chooses three initial tickets
     * @return chooseInitialTickets, the player tells which 3 out of the 5 tickets he's keeping
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        send(MessageId.CHOOSE_INITIAL_TICKETS);
        SortedBag<Ticket> initialTickets = Serdes.TICKET_SORTED_BAG_SERDE.deserialize(receive());
        return initialTickets;
    }

    /**
     * the player chooses which action among : DRAW_TICKETS, DRAW_CARDS or CLAIM_ROUTE; he wants to play.
     * @return nextTurn, the player chooses which action he wants to play
     */
    @Override
    public TurnKind nextTurn() {
        send(MessageId.NEXT_TURN);
        TurnKind nextTurn = Serdes.TURN_KIND_SERDE.deserialize(receive());
        return nextTurn;
    }

    /**
     * @param options, the list of tickets
     * @return chooseTickets, the player chooses the additional tickets he draws during the game
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        send(MessageId.CHOOSE_TICKETS, Serdes.TICKET_SORTED_BAG_SERDE.serialize(options));
        SortedBag<Ticket> chooseTickets = Serdes.TICKET_SORTED_BAG_SERDE.deserialize(receive());
        return chooseTickets;
    }

    /**
     *
     * @return drawSlot, the player decides to draw some cards, either from the deck or from the 5 visible face up cards.
     */
    @Override
    public int drawSlot() {
        send(MessageId.DRAW_SLOT);
        int drawSlot = Serdes.INTEGER_SERDE.deserialize(receive());
        return drawSlot;
    }

    /**
     * @return claimedRoute, the route claimed by a player
     */
    //TODO: Enlever les "" pour la string
    @Override
    public Route claimedRoute() {
        send(MessageId.ROUTE);
        Route route = Serdes.ROUTE_SERDE.deserialize(receive());
        return route;
    }

    /**
     * @return initialClaimCards, the player's initial cards in order to get the route he claimed
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        send(MessageId.CARDS);
        SortedBag<Card> cards = Serdes.CARD_SORTED_BAG_SERDE.deserialize(receive());
        return cards;
    }

    /**
     *
     * @param options, the options the player can use to get his route
     * @return chooseAdditionalCards, the cards the player can use to obtain his route
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        send(MessageId.CHOOSE_ADDITIONAL_CARDS, Serdes.CARD_SORTED_BAG_LIST_SERDE.serialize(options));
        SortedBag<Card> additionalCards = Serdes.CARD_SORTED_BAG_SERDE.deserialize(receive());
        return additionalCards;
    }


    //additional method

    //TODO: check the loop
    private void send(MessageId id, String... strings){
        try {

            StringJoiner joiner = new StringJoiner(" ");
            joiner.add(id.name());

            for (int i= 0; i< strings.length ; i++){
                joiner.add(strings[i]);
            }

            String result = joiner.toString();

            w.write(result);
            w.write('\n');
            w.flush();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    private String receive(){
        try {

            String s = r.readLine();
            return s;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Route claimWantedRoute() {
        send(MessageId.CLAIM_WANTED_ROUTE);
        Route route = Serdes.ROUTE_SERDE.deserialize(receive());
        return route;
    }

    @Override
    public Route givenRoute() {
        send(MessageId.GIVEN_ROUTE);
        Route route = Serdes.ROUTE_SERDE.deserialize(receive());
        return route;
    }


    @Override
    public Boolean acceptSwitchRoutes(Route wantedRoute, Route givenRoute) {
        send(MessageId.ACCEPT_SWITCH_ROUTES, Serdes.ROUTE_SERDE.serialize(wantedRoute), Serdes.ROUTE_SERDE.serialize(givenRoute));
        boolean accept = Serdes.BOOLEAN_SERDE.deserialize(receive());
        return accept;
    }

    @Override
    public String choosePlayerName() {
        send(MessageId.CHOOSE_NAME);
        String name = Serdes.STRING_SERDE.deserialize(receive());
        return name;
    }

    @Override
    public void setPlayerNames() {
        send(MessageId.SET_PLAYER_NAME);
    }


}