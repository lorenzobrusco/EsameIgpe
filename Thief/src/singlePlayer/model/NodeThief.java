package singlePlayer.model;

import java.io.IOException;

import javax.swing.plaf.synth.SynthSpinnerUI;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.audio.AudioSource.Status;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import control.GameManager;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.controls.Chat;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.tools.SizeValue;
import multiPlayer.NodeEnemyPlayers;
import singlePlayer.Collition;
import singlePlayer.SinglePlayer;
import singlePlayer.Sound;

public class NodeThief extends NodeCharacter implements Collition {

    private ChaseCamera camera;
    private Camera cameraDirection;
    private final String bonfire = "BonFire";
    private boolean isRun;
    private boolean isSinglePlayer;
    private boolean changeAttack;
    private boolean waitAnimation;
    private boolean multiplayer;
    private boolean chatboxIsEnable;
    private int controlRender;
    private final int RENDER = 25;
    private final float SPEED = 15;
    private final float BONFIREDISTANCE = 10f;
    private int sizeLifeBar = 17;
    private Vector3f viewDirection = new Vector3f(0, 0, 1);
    private Sound walkingOnGrassSound;
    private Sound swordSound;
    private Sound bonfireSound;
    private Sound voice1;
    private Sound voice2;
    private Sound voice3;
    private Sound voice4;
    private Sound voice5;
    private Sound voice6;
    private Sound voice7;
    private Sound enemyWin;
    private Sound enemyView;
    private int currentTime;
    private int talkFrequence;
    private int lifeWanted;
    private Element lifeBarThief;
    private Element borderLifeBarThief;
    private final NiftyImage innerLifeBarRed;
    private final NiftyImage innerLifeBarGreen;
    private String namePlayer;

    public NodeThief(Spatial model, boolean multiplayer) {
	super(model, new Vector3f(1.5f, 4.4f, 2f), model.getLocalTranslation(), 10, 10);
	this.lifeWanted = (life * 40) / 100;
	this.controlRender = RENDER;
	this.isRun = false;
	this.waitAnimation = false;
	this.chatboxIsEnable = false;
	this.multiplayer = multiplayer;
	this.currentTime = (int) System.currentTimeMillis();
	this.talkFrequence = 20;
	innerLifeBarRed = GameManager.getIstance().getNifty().getRenderEngine().createImage(null,
		"Interface/innerLifeRed.png", false);
	this.innerLifeBarGreen = GameManager.getIstance().getNifty().getRenderEngine().createImage(null,
		"Interface/innerLife.png", false);
	this.setViewed(true);
	this.setupAudio();

    }

    public void setCam(Camera cam) {

	this.cameraDirection = cam;
	this.camera = new ChaseCamera(cam, this.spatial, GameManager.getIstance().getApplication().getInputManager());
	this.camera.setMinVerticalRotation(0.2f);
	this.camera.setDragToRotate(false);
	this.camera.setDefaultDistance(25f);

    }

    public void stop() {
	this.characterControl.setWalkDirection(new Vector3f(0, -2f, 0));
	// this.walkingOnGrassSound.stopSound();
	// TODO test
	if (this.getWorldTranslation().y < -9f) {
	    this.death();
	}
    }

    public void run() {
	this.resetCurrentTime();
	// this.walkingOnGrassSound.playSound();
	// TODO test
	Vector3f vector3f = this.characterControl.getViewDirection().mult(SPEED);
	vector3f.y = -2f;
	this.characterControl.setWalkDirection(vector3f);
	this.controlRender++;
	if (this.getWorldTranslation().y < -9f) {
	    this.death();
	}
    }

    @Override
    public void death() {
	if (this.alive) {
	    super.death();
	    this.resetCurrentTime();
	    this.lifeBarThief.setVisible(false);
	    this.characterControl.setWalkDirection(new Vector3f(0, -2f, 0));

	    // this.walkingOnGrassSound.stopSound();
	    // TODO test
	}
    }

    public void sitNearToBonFire() {
	this.resetCurrentTime();
	if (this.getLocalTranslation()
		.distance(GameManager.getIstance().getBonfire().getLocalTranslation()) < BONFIREDISTANCE
		&& this.isSinglePlayer) {
	    this.resetAll();
	    this.resetProgressBar();
	    // this.bonfireSound.playSound();
	    this.channel.setAnim("BonFire");
	    this.channel.setLoopMode(LoopMode.DontLoop);
	    this.channel.setSpeed(0.7f);
	    this.waitAnimation = true;

	}
    }

    @Override
    public void checkCollition() {
	for (NodeCharacter enemy : GameManager.getIstance().getEnemys()) {
	    CollisionResults collisionResult = new CollisionResults();
	    BoundingBox box = (BoundingBox) this.node.getChild(0).getWorldBound();
	    enemy.collideWith(box, collisionResult);
	    CollisionResult closest = collisionResult.getClosestCollision();
	    if (closest != null) {
		enemy.isStricken(this.getDAMAGE());
		((NodeEnemy) enemy).getLifeBar().updateLifeBar(this.getDAMAGE());
		if (enemy.isDead()) {
		    // this.enemyWin.playSound();//TODO test
		    ((NodeEnemy) enemy).getLifeBar().updateLifeBar(0);
		    ((NodeEnemy) enemy).getLifeBar().setVisibleLifeBar();
		}
		if (enemy instanceof NodeEnemyPlayers) {
		    System.out.println(((NodeEnemyPlayers) enemy).getKeyModel());
		}
	    }
	}
    }

    public void resetCurrentTime() {
	this.currentTime = (int) System.currentTimeMillis();
    }

    @Override
    public void startAttack() {
	this.resetCurrentTime();
	super.startAttack();
	this.checkCollition();
	// this.playScream();//TODO test

    }

    public void notifyUpdate(boolean attack) {

	if (this.multiplayer)
	    GameManager.getIstance().getClient().notifyUpdate(characterControl.getWalkDirection(),
		    characterControl.getViewDirection(), getLife(), attack, this.getLocalTranslation());

    }

    @Override
    public void setLifeBar(Element lifeBar, Element border, String nameModel) {
	this.lifeBarThief = lifeBar;
	this.borderLifeBarThief = border;
	NiftyImage imageLifeBarThief = GameManager.getIstance().getNifty().getRenderEngine().createImage(null,
		"Interface/MultiPlayer/borderLifeCharacters/borderLife" + nameModel + ".png", false);
	this.borderLifeBarThief.getRenderer(ImageRenderer.class).setImage(imageLifeBarThief);
	this.borderLifeBarThief.getParent().layoutElements();

	resetProgressBar();

    }

    //
    @Override
    public void setDamageLifeBar(int damage) {

	int value = (life * sizeLifeBar) / STARTLIFE;
	this.lifeBarThief.setConstraintWidth(new SizeValue(value + "%"));
	this.lifeBarThief.getParent().layoutElements();

	if (life <= lifeWanted) {

	    this.lifeBarThief.getRenderer(ImageRenderer.class).setImage(innerLifeBarRed);
	    this.lifeBarThief.getParent().layoutElements();
	}

	if (life <= 0)
	    this.lifeBarThief.setVisible(false);

    }

    // GENERA LA BARRA DELLA VITA
    public void resetProgressBar() {

	if (this.lifeBarThief != null)
	    this.lifeBarThief.markForRemoval();

	final ImageBuilder builder = new ImageBuilder() {
	    {
		filename("Interface/innerLife.png");
		x("12%");
		y("87%");
		width(NodeThief.this.sizeLifeBar + "%");
		height("2%");
		imageMode("resize:7,2,7,7,7,2,7,2,7,2,7,7");
	    }
	};

	Element layer = GameManager.getIstance().getNifty().getScreen("lifeBarScreen")
		.findElementByName("panelProgressBar");
	this.lifeBarThief = builder.build(GameManager.getIstance().getNifty(),
		GameManager.getIstance().getNifty().getCurrentScreen(), layer);
	this.lifeBarThief.getParent().layoutElements();

    }

    private void saySomething() {
	// if (((int) System.currentTimeMillis() - this.currentTime) / 1000 ==
	// this.talkFrequence
	// &&
	// this.bonfireSound.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.deathSound.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.enemyView.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.enemyWin.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.scream1.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.scream2.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.scream3.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.scream4.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.swordSound.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.voice1.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.voice2.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.voice3.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.voice4.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.voice5.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.voice6.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.voice7.getAudioNode().getStatus().equals(Status.Stopped)
	// &&
	// this.walkingOnGrassSound.getAudioNode().getStatus().equals(Status.Stopped)
	// && this.alive) {
	// this.resetCurrentTime();
	// int rand = ((int) (Math.random() * 7)) + 1;
	//
	// switch (rand) {
	// case 1:
	// this.voice1.playSound();
	// break;
	// case 2:
	// this.voice2.playSound();
	// break;
	// case 3:
	// this.voice3.playSound();
	// break;
	// case 4:
	// this.voice4.playSound();
	// break;
	// case 5:
	// this.voice5.playSound();
	// break;
	// case 6:
	// this.voice6.playSound();
	// break;
	// case 7:
	// this.voice7.playSound();
	// break;
	// default:
	// break;
	// }
	// }
	// if (((int) System.currentTimeMillis() - this.currentTime) / 1000 >
	// this.talkFrequence) {
	// this.currentTime = (int) System.currentTimeMillis();
	// }
	// TODO tenere commentati fin quando non saranno presi tutti i file
	// audio per ogni personaggio
    }

    public void playEnemyView() {
	// this.enemyView.playSound();
	// TODO tenere commentati fin quando non saranno presi tutti i file
	// audio per ogni personaggio
    }

    @Override
    protected void setupAudio() {
	if (!GameManager.getIstance().isEditor()) {
	    // this.walkingOnGrassSound = new Sound(this, "WalkingOnGrass",
	    // false, false, false, 0.09f, false);
	    // this.swordSound = new Sound(this, "Sword", false, false, false,
	    // 0.1f, false);
	    // this.deathSound = new Sound(this, "Death", false, false, false,
	    // 1.0f, false);
	    // this.bonfireSound = new Sound(this, "Bonfire", false, false,
	    // false, 1.0f, false);
	    // this.scream1 = new Sound(this, "Scream1", false, false, false,
	    // 0.5f, false);
	    // this.scream2 = new Sound(this, "Scream2", false, false, false,
	    // 0.5f, false);
	    // this.scream3 = new Sound(this, "Scream3", false, false, false,
	    // 0.5f, false);
	    // this.scream4 = new Sound(this, "Scream4", false, false, false,
	    // 0.5f, false);
	    // this.voice1 = new Sound(this, "Voice1", false, false, false,
	    // 1.0f, false);
	    // this.voice2 = new Sound(this, "Voice2", false, false, false,
	    // 1.0f, false);
	    // this.voice3 = new Sound(this, "Voice3", false, false, false,
	    // 1.0f, false);
	    // this.voice4 = new Sound(this, "Voice4", false, false, false,
	    // 1.0f, false);
	    // this.voice5 = new Sound(this, "Voice5", false, false, false,
	    // 1.0f, false);
	    // this.voice6 = new Sound(this, "Voice6", false, false, false,
	    // 1.0f, false);
	    // this.voice7 = new Sound(this, "Voice7", false, false, false,
	    // 1.0f, false);
	    // this.enemyWin = new Sound(this, "EnemyWin", false, false, false,
	    // 1.0f, false);
	    // this.enemyView = new Sound(this, "EnemyView", false, false,
	    // false, 1.0f, false);
	    // TODO tenere commentati fin quando non saranno presi tutti i file
	    // audio per ogni personaggio
	}
    }

    public AnalogListener analogListener = new AnalogListener() {
	public void onAnalog(String name, float value, float tpf) {
	    if ((name.equals(run) && NodeThief.this.alive && !NodeThief.this.waitAnimation)
		    && !GameManager.getIstance().isPaused()) {
		run();
	    }
	    if ((name.equals(rotateClockwise) && NodeThief.this.alive && !NodeThief.this.waitAnimation)
		    && !GameManager.getIstance().isPaused()) {
		Quaternion rotateL = new Quaternion().fromAngleAxis(FastMath.PI * tpf, Vector3f.UNIT_Y);
		rotateL.multLocal(viewDirection);
	    } else if ((name.equals(rotateCounterClockwise) && NodeThief.this.alive && !NodeThief.this.waitAnimation)
		    && !GameManager.getIstance().isPaused()) {
		Quaternion rotateR = new Quaternion().fromAngleAxis(-FastMath.PI * tpf, Vector3f.UNIT_Y);
		rotateR.multLocal(viewDirection);
	    }
	    if (!GameManager.getIstance().isPaused()) {
		NodeThief.this.notifyUpdate(false);
		viewDirection.y = -2f;
		characterControl.setViewDirection(viewDirection);
	    }
	}
    };

    public ActionListener actionListener = new ActionListener() {
	public void onAction(String name, boolean pressed, float value) {
	    if ((name.equals(run) && pressed && NodeThief.this.alive && !NodeThief.this.waitAnimation)
		    && !GameManager.getIstance().isPaused()) {
		NodeThief.this.notifyUpdate(false);
		NodeThief.this.isRun = true;
		NodeThief.this.channel.setAnim(run);
	    } else if ((name.equals(run) && !pressed && NodeThief.this.alive && !NodeThief.this.waitAnimation)
		    && !GameManager.getIstance().isPaused()) {
		NodeThief.this.notifyUpdate(false);
		NodeThief.this.stop();
		NodeThief.this.isRun = false;
		NodeThief.this.channel.setAnim(idle);
	    } else if ((name.equals(attack1) && pressed && NodeThief.this.alive && NodeThief.this.alive
		    && !NodeThief.this.waitAnimation) && !GameManager.getIstance().isPaused()) {
		NodeThief.this.notifyUpdate(true);
		NodeThief.this.stop();
		NodeThief.this.isRun = false;
		NodeThief.this.waitAnimation = true;
		if (!NodeThief.this.changeAttack) {
		    NodeThief.this.channel.setAnim(attack1);
		    NodeThief.this.channel.setSpeed(3f);
		} else {
		    NodeThief.this.channel.setAnim(attack4);
		    NodeThief.this.channel.setSpeed(2f);
		}
		NodeThief.this.startAttack();
		NodeThief.this.changeAttack = !NodeThief.this.changeAttack;
		NodeThief.this.channel.setLoopMode(LoopMode.DontLoop);
	    } else if ((name.equals(bonfire) && pressed && NodeThief.this.alive && NodeThief.this.alive
		    && !NodeThief.this.waitAnimation && !NodeThief.this.isRun)
		    && !GameManager.getIstance().isPaused()) {
		NodeThief.this.stop();
		NodeThief.this.isRun = false;
		NodeThief.this.sitNearToBonFire();
	    } else if ((name.equals(pause) && !pressed)) {

		if (!GameManager.getIstance().isPaused()) {

		    GameManager.getIstance().getNifty().gotoScreen("pauseScreen");
		    GameManager.getIstance().pauseGame();
		}

		else {
		    GameManager.getIstance().getNifty().gotoScreen("lifeBarScreen");
		    GameManager.getIstance().resumeGame();
		    Element element = GameManager.getIstance().getNifty().getCurrentScreen()
			    .findElementByName("sureExitControl");
		    element.setVisible(false);

		}
	    } else if ((name.equals(chatBox) && !isSinglePlayer) && !pressed) {
		if (!chatboxIsEnable) {
		    Element el = GameManager.getIstance().getNifty().getScreen("lifeBarScreen")
			    .findElementByName("chatMultiPlayer");
		    el.setVisible(!el.isVisible());
		    GameManager.getIstance().pauseGame();
		    chatboxIsEnable = !chatboxIsEnable;
		} else {
		    Element el = GameManager.getIstance().getNifty().getScreen("lifeBarScreen")
			    .findElementByName("chatMultiPlayer");
		    el.setVisible(!el.isVisible());
		    GameManager.getIstance().resumeGame();
		    chatboxIsEnable = !chatboxIsEnable;
		}

	    }
	}
    };

    @Override
    public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {

	if (arg2.equals(attack1)) {
	    arg1.setAnim(idle);
	    NodeThief.this.waitAnimation = false;
	    NodeThief.this.endAttack();
	}
	if (arg2.equals(attack4)) {
	    NodeThief.this.waitAnimation = false;
	    arg1.setAnim(idle);
	    NodeThief.this.endAttack();
	}
	if (arg2.equals(bonfire)) {
	    arg1.setAnim(idle);
	    NodeThief.this.waitAnimation = false;
	    for (NodeCharacter enemy : GameManager.getIstance().getEnemys()) {
		enemy.resetAll();
	    }
	}
    }

    public ChaseCamera getCamera() {
	return this.camera;
    }

    public Camera getCameraDir() {
	return this.cameraDirection;
    }

    public BetterCharacterControl getControl() {
	return this.characterControl;
    }

    public boolean isRun() {
	return this.isRun;
    }

    public boolean isSinglePlayer() {
	return isSinglePlayer;
    }

    public void setSinglePlayer(boolean isSinglePlayer) {
	this.isSinglePlayer = isSinglePlayer;
    }

    public String getNamePlayer() {
	return namePlayer;
    }

    public void setNamePlayer(String namePlayer) {
	this.namePlayer = namePlayer;
    }

}