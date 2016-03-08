package logic.object.inventary;

import logic.character.ElementOfWorld;

public class Arrow extends ElementOfWorld {

    private boolean inFlight;

    public Arrow(float x, float y, float z, int sizex, int sizey) {
	super(x, y, z, sizex, sizey);
	this.inFlight = true;
    }

    public boolean isInFlight() {
	return inFlight;
    }

    public void stricken() {
	this.inFlight = false;
    }

}
