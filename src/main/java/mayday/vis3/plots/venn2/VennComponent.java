package mayday.vis3.plots.venn2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.text.NumberFormat;
import java.util.Set;

import mayday.core.Probe;


public class VennComponent
{
	private Set<Probe> probes;
	private Color color=Color.white;
	private Shape shape;
	private boolean selected=false; 
	private CheckeredPattern pattern;
	private Integer totalProbes=null;

	public VennComponent(Set<Probe> probes, Integer totalProbes)
	{		
		this.probes=probes;
		pattern = new CheckeredPattern(Color.white,Color.red);
		this.totalProbes = totalProbes;
	}

	public void paint(Graphics2D g)
	{
		if(shape==null)
			return;		
		
		g.setColor(color);
		AffineTransform at=g.getTransform();

		if(selected) {
			Paint p=g.getPaint();
			g.setPaint(pattern.getTexturePaint());
			g.fill(shape);
			g.setPaint(p);
		}else {
			g.fill(shape);
		}
		g.setColor(Color.black);
		g.setStroke(new BasicStroke((float) (2/g.getTransform().getScaleX())));
		g.draw(shape);

		double numFactor=1.5;
		
		int numSize= (int)Math.max(numFactor*g.getTransform().getScaleX(),12);		
		Font numFont=new Font(Font.SANS_SERIF,Font.BOLD,numSize );
		
		// present label
		g.setColor(Color.black);
		g.setTransform(AffineTransform.getTranslateInstance(0,0));

		Rectangle bounds=at.createTransformedShape(shape).getBounds();
		Font f=g.getFont();
		g.setFont(numFont);
		
		String textToPut;
		if (totalProbes==null) {
			textToPut = Integer.toString(probes.size());
		} else {
			double perc = probes.size();
			perc /= totalProbes.doubleValue();
			NumberFormat nf = NumberFormat.getPercentInstance();
			nf.setMaximumFractionDigits(2);
			nf.setMinimumFractionDigits(2);			
			textToPut = nf.format(perc);
		}
		
		VennPlot.placeStringAt(g, textToPut, bounds.getCenterX(), bounds.getCenterY(), .5f,.5f);
		
		g.setTransform(at);		
		g.setFont(f);
	}

	
	
	private Color alphaColor(Color c)
	{
		return new Color(c.getRed(), c.getGreen(), c.getBlue(),100);
	}

	public Set<Probe> getProbes() {
		return probes;
	}

	public void setProbes(Set<Probe> probes) {
		this.probes = probes;
	}

	public void setColor(Color color) 
	{
		this.color = alphaColor(color);
		pattern = new CheckeredPattern(color,alphaColor(Color.red));
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}





}
