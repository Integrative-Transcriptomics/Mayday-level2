package it.genomering.render.paths;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;

@SuppressWarnings("serial")
public class GRPath extends Path2D.Double {

	protected Color color;
	protected Stroke stroke;
	
	public void setStyle(Color color, Stroke stroke) {
		this.color=color;
		this.stroke=stroke;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Stroke getStroke() {
		return stroke;
	}
	
	public void prepareGraphics(Graphics2D g) {
		g.setColor(color);
		g.setStroke(stroke);
	}
	
	public void paint(Graphics2D g) {
		prepareGraphics(g);
		g.draw(this);
	}
	
	public void addSegment(double radius, double alpha_start, double alpha_end, boolean connect) {
		Arc2D arc = new Arc2D.Double(-radius,-radius,2*radius,2*radius,alpha_start,alpha_end-alpha_start,Arc2D.OPEN);		
		append(arc, connect);
	}

	public static double polarToX(double radius, double alpha) {
		return Math.sin(Math.toRadians(90+alpha))*radius;
	}
	
	public static double polarToY(double radius, double alpha) {
		return Math.cos(Math.toRadians(90+alpha))*radius;
	}
	
	public static double XYtoPolarAlpha(double x, double y) {		
		return 90+Math.toDegrees(Math.atan( y / x ))+(x<0?180:0); 
	}
	
	public static double XYtoPolarRadius(double x, double y) {
		return Math.sqrt(x*x + y*y);
	}
	
}
