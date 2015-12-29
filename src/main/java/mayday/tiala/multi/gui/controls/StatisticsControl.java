package mayday.tiala.multi.gui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComboBox;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;
import mayday.tiala.multi.statistics.AbstractCombinationStatistic;
import mayday.tiala.multi.statistics.ProbeCombinationStatistic;

/**
 * 
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class StatisticsControl extends JComboBox implements AlignmentStoreListener {
	
	protected AlignmentStore store;
	protected final int number;
	
	public StatisticsControl(final int number, AlignmentStore Store) {	

		store = Store;
		this.number = number; 

		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(AbstractCombinationStatistic.MC);
		Set<AbstractPlugin> apls = new TreeSet<AbstractPlugin>();
		for (PluginInfo pli : plis)
			apls.add(pli.getInstance());
		
		removeAllItems();
		for (Object o : apls)
			addItem(o);

		updateSelection();

	    addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ProbeCombinationStatistic as; 
				as = ((ProbeCombinationStatistic)getSelectedItem());
				if (store != null)
					store.setProbeStatistic(number, as);
			}
	    	
	    });
	}
	
	public void updateSelection() {
		AlignmentStore hiddenstore = store; 
		store = null;
		for (int i=0; i!=getItemCount(); ++i) {
			ProbeCombinationStatistic dmt = (ProbeCombinationStatistic)(getItemAt(i));
			if (dmt.getClass().equals(hiddenstore.getProbeStatistic(number).getClass())) {
				setSelectedIndex(i);
				break;
			}
		}
		store = hiddenstore;
	}
	
	public void removeNotify() {
		super.removeNotify();
		store.removeListener(this);
	}
	
	public void addNotify() {
		super.addNotify();
	    store.addListener(this);
	}

	public void alignmentChanged(AlignmentStoreEvent evt) {
		switch(evt.getChange()) {
		case AlignmentStoreEvent.STATISTIC_CHANGED:
			updateSelection();
			break;
		case AlignmentStoreEvent.STORE_CLOSED:
			store.removeListener(this);
			break;
		}
	}  
}
