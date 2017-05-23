package ksmori.hu.ait.spades;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
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
import ksmori.hu.ait.spades.model.GameVariable;
import ksmori.hu.ait.spades.model.Player;

public class StartActivity extends AppCompatActivity {

    private static final String START_ACTIVITY_TAG = "StartActivityTag";
    public static final String GAMES_KEY = "games";
    public static final String GAMES_LIST_KEY = "gamesList";
    public static final int JOIN_GAME_REQUEST = 500;

    private DatabaseReference database= FirebaseDatabase.getInstance().getReference();
    private String myName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ButterKnife.bind(this);
        myName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    @OnClick(R.id.btn_host)
    public void hostGame() {
        final EditText gameNameInput = new EditText(this);
        gameNameInput.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle("New Game")
                .setMessage("Choose a name for your game!")
                .setView(gameNameInput)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = gameNameInput.getText().toString();
                        createNewGame(newName);
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    private void createNewGame(String newName) {
        String gameID = database.child(GAMES_KEY).push().getKey();

        Game newGame = new Game();
        newGame.setHostPlayer(myName);
        newGame.setGameName(newName);
        database.child(GAMES_KEY).child(gameID).setValue(newGame);

        //Set state to WAITING
        database.child(GAMES_KEY).child(gameID)
                .child(GameVariable.KEY).child(GameVariable.STATE_KEY).setValue(Game.State.WAITING);
        //Add game to list of all games
        database.child(GAMES_LIST_KEY).child(gameID).setValue(newName);
        //Add player to the chosen game's list of players
        database.child(GAMES_KEY).child(gameID).child(Game.PLAYERS_KEY)
                .child(myName).setValue(Player.NORTH_KEY);
        Log.d(START_ACTIVITY_TAG, "New game created with id: " + gameID);

        //Start and join the waiting room
        Intent intent = new Intent(this, WaitingRoomActivity.class);
        intent.putExtra(WaitingRoomActivity.GAME_ID_INTENT_KEY, gameID);
        intent.putExtra(WaitingRoomActivity.HOST_PLAYER_INTENT_KEY, true);
        intent.putExtra(WaitingRoomActivity.ROOM_NAME_INTENT_KEY, newName);
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
                final String gameName = data.getStringExtra(JoinGameActivity.JOIN_GAME_NAME_RETURN);
                Log.d(START_ACTIVITY_TAG, "Returned from JoinGameActivity with game id: " + gameID);

                DatabaseReference statesRef = database.child(GAMES_KEY).child(gameID)
                        .child(GameVariable.KEY).child(GameVariable.STATE_KEY);
                statesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(String.class).equals(Game.State.WAITING.name())) {

                            DatabaseReference hostNameRef = database.child(GAMES_KEY).child(gameID)
                                    .child(Game.HOST_KEY);
                            hostNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //Join the waiting room
                                    boolean isHostPlayer = dataSnapshot.getValue(String.class).equals(myName);
                                    String dir = (isHostPlayer) ? Player.NORTH_KEY : WaitingRoomActivity.DIR_KEY_NONE;

                                    //Add player to the chosen game's list of players, KEY NONE
                                    database.child(GAMES_KEY).child(gameID).child(Game.PLAYERS_KEY)
                                            .child(myName).setValue(dir);

                                    Intent intent = new Intent(StartActivity.this, WaitingRoomActivity.class);
                                    intent.putExtra(WaitingRoomActivity.GAME_ID_INTENT_KEY, gameID);
                                    intent.putExtra(WaitingRoomActivity.HOST_PLAYER_INTENT_KEY, isHostPlayer);
                                    intent.putExtra(WaitingRoomActivity.ROOM_NAME_INTENT_KEY, gameName);
                                    startActivity(intent);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(START_ACTIVITY_TAG, databaseError.getMessage());
                                }
                            });
                        } else {
                            Toast.makeText(StartActivity.this, "Game is not available", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(START_ACTIVITY_TAG, databaseError.getMessage());
                    }
                });

            }
        }
    }
}
