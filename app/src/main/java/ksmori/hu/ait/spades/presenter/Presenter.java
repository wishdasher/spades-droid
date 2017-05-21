package ksmori.hu.ait.spades.presenter;

/** A somewhat silly generalization of a Presenter:
 * Every Presenter must be attachable and detachable to a Screen, which is an Interface
 * that declares what a specific View can be told to do.
 * @param <S> : The Screen Interface this Presenter is bound to
 */
public abstract class Presenter<S> {
    protected S screen;

    public void attachScreen(S screen) {
        this.screen = screen;
    }

    public void detachScreen() {
        this.screen = null;
    }
}
