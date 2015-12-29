package mayday.motifsearch.gui.visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import java.util.*;

import mayday.core.*;
import mayday.core.gui.MaydayFrame;
import mayday.motifsearch.exec.MotifSearchAlgoTask;
import mayday.motifsearch.gui.*;
import mayday.motifsearch.interfaces.IMotifSearchAlgoParser;
import mayday.motifsearch.interfaces.IMotifSearchAlgoStarter;
import mayday.motifsearch.io.*;
import mayday.motifsearch.model.Motif;
import mayday.motifsearch.model.MotifSearchModel;
import mayday.motifsearch.model.Sequence;
import mayday.motifsearch.model.Sequences;
import mayday.motifsearch.preparation.GeneLocation;


/**
 * frame containing visual Interface for results of motif search
 * 
 * @author Frederik Weber
 */
public class VisualisationGUI
extends MaydayFrame {

    private static final long serialVersionUID = 1L;

    /* initialization of visual parts */
    private SequenceTable sequenceTable;
    private JScrollPane sequenceScrollPane;

    private MotifMenueView motifPanel;
    private SequenceTableModel sequenceTableModel;
    private MotifTableModel motifTableModel;
    private SiteSelectionModel SiteSelectionModel;
    MotifSearchAlgoTask motifSearchAlgoTask;
    
    private static ProbeList allProbes;

    /* The Objects that contains the data */

    private Sequences sequences;
    private ArrayList<Motif> motifs;

    @SuppressWarnings("deprecation")
    public VisualisationGUI(MotifSearchModel motifSearchModel, SequencesAnntotation sequencesAnntotation, IMotifSearchAlgoStarter motifSearchAlgoStarter, MotifSearchAlgoTask motifSearchAlgoTask, ProbeList allProbes){

	super("Visual Motif Interface - Algorithm: " + motifSearchAlgoStarter.getName());
	
	VisualisationGUI.allProbes = allProbes;

	this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


	this.motifSearchAlgoTask = motifSearchAlgoTask;

	if (!sequencesAnntotation.isEmpty()){


	    /* set the strand plus for the sequences from the information given in the chromosome set's gene locations
	     * if they are not in chromosome set's gene location they are set as to be on the plus strand */

	    for (int i = 0; i < sequencesAnntotation.getGeneLocs().size(); i++) {
		GeneLocation geneLocation = sequencesAnntotation.getGeneLocs().get(i);

		Sequence tempSequence = motifSearchModel.getHashMapedSequences().get("sequence_"+ geneLocation.getSynonym());
		if (tempSequence != null){
		    tempSequence.setPlusStrand(geneLocation.isPlusStrand());
		    int TlSSPosition;
		    if (geneLocation.isPlusStrand()){
			TlSSPosition = 
			    (int) 
			    (geneLocation.getFrom() - sequencesAnntotation.getTakenFromPosition().get(i));
		    } else {
			TlSSPosition = 
			    (int) 
			    (sequencesAnntotation.getTakenToPosition().get(i) - geneLocation.getTo());
		    }
		    tempSequence.setTlSSPosition(TlSSPosition);
		    tempSequence.setExctractedFromPosition(sequencesAnntotation.getTakenFromPosition().get(i));
		    tempSequence.setExctractedToPosition(sequencesAnntotation.getTakenToPosition().get(i));
		}
	    }
	}

	/* Initialize the Data (sequences and motifs) */

	this.motifs = motifSearchModel.getMotifs();

	this.sequences = motifSearchModel.getSequences();

	Container mainContentPane = this.getContentPane();
	BorderLayout mainborderLayout = new BorderLayout();
	mainContentPane.setLayout(mainborderLayout);

	/* Model: site */
	this.SiteSelectionModel = new SiteSelectionModel();

	Double minSignificanceValue =  this.sequences.getMinSignificanceValue();
	Double maxSignificanceValue = this.sequences.getMaxSignificanceValue();

	if ((minSignificanceValue == null) || (maxSignificanceValue == null)){
	    JOptionPane.showMessageDialog(new JPanel(), "there seem to be no sites of motifs found on given sequences", "Warning", JOptionPane.WARNING_MESSAGE);
	}

	if (minSignificanceValue == null){
	    minSignificanceValue = 0.0;

	}

	if (maxSignificanceValue == null){
	    maxSignificanceValue = 0.0;
	}

	SequenceView sequenceView = new SequenceView(Math.log10(minSignificanceValue)
		, Math.log10(maxSignificanceValue));

	SequenceLogoView sequenceLogoView = new SequenceLogoView();
	sequenceView.setSiteSelectionModel(this.SiteSelectionModel);

	/* sequence table Model */
	SequenceCellRenderer sequenceCellRenderer = new SequenceCellRenderer(sequenceView);
	sequenceTableModel = new SequenceTableModel(this.sequences, this.motifs);
	this.sequenceTable = new SequenceTable(sequenceCellRenderer,
		sequenceTableModel);
	this.sequenceTable.addMouseListener(new MouseAdapterSequence(
		this.sequenceTable));
	SequenceRowSorter sequenceRowSorter = new SequenceRowSorter(
		sequenceTableModel);
	this.sequenceTable.setRowSorter(sequenceRowSorter);
	sequenceTableModel.addTableModelListener(this.sequenceTable);
	sequenceTableModel.setSequenceTable(this.sequenceTable);
	/* set table to toggle sort */
	this.sequenceTable.getRowSorter().toggleSortOrder(0);
	this.sequenceTable.getRowSorter().toggleSortOrder(0);

	/* Start initialisation of: sequenceScrollPane */
	this.sequenceScrollPane = new JScrollPane(this.sequenceTable,
		ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	sequenceScrollPane.addComponentListener(new ComponentAdapter() {

	    @Override
	    public void componentResized(ComponentEvent ce) {
		int columsWidthAfter0thColumn = 0;
		for (int i = 1;i < sequenceTable.getColumnModel().getColumnCount();i++){
		    columsWidthAfter0thColumn += sequenceTable.getColumnModel().getColumn(i).getWidth();
		}
		sequenceTable.getColumnModel().getColumn(0).setPreferredWidth(
			sequenceTable.getParent().getWidth()
			- columsWidthAfter0thColumn);
		if (sequenceTable.getRowCount() > 0
			&& sequenceTable.getParent().getHeight() > sequenceTable
			.getHeight()) {
		    sequenceTable.setRowHeight(sequenceTable.getParent()
			    .getHeight()
			    / (sequenceTable.getRowCount() + 1));
		}
	    }
	});


	sequenceScrollPane.getViewport().setBackground(Color.white);

	/*color models for black and white coloring IMPORTANT: ADD TABLE MODELS LAST!!!*/
	ColorModel colormodel = new ColorModel(true);
	colormodel.addColorModelListener(sequenceLogoView);
	colormodel.addColorModelListener(sequenceView);
	colormodel.fireColorChanged(true);

	/* Motif Menu & List */
	this.motifPanel = new MotifMenueView(this.SiteSelectionModel,this.motifs, colormodel, this.sequenceTable, sequenceLogoView, motifSearchAlgoStarter);
	this.motifTableModel = motifPanel.getMotifTableModel();
	this.motifPanel.addAllMotifSelectionListener(this.motifTableModel);
	this.motifPanel.addAndOrSelectionListener(this.sequenceTableModel);

	colormodel.addColorModelListener(motifTableModel);
	colormodel.addColorModelListener(sequenceTableModel);

	/* Listener on the promoTableModel */
	this.sequenceTableModel
	.addActivatedSequencesListener(this.motifTableModel);


	/* Listener on the MotifTableModel */
	this.motifTableModel
	.addMotifChangeListener(this.sequenceTableModel);
	this.motifTableModel.setActivatedSequences(this.sequenceTableModel.getActivatedSequences());

	/*
	 * the repainters for the highlighting according to the model that
	 * manages the highlighting
	 */
	this.SiteSelectionModel.addRepainter(this.sequenceTable);
	this.SiteSelectionModel.addRepainter(this.motifPanel
		.getMotifTable());

	JTextField jTFexec= new JTextField((motifSearchAlgoTask == null?"unknown":motifSearchAlgoStarter.getExecCommand()));
	jTFexec.setEditable(false);

	JLabel jLexec = new JLabel("Execution command for algorithm was:");

	JTextField jTFStandardoutput= new JTextField(motifSearchAlgoStarter.getStandardOutputFolderPath());
	jTFStandardoutput.setEditable(false);
	JLabel jLStandardoutput = new JLabel("path to standard output folder for algorithm is:");

	JTextField jTFActualoutput= new JTextField((motifSearchAlgoTask == null?"unknown":motifSearchAlgoStarter.getActualOutputFolderPath()));
	jTFActualoutput.setEditable(false);
	JLabel jLActualoutput = new JLabel("path to actual output folder for algorithm is:");



	JPanel infoPanel = new JPanel();
	GridBagLayout infoPanelGridBagLayout = new GridBagLayout();
	infoPanel.setLayout(infoPanelGridBagLayout);
	Layout.addComponentToGridBag(infoPanel, infoPanelGridBagLayout, jLexec
		, 0, 0, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	Layout.addComponentToGridBag(infoPanel, infoPanelGridBagLayout,jTFexec 
		, 0, 1, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	Layout.addComponentToGridBag(infoPanel, infoPanelGridBagLayout, jLStandardoutput
		, 0, 2, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	Layout.addComponentToGridBag(infoPanel, infoPanelGridBagLayout,jTFStandardoutput 
		, 0, 3, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	Layout.addComponentToGridBag(infoPanel, infoPanelGridBagLayout, jLActualoutput
		, 0, 4, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	Layout.addComponentToGridBag(infoPanel, infoPanelGridBagLayout,jTFActualoutput 
		, 0, 5, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);


	if (!sequencesAnntotation.isEmpty()){
	    Layout.addComponentToGridBag(infoPanel, infoPanelGridBagLayout
		    , new JLabel("chosen upstream length was: " + sequencesAnntotation.getUpstreamLength())
	    , 0, 6, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	    Layout.addComponentToGridBag(infoPanel, infoPanelGridBagLayout,
		    new JLabel("chosen minimal upstream length was: " + sequencesAnntotation.getMinUpstreamLength())
	    , 0, 7, 2, 1,1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	    Layout.addComponentToGridBag(infoPanel, infoPanelGridBagLayout,
		    new JLabel("chosen downstream length was: " + sequencesAnntotation.getDownstreamLength())
	    , 0, 8, 2, 1,1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH);
	}

	/*ordering of split panes*/

	JSplitPane splitPaneMotifsSequences = new JSplitPane(
		JSplitPane.HORIZONTAL_SPLIT, sequenceScrollPane, motifPanel);
	splitPaneMotifsSequences.setOneTouchExpandable(true);
	splitPaneMotifsSequences.setResizeWeight(0.5);

	JSplitPane splitPaneMotifsSequencesAndInfos = new JSplitPane(
		JSplitPane.VERTICAL_SPLIT, splitPaneMotifsSequences, infoPanel);
	splitPaneMotifsSequencesAndInfos.setOneTouchExpandable(true);
	splitPaneMotifsSequencesAndInfos.setResizeWeight(0.95);


	splitPaneMotifsSequences.setMinimumSize(new Dimension(300, 0));

	mainContentPane.add(splitPaneMotifsSequencesAndInfos, BorderLayout.CENTER);

	/* final initiations of main frame */
	this.pack();
	MaydayDefaults.centerWindowOnScreen(this);
	this.setSize(800, 600);
    }

    @Override
    public void dispose(){
	super.dispose();
	if (this.motifSearchAlgoTask != null){
	    this.motifSearchAlgoTask.doCancel();
	    this.motifSearchAlgoTask.cancel();
	}
    }

    public static void visualizeFromFolderPathForAlgorithm(String absoluteMotifSearchAlgoOutputFolderPath, IMotifSearchAlgoStarter motifSearchAlgoStarter){
	if (motifSearchAlgoStarter.getAlgoParserDummy().isParsableDataInFolderWithPath(absoluteMotifSearchAlgoOutputFolderPath)){
	    SequencesAnntotation sequencesAnntotation = new SequencesAnntotation();
	    try {
		/*check if SequencesAnnotation.xml exists and read data out*/
		if ((new File(absoluteMotifSearchAlgoOutputFolderPath + java.io.File.separator + "SequencesAnnotation.xml").exists())){

		    /*recreate sequence annotation from file*/
		    sequencesAnntotation = FileImport.fileToSequencesAnntotation(new File(absoluteMotifSearchAlgoOutputFolderPath + java.io.File.separator + "SequencesAnnotation.xml"));

		    IMotifSearchAlgoParser map = motifSearchAlgoStarter.getAlgoParser(absoluteMotifSearchAlgoOutputFolderPath, allProbes);

		    MotifSearchModel motifSearchModel = new MotifSearchModel(map.getFinalSequences(), map.getMotifs());

		    VisualisationGUI vi = new VisualisationGUI(motifSearchModel ,sequencesAnntotation , motifSearchAlgoStarter, null, allProbes);
		    vi.setVisible(true);


		} else if ((new File(absoluteMotifSearchAlgoOutputFolderPath).getParent() != null)){
		    if ((new File((new File(absoluteMotifSearchAlgoOutputFolderPath).getParent()) + java.io.File.separator + "SequencesAnnotation.xml").exists())){

			/*recreate sequence annotation from file*/
			sequencesAnntotation = FileImport.fileToSequencesAnntotation(new File((new File(absoluteMotifSearchAlgoOutputFolderPath).getParent()) + java.io.File.separator + "SequencesAnnotation.xml"));

			IMotifSearchAlgoParser map = motifSearchAlgoStarter.getAlgoParser(absoluteMotifSearchAlgoOutputFolderPath, allProbes);

			MotifSearchModel motifSearchModel = new MotifSearchModel(map.getFinalSequences(), map.getMotifs());

			VisualisationGUI vi = new VisualisationGUI(motifSearchModel ,sequencesAnntotation , motifSearchAlgoStarter, null, allProbes);
			vi.setVisible(true);

		    }
		}
	    } catch (Exception e) {
		System.err.println(e);
		JOptionPane.showMessageDialog(new JPanel(), "An Error Occured: "+e, "Error", JOptionPane.ERROR_MESSAGE);

	    }
	} else {
	    JOptionPane.showMessageDialog(new JPanel(), "choose valid folder for selected Algorithm: ", "Error", JOptionPane.ERROR_MESSAGE);
	}
    }
}
