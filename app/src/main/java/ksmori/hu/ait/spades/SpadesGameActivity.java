package ksmori.hu.ait.spades;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ksmori.hu.ait.spades.model.Card;
import ksmori.hu.ait.spades.model.Game;
import ksmori.hu.ait.spades.model.Play;
import ksmori.hu.ait.spades.model.Player;
import ksmori.hu.ait.spades.model.Utils;
import ksmori.hu.ait.spades.presenter.CardPresenter;
import ksmori.hu.ait.spades.presenter.SpadesPresenter;
import ksmori.hu.ait.spades.util.SpadesDebug;
import ksmori.hu.ait.spades.view.CardImageView;
import ksmori.hu.ait.spades.view.GameTableFragment;
import ksmori.hu.ait.spades.view.PlayerCardsFragment;
import ksmori.hu.ait.spades.view.SpadesGameRootLayout;

public class SpadesGameActivity extends AppCompatActivity implements SpadesGameScreen,
        View.OnTouchListener{

    private static final String DEBUG_TAG = "SpadesGameActivity";

    private SpadesPresenter mSpadesPresenter;
    private View mGameView;
    private CardPresenter mCardPresenter;
    private SpadesGameRootLayout rootLayout;

    private CardImageView activeCard;
    private int[] activeCardOriginalLocation;
    private float dX;
    private float dY;
    private int lastAction;

    private String myName;
    private String leftName;
    private String myPosition;
    private String gameID;
    private boolean isHostPlayer;
    private DatabaseReference databaseGame;
    private Map<DatabaseReference, ValueEventListener> listenerMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spades_game);

        myName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        gameID = getIntent().getStringExtra(WaitingRoomActivity.GAME_ID_INTENT_KEY);
        databaseGame = FirebaseDatabase.getInstance().getReference().child(StartActivity.GAMES_KEY).child(gameID);
        isHostPlayer = getIntent().getBooleanExtra(WaitingRoomActivity.HOST_PLAYER_INTENT_KEY, false);

        DatabaseReference mapRef = databaseGame.child(Game.MAP_PLAY2POS_KEY);
        mapRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = dataSnapshot.getValue(HashMap.class);
                myPosition = map.get(myName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // FIND PLAYER TO THE LEFT'S NAME
        DatabaseReference leftRef = databaseGame.child(myPosition).child(Player.LEFT_KEY);
        leftRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                leftName = (String) dataSnapshot.getValue();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listenerMap = new HashMap<>();
        setUpListeners();

        setupGameTableFragment();

        //TODO TEST CODE EVENTUALLY DELETE
        List<Card> playerCards = new ArrayList<>();
        for (int i = Card.MIN_VALUE; i <= Card.MAX_VALUE; i++) {
            playerCards.add(new Card(i, Card.Suit.SPADE));
        }
        setupPlayerCardsFragment(playerCards);

        activeCard = (CardImageView) findViewById(R.id.iv_active_card);
        activeCard.setOnTouchListener(this);
        rootLayout = (SpadesGameRootLayout) findViewById(R.id.layout_root_game_activity);
        activeCard.bringToFront();

        if (isHostPlayer) {
            //TODO WILL EVENTUALLY BE BIDDING
            databaseGame.child(Game.STATE_KEY).setValue(Game.State.PLAY);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(activeCardOriginalLocation == null) {
            activeCardOriginalLocation = new int[2];
            activeCard.getLocationOnScreen(activeCardOriginalLocation); // mutator method
        }
    }

    private void setUpListeners() {
        // set up listeners for players
        DatabaseReference stateRef = databaseGame.child(Game.STATE_KEY);
        stateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                switch (Game.State.valueOf((String) dataSnapshot.getValue())) {
                    case BIDDING:
                        break;
                    case PLAY:
                        break;
                    case RESET:
                        break;
                    case END:
                        break;
                    //TODO IMPLEMENT
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference nextPlayerRef = databaseGame.child(Game.NEXT_PLAYER_KEY);
        nextPlayerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nextPlayer = (String) dataSnapshot.getValue();
                if (nextPlayer.equals(myName)) {
                    // IT IS MY TURN
                    final int[] trickNumber = new int[1];
                    DatabaseReference trickRef = databaseGame.child(Game.TRICK_NUMBER_KEY);
                    trickRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            trickNumber[0] = (Integer) dataSnapshot.getValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    if (trickNumber[0] > Game.NUM_TRICKS) {
                        // GAME HAS ENDED
                        databaseGame.child(Game.STATE_KEY).setValue(Game.State.RESET);
                    } else {
                        // CONTINUE
                        Play myPlay = playCard();
                        DatabaseReference playsRef = databaseGame.child(Game.PLAYS_KEY);
                        final List<Play> plays = new ArrayList<Play>();
                        playsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                    Play play = snapshot.getValue(Play.class);
                                    plays.add(play);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        plays.add(myPlay);
                        if (plays.size() == Game.NUM_PLAYERS) {
                            // TRICK IS OVER
                            String winningPlayer = Utils.getTrickWinner(plays);
                            databaseGame.child(Game.LAST_PLAYER_KEY).setValue(myName);
                            databaseGame.child(Game.NEXT_PLAYER_KEY).setValue(winningPlayer);
                            databaseGame.child(Game.PLAYS_KEY).setValue(new ArrayList<>());
                            databaseGame.child(Game.TRICK_NUMBER_KEY).setValue(trickNumber[0] + 1);
                        } else {
                            // TRICK CONTINUES
                            databaseGame.child(Game.NEXT_PLAYER_KEY).setValue(leftName);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpNextGame() {
        //TODO called from reset if the player is host
    }

    private Play playCard() {
        return null;
        //TODO implmeent for David
    }

    public void setupGameTableFragment() {
        GameTableFragment gtf = new GameTableFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_game_table_container, gtf,GameTableFragment.TAG);
        ft.commit();


        mGameView = gtf.getView();
    }

    private void setupPlayerCardsFragment(List<Card> playerCards) {
        PlayerCardsFragment pcf = new PlayerCardsFragment();
        Bundle argBundle = new Bundle();
        argBundle.putSerializable(PlayerCardsFragment.CARDS_KEY,(Serializable) playerCards);
        pcf.setArguments(argBundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_player_cards_container, pcf,PlayerCardsFragment.TAG);
        ft.commit();

        mCardPresenter = pcf;
    }


    @Override
    public void displayBidding() {
        Player player = mSpadesPresenter.getBiddingPlayer();
        List<Card> playerCards = mSpadesPresenter.getCards(player);
        setupPlayerCardsFragment(playerCards);

    }

    @Override
    public void displayGame() {

    }

    @Override
    public void setActiveCard(CardImageView civ, MotionEvent touchEvent) {
        if(civ == null){
            activeCard.setImageResource(0);// invalid/none
            SpadesGameRootLayout.LayoutParams p =
                    (SpadesGameRootLayout.LayoutParams) activeCard.getLayoutParams();
            p.width = 0;
            p.height = 0;
            activeCard.setLayoutParams(p);
        } else {
            activeCard.setLayoutParams(new SpadesGameRootLayout.LayoutParams(
                    civ.getWidth(),civ.getHeight()));
            activeCard.setImageResource(/*(Integer) civ.getTag()*/R.drawable.card_2_of_diamonds);
            activeCard.setTag(civ.getTag());

//            Log.d(DEBUG_TAG,String.format("Active Card: x.locOG=%d, y.locOG=%d",
//                    activeCardOriginalLocation[0],activeCardOriginalLocation[1]));
            int[] loc = new int[2];
            civ.getLocationOnScreen(loc);
            activeCard.setX(loc[0] - activeCardOriginalLocation[0]);
            activeCard.setY(loc[1] - activeCardOriginalLocation[1]);

            activeCard.bringToFront();
            activeCard.requestLayout();

            // Attempt to steal subsequent ACTION_MOVEs from other cardimageviews
            MotionEvent subscribe = MotionEvent.obtain(touchEvent);
            subscribe.setAction(MotionEvent.ACTION_DOWN);
            activeCard.dispatchTouchEvent(subscribe);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        String actionStr = SpadesDebug.getActionString(event);
        Log.d(DEBUG_TAG, String.format("onTouch(%s,%s)", v.toString(), actionStr));
        if(event.getActionMasked() == MotionEvent.ACTION_CANCEL){
            return true;
        } else if(v.getId() == R.id.iv_active_card){
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(DEBUG_TAG, "ActiveCard DOWN!");
                    dX = v.getX() - event.getRawX();
                    dY = v.getY() - event.getRawY();
                    lastAction = MotionEvent.ACTION_DOWN;
                    break;

                case MotionEvent.ACTION_MOVE:
                    v.setY(event.getRawY() + dY);
                    v.setX(event.getRawX() + dX);
                    lastAction = MotionEvent.ACTION_MOVE;
                    break;

                case MotionEvent.ACTION_UP:
                    if (lastAction == MotionEvent.ACTION_DOWN) {
                        Log.d(DEBUG_TAG, "ActiveCard clickUp!");
                        mCardPresenter.cancelCardSelection();
                    } else if (lastAction == MotionEvent.ACTION_MOVE){
                        Log.d(DEBUG_TAG, "ActiveCard dragUp!");
                        if(inPlayArea(event.getRawX(),event.getRawY())){
                            performPlayAnimation(v);
                            mSpadesPresenter.playCard(((CardImageView) v).getCard());
                        }
                    }
                    break;

                default:
                    return false;
            }
            return true;
        } else if(v instanceof CardImageView && v.getId() != R.id.iv_active_card
                && event.getActionMasked() != MotionEvent.ACTION_DOWN){
            return true;
        }
        return false;
    }

    private boolean inPlayArea(float rawX, float rawY) {
        return false;
    }

    private void performPlayAnimation(View v) {

    }


}
