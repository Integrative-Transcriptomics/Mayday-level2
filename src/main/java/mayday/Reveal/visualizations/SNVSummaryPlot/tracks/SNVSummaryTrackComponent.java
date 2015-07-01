package mayday.Reveal.visualizations.SNVSummaryPlot.tracks;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import mayday.core.MaydayDefaults;

public abstract class SNVSummaryTrackComponent extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4027766532202863182L;
	protected SNVSummaryTrack track;
	
	public SNVSummaryTrackComponent(SNVSummaryTrack track) {
		this.track = track;
		this.setBackground(Color.WHITE);
		this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setBackground(Color.WHITE);
		//g2.clearRect(0, 0, getWidth(), getHeight());
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(MaydayDefaults.DEFAULT_PLOT_FONT);
		super.paint(g2);
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g2);
		this.doPaint(g2);
	}

	public abstract void doPaint(Graphics2D g2);
}
