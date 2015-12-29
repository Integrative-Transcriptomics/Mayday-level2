package mayday.gsanalysis.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.PreferencePane;
import mayday.core.io.csv.ParsedLine;
import mayday.core.io.csv.ParserSettings;
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
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.maps.MultiHashMap;

//TODO: describe what this does, comment options

public class GAFGenericParser extends AbstractPlugin implements ProbelistPlugin{
	protected HierarchicalSetting setting;
	protected PathSetting GOAnnotationFile;
	protected MultiHashMap<String,String> synonyms;

	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.GeneSetAnalysis.GAFGenericParser",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Roland Keller",
				"no e-mail provided",
				"Plugin for parsing annotations from files in GAF format. ", 
				"Import annotations from GAF files");
		pli.addCategory("Geneset Analysis/Annotations");
		pli.getProperties().put(Constants.NO_CACHE_CLASS_INSTANCE, true);
		return pli;
	}
	public Setting initSetting() {
		GOAnnotationFile = new PathSetting("Annotation file (GAF format)", 
				"Select a GAF file that maps gene symbols to GO terms.", null, false, true, false);
		setting = new HierarchicalSetting("Import GO annotations").addSetting(GOAnnotationFile);
		return setting;
	}

	public PreferencePane getPreferencesPanel() {
		return null; 
	}

	public Setting getSetting() {
		if(setting == null) {
			return initSetting();
		}
		else {
			return setting;
		}
	}

	protected MultiHashMap<String,String> readGAFFile(String path) throws Exception{
		MultiHashMap<String,String> annotations = new MultiHashMap<String,String>();
		synonyms=new MultiHashMap<String,String>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = reader.readLine();
		ParserSettings settings = new ParserSettings();
		ParsedLine currentLine = new ParsedLine(line,settings);

		while(line!=null) {
			if((line.startsWith("!gaf"))||!(line.startsWith("!"))) {
				break;
			}
			line=reader.readLine();
		}

		if(line!=null && line.startsWith("!gaf")) {
			line = reader.readLine();
			while(line!=null) {
				currentLine.replaceLine(line);
				if(currentLine.size()>=5) {
					String qualifier = currentLine.get(3);
					if(qualifier==null || !(qualifier.equals("NOT"))) {
						String geneName = currentLine.get(2);
						String annotation = currentLine.get(4);
						annotations.put(geneName, annotation);

						if(currentLine.size()>=11 && currentLine.get(10)!=null) {
							String aliases = currentLine.get(10);
							for(String alias:aliases.split("[|]")) {
								synonyms.put(geneName, alias);
							}
						}
					}

				}
				line=reader.readLine();
			}
		}
		else {
			line = reader.readLine();
			while(line!=null) {
				currentLine.replaceLine(line);
				if(currentLine.size()>=4) {
					String geneName = currentLine.get(2);
					String annotation = currentLine.get(3);
					annotations.put(geneName, annotation);
				}
				else if(currentLine.size()>=2){
					String geneName = currentLine.get(0);
					String annotation = currentLine.get(1);
					annotations.put(geneName, annotation);
				}
				line=reader.readLine();
			}
		}
		reader.close();
		return annotations;
	}

	@Override
	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable){
		SettingDialog sd = new SettingDialog(null,getClass().getName(),getSetting());
		sd.showAsInputDialog();
		MultiHashMap<String,String> annotations = null;
		if (sd.closedWithOK()) {
			try{
				annotations=readGAFFile(GOAnnotationFile.getStringValue());
				System.out.println(annotations.size());
			}
			catch(Exception e) {
				throw new RuntimeException("Error while reading the file");
			}			
			MIGroup migroup = masterTable.getDataSet().getMIManager().newGroup("PAS.MIO.StringList", new File(GOAnnotationFile.getStringValue()).getName());

			for(String probeName: annotations.keySet()) {
				boolean found=false;
				Probe p = getProbe(masterTable, probeName);
				
				if(p!=null) {
					migroup.add(p,new StringListMIO(annotations.get(probeName))); 
					found=true;
				}


				if(!found) {
					for(String synonym:synonyms.get(probeName)) {
						p = getProbe(masterTable, probeName);
						if(p!=null) {
							migroup.add(p,new StringListMIO(annotations.get(probeName))); 
						}
					}
				}

			}
		}
		return null;
	}
	
	protected HashMap<String, Probe> byDisplayName = new HashMap<String, Probe>();
	
	protected Probe getProbe(MasterTable mt, String probeName) {
		Probe p = mt.getProbe(probeName);		
		if (p!=null) 
			return p;
		if (byDisplayName.isEmpty() && mt.getDataSet().getProbeDisplayNames()!=null) {
			for (Entry<Object, MIType> e: mt.getDataSet().getProbeDisplayNames().getMIOs()) {
				if (e.getValue().toString().equals(probeName))
					return (Probe)e.getKey();
			}
		}		
		return null;
	}

}

