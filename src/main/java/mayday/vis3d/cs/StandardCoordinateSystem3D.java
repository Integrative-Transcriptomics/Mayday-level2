package mayday.vis3d.cs;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Array;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import com.jogamp.opengl.glu.GLUtessellator;
import com.jogamp.opengl.glu.GLUtessellatorCallback;
import com.sun.scenario.effect.impl.BufferUtil;
import mayday.vis3d.AbstractPlot3DPanel;
import mayday.vis3d.cs.settings.CoordinateSystem3DSetting;
import org.apache.commons.collections15.BufferUtils;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class StandardCoordinateSystem3D extends CoordinateSystem3D implements GLUtessellatorCallback {
	
	/**
	 * identifier of this coordinate system
	 */
	public static final String ID = "Standard";

	private GLU glu;
	private GL gl;

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
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);

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
		this.glu = glu;
		this.gl = gl;
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
//				getRenderer().begin3DRendering();
//				getRenderer().draw3D(xlabels[i], -(float)w2, -0.1f, 0.0f, scale);
				vectorText(xlabels[i], gl, -(float)w2, -0.1f, 0.0f, scale);
//				getRenderer().end3DRendering();
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
//				getRenderer().begin3DRendering();
//				getRenderer().draw3D(ylabels[i], -(float)w - 0.1f, 0.0f, 0.0f, scale);
				vectorText(ylabels[i], gl, -(float)w - 0.1f, 0.0f, 0.0f, scale);
//				getRenderer().end3DRendering();
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
				//getRenderer().begin3DRendering();
				//getRenderer().draw3D(zlabels[i], -(float)w2, -(float)h2 - 0.1f, 0, scale);
				vectorText(zlabels[i], gl, -(float)w2, -(float)h2 - 0.1f, 0, scale);
				//getRenderer().end3DRendering();
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
//			getRenderer().begin3DRendering();
//			getRenderer().draw3D(xLabel, -(float)w2, -(float)h2, 0, scale);
			vectorText(xLabel, gl, -(float)w2, -(float)h2, 0, scale);
//			getRenderer().end3DRendering();
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
//			getRenderer().begin3DRendering();
//			getRenderer().draw3D(yLabel, -(float)w2, -(float)h2, 0, scale);
			vectorText(yLabel, gl, -(float)w2, -(float)h2, 0, scale);
//			getRenderer().end3DRendering();
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
//			getRenderer().begin3DRendering();
//			getRenderer().draw3D(zLabel, -(float)w2, -(float)h2, -(float)w2, scale);
			vectorText(zLabel, gl, -(float)w2, -(float)h2, -(float)w2, scale);
//			getRenderer().end3DRendering();
			gl.glPopMatrix();
			
			getRenderer().setColor(Color.BLACK);
		}
	}

	public void vectorText(String text, GL gl, float x, float y, float z, float scale) {
		GL2 gl2 = gl.getGL2();

		FontRenderContext frc = getRenderer().getFontRenderContext();
		GlyphVector gv = getRenderer().getFont().createGlyphVector(frc,text);
		Shape shp = gv.getOutline();
		AffineTransform aff = new AffineTransform();
		aff.scale(scale, scale);
		PathIterator iter = shp.getPathIterator(aff, 0.01);//0.00001);


		gl2.glTranslated(x, y, z);


		GLUtessellator tesselator = GLU.gluNewTess();
		glu.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, this);
		glu.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, this);
		glu.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, this);
		glu.gluTessCallback(tesselator, GLU.GLU_TESS_END, this);
		glu.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, this);


		switch(iter.getWindingRule()) {
			case PathIterator.WIND_EVEN_ODD:
				glu.gluTessProperty(tesselator,
						GLU.GLU_TESS_WINDING_RULE,
						GLU.GLU_TESS_WINDING_ODD);
				break;
			case PathIterator.WIND_NON_ZERO:
				glu.gluTessProperty(tesselator,
						GLU.GLU_TESS_WINDING_RULE,
						GLU.GLU_TESS_WINDING_NONZERO);
				break;
		}


		//draw (points to glu tesselator must be independent objects)
		//gl2.glNewList(gl2.glGenLists(1), GL2.GL_COMPILE);
		gl2.glShadeModel(GL2.GL_FLAT);
		gl2.glPushAttrib(GL2.GL_LINE_BIT);
		gl2.glLineWidth(15.0f);
		float[] lastpoint;// = new float[3]
		double[] start = new double[3];
		Object userData = null;
		glu.gluTessBeginPolygon(tesselator, userData);
//		System.out.println("NEXT");
		while(!iter.isDone()) {
			double[] coords = new double[6];
			double[] vertexData;
			int lineType = iter.currentSegment(coords);
			/*System.out.print(lineType + ": ");
			for(float f : coords) {
				System.out.print(f + ", ");
			}
			System.out.print("\n");*/
			gl2.glColor3d(0, 0, 0);
			switch(lineType) {
				case PathIterator.SEG_MOVETO:
					//gl2.glBegin(GL2.GL_LINE_STRIP);
					//gl2.glVertex3f(start[0], -start[1], 0.0f);
					glu.gluTessBeginContour(tesselator);
					start = coords;
					vertexData = new double[] {
							start[0], -start[1], 0.0,
							0, 0, 0
					};
					glu.gluTessVertex(tesselator, vertexData, 0, vertexData);
					break;
				case PathIterator.SEG_CLOSE:
					vertexData = new double[]  {
							start[0], -start[1], 0.0,
							0, 0, 0
					};
					//glu.gluTessVertex(tesselator, vertexData, 0, vertexData);
					//gl2.glVertex3f(start[0], -start[1], 0.0f);
					//gl2.glEnd();
					glu.gluTessEndContour(tesselator);
					break;
				case PathIterator.SEG_LINETO:
					vertexData = new double[]  {
							coords[0], -coords[1], 0.0,
							0, 0, 0
					};
					glu.gluTessVertex(tesselator, vertexData, 0, vertexData);
//					gl2.glVertex3f(coords[0], -coords[1], 0.0f);
					break;
				case PathIterator.SEG_CUBICTO:
					System.err.println("this really happens");
					vertexData = new double[]  {
							coords[0], -coords[1], 0.0,
							0, 0, 0
					};
					glu.gluTessVertex(tesselator, vertexData, 0, vertexData);
//					gl2.glVertex3f(coords[0], -coords[1], 0.0f);
//					gl2.glVertex3f(coords[2], -coords[3], 0.0f);
//					gl2.glVertex3f(coords[4], -coords[5], 0.0f);
					break;
				case PathIterator.SEG_QUADTO:
					System.err.println("this really happens");
					vertexData = new double[]  {
							coords[0], -coords[1], 0.0,
							0, 0, 0
					};
					glu.gluTessVertex(tesselator, vertexData, 0, vertexData);
//					gl2.glVertex3f(coords[0], -coords[1], 0.0f);
//					gl2.glVertex3f(coords[2], -coords[3], 0.0f);*/
					break;
			}
			iter.next();
		}
		gl2.glPopAttrib();
		glu.gluTessEndPolygon(tesselator);
		//gl2.glEndList();
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public void begin(int primitiveType) {
		gl.getGL2().glBegin(primitiveType);
	}

	@Override
	public void beginData(int primitiveType, Object userData) {
		begin(primitiveType);
	}

	@Override
	public void edgeFlag(boolean b) {
		// nothing to do here
	}

	@Override
	public void edgeFlagData(boolean b, Object o) {
		// nothing to do here
	}

	@Override
	public void vertex(Object vertexData) {
		//double[] point =  (double[]) vertexData;
		//gl.getGL2().glVertex3d(point[0], point[1], point[2]);
		double[] pointer;
		if (vertexData instanceof double[]) {
			pointer = (double[]) vertexData;
			if (pointer.length == 6)
				gl.getGL2().glColor3dv(pointer, 3);
			gl.getGL2().glVertex3dv(pointer, 0);
		}
	}

	@Override
	public void vertexData(Object vertexData, Object userData) {
		vertex(vertexData);
	}

	@Override
	public void end() {
		gl.getGL2().glEnd();
	}

	@Override
	public void endData(Object o) {
		end();
	}

	@Override
	public void combine(
			double[] coords,
			Object[] vertex_data, //some may be null
			float[] weight,
			Object[] dataOut //length 1
	) {
//		dataForSplitOrMergeVertex[0] = Arrays.copyOf(position3DVertex,
//				position3DVertex.length);
		double[] vertex = new double[6];
		vertex[0] = coords[0];
		vertex[1] = coords[1];
		vertex[2] = coords[2];
		for (int i = 3; i < 6; i++) {
			vertex[i] = 0;
			if (vertex_data[0] != null)
				vertex[i] += weight[0] * ((double[]) vertex_data[0])[i];
			if (vertex_data[1] != null)
				vertex[i] += weight[1] * ((double[]) vertex_data[1])[i];
			if (vertex_data[2] != null)
				vertex[i] += weight[2] * ((double[]) vertex_data[2])[i];
			if (vertex_data[3] != null)
				vertex[i] += weight[3] * ((double[]) vertex_data[3])[i];
		}
		dataOut[0] = vertex;
	}

	@Override
	public void combineData(
			double[] position3DVertex,
			Object[] dataOfFourNeighbourVertices, //some may be null
			float[] weightsOfFourNeighbourVertices,
			Object[] dataForSplitOrMergeVertex, //length 1
			Object userData
	) {
		combine(position3DVertex, dataOfFourNeighbourVertices,
				weightsOfFourNeighbourVertices, dataForSplitOrMergeVertex);
	}

	@Override
	public void error(int errorID) {
		System.err.println("Tesselation error: " + errorID + ", " +
				glu.gluErrorString(errorID));
	}

	@Override
	public void errorData(int errorID, Object userData) {
		error(errorID);
	}
}
