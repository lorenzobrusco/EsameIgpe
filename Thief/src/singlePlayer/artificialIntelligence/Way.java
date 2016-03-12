package singlePlayer.artificialIntelligence;

import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.WireBox;

import control.GameManager;
import singlePlayer.model.NodeEnemy;
import singlePlayer.model.NodeModel;

public class Way {

	private final NodeEnemy ENEMY;

	public Way(final NodeEnemy ENEMY) {
		this.ENEMY = ENEMY;
		this.getWay();
	}

	public Vector3f getWay() {
		Vector3f result = new Vector3f();
		Node frontBox = this.makeBox(new Vector3f(this.ENEMY.getLocalTranslation().x + 5,
				this.ENEMY.getLocalTranslation().y, this.ENEMY.getLocalTranslation().z));
		Node backBox = this.makeBox(new Vector3f(this.ENEMY.getLocalTranslation().x - 5,
				this.ENEMY.getLocalTranslation().y, this.ENEMY.getLocalTranslation().z));
		Node leftBox = this.makeBox(new Vector3f(this.ENEMY.getLocalTranslation().x, this.ENEMY.getLocalTranslation().y,
				this.ENEMY.getLocalTranslation().z + 5));
		Node rightBox = this.makeBox(new Vector3f(this.ENEMY.getLocalTranslation().x,
				this.ENEMY.getLocalTranslation().y, this.ENEMY.getLocalTranslation().z - 5));

		GameManager.getIstance().getTerrain().attachChild(frontBox);
		GameManager.getIstance().getTerrain().attachChild(backBox);
		GameManager.getIstance().getTerrain().attachChild(leftBox);
		GameManager.getIstance().getTerrain().attachChild(rightBox);

		CollisionResults frontCollitions = new CollisionResults();
		CollisionResults backCollitions = new CollisionResults();
		CollisionResults leftCollitions = new CollisionResults();
		CollisionResults rightCollitions = new CollisionResults();

//		for (NodeModel node : GameManager.getIstance().getNodeModel()) {
//			frontBox.collideWith(ENEMY.getWorldBound(), leftCollitions);
//			System.out.println(leftCollitions.size());
//		}

		return result;
	}

	private Node makeBox(Vector3f localPosition) {

		Node box = new Node("box test");
		box.setLocalTranslation(localPosition);

		BoundingBox boundingBox = new BoundingBox();
		boundingBox.setXExtent(1.5f);
		boundingBox.setYExtent(0.5f);
		boundingBox.setZExtent(1.5f);

		final WireBox wireBox2 = new WireBox();
		wireBox2.fromBoundingBox(boundingBox);
		wireBox2.setLineWidth(0f);
		final Geometry boxAttach = new Geometry("boxAttach", wireBox2);
		boxAttach.setLocalTranslation(0, 2.5f, 4f);

		final Material material = new Material(GameManager.getIstance().getApplication().getAssetManager(),
				"Common/MatDefs/Misc/Unshaded.j3md");
		boxAttach.setMaterial(material);

		material.setColor("Color", ColorRGBA.Red);
		// material.getAdditionalRenderState().setDepthWrite(true);
		// material.getAdditionalRenderState().setColorWrite(true);
		//
		// boxAttach.setQueueBucket(Bucket.Transparent);

		box.attachChild(boxAttach);
		boundingBox.setCenter(boxAttach.getLocalTranslation());

		return box;

	}

	public NodeEnemy getENEMY() {
		return ENEMY;
	}

}
