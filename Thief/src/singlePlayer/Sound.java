package singlePlayer;

import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import control.GameManager;
import singlePlayer.model.NodeCharacter;
import singlePlayer.model.NodeModel;

/**
 * 
 * this class handles sounds
 *
 */

public class Sound {

    /**
     * This class is used to manage audio files
     */

    /** AudioNode where audio file is stored */
    private AudioNode sound;
    /** control value to know if this audio file has been already played */
    private boolean played;

    /** constructor */
    public Sound(Node node, String soundName, boolean reverb, boolean positional, boolean loop, float volume,
	    boolean played) {

	/** setup audio file for special models */
	if (!(node instanceof NodeModel)) {
	    this.sound = new AudioNode(GameManager.getIstance().getApplication().getAssetManager(),
		    "Sounds/" + soundName + ".ogg");
	} else {
	    /** setup audio file for characters */
	    if (node instanceof NodeCharacter) {
		if (node.getName().contains("ogremesh")) {
		    String[] name = node.getName().split("-");
		    this.sound = new AudioNode(GameManager.getIstance().getApplication().getAssetManager(),
			    "Models/Characters/" + name[0] + "/Sounds/" + soundName + ".ogg");
		} else {
		    this.sound = new AudioNode(GameManager.getIstance().getApplication().getAssetManager(),
			    "Models/Characters/" + node.getName() + "/Sounds/" + soundName + ".ogg");
		}
	    }
	    /** setup aduio file for buildings */
	    if (node instanceof NodeModel) {
		String name = node.getName();
		if (name.equals("Chapel")) {
		    this.sound = new AudioNode(GameManager.getIstance().getApplication().getAssetManager(),
			    "Models/Buildings/" + node.getName() + "/Sounds/" + soundName + ".ogg");
		}
		if (name.equals("Bonfire") || name.equals("Portal")) {
		    this.sound = new AudioNode(GameManager.getIstance().getApplication().getAssetManager(),
			    "Models/Specials/" + node.getName() + "/Sounds/" + soundName + ".ogg");
		}
	    }
	}
	this.setup(node, soundName, reverb, positional, loop, volume, played);
    }

    /** real audio file instantiation instantiation */
    private void setup(Node node, String soundName, boolean reverb, boolean positional, boolean loop, float volume,
	    boolean played) {
	this.played = played;
	this.sound.setVolume(volume);
	this.sound.setLocalTranslation(node.getLocalTranslation());
	this.sound.setLocalRotation(node.getLocalRotation().inverse());
	this.sound.setReverbEnabled(reverb);
	this.sound.setPitch(1.0f);
	this.sound.setPositional(positional);
	this.sound.setDirectional(positional);
	this.sound.setLooping(loop);
	this.sound.setRefDistance(10.0f);
	this.sound.setMaxDistance(20.0f);
    }

    /** play this sound */
    public void playSound() {
	this.sound.play();
    }

    /** play only once this sound */
    public void playSoundIstance() {
	this.sound.playInstance();
    }

    /** stop playing this sound */
    public void stopSound() {
	this.sound.stop();
    }

    /** return this audio file */
    public AudioNode getAudioNode() {
	return this.sound;
    }

    /** set this audio file as played or not */
    public void setPlayed(boolean played) {
	this.played = played;
    }

    /** return if this audio file has been played or not */
    public boolean isPlayed() {
	return this.played;
    }
}
