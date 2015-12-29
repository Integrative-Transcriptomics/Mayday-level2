package mayday.Reveal.visualizations;

import java.util.List;

import javax.swing.BoxLayout;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.viewmodel.RevealViewModel;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.components.BasicPlotPanel;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelListener;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public abstract class RevealVisualization extends BasicPlotPanel implements ViewModelListener {
	
	protected RevealViewModel viewModel;
	protected DataStorage data;
	
	/**
	 * @param title
	 * @param menu 
	 */
	public RevealVisualization() {
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
	}
	
	/**
	 * @param data
	 */
	public void setData(DataStorage data) {
		this.data = data;
	}
	
	/**
	 * @return data storage
	 */
	public DataStorage getData() {
		return this.data;
	}
	
	@Override
	public void setup(PlotContainer plotContainer) {
		if(viewModel == null) {
			viewModel = (RevealViewModel)plotContainer.getViewModel();
		}
		viewModel.addViewModelListener(this);
		
		HierarchicalSetting s = setupPrerequisites(plotContainer);
		
		for(Setting child : s.getChildren()) {
			plotContainer.addViewSetting(child, this);
		}
	}
	
	/**
	 * setup the settings for each plot
	 * @return hierarchical setting
	 */
	public abstract HierarchicalSetting setupPrerequisites(PlotContainer plotContainer);
	
	public abstract List<Integer> getPrerequisites();
	
	@Override
	public void removeNotify() {
		if(viewModel != null)
			viewModel.removeViewModelListener(this);
		super.removeNotify();
	}
	
	/**
	 * @return the view model
	 */
	public RevealViewModel getViewModel() {
		return this.viewModel;
	}

	public void setViewModel(RevealViewModel viewModel) {
		this.viewModel = viewModel;
	}
	
	public boolean isExporting() {
		return !isShowing();
	}
	
	public abstract HierarchicalSetting getViewSetting();
}
