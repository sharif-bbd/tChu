package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Random;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public class ServerMain extends Application {

    private String firstNameDefault = "Ada";
    private String secondNameDefault = "Charles";

    /**
     * launch the game
     * @param args, arguments accepted by the server
     */
    public static void main(String[] args){ launch(args); }

    /**
     * starts the server
     * @param primaryStage, don't use this parameter in this class
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        String firstPlayerName, secondPlayerName = new String();

        firstPlayerName = (getParameters().getRaw().size() >= 1) ? getParameters().getRaw().get(0) : firstNameDefault;
        secondPlayerName = (getParameters().getRaw().size() == 2) ? getParameters().getRaw().get(1) : secondNameDefault;

        try (ServerSocket serverSocket = new ServerSocket(5108)) {
            Socket socket = serverSocket.accept();
            GraphicalPlayerAdapter player1 = new GraphicalPlayerAdapter();
            RemotePlayerProxy player2 = new RemotePlayerProxy(socket);
            Player playerOneInterface = player1;
            Player playerTwoInterface = player2;
            new Thread(() -> Game.play(Map.of(PlayerId.PLAYER_1, playerOneInterface, PlayerId.PLAYER_2, playerTwoInterface), SortedBag.of(ChMap.tickets()), new Random())).start();
        }catch(Exception e){
            throw new Exception();
        }
    }
}