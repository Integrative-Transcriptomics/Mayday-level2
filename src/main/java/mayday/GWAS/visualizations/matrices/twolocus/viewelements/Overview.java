package mayday.GWAS.visualizations.matrices.twolocus.viewelements;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class Overview extends JPanel {

	private Color bgColor;
	
	/**
	 * @param bgColor
	 */
	public Overview(Color bgColor) {
		super();
		this.setBackground(bgColor);
		this.bgColor = bgColor;
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(bgColor);
		g2.clearRect(0, 0, getWidth(), getHeight());
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paint(g2);
	}
}
