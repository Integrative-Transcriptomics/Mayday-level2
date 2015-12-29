package mayday.vis3.plots.termpyramid;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Collection;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import mayday.core.Probe;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.graph.renderer.RendererTools;

@SuppressWarnings("serial")
public class BarCellRenderer extends DefaultTableCellRenderer
{
	protected double abundance;
	protected boolean left;	
	protected Color color; 

	private ColorMode mode=ColorMode.PROBELIST;
	protected ColorGradient gradient; 

	protected Color unselectedForeground; 
	protected Color unselectedBackground; 

	public BarCellRenderer(boolean l) 
	{
		left=l;		
	}

	@SuppressWarnings("unchecked")
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		if(value instanceof Double)
		{
			abundance=(Double)value;
		}
		
		if(value instanceof Collection)
		{
			abundance=((Collection<Probe>)value).size();
		}
		
		if(left)
		{
			abundance/=((TermPyramidModel)table.getModel()).getPl2Max()*1.0;			
		}else
		{
			abundance/=((TermPyramidModel)table.getModel()).getPl1Max()*1.0;			
		}
		
		switch (mode)		{
		case VALUE:
			color= gradient.mapValueToColor(abundance);
			break;
		case PROBELIST:
			color= getProbeListColor(table);
			break;
		case MIGROUP:
			color= RendererTools.wordToColor(table.getValueAt(row, 2).toString());
			break;
		default:
			color=Color.black;
		}

		if (isSelected) {
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			Color background = unselectedBackground != null
			? unselectedBackground
					: table.getBackground();
			if (background == null || background instanceof javax.swing.plaf.UIResource) {
				Color alternateColor = UIManager.getColor("Table.alternateRowColor"); 
					//DefaultLookup.getColor(this, ui, "Table.alternateRowColor");
				if (alternateColor != null && row % 2 == 0)
					background = alternateColor;
			}
			super.setForeground(unselectedForeground != null
					? unselectedForeground
							: table.getForeground());
			super.setBackground(background);
		}

		return this; 
	}
	
	private Color getProbeListColor(JTable table)
	{
		if(table.getModel() instanceof TermPyramidModel)
		{
			if(left)
				return ((TermPyramidModel)table.getModel()).getProbeList2().getColor();
			else
				return ((TermPyramidModel)table.getModel()).getProbeList1().getColor();
		}
		return Color.black;
	}
	
	
	public void paint(Graphics g)
	{
		super.paint(g);
		g.setColor(color);
		int l= (int) (getWidth()*abundance);
		if(left)
		{
			g.drawRect(0, 3, l, 10);
			g.fillRect(0, 3, l, 10);
		}else
		{
			g.drawRect(getWidth()-l-1, 3, l, 10);
			g.fillRect(getWidth()-l-1, 3, l, 10);
		}		
	}

	public enum ColorMode
	{
		PROBELIST,
		VALUE,
		MIGROUP
	}

	public void setColoring(ColorMode mode, ColorGradient grad)
	{
		this.mode=mode;
		gradient=grad;
	}
}
