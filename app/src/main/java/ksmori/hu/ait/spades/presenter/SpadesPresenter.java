package ksmori.hu.ait.spades.presenter;

import java.util.List;

import ksmori.hu.ait.spades.SpadesGameScreen;
import ksmori.hu.ait.spades.model.Card;
import ksmori.hu.ait.spades.model.Player;

public class SpadesPresenter extends Presenter<SpadesGameScreen> {
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

    public void startNewGame(){

    }

    public Player getBiddingPlayer(){
        return null;
    }

    public List<Card> getCards(Player player){
        return null;
    }


    public boolean playCard(Card card){
        return false;
    }
}
