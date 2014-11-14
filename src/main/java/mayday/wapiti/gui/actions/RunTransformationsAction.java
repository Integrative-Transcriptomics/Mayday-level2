
package mayday.wapiti.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class RunTransformationsAction extends AbstractAction {

	
	private final TransMatrix transMatrix;
	private final JCheckBox parallel;

	public RunTransformationsAction(TransMatrix transMatrix, JCheckBox runParallel) {
		super("Run Transformations Now");
		this.transMatrix = transMatrix;
		this.parallel = runParallel;
	}

	public void actionPerformed(ActionEvent e) {
//		new ExportDialog(transMatrix.getPane(), false);
		
		if (transMatrix.canRunTransformations()) {		
			transMatrix.getFrame().setState(JFrame.ICONIFIED);
			new Thread() {
				public void run() {
					transMatrix.runTransformations(parallel.isSelected());
				}
			}.start();
		}
	}
	
}