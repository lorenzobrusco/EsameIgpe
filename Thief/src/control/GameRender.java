package control;

import com.jme3.terrain.geomipmap.TerrainQuad;

import singlePlayer.model.NodeCharacter;
import singlePlayer.model.NodeEnemy;
import singlePlayer.model.NodeModel;
import singlePlayer.model.NodeThief;

public class GameRender {

	private TerrainQuad terrain;
	private NodeThief thief;
	private final float VIEWDISTANCE = 160;
	private final float AMBIENTSOUDNDISTANCE = 80;
	private final float ENEMYSOUNDDISTANCE = 50;

	public GameRender(TerrainQuad terrain) {

		this.terrain = terrain;
		this.thief = GameManager.getIstance().getNodeThief();
	}

	private float distance(NodeModel model) {
		return thief.getWorldTranslation().distance(model.getWorldTranslation());

	}

	public synchronized void rayRendering() {
		for (NodeModel model : GameManager.getIstance().getModels()) {
			if (!(model.getName().contains("Castle"))) {
				if (distance(model) < this.VIEWDISTANCE) {
					if (GameManager.getIstance().addModelRender(model)) {
					    if(model instanceof NodeEnemy)
						GameManager.getIstance().addModelEnemy((NodeEnemy) model);
					    
						terrain.attachChild(model);
						GameManager.getIstance().getBullet().getPhysicsSpace().add(model);
					}
					if (distance(model) < this.AMBIENTSOUDNDISTANCE) {
						model.playSound();
					} else {
						model.stopSound();
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
