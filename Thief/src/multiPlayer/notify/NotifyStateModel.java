package multiPlayer.notify;

import singlePlayer.model.NodeCharacter;

/**
 * 
 * this class take model's state
 *
 */
public class NotifyStateModel {

    /** it's true if model is attacking */
    private boolean attach;
    /** model to take care */
    private NodeCharacter model;

    /** builder */
    public NotifyStateModel(boolean attach, NodeCharacter model) {

	this.attach = attach;
	this.model = model;
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
    public NodeCharacter getModel() {
	return model;
    }

    /** this method set model */
    public void setModel(NodeCharacter model) {
	this.model = model;
    }

}
