package mayday.expressionmapping.view.ui;

import java.awt.Graphics;

import javax.swing.JSplitPane;

/**
 * @author jaeger
 */
@SuppressWarnings("serial")
public class PlotSplitPane extends JSplitPane {
	
	private int oldSize = 0;
	private int dividerLocation = 0;
	
	/**
	 * @param horizontalSplit
	 */
	public PlotSplitPane(int horizontalSplit) {
		super(horizontalSplit);
	}
	
	@Override
	public void paint(Graphics g) {
		/* 
		 * We don't want separators to show up in the exported image.
		 * During export, the exported component is set to invisible (ExportDialog.java)
		 * so we can use that criterion to find out if we're exported just now.
		 * btw: isShowing() != isVisible()
		 */
		if (!isShowing()) {
			this.removeDivider();
			super.paint(g);
			this.addDevider();
		} else {
			super.paint(g);
		}
	}
	
	/**
	 * remove the divider
	 */
	public void removeDivider() {
		this.oldSize = this.getDividerSize();
		this.dividerLocation = this.getDividerLocation();
		this.setDividerSize(0);
		this.invalidate();
		this.validate();
	}
	
	/**
	 * add the divider
	 */
	public void addDevider() {
		this.setDividerSize(oldSize);
		this.setDividerLocation(dividerLocation);
		this.invalidate();
		this.validate();
	}
}
