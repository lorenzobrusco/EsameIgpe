package multiPlayer;

import com.jme3.math.Vector3f;

/**
 * 
 * this class get main character's state to prevent duplicate states
 *
 */

public class ModelState {

    /** walk direction */
    private Vector3f walk;
    /** view direction */
    private Vector3f view;
    /** location */
    private Vector3f location;
    /** it's true when main character attach */
    private boolean attack;
    /** life */
    private int life;
    /** player's score */
    private int score;

    public ModelState(Vector3f walk, Vector3f view, int life, boolean attack, Vector3f location, int score) {

	this.walk = walk;
	this.view = view;
	this.location = location;
	this.life = life;
	this.attack = attack;
	this.score = score;
    }

    /** get attach */
    public boolean isAttack() {
	return attack;
    }

    /** set attack */
    public void setAttack(boolean attack) {
	this.attack = attack;
    }

    /** get walk direction */
    public Vector3f getWalk() {
	return walk;
    }

    /** set walk direction */
    public void setWalk(Vector3f walk) {
	this.walk = walk;
    }

    /** get view direction */
    public Vector3f getView() {
	return view;
    }

    /** set view direction */
    public void setView(Vector3f view) {
	this.view = view;
    }

    /** get location */
    public Vector3f getLocation() {
	return location;
    }

    /** set location */
    public void setLocation(Vector3f location) {
	this.location = location;
    }

    /** get life */
    public int getLife() {
	return life;
    }

    /** set life */
    public void setLife(int life) {
	this.life = life;
    }

    /**this method get score*/
    public int getScore() {
        return score;
    }

    /**this method set score*/
    public void setScore(int score) {
        this.score = score;
    }

    
    
}
