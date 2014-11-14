package mayday.tiala.pairwise.gui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.tiala.pairwise.data.AlignedDataSets;
import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.suggestion.ScoredAlignment;
import mayday.tiala.pairwise.suggestion.SuggestionFrame;

@SuppressWarnings("serial")
public class SuggestionButton extends JButton  {
	
	public SuggestionButton(final AlignmentStore Store) {
		super(new AbstractAction("Suggestions") {		
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						final SuggestionFrame sf = new SuggestionFrame(ScoredAlignment.generateList(Store), Store.getScoringFunction());
						sf.setVisible(true);
						sf.addWindowListener(new WindowAdapter() {
							public void windowClosed(WindowEvent evt) {
								AlignedDataSets ads  = sf.getResult();
								if (ads!=null)
									Store.setTimeShift(ads.getTimeShift());								
							}
						});
					}
				}.start();
			}
		});
	}

}
