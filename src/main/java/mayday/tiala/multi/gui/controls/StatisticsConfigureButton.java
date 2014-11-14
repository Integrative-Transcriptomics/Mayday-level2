package mayday.tiala.multi.gui.controls;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.core.gui.MaydayDialog;
import mayday.core.settings.Settings;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;

/**
 * 
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class StatisticsConfigureButton extends JButton implements AlignmentStoreListener {
	
	public AlignmentStore store;
	private final int number;
	
	public StatisticsConfigureButton(final int number, final AlignmentStore Store) {
		super(new AbstractAction("Configure") {
			public void actionPerformed(ActionEvent e) {
				Settings s = Store.getProbeStatistic(number).getSettings();
				MaydayDialog md = s.getDialog(null, "Statistics settings");
				md.setVisible(true);
			}
		});
		store = Store;
		this.number = number;
		setEnabled(store.getProbeStatistic(number).hasSettings());
	}
	
	public void alignmentChanged(AlignmentStoreEvent evt) {
		switch(evt.getChange()) {
		case AlignmentStoreEvent.STATISTIC_CHANGED:
			setEnabled(store.getProbeStatistic(number).hasSettings());
			break;
		case AlignmentStoreEvent.STORE_CLOSED:
			store.removeListener(this);
			break;
		}	
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
