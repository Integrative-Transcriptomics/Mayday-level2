package mayday.motifsearch.gui.visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import mayday.motifsearch.gui.listeners.*;
import mayday.motifsearch.model.Motif;
import mayday.motifsearch.sequenceLogo.*;

public class SequenceLogoView extends JComponent implements ColorModelListener{
    private static final long serialVersionUID = 1L;

    private Motif motif;
    protected boolean isWhiteColorModel; // color model
    protected boolean areCoorinatesPainted; 


    public SequenceLogoView() {
	super();
	this.setBackground(((this.isWhiteColorModel) ? Color.WHITE
		: Color.BLACK));
	this.areCoorinatesPainted = false;
    }

    public SequenceLogoView(boolean areCoorinatesPainted) {
	super();
	this.setBackground(((this.isWhiteColorModel) ? Color.WHITE
		: Color.BLACK));
	this.setAreCoorinatesPainted(areCoorinatesPainted);
    }


    @Override
    protected final void paintComponent(Graphics g) {
	Graphics2D g2d = (Graphics2D) g;

	Rectangle area = SwingUtilities.calculateInnerArea(this, null);

	g2d.setColor((this.isWhiteColorModel ? Color.WHITE : Color.BLACK));
	g2d.fillRect(area.x, area.y, area.x + area.width, area.y + area.height);

	SequenceLogo.paintGraphics(g2d, this.motif.getPSWM(), area.width, area.height, this.areCoorinatesPainted, this.isWhiteColorModel);
    }



    public void setMotif(Motif motif) {
	this.motif = motif;
	this.repaint();
    }

    /**
     * sets the color model to
     * 
     * @param isWhiteColorModel
     *                true if the white color model is used an the background is
     *                white, false if the black color model is used and
     *                background is black
     */

    public void colorModelChanged(boolean isWhiteColorModel){
	this.isWhiteColorModel = isWhiteColorModel;
	this.setBackground(((this.isWhiteColorModel) ? Color.WHITE
		: Color.BLACK));
    }


    public SequenceLogoView cloneForPreview(){
	SequenceLogoView sequenceLogoView = new SequenceLogoView();
	sequenceLogoView.setMotif(this.motif);
	sequenceLogoView.isWhiteColorModel = this.isWhiteColorModel;
	return sequenceLogoView;
    }

    public boolean isAreCoorinatesPainted() {
	return areCoorinatesPainted;
    }

    public void setAreCoorinatesPainted(boolean areCoorinatesPainted) {
	this.areCoorinatesPainted = areCoorinatesPainted;
    }
}
