package ch.epfl.tchu.net;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;

class SerdeTest {

    @Test
    void OfWorks() {
        Serde<Card> s = Serde.oneOf(Card.ALL);
        assertEquals("0",s.serialize(Card.BLACK));
        assertEquals(Card.BLACK,s.deserialize(s.serialize(Card.BLACK)));        
    }
    @Test
    void ListOfWorks() {
        Serde<Card> s = Serde.oneOf(Card.ALL);
        assertEquals("0,2,3",Serde.listOf(s, ",").serialize(List.of(Card.BLACK,Card.BLUE,Card.GREEN)));
        assertEquals(List.of(Card.BLACK,Card.BLUE,Card.GREEN),Serde.listOf(s, ",").deserialize("0,2,3"));
    }
    
    @Test
    void BagOfWorks() {
        Serde<Card> s = Serde.oneOf(Card.ALL);
        assertEquals("0,0,2,2",Serde.bagOf(s, ",").serialize(SortedBag.of(2,Card.BLACK,2,Card.BLUE)));
        assertEquals(SortedBag.of(2,Card.BLACK,2,Card.BLUE),Serde.bagOf(s, ",").deserialize("0,0,2,2"));
    }

}
