package thread;

import control.GameManager;
import logic.character.Enemy;

public class EnemyIntelligence extends Thread {

    private final Enemy enemy;

    public EnemyIntelligence(Enemy enemy) {

	this.enemy = enemy;
    }

    @Override
    public void run() {

	while (true) {
	    try {
		sleep((int) (Math.random() * 1000));
		enemy.searchThief();
		GameManager.getIstance().repaint();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

}
