package mayday.gsanalysis.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;

import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.GUIUtilities;
import mayday.core.gui.MaydayFrame;
import mayday.core.gui.PreferencePane;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringListMIO;
import mayday.core.meta.types.StringMIO;
import mayday.core.meta.types.StringMapMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.structures.Pair;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.maps.BidirectionalHashMap;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.tasks.AbstractTask;
import mayday.expressionmapping.utils.WindowUtils;
import mayday.gsanalysis.AbstractGSAnalysisPlugin;
import mayday.gsanalysis.Enrichment;
import mayday.gsanalysis.Geneset;
import mayday.gsanalysis.Result;
import mayday.gsanalysis.gui.GraphSelectionListener;
import mayday.gsanalysis.meta.GraphMIO;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.layout.SugiyamaLayout;
import mayday.vis3.graph.model.DefaultGraphModel;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.renderer.primary.CircleRenderer;
import mayday.vis3.gui.Layouter;
import mayday.vis3.gui.actions.ExportPlotAction;
import mayday.vis3.gui.actions.ExportVisibleAreaAction;


public class GraphEnrichmentAnalysisPlugin extends AbstractPlugin implements ProbelistPlugin {
	private BidirectionalHashMap<Node,Geneset> genesetMap;
	private HashMap<Node,HashMap<Pair<String,String>,Enrichment>> enrichmentMap;
	private PluginInstanceSetting<AbstractGSAnalysisPlugin> gsPlugin;
	private MIGroupSetting annotationGroup;
	private MIGroupSetting graphMIO;
	private Graph graph;
	private GraphCanvas canvas;
	private HierarchicalSetting setting;
	private MasterTable masterTable;
	private SelectableHierarchicalSetting select;
	private PluginInstanceSetting<AbstractGSAnalysisPlugin> graphEnrichmentMethod;
	private AbstractGSAnalysisPlugin plugin;
	private MIGroupSetting alternativeNamesMapping;
	
	protected void readGenesets() {
		if(annotationGroup==null) {
			throw new RuntimeException("No MIGroup defined");
		}
		//We assume that the names of the nodes are unique
		MIGroup migroup = annotationGroup.getMIGroup();
		MultiHashMap<String, String> nodeToGenes = new MultiHashMap<String,String>();
		genesetMap = new BidirectionalHashMap<Node,Geneset>();
		
		for(Entry<Object, MIType> e:migroup.getMIOs()) {
			if(e.getKey() instanceof Probe) {
				if(StringMIO.class.isAssignableFrom(migroup.getMIOClass())) {
					StringMIO stringm = (StringMIO)e.getValue();
					String mio = stringm.getValue();
					nodeToGenes.put(mio,((Probe)e.getKey()).getName());
				}
				else if(StringListMIO.class.isAssignableFrom(migroup.getMIOClass())){
					StringListMIO stringList = (StringListMIO)e.getValue();
					for(String name:stringList.getValue()) {
						nodeToGenes.put(name,((Probe)e.getKey()).getName());
						
					}
				}
				
			}
 		}
		List<Geneset> genesets=new LinkedList<Geneset>();
	
		graph = ((GraphMIO)graphMIO.getMIGroup().getMIO(masterTable.getDataSet())).getValue();
		
		Map<String,String> alternativeNames=null;
		MIGroup altNames=alternativeNamesMapping.getMIGroup();
		if(altNames!=null) {
			alternativeNames=((StringMapMIO)altNames.getMIO(masterTable.getDataSet())).getValue();
		}
		
		List<Node> sorted = Graphs.topologicalSort(graph);
		for(Node n:sorted) {
			Geneset newGeneset = null;
			String goID=n.getName().replaceFirst(" (.+)", "");
			newGeneset= new Geneset(n.getName());
			genesets.add(newGeneset);
			for(Node source:graph.getInNeighbors(n)) {
				newGeneset.addGenes(genesetMap.<Geneset>get(source).getGenes());
				
			}
			
			
			for(String name: nodeToGenes.get(goID)) {
				newGeneset.addGene(name);
				
			}	
			
			if(alternativeNames!=null) {
				if(alternativeNames.containsKey(n.getName())) {
					for(String altName:alternativeNames.get(n.getName()).split(",")) {
						for(String name:nodeToGenes.get(altName)) {
							newGeneset.addGene(name);
						}
					}
				}
			}
			genesetMap.put(n, newGeneset);
		}
		plugin.setGenesets(genesets);
		
	}

	@Override
	public void init() {
	}
	
	@Override
	public PreferencePane getPreferencesPanel() {
		return null;
	}
	
	public Setting initSetting() {
		setting = new HierarchicalSetting("Graph Enrichment Analysis");
		annotationGroup = new MIGroupSetting("Gene Annotation","Select annotations that map genes to genesets",null,masterTable.getDataSet().getMIManager(),false);
		annotationGroup.setAcceptableClass(StringMIO.class, StringListMIO.class);
		graphMIO = new MIGroupSetting("Graph structure","Select the graph structure for enrichment computation",null,masterTable.getDataSet().getMIManager(),false);
		graphMIO.setAcceptableClass(GraphMIO.class);
		alternativeNamesMapping = new MIGroupSetting("Alternative names",
				"Select meta information with alternative geneset names.\n" +
				"This can be used e.g. if gene annotations refer to old GO identifiers\n" +
				"while the graph contains newer identifiers",
				null,masterTable.getDataSet().getMIManager(),true);
		alternativeNamesMapping.setStrictAcceptability(true);
		alternativeNamesMapping.setAcceptableClass(StringMapMIO.class);
		
		
		Set<PluginInfo> plugins = PluginManager.getInstance().getPluginsFor(Constants.MC_PROBELIST);
		Set<AbstractGSAnalysisPlugin> predef = new HashSet<AbstractGSAnalysisPlugin>();
		for(PluginInfo pluginInfo:plugins) {
			AbstractPlugin plugin = pluginInfo.getInstance();
			if(plugin instanceof AbstractGSAnalysisPlugin) {
				predef.add((AbstractGSAnalysisPlugin)plugin);
				((AbstractGSAnalysisPlugin)plugin).setDataSet(masterTable.getDataSet());
				((AbstractGSAnalysisPlugin)plugin).setNoGenesetSetting();
			}
		}
		
		Set<PluginInfo> plugins2 = PluginManager.getInstance().getPluginsFor(GraphEnrichmentMethod.MC);
		Set<AbstractGSAnalysisPlugin> predef2 = new HashSet<AbstractGSAnalysisPlugin>();
		for(PluginInfo pluginInfo:plugins2) {
			AbstractPlugin plugin = pluginInfo.getInstance();
			if(plugin instanceof AbstractGSAnalysisPlugin) {
				predef2.add((AbstractGSAnalysisPlugin)plugin);
				((AbstractGSAnalysisPlugin)plugin).setDataSet(masterTable.getDataSet());
				((AbstractGSAnalysisPlugin)plugin).setNoGenesetSetting();
			}
		}
		
		gsPlugin=new PluginInstanceSetting<AbstractGSAnalysisPlugin>(" ", "Select a method for enrichment computation", predef.iterator().next(), predef);		
		graphEnrichmentMethod=new PluginInstanceSetting<AbstractGSAnalysisPlugin>("  ", "Select a method for enrichment computation", predef2.iterator().next(), predef2);
		
		HierarchicalSetting setting1=new HierarchicalSetting("Normal geneset enrichment methods").addSetting(gsPlugin);
		HierarchicalSetting setting2=new HierarchicalSetting("Graph-aware geneset enrichment methods").addSetting(graphEnrichmentMethod);
		
		select=new SelectableHierarchicalSetting("Method","Select a method for enrichment computation",0,new Setting[]{setting1,setting2})
		.setLayoutStyle(SelectableHierarchicalSetting.LayoutStyle.COMBOBOX);
		setting.addSetting(annotationGroup).addSetting(graphMIO).addSetting(alternativeNamesMapping).addSetting(select);
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
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.GeneSetAnalysis.GraphEnrichmentAnalysis",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Roland Keller",
				"no e-mail provided",
				"Geneset enrichment analysis on cycle-free graphs (e.g. GO ontology graph)",
				"Graph-based Geneset Enrichment Analysis");
		pli.addCategory("Geneset Analysis");
		pli.getProperties().put(Constants.NO_CACHE_CLASS_INSTANCE, true);
		return pli;
	}

	@Override
	public List<ProbeList> run(final List<ProbeList> probeLists, final MasterTable masterTable) {
		this.masterTable = masterTable;
		
		
		SettingDialog sd = new SettingDialog(null,"Graph Enrichment Analysis",getSetting());
		sd.showAsInputDialog();
		
		List<ProbeList> resultList = new LinkedList<ProbeList>();
		
		if(select.getSelectedIndex()==0) {
			plugin = gsPlugin.getInstance();
		} else {
			plugin = graphEnrichmentMethod.getInstance();
		}
		plugin.getResultGUI().addGraphPreferences(getPreferences(),this);

		final ProbeList probes=new ProbeList(masterTable.getDataSet(),true);
		final ProbeList probesMasterTable = new ProbeList(masterTable.getDataSet(),true);
		
		if (sd.closedWithOK()) {
			
			AbstractTask at = new AbstractTask("Preparing Gene Sets") {

				@Override
				protected void doWork() throws Exception {
					String name = "";
					for(ProbeList pl:probeLists) {
						if(!name.equals("")){
							name+=";";
						}
						name+=pl.getName();
					}
					probes.setName(name);
					
					for(Probe p:masterTable.getProbes().values()) {
						if(annotationGroup.getMIGroup().contains(p)) {
							probesMasterTable.addProbe(p);
						}
					}
					for(Probe p:ProbeList.createUniqueProbeList(probeLists).toCollection()) {
						if(annotationGroup.getMIGroup().contains(p)) {
							probes.addProbe(p);
						}
					}
					
					readGenesets();

				}

				protected void initialize() {}
				
			};
			
			at.start();
			at.waitFor();
			
			// start the analysis
			if(select.getSelectedIndex()==0) {
				resultList.addAll(plugin.run(probes, probesMasterTable));				
			} else {
				resultList.addAll(((GraphEnrichmentMethod)plugin).calculateGraphEnrichment(graph, genesetMap, probes, probesMasterTable));				
			}
			
			//find part of graph consisting of most significant terms
			Result result=plugin.getResult();
			if (plugin.getGenesets()==null || plugin.getGenesets().size()==0) { 
				throw new RuntimeException("No genesets found (or all genesets are too small or too big for your filter settings)");
			}
				
			Set<Node> interestingNodes = new TreeSet<Node>();
			enrichmentMap=new HashMap<Node,HashMap<Pair<String,String>,Enrichment>>();
			for(Node n:graph.getNodes()) {
				Geneset g=genesetMap.get(n);
				if(g!=null) {
					enrichmentMap.put(n, result.getEnrichments(g));
				}	
					
			}
			
			int nInterestingNodes=Math.min(30,(int)Math.ceil(0.05*genesetMap.size()));
			if(interestingNodes.size()<=10) {
				List<Enrichment> l = new LinkedList<Enrichment>();
				
				if(result.getEnrichmentWithClasses()) {
					for(Pair<String,String> classes: result.getClassCombinations()) {
						List<Enrichment> sorted=result.getSortedEnrichments(classes);
						l.addAll(sorted);
					}
					Collections.sort(l);
				}
				else {
					List<Enrichment> sorted=result.getSortedEnrichments(null);
					l.addAll(sorted);
				}
				for(Enrichment e:l) {
					interestingNodes.add(genesetMap.<Node>get(e.getGeneset()));
					if(interestingNodes.size()>=nInterestingNodes) {
						break;
					}
				}
			}
			
			Set<Node> importantNodes = new TreeSet<Node>();
			LinkedList<Node> queue=new LinkedList<Node>();
			while(interestingNodes.size()!=0 || queue.size()!=0) {
				if(queue.size()==0) {
					Node currentNode = interestingNodes.iterator().next();
					importantNodes.add(currentNode);
					queue.add(currentNode);
					interestingNodes.remove(currentNode);
				}
				else {
					Node currentNode = queue.poll();
					importantNodes.add(currentNode);
					if(interestingNodes.contains(currentNode)) {
						interestingNodes.remove(currentNode);
					}
					for(Node n:graph.getOutNeighbors(currentNode)) {
						queue.add(n);
					}
				}
			}
			
			MaydayFrame graphDialog=new MaydayFrame();
			JMenuBar menu = new JMenuBar();
			JMenu graphMenu = new JMenu("Graph");
			graphMenu.add(new ExportVisibleAreaAction(canvas));
			graphMenu.add(new ExportPlotAction(canvas));
			menu.add(graphMenu);
			graphDialog.setJMenuBar(menu);
			graphDialog.setTitle("Graph");
			graphDialog.setLayout(new BorderLayout());

			if (importantNodes.size()>0) {
				Graph restrictedGraph=Graphs.restrict(graph, importantNodes);
				GraphModel model=new DefaultGraphModel(restrictedGraph);
				canvas=new GraphCanvas(model);
				canvas.setLayouter(new SugiyamaLayout());
				canvas.setRenderer(new CircleRenderer(Color.lightGray));
				canvas.getSelectionModel().addSelectionListener(new GraphSelectionListener(plugin.getResultGUI().getTable(), plugin.getResult(), canvas));
				graphDialog.add(new JScrollPane(canvas));
			} else {
				graphDialog.add(new JLabel("No significant nodes found in graph"));
			}

			graphDialog.pack();
			graphDialog.setSize(800, 600);
			graphDialog.setVisible(true);
			
			Layouter l = new Layouter(2,1);
			l.nextElement().placeWindow(plugin.getResultGUI());
			l.nextElement().placeWindow(graphDialog);
		}
		return resultList;
	}
	
	public String getPreferences() {
		String preferences = "<html>";
		preferences+="GraphEnrichmentAnalysis" + "<p/>";
		preferences+="MIGroup for graph: " + graphMIO.getMIGroup().getName() + "<p/>";
		preferences+="MIGroup for annotations: " + annotationGroup.getMIGroup().getName() + "<p/>";
		preferences+="</html>";
	
		return preferences;
	}

	public GraphCanvas getGraphCanvas() {
		return canvas;
	}
}
