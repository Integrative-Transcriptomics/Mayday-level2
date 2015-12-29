package mayday.vis3d;

import mayday.vis3.ColorProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3d.utilities.Camera2D;
import mayday.vis3d.utilities.SelectionHandler;
import mayday.vis3d.utilities.SelectionHandler3D;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public abstract class AbstractPlot2DPanel extends AbstractPlot3DPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4198283945548234429L;
	
	/**
	 * view model changed indicator
	 */
	public boolean viewModelChanged = false;

	/**
	 * Default constructor
	 */
	public AbstractPlot2DPanel() {
		super();
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
		this.camera = new Camera2D();
		//define object picker
		this.selectionHandler = new SelectionHandler3D(this);
		
		//add listener to canvas
		this.addListenerToCanvas();
		
		this.setupPanel(plotContainer);
	}
	
	/**
	 * @return best start iterations
	 */
	public abstract double[] getBestStartIteration();
	
	public SelectionHandler getSelectionHandler() {
		return this.selectionHandler;
	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		if(vme.getChange() == ViewModelEvent.DATA_MANIPULATION_CHANGED 
				|| vme.getChange() == ViewModelEvent.TOTAL_PROBES_CHANGED) {
			viewModelChanged = true;
		}
		
		selectionHandler.update();
		updatePlot();
	}
}
