package mayday.Reveal.visualizations.LDPlot.tracks;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.SNVPair;
import mayday.Reveal.data.ld.LDResults;
import mayday.Reveal.visualizations.LDPlot.LDPlot;
import mayday.vis3.gradient.ColorGradient;

public class CorrelationTrack extends LDPlotTrack {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5821724996625839420L;

	public CorrelationTrack(LDPlot plot) {
		super(plot);
	}

	@Override
	public void doPaint(Graphics2D g2) {
		SNVList snps = getSelectedSNPs();
		
		int cellWidth = getSetting().getCellWidth();
		
		int startIndex = getSetting().getStartIndex();
		int stopIndex = getSetting().getStopIndex();
		
		int elongation = getSetting().getElongation();
		
		ColorGradient cg = getSetting().getColorGradient();
		LDResults ldRes = (LDResults)getDataStorage().getMetaInformationManager().get(LDResults.MYTYPE).get(0);
		
		//enlarge to left and right by 10 snps
		if(startIndex - elongation > 0) {
			startIndex -= elongation;
		} else {
			startIndex = 0;
		}
		
		if(stopIndex + elongation <= snps.size()) {
			stopIndex += elongation;
		} else {
			stopIndex = snps.size();
		}
		
		double diag = cellWidth / Math.sqrt(2);
		
		g2.translate(cellWidth * (startIndex+1), 0);
		
		for(int i = startIndex; i < stopIndex - 1; i++) {
			AffineTransform afi = g2.getTransform();
			SNV s1 = snps.get(i);
			for(int j = i + 1; j < stopIndex; j++) {
				AffineTransform afj = g2.getTransform();

				SNV s2 = snps.get(j);
				SNVPair sp = new SNVPair(s1, s2);
				double r2 = 0;
				Color cR2 = Color.WHITE;
				if(ldRes.contains(sp)) {
					r2 = ldRes.get(new SNVPair(s1, s2));
				}
				
				cR2 = cg.mapValueToColor(r2);
				
				Rectangle2D rec = new Rectangle2D.Double(0, 0, diag, diag);
				g2.rotate(Math.toRadians(45));
				g2.setColor(cR2);
				g2.fill(rec);
				
				if(getViewModel().isSelected(s1) || getViewModel().isSelected(s2)) {
					if(r2 >= getSetting().getCorrelationThreshold()) {
						g2.setColor(getSetting().getSelectionColor());
						g2.draw(rec);
					}
				}
				
				g2.setTransform(afj);
				g2.translate(cellWidth/2, cellWidth/2);
			}
			g2.setTransform(afi);
			g2.translate(cellWidth, 0);
		}
	}
}
