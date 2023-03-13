package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static ch.epfl.tchu.net.MessageId.RECEIVE_INFO;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class RemotePlayerClient {

    private final Player player;
    private final String host;
    private final int port;
    /**
     * construct a client of a distant player
     * @param player, the player
     * @param host, the name of the player
     * @param port, used to connect to the proxy
     */
    public RemotePlayerClient(Player player, String host, int port){
        this.player = player;
        this.host = host;
        this.port = port;


    }

    /**
     * perform a loop during which :
     * wait for the message send from the proxy and if necessary answer to the message
     */
    //TODO: do this method
    public void run(){
        try (Socket s = new Socket(host, port);
             BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream(), US_ASCII));
             BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII))){


            String receive;
            while((receive = r.readLine())!= null){

                String[] message = receive.split(Pattern.quote(" "), -1);



                switch (MessageId.valueOf(message[0])){ // le type du message determiné
                    case INIT_PLAYERS : {
                        PlayerId id = Serdes.PLAYER_ID_SERDE.deserialize(message[1]);
                        Map<PlayerId, String> map = new EnumMap<PlayerId, String>(PlayerId.class);
                        map.put(id, Serdes.STRING_LIST_SERDE.deserialize(message[2]).get(id.ordinal()));
                        map.put(id.next(), Serdes.STRING_LIST_SERDE.deserialize(message[2]).get(id.next().ordinal()));
                        player.initPlayers(id,map);
                        break;
                    }
                    case RECEIVE_INFO : {
                        String info = Serdes.STRING_SERDE.deserialize(message[1]);
                        player.receiveInfo(info);
                        break;
                    }
                    case UPDATE_STATE : {
                        PublicGameState publicGameState = Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(message[1]);
                        PlayerState playerState = Serdes.PLAYER_STATE_SERDE.deserialize(message[2]);
                        player.updateState(publicGameState, playerState);
                        break;
                    }
                    case SET_INITIAL_TICKETS : {
                        SortedBag<Ticket> tickets = Serdes.TICKET_SORTED_BAG_SERDE.deserialize(message[1]);
                        player.setInitialTicketChoice(tickets);
                        break;
                    }
                    case CHOOSE_INITIAL_TICKETS : {
                        SortedBag<Ticket> tickets = player.chooseInitialTickets();
                        w.write(Serdes.TICKET_SORTED_BAG_SERDE.serialize(tickets));
                        w.write('\n');
                        w.flush();
                        break;
                    }
                    case NEXT_TURN : {
                        Player.TurnKind turnKind = player.nextTurn();
                        w.write(Serdes.TURN_KIND_SERDE.serialize(turnKind));
                        w.write('\n');
                        w.flush();
                        break;
                    }
                    case CHOOSE_TICKETS : {
                        SortedBag<Ticket> tickets = Serdes.TICKET_SORTED_BAG_SERDE.deserialize(message[1]);
                        SortedBag<Ticket> ticket = player.chooseTickets(tickets);
                        w.write(Serdes.TICKET_SORTED_BAG_SERDE.serialize(ticket));
                        w.write('\n');
                        w.flush();
                        break;
                    }
                    case DRAW_SLOT : {
                        int slot = player.drawSlot();
                        w.write(Serdes.INTEGER_SERDE.serialize(slot));
                        w.write('\n');
                        w.flush();
                        break;
                    }
                    case ROUTE : {
                        Route route = player.claimedRoute();
                        w.write(Serdes.ROUTE_SERDE.serialize(route));
                        w.write('\n');
                        w.flush();
                        break;
                    }
                    case CARDS : {
                        SortedBag<Card> cards = player.initialClaimCards();
                        w.write(Serdes.CARD_SORTED_BAG_SERDE.serialize(cards));
                        w.write('\n');
                        w.flush();
                        break;
                    }
                    case CHOOSE_ADDITIONAL_CARDS : {
                        List<SortedBag<Card>> options = Serdes.CARD_SORTED_BAG_LIST_SERDE.deserialize(message[1]);
                        SortedBag<Card> cards =  player.chooseAdditionalCards(options);
                        w.write(Serdes.CARD_SORTED_BAG_SERDE.serialize(cards));
                        w.write('\n');
                        w.flush();
                        break;
                    }
                    case CLAIM_WANTED_ROUTE : {
                        Route route = player.claimWantedRoute();
                        w.write(Serdes.ROUTE_SERDE.serialize(route));
                        w.write('\n');
                        w.flush();
                        break;
                    }
                    case GIVEN_ROUTE : {
                        Route route = player.givenRoute();
                        w.write(Serdes.ROUTE_SERDE.serialize(route));
                        w.write('\n');
                        w.flush();
                        break;
                    }
                    case ACCEPT_SWITCH_ROUTES : {
                        Boolean accept = player.acceptSwitchRoutes(Serdes.ROUTE_SERDE.deserialize(message[1]), Serdes.ROUTE_SERDE.deserialize(message[2]));
                        w.write(Serdes.BOOLEAN_SERDE.serialize(accept));
                        w.write('\n');
                        w.flush();
                        break;
                    }
                    case SET_PLAYER_NAME : {
                        player.setPlayerNames();
                        break;
                    }
                    case CHOOSE_NAME : {
                        String name = player.choosePlayerName();
                        w.write(Serdes.STRING_SERDE.serialize(name));
                        w.write('\n');
                        w.flush();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }


}