package mayday.Reveal.filter.processors;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import mayday.Reveal.data.meta.SingleLocusResult;
import mayday.Reveal.data.meta.SingleLocusResult.Statistics;
import mayday.Reveal.filter.AbstractDataProcessor;
import mayday.Reveal.filter.StorageNodeStorable;
import mayday.Reveal.gui.IOptionPanelProvider;
import mayday.core.io.StorageNode;

/**
 * 
 * @author jaeger
 *
 */
public class SLRStatsFilter extends AbstractDataProcessor<SingleLocusResult.Statistics, Double> implements IOptionPanelProvider, StorageNodeStorable {
	
	private String[] filterValues = {"Beta", "SE", "R2", "t", "p"};
	private JComboBox valueSelectionBox;
	
	private int statValueIndex = 0;
	
	@Override
	public void dispose() {
		//nothing to do
	}

	@Override
	public StorageNode toStorageNode() {
		StorageNode parent = new StorageNode("SLRStatsFilter", "");
		parent.addChild("statValue", statValueIndex);
		return parent;
	}

	@Override
	public void fromStorageNode(StorageNode storageNode) {
		statValueIndex = Integer.parseInt(storageNode.getChild("statValue").Value);
	}

	@Override
	public JPanel getOptionPanel() {
		JPanel panel = new JPanel(new GridLayout(2,1));
		panel.setBorder(BorderFactory.createTitledBorder("Select the statistical value"));
		
		valueSelectionBox = new JComboBox(filterValues);
		valueSelectionBox.setSelectedIndex(statValueIndex);
		
		valueSelectionBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				statValueIndex = valueSelectionBox.getSelectedIndex();
				fireChanged();
			}
		});
		
		panel.add(valueSelectionBox);
		
		return panel;
	}

	@Override
	public Class<?>[] getDataClass() {
		return snpList == null ?
				null: new Class<?>[]{Double.class};
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return SingleLocusResult.Statistics.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return " " + filterValues[statValueIndex];
	}

	@Override
	protected Double convert(Statistics value) {
		switch(statValueIndex) {
		case 0: return value.beta;
		case 1: return value.se;
		case 2: return value.r2;
		case 3: return value.t;
		case 4: return value.p;
		}
		
		return null;
	}

	@Override
	public String getName() {
		return "SLR Statistics";
	}

	@Override
	public String getType() {
		return "data.snplist.filter.slrstatsfilter";
	}

	@Override
	public String getDescription() {
		return "Select the single locus statistical value which shall be used for filtering";
	}
}
