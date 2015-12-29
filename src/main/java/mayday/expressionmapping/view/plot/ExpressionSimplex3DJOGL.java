package mayday.expressionmapping.view.plot;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import com.jogamp.opengl.GL2;

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
public class ExpressionSimplex3DJOGL extends ExpressionSimplexBaseJOGL {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8154625101641406611L;
	
	private final Point3D A = new Point3D(-0.866, -0.5, 0.0);
	private final Point3D B = new Point3D( 0.866, -0.5, 0.0);
	private final Point3D C = new Point3D(   0.0,  1.0, 0.0);

	/**
	 * @param points
	 */
	public ExpressionSimplex3DJOGL(PointList<? extends Point> points) {
		super(points);
	}

	@Override
	protected void createHull(GL2 gl) {
		gl.glColor3fv(convertColor(Color.BLUE), 0);
		gl.glLineWidth(2.0f);
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3d(A.x, A.y, A.z);
		gl.glVertex3d(B.x, B.y, B.z);
		gl.glVertex3d(C.x, C.y, C.z);
		gl.glEnd();
	}

	@Override
	protected void createLabels(GL2 gl) {
		String[] labels = this.points.getGroupLabels();
		if(labels.length == 3) {
			String labelGroup1 = labels[0];
			String labelGroup2 = labels[1];
			String labelGroup3 = labels[2];
			float scale = 0.005f;
			double rotX = -((Camera3D)this.camera).getRotation()[0];
			double rotY = -((Camera3D)this.camera).getRotation()[1];
			double rotZ = -((Camera3D)this.camera).getRotation()[2];
			
			Rectangle2D bounds = renderer.getBounds(labelGroup1);
			double w2 = bounds.getWidth()*scale/2.0;
			double h2 = bounds.getHeight()*scale;
			
			gl.glPushMatrix();
			gl.glTranslated(A.x, A.y - h2, 0.0);
			gl.glRotated(rotZ, 0, 0, 1);
			gl.glRotated(rotY, 0, 1, 0);
			gl.glRotated(rotX, 1, 0, 0);
			renderer.begin3DRendering();
			renderer.draw3D(labelGroup1,  -(float)w2, 0.0f, 0.0f, scale);
			renderer.end3DRendering();
			gl.glPopMatrix();
			
			bounds = renderer.getBounds(labelGroup2);
			w2 = bounds.getWidth()*scale/2.0;
			h2 = bounds.getHeight()*scale;
			
			gl.glPushMatrix();
			gl.glTranslated(B.x, B.y - h2, 0.0);
			gl.glRotated(rotZ, 0, 0, 1);
			gl.glRotated(rotY, 0, 1, 0);
			gl.glRotated(rotX, 1, 0, 0);
			renderer.begin3DRendering();
			renderer.draw3D(labelGroup2, -(float)w2, 0.0f, 0.0f, scale);
			renderer.end3DRendering();
			gl.glPopMatrix();
			
			bounds = renderer.getBounds(labelGroup3);
			w2 = bounds.getWidth()*scale/2.0;
			h2 = bounds.getHeight()*scale;
			
			gl.glPushMatrix();
			gl.glTranslated(C.x, C.y + h2, 0.0);
			gl.glRotated(rotZ, 0, 0, 1);
			gl.glRotated(rotY, 0, 1, 0);
			gl.glRotated(rotX, 1, 0, 0);
			renderer.begin3DRendering();
			renderer.draw3D(labelGroup3,  - (float)w2, 0.0f, 0.0f, scale);
			renderer.end3DRendering();
			gl.glPopMatrix();
		}
	}

	@Override
	protected void createPoints(GL2 gl, int glRender) {
		int numberOfPoints = this.points.size();
		Probe[] probes = this.viewModel.getProbes().toArray(new Probe[0]);
		
		gl.glPointSize(this.pointSize);
		
		gl.glEnable(GL2.GL_BLEND);
		
		double[] pointCoords;
		Point3D presentPoint = new Point3D(0.0, 0.0, 0.0);
		Point3D moveA = new Point3D(), moveB = new Point3D(), moveC = new Point3D();
		
		for(int i = 0; i < numberOfPoints; ++i) {
			Point currentPoint = this.points.get(i);
			if(!currentPoint.areCoordinatesSet()) {
				throw new IllegalArgumentException("The coordinates of the point " + currentPoint.getID() + " are not set yet!");
			}
			
			pointCoords = currentPoint.getCoordinates();
			presentPoint.set(0.0, 0.0, 0.0);
			
			moveA.set(A.scaleV(Math.abs(pointCoords[0]) - 1f/3, A));
			moveB.set(B.scaleV(Math.abs(pointCoords[1]) - 1f/3, B));
			moveC.set(C.scaleV(Math.abs(pointCoords[2]) - 1f/3, C));

			presentPoint.add(moveA);
			presentPoint.add(moveB);
			presentPoint.add(moveC);
			
			float[] color;
			if(this.viewModel.isSelected(probes[currentPoint.getID()])) {
				color = convertColor(this.selectionColor);
			} else {
				color = convertColor(this.coloring.getColor(probes[currentPoint.getID()]));
				System.out.println("Color = " + Arrays.toString(color));
			}
			
			//why do we need transparency here?!
			gl.glColor4d(color[0], color[1], color[2], 0.8);
			
			if(glRender == GL2.GL_SELECT) {
				gl.glLoadName(probes[currentPoint.getID()].hashCode());
			}
			gl.glBegin(GL2.GL_POINTS);
			gl.glVertex3d(presentPoint.x, presentPoint.y, presentPoint.z);
			gl.glEnd();
		}
		gl.glDisable(GL2.GL_BLEND);
	}

	@Override
	public void drawNotSelectable(GL2 gl) {
		gl.glPushMatrix();
		this.createHull(gl);
		this.createLabels(gl);
		gl.glPopMatrix();
	}

	@Override
	public void drawSelectable(GL2 gl, int glRender) {
		gl.glPushMatrix();
		this.createPoints(gl, glRender);
		gl.glPopMatrix();
	}

	@Override
	public void initializeDisplay(GL2 gl) {
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glClearColor(this.backgroundColor[0], this.backgroundColor[1], this.backgroundColor[2], 0.0f);
		gl.glLineWidth(2.0f);
		
		renderer.setSmoothing(true);
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
