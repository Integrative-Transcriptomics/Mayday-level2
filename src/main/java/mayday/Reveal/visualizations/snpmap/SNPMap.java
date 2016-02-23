package mayday.Reveal.visualizations.snpmap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import mayday.Reveal.data.Haplotypes;
import mayday.Reveal.data.HaplotypesList;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.utilities.MultiArraySorter;
import mayday.Reveal.utilities.SNVLists;
import mayday.Reveal.utilities.SplitPaneSynchronizer;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.Reveal.visualizations.snpmap.aggregation.Aggregation;
import mayday.Reveal.visualizations.snpmap.aggregation.AggregationComponent;
import mayday.Reveal.visualizations.snpmap.aggregation.AggregationComponentHeader;
import mayday.Reveal.visualizations.snpmap.aggregation.AggregationMetaComponent;
import mayday.Reveal.visualizations.snpmap.aggregation.Aggregator;
import mayday.Reveal.visualizations.snpmap.meta.SNPMapMetaComponent;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.structures.maps.BidirectionalHashMap;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class SNPMap extends RevealVisualization {
	
	public SNPMapSetting setting;
	public SNVList snps;
	public SubjectList persons;
	public BidirectionalHashMap<Integer, Integer> personIndices;
	
	private SNPMapComponent snpMapComponent;
	private SNPMapColumnHeader columnHeader;
	private SNPMapRowHeader rowHeader;
	private PlaceHolder placeHolderLeft;
	private JPanel colPanel;
	
	public JScrollPane compScroller;
	public JScrollPane rowScroller;
	public JScrollPane colScroller;
	
	private PlaceHolder placeHolderRight;
	
	public SNPMapMetaComponent metaComp;
	
	private JSplitPane splitterH;
	private JSplitPane splitterV;
	private JSplitPane splitterH2;
	
	private JPanel mainPanel;
	private JPanel agMainPanel;
	private JPanel agCompMain;
	
	public AggregationComponent agComp;
	public AggregationComponentHeader agCompHeader;
	public AggregationMetaComponent agMetaComp;
	
	public List<Aggregation> hapAggregations;
	
	public Aggregator aggregator;
	
	/**
	 * @param projectHandler
	 */
	public SNPMap(ProjectHandler projectHandler) {
		this.setLayout(new BorderLayout());
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		setData(projectHandler.getSelectedProject());
		snps = SNVLists.createUniqueSNVList(projectHandler.getSelectedSNVLists());
		persons = getData().getSubjects().cloneProperly();
		personIndices = new BidirectionalHashMap<Integer, Integer>();
		aggregator = new Aggregator();
		
		for(int i = 0; i < persons.size(); i++)
			personIndices.put(i, i);
		
		hapAggregations = new ArrayList<Aggregation>();
		
		snpMapComponent = new SNPMapComponent(this);
		compScroller = new JScrollPane(snpMapComponent);
		mainPanel.add(compScroller, BorderLayout.CENTER);
		
		colPanel = new JPanel(new BorderLayout());
		colPanel.add(placeHolderLeft = new PlaceHolder(), BorderLayout.WEST);
		columnHeader = new SNPMapColumnHeader(this);
		colScroller = new JScrollPane(columnHeader);
		colPanel.add(colScroller, BorderLayout.CENTER);
		colPanel.add(placeHolderRight = new PlaceHolder(), BorderLayout.EAST);
		mainPanel.add(colPanel, BorderLayout.NORTH);
		
		rowHeader = new SNPMapRowHeader(this);
		rowScroller = new JScrollPane(rowHeader);
		mainPanel.add(rowScroller, BorderLayout.WEST);
		
		JScrollBar hSBcomp = compScroller.getHorizontalScrollBar();
		JScrollBar hSBcol = colScroller.getHorizontalScrollBar();
		hSBcol.setModel(hSBcomp.getModel()); // synchronize scrollbars
		//hide scrollbars
		colScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		colScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		
		JScrollBar vSBcomp = compScroller.getVerticalScrollBar();
		JScrollBar vSBrow = rowScroller.getVerticalScrollBar();
		vSBrow.setModel(vSBcomp.getModel()); // synchronize scrollbars
		//hide scrollbars
		rowScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		rowScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		rowScroller.setBorder(null);
		colScroller.setBorder(null);
		
		compScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		compScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		metaComp = new SNPMapMetaComponent(this);
		
		splitterH = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainPanel, metaComp);
		splitterH.setOneTouchExpandable(true);
		
		Dimension dim = new Dimension(0,0);
		mainPanel.setMinimumSize(dim);
		metaComp.setMinimumSize(dim);
		
		agMainPanel = new JPanel(new BorderLayout());
		
		agCompMain = new JPanel(new BorderLayout());
		agComp = new AggregationComponent(this);
		
		JScrollPane agCompScroller = new JScrollPane(agComp);
		agCompScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		agCompScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		agCompScroller.setBorder(null);
		
		JScrollBar hSBagComp = agCompScroller.getHorizontalScrollBar();
		hSBagComp.setModel(hSBcomp.getModel());
		
		agCompHeader = new AggregationComponentHeader(this);
		
		JScrollPane agCompHeaderScroller = new JScrollPane(agCompHeader);
		agCompHeaderScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		agCompHeaderScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		agCompHeaderScroller.setBorder(null);
		
		JScrollBar vSBagCompHeader = agCompHeaderScroller.getVerticalScrollBar();
		JScrollBar vSBagComp = agCompScroller.getVerticalScrollBar();
		vSBagCompHeader.setModel(vSBagComp.getModel());
		
		agCompMain.add(agCompScroller, BorderLayout.CENTER);
		agCompMain.add(agCompHeaderScroller, BorderLayout.WEST);
		
		agMetaComp = new AggregationMetaComponent(this);
		
		JScrollPane agMetaCompScroller = new JScrollPane(agMetaComp);
		agMetaCompScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		agMetaCompScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		agMetaCompScroller.setBorder(null);
		
		JScrollBar vSBagMetaComp = agMetaCompScroller.getVerticalScrollBar();
		JScrollBar hSBagMetaComp = agMetaCompScroller.getHorizontalScrollBar();
		
		hSBagMetaComp.setModel(metaComp.headerScroller.getHorizontalScrollBar().getModel());
		vSBagMetaComp.setModel(vSBagComp.getModel());
		
		splitterH2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, agCompMain, agMetaCompScroller);
		agMainPanel.add(splitterH2, BorderLayout.CENTER);
		
		splitterV = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitterH, agMainPanel);
		
		new SplitPaneSynchronizer(splitterH, splitterH2);
		
		this.add(splitterV, BorderLayout.CENTER);
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
	
	private boolean initial = true;
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setBackground(Color.white);
		g2.clearRect(0,0, getWidth(), getHeight());
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paint(g2);
		
		if(initial) {
			resize();
			initial = false;
		}
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		setting = new SNPMapSetting(this);
		resize();
		
		getViewModel().addViewModelListener(rowHeader);
		getViewModel().addViewModelListener(columnHeader);
		getViewModel().addViewModelListener(snpMapComponent);
		getViewModel().addViewModelListener(metaComp);
		getViewModel().addViewModelListener(metaComp.colHeader);
		getViewModel().addViewModelListener(metaComp.dataComp);
		
		HierarchicalSetting s = metaComp.getSetting();
		plotContainer.addViewSetting(s, this);
		plotContainer.addViewSetting(aggregator.setting, this);
		
		return setting;
	}
	
	public void resize() {
		int compWidth = setting.getCellWidth() * snps.size();
		int compHeight = setting.getCellWidth() * getData().getSubjects().size();
		
		Graphics g = this.getGraphics();
		int rowWidth = getRowHeaderSize(g) + 5;
		int colHeight = getColumnHeaderSize(g) + 5;
		
		int scrollBarSize = (int)compScroller.getVerticalScrollBar().getPreferredSize().getWidth();
		
		snpMapComponent.setPreferredSize(new Dimension(compWidth, compHeight));
		columnHeader.setPreferredSize(new Dimension(compWidth, colHeight));
		rowHeader.setPreferredSize(new Dimension(rowWidth, compHeight));
		placeHolderLeft.setPreferredSize(new Dimension(rowWidth, colHeight));
		placeHolderRight.setPreferredSize(new Dimension(scrollBarSize, colHeight));
		
		agComp.setPreferredSize(new Dimension(compWidth, compHeight));
		agCompHeader.setPreferredSize(new Dimension(rowWidth, compHeight));
		
		agComp.revalidate();
		agCompHeader.revalidate();
		
		agCompMain.revalidate();
		agCompMain.repaint();
		
		mainPanel.revalidate();
		mainPanel.repaint();
		
		metaComp.resizeComp(colHeight, compHeight);
		agMetaComp.setPreferredSize(metaComp.dataComp.getPreferredSize());
		
		agMetaComp.revalidate();
		agMetaComp.repaint();
		
		splitterH.revalidate();
		splitterH.setDividerLocation(0.8);
		splitterH.repaint();
		
		splitterV.revalidate();
		splitterV.setDividerLocation(0.95);
		splitterV.repaint();
		
		revalidate();
		repaint();
	}

	private Integer rowWidth, colHeight;

	private int getColumnHeaderSize(Graphics g) {
		if(colHeight != null)
			return colHeight;
		
		colHeight = 0;
		
		for(int i = 0; i < snps.size(); i++) {
			String id = snps.get(i).getID();
			Rectangle2D bounds = g.getFontMetrics().getStringBounds(id, g);
			if(bounds.getWidth() > colHeight)
				colHeight = (int)Math.rint(bounds.getWidth());
		}
		
		return colHeight;
	}
	
	private int getRowHeaderSize(Graphics g) {
		if(rowWidth != null)
			return rowWidth.intValue();
		
		SubjectList persons = getData().getSubjects();
		rowWidth = 0;
		
		for(int i = 0; i < persons.size(); i++) {
			Subject p = persons.get(i);
			String id = p.getID();
			
			Rectangle2D bounds = g.getFontMetrics().getStringBounds(id, g);
			if(bounds.getWidth() > rowWidth)
				rowWidth = (int)Math.rint(bounds.getWidth());
		}
		
		return rowWidth;
	}

	@Override
	public void updatePlot() {
		repaint();
	}
	
	public void sort(DoubleVector template) {
		Integer[] indices = MultiArraySorter.sort(template, false);
		//sort the person indices
		personIndices = MultiArraySorter.indicesToMap(indices, personIndices);
		metaComp.sort(indices);
		updatePlot();
	}
	
	public void aggregate() {
		Set<Subject> subjects = getViewModel().getSelectedPersons();
		if(subjects != null && subjects.size() > 0) {
			this.aggregate(subjects);
			this.metaComp.aggregate(subjects);
			repaint();
		}
	}
	
	public void aggregate(Set<Subject> subjects) {
		int numSNPs = snps.size();
		Aggregation ag = new Aggregation(numSNPs);
		
		HaplotypesList haplotypes = getData().getHaplotypes();
		
		for(int i = 0; i < numSNPs; i++) {
			DoubleVector values = new DoubleVector(subjects.size());
			SNV snp = snps.get(i);
			
			int j = 0;
			for(Subject s : subjects) {
				Haplotypes h = haplotypes.get(s.getIndex());
				double v = 0;
					
				char a = h.getSNPA(snp.getIndex());
				char b = h.getSNPB(snp.getIndex());
				char r = snp.getReferenceNucleotide();
				
				if(a == r && b == r) {
					v = 0;
				} else if(a == r && b != r || a != r && b == r) {
					v = 1;
				} else {
					v = 2;
				}
				
				values.set(j++, v);
			}
			aggregator.aggregate(values);
			ag.setAggregationValue(i, aggregator.getAggregationValue());
			ag.setFrequency(i, aggregator.getFrequency());
		}
		
		ag.setName("# " + (this.hapAggregations.size()+1));
		ag.setSubjects(subjects);
		
		this.hapAggregations.add(ag);
	}
	
	public void removeAggregations(Set<Aggregation> aggregations) {
		this.hapAggregations.removeAll(aggregations);
		this.metaComp.removeAggregations(aggregations);
		repaint();
	}
	
	private class PlaceHolder extends JComponent {
		
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setBackground(Color.white);
			g2.clearRect(0,0, getWidth(), getHeight());
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			super.paint(g2);
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setBackground(Color.WHITE);
			g2.clearRect(0, 0, getWidth(), getHeight());
		}
	}

	@Override
	public HierarchicalSetting getViewSetting() {
		return setting;
	}
	
	@Override
	public List<Integer> getPrerequisites() {
		List<Integer> prerequisites = new LinkedList<Integer>();
		prerequisites.add(Prerequisite.GENE_EXPRESSION);
		prerequisites.add(Prerequisite.SNP_LIST_SELECTED);
		return prerequisites;
	}
}
