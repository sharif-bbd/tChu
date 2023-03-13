package ch.epfl.tchu.net;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicCardState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.PublicPlayerState;
import ch.epfl.tchu.game.Station;
import ch.epfl.tchu.gui.Info;
import ch.epfl.tchu.net.RemotePlayerProxy;

public final class TestServer {
    public static void main(String[] args) throws IOException {
      System.out.println("Starting server!");
      try (ServerSocket serverSocket = new ServerSocket(5108);
       Socket socket = serverSocket.accept()) {
        
        Player playerProxy = new RemotePlayerProxy(socket);
        var playerNames = Map.of(PlayerId.PLAYER_1, "Ada",
                     PlayerId.PLAYER_2, "Charles");
        playerProxy.initPlayers(PlayerId.PLAYER_1, playerNames);
        Info player1 = new Info("Amine");
        Info player2 = new Info("Lina");
        playerProxy.receiveInfo(player1.drewTickets(3));
        System.out.println("ab");
        var faceUpCards = SortedBag.of(5, Card.LOCOMOTIVE).toList();
        var cardState = new PublicCardState(faceUpCards, 0, 0);
        var initialPlayerState = (PublicPlayerState) PlayerState.initial(SortedBag.of(4, Card.RED));
        var playerState = Map.of(
                PLAYER_1, initialPlayerState,
                PLAYER_2, initialPlayerState);
        var pgs = new PublicGameState(4, cardState, PLAYER_1, playerState, PLAYER_1);
        var s1 = new Station(0, "Lausanne");
        var s2 = new Station(1, "EPFL");
        var id = "id";

        var emptyPlayerState = new PlayerState(SortedBag.of(), SortedBag.of(), List.of());
        playerProxy.setInitialTicketChoice(SortedBag.of(2,ChMap.tickets().get(0),3,ChMap.tickets().get(4)));
        
        System.out.println(playerProxy.nextTurn());
        System.out.println(playerProxy.chooseInitialTickets());
        System.out.println(playerProxy.chooseTickets(SortedBag.of(1,ChMap.tickets().get(0),3,ChMap.tickets().get(3))));
        System.out.println(playerProxy.drawSlot());
        System.out.println(playerProxy.claimedRoute().station1().name()+" -> " + playerProxy.claimedRoute().station2().name());
      }
      System.out.println("Server done!");
    }
  } 