package ksmori.hu.ait.spades;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import ksmori.hu.ait.spades.model.GameVariable;
import ksmori.hu.ait.spades.model.Player;
import ksmori.hu.ait.spades.model.Team;

import static ksmori.hu.ait.spades.model.Game.State.SETUP;

public class WaitingRoomActivity extends AppCompatActivity {

    private static final String WAITING_ROOM_ACTIVITY_TAG = "WaitingRoomActivityTag";
    public static final String GAME_ID_INTENT_KEY = "GAME_ID_INTENT_KEY";
    public static final String HOST_PLAYER_INTENT_KEY = "HOST_PLAYER_INTENT_KEY";
    public static final String ROOM_NAME_INTENT_KEY = "ROOM_NAME_INTENT_KEY";
    public static final String DIR_KEY_NONE  = "NONE";
    private String gameID;
    private String gameName;
    private boolean isHostPlayer;
    private String myName;

    private DatabaseReference database;
    private DatabaseReference databaseGame;
    private Map<DatabaseReference, ValueEventListener> listenerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        gameID = getIntent().getStringExtra(GAME_ID_INTENT_KEY);
        gameName = getIntent().getStringExtra(ROOM_NAME_INTENT_KEY);
        isHostPlayer = getIntent().getBooleanExtra(HOST_PLAYER_INTENT_KEY, false);
        myName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Toast.makeText(this, "Entered game " + gameName, Toast.LENGTH_SHORT).show();

        database = FirebaseDatabase.getInstance().getReference();
        listenerMap = new HashMap<>();

        databaseGame = FirebaseDatabase.getInstance().getReference()
                .child(StartActivity.GAMES_KEY).child(gameID);

        TextView tvLabel = (TextView) findViewById(R.id.tv_current_players_label);
        tvLabel.setText("Current players in room " + gameName);

        setUpListeners();
    }

    private void setUpListeners() {
        DatabaseReference gamePlayersRef = databaseGame.child(Game.PLAYERS_KEY);
        ValueEventListener gamePlayersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updatePlayerList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(WAITING_ROOM_ACTIVITY_TAG, databaseError.getMessage());
            }
        };
        gamePlayersRef.addValueEventListener(gamePlayersListener);
        listenerMap.put(gamePlayersRef, gamePlayersListener);

        DatabaseReference statesRef = databaseGame.child(GameVariable.KEY).child(GameVariable.STATE_KEY);
        ValueEventListener statesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                switch (Game.State.valueOf(dataSnapshot.getValue(String.class))) {
                    case READY:
                        removeListeners();
                        Intent intent = new Intent(WaitingRoomActivity.this, SpadesGameActivity.class);
                        intent.putExtra(GAME_ID_INTENT_KEY, gameID);
                        intent.putExtra(HOST_PLAYER_INTENT_KEY, isHostPlayer);
                        startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(WAITING_ROOM_ACTIVITY_TAG, databaseError.getMessage());
            }
        };
        statesRef.addValueEventListener(statesListener);
        listenerMap.put(statesRef, statesListener);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        database.child(StartActivity.GAMES_KEY).child(gameID)
                .child(Game.PLAYERS_KEY).child(myName).removeValue();
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
            players += child.getKey() + "\n";
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
                            databaseGame.child(GameVariable.KEY).child(GameVariable.STATE_KEY).setValue(SETUP);
                            setUpGame(dataSnapshot);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }

    private void setUpGame(DataSnapshot playerSnapshot) {
        //DONE BY THE HOST ONLY
        Toast.makeText(this, "Setting up!", Toast.LENGTH_SHORT).show();
        databaseGame.child(GameVariable.KEY).child(GameVariable.STATE_KEY).setValue(Game.State.SETUP);

        List<String> playersList = new ArrayList<>();
        for (DataSnapshot child : playerSnapshot.getChildren()) {
            playersList.add(child.getKey());
        }

        //playersList = Arrays.asList("sophie", "david", "scrub", "sun");
        Deck deck = new Deck();
        List<ArrayList<Card>> hands = deck.deal(Game.NUM_PLAYERS);

        Player north = new Player(
                playersList.get(0),
                null,
                hands.get(0),
                0,
                playersList.get(1),
                playersList.get(2),
                playersList.get(3)
        );
        Player east = new Player(
                playersList.get(1),
                null,
                hands.get(1),
                0,
                playersList.get(2),
                playersList.get(3),
                playersList.get(0)
        );
        Player south = new Player(
                playersList.get(2),
                null,
                hands.get(2),
                0,
                playersList.get(3),
                playersList.get(0),
                playersList.get(1)
        );
        Player west = new Player(
                playersList.get(3),
                null,
                hands.get(3),
                0,
                playersList.get(0),
                playersList.get(1),
                playersList.get(2)
        );

        Team teamNS = new Team(north.getName(), south.getName(), 0, 0);
        Team teamEW = new Team(east.getName(), west.getName(), 0, 0);

        GameRecord gr = new GameRecord(teamNS.getName(), teamEW.getName(),
                new ArrayList<Integer>(), new ArrayList<Integer>());

        databaseGame.child(Game.HOST_KEY).setValue(myName);
        databaseGame.child(Game.NORTH_KEY).setValue(north);
        databaseGame.child(Game.EAST_KEY).setValue(east);
        databaseGame.child(Game.SOUTH_KEY).setValue(south);
        databaseGame.child(Game.WEST_KEY).setValue(west);
        databaseGame.child(Game.TEAM_NS_KEY).setValue(teamNS);
        databaseGame.child(Game.TEAM_EW_KEY).setValue(teamEW);
        databaseGame.child(Game.GAME_RECORD_KEY).setValue(gr);

        databaseGame.child(Game.PLAYS_KEY).setValue(new ArrayList<>());


        databaseGame.child(GameVariable.KEY).child(GameVariable.STATE_KEY).setValue(SETUP);
        databaseGame.child(GameVariable.KEY).child(GameVariable.ROUND_KEY).setValue(1);
        databaseGame.child(GameVariable.KEY).child(GameVariable.TRICK_NUMBER_KEY).setValue(1);
        databaseGame.child(GameVariable.KEY).child(GameVariable.SPADES_BROKEN_KEY).setValue(false);
        databaseGame.child(GameVariable.KEY).child(GameVariable.CURRENT_SUIT_KEY).setValue(null);
        databaseGame.child(GameVariable.KEY).child(GameVariable.LAST_PLAYER_KEY).setValue(myName);
        databaseGame.child(GameVariable.KEY).child(GameVariable.NEXT_PLAYER_KEY).setValue(myName);

        databaseGame.child(Game.PLAYERS_KEY).child(playersList.get(0)).setValue(Player.NORTH_KEY);
        databaseGame.child(Game.PLAYERS_KEY).child(playersList.get(1)).setValue(Player.EAST_KEY);
        databaseGame.child(Game.PLAYERS_KEY).child(playersList.get(2)).setValue(Player.SOUTH_KEY);
        databaseGame.child(Game.PLAYERS_KEY).child(playersList.get(3)).setValue(Player.WEST_KEY);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Setup done!", Toast.LENGTH_SHORT).show();
        databaseGame.child(GameVariable.KEY).child(GameVariable.STATE_KEY).setValue(Game.State.READY);
    }
}
