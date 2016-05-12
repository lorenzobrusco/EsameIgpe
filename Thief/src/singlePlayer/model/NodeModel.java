package singlePlayer.model;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import control.GameManager;
import singlePlayer.Sound;

/**
 * 
 * this class is used to create a model
 *
 */

public class NodeModel extends Node {

    /** model 3d */
    protected Spatial spatial;
    /** model's control */
    protected BetterCharacterControl characterControl;
    /** control's dimension */
    protected final Vector3f dimensionControll;
    /** gravity */
    protected final Vector3f normalGravity = new Vector3f(0, -9.81f, 0);
    /** chapel's sound */
    private Sound chapelSound;
    /** bonfire's sound */
    private Sound bonfireSound;

    /** builder */
    public NodeModel(Spatial model, Vector3f dimensionControll) {
	this.spatial = model;
	this.spatial.setShadowMode(RenderQueue.ShadowMode.Inherit);
	this.setName(this.spatial.getName());
	this.dimensionControll = dimensionControll;
	this.attachChild(spatial);
	this.spatial.setLocalTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
	this.setupAudio();
    }

    /** builder */
    public NodeModel(String path, Vector3f dimensionControll) {
	this.spatial = GameManager.getIstance().getApplication().getAssetManager().loadModel("Models/" + path);
	this.spatial.setShadowMode(RenderQueue.ShadowMode.Inherit);
	this.setName(this.spatial.getName());
	this.dimensionControll = dimensionControll;
	this.attachChild(spatial);
	this.spatial.setLocalTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
	this.setupAudio();
    }

    /** builder */
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

    /** builder */
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

    /** this method setup sounds */
    protected void setupAudio() {
	if (this.name.equals("Chapel")) {
	    this.chapelSound = new Sound(this, "Bell", false, true, false, 1.5f, false);
	}
	if (this.name.equals("Bonfire")) {
	    this.bonfireSound = new Sound(this, "Bonfire", false, true, true, 1.5f, false);
	}
    }

    /** this method add control */
    public void addCharacterControl() {
	this.characterControl = new BetterCharacterControl(dimensionControll.x, dimensionControll.y,
		dimensionControll.z);
	this.characterControl.setGravity(normalGravity);
	this.addControl(characterControl);
    }

    /** this method remove control */
    public void detachCaharacterControl() {
	this.removeControl(characterControl);
    }

    /** this method move model */
    public void moveModel(Vector3f intersection) {
	this.spatial.setLocalTranslation(intersection);
	this.setLocalTranslation(spatial.getLocalTranslation());
    }

    /** this method start chapel's sound */
    public void playChapelSound() {
	if (this.chapelSound != null)
	    this.chapelSound.playSound();
    }

    /** this method stop chapel's sound */
    public void stopChapelSound() {
	if (this.chapelSound != null)
	    this.chapelSound.stopSound();
    }

    /** this method play bonfire's sound */
    public void playBonfireSound() {
	if (this.bonfireSound != null) {
	    this.bonfireSound.playSound();
	}
    }

    /** this method stop bonfire's sound */
    public void stopBonfireSound() {
	if (this.bonfireSound != null)
	    this.bonfireSound.stopSound();
    }

    /** this method get model 3d */
    public Spatial getModel() {
	return spatial;
    }

    /** this method get control */
    public BetterCharacterControl getCharacterControl() {
	return characterControl;
    }
}
