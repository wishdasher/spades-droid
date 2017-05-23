package ksmori.hu.ait.spades.model;

import com.google.firebase.database.Exclude;

import java.util.List;
import java.util.Map;

public class Game {

    public enum State {
        WAITING, SETUP, READY, BIDDING, PLAY, RESET, END
    }

    @Exclude public static final int NUM_PLAYERS = 4;
    @Exclude public static final int NUM_TRICKS = 13;

    @Exclude public static final String HOST_KEY = "hostPlayer";
    @Exclude public static final String NORTH_KEY = "north";
    @Exclude public static final String EAST_KEY = "east";
    @Exclude public static final String SOUTH_KEY = "south";
    @Exclude public static final String WEST_KEY = "west";
    @Exclude public static final String TEAM_NS_KEY = "teamNS";
    @Exclude public static final String TEAM_EW_KEY = "teamEW";
    @Exclude public static final String GAME_RECORD_KEY = "gameRecord";

    @Exclude public static final String MAP_PLAY_TO_POS_KEY = "mapPlayerToPos";
    @Exclude public static final String PLAYERS_KEY = "players";
    @Exclude public static final String PLAYS_KEY = "plays";

    // UNCHANGING
    private String gameName;
    private String hostPlayer;
    private Player north;
    private Player east;
    private Player south;
    private Player west;
    private Team teamNS;
    private Team teamEW;
    private GameRecord gameRecord;

    private List<Play> plays;
    private Map<String, String> players;

    public Game() {
    }

    public Game(String gameName, String hostPlayer, Player north, Player east, Player south, Player west,
                Team teamNS, Team teamEW, GameRecord gameRecord, List<Play> plays) {
        this.gameName = gameName;
        this.hostPlayer = hostPlayer;
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
        this.teamNS = teamNS;
        this.teamEW = teamEW;
        this.gameRecord = gameRecord;
        this.plays = plays;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getHostPlayer() {
        return hostPlayer;
    }

    public void setHostPlayer(String hostPlayer) {
        this.hostPlayer = hostPlayer;
    }

    public Player getNorth() {
        return north;
    }

    public void setNorth(Player north) {
        this.north = north;
    }

    public Player getEast() {
        return east;
    }

    public void setEast(Player east) {
        this.east = east;
    }

    public Player getSouth() {
        return south;
    }

    public void setSouth(Player south) {
        this.south = south;
    }

    public Player getWest() {
        return west;
    }

    public void setWest(Player west) {
        this.west = west;
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

    public GameRecord getGameRecord() {
        return gameRecord;
    }

    public void setGameRecord(GameRecord gameRecord) {
        this.gameRecord = gameRecord;
    }

    public List<Play> getPlays() {
        return plays;
    }

    public void setPlays(List<Play> plays) {
        this.plays = plays;
    }

    public Map<String, String> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, String> players) {
        this.players = players;
    }
}

