package mayday.Reveal.visualizations.snpmap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

@SuppressWarnings("serial")
public class SNPMapColumnHeader extends JComponent implements MouseListener, ViewModelListener, MouseMotionListener {

	private SNPMap snpMap;
	
	public SNPMapColumnHeader(SNPMap map) {
		this.snpMap = map;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
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
		SNVList snps = snpMap.snps;
		int cellWidth = snpMap.setting.getCellWidth();
		
		Rectangle vBounds = snpMap.compScroller.getVisibleRect();
		int startx = snpMap.compScroller.getHorizontalScrollBar().getValue();
		int start = Math.max((int)Math.floor(startx / cellWidth), 0);
		int stop = (int)Math.ceil((startx + vBounds.width) / cellWidth) + 1;
		stop = Math.min(stop, snpMap.snps.size());
		
		AffineTransform af = g2.getTransform();
		double transX = start * cellWidth;
		af.translate(transX, 0);
		g2.setTransform(af);
		
		Color c = snpMap.setting.getSelectionColor();
		Color c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 100);
		Rectangle2D rec = new Rectangle2D.Double(0, 0, getHeight(), cellWidth);
		
		int count = 0;
		for(int i = start; i < stop; i++) {
			SNV s = snps.get(i);
			String snpID = s.getID();
			Rectangle2D bounds = g2.getFontMetrics().getStringBounds(snpID, g2);
			
			if(cellWidth < bounds.getHeight()/1.5)
				break;
			
			if(snpMap.getViewModel().isSelected(s)) {
				g2.translate((count+1) * cellWidth, 0);
				g2.rotate( Math.PI / 2 );
				g2.setColor(c2);
				g2.fill(rec);
				g2.setColor(Color.BLACK);
				g2.setTransform(af);
			}
			
			g2.translate((count+1) * cellWidth - (cellWidth - bounds.getHeight())/2., getHeight() - bounds.getWidth() - 5);
			g2.rotate( Math.PI / 2 );
			
			g2.drawString(snpID, 0, 10);
			
			g2.setTransform(af);
			
			count++;
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			int x = e.getX();
			int cellSize = snpMap.setting.getCellWidth();
			int snpIndex = x / cellSize;
			
			if(snpIndex < snpMap.snps.size()) {
				SNV s = snpMap.snps.get(snpIndex);
				
				if(e.isControlDown()) {
					snpMap.getViewModel().toggleSNPSelected(s);
				} else {
					snpMap.getViewModel().setSNPSelection(s);
				}
			}
		}
	}
	
	private int startIndex = -1;
	private int stopIndex = -1;

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			int x = e.getX();
			int cellSize = snpMap.setting.getCellWidth();
			startIndex = x / cellSize;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			int x = e.getX();
			int cellSize = snpMap.setting.getCellWidth();
			stopIndex = x / cellSize;
			
			if(draged) {
				draged = false;
				
				if(startIndex > stopIndex) {
					int tmp = startIndex;
					startIndex = stopIndex;
					stopIndex = tmp;
				}
				
				if(startIndex < snpMap.snps.size() && startIndex >= 0
						&& stopIndex < snpMap.snps.size() && stopIndex >= 0) {
					
					if(startIndex != stopIndex) {
						Set<SNV> selection = new HashSet<SNV>();
						for(int i = startIndex; i <= stopIndex; i++) {
							selection.add(snpMap.snps.get(i));
						}
						
						Set<SNV> newSelection = new HashSet<SNV>(snpMap.getViewModel().getSelectedSNPs());
						
						if(e.isControlDown()) {
							if(newSelection.containsAll(selection)) {
								newSelection.removeAll(selection);
							} else {
								newSelection.addAll(selection);
							}
						} else {
							newSelection = selection;
						}
						snpMap.getViewModel().setSNPSelection(newSelection);
					}
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			repaint();
		}
	}
	
	private boolean draged = false;

	@Override
	public void mouseDragged(MouseEvent e) {
		draged = true;
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
}
