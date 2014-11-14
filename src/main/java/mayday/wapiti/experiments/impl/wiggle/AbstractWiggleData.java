package mayday.wapiti.experiments.impl.wiggle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import mayday.core.io.csv.ParsedLine;
import mayday.core.io.csv.ParserSettings;
import mayday.core.math.average.IAverage;
import mayday.core.structures.natives.LinkedDoubleArray;
import mayday.genetics.ChromosomeBasedContainer;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.coordinatemodel.GBAtom;

public class AbstractWiggleData implements LocusData {
	
	protected ChromosomeBasedWiggleContainer wigData_fwd;
	protected ChromosomeBasedWiggleContainer wigData_bwd;
	protected ChromosomeBasedWiggleContainer wigData_both;
	protected ChromosomeSetContainer csc;
	protected LinkedDoubleArray[] cache;
	protected Chromosome cacheC;
	protected String species;		
		
	public AbstractWiggleData(List<String> input_fwd, List<String> input_bwd, List<String> input_both, String species) {
		this.csc=new ChromosomeSetContainer();
		this.species=species;
		wigData_fwd = new ChromosomeBasedWiggleContainer(csc);
		wigData_bwd = new ChromosomeBasedWiggleContainer(csc);
		wigData_both = new ChromosomeBasedWiggleContainer(csc);
		preparse(input_fwd, wigData_fwd);
		preparse(input_bwd, wigData_bwd);
		preparse(input_both, wigData_both);
	}
	
	protected static class ChromosomeBasedWiggleContainer extends ChromosomeBasedContainer<File> {
		public ChromosomeBasedWiggleContainer(ChromosomeSetContainer csc) {
			super(csc);
		}
		protected File getUnknown() {
			return null;
		}
	}

	public void preparse(List<String> files, ChromosomeBasedWiggleContainer wigData) {
		
		for (int pos=0; pos!=files.size(); ++pos) {
			String fname = files.get(pos);
			File f = new File(fname);
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(f));
				String line = br.readLine();
				
				// check if the file is a wiggle or a graph file
				String[] header = line.split("[\\s]+");			
				
				String chromosomeId = null;			
				
				for (String h : header) 
					if (h.startsWith("chrom")) 
						chromosomeId = h.substring("chrom=".length());
				
				if (chromosomeId==null)
					throw new RuntimeException("File contains no chromosome identifier: "+fname);
				
				br.close();
				
				wigData.updateLength(species, chromosomeId, getWiggleLength(f));
				wigData.add(f, species, chromosomeId);
				

			} catch (Exception e1) {
				if (e1 instanceof RuntimeException)
					throw (RuntimeException)e1;
				throw new RuntimeException("Could not read wiggle file:", e1);
			} 
		}
	}
	
	protected long getWiggleLength(File f) {

		long maxLen = 0;

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			
			String[] header = line.split("[\\s]+");
			boolean isVariable = true;
			
			long start=0;
			int step=1;
			int span=1;
			
			for (String h : header) {
				if (h.startsWith("start")) {
					start = Long.parseLong(h.substring("start=".length()));
				} else if (h.startsWith("step")) {
					step = Integer.parseInt(h.substring("step=".length()));
				} else if (h.startsWith("span")) {
					span = Integer.parseInt(h.substring("span=".length()));
				} else if (h.equals("variableStep")) {
					isVariable = true;
					line = null; // this file has a wiggle header, go to next line
				} else if (h.equals("fixedStep")) {
					isVariable = false;
					line = null; // this file has a wiggle header, go to next line 
				}
			}
			
			if (line!=null)
				throw new RuntimeException("File is not a wiggle file (no correct header found): "+f.getCanonicalPath());
			
			ParsedLine pl = new ParsedLine("", new ParserSettings());
			
			while ((line=br.readLine())!=null) {	
				if (isVariable) {
					pl.replaceLine(line);
					start = Long.parseLong(pl.get(0));
				} else {
					start+=step;					
				}
				maxLen = Math.max(maxLen, start+span-1);
			}

			br.close();
		
		} catch (Exception e) {
			System.err.println("Could not read wiggle file:");
			e.printStackTrace();
		} 

		return maxLen;
	}
	
	/** @returns 3 elements: forward, backward, both */
	public LinkedDoubleArray[] getWiggle(Chromosome c) {
		
		if (c==cacheC && cache!=null)
			return cache;
		
		cacheC=c;
		
		cache = new LinkedDoubleArray[]{
			readWiggle(wigData_fwd.get(c),c),
			readWiggle(wigData_bwd.get(c),c),
			readWiggle(wigData_both.get(c),c)
		};
		
		return cache;
		
	}
	
	protected LinkedDoubleArray readWiggle(File f, Chromosome c) {

		if (f==null)
			return null;
		
		LinkedDoubleArray data = null;
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			
			// is this a wiggle file?
			
			String[] header = line.split("[\\s]+");
			boolean isVariable = true;
			
			long start=0;
			int step=1;
			int span=1;
			
			for (String h : header) {
				if (h.startsWith("start")) {
					start = Long.parseLong(h.substring("start=".length()));
				} else if (h.startsWith("step")) {
					step = Integer.parseInt(h.substring("step=".length()));
				} else if (h.startsWith("span")) {
					span = Integer.parseInt(h.substring("span=".length()));
				} else if (h.equals("variableStep")) {
					isVariable = true;
				} else if (h.equals("fixedStep")) {
					isVariable = false;
				}
			}
			
			ParsedLine pl = new ParsedLine("", new ParserSettings());
			data = new LinkedDoubleArray(10000);
			data.ensureSize(c.getLength());
			
			if (isVariable) {
				while ((line=br.readLine())!=null) {		 
					pl.replaceLine(line);
					start = Long.parseLong(pl.get(0));
					double val = Double.NaN;
					try {
						val = Double.parseDouble(pl.get(1));
					} catch (Exception e) {}
					for (int i=span; i!=0; i--) 
						data.set(start+i-1, val);
				}
			} else {				
				while ((line=br.readLine())!=null) {		
					pl.replaceLine(line);
					double val = Double.NaN;
					try {
						val = Double.parseDouble(pl.get(0));
					} catch (Exception e) {}
					for (int i=span; i!=0; i--) 
						data.set(start+i-1, val);
					start+=step;
				}
			}

			br.close();

		
		} catch (Exception e) {
			System.err.println("Could not read wiggle file:");
			e.printStackTrace();
		} 

		return data;
		
	}

	public double getExpression(AbstractGeneticCoordinate locus, IAverage method) {
		
		LinkedDoubleArray[] ldas = getWiggle(locus.getChromosome());
		
		long length = locus.getCoveredBases();
		
		double[] vals = new double[(int)length];
		int vv = 0;
		
		for (GBAtom gba : locus.getCoordinateAtoms()) {
			LinkedDoubleArray lda = gba.strand==Strand.PLUS?ldas[0]:ldas[1];
			if (lda==null)
				lda = ldas[2];
			if (lda==null)
				return Double.NaN;
			for (long k = gba.from; k<=gba.to; ++k)
				vals[vv++] = lda.get(k);
		}
		
		return method.getAverage(vals);
	}

	@Override
	public ChromosomeSetContainer asChromosomeSetContainer() {		
		return csc;
	}

}
