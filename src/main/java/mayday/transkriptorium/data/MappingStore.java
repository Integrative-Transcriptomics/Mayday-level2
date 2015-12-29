package mayday.transkriptorium.data;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mayday.core.structures.CompactableStructure;
import mayday.core.structures.maps.MMStringLongMap;
import mayday.core.structures.natives.MultiArray;
import mayday.core.structures.natives.MultiArray.ListIterator;
import mayday.core.structures.natives.mmap.MMBooleanArray;
import mayday.core.structures.natives.mmap.MMDoubleArray;
import mayday.core.structures.natives.mmap.MMFinalStringArray;
import mayday.core.structures.natives.mmap.MMLongArray;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateLong;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.basic.coordinate.GeneticCoordinate;

public class MappingStore implements CompactableStructure { //, DumpableStructure {

	private final static int MAX_READ_ID_LENGTH = 50;
	private final static int BS = 10000;

	
	// for each coord: store       compute
	// -- read id					-- read end
	// -- read start
	// -- target locus
	// -- mismatches
	protected MMLongArray coordinate_id; 							// maps mr-id to id in mapping_coordinates
	protected MMLongArray readID;		 							// maps mr-id to the read that is mapped
	protected MMLongArray startInRead;   							// maps mr-id to the start of the alignment in the read
	protected IndexedChromosomeSetContainer mapping_coordinates;	// maps coordinate_id to coordinates, maps coordinates to mr-id
	protected MMDoubleArray quality;  	 							// maps mr-id to quality value (e.g. mismatches)
		
	// for each read
	// -- store if mapped uniquely
	// -- if yes: store id of coord
	// -- if no:  store index into list additionalCoordinates
	protected MMBooleanArray read_maps_unique;						// maps read-id to whether it is mapped uniquely
	protected MMLongArray mrID_or_additionalMappingCoordinatesIndex;// maps read-id to EITHER mapping-coordinates-id OR index in additionalMappingCoordinates
																	// CAUTION: This is stored "+1" to allow for negative marker (shifted -1 == 0) 
	protected MultiArray additionalMappingCoordinates;				// maps read-id to (multiple) mr-id
	protected MMLongArray matePair; 								// maps read-id to mate pair read-id
	protected MMFinalStringArray readName; 							// maps read-id to read name (from fasta header for instance)
	
	protected double minQuality = Double.POSITIVE_INFINITY;
	protected double maxQuality = Double.NEGATIVE_INFINITY;
	
	// these are needed for compact()ing
	protected int maxStartInRead=0;
	protected long maxCoordinateID=0;
	
	protected int bytes_read_id = 8; 	 		//number of bytes needed to store all possible read identifiers
	protected int bytes_mrID_or_additionalCoordinates = 8;  	 		//number of bytes needed to store all possible mapping (mr) identifiers	
	protected int bytes_coords_id = 8;   		//number of bytes needed to store all possible mapping-coordinate identifiers
	protected int bytes_startInRead = 4;		//number of bytes needed to store all possible mapping-coordinate identifiers
	protected int bytes_read_name = MAX_READ_ID_LENGTH;
	protected int observed_bytes_read_name = 0;
	protected boolean canCompact = true;
	
	// cache during filling
	protected MMStringLongMap readNameCache;
	
	protected void init() {
		// create structures based on known compaction		
		coordinate_id = new MMLongArray(BS, bytes_coords_id);
		readID = new MMLongArray(BS, bytes_read_id);
		if (bytes_startInRead>0)
			startInRead = new MMLongArray(BS, bytes_startInRead);
		readNameCache = new MMStringLongMap(bytes_read_name+1); // this is only needed during filling
		mrID_or_additionalMappingCoordinatesIndex = new MMLongArray(BS, bytes_mrID_or_additionalCoordinates);
		
		// create structures for later compaction
		mapping_coordinates = new IndexedChromosomeSetContainer();

		// create non-compactable structures
		additionalMappingCoordinates = new MultiArray(5);
		quality = new MMDoubleArray(BS);
		read_maps_unique = new MMBooleanArray(BS);
		readName = new MMFinalStringArray(BS,false);
	}
	
	public MappingStore(LinkedList<String> compactionInitializer) {
		setCompaction(compactionInitializer);
		init();
	}
	
	public MappingStore(int readIDBytes, int mrIDBytes, int coordIDBytes, int SIRbytes, int maxReadIDLength) {
		bytes_read_id = readIDBytes;
		bytes_mrID_or_additionalCoordinates = mrIDBytes;
		bytes_coords_id = coordIDBytes;
		bytes_startInRead = SIRbytes;
		bytes_read_name = maxReadIDLength;
		init();
	}
	
	public MappingStore() {
		this(8,8,8,4,MAX_READ_ID_LENGTH);
	}
	
	public IndexedChromosomeSetContainer getCSC() {
		return mapping_coordinates;
	}
	
	public ARead getRead(String identifier) {
		return new ARead(readNameCache.get(identifier));
	}
	
	/**
	 * add a read with a given identifer
	 * @param read the identifer (fasta header)
	 * @return the read-id
	 */
	public long addRead(String read) {
		observed_bytes_read_name=observed_bytes_read_name>read.length()?observed_bytes_read_name:read.length();
		
		long readIdentifier = read_maps_unique.size();
		read_maps_unique.set(readIdentifier, true);
		mrID_or_additionalMappingCoordinatesIndex.add(0);
		// store the read name
		readName.add(read);
		readNameCache.put(read,readIdentifier);
		// check if partnered read
		// -- we use the convention that partnered reads' ids end in "/1" and "/2"
		if (read.length()>2 && read.charAt(read.length()-2)=='/') {
			String partnerName = read.substring(0,read.length()-1)+ (read.charAt(read.length()-1)=='1'?'2':'1');
			Long partnerIdentifier = readNameCache.get(partnerName);
			if (partnerIdentifier!=null) {
				if (matePair==null) {
					System.out.println("Data seems to contain mate pairs, adding extra storage.");
					matePair = new MMLongArray(BS, bytes_read_id);
				}
				matePair.ensureSize(readIdentifier+1);
				matePair.ensureSize(partnerIdentifier+1);
				matePair.set(readIdentifier, partnerIdentifier+1);
				matePair.set(partnerIdentifier, readIdentifier+1);
			}
		}
		
		return readIdentifier;
	}
	
	/** add a mapping position for a read
	 * 
	 * @param read the Name (Fasta Identifier) of the read (add "/1" and "/2" to identify mate pairs)
	 * @param mappedPosition the coordinates of the mapping
	 * @param quality the quality of the mapping
	 * @param startInRead the position in the read where the alignment begins (alignment length is in the coordinate)
 	 * @return the mr-id of the added mapped read
	 */
	public long addMappedRead(String read, AbstractGeneticCoordinate mappedPosition, double quality, int startInRead) {
		if (readNameCache == null)
			throw new RuntimeException("Cannot add reads to mappingstore after compact() has been called");
			
		// first translate read name to id
		Long readIdentifier = readNameCache.get(read);		
		
		// if read not present, add it
		if (readIdentifier == null) {
			readIdentifier = addRead(read);
		}
		
		return addMappedRead(readIdentifier, mappedPosition, quality, startInRead);
	}
		
	protected long addMappedRead(long readIdentifier, AbstractGeneticCoordinate mappedPosition, double quality, int startInRead) {

		minQuality = minQuality<quality?minQuality:quality;
		maxQuality = maxQuality>quality?maxQuality:quality;
		maxStartInRead = maxStartInRead>startInRead?maxStartInRead:startInRead;

		// store data for this mapping
		long mr_id = this.quality.add(quality);
		long newCoordID = mapping_coordinates.addLocus(mappedPosition.getChromosome(), mappedPosition.getFrom(), mappedPosition.getTo(), mappedPosition.getStrand(), mr_id);
		if (bytes_startInRead!=0)
			this.startInRead.add(startInRead);		
		this.coordinate_id.add(newCoordID);
		maxCoordinateID=maxCoordinateID>newCoordID?maxCoordinateID:newCoordID;
				
		long currentCoordinate = mrID_or_additionalMappingCoordinatesIndex.get(readIdentifier)-1;
		
		// add coordinate
		if (currentCoordinate == -1) { // no coordinate yet
			mrID_or_additionalMappingCoordinatesIndex.set(readIdentifier, mr_id+1);
		} else {
			long addtlList;
			// only one coordinate
			if (read_maps_unique.get(readIdentifier)) {
				// set nonunique
				read_maps_unique.set(readIdentifier, false);
				// move old coord ID to new list of coords
				addtlList = additionalMappingCoordinates.createList();
				long old_mr_id = mrID_or_additionalMappingCoordinatesIndex.get(readIdentifier)-1;
				mrID_or_additionalMappingCoordinatesIndex.set(readIdentifier, addtlList+1);
				additionalMappingCoordinates.add(addtlList, old_mr_id);
			} else {
				addtlList = mrID_or_additionalMappingCoordinatesIndex.get(readIdentifier)-1;
			}
			// add new coord
			additionalMappingCoordinates.add(addtlList, mr_id); 
		}
		
		// fill in the coordinate info
		readID.add(readIdentifier);
					
		return mr_id;
	}
	
	public MappedRead getMappedRead(long id) {
		return new AMappedRead(id);
	}
	
	public long getTotalMappingCount() {
		return readID.size();
	}
	
	public long getTotalReadCount() {
		return readName.size();
	}
	
	public List<MappedRead> getOverlappingReads(Chromosome c, long startposition, long endposition, Strand strand) {
		Collection<LocusGeneticCoordinateLong> inner = mapping_coordinates.getOverlappingLoci(c, startposition, endposition, strand);
		ArrayList<MappedRead> result = new ArrayList<MappedRead>(inner.size());
		for (LocusGeneticCoordinateLong l : inner)
			result.add(new AMappedRead(l.getValue()));
		return result;
	}
	
	public List<MappedRead> getOverlappingReadsCluster(Chromosome c, long startposition, long endposition, Strand strand) {
		Collection<LocusGeneticCoordinateLong> inner = mapping_coordinates.getOverlappingLociCluster(c, startposition, endposition, strand);
		ArrayList<MappedRead> result = new ArrayList<MappedRead>(inner.size());
		for (LocusGeneticCoordinateLong l : inner)
			result.add(new AMappedRead(l.getValue()));
		return result;
	}
	

	@Override
	public String getCompactionInitializer() {
		return bytes_read_id+"\t"
				+bytes_mrID_or_additionalCoordinates+"\t"
				+bytes_startInRead+"\t"
				+bytes_coords_id+"\t"
				+observed_bytes_read_name+"\t"
				+mapping_coordinates.getCompactionInitializer()+"\t"
				+additionalMappingCoordinates.getCompactionInitializer()+"\t"
				+readName.getCompactionInitializer();
	}

	@Override
	public void setCompaction(LinkedList<String> compactionInitializer) {
		bytes_read_id = Integer.parseInt(compactionInitializer.removeFirst());
		bytes_mrID_or_additionalCoordinates = Integer.parseInt(compactionInitializer.removeFirst());
		bytes_startInRead = Integer.parseInt(compactionInitializer.removeFirst());
		bytes_coords_id = Integer.parseInt(compactionInitializer.removeFirst());
		bytes_read_name = Integer.parseInt(compactionInitializer.removeFirst());
		init();
		compact0();
		mapping_coordinates.setCompaction(compactionInitializer);
		additionalMappingCoordinates.setCompaction(compactionInitializer);
		readName.setCompaction(compactionInitializer);
	}
	
	
	/** apply compaction computed by compact() or set by setCompaction() */
	protected void compact0() {
		// --- bytes_read_id
		readID = readID.changeStorageBytes(bytes_read_id,true);
		if (matePair!=null)
			matePair = matePair.changeStorageBytes(bytes_read_id,true);		
		// --- bytes_mr_id...
		mrID_or_additionalMappingCoordinatesIndex = mrID_or_additionalMappingCoordinatesIndex.changeStorageBytes(bytes_mrID_or_additionalCoordinates, true);		
		
		// --- bytes_startInRead
		if (startInRead!=null) {
			if (bytes_startInRead>0)
				startInRead = startInRead.changeStorageBytes(bytes_startInRead,true);
			else {
				startInRead.finalize();
				startInRead = null; //drop this completely
			}
		}
		// --- bytes_coords_id 
		coordinate_id = coordinate_id.changeStorageBytes(bytes_coords_id, true);
		
		// --- bytes_read_name ---  will not be used here because it is only important for the constructor
		canCompact = false;
	}
	
	/** compute compaction and apply it */
	public void compact() {
		if (readNameCache!=null)
			readNameCache.finalize();
		readNameCache = null;
		
		if (!canCompact)
			return;
		
		// use smaller "long" sizes if possible
		// The number of unique read identifiers is equal to the number of read names
		int new_bytes_read_id = (int)Math.ceil((Math.log(readName.size())/Math.log(2)) /8); //bytes needed to store highest read id
		
		// the highest ID for mapping_coordinates as stored in IndexedCSC mapping_coordinates
		int new_bytes_coords_id = (int)Math.ceil((Math.log(maxCoordinateID)/Math.log(2)) /8);
		
		int new_bytes_startInRead = maxStartInRead==0?0:(int)Math.ceil((Math.log(maxStartInRead)/Math.log(2)) /8); // bytes needed to store highest startinread
		
		/* the highest value in mrID_or_.... is given by the maximum of:
		 * - the highest mr-id (this is given by readID.size() as shown above
		 * - the highest list identifier in additionalCoordinates (this is _at_most_ the 3*readIDs.size().
		 */
		long b = additionalMappingCoordinates.getHighestListIdentifier();
		b = (int)Math.ceil((Math.log(b)/Math.log(2)) /8);
		int new_bytes_mrID_or_additionalCoordinates = (int)Math.max(new_bytes_read_id,b); 
		
		bytes_read_id = new_bytes_read_id;
		bytes_mrID_or_additionalCoordinates = new_bytes_mrID_or_additionalCoordinates;
		bytes_startInRead = new_bytes_startInRead;		
		bytes_coords_id = new_bytes_coords_id;
		
		compact0();
		
		mapping_coordinates.compact();
		additionalMappingCoordinates.compact();
		readName.compact();
		
	}
	
	public class ARead implements Read {
		
		protected long read_id;
		
		public ARead(long id) {
			read_id = id;
		}
		
		public boolean hasUniqueMapping() {
			return read_maps_unique.get(read_id);
		}
		
		public Iterator<MappedRead> getAllMappings() {
			if (hasUniqueMapping())
				return new SingleMappingIterator(read_id);
			else
				return new MultiMappingIterator(read_id);
		}
		
		public String getIdentifier() {
			return readName.get(read_id);
		}

		public Read getPartner() {
			if (matePair!=null) {
				long pId = matePair.get(read_id)-1;
				if (pId>-1)
					return new ARead(pId);
			}
				
			return null;
		}
	}
	
	public class AMappedRead implements MappedRead {
		
		protected long mr_id;
		
		public AMappedRead(long id) {
			mr_id=id;
		}
		
		protected void setID(long id) {
			mr_id = id;
		}
		
		public long getInternalID() {
			return mr_id;
		}

		@Override
		public int getEndInRead() {
			long c_id = coordinate_id.get(mr_id);
			int alignmentLength = (int) mapping_coordinates.getLength(c_id);
			return getStartInRead()+alignmentLength-1; 
		}

		@Override
		public long getReadID() {
			return readID.get(mr_id);
		}
		
		public String getReadIdentifier() {
			return readName.get(getReadID());
		}

		@Override
		public int getStartInRead() {
			return startInRead==null?0:(int)startInRead.get(mr_id);
		}

		@Override
		public AbstractGeneticCoordinate getTargetCoordinate() {
			long c_id = coordinate_id.get(mr_id);
			return mapping_coordinates.getCoordinate(c_id);
		}

		@Override
		public boolean isUniqueMapping() {
			return read_maps_unique.get(getReadID());
		}

		@Override
		public double quality() {
			return quality.get(mr_id);
		}

		@Override
		public Iterator<MappedRead> getAllReadMappings() {
			if (isUniqueMapping())
				return new SingleMappingIterator(getReadID());
			else
				return new MultiMappingIterator(getReadID());
		}
		
		public String toString() {
			return toString(true);
		}
		
		public String toString(boolean allMappings) {
			String ret = getReadID()+": "+getStartInRead()+"---"+getEndInRead()+"   "+quality()+" mm\n"+
				getTargetCoordinate()+"\n"+
				"Unique: "+isUniqueMapping()+"\n";
			if (allMappings && !isUniqueMapping()) {
				Iterator<MappedRead> mri = getAllReadMappings();
				while (mri.hasNext())
					ret+= ((AMappedRead)mri.next()).toString(false);
			}
			return ret;					
		}
		
		public ARead getRead() {
			return new ARead(getReadID());
		}
		
		public boolean equals(Object other) {
			if (other instanceof AMappedRead)
				return ((AMappedRead)other).mr_id==mr_id;
			return super.equals(other);
		}
		
		public int hashCode() {
			return (int)(mr_id ^ (mr_id >>> 32));
		}
	}
	
	public class SingleMappingIterator implements Iterator<MappedRead> {
		protected MappedRead theMR;
		public SingleMappingIterator(long readid) {
			long index = mrID_or_additionalMappingCoordinatesIndex.get(readid);
			theMR = new AMappedRead(index);			
		}
		@Override
		public boolean hasNext() {
			return theMR!=null;
		}
		@Override
		public MappedRead next() {
			MappedRead mr = theMR;
			theMR=null;
			return mr;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();			
		}
		
	}
	
	public class MultiMappingIterator implements Iterator<MappedRead> {
		protected ListIterator li;
		
		public MultiMappingIterator(long readid) {
			long index = mrID_or_additionalMappingCoordinatesIndex.get(readid)-1;
			li = additionalMappingCoordinates.getList(index);
		}

		@Override
		public boolean hasNext() {
			return li.hasNext();
		}

		@Override
		public MappedRead next() {
			return new AMappedRead(li.next());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
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
	
	
	public void write(DataOutputStream dos) throws IOException {
		// write compaction status info
		writeString(dos, getCompactionInitializer());
		
		// Write reads
		long numberReads = readName.size();
		dos.writeLong(numberReads);
		for (long i=0; i!=numberReads; ++i) 
			writeString(dos, readName.get(i));

			
		//Write spec/chrome pairs
		HashMap<Chromosome, Integer> chromeMap = new HashMap<Chromosome, Integer>(); 		
		int ci = 0;
		Collection<Chromosome> lc = mapping_coordinates.getAllChromosomes();
		int numChromes = lc.size();
		dos.writeChar(numChromes);  // optimized: use 2 byte char instead of 4 byte int here, 65k chromosomes should be enough
		for (Chromosome c : lc) {
			chromeMap.put(c, ci++);
			writeString(dos, c.getSpecies().getName());
			writeString(dos, c.getId());
		}
		
		//Write mappings
		long numberMappings = readID.size();
		dos.writeLong(numberMappings);
		for (long i=0; i!=numberMappings; ++i) {
			dos.writeLong(readID.get(i));
			dos.writeDouble(quality.get(i));			
			dos.writeInt(startInRead==null?0:(int)startInRead.get(i));
			// write the coordinate
			long coordid = coordinate_id.get(i);
			AbstractGeneticCoordinate agc = mapping_coordinates.getCoordinate(coordid);
			int chromeID = chromeMap.get(agc.getChromosome());
			dos.writeChar(chromeID); // optimized: use 2 byte char instead of 4 byte int here, 65k chromosomes should be enough
			dos.writeLong(agc.getFrom());
			dos.writeLong(agc.getTo());
			dos.writeByte(agc.getStrand().ordinal()); // optimized: use 1 byte instead of 4 byte int here, this is still a 400% overhead
		}
		
		dos.flush();
	}
	
//	public void writeDump(DataOutputStream dos) throws IOException {
//		// write reads
//		readName.writeDump(dos);
//		mrID_or_additionalMappingCoordinatesIndex.writeDump(dos);		
//		additionalMappingCoordinates.writeDump(dos);
//		read_maps_unique.writeDump(dos);
//		if (matePair!=null) {
//			dos.writeBoolean(true);
//			matePair.writeDump(dos);
//		} else {
//			dos.writeBoolean(false);
//		}
//		
//		mapping_coordinates.writeDump(dos);
//
//		// dump all other elements
//		coordinate_id.writeDump(dos);
//		readID.writeDump(dos);
//		
//		if (startInRead!=null) {
//			dos.writeBoolean(true);
//			startInRead.writeDump(dos);
//		} else {
//			dos.writeBoolean(false);
//		}
//			
//		quality.writeDump(dos);
//		
//		// write some other things
//		dos.writeDouble(minQuality);
//		dos.writeDouble(maxQuality);
//	}
//	
//	public void readDump(DataInputStream dis) throws IOException {
//		// read all dumps
//		this.readName.readDump(dis);
//		this.mrID_or_additionalMappingCoordinatesIndex.readDump(dis);
//		this.additionalMappingCoordinates.readDump(dis);
//		read_maps_unique.readDump(dis);
//		if (dis.readBoolean()) {
//			matePair = new MMLongArray(BS);
//			matePair.readDump(dis);
//		}
//		
//		mapping_coordinates.readDump(dis);
//		
//		coordinate_id.readDump(dis);
//		readID.readDump(dis);
//		if (dis.readBoolean()) 
//			startInRead.readDump(dis);
//		else
//			startInRead = null;
//		quality.readDump(dis);
//		
//		minQuality = dis.readDouble();
//		maxQuality = dis.readDouble();
//		
//		canCompact = false;
//	}
	
	public void read(DataInputStream dis, int version) throws IOException {
		if (version>=1) {
			String compactionString = readString(dis);
			LinkedList<String> compactionParts = new LinkedList<String>(Arrays.asList(compactionString.split("\t")));
			setCompaction(compactionParts);
		}
		
		//read reads
		long numberReads = dis.readLong();
		for (long i=0; i!=numberReads; ++i) {
			String readName = readString(dis);
			addRead(readName);
		}
		
		//read spec/chrome pairs
		HashMap<Integer, Chromosome> chromeMap =new HashMap<Integer, Chromosome>();
		ChromosomeSetContainer csc = new ChromosomeSetContainer();		
		int numChromes = version==0?dis.readInt():(int)dis.readChar();  // optimized: use 2 byte char instead of 4 byte int here, 65k chromosomes should be enough
		for (int i=0; i!=numChromes; ++i) {
			String spec = readString(dis);
			String id = readString(dis);
			Chromosome c = csc.getChromosome(SpeciesContainer.getSpecies(spec), id);
			chromeMap.put(i,c);
		}

		// read mappings
		long numberMappings = dis.readLong();
		for (long i=0; i!=numberMappings; ++i) {
			long readID = dis.readLong();
			double qual = dis.readDouble();
			int start = dis.readInt();
			int chromeID = version==0?dis.readInt():(int)dis.readChar(); // optimized: use 2 byte char instead of 4 byte int here, 65k chromosomes should be enough
			Chromosome chrome = chromeMap.get(chromeID);
			long from = dis.readLong();
			long to = dis.readLong();
			int strandID = version==0?dis.readInt():(int)dis.readByte(); // optimized: use 1 byte instead of 4 byte int here, this is still a 400% overhead
			Strand strand = Strand.values()[strandID];
			GeneticCoordinate gc = new GeneticCoordinate(chrome, strand, from, to);
			addMappedRead(readID, gc, qual, start);
		}
		
		compact();
		
	}
	
	public double getMinQuality() {
		return minQuality;
	}
	
	public double getMaxQuality() {
		return maxQuality;
	}


}
