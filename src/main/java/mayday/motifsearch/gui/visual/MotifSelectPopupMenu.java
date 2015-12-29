package mayday.motifsearch.gui.visual;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import mayday.motifsearch.model.Motif;
import mayday.motifsearch.model.Site;
import mayday.motifsearch.model.SiteComparator;
import mayday.motifsearch.tool.Rounder;

/**
 * @see javax.swing.JPopupMenu
 * 
 * A popup menu that has references to motifs in it. When it pops
 * it shows functions to apply on Motifs.
 * 
 * @author Frederik Weber
 */

public class MotifSelectPopupMenu
	extends JPopupMenu
	implements ActionListener {

    private static final long serialVersionUID = 1L;

    protected SiteSelectionModel siteSelectionModel;
    private ArrayList<Site> selectedSites;
    private ArrayList<JMenuItem> menuItemList = new ArrayList<JMenuItem>();
    public static final byte LIST_ALL_SITES = 0;
    public static final byte ENABLE_DISABLE_DECITION_ONLY = 1;
    private JDialog dialog;
    private int mode;

    /**
     * constructor of a motif popup menu
     * 
     * @param selectedSites
     *                the sites the motif are taken from
     *                and the menu items are constructed with
     * @param siteSelectionModel
     *                a site selection model this menu informs over
     *                selection
     * @param mode
     *                a mode that determines the visual presentation of the
     *                popup: MotifSelectPopupMenu.LIST_ALL_SITES all 
     *                sites are listed in the menu
     *                MotifSelectPopupMenu.ENABLE_DISABLE_DECITION_ONLY only the
     *                first of the sites is used for the information of
     *                the site selection model and a color selection
     *                option is included
     */
    public MotifSelectPopupMenu(ArrayList<Site> selectedSites,
	    SiteSelectionModel siteSelectionModel, byte mode) {
	super();
	SiteComparator c = new SiteComparator(
		SiteComparator.SORT_BY_SIGNIFICANCE_VALUE);
	Collections.sort(selectedSites, c);
	this.selectedSites = selectedSites;
	this.siteSelectionModel = siteSelectionModel;
	this.mode = mode;
	for (Site s : this.selectedSites) {

	    JMenuItem menuItem = new JMenuItem();
	    Motif m = s.getMotif();
	    boolean isHighlighted = this.shouldMotifBeHighlighted(m);
	    switch (mode) {
		case MotifSelectPopupMenu.LIST_ALL_SITES:
		    menuItem.setText("Motif: " + ((m.getName().length() > 50) ? m
			    .getName().substring(0, 51) : m.getName())
			    + " pos: " + s.getPosition()+"-" +(s.getPosition()+m.getLength()-1) 
			    + " significance: "
			    + Double.toString(Rounder.round(
				    s.getSignificanceValue(), 4))
				    + " len: " + m.getLength());
		    break;
		case MotifSelectPopupMenu.ENABLE_DISABLE_DECITION_ONLY:
		    menuItem.setText((isHighlighted ? "disable Highlighting"
			    : "enable Highlighting"));
		    break;
		default:
		    System.err.println("unknown popup mode for highlighting");
		    break;
	    }

	    menuItem.setBackground(m.getColor());
	    menuItem.setForeground((isHighlighted ? Color.RED : Color.BLACK));
	    menuItem.addActionListener(this);
	    menuItemList.add(menuItem);
	    this.add(menuItem);
	}
	if (mode == MotifSelectPopupMenu.ENABLE_DISABLE_DECITION_ONLY) {
	    JMenuItem menuItem = new JMenuItem();
	    menuItem.addActionListener(this);
	    menuItem.setText("change color");
	    menuItemList.add(menuItem);
	    this.add(menuItem);
	}

	this.setMaximumSize(new Dimension(300, this.menuItemList.size() * 15));
    }

    /**
     * if a action is performed on an item. Then the appropriate action is
     * performed. According to the mode the menu shows a popup to highlight or
     * change the color of the motif.
     * 
     * @param e
     * @see java.awt.event.ActionEvent
     * 
     * @author Frederik Weber
     */
    /**/
    public void actionPerformed(ActionEvent e) {
	/*
	 * goes through all items in this menue and determis the source of the
	 * action event
	 */
	for (int i = 0; i < this.menuItemList.size(); i++) {
	    if (this.menuItemList.get(i) == e.getSource()) {

		if (i == this.menuItemList.size() - 1
			&& this.mode == MotifSelectPopupMenu.ENABLE_DISABLE_DECITION_ONLY) {
		    /*
		     * open a color chooser to set the color of the
		     * motif selected
		     */
		    final JColorChooser colorChooser = new JColorChooser(
			    this.selectedSites.get(i - 1)
				    .getMotif().getColor());

		    /* if user approves coloring */
		    ActionListener okActionListener = new ActionListener() {

			public void actionPerformed(ActionEvent actionEvent) {
			    selectedSites.get(menuItemList.size() - 2)
				    .getMotif().setColor(
					    colorChooser.getColor());
			    siteSelectionModel.fireChanged();
			}
		    };

		    /* if user cancels coloring */
		    ActionListener cancelActionListener = new ActionListener() {

			public void actionPerformed(ActionEvent actionEvent) {
			}
		    };

		    dialog = JColorChooser.createDialog(null,
			    "change color of the motif", true,
			    colorChooser, okActionListener,
			    cancelActionListener);

		    /*
		     * Wait until current event dispatching completes before
		     * showing dialog
		     */
		    Runnable showDialog = new Runnable() {

			public void run() {
			    dialog.setModal(true);
			    dialog.setVisible(true);
			}
		    };
		    SwingUtilities.invokeLater(showDialog);
		    break;
		}

		ArrayList<Site> toHighlightSite = new ArrayList<Site>();
		toHighlightSite.add(this.selectedSites.get(i));

		/*
		 * inform the site selection model of the seleced 
		 * sites to highlight
		 */
		this.fireSelected(toHighlightSite);
		break;
	    }
	}
	/* finally dispose this popup */
	this.setVisible(false);
    }

    protected void setSiteSelectionModel(
	    SiteSelectionModel siteSelectionModel) {
	this.siteSelectionModel = siteSelectionModel;
    }

    public void fireSelected(ArrayList<Site> sites) {
	this.siteSelectionModel.siteSelected(this, sites);
    }

    /**
     * determines if a motif according to the set site
     * selection model should be highlighted.
     * 
     * @param motif
     *                the motif to be tested
     * 
     * @return if the Motif is marked as highlighted in the
     *         site selection model
     */
    public boolean shouldMotifBeHighlighted(Motif motif) {
	return this.siteSelectionModel.getMotifHighLightList().contains(motif);
    }

}
