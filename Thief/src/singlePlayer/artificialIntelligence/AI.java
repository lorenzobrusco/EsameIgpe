package singlePlayer.artificialIntelligence;

import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import control.GameManager;
import singlePlayer.model.NodeEnemy;
import singlePlayer.model.NodeModel;

public class AI {

	private final NodeEnemy enemy;
	private final Way way;
	private final float SPEED = 15;
	private final int RIGHT = 1;
	private final int LEFT = 2;
	private final int BACK = 0;
	private boolean stopSearchThief;

	public AI(final NodeEnemy enemy) {
		this.enemy = enemy;
		this.stopSearchThief = false;
		this.way = new Way(this.enemy);
	}

	public void run() {

		if (!stopSearchThief) {
			Vector3f thiefLocation = GameManager.getIstance().getNodeThief().getLocalTranslation();
			Vector3f enemyLocation = this.enemy.getLocalTranslation();

			Vector3f thiefDirection = thiefLocation.subtract(enemyLocation);
			thiefDirection.y = -2.0f;

			float distance = thiefLocation.distance(enemyLocation);
			this.enemyRotate(this.enemy, thiefDirection);
			if (distance > this.enemy.getDISTANCE()) {
				this.enemy.setHasFound(false);
				this.enemyTranslate(enemy, thiefDirection, distance);
				if (this.collisionResults((BoundingBox) this.enemy.getBox().getWorldBound())) {
					this.stopSearchThief = true;
					if (!this.collisionResults((BoundingBox) this.way.getRightBox().getWorldBound())) {
						this.alternativeWay(this.RIGHT);
					} else if (!this.collisionResults((BoundingBox) this.way.getLeftBox().getWorldBound())) {
						this.alternativeWay(this.LEFT);
					}
				}
			} else if (distance <= this.enemy.getDISTANCE() && !this.enemy.hasFound()) {
				stop();
				this.enemy.setHasFound(true);
			}
		} else
			this.controll();
	}

	public void alternativeWay(final int DIRECTION) {

		if (!stopSearchThief) {
			Quaternion turn = new Quaternion();

			switch (DIRECTION) {
			case RIGHT:
				System.out.println("destra");
				turn.fromAngles(0, 90, 0);
				break;
			case LEFT:
				System.out.println("sinistra");
				turn.fromAngles(0, -90, 0);
				break;
			case BACK:
				System.out.println("in dietro");
				break;

			default:
				break;
			}
			Vector3f enemyLocation = this.enemy.getLocalTranslation();
			Vector3f view = turn.mult(Vector3f.UNIT_Z);
			System.out.println(enemy.getLocalTranslation() + "---> " + view);
			Vector3f direction = enemyLocation.subtract(view);
			direction.y = -2f;
			enemyRotate(enemy, direction);
			enemyTranslate(enemy, direction, this.SPEED * 4);
		}
	}

	public void controll() {
		if (stopSearchThief) {
			new Thread() {
				@Override
				public void run() {
					try {
						sleep(500);
						AI.this.stopSearchThief = false;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	public boolean collisionResults(BoundingBox box) {
		boolean collision = false;
		for (NodeModel node : GameManager.getIstance().getModels()) {
			if (node == this.enemy || node == GameManager.getIstance().getNodeThief())
				continue;
			CollisionResults collisionResult = new CollisionResults();
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
