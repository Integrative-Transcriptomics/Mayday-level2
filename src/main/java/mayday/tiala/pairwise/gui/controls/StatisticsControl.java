package mayday.tiala.pairwise.gui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComboBox;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.AlignmentStoreEvent;
import mayday.tiala.pairwise.data.AlignmentStoreListener;
import mayday.tiala.pairwise.statistics.AbstractCombinationStatistic;
import mayday.tiala.pairwise.statistics.ProbeCombinationStatistic;

@SuppressWarnings("serial")
public class StatisticsControl extends JComboBox implements AlignmentStoreListener {
	
	protected AlignmentStore store;
	
	public StatisticsControl(AlignmentStore Store) {	

		store=Store;

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
				if (store!=null)
					store.setProbeStatistic(as);
			}
	    	
	    });
	    
	}
	
	public void updateSelection() {
		AlignmentStore hiddenstore = store; 
		store=null;
		for (int i=0; i!=getItemCount(); ++i) {
			ProbeCombinationStatistic dmt = (ProbeCombinationStatistic)(getItemAt(i));
			if (dmt.getClass().equals(hiddenstore.getProbeStatistic().getClass())) {
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
		if (evt.getChange()==AlignmentStoreEvent.STATISTIC_CHANGED)
			updateSelection();
	}
    
}
