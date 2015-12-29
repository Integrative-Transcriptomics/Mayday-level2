package mayday.wapiti.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import mayday.core.gui.listdialog.BaseFrame;
import mayday.wapiti.gui.actions.LoadAction;
import mayday.wapiti.gui.actions.RunTransformationsAction;
import mayday.wapiti.gui.actions.StoreAction;
import mayday.wapiti.gui.actions.experiments.AddExperimentsAction;
import mayday.wapiti.gui.actions.experiments.RemoveExperimentsAction;
import mayday.wapiti.gui.actions.locus.FilterLocusDataAction;
import mayday.wapiti.gui.actions.locus.ImportLocusDataAction;
import mayday.wapiti.gui.actions.locus.LocusConsistencyCheckAction;
import mayday.wapiti.gui.actions.locus.LocusDataFromReadsAction;
import mayday.wapiti.gui.actions.locus.ManageLocusDataAction;
import mayday.wapiti.gui.actions.locus.MergeLocusDataAction;
import mayday.wapiti.gui.actions.locus.TransformLocusDataAction;
import mayday.wapiti.gui.actions.names.MapExperimentNamesAction;
import mayday.wapiti.gui.actions.names.SortExperimentsAction;
import mayday.wapiti.gui.actions.transform.AddTransformationAction;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class TransMatrixFrame extends BaseFrame{

	protected TransMatrix tm;
	protected JCheckBox parallel;

	public TransMatrixFrame(TransMatrix tm) {
		super("Mayday SeaSight - Add and configure experiments");
		this.tm = tm;
		setMinimumSize(new Dimension(800,600));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(TransMatrixFrame.this, 
						"Really close SeaSight?\nAll unsaved data will be lost.", 
						"Confirm close", 
						JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE, 
						null)
						==JOptionPane.YES_OPTION)
					dispose();
			}
		});
	}

	@Override
	public JComponent getDescription() {
		return new JLabel("Experiments in this Matrix: ");
	}

	@Override
	public JComponent getList() {
		return tm.getPane();
	}

	@Override
	public void fillListActions(List<Object> actions) {
		actions.add("Experiments");
		actions.add(new AddExperimentsAction(tm));
		actions.add(new RemoveExperimentsAction(tm, tm.getPane().getSelectionModel()));
		actions.add(null);
		actions.add("Experiment names");
		actions.add(new MapExperimentNamesAction(tm, tm.getPane().getSelectionModel()));
		actions.add(new SortExperimentsAction(tm, tm.getPane().getSelectionModel()));
		actions.add(null);
		actions.add("Transformations");
		actions.add(new AddTransformationAction(tm, tm.getPane().getSelectionModel()));		
		actions.add(null);
		actions.add(null);
		actions.add("Locus Data");
		actions.add(new LocusConsistencyCheckAction(tm, tm.getPane().getSelectionModel()));
		actions.add(new ManageLocusDataAction(tm, tm.getPane().getSelectionModel()));
		actions.add(new ImportLocusDataAction(tm, tm.getPane().getSelectionModel()));
		actions.add(new LocusDataFromReadsAction(tm, tm.getPane().getSelectionModel()));
		actions.add(null);
		actions.add(new MergeLocusDataAction(tm, tm.getPane().getSelectionModel()));
		actions.add(new TransformLocusDataAction(tm, tm.getPane().getSelectionModel()));
		actions.add(new FilterLocusDataAction(tm, tm.getPane().getSelectionModel()));
	}

	@Override
	public int getListSize() {
		return tm.getExperimentCount();
	}
	
	public TransMatrix getTransMatrix() {
		return tm;
	}
	
	public void fillDialogActions(List<Object> actions) {
		actions.add(new StoreAction(tm, tm.getPane().getSelectionModel()));
		actions.add(new LoadAction(tm, tm.getPane().getSelectionModel()));
		actions.add(null);
		actions.add(null);
		actions.add(parallel = new JCheckBox("Parallel execution", true));
		actions.add(new RunTransformationsAction(tm, parallel));
	}

}
