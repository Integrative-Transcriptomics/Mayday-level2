package mayday.vis3.plots.termpyramid;

import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class AlignedLabelRenderer extends DefaultTableCellRenderer 
{
	public AlignedLabelRenderer(int alignment) 
	{
		super();
		setHorizontalAlignment(alignment);
	}
}
