package singlePlayer.artificialIntelligence;

import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;

import control.GameManager;
import singlePlayer.model.NodeEnemy;
import singlePlayer.model.NodeModel;

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
			if (this.collisionResults()) {
				new Way(this.enemy);
			}
		} else if (distance <= this.enemy.getDISTANCE() && !this.enemy.hasFound()) {
			stop();
			this.enemy.setHasFound(true);
		}
	}

	public boolean collisionResults() {
		boolean collision = false;
		for (NodeModel node : GameManager.getIstance().getModels()) {
			if (node == this.enemy || node == GameManager.getIstance().getNodeThief())
				continue;
			CollisionResults collisionResult = new CollisionResults();
			BoundingBox box = (BoundingBox) this.enemy.getBox().getWorldBound();

			box.setXExtent(2.5f);
			box.setYExtent(1.0f);
			box.setZExtent(2.0f);

			node.getModel().collideWith(box, collisionResult);

			CollisionResult closest = collisionResult.getClosestCollision();

			if (closest != null) {
				collision = true;
			}
		}
		return collision;
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
