package mayday.vis3d.plots.scatterplot;

import java.awt.Color;
import java.util.List;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.settings.Setting;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.vis3.ValueProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3d.AbstractPlot3DPanel;
import mayday.vis3d.cs.CoordinateSystem3D;
import mayday.vis3d.cs.PlaneCoordinateSystem3D;
import mayday.vis3d.cs.StandardCoordinateSystem3D;
import mayday.vis3d.cs.settings.CoordinateSystem3DSetting;
import mayday.vis3d.primitives.Lighting;
import mayday.vis3d.primitives.Point3D;
import mayday.vis3d.utilities.Camera3D;
import mayday.vis3d.utilities.convexhull.ConvexHull;

import com.jogamp.opengl.GL2;

/**
 * @author G\u00FCnter J\u00E4ger
 * @date June 10, 2010
 */
public class Scatterplot3DPanel extends AbstractPlot3DPanel {

	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 8845893822828555323L;
	private CoordinateSystem3D coordSystem;
	private Scatterplot3DSetting settings;
	
	private ConvexHull hull = new ConvexHull();
	
	//data
	private ValueProvider X;
	private ValueProvider Y;
	private ValueProvider Z;
	
	//display list identifiers
	private int dataList;
	private int sphere;
	private int sphereProjection;
	private int cross;
	private int centroid;
	private int centroidsList;

	@Override
	public void drawNotSelectable(GL2 gl) {
		CoordinateSystem3DSetting coordSetting = this.settings.getCSSetting();
		
		gl.glPushMatrix();
			this.coordSystem.draw(gl, glu);
			
			if(coordSetting.getGridSetting().getGridVisible()) {
				this.coordSystem.drawGrid(gl);
			}
			
			
			if(coordSetting.getLabelingSetting().getAxesLabeling()) {
				this.coordSystem.drawLabeling(gl, ((Camera3D)this.camera).getRotation());
			}
			
			if(this.settings.getDrawCentroids()) {
				gl.glCallList(this.centroidsList);
			}
			
			if(this.settings.calcConvexHull()) {
				this.drawConvexHull(gl);
			}
			
		gl.glPopMatrix();
	}
	
	private void drawConvexHull(GL2 gl) {
		double spreadX = getSpread(X.getMaximum(), X.getMinimum());
		double spreadY = getSpread(Y.getMaximum(), Y.getMinimum());
		double spreadZ = getSpread(Z.getMaximum(), Z.getMinimum());
		
		List<ProbeList> probeLists = viewModel.getProbeLists(true);
		
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		
		double width = this.coordSystem.getSetting().getVisibleArea().getWidth();
		double height = this.coordSystem.getSetting().getVisibleArea().getHeight();
		double depth = this.coordSystem.getSetting().getVisibleArea().getDepth();
		
		for(int i = 0; i < probeLists.size(); i++) {
			ProbeList l = probeLists.get(i);
			Point3D[] points = new Point3D[l.getNumberOfProbes()];
			Color c = l.getColor();
			
			//create a point3d array containing all points in 3d space
			int j = 0;
			for(Probe pb : l) {
				points[j] = new Point3D(adjust(X.getValue(pb), width, spreadX), 
						adjust(Y.getValue(pb), height, spreadY), 
						adjust(Z.getValue(pb), depth, spreadZ));
				j++;
			}
			
			/*
			 * an exception is thrown, if the convex hull cannot be calculated.
			 * 
			 * catch this exception and continue to calculate the convex hull 
			 * for the other probe lists
			 */
			try {
				hull.build(points);
			} catch(IllegalArgumentException e) {
				continue;
			}
			
			hull.triangulate();
			
			int[][] faces = hull.getFaces();
			Point3D[] pointsNeeded = hull.getVertices();

			//draw triangles
			gl.glPushMatrix();
				float[] color = convertColor(c);
				gl.glColor4f(color[0], color[1], color[2], 0.5f);
				gl.glBegin(GL2.GL_TRIANGLES);
				for(j = 0; j < faces.length; j++) {
					for(int k = 0; k < faces[j].length; k++) {
						Point3D p = pointsNeeded[faces[j][k]];
						gl.glVertex3d(p.x, p.y, p.z);
					}
				}
				gl.glEnd();
			gl.glPopMatrix();
			
			//draw contour lines
			gl.glPushMatrix();
			gl.glColor3fv(convertColor(Color.BLACK), 0);
			gl.glBegin(GL2.GL_LINE_LOOP);
			for(j = 0; j < faces.length; j++) {
				for(int k = 0; k < faces[j].length; k++) {
					Point3D p = pointsNeeded[faces[j][k]];
					gl.glVertex3d(p.x, p.y, p.z);
				}
			}
			gl.glEnd();
			gl.glPopMatrix();
		}
		gl.glDisable(GL2.GL_BLEND);
	}

	private void drawCentroids(GL2 gl) {
		double spreadX = getSpread(X.getMaximum(), X.getMinimum());
		double spreadY = getSpread(Y.getMaximum(), Y.getMinimum());
		double spreadZ = getSpread(Z.getMaximum(), Z.getMinimum());
		
		List<ProbeList> probeLists = this.viewModel.getProbeLists(true);
		
		double width = this.coordSystem.getSetting().getVisibleArea().getWidth();
		double height = this.coordSystem.getSetting().getVisibleArea().getHeight();
		double depth = this.coordSystem.getSetting().getVisibleArea().getDepth();
		
		for(int i = 0; i < probeLists.size(); i++) {
			ProbeList l = probeLists.get(i);
			DoubleVector xValues = new DoubleVector(l.getNumberOfProbes());
			DoubleVector yValues = new DoubleVector(l.getNumberOfProbes());
			DoubleVector zValues = new DoubleVector(l.getNumberOfProbes());
			Color c = l.getColor();
			
			int j = 0;
			for(Probe pb : l) {
				xValues.set(j, X.getValue(pb));
				yValues.set(j, Y.getValue(pb));
				zValues.set(j, Z.getValue(pb));
				j++;
			}
			
			gl.glPushMatrix();
				gl.glColor3fv(convertColor(c), 0);
				gl.glTranslated(adjust(xValues.mean(), width, spreadX), 
						adjust(yValues.mean(), height, spreadY), 
						adjust(zValues.mean(), depth, spreadZ));
				gl.glScaled(adjust(xValues.sd(), width, spreadX), 
						adjust(yValues.sd(), height, spreadY), 
						adjust(zValues.sd(), depth, spreadZ));
				gl.glCallList(centroid);
			gl.glPopMatrix();
		}
	}

	@Override
	public void drawSelectable(GL2 gl, int glRender) {
		if(!settings.hideSpheres() || settings.getDrawProjections()) {
			if(glRender == GL2.GL_SELECT) {
				this.drawData(gl, glRender);
			} else {
				gl.glCallList(dataList);
			}
		}
	}
	/*
	 * draw all data on canvas
	 */
	private void drawData(GL2 gl, int glRender) {
		double spreadX = getSpread(X.getMaximum(), X.getMinimum());
		double spreadY = getSpread(Y.getMaximum(), Y.getMinimum());
		double spreadZ = getSpread(Z.getMaximum(), Z.getMinimum());
		float[] color = {0, 0, 0};
		
		if(glRender != GL2.GL_SELECT) {
			gl.glEnable(GL2.GL_LIGHTING);
		}
		
		double width = this.coordSystem.getSetting().getVisibleArea().getWidth();
		double height = this.coordSystem.getSetting().getVisibleArea().getHeight();
		double depth = this.coordSystem.getSetting().getVisibleArea().getDepth();
		
		gl.glPushMatrix();
		for (Probe pb : viewModel.getProbes()) {
			if(glRender != GL2.GL_SELECT) {
				// convert color to float array
				color = convertColor(coloring.getColor(pb));
				// switch color to selection color, if probe is selected
				if (viewModel.isSelected(pb)) {
					color = convertColor(settings.getSelectionColor());
				}
				// define color
				gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
			}
			
			double x = adjust(X.getValue(pb), width, spreadX);
			double y = adjust(Y.getValue(pb), height, spreadY);
			double z = adjust(Z.getValue(pb), depth, spreadZ);
			
			if(!settings.hideSpheres()) {
				if (glRender == GL2.GL_SELECT) {
					gl.glLoadName(pb.hashCode());
					gl.glPushMatrix();
					gl.glTranslated(x, y, z);
					gl.glCallList(cross);
					gl.glPopMatrix();
				} else {
					gl.glPushMatrix();
					gl.glTranslated(x, y, z);
					gl.glCallList(sphere);
					gl.glPopMatrix();
				}
			}
			
			//projections are not selectable
			if(glRender != GL2.GL_SELECT) {
				if(settings.getDrawProjections()) {
					gl.glDisable(GL2.GL_LIGHTING);
					gl.glColor3fv(color, 0);
					// project spheres as filled circles
					gl.glPushMatrix();
					gl.glTranslated(-width, y, z);
					gl.glRotated(90.0, 0, 1, 0);
					gl.glCallList(sphereProjection);
					gl.glPopMatrix();

					gl.glPushMatrix();
					gl.glTranslated(x, -height, z);
					gl.glRotated(-90, 1, 0, 0);
					gl.glCallList(sphereProjection);
					gl.glPopMatrix();

					gl.glPushMatrix();
					gl.glTranslated(x, y, -depth);
					gl.glCallList(sphereProjection);
					gl.glPopMatrix();
					gl.glEnable(GL2.GL_LIGHTING);
				}
			}
		}
		gl.glPopMatrix();
		
		if(glRender != GL2.GL_SELECT) {
			gl.glDisable(GL2.GL_LIGHTING);
		}
	}
	/*
	 * scale values to plot dimensions
	 */
	private double adjust(double value, double dimension, double spread) {
		return (value / spread) * 2 * dimension;
	}
	/*
	 * needed to scale values properly
	 */
	private float getSpread(double max, double min) {
		if(Double.isNaN(max) || Double.isNaN(min))
			return 0;
		return (float) (2 * Math.max(Math.abs(Math.ceil(max)), Math.abs(Math.floor(min))));
	}

	@Override
	public void initializeDisplay(GL2 gl) {
		Lighting.initLighting(gl);
		//create identifier for the call lists
		this.dataList = gl.glGenLists(1);
		this.sphere = gl.glGenLists(1);
		this.sphereProjection = gl.glGenLists(1);
		this.cross = gl.glGenLists(1);
		this.centroid = gl.glGenLists(1);
		this.centroidsList = gl.glGenLists(1);
		
		this.coordSystem.initAxesLabeling(gl);
		
		//fill display lists with vertex data
		this.updateDrawTypes(gl);
	}
	
	//update the call lists for object drawing
	private void updateDrawTypes(GL2 gl) {
		float r = (float)settings.getSphereRadius();
		//switch the coordinate system if necessary
		CoordinateSystem3DSetting coordSettings = this.settings.getCSSetting();
		if(coordSettings.getSelectedCS().equals(PlaneCoordinateSystem3D.ID) && this.coordSystem.getID().equals(StandardCoordinateSystem3D.ID)) {
			this.coordSystem = new PlaneCoordinateSystem3D(this, this.coordSystem.getSetting());
			this.coordSystem.initAxesLabeling(gl);
		} else if(coordSettings.getSelectedCS().equals(StandardCoordinateSystem3D.ID) && this.coordSystem.getID().equals(PlaneCoordinateSystem3D.ID)){
			this.coordSystem = new StandardCoordinateSystem3D(this, this.coordSystem.getSetting());
			this.coordSystem.initAxesLabeling(gl);
		}
		
		//update labeling
		
		double xSpread = getSpread(X.getMaximum(), X.getMinimum()) / 2.0;
		double ySpread = getSpread(Y.getMaximum(), Y.getMinimum()) / 2.0;
		double zSpread = getSpread(Z.getMaximum(), Z.getMinimum()) / 2.0;
		
		this.coordSystem.getLabeling().setXLabels(-xSpread, xSpread);
		this.coordSystem.getLabeling().setYLabels(-ySpread, ySpread, coordSystem.getSetting().getVisibleArea().getHeight());
		this.coordSystem.getLabeling().setZLabels(-zSpread, zSpread);
		
		this.coordSystem.getLabeling().setXAxisLabel(X.getSourceName());
		this.coordSystem.getLabeling().setYAxisLabel(Y.getSourceName());
		this.coordSystem.getLabeling().setZAxisLabel(Z.getSourceName());
		
		gl.glDeleteLists(sphere, 1);
		gl.glDeleteLists(sphereProjection, 1);
		gl.glDeleteLists(cross, 1);
		gl.glDeleteLists(dataList, 1);
		gl.glDeleteLists(centroid, 1);
		gl.glDeleteLists(centroidsList, 1);
		
		
		//initialize spheres representing selectable objects
		gl.glNewList(sphere, GL2.GL_COMPILE);
			glut.glutSolidSphere(r, 15, 7);
		gl.glEndList();
		//initialize projections as simple crosses
		gl.glNewList(sphereProjection, GL2.GL_COMPILE);
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex2f(r, r);
			gl.glVertex2f(-r, -r);
			gl.glVertex2f(-r, r);
			gl.glVertex2f(r, -r);
			gl.glEnd();
		gl.glEndList();
		//initialize approximation of 3d crosses for selection mode
		gl.glNewList(cross, GL2.GL_COMPILE);
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(-r, +r, 0);
			gl.glVertex3d(+r, +r, 0);
			gl.glVertex3d(+r, -r, 0);
			gl.glVertex3d(-r, -r, 0);
			
			gl.glVertex3d(-r, 0, +r);
			gl.glVertex3d(+r, 0, +r);
			gl.glVertex3d(+r, 0, -r);
			gl.glVertex3d(-r, 0, -r);
			
			gl.glVertex3d(0, +r, +r);
			gl.glVertex3d(0, -r, +r);
			gl.glVertex3d(0, -r, -r);
			gl.glVertex3d(0, +r, -r);
			gl.glEnd();
		gl.glEndList();
		
		//initialize the call list representing all data to be drawn
		gl.glNewList(dataList, GL2.GL_COMPILE);
			this.drawData(gl, GL2.GL_RENDER);
		gl.glEndList();
		//initialize the call list representing a centroid
		gl.glNewList(centroid, GL2.GL_COMPILE);
			glut.glutWireSphere(1.0, 10, 5);
		gl.glEndList();
		//initialize the call list representing all centroids
		gl.glNewList(centroidsList, GL2.GL_COMPILE);
			this.drawCentroids(gl);
		gl.glEndList();
	}

	@Override
	public void setupPanel(PlotContainer plotContainer) {
		plotContainer.setPreferredTitle("3D Scatter Plot", this);
		this.coordSystem = new PlaneCoordinateSystem3D(this, null);
		this.initValueProviders();
		//initialize the scatterplot3d setting and add all sub categories
		if(settings == null){
			settings = new Scatterplot3DSetting(this);
			settings.addSetting(coloring.getSetting());
			settings.addCoordinateSystemSetting(coordSystem.getSetting());
			settings.addSetting(X.getSetting());
			settings.addSetting(Y.getSetting());
			settings.addSetting(Z.getSetting());
		}
		//add everything to the view menu
		for (Setting s : settings.getChildren()){
			plotContainer.addViewSetting(s, this);
		}
	}
	
	//initialize the value providers
	private void initValueProviders() {
		this.X = new ValueProvider(this.viewModel, "X axis");
		this.Y = new ValueProvider(this.viewModel, "Y axis");
		this.Z = new ValueProvider(this.viewModel, "Z axis");

		if (this.viewModel.getDataSet().getMasterTable().getNumberOfExperiments() > 1)
			this.Y.setProvider(this.Y.new ExperimentProvider(1));
		if (this.viewModel.getDataSet().getMasterTable().getNumberOfExperiments() > 2)
			this.Z.setProvider(this.Z.new ExperimentProvider(2));
	}

	@Override
	public void update(GL2 gl) {
		//update the call lists
		this.updateDrawTypes(gl);
		//change background color
		this.setBackgroundColor(settings.getBackgroundColor());
	}

	@Override
	public Object[] getSelectableObjects() {
		return null;
	}

	@Override
	public void processSelectedObjects(Object[] objects, boolean controlDown, boolean altDown) {}

	@Override
	public double[] getInitDimension() {
		return new double[]{5.0, 5.0, 5.0};
	}
	/**
	 * @return settings
	 */
	public Scatterplot3DSetting getSetting() {
		return this.settings;
	}
}
