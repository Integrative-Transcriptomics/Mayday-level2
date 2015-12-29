package mayday.vis3d.plots.pcaplot;

import java.awt.Color;

import com.jogamp.opengl.GL2;

import mayday.core.structures.linalg.vector.DoubleVector;

/**
 * Histogram showing the eigenvalues of a pca calculation
 * @author G\u00FCnter J\u00E4ger
 * @version 20100715
 */
public class PCAEigenValuesPlot {
	
	PCAPlot3DPanel panel;
	
	/**
	 * @param panel
	 */
	public PCAEigenValuesPlot(PCAPlot3DPanel panel) {
		this.panel = panel;
	}
	
	/**
	 * @param gl
	 */
	public void draw(GL2 gl) {
		DoubleVector ev = new DoubleVector(panel.EigenValues);
		double spread = panel.getSpread(ev.max(), ev.min());
		
		double width = panel.getCSSetting().getVisibleArea().getWidth();
		double height = panel.getCSSetting().getVisibleArea().getHeight();
		double depth = panel.getCSSetting().getVisibleArea().getDepth();
		
		double boxWidth = width * 2.0 / ev.size();
		
		gl.glPushMatrix();
			gl.glTranslated(-width, -height, -depth);
			//draw rectangles representing eigenvalue heights
			gl.glColor3fv(panel.convertColor(Color.CYAN), 0);
			gl.glBegin(GL2.GL_QUADS);
			for(int i = 0; i < ev.size(); i++) {
				double v = panel.adjust(ev.get(i), height * 2, spread);
				gl.glVertex2d(i*boxWidth, 0);
				gl.glVertex2d((i+1)*boxWidth, 0);
				gl.glVertex2d((i+1)*boxWidth, v);
				gl.glVertex2d(i*boxWidth, v);
			}
			gl.glEnd();
			
			//draw contour lines
			gl.glColor3fv(panel.convertColor(Color.BLACK), 0);
			for(int i = 0; i < ev.size(); i++) {
				gl.glBegin(GL2.GL_LINE_LOOP);
				double v = panel.adjust(ev.get(i), height * 2, spread);
				gl.glVertex3d(i*boxWidth, 0, 0.01);
				gl.glVertex3d((i+1)*boxWidth, 0, 0.01);
				gl.glVertex3d((i+1)*boxWidth, v, 0.01);
				gl.glVertex3d(i*boxWidth, v, 0.01);
				gl.glEnd();
			}
		gl.glPopMatrix();
	}
}
