package mayday.Reveal.visualizations.SNVSummaryPlot.tracks;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JPanel;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.viewmodel.RevealViewModel;
import mayday.Reveal.visualizations.SNVSummaryPlot.SNVSummaryPlot;
import mayday.Reveal.visualizations.SNVSummaryPlot.SNVSummaryPlotSetting;

public class SNVSummaryTrack extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6958853174711584655L;
	
	protected DataStorage dataStorage;
	protected SNVList selectedSNPs;
	protected SNVSummaryPlotSetting setting;
	protected RevealViewModel viewModel;
	
	public SNVSummaryTrack(SNVSummaryPlot plot) {
		this.dataStorage = plot.getData();
		this.selectedSNPs = plot.getSelectedSNPs();
		this.setting = (SNVSummaryPlotSetting)plot.getViewSetting();
		this.viewModel = plot.getViewModel();
		this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());
	}
	
	public void setSNVSummaryTrackComponent(JComponent component) {
		this.add(component, BorderLayout.CENTER);
	}
	
	public DataStorage getDataStorage() {
		return this.dataStorage;
	}
	
	public SNVList getSelectedSNPs() {
		return this.selectedSNPs;
	}
	
	public SNVSummaryPlotSetting getSetting() {
		return this.setting;
	}

	public RevealViewModel getViewModel() {
		return this.viewModel;
	}
}
