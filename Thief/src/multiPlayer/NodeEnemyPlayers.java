package multiPlayer;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import singlePlayer.model.NodeCharacter;

public class NodeEnemyPlayers extends NodeCharacter {

    // private final static int SPEED = 15;
    private boolean waitAnimation;

    public NodeEnemyPlayers(String model, Vector3f dimensionControll, int life, int DAMAGE) {
	super(model, dimensionControll, life, DAMAGE);
	this.waitAnimation = false;
    }

    public NodeEnemyPlayers(Spatial model, Vector3f dimensionControll, int life, int DAMAGE) {
	super(model, dimensionControll, life, DAMAGE);
	this.waitAnimation = false;
    }

    public NodeEnemyPlayers(String model, Vector3f dimensionControll, Vector3f intersect, int life, int DAMAGE) {
	super(model, dimensionControll, intersect, life, DAMAGE);
	this.waitAnimation = false;
    }

    public NodeEnemyPlayers(Spatial model, Vector3f dimensionControll, Vector3f intersect, int life, int DAMAGE) {
	super(model, dimensionControll, intersect, life, DAMAGE);
	this.waitAnimation = false;
    }

    public void setWalkDirection(Vector3f direction) {
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

    public void setViewDirection(Vector3f view) {
	this.characterControl.setViewDirection(view);
    }

    public void stop() {
	this.characterControl.setWalkDirection(new Vector3f(0, -2f, 0));
	this.channel.setAnim(idle);
	// this.walkingOnGrassSound.stopSound(); //TODO test
	if (this.getWorldTranslation().y < -9f) {
	    this.death();
	}
    }

    public boolean isWaitAnimation() {
	return waitAnimation;
    }

    public void setWaitAnimation(boolean waitAnimation) {
	this.waitAnimation = waitAnimation;
    }

    @Override
    public void startAttack() {
	super.startAttack();
    }

    @Override
    public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {

	if (arg2.equals(attack1)) {
	    arg1.setAnim(idle);
	    NodeEnemyPlayers.this.waitAnimation = false;
	    NodeEnemyPlayers.this.endAttack();
	}
	if (arg2.equals(attack4)) {
	    NodeEnemyPlayers.this.waitAnimation = false;
	    arg1.setAnim(idle);
	    NodeEnemyPlayers.this.endAttack();
	}
    }

    // TODO Implementare questa classe per i nemici del multiplayer
}
