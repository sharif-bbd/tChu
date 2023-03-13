package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

public final class Game {


    /**
     * Launches a game of tChu for the given players
     * @param players  A map of every player's Id linked to the different actions available through the players' interface
     * @param tickets, the available tickets for the game
     * @param rng, a Random, to shuffle the discard
     * @throws IllegalArgumentException, if the size of the maps is different to 2
     */
    public static void play(Map<PlayerId, Player> players, SortedBag<Ticket> tickets, Random rng){

        GameState gameState = GameState.initial(tickets, rng);
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        players.forEach((playerId, player)-> player.setPlayerNames());
        players.forEach((playerId, player)-> playerNames.put(playerId, player.choosePlayerName()));

        List<String> randomNames = Arrays.asList(
                "Chuggington",
                "Thomas le petit train",
                "Mistral",
                "Orient-Express",
                "Maroc Express",
                "El Chepe",
                "Golden Montain Express",
                "Bumbay Express",
                "le Train Bleu",
                "California Zephyr");

        //Probleme quand le joueur 1 rentre une string vide
        if(playerNames.get(PlayerId.PLAYER_1).equals(playerNames.get(PlayerId.PLAYER_2))) {
            randomNames.remove(playerNames.get(PlayerId.PLAYER_1));
            playerNames.put(PlayerId.PLAYER_2, getRandomItems(randomNames, rng));
        }

        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);
        Map<PlayerId, Info> infoMap = new EnumMap<>(PlayerId.class);
        infoMap.put(PlayerId.PLAYER_1, new Info(playerNames.get(PlayerId.PLAYER_1)));
        infoMap.put(PlayerId.PLAYER_2, new Info(playerNames.get(PlayerId.PLAYER_2)));


        for(Map.Entry<PlayerId, Player> m : players.entrySet()){
            m.getValue().initPlayers(m.getKey(), playerNames);
        }
        publicInfos(infoMap.get(gameState.currentPlayerId()).willPlayFirst(), players);


        for(Map.Entry<PlayerId, Player> m : players.entrySet()){
            m.getValue().setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        for(Map.Entry<PlayerId, Player> m : players.entrySet()){
            updateState(players, gameState);
            SortedBag<Ticket> chosenTickets = m.getValue().chooseInitialTickets();
            gameState = gameState.withInitiallyChosenTickets(m.getKey(), chosenTickets);
        }

        for(Map.Entry<PlayerId, Player> m : players.entrySet()){
            publicInfos(infoMap.get(m.getKey()).keptTickets(gameState.playerState(m.getKey()).ticketCount()), players);
        }

        while(true){


            Player currentPlayerInterface = players.get(gameState.currentPlayerId());
            Player nextPlayerInterface = players.get(gameState.currentPlayerId().next());

            publicInfos(infoMap.get(gameState.currentPlayerId()).canPlay(), players);
            updateState(players, gameState);
            Player.TurnKind action = currentPlayerInterface.nextTurn();

            switch (action){

                case DRAW_TICKETS : {

                    SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    publicInfos(infoMap.get(gameState.currentPlayerId()).drewTickets(Constants.IN_GAME_TICKETS_COUNT), players);
                    SortedBag<Ticket> chosenTickets = currentPlayerInterface.chooseTickets(drawnTickets);
                    publicInfos(infoMap.get(gameState.currentPlayerId()).keptTickets(chosenTickets.size()), players);
                    gameState = gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets);

                    break;

                }

                case DRAW_CARDS : {
                    for (int i = 0; i < 2; i++) {

                        if(i == 1){
                            updateState(players, gameState);
                        }
                        int slot = currentPlayerInterface.drawSlot();

                        if(slot == Constants.DECK_SLOT){
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

                            publicInfos(infoMap.get(gameState.currentPlayerId()).drewBlindCard(), players);
                            gameState = gameState.withBlindlyDrawnCard();

                        }else{
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

                            publicInfos(infoMap.get(gameState.currentPlayerId()).drewVisibleCard(gameState.cardState().faceUpCard(slot)), players);
                            gameState = gameState.withDrawnFaceUpCard(slot);
                        }
                    }
                    break;
                }

                case CLAIM_ROUTE : {
                    Route chosenRoute = currentPlayerInterface.claimedRoute();


                    if(gameState.currentPlayerState().canClaimRoute(chosenRoute)){
                        SortedBag<Card> claimCards = currentPlayerInterface.initialClaimCards();

                        if(chosenRoute.level() == Route.Level.UNDERGROUND){
                            publicInfos(infoMap.get(gameState.currentPlayerId()).attemptsTunnelClaim(chosenRoute, claimCards), players);
                            List<Card> draw = new ArrayList<>();

                            for (int i = 0; i < 3; i++) {
                                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                                draw.add(gameState.topCard());
                                gameState = gameState.withoutTopCard();
                            }

                            SortedBag<Card> drawnCards = SortedBag.of(draw);
                            int additionalCardsCount = chosenRoute.additionalClaimCardsCount(claimCards, drawnCards);
                            publicInfos(infoMap.get(gameState.currentPlayerId()).drewAdditionalCards(drawnCards, additionalCardsCount), players);

                            if(additionalCardsCount > 0 ){
                                List<SortedBag<Card>> possibleCards = gameState.currentPlayerState().possibleAdditionalCards(additionalCardsCount, claimCards);
                                System.out.println(gameState.currentPlayerState().cards());
                                if(!possibleCards.isEmpty()){
                                    SortedBag<Card> chosenAdditionalCards = currentPlayerInterface.chooseAdditionalCards(possibleCards);

                                    if(!chosenAdditionalCards.isEmpty()){
                                        SortedBag<Card> newCards = claimCards.union(chosenAdditionalCards);
                                        gameState = gameState.withClaimedRoute(chosenRoute, newCards);
                                        publicInfos(infoMap.get(gameState.currentPlayerId()).claimedRoute(chosenRoute, newCards), players);
                                        gameState = gameState.withMoreDiscardedCards(drawnCards);

                                    }else{
                                        publicInfos(infoMap.get(gameState.currentPlayerId()).didNotClaimRoute(chosenRoute), players);
                                        gameState = gameState.withMoreDiscardedCards(drawnCards);
                                    }
                                }else{
                                    publicInfos(infoMap.get(gameState.currentPlayerId()).didNotClaimRoute(chosenRoute), players);
                                    gameState = gameState.withMoreDiscardedCards(drawnCards);
                                }

                            }else{
                                gameState = gameState.withClaimedRoute(chosenRoute, claimCards);
                                publicInfos(infoMap.get(gameState.currentPlayerId()).claimedRoute(chosenRoute, claimCards), players);
                                gameState = gameState.withMoreDiscardedCards(drawnCards);
                            }

                        }else{
                            publicInfos(infoMap.get(gameState.currentPlayerId()).claimedRoute(chosenRoute, claimCards), players);
                            gameState = gameState.withClaimedRoute(chosenRoute, claimCards);
                        }

                    }else{
                        publicInfos(infoMap.get(gameState.currentPlayerId()).didNotClaimRoute(chosenRoute), players);
                    }
                    break;
                }

                case SWITCH_ROUTE : {
                    Route wantedRoute = currentPlayerInterface.claimWantedRoute();
                    Route givenRoute = currentPlayerInterface.givenRoute();
                    boolean accepted = nextPlayerInterface.acceptSwitchRoutes(wantedRoute, givenRoute);

                    if(accepted){
                        gameState = gameState.withSwitchedRoutes(wantedRoute, givenRoute);
                    }else {

                    }
                    break;
                }
            }
            
            if(gameState.currentPlayerId().equals(gameState.lastPlayer())){
                break;
            }

            if(gameState.lastTurnBegins()){
                publicInfos(infoMap.get(gameState.currentPlayerId()).lastTurnBegins(gameState.currentPlayerState().carCount()), players);
            }

            gameState = gameState.forNextTurn();


        }

        int longestTrail1 = Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes()).length();
        int longestTrail2 = Trail.longest(gameState.playerState(PlayerId.PLAYER_2).routes()).length();

        int totalPoints1 = gameState.playerState(PlayerId.PLAYER_1).finalPoints();
        System.out.println(gameState.playerState(PlayerId.PLAYER_1).ticketPoints() + " / " + gameState.playerState(PlayerId.PLAYER_1).claimPoints());
        int totalPoints2 = gameState.playerState(PlayerId.PLAYER_2).finalPoints();
        System.out.println(gameState.playerState(PlayerId.PLAYER_2).ticketPoints() + " / " + gameState.playerState(PlayerId.PLAYER_2).claimPoints());

        if(longestTrail1 > longestTrail2){
            totalPoints1 += Constants.LONGEST_TRAIL_BONUS_POINTS;
            publicInfos(infoMap.get(gameState.currentPlayerId()).getsLongestTrailBonus(Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes())), players);
        }else if (longestTrail2 > longestTrail1){
            totalPoints2 += Constants.LONGEST_TRAIL_BONUS_POINTS;
            publicInfos(infoMap.get(gameState.currentPlayerId().next()).getsLongestTrailBonus(Trail.longest(gameState.playerState(PlayerId.PLAYER_2).routes())), players);
        }

        updateState(players, gameState);

        if(totalPoints1 > totalPoints2){
            publicInfos(infoMap.get(gameState.currentPlayerId()).won(totalPoints1, totalPoints2), players);
        }else if (totalPoints2 > totalPoints1){
            publicInfos(infoMap.get(gameState.currentPlayerId().next()).won(totalPoints2, totalPoints1), players);
        }else{
            List<String> names = List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2));
            publicInfos(Info.draw(names, totalPoints1), players);
        }


    }

    //une méthode privée permettant d'envoyer une information à tous les joueurs, en appelant la méthode receiveInfo de chacun d'eux
    private static void publicInfos(String string, Map<PlayerId, Player> players){
        players.forEach((playerId, player) -> player.receiveInfo(string));
    }

    //une méthode privée permettant d'informer tous les joueurs d'un changement d'état, en appelant la méthode updateState de chacun d'eux.
    private static void updateState(Map<PlayerId, Player> players, GameState newGameState){
        players.forEach((playerId, player) -> player.updateState(newGameState, newGameState.playerState(playerId)));
    }

    private static String getRandomItems(List<String> list, Random random){
        int index = random.nextInt(list.size());
        return list.get(index);
    }
}