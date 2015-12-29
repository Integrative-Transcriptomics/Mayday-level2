package mayday.motifsearch.gui.visual;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JScrollPane;
import javax.swing.JComponent;;

/**
 * TransFactor Scroll Pane, needed to set the background white
 * 
 * @author Frederik Weber
 * 
 */
public class WhiteScrollPane
extends JScrollPane {

    private static final long serialVersionUID = 1L;

    public WhiteScrollPane(JComponent component,  int vsbPolicy, int hsbPolicy) {
	super(component, vsbPolicy, hsbPolicy);
	this.getViewport().setBackground(Color.white);
    }

    @Override
    public void paintComponent(Graphics g) {
	this.setOpaque(true);
	super.paintComponent(g);
    }
}
