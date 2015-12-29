package mayday.motifsearch.gui.visual;

import java.util.ArrayList;

import mayday.motifsearch.gui.listeners.ColorModelListener;

/**
 * a color model for the coloring of Objects background color (e.g. SequenceVie, MotivView)
 * 
 * @param the cammand line and all parameters/arguments to execute in one String
 */

public class ColorModel {
    boolean isWhiteAndNotBlackColorModel;
    private ArrayList<ColorModelListener> listeners = new ArrayList<ColorModelListener>();

    public void addColorModelListener(ColorModelListener l) {
	listeners.add(l);
    }

    public void removeColorModelListener(ColorModelListener l) {
	listeners.remove(l);
    }

    protected void fireColorChanged(boolean isWhiteColorModel) {
	for (ColorModelListener l : this.listeners) {
	    l.colorModelChanged(isWhiteColorModel);
	}
    }

    public ColorModel(boolean isWhiteAndNotBlackColorModel) {
	super();
	this.isWhiteAndNotBlackColorModel = isWhiteAndNotBlackColorModel;
    }

}
