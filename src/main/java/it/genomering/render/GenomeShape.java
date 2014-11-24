package it.genomering.render;

import it.genomering.gui.GenomeRingPanel.VisualizationSetting;
import it.genomering.render.paths.ElementsPath;
import it.genomering.render.paths.GRPath;
import it.genomering.render.paths.GenomeFlags;
import it.genomering.render.paths.GenomePath;
import it.genomering.render.paths.GenomeSegments;
import it.genomering.structure.Genome;
import it.genomering.visconnect.ConnectionManager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.List;
import java.util.Set;

import mayday.Reveal.data.SNP;
import mayday.Reveal.viewmodel.RevealViewModel;
import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.genetics.Locus;
import mayday.genetics.LocusMIO;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.vis3.model.ViewModel;

/**
 * encapsulated all rendering elements for ONE genome:
 * - the path of jumps
 * - the segments on the supergenome blocks
 * - the start/end flags
 * @author battke
 *
 */
@SuppressWarnings("serial")
public class GenomeShape extends GRPath {

	protected GenomePath genomePath;
	protected GenomeFlags genomeFlags;
	protected GenomeSegments genomeSegments;
	protected ConnectionManager cm;
	
	protected RingDimensions rd;
	protected Genome g;
	
	private VisualizationSetting setting;
	
	public GenomeShape(Genome g, RingDimensions rd, ConnectionManager cm) {
		this.g = g;
		this.rd=rd;
		this.cm=cm;
		
		genomePath = new GenomePath(g, rd);
		genomeSegments = new GenomeSegments(g, rd);
		genomeFlags = new GenomeFlags(g, rd);

		setColor(g.getColor());
	}
	
	public void setColor(Color color) {
		Stroke stroke = getGenomeStroke(rd);
		genomePath.setStyle(color, stroke);
		genomeSegments.setStyle(color, stroke);		
		genomeFlags.setStyle(color, getFlagStroke(rd));
		this.color=color;
	}
	
	public double[] getCosts() {
		return genomePath.getCosts();
	}
	
	protected static Stroke getGenomeStroke(RingDimensions ringdim) {
		return new BasicStroke((float)ringdim.getGenomeWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);//, 0, new float[]{4f,6f,8f}, 0f);
	}
	
	protected static Stroke getMovingStroke(RingDimensions ringdim, int i) {
		return new BasicStroke((float)ringdim.getGenomeWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0, new float[]{60f,10f}, (float)i);
	}
	
	protected static Stroke getFlagStroke(RingDimensions ringdim) {
		return new BasicStroke((float)ringdim.getGenomeWidth()/4,BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
	}
	
	public void setSetting(VisualizationSetting setting) {
		this.setting = setting;
	}
	
	public void paint(Graphics2D g2d) {
		if (g.getColor()!=this.color) {
			setColor(g.getColor());
		}
		if (g.isVisible()) {
			if (DashMover.isActive()) {
				genomePath.setStyle(genomePath.getColor(), getMovingStroke(rd, DashMover.getPosition()));
			} else {
				genomePath.setStyle(genomePath.getColor(), getGenomeStroke(rd));
			}
			
			if(setting != null) {
				if(setting.getEnablePaths()) {
					genomePath.paint(g2d);
				}
			} else {
				genomePath.paint(g2d);
			}
			
			ViewModel vm = cm.getViewModel(g);
			
			if (vm!=null && setting.showAdditionalElements()) {
				// replace segment color by a brighter shade
				genomeSegments.setStyle(GenomeColors.pseudoAlphaColor(g.getColor(), Math.min(255,2*GenomePath.CONNECTOR_ALPHA)), getGenomeStroke(rd));
			}
			
			genomeSegments.paint(g2d);
			
			if (vm!=null && setting.showGenes()) {
				paintProbes(vm, g2d);
			}
			
			if(vm != null && setting.showSNVs()) {
				if(vm instanceof RevealViewModel)
					paintSNVs((RevealViewModel)vm, g2d);
			}
			
			
			
			genomeFlags.paint(g2d);
		}			
	}
	
	protected void paintProbes(ViewModel vm, Graphics2D g2d) {
		
		MIManager mim = vm.getDataSet().getMIManager();
		MIGroupSelection<LocusMIO> mgs = mim.getGroupsForInterface(LocusMIO.class);
		if (mgs.size()==0) 
			return;
		
		MIGroup locusMG = mgs.get(0); // TODO allow selection like in genomebrowser

		// paint probes // TODO allow color provider here
		Set<Probe> selectedProbes = vm.getSelectedProbes();
		
		Stroke elementStroke = getGenomeStroke(rd);
		
		for (Probe pb : vm.getProbes()) {
			if (!selectedProbes.contains(pb)) { // do not paint selected probes just yet
				AbstractGeneticCoordinate agc = getProbeCoordinate(pb, locusMG);
				if (agc!=null)
					paintProbe(pb, vm, agc, g2d, false, elementStroke);
			}
		}
		
		for (Probe pb : vm.getSelectedProbes()) {
			AbstractGeneticCoordinate agc = getProbeCoordinate(pb, locusMG);
			if (agc!=null)
				paintProbe(pb, vm, agc, g2d, true, elementStroke);
		}
	}
	
	protected void paintSNVs(RevealViewModel vm, Graphics2D g2d) {
		List<SNP> snps = vm.getTopPrioritySNPList();
		Set<SNP> selectedSNPs = vm.getSelectedSNPs();
		
		Stroke elementStroke = getGenomeStroke(rd);
		
		for(SNP snp : snps) {
			if(!selectedSNPs.contains(snp)) {
				paintSNV(snp, vm, g2d, false, elementStroke);
			}
		}
		
		for(SNP snp : selectedSNPs) {
			paintSNV(snp, vm, g2d, true, elementStroke);
		}
	}
	
	protected AbstractGeneticCoordinate getProbeCoordinate(Probe pb, MIGroup mg) {
		LocusMIO lm = (LocusMIO)mg.getMIO(pb);
		if (lm!=null) {
			Locus l = lm.getValue();
			AbstractGeneticCoordinate agc = l.getCoordinate();
			return agc;
		}
		return null;
	}
	
	protected void paintProbe(Probe pb, ViewModel vm, AbstractGeneticCoordinate locus, Graphics2D g2d, boolean selected, Stroke elementStroke) {
		//FIXME add color gradient paint
		Color c = selected?Color.RED:vm.getTopPriorityProbeList(pb).getColor();
		ElementsPath elementPath = new ElementsPath(g, rd); // TODO not very optimized to create new objects every time
		for (GBAtom a : locus.getModel().getCoordinateAtoms()) {
			// only draw objects that fit on my genome
			if (a.from<=g.getLastBase())
				elementPath.addElement((int)a.from, (int)a.to, null); 
		}
		elementPath.setStyle(c, elementStroke);
		elementPath.paint(g2d);
	}
	
	protected void paintSNV(SNP snp, RevealViewModel vm, Graphics2D g2d, boolean selected, Stroke elementStroke) {
		//make sure that the genomes fit
		if(snp.getChromosome().toLowerCase().equals(g.getName().toLowerCase())) {
			Color c = selected ? setting.getSNVSelectionColor() : setting.getSNVColor();
			ElementsPath elementPath = new ElementsPath(g, rd);
			//make sure that the snp fits on the genome
			if(snp.getPosition() <= g.getLastBase()) {
				System.out.println("SNPPos="+snp.getPosition());
				elementPath.addElement(snp.getPosition(), snp.getPosition()+setting.getSNPSizeFactor(), true);
			}
			
			elementPath.setStyle(c, elementStroke);
			elementPath.paint(g2d);
		}
	}
}
