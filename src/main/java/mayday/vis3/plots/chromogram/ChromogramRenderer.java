package mayday.vis3.plots.chromogram;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.graph.renderer.RendererTools;

@SuppressWarnings("serial")
public class ChromogramRenderer extends DefaultTableCellRenderer
{
	ColorGradient grad=ColorGradient.createDefaultGradient(0, 255);

	boolean selected;
	boolean renderText=true;
	
	public ChromogramRenderer(boolean renderText)
	{
		this.renderText=renderText;
		}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{
		if(renderText)
		{
			setText(value.toString());
		}
		setToolTipText(value.toString());
		setOpaque(true);
		Color color=RendererTools.wordToColor(value.toString());
		setBackground(color);
		selected=isSelected;
		return this;
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		if(selected){
			g.setColor(RendererTools.getInverseBlackOrWhite(getBackground()));
			g.drawLine(0, 0, getWidth(), getHeight());
		}
	}
	


	/**
	 * @return the renderText
	 */
	public boolean isRenderText() {
		return renderText;
	}

	/**
	 * @param renderText the renderText to set
	 */
	public void setRenderText(boolean renderText) {
		this.renderText = renderText;
	}
	
	
	
	
}
