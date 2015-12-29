package mayday.vis3.plots.tagcloud;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;

import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.graph.components.LabelRenderer.Orientation;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererTools;

public class TagRenderer implements ComponentRenderer
{
	private int minSize=10;
	private int maxSize=18;

	private final int P=10;
	private int defaultWidth=250;
	private ColorGradient gradient=ColorGradient.createDefaultGradient(0, 1);

	public TagRenderer() {}	
	public TagRenderer(ColorGradient grad) 
	{
		this.gradient=grad;
	}
	
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value, String label, boolean selected) 
	{
		DefaultNode dn=(DefaultNode)node;
		Tag w=new Tag(dn.getPropertyValue(TagConstants.TAG_KEY), Double.parseDouble(dn.getPropertyValue(TagConstants.TAG_FREQUENCY)));
		
		Color back = gradient.mapValueToColor(w.getFrequency());
		back = RendererTools.alphaColor(back,100);
		
//		RendererTools.fill(g, bounds, Color.white);
		RendererTools.fill(g, bounds, back,10);
		int s=(int)(maxSize*w.getFrequency()+minSize);
		Font font=new Font(Font.SANS_SERIF,Font.PLAIN,s);
		Font bak=g.getFont();
		g.setFont(font);
		g.setColor(Color.black);
		int h=g.getFontMetrics(font).getStringBounds(w.getTag().toString(), g).getBounds().height;
		h=(bounds.height-h);
		g.drawString(w.getTag().toString(), P, bounds.height-h);
		g.setFont(bak);	
		if(selected)
		{
			RendererTools.drawRoundBox(g, new Rectangle(2,2,bounds.width-2,bounds.height-2), selected, Color.red, P);
		}

	}
	


	public Dimension getStringBoundingBox(Graphics g, Tag t)
	{
		int s=(int)(maxSize*t.getFrequency()+minSize);
		Font font=new Font(Font.SANS_SERIF,Font.PLAIN,s);
		int w=g.getFontMetrics(font).getStringBounds(t.getTag().toString(), g).getBounds().width;
		int h=g.getFontMetrics(font).getStringBounds(t.getTag().toString(), g).getBounds().height;
		return new Dimension(w+2*P,h);
//		int s=(int)(maxSize*t.getFrequency()+minSize);
//		Font font=new Font(Font.SANS_SERIF,Font.PLAIN,s);
//		int w=g.getFontMetrics(font).getStringBounds(t.getTag().toString(), g).getBounds().width;
//		int h=2*P+ RendererTools.breakingStringHeight((Graphics2D)g, font, t.getTag().toString(), defaultWidth, 0, 0); // account for breaks
//		return new Dimension(Math.min(w, defaultWidth)+2*P,h);
	}

	public Dimension getSuggestedSize(Node node, Object value) 
	{
		return new Dimension(defaultWidth+2*P,90);
	}
	
	@Override
	public Orientation getLabelOrientation(Node node, Object value) 
	{
		return Orientation.CENTER;
	}
	
	@Override
	public boolean hasLabel(Node node, Object value) 
	{
		return false;
	}
}
