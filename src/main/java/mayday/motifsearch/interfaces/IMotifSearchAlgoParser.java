package mayday.motifsearch.interfaces;

import java.util.concurrent.ConcurrentHashMap;

import mayday.motifsearch.model.Motif;
import mayday.motifsearch.model.Sequence;


public interface IMotifSearchAlgoParser {
    
    /**
     * returns the final fully initialized Sequences with all full initialized Sites
     * 
     */
    public ConcurrentHashMap<String, Sequence> getFinalSequences();
    
    
    /**
     * returns all motifs that are be found in the data.
     * 
     */
    public ConcurrentHashMap<String, Motif> getMotifs(); 
    
    /**
     * determines if a folder path contains File which can be parsed
     * 
     */
    public boolean isParsableDataInFolderWithPath(String destFolderPath);
}
