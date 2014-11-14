package mayday.tiala.pairwise.gui.controls;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.manual.ManualAlignmentFrame;

@SuppressWarnings("serial")
public class ManualAlignmentButton extends JButton  {
	
	public ManualAlignmentButton(final AlignmentStore Store) {
		super(new AbstractAction("Edit timepoints") {		
			public void actionPerformed(ActionEvent e) {
				ManualAlignmentFrame maf = new ManualAlignmentFrame(Store);
				maf.setVisible(true);
			}
		});
	}

}
