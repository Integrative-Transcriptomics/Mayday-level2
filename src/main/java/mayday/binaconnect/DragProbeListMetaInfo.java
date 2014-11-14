package mayday.binaconnect;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.HashMap;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.dragndrop.DragSupportPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class DragProbeListMetaInfo extends AbstractPlugin implements
		DragSupportPlugin {

	public void init() {
		 try {
			FLAVOR = new DataFlavor("probelist/meta-data;class=java.lang.String");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not create dataflavor for mayday probelist d&d support.");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.D&D.ProbeListMetaInfoFlavor",
				new String[0],
				DragSupportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Implements meta-info drag support for probelists to connect to BiNA",
				"ProbeList meta-data for BiNA"
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
		StringBuilder sb = new StringBuilder();
		for (Object o : input) {
			ProbeList pl = (ProbeList)o;
			sb.append(protect(pl.getName()));
			sb.append("\t");
			for (Probe pb : pl.getAllProbes()) {
				sb.append(pb.getDisplayName());
				sb.append(",");
			}
			sb.append("\t");
			for (MIGroup mg : pl.getDataSet().getMIManager().getGroupsForObject(pl)) {
				sb.append(mg.getPath());
				sb.append("/");
				sb.append(mg.getName());
				sb.append("=");
				sb.append(protect(mg.getMIO(pl).serialize(MIType.SERIAL_TEXT)));
				sb.append("\t");
			}
			sb.append("\n");
		}
		return sb.toString().trim();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] processDrop(Class<T> targetClass, Transferable t) {
		return (T[])new Object[0];
	}
	
	public void setContext(Object contextObject) {
		// no context is needed
	}
	
	protected static String protect(String s) {
		return s.replaceAll("=", "&equals;").replaceAll("\t", " ").replaceAll("\n", "<br>");
	}

}
