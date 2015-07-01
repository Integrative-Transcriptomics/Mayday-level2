package mayday.Reveal.visualizations.LDPlot.tracks;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.viewmodel.RevealViewModel;
import mayday.Reveal.visualizations.LDPlot.LDPlot;
import mayday.Reveal.visualizations.LDPlot.LDPlotSetting;
import mayday.core.MaydayDefaults;

public abstract class LDPlotTrack extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 106811146524075236L;
	
	private DataStorage dataStorage;
	private LDPlotSetting setting;
	private RevealViewModel viewModel;
	private SNVList snps;
	
	public LDPlotTrack(LDPlot plot) {
		this.dataStorage = plot.getData();
		this.setting = (LDPlotSetting)plot.getViewSetting();
		this.viewModel = plot.getViewModel();
		this.snps = plot.getSelectedSNPs();
		
		this.setBackground(Color.WHITE);
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, getWidth(), getHeight());
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g2.setFont(MaydayDefaults.DEFAULT_PLOT_FONT);
		
		super.paint(g2);
	}
	
	public DataStorage getDataStorage() {
		return this.dataStorage;
	}
	
	public LDPlotSetting getSetting() {
		return this.setting;
	}
	
	public RevealViewModel getViewModel() {
		return this.viewModel;
	}
	
	public SNVList getSelectedSNPs() {
		return this.snps;
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g);
		this.doPaint(g2);
	}
	
	public abstract void doPaint(Graphics2D g2);
}
