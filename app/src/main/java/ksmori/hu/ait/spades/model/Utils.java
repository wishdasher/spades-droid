package ksmori.hu.ait.spades.model;


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
}
