package singlePlayer.artificialIntelligence;

import com.jme3.math.Vector3f;

import control.GameManager;
import singlePlayer.model.NodeEnemy;

public class IA {

	private final NodeEnemy enemy;

	public IA(final NodeEnemy enemy) {
		this.enemy = enemy;
	}

	public void run() {
		Vector3f thiefLocation = GameManager.getIstance().getNodeThief().getLocalTranslation();
		Vector3f enemyLocation = this.enemy.getLocalTranslation();

		// TODO attacca

		Vector3f thiefDirection = thiefLocation.subtract(enemyLocation);
		thiefDirection.y = -2.0f;

		float distance = thiefLocation.distance(enemyLocation);

		this.enemyRotate(this.enemy, thiefDirection);
		this.enemyTranslate(enemy, thiefDirection);

		
		System.out.println(distance);

		if (distance < 5) {
			this.stop();
		}
	}

	public void enemyRotate(NodeEnemy enemy, Vector3f thiefDirection) {
		enemy.getCharacterControl().setViewDirection(thiefDirection);
		// enemy.getCharacterControl().setWalkDirection(thiefDirection);
	}

	public void enemyTranslate(NodeEnemy enemy, Vector3f thiefDirection) {
		// enemy.getCharacterControl().setViewDirection(thiefDirection);
		enemy.getCharacterControl().setWalkDirection(thiefDirection);
		enemy.runAnimation();
	}

	public void stop() {
		enemyTranslate(enemy, new Vector3f(0, -2, 0));
		enemy.stopAnimation();
	}
}
