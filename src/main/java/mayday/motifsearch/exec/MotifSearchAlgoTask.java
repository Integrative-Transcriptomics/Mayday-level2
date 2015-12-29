package mayday.motifsearch.exec;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mayday.core.ProbeList;
import mayday.core.tasks.AbstractTask;
import mayday.motifsearch.gui.visual.VisualisationGUI;
import mayday.motifsearch.interfaces.IMotifSearchAlgoParser;
import mayday.motifsearch.interfaces.IMotifSearchAlgoStarter;
import mayday.motifsearch.io.FileExport;
import mayday.motifsearch.io.FileImport;
import mayday.motifsearch.io.SequencesAnntotation;
import mayday.motifsearch.model.MotifSearchModel;

/**
 * Task for the run of a motif search algorithm
 * 
 */

public class MotifSearchAlgoTask extends AbstractTask {
    private IMotifSearchAlgoStarter motifSearchAlgoStarter;
    private boolean openVisualisationAfterRun;
    private String sequencesAnntationFilePath;
    private String separateSequencesFilePath;
    private boolean openResultFASTASeparately;
    private String tempSequencesFilePath;
    private String statistics;
    private CommandExecution commandExecution;
    
    private ProbeList allProbes;

    public MotifSearchAlgoTask(String desc, IMotifSearchAlgoStarter motifSearchAlgoStarter, boolean openVisualisationAfterRun, String sequencesAnntationFilePath, String tempSequencesFilePath, String separateSequencesFilePath, boolean openResultFASTASeparately, String statistics, ProbeList allProbes) {
	super(desc);
	this.motifSearchAlgoStarter = motifSearchAlgoStarter;
	this.openVisualisationAfterRun = openVisualisationAfterRun;
	this.sequencesAnntationFilePath = sequencesAnntationFilePath;
	this.separateSequencesFilePath = separateSequencesFilePath;
	this.openResultFASTASeparately = openResultFASTASeparately;
	this.tempSequencesFilePath = tempSequencesFilePath;
	this.statistics = statistics;
	
	this.allProbes = allProbes;

    }

    @Override
    public void doCancel() {
	if (this.commandExecution != null){
	    this.commandExecution.doCancel();
	}
    }

    @Override
    protected void doWork() throws Exception {

	/*before launch motif search algorithm store and set a unique output path because a lot can happen during the run*/

	String storedAlgorithmOutputFolderPath = this.motifSearchAlgoStarter.getStandardOutputFolderPath() + java.io.File.separator
	+"run" + 
	+ java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) + "_"  
	+ (java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)+ 1)  + "_"  
	+ java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)  + "_"  
	+ java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)  + "_"  
	+ java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE)  + "_"  
	+ java.util.Calendar.getInstance().get(java.util.Calendar.SECOND)  + "_"  
	+ java.util.Calendar.getInstance().get(java.util.Calendar.MILLISECOND);



	this.motifSearchAlgoStarter.setOutputFolderPath(storedAlgorithmOutputFolderPath + java.io.File.separator + "output");

	this.motifSearchAlgoStarter.setInputFASTAPath(storedAlgorithmOutputFolderPath + java.io.File.separator + "sequences.fasta");

	String command = JOptionPane.showInputDialog(new JPanel(), statistics + "algorithm will be be executed with command: ",this.motifSearchAlgoStarter.getExecCommand());



	this.commandExecution = new CommandExecution();
	if (command != null){

	    FileExport.copy(new File((this.openResultFASTASeparately?this.separateSequencesFilePath:this.tempSequencesFilePath)), new File(storedAlgorithmOutputFolderPath + java.io.File.separator + "sequences.fasta"));
	    /*after motif search algorithm has run copy */
	    FileExport.copy(new File(this.sequencesAnntationFilePath), new File(storedAlgorithmOutputFolderPath + java.io.File.separator + "SequencesAnnotation.xml"));


	    int exitVal = commandExecution.execute(command);

	    if (exitVal == 0) {

		IMotifSearchAlgoParser motifAlgoParser = this.motifSearchAlgoStarter.getAlgoParser(storedAlgorithmOutputFolderPath + java.io.File.separator + "output", allProbes);

		MotifSearchModel motifSearchModel = new MotifSearchModel(motifAlgoParser.getFinalSequences(),motifAlgoParser.getMotifs());


		SequencesAnntotation sequencesAnntotation = new SequencesAnntotation();

		/*check if SequencesAnnotation.xml exists and read data out*/
		if (
			(new File((this.openResultFASTASeparately
				?((new File(this.separateSequencesFilePath)).getParent() + java.io.File.separator + "SequencesAnnotation.xml")
					:(storedAlgorithmOutputFolderPath + java.io.File.separator + "SequencesAnnotation.xml"))).exists())){
		    /*recrate sequence annotation from file*/
		    sequencesAnntotation = FileImport.fileToSequencesAnntotation(
			    (new File(
				    (this.openResultFASTASeparately
					    ?((new File(this.separateSequencesFilePath)).getParent() + java.io.File.separator + "SequencesAnnotation.xml")
						    :(storedAlgorithmOutputFolderPath + java.io.File.separator + "SequencesAnnotation.xml")))));
		}
		
		/* open the visual interface on demand*/
		if(this.openVisualisationAfterRun){
		    VisualisationGUI visualInterfaceFrame = new VisualisationGUI(motifSearchModel , sequencesAnntotation, this.motifSearchAlgoStarter, this, allProbes);
		    visualInterfaceFrame.setVisible(true);

		}
	    } else {
		JOptionPane.showMessageDialog(new JPanel(), "run of motif search algo failed\n exit vlaue was "+ exitVal +"\n can not parse or visualize Data\n Output of algorithm was:\n'" +commandExecution.getLastCommandlinesFormExecution()+ "'", "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}
    }

    @Override
    protected void initialize() {
    }
}

