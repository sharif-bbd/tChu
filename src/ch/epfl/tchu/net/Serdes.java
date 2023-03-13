package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */

public final class Serdes{

    /**
     * Serde is a framework for serializing and deserializing an Integer efficiently and generically.
     */
    public static final Serde<Integer> INTEGER_SERDE = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);

    /**
     *Serde is a framework for serializing and deserializing a string efficiently and generically.
     */
    public static final Serde<String> STRING_SERDE = Serde.of(
            s1 -> Base64.getEncoder().encodeToString(s1.getBytes(StandardCharsets.UTF_8)),
            s2 -> new String(Base64.getDecoder().decode(s2.getBytes(StandardCharsets.UTF_8)))
    );
    /**
     *Serde is a framework for serializing and deserializing a PlayerId efficiently and generically.
     */
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    /**
     *Serde is a framework for serializing and deserializing a TurnKind efficiently and generically.
     */
    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    /**
     *Serde is a framework for serializing and deserializing a Card efficiently and generically.
     */
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);
    /**
     *Serde is a framework for serializing and deserializing a Route efficiently and generically.
     */
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());
    /**
     *Serde is a framework for serializing and deserializing a Ticket efficiently and generically.
     */
    public final static Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());
    /**
     *Serde is a framework for serializing and deserializing a list of String efficiently and generically.
     */
    public final static Serde<List<String>> STRING_LIST_SERDE = Serde.listOf(STRING_SERDE, ",");
    /**
     *Serde is a framework for serializing and deserializing a list of Card efficiently and generically.
     */
    public final static Serde<List<Card>> CARD_LIST_SERDE = Serde.listOf(CARD_SERDE, ",");
    /**
     *Serde is a framework for serializing and deserializing a list of Route efficiently and generically.
     */
    public final static Serde<List<Route>> ROUTE_LIST_SERDE = Serde.listOf(ROUTE_SERDE, ",");
    /**
     *Serde is a framework for serializing and deserializing a SortedBag of Card efficiently and generically.
     */
    public final static Serde<SortedBag<Card>> CARD_SORTED_BAG_SERDE = Serde.bagOf(CARD_SERDE, ",");
    /**
     *Serde is a framework for serializing and deserializing a SortedBag of Ticket efficiently and generically.
     */
    public final static Serde<SortedBag<Ticket>> TICKET_SORTED_BAG_SERDE = Serde.bagOf(TICKET_SERDE, ",");
    /**
     *Serde is a framework for serializing and deserializing a list of a SortedBag Card efficiently and generically.
     */
    public final static Serde<List<SortedBag<Card>>> CARD_SORTED_BAG_LIST_SERDE = Serde.listOf(CARD_SORTED_BAG_SERDE, ";");

    /**
     *Serde is a framework for serializing and deserializing a PublicCardState efficiently and generically.
     */
    public final static Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = new Serde<PublicCardState>() {
        @Override
        public String serialize(PublicCardState publicCardState) {
            return String.join(";",  CARD_LIST_SERDE.serialize(publicCardState.faceUpCards()),
                    INTEGER_SERDE.serialize(publicCardState.deckSize()),
                    INTEGER_SERDE.serialize(publicCardState.discardsSize()));

        }

        @Override
        public PublicCardState deserialize(String s) {
            String[] separator = s.split(Pattern.quote(";"), -1);
            return new PublicCardState(CARD_LIST_SERDE.deserialize(separator[0]),
                    INTEGER_SERDE.deserialize(separator[1]),
                    INTEGER_SERDE.deserialize(separator[2]) );
        }
    };

    /**
     *Serde is a framework for serializing and deserializing a PublicPlayerState efficiently and generically.
     */
    public final static Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = new Serde<PublicPlayerState>() {
        @Override
        public String serialize(PublicPlayerState publicPlayerState) {
            return String.join(";", INTEGER_SERDE.serialize(publicPlayerState.ticketCount()),
                    INTEGER_SERDE.serialize(publicPlayerState.cardCount()),
                    ROUTE_LIST_SERDE.serialize(publicPlayerState.routes()));
        }

        @Override
        public PublicPlayerState deserialize(String s) {
            String[] separator = s.split(Pattern.quote(";"), -1);
            return new PublicPlayerState(INTEGER_SERDE.deserialize(separator[0]),
                    INTEGER_SERDE.deserialize(separator[1]),
                    (!separator[2].isEmpty()) ? ROUTE_LIST_SERDE.deserialize(separator[2]) : List.of());
        }
    };

    /**
     *Serde is a framework for serializing and deserializing a PlayerSate efficiently and generically.
     */
    public static final Serde<PlayerState> PLAYER_STATE_SERDE = new Serde<PlayerState>() {
        @Override
        public String serialize(PlayerState playerState) {
            return String.join(";", TICKET_SORTED_BAG_SERDE.serialize(playerState.tickets()),
                    CARD_SORTED_BAG_SERDE.serialize(playerState.cards()),
                    ROUTE_LIST_SERDE.serialize(playerState.routes()));
        }

        @Override
        public PlayerState deserialize(String s) {
            String[] separator = s.split(Pattern.quote(";"), -1);
            return new PlayerState(TICKET_SORTED_BAG_SERDE.deserialize(separator[0]),
                    CARD_SORTED_BAG_SERDE.deserialize(separator[1]),
                    ROUTE_LIST_SERDE.deserialize(separator[2]));
        }
    };

    /**
     *Serde is a framework for serializing and deserializing a PublicGameState efficiently and generically.
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = new Serde<PublicGameState>() {
        @Override
        public String serialize(PublicGameState publicGameState) {
            return String.join(":", INTEGER_SERDE.serialize(publicGameState.ticketsCount()),
                    PUBLIC_CARD_STATE_SERDE.serialize(publicGameState.cardState()),
                    PLAYER_ID_SERDE.serialize(publicGameState.currentPlayerId()),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_1)),
                    PUBLIC_PLAYER_STATE_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_2)),
                    (publicGameState.lastPlayer() != null) ? PLAYER_ID_SERDE.serialize(publicGameState.lastPlayer()) : "");
        }

        @Override
        public PublicGameState deserialize(String s) {
            String[] separator = s.split(Pattern.quote(":"), -1);
            Map<PlayerId, PublicPlayerState> playerState = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
            playerState.put(PlayerId.PLAYER_1, PUBLIC_PLAYER_STATE_SERDE.deserialize(separator[3]));
            playerState.put(PlayerId.PLAYER_2, PUBLIC_PLAYER_STATE_SERDE.deserialize(separator[4]));
            return new PublicGameState(INTEGER_SERDE.deserialize(separator[0]),
                    PUBLIC_CARD_STATE_SERDE.deserialize(separator[1]),
                    PLAYER_ID_SERDE.deserialize(separator[2]),
                    playerState,
                    (!separator[5].isEmpty()) ? PLAYER_ID_SERDE.deserialize(separator[5]) : null);
        }
    };

    public static final Serde<Boolean> BOOLEAN_SERDE = Serde.of(
            aBoolean -> aBoolean ? "1" : "0",
            s -> s.equals("1")
    );

}q