package mayday.GWAS.visualizations.snpmap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.VolatileImage;

import javax.swing.JComponent;

import mayday.GWAS.data.Haplotypes;
import mayday.GWAS.data.HaplotypesList;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.Subject;
import mayday.GWAS.viewmodel.RevealViewModel;
import mayday.GWAS.viewmodel.RevealViewModelEvent;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

@SuppressWarnings("serial")
public class SNPMapComponent extends JComponent implements MouseListener, ViewModelListener {

	private VolatileImage redSquare;
	private VolatileImage greenSquare;
	private VolatileImage yellowSquare;
	
	private VolatileImage redSquareSelected;
	private VolatileImage greenSquareSelected;
	private VolatileImage yellowSquareSelected;
	
	private BasicStroke defaultStroke = new BasicStroke(1);
	private BasicStroke selectionStroke = new BasicStroke(2);
	
	private SNPMap snpMap;
	
	public SNPMapComponent(SNPMap map) {
		this.snpMap = map;
		this.addMouseListener(this);
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
		this.paintPlot(g2);
	}
	
	private VolatileImage getRectangle(char a, char b, char r, boolean selected) {
		if(a == r && b == r) {
			return selected ? greenSquareSelected : greenSquare;
		} else if(a == r && b != r || a != r && b == r) {
			return selected ? yellowSquareSelected : yellowSquare;
		} else {
			return selected ? redSquareSelected : redSquare;
		}
	}
	
	protected void resizePlot() {
		int cellWidth = snpMap.setting.getCellWidth();
		int cellHeight = snpMap.setting.getCellHeight();
		
		GraphicsConfiguration gc = getGraphicsConfiguration();
		
		redSquare = gc.createCompatibleVolatileImage(cellWidth, cellHeight);
		greenSquare = gc.createCompatibleVolatileImage(cellWidth, cellHeight);
		yellowSquare = gc.createCompatibleVolatileImage(cellWidth, cellHeight);
		
		redSquareSelected = gc.createCompatibleVolatileImage(cellWidth, cellHeight);
		greenSquareSelected = gc.createCompatibleVolatileImage(cellWidth, cellHeight);
		yellowSquareSelected = gc.createCompatibleVolatileImage(cellWidth, cellHeight);
		
		Rectangle2D r = new Rectangle2D.Double(0, 0, cellWidth, cellHeight);
		
		float lineWidth = selectionStroke.getLineWidth();
		
		double rW = Math.max(0., cellWidth - lineWidth);
		double rH = Math.max(0., cellHeight - lineWidth);
		
		Rectangle2D rSelected = new Rectangle2D.Double(lineWidth/2, lineWidth/2, rW, rH);
		
		Graphics2D g = redSquare.createGraphics();
		g.setColor(snpMap.setting.getHomoSNPColor());
		g.fill(r);
		g.dispose();
		
		g = greenSquare.createGraphics();
		g.setColor(snpMap.setting.getNoSNPColor());
		g.fill(r);
		g.dispose();
		
		g = yellowSquare.createGraphics();
		g.setColor(snpMap.setting.getHeteroSNPColor());
		g.fill(r);
		g.dispose();
		
		Color sC = snpMap.setting.getSelectionColor();
		Color sCAlpha = new Color(sC.getRed(), sC.getGreen(), sC.getBlue(), 100);
		
		//selected squares
		g = redSquareSelected.createGraphics();
		g.setColor(snpMap.setting.getHomoSNPColor());
		g.setStroke(defaultStroke);
		g.fill(r);
		g.setColor(sCAlpha);
		g.setStroke(selectionStroke);
		g.draw(rSelected);
		g.dispose();
		
		g = greenSquareSelected.createGraphics();
		g.setColor(snpMap.setting.getNoSNPColor());
		g.setStroke(defaultStroke);
		g.fill(r);
		g.setColor(sCAlpha);
		g.setStroke(selectionStroke);
		g.draw(rSelected);
		g.dispose();
		
		g = yellowSquareSelected.createGraphics();
		g.setColor(snpMap.setting.getHeteroSNPColor());
		g.setStroke(defaultStroke);
		g.fill(r);
		g.setColor(sCAlpha);
		g.setStroke(selectionStroke);
		g.draw(rSelected);
		g.dispose();
	}
	
	public void setPreferredSize(Dimension d) {
		this.resizePlot();
		super.setPreferredSize(d);
	}
	
	public void paintPlot(Graphics2D g) {
		HaplotypesList haplotypes = snpMap.getData().getHaplotypes();
		
		int cellWidth = snpMap.setting.getCellWidth();
		int cellHeight = snpMap.setting.getCellHeight();
		
		Rectangle bounds = snpMap.compScroller.getVisibleRect();
		int startx = snpMap.compScroller.getHorizontalScrollBar().getValue();
		int start = Math.max((int)Math.floor(startx / cellWidth), 0);
		int stop = (int)Math.ceil((startx + bounds.width) / cellWidth) + 1;
		stop = Math.min(stop, snpMap.snps.size());
		
		int starty = snpMap.compScroller.getVerticalScrollBar().getValue();
		int start2 = Math.max((int)Math.floor(starty / cellHeight), 0);
		int stop2 = (int)Math.ceil((starty + bounds.height) / cellHeight) + 1;
		stop2 = Math.min(stop2, snpMap.persons.size());
		
		AffineTransform af = g.getTransform();
		double transX = start * cellWidth;
		double transY = start2 * cellHeight;
		af.translate(transX, transY);
		g.setTransform(af);
		
		for(int j = start2; j < stop2; j++) {
			Integer index = snpMap.personIndices.get(j);

			Haplotypes h = haplotypes.get(index);
			AffineTransform af2 = g.getTransform();
			for(int i = start; i < stop; i++) {
				SNP s = snpMap.snps.get(i);
				char A = h.getSNPA(s.getIndex());
				char B = h.getSNPB(s.getIndex());
				char R = s.getReferenceNucleotide();
				
				VolatileImage r;
				
				if(snpMap.getViewModel().isSelected(s) || snpMap.getViewModel().isSelected(snpMap.persons.get(index))) {
					r = getRectangle(A, B, R, true);
				} else {
					r = getRectangle(A, B, R, false);
				}
				
				g.drawImage(r, 0, 0, null);
				g.translate(cellWidth, 0);
			}
			
			g.setTransform(af2);
			g.translate(0, cellHeight);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			int x = e.getX();
			int cellWidth = snpMap.setting.getCellWidth();
			int cellHeight = snpMap.setting.getCellHeight();
			int snpIndex = x / cellWidth;
			
			if(snpIndex < snpMap.snps.size()) {
				SNP s = snpMap.snps.get(snpIndex);
				
				int y = e.getY();
				Integer pIndex = y / cellHeight;
				
				if(pIndex < snpMap.persons.size()) {
					Integer personIndex = snpMap.personIndices.get(pIndex);
					
					if(personIndex < snpMap.personIndices.size()) {
						Subject p = snpMap.persons.get(personIndex);
						RevealViewModel model = snpMap.getViewModel();
						
						if(e.isControlDown()) {
							if(!model.isSelected(s))
								model.toggleSNPSelected(s);
							if(!model.isSelected(p))
								model.togglePersonSelected(p);
							if(e.isShiftDown()) {
								if(model.isSelected(s))
									model.toggleSNPSelected(s);
								if(model.isSelected(p))
									model.togglePersonSelected(p);
							}
						} else {
							model.setSNPSelection(s);
							model.setPersonSelection(p);
						}	
					}
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			repaint();
			break;
		case RevealViewModelEvent.PERSON_SELECTION_CHANGED:
			repaint();
			break;
		}		
	}
}
