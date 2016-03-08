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
	private Sound ambientSonund;

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
		this.spatial = GameManager.getIstance().getApplication().getAssetManager().loadModel("Models/" + path);
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
		// TODO if temporaneo
		if (this.name.equals("Chapel")) {
			this.ambientSonund = new Sound(this, "Bell", false, true, false, 1.5f, false);
		}
	}

	public void playSound() {
		if (this.ambientSonund != null)
			this.ambientSonund.playSound();
	}

	public void stopSound() {
		if (this.ambientSonund != null)
			this.ambientSonund.stopSound();
	}
}
