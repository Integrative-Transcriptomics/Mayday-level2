package mayday.GWAS.visualizations.snpmap.meta;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.VolatileImage;

import javax.swing.JComponent;

import mayday.GWAS.data.Subject;
import mayday.GWAS.viewmodel.RevealViewModel;
import mayday.GWAS.viewmodel.RevealViewModelEvent;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

@SuppressWarnings("serial")
public class SNPMapMetaDataPanel extends JComponent implements MouseListener, ViewModelListener {

	private VolatileImage selectionSquare;
	
	private SNPMapMetaComponent metaComp;
	private BasicStroke selectionStroke = new BasicStroke(2);
	
	public SNPMapMetaDataPanel(SNPMapMetaComponent metaComp) {
		this.metaComp = metaComp;
		addMouseListener(this);
	}
	
	protected void resizePlot() {
		int cellWidth = metaComp.snpMap.setting.getCellWidth();
		int cellHeight = metaComp.snpMap.setting.getCellHeight();
		
		GraphicsConfiguration gc = getGraphicsConfiguration();
		selectionSquare = gc.createCompatibleVolatileImage(cellWidth, cellHeight, Transparency.TRANSLUCENT);
		
		Color sC = metaComp.snpMap.setting.getSelectionColor();
		Color sCAlpha = new Color(sC.getRed(), sC.getGreen(), sC.getBlue(), 100);
		
		float lineWidth = selectionStroke.getLineWidth();
		
		double rW = Math.max(0., cellWidth - lineWidth);
		double rH = Math.max(0., cellHeight - lineWidth);
		
		Rectangle2D rSelected = new Rectangle2D.Double(lineWidth/2, lineWidth/2, rW, rH);
		//selected squares
		Graphics2D g = selectionSquare.createGraphics();
		g.setComposite(AlphaComposite.DstOut);
	    g.fillRect(0, 0, selectionSquare.getWidth(), selectionSquare.getHeight());
		g.setColor(sCAlpha);
		g.setStroke(selectionStroke);
		g.setComposite(AlphaComposite.SrcOut);
		g.draw(rSelected);
		g.dispose();
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
		
		int cellWidth = metaComp.snpMap.setting.getCellWidth();
		int cellHeight = metaComp.snpMap.setting.getCellHeight();
		
		RevealViewModel model = metaComp.snpMap.getViewModel();
		Rectangle2D rect = new Rectangle2D.Double(0, 0, cellWidth, cellHeight);
		
		for(int i = 0; i < metaComp.columnData.size(); i++) {
			AbstractVector values = metaComp.columnData.get(i);
			AffineTransform af = g2d.getTransform();
			
			ColorGradient cg = metaComp.setting.getColorGradient(i);
			cg.getResolution();
			
			for(int j = 0; j < values.size(); j++) {
				double value = values.get(j);
				Color c = cg.mapValueToColor(value);
				g2d.setColor(c);
				
				Integer subjectIndex = metaComp.snpMap.personIndices.get(j);
				Subject s = metaComp.snpMap.persons.get(subjectIndex);
				
				g2d.fill(rect);
				
				if(model.isSelected(s)) {
					g2d.drawImage(selectionSquare, 0, 0, null);
				}
				
				g2d.translate(0, cellHeight);
			}
			
			g2d.setTransform(af);
			g2d.translate(cellWidth, 0);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		//TODO
		System.out.println("Meta Data Click");
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.PERSON_SELECTION_CHANGED:
			repaint();
			break;
		}
	}
}
