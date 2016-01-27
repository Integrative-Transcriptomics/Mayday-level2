package mayday.vis3d.utilities;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import mayday.core.Probe;
import mayday.vis3d.AbstractPlot3DPanel;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class SelectionHandler3D extends SelectionHandler {
	
	protected static final int BUF_SIZE = 1048576;
	
	protected Rectangle2D pickRect = null;
	protected boolean MB1 = false, altDown = false, controlDown = false;
	protected Point2D mousePosition;
	protected boolean pickable = false;
	protected boolean rectanglePicking = false;
	protected boolean MB3 = false;
	
	protected AbstractPlot3DPanel panel;
	protected HashMap<Integer, Probe> selectableProbes;
	protected HashMap<Integer, Object> selectableObjects;
	
	/**
	 * @param panel
	 */
	public SelectionHandler3D(AbstractPlot3DPanel panel) {
		this.mousePosition = new Point2D.Double(0,0);
		this.panel = panel;
	}
	
	/**
	 * @param pickable
	 */
	public void setPickable(boolean pickable) {
		this.pickable = pickable;
	}

	/**
	 * @param gl
	 * @param glu
	 * @param width
	 * @param height
	 * @param camera
	 */
	public void pickObjects(GL2 gl, GLU glu, int width, int height, Camera camera) {
		if(this.pickable) {
			// Tell OpenGL where to store the hits
			int[] selectBuf = new int[BUF_SIZE];
			IntBuffer selectBuffer = Buffers.newDirectIntBuffer(BUF_SIZE);
			gl.glSelectBuffer(BUF_SIZE, selectBuffer);
			// enter selection mode
			gl.glRenderMode(GL2.GL_SELECT);
			/*
			 * redefine the viewing volume so that only a small area around the
			 * place where the mouse was clicked is rendered. To do so, set matrix
			 * mode to GL_PROJECTION and push the current matrix to save the normal
			 * rendering mode settings.
			 */
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			/*
			 * Define the viewing volume to render only a small area around the
			 * cursor. To do so, get the current viewport.
			 */
			int[] viewport = new int[4];
			gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
			/*
			 * initialize the picking matrix (5x5 pixels around the cursor) and set
			 * the projection as for the normal rendering mode.
			 */
			glu.gluPickMatrix(pickRect.getCenterX(), viewport[3] - pickRect.getCenterY(), 
					pickRect.getWidth(), pickRect.getHeight(), viewport, 0);
			
			glu.gluPerspective(60, (double)width/(double)height, 1.0, 1000.0);

			// get back to the modelview matrix and initialize the name stack
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glInitNames();
			gl.glPushName(0);

			double x = ((Camera3D)camera).getPosition().x;
			double y = ((Camera3D)camera).getPosition().y;
			double z = ((Camera3D)camera).getPosition().z;
			double xlook = ((Camera3D)camera).getViewingDirection().x;
			double ylook = ((Camera3D)camera).getViewingDirection().y;
			double zlook = ((Camera3D)camera).getViewingDirection().z;
			
			// look in same direction as for drawing
			glu.gluLookAt(x, y, z, xlook, ylook, zlook, 0, 1, 0);
			
			gl.glPushMatrix();
			camera.adjustCamera(gl);
			// render scene in selection mode
			this.panel.drawSelectable(gl, GL2.GL_SELECT);
			gl.glPopMatrix();
			
			gl.glPopName();

			// restore the original matrix
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glPopMatrix();
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glFlush();
			/*
			 * return to normal rendering mode and get the number of hit records
			 * that were created while rendering in the selection mode.
			 */
			int hits = gl.glRenderMode(GL2.GL_RENDER);
			selectBuffer.get(selectBuf);
			
			if(this.rectanglePicking) {
				Set<Probe> selectedProbes = this.identifyRectanglePickedProbes(hits, selectBuf);
				this.processRectangleSelectedProbes(selectedProbes);
				this.rectanglePicking = false;
			} else {
				this.identifiyPickedHit(hits, selectBuf);
			}
			
			//reset selection
			this.clearRectangle();
			this.pickable = false;
			//update plot to remove selection rectangle
			this.panel.updatePlot();
		}
	}
	
	/**
	 * @param pressed
	 */
	public void setMouseButton3Pressed(boolean pressed) {
		this.MB3 = pressed;
	}
	
	/**
	 * initializes two hash maps for fast selection processing
	 * the first hash map only contains probes
	 * the second hash map contains selectable objects that are not probes
	 */
	public void initializeHashMaps() {
		//store probes and corresponding hash codes for selection processing
		Object[] probes = this.panel.viewModel.getProbes().toArray();
		this.selectableProbes = new HashMap<Integer, Probe>();
		for(int i = 0; i < probes.length; i++) {
			this.selectableProbes.put(probes[i].hashCode(), (Probe)probes[i]);
		}
		//store selectable objects and corresponding hash codes for selection processing
		Object[] objects = this.panel.getSelectableObjects();
		this.selectableObjects = new HashMap<Integer, Object>();
		if(objects != null) {
			for(int i = 0; i < objects.length; i++) {
				this.selectableObjects.put(objects[i].hashCode(), objects[i]);
			}
		}
	}
	/**
	 * update the selection handler hash maps for fast selection identification
	 */
	public void update() {
		this.initializeHashMaps();
	}
	
	/**
	 * @param hits
	 * @param selectBuf
	 */
	public void identifiyPickedHit(int hits, int[] selectBuf) {
		int names, ptr = 0, minZ, ptrNames = 0, numberOfNames = 0;
		minZ = Integer.MAX_VALUE;
		
		ArrayList<Integer> selection = new ArrayList<Integer>();
		
		//add only minimum hits to selection
		for (int i = 0; i < hits; i++) {
			names = selectBuf[ptr];
			ptr++;
			if (selectBuf[ptr] < minZ) {
				numberOfNames = names;
				minZ = selectBuf[ptr];
				ptrNames = ptr + 2;
			}
			ptr += names + 2;
		}
		ptr = ptrNames;
		for(int i = 0; i < numberOfNames; i++, ptr++){
			selection.add(selectBuf[ptr]);
		}
		
		if(selection.size() > 0) {
			Object selected = this.selectableObjects.get(selection.get(0));
			if(selected == null) {
				selected = this.selectableProbes.get(selection.get(0));
				if(selected != null) {
					this.processPickedHit(selected, true);
				} else {
					return;
				}
			} else {
				this.processPickedHit(selected, false);
			}
		}
	}
	
	/**
	 * @param hits
	 * @param selectBuf
	 * @return set of selected and identified probes
	 */
	public Set<Probe> identifyRectanglePickedProbes(int hits, int[] selectBuf) {
		int names, ptr = 0;
		ArrayList<Integer> selection = new ArrayList<Integer>();
		
		//add all hits to selection
		for(int i = 0; i < hits; i++){
			names = selectBuf[ptr];
			ptr++; //step over number of names
			ptr++; //step over minimum depth value
			ptr++; //step over maximum depth value
			for(int j = 0; j < names; j++, ptr++){
				selection.add(selectBuf[ptr]);
			}
		}
		
		Set<Probe> newSelection = new HashSet<Probe>();

		for (int i = 0; i < selection.size(); i++) {
			int hashCode = selection.get(i);
			Probe p = this.selectableProbes.get(hashCode);
			if(p != null) {
				newSelection.add(p);
			}
		}
		
		return newSelection;
	}
	
	/**
	 * @param pickedObject
	 * @param isProbe 
	 */
	public void processPickedHit(Object pickedObject, boolean isProbe) {
		if(pickedObject == null) return;
		/* 
		 * if the selected object is a probe, 
		 * add it to the set of selected probes depending on 
		 * the additionally pressed buttons
		 */
		if(isProbe) {
			Probe p = (Probe)pickedObject;
			Set<Probe> newSelection = new HashSet<Probe>();
			newSelection.add(p);
			
			//process probes according to key events specified by mayday
			Set<Probe> previousSelection = new HashSet<Probe>(this.panel.viewModel.getSelectedProbes());
			if (controlDown && altDown) {
				previousSelection.removeAll(newSelection);
				newSelection = previousSelection;
			} else if (controlDown) {
				Set<Probe> intersection = new HashSet<Probe>();
				intersection.addAll(newSelection);
				intersection.retainAll(previousSelection);
				newSelection.addAll(previousSelection);
				newSelection.removeAll(intersection);
			} else if (altDown) {
				newSelection.retainAll(previousSelection);
			}
			this.panel.viewModel.setProbeSelection(newSelection);
		} else {
			/*
			 * if the selected object is not a probe, run the call-back function to
			 * deal with the selected object
			 */
			this.panel.processSelectedObjects(new Object[]{pickedObject}, controlDown, altDown);
		}
	}
	
	/**
	 * @param newSelection
	 */
	public void processRectangleSelectedProbes(Set<Probe> newSelection) {
		Set<Probe> previousSelection = new HashSet<Probe>(this.panel.viewModel.getSelectedProbes());
		if (controlDown && altDown) {
			previousSelection.removeAll(newSelection);
			newSelection = previousSelection;
		} else if (controlDown) {
			newSelection.addAll(previousSelection);
		} else if (altDown) {
			newSelection.retainAll(previousSelection);
		}
		this.panel.viewModel.setProbeSelection(newSelection);
	}
	
	/**
	 * Takes a GLCanvas and registers ObjectPicker.this as Listener.
	 * If this step is left out, ObjectPciker cannot operate on a Canvas.
	 * @param canvas
	 */
	public void registerTo(GLCanvas canvas) {
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
	}
	
	public void registerTo(GLJPanel gljpanel) {
		gljpanel.addMouseListener(this);
		gljpanel.addMouseMotionListener(this);
		gljpanel.addKeyListener(this);
	}
	
	/**
	 * draws a selection rectangle in red
	 * @param gl
	 * @param glu
	 * @param width
	 * @param height
	 * @param camera
	 */
	public void drawSelectionRectangle(GL2 gl, GLU glu, int width, int height, Camera camera) {
		if(largeEnough()) {
			double x = pickRect.getX();
			double y = pickRect.getY();
			double w = pickRect.getWidth();
			double h = pickRect.getHeight();
			
			gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
			//gl.glDisable(GL.GL_LIGHTING);
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluOrtho2D(0, width, height, 0);
			
			//set color to red and double standard line width
			gl.glColor3f(1.0f, 0.0f, 0.0f);
			gl.glLineWidth(2.1f);
			
			//draw selection rectangle
			gl.glBegin(GL2.GL_LINE_LOOP);
				gl.glVertex2d(x, y);
				gl.glVertex2d(x+w, y);
				gl.glVertex2d(x+w, y+h);
				gl.glVertex2d(x, y+h);
			gl.glEnd();
			
			//switch back to normal
			gl.glLineWidth(1.1f);
			gl.glColor3f(0.0f, 0.0f, 0.0f);
			//gl.glEnable(GL.GL_LIGHTING);
			gl.glPopAttrib();
			
			//camera.setCamera(gl, glu, width, height);
		}
	}
	
	/**
	 * set selection rectangle to null, nothing to pick
	 */
	public void clearRectangle() {
		this.pickRect = null;
	}
	
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void setRectangle(double x, double y, double width, double height){
		this.pickRect = new Rectangle2D.Double(x , y, width, height);
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public void setMousePosition(double x, double y) {
		this.mousePosition.setLocation(x, y);
	}
	
	/**
	 * @return true, if rectangle is large enough to be drawn, false else
	 */
	public boolean largeEnough() {
		if(pickRect == null) return false;
		return pickRect.getWidth() > 5 && pickRect.getHeight() > 5;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			this.setRectangle(e.getX() - 2.5, e.getY() - 2.5, 2.5, 2.5);
			this.pickable = true;
			break;
		}
		panel.updatePlot();
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			this.setMousePosition(e.getX(), e.getY());
			this.MB1 = true;
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			if(this.pickRect != null){
				this.pickable = true;
			}
			this.MB1 = false;
			panel.updatePlot();
			break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(this.MB1) {
			int mX = e.getX();
			int mY = e.getY();
			
			double x = this.mousePosition.getX();
			double y = this.mousePosition.getY();
			
			double width = Math.max(Math.abs(x - mX), 5);
			double height = Math.max(Math.abs(y - mY), 5);
			
			if(mX < x){
				x = mX;
			}
			if(mY < y){
				y = mY;
			}
			
			this.setRectangle(x, y, width, height);
			this.rectanglePicking = true;
			//panel.getCanvas()..repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ALT:
			altDown = true;
		case KeyEvent.VK_CONTROL:
			controlDown = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ALT:
			altDown = false;
		case KeyEvent.VK_CONTROL:
			controlDown = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}
