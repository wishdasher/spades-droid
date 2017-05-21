package ksmori.hu.ait.spades;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.OnClick;
import ksmori.hu.ait.spades.game.Game;

public class StartActivity extends AppCompatActivity {

    public static final String GAMES_KEY = "games";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

    }

    @OnClick(R.id.btn_host)
    public void hostGame() {
        String key = FirebaseDatabase.getInstance().
                getReference().child(GAMES_KEY).push().getKey();

        Game newGame = new Game(
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                "STATE",
                0,
                false,
                0,
                null,
                null,
                null);

        FirebaseDatabase.getInstance().getReference().
                child(GAMES_KEY).child(key).setValue(newGame);
    }

    @OnClick(R.id.btn_join)
    public void joinGame() {

    }
}
