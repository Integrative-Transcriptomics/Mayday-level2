package mayday.Reveal.visualizations.manhattanplot_old;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.StatisticalTestResult;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class ManhattanPlotComponent extends JComponent implements MouseListener {
	
	private ManhattanPlot plot;
	
	/**
	 * @param plot
	 */
	public ManhattanPlotComponent(ManhattanPlot plot) {
		this.plot = plot;
		this.setBackground(Color.WHITE);
		this.setForeground(Color.BLACK);
		this.addMouseListener(this);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, getWidth(), getHeight());
		
		if(plot.getData() != null) {
			SNPList snps = plot.getData().getGlobalSNPList();
			
			StatisticalTestResult ttr = null;
			
			MetaInformation info = plot.getData().getProjectHandler().getSelectedMetaInformation();
			
			if(info != null && info instanceof StatisticalTestResult)
				ttr = (StatisticalTestResult)info;
			
			if(ttr == null) {
				return;
			}
			
			double minP = ttr.getMin();
			double mlogMinP = -Math.log10(minP);
			
			for(SNP s : snps) {
				double snpPos = ((double)s.getIndex() / snps.size()) * getWidth();
				double p = ttr.getPValue(s);
				double mlogP = -Math.log10(p);
				double scaledP = getHeight() - (mlogP / mlogMinP) * getHeight();
				
				Rectangle2D point = new Rectangle2D.Double(snpPos-1, scaledP-1, 3, 3);
				
//				System.out.println(point.getCenterX() + " : " + point.getCenterY());
				
				if(plot.getViewModel().isSelected(s)) {
					g2d.setColor(Color.RED);
				} else {
					if(p < 0.05) {
						g2d.setColor(Color.BLUE);
					} else {
						g2d.setColor(Color.DARK_GRAY);
					}
				}
				
				g2d.fill(point);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		plot.updatePlot();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
