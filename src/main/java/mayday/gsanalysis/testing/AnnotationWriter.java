package mayday.gsanalysis.testing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.NominalMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.settings.typed.PathSetting;

@PluginManager.IGNORE_PLUGIN
public class AnnotationWriter extends AbstractPlugin implements ProbelistPlugin{
	protected HierarchicalSetting setting;
	protected PathSetting annotationFile;
	protected MasterTable masterTable;
	protected MIGroupSetting annotationMIGroup;
	
	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.GeneSetAnalysis.AnnotationWriter",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Roland Keller",
				"no e-mail provided",
				"Plugin for Writing of Annotation Files",
		"Annotation Writer");
		pli.addCategory("Geneset Analysis");
		pli.getProperties().put(Constants.NO_CACHE_CLASS_INSTANCE, true);
		return pli;
	}
	public Setting initSetting() {
		annotationFile = new PathSetting("Select file to write", null, null, false, false, true);
		annotationMIGroup = new MIGroupSetting("MIGroup for Annotation",null,null,masterTable.getDataSet().getMIManager(),false);
		annotationMIGroup.setAcceptableClass(NominalMIO.class);
		setting = new HierarchicalSetting(getClass().getName()).addSetting(annotationFile).addSetting(annotationMIGroup);
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
	
	protected void writeAnnotationFile(String path) throws Exception{
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		writer.write("Probe Set ID\tGene Symbol");
		writer.newLine();
		MIGroup migroup = annotationMIGroup.getMIGroup();
		
		for(Entry<Object, MIType> e:migroup.getMIOs()) {
			if(e.getKey() instanceof Probe) {
				writer.write(((Probe)e.getKey()).getName() + "\t");
				writer.write(e.getValue().toString() + "\t");
				writer.newLine();
			}
		}
		writer.close();
	}
	
	@Override
	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		this.masterTable=masterTable;
		SettingDialog sd = new SettingDialog(null,getClass().getName(),getSetting());
		sd.showAsInputDialog();
		if (sd.closedWithOK()) {
			if(annotationFile.getStringValue().endsWith(".chip")) {
				try{
					writeAnnotationFile(annotationFile.getStringValue());
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