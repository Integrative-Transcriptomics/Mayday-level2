package mayday.gsanalysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import mayday.core.ClassSelectionModel;
import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.PreferencePane;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.meta.types.StringListMIO;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.structures.Pair;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;
import mayday.gsanalysis.gui.ResultFrame;

public abstract class AbstractGSAnalysisPlugin extends AbstractPlugin implements ProbelistPlugin{
	public final static String MC = "GeneSetAnalysis"; 
	protected ProbeList probes;
	protected List<String> probeListNames;
	protected ProbeList probesMasterTable;
	protected List<Geneset> genesets;
	protected HierarchicalSetting setting;
	protected MIGroupSetting genesetMIGroup;
	protected IntSetting minGenesetSize;
	protected IntSetting maxGenesetSize;
	protected Result result; 
	protected ResultFrame resultGUI;
	protected AbstractTask t;
	protected double progress;
	protected BooleanHierarchicalSetting writeInFile;
	protected boolean classesEnrichment;
	protected boolean genesetSetting;
	protected DataSet dataSet;
	protected HashSet<String> existingProbeNamesForGenesets = new HashSet<String>(); 

	
	public AbstractGSAnalysisPlugin() {
		genesetSetting=true;
		result = new Result();
		resultGUI = new ResultFrame(result,this);
	}
	
	public final List<ProbeList> resultAsProbeList() {
		if(genesets==null) {
			return  null;
		}
		List<ProbeList> lists = new LinkedList<ProbeList>();
		
		// 110122-fb: create migroups for annotation instead of using the quickinfo.
		ArrayList<MIGroup> migroups = new ArrayList<MIGroup>();
		
		int counter=0;
		if(result.getEnrichmentWithClasses()) {
			for(Pair<String,String> classes: result.getClassCombinations()) {
				
				String classString = classes.toString().replace(">"," ").replace("<"," ");
				
				for(Enrichment e:result.getSortedEnrichments(classes)) {
					if(e.isSignificant()) {
						ProbeList pl = new ProbeList(probesMasterTable.getDataSet(), false);
						pl.setName(e.getGeneset().getName());
						for(String gene: e.getGeneset().getGenes()) {
							pl.addProbe(probesMasterTable.getProbe(gene));
						}
						pl.getAnnotation().setQuickInfo("Significantly enriched category between "+classString);
						addInfo(pl, migroups, e);
						lists.add(pl);
						counter++;
						if(counter==10) {
							break;
						}
					}
				}
			}
		}
		else {
			for(Enrichment e:result.getSortedEnrichments(null)) {
				if(e.isSignificant()) {
					ProbeList pl = new ProbeList(probesMasterTable.getDataSet(), false);
					pl.setName(e.getGeneset().getName());
					for(String gene: e.getGeneset().getGenes()) {
						pl.addProbe(probesMasterTable.getProbe(gene));
					}
					pl.getAnnotation().setQuickInfo("Significantly enriched category");
					addInfo(pl, migroups, e);
					lists.add(pl);
					counter++;
					if(counter==10) {
						break;
					}
				}
			}
		}
		
		return lists;
		
	}
	
	protected void addInfo(ProbeList target, ArrayList<MIGroup> groups, Enrichment e) {
		if (groups.size()==0) {
			MIManager mim = target.getDataSet().getMIManager();
			for (String mName : e.getColumnIdentifiers()) {
				groups.add(mim.newGroup("PAS.MIO.Double", mName, "/Enrichment"));
			}
		}
		List<Double> mVals = e.getValues();
		for (int i=0; i!=groups.size(); ++i) {
			groups.get(i).add(target, new DoubleMIO(mVals.get(i)));
		}
	
	}
	
	
	public ResultFrame getResultGUI() {
		return resultGUI;
	}
	public abstract String getName();
	public abstract void calculateEnrichment();
	
	protected abstract List<Enrichment> calculateEnrichmentWithClasses(ClassSelectionModel csm);
	
	protected abstract List<Setting> additionalSettings();
	
	protected abstract String additionalPreferences();
	
	protected void removeSmallAndBigGenesets(int minSize, int maxSize,List<Geneset> genesets) {
		List<Geneset> genesetsToRemove= new LinkedList<Geneset>();
		for(Geneset g: genesets) {
			int containedGenes=0;
			List<String> genesToRemove = new LinkedList<String>();
			for(String gene: g.getGenes()) {
				if(existingProbeNamesForGenesets.contains(gene)) {
					containedGenes++;
				}
				else {
					genesToRemove.add(gene);
				}
			}
			g.removeGenes(genesToRemove);
			if(containedGenes<minSize||containedGenes>maxSize) {
				genesetsToRemove.add(g);
				g.setRemoved(true);
			}
		}
		genesets.removeAll(genesetsToRemove);
	}
	
	protected void enrichmentWithClasses(ClassSelectionModel csm) {
		if(classesEnrichment==false) {
			classesEnrichment=true;
		}
		for(int i=0; i!=csm.getNumClasses();i++) {
			for(int j=i+1;j!=csm.getNumClasses();j++) {
				ClassSelectionModel currentModel = csm;
				String class1 = csm.getClassNames().get(i);
				String class2 = csm.getClassNames().get(j);
				
				if(i!=0 || j!=1) {
					currentModel = new ClassSelectionModel();
					List<String> objects = new LinkedList<String>();
					TreeMap<String,String> classes = new TreeMap<String,String>();
					
					for(int position=0;position!=csm.getNumObjects();position++) {
						String currentClass = csm.getPartition().get(position);
						String currentObject = csm.getObjectName(position);
						if((currentClass!=null)&&(currentClass.equals(class1)||currentClass.equals(class2))) {
							currentModel.addObject(currentObject,currentClass);
						}
						else {
							objects.add(currentObject);
							classes.put(currentObject, currentClass);
						}
					}
					
					for(String objectName:objects) {
						currentModel.addObject(objectName,classes.get(objectName));
					}
 		
				}
				List<Enrichment> currentResults = calculateEnrichmentWithClasses(currentModel);
				result.addEnrichments(new Pair<String,String>(class1,class2),currentResults);
				
			}
		}
	}
	
	public Setting initSetting() {
		setting = new HierarchicalSetting(this.getName());
		if(genesetSetting) {
			genesetMIGroup = new MIGroupSetting("Gene Annotation","Select annotations that map genes to genesets",null,dataSet.getMIManager(),false);
			genesetMIGroup.setAcceptableClass(StringMIO.class, StringListMIO.class);
			setting.addSetting(genesetMIGroup);
		}
		for(Setting s:additionalSettings()) {
			setting.addSetting(s);
		}
		minGenesetSize = new IntSetting("Minimum geneset size", "All smaller genesets will be removed",15,1,100,true,true);
		maxGenesetSize = new IntSetting("Maximum geneset size", "All larger genesets will be removed",500,100,30000,true,true);
		setting.addSetting(minGenesetSize);
		setting.addSetting(maxGenesetSize);
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
	
	public void setDataSet(DataSet dataSet) {
		this.dataSet=dataSet;
	}
	@Override
	public PreferencePane getPreferencesPanel() {
		return null;
	}
	
	protected void readGenesets() {
		if(genesetMIGroup==null) {
			throw new RuntimeException("No MIGroup defined");
		}
		MIGroup migroup = genesetMIGroup.getMIGroup();
		MultiHashMap<String,String> sets = new MultiHashMap<String,String>();
		
		for(Entry<Object, MIType> e:migroup.getMIOs()) {
			if(e.getKey() instanceof Probe) {
				if(StringMIO.class.isAssignableFrom(migroup.getMIOClass())) {
					sets.put(e.getValue().toString(),((Probe)e.getKey()).getName());
				}
				else if(StringListMIO.class.isAssignableFrom(migroup.getMIOClass())){
					StringListMIO stringList = (StringListMIO)e.getValue();
					for(String name:stringList.getValue()) {
						sets.put(name,((Probe)e.getKey()).getName());
					}
				}
				
			}
 		}
		
		genesets=new LinkedList<Geneset>();
	
		for(String setName:sets.keySet()) {
			Geneset newGeneset = new Geneset(setName);
			genesets.add(newGeneset);
			
			for(String geneName: sets.get(setName)) {
				newGeneset.addGene(geneName);
			}
		}
	}
	
	public List<Geneset> getGenesets() {
		return genesets;
	}
	
	public void init() {
	}
	
	@Override
	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		probeListNames=new LinkedList<String>();
		for(ProbeList pl: probeLists) {
			probeListNames.add(pl.getName());
		}
		ProbeList probes = ProbeList.createUniqueProbeList(probeLists);
		return run(probes, masterTable.createGlobalProbeList(true));
		
	}
	
	public List<ProbeList> run(ProbeList probes,ProbeList probesMasterTable) {
		this.probes=probes;
		MIGroup mg = probesMasterTable.getDataSet().getProbeDisplayNames();
		for (Probe pb : probes) {
			existingProbeNamesForGenesets.add(pb.getName());
			if (mg!=null) {
				MIType mt = mg.getMIO(pb);
				if (mt!=null)
					existingProbeNamesForGenesets.add(mt.toString());
			}
		}
		this.dataSet=probesMasterTable.getDataSet();
		this.probesMasterTable=probesMasterTable;
		classesEnrichment=false;
		if(probeListNames==null) {
			probeListNames=new LinkedList<String>();
			probeListNames.add(probes.getName());
		}
		if(result==null) {
			result = new Result();
			resultGUI = new ResultFrame(result,this);
		}
		SettingDialog sd = null;
		
		boolean showSetting=false;
		if(setting==null) {
			sd=new SettingDialog(null,getName(),getSetting());
			showSetting=true;
			sd.showAsInputDialog();
		}
		
		final List<ProbeList> resultList = new LinkedList<ProbeList>();
		
		if ((showSetting&&sd!=null&&sd.closedWithOK())||!showSetting) {

			t = new AbstractTask(getName()) {
				@Override
				protected void doWork() {
					System.out.println("Running " + getClass().getName());
					if(genesetSetting) {
						readGenesets();

					}
					if(genesets!=null) {
						//remove genesets with small and large size
						removeSmallAndBigGenesets(minGenesetSize.getIntValue(),maxGenesetSize.getIntValue(),genesets);
					}

					if(genesets!=null&&genesets.size()!=0) {
						progress=0;
						setProgress(0,"Analysis started");
						calculateEnrichment();
						resultGUI.showResult();
						resultList.addAll(resultAsProbeList());
					} 
					else {
						throw new RuntimeException("No GeneSets found");
					}
					

				}

				@Override
				protected void initialize() {

				}	
			};
			t.start();
			t.waitFor();
		
		}
		
		return resultList;
	}

	public void setGenesets(List<Geneset> genesets) {
		this.genesets=genesets;
	}
	
	public void setProbes(ProbeList probes) {
		this.probes=probes;
	}
	
	public Result getResult() {
		return result;
	}

	public String getPreferences() {
		String preferences = "<html>";
		preferences+=additionalPreferences();
		preferences+="DataSet: " + dataSet.getName()+"<p/>";;
		preferences+="ProbeLists: "; 
		for(String name:probeListNames) {
			preferences+=name+", ";
		}
		preferences+="<p/>";
		if(genesetSetting) {
			preferences+="GenesetMIGroup: " + genesetMIGroup.getMIGroup().getName()+"<p/>"; 
		}
		preferences+="Minimum geneset size: " + minGenesetSize.getIntValue()+"<p/>";
		preferences+= "Maximum geneset size: " + maxGenesetSize.getIntValue();
		preferences+="</html>";
		
		return preferences;
	}

	public void setNoGenesetSetting() {
		genesetSetting=false;
	}



	
}