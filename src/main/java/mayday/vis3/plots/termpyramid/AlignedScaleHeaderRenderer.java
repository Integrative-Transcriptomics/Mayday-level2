package mayday.vis3.plots.termpyramid;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class AlignedScaleHeaderRenderer extends SimpleHeaderRenderer
{
	private double maxValue=1.0;
	
	public AlignedScaleHeaderRenderer(int alignment) 
	{
		super(alignment);
		setMinimumSize(new Dimension(100,20));
		setPreferredSize(new Dimension(250,20));
	}
	
	public AlignedScaleHeaderRenderer(int horizontalAlignment, int verticalAlignment) 
	{
		super(horizontalAlignment, verticalAlignment);
		setMinimumSize(new Dimension(100,20));
		setPreferredSize(new Dimension(250,20));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,	boolean isSelected, boolean hasFocus, int row, int column) 
	{
		if(table==null) 
			return new JLabel();
		if(getHorizontalAlignment()==SwingConstants.LEFT)
			maxValue=((TermPyramidModel)table.getModel()).getPl2Max();
		else
		{
			
			maxValue=((TermPyramidModel)table.getModel()).getPl1Max();
		}
		
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
	
	@Override
	public void paint(Graphics g) 
	{
		super.paint(g);
		g.drawLine(0, getHeight()-1, getWidth(),getHeight()-1);
		g.drawLine(getWidth()/2, getHeight()-5, getWidth()/2,getHeight());
	
		g.setColor(Color.black);
		
		NumberFormat form= NumberFormat.getNumberInstance();
		
		if(getHorizontalAlignment()==SwingConstants.LEFT)
		{
			g.drawLine(getWidth(), getHeight()-5, getWidth(),getHeight());
			g.drawLine(0, getHeight()-5 , 0,getHeight());
			g.drawLine(getWidth()-1, getHeight()-5, getWidth()-1,getHeight());
			g.drawString("0", 2, getHeight()-10);
			g.drawString(form.format(maxValue/2.0), (getWidth()/2)-10, getHeight()-10);
			g.drawString(form.format(maxValue), getWidth()-15, getHeight()-10);
		}
		if(getHorizontalAlignment()==SwingConstants.RIGHT)
		{
			g.drawLine(getWidth()-1, getHeight()-5, getWidth()-1,getHeight());
			g.drawLine(0, getHeight()-5 , 0,getHeight());
			
			g.drawString(form.format(maxValue), 2, getHeight()-10);
			g.drawString(form.format(maxValue/2.0), (getWidth()/2)-10, getHeight()-10);
			g.drawString("0", getWidth()-10, getHeight()-10);
		}
	}

}
