package singlePlayer;

import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import control.GameManager;
import singlePlayer.model.NodeCharacter;
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
			// TODO aggiorna contatote di thief -> non sparare cazzate se ti
			// stanno attaccando
			if (node instanceof NodeCharacter) {
				this.sound = new AudioNode(GameManager.getIstance().getApplication().getAssetManager(),
						"Models/Characters/" + node.getName() + "/Sounds/" + soundName + ".ogg");
			}
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
