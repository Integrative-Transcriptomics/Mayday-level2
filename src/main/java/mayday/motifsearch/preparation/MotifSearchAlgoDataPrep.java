package mayday.motifsearch.preparation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mayday.core.ProbeList;
import mayday.core.tasks.AbstractTask;
import mayday.motifsearch.io.FileExport;
import mayday.motifsearch.io.FileImport;
import mayday.motifsearch.io.SequencesAnntotation;

/**
 * Class that handles the preparation of Sequences 
 * and other data used for a motif search algorithm
 * 
 * @author Frederik Weber
 */

public class MotifSearchAlgoDataPrep extends AbstractTask{


    public boolean canceled = false;
    public boolean isDataPreparedForMoSuAl = false;

    private File[] fastaFiles; 
    private File[] PTTFiles;
    private List<ProbeList> probeLists; 
    private int searchUpstreamATGLength;
    private int searchDownstreamIncludingATGMaxLength;
    private int minUpstreamLength;
    private boolean isSafeExtractionMode; 
    private String sequencesFilePath;
    private String sequencesAnntationFilePath; 
    private String separateSequencesFilePath;
    private boolean saveResultFASTASeparately;

    public MotifSearchAlgoDataPrep(String title, File[] fastaFiles, File[] files,
	    List<ProbeList> probelists, int searchUpstreamATGLength,
	    int searchDownstreamIncludingATGMaxLength, int minUpstreamLength,
	    boolean isSafeExtractionMode, String sequencesFilePath,
	    String sequencesAnntationFilePath,
	    String separateSequencesFilePath, boolean saveResultFASTASeparately) {
	super(title);
	this.fastaFiles = fastaFiles;
	PTTFiles = files;
	this.probeLists = probelists;
	this.searchUpstreamATGLength = searchUpstreamATGLength;
	this.searchDownstreamIncludingATGMaxLength = searchDownstreamIncludingATGMaxLength;
	this.minUpstreamLength = minUpstreamLength;
	this.isSafeExtractionMode = isSafeExtractionMode;
	this.sequencesFilePath = sequencesFilePath;
	this.sequencesAnntationFilePath = sequencesAnntationFilePath;
	this.separateSequencesFilePath = separateSequencesFilePath;
	this.saveResultFASTASeparately = saveResultFASTASeparately;
    }

    /**
     * prepares data used for a motif 
     * search algorithm and copies data automatic in files to different locations.
     * 
     * @author Frederik Weber
     */
    public final boolean prepareDataForMoSuAl(){
	try {
		
		ProbeList allProbes = ProbeList.createUniqueProbeList(probeLists);
		
	    ChromosomeSet chromosomeSet = new ChromosomeSet();
	    ArrayList<FastaRepresentation> fastaReps = new ArrayList<FastaRepresentation>();

	    for(File fastaFile: fastaFiles){
		fastaReps.add(FileImport
			.fileToFastaRepresentation(fastaFile, false, true));
	    }

	    ArrayList<PTTRepresentation> PTTReps = new ArrayList<PTTRepresentation>();
	    for(File PTTFile:  PTTFiles){
		PTTReps.add(FileImport
			.fileToPPTRepresentation(PTTFile));
	    }

	    for(FastaRepresentation fastaRep:  fastaReps){

		/* turn fasta representation to chromosome and add to chromosome set */
		chromosomeSet.addFromFastaRep(fastaRep);

		/* get the actual index */
		int actIndex = chromosomeSet.size()-1;

		/* set chromosome's gene locations to corresponding PTT representation gene locations */
		chromosomeSet.get(actIndex)
		.setGeneLocationHashMap(
			PTTReps.get(actIndex).getGeneLocationHashMap());

		/* add chromosome gene locations to chromosomes set gene locations */
		chromosomeSet.putAllGeneLocationsFromHashMap(chromosomeSet.get(actIndex).getGeneLocationHashMap());
	    }
	    /* write FASTA file with sequences for motif search */
	    this.setProgress(0);
	    SequencesAnntotation sequencesAnntotation = new SequencesAnntotation();
	    sequencesAnntotation.setDownstreamLength(searchDownstreamIncludingATGMaxLength);
	    sequencesAnntotation.setUpstreamLength(searchUpstreamATGLength);
	    sequencesAnntotation.setMinUpstreamLength(minUpstreamLength);

	    FastaRepresentation.createGenesFastaRepAndFillSequencesAnnotation(
		    chromosomeSet, 
		    sequencesAnntotation,
		    allProbes,
		    searchUpstreamATGLength,
		    searchDownstreamIncludingATGMaxLength,
		    minUpstreamLength,
		    isSafeExtractionMode, true, sequencesFilePath, sequencesAnntationFilePath, this);

	    /*if there is a copy to be stored separately*/
	    if (saveResultFASTASeparately){
		try {
		    FileExport.copy(new File(sequencesFilePath), new File(separateSequencesFilePath));
		    FileExport.copy(new File(sequencesAnntationFilePath), new File(new File(separateSequencesFilePath).getParent() + java.io.File.separator + "SequencesAnnotation.xml"));
		}
		catch (Exception e) {
		    System.err.print(e);
		}
	    }


	}
	catch (Exception e) {
	    System.out.print(e.getMessage());
	    return false;
	}
	return true;
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void doWork() throws Exception {
    	this.isDataPreparedForMoSuAl = this.prepareDataForMoSuAl();
    }

    @Override
    public void doCancel() {
	this.canceled = true;
    }
}
