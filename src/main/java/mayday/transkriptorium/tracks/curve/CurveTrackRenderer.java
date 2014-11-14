package mayday.transkriptorium.tracks.curve;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import mayday.genetics.advanced.chromosome.AbstractLocusChromosome;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.transkriptorium.data.MappingStore;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackRenderer;

public class CurveTrackRenderer extends AbstractTrackRenderer {

	protected MappingStore theData;
	
	protected int exp=-1;
	protected Chromosome chrome;
	protected double maxPLUS, maxMINUS;
	protected Boolean useMean;
	protected boolean redoMax = true;
	
	public CurveTrackRenderer(GenomeOverviewModel Model, AbstractTrackPlugin track) {
		super(Model,track);
	}

	public void updateInternalVariables() {
		CurveTrackSettings rts = ((CurveTrackSettings)tp.getTrackSettings());
		try {
			theData = rts.getMSM(rts.getExperiment());
		} catch (Exception e) {}
		Chromosome c = chromeModel.getActualChrome();
		if (exp!=rts.getExperiment() || chrome!=c ) {
			exp = rts.getExperiment();
			chrome = c;
			redoMax = true;
		}
		if (Boolean.valueOf(rts.useMean())!=useMean) {
			redoMax = true;
			useMean = rts.useMean();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void paint(Graphics g) {
		super.paint(g);
		
		if (theData==null)
			updateInternalVariables();

		String specID = chromeModel.getActualSpecies().getName();
		String chromeID = chromeModel.getActualChrome().getId();
		Chromosome c = theData.getCSC().getChromosome(SpeciesContainer.getSpecies(specID), chromeID);
		AbstractLocusChromosome alc = ((AbstractLocusChromosome)c);

		if (redoMax) {
			maxPLUS=0; maxMINUS=0;
			for (int i=0; i<=width; ++i) {
				DataMapper.getBpOfView(width, chromeModel, i, ftp);
				long start = ftp.getFrom();
				long end = ftp.getTo();
				if (start<0)
					start=0;
				if (end<0)
					end=0;
				if (end>c.getLength())
					end = c.getLength();
				if (start>c.getLength())
					start = c.getLength();
				double[] res = alc.computeCoverage(start, end, useMean);
				double sumPLUS = res[0];
				double sumMINUS = res[1];
				
				maxPLUS = maxPLUS>sumPLUS?maxPLUS:sumPLUS;
				maxMINUS = maxMINUS>sumMINUS?maxMINUS:sumMINUS;
			}
			redoMax = false;
		}
		
		boolean log = ((CurveTrackSettings)tp.getTrackSettings()).useLog();
		
		int h2 = height/2;
		
		g.setColor(new Color(0,0,0,50));
		g.drawLine(beg_x, h2, end_x, h2);
		g.drawLine(beg_x, height-1, end_x, height-1);
	
		int lastPLUS=0, lastMINUS=0;
		
		double mP = log? Math.log(maxPLUS+1)/Math.log(2) : maxPLUS;
		double mM = log? Math.log(maxMINUS+1)/Math.log(2) : maxMINUS;
		
		g.setColor(Color.black);
		for (int i=beg_x; i<=end_x; ++i) {
			ftp.clear();
			DataMapper.getBpOfView(width, chromeModel, i, ftp);
			long start = ftp.getFrom();
			long end = ftp.getTo();
			if (start<0)
				start=0;
			if (end<0)
				end=0;
			if (end>c.getLength())
				end = c.getLength();
			if (start>c.getLength())
				start = c.getLength();
			
			double[] res = alc.computeCoverage(start, end, useMean);
			double sumPLUS = res[0];
			double sumMINUS = res[1];
			
			if (log) {
				sumPLUS = (Math.log(sumPLUS+1)/Math.log(2));
				sumMINUS = (Math.log(sumMINUS+1)/Math.log(2));
			}

			double percPLUS = sumPLUS/mP;
			double percMINUS = sumMINUS/mM;
			
			sumPLUS = (int)(h2*percPLUS);
			sumMINUS = (int)(h2*percMINUS);
			
			g.setColor(Color.black);
			
//			g.drawLine(i, h2, i, h2-(int)sumPLUS);
//			g.drawLine(i, height, i, height-(int)sumMINUS);
//			
//			g.setColor(Color.red);
			
			if (i>beg_x) {
				g.drawLine(i-1, h2-lastPLUS, i, h2-(int)sumPLUS);
				g.drawLine(i-1, height-lastMINUS-1, i, height-(int)sumMINUS-1);
			} else {
				g.drawLine(i, h2-(int)sumPLUS, i, h2-(int)sumPLUS);
				g.drawLine(i, height-(int)sumMINUS-1, i, height-(int)sumMINUS-1);				
			}
			lastPLUS = (int)sumPLUS;
			lastMINUS = (int)sumMINUS;
		}
	}
		
	@SuppressWarnings("unchecked")
	public String getInformationAtMousePosition(Point point) {		
		
		if (theData==null)
			return null;
		
		String specID = chromeModel.getActualSpecies().getName();
		String chromeID = chromeModel.getActualChrome().getId();
		Chromosome c = theData.getCSC().getChromosome(SpeciesContainer.getSpecies(specID), chromeID);
		if (c==null )
			return null;
		
		DataMapper.getBpOfView(width, chromeModel, point.getX(),ftp);

		if (!ftp.isValid())
			return null;
		
		Strand s = Strand.PLUS;

		if (point.getY()>height/2)
			s = Strand.MINUS;
		
		
		String t = "<html>loc: ";
		
		if (ftp.getFrom() == ftp.getTo()) {
			t+= ftp.getFrom()+ " (" + 1 + "bp "+s+") ";
		} else {
			t+= ftp.getFrom()+ "-" + ftp.getTo() + " ("+ ((ftp.getTo() - ftp.getFrom() + 1)) + "bp "+s+")";
		}
		
		double[] res = ((AbstractLocusChromosome)c).computeCoverage(ftp.getFrom(), ftp.getTo(), useMean);
		
		t+="<br>";
		t+= (useMean? "Mean " : "Total ") + "coverage: "+ res[s==Strand.PLUS?0:1];
		
		return t;
	}
	
}
