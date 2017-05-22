package ksmori.hu.ait.spades.model;

import java.util.List;

public class GameRecord {

    private String teamNS;
    private String teamEW;

    private List<Integer> scoresNS;
    private List<Integer> scoresEW;

    public GameRecord() {
    }

    public GameRecord(String teamNS, String teamEW, List<Integer> scoresNS, List<Integer> scoresEW) {
        this.teamNS = teamNS;
        this.teamEW = teamEW;
        this.scoresNS = scoresNS;
        this.scoresEW = scoresEW;
    }

    public String getTeamNS() {
        return teamNS;
    }

    public void setTeamNS(String teamNS) {
        this.teamNS = teamNS;
    }

    public String getTeamEW() {
        return teamEW;
    }

    public void setTeamEW(String teamEW) {
        this.teamEW = teamEW;
    }

    public List<Integer> getScoresNS() {
        return scoresNS;
    }

    public void setScoresNS(List<Integer> scoresNS) {
        this.scoresNS = scoresNS;
    }

    public List<Integer> getScoresEW() {
        return scoresEW;
    }

    public void setScoresEW(List<Integer> scoresEW) {
        this.scoresEW = scoresEW;
    }
}
