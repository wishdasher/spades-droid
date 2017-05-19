package ksmori.hu.ait.spades.model;

import java.util.ArrayList;
import java.util.List;

public class Trick {

    private Card.Suit startingSuit;
    private List<Play> plays;

    public Trick() {
        plays = new ArrayList<>(GameModel.NUM_PLAYERS);
    }

    public Card.Suit getStartingSuit() {
        return startingSuit;
    }

    public void add(Play play) {
        if (plays.size() == 0) {
            startingSuit = play.getCard().getSuit();

        }
        plays.add(play);
    }

    public Player getTrickWinner() {
        assert hasAllPlays();

        Play currentWinner = plays.get(0);

        for (int p = 1; p < plays.size(); p++) {
            Card playedCard = plays.get(p).getCard();
            if (playedCard.getSuit() == currentWinner.getCard().getSuit() &&
                    playedCard.getValue() > currentWinner.getCard().getValue()) {
                currentWinner = plays.get(p);
            } else if (playedCard.getSuit() == Card.Suit.SPADE &&
                    playedCard.getValue() > currentWinner.getCard().getValue()) {
                currentWinner = plays.get(p);
            }
        }

        return currentWinner.getPlayer();
    }

    public boolean hasAllPlays() {
        return plays.size() == GameModel.NUM_PLAYERS;
    }
}
