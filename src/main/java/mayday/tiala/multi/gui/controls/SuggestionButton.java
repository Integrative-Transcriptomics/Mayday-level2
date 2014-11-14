package mayday.tiala.multi.gui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.tiala.multi.data.AlignedDataSets;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.suggestion.ScoredAlignment;
import mayday.tiala.multi.suggestion.SuggestionFrame;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class SuggestionButton extends JButton  {
	
	/**
	 * @param datasetID
	 * @param Store
	 */
	public SuggestionButton(final int datasetID, final AlignmentStore Store) {
		super(new AbstractAction("Suggestions") {		
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						final SuggestionFrame sf = new SuggestionFrame(ScoredAlignment.generateList(datasetID, Store), Store.getScoringFunction(datasetID));
						sf.setVisible(true);
						sf.addWindowListener(new WindowAdapter() {
							public void windowClosed(WindowEvent evt) {
								AlignedDataSets ads  = sf.getResult();
								if (ads!=null) {
									Store.setTimeShift(datasetID, ads.getTimeShifts().get(datasetID));	
								}								
							}
						});
					}
				}.start();
			}
		});
	}
}
