package ksmori.hu.ait.spades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ksmori.hu.ait.spades.model.Game;

public class WaitingRoomActivity extends AppCompatActivity {

    public static final String GAME_ID_INTENT_KEY = "GAME_ID_INTENT_KEY";
    public static final String HOST_PLAYER_INTENT_KEY = "HOST_PLAYER_INTENT_KEY";
    private String gameID;
    private boolean isHostPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        gameID = getIntent().getStringExtra(GAME_ID_INTENT_KEY);
        isHostPlayer = getIntent().getBooleanExtra(HOST_PLAYER_INTENT_KEY, false);
        Toast.makeText(this, gameID, Toast.LENGTH_SHORT).show();

        DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference(StartActivity.GAMES_KEY).
                child(gameID).
                child(StartActivity.PLAYERS_KEY);
        gamesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updatePlayerList(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updatePlayerList(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                updatePlayerList(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                updatePlayerList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference statesRef = FirebaseDatabase.getInstance().getReference(StartActivity.GAMES_KEY).
                child(gameID).
                child(SpadesGameActivity.STATE_KEY);
        statesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                switch (Game.State.valueOf((String) dataSnapshot.getValue())) {
                    case READY:
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updatePlayerList(DataSnapshot dataSnapshot) {
        String players = "";
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            players += child.getValue().toString() + "\n";
        }
        TextView tvPlayers = (TextView) findViewById(R.id.tv_all_players);
        tvPlayers.setText(players);
        //display all players
        // if host player, make option to start the game (change state) once 4 players
        // set tp seti[
        // other plays should also be listening for state changes
    }

    private void setUpGame() {
        //
    }
}
