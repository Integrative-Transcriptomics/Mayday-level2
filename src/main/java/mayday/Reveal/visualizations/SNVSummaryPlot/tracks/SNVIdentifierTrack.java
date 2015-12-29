package mayday.Reveal.visualizations.SNVSummaryPlot.tracks;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.viewmodel.RevealViewModel;
import mayday.Reveal.visualizations.SNVSummaryPlot.SNVSummaryPlotMouseListener;

public class SNVIdentifierTrack extends SNVSummaryTrackComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6274926886797759826L;
	
	private RevealViewModel viewModel;
	
	public SNVIdentifierTrack(SNVSummaryTrack track) {
		super(track);
		this.viewModel = track.getViewModel();
		this.addMouseListener(new SNVSummaryPlotMouseListener(track));
	}

	@Override
	public void doPaint(Graphics2D g2) {
		int cellWidth = track.getSetting().getCellWidth();
		
		int startIndex = track.getSetting().getStartIndex();
		int stopIndex = track.getSetting().getStopIndex();
		
		SNVList snps = track.getSelectedSNPs();
		
		int space = 2;
		
		for(int i = startIndex; i < stopIndex; i++) {
			SNV s = snps.get(i);
			String snvLabel = s.getID();
			Rectangle2D r = g2.getFontMetrics().getStringBounds(snvLabel, g2);
			
			if(viewModel.isSelected(s)) {
				Rectangle2D rec = new Rectangle2D.Double(cellWidth * i, 0, cellWidth, getHeight());
				g2.setColor(track.getSetting().getSelectionColor());
				g2.fill(rec);
			}
			
			if(cellWidth > r.getHeight()) {
				AffineTransform af = g2.getTransform();
				g2.translate(cellWidth * i + cellWidth/2 + r.getCenterY(), getHeight()-r.getWidth()-space);
				g2.rotate(Math.toRadians(90));
				g2.setColor(Color.DARK_GRAY);
				g2.drawString(snvLabel, 0, 0);
				g2.setTransform(af);
			}
		}
	}
}
