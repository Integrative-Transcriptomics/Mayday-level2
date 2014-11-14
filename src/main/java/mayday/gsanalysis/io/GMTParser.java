package mayday.gsanalysis.io;

import java.io.BufferedReader;
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
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.maps.MultiHashMap;

public class GMTParser extends AbstractPlugin implements ProbelistPlugin{
	protected HierarchicalSetting setting;
	protected PathSetting genesetsFile;
	protected PathSetting annotationFile;
	protected MappingSourceSetting mapping;
	protected MasterTable masterTable;
	protected SelectableHierarchicalSetting select;
	
	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.GeneSetAnalysis.GMTParser",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Roland Keller",
				"no e-mail provided",
				"Load geneset annotations from GMT files",
		"Import genesets from GMT files");
		pli.addCategory("Geneset Analysis/Annotations");
		pli.getProperties().put(Constants.NO_CACHE_CLASS_INSTANCE, true);
		return pli;
	}
	public Setting initSetting() {
		genesetsFile = new PathSetting("Select gmt-File", null, null, false, true, false);
		annotationFile = new PathSetting("Select Annotation-File", null, null, false, true, false);
		mapping = new MappingSourceSetting(masterTable.getDataSet());
		select = new SelectableHierarchicalSetting("Use annotation file or Mapping?",null,0,new Setting[]{annotationFile,mapping});
		setting = new HierarchicalSetting("Import genesets from GMT files").addSetting(genesetsFile).addSetting(select);
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
	
	protected MultiHashMap<String,String> readChipFile(String path) throws Exception{
		MultiHashMap<String,String> mapping = new MultiHashMap<String,String>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = reader.readLine();
		if(line==null) {
			return null;
		}
		
		//searchColumnNumbers
		int columnID=-1;
		int columnGenename=-1;
		int columnAlias=-1; 
		
		ParserSettings settings = new ParserSettings();
		ParsedLine currentLine = new ParsedLine(line,settings);
		currentLine.replaceLine(line);
		
		for(int i=0;i!=currentLine.size();i++) {
			String columnName = currentLine.get(i);
			if(columnName.equals("Probe Set ID")) {
				columnID=i;
			}
			else if(columnName.equals("Gene Symbol")) {
				columnGenename=i;
			}
			else if(columnName.equals("Aliases")) {
				columnAlias=i;
			}
		}
		
		if(columnID==-1 || columnGenename==-1) {
			return null;
		}
		line = reader.readLine();
		while(line!=null) {
			currentLine.replaceLine(line);
			
			String [] geneNames = currentLine.get(columnGenename).split(" /// ");
			String probeID = currentLine.get(columnID);
			
			for(String geneName:geneNames) {
				mapping.put(geneName, probeID);
			}
			
			if(columnAlias!=-1) {
				String alias = currentLine.get(columnAlias);
				ParserSettings parser = new ParserSettings();
				parser.separator = ",";
				ParsedLine al = new ParsedLine(alias,parser);
				for(int i=0;i!=al.size();i++) {
					String aliasGeneName = al.get(i);
					mapping.put(aliasGeneName, probeID);
				}
			}
			line=reader.readLine();
			
		}
		reader.close();
		return mapping;
	}
	
	protected MultiHashMap<String,String> readGMTFile(String path) throws Exception{
		MultiHashMap<String,String> genesets = new MultiHashMap<String,String>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = reader.readLine();
		ParserSettings settings = new ParserSettings();
		ParsedLine currentLine = new ParsedLine(line,settings);
		while(line!=null) {
			currentLine.replaceLine(line);
			String genesetName = currentLine.get(0);
			//ignore description (second column)
			for(int i=2;i!=currentLine.size();i++) {
				String geneName = currentLine.get(i);
				genesets.put(geneName, genesetName);
			}
			line=reader.readLine();
		}
		reader.close();
		return genesets;
	}
	
	@Override
	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		this.masterTable=masterTable;
		SettingDialog sd = new SettingDialog(null,getClass().getName(),getSetting());
		sd.showAsInputDialog();
		MultiHashMap<String,String> genesets = null;
		if (sd.closedWithOK()) {
			if(genesetsFile.getStringValue().endsWith(".gmt")) {
				try{
					genesets=readGMTFile(genesetsFile.getStringValue());
				}
				catch(Exception e) {
				}
			}
			else {
				System.out.println("File type not supported");
			}
			if (genesets==null)
				throw new RuntimeException("Genesets could not be loaded.");
			String[] stringSplit = genesetsFile.getStringValue().split("/");
			String end = stringSplit[stringSplit.length-1];
			MIGroup migroup = masterTable.getDataSet().getMIManager().newGroup("PAS.MIO.StringList", "Genesets " + end);
			if(select.getSelectedIndex()==0) {
				try {
					if(!annotationFile.getStringValue().endsWith(".chip")) {
						return null;
					}
					MultiHashMap<String,String> geneNamesToProbes = readChipFile(annotationFile.getStringValue());;
					if(geneNamesToProbes != null) {
						System.out.println(geneNamesToProbes.size_everything());
						for(String geneName: genesets.keySet()) {
							for(String probe: geneNamesToProbes.get(geneName)) {
								Probe p = masterTable.getProbe(probe);
								if(p!=null) {
									migroup.add(p,new StringListMIO(genesets.get(geneName))); 
								}
								
							}
						}
					}
				}
				catch(Exception e) {
					
				}
			}
			else {
				if(mapping.getMappingSource()==MappingSourceSetting.PROBE_DISPLAY_NAMES) {
					for(Probe p: masterTable.getGlobalProbeList().getAllProbes()) {
						List<String> genesetsForProbe = genesets.get(p.getDisplayName());
						if(genesetsForProbe!=null && genesetsForProbe.size()!=0) {
							migroup.add(p,new StringListMIO(genesetsForProbe)); 
						}
					}
				}
				else if(mapping.getMappingSource()==MappingSourceSetting.PROBE_NAMES) {
					for(String probe: genesets.keySet()) {
						Probe p = masterTable.getProbe(probe);
						if(p!=null) {
							migroup.add(p,new StringListMIO(genesets.get(probe))); 
						}
					}
				}
				else {
					for(Entry<Object, MIType> e: mapping.getMappingGroup().getMIOs()) {
						if(e.getKey() instanceof Probe) {
							Probe p = (Probe)e.getKey();
							migroup.add(p,new StringListMIO(genesets.get(e.getValue().toString()))); 
						}
					}
				}
			}
				 
		}
		return null;
	}
	

}
