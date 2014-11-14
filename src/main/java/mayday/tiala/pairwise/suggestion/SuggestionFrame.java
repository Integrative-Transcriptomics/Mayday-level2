package mayday.tiala.pairwise.suggestion;

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.gui.MaydayDialog;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.tiala.pairwise.data.AlignedDataSets;

@SuppressWarnings("serial")
public class SuggestionFrame extends MaydayDialog {
	
	protected AlignedDataSets result;
	
	public SuggestionFrame( Collection<ScoredAlignment> cr, DistanceMeasurePlugin distance ) {
		GroupLayout layout = new GroupLayout(getContentPane());
		setLayout(layout);
		
		setTitle("Suggested Alignments ("+distance+")");
		
		JLabel titleLabel;
		if (cr.size()>0)
			titleLabel = new JLabel("Distances were computed for "+cr.iterator().next().getDistances().size()+" probes. \n"
					+"Please select an alignment from the list");
		else
			titleLabel = new JLabel("No possible alignments found");
		
		
		final JList suggestions = new JList(cr.toArray());
		JScrollPane suggestionPane = new JScrollPane(suggestions);
		final SuggestionScoreView histogram = new SuggestionScoreView();
		
		JButton okButton = new JButton(new AbstractAction("OK"){
			public void actionPerformed(ActionEvent e) {
				result = ((ScoredAlignment)suggestions.getSelectedValue()).getAlignment();
				dispose();
			} 
		});
		
		JButton cancelButton = new JButton(new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				result = null;
				dispose();
			} 
		});
		
		suggestions.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				ScoredAlignment sa = ((ScoredAlignment)suggestions.getSelectedValue());
				histogram.setValues(sa);
			}			
		});
		
		suggestions.setSelectedIndex(0);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(titleLabel)
				.addComponent(suggestionPane)
				.addComponent(histogram)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(cancelButton)
						.addComponent(okButton)
				)
		);
				
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(titleLabel)
				.addComponent(suggestionPane)
				.addComponent(histogram)
				.addGroup(layout.createSequentialGroup()
						.addComponent(cancelButton)
						.addComponent(okButton)
				)
		);
		
		pack();
		
	}

	public AlignedDataSets getResult() {
		return result;
	}
	
}
