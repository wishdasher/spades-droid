package ksmori.hu.ait.spades.view;

import java.util.Map;

import ksmori.hu.ait.spades.model.Card;

public interface GameTable {

    void setCurrentPlayerDir(String playerDir);

    void updateNorthCard(Card northCard);

    void updateEastCard(Card eastCard);

    void updateSouthCard(Card southCard);

    void updateWestCard(Card westCard);
}
