package mayday.gsanalysis.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.PreferencePane;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringListMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.maps.MultiHashMap;

public class GMTWriter extends AbstractPlugin implements ProbelistPlugin{
	protected HierarchicalSetting setting;
	protected PathSetting genesetsFile;
	protected MappingSourceSetting mapping;
	protected MasterTable masterTable;
	protected SelectableHierarchicalSetting select;
	protected MIGroupSetting genesetMIGroup;
	
	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.GeneSetAnalysis.GMTWriter",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Roland Keller",
				"no e-mail provided",
				"Export geneset annotations from GMT files",
		"Export genesets to GMT files");
		pli.addCategory("Geneset Analysis/Annotations");
		pli.getProperties().put(Constants.NO_CACHE_CLASS_INSTANCE, true);
		return pli;
	}
	public Setting initSetting() {
		genesetsFile = new PathSetting("Select file to write", null, null, false, false, true);
		mapping = new MappingSourceSetting(masterTable.getDataSet());
		genesetMIGroup = new MIGroupSetting("Gene Sets",null,null,masterTable.getDataSet().getMIManager(),false);
		setting = new HierarchicalSetting("Export genesets to GMT files").addSetting(genesetsFile).addSetting(mapping).addSetting(genesetMIGroup);
		return setting;
	}
	
	public Setting getSetting() {
		if(setting == null) {
			return initSetting();
		}
		else {
			return setting;
		}
	}
	
	@Override
	public PreferencePane getPreferencesPanel() {
		return null;
	}

	
	protected void writeGMTFile(String path) throws Exception{
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		MultiHashMap<String,String> genesets = new MultiHashMap<String,String>();
		MIGroup migroup = genesetMIGroup.getMIGroup();
		
		for(Entry<Object, MIType> e:migroup.getMIOs()) {
			if(e.getKey() instanceof Probe) {
				if(mapping.getMappingSource()==MappingSourceSetting.PROBE_NAMES) {
					if(migroup.getMIOClass().getName().equals("mayday.core.meta.types.StringMIO")) {
						genesets.put(e.getValue().toString(),((Probe)e.getKey()).getName());
					}
					else if(migroup.getMIOClass().getName().equals("mayday.core.meta.types.StringListMIO")){
						StringListMIO stringList = (StringListMIO)e.getValue();
						for(String name:stringList.getValue()) {
							genesets.put(name,((Probe)e.getKey()).getName());
							
						}
					}
				}
				else if(mapping.getMappingSource()==MappingSourceSetting.PROBE_DISPLAY_NAMES) {
					if(migroup.getMIOClass().getName().equals("mayday.core.meta.types.StringMIO")) {
						genesets.put(e.getValue().toString(),((Probe)e.getKey()).getDisplayName());
					}
					else if(migroup.getMIOClass().getName().equals("mayday.core.meta.types.StringListMIO")){
						StringListMIO stringList = (StringListMIO)e.getValue();
						for(String name:stringList.getValue()) {
							genesets.put(name,((Probe)e.getKey()).getDisplayName());
							
						}
					}
				}
				else {
					MIType mio = mapping.getMappingGroup().getMIO(e.getKey());
					if(mio!=null) {
						if(migroup.getMIOClass().getName().equals("mayday.core.meta.types.StringMIO")) {
							genesets.put(e.getValue().toString(),mio.toString());
						}
						else if(migroup.getMIOClass().getName().equals("mayday.core.meta.types.StringListMIO")){
							StringListMIO stringList = (StringListMIO)e.getValue();
							for(String name:stringList.getValue()) {
								genesets.put(name,mio.toString());
								
							}
						}
					}
				}
			}
 		}
		System.out.println(genesets.size_everything());
		for(String geneset: genesets.keySet()) {
			writer.write(geneset + "\t");
			writer.write("na\t");
			for(String gene: genesets.get(geneset)) {
				writer.write(gene + "\t");
			}
			writer.newLine();
		}
		writer.close();
	
		
	}
	
	@Override
	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		this.masterTable=masterTable;
		SettingDialog sd = new SettingDialog(null,getClass().getName(),getSetting());
		sd.showAsInputDialog();
		if (sd.closedWithOK()) {
			if(genesetsFile.getStringValue().endsWith(".gmt")) {
				try{
					writeGMTFile(genesetsFile.getStringValue());
				}
				catch(Exception e) {
				}
			}
			else {
				System.out.println("File type not supported");
			}
		}
		return null;
	}
	

}