package mayday.GWAS.filter.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.GWAS.data.SNPList;
import mayday.core.gui.properties.items.AbstractPropertiesItem;

@SuppressWarnings("serial")
public class RuleSetEditorItem extends AbstractPropertiesItem {

	public RuleSetEditorItem() {
		super("SNP Rule Set");
	}
	
	private SNPList snpList;
	private RuleEditorPanel rep;
	
	public RuleSetEditorItem(SNPList snpList) {
		this();
		setLayout(new BorderLayout());
		this.snpList = snpList;
		rep = new RuleEditorPanel(snpList);
		add(rep, BorderLayout.CENTER);
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public void setValue(Object value) {}

	@Override
	public boolean hasChanged() {
		return false;
	}
	
	public void apply() {
		rep.doExternalApply();
	}
	
	protected class OpenEditorAction extends AbstractAction {
		public OpenEditorAction() {
			super( "Open Rule Set Editor" );  		
		}		  	
		public void actionPerformed( ActionEvent event ) {
			new RuleEditorDialog(snpList).setVisible(true);
		}
	}

}
