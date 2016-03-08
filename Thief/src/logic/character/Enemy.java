package logic.character;

import java.awt.geom.Point2D;

import control.GameManager;
import thread.EnemyIntelligence;

public class Enemy extends Character {

    private EnemyIntelligence enemyntelligence;
    private boolean sighted;
    private Point2D.Double normalize;
    private final int ENEMY_VIEW = 100;
    private final int ENEMY_HEARING = 200;

    public Enemy(float x, float y, float z, int life, int damage) {
	super(x, y, z, life, damage, 32, 32);
	sighted = false;
	enemyntelligence = new EnemyIntelligence(this);
	enemyntelligence.start();
    }

    public void setSighted() {
	sighted = true;
    }

    public int getENEMY_VIEW() {
	return ENEMY_VIEW;
    }

    public int getENEMY_HEARING() {
	return ENEMY_HEARING;
    }

    public void damage(int damage) {
	super.setLife(super.getLife() - damage);
    }

    public void searchThief() {

	if (collition(this, GameManager.getIstance().getControl().getWorld().getThief())) {
	    this.setX((float) (this.getX() - normalize.x));
	    this.setY((float) (this.getY() - normalize.y));
	    return;
	}

	if ((distance(this, GameManager.getIstance().getControl().getWorld().getThief()) < ENEMY_VIEW) || sighted) {

	    normalize = new Point2D.Double(
		    (GameManager.getIstance().getControl().getWorld().getThief().getX() - this.getX())
			    / (float) (distanceFloat(this.getX(), this.getY(),
				    GameManager.getIstance().getControl().getWorld().getThief().getX(),
				    GameManager.getIstance().getControl().getWorld().getThief().getY())),
		    (GameManager.getIstance().getControl().getWorld().getThief().getY() - this.getY())
			    / (float) (distanceFloat(this.getX(), this.getY(),
				    GameManager.getIstance().getControl().getWorld().getThief().getX(),
				    GameManager.getIstance().getControl().getWorld().getThief().getY())));
	    this.setX((float) (this.getX() + normalize.x));
	    this.setY((float) (this.getY() + normalize.y));

	    sighted = true;
	}

	else if ((distance(this, GameManager.getIstance().getControl().getWorld().getThief()) < ENEMY_HEARING
		&& !GameManager.getIstance().getControl().getWorld().getThief().isSquatting()) || sighted) {

	    normalize = new Point2D.Double(
		    (GameManager.getIstance().getControl().getWorld().getThief().getX() - this.getX())
			    / (float) (distanceFloat(this.getX(), this.getY(),
				    GameManager.getIstance().getControl().getWorld().getThief().getX(),
				    GameManager.getIstance().getControl().getWorld().getThief().getY())),
		    (GameManager.getIstance().getControl().getWorld().getThief().getY() - this.getY())
			    / (float) (distanceFloat(this.getX(), this.getY(),
				    GameManager.getIstance().getControl().getWorld().getThief().getX(),
				    GameManager.getIstance().getControl().getWorld().getThief().getY())));
	    this.setX((float) (this.getX() + normalize.x));
	    this.setY((float) (this.getY() + normalize.y));

	    sighted = true;
	}
    }

    private float distanceFloat(float x1, float y1, float x2, float y2) {
	return (float) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

}
