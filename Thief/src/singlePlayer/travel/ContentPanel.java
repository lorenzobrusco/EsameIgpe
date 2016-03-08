package singlePlayer.travel;

import javax.swing.JPanel;

public class ContentPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public ContentPanel() {
	this.setVisible(true);
	this.add(new PanelGame());
    }

}
