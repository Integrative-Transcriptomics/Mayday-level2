package mayday.Reveal.filter.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;

import javax.swing.JOptionPane;

import mayday.Reveal.data.SNPList;
import mayday.Reveal.events.SNPListEvent;
import mayday.Reveal.events.SNPListListener;
import mayday.Reveal.listeners.DataStorageEvent;
import mayday.Reveal.listeners.DataStorageListener;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.gui.properties.items.NameItem;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class SNPListProperties extends AbstractPlugin {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.GWAS.properties.snplist",
				new String[0],
				Constants.MC_PROPERTYDIALOG,
				new HashMap<String, Object>(),
				"Günter Jäger",
				"jaeger@informatik.uni-tuebingen.de",
				"SNP List Properties Dialog",
				"SNPList"
				);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey1, SNPList.class);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey2, Dialog.class);
		return pli;
	}

	@Override
	public void init() {}
	
	@SuppressWarnings("serial")
	public static class Dialog extends AbstractPropertiesDialog {

		public Dialog() {
			super();
			setTitle("SNPList Properties");
		}
		
		private SNPList snpList;
		private NameItem ni;
		private RuleSetEditorItem rep;
		
		@Override
		public void assignObject(Object o) {
			snpList = (SNPList)o;
			ni = new NameItem(snpList.getAttribute().getName());
			
			final SNPListListener closingSNPListListener = new SNPListListener() {
				public void snpListChanged(SNPListEvent event) {
					if (event.getChange()==SNPListEvent.SNPLIST_CLOSED)
						dispose();
				}
			};
			final DataStorageListener closingDSListener = new DataStorageListener() {
				public void dataChanged(DataStorageEvent event) {
					if (event.getChange()==DataStorageEvent.CLOSING_CHANGE)
						dispose();
				}
			};
			snpList.addSNPListListener(closingSNPListListener);
			snpList.getDataStorage().addDataStorageListener(closingDSListener);
			this.addWindowListener(new WindowListener() {
				public void windowActivated(WindowEvent arg0) {}
				public void windowClosed(WindowEvent arg0) {
					snpList.removeSNPListListener(closingSNPListListener);
					snpList.getDataStorage().removeDataStorageListener(closingDSListener);
				}
				public void windowClosing(WindowEvent arg0) {}
				public void windowDeactivated(WindowEvent arg0) {}
				public void windowDeiconified(WindowEvent arg0) {}
				public void windowIconified(WindowEvent arg0) {}
				public void windowOpened(WindowEvent arg0) {}				
			});
			
			rep = new RuleSetEditorItem(snpList);
			
			addDialogItem( ni );
			//addDialogItem( new ProbeListItem(pl.getDataSet().getMasterTable(),pl), 1.0);
			addDialogItem(rep, .5);
//			addDialogItem(new RuleSetEditorOpenItem(pl));
//			addDialogItem( new MIOTableItem(pl,pl.getDataSet().getMIManager()), .5);
		}

		@Override
		protected void doOKAction() {
			String newName = (String)ni.getValue();
			
			// apply rule set changes
			rep.apply();
			
			// Change DataSet name
			if (newName.equals(snpList.getAttribute().getName()))
				return;
			
			while (snpList.getDataStorage().getSNPListNames().contains(newName)) {
				newName = JOptionPane.showInputDialog(null, "Enter a unique SNPList name: ",newName); 
			}
				
			if (newName != null) {
				snpList.getAttribute().setName(newName);
			}			
		}
	}
}
