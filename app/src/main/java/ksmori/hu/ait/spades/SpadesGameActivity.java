package ksmori.hu.ait.spades;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
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
    private static final long ANIM_DURATION_MILLIS = 750;
    private SpadesPresenter mSpadesPresenter;
    private Fragment mGameFragment;
    private CardPresenter mCardPresenter;
    private SpadesGameRootLayout rootLayout;

    private CardImageView activeCard;
    private int[] activeCardOriginalLocation;
    private float dX;
    private float dY;
    private int lastAction;

    private String myName;
    private String leftName;
    private String myPosition = "north";
    private String gameID;
    private boolean isHostPlayer;
    private DatabaseReference databaseGame;
    private Map<DatabaseReference, ValueEventListener> listenerMap;
    private boolean spadesBroken;
    private Map<String, String> mapPlayerToPos;
    private Map<String, Integer> mapPositionToTricks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spades_game);

        myName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        gameID = getIntent().getStringExtra(WaitingRoomActivity.GAME_ID_INTENT_KEY);
        databaseGame = FirebaseDatabase.getInstance().getReference().child(StartActivity.GAMES_KEY).child(gameID);
        isHostPlayer = getIntent().getBooleanExtra(WaitingRoomActivity.HOST_PLAYER_INTENT_KEY, false);
        spadesBroken = false;
        mapPlayerToPos = new HashMap<>();
        mapPositionToTricks = new HashMap<>();
        listenerMap = new HashMap<>();

        DatabaseReference mapRef = databaseGame.child(Game.MAP_PLAY2POS_KEY);
        mapRef.keepSynced(true);
        mapRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    mapPlayerToPos.put(child.getKey(), child.getValue(String.class));
                }
                myPosition = mapPlayerToPos.get(myName);
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
                leftName = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final List<Card> myHand = new ArrayList<>();
        // GET INITIAL CARDS
        DatabaseReference cardsRef = databaseGame.child(myPosition).child(Player.HAND_KEY);
        cardsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    myHand.add(child.getValue(Card.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        setUpListeners();

        setupGameTableFragment();

        setupPlayerCardsFragment(myHand);

        activeCard = (CardImageView) findViewById(R.id.iv_active_card);
        activeCard.setOnTouchListener(this);
//        rootLayout = (SpadesGameRootLayout) findViewById(R.id.layout_root_game_activity);
        activeCard.bringToFront();

        mSpadesPresenter = new SpadesPresenter();

        if (isHostPlayer) {
            //EVENTUALLY BE BIDDING
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
        DatabaseReference northTricksRef = databaseGame.child(Player.NORTH_KEY).child(Player.TRICKS_KEY);
        northTricksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mapPositionToTricks.put(Game.NORTH_KEY, dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference eastTricksRef = databaseGame.child(Player.EAST_KEY).child(Player.TRICKS_KEY);
        eastTricksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mapPositionToTricks.put(Game.EAST_KEY, dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference southTricksRef = databaseGame.child(Player.SOUTH_KEY).child(Player.TRICKS_KEY);
        southTricksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mapPositionToTricks.put(Game.SOUTH_KEY, dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference westTricksRef = databaseGame.child(Player.WEST_KEY).child(Player.TRICKS_KEY);
        westTricksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mapPositionToTricks.put(Game.WEST_KEY, dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference spadesBrokenRef = databaseGame.child(Game.SPADES_BROKEN_KEY);
        spadesBrokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                spadesBroken = dataSnapshot.getValue(Boolean.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference stateRef = databaseGame.child(Game.STATE_KEY);
        stateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                switch (Game.State.valueOf(dataSnapshot.getValue(String.class))) {
                    case RESET:
                        if (isHostPlayer) {
                            setUpNextGame();
                        }
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
                String nextPlayer = dataSnapshot.getValue(String.class);
                if (nextPlayer.equals(myName)) {
                    // IT IS MY TURN
                    final int[] trickNumber = new int[1];
                    DatabaseReference trickRef = databaseGame.child(Game.TRICK_NUMBER_KEY);
                    trickRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            trickNumber[0] = dataSnapshot.getValue(Integer.class);
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
                            Play winningPlay = Utils.getWinningPlay(plays);
                            if (!spadesBroken && winningPlay.getCard().getSuitValue() == Card.Suit.SPADE) {
                                spadesBroken = true;
                                databaseGame.child(Game.SPADES_BROKEN_KEY).setValue(true);
                            }
                            databaseGame.child(Game.LAST_PLAYER_KEY).setValue(myName);
                            databaseGame.child(Game.NEXT_PLAYER_KEY).setValue(winningPlay.getPlayer());
                            String winningPos = mapPlayerToPos.get(winningPlay.getPlayer());
                            databaseGame.child(winningPos)
                                    .child(Player.TRICKS_KEY).setValue(mapPositionToTricks.get(winningPos) + 1);
                            databaseGame.child(Game.PLAYS_KEY).setValue(new ArrayList<>());
                            databaseGame.child(Game.CURRENT_SUIT_KEY).setValue(null);
                            databaseGame.child(Game.TRICK_NUMBER_KEY).setValue(trickNumber[0] + 1);
                        } else {
                            // TRICK CONTINUES
                            if (plays.size() == 1) {
                                databaseGame.child(Game.CURRENT_SUIT_KEY).setValue(myPlay.getCard().getSuit());
                            }
                            databaseGame.child(Game.PLAYS_KEY).setValue(plays);
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
        databaseGame.child(Game.SPADES_BROKEN_KEY).setValue(false);
        databaseGame.child(Game.TRICK_NUMBER_KEY).setValue(1);
        databaseGame.child(Game.NEXT_PLAYER_KEY).setValue(leftName);
        databaseGame.child(Game.CURRENT_SUIT_KEY).setValue(null);
        databaseGame.child(Game.PLAYS_KEY).setValue(new ArrayList<>());
    }

    private Play playCard() {
        final Player[] myPlayerArray = new Player[1];
        DatabaseReference playerRef = databaseGame.child(myPosition);
        playerRef.keepSynced(true);
        ValueEventListener playerRefListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myPlayerArray[0] = dataSnapshot.getValue(Player.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        playerRef.addListenerForSingleValueEvent(playerRefListener);
        Player myPlayer = myPlayerArray[0];
        final Card.Suit[] currentSuit = new Card.Suit[1];
        DatabaseReference currentSuitRef = databaseGame.child(Game.CURRENT_SUIT_KEY);
        currentSuitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentSuit[0] = Card.Suit.valueOf(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        List<Card> playableCards = myPlayer.getPlayableHand(currentSuit[0], spadesBroken);
        return null;
        //TODO implmeent for David
    }

    public void setupGameTableFragment() {
        GameTableFragment gtf = new GameTableFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_game_table_container, gtf,GameTableFragment.TAG);
        ft.commit();

        mGameFragment = gtf;
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
                            performPlayAnimation();
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
        View gameView = mGameFragment.getView();
        if(gameView!=null) {
            int[] gameLoc = new int[2];
            gameView.getLocationOnScreen(gameLoc);
            int minX = gameLoc[0];
            int maxX = minX + gameView.getWidth();
            int minY = gameLoc[1];
            int maxY = minY + gameView.getHeight();
            Log.d(DEBUG_TAG, String.format("X validation: %d < %f < %d ?",minX, rawX, maxX));
            Log.d(DEBUG_TAG, String.format("Y validation: %d < %f < %d ?",minY, rawY, maxY));

            return (minX <= rawX && rawX <= maxX && minY <= rawY && rawY <= maxY);
        } else {
            return false;
        }
    }

    private void performPlayAnimation() {
        View viewTarget = mGameFragment.getView().findViewById(R.id.iv_player_card_bottom);
        int[] targetLoc = new int[2];
        viewTarget.getLocationOnScreen(targetLoc);
        float endX = (float) targetLoc[0] - activeCardOriginalLocation[0];
        float endY = (float) targetLoc[1] - activeCardOriginalLocation[1];
        ViewPropertyAnimatorCompat vpAnim = ViewCompat.animate(activeCard).translationX(endX).translationY(endY);
        vpAnim.scaleXBy(viewTarget.getWidth() / (float) activeCard.getWidth());
        vpAnim.scaleYBy(viewTarget.getHeight() / (float) activeCard.getHeight());
        vpAnim.setDuration(ANIM_DURATION_MILLIS);
        vpAnim.withEndAction(new Runnable() {
            @Override
            public void run() {
                mSpadesPresenter.playCard(activeCard.getCard());
            }
        });
        vpAnim.start();
    }
}
