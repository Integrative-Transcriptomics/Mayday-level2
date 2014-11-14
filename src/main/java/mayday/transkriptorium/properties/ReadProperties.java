package mayday.transkriptorium.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.gui.properties.items.InfoItem;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.transkriptorium.data.MappedRead;
import mayday.transkriptorium.data.Read;

public class ReadProperties extends AbstractPlugin {

	
	public void init() {}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.transkriptorium.properties.read",
				new String[0],
				Constants.MC_PROPERTYDIALOG,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads Properties Dialog",
				"Read"
				);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey1, Read.class);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey2, Dialog.class);
		return pli;
	}
	
	@SuppressWarnings("serial")
	public static class Dialog extends AbstractPropertiesDialog {

		public Dialog() {
			super();
			setTitle("Read Info");
		}
		
		@Override
		public void assignObject(Object o) {
			Read mr = (Read)o;
			InfoItem ni;
			
			ni = new InfoItem("Read Identifier", mr.getIdentifier());
			addDialogItem( ni );
			
			Read partner = mr.getPartner();
			if (partner!=null) {
				ni = new ReadItem("Mate pair", partner);
			} else {
				ni = new InfoItem("Mate pair", "This read has no mapped partner, or the data contains no mate-pairs.");
			}
			addDialogItem(ni);
			
			String unique = mr.hasUniqueMapping()?"<html>This read is mapped <b>uniquely</b>.":"This read has multiple mapping positions.";
			ni = new InfoItem("Mapping information", unique);
			addDialogItem( ni );

			List<MappedRead> allMappings = new ArrayList<MappedRead>();
			Iterator<MappedRead> imr = mr.getAllMappings();
			while (imr.hasNext()) {
				MappedRead mmr = imr.next();
					allMappings.add(mmr);
			}
			addDialogItem(new MappingsListItem("Mapping coordinates of this read",allMappings),1.0);
			
		}

		@Override
		protected void doOKAction() {
			// nothing to do			
		}
		
	}

}
