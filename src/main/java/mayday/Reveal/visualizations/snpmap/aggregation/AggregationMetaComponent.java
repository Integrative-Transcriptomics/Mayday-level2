package mayday.Reveal.visualizations.snpmap.aggregation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.snpmap.SNPMap;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

@SuppressWarnings("serial")
public class AggregationMetaComponent extends JComponent implements ViewModelListener {

	private SNPMap snpMap;
	
	public AggregationMetaComponent(SNPMap snpMap) {
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
		Graphics2D g2d = (Graphics2D)g;
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.BLACK);
		
		int cellWidth = snpMap.setting.getCellWidth();
		int cellHeight = snpMap.setting.getAggregationCellHeight();
		
		AffineTransform af = g2d.getTransform();
		
		for(int i = 0; i < snpMap.metaComp.aggregations.size(); i++) {
			Aggregation a  = snpMap.metaComp.aggregations.get(i);
			AffineTransform af2 = g2d.getTransform();
			
			for(int j = 0; j < snpMap.metaComp.columnHeader.size(); j++) {
				ColorGradient cg = snpMap.metaComp.setting.getColorGradient(j);
				double value = a.getAggregationValue(j);
				Color c = cg.mapValueToColor(value);
				int alpha = (int)Math.rint(255 * a.getFrequency(j));
				Color c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
				
				g2d.setTransform(af2);
				
				g2d.setColor(c2);
				Rectangle2D rect = new Rectangle2D.Double(0, 0, cellWidth, cellHeight);
				g2d.translate(j * cellWidth, 0);
				g2d.fill(rect);
			}
			
			g2d.setTransform(af);
			g2d.translate(0, (i+1) * cellHeight);
		}
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.PROBE_SELECTION_CHANGED:
			break;
		}
	}
}
