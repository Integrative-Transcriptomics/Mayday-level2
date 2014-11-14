package mayday.motifsearch.gui.visual;
/**
 * This class handles the Highlighting of motif and theri sites
 * 
 * @author Frederik Weber
 */
import java.util.ArrayList;

import javax.swing.JComponent;

import mayday.motifsearch.model.Motif;
import mayday.motifsearch.model.Site;

public class SiteSelectionModel {


    /* a list containing the motif that should be highlighted */
    private ArrayList<Motif>  motifHighLightList = new ArrayList<Motif>();

    private boolean isMulitHighlightingEnabled = false;

    /*
     * a list containing the components that should be repainted after a change
     * in this model
     */
    private ArrayList<JComponent> repainterList = new ArrayList<JComponent>();

    /**
     * sends a list of sites that contain motif that
     * should be highlighted if the sended site list is not empty the
     * model fires a change
     * 
     * @see #fireChanged() if some of motifs are already in the
     *      model these will be removed from the list of the motifs that should be highlighted if not it is added to be
     *      highlighted
     * 
     * @param source
     *                the source that sends the highlight information
     * @param sites
     *                a list of sites that contain motifs
     *                that should be highlighted
     * @author Frederik Weber
     */
    public void siteSelected(Object source, ArrayList<Site> sites) {
	if (!this.isMulitHighlightingEnabled) {
	    for (Site s : sites) {
		Motif motif = s.getMotif();
		if (this.motifHighLightList.contains(motif)) {
		    this.motifHighLightList.remove(motif);
		} else {
		    this.motifHighLightList.clear();
		    this.motifHighLightList.add(motif); 
		}

		this.fireChanged();
	    }
	}
	else {
	    for (Site s : sites) {
		Motif motif = s.getMotif();
		if (this.motifHighLightList.contains(motif)) {
		    this.motifHighLightList.remove(motif);
		}
		else {
		    this.motifHighLightList.add(motif);
		}
		/* send that this model has changed */
		this.fireChanged();
	    }
	}

    }

    /**
     * forces the model to end all highlighting and fires that it has changed
     * 
     * @see #fireChanged()
     * 
     */
    public final void endAllHighLighting() {
	this.motifHighLightList.clear();
	this.fireChanged();
    }

    /**
     * adds a component to the model that should be informed to repaint on
     * changes
     * 
     * @param component
     *                the component to be informed for repaint
     * @author Frederik Weber
     */
    public void addRepainter(JComponent component) {
	this.repainterList.add(component);
    }

    /**
     * handles other components in case of changes in the model
     * 
     */
    public void fireChanged() {
	/*
	 * repaints all components that are known to be repainted on changes in
	 * this model
	 */
	if (!repainterList.isEmpty()) {
	    for (JComponent c : this.repainterList) {
		c.repaint();
	    }
	}

    }

    public void setMulithighlighting(boolean isMulitHighlightingEnabled) {
	this.isMulitHighlightingEnabled = isMulitHighlightingEnabled;
    }

    public final ArrayList<Motif> getMotifHighLightList() {
	return this.motifHighLightList;
    }

    public final boolean isMulitHighlightingEnabled() {
	return isMulitHighlightingEnabled;
    }

}

