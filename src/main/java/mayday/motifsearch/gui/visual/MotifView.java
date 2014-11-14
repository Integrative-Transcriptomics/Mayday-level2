package mayday.motifsearch.gui.visual;

/**
 * Represents the color model of a motif which is mainly just its color.
 * After instantiation a motif has to be set to this object.
 * 
 * @author Frederik Weber
 */

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import mayday.motifsearch.model.Motif;


public class MotifView
extends HighLightSelector {

    private Motif motif;
    private static final long serialVersionUID = 1L;

    /**
     * creates a motif colorModel
     * 
     */
    public MotifView() {
	super();
	this.setBackground(Color.white);
    }

    /**
     * sets the motif that should be painted and then repaints
     * itself
     * 
     * @param motif
     *                the motif to be painted
     * 
     */
    public void setMotif(Motif motif) {
	this.motif = motif;
	this.repaint();
    }

    /**
     * @see javax.swing.JComponent paints a motif in the inner
     *      area of this component. It is mainly a area filled with the color of
     *      the transcription factor. Highlighting is drawn in this method too.
     * 
     * @param g
     *                the Graphics object of this Component
     * 
     */

    @Override
    protected void paintComponent(Graphics g) {
	Graphics2D gc = (Graphics2D) g;
	/* alpha blending */
	AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		0.99f);
	gc.setComposite(ac);

	Rectangle area = SwingUtilities.calculateInnerArea(this, null);

	/* draw the motif in the area */
	if (this.motif != null) {
	    gc.setColor(this.motif.getColor());
	    gc.fillRect(area.x, area.y, area.x + area.width, area.y
		    + area.height);
	    if (this.shouldMotifBeHighlighted(this.motif)) {
		gc.setColor(Color.RED);
		int[] xPoints = { 1, area.width / 2 + 1, area.width - 1 };
		int[] yPoints = { 1, area.height - 1, 1 };
		gc.fillPolygon(xPoints, yPoints, 3);

		gc.setColor(Color.BLACK);
		gc.setFont(new Font("Sans-Serif", Font.BOLD, 12));
		gc.drawString("H", area.width/2-3, 15);
	    }
	    gc.setColor(this.motif.getColor());

	}
	else {
	    gc.setColor(this.getBackground());
	    gc.fillRect(area.x, area.y, area.x + area.width, area.y
		    + area.height);
	}

    }
}
