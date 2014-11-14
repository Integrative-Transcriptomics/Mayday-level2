package mayday.tiala.multi.gui.controls;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.manual.ManualAlignmentFrame;
/**
 * 
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class ManualAlignmentButton extends JButton  {
	
	public ManualAlignmentButton(final AlignmentStore Store, final int number) {
		super(new AbstractAction("Edit timepoints") {		
			public void actionPerformed(ActionEvent e) {
				ManualAlignmentFrame maf = new ManualAlignmentFrame(Store, number);
				maf.setVisible(true);
			}
		});
	}
}
