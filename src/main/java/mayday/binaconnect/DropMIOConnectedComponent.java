package mayday.binaconnect;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.gui.dragndrop.DragSupportPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.types.StringListMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.maps.MultiHashMap;

public class DropMIOConnectedComponent extends AbstractPlugin implements
		DragSupportPlugin {

	public void init() {
		 try {
			FLAVOR = new DataFlavor("graph/connected-components;class=java.lang.String");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not create dataflavor for mayday probelist d&d support.");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.D&D.MIOConnectedComponentFlavor",
				new String[0],
				DragSupportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Implements drop support for connected components from BiNA",
				"String List MIOs via connected graph components from BiNA (for GSEA)"
				);
		return pli;
	}
	
	protected static DataFlavor FLAVOR;
	
	protected MIManager targetManager;
	
	@Override
	public DataFlavor getSupportedFlavor() {
		return FLAVOR;
	}

	@Override
	public Class<?>[] getSupportedTransferObjects() {
		return new Class[]{MIGroup.class};
	}

	@Override
	public Object getTransferData(Object... input) {
		return "";
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] processDrop(Class<T> targetClass, Transferable t) {
		
		try {
			String s = t.getTransferData(FLAVOR).toString();
			BufferedReader br = new BufferedReader(new StringReader(s));
			String line;
			
			MasterTable mt = targetManager.getDataSet().getMasterTable();		
			MIGroup mg = targetManager.newGroup("PAS.MIO.StringList","BiNA connected components");

			MultiHashMap<String, Probe> byDisplayName = new MultiHashMap<String, Probe>();
			for (Probe pb : mt.getProbes().values())
				byDisplayName.put(pb.getDisplayName(), pb);
			
			
			while ((line=br.readLine())!=null) {
				String[] parts = line.split("\t");
				String label = parts[0];
				String[] ids = parts[1].split(",");
								
				for (String nameCandidate : ids) {
					nameCandidate = nameCandidate.trim();	
					Probe directTarget = mt.getProbe(nameCandidate);
					
					if (directTarget!=null) {
						StringListMIO slm = (StringListMIO)mg.getMIO(directTarget);
						if (slm==null)
							mg.add(directTarget, slm=new StringListMIO());
						slm.getValue().add(label);
					} else {
						Collection<Probe> indirectTarget = null;
						indirectTarget = byDisplayName.get(nameCandidate);
						for (Probe pb : indirectTarget) {
							StringListMIO slm = (StringListMIO)mg.getMIO(pb);
							if (slm==null)
								mg.add(pb, slm=new StringListMIO());
							slm.getValue().add(label);
						}
					}
				}
			}
			
			return (T[])new MIGroup[]{mg};
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return (T[])new Object[0];
	}

	public void setContext(Object contextObject) {
		targetManager = (MIManager)contextObject;
	}

	
}
