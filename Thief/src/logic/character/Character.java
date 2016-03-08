package logic.character;

import control.GameManager;

public class Character extends ElementOfWorld implements Move {

    private int life;
    private int damage;
    private Character tmp;
    protected int speed;

    public Character(float x, float y, float z, int life, int damage, int sizex, int sizey) {
	super(x, y, z, sizex, sizey);
	this.life = life;
	this.damage = damage;
	tmp = this;
	speed = 2;
    }

    public int getLife() {
	return life;
    }

    public void setLife(int life) {
	this.life = life;
    }

    public int getDamage() {
	return damage;
    }

    public void setDamage(int damage) {
	this.damage = damage;
    }

    public boolean isAlive() {
	return life > 0;
    }

    @Override
    public void moveUp() {

	tmp.setY(tmp.getY() - speed);
	searchCollison('w');
    }

    @Override
    public void moveDown() {

	tmp.setY(tmp.getY() + speed);
	searchCollison('s');
    }

    @Override
    public void moveLeft() {

	tmp.setX(tmp.getX() - speed);
	searchCollison('a');
    }

    public void run() {
	speed = 4;
    }

    public void walk() {
	speed = 2;
    }

    @Override
    public void moveRight() {

	tmp.setX(tmp.getX() + speed);
	searchCollison('d');
    }

    protected boolean searchCollison(char direction) {

	if (GameManager.getIstance().getControl().searchCollision(this).first) {
	    switch (direction) {
	    case 'w':
		this.setY(this.getY() + speed);
		break;

	    case 's':
		this.setY(this.getY() - speed);
		break;

	    case 'd':
		this.setX(this.getX() - speed);
		break;

	    case 'a':
		this.setX(this.getX() + speed);
		break;

	    default:
		break;
	    }
	    return true;
	}

	return false;
    }

}
