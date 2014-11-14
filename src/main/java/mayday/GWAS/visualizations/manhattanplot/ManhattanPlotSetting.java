package mayday.GWAS.visualizations.manhattanplot;

import java.awt.Color;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.ColorSetting;

public class ManhattanPlotSetting extends HierarchicalSetting implements ChangeListener {

	private ManhattanPlot plot;
	
	private ColorSetting selectionColor;
	
	public ManhattanPlotSetting(ManhattanPlot plot) {
		super("Manhattan Plot Setting");
		this.plot = plot;
		
		addSetting(selectionColor = new ColorSetting("Selection Color", null, Color.RED));
	}
	
	public ManhattanPlotSetting clone() {
		ManhattanPlotSetting mps = new ManhattanPlotSetting(this.plot);
		mps.fromPrefNode(this.toPrefNode());
		return mps;
	}

	public Color getSelectionColor() {
		return selectionColor.getColorValue();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		plot.updatePlot();
	}
}
