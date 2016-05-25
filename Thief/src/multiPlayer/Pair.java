package multiPlayer;

/*
 *  class pair
 */

public class Pair<K, T> {

    /** first member */
    private final K arg0;
    /** second member */
    private final T arg1;

    /** constructor */
    public Pair(K arg0, T arg1) {
	this.arg0 = arg0;
	this.arg1 = arg1;
    }

    /** this method get arg0 */
    public K getArg0() {
	return arg0;
    }

    /** this method get arg1 */
    public T getArg1() {
	return arg1;
    }

}
