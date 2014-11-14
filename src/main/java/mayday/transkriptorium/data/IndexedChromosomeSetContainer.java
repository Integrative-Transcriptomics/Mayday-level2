package mayday.transkriptorium.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.structures.CompactableStructure;
import mayday.core.structures.Pair;
import mayday.genetics.advanced.chromosome.LocusChromosomeLong;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateLong;
import mayday.genetics.basic.ChromosomeSet;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.coordinatemodel.GBNode;

public class IndexedChromosomeSetContainer extends ChromosomeSetContainer implements CompactableStructure { //, DumpableStructure {
	
	/** identifiers produced by this class are composed of two parts:
	 * - chromosome identifier 
	 * - chromosome-internal locus identifier
	 * the chromosome identifier is placed in the two least-significant bytes, the inner identifier is shifted to the left
	 * accordingly. Thus the value range needed is just two bytes more than the original range of the inner identifier.
	 * If the chromosome id was placed in the most significant bytes, we would always need the full long value range.
	 * 
	 *               InnerId  00xxxxxx
	 *             + ChromeID 000000cc
	 *           ==> OuterID  xxxxxxcc   
	 * */
	
	public IndexedChromosomeSetContainer() {
		super(new LocusChromosomeLong.Factory(), new ChromosomeSet.Factory());
	}
		
	/* Index structures for fast access */
	
	protected ArrayList<LocusChromosomeLong> chromosomeList = new ArrayList<LocusChromosomeLong>();	
	protected HashMap<Pair<String,String>, Integer> chromeMapping = new HashMap<Pair<String,String>, Integer>();
	
	
	protected LocusChromosomeLong mapOuterIDtoChromosome(long outerID) {
		int chromeID = (int)(outerID & 0xffff);
		return chromosomeList.get(chromeID);
	}
	
	protected long mapOuterIDtoInnerID(long outerID) {
		outerID = outerID >>> 16;  // drop out rightmost two bytes
		return outerID;
	}

	public LocusGeneticCoordinateLong getCoordinate(long outerID) {
		return chromosomeList.get( (int)(outerID & 0xffff) ).getCoordinate( outerID >>> 16 );
	}
	
	/* pass-through getters */
	
	public Species getSpecies(long id) {
		return mapOuterIDtoChromosome(id).getSpecies();
	}
	
	public LocusChromosomeLong getChromosome(long id) {
		return mapOuterIDtoChromosome(id);
	}
	
	
	public long getLength(long id){
		return getCoordinate(id).length();
	}


	public List<LocusGeneticCoordinateLong> getOverlappingLoci(Chromosome c, long startposition, long endposition, Strand strand) {
		return ((LocusChromosomeLong)getChromosome(c)).getOverlappingLoci(startposition, endposition, strand);
	}
	
	public List<LocusGeneticCoordinateLong> getOverlappingLoci(Chromosome c, GBNode model) {
		return ((LocusChromosomeLong)getChromosome(c)).getOverlappingLoci(model);
	}
	
	public List<LocusGeneticCoordinateLong> getOverlappingLociCluster(Chromosome c, long startposition, long endposition, Strand strand) {
		return ((LocusChromosomeLong)getChromosome(c)).getOverlappingLociCluster(startposition, endposition, strand);
	}
	
	protected long addLocus(Chromosome c, long startposition, long endposition, Strand strand, long mr_id) {
		Pair<String, String> pp = new Pair<String,String>(c.getSpecies().getName(), c.getId());
		Integer outer = chromeMapping.get(pp);
		Chromosome cc = getChromosome(c);
		if (outer==null) {
			chromeMapping.put(pp, outer = chromosomeList.size());
			chromosomeList.add((LocusChromosomeLong)cc);
			if (chromosomeList.size()>65535)
				throw new RuntimeException ("At most 2^16 chromosomes can be added to IndexedCSC");
		}		
		long innerID = ((LocusChromosomeLong)cc).addLocus(startposition, endposition, strand, mr_id);
		long outerID = (innerID << 16);
		outerID |= outer;
		return outerID;
	}

	@Override
	public void compact() {
		for (LocusChromosomeLong lcl : chromosomeList)
			lcl.compact();
	}

	
	protected void writeString(DataOutputStream dos, String s) throws IOException {
		dos.writeInt(s.length());
		dos.writeChars(s);
	}
	
	protected String readString(DataInputStream dis) throws IOException {
		int len = dis.readInt();
		StringBuffer sb = new StringBuffer();
		while (sb.length()<len)
			sb.append(dis.readChar());
		return sb.toString();
	}
//	
//	@Override
//	public void readDump(DataInputStream dis) throws IOException {
//		int chromeCount = dis.readInt();
//		for (int i=0; i!=chromeCount; ++i) {
//			String spec = readString(dis);
//			String chrid = readString(dis);
//			Pair<String, String> pp = new Pair<String,String>(spec, chrid);
//			Chromosome cc = getChromosome(SpeciesContainer.getSpecies(spec), chrid);
//			chromeMapping.put(pp, chromosomeList.size());
//			chromosomeList.add((LocusChromosomeLong)cc);
//			if (chromosomeList.size()>65535)
//				throw new RuntimeException ("At most 2^16 chromosomes can be added to IndexedCSC");
//			((LocusChromosomeLong)cc).readDump(dis);		
//		}		
//	}
//
//	@Override
//	public void writeDump(DataOutputStream dos) throws IOException {
//		dos.writeInt(chromosomeList.size());
//		for (LocusChromosomeLong lcl : chromosomeList) {
//			writeString(dos,lcl.getSpecies().getName());
//			writeString(dos, lcl.getId());
//			lcl.writeDump(dos);
//		}
//	}

	@Override
	public String getCompactionInitializer() {
		// we need a list of chromosomes as well as their compaction statistics
		String res = Integer.toString(chromosomeList.size());
		for (LocusChromosomeLong lcl : chromosomeList) {
			res+="\t"+lcl.getSpecies().getName()+"\t"+lcl.getId()+"\t"+lcl.getCompactionInitializer();
		}
		return res;
	}

	@Override
	public void setCompaction(LinkedList<String> compactionInitializer) {
		int chromeCount = Integer.parseInt(compactionInitializer.removeFirst());
		for (int i=0; i!=chromeCount; ++i) {
			String spec = compactionInitializer.removeFirst();
			String chrid = compactionInitializer.removeFirst();
			Pair<String, String> pp = new Pair<String,String>(spec, chrid);
			Chromosome cc = getChromosome(SpeciesContainer.getSpecies(spec), chrid);
			chromeMapping.put(pp, chromosomeList.size());
			chromosomeList.add((LocusChromosomeLong)cc);
			if (chromosomeList.size()>65535)
				throw new RuntimeException ("At most 2^16 chromosomes can be added to IndexedCSC");
			((LocusChromosomeLong)cc).setCompaction(compactionInitializer);
		}
	}	

	
}
