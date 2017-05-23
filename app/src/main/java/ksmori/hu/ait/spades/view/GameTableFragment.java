package ksmori.hu.ait.spades.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
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
    public static final String HASHMAP_KEY = "HASHMAP_KEY";
    private View rootView;

    ImageView ivNorth, ivEast, ivSouth, ivWest;
    ImageView ivNorthToMove, ivEastToMove, ivSouthToMove, ivWestToMove;

    View boxNorth, boxEast, boxSouth, boxWest;
    private Map<String, String> mapPlayerToPos;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game_table, container, false);

        Bundle b = this.getArguments();
        if(b.getSerializable(HASHMAP_KEY) != null)
            mapPlayerToPos = (Map<String,String>)b.getSerializable(HASHMAP_KEY);
        return rootView;
    }


    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

//        boxNorth = rootView.findViewById(getResources()
//                .getIdentifier("player_box_"+mapPlayerToPos.get(Player.TOP_KEY),"id","ksmori.hu.ait.spades"));
//        boxEast = rootView.findViewById(getResources()
//                .getIdentifier("player_box_"+mapPlayerToPos.get(Player.EAST_KEY),"id","ksmori.hu.ait.spades"));
//        boxSouth = rootView.findViewById(getResources()
//                .getIdentifier("player_box_"+mapPlayerToPos.get(Player.SOUTH_KEY),"id","ksmori.hu.ait.spades"));
//        boxWest = rootView.findViewById(getResources()
//                .getIdentifier("player_box_"+mapPlayerToPos.get(Player.WEST_KEY),"id","ksmori.hu.ait.spades"));
//
//        ivNorth = (ImageView) boxNorth.findViewById(getResources().getIdentifier(
//                "iv_player_card_"+mapPlayerToPos.get(Player.NORTH_KEY),"id","ksmori.hu.ait.spades"));
//        ivEast = (ImageView) boxEast.findViewById(getResources().getIdentifier(
//                "iv_player_card_"+mapPlayerToPos.get(Player.EAST_KEY),"id","ksmori.hu.ait.spades"));
//        ivSouth = (ImageView) boxSouth.findViewById(getResources().getIdentifier(
//                "iv_player_card_"+mapPlayerToPos.get(Player.SOUTH_KEY),"id","ksmori.hu.ait.spades"));
//        ivWest = (ImageView) boxWest.findViewById(getResources().getIdentifier(
//                "iv_player_card_"+mapPlayerToPos.get(Player.WEST_KEY),"id","ksmori.hu.ait.spades"));
//
//        ivNorthToMove = (ImageView) boxNorth.findViewById(getResources().getIdentifier(
//                "iv_move_indicator_"+mapPlayerToPos.get(Player.NORTH_KEY),"id","ksmori.hu.ait.spades"));
//        ivEastToMove = (ImageView) boxEast.findViewById(getResources().getIdentifier(
//                "iv_move_indicator_"+mapPlayerToPos.get(Player.EAST_KEY),"id","ksmori.hu.ait.spades"));
//        ivSouthToMove = (ImageView) boxSouth.findViewById(getResources().getIdentifier(
//                "iv_move_indicator_"+mapPlayerToPos.get(Player.SOUTH_KEY),"id","ksmori.hu.ait.spades"));
//        ivWestToMove = (ImageView) boxWest.findViewById(getResources().getIdentifier(
//                "iv_move_indicator_"+mapPlayerToPos.get(Player.WEST_KEY),"id","ksmori.hu.ait.spades"));
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
    public void setMapPlayerToPos(Map<String, String> mapPlayerToPos) {
        this.mapPlayerToPos = mapPlayerToPos;
    }

    @Override
    public void updateNorthCard(Card northCard) {
//        ivNorth.setImageResource(getResources().getIdentifier(
//                Card.determineImageName(northCard),"id","ksmori.hu.ait.spades"));
//        ivNorthToMove.setImageResource(R.drawable.move_indicator_off);
//        ivEastToMove.setImageResource(R.drawable.move_indicator_on);
    }

    @Override
    public void updateEastCard(Card eastCard) {
//                ivEast.setImageResource(getResources().getIdentifier(
//                        Card.determineImageName(eastCard),"id","ksmori.hu.ait.spades"));
//                ivEastToMove.setImageResource(R.drawable.move_indicator_off);
//                ivSouthToMove.setImageResource(R.drawable.move_indicator_on);
    }

    @Override
    public void updateSouthCard(Card southCard) {
//        ivSouth.setImageResource(getResources().getIdentifier(
//                Card.determineImageName(southCard),"id","ksmori.hu.ait.spades"));
//        ivSouthToMove.setImageResource(R.drawable.move_indicator_off);
//        ivWestToMove.setImageResource(R.drawable.move_indicator_on);
    }

    @Override
    public void updateWestCard(Card westCard) {
//        ivWest.setImageResource(getResources().getIdentifier(
//                Card.determineImageName(westCard),"id","ksmori.hu.ait.spades"));
//        ivWestToMove.setImageResource(R.drawable.move_indicator_off);
//        ivNorthToMove.setImageResource(R.drawable.move_indicator_on);
    }
}
