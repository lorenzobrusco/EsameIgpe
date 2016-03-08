package logic.object.inventary;

public class Sword extends Weapon {

    private boolean attack;

    public Sword(int damage) {
	super(damage);
	attack = false;
    }

    public boolean isAttack() {
	return attack;
    }

    public void setAttack(boolean attack) {
	this.attack = attack;
    }

}
