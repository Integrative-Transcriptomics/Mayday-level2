package mayday.motifsearch.gui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.generic.*;
import mayday.core.settings.*;
import javax.swing.*;

import mayday.motifsearch.*;
import mayday.motifsearch.exec.MotifSearchAlgoTask;
import mayday.motifsearch.interfaces.IMotifSearchAlgoStarter;
import mayday.motifsearch.interfaces.MotifSearchAlgoArgument;
import mayday.motifsearch.interfaces.SupportedAlgorithms;
import mayday.motifsearch.preparation.*;
import mayday.motifsearch.tool.*;

public class MotifSearchSettingDialog extends SettingDialog implements
		ActionListener {
	private static final long serialVersionUID = 1L;

	private List<ProbeList> probelists;
	private ArrayList<String> synonymNames;

	private JSpinner jSpUpstreamLength;
	private JSpinner jSpMinUpstreamLength;
	private JLabel jLabelJSpUpstreamLength;
	private JLabel jLabelJSpMinUpstreamLength;
	private JSpinner jSpDownstreamLength;
	private JLabel jLabelJSpDownstreamLength;

	private JButton jBChooseFiles;
	private JFileChooser jFChooser;
	private JCheckBox jCBSaveResultFASTASeparately;
	private JCheckBox jCBOpenResultFASTASeparately;
	private JCheckBox jCBSafeExtractionMode;
	private JCheckBox jCBEnableOpenVisualisation;

	private File[] PTTFiles;
	private File[] fastaFiles;
	private String sequencesFileTempDirectory;
	private String sequencesFilePath;
	private String separateSequencesFilePath;
	private String sequencesAnnotationFilePath;
	private int searchUpstreamATGLength;
	private int searchMinUpstreamATGLength;
	private int searchDownstreamIncludingATGMaxLength;

	private SupportedAlgorithms supportedAlgorithms;
	private JButton jBSaveSeparately;
	private PluginInfo pli;
	private SelectableHierarchicalSetting sHSforAlgorithms;

	private boolean areAllFilesCorrectInitiated = false;
	private boolean isSafeExtractionMode = false;
	private boolean lastIsExtractionModeSinceLastPreparation = true;
	private boolean isDataPreparedForMoSuAl = false;

	private Settings settings;

	public MotifSearchSettingDialog(Window owner, String title,
			List<ProbeList> probelists, PluginInfo pli) {
		super(owner, title, null);
		this.probelists = probelists;
		this.pli = pli;
		this.init();
	}

	@Override
	protected void init() {

		/* get gene synonymNames names from MasterTable's Probes */
		this.synonymNames = new ArrayList<String>();

		boolean areSynonymNamesValid = true;
		for (ProbeList pl : this.probelists) {
			for (Probe p : pl.getAllProbes()) {

				/* check for validity of synonym names */

				String synName = p.getDisplayName();

				if (!MotifSearch.isValidSynonymName(synName)) {
					areSynonymNamesValid = false;
					System.out.println("'" + p.getName()
							+ "' does not seem to be a valid synonym name");
				}
				this.synonymNames.add(synName);
			}
		}

		/* show warning if probelist has invalid synonym names */
		if (!areSynonymNamesValid) {
			JOptionPane
					.showMessageDialog(
							new JPanel(),
							"not all elements in the selected probelist seem to have a valid\n synonym name and may not be found in PTT files",
							"Warning", JOptionPane.WARNING_MESSAGE);
		}

		this.sequencesFileTempDirectory = MaydayDefaults.Prefs
				.getPluginDirectory()
				+ java.io.File.separator
				+ "MotifSearch"
				+ java.io.File.separator + "tempAlgo";
		this.sequencesFilePath = this.sequencesFileTempDirectory
				+ java.io.File.separator + "tempSequences.fasta";

		this.sequencesAnnotationFilePath = this.sequencesFileTempDirectory
				+ java.io.File.separator + "SequencesAnnotation.xml";

		this.supportedAlgorithms = new SupportedAlgorithms();

		this.jFChooser = new JFileChooser();

		SpinnerModel spinnerModelUpstream = new SpinnerNumberModel(100, // initial
																		// value
				1, // min
				1999999999, // max
				1); // step

		SpinnerModel spinnerModelDownstream = new SpinnerNumberModel(0, // initial
																		// value
				0, // min
				1999999999, // max
				1); // step

		SpinnerModel spinnerModelMinUpstream = new SpinnerNumberModel(8, // initial
																			// value
				0, // min
				1999999999, // max
				1); // step

		this.jSpUpstreamLength = new JSpinner(spinnerModelUpstream);
		this.jLabelJSpUpstreamLength = new JLabel("upstream:");
		this.jLabelJSpUpstreamLength.setLabelFor(this.jSpUpstreamLength);

		this.jSpDownstreamLength = new JSpinner(spinnerModelDownstream);
		this.jLabelJSpDownstreamLength = new JLabel("downstream:");
		this.jLabelJSpDownstreamLength.setLabelFor(this.jSpDownstreamLength);

		this.jSpMinUpstreamLength = new JSpinner(spinnerModelMinUpstream);
		this.jLabelJSpMinUpstreamLength = new JLabel("minimal upstream:");
		this.jLabelJSpMinUpstreamLength.setLabelFor(this.jSpUpstreamLength);

		this.jBChooseFiles = new JButton("choose files");
		this.jBChooseFiles.addActionListener(this);

		this.jCBSaveResultFASTASeparately = new JCheckBox(
				"save prepared sequences separately", false);
		this.jCBSaveResultFASTASeparately.addActionListener(this);

		this.jCBOpenResultFASTASeparately = new JCheckBox(
				"open sequences separately", false);
		this.jCBOpenResultFASTASeparately.addActionListener(this);

		this.jCBSafeExtractionMode = new JCheckBox("safe extraction mode",
				this.isSafeExtractionMode);
		this.jCBSafeExtractionMode.addActionListener(this);

		this.jCBEnableOpenVisualisation = new JCheckBox(
				"enable Visualization after run", this.isSafeExtractionMode);
		this.jCBEnableOpenVisualisation.setSelected(true);
		this.jCBEnableOpenVisualisation.addActionListener(this);

		this.jBSaveSeparately = new JButton("save separately");
		this.jBSaveSeparately.addActionListener(this);
		this.jBSaveSeparately.setEnabled(false);

		ArrayList<HierarchicalSetting> sHSAlgos = new ArrayList<HierarchicalSetting>();

		/* update the Arguments of the IMotifAlgos */
		for (IMotifSearchAlgoStarter ma : this.supportedAlgorithms.values()) {
			ma.setInputFASTAPath(this.sequencesFilePath);
		}

		for (IMotifSearchAlgoStarter ma : this.supportedAlgorithms.values()) {
			HierarchicalSetting hSAlgos = new HierarchicalSetting(ma.toString())
					.setCombineNonhierarchicalChildren(true).setLayoutStyle(
							HierarchicalSetting.LayoutStyle.PANEL_VERTICAL);

			hSAlgos = hSAlgos
					.addSetting(new ComponentPlaceHolderSetting(
							"authors: " + ma.getAuthors() + "\nhomepage: "
									+ ma.getHomepage() + "\nmanual: "
									+ ma.getManualLink(),
							new JLabel(
									"for more info to the algorithm click on the questionmark")));
			for (MotifSearchAlgoArgument arg : ma.getEditableArguments()) {
				hSAlgos = hSAlgos.addSetting(arg.getSetting());
			}

			sHSAlgos.add(hSAlgos);
		}

		this.sHSforAlgorithms = new SelectableHierarchicalSetting("algorithms",
				null, 0, sHSAlgos.toArray(),
				SelectableHierarchicalSetting.LayoutStyle.COMBOBOX, false);

		HierarchicalSetting hierarchicalSetting = new HierarchicalSetting(
				"Motif Search", LayoutStyle.PANEL_HORIZONTAL, false)
				.addSetting(
						new HierarchicalSetting("Preparation Parameters",
								LayoutStyle.PANEL_VERTICAL, false)
								.addSetting(
										new ComponentPlaceHolderSetting(
												"number of bases to be chosen for sequence extraction upstream of transcription start site (excluded) of selected genes",
												this.jLabelJSpUpstreamLength))
								.addSetting(
										new ComponentPlaceHolderSetting(
												"number of bases to be chosen for sequence extraction upstream of transcription start site (excluded) of selected genes",
												this.jSpUpstreamLength))
								.addSetting(
										new ComponentPlaceHolderSetting(
												"min number of bases to be chosen for sequence extraction upstream of transcription start site (excluded) of selected genes (use with caution!)",
												this.jLabelJSpMinUpstreamLength))
								.addSetting(
										new ComponentPlaceHolderSetting(
												"min number of bases to be chosen for sequence extraction upstream of transcription start site (excluded) of selected genes",
												this.jSpMinUpstreamLength))
								.addSetting(
										new ComponentPlaceHolderSetting(
												"number of bases to be chosen for sequence extraction downstream of transcription start site (included) of selected genes",
												this.jLabelJSpDownstreamLength))
								.addSetting(
										new ComponentPlaceHolderSetting(
												"number of bases to be chosen for sequence extraction downstream of transcription start site (included) of selected genes",
												this.jSpDownstreamLength))
								.addSetting(
										new ComponentPlaceHolderSetting(
												"choose FASTA-Format files describing a genome of a species and the appropriate PTT files with the same file names as the FASTA files. Multiple selecetion is allowed",
												this.jBChooseFiles))
								.addSetting(
										new ComponentPlaceHolderSetting(
												"exctracts the sequences in a way that no interferrence with other genes or length of gene is violated. Use this feature with caution!",
												this.jCBSafeExtractionMode))
								.addSetting(
										new ComponentPlaceHolderSetting(
												"enable saving the extracted sequences also on a separate location in FASTA format",
												this.jCBSaveResultFASTASeparately))
								.addSetting(
										new ComponentPlaceHolderSetting(
												"choose the destination and file to save/open the files separately",
												this.jBSaveSeparately))
								.addSetting(
										new ComponentPlaceHolderSetting(
												"enable the openting of FASTA file containing the sequences of the for the motif search",
												this.jCBOpenResultFASTASeparately))
								.addSetting(
										new ComponentPlaceHolderSetting(
												"enable a visualisation after run of motif search algorithm",
												this.jCBEnableOpenVisualisation)))
				.addSetting(sHSforAlgorithms);

		Settings s = new Settings(hierarchicalSetting, pli.getPreferences());
		this.settingComponent = s.getSettingComponent();
		this.settings = s;
		super.init();
		this.okButton.setText("prepare, run and close");
		this.applyButton.setText("prepare and run");
		this.applyButton.setEnabled(false);
		this.okButton.setEnabled(false);
		this.setVisible(true);
	}

	/**
	 * Invoked when the action occurs.
	 * 
	 * @param event
	 *            The received action event.
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == this.jBChooseFiles) {
			this.areAllFilesCorrectInitiated = this.chooseGenomeAndPTTFiles();
			this.checkIfReadyForExec();

		} else if (event.getSource() == this.jCBSaveResultFASTASeparately) {
			this.jBSaveSeparately.setEnabled(this.jCBSaveResultFASTASeparately
					.isSelected());
			this.jCBOpenResultFASTASeparately.setSelected(false);

			this.jSpUpstreamLength.setEnabled(true);
			this.jLabelJSpUpstreamLength.setEnabled(true);
			this.jSpDownstreamLength.setEnabled(true);
			this.jLabelJSpDownstreamLength.setEnabled(true);
			this.jBChooseFiles.setEnabled(true);

			if (this.jCBSaveResultFASTASeparately.isSelected()) {
				this.jBSaveSeparately.setText("save separately");
			}
			this.isSafeExtractionMode = jCBOpenResultFASTASeparately
					.isSelected();

			/* set the chosen file back, user has too choose again */
			this.separateSequencesFilePath = null;
			this.checkIfReadyForExec();

		} else if (event.getSource() == this.jCBSafeExtractionMode) {
			this.isSafeExtractionMode = this.jCBSafeExtractionMode.isSelected();

		} else if (event.getSource() == this.jCBOpenResultFASTASeparately) {
			this.jBSaveSeparately.setEnabled(this.jCBOpenResultFASTASeparately
					.isSelected());
			this.jCBSaveResultFASTASeparately.setSelected(false);

			this.jSpUpstreamLength
					.setEnabled(!this.jCBOpenResultFASTASeparately.isSelected());
			this.jLabelJSpUpstreamLength
					.setEnabled(!this.jCBOpenResultFASTASeparately.isSelected());
			this.jSpDownstreamLength
					.setEnabled(!this.jCBOpenResultFASTASeparately.isSelected());
			this.jLabelJSpDownstreamLength
					.setEnabled(!this.jCBOpenResultFASTASeparately.isSelected());
			this.jBChooseFiles.setEnabled(!this.jCBOpenResultFASTASeparately
					.isSelected());

			if (this.jCBOpenResultFASTASeparately.isSelected()) {
				this.jBSaveSeparately.setText("open separately");
			}

			this.isSafeExtractionMode = jCBOpenResultFASTASeparately
					.isSelected();

			/* set the chosen file back, user has too choose again */
			this.separateSequencesFilePath = null;
			this.checkIfReadyForExec();

		} else if (event.getSource() == this.jBSaveSeparately) {

			jFChooser.setMultiSelectionEnabled(false);
			jFChooser.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory()
							|| f.getName().toLowerCase().endsWith(".fna")
							|| f.getName().toLowerCase().endsWith(".fasta")
							|| f.getName().toLowerCase().endsWith(".txt");
				}

				@Override
				public String getDescription() {
					return "Fasta GeneSequences";
				}
			});

			int state;
			if (this.jCBOpenResultFASTASeparately.isSelected()) {
				state = jFChooser.showOpenDialog(null);
			} else {
				state = jFChooser.showSaveDialog(null);
			}

			if (state == JFileChooser.APPROVE_OPTION) {
				if (this.jCBSaveResultFASTASeparately.isSelected()) {
					this.separateSequencesFilePath = jFChooser
							.getSelectedFile().getAbsolutePath();
				} else if (this.jCBOpenResultFASTASeparately.isSelected()) {
					this.separateSequencesFilePath = jFChooser
							.getSelectedFile().getAbsolutePath();
				}
				/* update the Arguments of the IMotifAlgos */
				for (IMotifSearchAlgoStarter ma : this.supportedAlgorithms
						.values()) {
					ma.setInputFASTAPath(this.separateSequencesFilePath);
				}

				this.checkIfReadyForExec();
			}
			/* reset the selected files */
			File[] emptyDumpFiles = { new File("") };
			jFChooser.setSelectedFiles(emptyDumpFiles);

		}

	}

	private final boolean checkIfReadyForExec() {

		boolean confirmedUserSelection = ((this.separateSequencesFilePath != null)
				&& (new File(this.separateSequencesFilePath)).exists() && (this.jCBOpenResultFASTASeparately
					.isSelected())) || this.areAllFilesCorrectInitiated;

		if ((this.jCBSaveResultFASTASeparately.isSelected() || this.jCBOpenResultFASTASeparately
				.isSelected()) && (this.separateSequencesFilePath == null)) {
			confirmedUserSelection = false;
		}
		this.applyButton.setEnabled(confirmedUserSelection);
		this.okButton.setEnabled(confirmedUserSelection);
		return confirmedUserSelection;

	}

	private final boolean chooseGenomeAndPTTFiles() {

		boolean areAllFilesCorrectInitiated = false;

		jFChooser.setMultiSelectionEnabled(true);
		jFChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory()
						|| f.getName().toLowerCase().endsWith(".fna")
						|| f.getName().toLowerCase().endsWith(".fasta")
						|| f.getName().toLowerCase().endsWith(".txt");
			}

			@Override
			public String getDescription() {
				return "Fasta Chromosome File(s)";
			}
		});

		int state = jFChooser.showOpenDialog(null);

		if (state == JFileChooser.APPROVE_OPTION) {
			this.fastaFiles = jFChooser.getSelectedFiles();

			/* reset the selected files */
			File[] emptyDumpFiles = { new File("") };
			jFChooser.setSelectedFiles(emptyDumpFiles);

			jFChooser.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory()
							|| f.getName().toLowerCase().endsWith(".ptt");
				}

				@Override
				public String getDescription() {
					return "ProTein File(s)";
				}
			});

			state = jFChooser.showOpenDialog(null);

			if (state == JFileChooser.APPROVE_OPTION) {
				this.PTTFiles = jFChooser.getSelectedFiles();
				/* reset the selected files */
				jFChooser.setSelectedFiles(emptyDumpFiles);

				boolean areValidFiles = true;
				if (this.PTTFiles.length == this.fastaFiles.length) {

					boolean doAllFilesExist = true;
					for (int i = 0; i < this.fastaFiles.length; i++) {
						doAllFilesExist = doAllFilesExist
								&& this.fastaFiles[i].exists();
					}

					for (int i = 0; i < this.PTTFiles.length; i++) {
						doAllFilesExist = doAllFilesExist
								&& this.PTTFiles[i].exists();
					}

					for (int i = 0; i < this.fastaFiles.length; i++) {
						boolean isFileNameSomeWhereInPTTFilesTheSame = false;
						for (int j = 0; j < this.PTTFiles.length; j++) {

							isFileNameSomeWhereInPTTFilesTheSame = isFileNameSomeWhereInPTTFilesTheSame
									|| (this.fastaFiles[i].getName().substring(0,this.fastaFiles[i].getName().lastIndexOf('.'))
											.equalsIgnoreCase(this.PTTFiles[j].getName()
													.substring(0, this.PTTFiles[j].getName().lastIndexOf('.'))));
						}
						areValidFiles = areValidFiles
								&& isFileNameSomeWhereInPTTFilesTheSame
								&& doAllFilesExist;
					}

				} else {
					areValidFiles = false;
				}

				if (areValidFiles) {
					areAllFilesCorrectInitiated = true;
					this.isDataPreparedForMoSuAl = false;
				} else {
					JOptionPane
							.showMessageDialog(
									new JPanel(),
									"FASTA files and PTT files do not match (they have not the same name or not every FASTA file has a corresponding PTT file) or some of them do not exist",
									"Error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				// System.out.println("file selection for motif search aborted");
				/* reset the selected files */
				jFChooser.setSelectedFiles(emptyDumpFiles);

			}

		} else {
			// System.out.println("file selection for motif search aborted");
			/* reset the selected files */
			File[] emptyDumpFiles = { new File("") };
			jFChooser.setSelectedFiles(emptyDumpFiles);

		}
		return areAllFilesCorrectInitiated;
	}

	private void exec() {

		/* get the user-selected motif search algorithm */
		IMotifSearchAlgoStarter selectedAlgorithm = this.supportedAlgorithms
				.get(this.sHSforAlgorithms.getValueString());
		selectedAlgorithm.setInputFASTAPath((this.jCBOpenResultFASTASeparately
				.isSelected() ? this.separateSequencesFilePath
				: this.sequencesFilePath));

		if ((this.lastIsExtractionModeSinceLastPreparation != this.jCBSafeExtractionMode
				.isSelected())
				|| ((!((Integer) this.jSpUpstreamLength.getValue())
						.equals(this.searchUpstreamATGLength)
						|| !((Integer) this.jSpDownstreamLength.getValue())
								.equals(this.searchDownstreamIncludingATGMaxLength) || !((Integer) this.jSpMinUpstreamLength
							.getValue())
						.equals(this.searchMinUpstreamATGLength))
						|| !this.isDataPreparedForMoSuAl || (this.jCBSafeExtractionMode
						.isSelected() && (!((Integer) this.jSpMinUpstreamLength
						.getValue()).equals(this.searchMinUpstreamATGLength))))) {
			if (!this.jCBOpenResultFASTASeparately.isSelected()) {

				this.lastIsExtractionModeSinceLastPreparation = this.jCBSafeExtractionMode
						.isSelected();

				MotifSearchAlgoDataPrep motifSearchAlgoDataPrep = new MotifSearchAlgoDataPrep(
						"prep. motif search of " + this.synonymNames.size()
								+ " genes", this.fastaFiles, this.PTTFiles,
						this.probelists,
						(Integer) this.jSpUpstreamLength.getValue(),
						(Integer) this.jSpDownstreamLength.getValue(),
						(Integer) this.jSpMinUpstreamLength.getValue(),
						this.isSafeExtractionMode, this.sequencesFilePath,
						this.sequencesAnnotationFilePath,
						this.separateSequencesFilePath,
						this.jCBSaveResultFASTASeparately.isSelected());

				FastaRepresentation.count = 0;
				DNASequenceUtils.countu = 0;
				DNASequenceUtils.countd = 0;
				GeneLocation.countu = 0;
				GeneLocation.countd = 0;
				DNASequenceUtils.report = "";
				GeneLocation.report = "";

				motifSearchAlgoDataPrep.start();

				motifSearchAlgoDataPrep.waitFor();

				this.isDataPreparedForMoSuAl = motifSearchAlgoDataPrep.isDataPreparedForMoSuAl;

			}

		}

		if (this.isDataPreparedForMoSuAl) {
			this.searchDownstreamIncludingATGMaxLength = (Integer) this.jSpDownstreamLength
					.getValue();
			this.searchUpstreamATGLength = (Integer) this.jSpUpstreamLength
					.getValue();
			this.searchMinUpstreamATGLength = (Integer) this.jSpMinUpstreamLength
					.getValue();
			try {

				String statistics = "# not found genes: "
						+ FastaRepresentation.count + "\n"
						+ "# upstream interference with other genes: "
						+ GeneLocation.countu + "\n"
						+ "# downstream interference with other genes: "
						+ GeneLocation.countd + "\n"
						+ "# upstream exeded DNA Stand: "
						+ DNASequenceUtils.countu + "\n"
						+ "# downstream exeded Stand : "
						+ DNASequenceUtils.countd + "\n";

				ProbeList allProbes = ProbeList
						.createUniqueProbeList(probelists);

				MotifSearchAlgoTask saTask = new MotifSearchAlgoTask(
						"Run and parse "
								+ selectedAlgorithm.toString()
								+ (this.jCBEnableOpenVisualisation.isSelected() ? " and visualize"
										: ""), selectedAlgorithm.clone(),
						this.jCBEnableOpenVisualisation.isSelected(),
						this.sequencesAnnotationFilePath,
						this.sequencesFilePath, this.separateSequencesFilePath,
						this.jCBOpenResultFASTASeparately.isSelected(),
						statistics, allProbes);

				saTask.start();

			} catch (Exception e) {
				System.out.print(e);
			}
		} else {
			JOptionPane
					.showMessageDialog(
							new JPanel(),
							"Data preparation for motif search algorithm failed, can not run algorithm!",
							"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public boolean apply() {
		boolean isSettingsPrepared = super.apply();
		if (isSettingsPrepared) {
			new Thread() {
				@Override
				public void run() {
					exec();
				}

			}.start();
		}
		return isSettingsPrepared;
	}

	public boolean applyAndSave() {
		boolean b = apply();
		if (b)
			settings.storeCurrentSettingAsDefault();
		return b;
	}
}
