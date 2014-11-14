package mayday.tiala.multi.manual;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import mayday.core.gui.MaydayDialog;
import mayday.tiala.multi.data.AlignmentStore;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class ManualAlignmentFrame extends MaydayDialog {
	
	/**
	 * @param store
	 * @param datasetID
	 */
	public ManualAlignmentFrame( final AlignmentStore store, final int datasetID ) {
		GroupLayout layout = new GroupLayout(getContentPane());
		setLayout(layout);
		
		setTitle("Alignment Editor");
				
		JLabel titleLabel = new JLabel("You can modify the time points associated with each experiment.");
		
		final TimeseriesMIOEditor map1 = new TimeseriesMIOEditor(store.get(0));
		final TimeseriesMIOEditor map2 = new TimeseriesMIOEditor(store.get(datasetID+1));
		
		JButton okButton = new JButton(new AbstractAction("OK"){
			public void actionPerformed(ActionEvent e) {
				if (map1.save(store.get(0)) && map2.save(store.get(datasetID+1))) {
					dispose();
					store.getAlignedDataSets().changeShift(datasetID, 0);
					if(store.getTimeShifts().get(datasetID) == 0) {
						store.fireAlignmentChanged();
					} else {
						store.setTimeShift(datasetID, 0.0);
					}
				}
			} 
		});
		
		JButton cancelButton = new JButton(new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				dispose();
			} 
		});
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(titleLabel)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(map1)
						.addComponent(map2)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(cancelButton)
						.addComponent(okButton)
				)
		);
				
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(titleLabel)
				.addGroup(layout.createSequentialGroup()
						.addComponent(map1)
						.addComponent(map2)
				)
				.addGroup(layout.createSequentialGroup()
						.addComponent(cancelButton)
						.addComponent(okButton)
				)
		);
		
		pack();
	}
}
