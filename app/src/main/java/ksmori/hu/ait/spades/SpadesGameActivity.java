package ksmori.hu.ait.spades;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ksmori.hu.ait.spades.model.Card;
import ksmori.hu.ait.spades.model.Deck;
import ksmori.hu.ait.spades.model.Game;
import ksmori.hu.ait.spades.model.Play;
import ksmori.hu.ait.spades.model.Player;
import ksmori.hu.ait.spades.model.Utils;
import ksmori.hu.ait.spades.presenter.CardsDisplay;
import ksmori.hu.ait.spades.presenter.SpadesPresenter;
import ksmori.hu.ait.spades.view.CardImageView;
import ksmori.hu.ait.spades.view.GameTableFragment;
import ksmori.hu.ait.spades.view.PlayerCardsFragment;
import ksmori.hu.ait.spades.view.SpadesGameRootLayout;

public class SpadesGameActivity extends AppCompatActivity implements SpadesGameScreen,
        View.OnTouchListener{

    private static final String DEBUG_TAG = "SpadesGameActivity";
    private static final long ANIM_DURATION_MILLIS = 750;
    public static final float PLAY_AREA_MIN_WIDTH_PERCENT = 0.25f;
    public static final float PLAY_AREA_MAX_WIDTH_PERCENT = 0.75f;
    public static final float PLAY_AREA_MIN_HEIGHT_PERCENT = 0.34f;
    public static final float PLAY_AREA_MAX_HEIGHT_PERCENT = 0.95f;
    private SpadesPresenter mSpadesPresenter;
    private Fragment mGameFragment;
    private CardsDisplay mCardsDisplay;
    private SpadesGameRootLayout rootLayout;
    private List<Card> playerCards;

    private CardImageView activeCard;
    private int[] activeCardOriginalLocation;
    private int[] cardCancelLocation;
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

//        DatabaseReference mapRef = databaseGame.child(Game.MAP_PLAY2POS_KEY);
//        mapRef.keepSynced(true);
//        mapRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    mapPlayerToPos.put(child.getKey(), child.getValue(String.class));
//                }
//                myPosition = mapPlayerToPos.get(myName);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        // FIND PLAYER TO THE LEFT'S NAME
//        DatabaseReference leftRef = databaseGame.child(myPosition).child(Player.LEFT_KEY);
//        leftRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                leftName = dataSnapshot.getValue(String.class);
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        listenerMap = new HashMap<>();
//        setUpListeners();

        setupGameTableFragment();

        //TODO TEST CODE EVENTUALLY DELETE
        Deck deck = new Deck();
        List<ArrayList<Card>> hands = deck.deal(Game.NUM_PLAYERS);
        setupPlayerCardsFragment(hands.get(0));

//        String gameID = getIntent().getStringExtra(WaitingRoomActivity.GAME_ID_INTENT_KEY);
//        boolean isHost = getIntent().getBooleanExtra(WaitingRoomActivity.HOST_PLAYER_INTENT_KEY, false);
//        mSpadesPresenter = new SpadesPresenter(gameID,isHost);
        setupGameTableFragment();

        activeCard = (CardImageView) findViewById(R.id.iv_active_card);
        activeCard.setOnTouchListener(this);
        rootLayout = (SpadesGameRootLayout) findViewById(R.id.layout_root_game_activity);
        activeCard.bringToFront();


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(activeCardOriginalLocation == null) {
            activeCardOriginalLocation = new int[2];
            activeCard.getLocationOnScreen(activeCardOriginalLocation); // mutator method
        }
    }

    private void setUpListeners() {
        // TODO set up listeners for players attributes
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
                            DatabaseReference winningTrickRef = databaseGame.child(winningPos)
                                    .child(Player.TRICKS_KEY);
                            final int[] currentTricksTaken = new int[1];
                            winningTrickRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    currentTricksTaken[0] = dataSnapshot.getValue(Integer.class);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            databaseGame.child(winningPos)
                                    .child(Player.TRICKS_KEY).setValue(currentTricksTaken[0] + 1);
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
        //TODO called from reset if the player is host
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

        mCardsDisplay = pcf;
    }


    @Override
    public void displayBidding() {
        //TODO ADD AN ACTUAL BIDDING FRAGMENT AND LOGIC
        List<Card> playerCards = mSpadesPresenter.getCards();
        setupPlayerCardsFragment(playerCards);

    }

    @Override
    public void displayGame() {

    }

    @Override
    public void setActiveCard(CardImageView civ, MotionEvent touchEvent) {
        if(civ == null){
            hideActiveCard();
        } else {
            activeCard.setLayoutParams(new SpadesGameRootLayout.LayoutParams(
                    civ.getWidth(),civ.getHeight()));
            int resID = getResources().getIdentifier(Card.determineImageName(civ.getCard()),
                    "drawable","ksmori.hu.ait.spades");
            activeCard.setImageResource(resID);
            activeCard.setCard(civ.getCard());

            Log.d(DEBUG_TAG,String.format("Active Card: x.locOG=%d, y.locOG=%d",
                    activeCardOriginalLocation[0],activeCardOriginalLocation[1]));
            int[] loc = new int[2];
            civ.getLocationOnScreen(loc);
            cardCancelLocation = Arrays.copyOf(loc,loc.length);
            activeCard.setX(loc[0] - activeCardOriginalLocation[0]);
            activeCard.setY(loc[1] - activeCardOriginalLocation[1]);

            activeCard.bringToFront();
            activeCard.requestLayout();

            // Capture the first DOWN event and pointer location
            MotionEvent subscribe = MotionEvent.obtain(touchEvent);
            subscribe.setAction(MotionEvent.ACTION_DOWN);
            activeCard.dispatchTouchEvent(subscribe);
        }
    }

    private void hideActiveCard() {
        activeCard.setImageResource(0);// invalid/none
        activeCard.setCard(null);
        SpadesGameRootLayout.LayoutParams p =
                (SpadesGameRootLayout.LayoutParams) activeCard.getLayoutParams();
        p.width = 0;
        p.height = 0;
        activeCard.setLayoutParams(p);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        String actionStr = SpadesDebug.getActionString(event);
//        Log.d(DEBUG_TAG, String.format("onTouch(%s,%s)", v.toString(), actionStr));
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
                        performCancelAnimation();
                    } else if (lastAction == MotionEvent.ACTION_MOVE){
                        Log.d(DEBUG_TAG, "ActiveCard dragUp!");
                        if(inPlayArea(event.getRawX(),event.getRawY())){
                            performPlayAnimation();
                        } else {
                            performCancelAnimation();
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

    private void performCancelAnimation() {
        float endX = cardCancelLocation[0] - activeCardOriginalLocation[0];
        float endY = cardCancelLocation[1] - activeCardOriginalLocation[1];

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(ANIM_DURATION_MILLIS);
        ObjectAnimator animTransX = ObjectAnimator.ofFloat(activeCard, "X", endX);
        ObjectAnimator animTransY = ObjectAnimator.ofFloat(activeCard, "Y", endY);
        animTransY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hideActiveCard();
                mCardsDisplay.cancelCardSelection();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                hideActiveCard();
                mCardsDisplay.cancelCardSelection();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animSet.playTogether(animTransX,animTransY); // lol
        animSet.start();
    }

    private boolean inPlayArea(float rawX, float rawY) {
        View gameView = mGameFragment.getView();
        if(gameView!=null) {
            int[] gameLoc = new int[2];
            gameView.getLocationOnScreen(gameLoc);
            int minX = gameLoc[0] + Math.round(PLAY_AREA_MIN_WIDTH_PERCENT *gameView.getWidth());
            int maxX = gameLoc[0] + Math.round(PLAY_AREA_MAX_WIDTH_PERCENT *gameView.getWidth());
            int minY = gameLoc[1] + Math.round(PLAY_AREA_MIN_HEIGHT_PERCENT*gameView.getHeight());
            int maxY = gameLoc[1] + Math.round(PLAY_AREA_MAX_HEIGHT_PERCENT*gameView.getHeight());
            Log.d(DEBUG_TAG, String.format("X validation: %d < %f < %d ?",minX, rawX, maxX));
            Log.d(DEBUG_TAG, String.format("Y validation: %d < %f < %d ?",minY, rawY, maxY));

            return (minX <= rawX && rawX <= maxX && minY <= rawY && rawY <= maxY);
        } else {
            return false;
        }
    }

    private void performPlayAnimation() {
        final ImageView viewTarget = (ImageView) mGameFragment.getView().findViewById(R.id.iv_player_card_bottom);
        int[] targetLoc = new int[2];
        viewTarget.getLocationOnScreen(targetLoc);
        final float endX = (float) targetLoc[0] - activeCardOriginalLocation[0];
        final float endY = (float) targetLoc[1] - activeCardOriginalLocation[1];
//        rootLayout.addTestPt(new PointF(endX,endY));

//        int[] currentLoc = new int[2];
//        activeCard.getLocationOnScreen(currentLoc);
//        final float startX = (float) currentLoc[0] - activeCardOriginalLocation[0];
//        final float startY = (float) currentLoc[1] - activeCardOriginalLocation[1];

        // Translation Correction from https://dannysu.com/2015/05/12/android-translate-and-scale/
        final float w1 = (float) activeCard.getMeasuredWidth();
        final float w2 = (float) viewTarget.getWidth();
        final float adjX = w1 * (1 - (w2 / w1)) / 2;

        final float h1 = (float) activeCard.getMeasuredHeight();
        final float h2 = (float) viewTarget.getHeight();
        final float adjY = h1 * (1 - (h2 / h1)) / 2;

        final float endScaleX = viewTarget.getWidth() / (float) activeCard.getMeasuredWidth();
        final float endScaleY = viewTarget.getHeight() / (float) activeCard.getMeasuredHeight();

        // ObjectAnimators from https://stackoverflow.com/questions/26024555/
        // because ViewPropertyAnimators are buggy
        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(ANIM_DURATION_MILLIS);
        ObjectAnimator animScaleX = ObjectAnimator.ofFloat(activeCard, "scaleX", endScaleX);
        ObjectAnimator animScaleY = ObjectAnimator.ofFloat(activeCard, "scaleY", endScaleY);
        ObjectAnimator animTransX = ObjectAnimator.ofFloat(activeCard, "X", endX - adjX);
        ObjectAnimator animTransY = ObjectAnimator.ofFloat(activeCard, "Y", endY - adjY);
        animTransY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //mSpadesPresenter.playCard(activeCard.getCard());
                viewTarget.setImageResource(getResources().getIdentifier(Card.determineImageName(
                        activeCard.getCard()),"drawable","ksmori.hu.ait.spades"));
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                performCancelAnimation();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animSet.playTogether(animScaleX,animScaleY,animTransX,animTransY); // lol
        animSet.start();
    }
}
