package ksmori.hu.ait.spades.game;

public class Play {

    private String playerID;
    private Card card;

    public Play() {
    }

    public Play(String playerID, Card card) {
        this.playerID = playerID;
        this.card = card;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
