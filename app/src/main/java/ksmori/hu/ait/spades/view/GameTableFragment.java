package ksmori.hu.ait.spades.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Map;

import ksmori.hu.ait.spades.R;
import ksmori.hu.ait.spades.model.Card;
import ksmori.hu.ait.spades.model.Player;

public class GameTableFragment extends FragmentTagged implements GameTable{

    public static final String TAG = "GameTableFragment";
    public static final String PLAYER_POS_KEY = "PLAYER_POS_KEY";

    private static final String LEFT_KEY = "left";
    private static final String TOP_KEY = "top";
    private static final String RIGHT_KEY = "right";
    private static final String BOTTOM_KEY = "bottom";
    private View rootView;

    ImageView ivNorth, ivEast, ivSouth, ivWest;
    ImageView ivNorthToMove, ivEastToMove, ivSouthToMove, ivWestToMove;

    View boxNorth, boxEast, boxSouth, boxWest;
    private String myPosition;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game_table, container, false);

        Bundle b = this.getArguments();
//        myPosition = b.getString(PLAYER_POS_KEY); //this info isn't known at this point in runtime
        return rootView;
    }


    @Override
    public String getTAG() {
        return TAG;
    }

    private void findViewsByDirection(String northRelToMe, String eastRelToMe, String westRelToMe, String southRelToMe) {
        boxNorth = rootView.findViewById(getResources()
                .getIdentifier("player_box_"+northRelToMe,"id","ksmori.hu.ait.spades"));
        boxEast = rootView.findViewById(getResources()
                .getIdentifier("player_box_"+eastRelToMe,"id","ksmori.hu.ait.spades"));
        boxSouth = rootView.findViewById(getResources()
                .getIdentifier("player_box_"+southRelToMe,"id","ksmori.hu.ait.spades"));
        boxWest = rootView.findViewById(getResources()
                .getIdentifier("player_box_"+westRelToMe,"id","ksmori.hu.ait.spades"));

        ivNorth = (ImageView) boxNorth.findViewById(getResources().getIdentifier(
                "iv_player_card_"+northRelToMe,"id","ksmori.hu.ait.spades"));
        ivEast = (ImageView) boxEast.findViewById(getResources().getIdentifier(
                "iv_player_card_"+eastRelToMe,"id","ksmori.hu.ait.spades"));
        ivSouth = (ImageView) boxSouth.findViewById(getResources().getIdentifier(
                "iv_player_card_"+southRelToMe,"id","ksmori.hu.ait.spades"));
        ivWest = (ImageView) boxWest.findViewById(getResources().getIdentifier(
                "iv_player_card_"+westRelToMe,"id","ksmori.hu.ait.spades"));

        ivNorthToMove = (ImageView) boxNorth.findViewById(getResources().getIdentifier(
                "iv_move_indicator_"+northRelToMe,"id","ksmori.hu.ait.spades"));
        ivEastToMove = (ImageView) boxEast.findViewById(getResources().getIdentifier(
                "iv_move_indicator_"+eastRelToMe,"id","ksmori.hu.ait.spades"));
        ivSouthToMove = (ImageView) boxSouth.findViewById(getResources().getIdentifier(
                "iv_move_indicator_"+southRelToMe,"id","ksmori.hu.ait.spades"));
        ivWestToMove = (ImageView) boxWest.findViewById(getResources().getIdentifier(
                "iv_move_indicator_"+westRelToMe,"id","ksmori.hu.ait.spades"));
    }


    @Override
    public void onResume() {
        super.onResume();

        // called when view becomes visible and has its final size
        rootView.post(new Runnable() {
            @Override
            public void run() {
                final String[] positions = {"top","left","bottom","right"};
                for(String str : positions) {
                    LinearLayout layout = (LinearLayout) getView().findViewById(getResources().getIdentifier(
                            "player_box_" + str, "id", "ksmori.hu.ait.spades")
                    );
                    ImageView imageView = (ImageView) layout.findViewById(getResources().getIdentifier(
                            "iv_player_card_" + str, "id", "ksmori.hu.ait.spades")
                    );
                    LinearLayout textLayout = (LinearLayout) layout.findViewById(getResources().getIdentifier(
                            "ll_info_box_" + str, "id", "ksmori.hu.ait.spades")
                    );

                    if (textLayout.getWidth() + imageView.getWidth() != 0) {
                        layout.getLayoutParams().width = textLayout.getWidth() + imageView.getWidth()
                                + (int) getResources().getDimension(R.dimen.spacing_card_info);
                        layout.requestLayout();
                    } else {
                        Log.d("SpadesGameActivity", "Failed to resize layout: could not retrieve view dimensions");
                    }
                }
            }
        });
    }

    @Override
    public void setCurrentPlayerDir(String playerDir){
        myPosition = playerDir;
        processPlayerPosition();
    }

    private void processPlayerPosition() {

        // views are identified left/right/top in XML. Convert from east/west/north:
        String northRelToMe, eastRelToMe, westRelToMe, southRelToMe;
        switch(myPosition){
            case Player.NORTH_KEY : {
                northRelToMe = BOTTOM_KEY;
                eastRelToMe = LEFT_KEY;
                southRelToMe = TOP_KEY;
                westRelToMe = RIGHT_KEY;
                break;
            }
            case Player.EAST_KEY : {
                eastRelToMe = BOTTOM_KEY;
                southRelToMe = LEFT_KEY;
                westRelToMe = TOP_KEY;
                northRelToMe = RIGHT_KEY;
                break;
            }
            case Player.SOUTH_KEY : {
                southRelToMe = BOTTOM_KEY;
                westRelToMe = LEFT_KEY;
                northRelToMe = TOP_KEY;
                eastRelToMe = RIGHT_KEY;
                break;
            }
            case Player.WEST_KEY : {
                westRelToMe = BOTTOM_KEY;
                northRelToMe = LEFT_KEY;
                eastRelToMe = TOP_KEY;
                southRelToMe = RIGHT_KEY;
                break;
            }
            default: {
                throw new IllegalArgumentException("GameTableFragment not assigned legal Player key");
            }
        }

        findViewsByDirection(northRelToMe, eastRelToMe, westRelToMe, southRelToMe);
        // TODO update the names, bids of each player
    }

    @Override
    public void updateNorthCard(Card northCard) {
        ivNorth.setImageResource(getResources().getIdentifier(
                Card.determineImageName(northCard),"drawable","ksmori.hu.ait.spades"));
        ivNorth.requestLayout();
        // TODO fix the initial on/off move indicator settings. The below operations are
        // fine, after Trick 1.
        ivNorthToMove.setImageResource(R.drawable.move_indicator_off);
        ivEastToMove.setImageResource(R.drawable.move_indicator_on);
    }

    @Override
    public void updateEastCard(Card eastCard) {
        ivEast.setImageResource(getResources().getIdentifier(
                Card.determineImageName(eastCard),"drawable","ksmori.hu.ait.spades"));
        ivEastToMove.setImageResource(R.drawable.move_indicator_off);
        ivSouthToMove.setImageResource(R.drawable.move_indicator_on);
    }

    @Override
    public void updateSouthCard(Card southCard) {
        ivSouth.setImageResource(getResources().getIdentifier(
                Card.determineImageName(southCard),"drawable","ksmori.hu.ait.spades"));
        ivSouthToMove.setImageResource(R.drawable.move_indicator_off);
        ivWestToMove.setImageResource(R.drawable.move_indicator_on);
    }

    @Override
    public void updateWestCard(Card westCard) {
        ivWest.setImageResource(getResources().getIdentifier(
                Card.determineImageName(westCard),"drawable","ksmori.hu.ait.spades"));
        ivWestToMove.setImageResource(R.drawable.move_indicator_off);
        ivNorthToMove.setImageResource(R.drawable.move_indicator_on);
    }
}
