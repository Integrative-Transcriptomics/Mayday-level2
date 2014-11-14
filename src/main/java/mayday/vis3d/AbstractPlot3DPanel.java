package mayday.vis3d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLPbuffer;
import javax.media.opengl.glu.GLU;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.Screenshot;

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
	
	public void removeNotify() {
		System.out.println("A3D: removeNotify");
		super.removeNotify();
	}
	
	/**
	 * Constructor
	 * Sets the GLCanvas and generates GLU and GLUT
	 */
	public AbstractPlot3DPanel() {
		this.canvas = new GLCanvas(getGLCaps());
		
		HeavyWeightWorkaround.forceHeavyWeightPopups(this);
		
		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);
		
		this.glu = new GLU();
		this.glut = new GLUT();
		
		this.setPreferredSize(new Dimension(0, 0));
		this.setMinimumSize(new Dimension(0, 0));
	}
	
	@Override
	public void updatePlot() {
		this.drawTypeUpdate();
	}

	@Override
	public void paint(Graphics g) {
		if (!isShowing()) {			
			Graphics2D g2d = (Graphics2D)g;
			
			double sx = g2d.getTransform().getScaleX();
			double sy = g2d.getTransform().getScaleY();
			
			double width = getWidth() * sx;
			double height = getHeight() * sy;
			
			if(!GLDrawableFactory.getFactory().canCreateGLPbuffer())
				throw new RuntimeException("No GLPBuffer");

			GLDrawableFactory fac = GLDrawableFactory.getFactory();
			GLCapabilities glCaps = getGLCaps();
			// Without line below, there is an error on Windows.
			glCaps.setDoubleBuffered(false);
			
			//makes a new buffer
			GLPbuffer pbuffer = fac.createGLPbuffer(glCaps, null, (int)width, (int)height, null);
			
			//required for drawing to the buffer
			GLContext context =  pbuffer.createContext(null); 
			
			context.makeCurrent();
			GL gl = context.getGL();
			
			this.basicInitializations(gl);
			this.drawScene(gl, (int)width, (int)height);
						
			BufferedImage image = Screenshot.readToBufferedImage((int)width, (int)height);
			context.release();
			
			AffineTransform te = g2d.getTransform();
			g2d.scale(1/sx,1/sy);
			
			g.drawImage(image, 0, 0 , null);
			
			g2d.setTransform(te);
			
			context.destroy();
			pbuffer.destroy();
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
	public abstract void initializeDisplay(GL gl);
	/**
	 * draw all selectable objects
	 * 
	 * to initialize for selection use gl.glLoadName(identifier) before drawing the
	 * selectable object
	 * 
	 * @param gl
	 * @param glRender
	 */
	public abstract void drawSelectable(GL gl, int glRender);
	/**
	 * draw everything that will not be selected ever
	 * @param gl
	 */
	public abstract void drawNotSelectable(GL gl);
	/**
	 * perform an update operation 
	 * this method should at least call the canvas repaint function
	 * @param gl 
	 */
	public abstract void update(GL gl);
	
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
		
//		this.gljpanel.addGLEventListener(this);
//		this.gljpanel.addMouseListener(new Plot3DMouseListener());
//		this.camera.registerTo(this.gljpanel);
//		this.selectionHandler.registerTo(this.gljpanel);
	}
	
	private GLCapabilities getGLCaps() {
		GLCapabilities glCap = new GLCapabilities();
		glCap.setSampleBuffers(true);
		glCap.setHardwareAccelerated(true);
		glCap.setNumSamples(4);
		return glCap;
	}
	
	protected void basicInitializations(GL gl) {
		// enable=1/disable=0 vsync
		gl.setSwapInterval(0);
		gl.glClearDepth(10.0f); // set up depth buffer
		gl.glEnable(GL.GL_DEPTH_TEST); // enable depth test
		gl.glDepthFunc(GL.GL_LESS); // set depth func. this should be default val, anyway
		
		//set point size and line width;
		gl.glPointSize(1.1f);
		gl.glLineWidth(1.1f);
		
		initializeDisplay(gl);
	}
	
	protected void drawScene(GL gl, double width, double height) {
		gl.glClearColor(bgColor[0], bgColor[1], bgColor[2], 0.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		camera.setCamera(gl, glu, width, height);
		
		gl.glPushMatrix();
		camera.adjustCamera(gl);
		drawSelectable(gl, GL.GL_RENDER);
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
		GL gl = drawable.getGL();
		
		if(update) {
			update(gl);
			update = false;
		}
		
		drawScene(gl, getWidth(), getHeight());
		
		if(selectionHandler != null) {
			selectionHandler.drawSelectionRectangle(gl, glu, getWidth(), getHeight(), camera);
			selectionHandler.pickObjects(gl, glu, getWidth(), getHeight(), camera);
		}
		
		gl.glFlush();
	}

	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
		updatePlot();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
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
		updatePlot();
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
}
