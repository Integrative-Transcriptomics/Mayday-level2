package mayday.motifsearch.gui.visual;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableRowSorter;

import mayday.motifsearch.gui.listeners.*;
import mayday.motifsearch.interfaces.IMotifSearchAlgoStarter;
import mayday.motifsearch.model.Motif;



/**
 * View of the motif table and options
 * 
 * @author Frederik Weber
 */

public class MotifMenueView
extends JPanel
implements ActionListener {

    private static final long serialVersionUID = 1L;

    private MotifTableModel motifTableModel;
    private ButtonGroup groupANDOR;
    private JToggleButton buttonAND;
    private JToggleButton buttonOR;
    private JButton buttonALL;
    private JButton buttonNONE;
    private JButton buttonResetHighlighting;
    private MotifTable motifTable;
    private boolean areAllSelected;
    private boolean isOrModusNotAndModus;
    private SiteSelectionModel siteSelectionModel;
    private JCheckBox jCBMultiHighlighting;
    private IMotifSearchAlgoStarter motifSearchAlgoStarter;

    private ArrayList<MotifSelectionListener> listeners = new ArrayList<MotifSelectionListener>();
    private ArrayList<AndOrSelectionListener> aoListeners = new ArrayList<AndOrSelectionListener>();

    public MotifMenueView(SiteSelectionModel siteSelectionModel, ArrayList<Motif> motifs, ColorModel colorModel, SequenceTable sequenceTable, SequenceLogoView sequenceLogoView, IMotifSearchAlgoStarter motifSearchAlgoStarter) {
	super();
	super.setOpaque(true);
	this.setLayout(new GridBagLayout());
	this.motifSearchAlgoStarter = motifSearchAlgoStarter;

	/* Add JScroll Pane */
	GridBagConstraints c = new GridBagConstraints();
	c.fill = GridBagConstraints.BOTH;
	c.gridx = 0;
	c.gridy = 0;
	c.weightx = 1.0;
	c.weighty = 1;

	this.add(this.init(siteSelectionModel, motifs, sequenceLogoView), c);

	c.gridx = 0;
	c.gridy = 1;
	c.weighty = 0;

	this.add(this.buttonMenue(colorModel, sequenceTable), c);



    }

    /**
     * Creates the Button Menu on a new JPanel !
     * 
     * @return the Button Menu as type: JPanel
     */
    private JPanel buttonMenue(ColorModel colorModel, SequenceTable sequenceTable) {

	this.initButtons();
	/* Add JScroll Pane */
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new GridBagLayout());
	buttonPanel.setBackground(Color.white);

	GridBagConstraints c = new GridBagConstraints();

	// ADD ALL BUTTON
	c.fill = GridBagConstraints.HORIZONTAL;
	c.gridx = 0;
	c.gridy = 0;
	c.weightx = 0.5;
	c.gridwidth = 1;
	buttonPanel.add(this.buttonALL, c);

	// ADD NONE BUTTON
	c.fill = GridBagConstraints.HORIZONTAL;
	c.weightx = 0.5;
	c.gridx = 1;
	c.gridy = 0;
	c.weightx = 0.5;
	c.gridwidth = 1;
	buttonPanel.add(this.buttonNONE, c);

	// ADD AND BUTTON
	c.fill = GridBagConstraints.HORIZONTAL;
	c.weightx = 0.5;
	c.gridx = 0;
	c.gridy = 1;
	c.weightx = 0.5;
	buttonPanel.add(this.buttonAND, c);

	// ADD OR BUTTON
	c.fill = GridBagConstraints.HORIZONTAL;
	c.weightx = 0.5;
	c.gridx = 1;
	c.gridy = 1;
	c.weightx = 0.5;
	buttonPanel.add(this.buttonOR, c);

	// ADD Reset Highlighting BUTTON
	c.fill = GridBagConstraints.HORIZONTAL;
	c.weightx = 1.0;
	c.gridx = 0;
	c.gridy = 2;
	c.gridwidth = 2;
	buttonPanel.add(this.buttonResetHighlighting, c);

	// ADD Reset Highlighting BUTTON
	c.fill = GridBagConstraints.HORIZONTAL;
	c.weightx = 1.0;
	c.gridx = 0;
	c.gridy = 3;
	c.gridwidth = 2;
	buttonPanel.add(this.jCBMultiHighlighting, c);


	// ADD Reset Highlighting BUTTON
	c.fill = GridBagConstraints.HORIZONTAL;
	c.weightx = 1.0;
	c.weighty = 4.0;
	c.gridx = 0;
	c.gridy = 4;
	c.gridwidth = 2;
	c.gridheight = 4;
	buttonPanel.add(new SequenceScalingPanel(sequenceTable, colorModel, this.motifSearchAlgoStarter), c);

	return buttonPanel;

    }

    /*
     * Initialization method for the motif model
     */
    private MotifTable initMotifTable(
	    MotifCellRenderer motifCellRenderer,
	    SequenceLogoCellRenderer sequenceLogoCellRenderer,
	    MotifTableModel motifTableModel) {

	return new MotifTable(motifCellRenderer, sequenceLogoCellRenderer , motifTableModel);
    }

    /*
     * Initialization method for the motif scroll pane, creates
     * the motif table model */
    private JScrollPane init(SiteSelectionModel siteSelectionModel, ArrayList<Motif> motifs, SequenceLogoView sequenceLogoView) {
	this.siteSelectionModel = siteSelectionModel;
	MotifView motifView = new MotifView();

	motifView.setSiteSelectionModel(siteSelectionModel);
	MotifCellRenderer motifCellRenderer = new MotifCellRenderer(
		motifView);
	SequenceLogoCellRenderer sequenceLogoCellRenderer = new SequenceLogoCellRenderer(
		sequenceLogoView);

	this.motifTableModel = new MotifTableModel(motifs);
	this.motifTable = initMotifTable(motifCellRenderer,sequenceLogoCellRenderer,
		this.motifTableModel);
	this.motifTable.getColumn("significance").setCellRenderer(new DoubleCellRender());
	this.motifTable.addMouseListener(new MouseAdapterMotif(
		this.motifTable));
	this.motifTable.setSiteSelectionModel(siteSelectionModel);
	motifTable.setRowSorter(new TableRowSorter<MotifTableModel>(
		this.motifTableModel));
	JScrollPane motifsScrollPane = new WhiteScrollPane(motifTable,
		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	motifsScrollPane.getViewport().setPreferredSize(new Dimension(160, 0));
	motifsScrollPane.getViewport().setMinimumSize(new Dimension(100, 100));
	motifsScrollPane.setBackground(Color.white);

	return motifsScrollPane;

    }
    public final JTable getMotifTable() {
	return motifTable;
    }

    public MotifTableModel getMotifTableModel() {
	return this.motifTableModel;
    }

    /* initButtons initializes all the Buttons */
    private void initButtons() {

	/* Initialize the AND OR BUTTONs */
	this.groupANDOR = new ButtonGroup();

	this.buttonAND = new JToggleButton("AND");
	this.buttonAND
	.setToolTipText("only visualise if selected motifs are on the same sequence");
	this.buttonAND.addActionListener(this);

	this.buttonOR = new JToggleButton("OR");
	this.buttonOR
	.setToolTipText("allways visualise if selected motifs are on sequence");
	this.buttonOR.addActionListener(this);

	this.groupANDOR.add(this.buttonAND);
	this.groupANDOR.add(this.buttonOR);
	this.buttonOR.setSelected(true);

	/* Initialize the ALL / NONE Buttons */
	this.buttonALL = new JButton("ALL");
	this.buttonALL.setToolTipText("select all motifs");
	this.buttonALL.addActionListener(this);

	this.buttonNONE = new JButton("NONE");
	this.buttonNONE.setToolTipText("deselect all motifs");
	this.buttonNONE.addActionListener(this);

	/* Initialize the highlight Button */
	this.buttonResetHighlighting = new JButton("reset highlighting");
	this.buttonResetHighlighting.setToolTipText("reset the highlighting for all motifs and unhighlight them");
	this.buttonResetHighlighting.addActionListener(this);

	/* Initialize the highlight Button */
	this.jCBMultiHighlighting = new JCheckBox();
	this.jCBMultiHighlighting.setText("mulithighlighting");
	this.jCBMultiHighlighting
	.setToolTipText("mulithighlighting enables highlighting of more than one motif");
	this.jCBMultiHighlighting.setSelected(false);
	this.jCBMultiHighlighting.addActionListener(this);

    }

    public void actionPerformed(ActionEvent e) {

	if (e.getSource() == this.buttonALL) {
	    this.areAllSelected = true;
	    this.fireAllSelected();
	}
	else if (e.getSource() == this.buttonNONE) {
	    this.areAllSelected = false;
	    this.fireAllSelected();
	}
	else if (e.getSource() == this.buttonAND) {
	    this.isOrModusNotAndModus = false;
	    this.fireAndOrSelected();

	}
	else if (e.getSource() == this.buttonOR) {
	    this.isOrModusNotAndModus = true;
	    this.fireAndOrSelected();

	}
	else if (e.getSource() == this.buttonResetHighlighting) {
	    this.siteSelectionModel.endAllHighLighting();

	}
	else if (e.getSource() == this.jCBMultiHighlighting) {
	    this.siteSelectionModel
	    .setMulithighlighting(this.jCBMultiHighlighting.isSelected());
	}
    }


    protected void fireAllSelected() {
	for (MotifSelectionListener l : this.listeners) {
	    l.fireAllSelected(this.areAllSelected);
	}
    }


    protected void fireAndOrSelected() {
	for (AndOrSelectionListener l : this.aoListeners) {
	    l.isOrModusNotAndModusChanged(this.isOrModusNotAndModus);
	}
    }


    public void addAllMotifSelectionListener(MotifSelectionListener l) {
	listeners.add(l);
    }

    public void removeMotifSelectionListener(MotifSelectionListener l) {
	listeners.remove(l);
    }

    public void addAndOrSelectionListener(AndOrSelectionListener l) {
	aoListeners.add(l);
    }

    public void removeAndOrSelectionListener(AndOrSelectionListener l) {
	aoListeners.remove(l);
    }


}
