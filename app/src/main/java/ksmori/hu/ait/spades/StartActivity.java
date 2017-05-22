package ksmori.hu.ait.spades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ksmori.hu.ait.spades.model.Game;

public class StartActivity extends AppCompatActivity {

    private static final String START_ACTIVITY_TAG = "StartActivityTag";
    public static final String GAMES_KEY = "games";
    public static final String PLAYERS_KEY = "players";
    public static final String GAMES_LIST_KEY = "gamesList";
    public static final int JOIN_GAME_REQUEST = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_host)
    public void hostGame() {
        String gameID = FirebaseDatabase.getInstance().getReference()
                .child(GAMES_KEY).push().getKey();

        Game newGame = new Game();
        newGame.setHostPlayer(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        newGame.setState(Game.State.WAITING.name());

        FirebaseDatabase.getInstance().getReference()
                .child(GAMES_KEY).child(gameID).setValue(newGame);
        //Set state to WAITING
        FirebaseDatabase.getInstance().getReference(StartActivity.GAMES_KEY)
                .child(gameID).child(Game.STATE_KEY).setValue(Game.State.WAITING);
        //Add game to list of all games
        FirebaseDatabase.getInstance().getReference().child(GAMES_LIST_KEY).push().setValue(gameID);
        //Add player to the chosen game's list of players
        String playerKey = FirebaseDatabase.getInstance().getReference().child(GAMES_KEY).child(gameID)
                .child(PLAYERS_KEY).push().getKey();
        FirebaseDatabase.getInstance().getReference().child(GAMES_KEY).child(gameID)
                .child(PLAYERS_KEY).child(playerKey)
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        Log.d(START_ACTIVITY_TAG, "New game created with id: " + gameID);

        Intent intent = new Intent(this, WaitingRoomActivity.class);
        intent.putExtra(WaitingRoomActivity.GAME_ID_INTENT_KEY, gameID);
        intent.putExtra(WaitingRoomActivity.PLAYER_MEMBER_INTENT_KEY, playerKey);
        intent.putExtra(WaitingRoomActivity.HOST_PLAYER_INTENT_KEY, true);
        startActivity(intent);
    }

    @OnClick(R.id.btn_join)
    public void joinGame() {
        Intent intent = new Intent(this, JoinGameActivity.class);
        startActivityForResult(intent, JOIN_GAME_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == JOIN_GAME_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                final String gameID = data.getStringExtra(JoinGameActivity.JOIN_GAME_RETURN);
                Log.d(START_ACTIVITY_TAG, "Returned from JoinGameActivity with game id: " + gameID);

                DatabaseReference statesRef = FirebaseDatabase.getInstance().getReference(StartActivity.GAMES_KEY).
                        child(gameID).
                        child(Game.STATE_KEY);
                statesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue().equals(Game.State.WAITING.name())) {
                            //Add player to the chosen game's list of players
                            String playerKey = FirebaseDatabase.getInstance().getReference().child(GAMES_KEY).child(gameID)
                                    .child(PLAYERS_KEY).push().getKey();
                            FirebaseDatabase.getInstance().getReference().child(GAMES_KEY).child(gameID)
                                    .child(PLAYERS_KEY).child(playerKey)
                                    .setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

                            Intent intent = new Intent(StartActivity.this, WaitingRoomActivity.class);
                            intent.putExtra(WaitingRoomActivity.GAME_ID_INTENT_KEY, gameID);
                            intent.putExtra(WaitingRoomActivity.PLAYER_MEMBER_INTENT_KEY, playerKey);
                            intent.putExtra(WaitingRoomActivity.HOST_PLAYER_INTENT_KEY, false);
                            startActivity(intent);
                        } else {
                            Toast.makeText(StartActivity.this, "Game is not available", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }
    }
}
