package mayday.GWAS.visualizations.SNPExpHeatMap;

import java.awt.BorderLayout;
import java.util.List;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Subject;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.viewmodel.RevealViewModelEvent;
import mayday.GWAS.visualizations.RevealVisualization;
import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.components.PlotWithLegendAndTitle;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.plots.heatmap2.component.HeatmapOuterComponent;

@SuppressWarnings("serial")
public class SNPExpressionHeatMap extends RevealVisualization {
	
	public static final String TITLE = "SNPExp HeatMap";
	public static final String DESCRIPTION = "Create a new SNP Expression Heat Map";

	private SNPExpressionHeatMapSetting setting;
	
	private PlotWithLegendAndTitle heatMap;
	
	public SNPExpressionHeatMap(DataStorage ds) {
		setData(ds);
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			break;
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			break;
		}
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		setting = new SNPExpressionHeatMapSetting(this);
		return setting;
	}
	
	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		
		DataSet dataSet = createSNPDataSet();
		MasterTable mt = new MasterTable(dataSet);
		
		heatMap = new PlotWithLegendAndTitle();
		HeatmapOuterComponent hmc = new HeatmapOuterComponent(heatMap);
		heatMap.setPlot(hmc);
		this.add(heatMap, BorderLayout.CENTER);
		
//		hmc.getData().setData(pc);
	}

	private DataSet createSNPDataSet() {
		DataSet ds = new DataSet();
		MasterTable mt = new MasterTable(ds);
		ds.setMasterTable(mt);
		
		SNPList snps = getData().getGlobalSNPList();
		List<Subject> affected = getData().getSubjects().getAffectedSubjects();
		List<Subject> unaffected = getData().getSubjects().getUnaffectedSubjects();
		
		for(SNP s : snps) {
			int cRefA = 0;
			int cHeteroA = 0;
			int cHomoA = 0;
			
			int cRefU = 0;
			int cHeteroU = 0;
			int cHomoU = 0;
		}
		
		return null;
	}

	@Override
	public void updatePlot() {
		repaint();
	}

	@Override
	public HierarchicalSetting getViewSetting() {
		return setting;
	}
}
