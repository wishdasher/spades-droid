package ksmori.hu.ait.spades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    public static final String JOIN_GAME_RETURN = "JOIN_GAME_RETURN";

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
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String gameID = (String) ds.getValue();
                    Button gameButton = new Button(JoinGameActivity.this);
                    gameButton.setText(gameID);
                    linearLayout.addView(gameButton, layoutParams);
                    gameButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intentResult = new Intent();
                            intentResult.putExtra(JOIN_GAME_RETURN, gameID);
                            setResult(RESULT_OK, intentResult);
                            JoinGameActivity.this.finish();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

}
