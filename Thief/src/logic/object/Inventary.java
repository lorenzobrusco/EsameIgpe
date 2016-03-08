package logic.object;

import logic.object.inventary.Bow;
import logic.object.inventary.Sword;

public class Inventary {

    private Sword sword;

    private Bow bow;

    private int numberArrows;

    private int moneys;

    public Inventary() {

	this.sword = new Sword(10);

	this.bow = new Bow(10);

	this.numberArrows = 50;

	this.moneys = 0;
    }

    public Sword getSword() {
	return sword;
    }

    public void addStrengtheningSword(int add) {
	this.sword.setDamage(this.sword.getDamage() + add);
    }

    public Bow getBow() {
	return bow;
    }

    public void addStrengtheningBow(int add) {
	this.bow.setDamage(this.bow.getDamage() + add);
    }

    public int getNumberArrows() {
	return numberArrows;
    }

    public void setNumberArrows(int numberArrows) {
	this.numberArrows = numberArrows;
    }

    public int getMoney() {
	return moneys;
    }

    public void setMoney(int money) {
	this.moneys = money;
    }

    public void printInfo() {
	System.out.println("Invertario:");
	System.out.println("spada: " + sword.getDamage());
	System.out.println("arco: " + bow.getDamage());
	System.out.println();
	System.out.println();
	System.out.println();

    }

}
