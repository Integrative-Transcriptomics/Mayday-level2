package mayday.GWAS.visualizations.snpmap.aggregation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JComponent;

import mayday.GWAS.viewmodel.RevealViewModelEvent;
import mayday.GWAS.visualizations.snpmap.SNPMap;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

@SuppressWarnings("serial")
public class AggregationComponent extends JComponent implements ViewModelListener {

	private SNPMap snpMap;
	
	public AggregationComponent(SNPMap snpMap) {
		this.snpMap = snpMap;
	}
	
	public void removeNotify() {
		if(snpMap.getViewModel() != null)
			snpMap.getViewModel().removeViewModelListener(this);
		super.removeNotify();
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
		
		List<Aggregation> aggregations = snpMap.hapAggregations;
		
		Color homoColor = snpMap.setting.getHomoSNPColor();
		Color heteroColor = snpMap.setting.getHeteroSNPColor();
		Color refColor = snpMap.setting.getNoSNPColor();
		
		int cellWidth = snpMap.setting.getCellWidth();
		int cellHeight = snpMap.setting.getAggregationCellHeight();
		
		Rectangle bounds = snpMap.compScroller.getVisibleRect();
		int startx = snpMap.compScroller.getHorizontalScrollBar().getValue();
		int start = Math.max((int)Math.floor(startx / cellWidth), 0);
		int stop = (int)Math.ceil((startx + bounds.width) / cellWidth) + 1;
		stop = Math.min(stop, snpMap.snps.size());
		
		AffineTransform af = g2.getTransform();
		
		for(int i = 0; i < aggregations.size(); i++) {
			AffineTransform af2 = g2.getTransform();
			
			Aggregation a = aggregations.get(i);
			for(int j = start; j < stop; j++) {
				double value = a.getAggregationValue(j);
				double freq = a.getFrequency(j);
				
				g2.setTransform(af2);
				
				int alpha = (int)Math.rint(255 * freq);
				int v = (int) value;
				
				Color c = Color.WHITE;
				
				switch(v) {
				case 0:
					c = new Color(refColor.getRed(), refColor.getGreen(), refColor.getBlue(), alpha);
					break;
				case 1:
					c = new Color(heteroColor.getRed(), heteroColor.getGreen(), heteroColor.getBlue(), alpha);
					break;
				case 2:
					c = new Color(homoColor.getRed(), homoColor.getGreen(), homoColor.getBlue(), alpha);
					break;
				}
				
				g2.setColor(c);
				
				Rectangle2D rec = new Rectangle2D.Double(0, 0, cellWidth, cellHeight);
				g2.translate(j * cellWidth, 0);
				g2.fill(rec);
			}
			
			g2.setTransform(af);
			g2.translate(0, (i+1) * cellHeight);
		}
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			break;
		}
	}
}
