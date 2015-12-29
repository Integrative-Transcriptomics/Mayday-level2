package mayday.motifsearch.gui.visual;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import mayday.core.gui.*;
import mayday.motifsearch.gui.Layout;

import javax.swing.*;


public class PreparationDialog extends MaydayDialog{
    private static final long serialVersionUID = 1L;
    private JLabel jLabelStatistic;
    private JTextArea jTAStatistic;

    public PreparationDialog(String lineSeparatedTextStatistic, String lineSeparatedTextReport) {
	super();
	this.jLabelStatistic = new JLabel(lineSeparatedTextStatistic);
	this.jTAStatistic = new JTextArea(lineSeparatedTextReport);
	GridBagLayout gbl = new GridBagLayout();
	this.setLayout(gbl);
	Layout.addComponentToGridBag(this, gbl, this.jLabelStatistic, 0, 0, 1, 1,
		1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	Layout.addComponentToGridBag(this, gbl, this.jTAStatistic, 0, 1, 1 , 1,
		1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

    }
}
