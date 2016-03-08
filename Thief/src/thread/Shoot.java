package thread;

import control.GameManager;
import logic.object.inventary.Arrow;

public class Shoot extends Thread {

    private float[] vector;
    private float[] normalize;
    private Arrow arrow;
    private float x2, y2;
    private float speed;
    private final int FORCE;

    public Shoot(Arrow arrow, int x2, int y2) {

	this.arrow = arrow;
	this.x2 = x2;
	this.y2 = y2;
	this.FORCE = 20;
	this.speed = (float) (distanceFloat(arrow.getX(), arrow.getY(), x2, y2) / FORCE);
	vector = new float[2];
	normalize = new float[2];
    }

    private float distanceFloat(float x1, float y1, float x2, float y2) {
	return (float) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    public void vector() {
	vector[0] = x2 - arrow.getX();
	vector[1] = y2 - arrow.getY();
    }

    public void normalize() {
	normalize[0] = vector[0] / speed;
	normalize[1] = vector[1] / speed;
    }

    @Override
    public void run() {

	vector();
	normalize();
	while (arrow.isInFlight()) {
	    try {
		arrow.setX(arrow.getX() + normalize[0]);
		arrow.setY(arrow.getY() + normalize[1]);
		GameManager.getIstance().repaint();
		sleep(100);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

}
