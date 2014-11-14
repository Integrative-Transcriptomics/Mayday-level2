package mayday.binaconnect;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.HashMap;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.dragndrop.DragSupportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class DragProbeListCSV extends AbstractPlugin implements
		DragSupportPlugin {


	public void init() {
		 try {
			FLAVOR = new DataFlavor("probelist/value-matrix;class=java.lang.String");
		} catch (ClassNotFoundException e) {
			System.err.println("Could not create dataflavor for mayday probelist d&d support.");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.D&D.ProbeListCSVFlavorForBina",
				new String[0],
				DragSupportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Implements internal drag / drop support for probelists",
				"ProbeList CSV format for BiNA"
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
		return asTabular("\t", ((ProbeList[])input));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] processDrop(Class<T> targetClass, Transferable t) {
		return (T[])new Object[0];
	}

	protected String asTabular(String sep, ProbeList[] lists) {
		// serialize probelist matrix 
		StringBuilder sb = new StringBuilder();
		
		MasterTable mt = lists[0].getDataSet().getMasterTable();
		
		int noe = mt.getNumberOfExperiments();
		
		for (int i=0; i<noe; ++i) {
			sb.append(sep);
			sb.append(mt.getExperimentName(i));
		}
		
		sb.append("\n");
		
		for (ProbeList pl : lists) {
			for (Probe pb : pl.toCollection()) {
				sb.append(pb.getDisplayName());
				for (int i=0; i<noe; ++i) { 
					sb.append(sep);
					sb.append(pb.getValue(i));
				}
				sb.append("\n");					
			}		
		}
		
		return sb.toString();
	}

	@Override
	public void setContext(Object contextObject) {
		// no context is needed
	}
}
