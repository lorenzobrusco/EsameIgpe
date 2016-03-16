package singlePlayer.artificialIntelligence;

import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import control.GameManager;
import singlePlayer.model.NodeEnemy;
import singlePlayer.model.NodeModel;

public class AI {

    private final NodeEnemy enemy;
    private final Way way;
    private final float SPEED = 15;
    private boolean stopSearchThief;
    private Vector3f newWay;

    public AI(final NodeEnemy enemy) {
	this.enemy = enemy;
	this.stopSearchThief = false;
	this.newWay = new Vector3f(0f, 0f, 0f);
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
		    this.way.newWay();
		    if (!this.collisionResults((BoundingBox) this.way.getRightBox().getWorldBound())){
			this.newWay = thiefDirection.add(new Vector3f(5f,0,0));
			
		    }
		    else if (!this.collisionResults((BoundingBox) this.way.getLeftBox().getWorldBound())){
			this.newWay = thiefDirection.add(new Vector3f(-5f,0,0));
			
		    }
//		    else {
//			this.newWay = this.way.getBackBox().getLocalTranslation();
//			this.newWay = this.way.getBackBox().getLocalTranslation();
//			System.out.println(this.newWay);
//		    }
		    this.alternativeWay(this.newWay);
		}
	    } else if (distance <= this.enemy.getDISTANCE() && !this.enemy.hasFound()) {
		stop();
		this.enemy.setHasFound(true);
	    }
	} else
	    controll(this.enemy.getLocalTranslation(), this.newWay);
    }

    public void alternativeWay(Vector3f direction) {

	Vector3f enemyLocation = this.enemy.getLocalTranslation();
	Vector3f view=this.enemy.getCharacterControl().getViewDirection();
	Quaternion rotateL = new Quaternion().fromAngleAxis(FastMath.PI /8, Vector3f.UNIT_Y);
	rotateL.multLocal(view);
	enemyRotate(enemy, view);
	
	
//	this.newWay = enemyLocation.subtract(direction);
//	this.newWay.y = -2f;
//	System.out.println(newWay);
//	float distance = enemyLocation.distance(direction);
//	this.enemyRotate(this.enemy, direction);
//	this.enemyTranslate(this.enemy, direction, distance);

    }

    public void controll(Vector3f start, Vector3f end) {
	// System.out.println(start + "----" + end);
	new Thread(){
	    @Override
	    public void run() {
		try {
		    sleep(1000);
		    AI.this.stopSearchThief = false;
		} catch (InterruptedException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}.start();
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
