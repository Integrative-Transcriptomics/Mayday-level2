package mayday.GWAS.visualizations.SNPSummaryPlot;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Field;

import javax.swing.ToolTipManager;

import mayday.GWAS.data.Gene;
import mayday.GWAS.data.Subject;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.meta.SLResults;

/**
 * @author jaeger
 *
 */
public class SNPPlotMouseListener implements MouseListener, MouseWheelListener {

	protected SNPSummaryPlot plot;
	
	/**
	 * @param plot
	 */
	public SNPPlotMouseListener(SNPSummaryPlot plot) {
		this.plot = plot;
	}
	
	@Override
	public void mousePressed(MouseEvent ev) {
		Point p = ev.getPoint();
		double mouseX = p.getX();
		int snpIndex = (int)Math.floor(mouseX / plot.setting.getCellWidth());
		
		if(snpIndex >= plot.snpList.size())
			return;
		
		SNP snp = plot.snpList.get(snpIndex);
		
		if(ev.getButton() == MouseEvent.BUTTON3) {
			Subject person = plot.setting.getPerson();
			Gene gene = plot.setting.getGene();
			
			StringBuffer buf = new StringBuffer();
			
			buf.append("<html>");
			buf.append("SNP ID: " + snp.getID());
			buf.append("<br>");
			buf.append("Associated gene: " + snp.getGene());
			buf.append("<br>");
			buf.append("Chromosomal Location:");
			buf.append("<br>");
			buf.append("- Chromosome " + snp.getChromosome());
			buf.append("<br>");
			buf.append("- base-pair position " + snp.getPosition());
			buf.append("<br>");
			buf.append("--------------------");
			buf.append("<br>");
			buf.append("Reference nucleotide: " + snp.getReferenceNucleotide());
			buf.append("<br>");
			double pValue = ((SLResults)(plot.getData().getMetaInformationManager().get(SLResults.MYTYPE).get(0))).get(gene).get(snp).p;
			buf.append("p-Value for gene " + gene.getName() + " = " + pValue);
			buf.append("<br>");
			buf.append("--------------------");
			buf.append("<br>");
			buf.append("Displayed Person: " + person.toString());
			buf.append("<br>");
			buf.append("Affection state: " + (person.affected() ? "affected" : "unaffected"));
			
			plot.setToolTipText(buf.toString());
			boolean canShowImmediately = false;
			
			try {
				Field f = ToolTipManager.class.getDeclaredField("showImmediately");
				f.setAccessible(true);
				f.set(ToolTipManager.sharedInstance(), Boolean.TRUE);
				canShowImmediately = true;
				//ToolTipManager.sharedInstance().setDismissDelay(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ToolTipManager.sharedInstance().mouseMoved(new MouseEvent(plot, 0, 0, 0, p.x, p.y, 0, false));
			
			if (canShowImmediately)
				plot.setToolTipText(null);
		}
		
		if(ev.getButton() == MouseEvent.BUTTON1) {
			plot.getViewModel().toggleSNPSelected(snp);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.isControlDown()) {
			int rotation = e.getWheelRotation();
			int cellWidth = plot.setting.getCellWidth();
			if(cellWidth + rotation >= 10) {
				cellWidth += rotation;
				plot.setting.setCellWidth(cellWidth);
				plot.resizePlot();
			}
		}
	}
}
