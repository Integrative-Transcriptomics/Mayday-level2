package mayday.motifsearch.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import mayday.motifsearch.preparation.FastaRepresentation;
import mayday.motifsearch.preparation.GeneLocation;
import mayday.motifsearch.preparation.Header;
import mayday.motifsearch.preparation.PTTRepresentation;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nux.xom.xquery.ResultSequence;
import nux.xom.xquery.XQuery;
import nux.xom.xquery.XQueryException;

/**
 * This class contains all necessary import function to import from relevant
 * files for the motif search plugin
 * 
 * @author Frederik Weber
 */
public class FileImport {

    /**
     * Creates a BufferedReader
     * 
     * @author Frederik Weber
     * @return a BufferedReader
     * @param absoluteFilePath
     *            The absolute Path to the File
     * @throws FileNotFoundException
     *             if the named file does not exist, is a directory rather than
     *             a regular file, or for some other reason cannot be opened for
     *             reading.
     * @throws IOException
     *             if If an I/O error occurs in readline() method of Buffered
     *             reader
     */

    public static final BufferedReader FileReader(File absoluteFilePath)
    throws FileNotFoundException, IOException {

	Reader in = new FileReader(absoluteFilePath); // may throw
	// FileNotFoundException
	BufferedReader bin = new BufferedReader(in);

	return bin;

    }

    /**
     * extracts Information and Data from a PTT file given by a file path
     * 
     * @author Frederik Weber
     * @return Representation of a PTT file format
     * @param absoluteFilePath
     *            The absolute Path to the File
     * @throws FileNotFoundException
     *             if the named file does not exist, is a directory rather than
     *             a regular file, or for some other reason cannot be opened for
     *             reading.
     * @throws IOException
     *             if If an I/O error occurs in readline() method of Buffered
     *             reader
     */

    public static final PTTRepresentation fileToPPTRepresentation(
	    File absoluteFilePath)
    throws FileNotFoundException, IOException {
	/* creates a PTT representation to store the information */
	PTTRepresentation PTTRep = new PTTRepresentation();
	BufferedReader bin = FileImport.FileReader(absoluteFilePath); // creates the
	// BufferedReader
	String actReadedLine = ""; // stores a readed line of file

	/*
	 * read in line by line, store actual line in actReadedLine until the
	 * file has no more lines
	 */
	// counts the actual line that is read
	int actLineCounter = 0;
	while ((actReadedLine = bin.readLine()) != null) { // may throw
	    // IOException
	    actReadedLine = actReadedLine.trim(); // trim the string

	    actLineCounter++;

	    /* header begins * */
	    if (actLineCounter <= 3) {
		if (actReadedLine.startsWith("#")){
		    actReadedLine = actReadedLine.substring(1);  
		}

		switch (actLineCounter) {
		    case 1:
			PTTRep.setDescription(actReadedLine);
			break;
		    case 2:

			PTTRep.setNumberProteins(Integer.valueOf(actReadedLine
				.split(" ")[0]));
			break;
		    case 3:
			PTTRep.setColumnHeaders(actReadedLine
				.split("\t"));
			break;
		    default:
			System.out
			.println("unexpected header line in file, may be more than 3 headerlines in ptt file.");
		    break;

		}
	    }
	    else if (actReadedLine.length() != 0) {
			/* */
			String tempLineSplitted[] = actReadedLine.split("\u0009");
			String tempLocationSplitted[] = tempLineSplitted[0]
			                                                 .split("\\.\\.");
			GeneLocation geneLoc = new GeneLocation(
				Integer.valueOf(tempLocationSplitted[0]),
				Integer.valueOf(tempLocationSplitted[1]),
				(tempLineSplitted[1].equals("+")),// .(compareTo("+")==
				// 0),
				(tempLineSplitted[2].equals("-") ? 0 : Integer.valueOf(tempLineSplitted[2])),
				(tempLineSplitted[3].equals("-") ? "" : tempLineSplitted[3]),
				(tempLineSplitted[4].equals("-") ? "" : tempLineSplitted[4]),
				(tempLineSplitted[5].equals("-") ? "" : tempLineSplitted[5]),
				(tempLineSplitted[6].equals("-") ? "" : tempLineSplitted[6]),
				(tempLineSplitted[7].equals("-") ? "" : tempLineSplitted[7]),
				(tempLineSplitted[8].equals("-") ? "" : tempLineSplitted[8]));
			PTTRep.addGeneLocation(geneLoc);
	    }
	}

	/* close reader */
	bin.close();
	return PTTRep;
    }

    /**
     * extracts codes of genes in a file given by a file path
     * 
     * @author Frederik Weber
     * @return ArrayList<String> of codes of genes
     * @param absoluteFilePath
     *            The absolute Path to the File
     * @throws FileNotFoundException
     *             if the named file does not exist, is a directory rather than
     *             a regular file, or for some other reason cannot be opened for
     *             reading.
     * @throws IOException
     *             if If an I/O error occurs in readline() method of Buffered
     *             reader
     */

    public static final ArrayList<String> fileToGeneSynonymArrayList(
	    File absoluteFilePath)
	    throws FileNotFoundException, IOException {
	/* creates a fasta representation to store the information */
	ArrayList<String> codes = new ArrayList<String>();
	BufferedReader bin = FileImport.FileReader(absoluteFilePath); // creates the
	// BufferedReader
	String actReadedLine = ""; // stores a readed line of file

	/*
	 * read in line by line, store actual line in actReadedLine until the
	 * file has no more lines
	 */
	while ((actReadedLine = bin.readLine()) != null) { // may throw
	    // IOException

	    actReadedLine = actReadedLine.trim(); // trim the string

	    /* read one line */
	    if (actReadedLine.length() != 0) {
		codes.add(actReadedLine);
	    }

	}
	/* close reader */
	bin.close();
	return codes;
    }

    /**
     * extracts headers and sequences from a Fasta file given by a file path
     * 
     * @author Frederik Weber
     * @return Representation of header an sequence of a Fasta file format
     * @param FASTAFile
     *            The absolute Path to the File
     * @throws FileNotFoundException
     *             if the named file does not exist, is a directory rather than
     *             a regular file, or for some other reason cannot be opened for
     *             reading.
     * @throws IOException
     *             if If an I/O error occurs in readline() method of Buffered
     *             reader
     */
    public static final FastaRepresentation fileToFastaRepresentation(
	    File FASTAFile, boolean loadSequencesInMemory, boolean loadHeadersInMemory)
    throws FileNotFoundException, IOException {
	/* creates a fasta representation to store the information */
	FastaRepresentation fastaRep = new FastaRepresentation(FASTAFile);

	if (loadSequencesInMemory ||loadHeadersInMemory){
	    BufferedReader bin = FileImport.FileReader(FASTAFile); // creates the
	    // BufferedReader

	    String actReadedLine = ""; // stores a readed line of file


	    if (loadSequencesInMemory && loadHeadersInMemory) {
		StringBuffer sequenceParts = null; // contains the parts of a sequences

		/*
		 * read in line by line, store actual line in actReadedLine until the
		 * file has no more lines
		 */
		while ((actReadedLine = bin.readLine()) != null) { // may throw
		    // IOException

		    actReadedLine = actReadedLine.trim(); // trim the string

		    /* header begins * */
		    if (actReadedLine.startsWith(">")) {

			fastaRep.addHeader(new Header(actReadedLine.substring(1)));

			/* checks if there was a sequence setted; */
			if (sequenceParts != null) {
			    fastaRep.addSequnce(sequenceParts.toString());

			    /* clear sequence parts */
			    sequenceParts = new StringBuffer();
			}
		    }
		    else if (!(actReadedLine.length() == 0)
			    || actReadedLine.startsWith(";")) {

			/* initiate the sequenceParts the first time */
			if (sequenceParts == null) {
			    sequenceParts = new StringBuffer();
			}
			sequenceParts.append(actReadedLine.trim());
		    }
		}

		/* eof read the last sequence */
		if (sequenceParts != null)
			fastaRep.addSequnce(sequenceParts.toString());


	    }else if (loadHeadersInMemory){
		/*
		 * read in line by line, store actual line in actReadedLine until the
		 * file has no more lines
		 */
		while ((actReadedLine = bin.readLine()) != null) { // may throw
		    // IOException

		    actReadedLine = actReadedLine.trim();

		    /* header begins * */
		    if (actReadedLine.startsWith(">")) {

			fastaRep.addHeader(new Header(actReadedLine.substring(1)));

		    }
		}

	    }
	    bin.close();
	}
	return fastaRep;
    }

    /**
     * extracts annotation information from a sequence annotation File 
     * 
     * @author Frederik Weber
     * @return sequence Annotation
     * @param AnnotaionFile
     *            The absolute Path to the File
     */
    public static final SequencesAnntotation fileToSequencesAnntotation(
	    File AnnotaionFile)
    throws FileNotFoundException, IOException, ValidityException, ParsingException, XQueryException{
	/* creates a FASTA representation to store the information */
	SequencesAnntotation sequenceAnnotaion = new SequencesAnntotation();

	Document document = new Builder().build(AnnotaionFile);

	XQuery xquery = new XQuery("//upstreamParameter", null);
	ResultSequence resultSequence = xquery.execute(document);
	sequenceAnnotaion.setUpstreamLength(Integer.valueOf(resultSequence.toNodes().get(0).getValue()));

	xquery = new XQuery("//minUpstreamParameter", null);
	resultSequence = xquery.execute(document);
	sequenceAnnotaion.setMinUpstreamLength(Integer.valueOf(resultSequence.toNodes().get(0).getValue()));

	xquery = new XQuery("//downstreamParameter", null);
	resultSequence = xquery.execute(document);
	sequenceAnnotaion.setDownstreamLength(Integer.valueOf(resultSequence.toNodes().get(0).getValue()));


	xquery = new XQuery("//sequence", null);
	resultSequence = xquery.execute(document);

	Nodes sitesNodes = resultSequence.toNodes();
	int numSitesSequence = sitesNodes.size();

	for (int j = 0; j<numSitesSequence; j++){

	    GeneLocation geneLoc =  new GeneLocation(
		    Integer.valueOf(sitesNodes.get(j).query("@geneFromPos").get(0).getValue()),
		    Integer.valueOf(sitesNodes.get(j).query("@geneToPos").get(0).getValue()), 
		    (sitesNodes.get(j).query("@strand").get(0).getValue().equals("+")?true:false),
		    Integer.valueOf(sitesNodes.get(j).query("@length").get(0).getValue()),
		    sitesNodes.get(j).query("@PID").get(0).getValue(),
		    sitesNodes.get(j).query("@geneOriginName").get(0).getValue(),
		    sitesNodes.get(j).query("@geneOriginSynonym").get(0).getValue(),
		    sitesNodes.get(j).query("@code").get(0).getValue(),
		    sitesNodes.get(j).query("@COG").get(0).getValue(),
		    sitesNodes.get(j).query("@product").get(0).getValue());

	    sequenceAnnotaion.addGeneLocAndGeneSequence(
		    geneLoc, 
		    sitesNodes.get(j).getValue(),
		    Long.valueOf(sitesNodes.get(j).query("@takenfromposition").get(0).getValue()),
		    Long.valueOf(sitesNodes.get(j).query("@takentoposition").get(0).getValue()));

	}
	return sequenceAnnotaion;
    }

}
