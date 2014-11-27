package mayday.Reveal.visualizations.SNPSummaryPlot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JScrollPane;

import mayday.Reveal.data.Haplotypes;
import mayday.Reveal.data.HaplotypesList;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;
import mayday.Reveal.data.meta.SLResults;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.utilities.ATCGColors;
import mayday.Reveal.utilities.SNPLists;
import mayday.Reveal.utilities.SNPSorter;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.core.MaydayDefaults;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class SNPSummaryPlot extends RevealVisualization {

	protected SNPSorter snpSorter;
	
	protected HaplotypesList haplotypesList;
	protected SNPList snpList;
	protected SubjectList personList;
	protected SLResults slrs;
	
	SNPSummaryPlotSetting setting;
	
	double labelHeight;
	double frequencyHeight;
	double aggregationHeight;
	double referenceHeight;
	
	protected HistBar[] labelBars;
	protected FrequencyBar[] freqBars;
	protected AggregationBar[] agBars;
	protected ReferenceBar[] refBars;
	
	double[][] affected, unaffected;
	
	/**
	 * @param projectHandler 
	 * 
	 */
	public SNPSummaryPlot(ProjectHandler projectHandler) {
		setData(projectHandler.getSelectedProject());
		
		haplotypesList = getData().getHaplotypes();
		snpList = SNPLists.createUniqueSNPList(projectHandler.getSelectedSNPLists());
		personList = getData().getSubjects();
		
		snpSorter = new SNPSorter(snpList);
		
		affected = new double[snpList.size()][];
		unaffected = new double[snpList.size()][];
		
		SNPPlotMouseListener listener = new SNPPlotMouseListener(this);
		this.addMouseListener(listener);
		this.addMouseWheelListener(listener);
		setOpaque(true);
	}

	protected void calculateViewElements() {
		for(int i = 0; i < snpList.size(); i++) {
			affected[i] = getFrequencyForPairs(snpList.get(i).getIndex(), true);
			unaffected[i] = getFrequencyForPairs(snpList.get(i).getIndex(), false);
		}
		
		int numSNPs = snpList.size();
		
		labelBars = new HistBar[numSNPs];
		freqBars = new FrequencyBar[numSNPs];
		agBars = new AggregationBar[numSNPs];
		refBars = new ReferenceBar[numSNPs];
		
		for(int i = 0; i < snpList.size(); i++) {
			SNP s = snpList.get(i);
			labelBars[i] = new HistBar(snpList.get(i).getID());
			freqBars[i] = new FrequencyBar(affected[i], unaffected[i]);
			char ref = s.getReferenceNucleotide();
			agBars[i] = new AggregationBar(affected[i], unaffected[i], ref);
			refBars[i] = new ReferenceBar(ref);
		}
	}
	
	private void doPaint(Graphics g) {
		if(setting == null)
			return;

		int snpBoxWidth = setting.getCellWidth();
		
		Graphics2D g2d = transformGraphics(g);
		AffineTransform af = g2d.getTransform();
		
		g2d.setFont(MaydayDefaults.DEFAULT_PLOT_FONT);
		
		Rectangle r = ((JScrollPane)getParent().getParent()).getVisibleRect();
		int startx = ((JScrollPane)getParent().getParent()).getHorizontalScrollBar().getValue();
		int start = Math.max((int)Math.floor(startx / snpBoxWidth), 0);
		int stop = (int)Math.ceil((startx + r.width) / snpBoxWidth) + 1;
		
		stop = Math.min(stop, snpList.size());
		
		if(!isShowing()) {
			start = 0;
			stop = snpList.size();
		}
		
		if(snpList.size() > 0) {
			for(int i = start; i < stop; i++) {
				double transX = i * snpBoxWidth;
				double h = 0;
				
				SNP snp = snpList.get(snpSorter.get(i));
				boolean selected = getViewModel().isSelected(snp);
				
				if(selected) {
					Color sC = setting.getSelectionColor();
					Color sCt = new Color(sC.getRed(), sC.getGreen(), sC.getBlue(), 75);
					Rectangle2D r2d = new Rectangle2D.Double(transX, 0, snpBoxWidth, labelHeight);
					g2d.setColor(sCt);
					g2d.fill(r2d);
				}
				
				labelBars[snpSorter.get(i)].draw(g2d, af, transX, 0, snpBoxWidth, labelHeight);
				
				//frequencyBar
				h += labelHeight;
				freqBars[snpSorter.get(i)].draw(g2d, af, transX, h, snpBoxWidth, frequencyHeight);
				
				g2d.setTransform(af);
				
				//aggregationBar
				h += frequencyHeight;
				agBars[snpSorter.get(i)].draw(g2d, af, transX, h, snpBoxWidth, aggregationHeight, setting.getHorizontalAggregation());
				
				//referenceBox
				h += aggregationHeight; 
				refBars[snpSorter.get(i)].draw(g2d, af, transX, h, snpBoxWidth, referenceHeight, setting.getRefWithChange());
			}
		}
	}
	
	private Graphics2D transformGraphics(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, getWidth(), getHeight());
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		int h = getHeight();
		
		labelHeight = h/5.;
		frequencyHeight = h*2/5.;
		aggregationHeight = h/5.;
		referenceHeight = h/5.;
		
		return g2d;
	}
	
	private double[] getFrequencyForPairs(Integer snpIndex, boolean affected) {
		int[] count = new int[10];
		Arrays.fill(count, 0);
		
		if(affected) {
			ArrayList<Subject> affectedPersons = personList.getAffectedSubjects();
			for(Subject p : affectedPersons) {
				Haplotypes h = haplotypesList.get(p.getIndex());
				char[] snpPairs = new char[]{h.getSNPA(snpIndex), h.getSNPB(snpIndex)};
				Arrays.sort(snpPairs);
				count[ATCGColors.getPairIndex(snpPairs[0], snpPairs[1])]++;
			}
			double[] result = new double[10];
			for(int j = 0; j < result.length; j++) {
				result[j] = count[j] / (double)affectedPersons.size();
			}
			return result;
		} else {
			ArrayList<Subject> unaffectedPersons = personList.getUnaffectedSubjects();
			for(Subject p : unaffectedPersons) {
				Haplotypes h = haplotypesList.get(p.getIndex());
				
				char[] snpPairs = new char[]{h.getSNPA(snpIndex), h.getSNPB(snpIndex)};
				Arrays.sort(snpPairs);
				count[ATCGColors.getPairIndex(snpPairs[0], snpPairs[1])]++;
			}
			double[] result = new double[10];
			for(int j = 0; j < result.length; j++) {
				result[j] = count[j] / (double)unaffectedPersons.size();
			}
			return result;
		}
	}
	
	/**
	 * resize this plot
	 */
	public void resizePlot() {
		int snpBoxWidth = setting.getCellWidth();
		this.setPreferredSize(new Dimension(snpList.size() * snpBoxWidth, 0));
		revalidate();
		repaint();
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			updatePlot();
			break;
		}
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		setting = new SNPSummaryPlotSetting(this);
		this.calculateViewElements();
		this.resizePlot();
		return setting;
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setBackground(Color.white);
		g2.clearRect(0,0, getWidth(), getHeight());
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paint(g2);
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		doPaint(g2);
	}
	
	/**
	 * @return snps used for visualization
	 */
	public SNPList getSNPs() {
		return this.snpList;
	}

	@Override
	public void updatePlot() {
		repaint();
	}

	@Override
	public HierarchicalSetting getViewSetting() {
		return setting;
	}
	
	@Override
	public List<Integer> getPrerequisites() {
		List<Integer> prerequisites = new LinkedList<Integer>();
		prerequisites.add(Prerequisite.SNP_LIST_SELECTED);
		return prerequisites;
	}

	public void setSNPList(SNPList newList) {
		this.snpList = newList;
	}

	public SNPSorter getSNPSorter() {
		return this.snpSorter;
	}
}
