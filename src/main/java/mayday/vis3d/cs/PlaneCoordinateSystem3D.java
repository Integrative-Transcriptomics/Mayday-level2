package mayday.vis3d.cs;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import mayday.vis3d.AbstractPlot3DPanel;
import mayday.vis3d.cs.settings.CoordinateSystem3DSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class PlaneCoordinateSystem3D extends CoordinateSystem3D {

	/**
	 * identifier for this coordinate system
	 */
	public static final String ID = "Plane";
	
	/**
	 * @param panel
	 * @param settings
	 */
	public PlaneCoordinateSystem3D(AbstractPlot3DPanel panel, CoordinateSystem3DSetting settings) {
		super(panel, settings);
	}
	/**
	 * @param panel
	 * @param settings
	 * @param oneCS
	 */
	public PlaneCoordinateSystem3D(AbstractPlot3DPanel panel, CoordinateSystem3DSetting settings, boolean oneCS) {
		super(panel, settings, oneCS);
	}

	@Override
	public void draw(GL gl, GLU glu) {
		this.drawAxes(gl, glu);
	}

	@Override
	public double[] getDimension3D() {
		return this.settings.getVisibleArea().getDimension();
	}
	
	/**
	 * @param gl
	 * @param glu
	 */
	public void drawAxes(GL gl, GLU glu) {
		double width = settings.getVisibleArea().getWidth();
		double height = settings.getVisibleArea().getHeight();
		double depth = settings.getVisibleArea().getDepth();
		
		gl.glLineWidth(2.0f);
		gl.glBegin(GL.GL_LINES);
			//x-axis
			gl.glColor3d(0, 0, 0);
			gl.glVertex3d(-width, -height, depth);
			gl.glVertex3d(width, -height, depth);
			//y-axis
			gl.glColor3d(0, 0, 0);
			gl.glVertex3d(-width, -height, depth);
			gl.glVertex3d(-width, height, depth);
			//z-axis
			gl.glColor3d(0, 0, 0);
			gl.glVertex3d(width, -height, depth);
			gl.glVertex3d(width, -height, -depth);
		gl.glEnd();
		
		gl.glEnable(GL.GL_LIGHTING);
		
		GLUquadric arrow = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(arrow, GLU.GLU_FILL);
		glu.gluQuadricNormals(arrow, GLU.GLU_SMOOTH);
		
		// define arrow color
		float[] color = new float[]{0, 0, 0};
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, color, 0);
		// arrow on x-axis
		gl.glPushMatrix();
		gl.glTranslated(width, -height, depth);
		gl.glRotated(90, 0, 1, 0);
		glu.gluCylinder(arrow, 0.125f, 0.0f, 0.5f, 10, 5);
		gl.glPopMatrix();

		color = new float[]{0, 0, 0};
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, color, 0);
		// arrow on y-axis
		gl.glPushMatrix();
		gl.glTranslated(-width, height, depth);
		gl.glRotated(-90, 1, 0, 0);
		glu.gluCylinder(arrow, 0.125f, 0.0f, 0.5f, 10, 5);
		gl.glPopMatrix();
		
		color = new float[]{0, 0, 0};
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, color, 0);
		// arrow on z-axis
		gl.glPushMatrix();
		gl.glTranslated(width, -height, -depth);
		gl.glRotated(180, 1, 0, 0);
		glu.gluCylinder(arrow, 0.125f, 0.0f, 0.5f, 10, 5);
		gl.glPopMatrix();
		
		gl.glDisable(GL.GL_LIGHTING);
	}

	@Override
	public void drawLabeling(GL gl, double[] rotation, double[] timepoints) {
		double width = settings.getVisibleArea().getWidth();
		double height = settings.getVisibleArea().getHeight();
		double depth = settings.getVisibleArea().getDepth();

		float scale = (float)this.settings.getFontScale();
		
		double[] iteration = settings.getIteration();
		if(this.labeling != null) {
			String[] xlabels = labeling.getXLabels();
			String[] ylabels = labeling.getYLabels();
			String[] zlabels = labeling.getZLabels();
			
			double xrot = -rotation[0];
			double yrot = -rotation[1];
			double zrot = -rotation[2];
			
			double xit = iteration[0];
			double yit = iteration[1];
			double zit = iteration[2];
			
			double k = -width;
			
			boolean xhorizontal = !settings.getVerticalXAxisLabels();
			
			for(int i = 0; i < xlabels.length; i++, k += xit) {
				double xpos = k;
				if(timepoints != null) {
					xpos = (timepoints[i] - timepoints[0]) * xit - width;
				}
				this.drawXLabel(gl, xlabels[i], 
						xpos, -height, depth, 
						xrot, yrot, zrot, xhorizontal);
			}
			
			k = -height;
			for(int i = 0; i < ylabels.length; i++, k += yit) {
				this.drawYLabel(gl, ylabels[i], 
						-width, k, depth, 
						xrot, yrot, zrot);
			}
			
			k = -depth;
			for(int i = 0; i  < zlabels.length; i++, k += zit) {
				this.drawZLabel(gl, zlabels[i], 
						width, -height, k, 
						xrot, yrot, zrot);
			}
			
			String xLabel = labeling.getXAxisLabel();
			String yLabel = labeling.getYAxisLabel();
			String zLabel = labeling.getZAxisLabel();
			
			Rectangle2D bounds = getRenderer().getBounds(xLabel);
			double w = bounds.getWidth()*scale;
			double w2 = w/2.0;
			double h2 = bounds.getHeight()*scale/2;
			
			getRenderer().setColor(Color.RED);
			gl.glPushMatrix();
			gl.glTranslated(width + w + 0.5, -height, depth);
			gl.glRotated(zrot, 0, 0, 1);
			gl.glRotated(yrot, 0, 1, 0);
			gl.glRotated(xrot, 1, 0, 0);
			getRenderer().begin3DRendering();
			getRenderer().draw3D(xLabel, -(float)w2, -(float)h2, 0, scale);
			getRenderer().end3DRendering();
			gl.glPopMatrix();
			
			bounds = getRenderer().getBounds(yLabel);
			w2 = bounds.getWidth()*scale/2.0;
			double h = bounds.getHeight()*scale;
			h2 = h/2.0;
			
			getRenderer().setColor(Color.GREEN);
			gl.glPushMatrix();
			gl.glTranslated(-width, height + h + 0.5, depth);
			gl.glRotated(zrot, 0, 0, 1);
			gl.glRotated(yrot, 0, 1, 0);
			gl.glRotated(xrot, 1, 0, 0);
			getRenderer().begin3DRendering();
			getRenderer().draw3D(yLabel, -(float)w2, -(float)h2, 0, scale);
			getRenderer().end3DRendering();
			gl.glPopMatrix();
			
			bounds = getRenderer().getBounds(zLabel);
			w = bounds.getWidth()*scale;
			w2 = w/2.0;
			h2 = bounds.getHeight()*scale/2.0;
			
			getRenderer().setColor(Color.BLUE);
			gl.glPushMatrix();
			gl.glTranslated(width, -height, -(depth + w + 0.5));
			gl.glRotated(zrot, 0, 0, 1);
			gl.glRotated(yrot, 0, 1, 0);
			gl.glRotated(xrot, 1, 0, 0);
			getRenderer().begin3DRendering();
			getRenderer().draw3D(zLabel, -(float)w2, -(float)h2, -(float)w2, scale);
			getRenderer().end3DRendering();
			gl.glPopMatrix();
			
			getRenderer().setColor(Color.BLACK);
		}
	}

	@Override
	public String getID() {
		return ID;
	}
	
	private void drawXLabel(GL gl, String label, double x, double y,double z, double xrot, double yrot, double zrot, boolean horizontal) {
		if(label == null) {
			label = "0.0";
		}

		float scale = (float)this.settings.getFontScale();
		
		Rectangle2D bounds = getRenderer().getBounds(label);
		double w2 = bounds.getWidth()*scale / 2.0;
		double h2 = bounds.getHeight() * scale / 2.0;
		
		gl.glPushMatrix();
		if(!horizontal) {
			gl.glTranslated(x, y-w2-0.1, z);
		} else {
			gl.glTranslated(x, y-h2-0.1, z);
		}
		gl.glRotated(zrot, 0, 0, 1);
		gl.glRotated(yrot, 0, 1, 0);
		gl.glRotated(xrot, 1, 0, 0);
		if(!horizontal) {
			gl.glPushMatrix();
			gl.glRotated(90, 0, 0, 1);
		}
		getRenderer().begin3DRendering();
		getRenderer().draw3D(label, -(float)w2, -(float)h2, 0.0f, (float)scale);
		getRenderer().end3DRendering();
		if(!horizontal) {
			gl.glPopMatrix();
		}
		gl.glPopMatrix();
	}
	
	private void drawYLabel(GL gl, String label, double x, double y,double z, double xrot, double yrot, double zrot) {
		if(label == null) {
			label = "0.0";
		}

		float scale = (float)this.settings.getFontScale();
		
		Rectangle2D bounds = getRenderer().getBounds(label);
		double w = bounds.getWidth() * scale;
		double h2 = (bounds.getHeight() * scale) / 2.0;
		
		gl.glPushMatrix();
		gl.glTranslated(x-0.1, y, z);

		gl.glRotated(zrot, 0, 0, 1);
		gl.glRotated(yrot, 0, 1, 0);
		gl.glRotated(xrot, 1, 0, 0);
		
		getRenderer().begin3DRendering();
		getRenderer().draw3D(label, -(float)w, -(float)h2, 0.0f, (float)scale);
		getRenderer().end3DRendering();
		
		gl.glPopMatrix();
	}
	
	private void drawZLabel(GL gl, String label, double x, double y,double z, double xrot, double yrot, double zrot) {
		if(label == null) {
			label = "0.0";
		}

		float scale = (float)this.settings.getFontScale();
		
		Rectangle2D bounds = getRenderer().getBounds(label);
		double h2 = bounds.getHeight() * scale / 2.0;
		
		gl.glPushMatrix();
		gl.glTranslated(x+0.1, y-h2, z);
		
		gl.glRotated(zrot, 0, 0, 1);
		gl.glRotated(yrot, 0, 1, 0);
		gl.glRotated(xrot, 1, 0, 0);
		
		getRenderer().begin3DRendering();
		getRenderer().draw3D(label, 0.0f, -(float)h2, 0.0f, (float)scale);
		getRenderer().end3DRendering();
		
		gl.glPopMatrix();
	}
}
