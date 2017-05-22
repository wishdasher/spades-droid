package ksmori.hu.ait.spades.model;


import com.google.firebase.database.Exclude;

public class Team {

    private String playerA;
    private String playerB;

    private int score;
    private int bags;

    public Team() {
    }

    public Team(String playerA, String playerB, int score, int bags) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.score = score;
        this.bags = bags;
    }

    public String getPlayerA() {
        return playerA;
    }

    public void setPlayerA(String playerA) {
        this.playerA = playerA;
    }

    public String getPlayerB() {
        return playerB;
    }

    public void setPlayerB(String playerB) {
        this.playerB = playerB;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getBags() {
        return bags;
    }

    public void setBags(int bags) {
        this.bags = bags;
    }

    @Exclude
    private void checkRep() {
        assert bags >= 0 && bags < 10;
    }

    @Exclude
    public int calculateRoundScore() {
        //TODO: calculate based on bids and
        return 0;
    }

    @Exclude
    public void updateScore(int points, int bags) {
        score += points;
        this.bags += bags;
        if (bags > 10) {
            bags -= 10;
            score -= 100;
        }
        checkRep();
    }

    @Exclude
    public int getTotalScore() {
        if (score > 0) {
            return score + bags;
        } else {
            return score - bags;
        }
    }

    @Exclude
    public String getName() {
        return playerA + "&" + playerB;
    }

}
