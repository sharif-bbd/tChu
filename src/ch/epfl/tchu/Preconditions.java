package ch.epfl.tchu;

/**
 * @author Cecile Tangwendi Michel (324622)
 * @author Sharif Bouabid (329141)
 */
public final class Preconditions {
    /**
     * constructor
     */
    private Preconditions() {}

    /**
     *
     * @param shouldBeTrue a boolean that should be true
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }
}
