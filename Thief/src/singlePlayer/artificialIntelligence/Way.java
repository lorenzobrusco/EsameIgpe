package singlePlayer.artificialIntelligence;

import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.WireBox;
import control.GameManager;
import singlePlayer.model.NodeEnemy;

public class Way {

    private final NodeEnemy ENEMY;
    private final Node leftBox;
    private final Node rightBox;
    private final Node backBox;

    public Way(final NodeEnemy ENEMY) {
	this.ENEMY = ENEMY;
	this.leftBox = new Node("left");
	this.rightBox = new Node("right");
	this.backBox = new Node("back");
	ENEMY.attachChild(this.leftBox);
	ENEMY.attachChild(this.rightBox);
	ENEMY.attachChild(this.backBox);
    }

    public void newWay() {
	this.makeBox(leftBox, this.ENEMY.getCharacterControl().getViewDirection().add(new Vector3f(5f, 0f, 0f)));
	this.makeBox(rightBox, this.ENEMY.getCharacterControl().getViewDirection().add(new Vector3f(5f, 0f, 0f)));
	this.makeBox(backBox, this.ENEMY.getCharacterControl().getViewDirection().add(new Vector3f(5f, 0f, 0f)));
    }

    public void deleteOldWay() {
	ENEMY.detachChild(backBox);
	ENEMY.detachChild(leftBox);
	ENEMY.detachChild(rightBox);
    }

    public Vector3f getWay() {
	Vector3f result = new Vector3f();
	return result;
    }

    private void makeBox(Node box, Vector3f localPosition) {

	BoundingBox boundingBox = new BoundingBox();
	boundingBox.setXExtent(1.5f);
	boundingBox.setYExtent(0.5f);
	boundingBox.setZExtent(1.5f);

	final WireBox wireBox2 = new WireBox();
	wireBox2.fromBoundingBox(boundingBox);
	wireBox2.setLineWidth(0f);
	final Geometry boxAttach = new Geometry("boxAttach", wireBox2);

	final Material material = new Material(GameManager.getIstance().getApplication().getAssetManager(),
		"Common/MatDefs/Misc/Unshaded.j3md");
	boxAttach.setMaterial(material);

	material.setColor("Color", ColorRGBA.Red);
	// material.getAdditionalRenderState().setDepthWrite(true);
	// material.getAdditionalRenderState().setColorWrite(true);
	//
	// boxAttach.setQueueBucket(Bucket.Transparent);

	box.setLocalTranslation(localPosition);
	box.attachChild(boxAttach);
	boundingBox.setCenter(boxAttach.getLocalTranslation());

    }

    public Node getLeftBox() {
	return leftBox;
    }

    public Node getRightBox() {
	return rightBox;
    }

    public Node getBackBox() {
	return backBox;
    }

    public NodeEnemy getENEMY() {
	return ENEMY;
    }

}
