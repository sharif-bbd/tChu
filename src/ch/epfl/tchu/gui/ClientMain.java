package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;

import javafx.application.Application;
import javafx.stage.Stage;
/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class ClientMain extends Application {

    private final int portDefault = 5108;
    private final String hostDefault = "localhost";

    /**
     * launch the game
     * @param args, arguments accepted by the client
     */
    public static void main(String[] args){ launch(args); }

    /**
     * starts the client
     * @param primaryStage, don't use this parameter in this class
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        String host = new String();
        int port = 0;

        host = (getParameters().getRaw().size() >= 1) ? getParameters().getRaw().get(0) : hostDefault;
        port = (getParameters().getRaw().size() == 2) ? Integer.parseInt(getParameters().getRaw().get(1)) : portDefault;

        GraphicalPlayerAdapter graphicalPlayerAdapter = new GraphicalPlayerAdapter();
        RemotePlayerClient client = new RemotePlayerClient(graphicalPlayerAdapter, host, port);
        new Thread(() -> client.run()).start();
    }
}