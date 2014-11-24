package mayday.Reveal.visualizations.snpmap.meta;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

@SuppressWarnings("serial")
public class SNPMapMetaColumnHeader extends JComponent implements MouseListener, ViewModelListener {

	private SNPMapMetaComponent metaComp;
	
	public SNPMapMetaColumnHeader(SNPMapMetaComponent metaComp) {
		this.metaComp = metaComp;
		addMouseListener(this);
	}
	
	public void removeNotify() {
		if(metaComp.snpMap.getViewModel() != null)
			metaComp.snpMap.getViewModel().removeViewModelListener(this);
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
		
		int cellWidth = metaComp.snpMap.setting.getCellWidth();
		
		AffineTransform af = g2d.getTransform();
		
		for(int i = 0; i < metaComp.columnHeader.size(); i++) {
			String header = metaComp.columnHeader.get(i);
			Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(header, g2d);
			
			if(cellWidth < bounds.getHeight()/1.5)
				break;
			
			g2d.translate((i+1) * cellWidth - (cellWidth - bounds.getHeight())/2., getHeight() - bounds.getWidth() - 5);
			g2d.rotate( Math.PI / 2 );
			
			g2d.drawString(header, 0, 10);
			
			g2d.setTransform(af);
		}
	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.PROBE_SELECTION_CHANGED:
			break;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int index = x / metaComp.snpMap.setting.getCellWidth();
		
		if(index < metaComp.columnHeader.size()) {
			if(e.getClickCount() == 2) {
				DoubleVector template = metaComp.columnData.get(index);
				metaComp.snpMap.sort(template);
			}
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
