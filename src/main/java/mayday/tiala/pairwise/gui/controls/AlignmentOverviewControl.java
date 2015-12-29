package mayday.tiala.pairwise.gui.controls;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;

import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.gui.AlignmentEditingDialog;
import mayday.tiala.pairwise.gui.views.Zahlenstrahl;

@SuppressWarnings("serial")
public class AlignmentOverviewControl extends JPanel {
	
	protected AlignmentStore store;
	
	public AlignmentOverviewControl(AlignmentStore Store) {
		store=Store;
		
		JButton alignmentEditorButton = new JButton(new AbstractAction("Edit...") {
			public void actionPerformed(ActionEvent e) {
				new AlignmentEditingDialog(store).setVisible(true);
			}
		});
		TimeshiftControl timeShiftControl = new TimeshiftControl(store);
		Dimension maxSize = timeShiftControl.getMaximumSize();
		maxSize.width = alignmentEditorButton.getPreferredSize().width;
		timeShiftControl.setMaximumSize(maxSize);
		Zahlenstrahl zahlenstrahl = new Zahlenstrahl(store);
		
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.CENTER)
				.addComponent(zahlenstrahl)
				.addGroup(layout.createSequentialGroup()
						.addComponent(timeShiftControl)
						.addComponent(alignmentEditorButton)
				)
		);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(zahlenstrahl)
				.addGroup(layout.createParallelGroup()
						.addComponent(timeShiftControl)
						.addComponent(alignmentEditorButton)
				)
		);
	}

}
