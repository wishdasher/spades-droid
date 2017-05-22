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
import android.view.ViewGroup;
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
import ksmori.hu.ait.spades.util.SpadesDebug;
import ksmori.hu.ait.spades.view.CardImageView;
import ksmori.hu.ait.spades.view.GameTableFragment;
import ksmori.hu.ait.spades.view.PlayerCardsFragment;
import ksmori.hu.ait.spades.view.SpadesGameRootLayout;

public class SpadesGameActivity extends AppCompatActivity implements SpadesGameScreen,
        View.OnTouchListener{

    private static final String DEBUG_TAG = "SpadesGameActivity";
//    private static final long ANIM_DURATION_MILLIS = 750;
    private static final long ANIM_VELOCITY_DP_PER_MILLI = 2;
    private static final float PLAY_AREA_MIN_WIDTH_PERCENT = 0.25f;
    private static final float PLAY_AREA_MAX_WIDTH_PERCENT = 0.75f;
    private static final float PLAY_AREA_MIN_HEIGHT_PERCENT = 0.34f;
    private static final float PLAY_AREA_MAX_HEIGHT_PERCENT = 0.95f;
    private static final float CARD_SELECT_SCALE_FACTOR = 1.2f;
    public static final float SLOP_RADIUS_PERCENT = 3f;
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
    public boolean isTouchable = true;

    private String myName;
    private String leftName;
    private String myPosition = "south";
    private String gameID;
    private boolean isHostPlayer;
    private DatabaseReference databaseGame;
    private Map<DatabaseReference, ValueEventListener> listenerMap;
    private boolean spadesBroken;
    private Map<String, String> mapPlayerToPos;

    private Player north;
    private Player east;
    private Player south;
    private Player west;
    private Player mePlayer;

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

//        final List<String> playerNames = new ArrayList<>();
//        DatabaseReference playersRef = databaseGame.child(StartActivity.PLAYERS_KEY);
//        playersRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    playerNames.add(child.getValue(String.class));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        DatabaseReference ref1 = databaseGame.child(playerNames.get(0));
//        ref1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    north = dataSnapshot.getValue(Player.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        DatabaseReference ref2 = databaseGame.child(playerNames.get(1));
//        ref2.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    east = dataSnapshot.getValue(Player.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        DatabaseReference ref3 = databaseGame.child(playerNames.get(2));
//        ref3.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    south = dataSnapshot.getValue(Player.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        DatabaseReference ref4 = databaseGame.child(playerNames.get(3));
//        ref4.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    west = dataSnapshot.getValue(Player.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        if (myName.equals(north.getName())) {
//            myPosition = Player.NORTH_KEY;
//            mePlayer = north;
//        } else if (myName.equals(east.getName())) {
//            myPosition = Player.EAST_KEY;
//            mePlayer = east;
//        } else if (myName.equals(south.getName())) {
//            myPosition = Player.SOUTH_KEY;
//            mePlayer = south;
//        } else if (myName.equals(west.getName())) {
//            myPosition = Player.WEST_KEY;
//            mePlayer = west;
//        }

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

        Deck deck = new Deck();
        List<ArrayList<Card>> hands = deck.deal(Game.NUM_PLAYERS);
        playerCards = hands.get(0);
        setupPlayerCardsFragment();

//        String gameID = getIntent().getStringExtra(WaitingRoomActivity.GAME_ID_INTENT_KEY);
//        boolean isHost = getIntent().getBooleanExtra(WaitingRoomActivity.HOST_PLAYER_INTENT_KEY, false);
//        mSpadesPresenter = new SpadesPresenter(gameID,isHost);
        setupGameTableFragment();

        activeCard = (CardImageView) findViewById(R.id.iv_active_card);
        activeCard.setOnTouchListener(this);
        rootLayout = (SpadesGameRootLayout) findViewById(R.id.layout_root_game_activity);
        activeCard.bringToFront();

        DatabaseReference westPlayerRef = databaseGame.child(Player.WEST_KEY).child(Player.CARD_KEY);
        westPlayerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String card = dataSnapshot.getValue(String.class);
                ViewGroup left = (ViewGroup) mGameFragment.getView().findViewById(R.id.player_box_left);
                ImageView iv = (ImageView) left.findViewById(R.id.iv_player_card_left);
                int resID = getResources().getIdentifier(card,
                        "drawable","ksmori.hu.ait.spades");
                iv.setImageResource(resID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(activeCardOriginalLocation == null) {
            activeCardOriginalLocation = new int[2];
            activeCard.getLocationOnScreen(activeCardOriginalLocation); // mutator method
        }
    }

    private void setUpListeners() {
        DatabaseReference westPlayerRef = databaseGame.child(Player.WEST_KEY).child(Player.CARD_KEY);
        westPlayerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String card = dataSnapshot.getValue(String.class);
                ViewGroup left = (ViewGroup) mGameFragment.getView().findViewById(R.id.player_box_left);
                ImageView iv = (ImageView) left.findViewById(R.id.iv_player_card_left);
                int resID = getResources().getIdentifier(card,
                        "drawable","ksmori.hu.ait.spades");
                iv.setImageResource(resID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    private void setupPlayerCardsFragment() {
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
        playerCards = mSpadesPresenter.getCards();
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

            Log.d(DEBUG_TAG,String.format("Active Card: x.locOG=%d, y.locOG=%d",
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
        Log.d(DEBUG_TAG, String.format("onTouch(%s,%s)", v.toString(), actionStr));
        if(event.getActionMasked() == MotionEvent.ACTION_CANCEL){
            return true; // no need to check all children for CANCEL to
        } else if(isTouchable && v.getId() == R.id.iv_active_card && isForActiveCard(event)){
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
                    isTouchable = false; //prohibit interruptions until touch handling finishes
                    if (lastAction == MotionEvent.ACTION_MOVE) {
                        Log.d(DEBUG_TAG, "ActiveCard dragUp!");
                        if (inPlayArea(event.getRawX(), event.getRawY())) {
                            performPlayAnimation();
                        } else {
                            performCancelAnimation();
                        }
                    } else if (lastAction == MotionEvent.ACTION_DOWN){
                        Log.d(DEBUG_TAG, "ActiveCard clickUp!");
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
        animSet.setDuration(Math.round(dist / vel)+250);
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
        animSet.setDuration(Math.round(dist / vel));
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
                //mSpadesPresenter.playCard(activeCard.getCard());
                viewTarget.setImageResource(getResources().getIdentifier(Card.determineImageName(
                        activeCard.getCard()),"drawable","ksmori.hu.ait.spades"));
                mCardsDisplay.removeSelectedCard();
                hideActiveCard();
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
        animSet.playTogether(animScaleX,animScaleY,animTransX,animTransY); // lol
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

    public boolean isForActiveCard(MotionEvent ev) {
        int[] loc = new int[2];
        activeCard.getLocationOnScreen(loc);
        return loc[0] - SLOP_RADIUS_PERCENT * activeCard.getWidth() <= ev.getRawX()
                && ev.getRawX() <= loc[0] + (1f + SLOP_RADIUS_PERCENT) * activeCard.getWidth()
                && loc[1] - SLOP_RADIUS_PERCENT * activeCard.getHeight() <= ev.getRawY()
                && ev.getRawY() <= loc[1] + (1f + SLOP_RADIUS_PERCENT) * activeCard.getHeight();
    }
}
