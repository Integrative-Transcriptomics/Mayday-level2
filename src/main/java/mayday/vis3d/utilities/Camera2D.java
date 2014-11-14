package mayday.vis3d.utilities;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLJPanel;

import mayday.vis3d.primitives.Point3D;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class Camera2D extends Camera3D implements MouseListener, MouseMotionListener, MouseWheelListener {
	private double scale = 1.0;
	private boolean MB3;
	
	private Point2D mousePosition;
	private Point2D movingDistance;
	private Point2D position2D;
	
	/**
	 * Default constructor
	 */
	public Camera2D() {
		super(new Point3D(0.0,0.0,250.0), 
				new Point3D(0.0,0.0,0.0));
		this.mousePosition = new Point2D.Double(0, 0);
		this.movingDistance = new Point2D.Double(0, 0);
		this.position2D = new Point2D.Double(0, 0);
	}
	
	/**
	 * @return moving distance
	 */
	public Point2D getPosition2D() {
		return this.position2D;
	}
	
	/**
	 * @param canvas
	 */
	public void registerTo(GLCanvas canvas) {
		canvas.addMouseWheelListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
	}
	
	/**
	 * @param panel
	 */
	public void registerTo(GLJPanel panel) {
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
		panel.addMouseWheelListener(this);
	}
	
	public void adjustCamera(GL gl) {
		gl.glScaled(scale, scale, 1);
		double scaleI = 1.0 / scale;
		gl.glTranslated(getPosition2D().getX() * scaleI, 
				getPosition2D().getY() * scaleI, 0);
	}
	
	/**
	 * @return current scale
	 */
	public double getScale() {
		return this.scale;
	}
	
	/**
	 * @param scale
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.isControlDown()) {
			int curRot = e.getWheelRotation();
			if(curRot > 0) {
				this.scale /= 1.1;
			} else {
				this.scale *= 1.1;
			}
			
		}
		((Component)e.getSource()).repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON3:
			this.MB3 = true;
			break;
		}
		this.mousePosition.setLocation(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON3:
			this.MB3 = false;
			break;
		}
		this.mousePosition.setLocation(e.getX(), e.getY());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(e.isAltGraphDown() && this.MB3) {
			double x = e.getX();
			double y = e.getY();
			double mX = this.mousePosition.getX();
			double mY = this.mousePosition.getY();
			this.movingDistance.setLocation(x-mX, mY-y);
			this.mousePosition.setLocation(x, y);
			this.position2D.setLocation(position2D.getX() + movingDistance.getX(), 
					position2D.getY() + movingDistance.getY());
		}
		
		if(e.getSource().getClass().equals(GLCanvas.class)) {
			((GLCanvas)e.getSource()).repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
}
