package logic;

import logic.character.Character;
import logic.character.ElementOfWorld;
import logic.character.Enemy;
import logic.character.Thief;
import logic.object.inventary.Arrow;
import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

public class World {

	@SuppressWarnings("unused")
	private double sizeX, sizeY, sizeZ;
	private ConcurrentHashMap<Point2D.Double, ElementOfWorld> elementsOfWorld;
	private Thief thief;
	private Point2D.Double point;

	public World(double sizeX, double sizeY, double sizeZ) {
		this.setParams(sizeX, sizeY, sizeZ);
	}

	public void setParams(double sizeX, double sizeY, double sizeZ) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		elementsOfWorld = new ConcurrentHashMap<Point2D.Double, ElementOfWorld>(100);
		point = new Point2D.Double();
		point.setLocation(50, 50);
		thief = new Thief(50, 50, 0, 100, 10);
		elementsOfWorld.put(new Point2D.Double(50, 50), thief);
		insetObject(10, "logic.character.Enemy", new Class[] { double.class, double.class, double.class, int.class,
				int.class}, new Object[] { 0, 0, 0, 50, 10});
		insetObject(5, "logic.object.Casket",
				new Class[] { double.class, double.class, double.class, int.class, int.class },
				new Object[] { 0, 0, 0, 32, 32 });
		insetObject(20, "logic.object.Tree",
				new Class[] { double.class, double.class, double.class, int.class, int.class },
				new Object[] { 0, 0, 0, 32, 32 });
	}

	@SuppressWarnings("rawtypes")
	private void insetObject(int number, String nameclass, Class[] args, Object[] params) {
		int i = 0;
		try {
			while (i < number) {
				do {
					params[0] = Math.random() * sizeX;

				} while ((double) params[0] < 100 | (double) params[0] > 1100);
				do {
					params[1] = Math.random() * sizeY;

				} while ((double) params[1] < 100 | (double) params[1] > 600);
				point.setLocation((double) params[0], (double) params[1]);
				if (!elementsOfWorld.containsKey(point)) {
					Object e = Class.forName(nameclass).getConstructor(args).newInstance(params);
					boolean collision = false;
					for (Point2D.Double d : elementsOfWorld.keySet()) {
						if (((ElementOfWorld) e).collition((ElementOfWorld) e, elementsOfWorld.get(d))) {
							collision = true;
						}
					}
					if (!collision) {
						elementsOfWorld.put(new Point2D.Double((double) params[0], (double) params[1]),
								(ElementOfWorld) e);
						i++;
					}
				}
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	public Thief getThief() {
		return thief;
	}

	public synchronized ConcurrentHashMap<Point2D.Double, ElementOfWorld> getElementsOfWorld() {
		return elementsOfWorld;
	}

	public synchronized void removeElementsOfWord(ElementOfWorld elementOfWorld){
		for (Point2D.Double d : elementsOfWorld.keySet()) {
			if(elementsOfWorld.get(d).equals(elementOfWorld))
				elementsOfWorld.remove(d);
		}
	}
	
	public void searchStricken() {
		for (ElementOfWorld e : thief.getArrowsInFlight()) {
			for (Point2D.Double d : elementsOfWorld.keySet()) {
				if (elementsOfWorld.get(d).collition(elementsOfWorld.get(d), e)
						&& !(elementsOfWorld.get(d) instanceof Thief)) {
					if (elementsOfWorld.get(d) instanceof Enemy) {
						((Character) elementsOfWorld.get(d))
								.setLife(((Character) elementsOfWorld.get(d)).getLife() - thief.getDamage());
						if (!((Character) elementsOfWorld.get(d)).isAlive()) {
							elementsOfWorld.remove(d);
						}
					}
					((Arrow) e).stricken();
					thief.getArrowsInFlight().remove(e);
					return;
				}
			}
		}
	}

}
