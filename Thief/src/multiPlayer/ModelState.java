package multiPlayer;

import com.jme3.math.Vector3f;

public class ModelState {

    private Vector3f walk;
    private Vector3f view;
    private boolean attack;
    private int life;

    public ModelState(Vector3f walk, Vector3f view, int life, boolean attack) {

	this.walk = walk;
	this.view = view;
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

    public int getLife() {
	return life;
    }

    public void setLife(int life) {
	this.life = life;
    }

}
