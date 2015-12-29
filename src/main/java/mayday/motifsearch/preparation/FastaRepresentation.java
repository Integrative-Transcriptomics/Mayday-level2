package mayday.motifsearch.preparation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.motifsearch.io.FileExport;
import mayday.motifsearch.io.FileImport;
import mayday.motifsearch.io.SequencesAnntotation;
import mayday.motifsearch.tool.DNASequenceUtils;

/**
 * Representation of header and sequence of a FASTA file format
 * 
 * @author Frederik Weber
 */
public class FastaRepresentation {

    private Vector<Header> headers;
    private Vector<String> sequences;
    private File FASTAFile;
    public static long count = 0;
    public static int CHAR_LENGTH = 80;

    public File getFASTAFile() {
	return FASTAFile;
    }


    private static final String getNextValidSequenceLine(BufferedReader bin) 
    throws FileNotFoundException, IOException {
	String tempLine = null;
	if (bin != null){
	    tempLine = bin.readLine();
	    if (tempLine != null){
		tempLine = tempLine.trim();
	    }
	    while ((tempLine != null) && ((tempLine.length() == 0) || (tempLine.startsWith(">") || tempLine.startsWith(";")))) {
		tempLine = bin.readLine();
		if (tempLine != null){
		    tempLine = tempLine.trim();
		}
	    } 
	} 
	return tempLine;
    }


    public static final FastaRepresentation createGenesFastaRepAndFillSequencesAnnotation(ChromosomeSet chromosomeSet, SequencesAnntotation sequencesAnntotation, ProbeList allProbes, int additionalUpstreamLength, int maxDownstreamLength,int minUpstreamLength, boolean isSafeExtractionMode, boolean isMemSaveMode, String filePath, String filePath2, MotifSearchAlgoDataPrep motifSearchAlgoDataPrep) 
    throws FileNotFoundException, IOException {

	/* creates a fasta representation to store the information */
	FastaRepresentation genesFastaRep = new FastaRepresentation();

	BufferedWriter bw = null;
	BufferedWriter bw2 = null;

	if(isMemSaveMode){
	    bw = FileExport.fileWriter(filePath);
	    bw2 = FileExport.fileWriter(filePath2);

	    bw2.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
	    bw2.newLine();


	    bw2.write("<data>");
	    bw2.newLine();

	    bw2.write("<upstreamParameter>"+sequencesAnntotation.getUpstreamLength() +"</upstreamParameter>");
	    bw2.newLine();

	    bw2.write("<minUpstreamParameter>"+sequencesAnntotation.getMinUpstreamLength() +"</minUpstreamParameter>");
	    bw2.newLine();

	    bw2.write("<downstreamParameter>"+sequencesAnntotation.getDownstreamLength() +"</downstreamParameter>");
	    bw2.newLine();

	    bw2.write("<sequences>");
	    bw2.newLine();


	    bw2.flush();
	}
	try{
	    /* genome sequence */
		
	    for (Probe p : allProbes) {
	    	String synonym = p.getDisplayName();
			GeneLocation geneLocation = chromosomeSet.getGeneLocationBySynonym(synonym);
			if (geneLocation != null) {
			    geneLocation.getChromosome().addUnsortedTempNeededgeneLocation(geneLocation);
			} else {
			    System.out.println("could not find gene with Synonym " + synonym + " in given Data(PTT files)");
			    FastaRepresentation.count++;
			}
	    }

	    /*Buffer for read up and downstream*/
	    int upDownStreamLengthReadBufferLength = Math.max(additionalUpstreamLength, maxDownstreamLength);

	    /* creates nulled BufferedReader */
	    BufferedReader bin = null; 
	    String actReadedLine = null; // stores a readed line of file

	    int iterCount = 0;
	    chromosomeSet.sortChomosomesGeneLocations(GeneticCoordinateComparator.FIRST_OCCURRENCE_FIRST);
	    for (Chromosome chromosome : chromosomeSet) {
		bin = FileImport.FileReader(chromosome.getSequenceFASTAFile());
		int currentOffsetSequencePart = 0;
		int currentEndSequencePart = 0;

		ArrayList<String> ReadLineBuffer = new ArrayList<String>();

		for (GeneLocation geneLoc : chromosome.getTempNeededgeneLocationArrayList()) {
		    /*
		     * read in line by line, store actual line in actReadedLine until the
		     * file has no more lines
		     */

		    iterCount++;
		    motifSearchAlgoDataPrep.setProgress((int)(((double)iterCount*10000)/((double)chromosomeSet.getGeneLocationHashMap().size())));
		    if (motifSearchAlgoDataPrep.canceled){
			return genesFastaRep;
		    }

		    if (currentEndSequencePart == 0){
			actReadedLine = FastaRepresentation.getNextValidSequenceLine(bin);

			/* read until first occurrence of start of gene location */
			while ((actReadedLine != null) 
				&& (((geneLoc.getFrom() -1 - upDownStreamLengthReadBufferLength)) > currentOffsetSequencePart + actReadedLine.length() )) { 

			    actReadedLine = FastaRepresentation.getNextValidSequenceLine(bin);
			    currentOffsetSequencePart += actReadedLine.length();

			}
			if (actReadedLine != null){
			    /* save line with first occurrence of gene*/
			    ReadLineBuffer.add(0, actReadedLine);

			    /* set to fist sequence character offset*/
			    currentEndSequencePart = currentOffsetSequencePart + actReadedLine.length();
			}

			/* read until last occurrence of end of gene location */
			while ((actReadedLine != null) 
				&& (geneLoc.getTo() + upDownStreamLengthReadBufferLength > currentEndSequencePart )) { 

			    actReadedLine = FastaRepresentation.getNextValidSequenceLine(bin);
			    if (actReadedLine != null){
				ReadLineBuffer.add(actReadedLine);
				currentEndSequencePart += actReadedLine.length();
			    }

			}
		    } else {

			while ((!ReadLineBuffer.isEmpty())
				&& (((geneLoc.getFrom() - 1 - upDownStreamLengthReadBufferLength)) > currentOffsetSequencePart + ReadLineBuffer.get(0).length())) { 
			    currentOffsetSequencePart += ReadLineBuffer.get(0).length();
			    ReadLineBuffer.remove(0);
			}

			if (ReadLineBuffer.isEmpty()){

			    actReadedLine = FastaRepresentation.getNextValidSequenceLine(bin);

			    /* read until first occurrence of start of gene location */
			    while ((actReadedLine != null) 
				    && (((geneLoc.getFrom() -1 - upDownStreamLengthReadBufferLength)) > currentOffsetSequencePart + actReadedLine.length() )) { 
				actReadedLine = FastaRepresentation.getNextValidSequenceLine(bin);
				if (actReadedLine != null){
				    currentOffsetSequencePart += actReadedLine.length();
				}

			    }
			    /* save line with first occurrence of gene*/
			    if (actReadedLine != null){
				ReadLineBuffer.add(0, actReadedLine);
				/* set to fist sequence character offset*/
				currentEndSequencePart = currentOffsetSequencePart + actReadedLine.length();
			    }

			    /* read until last occurrence of end of gene location */
			    while ((actReadedLine != null) 
				    && (geneLoc.getTo() + upDownStreamLengthReadBufferLength > currentEndSequencePart )) { 

				actReadedLine = FastaRepresentation.getNextValidSequenceLine(bin);
				if (actReadedLine != null){
				    ReadLineBuffer.add(actReadedLine);
				    currentEndSequencePart += actReadedLine.length();
				}

			    }

			} else {

			    /* read until last occurrence of end of gene location */
			    while ((actReadedLine != null) 
				    && (geneLoc.getTo() + upDownStreamLengthReadBufferLength > currentEndSequencePart )) { 

				actReadedLine = FastaRepresentation.getNextValidSequenceLine(bin);
				if (actReadedLine != null){
				    ReadLineBuffer.add(actReadedLine);
				    currentEndSequencePart += actReadedLine.length();
				}

			    }
			}


		    }

		    String tmpSequencePart = "";
		    for (String sequenceParts: ReadLineBuffer){
			tmpSequencePart = tmpSequencePart.concat(sequenceParts);
		    }

		    /* get the extracted sequence part*/
		    try {
			int actuallTakenUpstream = geneLoc.checkInterferenceUpstream(additionalUpstreamLength, isSafeExtractionMode, minUpstreamLength); 
			int actuallTakenDownstream = geneLoc.checkInterferenceDownstream(maxDownstreamLength, isSafeExtractionMode); 
			String tempAddSequence = DNASequenceUtils.substringFromSectionSequence(
				tmpSequencePart, 
				geneLoc.getFrom() - currentOffsetSequencePart, 
				geneLoc.getTo() - currentOffsetSequencePart, 
				actuallTakenUpstream, 
				actuallTakenDownstream, 
				!geneLoc.isPlusStrand());

			/* add header and sequence of extracted sequence regions to FASTA-File  */
			FastaRepresentation.genesFastaRepAddHeaderAndSequence(
				genesFastaRep, 
				(new Header(geneLoc.getSynonym()
					+ " Code: " + geneLoc.getCode()
					+ " PID: " + geneLoc.getPID() + " length: "
					+ geneLoc.getLengthProtein() + " Product: "
					+ geneLoc.getProduct() + " Strand: "
					+ (geneLoc.isPlusStrand()? "+": "-"))),
					(tempAddSequence.isEmpty()?"A":tempAddSequence),isMemSaveMode, bw); // add no empty sequence because some motif search algorithms need not empty sequences

			if (isMemSaveMode && bw2!=null){
			    bw2.write(SequencesAnntotation.geneLocAndSequencestoXML(
				    geneLoc, 
				    (tempAddSequence.isEmpty()?"A":tempAddSequence), 
				    (geneLoc.isPlusStrand()? geneLoc.getFrom() - actuallTakenUpstream: geneLoc.getTo()- actuallTakenDownstream), 
				    (geneLoc.isPlusStrand()? geneLoc.getFrom() + actuallTakenDownstream: geneLoc.getTo() + actuallTakenUpstream)));
			    bw2.newLine();  
			    bw2.flush();
			} else {
			    /*add Inforamtion to Annotation*/
			    sequencesAnntotation.addGeneLocAndGeneSequence(geneLoc, (tempAddSequence.isEmpty()?"A":tempAddSequence),// add no empty sequence because some motif search algorithms need not empty sequences
				    (geneLoc.isPlusStrand()? geneLoc.getFrom() - actuallTakenUpstream: geneLoc.getTo()- actuallTakenDownstream),
				    (geneLoc.isPlusStrand()? geneLoc.getFrom() + actuallTakenDownstream: geneLoc.getTo() + actuallTakenUpstream));
			}

		    } catch (Exception e) {
			System.out.println(e);
		    }
		}
		bin.close();
	    }


	    /* close writer */
	    if(bw != null){
		bw.close();
		System.out.println("writen file path: " + filePath);
	    }
	    if(bw2 != null){
		bw2.write("</sequences>");
		bw2.newLine();

		bw2.write("</data>");
		bw2.close();
		System.out.println("writen file path: " + filePath2);
	    }
	} catch (Exception e) {
	    System.out.println(e);

	}
	return  genesFastaRep;
    }


    /**
     * add to a new fasta representation
     * 
     */
    private static final void genesFastaRepAddHeaderAndSequence(FastaRepresentation genesFastaRep, Header header, String geneSequence, boolean isMemSaveMode, BufferedWriter bw)throws FileNotFoundException, IOException {
	if (isMemSaveMode){
	    genesFastaRep.addHeaderAndSequence( header,  geneSequence,  isMemSaveMode,  bw);
	} else {
	    genesFastaRep.addHeaderAndSequence(header
		    , geneSequence); 
	}


    }

    /**
     * Constructs an simple Representation with sorted header and sequence
     * Vectors of String
     * 
     * @param headers
     *            the headers corresponding to the sequences
     * @param sequences
     *            the sequences corresponding to the headers
     */
    public FastaRepresentation(Vector<Header> headers, Vector<String> sequences) {
	if (headers.size() != sequences.size()) {
	    throw new RuntimeException("headers do not match sequences");
	}
	this.headers = headers;
	this.sequences = sequences;
    }

    /**
     * Constructs an simple Representation with empty header and sequence
     * Vectors of String
     * 
     */
    public FastaRepresentation() {
	this.headers = new Vector<Header>();
	this.sequences = new Vector<String>();
    }

    /**
     * Constructs an simple Representation with empty header and sequence
     * Vectors of String
     * 
     */
    public FastaRepresentation(File FASTAFile) {
	this.FASTAFile = FASTAFile;
	this.headers = new Vector<Header>();
	this.sequences = new Vector<String>();
    }



    /**
     * clears the representation
     * 
     */
    public void clear() {
	this.headers.clear();
	this.sequences.clear();
    }

    /**
     * converts the class into a String representation
     * 
     */
    @Override
    public String toString() {
	String s = new String();
	for (int i = 0; i < this.size(); i++) {
	    s += "Name: " + this.getHeader(i).toString() + "; Sequence: "
	    + this.getSequence(i) + "\n";
	}
	return s;
    }

    /**
     * gets number of headers listed
     * 
     */
    public int size() {
	return this.headers.size();
    }

    /**
     * adds header
     * 
     * @param header
     *            the String of a header
     */
    public void addHeader(Header header) {
	this.headers.add(header);
    }

    /**
     * adds sequence
     * 
     * @param sequence
     *            the String of a sequence
     */
    public void addSequnce(String sequence) {
	this.sequences.add(sequence);
    }

    /**
     * adds header and corresponding sequence
     * 
     * @param sequence
     *            the String of a sequence
     */
    public void addHeaderAndSequence(Header header, String sequence) {
	this.headers.add(header);
	this.sequences.add(sequence);
    }

    /**
     * adds header and corresponding sequence in memory save mode
     * 
     * @param sequence
     *            the String of a sequence
     */
    public void addHeaderAndSequence(Header header, String sequence, boolean isMemSaveMode, BufferedWriter bw)  throws FileNotFoundException, IOException {
	/* write the lines of header and sequences of the fasta representation*/
	bw.write(">" + header.getContent());
	bw.newLine();

	StringBuffer sequenceBuffer = new StringBuffer(sequence);

	/*consider FastaRepresentation.CHAR_LENGTH characters per line only*/
	while (sequenceBuffer.length() > FastaRepresentation.CHAR_LENGTH) {
	    bw.write(sequenceBuffer.substring(0, FastaRepresentation.CHAR_LENGTH));
	    bw.newLine();
	    sequenceBuffer.delete(0, FastaRepresentation.CHAR_LENGTH);
	}
	/* write the last line of the sequences */
	if (sequenceBuffer.length() != 0) {
	    bw.write(sequenceBuffer.substring(0, sequenceBuffer.length()));
	    bw.newLine();
	}   
	bw.flush();

    }

    /**
     * gets header at corresponding index
     * 
     * @param index
     *            the index the header should be returned
     */
    public Header getHeader(int index) {
	return this.headers.get(index);
    }

    /**
     * gets sequence at corresponding index
     * 
     * @param index
     *            the index the sequence should be returned
     */
    public String getSequence(int index) {
	return this.sequences.get(index);
    }

}
