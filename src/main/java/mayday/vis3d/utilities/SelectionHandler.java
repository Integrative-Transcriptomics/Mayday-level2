package mayday.vis3d.utilities;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;

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
	public abstract void drawSelectionRectangle(GL gl, GLU glu, int width, int height, Camera camera);
	/**
	 * @param gl
	 * @param glu
	 * @param width
	 * @param height
	 * @param camera
	 */
	public abstract void pickObjects(GL gl, GLU glu, int width, int height, Camera camera);
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
