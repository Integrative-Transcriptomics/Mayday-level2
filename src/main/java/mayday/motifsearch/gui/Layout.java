package mayday.motifsearch.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

/**
 * This class contains Methods to add a Component with a GridBagLayout to a
 * Container
 * 
 * @author Frederik Weber
 */

public final class Layout {

    /**
     * adds a component to a container according to GridBag constraints
     * 
     * @see java.awt.GridBagConstraints of a Gridbag Layout
     * @see java.awt.GridBagLayout
     * 
     * @author Frederik Weber
     */
    public static void addComponentToGridBag(Container cp, GridBagLayout gbl,
	    Component c, int x, int y, int gw, int gh, double wx, double wy,
	    int anchor, int fill) {
	GridBagConstraints cc = new GridBagConstraints();
	cc.gridx = x;
	cc.gridy = y;
	cc.gridwidth = gw;
	cc.gridheight = gh;
	cc.weightx = wx;
	cc.weighty = wy;
	cc.anchor = anchor;
	cc.fill = fill;
	cp.add(c);
	gbl.setConstraints(c, cc);
    }

    public static void addComponentToGridBag(Container cp, GridBagLayout gbl,
	    Component c, int x, int y, int gw, int gh, double wx, double wy) {
	Layout.addComponentToGridBag(cp, gbl, c, x, y, gw, gh, wx, wy,
		GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
    }

    public static void addComponentToGrid(Container cp, GridLayout gl,
	    Component c) {
	if (cp.getLayout() != gl){
	    cp.setLayout(gl);
	}
	cp.add(c);
    }

}
