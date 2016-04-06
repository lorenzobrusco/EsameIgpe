package singlePlayer.model;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import control.GameManager;
import singlePlayer.Sound;

public class NodeModel extends Node {

	protected Spatial spatial;
	protected BetterCharacterControl characterControl;
	protected final Vector3f dimensionControll;
	protected final Vector3f normalGravity = new Vector3f(0, -9.81f, 0);
	private Sound chapelSound;
	private Sound bonfireSound;

	public NodeModel(Spatial model, Vector3f dimensionControll) {
		this.spatial = model;
		this.spatial.setShadowMode(RenderQueue.ShadowMode.Inherit);
		this.setName(this.spatial.getName());
		this.dimensionControll = dimensionControll;
		this.attachChild(spatial);
		this.spatial.setLocalTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
		this.setupAudio();
	}

	public NodeModel(String path, Vector3f dimensionControll) {
		this.spatial = GameManager.getIstance().getApplication().getAssetManager().loadModel("Models/" + path );
		this.spatial.setShadowMode(RenderQueue.ShadowMode.Inherit);
		this.setName(this.spatial.getName());
		this.dimensionControll = dimensionControll;
		this.attachChild(spatial);
		this.spatial.setLocalTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
		this.setupAudio();
	}

	public NodeModel(Spatial model, Vector3f dimensionControll, Vector3f intersection) {
		this.spatial = model;
		this.spatial.setShadowMode(RenderQueue.ShadowMode.Inherit);
		this.setName(this.spatial.getName());
		this.dimensionControll = dimensionControll;
		this.moveModel(intersection);
		this.attachChild(spatial);
		this.spatial.setLocalTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
		this.setupAudio();
	}

	public NodeModel(String path, Vector3f dimensionControll, Vector3f intersection) {
		System.out.println("secondo");
		System.out.println(path);
		this.spatial = GameManager.getIstance().getApplication().getAssetManager().loadModel("Models/" + path);
		this.spatial.setShadowMode(RenderQueue.ShadowMode.Inherit);
		this.setName(this.spatial.getName());
		this.dimensionControll = dimensionControll;
		this.moveModel(intersection);
		this.attachChild(spatial);
		this.spatial.setLocalTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
		this.setupAudio();
	}

	public void addCharacterControll() {
		this.characterControl = new BetterCharacterControl(dimensionControll.x, dimensionControll.y,
				dimensionControll.z);
		this.characterControl.setGravity(normalGravity);
		this.addControl(characterControl);
	}

	public void detachCaharacterControll() {
		this.removeControl(characterControl);
	}

	public Spatial getModel() {
		return spatial;
	}

	public BetterCharacterControl getCharacterControl() {
		return characterControl;
	}

	public void moveModel(Vector3f intersection) {
		this.spatial.setLocalTranslation(intersection);
		this.setLocalTranslation(spatial.getLocalTranslation());
	}

	protected void setupAudio() {
		if (this.name.equals("Chapel")) {
			this.chapelSound = new Sound(this, "Bell", false, true, false, 1.5f, false);
		}
		if (this.name.equals("Bonfire")) {
			this.bonfireSound = new Sound(this, "Bonfire", false, true, true, 1.5f, false);
		}
	}

	public void playChapelSound() {
		if (this.chapelSound != null)
			this.chapelSound.playSound();
	}

	public void stopChapelSound() {
		if (this.chapelSound != null)
			this.chapelSound.stopSound();
	}

	public void playBonfireSound() {
		if (this.bonfireSound != null) {
			this.bonfireSound.playSound();
		}
	}

	public void stopBonfireSound() {
		if (this.bonfireSound != null)
			this.bonfireSound.stopSound();
	}
}
