package mayday.motifsearch.gui.listeners;

import java.util.ArrayList;
import java.util.EventListener;

import mayday.motifsearch.model.Site;

/**
 * Listener that listens for the selection of sites in the gui
 * 
 * @author Frederik Weber
 */

public interface SiteSelectionListener
	extends EventListener {

    public void siteSelected(Object source, ArrayList<Site> sites);
}
