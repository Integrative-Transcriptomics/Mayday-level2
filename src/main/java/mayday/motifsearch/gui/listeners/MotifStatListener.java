package mayday.motifsearch.gui.listeners;

import java.util.ArrayList;

/**
 * Listener that listens for changes within the numbers of motifs on
 * the sequences.
 * 
 *  @author Frederik Weber
 */

public interface MotifStatListener {

    public void setTransFactorStatistics(ArrayList<Integer> bList);
}
