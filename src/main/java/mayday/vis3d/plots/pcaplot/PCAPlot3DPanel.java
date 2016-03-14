package mayday.vis3d.plots.pcaplot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;

import com.jogamp.opengl.GL2;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.JamaSubset.Matrix;
import mayday.core.settings.Setting;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.plots.pca.ExportPCAAction;
import mayday.vis3d.AbstractPlot3DPanel;
import mayday.vis3d.cs.CoordinateSystem3D;
import mayday.vis3d.cs.PlaneCoordinateSystem3D;
import mayday.vis3d.cs.StandardCoordinateSystem3D;
import mayday.vis3d.cs.settings.CoordinateSystem3DSetting;
import mayday.vis3d.primitives.Lighting;
import mayday.vis3d.primitives.Point3D;
import mayday.vis3d.utilities.Camera3D;
import mayday.vis3d.utilities.convexhull.ConvexHull;

/**
 * @author G\u00FCnter J\u00E4ger
 * @version 20100715
 */
public class PCAPlot3DPanel extends AbstractPlot3DPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6552032293613452886L;

	/**
	 * source of datagetSpread(Z.getMaximum(), Z.getMinimum()) / 2.0
	 */
	public Matrix PCAData;

	private ConvexHull hull = new ConvexHull();

	/**
	 * array of eigenvalues gained from pca calculation
	 */
	public double[] EigenValues;
	/**
	 * transpose before pca calculation?
	 */
	public boolean transpose_first = false;

	private PCAPlot3DSetting settings;
	protected ExportPCAAction exportPCA = new ExportPCAAction();

	private CoordinateSystem3D coordSystem;

	private PCAEigenValuesPlot evPlot;

	// call list identifiers
	private int dataList;
	private int sphere;
	private int sphereProjection;
	private int cross;
	private int centroid;
	private int centroidsList;

	public PCAPlot3DPanel(boolean transpose_first) {
		this.transpose_first = transpose_first;
	}

	@Override
	public void drawNotSelectable(GL2 gl) {
		CoordinateSystem3DSetting coordSetting = this.settings
				.getCoordianteSystemSetting();
		gl.glPushMatrix();
		this.coordSystem.draw(gl, glu);

		if (coordSetting.getGridSetting().getGridVisible()) {
			this.coordSystem.drawGrid(gl);
		}

		if (coordSetting.getLabelingSetting().getAxesLabeling()) {
			this.coordSystem.drawLabeling(gl, ((Camera3D)this.camera).getRotation());
		}

		if (settings.getDrawCentroids()) {
			gl.glCallList(this.centroidsList);
		}

		if (settings.drawEigenvalues()) {
			if (this.evPlot != null) {
				this.evPlot.draw(gl);
			}
		}

		if (settings.calcConvexHull()) {
			this.drawConvexHull(gl);
		}

		gl.glPopMatrix();
	}

	private void drawConvexHull(GL2 gl) {
		if (PCAData != null) {
			Matrix X = PCAData.getMatrix(0, PCAData.getRowDimension() - 1,
					settings.getPC1(), settings.getPC1());
			Matrix Y = PCAData.getMatrix(0, PCAData.getRowDimension() - 1,
					settings.getPC2(), settings.getPC2());
			Matrix Z = PCAData.getMatrix(0, PCAData.getRowDimension() - 1,
					settings.getPC3(), settings.getPC3());

			double spreadX = getSpread(getMaximum(X), getMinimum(X));
			double spreadY = getSpread(getMaximum(Y), getMinimum(Y));
			double spreadZ = getSpread(getMaximum(Z), getMinimum(Z));
			
			double width = this.coordSystem.getSetting().getVisibleArea().getWidth();
			double height = this.coordSystem.getSetting().getVisibleArea().getHeight();
			double depth = this.coordSystem.getSetting().getVisibleArea().getDepth();

			List<ProbeList> probeLists = this.viewModel.getProbeLists(true);
			Set<Probe> allProbes = viewModel.getProbes();
			
			gl.glEnable(GL2.GL_BLEND);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

			for (int i = 0; i < probeLists.size(); i++) {
				ProbeList pl = probeLists.get(i);
				Point3D[] points = new Point3D[pl.getNumberOfProbes()];
				Color c = pl.getColor();

				// add only probe values for probes contained in the probe-list
				int k = 0, j = 0;
				for (Probe pb : allProbes) {
					if (pl.contains(pb)) {
						points[k] = new Point3D(
								adjust(X.get(j, 0), width, spreadX), 
								adjust(Y.get(j, 0), height, spreadY), 
								adjust(Z.get(j, 0), depth, spreadZ));
						k++;
					}
					j++;
				}

				try {
					hull.build(points);
				} catch (IllegalArgumentException e) {
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
						for(k = 0; k < faces[j].length; k++) {
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
					for(k = 0; k < faces[j].length; k++) {
						Point3D p = pointsNeeded[faces[j][k]];
						gl.glVertex3d(p.x, p.y, p.z);
					}
				}
				gl.glEnd();
				gl.glPopMatrix();
			}
			gl.glDisable(GL2.GL_BLEND);
		}
	}

	@Override
	public void drawSelectable(GL2 gl, int glRender) {
		if (!settings.hideSpheres() || settings.getDrawProjections()) {
			if (glRender == GL2.GL_SELECT) {
				this.drawData(gl, glRender);
			} else {
				gl.glCallList(dataList);
			}
		}
	}

	private void drawData(GL2 gl, int glRender) {
		if (PCAData != null) {
			Matrix X = PCAData.getMatrix(0, PCAData.getRowDimension() - 1,
					settings.getPC1(), settings.getPC1());
			Matrix Y = PCAData.getMatrix(0, PCAData.getRowDimension() - 1,
					settings.getPC2(), settings.getPC2());
			Matrix Z = PCAData.getMatrix(0, PCAData.getRowDimension() - 1,
					settings.getPC3(), settings.getPC3());

			double spreadX = getSpread(getMaximum(X), getMinimum(X));
			double spreadY = getSpread(getMaximum(Y), getMinimum(Y));
			double spreadZ = getSpread(getMaximum(Z), getMinimum(Z));
			
			double width = this.coordSystem.getSetting().getVisibleArea().getWidth();
			double height = this.coordSystem.getSetting().getVisibleArea().getHeight();
			double depth = this.coordSystem.getSetting().getVisibleArea().getDepth();

			float[] color = { 0, 0, 0 };
			gl.glEnable(GL2.GL_LIGHTING);

			gl.glPushMatrix();
			int i = 0;
			for (Probe pb : viewModel.getProbes()) {
				if (glRender != GL2.GL_SELECT) {
					// convert color to float array
					color = convertColor(coloring.getColor(pb));
					// switch color to selection color, if probe is selected
					if (viewModel.isSelected(pb)) {
						color = convertColor(settings.getSelectionColor());
					}
					// define color for enlighten objects
					gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color,
							0);
				}

				double x = 0, y = 0, z = 0;

				if (this.transpose_first) {
					x = adjust(PCAData.get(settings.getPC1(), i), width, spreadX);
					y = adjust(PCAData.get(settings.getPC2(), i), height, spreadY);
					z = adjust(PCAData.get(settings.getPC3(), i), depth, spreadZ);
				} else {
					x = adjust(PCAData.get(i, settings.getPC1()), width, spreadX);
					y = adjust(PCAData.get(i, settings.getPC2()), height, spreadY);
					z = adjust(PCAData.get(i, settings.getPC3()), depth, spreadZ);
				}
				// draw spheres
				if (!settings.hideSpheres()) {
					if (glRender == GL2.GL_SELECT) {
						gl.glLoadName(pb.hashCode());
					}
					gl.glPushMatrix();
					gl.glTranslated(x, y, z);
					gl.glColor3fv(color, 0);
					gl.glCallList(sphere);
					gl.glPopMatrix();
				}

				if (glRender != GL2.GL_SELECT && settings.getDrawProjections()) {
					gl.glDisable(GL2.GL_LIGHTING);
					gl.glColor3fv(color, 0);
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
				i++;
			}
			gl.glPopMatrix();
			gl.glDisable(GL2.GL_LIGHTING);
		}
	}

	@Override
	public void initializeDisplay(GL2 gl) {
		Lighting.initLighting(gl);
		this.dataList = gl.glGenLists(1);
		this.sphere = gl.glGenLists(1);
		this.sphereProjection = gl.glGenLists(1);
		this.cross = gl.glGenLists(1);
		this.centroid = gl.glGenLists(1);
		this.centroidsList = gl.glGenLists(1);

		this.coordSystem.initAxesLabeling(gl);
	}

	private void updateDrawTypes(GL2 gl) {
		if(PCAData == null) {
			setBackground(Color.WHITE);
			return;
		}
		
		float r = (float) settings.getSphereRadius();
		CoordinateSystem3DSetting coordSettings = settings
				.getCoordianteSystemSetting();

		if (coordSettings.getSelectedCS().equals(PlaneCoordinateSystem3D.ID)
				&& this.coordSystem.getID().equals(StandardCoordinateSystem3D.ID)) {
			this.coordSystem = new PlaneCoordinateSystem3D(this, this.coordSystem.getSetting());
			this.coordSystem.initAxesLabeling(gl);
		} else if (coordSettings.getSelectedCS().equals(
				StandardCoordinateSystem3D.ID)
				&& this.coordSystem.getID().equals(PlaneCoordinateSystem3D.ID)) {
			this.coordSystem = new StandardCoordinateSystem3D(this, this.coordSystem.getSetting());
			this.coordSystem.initAxesLabeling(gl);
		}

		// update labeling
		Matrix X = PCAData.getMatrix(0, PCAData.getRowDimension() - 1, settings
				.getPC1(), settings.getPC1());
		Matrix Y = PCAData.getMatrix(0, PCAData.getRowDimension() - 1, settings
				.getPC2(), settings.getPC2());
		Matrix Z = PCAData.getMatrix(0, PCAData.getRowDimension() - 1, settings
				.getPC3(), settings.getPC3());

		double xSpread = getSpread(getMaximum(X), getMinimum(X)) / 2.0;
		double ySpread = getSpread(getMaximum(Y), getMinimum(Y)) / 2.0;
		double zSpread = getSpread(getMaximum(Z), getMinimum(Z)) / 2.0;

		this.coordSystem.getLabeling().setXLabels(-xSpread, xSpread);
		this.coordSystem.getLabeling().setYLabels(-ySpread, ySpread, coordSystem.getSetting().getVisibleArea().getHeight());
		this.coordSystem.getLabeling().setZLabels(-zSpread, zSpread);
		
		this.coordSystem.getLabeling().setXAxisLabel("PC " + (settings.getPC1()+1));
		this.coordSystem.getLabeling().setYAxisLabel("PC " + (settings.getPC2()+1));
		this.coordSystem.getLabeling().setZAxisLabel("PC " + (settings.getPC3()+1));

		gl.glNewList(sphere, GL2.GL_COMPILE);
		glut.glutSolidSphere(r, 15, 7);
		gl.glEndList();

		gl.glNewList(sphereProjection, GL2.GL_COMPILE);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2f(r, r);
		gl.glVertex2f(-r, -r);
		gl.glVertex2f(-r, r);
		gl.glVertex2f(r, -r);
		gl.glEnd();
		gl.glEndList();

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

		gl.glNewList(dataList, GL2.GL_COMPILE);
		this.drawData(gl, GL2.GL_RENDER);
		gl.glEndList();

		// initialize the call list for drawing a centroid
		gl.glNewList(centroid, GL2.GL_COMPILE);
		glut.glutWireSphere(1.0, 10, 5);
		gl.glEndList();
		// initialize the call list for drawing all centroids
		gl.glNewList(centroidsList, GL2.GL_COMPILE);
		this.drawCentroids(gl);
		gl.glEndList();
	}

	private void drawCentroids(GL2 gl) {
		if (PCAData != null) {
			Matrix X = PCAData.getMatrix(0, PCAData.getRowDimension() - 1,
					settings.getPC1(), settings.getPC1());
			Matrix Y = PCAData.getMatrix(0, PCAData.getRowDimension() - 1,
					settings.getPC2(), settings.getPC2());
			Matrix Z = PCAData.getMatrix(0, PCAData.getRowDimension() - 1,
					settings.getPC3(), settings.getPC3());

			double spreadX = getSpread(getMaximum(X), getMinimum(X));
			double spreadY = getSpread(getMaximum(Y), getMinimum(Y));
			double spreadZ = getSpread(getMaximum(Z), getMinimum(Z));
			
			double width = this.coordSystem.getSetting().getVisibleArea().getWidth();
			double height = this.coordSystem.getSetting().getVisibleArea().getHeight();
			double depth = this.coordSystem.getSetting().getVisibleArea().getDepth();

			List<ProbeList> probeLists = this.viewModel.getProbeLists(true);
			Set<Probe> allProbes = viewModel.getProbes();

			/*
			 * for each probe-list walk over all probes and identify the probes
			 * contained in the probe list. This seems superfluous, but it is
			 * necessary to preserve the order of the data
			 */
			for (int i = 0; i < probeLists.size(); i++) {
				ProbeList pl = probeLists.get(i);
				DoubleVector xValues = new DoubleVector(pl.getNumberOfProbes());
				DoubleVector yValues = new DoubleVector(pl.getNumberOfProbes());
				DoubleVector zValues = new DoubleVector(pl.getNumberOfProbes());
				Color c = pl.getColor();

				// add only probe values for probes contained in the probe-list
				int k = 0, j = 0;
				for (Probe pb : allProbes) {
					if (pl.contains(pb)) {
						xValues.set(k, X.get(j, 0));
						yValues.set(k, Y.get(j, 0));
						zValues.set(k, Z.get(j, 0));
						k++;
					}
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
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setupPanel(PlotContainer plotContainer) {
		plotContainer.setPreferredTitle("3D Principal Component Plot", this);
		this.coordSystem = new StandardCoordinateSystem3D(this, null);

		PCA3DComputer pcaComputer = new PCA3DComputer(this);
		pcaComputer.startCalculation();
		
		JMenu m = plotContainer.getMenu(PlotContainer.FILE_MENU, this);
		m.add(exportPCA);

		if (settings == null) {
			settings = new PCAPlot3DSetting(this);
			settings.addSetting(this.coloring.getSetting());
			settings.addCoordinateSystemSetting(this.coordSystem.getSetting());
		}

		for (Setting s : settings.getChildren()) {
			plotContainer.addViewSetting(s, this);
		}
	}

	@Override
	public void update(GL2 gl) {
		this.updateDrawTypes(gl);
		this.setBackgroundColor(settings.getBackgroundColor());
	}

	/**
	 * update the plot after pca calculation
	 * 
	 * @param pcc
	 */
	public void updateWithPCAResult(PCA3DComputer pcc) {
		if (PCAData == null) { // computation failed
			System.err.println("PCA computation failed.");
		} else {
			ArrayList<Probe> allProbes = new ArrayList<Probe>();
			for (int i = viewModel.getProbeLists(true).size(); i != 0; --i) {
				allProbes.addAll(viewModel.getProbeLists(true).get(i - 1)
						.toCollection());
			}
			exportPCA.setData(PCAData, allProbes);

			int numPCs = PCAData.getColumnDimension();
			System.out.println("Number of PCs = " + numPCs);

			// initialize the eigen-values histogram
			this.evPlot = new PCAEigenValuesPlot(this);
			this.updatePlot();
		}
	}

	/**
	 * @return availablePCS , ArrayList<String>
	 */
	public ArrayList<String> getAvailablePCs() {
		ArrayList<String> availablePCs = new ArrayList<String>();
		int numPCs = 0;

		if (this.transpose_first) {
			numPCs = viewModel.getProbes().size();
		} else {
			numPCs = viewModel.getDataSet().getMasterTable()
					.getNumberOfExperiments();
		}
		// int numPCs = PCAData.getColumnDimension();
		for (int i = 1; i <= numPCs; i++) {
			availablePCs.add(Integer.toString(i));
		}
		return availablePCs;
	}

	private double getMinimum(Matrix A) {
		double min = A.get(0, 0);
		for (int i = 0; i < A.getColumnDimension(); i++) {
			for (int j = 0; j < A.getRowDimension(); j++) {
				if (A.get(j, i) < min)
					min = A.get(j, i);
			}
		}
		return min;
	}

	private double getMaximum(Matrix A) {
		double max = A.get(0, 0);
		for (int i = 0; i < A.getColumnDimension(); i++) {
			for (int j = 0; j < A.getRowDimension(); j++) {
				if (A.get(j, i) > max)
					max = A.get(j, i);
			}
		}
		return max;
	}

	protected float getSpread(double max, double min) {
		return (float) (2 * Math.max(Math.abs(Math.ceil(max)), Math.abs(Math
				.floor(min))));
	}

	protected double adjust(double value, double dimension, double spread) {
		return (value / spread) * 2 * dimension;
	}

	@Override
	public Object[] getSelectableObjects() {
		return null;
	}

	@Override
	public void processSelectedObjects(Object[] objects, boolean controlDown, boolean altDown) {
	}

	@Override
	public double[] getInitDimension() {
		return new double[]{5.0, 5.0, 5.0};
	}
	
	/**
	 * @return coordinate system setting
	 */
	public CoordinateSystem3DSetting getCSSetting() {
		return this.coordSystem.getSetting();
	}
}
