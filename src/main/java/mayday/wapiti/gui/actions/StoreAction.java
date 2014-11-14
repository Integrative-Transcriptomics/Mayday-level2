
package mayday.wapiti.gui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class StoreAction extends AbstractAction {


	private final TransMatrix transMatrix;
	//	private final SelectionModel selection;

	public StoreAction(TransMatrix transMatrix, SelectionModel sm) {
		super("Save matrix");
		this.transMatrix = transMatrix;
		//		this.selection = sm;
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser;
		chooser = new JFileChooser(LoadAction.lastLoadSource);
		chooser.setFileSelectionMode(  JFileChooser.FILES_ONLY );

		String fileName=null;
		while(fileName==null) {
			int l_option = chooser.showSaveDialog( transMatrix.getFrame() );
			if ( l_option  == JFileChooser.APPROVE_OPTION ) {
				fileName = chooser.getSelectedFile().getAbsolutePath();
				// if the user presses cancel, then quit

				if (fileName!=null) {

					if (!fileName.toLowerCase().endsWith(".seasight"))
						fileName += ".SeaSight";

					if (new File(fileName).exists()) {
						if (JOptionPane.showConfirmDialog(transMatrix.getFrame(), 
								"Do you really want to overwrite the existing file \""+fileName+"\"?",
								"Confirm file overwrite", 
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
								!=JOptionPane.YES_OPTION) 
							fileName=null;
					}
					transMatrix.saveToFile(fileName);
					LoadAction.setLastLoadSource(new File(fileName).getParent());
					return;
				}

			} else {
				return;
			}
		}
	}

}