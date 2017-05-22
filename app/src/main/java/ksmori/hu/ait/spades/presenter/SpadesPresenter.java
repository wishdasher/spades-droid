package ksmori.hu.ait.spades.presenter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ksmori.hu.ait.spades.SpadesGameScreen;
import ksmori.hu.ait.spades.StartActivity;
import ksmori.hu.ait.spades.WaitingRoomActivity;
import ksmori.hu.ait.spades.model.Card;
import ksmori.hu.ait.spades.model.Game;
import ksmori.hu.ait.spades.model.Play;
import ksmori.hu.ait.spades.model.Player;
import ksmori.hu.ait.spades.model.Utils;

public class SpadesPresenter extends Presenter<SpadesGameScreen> {

    private String myName;
    private String leftName;
    private String myPosition;
    private String gameID;
    private boolean isHostPlayer;
    private DatabaseReference databaseGame;
    private Map<DatabaseReference, ValueEventListener> listenerMap;
    private boolean spadesBroken;
    private Map<String, String> mapPlayerToPos;

    public SpadesPresenter(String gameID,boolean isHostPlayer){

        myName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        this.gameID = gameID;
        databaseGame = FirebaseDatabase.getInstance().getReference().child(StartActivity.GAMES_KEY).child(gameID);
        this.isHostPlayer = isHostPlayer;
        spadesBroken = false;

        DatabaseReference mapRef = databaseGame.child(Game.MAP_PLAY2POS_KEY);
        mapRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mapPlayerToPos = dataSnapshot.getValue(HashMap.class);
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
        listenerMap = new HashMap<>();
        setUpListeners();
        if (isHostPlayer) {
            //TODO WILL EVENTUALLY BE BIDDING
            databaseGame.child(Game.STATE_KEY).setValue(Game.State.PLAY);
        }
    }

    public Player getCurrentPlayer(){
        return null;
    }
    public Player getLeftPlayer(){
        return null;
    }
    public Player getTopPlayer(){
        return null;
    }
    public Player getRightPlayer(){
        return null;
    }

    public Player getBiddingPlayer(){
        return null;
    }

    // TODO TEST CODE EVENTUALLY DELETE
    public List<Card> getCards(){
        ArrayList<Card> playerCards = new ArrayList<>();
        for (int i = Card.MIN_VALUE; i <= Card.MAX_VALUE; i++) {
            playerCards.add(new Card(i, Card.Suit.SPADE));
        }
        return playerCards;
    }


    public boolean playCard(Card card){
        return false;
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
        playerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myPlayerArray[0] = dataSnapshot.getValue(Player.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
}
