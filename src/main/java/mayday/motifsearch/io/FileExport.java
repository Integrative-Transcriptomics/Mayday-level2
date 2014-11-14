package mayday.motifsearch.io;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  




import java.nio.channels.ByteChannel;  
import java.nio.channels.FileChannel;  
import java.util.ArrayList;

import mayday.motifsearch.preparation.FastaRepresentation;
import mayday.motifsearch.preparation.GeneLocation;

public class FileExport {

    /**
     * Creates a BufferedWriter
     * 
     * @author Frederik Weber
     * @return creates a BufferedWriter and  builds the directories if they do not exist 
     * @param absoluteFilePath
     *            The absolute Path to the File
     * @throws FileNotFoundException
     *             if the named file does not exist, is a directory rather than
     *             a regular file, or for some other reason cannot be opened for
     *             reading.
     * @throws IOException
     *             if If an I/O error occurs in writeline() method of
     *             BufferedWriter
     */

    public static final BufferedWriter fileWriter(String absoluteFilePath)
    throws FileNotFoundException, IOException {
	File file = new File(absoluteFilePath);
	if (!file.exists()){
	    new File(file.getParent()).mkdirs();
	    file.createNewFile();
	}
	BufferedWriter bw = new BufferedWriter(new FileWriter(file));
	return bw;

    }

    /**
     * @author Frederik Weber 
     * 
     *  @throws FileNotFoundException if the named file
     *         does not exist, is a directory rather than a regular file, or for
     *         some other reason cannot be opened for reading.
     * @throws IOException
     *             if If an I/O error occurs in BufferedWriter
     * 
     */
    public static final void writeFastaFileFromFastaRep(
	    FastaRepresentation fastaRep, String filePath)
    throws FileNotFoundException, IOException {

	/* open buffered file writer */
	BufferedWriter bw = FileExport.fileWriter(filePath);

	/* write the lines of header and sequences of the fasta representation*/
	for (int i = 0; i < fastaRep.size(); i++) {
	    bw.write(">" + fastaRep.getHeader(i).getContent());
	    bw.newLine();

	    StringBuffer sequence = new StringBuffer(fastaRep.getSequence(i));
	    /*consider FastaRepresentation.CHAR_LENGTH characters per line only*/
	    while (sequence.length() > FastaRepresentation.CHAR_LENGTH) {
		bw.write(sequence.substring(0, FastaRepresentation.CHAR_LENGTH));
		bw.newLine();
		sequence.delete(0, FastaRepresentation.CHAR_LENGTH);
	    }
	    /* write the last line of the sequences */
	    if (sequence.length() != 0) {
		bw.write(sequence.substring(0, sequence.length()));
		bw.newLine();
	    }


	}
	/* close writer */
	bw.close();
	System.out.println("writen file with path: " + filePath);
    }

    /**
     * @author Frederik Weber 
     * 
     * writes a sequence annotation file to a given path
     * 
     * @throws FileNotFoundException if the named file
     *         does not exist, is a directory rather than a regular file, or for
     *         some other reason cannot be opened for reading.
     * @throws IOException
     *             if If an I/O error occurs in BufferedWriter
     * 
     */
    public static final void writeSequencesAnnotationFileFromSequencesAnnotation(
	    SequencesAnntotation sequencesAnntotation, String filePath)
    throws FileNotFoundException, IOException {

	/* open buffered file writer */
	BufferedWriter bw2 = FileExport.fileWriter(filePath);

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

	ArrayList<GeneLocation> geneLocs = sequencesAnntotation.getGeneLocs();
	ArrayList<String> geneSequences = sequencesAnntotation.getGeneSequences();
	ArrayList<Long> takenFromPosition = sequencesAnntotation.getTakenFromPosition();
	ArrayList<Long> takenToPosition = sequencesAnntotation.getTakenToPosition();

	for (int i = 0; i < geneSequences.size(); i++) {
	    bw2.write(SequencesAnntotation.geneLocAndSequencestoXML(
		    geneLocs.get(i), 
		    geneSequences.get(i), 
		    takenFromPosition.get(i), 
		    takenToPosition.get(i)));
	    bw2.newLine();  
	}

	bw2.write("</sequences>");
	bw2.newLine();

	bw2.write("</data>");

	/* close writer */
	bw2.close();
	System.out.println("writen file path: " + filePath);
    }

    /**
     * @author Frederik Weber 
     * 
     * copys a file form an absolute path to an other and creates the necessary directories
     * 
     * @throws FileNotFoundException if the named file
     *         does not exist, is a directory rather than a regular file, or for
     *         some other reason cannot be opened for reading.
     * @throws IOException
     *             if If an I/O error occurs in BufferedWriter
     * 
     */
    public static void copy(File sourceFile, File destinationFile) throws IOException{
	if (!destinationFile.exists()){
	    new File(destinationFile.getParent()).mkdirs();
	    destinationFile.createNewFile();
	}
	FileInputStream fileInputStream = new FileInputStream(sourceFile);  
	FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);  
	FileChannel inputChannel = fileInputStream.getChannel();  
	FileChannel outputChannel = fileOutputStream.getChannel();  
	FileExport.transfer(inputChannel, outputChannel, sourceFile.length());  
	fileInputStream.close();  
	fileOutputStream.close();  
	destinationFile.setLastModified(sourceFile.lastModified());  
	System.out.println("copied file from path: "+ sourceFile.getAbsolutePath()+  "\n   to path: " + destinationFile.getAbsolutePath());
    }  

    private static void transfer(FileChannel fileChannel, ByteChannel byteChannel, long lengthInBytes)  
    throws IOException {  
	long chunckSizeInBytes = 1024 * 1024;
	long overallBytesTransfered = 0L;  
	while (overallBytesTransfered < lengthInBytes) {  
	    long bytesTransfered = 0L;  
	    bytesTransfered = fileChannel.transferTo(overallBytesTransfered, Math.min(chunckSizeInBytes, lengthInBytes - overallBytesTransfered), byteChannel);  
	    overallBytesTransfered += bytesTransfered;  
	}  
    }


}
