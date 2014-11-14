package mayday.vis3d.cs;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import mayday.vis3.plots.PlotTimepointSetting;
import mayday.vis3d.AbstractPlot2DPanel;
import mayday.vis3d.cs.settings.CoordinateSystem2DSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class StandardCoordinateSystem2D extends CoordinateSystem2D {

	/**
	 * @param panel
	 * @param dimension
	 * @param settings
	 */
	public StandardCoordinateSystem2D(AbstractPlot2DPanel panel,
			CoordinateSystem2DSetting settings) {
		super(panel, settings);
	}

	@Override
	public void draw(GL gl, GLU glu) {
		this.drawAxes(gl);
		if(settings.getGridSetting().getGridVisible()) {
			this.drawGrid(gl);
		}
	}

	@Override
	public void drawLabeling(GL gl, PlotTimepointSetting timepoints) {
		double[] iteration = settings.getIteration();
		float scale = (float)this.settings.getFontScale();
		
		if(this.labeling != null) {
			String[] xlabels = labeling.getXLabels();
			String[] ylabels = labeling.getYLabels();
			
			double width = settings.getChartSetting().getWidth();
			double height = settings.getChartSetting().getHeight();
			
			double xit = width / iteration[0];
			double yit = height / iteration[1];
			
			double xspace = 0.0;
			double yspace = 0.0;
			
			if(this.settings.getChartSetting().showXLabels()) {
				double k = 0, xpos = 0;
				for(int i = 0; i < xlabels.length; i++, k += xit) {
					Rectangle2D bounds = getRenderer().getBounds(xlabels[i]);
					double w2 = bounds.getWidth()*scale/2.0;
					double h2 = bounds.getHeight()*scale;
					xpos = k;

					gl.glPushMatrix();
					if(timepoints.useTimepoints()) {
						double[] experimentTimepoints = timepoints.getExperimentTimpoints();
						xpos = (experimentTimepoints[i] - experimentTimepoints[0]) * xit;
					}
					
					if(settings.verticalXLabels()) {
						gl.glTranslated(xpos + h2/2.0, -w2-2, 0.0);
						xspace = Math.max(xspace, bounds.getWidth() * scale);
					} else {
						gl.glTranslated(xpos, -h2-2, 0.0);
						if(i == 0) {
							xspace = Math.max(xspace, bounds.getHeight() * scale);
						}
					}
					
					if(settings.verticalXLabels()) {
						gl.glPushMatrix();
						gl.glRotated(90, 0, 0, 1);
					}
					
					getRenderer().setColor(Color.BLACK);
					getRenderer().begin3DRendering();
					getRenderer().draw3D(xlabels[i], -(float)w2, 0.0f, 0.0f, scale);
					getRenderer().end3DRendering();
					
					if(settings.verticalXLabels()) {
						gl.glPopMatrix();
					}
					
					gl.glPopMatrix();
				}
			}
			
			if(settings.getChartSetting().showYLabels()) {
				double k = 0;
				for(int i = 0; i < ylabels.length; i++, k += yit) {
					Rectangle2D bounds = getRenderer().getBounds(ylabels[i]);
					double w = bounds.getWidth() * scale;
					double h = bounds.getHeight() * scale;
					
					yspace = Math.max(yspace, w);
					
					gl.glPushMatrix();
					gl.glTranslated(0.0, k, 0.0);
					getRenderer().begin3DRendering();
					getRenderer().draw3D(ylabels[i], -(float)w - 5f, -(float)h/2, 0.0f, scale);
					getRenderer().end3DRendering();
					gl.glPopMatrix();
				}
			}
		
			String xLabel = this.settings.getVisibleArea().getXAxisTitle();
			String yLabel = this.settings.getVisibleArea().getYAxisTitle();
			
			Rectangle2D bounds = getRenderer().getBounds(xLabel);
			double w = bounds.getWidth() * scale;
			double h = bounds.getHeight() * scale;
			double w2 = w / 2.0;
			
			gl.glPushMatrix();
			gl.glTranslated(width/2-w2, -h-xspace-5, 0.0);
			getRenderer().begin3DRendering();
			getRenderer().draw3D(xLabel, 0, 0, 0, scale);
			getRenderer().end3DRendering();
			gl.glPopMatrix();
			
			bounds = getRenderer().getBounds(yLabel);
			w = bounds.getWidth() * scale;
			h = bounds.getHeight() * scale;
			
			gl.glPushMatrix();
			gl.glTranslated(-h-yspace-5, height/2-w/2, 0);
			gl.glRotated(90, 0, 0, 1);
			getRenderer().begin3DRendering();
			getRenderer().draw3D(yLabel, 0, 0, 0, scale);
			getRenderer().end3DRendering();
			gl.glPopMatrix();
		}
	}
	
	private void drawAxes(GL gl) {
		gl.glColor3d(0, 0, 0);
		gl.glLineWidth(2.0f);
		gl.glBegin(GL.GL_LINE_STRIP);
			gl.glVertex3d(settings.getChartSetting().getWidth(), 0, 0.05);
			gl.glVertex3d(0, 0, 0.05);
			gl.glVertex3d(0, settings.getChartSetting().getHeight(), 0.05);
		gl.glEnd();
		
		gl.glBegin(GL.GL_TRIANGLES);
			gl.glVertex3d(-5, settings.getChartSetting().getHeight(), 0.05);
			gl.glVertex3d(5, settings.getChartSetting().getHeight(), 0.05);
			gl.glVertex3d(0, settings.getChartSetting().getHeight() + 10, 0.05);
			
			gl.glVertex3d(settings.getChartSetting().getWidth(), 5, 0.05);
			gl.glVertex3d(settings.getChartSetting().getWidth(), -5, 0.05);
			gl.glVertex3d(settings.getChartSetting().getWidth() + 10, 0, 0.05);
		gl.glEnd();
		gl.glLineWidth(1.1f);
	}
}
