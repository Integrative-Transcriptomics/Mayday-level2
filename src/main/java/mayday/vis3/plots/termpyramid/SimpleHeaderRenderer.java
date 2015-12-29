package mayday.vis3.plots.termpyramid;

import java.awt.Color;

import mayday.core.gui.tablespecials.EmptyHeaderRenderer;

@SuppressWarnings("serial")
public class SimpleHeaderRenderer extends EmptyHeaderRenderer {
	
	
	public SimpleHeaderRenderer(int alignment) 
	{
		this.setOpaque(true);
		this.setForeground(Color.BLACK);
		setHorizontalAlignment(alignment);		
	}
	
	public SimpleHeaderRenderer(int horizontalAlignment, int verticalAlignment) 
	{
		this.setOpaque(true);
		this.setForeground(Color.BLACK);
		setHorizontalAlignment(horizontalAlignment);	
		setVerticalAlignment(verticalAlignment);	
	}
	
}
