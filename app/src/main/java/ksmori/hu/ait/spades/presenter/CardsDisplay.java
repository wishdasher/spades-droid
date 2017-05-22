package ksmori.hu.ait.spades.presenter;

// We can't make this an abstract class because the PlayerCardsFragment
// needs to extend Fragment
public interface CardsDisplay {
    public void cancelCardSelection();
}
