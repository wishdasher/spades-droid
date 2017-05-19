package ksmori.hu.ait.spades.model;

public class Play {

    private final Player player;
    private final Card card;

    public Play(Player player, Card card) {
        this.player = player;
        this.card = card;
    }

    public Player getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }
}
