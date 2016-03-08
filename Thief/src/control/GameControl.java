package control;

import java.awt.geom.Point2D;

import logic.World;
import logic.character.ElementOfWorld;
import logic.character.Enemy;
import logic.info.WhatIs;
import logic.object.Tree;

public class GameControl {

    private World world;
    private WhatIs<Boolean, ElementOfWorld> collision_with_object;

    public GameControl(World world) {
	this.world = world;
	collision_with_object = new WhatIs<Boolean, ElementOfWorld>(false, null);
    }

    public World getWorld() {
	return world;
    }

    public void setWorld(World world) {
	this.world = world;
    }

    public WhatIs<Boolean, ElementOfWorld> searchCollision(ElementOfWorld everyone) {

	boolean collision = false;
	for (Point2D.Double d : world.getElementsOfWorld().keySet()) {
	    if (everyone.collition(everyone, world.getElementsOfWorld().get(d))) {
		collision = true;
		if (world.getElementsOfWorld().get(d) instanceof Enemy)
		    collision_with_object.set(true, world.getElementsOfWorld().get(d));
		if (world.getElementsOfWorld().get(d) instanceof Tree)
		    collision_with_object.set(true, world.getElementsOfWorld().get(d));
	    }
	}
	if (!collision)
	    collision_with_object.first = false;
	return collision_with_object;
    }

}
