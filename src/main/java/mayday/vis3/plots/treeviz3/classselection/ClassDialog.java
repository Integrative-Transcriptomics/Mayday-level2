package mayday.vis3.plots.treeviz3.classselection;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;

import mayday.core.ClassSelectionModel;
import mayday.core.gui.MaydayDialog;

/**
 * @author Eugen Netz
 */
@SuppressWarnings("serial")
public class ClassDialog extends MaydayDialog{
	private ClassPanel panel;
	private boolean applyClustering;
	
	public ClassDialog(ClassSelectionModel model) {
		this.setModal(true);
		this.setTitle("Class Selection");
		this.applyClustering = false;
		
    	panel= new ClassPanel(model);
//    	panel.setObjectsFixed(true);
    	Box totalBox=Box.createVerticalBox();
    	totalBox.add(panel);
    	Box buttonBox=Box.createHorizontalBox();
    	buttonBox.add(Box.createHorizontalGlue());
    	buttonBox.add(new JButton(new OKAction()));
    	buttonBox.add(new JButton(new CancelAction()));
    	totalBox.add(buttonBox);
    	this.add(totalBox);
        pack();
	}
	
	/**
	 * @return True, if the "Apply" button was pressed and the clustering should be accepted. False otherwise.
	 */
	public boolean getApplyClustering() {
		return applyClustering;
	}
	
	private class OKAction extends AbstractAction {

		public OKAction() {
			super( "Apply" );
		}

		public void actionPerformed( ActionEvent event ) {
			applyClustering = true;
			dispose();
		}
	}

	
	private class CancelAction extends AbstractAction {
		
		public CancelAction() {
			super("Cancel");
		}
		
		public void actionPerformed(ActionEvent event) {
			dispose();
		}
	}
}

