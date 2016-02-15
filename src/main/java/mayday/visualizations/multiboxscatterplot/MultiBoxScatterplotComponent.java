package mayday.visualizations.multiboxscatterplot;

import java.awt.Component;
import java.util.LinkedList;
import java.util.List;

import mayday.core.ClassSelectionModel;
import mayday.core.Preferences;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.classes.ClassSelectionDialog;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.components.MultiPlotPanel;
import mayday.vis3.components.PlotWithLegendAndTitle;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.legend.SimpleTitle;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.multiprofile.ProfilePlotComponentMulti;
import mayday.vis3.vis2base.DataSeries;
import mayday.visualizations.PairwiseBoxPlot.PairwiseBoxScatterplotComponent;

import javax.swing.*;

@SuppressWarnings("serial")
public class MultiBoxScatterplotComponent extends MultiPlotPanel implements ViewModelListener, PlotContainer {

	/**
	 * the class selection dialog allowing the user to define multiple groups
	 */
	private ClassSelectionModel classSelection;
	/**
	 * contains all selected data points 
	 */
	protected DataSeries selectionLayer;
	protected DataSeries[] Layers;
	protected ViewModel viewModel;

	protected MultiHashMap<String, Setting> sub_settings = new MultiHashMap<String, Setting>();
	protected boolean now_adding_master_setting = false;
	protected SettingChangeListener subsetting_updater = new SettingChangeListener() {
		public void stateChanged(SettingChangeEvent e) {
			if (now_adding_master_setting)
				return;
			// first go up until we find matching parent settings 			
			Setting s = ((Setting)e.getSource());
			Preferences newValues = s.toPrefNode();

			List<Setting> targets = sub_settings.get(s.getName());
			for (Object o : e.getAdditionalSources()) {
				if (targets!=null && targets.size()>0)
					break;
				targets = sub_settings.get(((Setting)o).getName());
			}
			if (targets==null || targets.size()==0)
				return;
			// now go down the hierarchy to search for the specific setting we want to change
			for (Setting sub : targets) {
				if (sub instanceof HierarchicalSetting) {
					sub = ((HierarchicalSetting) sub).getChild(s.getName(), true);
				}
				if (sub!=null && sub.getName().equals(s.getName()))
					sub.fromPrefNode(newValues);
			}
		}
	};

	public MultiBoxScatterplotComponent() {
	}

	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		viewModel = plotContainer.getViewModel();
		viewModel.addViewModelListener(this);
		plotContainer.setPreferredTitle("Multi Box-Scatter Plot", this);
		zoomController.setAllowXOnlyZooming(true);
		zoomController.setAllowYOnlyZooming(true);
		zoomController.setActive(true);

		classSelection = new ClassSelectionModel(viewModel.getDataSet().getMasterTable());

		ClassSelectionDialog csd = new ClassSelectionDialog(classSelection);

		csd.setTitle("Select two groups of data points to visualize");
		csd.setVisible(true);

		if(csd.isCancelled()){
			// cancel. setup is called after the frame is already created
			// => close it
			JFrame win = (JFrame) SwingUtilities.getWindowAncestor(this);
			win.setVisible(false);
			win.dispose();
			return;
		}else{
			//check for user input validity
			if(classSelection.getClassesLabels().size() < 2){

				AbstractTask task = new AbstractTask("Check user input validity") {
					@Override
					protected void initialize() {
					}

					@Override
					protected void doWork() throws Exception {

						if(classSelection.getClassesLabels().size() < 2){
							throw new Exception("To perform a comparison of multiple groups, please select at least two groups.");
						}
						
					}
				};
				task.start();

			} 
		}
		updatePlot();
	}


	public void viewModelChanged(ViewModelEvent vme) {
		switch (vme.getChange()) {
		case ViewModelEvent.PROBELIST_SELECTION_CHANGED: // fallthrouh
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED:
			updatePlot();
			break;
		case ViewModelEvent.PROBE_SELECTION_CHANGED: // ignore
			break;
		}	
	}

	public void updatePlot() {
		int oldNumber = plots.length;
		LinkedList<Component> pcs = new LinkedList<Component>();
		
		for(Probe probe : viewModel.getProbeLists(false).get(0)){
			PairwiseBoxScatterplotComponent boxscatterplot = new PairwiseBoxScatterplotComponent(probe, classSelection);

			PlotWithLegendAndTitle plot = new PlotWithLegendAndTitle(boxscatterplot);
			plot.setTitle(new SimpleTitle(probe.getDisplayName(),plot));
		
			pcs.add((Component)plot);
		}

		sub_settings.clear(); // -- this should be done in a generic fashion in MultiPlotPanel

		if (oldNumber==pcs.size())
			setPlots(pcs, dimensions);
		else 
			setPlots(pcs);
	}

	public void addViewSetting(Setting s, PlotComponent askingObject) {
		if (!now_adding_master_setting)
			sub_settings.put(s.getName(), s);
		else
			s.addChangeListener(subsetting_updater);

		super.addViewSetting(s, askingObject);		
	}








}
