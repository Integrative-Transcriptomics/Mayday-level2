package mayday.vis3.plots.termpyramid;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import mayday.core.Probe;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.vis3.SuperColorProvider;

@SuppressWarnings("serial")
public class HeatStreamCellRenderer  extends DefaultTableCellRenderer
{
	private SuperColorProvider coloring;
	
	private List<Color> colors;
	
	private Color unselectedForeground; 
	private Color unselectedBackground; 
	
	private HeatStreamCellRenderer(SuperColorProvider coloring,List<Color> colors)
	{
		this.coloring=coloring;
		this.colors=colors;
	}
	
	public HeatStreamCellRenderer(SuperColorProvider coloring) 
	{
		this.coloring=coloring;
	}
	
	@SuppressWarnings("unchecked")
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
	
		if(value instanceof Collection)
		{
			if(!((Collection<Probe>)value).isEmpty())
			{
				colors=coloring.getMeanColors(((Collection<Probe>)value));
			}
			else
			{
				colors=new ArrayList<Color>();
				colors.add(Color.white);
			}
			setToolTipText("Mean expression of probes in "+table.getModel().getColumnName(column)+"with this term");			
			setText(Integer.toString(((Collection<Probe>)value).size())+" Probes");
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
//					DefaultLookup.getColor(this, ui, "Table.alternateRowColor");
				if (alternateColor != null && row % 2 == 0)
					background = alternateColor;
			}
			super.setForeground(unselectedForeground != null
					? unselectedForeground
							: table.getForeground());
			super.setBackground(background);
		}

		return new HeatStreamCellRenderer(coloring,colors); 
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
//		g.setColor(Color.blue);
//		g.fillRect(0, 0, getWidth(), getHeight());
		
//		System.out.println(getBounds().toString()+"\t..."+colors==null?"blank":"full");
		
		if(getBounds()==null || colors==null)
			return;
		RendererTools.drawColorLine((Graphics2D) g, colors, new Rectangle(0,0,getWidth(),getHeight()));		
	}
	
	public void setColoring(SuperColorProvider coloring) 
	{
		this.coloring = coloring;
	}
}
