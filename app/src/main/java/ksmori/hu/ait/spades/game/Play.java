package ksmori.hu.ait.spades.game;

public class Play {

    private Player player;
    private Card card;

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
