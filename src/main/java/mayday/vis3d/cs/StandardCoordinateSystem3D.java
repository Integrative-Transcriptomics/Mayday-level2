package mayday.vis3d.cs;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import mayday.vis3d.AbstractPlot3DPanel;
import mayday.vis3d.cs.settings.CoordinateSystem3DSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class StandardCoordinateSystem3D extends CoordinateSystem3D {
	
	/**
	 * identifier of this coordinate system
	 */
	public static final String ID = "Standard";
	
	/**
	 * @param panel
	 * @param settings
	 */
	public StandardCoordinateSystem3D(AbstractPlot3DPanel panel, CoordinateSystem3DSetting settings) {
		super(panel, settings);
	}
	/**
	 * @param panel
	 * @param settings
	 * @param oneCS
	 */
	public StandardCoordinateSystem3D(AbstractPlot3DPanel panel, CoordinateSystem3DSetting settings, boolean oneCS) {
		super(panel, settings, oneCS);
	}

	@Override
	public void draw(GL2 gl, GLU glu) {
		this.drawAxes(gl, glu);
	}

	@Override
	public double[] getDimension3D() {
		return settings.getVisibleArea().getDimension();
	}
	
	/**
	 * @param gl
	 * @param glu
	 */
	public void drawAxes(GL2 gl, GLU glu) {
		double width = settings.getVisibleArea().getWidth();
		double height = settings.getVisibleArea().getHeight();
		double depth = settings.getVisibleArea().getDepth();
		
		gl.glLineWidth(3.0f);
		gl.glBegin(GL2.GL_LINES);
			//x-axis
			gl.glColor3d(1, 0, 0);
			gl.glVertex3d(-width, 0, 0);
			gl.glVertex3d(width, 0, 0);
			//y-axis
			gl.glColor3d(0, 1, 0);
			gl.glVertex3d(0, -height, 0);
			gl.glVertex3d(0, height, 0);
			//z-axis
			gl.glColor3d(0, 0, 1);
			gl.glVertex3d(0, 0, depth);
			gl.glVertex3d(0, 0, -depth);
		gl.glEnd();
		
		gl.glEnable(GL2.GL_LIGHTING);
		
		GLUquadric arrow = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(arrow, GLU.GLU_FILL);
		glu.gluQuadricNormals(arrow, GLU.GLU_SMOOTH);
		
		// define color
		float[] color = new float[]{1, 0, 0};
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
		// x-axis
		gl.glPushMatrix();
		gl.glTranslated(width, 0, 0);
		gl.glRotated(90, 0, 1, 0);
		glu.gluCylinder(arrow, 0.125f, 0.0f, 0.5f, 10, 5);
		gl.glPopMatrix();
		
		color = new float[]{0, 1, 0};
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
		// y-axis
		gl.glPushMatrix();
		gl.glTranslated(0, height, 0);
		gl.glRotated(-90, 1, 0, 0);
		glu.gluCylinder(arrow, 0.125f, 0.0f, 0.5f, 10, 5);
		gl.glPopMatrix();
		
		color = new float[]{0, 0, 1};
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
		// z-axis
		gl.glPushMatrix();
		gl.glTranslated(0, 0, depth);
		glu.gluCylinder(arrow, 0.125f, 0.0f, 0.5f, 10, 5);
		gl.glPopMatrix();
		
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glLineWidth(1.1f);
	}

	@Override
	public void drawLabeling(GL2 gl, double[] rotation, double[] timepoints) {
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
			
			double width = settings.getVisibleArea().getWidth();
			double height = settings.getVisibleArea().getHeight();
			double depth = settings.getVisibleArea().getDepth();
			
			float scale = (float)this.settings.getFontScale();
			
			double k = - xit * (xlabels.length / 2.0);
			for(int i = 0; i < xlabels.length; i++, k += xit) {
				if(i == xlabels.length/2) {
					continue;
				}
				Rectangle2D bounds = getRenderer().getBounds(xlabels[i]);
				double w2 = bounds.getWidth()*scale/2.0;
				double h2 = bounds.getHeight()*scale;
				
				gl.glPushMatrix();
				gl.glTranslated(k, -h2, 0.0);
				gl.glRotated(zrot, 0, 0, 1);
				gl.glRotated(yrot, 0, 1, 0);
				gl.glRotated(xrot, 1, 0, 0);
				getRenderer().begin3DRendering();
				getRenderer().draw3D(xlabels[i], -(float)w2, -0.1f, 0.0f, scale);
				getRenderer().end3DRendering();
				gl.glPopMatrix();
			}
			
			k = -yit * (ylabels.length / 2.0);
			for(int i = 0; i < ylabels.length; i++, k += yit) {
				if(i == ylabels.length / 2) {
					continue;
				}
				Rectangle2D bounds = getRenderer().getBounds(ylabels[i]);
				double w = bounds.getWidth()*scale;
				
				gl.glPushMatrix();
				gl.glTranslated(0.0, k, 0.0);
				gl.glRotated(zrot, 0, 0, 1);
				gl.glRotated(yrot, 0, 1, 0);
				gl.glRotated(xrot, 1, 0, 0);
				getRenderer().begin3DRendering();
				getRenderer().draw3D(ylabels[i], -(float)w - 0.1f, 0.0f, 0.0f, scale);
				getRenderer().end3DRendering();
				gl.glPopMatrix();
			}
			
			k = -zit * (zlabels.length / 2.0);
			for(int i = 0; i < zlabels.length; i++, k += zit) {
				if(i == zlabels.length / 2) {
					continue;
				}
				Rectangle2D bounds = getRenderer().getBounds(zlabels[i]);
				double w2 = bounds.getWidth()*scale/2.0;
				double h2 = bounds.getHeight()*scale;
				
				gl.glPushMatrix();
				gl.glTranslated(0.0, 0.0, k);
				gl.glRotated(zrot, 0, 0, 1);
				gl.glRotated(yrot, 0, 1, 0);
				gl.glRotated(xrot, 1, 0, 0);
				getRenderer().begin3DRendering();
				getRenderer().draw3D(zlabels[i], -(float)w2, -(float)h2 - 0.1f, 0, scale);
				getRenderer().end3DRendering();
				gl.glPopMatrix();
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
			gl.glTranslated(width + w + 0.5, 0.0, 0.0);
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
			gl.glTranslated(0.0, height + h + 0.5, 0.0);
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
			gl.glTranslated(0.0, 0.0, depth + w + 0.5);
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
}
