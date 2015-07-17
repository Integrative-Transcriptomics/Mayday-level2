package mayday.vis3d.utilities;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public abstract class SelectionHandler implements MouseListener, MouseMotionListener, KeyListener{
	/**
	 * @param gl
	 * @param glu
	 * @param width
	 * @param height
	 * @param camera
	 */
	public abstract void drawSelectionRectangle(GL2 gl, GLU glu, int width, int height, Camera camera);
	/**
	 * @param gl
	 * @param glu
	 * @param width
	 * @param height
	 * @param camera
	 */
	public abstract void pickObjects(GL2 gl, GLU glu, int width, int height, Camera camera);
	/**
	 * 
	 */
	public abstract void initializeHashMaps();
	/**
	 * 
	 */
	public abstract void update();
	/**
	 * @param canvas
	 */
	public abstract void registerTo(GLCanvas canvas);
	
	public abstract void registerTo(GLJPanel gljpanel);
}
