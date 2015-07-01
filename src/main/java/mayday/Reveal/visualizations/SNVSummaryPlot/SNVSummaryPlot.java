package mayday.Reveal.visualizations.SNVSummaryPlot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.utilities.SNVLists;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.SNVSummaryPlot.tracks.SNVAggregationTrack;
import mayday.Reveal.visualizations.SNVSummaryPlot.tracks.SNVDistributionTrack;
import mayday.Reveal.visualizations.SNVSummaryPlot.tracks.SNVIdentifierTrack;
import mayday.Reveal.visualizations.SNVSummaryPlot.tracks.SNVReferenceTrack;
import mayday.Reveal.visualizations.SNVSummaryPlot.tracks.SNVSubjectTrack;
import mayday.Reveal.visualizations.SNVSummaryPlot.tracks.SNVSummaryTrack;
import mayday.Reveal.visualizations.SNVSummaryPlot.tracks.SNVSummaryTrackComponent;
import mayday.Reveal.visualizations.SNVSummaryPlot.tracks.SNVStatisticsTrackComponent;
import mayday.Reveal.visualizations.SNVSummaryPlot.tracks.TrackLabelPanel;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

public class SNVSummaryPlot extends RevealVisualization implements SettingChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6460182081659268706L;

	private SNVSummaryPlotSetting setting;
	
	private SNVList snps;
	
	private JPanel labelPanel;
	private JPanel trackPanel;
	private JScrollPane trackPanelScroller;
	
	private List<SNVSummaryTrack> tracks;
	private List<TrackLabelPanel> labels;
	private List<Boolean> trackShowing;
	
	public SNVSummaryPlot(ProjectHandler projectHandler) {
		this.setData(projectHandler.getSelectedProject());
		this.snps = SNVLists.createUniqueSNVList(projectHandler.getSelectedSNVLists());
		
		this.setLayout(new BorderLayout());
		
		this.labelPanel = new JPanel();
		labelPanel.setBackground(Color.WHITE);
		BoxLayout labelPanelLayout = new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS);
		this.labelPanel.setLayout(labelPanelLayout);
		
		this.trackPanel = new JPanel();
		trackPanel.setBackground(Color.WHITE);
		BoxLayout trackPanelLayout = new BoxLayout(trackPanel, BoxLayout.PAGE_AXIS);
		this.trackPanel.setLayout(trackPanelLayout);
		
		this.trackPanelScroller = new JScrollPane(this.trackPanel);
		this.trackPanelScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		this.trackPanelScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		this.add(labelPanel, BorderLayout.WEST);
		this.add(trackPanelScroller, BorderLayout.CENTER);
		
		this.tracks = new ArrayList<SNVSummaryTrack>();
		this.labels = new ArrayList<TrackLabelPanel>();
		this.trackShowing = new ArrayList<Boolean>();
		
		setOpaque(true);
	}
	
	private void updateTracks() {
		this.trackPanel.removeAll();
		for(int i = 0; i < tracks.size(); i++) {
			if(trackShowing.get(i))
				this.trackPanel.add(tracks.get(i));
		}
		
		this.labelPanel.removeAll();
		for(int i = 0; i < labels.size(); i++) {
			if(trackShowing.get(i))
				this.labelPanel.add(labels.get(i));
		}
		
		JPanel placeHolder = new JPanel();
		placeHolder.setBackground(Color.WHITE);
		placeHolder.setPreferredSize(new Dimension(10, (int)trackPanelScroller.getHorizontalScrollBar().getPreferredSize().getHeight()));
		placeHolder.setMaximumSize(placeHolder.getPreferredSize());
		placeHolder.setMinimumSize(placeHolder.getPreferredSize());
		placeHolder.setSize(placeHolder.getPreferredSize());
		
		this.labelPanel.add(placeHolder);
		
		updatePlot();
	}
	
	public void addSNVAggregationTrack() {
		TrackLabelPanel label = new TrackLabelPanel("Aggregation");
		SNVSummaryTrack track = new SNVSummaryTrack(this);
		SNVSummaryTrackComponent c = new SNVAggregationTrack(track);
		track.setSNVSummaryTrackComponent(c);
		addTrack(label, track);
	}
	
	public void addSNVSubjectTrack() {
		TrackLabelPanel label = new TrackLabelPanel("Subject");
		SNVSummaryTrack track = new SNVSummaryTrack(this);
		SNVSummaryTrackComponent c = new SNVSubjectTrack(track);
		track.setSNVSummaryTrackComponent(c);
		addTrack(label, track);
	}
	
	public void addSNVReferenceTrack() {
		TrackLabelPanel label = new TrackLabelPanel("Reference");
		SNVSummaryTrack track = new SNVSummaryTrack(this);
		SNVSummaryTrackComponent c = new SNVReferenceTrack(track);
		track.setSNVSummaryTrackComponent(c);
		addTrack(label, track);
	}
	
	public void addSNVDistributionTrack() {
		TrackLabelPanel label = new TrackLabelPanel("SNV Distribution");
		SNVSummaryTrack track = new SNVSummaryTrack(this);
		SNVSummaryTrackComponent c = new SNVDistributionTrack(track);
		track.setSNVSummaryTrackComponent(c);
		addTrack(label, track);
	}
	
	public void addSNVLabelTrack() {
		TrackLabelPanel label = new TrackLabelPanel("Identifier");
		SNVSummaryTrack track = new SNVSummaryTrack(this);
		SNVSummaryTrackComponent c = new SNVIdentifierTrack(track);
		track.setSNVSummaryTrackComponent(c);
		addTrack(label, track);
	}
	
	public void addSNVStatisticsTrack() {
		TrackLabelPanel label = new TrackLabelPanel("Statistics");
		SNVSummaryTrack track = new SNVSummaryTrack(this);
		SNVSummaryTrackComponent c = new SNVStatisticsTrackComponent(track);
		track.setSNVSummaryTrackComponent(c);
		addTrack(label, track);
	}
	
	public void addTrack(TrackLabelPanel label, SNVSummaryTrack track) {
		this.tracks.add(track);
		this.labels.add(label);
		this.trackShowing.add(true);
	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			repaint();
			break;
		}
	}

	@Override
	public void updatePlot() {
		resize();
		revalidate();
		repaint();
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		setting = new SNVSummaryPlotSetting();
		setting.setStartIndex(0);
		setting.setStopIndex(snps.size());
		
		setting.addChangeListener(this);
		
		//add all tracks by default
		this.addSNVLabelTrack();
		this.addSNVStatisticsTrack();
		this.addSNVDistributionTrack();
		this.addSNVAggregationTrack();
		this.addSNVReferenceTrack();
		this.addSNVSubjectTrack();
		
		this.updateTracks();
		
		trackPanelScroller.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				Rectangle r = trackPanelScroller.getVisibleRect();
				int startx = trackPanelScroller.getHorizontalScrollBar().getValue();
				int start = Math.max((int)Math.floor(startx / setting.getCellWidth()), 0);
				int stop = (int)Math.ceil((startx + r.width) / setting.getCellWidth()) + 1;
				
				stop = Math.min(stop, snps.size());
				
				setting.setStartIndex(start);
				setting.setStopIndex(stop);	
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						SNVSummaryPlot.this.repaint();
					}
				});
			}
		});
		
		updatePlot();
		
		return setting;
	}
	
	public void resize() {
		for(SNVSummaryTrack track : this.tracks) {
			track.setPreferredSize(new Dimension(setting.getCellWidth() * snps.size(), track.getHeight()));
			track.revalidate();
			track.repaint();
		}
		for(TrackLabelPanel label : this.labels) {
			label.setPreferredSize(new Dimension(25, label.getHeight()));
			label.revalidate();
			label.repaint();
		}
		revalidate();
		repaint();
	}

	@Override
	public List<Integer> getPrerequisites() {
		List<Integer> prerequisites = new LinkedList<Integer>();
		prerequisites.add(Prerequisite.SNP_LIST_SELECTED);
		return prerequisites;
	}

	@Override
	public HierarchicalSetting getViewSetting() {
		return this.setting;
	}

	public SNVList getSelectedSNPs() {
		return this.snps;
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		Object source = e.getSource();
		if(source == setting.getCellWidthSetting()) {
			updatePlot();
		} else {
			repaint();
		}
	}
}
