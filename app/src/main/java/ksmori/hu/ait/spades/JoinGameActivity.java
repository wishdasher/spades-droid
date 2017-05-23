package ksmori.hu.ait.spades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinGameActivity extends AppCompatActivity {

    private static final String JOIN_GAME_ACTIVITY_TAG = "JoinGameActivityTag";
    public static final String JOIN_GAME_RETURN = "JOIN_GAME_RETURN";
    public static final String JOIN_GAME_NAME_RETURN = "JOIN_GAME_NAME_RETURN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_games);
        final LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(StartActivity.GAMES_LIST_KEY);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    final String gameID = child.getKey();
                    Button gameButton = new Button(JoinGameActivity.this);
                    gameButton.setText(child.getValue(String.class));
                    linearLayout.addView(gameButton, layoutParams);
                    gameButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intentResult = new Intent();
                            intentResult.putExtra(JOIN_GAME_RETURN, gameID);
                            intentResult.putExtra(JOIN_GAME_NAME_RETURN, child.getValue(String.class));
                            setResult(RESULT_OK, intentResult);
                            JoinGameActivity.this.finish();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(JOIN_GAME_ACTIVITY_TAG, databaseError.getMessage());
            }
        });

    }

}
