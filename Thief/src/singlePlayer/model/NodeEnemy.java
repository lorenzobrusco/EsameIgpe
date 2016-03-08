package singlePlayer.model;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class NodeEnemy extends NodeCharacter {

	public NodeEnemy(Spatial model, Vector3f dimensionControll, Vector3f intersection) {
		super(model, dimensionControll, intersection, 50, 10);
	}
}
