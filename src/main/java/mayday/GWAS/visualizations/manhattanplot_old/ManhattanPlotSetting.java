package mayday.GWAS.visualizations.manhattanplot_old;

import mayday.core.settings.generic.HierarchicalSetting;

public class ManhattanPlotSetting extends HierarchicalSetting {

	private ManhattanPlot plot;
	
	public ManhattanPlotSetting(ManhattanPlot plot) {
		super("Manhattan Plot Setting");
		this.plot = plot;
	}
	
	public ManhattanPlotSetting clone() {
		ManhattanPlotSetting mps = new ManhattanPlotSetting(plot);
		mps.fromPrefNode(this.toPrefNode());
		return mps;
	}
}
