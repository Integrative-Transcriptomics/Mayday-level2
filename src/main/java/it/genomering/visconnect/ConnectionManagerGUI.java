package it.genomering.visconnect;

import it.genomering.structure.Genome;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.PlotPlugin;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.Visualizer;

public class ConnectionManagerGUI {
	
	public ConnectionManagerGUI(ConnectionManager cm) {

		ArrayList<Object> avm = new ArrayList<Object>();
		avm.add(null);
		avm.addAll(DataSetManager.singleInstance.getDataSets());
		
		for (LinkedList<Visualizer> cvm : Visualizer.openVisualizers.values())
			for (Visualizer viz : cvm)
				avm.add(viz.getViewModel());
				
		Object[] predef = avm.toArray();
		String[] predefNames = new String[predef.length];
		for (int i=0; i!=predef.length; ++i) {
			if (predef[i]==null)
				predefNames[i] = "(none)";
			else if (predef[i] instanceof ViewModel) {
				ViewModel vm = ((ViewModel)predef[i]);
				predefNames[i] = vm.getDataSet().getName()+", "+vm.getVisualizer().getName();
			} else if (predef[i] instanceof DataSet) {
				DataSet ds = ((DataSet)predef[i]);
				predefNames[i] = "Create new Visualizer for "+ds.getName();
			}
		}
		
		HierarchicalSetting hs = new HierarchicalSetting("Connect Genomes to Visualizers");
		for (Genome g : cm.getGenomes())
			hs.addSetting(makeSetting(predefNames, predef, g, cm));
		
		SettingDialog sd = new SettingDialog(null, hs.getName(), hs);
		sd.setModal(true);
		sd.setVisible(true);		
		
	}
	
	protected RestrictedStringSetting makeSetting(final String[] predefNames, final Object[] predef, final Genome g, final ConnectionManager cm) {
		
		ViewModel current = cm.getViewModel(g);
		
		int idx=0;
		for (int i=0; i!=predef.length; ++i)
			if (predef[i]==current)
				idx = i;
		
		final RestrictedStringSetting s = new RestrictedStringSetting(g.getName(), null, idx, predefNames);
		s.setLayoutStyle(ObjectSelectionSetting.LayoutStyle.COMBOBOX);
		
		s.addChangeListener(new SettingChangeListener() {

			@Override
			public void stateChanged(SettingChangeEvent e) {
				Object sel = (predef[s.getSelectedIndex()]);
				ViewModel vm = null;
				if (sel!=null) {
					if (sel instanceof DataSet) {
						// create new Visualizer
						DataSet ds = (DataSet)sel;
						ProbeList all = ds.getProbeListManager().getProbeLists().get(0); // this is always global
						PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.zippi.GenomeBrowser");
						PlotPlugin pp = (PlotPlugin)pli.getInstance();						
						Component pc = pp.getComponent();
						Visualizer viz = Visualizer.createWithPlot(ds, Arrays.asList(new ProbeList[]{all}), pc);
						vm = viz.getViewModel();						
					} else {
						vm = (ViewModel)sel;
					}
				}	
				cm.map(g, vm);
			}
			
		});
		
		return s;
	}
	
	protected static class NoVisualizer extends Visualizer {
		
	}

}
