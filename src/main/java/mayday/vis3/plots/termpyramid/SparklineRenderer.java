package mayday.vis3.plots.termpyramid;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import mayday.core.Probe;
import mayday.vis3.graph.vis3.SuperColorProvider;

@SuppressWarnings("serial")
public class SparklineRenderer  extends DefaultTableCellRenderer
{
	private SuperColorProvider coloring;
	private List<Double> values;

	private Color unselectedForeground; 
	private Color unselectedBackground; 

	int space=2;	
	int mode=2;
	//	"Adundance","Mean Expression (Heatmap)","Mean Expression (Bar)",
	//	"Mean Expression (Profile)","Deviance from mean (Bar)"
	public static final int ABUNDANCE_MODE=0;
	public static final int HEATMAP_MODE=1;
	public static final int BAR_MODE=2;
	public static final int PROFILE_MODE=3;
	public static final int DEVIANCE_MODE=4;


	public SparklineRenderer(SuperColorProvider coloring, int mode, int space) 
	{
		this.coloring=coloring;
		this.mode=mode;
		this.space=space;
	}

	@SuppressWarnings("unchecked")
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{

		if(value instanceof Collection)
		{
			if(!((Collection<Probe>)value).isEmpty())
			{
				values=new ArrayList<Double>();
				if(mode==2 || mode==3)
				{
					double max=Collections.max(coloring.getExpMaxValues());
					double min=Collections.max(coloring.getExpMinValues());

					for(double d: coloring.getProbesMeanValue(((Collection<Probe>)value)))
					{
						values.add((d-min)/(max-min));
					}
				}
				if(mode==4)
				{

					double mean=coloring.grandMean();
					for(double d: coloring.getProbesMeanValue(((Collection<Probe>)value)))
					{
						values.add(d-mean);
					}
				}
			}
			else
			{
				values=new ArrayList<Double>();
				//				values.add(0.0d);
			}
			setToolTipText("Mean expression of probes in "+table.getModel().getColumnName(column)+"with this term");			
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
		return this;
		//		return new HeatStreamCellRenderer(coloring,colors); 
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		if(getBounds()==null || values==null)
			return;
		if(values.size()==0)
			return;
		if(mode==2)
		{
			if(space<0)
			{
				drawSparkBar((Graphics2D) g, values, new Rectangle(0,0,getWidth(),getHeight()));
			}else
			{
				drawSparkBar((Graphics2D) g, values, new Rectangle(0,0,getWidth(),getHeight()),space);
			}			
		}
		if(mode==3)
		{
			if(space<0)
			{
				drawSparkLine((Graphics2D) g, values, new Rectangle(0,0,getWidth(),getHeight()));
			}else
			{
				drawSparkLine((Graphics2D) g, values, new Rectangle(0,0,getWidth(),getHeight()),space);
			}			
		}
		
		if(mode==4)
		{
			if(space<0)
			{
				drawBinarySparkBar((Graphics2D) g, values, new Rectangle(0,0,getWidth(),getHeight()));
			}else
			{
				drawBinarySparkBar((Graphics2D) g, values, new Rectangle(0,0,getWidth(),getHeight()),space);
			}			
		}
	}

	private void drawSparkBar(Graphics2D g, List<Double> values, Rectangle bounds)
	{
		AffineTransform tBak=g.getTransform();
		double sx=bounds.getWidth()/(values.size());
		g.scale(sx, 1);
		g.setStroke(new BasicStroke(0));
		int h=bounds.y+bounds.height;
		for(int i=0; i!= values.size(); ++i)
		{
			g.drawLine(i, h, i, (int)(values.get(i)*h) );
		}
		g.setTransform(tBak);
	}

	private void drawSparkBar(Graphics2D g, List<Double> values, Rectangle bounds, int space)
	{
		g.setStroke(new BasicStroke(0));
		int h=bounds.y+bounds.height;
		for(int i=0; i!= values.size(); ++i)
		{
			g.drawLine(space*i, h, space*i, (int)(values.get(i)*h) );
		}		
	}

	private void drawSparkLine(Graphics2D g, List<Double> values, Rectangle bounds, int space)
	{
		g.setStroke(new BasicStroke(0));
		int h=bounds.y+bounds.height;
		double p=h*values.get(0);
		for(int i=1; i!= values.size(); ++i)
		{
			g.drawLine((i-1)*space, (int) p, i*space, (int)(values.get(i)*h) );
			p=(values.get(i)*h);
		}		
	}

	private void drawSparkLine(Graphics2D g, List<Double> values, Rectangle bounds)
	{

		AffineTransform tBak=g.getTransform();
		double sx=bounds.getWidth()/(values.size());
		g.scale(sx, 1);
		g.setStroke(new BasicStroke(0));
		int h=bounds.y+bounds.height;
		double p=h*values.get(0);
		for(int i=1; i!= values.size(); ++i)
		{
			g.drawLine(i-1, (int) p, i, (int)(values.get(i)*h) );
			p=(values.get(i)*h);
		}
		g.setTransform(tBak);	
	}

	private void drawBinarySparkBar(Graphics2D g, List<Double> values, Rectangle bounds, int space)
	{
		g.setStroke(new BasicStroke(0));
		int h=bounds.y+bounds.height;
		for(int i=0; i!= values.size(); ++i)
		{
			g.drawLine(space*i, (int)(0.5*h), space*i, (values.get(i)*h)<0?h:0 );
		}		
	}

	private void drawBinarySparkBar(Graphics2D g, List<Double> values, Rectangle bounds)
	{
		AffineTransform tBak=g.getTransform();
		double sx=bounds.getWidth()/(values.size());
		g.scale(sx, 1);
		g.setStroke(new BasicStroke(0));
		int h=bounds.y+bounds.height;
		for(int i=0; i!= values.size(); ++i)
		{
			g.drawLine(i, (int)(0.5*h), i, (values.get(i)*h)<0?h:0 );
		}
		g.setTransform(tBak);
	}

}
