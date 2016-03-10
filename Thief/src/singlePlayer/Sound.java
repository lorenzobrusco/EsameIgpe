package singlePlayer;

import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import control.GameManager;
import singlePlayer.model.NodeModel;

public class Sound {

	private AudioNode sound;
	private boolean played;
	
	public Sound(Node node, String soundName, boolean reverb, boolean positional, boolean loop, float volume,
			boolean played) {
		if (!(node instanceof NodeModel)) {
			this.sound = new AudioNode(GameManager.getIstance().getApplication().getAssetManager(),
					"Sounds/" + soundName + ".ogg");
		} else {
			this.sound = new AudioNode(GameManager.getIstance().getApplication().getAssetManager(),
					"Models/" + node.getName() + "/Sounds/" + soundName + ".ogg");
		}
		this.setup(node, soundName, reverb, positional, loop, volume, played);
	}

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

	public void playSound() {
		this.sound.play();
	}

	public void playSoundIstance() {
		this.sound.playInstance();
	}

	public void stopSound() {
		this.sound.stop();
	}

	public AudioNode getAudioNode() {
		return this.sound;
	}

	public void setPlayed(boolean played) {
		this.played = played;
	}

	public boolean isPlayed() {
		return this.played;
	}
}
