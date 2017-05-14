package ksmori.hu.ait.spades.game;


public class Team {

    private Player playerA;
    private Player playerB;

    private int score;
    private int bags;

    public Team(Player a, Player b) {
        playerA = a;
        playerB = b;
        score = 0;
        bags = 0;
        checkRep();
    }

    private void checkRep() {
        assert bags >= 0 && bags < 10;
    }

    public Player[] getPlayers() {
        return new Player[]{playerA, playerB};
    }

    public void updateScore(int points, int bags) {
        score += points;
        this.bags += bags;
        if (bags > 10) {
            bags -= 10;
            score -= 100;
        }
        checkRep();
    }

    public int getTotalScore() {
        if (score > 0) {
            return score + bags;
        } else {
            return score - bags;
        }
    }

}