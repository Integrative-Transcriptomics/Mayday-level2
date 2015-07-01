package mayday.Reveal.actions.snplist;

import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.Reveal.data.SNVList;
import mayday.Reveal.filter.DataProcessors;
import mayday.Reveal.filter.Rule;
import mayday.Reveal.filter.RuleSet;
import mayday.Reveal.filter.DataProcessors.Item;
import mayday.Reveal.filter.processors.ContainedInSNPListFilter;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;

public class NewSNVList extends SNVListPlugin {

	@Override
	public String getName() {
		return "New SNVList";
	}

	@Override
	public String getType() {
		return "data.snvlist.newSNVList";
	}

	@Override
	public String getDescription() {
		return "Create a new SNVList";
	}

	@Override
	public String getMenuName() {
		return "Add";
	}

	@Override
	public void run(Collection<SNVList> snpLists) {
		SNVList snpList = new SNVList("New SNVList", projectHandler.getSelectedProject());
		
		RuleSet parent = snpList.getRuleSet();
		
        Item i = DataProcessors.getProcessorByID("mayday.Reveal.data.snplist.filter." + ContainedInSNPListFilter.MYTYPE);
        
        if(projectHandler.getSelectedProject() == null) {
        	JOptionPane.showMessageDialog(null, "Can't create SNVList. There is no active project!");
        	return;
        }
        
        SNVList globalSNPList = projectHandler.getSelectedProject().getGlobalSNVList();
        Rule r = new Rule(snpList);
        ContainedInSNPListFilter ciSL = (ContainedInSNPListFilter)i.newInstance(globalSNPList);
        ciSL.setCurrentSNPList(globalSNPList);
        r.addProcessor(ciSL);
        
        parent.addSubRule(r);
        
        AbstractPropertiesDialog apd = PropertiesDialogFactory.createDialog(snpList);
        apd.setModal(true);
        apd.setVisible(true);
        
        if(!apd.isCancelled()) {
            projectHandler.getSelectedProject().addSNVList(snpList.getAttribute().getName(), snpList);        	
        }
	}
}
