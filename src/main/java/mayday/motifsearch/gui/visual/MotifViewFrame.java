package mayday.motifsearch.gui.visual;

/**
 * This class holds a rectangle for every site of a sequence in its colorModel to store information where the site has been drawn
 * 
 * @author Frederik Weber
 */

import java.awt.Rectangle;

import mayday.motifsearch.model.Site;

public class MotifViewFrame {

    public Rectangle area;
    public Site site;

    /**
     * constructor for a MotifViewFrame
     * 
     * @param area
     *                the area the site was drawn in the colorModel
     * @param site
     *                site in the rectangle/area
     */
    public MotifViewFrame(Rectangle area, Site site) {
	this.area = area;
	this.site = site;
    }

}
