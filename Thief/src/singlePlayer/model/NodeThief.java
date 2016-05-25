package singlePlayer.model;

import java.util.ArrayList;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.audio.AudioSource.Status;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import control.GameManager;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.tools.SizeValue;
import multiPlayer.NodeEnemyPlayers;
import singlePlayer.Collition;
import singlePlayer.Sound;

/**
 * 
 * this class is main character
 *
 */

public class NodeThief extends NodeCharacter implements Collition {

	/** camera attach to main character */
	private ChaseCamera camera;
	private final String bonfire = "BonFire";
	/** it's true main character is running */
	private boolean isRun;
	/** check if is single player */
	private boolean isSinglePlayer;
	/** check if is multiplayer */
	private boolean multiplayer;
	/** swicth attach animation */
	private boolean changeAttack;
	/** wait that animation is ends */
	private boolean waitAnimation;
	// TODO
	private boolean chatboxIsEnable;
	/** speed main character */
	private static final float SPEED = 15;
	/** minimum distace to active bonfire */
	private static final float BONFIREDISTANCE = 10f;
	/** minimum distace to active portal */
	private static final float PORTALDISTANCE = 10f;
	/** length lifebar */
	private static final int SIZELIFEBAR = 17;
	/** view direction */
	private Vector3f viewDirection;
	/** start position */
	private Vector3f startPosition;
	/** minimum threshold for change color to lifebar */
	private int lifebarThreshold;
	/** thief's Lifebar */
	private Element lifeBarThief;
	private Element borderLifeBarThief;
	/***/
	/** sounds */
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
	/** timer to speech */
	private int currentTime;
	/** minimum time to speech */
	private int talkFrequence;
	/***/
	private boolean win;

	private boolean canTalk;

	/** builder */
	public NodeThief(Spatial model, Vector3f startPositon, boolean multiplayer) {
		super(model, new Vector3f(1.5f, 4.4f, 2f), model.getLocalTranslation(), 50, 10);
		this.viewDirection = new Vector3f(0, 0, 1);
		this.startPosition = new Vector3f();
		this.lifebarThreshold = (this.life * 40) / 100;
		this.startPosition = startPositon;
		this.score = 0;
		this.isRun = false;
		this.win = false;
		this.waitAnimation = false;
		this.chatboxIsEnable = false;
		this.multiplayer = multiplayer;
		this.currentTime = (int) System.currentTimeMillis();
		this.talkFrequence = 10;
		this.setViewed(true);
		this.setupAudio();
		this.canTalk = true;

	}

	/** this method set camera */
	public void setCam(Camera cam, InputManager inputManager) {
		this.camera = new ChaseCamera(cam, this.spatial, inputManager);
		this.camera.setMinVerticalRotation(0.2f);
		this.camera.setDragToRotate(false);
		this.camera.setDefaultDistance(25f);
	}

	/** this method stop main character */
	public void stop() {
		this.characterControl.setWalkDirection(new Vector3f(0, -2f, 0));
		if (GameManager.getIstance().isPaused()) {
			this.setIdle();
		}
		this.walkingOnGrassSound.stopSound();
		if (this.getWorldTranslation().y < -9f) {
			this.death();
		}
	}

	/** if in pause set Thief animation to idle */
	private void setIdle() {
		this.channel.setAnim(idle);
	}

	/** this method is invoked to run main character */
	public void run() {
		this.resetCurrentTime();
		this.walkingOnGrassSound.playSound();
		Vector3f vector3f = this.characterControl.getViewDirection().mult(SPEED);
		vector3f.y = -2f;
		this.characterControl.setWalkDirection(vector3f);
		if (this.getWorldTranslation().y < -9f) {
			this.death();
		}
	}

	/** this method is called when main character is near bonfire */
	public void sitNearToBonFire() {
		this.resetCurrentTime();
		if (this.getLocalTranslation()
				.distance(GameManager.getIstance().getBonfire().getLocalTranslation()) < BONFIREDISTANCE
				&& this.isSinglePlayer) {
			this.resetAll();
			this.resetProgressBar();
			this.channel.setAnim("BonFire");
			this.channel.setLoopMode(LoopMode.DontLoop);
			this.channel.setSpeed(0.7f);
			this.waitAnimation = true;
			if (!this.multiplayer)
				for (Sound sound : this.getAllSound()) {
					sound.stopSound();
				}
			this.bonfireSound.playSound();
		}
	}

	/** this method is called when thief is near portal */
	public void nearToPortal() {
		this.win = true;
		GameManager.getIstance().getSinglePlayer().win();
	}

	/** this method is called when main character is near portal */
	public void endGame() {
		if (this.getLocalTranslation()
				.distance(GameManager.getIstance().getPortal().getLocalTranslation()) < PORTALDISTANCE) {
			// TODO avviare fine gioco
		}
	}

	/** this method reset current time */
	public void resetCurrentTime() {
		this.currentTime = (int) System.currentTimeMillis();
	}

	/** this method, if is multiplayer, notify update */
	public void notifyUpdate(boolean attack) {
		if (this.multiplayer)
			GameManager.getIstance().getClient().notifyUpdate(characterControl.getWalkDirection(),
					characterControl.getViewDirection(), super.life, attack, this.getLocalTranslation(), this.score);
	}

	/** this method is called when thief kill someone */
	public void killSomeOne() {
		this.score += 10;
	}

	/** this method is called when main character is death */
	public void startAgain() {
		this.stop();
		this.resetAll();
		this.resetCurrentTime();
		this.resetProgressBar();
		Vector3f spawnPoint;
		if (multiplayer)
			spawnPoint = this.startPosition;
		else {
			spawnPoint = GameManager.getIstance().getBonfire().getLocalTranslation();
			spawnPoint.x += 2f;
		}
		this.characterControl.warp(spawnPoint);
		this.addPhysicsSpace();
		GameManager.getIstance().getBullet().getPhysicsSpace().add(this);
		this.notifyUpdate(false);
	}

	/** this method reset lifebar */
	public void resetProgressBar() {
		if (this.lifeBarThief != null)
			this.lifeBarThief.markForRemoval();
		final ImageBuilder builder = new ImageBuilder() {
			{
				filename("Interface/Image/LifeBar/innerLife.png");
				x("12%");
				y("87%");
				width(SIZELIFEBAR + "%");
				height("2%");
				imageMode("resize:7,2,7,7,7,2,7,2,7,2,7,7");
			}
		};
		final Element layer = GameManager.getIstance().getNifty().getScreen("lifeBarScreen")
				.findElementByName("panelProgressBar");
		this.lifeBarThief = builder.build(GameManager.getIstance().getNifty(),
				GameManager.getIstance().getNifty().getCurrentScreen(), layer);
		this.lifeBarThief.getParent().layoutElements();

	}

	/** only in single player Thief says something when is not moveing */
	public void saySomething() {
		if (!this.multiplayer && !GameManager.getIstance().isPaused() && this.canTalk) {
			if (((int) System.currentTimeMillis() - this.currentTime) / 1000 == this.talkFrequence
					&& this.bonfireSound.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.deathSound.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.enemyView.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.enemyWin.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.scream1.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.scream2.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.scream3.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.scream4.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.swordSound.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.voice1.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.voice2.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.voice3.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.voice4.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.voice5.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.voice6.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.voice7.getAudioNode().getStatus().equals(Status.Stopped)
					&& this.walkingOnGrassSound.getAudioNode().getStatus().equals(Status.Stopped) && this.alive) {
				this.resetCurrentTime();
				int rand = ((int) (Math.random() * 7)) + 1;

				switch (rand) {
				case 1:
					this.voice1.playSound();
					break;
				case 2:
					this.voice2.playSound();
					break;
				case 3:
					this.voice3.playSound();
					break;
				case 4:
					this.voice4.playSound();
					break;
				case 5:
					this.voice5.playSound();
					break;
				case 6:
					this.voice6.playSound();
					break;
				case 7:
					this.voice7.playSound();
					break;
				default:
					break;
				}
			}
			if (((int) System.currentTimeMillis() - this.currentTime) / 1000 > this.talkFrequence) {
				this.currentTime = (int) System.currentTimeMillis();
			}
		}
	}

	/** say something when Thief can see an enemy */
	public void playEnemyView() {
		if (!this.multiplayer)
			if (!this.waitAnimation)
				this.enemyView.playSound();
	}

	/** check which button is pressed */
	public AnalogListener analogListener = new AnalogListener() {
		public void onAnalog(String name, float value, float tpf) {
			if ((name.equals(run) && NodeThief.this.alive && !NodeThief.this.waitAnimation)
					&& !GameManager.getIstance().isPaused()) {
				NodeThief.this.run();
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

	/** check which button is pressed */
	public ActionListener actionListener = new ActionListener() {
		public void onAction(String name, boolean pressed, float value) {
			if ((name.equals(run) && pressed && NodeThief.this.alive && !NodeThief.this.waitAnimation)
					&& !GameManager.getIstance().isPaused()) {
				NodeThief.this.isRun = true;
				NodeThief.this.channel.setAnim(run);
				NodeThief.this.notifyUpdate(false);
			} else if ((name.equals(run) && !pressed && NodeThief.this.alive && !NodeThief.this.waitAnimation)
					&& !GameManager.getIstance().isPaused()) {
				NodeThief.this.stop();
				NodeThief.this.isRun = false;
				NodeThief.this.channel.setAnim(idle);
				NodeThief.this.notifyUpdate(false);
			} else if ((name.equals(attack1) && pressed && NodeThief.this.alive && NodeThief.this.alive
					&& !NodeThief.this.waitAnimation) && !GameManager.getIstance().isPaused()) {
				NodeThief.this.isRun = false;
				NodeThief.this.waitAnimation = true;
				if (!NodeThief.this.changeAttack) {
					NodeThief.this.channel.setAnim(attack1);
					NodeThief.this.channel.setSpeed(3f);
				} else {
					NodeThief.this.channel.setAnim(attack2);
					NodeThief.this.channel.setSpeed(2f);
				}
				NodeThief.this.startAttack();
				NodeThief.this.changeAttack = !NodeThief.this.changeAttack;
				NodeThief.this.channel.setLoopMode(LoopMode.DontLoop);
				NodeThief.this.notifyUpdate(true);
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
				} else {
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
					GameManager.getIstance().getNifty().getScreen("lifeBarScreen").findElementByName("#chat-text-input")
							.setFocus();
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
			if ((name.equals("win") && !win && NodeThief.this.alive && !NodeThief.this.waitAnimation)
					&& !GameManager.getIstance().isPaused()) {
				NodeThief.this.nearToPortal();
				// GameManager.getIstance().pauseGame();
			} else if (win && pressed) {
				System.out.println("pressed");
				GameManager.getIstance().getSinglePlayer().quitGame();

			}
		
	
	     else if ((name.equals(chatBox) && !isSinglePlayer) && !pressed) {
		if (!chatboxIsEnable) {
		    Element el = GameManager.getIstance().getNifty().getScreen("lifeBarScreen")
			    .findElementByName("chatMultiPlayer");
		    el.setVisible(!el.isVisible());
		    GameManager.getIstance().getNifty().getScreen("lifeBarScreen").findElementByName("#chat-text-input")
			    .setFocus();
		    GameManager.getIstance().getApplication().getInputManager().setCursorVisible(true);
		    NodeThief.this.getCamera().setEnabled(false);

		    chatboxIsEnable = !chatboxIsEnable;
		} else {
		    Element el = GameManager.getIstance().getNifty().getScreen("lifeBarScreen")
			    .findElementByName("chatMultiPlayer");
		    el.setVisible(!el.isVisible());
		    GameManager.getIstance().getApplication().getInputManager().setCursorVisible(false);
		    NodeThief.this.getCamera().setEnabled(true);
		    NodeThief.this.getCamera().setDragToRotate(false);
		    chatboxIsEnable = !chatboxIsEnable;
		}
	    }
	    if ((name.equals("win") && !win && NodeThief.this.alive && !NodeThief.this.waitAnimation)
		    && !GameManager.getIstance().isPaused()) {
		NodeThief.this.nearToPortal();
		// GameManager.getIstance().pauseGame();
	    } else if (win && pressed) {
		System.out.println("pressed");
		GameManager.getIstance().getSinglePlayer().quitGame();
		}}};

	/** jmonkey's method */
	@Override
	public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String arg2) {

		if (arg2.equals(attack1)) {
			arg1.setAnim(idle);
			NodeThief.this.waitAnimation = false;
			NodeThief.this.endAttack();
		}

		if (arg2.equals(attack2)) {
			NodeThief.this.waitAnimation = false;
			arg1.setAnim(idle);
			NodeThief.this.endAttack();
		}
		if (arg2.equals(bonfire)) {
			arg1.setAnim(idle);
			NodeThief.this.waitAnimation = false;
			for (NodeCharacter enemy : GameManager.getIstance().getEnemies()) {
				if (enemy instanceof NodeEnemy)
					enemy.resetAll();
			}
		}
		if (arg2.equals(death)) {
			arg1.setAnim(idle);
			NodeThief.this.waitAnimation = false;
			for (NodeCharacter enemy : GameManager.getIstance().getEnemies()) {
				if (enemy instanceof NodeEnemy)
					enemy.resetAll();
			}
			NodeThief.this.startAgain();
		}
	}

	/** this methos is called when main character is death */
	@Override
	public void death() {
		if (this.alive) {
			super.death();
			this.resetCurrentTime();
			this.characterControl.setWalkDirection(new Vector3f(0, -2f, 0));
			this.walkingOnGrassSound.stopSound();
			GameManager.getIstance().sortScorePlyer();
		}
	}

	/** this method check if there is a collition with enemies */
	@Override
	public void checkCollition() {
		for (NodeCharacter enemy : GameManager.getIstance().getEnemies()) {
			final CollisionResults collisionResult = new CollisionResults();
			final BoundingBox box = (BoundingBox) this.node.getChild(0).getWorldBound();
			enemy.collideWith(box, collisionResult);
			final CollisionResult closest = collisionResult.getClosestCollision();
			if (closest != null) {
				if (enemy instanceof NodeEnemy) {
					enemy.isStricken(this.getDAMAGE());
					((NodeEnemy) enemy).getLifeBar().updateLifeBar(this.getDAMAGE());
					if (enemy.isDead()) {
						this.enemyWin.playSound();
						((NodeEnemy) enemy).getLifeBar().updateLifeBar(0);
						((NodeEnemy) enemy).getLifeBar().setVisibleLifeBar();
					}
				} else if (enemy instanceof NodeEnemyPlayers) {
					if (enemy.isDead()) {
						this.killSomeOne();
						GameManager.getIstance().sortScorePlyer();
						((NodeEnemyPlayers) enemy).getLifeBar().updateLifeBar(0);
						((NodeEnemyPlayers) enemy).getLifeBar().setVisibleLifeBar();
					}
				}
			}
		}
	}

	/** this method start attack */
	@Override
	public void startAttack() {
		this.resetCurrentTime();
		super.startAttack();
		this.checkCollition();
		this.playScream();

	}

	@Override
	public void isStricken(int DAMAGE) {
		// TODO Auto-generated method stub
		super.isStricken(DAMAGE);
		this.setDamageLifeBar();
		this.notifyUpdate(false);
	}

	/** this method is called if lifebar isn't create */
	@Override
	public void setLifeBar(Element lifeBar, Element border, String nameModel) {
		this.lifeBarThief = lifeBar;
		this.borderLifeBarThief = border;
		final NiftyImage imageLifeBarThief = GameManager.getIstance().getNifty().getRenderEngine().createImage(null,
				"Interface/MultiPlayer/borderLifeCharacters/borderLife" + nameModel + ".png", false);
		this.borderLifeBarThief.getRenderer(ImageRenderer.class).setImage(imageLifeBarThief);
		this.borderLifeBarThief.getParent().layoutElements();
		this.resetProgressBar();

	}

	/** this method set lifebar's dimension */
	@Override
	public void setDamageLifeBar() {

		final int value = (life * SIZELIFEBAR) / STARTLIFE;
		this.lifeBarThief.setConstraintWidth(new SizeValue(value + "%"));
		this.lifeBarThief.getParent().layoutElements();
		if (life <= lifebarThreshold) {
			this.lifeBarThief.getRenderer(ImageRenderer.class).setImage(GameManager.getIstance().getNifty()
					.getRenderEngine().createImage(null, "Interface/Image/LifeBar/innerLifeRed.png", false));
			this.lifeBarThief.getParent().layoutElements();
		}
		if (life <= 0)
			this.lifeBarThief.setVisible(false);
	}

	/** this method setip sounds */
	@Override
	protected void setupAudio() {
		if (!GameManager.getIstance().isEditor()) {
			this.walkingOnGrassSound = new Sound(this, "WalkingOnGrass", false, false, false, 0.09f, false);
			this.scream1 = new Sound(this, "Scream1", false, false, false, 0.5f, false);
			this.scream2 = new Sound(this, "Scream2", false, false, false, 0.5f, false);
			this.scream3 = new Sound(this, "Scream3", false, false, false, 0.5f, false);
			this.scream4 = new Sound(this, "Scream4", false, false, false, 0.5f, false);
			if (!this.multiplayer) {
				this.swordSound = new Sound(this, "Sword", false, false, false, 0.1f, false);
				this.bonfireSound = new Sound(this, "Bonfire", false, false, false, 1.0f, false);
				this.voice1 = new Sound(this, "Voice1", false, false, false, 1.0f, false);
				this.voice2 = new Sound(this, "Voice2", false, false, false, 1.0f, false);
				this.voice3 = new Sound(this, "Voice3", false, false, false, 1.0f, false);
				this.voice4 = new Sound(this, "Voice4", false, false, false, 1.0f, false);
				this.voice5 = new Sound(this, "Voice5", false, false, false, 1.0f, false);
				this.voice6 = new Sound(this, "Voice6", false, false, false, 1.0f, false);
				this.voice7 = new Sound(this, "Voice7", false, false, false, 1.0f, false);
				this.deathSound = new Sound(this, "Death", false, false, false, 1.0f, false);
				this.enemyWin = new Sound(this, "EnemyWin", false, false, false, 1.0f, false);
				this.enemyView = new Sound(this, "EnemyView", false, false, false, 1.0f, false);
			}
		}
	}

	/** this method get chase camera */
	public ChaseCamera getCamera() {
		return this.camera;
	}

	/** this method get controll */
	public BetterCharacterControl getControl() {
		return this.characterControl;
	}

	/** this method get isRun */
	public boolean isRun() {
		return this.isRun;
	}

	/** this method get isSinglePlayer */
	public boolean isSinglePlayer() {
		return isSinglePlayer;
	}

	/** this method set singlePlayer */
	public void setSinglePlayer(boolean isSinglePlayer) {
		this.isSinglePlayer = isSinglePlayer;
	}

	/** return all sounds about this model */
	@Override
	public ArrayList<Sound> getAllSound() {
		this.canTalk = false;
		return super.getAllSound();
	}
}