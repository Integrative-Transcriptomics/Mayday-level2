package mayday.tiala.multi.gui.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import mayday.core.DataSet;
import mayday.core.gui.MaydayDialog;
import mayday.tiala.multi.data.AlignmentStore;

/**
 * @author jaeger
 *
 */
public class SetCenterDialog extends MaydayDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param store
	 */
	public SetCenterDialog(final AlignmentStore store) {
		setTitle("Change DataSet Order");
		
		int numDS = store.getAlignedDataSets().getNumberOfDataSets();
		String[] dsNames = new String[numDS];
		final DataSet[] newOrder = new DataSet[numDS];

		for(int i = 0; i < numDS; i++) {
			dsNames[i] = store.getAlignedDataSets().getDataSet(i).getName();
			newOrder[i] = store.getAlignedDataSets().getDataSet(i);
		}
		
		final ButtonGroup group = new ButtonGroup();
		JPanel radiobuttonPanel = new JPanel();
		radiobuttonPanel.setLayout(new BoxLayout(radiobuttonPanel, BoxLayout.PAGE_AXIS));
		
		for(int i = 0; i < numDS; i++) {
			JRadioButton tmp = new JRadioButton(dsNames[i]);
			tmp.setActionCommand(i+"");
			if(i == 0) {
				tmp.setSelected(true);
			}
			group.add(tmp);
			radiobuttonPanel.add(tmp);
		}
		
		add(radiobuttonPanel, BorderLayout.CENTER);
		
		JButton applyButton = new JButton(new AbstractAction("Apply"){
			/**
			 * 
			 */
			private static final long serialVersionUID = 5094021965998633547L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String ac = group.getSelection().getActionCommand();
				int index = Integer.parseInt(ac);
				if(index != 0) {
					DataSet oldCenter = newOrder[0];
					newOrder[0] = newOrder[index];
					newOrder[index] = oldCenter;
					store.initialize(Arrays.asList(newOrder));
					store.fireCenterChanged();
				}
				dispose();
			}
		});
		
		JButton cancelButton = new JButton(new AbstractAction("Cancel") {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1811011386404818738L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		setLayout(new BorderLayout());
		
		add(radiobuttonPanel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		buttonPanel.add(cancelButton);
		buttonPanel.add(applyButton);
		
		add(buttonPanel, BorderLayout.SOUTH);
		
		setMinimumSize(new Dimension(300, 100));
		
		pack();
	}
}
