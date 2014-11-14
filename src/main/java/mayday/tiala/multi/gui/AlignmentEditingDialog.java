package mayday.tiala.multi.gui;

import java.awt.BorderLayout;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.core.gui.MaydayDialog;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.gui.controls.ManualAlignmentButton;
import mayday.tiala.multi.gui.controls.OnlyMatchingControl;
import mayday.tiala.multi.gui.controls.ScoreDistanceControl;
import mayday.tiala.multi.gui.controls.ScoreSetControl;
import mayday.tiala.multi.gui.controls.SuggestionButton;
import mayday.tiala.multi.gui.controls.TimeshiftControl;
import mayday.tiala.multi.gui.views.AlignmentMatchesLabel;
import mayday.tiala.multi.gui.views.ScoreView;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class AlignmentEditingDialog extends MaydayDialog {
	
	protected final int number;
	
	/**
	 * @param number
	 * @param store
	 */
	public AlignmentEditingDialog(final int number, AlignmentStore store ) {
		this.number = number;
		
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
		
		//label for matching first and second dataset
		AlignmentMatchesLabel alignmentMatchesLabel = new AlignmentMatchesLabel(store);
		JLabel shiftLabel = new JLabel("Shift of "+store.get(number+1).getDataSet().getName());
		
		//timeshift control for alignment of first and second dataset
		TimeshiftControl timeShiftControl = new TimeshiftControl(store, number);
		OnlyMatchingControl onlyMatchingControl = new OnlyMatchingControl(store);
		ManualAlignmentButton manualAlignmentButton = new ManualAlignmentButton(store, number);
		SuggestionButton suggestionButtion = new SuggestionButton(number, store);
		
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
		
		ScoreView scoreView = new ScoreView(number, store);
		ScoreDistanceControl scoreDistanceControl = new ScoreDistanceControl(number, store);
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
