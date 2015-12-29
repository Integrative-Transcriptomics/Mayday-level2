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

public class MappedReadProperties extends AbstractPlugin {

	
	public void init() {}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.transkriptorium.properties.mappedread",
				new String[0],
				Constants.MC_PROPERTYDIALOG,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Mapped Reads Properties Dialog",
				"Mapped Read"
				);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey1, MappedRead.class);
		pli.getProperties().put(PropertiesDialogFactory.PropertyKey2, Dialog.class);
		return pli;
	}
	
	@SuppressWarnings("serial")
	public static class Dialog extends AbstractPropertiesDialog {

		public Dialog() {
			super();
			setTitle("Read Mapping Position Info");
		}
		
		@Override
		public void assignObject(Object o) {
			MappedRead mr = (MappedRead)o;
			InfoItem ni;
			
			ni = new ReadItem("Read Identifier", mr.getRead());
			addDialogItem( ni );
			
			String unique = mr.isUniqueMapping()?"This read is mapped <b>uniquely</b>.":"This read has other mapping positions.";
			String quality = "<html>Quality of the mapping: <b>"+mr.quality()+"</b>";
			ni = new InfoItem("Mapping information", quality+"<br>"+unique);
			addDialogItem( ni );
			
			String alignm = "<html>The read is mapped from position <b>"+mr.getStartInRead()+"</b> to <b>"+mr.getEndInRead();
			ni = new InfoItem("Alignment data", alignm);
			addDialogItem( ni );
			
			CoordinateItem ci = new CoordinateItem("Mapped target",mr.getTargetCoordinate());
			addDialogItem(ci);
			
			if (!mr.isUniqueMapping()) {
				List<MappedRead> otherMappings = new ArrayList<MappedRead>();
				Iterator<MappedRead> imr = mr.getAllReadMappings();
				while (imr.hasNext()) {
					MappedRead mmr = imr.next();
					if (!mmr.equals(mr))
						otherMappings.add(mmr);
				}
				addDialogItem(new MappingsListItem("Additional mapping coordinates of this read",otherMappings),1.0);
			} else {			
				addDialogItem(new FillerItem(), 1.0);
			}
			
		}

		@Override
		protected void doOKAction() {
			// nothing to do			
		}
		
	}

}
