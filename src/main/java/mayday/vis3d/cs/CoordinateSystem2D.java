package mayday.vis3d.cs;

import java.awt.Color;
import java.awt.Font;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;

import mayday.vis3.plots.PlotTimepointSetting;
import mayday.vis3d.AbstractPlot2DPanel;
import mayday.vis3d.cs.settings.CoordinateSystem2DSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public abstract class CoordinateSystem2D {

	protected CoordinateSystem2DSetting settings;
	
	private TextRenderer renderer;
	protected Labeling2D labeling;
	protected AbstractPlot2DPanel panel;
		
	/**
	 * @param panel
	 * @param dimension
	 * @param settings
	 */
	public CoordinateSystem2D(AbstractPlot2DPanel panel, CoordinateSystem2DSetting settings) {
		this.panel = panel;
		if(settings == null) {
			this.settings = new CoordinateSystem2DSetting(panel);
		} else {
			this.settings = settings;
		}
	}
	
	/**
	 * @param gl
	 * @param glu
	 */
	public abstract void draw(GL2 gl, GLU glu);
	/**
	 * @param gl
	 * @param timepoints 
	 * @param rotation
	 */
	public abstract void drawLabeling(GL2 gl, PlotTimepointSetting timepoints);
	
	/**
	 * @param gl
	 */
	public void drawGrid(GL2 gl) {
		gl.glLineWidth(1.1f);
		gl.glColor3d(Color.GRAY.getRed()/255.0, Color.GRAY.getGreen()/255.0, Color.GRAY.getBlue()/255.0);
		gl.glBegin(GL2.GL_LINE_LOOP);
			gl.glVertex2d(0, 0);
			gl.glVertex2d(settings.getChartSetting().getWidth(), 0);
			gl.glVertex2d(settings.getChartSetting().getWidth(), settings.getChartSetting().getHeight());
			gl.glVertex2d(0, settings.getChartSetting().getHeight());
		gl.glEnd();
		
		gl.glEnable(GL2.GL_LINE_STIPPLE);
		gl.glLineStipple(2, (short) 0x0f0f);

		double[] iteration = settings.getIteration();
		double xit = settings.getChartSetting().getWidth() / iteration[0];
		double yit = settings.getChartSetting().getHeight() / iteration[1];
		
		gl.glBegin(GL2.GL_LINES);
		for (double i = xit; i < settings.getChartSetting().getWidth(); i += xit) {
			gl.glVertex2d(i, 0);
			gl.glVertex2d(i, settings.getChartSetting().getHeight());
		}
		for (double i = yit; i < settings.getChartSetting().getHeight(); i += yit) {
			gl.glVertex2d(0, i);
			gl.glVertex2d(settings.getChartSetting().getWidth(), i);
		}
		gl.glEnd();
		gl.glDisable(GL2.GL_LINE_STIPPLE);
	}
	
	/**
	 * @return coordinate system settings
	 */
	public CoordinateSystem2DSetting getSetting() {
		return this.settings;
	}
	
	/**
	 * initialize labeling
	 * @param gl 
	 */
	public void initLabeling(GL2 gl) {
		this.labeling = new Labeling2D(this.settings);
		this.setRenderer(new TextRenderer(new Font("Sans.Serif", Font.BOLD, 24),
				true, true));
		
		getRenderer().setSmoothing(true);
		getRenderer().setColor(0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	/**
	 * Define the labeling of the coordinate system
	 * @param labeling
	 */
	public void setLabeling(Labeling2D labeling) {
		this.labeling = labeling;
	}
	/**
	 * @return the labeling of the coordinate system
	 */
	public Labeling2D getLabeling() {
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
}
