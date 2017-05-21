package ksmori.hu.ait.spades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class WaitingRoomActivity extends AppCompatActivity {

    public static final String GAME_ID_KEY = "GAME_ID_KEY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        String gameID = getIntent().getStringExtra(GAME_ID_KEY);
        Toast.makeText(this, gameID, Toast.LENGTH_SHORT).show();
    }
}
