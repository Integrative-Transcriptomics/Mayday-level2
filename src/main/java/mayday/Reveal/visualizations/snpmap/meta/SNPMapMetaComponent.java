package mayday.Reveal.visualizations.snpmap.meta;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import mayday.Reveal.data.Gene;
import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;
import mayday.Reveal.utilities.MultiArraySorter;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.snpmap.SNPMap;
import mayday.Reveal.visualizations.snpmap.aggregation.Aggregation;
import mayday.Reveal.visualizations.snpmap.aggregation.Aggregator;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

@SuppressWarnings("serial")
public class SNPMapMetaComponent extends JComponent implements ViewModelListener {

	protected SNPMap snpMap;
	
	public ArrayList<String> columnHeader;
	public ArrayList<DoubleVector> columnData;
	
	public SNPMapMetaColumnHeader colHeader;
	public SNPMapMetaDataPanel dataComp;
	
	public JScrollPane headerScroller;
	public JScrollPane dataScroller;
	
	public List<Aggregation> aggregations;
	
	public SNPMapMetaDataSetting setting;
	
	public SNPMapMetaComponent(SNPMap snpMap) {
		this.snpMap = snpMap;
		this.columnHeader = new ArrayList<String>();
		this.columnData = new ArrayList<DoubleVector>();
		aggregations = new ArrayList<Aggregation>();
		
		this.setting = new SNPMapMetaDataSetting(this);
		
		this.setupDefaultColumns();
		
		colHeader = new SNPMapMetaColumnHeader(this);
		dataComp = new SNPMapMetaDataPanel(this);
		
		headerScroller = new JScrollPane(colHeader);
		dataScroller = new JScrollPane(dataComp);
		
		dataScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		dataScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		dataScroller.setBorder(null);
		
		JScrollBar vSBcomp = snpMap.compScroller.getVerticalScrollBar();
		JScrollBar vSBdata = dataScroller.getVerticalScrollBar();
		vSBdata.setModel(vSBcomp.getModel()); // synchronize scrollbars
		
		headerScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		headerScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		headerScroller.setBorder(null);
		
		JScrollBar hSBdata = dataScroller.getHorizontalScrollBar();
		JScrollBar hSBheader = headerScroller.getHorizontalScrollBar();
		hSBdata.setModel(hSBheader.getModel()); // synchronize scrollbars
		
		this.setLayout(new BorderLayout());
		
		add(headerScroller, BorderLayout.NORTH);
		add(dataScroller, BorderLayout.CENTER);
		
	}
	
	private void setupDefaultColumns() {
		//subject affection state
		SubjectList subjects = snpMap.getData().getSubjects();
		DoubleVector affectionData = new DoubleVector(subjects.size());
		for(int i = 0; i < subjects.size(); i++) {
			affectionData.set(i, subjects.get(i).affected() ? 1 : 0);
		}
		
		columnData.add(affectionData);
		columnHeader.add("Affection");
		setting.addColorGradientSetting("Affection", 0, 1);
		
		//gene expression levels
		GeneList genes = snpMap.getData().getGenes();
		
		if(genes != null) {
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			for(int i = 0; i < genes.size(); i++) {
				Gene g = genes.getGene(i);
				DoubleVector values = new DoubleVector(g.getValues());
				
				double tmin = values.min();
				double tmax = values.max();
				
				if(tmin < min)
					min = tmin;
				
				if(tmax > max)
					max = tmax;
				
				columnData.add(values);
				columnHeader.add(genes.getGene(i).getDisplayName());
				
			}
			
			setting.addColorGradientSetting("Gene Expression", min, max);
		}
	}
	
	public HierarchicalSetting getSetting() {
		return setting;
	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.PERSON_SELECTION_CHANGED:
			break;
		}
	}

	public void resizeComp(int colHeight, int compHeight) {
		int width = snpMap.setting.getCellWidth() * columnHeader.size();

		colHeader.setPreferredSize(new Dimension(width, colHeight));
		dataComp.setPreferredSize(new Dimension(width, compHeight));
		dataComp.resizePlot();
		
		colHeader.revalidate();
		dataComp.revalidate();
		
		colHeader.repaint();
		dataComp.repaint();
		
		revalidate();
		repaint();
	}

	public void update(int change) {
		switch(change) {
		case SNPMapMetaDataSetting.SORTING_CHANGED:
			repaint();
			break;
		default:
			repaint();
		}
	}

	public void sort(Integer[] indices) {
		for(int i = 0; i < columnData.size(); i++) {
			DoubleVector data = columnData.get(i);
			columnData.set(i, MultiArraySorter.sort(indices, data));
		}
		update(SNPMapMetaDataSetting.SORTING_CHANGED);
	}

	public void aggregate(Set<Subject> subjects) {
		Aggregator ag = snpMap.aggregator;
		
		GeneList genes = snpMap.getData().getGenes();
		Aggregation a = new Aggregation(columnHeader.size());
		a.setName("# " +( aggregations.size()+1));
		a.setSubjects(subjects);
		
		for(int j = 0; j < columnHeader.size(); j++) {
			String name = columnHeader.get(j);
			DoubleVector values = new DoubleVector(subjects.size());
			
			int i = 0;
			for(Subject s :subjects) {
				Integer rowIndex = snpMap.personIndices.getRightToLeft(s.getIndex());
//				System.out.println(rowIndex);
				double value = columnData.get(j).get(rowIndex.intValue());
				values.set(i++, value);
			}
			
			if(genes.contains(name)) {
				ag.aggregateGene(values);
			} else {
				ag.aggregate(values);
			}
			
			a.setAggregationValue(j, ag.getAggregationValue());
			a.setFrequency(j, ag.getFrequency());
		}
		
		aggregations.add(a);
	}

	public void removeAggregations(Set<Aggregation> aggregations) {
		//TODO
		this.aggregations.removeAll(aggregations);
	}
}
