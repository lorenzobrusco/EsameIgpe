package multiPlayer.notify;

import com.jme3.scene.Node;

/**
 * 
 * this class take model's state
 *
 */

public class NotifyBoxAttack {

    /** it's true if model is attacking */
    private boolean attach;
    /** model to take care */
    private Node box;

    /** builder */
    public NotifyBoxAttack(boolean attach, Node box) {

	this.attach = attach;
	this.box = box;
    }

    /** this method get attach */
    public boolean isAttach() {
	return attach;
    }

    /** this method set attach */
    public void setAttach(boolean attach) {
	this.attach = attach;
    }

    /** this method get model */
    public Node getModel() {
	return box;
    }

    /** this method set model */
    public void setModel(Node model) {
	this.box = model;
    }

}
