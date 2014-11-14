package mayday.motifsearch.gui.visual;

/**
 * This class is an abstract class storing functionality for highlighting implementation of components 
 * to interact with the site selection model and more
 * @author Frederik Weber
 */
import java.util.ArrayList;

import javax.swing.JComponent;

import mayday.motifsearch.gui.listeners.SiteSelectionListener;
import mayday.motifsearch.model.Motif;
import mayday.motifsearch.model.Site;

public abstract class HighLightSelector
extends JComponent {
    private static final long serialVersionUID = 1L;

    /* the site selection model */
    protected SiteSelectionModel siteSelectionModel;

    protected void setSiteSelectionModel(
	    SiteSelectionModel siteSelectionModel) {
	this.siteSelectionModel = siteSelectionModel;
    }

    /**
     * send the information of the selection of sites to all 
     * site selection listeners
     * 
     * @param sites
     *                list of sites that are send to be selected to all
     *                site selection listeners
     * 
     */
    public void fireSelected(ArrayList<Site> sites) {
	for (SiteSelectionListener l : listenerList
		.getListeners(SiteSelectionListener.class)) {
	    l.siteSelected(this, sites);
	}
    }

    protected void addSiteSelectionListener(
	    SiteSelectionListener l) {
	listenerList.add(SiteSelectionListener.class, l);
    }

    protected void removeSiteSelectionListener(
	    SiteSelectionListener l) {
	listenerList.remove(SiteSelectionListener.class, l);
    }

    /**
     * determines if a motif of a site according to the
     * set site selection model should be highlighted
     * 
     * @param site
     *                the site that references the motif
     *                to be tested
     * 
     * @return if the motif referenced by the site is
     *         marked as highlighted in the site selection model
     */
    public boolean shouldMotifOfSiteBeHighlighted(Site site) {
	for (Motif m : this.siteSelectionModel
		.getMotifHighLightList()) {
	    if (m == site.getMotif()) {
		return true;
	    }
	}
	return false;
    }

    /**
     * determines if a motif according to the set site
     * selection model should be highlighted
     * 
     * @param motif
     *                the motif to be tested
     * 
     * @return if the motif is marked as highlighted in the
     *         site selection model
     */
    public boolean shouldMotifBeHighlighted(Motif motif) {
	for (Motif m : this.siteSelectionModel
		.getMotifHighLightList()) {
	    if (m == motif) {
		return true;
	    }
	}
	return false;
    }

    /**
     * ends all highlighting of this model and clears list of the motifs that should be highlighted
     * 
     */
    protected final void endAllHighLighting() {
	this.siteSelectionModel.endAllHighLighting();
    }

}
