package it.genomering.gui;

import it.genomering.structure.Genome;
import it.genomering.structure.SuperGenome;
import it.genomering.structure.SuperGenomeEvent;
import it.genomering.structure.SuperGenomeListener;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GenomeLegend extends JPanel implements SuperGenomeListener {
 
	protected GenomeRingPanel grPanel;
	
	public GenomeLegend(SuperGenome g, GenomeRingPanel grp) {
		//setLayout(new GridLayout(0,1));		
		setBackground(Color.WHITE);
		setOpaque(true);
		g.addListener(this);
		this.grPanel = grp;
		updateLegend(g);
	}
	
	protected void updateLegend(SuperGenome superGenome) {
		removeAll();
		int elementcount = superGenome.getNumberOfGenomes();
		int[] dims = findBestRC(elementcount);
		setLayout(new GridLayout(dims[1],dims[0]));		
		for (Genome genome : superGenome.getGenomes()) {
			add(new GenomeLegendItem(genome));
		}
		revalidate();
	}
	
	
	public static int[] findBestRC(int numberOfPlots) {
		int r=0,c=0;
		int waste=Integer.MAX_VALUE;
		for (int deltaC = -1; deltaC <= 1; ++deltaC ) {
			int xc = (int)Math.ceil(Math.sqrt(numberOfPlots))+deltaC;
			if (xc==0)
				continue;
			int xr = (int)Math.ceil((double)numberOfPlots/(double)xc);
			if (xc<0 || xr<0)
				continue;
			int xpenalty = Math.abs(xr-xc);
			int xwaste = 100 * ((xc*xr) - numberOfPlots)  + xpenalty;
			if (xwaste<waste) {
				c=xc;
				r=xr;
				waste = xwaste;
			}
			if (waste==0) break;
		}
		return new int[]{r,c};
	}

	@Override
	public void superGenomeChanged(SuperGenomeEvent evt) {
		updateLegend(evt.getSource());	
	}
}
