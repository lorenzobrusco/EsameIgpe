package singlePlayer.model;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import control.GameManager;
import de.lessvoid.nifty.elements.Element;
import multiPlayer.notify.NotifyBoxAttack;
import singlePlayer.Sound;

/**
 * 
 * this class is used to create a character
 *
 */
public class NodeCharacter extends NodeModel implements AnimEventListener {

    /** animation objects */
    protected AnimChannel channel;
    protected AnimControl control;
    /***/
    /***/
    protected final String rotateClockwise = "rotateClockwise";
    protected final String rotateCounterClockwise = "rotateCounterClockwise";
    protected final String idle = "Idle";
    protected final String attack1 = "Attack1";
    protected final String attack2 = "Attack2";
    protected final String death = "Death";
    protected final String run = "Run";
    protected final String rotateLeft = "rotateLeft";
    protected final String rotateRight = "rotateRight";
    protected final String pause = "Pause";
    protected final String chatBox = "chatBox";
    /***/
    /** start life */
    protected final int STARTLIFE;
    /** life */
    protected int life;
    /** damage */
    private final int DAMAGE;
    /** father's node */
    protected final Node node = new Node("attackBox");
    /** check if it's alive */
    protected boolean alive;
    /** score */
    protected int score;
    /** start position */
    private Vector3f startPosition;
    // TODO
    private boolean viewed;
    /** sounds */
    protected Sound deathSound;
    protected Sound ambientSound;
    protected Sound scream1;
    protected Sound scream2;
    protected Sound scream3;
    protected Sound scream4;

    /***/

    /** builder */
    public NodeCharacter(Spatial model, Vector3f dimensionControll, int life, final int DAMAGE) {
	super(model, dimensionControll);
	this.alive = true;
	this.control = this.spatial.getControl(AnimControl.class);
	this.control.addListener(this);
	this.channel = control.createChannel();
	this.channel.setAnim(idle);
	this.STARTLIFE = life;
	this.life = life;
	this.score = 0;
	this.DAMAGE = DAMAGE;
	this.viewed = false;
	this.setupAudio();
    }

    /** builder */
    public NodeCharacter(String model, Vector3f dimensionControll, int life, final int DAMAGE) {
	super(model, dimensionControll);
	this.alive = true;
	this.control = this.spatial.getControl(AnimControl.class);
	this.control.addListener(this);
	this.channel = control.createChannel();
	this.channel.setAnim(idle);
	this.STARTLIFE = life;
	this.life = life;
	this.score = 0;
	this.DAMAGE = DAMAGE;
	this.setupAudio();
    }

    /** builder */
    public NodeCharacter(Spatial model, Vector3f dimensionControll, Vector3f intersection, int life, final int DAMAGE) {
	super(model, dimensionControll, intersection);
	this.alive = true;
	this.control = this.spatial.getControl(AnimControl.class);
	this.control.addListener(this);
	this.channel = control.createChannel();
	this.channel.setAnim(idle);
	this.STARTLIFE = life;
	this.life = life;
	this.score = 0;
	this.DAMAGE = DAMAGE;
	this.startPosition = intersection;
	this.setupAudio();

    }

    /** builder */
    public NodeCharacter(String model, Vector3f dimensionControll, Vector3f intersection, int life, final int DAMAGE) {
	super(model, dimensionControll, intersection);
	this.alive = true;
	this.control = this.spatial.getControl(AnimControl.class);
	this.control.addListener(this);
	this.channel = control.createChannel();
	this.channel.setAnim(idle);
	this.STARTLIFE = life;
	this.life = life;
	this.score = 0;
	this.DAMAGE = DAMAGE;
	this.startPosition = intersection;
	this.setupAudio();
    }

    /** this method start scream */
    protected void playScream() {
//	if (!GameManager.getIstance().isEditor()) {
//	    int rand = (int) (Math.random() * 4) + 1;
//	    switch (rand) {
//	    case 1:
//		this.scream1.playSoundIstance();
//		break;
//	    case 2:
//		this.scream2.playSoundIstance();
//		break;
//	    case 3:
//		this.scream3.playSoundIstance();
//		break;
//	    case 4:
//		this.scream4.playSoundIstance();
//		break;
//	    default:
//		break;
//	    }
//	}
    }

    /** this method add physic */
    public void addPhysicsSpace() {
	GameManager.getIstance().getBullet().getPhysicsSpace().add(characterControl);
    }

    /** this method stop attack */
    public void endAttack() {
	GameManager.getIstance().addBoxAttack(new NotifyBoxAttack(false, this.node));
    }

    /** this method check if there is a collition */
    public void isStricken(final int DAMAGE) {
	if (this.alive) {
	    this.life -= DAMAGE;
	    if (this.isDead()) {
		// this.deathSound.playSound(); //TODO
		this.alive = false;
		this.channel.setAnim(death);
		this.channel.setLoopMode(LoopMode.DontLoop);
		GameManager.getIstance().getBullet().getPhysicsSpace().remove(this.characterControl);
	    }
	}
    }

    /** this method restores character */
    public void resetAll() {
	this.alive = true;
	this.life = this.STARTLIFE;
	this.viewed = false;
	this.channel.setAnim(idle);
	this.channel.setLoopMode(LoopMode.Loop);
    }

    /** this method attach a box for check collition */
    public void startAttack() {
	GameManager.getIstance().getNodeThief().resetCurrentTime();
	this.playScream();
	this.node.setLocalTranslation(this.getLocalTranslation());

	final BoundingBox boundingBox = new BoundingBox();
	boundingBox.setXExtent(1.5f);
	boundingBox.setYExtent(0.5f);
	boundingBox.setZExtent(1.5f);

	final WireBox wireBox2 = new WireBox();
	wireBox2.fromBoundingBox(boundingBox);
	wireBox2.setLineWidth(0f);
	final Geometry boxAttach = new Geometry("boxAttach", wireBox2);
	boxAttach.setLocalTranslation(0, 2.5f, 4f);

	final Material material = new Material(GameManager.getIstance().getApplication().getAssetManager(),
		"Common/MatDefs/Misc/Unshaded.j3md");
	boxAttach.setMaterial(material);

	material.getAdditionalRenderState().setDepthWrite(false);
	material.getAdditionalRenderState().setColorWrite(false);

	boxAttach.setQueueBucket(Bucket.Transparent);

	this.node.attachChild(boxAttach);
	this.node.setLocalRotation(this.getLocalRotation());
	boundingBox.setCenter(boxAttach.getLocalTranslation());
	GameManager.getIstance().addBoxAttack(new NotifyBoxAttack(true, this.node));
    }

    /** this method get isDead */
    public boolean isDead() {
	return this.life <= 0;
    }

    /** this method is called when character is death */
    public void death() {
	// if (this.alive)
	/// this.deathSound.playSound();
	this.alive = false;
	this.channel.setAnim(death, 0.50f);
	this.channel.setLoopMode(LoopMode.DontLoop);
    }

    /** this method make a key */
    public int getKeyCharacter() {
	return (this.getName() + this.startPosition).hashCode();
    }

    @Override
    public void onAnimChange(AnimControl arg0, AnimChannel arg1, String arg2) {

    }

    @Override
    public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {
    }

    /** this method setup sounds */
    @Override
    protected void setupAudio() {
	// if (!GameManager.getIstance().isEditor()) {
	// this.deathSound = new Sound(this, "Death", false, true, false, 1.2f,
	// false);
	// this.scream1 = new Sound(this, "Scream1", false, true, false, 1.2f,
	// false);
	// this.scream2 = new Sound(this, "Scream2", false, true, false, 1.2f,
	// false);
	// this.scream3 = new Sound(this, "Scream3", false, true, false, 1.2f,
	// false);
	// this.scream4 = new Sound(this, "Scream4", false, true, false, 1.2f,
	// false);
	// }
    }

    /** this method set viewed */
    public void setViewed(boolean viewed) {
	this.viewed = viewed;
    }

    /** this method get viewed */
    public boolean isViewed() {
	return this.viewed;

    }

    /** this method get life */
    public int getLife() {
	return life;
    }

    /** this method set life */
    public void setLife(int life) {
	this.life = life;
    }

    /** this method get startLife */
    public int getStartLife() {
	return this.STARTLIFE;
    }

    /** this method get damage */
    public int getDAMAGE() {
	return DAMAGE;
    }

    /** this method get score */
    public int getScore() {
	return this.score;
    }

    /** this method set score */
    public void setScore(int score) {
	this.score = score;
    }

    /** override this method */
    public void checkCollition() {
    }

    /** override this method */
    public void setLifeBar(Element lifeBar, Element Border, String nameModel) {
    }

    /** override this method */
    public void setDamageLifeBar() {

    }

    /** override this method */
    public void setVisibleLifeBar() {
    }

}
