package mayday.tiala.pairwise.gui;

import java.awt.BorderLayout;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.core.gui.MaydayDialog;
import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.gui.controls.ManualAlignmentButton;
import mayday.tiala.pairwise.gui.controls.OnlyMatchingControl;
import mayday.tiala.pairwise.gui.controls.ScoreDistanceControl;
import mayday.tiala.pairwise.gui.controls.ScoreSetControl;
import mayday.tiala.pairwise.gui.controls.SuggestionButton;
import mayday.tiala.pairwise.gui.controls.TimeshiftControl;
import mayday.tiala.pairwise.gui.views.AlignmentMatchesLabel;
import mayday.tiala.pairwise.gui.views.ScoreView;

@SuppressWarnings("serial")
public class AlignmentEditingDialog extends MaydayDialog {
	
	public AlignmentEditingDialog( AlignmentStore store ) {
		setLayout(new BorderLayout());
		setTitle("Alignment Properties");
		
		add(makeBasicPanel(store), BorderLayout.NORTH);
		add(makeExtendedPanel(store), BorderLayout.CENTER);

		pack();		

	}
	
	protected JPanel makeBasicPanel(AlignmentStore store) {		
		JPanel basicPart = new JPanel();		
		GroupLayout layout;
		basicPart.setLayout(layout = new GroupLayout(basicPart));		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);		
		
		AlignmentMatchesLabel alignmentMatchesLabel = new AlignmentMatchesLabel(store);
		JLabel shiftLabel = new JLabel("Shift of "+store.getOne().getDataSet().getName());
		TimeshiftControl timeShiftControl = new TimeshiftControl(store);
		//ScoreSetControl scoreSetControl = new ScoreSetControl(store);
		OnlyMatchingControl onlyMatchingControl = new OnlyMatchingControl(store);
		ManualAlignmentButton manualAlignmentButton = new ManualAlignmentButton(store);
		SuggestionButton suggestionButtion = new SuggestionButton(store);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addComponent(alignmentMatchesLabel)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(shiftLabel)
						.addComponent(timeShiftControl)
				    )
					.addComponent(onlyMatchingControl)
					.addGroup(layout.createParallelGroup()
							.addComponent(manualAlignmentButton)
							.addComponent(suggestionButtion)
					)
		);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addComponent(alignmentMatchesLabel)
					.addGroup(layout.createSequentialGroup()
						.addComponent(shiftLabel)
						.addComponent(timeShiftControl)
				    )
					.addComponent(onlyMatchingControl)
					.addGroup(layout.createSequentialGroup()
							.addComponent(manualAlignmentButton)
							.addComponent(suggestionButtion)
					)
		);
		
		return basicPart;
	}
	
	protected JPanel makeExtendedPanel(AlignmentStore store) {
		ShowHidePanel shp = new ShowHidePanel(this, false);
		JPanel p = shp.getPanel();
		p.setLayout(new GroupLayout(p));
		GroupLayout layout = ((GroupLayout)p.getLayout());
		
		ScoreView scoreView = new ScoreView(store);
		ScoreDistanceControl scoreDistanceControl = new ScoreDistanceControl(store);
		ScoreSetControl scoreSetControl = new ScoreSetControl(store);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(scoreView)
				.addComponent(scoreSetControl)
				.addComponent(scoreDistanceControl)
		);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(scoreSetControl)
				.addComponent(scoreDistanceControl)
				.addComponent(scoreView)
		);
		
		
		return shp;
	}

}
