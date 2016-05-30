package singlePlayer.model;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import control.GameManager;
import singlePlayer.artificialIntelligence.AI;

/**
 * 
 * this class is used to create enemy
 *
 */

public class NodeEnemy extends NodeCharacter {

	/** artificial intelligence */
	private AI artificialIntelligence;
	/** check if enemy is running */
	private boolean isRun;
	/** check if enemy is standing */
	private boolean isStanding;
	/** check if enemy has found thief */
	private boolean hasFound;
	/** switch attack's animations */
	private boolean switchAttack;
	/** check if there is some animation */
	private boolean waitAnimation;
	/** minimum distance for attack thief */
	private final float DISTANCE;
	/** attack box */
	private Geometry box;
	/** enemie's lifebar */
	private LifeBar lifeBar;

	/** constructor */
	public NodeEnemy(Spatial model, Vector3f dimensionControll, Vector3f intersection) {
		super(model, dimensionControll, intersection, 50, 10);
		this.artificialIntelligence = new AI(this);
		this.lifeBar = new LifeBar(this);
		this.setBoundingBox();
		this.isRun = false;
		this.hasFound = false;
		this.switchAttack = false;
		this.waitAnimation = false;
		this.DISTANCE = 5;
		this.isStanding = !isRun;
	}

	/** this method create attack box */
	private void setBoundingBox() {
		BoundingBox boundingBox = new BoundingBox();
		boundingBox.setXExtent(1.5f);
		boundingBox.setYExtent(2.3f);
		boundingBox.setZExtent(1.5f);

		final WireBox wireBox2 = new WireBox();
		wireBox2.fromBoundingBox(boundingBox);
		wireBox2.setLineWidth(0f);
		this.box = new Geometry("boxAttach", wireBox2);
		this.box.setLocalTranslation(0, 2.4f, 0f);

		final Material material = new Material(GameManager.getIstance().getApplication().getAssetManager(),
				"Common/MatDefs/Misc/Unshaded.j3md");
		this.box.setMaterial(material);
		material.getAdditionalRenderState().setDepthWrite(true);
		material.getAdditionalRenderState().setColorWrite(true);
		box.setQueueBucket(Bucket.Transparent);
		box.setShadowMode(ShadowMode.Off);
		material.getAdditionalRenderState().setDepthWrite(false);
		material.getAdditionalRenderState().setColorWrite(false);
		boundingBox.setCenter(box.getLocalTranslation());

		this.attachChild(box);

	}

	/** this method start artificial intelligence */
	public void runIntelligence() {
		if (!waitAnimation && this.alive) {
			this.artificialIntelligence.run();
		}
	}

	/** this method start attack */
	public void attack() {
		if (!GameManager.getIstance().getNodeThief().isDead() && !GameManager.getIstance().isPaused()) {
			if (this.hasFound && !this.waitAnimation) {
				this.waitAnimation = true;
				this.startAttack();
				GameManager.getIstance().getNodeThief().resetCurrentTime();
				this.playScream();
				this.checkCollition();
				if (this.waitAnimation) {
					this.channel.setAnim(this.attack1);
					if (this.name.equals("Wukong")) {
						this.channel.setSpeed(2.0f);
					}
					this.switchAttack = !this.switchAttack;
				} else {
					this.channel.setAnim(this.attack2);
					if (this.name.equals("Wukong")) {
						this.channel.setSpeed(2.0f);
					}
					this.switchAttack = !this.switchAttack;
				}
			}
		}
	}

	/** this method stop animation */
	public void stopAnimation() {
		if (this.getWorldTranslation().y > -9f) {
			if (!isStanding) {
				this.channel.setAnim(this.idle);
				this.isRun = false;
				this.isStanding = !this.isRun;
			}
		} else {
			this.death();
		}

	}

	/** this method run animation */
	public void runAnimation() {
		if (!isRun && this.alive) {
			this.channel.setAnim(this.run);
			this.isRun = true;
			this.isStanding = !this.isRun;
			if (this.getWorldTranslation().y < -9f) {
				this.isRun = false;
				this.isStanding = !this.isRun;
				this.death();
			}
		}
	}

	/** this method start first attack's animation */
	public void attack1Animation() {
		if (this.alive) {
			this.channel.setAnim(this.attack1);
			this.stopAnimation();
		}
	}

	/** this method start second attack's animation */
	public void attack2Animation() {
		if (this.alive) {
			this.channel.setAnim(this.attack2);
			this.stopAnimation();
		}
	}

	/** this method reset everything */
	@Override
	public void resetAll() {
		this.artificialIntelligence.stop();
		this.artificialIntelligence = new AI(this);
		this.waitAnimation = false;
		if (!this.alive)
			this.lifeBar = new LifeBar(this);
		super.resetAll();
		GameManager.getIstance().getBullet().getPhysicsSpace().add(this);
		if (!this.alive)
			this.runIntelligence();
	}

	/** this method check if there is a coalition */
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

	/** this method is called when enemy is deaths */
	@Override
	public void death() {
		if (this.alive) {
			super.death();
			this.artificialIntelligence.stop();
			this.artificialIntelligence = null;
			this.characterControl.setWalkDirection(new Vector3f(0, -2f, 0));
		} else {
			super.death();
			this.artificialIntelligence = null;
		}
	}

	/** jmonkey's method */
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

	public void resetIntelligence() {
		this.artificialIntelligence.stop(); 
		this.artificialIntelligence = new AI(this);
		this.artificialIntelligence.run();
	}

	/** this method check if enemy has found thief */
	public boolean hasFound() {
		return hasFound;
	}

	/** this method set if enemy has found thief */
	public void setHasFound(boolean hasFound) {
		this.hasFound = hasFound;
	}

	/** this method get distance */
	public float getDISTANCE() {
		return DISTANCE;
	}

	/** this method get box */
	public Geometry getBox() {
		return box;
	}

	/** this method set box */
	public void setBox(Geometry box) {
		this.box = box;
	}

	/** this method get lifebar */
	public LifeBar getLifeBar() {
		return lifeBar;
	}

	/** this method set lifebar */
	public void setLifeBar(LifeBar lifeBar) {
		this.lifeBar = lifeBar;
	}

	/** this method stop artificial intelligence */
	public void pauseIntelligence() {
		artificialIntelligence.pause();
	}

	/** this method resume artificial intelligence */
	public void resumeIntelligence() {
		artificialIntelligence.resume();
	}

//	public void stop() {
//		this.artificialIntelligence.stop();
//	}
}
