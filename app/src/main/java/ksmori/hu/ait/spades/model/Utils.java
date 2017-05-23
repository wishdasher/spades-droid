package ksmori.hu.ait.spades.model;


import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static Play getWinningPlay(List<Play> plays) {
        assert plays.size() == Game.NUM_PLAYERS;

        Play currentWinner = plays.get(0);

        for (int p = 1; p < plays.size(); p++) {
            Card playedCard = plays.get(p).getCard();
            if (playedCard.getSuit() == currentWinner.getCard().getSuit() &&
                    playedCard.getValue() > currentWinner.getCard().getValue()) {
                currentWinner = plays.get(p);
            } else if (playedCard.getSuitValue() == Card.Suit.SPADE &&
                    playedCard.getValue() > currentWinner.getCard().getValue()) {
                currentWinner = plays.get(p);
            }
        }

        return currentWinner;
    }

    public static List<Card> getPlayableHand(List<Card> hand, final Card.Suit currentSuit, final boolean spadesBroken) {
        //TODO what if player only has spades
        if (currentSuit == null) {
            if (spadesBroken) {
                return hand;
            } else {
                List<Card> playableCards = new ArrayList<>();
                for (Card card : hand) {
                    if (card.getSuitValue() != Card.Suit.SPADE) {
                        playableCards.add(card);
                    }
                }
                return playableCards;
            }
        }
        else {
            List<Card> playableCards = new ArrayList<>();
            for (Card card : hand) {
                if (card.getSuitValue() == currentSuit || ( spadesBroken && card.getSuitValue() == Card.Suit.SPADE)) {
                    playableCards.add(card);
                }
            }
            return playableCards;
        }
    }

}
