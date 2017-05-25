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
import com.google.firebase.database.ChildEventListener;
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
import ksmori.hu.ait.spades.model.Game;
import ksmori.hu.ait.spades.model.GameVariable;
import ksmori.hu.ait.spades.model.Play;
import ksmori.hu.ait.spades.model.Player;
import ksmori.hu.ait.spades.model.Utils;
import ksmori.hu.ait.spades.view.CardsDisplay;
import ksmori.hu.ait.spades.presenter.SpadesPresenter;
import ksmori.hu.ait.spades.util.SpadesDebug;
import ksmori.hu.ait.spades.view.CardImageView;
import ksmori.hu.ait.spades.view.GameTable;
import ksmori.hu.ait.spades.view.GameTableFragment;
import ksmori.hu.ait.spades.view.PlayerCardsFragment;
import ksmori.hu.ait.spades.view.SpadesGameRootLayout;

public class SpadesGameActivity extends AppCompatActivity implements SpadesGameScreen,
        View.OnTouchListener{

    private static final String SPADES_GAME_ACTIVITY_TAG = "SpadesGameActivityTag";
    private static final long ANIM_BASE_DURATION_MILLIS = 250;
    private static final long ANIM_VELOCITY_DP_PER_MILLI = 2;
    private static final float PLAY_AREA_MIN_WIDTH_PERCENT = 0.25f;
    private static final float PLAY_AREA_MAX_WIDTH_PERCENT = 0.75f;
    private static final float PLAY_AREA_MIN_HEIGHT_PERCENT = 0.34f;
    private static final float PLAY_AREA_MAX_HEIGHT_PERCENT = 0.95f;
    private static final float CARD_SELECT_SCALE_FACTOR = 1.2f;
    public static final float SLOP_RADIUS_PERCENT = 3f;

    private SpadesPresenter mSpadesPresenter;
    private GameTable mGameTable;
    private CardsDisplay mCardsDisplay;

    private CardImageView activeCard;
    private int[] activeCardOriginalLocation;
    private int[] cardCancelLocation;
    private float dX;
    private float dY;
    private int lastAction;
    public boolean isTouchable = true;

    //From Intent
    private String myName;
    private String gameID;
    private boolean isHostPlayer;

    //Game variables - static
    private String myPosition; // NORTH EAST SOUTH OR WEST
    private String leftName;
    private Map<String, String> mapPlayerToPos = new HashMap<>();

    //Game variables - dynamic
    private Game.State gameState;
    private int roundNumber;
    private int trickNumber;
    private boolean spadesBroken = false;
    private Card.Suit currentSuit;
    private String lastPlayer;
    private String nextPlayer;

    private Map<String, Integer> mapPositionToTricks = new HashMap<>();

    private List<Card> myHand = new ArrayList<>();
    private List<Play> plays = new ArrayList<>();

    private Card northCard;
    private Card eastCard;
    private Card southCard;
    private Card westCard;

    //Utils
    private DatabaseReference databaseGame;
    private Map<DatabaseReference, ValueEventListener> listenerMap = new HashMap<>();

    private Card myCardToPlay;
    private GameTableFragment mGameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spades_game);

        myName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        gameID = getIntent().getStringExtra(WaitingRoomActivity.GAME_ID_INTENT_KEY);
        databaseGame = FirebaseDatabase.getInstance().getReference().child(StartActivity.GAMES_KEY).child(gameID);
        isHostPlayer = getIntent().getBooleanExtra(WaitingRoomActivity.HOST_PLAYER_INTENT_KEY, false);

        activeCard = (CardImageView) findViewById(R.id.iv_active_card);
        activeCard.setOnTouchListener(SpadesGameActivity.this);
        //rootLayout = (SpadesGameRootLayout) findViewById(R.id.layout_root_game_activity);
        activeCard.bringToFront();

        setupGameTableFragment(); // can't wait to setup on listen

        // 1. Get Players
        DatabaseReference mapRef = databaseGame.child(Game.PLAYERS_KEY);
        mapRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    mapPlayerToPos.put(child.getKey(), child.getValue(String.class));
                } // 2. Get mapping between player names and positions
                myPosition = mapPlayerToPos.get(myName);
                mGameTable.setCurrentPlayerDir(myPosition);
                DatabaseReference leftRef = databaseGame.child(myPosition).child(Player.LEFT_KEY);
                leftRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        leftName = dataSnapshot.getValue(String.class);
                        // GET INITIAL CARDS
                        DatabaseReference cardsRef = databaseGame.child(myPosition).child(Player.HAND_KEY);
                        cardsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    myHand.add(child.getValue(Card.class));
                                } // 4. Once cards are obtained, show Card fragments
                                setupPlayerCardsFragment();

                                mSpadesPresenter = new SpadesPresenter(gameID, isHostPlayer);

                                if (isHostPlayer) {
                                    // EVENTUALLY BE BIDDING
                                    databaseGame.child(GameVariable.KEY).child(GameVariable.STATE_KEY).setValue(Game.State.PLAY);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(SPADES_GAME_ACTIVITY_TAG, databaseError.getMessage());
                            }
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(SPADES_GAME_ACTIVITY_TAG, databaseError.getMessage());
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(SPADES_GAME_ACTIVITY_TAG, databaseError.getMessage());
            }
        });



    }

    private void setUpListeners() {
        setUpTrickValueListeners();
        setUpPlayedCardListeners();
        setUpGameVariableListeners();
        setUpPlayListener();
    }

    private void setUpTrickValueListeners() {
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
    }

    private void setUpPlayedCardListeners() {
        final DatabaseReference northCardRef = databaseGame.child(Player.NORTH_KEY).child(Player.CARD_KEY);
        northCardRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                northCard = dataSnapshot.getValue(Card.class);
                if(northCard!=null) { //TODO figure out why this would be triggered for null values
                    mGameTable.updateNorthCard(northCard);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        DatabaseReference eastCardRef = databaseGame.child(Player.EAST_KEY).child(Player.CARD_KEY);
        eastCardRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eastCard = dataSnapshot.getValue(Card.class);
                if(eastCard!=null) {
                    mGameTable.updateEastCard(eastCard);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        final DatabaseReference southCardRef = databaseGame.child(Player.SOUTH_KEY).child(Player.CARD_KEY);
        southCardRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                southCard = dataSnapshot.getValue(Card.class);
                if(southCard != null) {
                    mGameTable.updateSouthCard(southCard);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        final DatabaseReference westCardRef = databaseGame.child(Player.WEST_KEY).child(Player.CARD_KEY);
        westCardRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                westCard = dataSnapshot.getValue(Card.class);
                if(westCard!=null) {
                    mGameTable.updateWestCard(westCard);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setUpGameVariableListeners() {
        databaseGame.child(GameVariable.KEY).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                switch (key) {
                    case GameVariable.CURRENT_SUIT_KEY:
                        currentSuit = Card.Suit.valueOf(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                switch (key) {
                    case GameVariable.STATE_KEY:
                        gameState = Game.State.valueOf(dataSnapshot.getValue(String.class));
                        if (gameState == Game.State.RESET && isHostPlayer) {
                            setUpNextGame();
                        }
                        break;
                    case GameVariable.ROUND_KEY:
                        roundNumber = dataSnapshot.getValue(Integer.class);
                        break;
                    case GameVariable.TRICK_NUMBER_KEY:
                        trickNumber = dataSnapshot.getValue(Integer.class);
                        break;
                    case GameVariable.SPADES_BROKEN_KEY:
                        spadesBroken = dataSnapshot.getValue(Boolean.class);
                        break;
                    case GameVariable.CURRENT_SUIT_KEY:
                        currentSuit = Card.Suit.valueOf(dataSnapshot.getValue(String.class));
                        break;
                    case GameVariable.NEXT_PLAYER_KEY:
                        nextPlayer = dataSnapshot.getValue(String.class);
                        if (nextPlayer.equals(myName)) {
                            enablePlayUI();
                            makeMove();
                        } else {
                            disablePlayUI();
                        }
                        break;

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void enablePlayUI() {
        mCardsDisplay.attachListeners(Utils.getPlayableHand(myHand, currentSuit, spadesBroken));
    }

    private void disablePlayUI(){
        mCardsDisplay.detachListeners();
    }

    private void setUpPlayListener() {
        DatabaseReference playsRef = databaseGame.child(Game.PLAYS_KEY);
        playsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Play newPlay = dataSnapshot.getValue(Play.class);
                plays.add(newPlay);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                plays.clear();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void makeMove() {
        if (trickNumber > Game.NUM_TRICKS) {
            //TODO GAME END LOGIC
            databaseGame.child(GameVariable.KEY).child(GameVariable.STATE_KEY).setValue(Game.State.RESET);
        } else {
            Play myPlay = playCard();
            plays.add(myPlay);
            databaseGame.child(myPosition).child(Player.CARD_KEY).setValue(myPlay.getCard());

            if (plays.size() == Game.NUM_PLAYERS) {
                // TRICK IS OVER
                Play winningPlay = Utils.getWinningPlay(plays);
                if (!spadesBroken && winningPlay.getCard().getSuitValue() == Card.Suit.SPADE) {
                    spadesBroken = true;
                    databaseGame.child(GameVariable.KEY).child(GameVariable.SPADES_BROKEN_KEY).setValue(true);
                }
                databaseGame.child(GameVariable.KEY).child(GameVariable.LAST_PLAYER_KEY).setValue(myName);
                databaseGame.child(GameVariable.KEY).child(GameVariable.NEXT_PLAYER_KEY).setValue(winningPlay.getPlayer());
                String winningPos = mapPlayerToPos.get(winningPlay.getPlayer());
                databaseGame.child(winningPos)
                        .child(Player.TRICKS_KEY).setValue(mapPositionToTricks.get(winningPos) + 1);
                databaseGame.child(Game.PLAYS_KEY).setValue(new ArrayList<>());
                databaseGame.child(Game.NORTH_KEY).child(Player.CARD_KEY).setValue(null);
                databaseGame.child(Game.EAST_KEY).child(Player.CARD_KEY).setValue(null);
                databaseGame.child(Game.SOUTH_KEY).child(Player.CARD_KEY).setValue(null);
                databaseGame.child(Game.WEST_KEY).child(Player.CARD_KEY).setValue(null);
                databaseGame.child(GameVariable.KEY).child(GameVariable.CURRENT_SUIT_KEY).setValue(null);
                databaseGame.child(GameVariable.KEY).child(GameVariable.TRICK_NUMBER_KEY).setValue(trickNumber + 1);
            } else {
                // TRICK CONTINUES
                if (plays.size() == 1) {
                    databaseGame.child(GameVariable.KEY).child(GameVariable.CURRENT_SUIT_KEY).setValue(myPlay.getCard().getSuit());
                }
                databaseGame.child(Game.PLAYS_KEY).setValue(plays);
                databaseGame.child(GameVariable.KEY).child(GameVariable.NEXT_PLAYER_KEY).setValue(leftName);
            }
        }
    }

    private void setUpNextGame() {
        //TODO REDEAL
        roundNumber++;
        databaseGame.child(GameVariable.KEY).child(GameVariable.ROUND_KEY).setValue(roundNumber);
        databaseGame.child(GameVariable.KEY).child(GameVariable.TRICK_NUMBER_KEY).setValue(1);
        databaseGame.child(GameVariable.KEY).child(GameVariable.SPADES_BROKEN_KEY).setValue(false);
        databaseGame.child(GameVariable.KEY).child(GameVariable.CURRENT_SUIT_KEY).setValue(null);
        databaseGame.child(GameVariable.KEY).child(GameVariable.NEXT_PLAYER_KEY).setValue(leftName);
        databaseGame.child(Game.PLAYS_KEY).setValue(new ArrayList<>());
    }

    private Play playCard() {
        return new Play(myName,myCardToPlay);
    }

    public void setupGameTableFragment() {
        GameTableFragment gtf = new GameTableFragment();
        Bundle argBundle = new Bundle();
//        argBundle.putSerializable(GameTableFragment.HASHMAP_KEY,(Serializable) mapPlayerToPos);
//        argBundle.putString(GameTableFragment.PLAYER_POS_KEY,myPosition);
        gtf.setArguments(argBundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fl_game_table_container, gtf,GameTableFragment.TAG);
        ft.commit();
    }

    private void setupPlayerCardsFragment() {
        PlayerCardsFragment pcf = new PlayerCardsFragment();
        Bundle argBundle = new Bundle();
        argBundle.putSerializable(PlayerCardsFragment.CARDS_KEY,(Serializable) myHand);
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
        setupPlayerCardsFragment();

    }

    @Override
    public void displayGame() {

    }

    @Override
    public void setActiveCard(CardImageView civ) {
        if(activeCard.getCard()!=null){
//            throw new IllegalArgumentException("Attempting to override an unfinished card action");
            mCardsDisplay.cancelCardSelection();
            performCancelAnimation();
        }
        if(civ == null){
            hideActiveCard();
        } else {
            activeCard.setScaleX(1f);
            activeCard.setScaleY(1f);
            SpadesGameRootLayout.LayoutParams layoutParams = (SpadesGameRootLayout.LayoutParams) activeCard.getLayoutParams();
            layoutParams.width = Math.round(CARD_SELECT_SCALE_FACTOR*civ.getWidth());
            layoutParams.height =  Math.round(CARD_SELECT_SCALE_FACTOR*civ.getHeight());
            int resID = getResources().getIdentifier(Card.determineImageName(civ.getCard()),
                    "drawable","ksmori.hu.ait.spades");
            activeCard.setImageResource(resID);
            activeCard.setCard(civ.getCard());

            Log.d(SPADES_GAME_ACTIVITY_TAG,String.format("Active Card: x.locOG=%d, y.locOG=%d",
                    activeCardOriginalLocation[0],activeCardOriginalLocation[1]));
            int[] loc = new int[2];
            civ.getLocationOnScreen(loc);
            cardCancelLocation = Arrays.copyOf(loc,loc.length);
            activeCard.setX(loc[0] - activeCardOriginalLocation[0]
                    - Math.round((CARD_SELECT_SCALE_FACTOR - 1f)/2f *civ.getWidth()));
            activeCard.setY(loc[1] - activeCardOriginalLocation[1]
                    - Math.round((CARD_SELECT_SCALE_FACTOR - 1f)/2f*civ.getHeight()));

            activeCard.bringToFront();
            activeCard.requestLayout();
            lastAction = MotionEvent.ACTION_CANCEL;
        }
    }

    @Override
    public CardImageView getActiveCard() {
        return activeCard;
    }

    @Override
    public boolean getIsTouchable() {
        return isTouchable;
    }

    @Override
    public void setIsTouchable(boolean isTouchable) {
        this.isTouchable = isTouchable;
    }

    @Override
    public void setLastAction(int motionEventAction) {
        lastAction = motionEventAction;
    }

    private void hideActiveCard() {
        activeCard.setImageDrawable(null);
        activeCard.setCard(null);
        SpadesGameRootLayout.LayoutParams p =
                (SpadesGameRootLayout.LayoutParams) activeCard.getLayoutParams();
        p.width = 0;
        p.height = 0;
        activeCard.setLayoutParams(p);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        String actionStr = SpadesDebug.getActionString(event);
        Log.d(SPADES_GAME_ACTIVITY_TAG, String.format("onTouch(%s,%s)", v.toString(), actionStr));
        if(event.getActionMasked() == MotionEvent.ACTION_CANCEL){
            return true; // no need to check all children for CANCEL to
        } else if(isTouchable && v.getId() == R.id.iv_active_card && isForActiveCard(event)){
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(SPADES_GAME_ACTIVITY_TAG, "ActiveCard DOWN!");
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
                    isTouchable = false; //prohibit interruptions until touch handling finishes
                    if (lastAction == MotionEvent.ACTION_MOVE) {
                        Log.d(SPADES_GAME_ACTIVITY_TAG, "ActiveCard dragUp!");
                        if (inPlayArea(event.getRawX(), event.getRawY())) {
                            performPlayAnimation();
                        } else {
                            performCancelAnimation();
                        }
                    } else if (lastAction == MotionEvent.ACTION_DOWN){
                        Log.d(SPADES_GAME_ACTIVITY_TAG, "ActiveCard clickUp!");
                        performCancelAnimation();
                    }
                    return true;

                default:
                    return false;
            }
            return true;
        } else if(v instanceof CardImageView && v.getId() != R.id.iv_active_card
                && event.getActionMasked()!=MotionEvent.ACTION_UP){
                // only let the subservient PlayerCardsFragment respond to ACTION_DOWN and MOVE
            return true;
        }
        return false;
    }

    private void performCancelAnimation() {
        isTouchable = false; // busy processing animation
        float endX = cardCancelLocation[0] - activeCardOriginalLocation[0];
        float endY = cardCancelLocation[1] - activeCardOriginalLocation[1];

        final float w1 = (float) activeCard.getMeasuredWidth();
        final float w2 = w1 / CARD_SELECT_SCALE_FACTOR;
        final float adjX = w1 * (1 - (w2 / w1)) / 2;

        final float h1 = (float) activeCard.getMeasuredHeight();
        final float h2 = h1 / CARD_SELECT_SCALE_FACTOR;
        final float adjY = h1 * (1 - (h2 / h1)) / 2;

        // ObjectAnimators from https://stackoverflow.com/questions/26024555/
        // because ViewPropertyAnimators are buggy
        AnimatorSet animSet = new AnimatorSet();
        float vel = ANIM_VELOCITY_DP_PER_MILLI /(float) (getResources().getDisplayMetrics().densityDpi/160);
        double dist = Math.sqrt((activeCard.getX() - endX + adjX)*(activeCard.getX() - endX + adjX)
                        +(activeCard.getY() - endY + adjY)*(activeCard.getY() - endY + adjY));
        animSet.setDuration(Math.round(dist / vel)+ANIM_BASE_DURATION_MILLIS);
        ObjectAnimator animScaleX = ObjectAnimator.ofFloat(activeCard, "scaleX", 1f/CARD_SELECT_SCALE_FACTOR);
        ObjectAnimator animScaleY = ObjectAnimator.ofFloat(activeCard, "scaleY", 1f/CARD_SELECT_SCALE_FACTOR);
        ObjectAnimator animTransX = ObjectAnimator.ofFloat(activeCard, "X", endX - adjX);
        ObjectAnimator animTransY = ObjectAnimator.ofFloat(activeCard, "Y", endY - adjY);
        animTransY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mCardsDisplay.cancelCardSelection();
                hideActiveCard();
                isTouchable = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                performCancelAnimation(); //try again until it succeeds, I guess
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animSet.playTogether(animScaleX,animScaleY,animTransX,animTransY);
        animSet.start();
    }

    private void performPlayAnimation() {
        isTouchable = false;
        final ImageView viewTarget = (ImageView) mGameFragment.getView()
                .findViewById(R.id.iv_player_card_bottom);
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

        // ObjectAnimators from https://stackoverflow.com/questions/26024555/
        // because ViewPropertyAnimators are buggy
        AnimatorSet animSet = new AnimatorSet();
        float vel = ANIM_VELOCITY_DP_PER_MILLI /(float) (getResources().getDisplayMetrics().densityDpi/160);
        double dist = Math.sqrt((activeCard.getX() - endX + adjX)*(activeCard.getX() - endX + adjX)
                +(activeCard.getY() - endY + adjY)*(activeCard.getY() - endY + adjY));
        animSet.setDuration(Math.round(dist / vel)+ANIM_BASE_DURATION_MILLIS);
        ObjectAnimator animScaleX = ObjectAnimator.ofFloat(activeCard, "scaleX", w2/w1);
        ObjectAnimator animScaleY = ObjectAnimator.ofFloat(activeCard, "scaleY", h2/h1);
        ObjectAnimator animTransX = ObjectAnimator.ofFloat(activeCard, "X", endX - adjX);
        ObjectAnimator animTransY = ObjectAnimator.ofFloat(activeCard, "Y", endY - adjY);
        animTransY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                myCardToPlay = activeCard.getCard();
                viewTarget.setImageResource(getResources().getIdentifier(Card.determineImageName(
                        activeCard.getCard()),"drawable","ksmori.hu.ait.spades"));
                mCardsDisplay.removeSelectedCard();
                hideActiveCard();
                makeMove();
                isTouchable = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                performCancelAnimation();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animSet.playTogether(animScaleX,animScaleY,animTransX,animTransY); // lol play together
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
            Log.d(SPADES_GAME_ACTIVITY_TAG, String.format("X validation: %d < %f < %d ?",minX, rawX, maxX));
            Log.d(SPADES_GAME_ACTIVITY_TAG, String.format("Y validation: %d < %f < %d ?",minY, rawY, maxY));

            return (minX <= rawX && rawX <= maxX && minY <= rawY && rawY <= maxY);
        } else {
            return false;
        }
    }

    public boolean isForActiveCard(MotionEvent ev) {
        int[] loc = new int[2];
        activeCard.getLocationOnScreen(loc);
        return loc[0] - SLOP_RADIUS_PERCENT * activeCard.getWidth() <= ev.getRawX()
                && ev.getRawX() <= loc[0] + (1f + SLOP_RADIUS_PERCENT) * activeCard.getWidth()
                && loc[1] - SLOP_RADIUS_PERCENT * activeCard.getHeight() <= ev.getRawY()
                && ev.getRawY() <= loc[1] + (1f + SLOP_RADIUS_PERCENT) * activeCard.getHeight();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameFragment = (GameTableFragment) getSupportFragmentManager()
                    .findFragmentByTag(GameTableFragment.TAG);
        mGameTable = (GameTable) mGameFragment;

        if(activeCardOriginalLocation == null) {
            activeCardOriginalLocation = new int[2];
            activeCard.getLocationOnScreen(activeCardOriginalLocation); // mutator method
        }

        setUpListeners();

    }
}
