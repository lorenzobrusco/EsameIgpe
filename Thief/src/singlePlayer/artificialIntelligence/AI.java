package singlePlayer.artificialIntelligence;

import com.jme3.math.Vector3f;

import control.GameManager;
import singlePlayer.model.NodeEnemy;

public class AI {

	private final NodeEnemy enemy;
	private final float SPEED = 15;

	public AI(final NodeEnemy enemy) {
		this.enemy = enemy;
	}

	public void run() {

		Vector3f thiefLocation = GameManager.getIstance().getNodeThief().getLocalTranslation();
		Vector3f enemyLocation = this.enemy.getLocalTranslation();

		Vector3f thiefDirection = thiefLocation.subtract(enemyLocation);
		thiefDirection.y = -2.0f;

		float distance = thiefLocation.distance(enemyLocation);

		if (distance > this.enemy.getDISTANCE()) {
			this.enemy.setHasFound(false);
			this.enemyRotate(this.enemy, thiefDirection);
			this.enemyTranslate(enemy, thiefDirection, distance);
		} else if (distance <= this.enemy.getDISTANCE() && !this.enemy.hasFound()) {
			stop();
			this.enemy.setHasFound(true);
		}
	}

	public void enemyRotate(NodeEnemy enemy, Vector3f thiefDirection) {
		enemy.getCharacterControl().setViewDirection(thiefDirection);
	}

	public void enemyTranslate(NodeEnemy enemy, Vector3f thiefDirection, float distance) {
		enemy.getCharacterControl().setWalkDirection(thiefDirection.mult((1 / distance) * this.SPEED));
		enemy.runAnimation();
	}

	public void stop() {
		enemyTranslate(enemy, new Vector3f(0, -2, 0), 00);
		enemy.stopAnimation();
	}

}
