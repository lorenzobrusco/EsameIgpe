package singlePlayer.model;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import control.GameManager;

/**
 * 
 * this class is used to painting enemy's lifebar
 *
 */

public class LifeBar extends Node {

    /** minimum value to painting red lifebar */
    private final int limitRedLife;
    /** initial color */
    private Box greenBox;
    /** jmonkey's object */
    private Geometry geometryGreenBox;
    /** jmonkey's object */
    private Material materialGreenBox;
    /** red lifebar */
    private Material materialRedBox;
    /** life's value */
    private int life;
    /** model to take care */
    private final NodeCharacter character;

    /** builder */
    public LifeBar(NodeCharacter character) {

	this.character = character;
	this.life = this.character.getLife();
	this.limitRedLife = (this.character.getLife() * 40) / 100;
	greenBox = new Box(1f, 0.05f, 0.05f);
	geometryGreenBox = new Geometry("GreenBox", greenBox);
	materialGreenBox = new Material(GameManager.getIstance().getApplication().getAssetManager(),
		"Common/MatDefs/Misc/Unshaded.j3md");
	materialGreenBox.setColor("Color", ColorRGBA.Green);
	materialGreenBox.setTexture("ColorMap",
		GameManager.getIstance().getApplication().getAssetManager().loadTexture("Interface/Image/LifeBar/lifeBar.png"));
	geometryGreenBox.setMaterial(materialGreenBox);

	materialRedBox = materialGreenBox.clone();
	materialRedBox.setColor("Color", ColorRGBA.Red);

	this.geometryGreenBox.setLocalTranslation(0f, 5f, 0f);

	this.character.attachChild(geometryGreenBox);
    }

    /** this method calculate remaining life before thief' attach */
    public void updateLifeBar(int damage) {

	if (damage >= life) {
	    this.setVisibleLifeBar();
	} else {
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
	    greenBox.zExtent = 0;
	}

    }

    /** this method set invisible lifebar */
    public void setVisibleLifeBar() {
	greenBox.xExtent = 0;
	greenBox.yExtent = 0;
	greenBox.zExtent = 0;
	this.updateModelBound();
	this.updateGeometricState();
    }

}
