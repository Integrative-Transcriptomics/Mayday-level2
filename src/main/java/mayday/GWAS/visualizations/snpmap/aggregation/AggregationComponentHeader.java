package mayday.GWAS.visualizations.snpmap.aggregation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import mayday.GWAS.data.Subject;
import mayday.GWAS.visualizations.snpmap.SNPMap;

@SuppressWarnings("serial")
public class AggregationComponentHeader extends JComponent implements MouseListener {

	private SNPMap snpMap;
	
	public AggregationComponentHeader(SNPMap snpMap) {
		this.snpMap = snpMap;
		addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			int y = e.getY();
			int cellHeight = snpMap.setting.getAggregationCellHeight();
			int index = y / cellHeight;
			
			if(index < snpMap.hapAggregations.size()) {
				Aggregation a = snpMap.hapAggregations.get(index);
				
				if(e.isControlDown()) {
					Set<Subject> selectedSubjects = snpMap.getViewModel().getSelectedPersons();
					Set<Subject> newSelection = new HashSet<Subject>(selectedSubjects);
					
					if(newSelection.containsAll(a.getAggregatedSubjects())) {
						newSelection.removeAll(a.getAggregatedSubjects());
					} else {
						newSelection.addAll(a.getAggregatedSubjects());
					}
				} else {
					snpMap.getViewModel().setPersonSelection(a.getAggregatedSubjects());
				}
			}
		}
		
		if(e.getButton() == MouseEvent.BUTTON3) {
			//TODO show something?!
		}
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
		
		int cellHeight = snpMap.setting.getAggregationCellHeight();

		AffineTransform af = g2.getTransform();
//		Color selectionColor = snpMap.setting.getSelectionColor();
		
		for(int i = 0; i < snpMap.hapAggregations.size(); i++) {
			Aggregation a = snpMap.hapAggregations.get(i);
			String name = a.getName();
			
			Rectangle2D bounds = g2.getFontMetrics().getStringBounds(name, g2);
			
			if(cellHeight < bounds.getHeight()/1.5)
				break;
			
			g2.translate(0, i * cellHeight + 1);
			
//			if(snpMap.getViewModel().isSelected(p)) {
//				Color c = new Color(selectionColor.getRed(), selectionColor.getGreen(), selectionColor.getBlue(), 100);
//				g2.setColor(c);
//				Rectangle2D rec = new Rectangle2D.Double(0, 0, getWidth(), cellHeight);
//				g2.fill(rec);
//				g2.setColor(Color.BLACK);
//			}
			
			g2.translate(0, bounds.getHeight() + (cellHeight - bounds.getHeight())/2. - 2);
			g2.drawString(name, 0, 0);
			g2.setTransform(af);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
}
