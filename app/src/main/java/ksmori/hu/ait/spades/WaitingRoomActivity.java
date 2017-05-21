package ksmori.hu.ait.spades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WaitingRoomActivity extends AppCompatActivity {

    public static final String GAME_ID_INTENT_KEY = "GAME_ID_INTENT_KEY";
    public static final String HOST_PLAYER_INTENT_KEY = "HOST_PLAYER_INTENT_KEY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        String gameID = getIntent().getStringExtra(GAME_ID_INTENT_KEY);
        boolean isHostPlayer = getIntent().getBooleanExtra(HOST_PLAYER_INTENT_KEY, false);
        Toast.makeText(this, gameID, Toast.LENGTH_SHORT).show();

        DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference(StartActivity.GAMES_KEY).
                child(gameID).
                child(StartActivity.PLAYERS_KEY);
        gamesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updatePlayerList();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updatePlayerList();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                updatePlayerList();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                updatePlayerList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase statesRef = FirebaseDatabase.getInstance().getReference(StartActivity.GAMES_KEY).
                child(gameID).
                child(StartActivity.PLAYERS_KEY);
    }

    private void updatePlayerList() {
        //display all players
        // if host player, make option to start the game (change state)
        // other plays should also be listening for state changes
    }

    private void setUpGame() {
        //
    }
}
