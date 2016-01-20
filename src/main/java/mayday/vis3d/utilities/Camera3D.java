package mayday.vis3d.utilities;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;

import mayday.vis3d.primitives.Point3D;
import mayday.vis3d.primitives.Rotation3D;
/**
 * @author G\u00FCnter J\u00E4ger
 * @date May 6, 2010
 */
public class Camera3D extends Camera implements MouseListener, MouseMotionListener, MouseWheelListener {
	
	/**
	 * x-axis
	 */
	public static final int X = 0;
	/**
	 * y-axis
	 */
	public static final int Y = 1;
	/**
	 * z-axis
	 */
	public static final int Z = 2;
	
	//camera positioning
	protected Point3D position;
	protected Point2D mousePosition;
	protected Point3D viewingDirection;
	//scene rotation
	private Rotation3D<Double> rotation;
	protected boolean MB3 = false;
	
	
	//zooming
	private double xStep = Math.cos(Math.toRadians(-90.0));
	private double zStep = Math.sin(Math.toRadians(-90.0));
	/**
	 * Create a new camera for 3d plots
	 * @param position
	 * @param viewingDirection
	 */
	public Camera3D(Point3D position, Point3D viewingDirection) {
		this.position = position;
		this.viewingDirection = viewingDirection;
		this.mousePosition = new Point2D.Double(0,0);
		this.rotation = new Rotation3D<Double>(0.0, 0.0, 0.0);
	}
	
	/**
	 * @param canvas
	 */
	public void registerTo(GLCanvas canvas) {
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseWheelListener(this);
	}
	
	/**
	 * @param panel
	 */
	public void registerTo(GLJPanel panel) {
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
		panel.addMouseWheelListener(this);
	}
	
	/**
	 * position the camera and the set the viewing direction
	 * @param gl
	 * @param glu
	 * @param width
	 * @param height
	 */
	public void setCamera(GL2 gl, GLU glu, double width, double height) {
		gl.glViewport(0, 0, (int)width * 2, (int)height * 2);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(60, width/height, 1.0, 1000.0);
		glu.gluLookAt(position.x, position.y, position.z, 
				viewingDirection.x, viewingDirection.y, viewingDirection.z, 
				0,1, 0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	public void adjustCamera(GL2 gl) {
		gl.glRotated(rotation.getXRotation(), 1.0f, 0.0f, 0.0f);
		gl.glRotated(rotation.getYRotation(), 0.0f, 1.0f, 0.0f);
		gl.glRotated(rotation.getZRotation(), 0.0f, 0.0f, 1.0f);
	}
	
	/**
	 * update mouse position on screen
	 * @param mousePosition
	 */
	public void setMousePosition(Point2D mousePosition) {
		this.mousePosition = mousePosition;
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public void setMousePosition(double x, double y) {
		this.mousePosition.setLocation(x, y);
	}
	/**
	 * @return current mouse position on the screen
	 */
	public Point2D getMousePosition() {
		return this.mousePosition;
	}
	/**
	 * Set the viewing direction of the camera
	 * @param viewingDirection
	 */
	public void setViewingDirection(Point3D viewingDirection) {
		this.viewingDirection = viewingDirection;
	}
	/**
	 * Set the viewing direction of the camera
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setViewingDirection(double x, double y, double z) {
		this.viewingDirection.set(x, y, z);
	}
	/**
	 * @return current viewing direction
	 */
	public Point3D getViewingDirection() {
		return this.viewingDirection;
	}
	/**
	 * Set the current camera position in 3d space
	 * @param position
	 */
	public void setPosition(Point3D position) {
		this.position = position;
	}
	/**
	 * Set the current camera position in 3d space
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(double x, double y, double z) {
		this.position.set(x, y, z);
	}
	
	/**
	 * @param i
	 * @param z
	 */
	public void setPosition(int i, double z) {
		this.position.set(i, z);
	}
	
	/**
	 * @return current camera position
	 */
	public Point3D getPosition() {
		return this.position;
	}
	/**
	 * Sets the current rotation angles of the scene
	 * @param xRotation
	 * @param yRotation
	 * @param zRotation
	 */
	public void setRotation(double xRotation, double yRotation, double zRotation) {
		this.rotation.setXRotation(xRotation);
		this.rotation.setYRotation(yRotation);
		this.rotation.setZRotation(zRotation);
	}
	/**
	 * @return current scene rotation
	 */
	public double[] getRotation() {
		return new double[]{rotation.getXRotation(), rotation.getYRotation(), rotation.getZRotation()};
	}
	/**
	 * Steps in x direction when zooming
	 * @return number of steps in x direction
	 */
	public double getZoomStepX() {
		return xStep;
	}
	/**
	 * Steps in z-direction when zooming
	 * @return number of steps in z direction
	 */
	public double getZoomStepZ() {
		return zStep;
	}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {
		switch (e.getButton()) {
		case MouseEvent.BUTTON3:
			this.MB3 = true;
			setMousePosition(e.getX(), e.getY());
			break;
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON3:
			this.MB3 = false;
			break;
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(e.isAltGraphDown() && this.MB3) {
			int xdist = e.getX() - (int)getMousePosition().getX();
			int ydist = e.getY() - (int)getMousePosition().getY();
			
			double xfactor = Math.abs(xdist)/10.0;
			double yfactor = Math.abs(ydist)/10.0;
			
			double xPos = getPosition().x;
			double yPos = getPosition().y;
			double zPos = getPosition().z;
			double ylook = getViewingDirection().y;
			
			//process camera movement
			if (xdist > 0) {// move left
				xPos = getPosition().x + getZoomStepZ() * xfactor;
				zPos = getPosition().z - getZoomStepX() * xfactor;
			} else if (xdist < 0) {// move right
				xPos = getPosition().x - getZoomStepZ() * xfactor;
				zPos = getPosition().z + getZoomStepX() * xfactor;
			}
			if (ydist > 0) { // move up
				yPos = getPosition().y + yfactor;
				ylook = getViewingDirection().y + yfactor;
			} else if (ydist < 0) { // move down
				yPos = getPosition().y - yfactor;
				ylook = getViewingDirection().y - yfactor;
			}
			// new look-at position
			double xlook = getPosition().x + getZoomStepX() * 1000.0;
			double zlook = getPosition().z + getZoomStepZ() * 1000.0;
			
			setPosition(xPos, yPos, zPos);
			setViewingDirection(xlook, ylook, zlook);
			//update mouse location
			setMousePosition(e.getX(), e.getY());
		} else if(this.MB3 && !e.isControlDown()){
			/*
			if (MB1) {
				int mX = e.getX();
				int mY = e.getY();
				
				int x = camera.getPrevMouseX();
				int y = camera.getPrevMouseY();
				
				double width = Math.max(Math.abs(x - mX), 5);
				double height = Math.max(Math.abs(y - mY), 5);
				
				if(mX < x){
					x = mX;
				}
				if(mY < y){
					y = mY;
				}
				picker.setRectangle(x, y, width, height);
			}
			if (MB2) {
			}*/
			// dragging with mouse button 3 results in a rotation of the system
			int x = e.getX();
			int y = e.getY();

			Dimension size = e.getComponent().getSize();

			double thetaY = 360.0 * ((x - getMousePosition().getX()) / (double)size.width);
			double thetaX = 360.0 * ((getMousePosition().getY() - y) / (double) size.height);

			setMousePosition(x, y);
			rotation.setXRotation(rotation.getXRotation() + thetaX);
			rotation.setYRotation(rotation.getYRotation() + thetaY);
		} else if(this.MB3 && e.isControlDown()) {
			int x = e.getX();
			int y = e.getY();

			Dimension size = e.getComponent().getSize();

			double thetaZ = 360.0 * ((getMousePosition().getY() - y) / (double)size.height);

			setMousePosition(x, y);
			rotation.setZRotation(rotation.getZRotation() + thetaZ);
		}
		
		if(e.getSource().getClass().equals(GLCanvas.class)) {
			((GLCanvas)e.getSource()).repaint();
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.isControlDown()) {
			int curRot = e.getWheelRotation();
			double xPos = getPosition().x;
			double yPos = getPosition().y;
			double zPos = getPosition().z;
			double xlook = getViewingDirection().x;
			double ylook = getViewingDirection().y;
			double zlook = getViewingDirection().z;
			
			if(curRot < 0){
				curRot = Math.abs(curRot);
				xPos = getPosition().x + getZoomStepX() * curRot;
				zPos = getPosition().z + getZoomStepZ() * curRot;
			} else {
				xPos = getPosition().x - getZoomStepX() * curRot;
				zPos = getPosition().z - getZoomStepZ() * curRot;
			}
			
			xlook = getPosition().x + getZoomStepX() * 1000.0;
			zlook = getPosition().z + getZoomStepZ() * 1000.0;
			
			setPosition(xPos, yPos, zPos);
			setViewingDirection(xlook, ylook, zlook);
			
			((Component)e.getSource()).repaint();
		}
	}
}
