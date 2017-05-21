package ksmori.hu.ait.spades.game;

import java.util.List;

class GameRecord {

    private Team teamNS;
    private Team teamEW;

    private List<Integer> scoresNS;
    private List<Integer> scoresEW;

    public GameRecord() {
    }

    public GameRecord(Team teamNS, Team teamEW, List<Integer> scoresNS, List<Integer> scoresEW) {
        this.teamNS = teamNS;
        this.teamEW = teamEW;
        this.scoresNS = scoresNS;
        this.scoresEW = scoresEW;
    }

    public Team getTeamNS() {
        return teamNS;
    }

    public void setTeamNS(Team teamNS) {
        this.teamNS = teamNS;
    }

    public Team getTeamEW() {
        return teamEW;
    }

    public void setTeamEW(Team teamEW) {
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
