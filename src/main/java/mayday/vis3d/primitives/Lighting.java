package mayday.vis3d.primitives;

import javax.media.opengl.GL;

/**
 * This class defines and initializes lighting in opengl applications
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class Lighting {
	
	/**
	 * Initialize lighting with a white global light and 
	 * an additional white light source
	 * @param gl
	 */
	public static void initLighting(GL gl) {
		// define and enable lighting
		float[] ambient = { 0.0f, 0.0f, 0.0f, 1.0f };
		float[] diffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] position = { 0.0f, 3.0f, 10.0f, 0.0f };
		float[] lmodel_ambient = { 0.4f, 0.4f, 0.4f, 1.0f };
		float[] local_view = { 0.0f };

		gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambient, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, local_view, 0);
		
		float[] no_mat = { 0.0f, 0.0f, 0.0f, 1.0f };
		float[] no_shininess = { 0.0f };

		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, no_mat, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, no_mat, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, no_shininess, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_EMISSION, no_mat, 0);
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE);

		gl.glEnable(GL.GL_LIGHT0);
	}
}
