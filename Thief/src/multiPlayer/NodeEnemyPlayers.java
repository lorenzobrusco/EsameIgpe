package multiPlayer;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import control.GameManager;
import singlePlayer.model.LifeBar;
import singlePlayer.model.NodeCharacter;

/**
 * 
 * This class is online enemy's character
 *
 */

public class NodeEnemyPlayers extends NodeCharacter {

    /** time to wait between ends animation */
    private boolean waitAnimation;
    /** switch animation for attacks */
    private boolean switchAttack;
    /** make a hash code from name's model */
    private final String keyModel;
    /** enemy's lifebar */
    private final LifeBar lifeBar;

    /** builder */
    public NodeEnemyPlayers(String model, Vector3f dimensionControll, int life, int DAMAGE, String key) {
	super(model, dimensionControll, life, DAMAGE);
	this.setShadowMode(RenderQueue.ShadowMode.Inherit);
	this.waitAnimation = false;
	this.switchAttack = false;
	this.lifeBar = new LifeBar(this);
	this.keyModel = key;
    }

    /** builder */
    public NodeEnemyPlayers(Spatial model, Vector3f dimensionControll, int life, int DAMAGE, String key) {
	super(model, dimensionControll, life, DAMAGE);
	this.setShadowMode(RenderQueue.ShadowMode.Inherit);
	this.waitAnimation = false;
	this.switchAttack = false;
	this.lifeBar = new LifeBar(this);
	this.keyModel = key;
    }

    /** builder */
    public NodeEnemyPlayers(String model, Vector3f dimensionControll, Vector3f intersect, int life, int DAMAGE,
	    String key) {
	super(model, dimensionControll, intersect, life, DAMAGE);
	this.setShadowMode(RenderQueue.ShadowMode.Inherit);
	this.waitAnimation = false;
	this.switchAttack = false;
	this.lifeBar = new LifeBar(this);
	this.keyModel = key;
    }

    /** builder */
    public NodeEnemyPlayers(Spatial model, Vector3f dimensionControll, Vector3f intersect, int life, int DAMAGE,
	    String key) {
	super(model, dimensionControll, intersect, life, DAMAGE);
	this.setShadowMode(RenderQueue.ShadowMode.Inherit);
	this.waitAnimation = false;
	this.switchAttack = false;
	this.lifeBar = new LifeBar(this);
	this.keyModel = key;
    }

    /** this method set enemy's walk direction */
    public void setWalkDirection(Vector3f direction) {
	if (!this.waitAnimation) {
	    if (direction.x == 0.0f && direction.y == -2.0f && direction.z == 0.0f) {
		this.characterControl.setWalkDirection(direction);
		this.channel.setAnim(idle);
	    } else {
		this.characterControl.setWalkDirection(direction);
		if (!this.channel.getAnimationName().equals(run))
		    this.channel.setAnim(run);
		if (this.getWorldTranslation().y < -9f) {
		    this.death();
		}
	    }
	}
    }

    /** this method is invoked when character must stop */
    public void stop() {
	if (!this.waitAnimation) {
	    this.characterControl.setWalkDirection(new Vector3f(0, -2f, 0));
	    this.channel.setAnim(idle);
	    // this.walkingOnGrassSound.stopSound(); //TODO test
	    if (this.getWorldTranslation().y < -9f) {
		this.death();
	    }
	}
    }

    /** this method is called when enemy death */
    @Override
    public void death() {
	super.death();
	this.waitAnimation = true;
    }

    /** this method is invoked when attach enemy */
    @Override
    public void startAttack() {
	super.startAttack();
	this.checkCollition();
	this.waitAnimation = true;
	if (!switchAttack)
	    this.channel.setAnim(attack1);
	else
	    this.channel.setAnim(attack2);
	this.switchAttack = !this.switchAttack;
    }

    /** this method check if enemy strikes main character */
    @Override
    public void checkCollition() {
	CollisionResults collisionResult = new CollisionResults();
	BoundingBox box = (BoundingBox) this.node.getChild(0).getWorldBound();
	GameManager.getIstance().getNodeThief().collideWith(box, collisionResult);
	CollisionResult closest = collisionResult.getClosestCollision();
	if (closest != null) {
	    GameManager.getIstance().getNodeThief().isStricken(this.getDAMAGE());
	}
    }

    /** jmonkey's method */
    @Override
    public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {

	if (arg2.equals(attack1)) {
	    arg1.setAnim(idle);
	    NodeEnemyPlayers.this.waitAnimation = false;
	    NodeEnemyPlayers.this.endAttack();
	}
	if (arg2.equals(attack2)) {
	    NodeEnemyPlayers.this.waitAnimation = false;
	    arg1.setAnim(idle);
	    NodeEnemyPlayers.this.endAttack();
	}
	if (arg2.equals(death)) {
	    NodeEnemyPlayers.this.waitAnimation = false;
	    NodeEnemyPlayers.this.endAttack();
	}
    }

    /** this method set enemy's view direction */
    public void setViewDirection(Vector3f view) {
	this.characterControl.setViewDirection(view);
    }

    /** this method get waitAnimation */
    public boolean isWaitAnimation() {
	return waitAnimation;
    }

    /** this method set waitAnimation */
    public void setWaitAnimation(boolean waitAnimation) {
	this.waitAnimation = waitAnimation;
    }

    /** this method get keyModel */
    public String getKeyModel() {
	return this.keyModel;
    }

    /** this method get lifebar */
    public LifeBar getLifeBar() {
	return lifeBar;
    }

    /** this method get score */
    public int getScore() {
	return score;
    }

    /** this method set score */
    public void setScore(int score) {
	this.score = score;
    }

}
