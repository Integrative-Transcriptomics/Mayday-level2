package mayday.Reveal.visualizations.LDPlot.tracks;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.visualizations.LDPlot.LDPlot;

public class SNVIdentifierTrack extends LDPlotTrack implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7230572144708324305L;

	public SNVIdentifierTrack(LDPlot plot) {
		super(plot);
		this.addMouseListener(this);
	}
	
	@Override
	public void doPaint(Graphics2D g2) {
		SNVList snps = getSelectedSNPs();
		int cellWidth = getSetting().getCellWidth();
		
		int startIndex = getSetting().getStartIndex();
		int stopIndex = getSetting().getStopIndex();
		
		int space = 2;
		
		for(int i = startIndex; i < stopIndex; i++) {
			SNV s = snps.get(i);
			String snvLabel = s.getID();
			Rectangle2D r = g2.getFontMetrics().getStringBounds(snvLabel, g2);
			
			if(getViewModel().isSelected(s)) {
				Rectangle2D rec = new Rectangle2D.Double(cellWidth * i, 0, cellWidth, getHeight());
				g2.setColor(getSetting().getSelectionColor());
				g2.fill(rec);
			}
			
			if(cellWidth > r.getHeight()) {
				AffineTransform af = g2.getTransform();
				g2.translate(cellWidth * i + cellWidth/2 + r.getCenterY(), getHeight()-r.getWidth()-space);
				g2.rotate(Math.toRadians(90));
				g2.setColor(Color.DARK_GRAY);
				g2.drawString(snvLabel, 0, 0);
				g2.setColor(getSetting().getSNVColor());
				g2.setTransform(af);
				g2.drawLine(cellWidth * i + cellWidth/2, 0, cellWidth * i + cellWidth/2, (int)(getHeight() - r.getWidth() - space*2));
			} else {
				g2.setColor(getSetting().getSNVColor());
				g2.drawLine(cellWidth * i + cellWidth/2, 0, cellWidth * i + cellWidth/2, getHeight());
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		SNVList snps = getSelectedSNPs();
		int cellWidth = getSetting().getCellWidth();
		int x = e.getX();
		
		int snpIndex = (int)Math.floor((double)x / (double)cellWidth);
		
		if(snpIndex >= 0 && snpIndex < snps.size()) {
			SNV s = snps.get(snpIndex);
			getViewModel().toggleSNPSelected(s);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
