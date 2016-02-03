package mayday.vis3d.cs;

import java.awt.Color;
import java.awt.Font;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;

import mayday.vis3d.AbstractPlot3DPanel;
import mayday.vis3d.cs.settings.CoordinateSystem3DSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public abstract class CoordinateSystem3D {
	
	protected CoordinateSystem3DSetting settings;
	protected Labeling3D labeling;
	private TextRenderer renderer;
		
	/**
	 * @param panel
	 * @param settings
	 */
	public CoordinateSystem3D(AbstractPlot3DPanel panel, CoordinateSystem3DSetting settings) {
		this(panel, settings, false);
	}
	
	/**
	 * @param panel
	 * @param settings
	 * @param oneCS
	 */
	public CoordinateSystem3D(AbstractPlot3DPanel panel, CoordinateSystem3DSetting settings, boolean oneCS) {
		if(settings == null) {
			this.settings = new CoordinateSystem3DSetting(panel, oneCS);
		} else {
			this.settings = settings;
		}
	}
	
	/**
	 * @param gl
	 */
	public void initAxesLabeling(GL2 gl) {
		this.labeling = new Labeling3D(settings);
		this.setRenderer(new TextRenderer(new Font("Sans.Serif", Font.BOLD, 24),
					true, true));
			
		getRenderer().setSmoothing(true);
		getRenderer().setColor(0.0f, 0.0f, 0.0f, 1.0f);
	}
	/**
	 * @param gl
	 * @param glu
	 */
	public abstract void draw(GL2 gl, GLU glu);
	/**
	 * @param gl
	 * @param rotation
	 * @param timepoints 
	 */
	public abstract void drawLabeling(GL2 gl, double[] rotation, double[] timepoints);
	/**
	 * draw labels without time points
	 * @param gl
	 * @param rotation
	 */
	public void drawLabeling(GL2 gl, double[] rotation) {
		this.drawLabeling(gl, rotation, null);
	}
	
	/**
	 * @param gl
	 */
	public void drawGrid(GL2 gl) {
		
		double width = settings.getVisibleArea().getWidth();
		double height = settings.getVisibleArea().getHeight();
		double depth = settings.getVisibleArea().getDepth();
		
		gl.glLineWidth(1.1f);
		gl.glColor3d(Color.GRAY.getRed()/255.0, Color.GRAY.getGreen()/255.0, Color.GRAY.getBlue()/255.0);
		gl.glBegin(GL2.GL_LINE_LOOP);
			gl.glVertex3d(-width, height, depth);
			gl.glVertex3d(-width, -height, depth);
			gl.glVertex3d(width, -height, depth);
			gl.glVertex3d(width, -height, -depth);
			gl.glVertex3d(width, height, -depth);
			gl.glVertex3d(-width, height, -depth);
		gl.glEnd();
		
		gl.glBegin(GL2.GL_LINES);
			gl.glVertex3d(-width, -height, depth);
			gl.glVertex3d(-width, -height, -depth);
			gl.glVertex3d(-width, -height, -depth);
			gl.glVertex3d(-width, height, -depth);
			gl.glVertex3d(-width, -height, -depth);
			gl.glVertex3d(width, -height, -depth);
		gl.glEnd();
		
		gl.glEnable(GL2.GL_LINE_STIPPLE);
		gl.glLineStipple(2, (short) 0x0f0f);

		double[] iteration = settings.getIteration();
		
		gl.glBegin(GL2.GL_LINES);
		for (double i = iteration[2]; i < depth * 2; i += iteration[2]) {
			gl.glVertex3d(-width, -height, depth-i);
			gl.glVertex3d(-width, height, depth-i);
		}
		for (double i = iteration[1]; i < height * 2; i += iteration[1]) {
			gl.glVertex3d(-width, height-i, depth);
			gl.glVertex3d(-width, height-i, -depth);
		}
		for (double i = iteration[2]; i < depth * 2; i += iteration[2]) {
			gl.glVertex3d(-width, -height, depth-i);
			gl.glVertex3d(width, -height, depth-i);
		}
		for (double i = iteration[0]; i < width * 2; i += iteration[0]) {
			gl.glVertex3d(width-i, -height, depth);
			gl.glVertex3d(width-i, -height, -depth);
		}
		for (double i = iteration[0]; i < width * 2; i += iteration[0]) {
			gl.glVertex3d(width-i, height, -depth);
			gl.glVertex3d(width-i, -height, -depth);
		}
		for (double i = iteration[1]; i < height * 2; i += iteration[1]) {
			gl.glVertex3d(width, height-i, -depth);
			gl.glVertex3d(-width, height-i, -depth);
		}
		gl.glEnd();
		gl.glDisable(GL2.GL_LINE_STIPPLE);
	}
	/**
	 * @return the chosen iterations
	 */
	public double[] getIteration() {
		return this.settings.getIteration();
	}

	/**
	 * @return the chosen dimensions
	 */
	public abstract double[] getDimension3D();
	/**
	 * @return the coordinate system identifier
	 */
	public abstract String getID();
	/**
	 * @return the attached coordinate system setting
	 */
	public CoordinateSystem3DSetting getSetting() {
		return this.settings;
	}
	/**
	 * Define the labeling of the coordinate system
	 * @param labeling
	 */
	public void setLabeling(Labeling3D labeling) {
		this.labeling = labeling;
	}
	/**
	 * @return the labeling of the coordinate system
	 */
	public Labeling3D getLabeling() {
		return this.labeling;
	}

	/**
	 * @param renderer
	 */
	public void setRenderer(TextRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * @return text renderer
	 */
	public TextRenderer getRenderer() {
		return renderer;
	}

	/**
	 * @return font scale
	 */
	public double getScale() {
		return this.settings.getFontScale();
	}
}
