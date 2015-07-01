package mayday.Reveal.visualizations.LDPlot.tracks;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;

import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.meta.Genome;
import mayday.Reveal.visualizations.LDPlot.LDPlot;

public class GenomeTrack extends LDPlotTrack implements MouseWheelListener, MouseMotionListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7377361597211061836L;

	private double zoomLevel = 1;
	private int shift = 0;
	
	public GenomeTrack(LDPlot plot) {
		super(plot);
		setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		this.addMouseWheelListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	@Override
	public void doPaint(Graphics2D g2) {
		SNVList snps = getSelectedSNPs();
		Genome g = getDataStorage().getGenome();
		long genomeLength = g.getTotalLength();
		
		long offset = 0;
		
		for(int i = 0; i < g.getNumberOfSequences(); i++) {
			String chrName = g.getSequenceName(i);
			long length = g.getSequencLength(chrName);
			
			String mappedName = g.getMappedSequenceName(chrName);
			
			double oRel = (double)offset / (double) genomeLength;
			double chrRel = (double)length / (double) genomeLength;
		
			int oPos = (int)(getWidth() * zoomLevel * oRel) + shift;
			int chrLength = (int)(getWidth() * zoomLevel * chrRel);
			
			if(i % 2 == 0) {
				Rectangle2D rec = new Rectangle2D.Double(oPos, 0, chrLength, getHeight());
				g2.setColor(Color.LIGHT_GRAY);
				g2.fill(rec);
			}
			
			Rectangle2D r = g2.getFontMetrics().getStringBounds(mappedName, g2);
			
			g2.setColor(Color.BLACK);
			AffineTransform af = g2.getTransform();
			g2.translate(oPos + chrLength/2 - r.getCenterX() , 3);
			g2.rotate(Math.toRadians(90));
			g2.setColor(Color.DARK_GRAY);
			g2.drawString(getSetting().getChromosomePrefix() + " " + mappedName, 0, 0);
			g2.setTransform(af);
			
			offset += length;
		}
		
		int startIndex = getSetting().getStartIndex();
		int stopIndex = getSetting().getStopIndex();
		
		int cellWidth = getSetting().getCellWidth();
		
		for(int i = startIndex; i < stopIndex; i++) {
			SNV s = snps.get(i);
			
			long snpGlobal = g.getGlobalPosition(s.getChromosome(), s.getPosition());
			double relPos = (double)snpGlobal / (double)genomeLength;
			
			int snpPos = (int)(getWidth() * zoomLevel * relPos) + shift;
			
			g2.setColor(getSetting().getSNVColor());
			g2.drawLine(snpPos, 0, snpPos, getHeight()/2);
			
			g2.drawLine(snpPos, getHeight()/2, cellWidth * i + cellWidth/2, getHeight());
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int wheelRotation = e.getWheelRotation();
		
		if(e.isControlDown()) {
			if(wheelRotation < 0) {
				zoomLevel *= 1.2;
				shift *= 1.2;
			} else if(wheelRotation > 0) {
				zoomLevel /= 1.2;
				shift /= 1.2;
			}
		} else {
			if(e.isShiftDown()) {
				this.shift += wheelRotation*10;	
			} else {
				this.shift += wheelRotation*100;
			}
		}
		
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2) {
			this.shift = 0;
			this.zoomLevel = 1;
			repaint();
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
}
