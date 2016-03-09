package singlePlayer.model;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import control.GameManager;
import singlePlayer.artificialIntelligence.AI;

public class NodeEnemy extends NodeCharacter {

	private final AI artificialIntelligence;
	private boolean isRun;
	private boolean isStanding;
	private boolean hasFound;
	private boolean switchAttack;
	private boolean waitAnimation;
	private final float DISTANCE;

	public NodeEnemy(Spatial model, Vector3f dimensionControll, Vector3f intersection) {
		super(model, dimensionControll, intersection, 50, 10);
		this.artificialIntelligence = new AI(this);
		this.isRun = false;
		this.hasFound = false;
		this.switchAttack = false;
		this.waitAnimation = false;
		this.DISTANCE = 5;
		this.isStanding = !isRun;
	}

	public void runIntelligence() {
		if (!waitAnimation)
			this.artificialIntelligence.run();
	}

	public void attack() {
		if (this.hasFound && !this.waitAnimation) {
			this.waitAnimation = true;
			this.startAttack();
			this.checkCollition();
			if (this.waitAnimation) {
				this.channel.setAnim(this.attack1);
				this.switchAttack = !this.switchAttack;
			} else {
				this.channel.setAnim(this.attack2);
				this.switchAttack = !this.switchAttack;
			}
		}
	}

	public void stopAnimation() {
		if (!isStanding) {
			this.channel.setAnim(this.idle);
			this.isRun = false;
			this.isStanding = !this.isRun;
		}
	}

	public void runAnimation() {
		if (!isRun) {
			this.channel.setAnim(this.run);
			this.isRun = true;
			this.isStanding = !this.isRun;
			if (this.getWorldTranslation().y < -9f) {
				this.death();
			}
		}
	}

	public void attack1Animation() {
		this.channel.setAnim(this.attack1);
		this.stopAnimation();
	}

	public void attack2Animation() {
		this.channel.setAnim(this.attack2);
		this.stopAnimation();
	}

	public boolean hasFound() {
		return hasFound;
	}

	public void setHasFound(boolean hasFound) {
		this.hasFound = hasFound;
	}

	public float getDISTANCE() {
		return DISTANCE;
	}

	public boolean isSwitchAttack() {
		return switchAttack;
	}

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

	@Override
	public void death() {
		super.death();
		this.artificialIntelligence.stop();
	}

	@Override
	public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {
		if (arg2.equals(attack1)) {
			arg1.setAnim(this.idle);
			this.endAttack();
			this.waitAnimation = false;
		}
		if (arg2.equals(attack2)) {

			arg1.setAnim(this.idle);
			this.endAttack();
			this.waitAnimation = false;
		}
	}

}
