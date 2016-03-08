package singlePlayer.model;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import singlePlayer.artificialIntelligence.IA;

public class NodeEnemy extends NodeCharacter {

	private final IA ia;

	public NodeEnemy(Spatial model, Vector3f dimensionControll, Vector3f intersection) {
		super(model, dimensionControll, intersection, 50, 10);
		this.ia = new IA(this);
	}

	public void runIntelligence() {
		this.ia.run();
	}

	public void stopAnimation() {
		this.channel.setAnim(this.idle);
	}

	public void runAnimation() {
		this.channel.setAnim(this.run);
	}

	public void attack1Animation() {
		this.channel.setAnim(this.attack1);
	}

	public void attack2Animation() {
		this.channel.setAnim(this.attack2);
	}
}
