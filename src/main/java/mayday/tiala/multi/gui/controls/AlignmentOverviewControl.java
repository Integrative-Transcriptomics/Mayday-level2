package mayday.tiala.multi.gui.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.gui.AlignmentEditingDialog;
import mayday.tiala.multi.gui.views.Zahlenstrahl;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class AlignmentOverviewControl extends JPanel {
	
	protected AlignmentStore store;
	
	protected Zahlenstrahl zahlenstrahl;
	
	/**
	 * @param Store
	 */
	public AlignmentOverviewControl(AlignmentStore Store) {
		store=Store;
		
		//get number of aligned data sets
		int numDataSets = store.getAlignedDataSets().getNumberOfDataSets();
		
		JButton[] alignmentEditorButtons = new JButton[numDataSets - 1];
		
		for(int i = 0; i < alignmentEditorButtons.length; i++) {
			final int j = i;
			alignmentEditorButtons[i] = new JButton(new AbstractAction("Edit...") {
				public void actionPerformed(ActionEvent e) {
					new AlignmentEditingDialog(j,store).setVisible(true);
				}
			});
		}
		
		JButton changeCenterDataSetButton = new JButton(new AbstractAction("Set Center...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SetCenterDialog(store).setVisible(true);
			}
		});
		
		setLayout(new BorderLayout());
		
		//create the time lines
		zahlenstrahl = new Zahlenstrahl(store);
		
		this.add(zahlenstrahl, BorderLayout.CENTER);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
		
		//add change center data set button
		Dimension buttonSize = new Dimension(100, 25);
		changeCenterDataSetButton.setMinimumSize(buttonSize);
		changeCenterDataSetButton.setMaximumSize(buttonSize);
		changeCenterDataSetButton.setPreferredSize(buttonSize);
		
		rightPanel.add(Box.createVerticalStrut(buttonSize.height/2));
		
		JPanel centerButton = new JPanel();
		centerButton.setLayout(new FlowLayout(FlowLayout.LEFT));
		centerButton.add(changeCenterDataSetButton);

		rightPanel.add(centerButton);
		
		//create a time shift control field for all data sets that can be aligned to the first data set
		TimeshiftControl[] tsc = new TimeshiftControl[numDataSets - 1];
		
		Dimension buttonSize2 = new Dimension(buttonSize.width/2, buttonSize.height);
		
		for(int i = 0; i < tsc.length; i++) {
			TimeshiftControl timeShiftControl = new TimeshiftControl(store, i);
			
			JPanel editPanel = new JPanel();
			editPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

			tsc[i] = timeShiftControl;
			
			tsc[i].setMinimumSize(buttonSize2);
			tsc[i].setMaximumSize(buttonSize2);
			
			alignmentEditorButtons[i].setMinimumSize(buttonSize2);
			alignmentEditorButtons[i].setMaximumSize(buttonSize2);
			
			editPanel.add(tsc[i]);
			editPanel.add(alignmentEditorButtons[i]);
			
			rightPanel.add(editPanel);
		}
		
		rightPanel.setPreferredSize(new Dimension(buttonSize.width+20, zahlenstrahl.getPreferredSize().height));
		this.add(rightPanel, BorderLayout.EAST);
	}

	public void dispose() {
		zahlenstrahl.dispose();
	}
}
