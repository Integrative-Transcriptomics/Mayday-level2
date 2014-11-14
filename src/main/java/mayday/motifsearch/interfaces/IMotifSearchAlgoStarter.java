package mayday.motifsearch.interfaces;

import java.util.ArrayList;

import mayday.core.ProbeList;

public interface IMotifSearchAlgoStarter {
    
    /**
     * gets the execution command of a motif search algorithm 
     * with all its arguments to run in a command line
     * 
     */
    public String getExecCommand();
    
    
    /**
     * gives a unique String for the identification of a motif search 
     * algorithm
     * 
     */
    public String toString();
    
    /**
     * sets the absolute path for a FASTA file containing the sequences 
     * as an input for the motif search algorithm   
     * 
     * @param inputFASTAPath
     * 		the path to the input FASTA file
     * 
     */
    public void setInputFASTAPath(String inputFASTAPath);
    
    
    /**
     * sets the absolute path for the output folder of a motif search algorithm   
     * 
     * @param inputFASTAPath
     * 		a path for the output folder
     */
    public void setOutputFolderPath(String outputFolderPath);
    
    /*trivia methods to get information about the motif search algorithm */
    public String getName();
    public String getAuthors();
    public String getHomepage();
    public String getManualLink();
    
    
    public void addArgument(MotifSearchAlgoArgument motifSearchAlgoArgument) ;
    public ArrayList<MotifSearchAlgoArgument> getEditableArguments();
    
    /**
     * gets a standard output folder path where all input and output 
     * files from different runs of the same algorithm can be found 
     * 
     */
    public String getStandardOutputFolderPath();
    
    /**
     * gets the actual output folder path where all input and output 
     * files can be found.
     * 
     */
    public String getActualOutputFolderPath();
    
    /**
     * gets a dummy parser which is never executed.
     * 
     */
    public IMotifSearchAlgoParser getAlgoParserDummy();
    
    /**
     * runs an appropriate parser on files in an folder and returns it 
     * 
     * @param motifSearchAlgoOutputFolderPath
     * 		a path to files to parse for an appropriate parser 
     */
    public IMotifSearchAlgoParser getAlgoParser(String motifSearchAlgoOutputFolderPath, ProbeList allProbes) throws Exception;
    
    public IMotifSearchAlgoStarter clone();

}
