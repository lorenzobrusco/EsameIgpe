package singlePlayer.travel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import control.GameManager;
import logic.character.ElementOfWorld;
import logic.character.Enemy;
import logic.object.Tree;
import logic.object.inventary.Bow;
import logic.object.inventary.Sword;

public class PanelGame extends JPanel {

    private static final long serialVersionUID = 1L;
    private Image thief = new ImageIcon("assets" + File.separator + "Images" + File.separator + "thief.png").getImage();
    private Image enemy2 = new ImageIcon("assets" + File.separator + "Images" + File.separator + "enemy2.png")
	    .getImage();
    private Image tree = new ImageIcon("assets" + File.separator + "Images" + File.separator + "tree.png").getImage();
    private Image sword = new ImageIcon("assets" + File.separator + "Images" + File.separator + "sword.gif").getImage();
    private Image sword_down = new ImageIcon("assets" + File.separator + "Images" + File.separator + "sword_down.png")
	    .getImage();
    private Image bow = new ImageIcon("assets" + File.separator + "Images" + File.separator + "bow.jpg").getImage();

    private final char RIGHT = 'd';
    private final char LEFT = 'a';
    private final char UP = 'w';
    private final char DOWN = 's';
    private final char SQUATTING = 'c';
    private final int SPEEP = 16;
    private final char CHANGEWEAPON = 'q';
    private final char INVENTARY = 'i';

    public PanelGame() {
	GameManager.getIstance().setPanelGame(this);
	this.setFocusable(true);
	this.setPreferredSize(new Dimension(1200, 650));
	this.setBackground(Color.green);
	this.setVisible(true);

	// -------------evento tastiera------------------//
	this.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyPressed(KeyEvent e) {

		// --------------------movimento----------------------//

		if (e.getKeyCode() == SPEEP) {
		    GameManager.getIstance().getControl().getWorld().getThief().changespeed();
		    System.out.println("corro");
		    GameManager.getIstance().repaint();
		}

		if (e.getKeyChar() == UP) {
		    GameManager.getIstance().getControl().getWorld().getThief().moveUp();
		    GameManager.getIstance().repaint();
		} else if (e.getKeyChar() == DOWN) {
		    GameManager.getIstance().getControl().getWorld().getThief().moveDown();
		    GameManager.getIstance().repaint();
		} else if (e.getKeyChar() == LEFT) {
		    GameManager.getIstance().getControl().getWorld().getThief().moveLeft();
		    GameManager.getIstance().repaint();
		} else if (e.getKeyChar() == RIGHT) {
		    GameManager.getIstance().getControl().getWorld().getThief().moveRight();
		    GameManager.getIstance().repaint();
		} else if (e.getKeyChar() == SQUATTING) {
		    GameManager.getIstance().getControl().getWorld().getThief().setSquatting();
		    GameManager.getIstance().repaint();
		}
		// ---------------------------------------------------//

		// -----------------------other-----------------------//
		else if (e.getKeyChar() == INVENTARY) {
		    GameManager.getIstance().getControl().getWorld().getThief().getInventary().printInfo();
		}
		// ---------------------------------------------------//

		// ------------------------armi-----------------------//
		else if (e.getKeyChar() == CHANGEWEAPON) {
		    GameManager.getIstance().getControl().getWorld().getThief().changeArmed();
		    GameManager.getIstance().repaint();
		}
		// ---------------------------------------------------//

	    }
	});
	// --------------------------------------------------//

	// ------------------evento mouse-------------------//
	this.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (GameManager.getIstance().getControl().getWorld().getThief().getArmed() instanceof Bow) {
		    GameManager.getIstance().getControl().getWorld().getThief().attackBow(e.getX(), e.getY());
		} else {
		    new Thread() {
			public void run() {
			    try {
				((Sword) GameManager.getIstance().getControl().getWorld().getThief().getArmed())
					.setAttack(true);
				ElementOfWorld tmp = new ElementOfWorld(
					(int) GameManager.getIstance().getControl().getWorld().getThief().getX()
						+ (GameManager.getIstance().getControl().getWorld().getThief()
							.getSizex() - 19),
					(int) GameManager.getIstance().getControl().getWorld().getThief().getY()
						+ ((GameManager.getIstance().getControl().getWorld().getThief()
							.getSizey() / 2) - 4),
					0, 32, 32);
				GameManager.getIstance().getControl().searchCollision(tmp);
				GameManager.getIstance().getControl().getWorld().getThief().attackSword();
				PanelGame.this.repaint();
				sleep(100);
				((Sword) GameManager.getIstance().getControl().getWorld().getThief().getArmed())
					.setAttack(false);
				PanelGame.this.repaint();
			    } catch (InterruptedException e) {
				e.printStackTrace();
			    }
			};
		    }.start();
		}
	    }
	});
	// --------------------------------------------------//
    }

    @Override
    protected void paintComponent(Graphics g) {

	super.paintComponent(g);
	GameManager.getIstance().getControl().getWorld().searchStricken();
	for (Point2D.Double d : GameManager.getIstance().getControl().getWorld().getElementsOfWorld().keySet()) {
	    if (GameManager.getIstance().getControl().getWorld().getElementsOfWorld().get(d) instanceof Enemy) {
		g.drawImage(enemy2,
			(int) (GameManager.getIstance().getControl().getWorld().getElementsOfWorld().get(d).getX()),
			(int) (GameManager.getIstance().getControl().getWorld().getElementsOfWorld().get(d).getY()),
			this);
	    }
	    if (GameManager.getIstance().getControl().getWorld().getElementsOfWorld().get(d) instanceof Tree) {
		g.drawImage(tree,
			(int) (GameManager.getIstance().getControl().getWorld().getElementsOfWorld().get(d).getX()),
			(int) (GameManager.getIstance().getControl().getWorld().getElementsOfWorld().get(d).getY()),
			this);
	    }
	}

	for (ElementOfWorld e : GameManager.getIstance().getControl().getWorld().getThief().getArrowsInFlight()) {
	    g.drawRect((int) e.getX(), (int) e.getY(), 1, 1);
	}

	g.drawImage(thief, (int) GameManager.getIstance().getControl().getWorld().getThief().getX(),
		(int) GameManager.getIstance().getControl().getWorld().getThief().getY(), this);

	if (GameManager.getIstance().getControl().getWorld().getThief().getArmed() instanceof Bow)
	    g.drawImage(bow,
		    (int) GameManager.getIstance().getControl().getWorld().getThief().getX()
			    + (GameManager.getIstance().getControl().getWorld().getThief().getSizex() / 2) - 12,
		    (int) GameManager.getIstance().getControl().getWorld().getThief().getY()
			    + ((GameManager.getIstance().getControl().getWorld().getThief().getSizey() / 2) - 5),
		    this);

	if (GameManager.getIstance().getControl().getWorld().getThief().getArmed() instanceof Sword) {
	    if (((Sword) GameManager.getIstance().getControl().getWorld().getThief().getArmed()).isAttack())
		g.drawImage(sword_down,
			(int) GameManager.getIstance().getControl().getWorld().getThief().getX()
				+ (GameManager.getIstance().getControl().getWorld().getThief().getSizex() - 19),
			(int) GameManager.getIstance().getControl().getWorld().getThief().getY()
				+ ((GameManager.getIstance().getControl().getWorld().getThief().getSizey() / 2) - 4),
			this);
	    else
		g.drawImage(sword,
			(int) GameManager.getIstance().getControl().getWorld().getThief().getX()
				+ (GameManager.getIstance().getControl().getWorld().getThief().getSizex() - 19),
			(int) GameManager.getIstance().getControl().getWorld().getThief().getY()
				+ ((GameManager.getIstance().getControl().getWorld().getThief().getSizey() / 2) - 8),
			this);
	}
    }

}