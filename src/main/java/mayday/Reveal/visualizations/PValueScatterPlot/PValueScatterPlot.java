package mayday.Reveal.visualizations.PValueScatterPlot;
//package mayday.GWAS.visualizations.PValueScatterPlot;
//
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Shape;
//import java.awt.Stroke;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.GeneralPath;
//import java.awt.geom.Line2D;
//import java.awt.geom.Rectangle2D;
//import java.util.ArrayList;
//import java.util.Collections;
//
//import mayday.GWAS.data.SNP;
//import mayday.GWAS.data.SNPList;
//import mayday.GWAS.data.SingleLocusResult;
//import mayday.GWAS.io.GWASFileReader;
//import mayday.GWAS.utilities.GeneColors;
//import mayday.GWAS.utilities.HumanGenome;
//import mayday.GWAS.visualizations.GWASVisualization;
//import mayday.core.settings.generic.HierarchicalSetting;
//import mayday.vis3.model.ViewModelEvent;
//
///**
// * @author jaeger
// *
// */
//@SuppressWarnings("serial")
//public class PValueScatterPlot extends GWASVisualization implements MouseListener {
//
//	/**
//	 * dot diameter
//	 */
//	public static final int dotDiameter = 4; //TODO use settings to define this!
//	
//	private GWASFileReader gwasfr;
//	private double genomeLength = 0;
//	private ArrayList<String> includedChromosomes;
//	private SNP selectedSNP = null;
//	
//	/**
//	 * @param gwasfr
//	 */
//	public PValueScatterPlot(GWASFileReader gwasfr) {
//		super("p-value scatter plot");
//		this.gwasfr = gwasfr;
//		
//		includedChromosomes = new ArrayList<String>();
//		for(int i = 0; i < gwasfr.getSNPs().size(); i++) {
//			SNP s = gwasfr.getSNPs().get(i);
//			if(!includedChromosomes.contains(s.getChromosome())) {
//				includedChromosomes.add(s.getChromosome());
//			}
//		}
//		
//		Collections.sort(includedChromosomes, HumanGenome.getChromosomeComparator());
//		
//		for(String s: includedChromosomes) {
//			genomeLength += HumanGenome.chromosomeLength(s);
//		}
//		
//		this.addMouseListener(this);
//	}
//
//	@Override
//	public void viewModelChanged(ViewModelEvent vme) {
//		//TODO react on these events
//		switch(vme.getChange()) {
//		case ViewModelEvent.EXPERIMENT_SELECTION_CHANGED: break;
//		case ViewModelEvent.PROBE_SELECTION_CHANGED: break;
//		}
//	}
//
//	@Override
//	public void paint(Graphics g1) {
//		super.paint(g1);
//		Graphics2D g = (Graphics2D)g1;
//		g.setColor(Color.BLACK);
//		AffineTransform af = g.getTransform();
//		Line2D xaxis = new Line2D.Double(0, 0, getWidth(), 0);
//		Line2D yaxis = new Line2D.Double(0, 0,0, getHeight());
//		double triangleSize = Math.min(getWidth(), getHeight()) * 0.01;
//		Shape t1 = createUpTriangle(triangleSize);
//		Shape t2 = createUpTriangle(triangleSize);
//		
//		g.translate(0, getHeight()*0.95);
//		g.draw(xaxis);
//		g.translate(getWidth()-triangleSize, 0);
//		g.rotate(Math.toRadians(90));
//		g.fill(t2);
//
//		g.setTransform(af);
//		g.translate(getWidth()*0.05, triangleSize);
//		g.fill(t1);
//		g.draw(yaxis);
//		g.setTransform(af);
//		
//		Color[] geneColors = GeneColors.rainbow(gwasfr.getGenes().size(), 0.8);
//		
//		double minPValue = -Math.log10(gwasfr.getSingleLocusResults().getMinPValue());
//		
//		double yrange = getHeight()*0.95 - triangleSize;
//		double xrange = getWidth()*0.95 - triangleSize;
//		
//		g.translate(getWidth()*0.05, 0);
//		
//		AffineTransform afData = g.getTransform();
//		
//		for(int i = 0; i < gwasfr.getGenes().size(); i++) {
//			SingleLocusResult slr = gwasfr.getSingleLocusResults().get(gwasfr.getGenes().getGene(i));
//			g.setColor(geneColors[i]);
//			for(int j = 0; j < slr.size(); j++) {
//				SNP s = gwasfr.getSNPs().get(j);
//				double pvalue = slr.get(s).p;
//				
//				if(pvalue == -1 || pvalue == Double.NaN) {
//					continue;
//				}
//				
//				double logPvalue = -Math.log10(pvalue);
//
//				String chromosome = s.getChromosome();
//				int positionOnChrom = s.getPosition();
//				
//				double ycoord = getHeight()*0.95-((logPvalue / minPValue) * yrange);
//				double xcoord = getXPosition(chromosome, positionOnChrom, xrange);
//				g.translate(xcoord, ycoord);
//				Stroke stroke = g.getStroke();
//				if(s.equals(selectedSNP)) {
//					g.setStroke(new BasicStroke(10));
//					g.drawOval(-dotDiameter/2, -dotDiameter/2, dotDiameter, dotDiameter);
//					g.setStroke(stroke);
//				}
//				g.drawOval(-dotDiameter/2, -dotDiameter/2, dotDiameter, dotDiameter);
//				g.setTransform(afData);
//			}
//		}
//		
//		g.setColor(Color.GRAY);
//		for(int i = 0; i < includedChromosomes.size(); i++) {
//			double position = 0;
//			for(int j = 0; j <= i; j++) {
//				position += HumanGenome.chromosomeLength(includedChromosomes.get(j));
//			}
//			position = (position / this.genomeLength) * xrange - triangleSize;
//			Line2D line = new Line2D.Double(position, triangleSize, position, 2*triangleSize+yrange);
//			g.draw(line);
//			
//			double chromLength = (HumanGenome.chromosomeLength(includedChromosomes.get(i)) / this.genomeLength) * xrange;
//			double labelX = position - chromLength/2;
//			double labelY = getHeight()*0.95;
//			
//			Rectangle2D labelBounds = g.getFontMetrics().getStringBounds(includedChromosomes.get(i), g);
//			
//			g.translate(labelX-labelBounds.getCenterX(), labelY+labelBounds.getHeight());
//			g.drawString(includedChromosomes.get(i), 0, 0);
//			g.setTransform(afData);
//		}
//	}
//	
//	private double getXPosition(String chromosome, int positionOnChrom, double xrange) {
//		int indexOfChromosome = includedChromosomes.indexOf(chromosome);
//		long baseOffset = this.getBaseOffset(indexOfChromosome);
//		double diff = (positionOnChrom+baseOffset) / this.genomeLength;
//		double position = diff * xrange;
//		
//		if(position > xrange + getWidth() * 0.1)
//			System.out.println(chromosome);
//		return position;
//	}
//	
//	/**
//	 * @param s
//	 * @return create a triangle of size s
//	 */
//	public static Shape createUpTriangle(final double s) {
//	      final GeneralPath p = new GeneralPath();
//	      p.moveTo(0.0f, -s);
//	      p.lineTo(s, s);
//	      p.lineTo(-s, s);
//	      p.closePath();
//	      return p;
//	}
//
//	@Override
//	public void mouseClicked(MouseEvent e) {
//		double x = e.getX() - getWidth()*0.05;
//		double xrange = getWidth() * 0.95 - Math.min(getWidth(), getHeight()) * 0.01;
//		long clickLocation = (int)Math.round((x / xrange) * this.genomeLength);
//		int chrIndex = getChromosomeIndex(clickLocation);
//		long baseOffset = getBaseOffset(chrIndex);
//		long locationInChr = clickLocation - baseOffset;
//		this.selectedSNP = getClosestSNP(locationInChr, includedChromosomes.get(chrIndex));
//		
//		System.out.println("Selected SNP: " + selectedSNP.getID());
//		System.out.println("On chromosome: " + selectedSNP.getChromosome());
//		System.out.println("At position: " + selectedSNP.getPosition());
//		System.out.println("Near gene: " + selectedSNP.getGene());
//		
//		repaint();
//	}
//
//	private SNP getClosestSNP(long locationInChr, String chromosome) {
//		SNPList snps = gwasfr.getSNPs();
//		SNP closest = null;
//		long distance = Long.MAX_VALUE;
//		
//		for(int i = 0; i < snps.size(); i++) {
//			SNP s = snps.get(i);
//			if(s.getChromosome().equals(chromosome)) {
//				long currentDistance = Math.abs(locationInChr - s.getPosition());
//				if(currentDistance < distance) {
//					distance = currentDistance;
//					closest = s;
//				}
//			}
//		}
//		return closest;
//	}
//
//	private long getBaseOffset(int chrIndex) {
//		long baseOffset = 0;
//		for(int i = 1; i <= chrIndex; i++) {
//			baseOffset += HumanGenome.chromosomeLength(includedChromosomes.get(i-1));
//		}
//		return baseOffset;
//	}
//
//	private int getChromosomeIndex(long clickLocation) {
//		long sum = clickLocation;
//		int i = 0;
//		while(sum > 0) {
//			sum -= HumanGenome.chromosomeLength(includedChromosomes.get(i++));
//		}
//		return i-1;
//	}
//
//	@Override
//	public void mousePressed(MouseEvent e) {}
//
//	@Override
//	public void mouseReleased(MouseEvent e) {}
//
//	@Override
//	public void mouseEntered(MouseEvent e) {}
//
//	@Override
//	public void mouseExited(MouseEvent e) {}
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
