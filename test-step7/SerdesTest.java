package ch.epfl.tchu.net;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.Route.Level;
class SerdesTest {
    
    @Test
    void integerWorks() {
        assertEquals("4",Serdes.INTEGER_SERDE.serialize(4));
        assertEquals(4,Serdes.INTEGER_SERDE.deserialize("4"));
    }
    
    @Test 
    void stringWorks() {
        assertEquals("Q2hhcmxlcw==",Serdes.STRING_SERDE.serialize("Charles"));
        assertEquals("Charles",Serdes.STRING_SERDE.deserialize("Q2hhcmxlcw=="));
    }
    
    @Test
    void playerIdWorks() {
        assertEquals("0",Serdes.PLAYER_ID_SERDE.serialize(PlayerId.PLAYER_1));
        assertEquals(PlayerId.PLAYER_1,Serdes.PLAYER_ID_SERDE.deserialize("0"));
    }
    
    @Test
    void turnKindWorks() {
        assertEquals("2",Serdes.TURN_KIND_SERDE.serialize(TurnKind.CLAIM_ROUTE));
        assertEquals(TurnKind.CLAIM_ROUTE,Serdes.TURN_KIND_SERDE.deserialize("2"));
    }
    
    @Test
    void cardWorks() {
        assertEquals("3",Serdes.CARD_SERDE.serialize(Card.GREEN));
        assertEquals(Card.GREEN,Serdes.CARD_SERDE.deserialize("3"));
    }
    
    @Test
    void routeWorks() {
        assertEquals("2",Serdes.ROUTE_SERDE.serialize(ChMap.routes().get(2)));
        assertEquals(ChMap.routes().get(2),Serdes.ROUTE_SERDE.deserialize("2"));
    }
    
    @Test 
    void ticketWorks() {
        assertEquals("2",Serdes.TICKET_SERDE.serialize(ChMap.tickets().get(2)));
        assertEquals(ChMap.tickets().get(2),Serdes.TICKET_SERDE.deserialize("2"));
        
    }
    
    @Test
    void listStringWorks() {
        assertEquals("Q2hhcmxlcw==,QW1pbmU=",Serdes.STRING_LIST_SERDE.serialize(List.of("Charles","Amine")));
        assertEquals(List.of("Charles","Amine"),Serdes.STRING_LIST_SERDE.deserialize("Q2hhcmxlcw==,QW1pbmU="));
    }
    
    @Test
    void listCardWorks() {
        assertEquals("0,1",Serdes.CARD_LIST_SERDE.serialize(List.of(Card.BLACK,Card.VIOLET)));
        assertEquals(List.of(Card.BLACK,Card.VIOLET),Serdes.CARD_LIST_SERDE.deserialize("0,1"));
    } 

    @Test 
   void publicGameStateWork(){ 
        List<Card> fu = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
          PlayerId.PLAYER_1, new PublicPlayerState(10, 11, rs1),
         PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs =
          new PublicGameState(40, cs, PlayerId.PLAYER_2, ps, null); 
        assertEquals("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:",Serdes.PUBLIC_GAME_STATE_SERDE.serialize(gs));
        assertEquals(gs.ticketsCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").ticketsCount());
        assertEquals(gs.cardState().discardsSize(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").cardState().discardsSize());
        assertEquals(gs.lastPlayer(),Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").lastPlayer());
    }
    
    @Test
    
    void listOfBagCardWorks() {
        SortedBag.Builder<Card> bagOfCards = new SortedBag.Builder<Card>();
        bagOfCards.add(2, Card.BLACK);
        bagOfCards.add(3, Card.ORANGE);
        bagOfCards.add(1, Card.LOCOMOTIVE);
        SortedBag<Card> card1 = bagOfCards.build();

        SortedBag.Builder<Card> bagOfCards2 = new SortedBag.Builder<Card>();
        bagOfCards2.add(2, Card.WHITE);
        bagOfCards2.add(1, Card.VIOLET);
        SortedBag<Card> card2 = bagOfCards2.build();

        SortedBag.Builder<Card> bagOfCards3 = new SortedBag.Builder<Card>();
        bagOfCards3.add(1, Card.RED);
        bagOfCards3.add(3, Card.BLACK);
        SortedBag<Card> card3 = bagOfCards3.build();

        assertEquals("0,0,5,5,5,8;1,7,7;0,0,0,6",
                Serdes.CARD_SORTED_BAG_LIST_SERDE.serialize(List.of(card1, card2, card3)));
        assertEquals(List.of(card1, card2, card3),
                Serdes.CARD_SORTED_BAG_LIST_SERDE.deserialize("0,0,5,5,5,8;1,7,7;0,0,0,6"));

    }
    
    @Test
    void listRouteWorks() {
        Route r1 = ChMap.routes().get(0);
        Route r2 = ChMap.routes().get(1);
        assertEquals("0,1", Serdes.ROUTE_LIST_SERDE.serialize(List.of(r1, r2)));
        assertEquals(List.of(r1, r2), Serdes.ROUTE_LIST_SERDE.deserialize("0,1"));
        Route rx = ChMap.routes().get(8);
        Route ry = ChMap.routes().get(9);
        assertEquals("8,9", Serdes.ROUTE_LIST_SERDE.serialize(List.of(rx, ry)));
        assertEquals(List.of(rx, ry), Serdes.ROUTE_LIST_SERDE.deserialize("8,9"));

        Route rz = ChMap.routes().get(11);
        Route ra = ChMap.routes().get(19);
        assertEquals("11,19", Serdes.ROUTE_LIST_SERDE.serialize(List.of(rz, ra)));
        assertEquals(List.of(rz, ra), Serdes.ROUTE_LIST_SERDE.deserialize("11,19"));

    }
    
    @Test
    void bagCardWorks() {
        SortedBag.Builder<Card> bagOfCards = new SortedBag.Builder<Card>();
        bagOfCards.add(2, Card.BLACK);
        bagOfCards.add(3, Card.ORANGE);
        bagOfCards.add(1, Card.LOCOMOTIVE);

        SortedBag<Card> cardB = bagOfCards.build();

        assertEquals("0,0,5,5,5,8", Serdes.CARD_SORTED_BAG_SERDE.serialize(cardB));
        assertEquals(cardB, Serdes.CARD_SORTED_BAG_SERDE.deserialize("0,0,5,5,5,8"));
    }

    @Test
    void bagTicketWorks() {
        SortedBag.Builder<Ticket> bagOfTickets = new SortedBag.Builder<Ticket>();
        bagOfTickets.add(1, ChMap.tickets().get(8));
        bagOfTickets.add(3, ChMap.tickets().get(4));
        bagOfTickets.add(2, ChMap.tickets().get(11));

        SortedBag<Ticket> tickeB = bagOfTickets.build();
        assertEquals("4,4,4,8,11,11", Serdes.TICKET_SORTED_BAG_SERDE.serialize(tickeB));
        assertEquals(tickeB, Serdes.TICKET_SORTED_BAG_SERDE.deserialize("8,4,4,4,11,11"));

    }
    
    @Test
    void publicCardStateWorks() {
        
        List<Card> cardss = List.of(Card.BLACK, Card.BLACK, Card.BLUE, Card.WHITE, Card.GREEN);
        PublicCardState w = new PublicCardState( cardss, 6, 8);
        
        assertEquals("0,0,2,7,3;6;8", Serdes.PUBLIC_CARD_STATE_SERDE.serialize(w));
        assertEquals(w.faceUpCards(), Serdes.PUBLIC_CARD_STATE_SERDE.deserialize("0,0,2,7,3;6;8").faceUpCards());
        assertEquals( w.deckSize(), Serdes.PUBLIC_CARD_STATE_SERDE.deserialize("0,0,2,7,3;6;8").deckSize());
        assertEquals( w.discardsSize(), Serdes.PUBLIC_CARD_STATE_SERDE.deserialize("0,0,2,7,3;6;8").discardsSize());
    }
    

}
