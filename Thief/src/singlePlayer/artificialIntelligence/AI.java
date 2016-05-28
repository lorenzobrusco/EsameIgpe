package singlePlayer.artificialIntelligence;

import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import control.GameManager;
import singlePlayer.model.NodeEnemy;
import singlePlayer.model.NodeModel;

public class AI {

    /** instantiate the current enemy */
    private final NodeEnemy enemy;

    /** control value for enemy speed */
    private final float SPEED = 15;

    /** control value to activate Thief research */
    private boolean stopSearchThief;

    /** counter for collisions done with the scene elements */
    private int countCollisions;

    /** control value to indicate to the current enemy to get a new way */
    private boolean setNewWay;

    /** an enemy random location when Thief is too far */
    private Vector3f random;

    /** control value to set enemy view length */
    private final int SEARCHTHIEF = 40;

    /** control value to make enemy move */
    private boolean move;

    /** check if game is stopped */
    private boolean pause;

    /** constructor */
    public AI(final NodeEnemy enemy) {
	this.enemy = enemy;
	this.stopSearchThief = false;
	this.countCollisions = 0;
	this.setNewWay = true;
	this.move = true;
	this.random = new Vector3f();
	this.pause = false;
    }

    /** in this method are setted up all the enemy movements */
    public void run() {
	if (!this.pause) {

	    Vector3f thiefLocation = GameManager.getIstance().getNodeThief().getLocalTranslation();
	    Vector3f enemyLocation = this.enemy.getLocalTranslation();

	    Vector3f thiefDirection = thiefLocation.subtract(enemyLocation);
	    thiefDirection.y = -2.0f;

	    float distance = thiefLocation.distance(enemyLocation);

	    if (!stopSearchThief) {

		/** if enemy sees thief tries to catch him */
		if (distance > this.enemy.getDISTANCE() && distance < this.SEARCHTHIEF) {

		    /** if enemy does too much collisions then goes away */
		    if (this.countCollisions < 20) {
			this.enemyRotate(this.enemy, thiefDirection);
			this.enemy.setHasFound(false);
			this.enemyTranslate(enemy, thiefDirection, distance);
			if (this.collisionResults((BoundingBox) this.enemy.getBox().getWorldBound())) {
			    this.countCollisions++;
			}
		    } else {
			this.stopSearchThief = true;
			this.move = true;
		    }

		}
		/** if Thief is near enough attach */
		else if (distance <= this.enemy.getDISTANCE() && !this.enemy.hasFound()) {
		    this.stop();
		    this.enemy.setHasFound(true);
		    this.enemy.attack();
		    this.stopSearchThief = false;
		    this.enemy.setHasFound(false);
		    this.countCollisions = 0;
		    this.move = false;

		}
	    } else {
		/** restart looking for Thief */
		this.controll();
	    }

	    /** if enemy can't see Thief then move somewhere */
	    if (this.move) {
		this.stopSearchThief = false;
		this.countCollisions = 0;
		if (distance > this.SEARCHTHIEF) {
		    if (setNewWay) {
			do {
			    int x = (int) (Math.random() * 10);
			    int z = (int) (Math.random() * 10);
			    int chooseDirection = (int) (Math.random() * 2);
			    if (chooseDirection == 1) {
				x *= -1;
			    }

			    chooseDirection = (int) (Math.random() * 2);
			    if (chooseDirection == 1) {
				z *= -1;
			    }

			    this.random.setX(x);
			    this.random.setY(0.0f);
			    this.random.setZ(z);
			    this.random.setX(this.enemy.getLocalTranslation().getX() + this.random.getX());
			    this.random.setZ(this.enemy.getLocalTranslation().getZ() + this.random.getZ());
			    this.random.setY(-2.0f);
			} while ((this.random.getX() == 0 && this.random.getZ() == 0)
				&& !GameManager.getIstance().isWalkable(this.random.getX(), this.random.getZ()));
			this.setNewWay = false;
		    }

		    /**
		     * if enemy is too much far from Thief than stop move itself
		     */
		    else if (distance > this.SEARCHTHIEF && distance < this.SEARCHTHIEF * 3) {

			this.enemyRotate(this.enemy, this.random.subtract(this.enemy.getLocalTranslation()));
			this.enemy.setHasFound(false);
			if (GameManager.getIstance().getTerrainQuad()
				.getHeight(new Vector2f(this.enemy.getLocalTranslation().getX(),
					this.enemy.getLocalTranslation().getZ())) < -2
				&& GameManager.getIstance().getTerrainQuad()
					.getHeight(new Vector2f(this.enemy.getLocalTranslation().getX(),
						this.enemy.getLocalTranslation().getZ())) > 90) {
			    this.setNewWay = true;
			    return;
			}
			this.enemyTranslate(this.enemy, this.random.subtract(this.enemy.getLocalTranslation()),
				this.enemy.getLocalTranslation().distance(this.random) * 2);
			this.countCollisions = 0;
			this.controll();

		    }
		    /**
		     * if enemy is arrived to its new position than stop and
		     * choose another way
		     */
		    if ((((int) this.enemy.getLocalTranslation().getX() >= (int) this.random.getX() - 1)
			    && (int) this.enemy.getLocalTranslation().getX() <= (int) this.random.getX() + 1)
			    || ((int) this.enemy.getLocalTranslation().getZ() >= (int) this.random.getZ() - 1
				    && (int) this.enemy.getLocalTranslation().getZ() <= (int) this.random.getZ() + 1)) {
			this.move = false;
		    }
		}
	    } else {
		if (!this.setNewWay) {
		    this.setNewWay = true;
		    this.move = true;
		}
	    }
	} else {
	    this.stop();
	}
    }

    /** reset the Thief research */
    public void controll() {
	if (this.stopSearchThief) {
	    this.stopSearchThief = false;
	}
    }

    /** check if enemy is colliding with something in the scene */
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

    /** rotate enemy to a new direction */
    public void enemyRotate(NodeEnemy enemy, Vector3f thiefDirection) {
	enemy.getCharacterControl().setViewDirection(thiefDirection);
    }

    /** move enemy to a new position */
    public void enemyTranslate(NodeEnemy enemy, Vector3f thiefDirection, float distance) {
	enemy.getCharacterControl().setWalkDirection(thiefDirection.mult((1 / distance) * this.SPEED));
	enemy.runAnimation();
    }

    /** stop the enemy */
    public void stop() {
	enemyTranslate(this.enemy, new Vector3f(0, -2, 0), 0);
	enemy.stopAnimation();
    }

    /** stop ai */
    public void pause() {
	this.pause = true;
    }

    /** resume ai */
    public void resume() {
	this.pause = false;
    }
}
