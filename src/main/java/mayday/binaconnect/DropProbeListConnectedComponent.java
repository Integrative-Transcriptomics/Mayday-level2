package mayday.binaconnect;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.dragndrop.DragSupportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;

public class DropProbeListConnectedComponent extends AbstractPlugin implements
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
				"PAS.D&D.ProbeListConnectedComponentFlavor",
				new String[0],
				DragSupportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Implements drop support for connected components from BiNA",
				"ProbeList via connected graph components from BiNA"
				);
		return pli;
	}
	
	protected static DataFlavor FLAVOR;
	
	
	@Override
	public DataFlavor getSupportedFlavor() {
		return FLAVOR;
	}

	@Override
	public Class<?>[] getSupportedTransferObjects() {
		return new Class[]{ProbeList.class};
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
			
			DataSet ds = new DataSet(true);
			ds.setName("BiNA as connected component");
			MasterTable mt = new MasterTable(ds);
			
			LinkedList<ProbeList> lpl = new LinkedList<ProbeList>();

			while ((line=br.readLine())!=null) {
				String[] parts = line.split("\t");
				String label = parts[0];
				String[] ids = parts[1].split(",");
				ProbeList targetPL = new ProbeList(ds, false);
				targetPL.setName(label);
				
				for (String nameCandidate : ids) {
					nameCandidate = nameCandidate.trim();	
					if (targetPL.contains(nameCandidate))
						continue;
					Probe pb = new Probe(mt);
					pb.setName(nameCandidate);
					targetPL.addProbe(pb);
				}
				
				lpl.add(targetPL);
				
			}
			
			try {
				PluginInfo rcpli = PluginManager.getInstance().getPluginFromID("PAS.core.RecolorProbelists");
				if (rcpli != null)
					((ProbelistPlugin)rcpli.getInstance()).run(lpl, mt);
			} catch (Throwable anything) {}; 
			
			return (T[])lpl.toArray(new ProbeList[lpl.size()]);
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return (T[])new Object[0];
	}
	
	public void setContext(Object contextObject) {
		// no context is needed
	}


}
