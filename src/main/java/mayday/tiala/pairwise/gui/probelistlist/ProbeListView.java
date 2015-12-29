package mayday.tiala.pairwise.gui.probelistlist;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import mayday.core.ProbeList;
import mayday.core.gui.probelist.ProbeListSelectionDialog;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.tiala.pairwise.data.AlignmentStore;

@SuppressWarnings("serial")
public class ProbeListView extends JPanel {
	
	public ProbeListView(final AlignmentStore store) {
		super(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Probelists"));

		final DragNDropProbeListListBox dndpllb = new DragNDropProbeListListBox(store);
		add(dndpllb, BorderLayout.CENTER);
		
		JButton removeButton = new JButton(new AbstractAction("Remove") {

			public void actionPerformed(ActionEvent e) {				
				ArrayList<ProbeList> a = new ArrayList<ProbeList>();
				for (Object o : dndpllb.getSelectedValues())
					a.add((ProbeList)o);
				for (ProbeList pl : a)
					store.removeProbeListFromViewModels(pl);
				store.fireScoringChanged();
			}
			
		});
		
		JButton addButton = new JButton(new AbstractAction("Add") {
			public void actionPerformed(ActionEvent e) {
				Collection<ProbeListManager> plms  = new LinkedList<ProbeListManager>();
				plms.add(store.getOne().getDataSet().getProbeListManager());
				plms.add(store.getTwo().getDataSet().getProbeListManager());
				ProbeListSelectionDialog plsd = new ProbeListSelectionDialog(plms);
				plsd.setVisible(true);
				for (ProbeList pl : plsd.getSelection())
					store.addProbeListToViewModels(pl);
			}
		});
		
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.add(addButton);
		buttons.add(removeButton);
		buttons.setBackground(dndpllb.getBackground());
		
		add(buttons, BorderLayout.SOUTH);
		
		dndpllb.getModel().addListDataListener(new ListDataListener() {
			public void contentsChanged(ListDataEvent e) {
				store.fireScoringChanged();
			}

			public void intervalAdded(ListDataEvent e) {
				store.fireScoringChanged();
			}

			public void intervalRemoved(ListDataEvent e) {
				store.fireScoringChanged();
			}			
		});
	}

}
