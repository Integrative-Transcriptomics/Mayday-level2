package mayday.motifsearch.gui.listeners;

import java.util.*;

import mayday.motifsearch.model.Motif;

/**
 * Listener for Changes in the TF Table
 * 
 * @author Frederik Weber
 * 
 */

public interface MotifChangeListener {

    public void motifChanged(ArrayList<Motif> selectedMotifs);
}
