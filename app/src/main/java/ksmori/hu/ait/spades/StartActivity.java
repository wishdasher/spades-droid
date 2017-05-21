package ksmori.hu.ait.spades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ksmori.hu.ait.spades.model.Game;
import ksmori.hu.ait.spades.model.Play;

public class StartActivity extends AppCompatActivity {

    private static final String START_ACTIVITY_TAG = "StartActivityTag";
    public static final String GAMES_KEY = "games";
    public static final String PLAYERS_KEY = "players";
    public static final String GAMES_LIST_KEY = "games_list";
    public static final int JOIN_GAME_REQUEST = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_host)
    public void hostGame() {
        String key = FirebaseDatabase.getInstance().getReference().
                child(GAMES_KEY).push().getKey();

        Game newGame = new Game(
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                Game.State.WAITING,
                0,
                null,
                false,
                0,
                null,
                null,
                null,
                new ArrayList<Play>());

        FirebaseDatabase.getInstance().getReference().
                child(GAMES_KEY).child(key).setValue(newGame);
        FirebaseDatabase.getInstance().getReference().child(GAMES_LIST_KEY).push().setValue(key);
        FirebaseDatabase.getInstance().getReference().child(GAMES_KEY).child(key).child(PLAYERS_KEY).
                push().setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        Log.d(START_ACTIVITY_TAG, "New game created with id: " + key);

        Intent intent = new Intent(this, WaitingRoomActivity.class);
        intent.putExtra(WaitingRoomActivity.GAME_ID_INTENT_KEY, key);
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
                //TODO disallow if game is full
                String gameID = data.getStringExtra(JoinGameActivity.JOIN_GAME_RETURN);
                Log.d(START_ACTIVITY_TAG, "Returned from JoinGameActivity with game id: " + gameID);
                FirebaseDatabase.getInstance().getReference().child(GAMES_KEY).child(gameID).child(PLAYERS_KEY).
                        push().setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                Intent intent = new Intent(this, WaitingRoomActivity.class);
                intent.putExtra(WaitingRoomActivity.GAME_ID_INTENT_KEY, gameID);
                intent.putExtra(WaitingRoomActivity.HOST_PLAYER_INTENT_KEY, false);
                startActivity(intent);

            }
        }
    }
}
