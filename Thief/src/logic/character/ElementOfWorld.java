package logic.character;

public class ElementOfWorld {

    private float x, y, z;
    private final int sizex, sizey;

    public ElementOfWorld(float x, float y, float z, int sizex, int sizey) {
	this.x = x;
	this.y = y;
	this.z = z;
	this.sizex = sizex;
	this.sizey = sizey;
    }

    public float getX() {
	return x;
    }

    public void setX(float x) {
	this.x = x;
    }

    public float getY() {
	return y;
    }

    public void setY(float y) {
	this.y = y;
    }

    public float getZ() {
	return z;
    }

    public void setZ(float z) {
	this.z = z;
    }

    public int getSizex() {
	return sizex;
    }

    public int getSizey() {
	return sizey;
    }

    public float distance(ElementOfWorld e1, ElementOfWorld e2) {
	return (float) (Math.sqrt((Math.pow(((e1.x + (e1.sizex / 2)) - (e2.x + (e2.sizex / 2))), 2)
		+ (Math.pow(((e1.y + (e1.sizey / 2)) - (e2.y + (e2.sizey / 2))), 2)))));
    }

    public boolean collition(ElementOfWorld elementOfWorld1, ElementOfWorld elementOfWorld2) {
	if (elementOfWorld1.equals(elementOfWorld2))
	    return false;
	else
	    return distance(elementOfWorld1, elementOfWorld2) <= (this.getSizex() / 2);
    }

}
