package mayday.vis3d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jogamp.nativewindow.NativeSurface;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.vis3.ColorProvider;
import mayday.vis3.components.BasicPlotPanel;
import mayday.vis3.components.HeavyWeightWorkaround;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3d.primitives.Point3D;
import mayday.vis3d.utilities.Camera;
import mayday.vis3d.utilities.Camera3D;
import mayday.vis3d.utilities.SelectionHandler;
import mayday.vis3d.utilities.SelectionHandler3D;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * @author G\u00FCnter J\u00E4ger
 * @version 20100729
 *
 */
public abstract class AbstractPlot3DPanel extends BasicPlotPanel implements GLEventListener, ViewModelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2281441208785723645L;
	
	/**
	 * GLU object
	 */
	public GLU glu;
	protected GLUT glut;
	
	protected float[] bgColor = convertColor(Color.WHITE);
	
	/**
	 * GLCanvas on which things get drawn
	 */
	public GLCanvas canvas;
	/**
	 * Camera for movement
	 */
	public Camera camera;
	/**
	 * Selection Handler for selectable object handling
	 */
	public SelectionHandler selectionHandler;
	
	/**
	 * the view model
	 */
	public ViewModel viewModel;
	/**
	 * the color provider
	 */
	public volatile ColorProvider coloring;
	
	private boolean update = true;
	
	private GLProfile glp;
	
	public void removeNotify() {
		//System.out.println("A3D: removeNotify");
		super.removeNotify();
	}
	
	/**
	 * Constructor
	 * Sets the GLCanvas and generates GLU and GLUT
	 */
	public AbstractPlot3DPanel() {
		// dont allow jogl to cover the menu
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		this.canvas = new GLCanvas(getGLCaps());
		
		HeavyWeightWorkaround.forceHeavyWeightPopups(this);
		
		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);
		
		this.glu = new GLU();
		this.glut = new GLUT();
		
		this.glp = GLProfile.getDefault();
		
		this.setPreferredSize(new Dimension(0, 0));
		this.setMinimumSize(new Dimension(0, 0));
	}
	
	@Override
	public void updatePlot() {
		this.drawTypeUpdate();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		//this is used to draw the plot for export
		if (!isShowing()) {
			Graphics2D g2 = (Graphics2D)g;
			canvas.getContext().makeCurrent();
			GL2 gl = canvas.getGL().getGL2();
			
			int width = canvas.getWidth();
			int height = canvas.getHeight();
			
			this.drawScene(gl, width, height);
			
			ByteBuffer pixelsRGB = Buffers.newDirectByteBuffer(width * height * 3);

		    gl.glReadBuffer(GL2.GL_BACK);
		    gl.glPixelStorei(GL2.GL_PACK_ALIGNMENT, 1);

		    gl.glReadPixels(0, 0, width, height, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, pixelsRGB); 

		    int[] pixelInts = new int[width * height];

		    // Convert RGB bytes to ARGB ints with no transparency. Flip image vertically by reading the
		    // rows of pixels in the byte buffer in reverse - (0,0) is at bottom left in OpenGL.

		    int p = width * height * 3;	// Points to first byte (red) in each row.
		    int q;						// Index into ByteBuffer
		    int i = 0;					// Index into target int[]
		    int w3 = width * 3;			// Number of bytes in each row

		    for (int row = 0; row < height; row++) {
		    	p -= w3;
		        q = p;
		        for (int col = 0; col < width; col++) {
		        	int iR = pixelsRGB.get(q++);
		            int iG = pixelsRGB.get(q++);
		            int iB = pixelsRGB.get(q++);

		            pixelInts[i++] = 0xFF000000
		            		| ((iR & 0x000000FF) << 16)
		                    | ((iG & 0x000000FF) << 8)
		                    | (iB & 0x000000FF);
		        }
		    }

		    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		    bufferedImage.setRGB(0, 0, width, height, pixelInts, 0, width);

		    g2.drawImage(bufferedImage, 0, 0, null);
		}
	}

	@Override
	public void setup(PlotContainer plotContainer) {
		this.viewModel = plotContainer.getViewModel();
		
		this.viewModel.addViewModelListener(this);
		this.viewModel.addRefreshingListenerToAllProbeLists(new Plot3DProbeListListener(), true);

		if (this.coloring == null) { // don't loose old CP
			this.coloring = new ColorProvider(viewModel);
		} else {
			this.coloring.addNotify();
		}

		coloring.addChangeListener(new ColorChangeListener());
		//this.coloring.addChangeListener(new ColorChangeListener());
		plotContainer.addViewSetting(this.coloring.getSetting(), this);
		
		//define camera
		this.camera = new Camera3D(new Point3D(0.0,0.0,15.0), 
				new Point3D(0.0,0.0,0.0));
		//define object picker
		this.selectionHandler = new SelectionHandler3D(this);
		
		//add listener to canvas
		this.addListenerToCanvas();
		this.setupPanel(plotContainer);
	}
	
	/**
	 * @param c
	 */
	public void setBackgroundColor(Color c) {
		this.bgColor = this.convertColor(c);
	}
	
	/**
	 * @param c
	 * @return float[] representing c (values range: [0,1])
	 */
	public float[] convertColor(Color c) {
		if(c == null) {
			c = Color.BLACK;
		}
		return new float[] { c.getRed() / 255.0f, c.getGreen() / 255.0f,
				c.getBlue() / 255.0f };
	}
	
	/**
	 * @return canvas
	 */
	public GLAutoDrawable getCanvas() {
		return this.canvas;
	}
	
	/**
	 * Set the SelectionHandler for the plot
	 * @param selectionHandler
	 */
	public void setSelectionHandler(SelectionHandler3D selectionHandler) {
		this.selectionHandler = selectionHandler;
	}
	
	/**
	 * this method processes all selected objects that are not probes!
	 * @param objects 
	 * @param altDown 
	 * @param controlDown 
	 */
	public void processSelectedObjects(Object[] objects, boolean controlDown, boolean altDown){};
	
	/**
	 * @return array of selectable objects, 
	 * which are selectable with the selection handler
	 */
	/*
	 * this method is needed to provide fast 
	 * object identification in the selection handler
	 */
	public Object[] getSelectableObjects(){return null;};
	
	/**
	 * allows for additional setup operations
	 * @param plotContainer
	 */
	public abstract void setupPanel(PlotContainer plotContainer);
	/**
	 * allows for additional display initialization steps
	 * @param gl
	 */
	public abstract void initializeDisplay(GL2 gl);
	/**
	 * draw all selectable objects
	 * 
	 * to initialize for selection use gl.glLoadName(identifier) before drawing the
	 * selectable object
	 * 
	 * @param gl
	 * @param glRender
	 */
	public abstract void drawSelectable(GL2 gl, int glRender);
	/**
	 * draw everything that will not be selected ever
	 * @param gl
	 */
	public abstract void drawNotSelectable(GL2 gl);
	/**
	 * perform an update operation 
	 * this method should at least call the canvas repaint function
	 * @param gl 
	 */
	public abstract void update(GL2 gl);
	
	/**
	 * 
	 */
	public void drawTypeUpdate() {
		this.update = true;
		canvas.repaint();
	}
	
	/**
	 * @return dimension for initialization of the plot
	 */
	public abstract double[] getInitDimension();
	
	protected void addListenerToCanvas() {
		this.canvas.addGLEventListener(this);
		this.canvas.addMouseListener(new Plot3DMouseListener());
		this.camera.registerTo(this.canvas);
		this.selectionHandler.registerTo(this.canvas);
	}
	
	private GLCapabilities getGLCaps() {
		GLCapabilities glCap = new GLCapabilities(glp);
		glCap.setSampleBuffers(true);
		glCap.setHardwareAccelerated(true);
		glCap.setNumSamples(4);
		return glCap;
	}
	
	protected void basicInitializations(GL2 gl) {
		// enable=1/disable=0 vsync
		gl.setSwapInterval(0);
		gl.glClearDepth(10.0f); // set up depth buffer
		gl.glEnable(GL2.GL_DEPTH_TEST); // enable depth test
		gl.glDepthFunc(GL2.GL_LESS); // set depth func. this should be default val, anyway
		
		//set point size and line width;
		gl.glPointSize(1.1f);
		gl.glLineWidth(1.1f);
		
		initializeDisplay(gl);
	}
	
	protected void drawScene(GL2 gl, double width, double height) {
		gl.glClearColor(bgColor[0], bgColor[1], bgColor[2], 0.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		camera.setCamera(gl, glu, width, height);

		gl.glPushMatrix();
		camera.adjustCamera(gl);
		drawSelectable(gl, GL2.GL_RENDER);
		drawNotSelectable(gl);
		gl.glPopMatrix();
	}

	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		if(selectionHandler != null) {
			selectionHandler.update();
		}
		updatePlot();
	}
	
	/**
	 * @author G\u00FCnter J\u00E4ger
	 *
	 */
	public class Plot3DMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			switch(e.getButton()){
			case MouseEvent.BUTTON3:
				if(viewModel != null) {
					ProbeMenu pm = new ProbeMenu(viewModel.getSelectedProbes(), viewModel.getDataSet().getMasterTable());
					pm.getPopupMenu().show(AbstractPlot3DPanel.this, e.getX(), e.getY());
				}
				break;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		if(update) {
			update(gl);
			update = false;
		}

		int height = canvas.getHeight();
		int width = canvas.getWidth();

		
		drawScene(gl, width, height);
		
		if(selectionHandler != null) {
			selectionHandler.drawSelectionRectangle(gl, glu, width, height, camera);
			selectionHandler.pickObjects(gl, glu, width, height, camera);
		}
		
		gl.glFlush();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		updatePlot();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		//enable depth test
		basicInitializations(gl);
		//initialize the selection handler
		if(selectionHandler != null) {
			selectionHandler.update();
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		drawable.getGL().glViewport(x, y, width, height);
		// reshape is just a notification, display() is called afterwards by the framework
		//updatePlot();
	}
	
	protected class ColorChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			updatePlot();
		}
	}
	
	protected class Plot3DProbeListListener implements ProbeListListener {
		@Override
		public void probeListChanged(ProbeListEvent event) {
			if(selectionHandler != null) {
				selectionHandler.update();
			}
			updatePlot();
		}
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) {
		//TODO
	}
}
