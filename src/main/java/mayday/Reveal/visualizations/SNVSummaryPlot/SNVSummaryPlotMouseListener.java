package mayday.Reveal.visualizations.SNVSummaryPlot;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Field;

import javax.swing.ToolTipManager;

import mayday.Reveal.data.SNV;
import mayday.Reveal.viewmodel.RevealViewModel;
import mayday.Reveal.visualizations.SNVSummaryPlot.tracks.SNVSummaryTrack;

public class SNVSummaryPlotMouseListener implements MouseListener, MouseWheelListener {

	private RevealViewModel viewModel;
	private SNVSummaryTrack track;
	
	public SNVSummaryPlotMouseListener(SNVSummaryTrack track) {
		this.track = track;
		this.viewModel = track.getViewModel();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.isControlDown()) {
			int rotation = e.getWheelRotation();
			int cellWidth = track.getSetting().getCellWidth();
			if(cellWidth + rotation >= 10) {
				cellWidth += rotation;
				track.getSetting().setCellWidth(cellWidth);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent ev) {
		Point p = ev.getPoint();
		double mouseX = p.getX();
		int snpIndex = (int)Math.floor(mouseX / track.getSetting().getCellWidth());
		
		if(snpIndex >= track.getSelectedSNPs().size())
			return;
		
		SNV snp = track.getSelectedSNPs().get(snpIndex);
		
		if(ev.getButton() == MouseEvent.BUTTON3) {
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
			
			track.setToolTipText(buf.toString());
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
			
			ToolTipManager.sharedInstance().mouseMoved(new MouseEvent(track, 0, 0, 0, p.x, p.y, 0, false));
			
			if (canShowImmediately)
				track.setToolTipText(null);
		}
		
		if(ev.getButton() == MouseEvent.BUTTON1) {
			viewModel.toggleSNPSelected(snp);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
