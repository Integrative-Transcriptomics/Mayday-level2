package mayday.tiala.pairwise.gui.controls;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.core.gui.MaydayDialog;
import mayday.core.settings.Settings;
import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.AlignmentStoreEvent;
import mayday.tiala.pairwise.data.AlignmentStoreListener;

@SuppressWarnings("serial")
public class StatisticsConfigureButton extends JButton implements AlignmentStoreListener {
	
	public AlignmentStore store;
	
	public StatisticsConfigureButton(final AlignmentStore Store) {
		super(new AbstractAction("Configure") {
			public void actionPerformed(ActionEvent e) {
				Settings s = Store.getProbeStatistic().getSettings();
				MaydayDialog md = s.getDialog(null, "Statistics settings");
				md.setVisible(true);
			}
		});
		store = Store;
		setEnabled(store.getProbeStatistic().hasSettings());
	}
		
	
	public void alignmentChanged(AlignmentStoreEvent evt) {
		if (evt.getChange()==AlignmentStoreEvent.STATISTIC_CHANGED)
			setEnabled(store.getProbeStatistic().hasSettings());
	}
	
	public void removeNotify() {
		super.removeNotify();
		store.removeListener(this);
	}
	
	public void addNotify() {
		super.addNotify();
	    store.addListener(this);
	}

}
