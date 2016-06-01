package control;

import com.jme3.terrain.geomipmap.TerrainQuad;
import singlePlayer.model.NodeCharacter;
import singlePlayer.model.NodeEnemy;
import singlePlayer.model.NodeModel;
import singlePlayer.model.NodeThief;

/**
 * 
 * This class set the minimum distance to render starting of the character's
 * position
 * 
 */
public class GameRender {

    /** The main land */
    private TerrainQuad terrain;
    /** main character */
    private NodeThief thief;
    /** minimum distace to render */
    private final float VIEWDISTANCE = 160;
    /** minimum distace to listen a static object's noises */
    private final float AMBIENTSOUDNDISTANCE = 80;
    /** minimum distance to listen a enemie's noises */
    private final float ENEMYSOUNDDISTANCE = 50;
    /** minimum distance to see message for bonfire */
    private final float MESSAGEDISTANCE = 10;

    public GameRender(TerrainQuad terrain) {
	/** set terrain and take thief from GameManager */
	this.terrain = terrain;
	this.thief = GameManager.getIstance().getNodeThief();
    }

    private float distance(NodeModel model) {
	/** Return distance from thief to each every models */
	return thief.getWorldTranslation().distance(model.getWorldTranslation());
    }

    public synchronized void rayRendering() {
	/**
	 * this methods attach and detach every models according to the distance
	 * from thief, expect castle
	 */
	for (NodeModel model : GameManager.getIstance().getModels()) {
	    if (!(model.getName().contains("Castle"))) {
		if (distance(model) < this.VIEWDISTANCE) {
		    if (GameManager.getIstance().addModelRender(model)) {
			if (model instanceof NodeEnemy)
			    GameManager.getIstance().addModelEnemy((NodeEnemy) model);

			terrain.attachChild(model);
			GameManager.getIstance().getBullet().getPhysicsSpace().add(model);
		    }

		    if (distance(model) < this.AMBIENTSOUDNDISTANCE && model.getName().equals("Chapel")) {
			model.playChapelSound();
		    } else {
			model.stopChapelSound();
		    }
		    if (distance(model) < this.AMBIENTSOUDNDISTANCE && model.getName().equals("Bonfire")) {
			model.playBonfireSound();

		    } else {
			model.stopBonfireSound();
		    }
		    if (distance(model) <= this.MESSAGEDISTANCE && model.getName().equals("Bonfire")
			    && !GameManager.getIstance().isPaused()) {
			GameManager.getIstance().getSinglePlayer().showMessageForPlayer("MessageForPlayerBonFire");

		    } else if (distance(model) > this.MESSAGEDISTANCE && model.getName().equals("Bonfire")
			    && !GameManager.getIstance().isPaused()) {
			GameManager.getIstance().getSinglePlayer().hideMessageForPlayer("MessageForPlayerBonFire");
		    }

		    if (distance(model) <= this.MESSAGEDISTANCE && model.getName().equals("Portal")
			    && !GameManager.getIstance().isPaused()) {
			GameManager.getIstance().getSinglePlayer().showMessageForPlayer("MessageForPlayerPortal");
		    } else if (distance(model) > this.MESSAGEDISTANCE && model.getName().equals("Portal")
			    && !GameManager.getIstance().isPaused()) {
			GameManager.getIstance().getSinglePlayer().hideMessageForPlayer("MessageForPlayerPortal");
		    }

		    if ((model instanceof NodeCharacter) && distance(model) < this.ENEMYSOUNDDISTANCE
			    && !((NodeCharacter) model).isViewed()) {
			((NodeCharacter) model).setViewed(true);
			thief.playEnemyView();
		    }

		} else {
		    GameManager.getIstance().detachModelRender(model);
		    terrain.detachChild(model);
		    GameManager.getIstance().getBullet().getPhysicsSpace().remove(model);
		}
	    } else {
		if (GameManager.getIstance().addModelRender(model)) {
		    terrain.attachChild(model);
		    GameManager.getIstance().getBullet().getPhysicsSpace().add(model);
		}
	    }
	}
    }

}
