package multiPlayer;

import singlePlayer.model.NodeCharacter;

public class NotifyStateModel {

    private boolean attach;
    private NodeCharacter model;

    public NotifyStateModel(boolean attach, NodeCharacter model) {

	this.attach = attach;
	this.model = model;
    }

    public boolean isAttach() {
	return attach;
    }

    public void setAttach(boolean attach) {
	this.attach = attach;
    }

    public NodeCharacter getModel() {
	return model;
    }

    public void setModel(NodeCharacter model) {
	this.model = model;
    }

}
