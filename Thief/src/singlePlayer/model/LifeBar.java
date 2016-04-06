package singlePlayer.model;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

import control.GameManager;
import de.lessvoid.nifty.tools.SizeValue;

public class LifeBar extends Node {
	private int maxLife = 100;
	private final int limitRedLife;
	private Box greenBox;
	private Geometry geometryGreenBox;
	private Geometry geometryGrayBox;
	private Material materialGreenBox;
	private Material materialRedBox;
	private int life;
	private final NodeCharacter character;

	public LifeBar(NodeCharacter character) {

		ColorRGBA color = new ColorRGBA(0, 64, 0, 1);
		this.character = character;
		this.life = this.character.getLife();
		this.limitRedLife = (this.character.getLife() * 40) / 100;
		greenBox = new Box(1f, 0.05f, 0.05f);
		geometryGreenBox = new Geometry("GreenBox", greenBox);
		materialGreenBox = new Material(GameManager.getIstance().getApplication().getAssetManager(),
				"Common/MatDefs/Misc/Unshaded.j3md");
//		materialGreenBox.setColor("Color", color);
		materialGreenBox.setTexture("ColorMap", GameManager.getIstance().getApplication().getAssetManager().loadTexture("Images/lifeBar2.png"));
		geometryGreenBox.setMaterial(materialGreenBox);

		materialRedBox = materialGreenBox.clone();
		materialRedBox.setColor("Color", ColorRGBA.Red);

		this.geometryGreenBox.setLocalTranslation(0f, 5f, 0f);

		this.character.attachChild(geometryGreenBox);

	}

	boolean setLife(int damage) {

		if (this.life == damage)
			return false;

		int xGreenBox = damage / this.life;
		greenBox.xExtent = xGreenBox;

		System.out.println(xGreenBox + "--->" + damage);

		this.life = damage;
		if (damage < limitRedLife && maxLife > limitRedLife)
			geometryGreenBox.setMaterial(materialRedBox);

		else
			geometryGreenBox.setMaterial(materialGreenBox);
		greenBox.updateBound();
		greenBox.updateGeometry();
		geometryGreenBox.updateModelBound();
		// int xGreyBox = maxBoxDimension - xGreenBox;
		// grayBox.xExtent = xGreyBox;
		// grayBox.updateBound();
		// grayBox.updateGeometry();
		// geometryGreenBox.updateModelBound();
		return true;

	}

	public void updateLifeBar(int damage) {
		if (damage > this.life)
			// TODO
			;

		this.life -= damage;

		float xGreenBox = (life * greenBox.xExtent) / character.getStartLife();
		greenBox.xExtent = xGreenBox;
		if (life <= limitRedLife)
			geometryGreenBox.setMaterial(materialRedBox);

		else
			geometryGreenBox.setMaterial(materialGreenBox);
		greenBox.updateBound();
		greenBox.updateGeometry();
		geometryGreenBox.updateModelBound();

		if (life <= 0) {
			greenBox.xExtent = 0;
			greenBox.yExtent = 0;
			greenBox.zExtent = 0;
		}

	}

	public void setVisibleLifeBar(){
		greenBox.xExtent = 0;
		greenBox.yExtent = 0;
		greenBox.zExtent = 0;
		this.updateModelBound();
		this.updateGeometricState();
	}
	
	public Geometry getGreenGeometry() {
		return geometryGreenBox;
	}

	public Geometry getGrayGeometry() {
		return geometryGrayBox;
	}

}
