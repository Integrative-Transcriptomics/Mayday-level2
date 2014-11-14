package mayday.GWAS.visualizations.PValueHistogram;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import mayday.core.gui.components.VerticalLabel;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class PValColumn extends JPanel {
	
	protected PValBox box;
	protected VerticalLabel label;
	protected int maxLabelLength;
	protected int id;
	
	/**
	 * @param id 
	 * @param box
	 * @param label
	 * @param maxLabelLength 
	 */
	public PValColumn(int id, PValBox box, VerticalLabel label, int maxLabelLength) {
		this.id = id;
		this.box = box;
		this.label = label;
		this.maxLabelLength = maxLabelLength;
		this.setLayout(new GridLayout(0,1));
		
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setMinimumSize(new Dimension(label.getBounds().width, maxLabelLength));
		
		this.add(box);
		this.add(label);
	}
	
	/**
	 * @return id
	 */
	public int getID() {
		return this.id;
	}
}
