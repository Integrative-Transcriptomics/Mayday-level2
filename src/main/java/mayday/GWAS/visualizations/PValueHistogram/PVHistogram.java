//package mayday.GWAS.visualizations.PValueHistogram;
//
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;
//import java.awt.geom.AffineTransform;
//import java.util.List;
//
//import mayday.GWAS.data.Gene;
//import mayday.GWAS.data.GeneList;
//import mayday.GWAS.data.LocusResults;
//import mayday.GWAS.data.SNPList;
//import mayday.GWAS.data.SingleLocusResult;
//import mayday.GWAS.io.GWASFileReader;
//import mayday.GWAS.visualizations.GWASVisualization;
//import mayday.GWAS.visualizations.SNPPlot.HistBar;
//import mayday.core.MaydayDefaults;
//import mayday.core.settings.generic.HierarchicalSetting;
//import mayday.vis3.model.ViewModelEvent;
//
///**
// * @author jaeger
// *
// */
//@SuppressWarnings("serial")
//public class PVHistogram extends GWASVisualization {
//
//	protected double histHeight, labelHeight;
//	protected double snpBoxWidth = 15;
//	protected double logMinPValue;
//	protected SNPList snps;
//	protected LocusResults<Gene, SingleLocusResult> slrs;
//	protected GeneList genes;
//	protected List<String> externalSNPs;
//		
//	protected int snpCount = 0;
//	
//	/**
//	 * @param gwasfr
//	 * @param menu 
//	 */
//	public PVHistogram(GWASFileReader gwasfr) {
//		super("p-Value Histogram");
//		
//		this.snps = gwasfr.getSNPs();
//		this.slrs = gwasfr.getSingleLocusResults();
//		this.genes = gwasfr.getGenes();
//		this.externalSNPs = gwasfr.getExternalSNPList();
//	}
//	
//	private Graphics2D transformGraphics(Graphics g) {
//		Graphics2D g2d = (Graphics2D)g;
//		
//		g2d.setBackground(Color.WHITE);
//		g2d.clearRect(0, 0, getWidth(), getHeight());
//		
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		
//		this.histHeight = ((double)getHeight() / genes.size())/2.;
//		this.labelHeight = histHeight;
//		
//		return g2d;
//	}
//
//	@Override
//	public void viewModelChanged(ViewModelEvent vme) {
//		//TODO react on this event
//		switch(vme.getChange()) {
//		case ViewModelEvent.PROBE_SELECTION_CHANGED: break;
//		}
//	}
//
//	@Override
//	public void paint(Graphics g) {
//		super.paint(g);
//		Graphics2D g2d = (Graphics2D)g;
//		
//		this.transformGraphics(g2d);
//		g2d.setFont(MaydayDefaults.DEFAULT_PLOT_FONT);
//		AffineTransform af = g2d.getTransform();
//		int count = 0;
//		
//		for(int i = 0; i < genes.size(); i++) {
//			double transY = i * (histHeight);
//			logMinPValue = -Math.log10(slrs.get(genes.getGene(i)).minPValue);
//			for(int j = 0; j < snps.size(); j++) {
//				
//				if(!externalSNPs.contains(snps.get(j).getID())) continue;
//				
//				double transX = count * snpBoxWidth;
//				double p = slrs.get(genes.getGene(i)).get(snps.get(j)).p;
//				//if(p == -1 || p == 0 || p > 0.01) continue;
//				
//				double barHeight = (-Math.log10(p) / logMinPValue) * histHeight;
//				g2d.translate(0, transY);
//				String snpID = "";
//				if(i == genes.size() - 1) {
//					snpID = snps.get(j).getID();
//				}
//				AffineTransform af2 = g2d.getTransform();
//				HistBar histBar = new HistBar(snpID);
//				histBar.draw(g2d, af2, transX, barHeight, snpBoxWidth, histHeight);
//				g2d.setTransform(af);
//				count++;
//			}
//			if(i != genes.size() -1)
//				count = 0;
//			g2d.setColor(Color.BLACK);
//			g2d.drawString(genes.getGene(i).getName(), (int)(externalSNPs.size() * snpBoxWidth + 10), (int)(transY+histHeight));
//		}
//		
//		if(snpCount != count) {
//			snpCount = count;
//			this.setPreferredSize(new Dimension(snpCount*(int)snpBoxWidth+60, getHeight()));
//			revalidate();
//		}
//		
//	}
//
//	@Override
//	public HierarchicalSetting setupSettings() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void updatePlot() {
//		repaint();
//	}
//}
