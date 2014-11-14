package mayday.vis3d.utilities;

import javax.media.opengl.GL;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;

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
	public abstract void setCamera(GL gl, GLU glu, double width, double height);
	
	/**
	 * @param gl
	 */
	public abstract void adjustCamera(GL gl);
}
