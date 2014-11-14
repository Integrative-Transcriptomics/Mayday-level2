package mayday.motifsearch.model;

import java.awt.Color;
import java.util.*;

import mayday.core.gui.GUIUtilities;

public class MotifColorer {
    
    /**
     * colors a list of motifs by seting their color. 
     * every motif gets a different color.
     * 
     * @param motifs a list of motifs which colors should be set.
     * 		
     */
    public static final List<Motif> colorizeMotifs(List<Motif> motifs){

	Color[] rb = GUIUtilities.rainbow(motifs.size(), 0.75);

	for (int i=0; i<motifs.size(); ++i) {
	    motifs.get(i).setColor(rb[i]);
	}

	return motifs;

    }


}
