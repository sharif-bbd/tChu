package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Station;
import ch.epfl.tchu.game.StationConnectivity;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.property.ObjectProperty;
import javafx.util.StringConverter;

public class TicketStringConverter extends StringConverter<Ticket> {
    private final StationConnectivity connectivity;

    public TicketStringConverter(StationConnectivity connectivity){
        this.connectivity = connectivity;
    }

    @Override
    public String toString(Ticket ticket) {

        return ticket.toString(connectivity);
    }

    @Override
    public Ticket fromString(String s) {
        throw new UnsupportedOperationException();
    }
}
