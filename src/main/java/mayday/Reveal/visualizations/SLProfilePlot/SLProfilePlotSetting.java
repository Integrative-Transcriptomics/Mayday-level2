package mayday.Reveal.visualizations.SLProfilePlot;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.Reveal.data.Gene;
import mayday.Reveal.gui.genes.GeneSelectionDialog;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;

public class SLProfilePlotSetting extends HierarchicalSetting implements SettingChangeListener {

	private SLProfilePlot plot;
	
	private BooleanSetting showGeneSelectionDialog;
	private GeneSelectionDialog geneSelectionDialog;
	private BooleanSetting showDots;
	private ColorSetting selectionColor;
	private ColorSetting geneColor;
	
	protected SynchronizedChartSetting chartSetting;
	
	private boolean isShown= false;
	
	public SLProfilePlotSetting(SLProfilePlot plot) {
		super("Single Locus Profile Plot Setting");
		this.plot = plot;
		addSetting(showGeneSelectionDialog = new BooleanSetting("Select Genes", "Select the genes for which a Single Locus Profile Plot is shown", false));
		
		geneSelectionDialog = new GeneSelectionDialog(plot.getData());
		geneSelectionDialog.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {}

			@Override
			public void windowClosed(WindowEvent e) {
				showGeneSelectionDialog.setBooleanValue(false);
			}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowDeactivated(WindowEvent e) {}
		});
		
		geneSelectionDialog.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Set<Gene> genes = geneSelectionDialog.getSelectedGenes();
				SLProfilePlotSetting.this.plot.removeAll();
				SLProfilePlotSetting.this.plot.addPlotComponents(genes);
			}
		});
		
		showGeneSelectionDialog.addChangeListener(new SettingChangeListener() {
			@Override
			public void stateChanged(SettingChangeEvent e) {
				if(showGeneSelectionDialog.getBooleanValue() != isShown) {
					if(showGeneSelectionDialog.getBooleanValue() == true)
						geneSelectionDialog.setVisible(true);
					else {
						geneSelectionDialog.dispose();
					}
					isShown = showGeneSelectionDialog.getBooleanValue();
				}
			}
		});
		
		addSetting(showDots = new BooleanSetting("Show Dots", null, true));
		addSetting(selectionColor = new ColorSetting("Selection Color", null, Color.RED));
		addSetting(geneColor = new ColorSetting("Gene Color", null, Color.BLUE));
		
		addSetting(chartSetting = new SynchronizedChartSetting(plot));
		
		addChangeListener(this);
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		plot.updatePlot();
	}
	
	public SLProfilePlotSetting clone() {
		SLProfilePlotSetting cp = new SLProfilePlotSetting(plot);
		cp.fromPrefNode(this.toPrefNode());
		return cp;
	}

	public boolean showDots() {
		return showDots.getBooleanValue();
	}

	public Color getSelectionColor() {
		return selectionColor.getColorValue();
	}

	public SynchronizedChartSetting getSynchronizedChartSetting() {
		return this.chartSetting;
	}

	public Color getGeneColor() {
		return geneColor.getColorValue();
	}
}
