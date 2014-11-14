package mayday.GWAS.filter.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import mayday.GWAS.filter.AbstractDataProcessor;
import mayday.GWAS.filter.DataProcessors;
import mayday.GWAS.filter.Rule;
import mayday.GWAS.gui.OptionPanelProvider;
import mayday.core.pluma.PluginManager;

@SuppressWarnings("serial")
public class DataProcessorGUI extends JPanel {
	protected Rule rule;
	@SuppressWarnings("rawtypes")
	protected AbstractDataProcessor dataSource;
	protected Set<DataProcessors.Item> nextCandidates;
	protected JComboBox selectedSource = new JComboBox();
	
	@SuppressWarnings("rawtypes")
	public DataProcessorGUI(Rule theRule, AbstractDataProcessor ads, Set<DataProcessors.Item> theNextCandidates) {
		dataSource = ads;
		nextCandidates = theNextCandidates;
		rule=theRule;
		init();
	}
	
	protected void init() {
		setLayout(new BorderLayout());		
		
		if (nextCandidates.size()==0) {
			selectedSource.addItem("-- No matching processors (is the previous processor configured correctly?)");
		} else {
			int selectedIndex=0;
			selectedSource.addItem("-- Please select a data processor");
			
			for (DataProcessors.Item candidate : nextCandidates) {
				selectedSource.addItem(candidate);
				if (dataSource!=null)
					if (candidate.getPluginInfo()==PluginManager.getInstance().getPluginFromClass(dataSource.getClass()))
							selectedIndex = selectedSource.getItemCount()-1;
			}
			selectedSource.setSelectedIndex(selectedIndex);
			selectedSource.addActionListener(new ActionListener() {

				@SuppressWarnings("rawtypes")
				public void actionPerformed(ActionEvent arg0) {
					Object selO = selectedSource.getSelectedItem();
					AbstractDataProcessor selected = null;
					if (selO instanceof DataProcessors.Item) {
						selected = ((DataProcessors.Item)selO).newInstance(rule.getSNPList());
						if (dataSource==null) {
							// add new source
							rule.addProcessor(selected);
						} else {
							// replace existing
							rule.removeProcessor(dataSource);
							rule.addProcessor(selected);
						}
					} else {
						if (dataSource!=null) //remove existing
							rule.removeProcessor(dataSource);
					}
				
				}
				
			});
		}
				
		add(selectedSource, BorderLayout.NORTH);
		
		if (dataSource!=null && dataSource instanceof OptionPanelProvider) {
			add(((OptionPanelProvider)dataSource).getOptionPanel(), BorderLayout.CENTER);
		}
					
	}
	
	public JComboBox getComboBox() {
		return selectedSource;
	}
}
