package mayday.Reveal.visualizations.SNVSummaryPlot.tracks;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import mayday.core.MaydayDefaults;

public class TrackLabelPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8615808549254073651L;
	
	private String label;
	
	public TrackLabelPanel(String trackLabel) {
		this.label = trackLabel;
		this.setBackground(Color.WHITE);
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setBackground(Color.white);
		g2.clearRect(0, 0, getWidth(), getHeight());
		
		g2.setFont(MaydayDefaults.DEFAULT_PLOT_FONT);
		
		Rectangle2D r = g2.getFontMetrics().getStringBounds(label, g2);
		
		AffineTransform af = g2.getTransform();
		g2.translate(getWidth()/2 + r.getCenterY(), getHeight()/2-r.getCenterX());
		g2.rotate(Math.toRadians(90));
		g2.setColor(Color.DARK_GRAY);
		g2.drawString(label, 0, 0);
		g2.setTransform(af);
	}
}
