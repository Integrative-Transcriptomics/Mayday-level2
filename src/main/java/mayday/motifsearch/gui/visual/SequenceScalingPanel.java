package mayday.motifsearch.gui.visual;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.dataset.DataSetSelectionDialog;
import mayday.motifsearch.gui.*;
import mayday.motifsearch.interfaces.IMotifSearchAlgoStarter;
import mayday.motifsearch.model.*;

public class SequenceScalingPanel
extends JPanel
implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JButton buttonScaleUp = new JButton();
    private JButton buttonScaleDown = new JButton();
    private JButton buttonScaleRight = new JButton();
    private JButton buttonScaleLeft = new JButton();
    private JButton labelButtonZoom = new JButton();
    private JButton exportActivated  = new JButton();
    private JCheckBox colorCheckbox = new JCheckBox();
    private final int ScalingWidth = 100;
    private final int ScalingHigth = 4;
    private SequenceTable sequenceTable;
    private ColorModel colorModel;
    private IMotifSearchAlgoStarter motifSearchAlgoStarter;

    public SequenceScalingPanel(SequenceTable sequenceTable, ColorModel colorModel, IMotifSearchAlgoStarter motifSearchAlgoStarter) {
	super();
	this.motifSearchAlgoStarter = motifSearchAlgoStarter;
	this.colorModel = colorModel;
	this.sequenceTable = sequenceTable;
	this.buttonScaleUp
	.setToolTipText("decrease the colorModel height of the sequences");
	this.buttonScaleDown
	.setToolTipText("increase the colorModel height of the sequences");
	this.buttonScaleRight
	.setToolTipText("increase the colorModel length of the sequences");
	this.buttonScaleLeft
	.setToolTipText("decrease the colorModel length of the sequences");
	this.exportActivated
	.setToolTipText("Export the activated Sequences as Probelist");

	this.buttonScaleUp.setText("up");
	this.buttonScaleDown.setText("down");
	this.buttonScaleRight.setText("right");
	this.buttonScaleLeft.setText("left");
	this.exportActivated.setText("export activated");


	this.colorCheckbox.setText("black colored background");
	this.colorCheckbox.setSelected(false);
	this.colorCheckbox.setToolTipText("changes color of background for sequences and motif's logo");


	this.labelButtonZoom.setText("Scale");
	this.labelButtonZoom.setEnabled(false);

	this.colorCheckbox.addActionListener(this);
	this.buttonScaleUp.addActionListener(this);
	this.buttonScaleDown.addActionListener(this);
	this.buttonScaleRight.addActionListener(this);
	this.buttonScaleLeft.addActionListener(this);
	this.exportActivated.addActionListener(this);

	if (this.motifSearchAlgoStarter == null){
	    this.exportActivated.setEnabled(false);
	}

	/* initialise gridbag layout and constraints */
	GridBagLayout gbl = new GridBagLayout();
	this.setLayout(gbl);
	Layout.addComponentToGridBag(this, gbl, this.colorCheckbox, 0, 0, 3, 1,
		1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	Layout.addComponentToGridBag(this, gbl, this.exportActivated, 0, 1, 3, 1,
		1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	Layout.addComponentToGridBag(this, gbl, this.buttonScaleUp, 1, 2, 1, 1,
		1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	Layout.addComponentToGridBag(this, gbl, this.labelButtonZoom, 1, 3, 1,
		1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	Layout.addComponentToGridBag(this, gbl, this.buttonScaleDown, 1, 4, 1,
		1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	Layout.addComponentToGridBag(this, gbl, this.buttonScaleRight, 2, 2, 1,
		3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	Layout.addComponentToGridBag(this, gbl, this.buttonScaleLeft, 0, 2, 1,
		3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

    }

    public void actionPerformed(ActionEvent e) {
	if (sequenceTable.getRowCount() > 0) {
	    if (e.getSource() == this.buttonScaleUp) {
		if ((this.sequenceTable.getHeight() - ScalingHigth
			* sequenceTable.getRowCount()) < this.sequenceTable
			.getParent().getHeight()) {
		    sequenceTable.setRowHeight(this.sequenceTable.getParent()
			    .getHeight()
			    / (sequenceTable.getRowCount() + 1));
		}
		else if (this.sequenceTable.getRowHeight() > ScalingHigth) {
		    this.sequenceTable.setRowHeight(this.sequenceTable
			    .getRowHeight()
			    - ScalingHigth);
		}
	    }
	    else if (e.getSource() == this.buttonScaleDown) {
		this.sequenceTable.setRowHeight(this.sequenceTable
			.getRowHeight()
			+ ScalingHigth);
	    }
	    else if (e.getSource() == this.buttonScaleRight) {
		if (this.sequenceTable.getParent().getWidth() > this.sequenceTable
			.getWidth()) {
		    this.sequenceTable.getColumnModel().getColumn(0)
		    .setPreferredWidth(
			    this.sequenceTable.getParent().getWidth()
			    - this.sequenceTable
			    .getColumnModel()
			    .getColumn(1).getWidth());
		}
		else {
		    this.sequenceTable.getColumnModel().getColumn(0)
		    .setPreferredWidth(
			    this.sequenceTable.getColumnModel()
			    .getColumn(0).getWidth()
			    + ScalingWidth);
		}
	    }
	    else if (e.getSource() == this.buttonScaleLeft) {
		int columsWidthAfter0thColumn = 0;
		for (int i = 1;i < sequenceTable.getColumnModel().getColumnCount();i++){
		    columsWidthAfter0thColumn += sequenceTable.getColumnModel().getColumn(i).getWidth();
		}
		if ((this.sequenceTable.getWidth() - ScalingWidth) < this.sequenceTable
			.getParent().getWidth()) {
		    this.sequenceTable.getColumnModel().getColumn(0)
		    .setPreferredWidth(
			    this.sequenceTable.getParent().getWidth()
			    - columsWidthAfter0thColumn);

		}
		else if (this.sequenceTable.getWidth() > ScalingWidth
			|| this.sequenceTable.getWidth() < 5) {
		    this.sequenceTable.getColumnModel().getColumn(0)
		    .setPreferredWidth(
			    this.sequenceTable.getColumnModel()
			    .getColumn(0).getWidth()
			    - ScalingWidth);
		}
	    }
	    else if (e.getSource() == this.colorCheckbox) {
		this.colorModel.fireColorChanged(!this.colorCheckbox.isSelected());
	    }else if (e.getSource() == this.exportActivated) {
		if (this.motifSearchAlgoStarter !=null){
		    Sequences ss = ((SequenceTableModel) this.sequenceTable.getModel()).getUnEmptySequences();

		    /* get gene synonymNames names from MasterTable's Probes */
		    ArrayList<String> tempListProbeNames = new ArrayList<String>();
		    DataSetSelectionDialog dataSetSelectionDialog = new DataSetSelectionDialog();
		    dataSetSelectionDialog.setVisible(true);
		    List<DataSet> dataSetTempList = dataSetSelectionDialog.getSelection();
		    DataSet dataSet;
		    if ((dataSetTempList != null) && (!dataSetTempList.isEmpty())){
			dataSet = dataSetTempList.get(0);


			if (dataSet != null){

			    for (Sequence s: ss){
			    	for(String pn : s.getProbeNames())
			    		tempListProbeNames.add(pn);
			    }
			    ProbeList res = new ProbeList(dataSet,true);
			    for (String s: tempListProbeNames){
				Probe p = dataSet.getMasterTable().getProbe(s);
				if (p != null){
				    res.addProbe(p);
				}
			    }

			    res.setName("exported Probesmotif search");
			    res.getAnnotation().setQuickInfo("motif search algorithm was: " +this.motifSearchAlgoStarter.getName());
			    res.getDataSet().getProbeListManager().addObjectAtTop(res);
			}
		    }
		}
	    }
	}
    }

}
