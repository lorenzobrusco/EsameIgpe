package singlePlayer.travel;

import javax.swing.JFrame;

public class MyFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public MyFrame() {

	this.add(new ContentPanel());
	this.pack();
	this.setLocationRelativeTo(null);
	this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

	MyFrame frame = new MyFrame();
	frame.setVisible(true);
    }

}
