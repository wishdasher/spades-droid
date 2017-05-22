package ksmori.hu.ait.spades;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ksmori.hu.ait.spades.model.Card;
import ksmori.hu.ait.spades.model.Deck;
import ksmori.hu.ait.spades.model.Game;
import ksmori.hu.ait.spades.model.GameRecord;
import ksmori.hu.ait.spades.model.Play;
import ksmori.hu.ait.spades.model.Player;
import ksmori.hu.ait.spades.model.Team;

import static ksmori.hu.ait.spades.model.Game.State.SETUP;

public class WaitingRoomActivity extends AppCompatActivity {

    public static final String PLAYER_MEMBER_INTENT_KEY = "PLAYER_MEMBER_INTENT_KEY";
    public static final String GAME_ID_INTENT_KEY = "GAME_ID_INTENT_KEY";
    public static final String HOST_PLAYER_INTENT_KEY = "HOST_PLAYER_INTENT_KEY";
    private String gameID;
    private boolean isHostPlayer;
    private String playerKeyValue;

    private DatabaseReference database;
    private Map<DatabaseReference, ValueEventListener> listenerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        database = FirebaseDatabase.getInstance().getReference();
        listenerMap = new HashMap<>();

        gameID = getIntent().getStringExtra(GAME_ID_INTENT_KEY);
        isHostPlayer = getIntent().getBooleanExtra(HOST_PLAYER_INTENT_KEY, false);
        playerKeyValue = getIntent().getStringExtra(PLAYER_MEMBER_INTENT_KEY);
        Toast.makeText(this, gameID, Toast.LENGTH_SHORT).show();

        DatabaseReference gamePlayersRef = database.child(StartActivity.GAMES_KEY)
                .child(gameID)
                .child(StartActivity.PLAYERS_KEY);
        ValueEventListener gamePlayersListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updatePlayerList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        gamePlayersRef.addValueEventListener(gamePlayersListener);
        listenerMap.put(gamePlayersRef, gamePlayersListener);

        DatabaseReference statesRef = database.child(StartActivity.GAMES_KEY)
                .child(gameID)
                .child(Game.STATE_KEY);
        ValueEventListener statesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    switch (Game.State.valueOf(dataSnapshot.getValue(String.class))) {
                    case READY:
                        removeListeners();
                        //TODO START GAME
                        Intent intent = new Intent(WaitingRoomActivity.this, SpadesGameActivity.class);
                        intent.putExtra(GAME_ID_INTENT_KEY, gameID);
                        intent.putExtra(HOST_PLAYER_INTENT_KEY, isHostPlayer); //TODO needed?
                        startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        statesRef.addValueEventListener(statesListener);
        listenerMap.put(statesRef, statesListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        database.child(StartActivity.GAMES_KEY).child(gameID)
                .child(StartActivity.PLAYERS_KEY).child(playerKeyValue).removeValue();
        removeListeners();
    }

    private void removeListeners() {
        for (DatabaseReference dr : listenerMap.keySet()) {
            dr.removeEventListener(listenerMap.get(dr));
        }
    }

    private void updatePlayerList(final DataSnapshot dataSnapshot) {
        String players = "";
        int numPlayers = 0;

        for (DataSnapshot child : dataSnapshot.getChildren()) {
            players += child.getValue() + "\n";
            numPlayers += 1;
        }

        TextView tvPlayers = (TextView) findViewById(R.id.tv_all_players);
        tvPlayers.setText(players);

        if (numPlayers == Game.NUM_PLAYERS && isHostPlayer) {
            new AlertDialog.Builder(this)
                    .setTitle("Start game")
                    .setMessage("There are four players, start game?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Set state to SETUP
                            database.child(StartActivity.GAMES_KEY).child(gameID)
                                    .child(Game.STATE_KEY).setValue(SETUP);
                            setUpGame(dataSnapshot);
                        }
                    })
                    .setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }

    private void setUpGame(DataSnapshot playerSnapshot) {
        Toast.makeText(this, "Setting up!", Toast.LENGTH_SHORT).show();
        //hostPlayer already set
        database.child(StartActivity.GAMES_KEY).child(gameID)
                .child(Game.STATE_KEY).setValue(Game.State.READY);

        String hostPlayer = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        List<String> playersList = new ArrayList<>();
        for (DataSnapshot child : playerSnapshot.getChildren()) {
            playersList.add(child.getValue(String.class));
        }

        Deck deck = new Deck();
        List<List<Card>> hands = deck.deal(Game.NUM_PLAYERS);
        Map<String, String> mapNameToPosition = new HashMap<>();

        Player north = new Player(
                playersList.get(0),
                null,
                hands.get(0),
                0,
                playersList.get(1),
                playersList.get(2),
                playersList.get(3)
        );
        mapNameToPosition.put(playersList.get(0), Player.NORTH_KEY);
        Player east = new Player(
                playersList.get(1),
                null,
                hands.get(1),
                0,
                playersList.get(2),
                playersList.get(3),
                playersList.get(0)
        );
        mapNameToPosition.put(playersList.get(1), Player.EAST_KEY);
        Player south = new Player(
                playersList.get(2),
                null,
                hands.get(2),
                0,
                playersList.get(3),
                playersList.get(0),
                playersList.get(1)
        );
        mapNameToPosition.put(playersList.get(2), Player.SOUTH_KEY);
        Player west = new Player(
                playersList.get(3),
                null,
                hands.get(3),
                0,
                playersList.get(0),
                playersList.get(1),
                playersList.get(2)
        );
        mapNameToPosition.put(playersList.get(3), Player.WEST_KEY);

        Team teamNS = new Team(north.getName(), south.getName(), 0, 0);
        Team teamEW = new Team(east.getName(), west.getName(), 0, 0);

        Game.State state = SETUP;

        int roundNumber = 1;
        GameRecord gr = new GameRecord(teamNS.getName(), teamEW.getName(), new ArrayList<Integer>(), new ArrayList<Integer>());

        boolean spadesBroken = false;
        int trickNumber = 1;
        String lastPlayer = hostPlayer;
        String nextPlayer = hostPlayer;
        Card.Suit currentSuit = null;
        List<Play> plays = new ArrayList<>();

        Game newGame = new Game(
                hostPlayer,
                north,
                east,
                south,
                west,
                teamNS,
                teamEW,
                state,
                roundNumber,
                gr,
                spadesBroken,
                trickNumber,
                lastPlayer,
                nextPlayer,
                currentSuit,
                plays
        );
        database.child(StartActivity.GAMES_KEY).child(gameID).setValue(newGame);
        database.child(StartActivity.GAMES_KEY).child(gameID)
                .child(Game.MAP_PLAY2POS_KEY).setValue(mapNameToPosition);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Setup done!", Toast.LENGTH_SHORT).show();
        database.child(StartActivity.GAMES_KEY).child(gameID)
                .child(Game.STATE_KEY).setValue(Game.State.READY);
    }
}
