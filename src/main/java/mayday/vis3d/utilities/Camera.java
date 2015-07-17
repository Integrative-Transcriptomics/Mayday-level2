package mayday.vis3d.utilities;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public abstract class Camera {

	/**
	 * @param canvas
	 */
	public abstract void registerTo(GLCanvas canvas);
	
	/**
	 * @param panel
	 */
	public abstract void registerTo(GLJPanel panel);
	
	/**
	 * @param gl
	 * @param glu
	 * @param width
	 * @param height
	 */
	public abstract void setCamera(GL2 gl, GLU glu, double width, double height);
	
	/**
	 * @param gl
	 */
	public abstract void adjustCamera(GL2 gl);
}
