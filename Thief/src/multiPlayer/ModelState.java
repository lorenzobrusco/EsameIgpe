package multiPlayer;

import com.jme3.math.Vector3f;

public class ModelState {

    private Vector3f walk;
    private Vector3f view;
    private Vector3f location;
    private boolean attack;
    private int life;

    public ModelState(Vector3f walk, Vector3f view, int life, boolean attack, Vector3f location) {

	this.walk = walk;
	this.view = view;
	this.location = location;
	this.life = life;
	this.attack = attack;
    }

    public boolean isAttack() {
	return attack;
    }

    public void setAttack(boolean attack) {
	this.attack = attack;
    }

    public Vector3f getWalk() {
	return walk;
    }

    public void setWalk(Vector3f walk) {
	this.walk = walk;
    }

    public Vector3f getView() {
	return view;
    }

    public void setView(Vector3f view) {
	this.view = view;
    }
    
    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public int getLife() {
	return life;
    }

    public void setLife(int life) {
	this.life = life;
    }

}
