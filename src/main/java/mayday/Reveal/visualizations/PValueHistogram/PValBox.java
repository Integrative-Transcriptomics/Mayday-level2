package mayday.Reveal.visualizations.PValueHistogram;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class PValBox extends JPanel {

	protected double pValue;
	protected double maxPValue;
	
	
	/**
	 * @param id
	 * @param pValue
	 * @param maxPValue 
	 */
	public PValBox(double pValue, double maxPValue) {
		this.pValue = pValue;
		this.maxPValue = maxPValue;
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.clearRect(0, 0, getWidth(), getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		
		int h = getHeight();
		int w = getWidth();
		
		double pLog = -(Math.log(pValue)/Math.log(2));
		int normalized = (int)((h / maxPValue) * pLog);
		
		g2d.setColor(Color.GRAY);
		g2d.setPaint(new GradientPaint(0, 0, g2d.getColor().darker(), w/2, 0, g2d.getColor().brighter(), true));
		
		g2d.fillRect(0, h - normalized, w, h);
		g2d.setColor(Color.BLACK);
		g2d.drawRect(0, h - normalized, w-1, h-1);
	}
}
