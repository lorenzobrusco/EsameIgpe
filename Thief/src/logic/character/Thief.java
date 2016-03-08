package logic.character;

import java.util.ArrayList;
import java.util.List;

import control.GameManager;
import logic.object.Inventary;
import logic.object.inventary.Arrow;
import logic.object.inventary.Bow;
import logic.object.inventary.Sword;
import logic.object.inventary.Weapon;
import thread.Shoot;

public class Thief extends Character {

    private boolean hidden;

    private List<ElementOfWorld> arrows_in_flight;

    private boolean squatting;

    private Inventary inventary;

    private Weapon armed;

    public Thief(float x, float y, float z, int life, int damage) {
	super(x, y, z, life, damage, 42, 42);
	this.hidden = false;
	arrows_in_flight = new ArrayList<ElementOfWorld>();
	this.squatting = false;
	this.inventary = new Inventary();
	this.armed = inventary.getBow();
    }

    public boolean isHidden() {
	return hidden;
    }

    public void setHidden(boolean hidden) {
	this.hidden = hidden;
    }

    public void insertElement(ElementOfWorld e) {
	this.arrows_in_flight.add(e);
    }

    public List<ElementOfWorld> getArrowsInFlight() {
	return arrows_in_flight;
    }

    public void setSquatting() {
	this.squatting = !this.squatting;
    }

    public boolean isSquatting() {
	return squatting;
    }

    public Inventary getInventary() {
	return inventary;
    }

    public Weapon getArmed() {
	return armed;
    }

    public void changeArmed() {
	if (armed instanceof Bow)
	    armed = inventary.getSword();
	else
	    armed = inventary.getBow();
    }

    public void changespeed() {
	if (super.speed == 2)
	    super.run();
	else
	    super.walk();
    }

    public void attackSword() {
	if (GameManager.getIstance().getControl().searchCollision(this).second != null) {
	    if (GameManager.getIstance().getControl().searchCollision(this).second instanceof Enemy) {
		if (this.distance(this,
			GameManager.getIstance().getControl().searchCollision(
				this).second) <= (GameManager.getIstance().getControl().searchCollision(this).second
					.getSizex()))
		    ((Enemy) GameManager.getIstance().getControl().searchCollision(this).second)
			    .damage(((Sword) getArmed()).getDamage());
		if (!((Enemy) GameManager.getIstance().getControl().searchCollision(this).second).isAlive())
		    GameManager.getIstance().getControl().getWorld()
			    .removeElementsOfWord(GameManager.getIstance().getControl().searchCollision(this).second);
	    }
	}
    }

    public void attackBow(int x, int y) {
	Arrow arrow = new Arrow(this.getX(), this.getY(), this.getZ(), 2, 2);
	this.insertElement(arrow);
	new Shoot(arrow, x, y).start();
    }

}
