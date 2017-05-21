package ksmori.hu.ait.spades.game;


import com.google.firebase.database.Exclude;

public class Team {

    private String playerAKey;
    private String playerBKey;

    private int score;
    private int bags;

    public Team() {
    }

    public Team(String playerAKey, String playerBKey, int score, int bags) {
        this.playerAKey = playerAKey;
        this.playerBKey = playerBKey;
        this.score = score;
        this.bags = bags;
    }

    public String getPlayerAKey() {
        return playerAKey;
    }

    public void setPlayerAKey(String playerAKey) {
        this.playerAKey = playerAKey;
    }

    public String getPlayerBKey() {
        return playerBKey;
    }

    public void setPlayerBKey(String playerBKey) {
        this.playerBKey = playerBKey;
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
        return playerAKey + "&" + playerBKey;
    }

}
