package ksmori.hu.ait.spades.model;

public class Play {

    private String player;
    private Card card;

    public Play() {
    }

    public Play(String player, Card card) {
        this.player = player;
        this.card = card;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
