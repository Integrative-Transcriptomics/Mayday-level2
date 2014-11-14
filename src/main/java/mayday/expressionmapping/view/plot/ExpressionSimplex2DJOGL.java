package mayday.expressionmapping.view.plot;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;

import mayday.core.Probe;
import mayday.expressionmapping.model.geometry.Point;
import mayday.expressionmapping.model.geometry.container.PointList;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3d.primitives.Point3D;
import mayday.vis3d.utilities.Camera3D;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class ExpressionSimplex2DJOGL extends ExpressionSimplexBaseJOGL {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -966681645036028240L;
	private final Point3D A = new Point3D(-1.5, -1.0, 0.0);
	private final Point3D B = new Point3D( 1.5, -1.0, 0.0);
	private final Point3D C = new Point3D( 1.5,  1.0, 0.0);
	private final Point3D D = new Point3D(-1.5,  1.0, 0.0);
	
	private double[] yCoords = new double[20];
	
	/**
	 * @param points
	 */
	public ExpressionSimplex2DJOGL(PointList<? extends Point> points) {
		super(points);
		//initialize y coordinates
		for(int i = 0; i < this.yCoords.length; ++i) {
			this.yCoords[i] = (-0.5 + i * 1.0 / this.yCoords.length);
		}
	}

	@Override
	protected void createHull(GL gl) {
		gl.glColor3fv(convertColor(Color.BLUE), 0);
		gl.glLineWidth(2.0f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3d(A.x, A.y, A.z);
		gl.glVertex3d(D.x, D.y, D.z);
		gl.glVertex3d(B.x, B.y, B.z);
		gl.glVertex3d(C.x, C.y, C.z);
		gl.glEnd();
	}

	@Override
	protected void createLabels(GL gl) {
		String[] labels = this.points.getGroupLabels();
		if(labels.length == 2) {
			String labelGroup1 = labels[0];
			String labelGroup2 = labels[1];
			float scale = 0.005f;
			double rotY = -((Camera3D)this.camera).getRotation()[1];
			
			Rectangle2D bounds = renderer.getBounds(labelGroup1);
			double w2 = bounds.getWidth()*scale/2.0;
			double h2 = bounds.getHeight()*scale;
			gl.glPushMatrix();
			gl.glTranslated(-1.5-h2, -w2, 0.0);
			gl.glRotated(rotY, 0, 1, 0);
			gl.glRotated(90, 0, 0, 1);
			renderer.begin3DRendering();
			renderer.draw3D(labelGroup1, 0.0f, 0.0f, 0.0f, scale);
			renderer.end3DRendering();
			gl.glPopMatrix();
			
			bounds = renderer.getBounds(labelGroup2);
			w2 = bounds.getWidth()*scale/2.0;
			h2 = bounds.getHeight()*scale;
			gl.glPushMatrix();
			gl.glTranslated(1.5 + h2, w2, 0.0);
			gl.glRotated(rotY, 0, 1, 0);
			gl.glRotated(-90, 0, 0, 1);
			renderer.begin3DRendering();
			renderer.draw3D(labelGroup2, 0.0f, 0.0f, 0.0f, scale);
			renderer.end3DRendering();
			gl.glPopMatrix();
		}
	}

	@Override
	protected void createPoints(GL gl, int glRender) {
		int numberOfPoints = this.points.size();
		Probe[] probes = this.viewModel.getProbes().toArray(new Probe[0]);
		
		gl.glPointSize(this.pointSize);
		
		gl.glEnable(GL.GL_BLEND);
		
		double[] pointCoords;
		
		for(int i = 0; i < numberOfPoints; ++i) {
			Point currentPoint = this.points.get(i);
			if(!currentPoint.areCoordinatesSet()) {
				throw new IllegalArgumentException("The coordinates of the point " + currentPoint.getID() + " are not set yet!");
			}
			
			pointCoords = currentPoint.getCoordinates();
			
			//parse points to probes and determine their color
			float[] color;
			if(this.viewModel.isSelected(probes[currentPoint.getID()])) {
				color = convertColor(selectionColor);
			} else {
				color = convertColor(coloring.getColor(probes[currentPoint.getID()]));
			}
			
			//why do we need blending here?!
			gl.glColor4f(color[0], color[1], color[2], 0.8f);
			
			if(glRender == GL.GL_SELECT) {
				//load the hashCode of the probe to be able to select the points
				gl.glLoadName(probes[currentPoint.getID()].hashCode());
			}
			gl.glBegin(GL.GL_POINTS);
			gl.glVertex3d(-1.5 * pointCoords[0] + 1.5 * pointCoords[1], this.yCoords[i % yCoords.length], 0.0);
			gl.glEnd();
		}
		
		gl.glDisable(GL.GL_BLEND);
	}

	@Override
	public void drawNotSelectable(GL gl) {
		gl.glPushMatrix();
		this.createHull(gl);
		this.createLabels(gl);
		gl.glPopMatrix();
	}

	@Override
	public void drawSelectable(GL gl, int glRender) {
		gl.glPushMatrix();
		this.createPoints(gl, glRender);
		gl.glPopMatrix();
	}

	@Override
	public void initializeDisplay(GL gl) {
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glClearColor(this.backgroundColor[0], this.backgroundColor[1], this.backgroundColor[2], 0.0f);
		gl.glLineWidth(2.0f);
		
		renderer.setColor(labelColor[0], labelColor[1], labelColor[2], 1.0f);
	}

	@Override
	public void setupPanel(PlotContainer plotContainer) {
		super.setupPanel(plotContainer);
	}

	@Override
	public Object[] getSelectableObjects() {
		return null;
	}

	@Override
	public void processSelectedObjects(Object[] objects, boolean controlDown, boolean altDown) {}

	@Override
	public double[] getInitDimension() {
		return new double[]{1.0, 1.0, 1.0};
	}
}
