package mayday.Reveal.visualizations.LDPlot;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.utilities.SNVLists;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.LDPlot.tracks.CorrelationTrack;
import mayday.Reveal.visualizations.LDPlot.tracks.GenomeTrack;
import mayday.Reveal.visualizations.LDPlot.tracks.LDPlotTrack;
import mayday.Reveal.visualizations.LDPlot.tracks.SNVIdentifierTrack;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

public class LDPlot extends RevealVisualization implements SettingChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9097854935264867515L;

	private LDPlotSetting setting;
	private SNVList snps;
	
	private JScrollPane trackScroller;
	
	private List<LDPlotTrack> tracks;
	
	public LDPlot(ProjectHandler projectHandler) {
		this.snps = SNVLists.createUniqueSNVList(projectHandler.getSelectedSNVLists());
		
		Collections.sort(snps, new Comparator<SNV>() {
			@Override
			public int compare(SNV s1, SNV s2) {
				if(isInt(s1.getChromosome()) && isInt(s2.getChromosome())) {
					int chr1 = Integer.parseInt(s1.getChromosome());
					int chr2 = Integer.parseInt(s2.getChromosome());
					if(chr1 == chr2) {
						return Integer.compare(s1.getPosition(), s2.getPosition());
					} else {
						return Integer.compare(chr1, chr2);
					}
				} else {
					if(s1.getChromosome().equals(s2.getChromosome())) {
						return Integer.compare(s1.getPosition(), s2.getPosition());
					} else {
						return s1.getChromosome().compareTo(s2.getChromosome());
					}
				}
			}
			
			private boolean isInt(String chrom) {
				try {
					Integer.parseInt(chrom);
					return true;
				} catch(Exception ex) {
					return false;
				}
			}
		});
		
		this.setData(projectHandler.getSelectedProject());
		this.tracks = new ArrayList<LDPlotTrack>();
		BoxLayout l = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		this.setLayout(l);
	}
	
	public SNVList getSelectedSNPs() {
		return this.snps;
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			repaint();
			break;
		}
	}
	
	public void resize() {
		int cellWidth = this.setting.getCellWidth();
		int numSNPs = this.snps.size();
		
		for(int i = 0; i < this.tracks.size(); i++) {
			LDPlotTrack track = this.tracks.get(i);
			track.setPreferredSize(new Dimension(cellWidth * numSNPs, 100));
			
			if(track instanceof GenomeTrack) {
				track.setMaximumSize(new Dimension((int)track.getPreferredSize().getWidth(), 100));
			}
			
			if(track instanceof SNVIdentifierTrack) {
				track.setMaximumSize(new Dimension((int)track.getPreferredSize().getWidth(), 100));
			}
			
			track.revalidate();
			track.repaint();
		}
		
		this.setPreferredSize(new Dimension(cellWidth * numSNPs, getHeight()));
		
		revalidate();
		repaint();
	}

	@Override
	public void updatePlot() {
		resize();
		revalidate();
		repaint();
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		this.setting = new LDPlotSetting();
		//TODO
		this.setting.addChangeListener(this);
		
		trackScroller = (JScrollPane)getParent().getParent();
		
		GenomeTrack genomeTrack = new GenomeTrack(this);
		SNVIdentifierTrack snvIDTrack = new SNVIdentifierTrack(this);
		CorrelationTrack corTrack = new CorrelationTrack(this);
		
		this.tracks.add(genomeTrack);
		this.tracks.add(snvIDTrack);
		this.tracks.add(corTrack);
		
		this.add(genomeTrack);
		this.add(snvIDTrack);
		this.add(corTrack);
		
		this.updatePlot();
		
		trackScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		trackScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		trackScroller.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				Rectangle r = trackScroller.getVisibleRect();
				int startx = trackScroller.getHorizontalScrollBar().getValue();
				int start = Math.max((int)Math.floor(startx / setting.getCellWidth()), 0);
				int stop = (int)Math.ceil((startx + r.width) / setting.getCellWidth()) + 1;
				
				stop = Math.min(stop, snps.size());
				
				setting.setStartIndex(start);
				setting.setStopIndex(stop);	
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						LDPlot.this.repaint();
					}
				});
			}
		});
		
		return setting;
	}

	@Override
	public List<Integer> getPrerequisites() {
		List<Integer> prerequisites = new LinkedList<Integer>();
		prerequisites.add(Prerequisite.GENOME);
		prerequisites.add(Prerequisite.SNP_LIST_SELECTED);
		prerequisites.add(Prerequisite.LD_VALUES);
		return prerequisites;
	}

	@Override
	public HierarchicalSetting getViewSetting() {
		return this.setting;
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		//Object source = e.getSource();
		//TODO
		updatePlot();
	}
}
