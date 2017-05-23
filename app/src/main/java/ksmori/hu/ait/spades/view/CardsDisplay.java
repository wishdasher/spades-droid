package ksmori.hu.ait.spades.view;

import java.util.List;
import ksmori.hu.ait.spades.model.Card;

// We can't make this an abstract class because the PlayerCardsFragment
// needs to extend Fragment
public interface CardsDisplay {

    public void attachListeners();
    public void attachListeners(List<Card> playableCards);
    public void detachListeners();

    public void cancelCardSelection();

    void removeSelectedCard();
}
