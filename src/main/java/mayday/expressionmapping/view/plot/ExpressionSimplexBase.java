/*
 * modified by Guenter Jaeger
 * on May 22, 2010
 * 
 * this class is not needed any longer!
 */

/*
package mayday.expressionmapping.view.plot;

import mayday.expressionmapping.controller.MainFrame;
import mayday.expressionmapping.model.geometry.Point;
import mayday.expressionmapping.model.geometry.container.PointList;



import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Dimension;
import javax.swing.JPanel;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.PointArray;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;

import com.sun.j3d.utils.universe.SimpleUniverse;

//import org.apache.commons.collections.primitives.*;

import gnu.trove.*;
import java.awt.Color;
import java.util.Arrays;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.swing.JComponent;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;

/**
 * Expression Simplex models the points were plotted in.  The class provides a method for plotting the simplex itself as well as plotting the the data pints within.
 */
/*
public abstract class ExpressionSimplexBase extends JPanel{

	//The Panel/Frame the simplex is drawn in.
	protected MainFrame master;

	protected SimpleUniverse univ = null;

	protected Canvas3D c = null;

	protected BranchGroup scene = null;

	protected PointArray pointArray;

	protected PointList<? extends Point> points;

	protected int width;

	protected int height;
	
	protected Color4f pointColor = new Color4f(Color.black);
	
	protected float[] dimmPointColor = {0,0,0,0.1f};
	
	protected Color3f backgroundColor = new Color3f(Color.lightGray);
	
	protected Color3f labelColor = new Color3f(Color.blue);


	/**
	 * Constructor for our class ExpressionSimplex
	 *
	 * @param master sets the JPanel the simplex is drawn in
	 */
/*
	public ExpressionSimplexBase(MainFrame master) {

		this.master = master;

		this.points = this.master.getPoints();
		
		this.setLayout(new java.awt.BorderLayout());

	}

	
	/**
	 * For all three simplices this method is indentic
	 * It is called from the Main Window and plots the Simplex with the points
	 * @param pointCoords the Coordinates of the points we have to plot in the simplex
	 */
/*
	public void plot() {


		//create the universe the scene graph is settled in
		createUniverse();

		this.add(c, java.awt.BorderLayout.CENTER);

		// Create the content branch and add it to the universe
		scene = createScene(c);
		// scene = createSceneGraph();
		univ.addBranchGraph(scene);

	}

	private void createUniverse() {

		// Get the preferred graphics configuration for the default screen

		GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
		template.setSceneAntialiasing(template.PREFERRED);
		GraphicsConfiguration config = GraphicsEnvironment
		.getLocalGraphicsEnvironment().getDefaultScreenDevice()
		.getBestConfiguration(template);

		// GraphicsConfiguration config =
		// SimpleUniverse.getPreferredConfiguration();

		// Create a Canvas3D using the preferred configuration
		c = new Canvas3D(config);

		// Create simple universe with view branch
		univ = new SimpleUniverse(c);

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		univ.getViewingPlatform().setNominalViewingTransform();

		// //set Viewing Parameter
		View myView = univ.getViewer().getView();

		// Ensure at least 5 msec per frame (i.e., < 200Hz)
		myView.setMinimumFrameCycleTime(5);

		myView.setSceneAntialiasingEnable(true);

	}


	private BranchGroup createScene(Canvas3D c) {

		// the root Branchgroup
		BranchGroup scene = new BranchGroup();
		
		//change background color
		Background b = new Background(this.backgroundColor);
		b.setApplicationBounds(new BoundingSphere());
		scene.addChild(b);

		// the root TransformationGroup
		TransformGroup root = new TransformGroup();

		// set rights
		root.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		root.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

		// add root TransformGroup to the scene graph
		scene.addChild(root);

		// create lines, that form the simplex bounds
		createHull(root);

		// add Points to the simplex
		createPoints(root);

		// create Labels
		createLables(root);

		//set mousebehaviour
		setBehavior(scene, root);

		// compile the Branchgroup
		scene.compile();

		return scene;

	}
        
        public JComponent getComponent()  {
            
            return this;
            
        }
 
	
	public void colorPoints(TIntArrayList mainAccessList, ColorPack mainColorPack, TIntArrayList subAccessList, ColorPack subColorPack) {
		
		int numberOfPoints = this.points.size();
		
		float[] fWhite = {1f, 1f, 1f, 0.1f};
		
		//initial global color array we will use to color the
		//poinnts of the simplex
		Color4f[] colors = new Color4f[numberOfPoints];
		Arrays.fill(colors, new Color4f(this.dimmPointColor));
		
		Point currentPoint;
		
		//process first list, if the second list is null, the first list
		//corresponds to a main attractor
		int size_1 = mainAccessList.size();
		
		for (int i = 0; i < size_1; ++i) {
			
			currentPoint = this.points.get(mainAccessList.get(i));
				
			colors[mainAccessList.get(i)] = mainColorPack.getColor4f(currentPoint);
		}
		
		// process second list if unequal null
		if (subAccessList != null)  {
			
			int size_2 = subAccessList.size();
			
			for (int i = 0; i < size_2; ++i)  {
				
				currentPoint = this.points.get(subAccessList.get(i));
				
				colors[subAccessList.get(i)] = subColorPack.getColor4f(currentPoint); 
			}
			
		}
		
		// at last color points
		this.pointArray.setColors(0, colors);
			
	}

	

	public void resetPointColor() {

		//create Color Array, filled woth the Color white
		Color4f[] color = new Color4f[pointArray.getVertexCount()];
		Arrays.fill(color, this.pointColor);

		//use it to set all points to whitte
		pointArray.setColors(0, color);

	}
	protected abstract void createHull(TransformGroup trans);

	protected abstract void createPoints(TransformGroup trans);

	protected abstract void createLables(TransformGroup trans);

	protected abstract void setBehavior(BranchGroup root, TransformGroup behavior);
        
        


}
*/