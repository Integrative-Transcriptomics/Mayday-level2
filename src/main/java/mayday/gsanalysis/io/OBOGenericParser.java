package mayday.gsanalysis.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.gui.PreferencePane;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.StringMapMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.maps.MultiHashMap;
import mayday.gsanalysis.meta.GraphMIO;

public class OBOGenericParser extends AbstractPlugin implements ProbelistPlugin{
	protected HierarchicalSetting setting;
	protected MasterTable masterTable;
	protected PathSetting oboFile;
	public static String TERM_START_TAG="<term>";
	public static String TERM_END_TAG="</term>";
	public static String ID_START_TAG="<id>";
	public static String ID_END_TAG="</id>";
	public static String NAME_START_TAG="<name>";
	public static String NAME_END_TAG="</name>";
	public static String ISA_START_TAG="<is_a>";
	public static String ISA_END_TAG="</is_a>";
	public static String RELATIONSHIP_START_TAG="<relationship>";
	public static String RELATIONSHIP_END_TAG="</relationship>";
	public static String TYPE_START_TAG="<type>";
	public static String TYPE_END_TAG="</type>";
	public static String TO_START_TAG="<to>";
	public static String TO_END_TAG="</to>";
	public static String ALTID_START_TAG="<alt_id>";
	public static String ALTID_END_TAG="</alt_id>";
	public static String REPLACEDBY_START_TAG="<replaced_by>";
	public static String REPLACEDBY_END_TAG="</replaced_by>";
	
	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.GeneSetAnalysis.OBOGenericParser",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Roland Keller",
				"no e-mail provided",
				"Plugin for Parsing of generic Obo-Files ",
		"Import annotations from OBO files");
		pli.addCategory("Geneset Analysis/Annotations");
		pli.getProperties().put(Constants.NO_CACHE_CLASS_INSTANCE, true);
		return pli;
	}
	public Setting initSetting() {
		oboFile = new PathSetting("Select obo-File", null, null, false, true, false);
		setting = new HierarchicalSetting("Import annotations from generic OBO files").addSetting(oboFile);
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
	
	public PreferencePane getPreferencesPanel() {
		return null; 
	}
	
	protected void readOBOFile(String path) throws Exception{
		MultiHashMap<String,String> isAEdges = new MultiHashMap<String,String>();
		MultiHashMap<String,String> partOfEdges = new MultiHashMap<String,String>();
		MultiHashMap<String,String> regulatesEdges = new MultiHashMap<String,String>();
		HashMap<String,Node> nodeMap = new HashMap<String,Node>();
		MultiHashMap<String,String> alternativeNames = new MultiHashMap<String,String>();
		Graph graph = new Graph();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		MultiHashMap<String,String> namespaces = new MultiHashMap<String,String>();
		HashMap<String, Node> namespace_nodes = new HashMap<String, Node>();
		
		int replaced=0;
		if(path.endsWith(".obo")) {
			String line = reader.readLine();
			String currentTerm="";
			
			boolean inTerm=false;
			boolean hasParent=false;
			String namespace="";
			Node currentNode=null;
		
			while(line!=null) {
				if(line.equals("[Term]")) {
					if(inTerm&&!hasParent) {
						namespaces.put(namespace, currentTerm);
					}
				
					inTerm=true;
					hasParent=false;
				}
				else if(line.matches("id:.*")) {
					if(inTerm) {
						currentNode=new Node(graph);
						currentTerm=line.replaceFirst("id:[\\s]", "");
						currentNode.setName(currentTerm);
						graph.addNode(currentNode);
						nodeMap.put(currentTerm, currentNode);
					}
				}
				else if(line.matches("name:.*")) {
					if(inTerm && currentNode!=null) {
						String name=line.replaceFirst("name: ", "");
						currentNode.setName(currentNode.getName()+ " (" + name + ")");						
					}
				}
				else if(line.matches("is_a.*")) {
					if(inTerm) {
						hasParent=true;
						String parent=line.replaceFirst("is_a:[\\s]*", "").replaceFirst(" !.*", "");
						isAEdges.put(currentTerm,parent);
					}
				}
				else if(line.matches("relationship: part_of.*")) {
					if(inTerm) {
						hasParent=true;
						String parent = line.replaceFirst("relationship: part_of:[\\s]*", "").replaceFirst(" !.*", "");
						partOfEdges.put(currentTerm,parent);
					}
				}
				/*
				else if(line.matches("relationship: .*regulates.*")) {
					if(inTerm) {
						hasParent=true;
						String parent = line.replaceFirst("relationship: .*regulates.*GO:", "GO:").replaceFirst(" !.*", "");
						regulatesEdges.put(currentTerm,parent);
					}
				}*/
				else if(line.matches("namespace:.*")) {
					if(inTerm) {
						namespace=line.replaceFirst("namespace: ", "");
					}
				}
				else if(line.matches("alt_id.*")) {
					if(inTerm) {
						String altID=line.replaceFirst("alt_id:[\\s]*", "").replaceFirst(" !.*", "");
						alternativeNames.put(currentTerm,altID);
					}
				}
				else if(line.matches("replaced_by.*")) {
					if(inTerm) {
						String replacement=line.replaceFirst("replaced_by:[\\s]*", "").replaceFirst(" !.*", "");
						alternativeNames.put(replacement,currentTerm);
						if(currentNode!=null) {
							replaced++;
							graph.removeNode(currentNode);
							nodeMap.remove(currentTerm);
							currentNode=null;
						}
					}
				}
				else if(!(line.equals("[Term]"))&&line.matches("\\[.+\\]")) {
					inTerm=false;
				}
				line=reader.readLine();
			}
			if(inTerm&&!hasParent) {
				namespaces.put(namespace, currentTerm);
			}
		}
		else if(path.endsWith(".obo-xml")) {
			throw new RuntimeException("OBO-XML not implemented yet");
		}
//			String line = reader.readLine();
//			StringBuilder wholeTextBuilder=new StringBuilder();
//			
//			while(line!=null)  {
//				if(line.contains(TERM_START_TAG)) {
//					wholeTextBuilder=new StringBuilder();
//					wholeTextBuilder.append(line);
//				}
//				else if(line.contains(TERM_END_TAG)) {
//					wholeTextBuilder.append(line);
//					Object[] ret = new Object[]{null,0};
//			
//					String wholeText=wholeTextBuilder.toString();
//					ret = XMLTools.nextSubstring(wholeText, TERM_START_TAG, TERM_END_TAG, (Integer)ret[1]);
//					String termText = (String)ret[0];
//				
//					//id
//					Object[] ret1 = new Object[]{null,0};
//					ret1 = XMLTools.nextSubstring(termText, ID_START_TAG, ID_END_TAG, (Integer)ret1[1]);
//					String currentTerm = (String)ret1[0];
//					Node currentNode=new Node(graph);
//					currentNode.setName(currentTerm);
//					graph.addNode(currentNode);
//					nodeMap.put(currentTerm, currentNode);
//				
//					ret1[0] = null;
//					ret1[1] = 0;
//					ret1 = XMLTools.nextSubstring(termText, NAME_START_TAG, NAME_END_TAG, (Integer)ret1[1]);
//				
//					//name
//					if (ret[1]!=null) {
//						String name = (String)ret1[0];
//						currentNode.setName(currentNode.getName()+ " (" + name + ")");
//						if(name.equals("biological_process")) {
//							biologicalProcessNode=currentNode;
//						}
//						else if(name.equals("molecular_function")) {
//							molecularFunctionNode=currentNode;
//						}
//						else if(name.equals("cellular_component")) {
//							cellularComponentNode=currentNode;
//						}
//					}
//	
//					//is-a-edges
//					ret1[0] = null;
//					ret1[1] = 0;
//					while(true) {
//						ret1 = XMLTools.nextSubstring(termText, ISA_START_TAG, ISA_END_TAG, (Integer)ret1[1]);
//						if(ret1[0]==null) {
//							break;
//						}
//						String parent = (String)ret1[0];
//						isAEdges.put(currentTerm,parent);
//					}
//				
//					//part-of-edges
//					ret1[0] = null;
//					ret1[1] = 0;
//					while(true) {
//						ret1 = XMLTools.nextSubstring(termText, RELATIONSHIP_START_TAG, RELATIONSHIP_END_TAG, (Integer)ret1[1]);
//						if(ret1[0]==null) {
//							break;
//						}
//						String relationshipText = (String)ret1[0];
//						Object[] ret2 = new Object[]{null,0};
//						ret2 = XMLTools.nextSubstring(relationshipText, TYPE_START_TAG, TYPE_END_TAG, (Integer)ret2[1]);
//						String typeString = (String)ret2[0];
//						
//						ret2[0] = null;
//							ret2[1] = 0;
//							ret2 = XMLTools.nextSubstring(relationshipText, TO_START_TAG, TO_END_TAG, (Integer)ret2[1]);
//							String parent = (String)ret2[0];
//							
//						if(typeString.equals("part_of")) {	
//							partOfEdges.put(currentTerm,parent);
//						}/*
//						else {
//							regulatesEdges.put(currentTerm,parent);
//						}*/
//					}
//					
//					//alternative ids
//					ret1[0] = null;
//					ret1[1] = 0;
//					while(true) {
//						ret1 = XMLTools.nextSubstring(termText, ALTID_START_TAG, ALTID_END_TAG, (Integer)ret1[1]);
//						if(ret1[0]==null) {
//							break;
//						}
//						String altID = (String)ret1[0];
//						alternativeNames.put(currentTerm,altID);
//					}
//					
//					//replacements
//					ret1[0] = null;
//					ret1[1] = 0;
//					while(true) {
//						ret1 = XMLTools.nextSubstring(termText, REPLACEDBY_START_TAG, REPLACEDBY_END_TAG, (Integer)ret1[1]);
//						if(ret1[0]==null) {
//							break;
//						}
//						String replacement = (String)ret1[0];
//						
//						alternativeNames.put(replacement,currentTerm);
//						if(currentNode!=null) {
//							replaced++;
//							graph.removeNode(currentNode);
//							nodeMap.remove(currentTerm);
//							currentNode=null;
//						}
//					}
//				}
//				else {
//					wholeTextBuilder.append(line);
//				}
//				line=reader.readLine();
//			}
//				
//			
//		
//		}
		reader.close();
		
		//find namespace nodes
		for (String namespace : namespaces.keySet()) {
			Node nsnode = nodeMap.get(namespace);
			if (nsnode!=null)
				namespace_nodes.put(namespace, nsnode);
		}
		
		
		List<Edge> edges = new LinkedList<Edge>();
		for(String source: isAEdges.keySet()) {
			for(String target: isAEdges.get(source)) {
				Node sourceNode=nodeMap.get(source);
				Node targetNode=nodeMap.get(target);
				if(sourceNode!=null&&targetNode!=null) {
					Edge e = new Edge(sourceNode,targetNode);
					e.setRole("is_a");
					edges.add(e);
				}
				else {
					System.out.println(source + " " + target);
				}
			}
		}
	
		for(String source: partOfEdges.keySet()) {
			for(String target: partOfEdges.get(source)) {
				Node sourceNode=nodeMap.get(source);
				Node targetNode=nodeMap.get(target);
				if(sourceNode!=null&&targetNode!=null) {
					Edge e = new Edge(sourceNode,targetNode);
					e.setRole("part_of");
					edges.add(e);
				}
				else {
					System.out.println(source + " " + target);
				}
			}
		}
		for(String source: regulatesEdges.keySet()) {
			for(String target: regulatesEdges.get(source)) {
				Node sourceNode=nodeMap.get(source);
				Node targetNode=nodeMap.get(target);
				if(sourceNode!=null&&targetNode!=null) {
					Edge e = new Edge(sourceNode,targetNode);
					e.setRole("regulates");
					edges.add(e);
				}
				else {
					System.out.println(source + " " + target);
				}
			}
		}
		Node root=new Node(graph);
		String rootName = "root";
		root.setName(rootName);
		graph.addNode(root);
		nodeMap.put(rootName, root);
		for (Node nsNode : namespace_nodes.values())
			edges.add(new Edge(nsNode,root));

		for(Edge e: edges) {
			graph.connect(e);	
		}
		System.out.println(graph.edgeCount() + " edges");
		System.out.println(graph.nodeCount()+ " nodes");
		System.out.println(replaced + " replaced terms");		
		GraphMIO graphMIO = new GraphMIO(graph);
		MIGroup migroup = masterTable.getDataSet().getMIManager().newGroup(GraphMIO.myType, "Ontology", (new File(path).getName()));
		migroup.add(masterTable.getDataSet(),graphMIO);
		
		HashMap<String,String> altNames=new HashMap<String,String>();
		for(String name: alternativeNames.keySet()) {
			StringBuilder sb=new StringBuilder();
			for(String alt:alternativeNames.get(name)) {
				if(sb.length()!=0) {
					sb.append(",");
				}	
				sb.append(alt);	
			}
			altNames.put(name, sb.toString());
		}
		StringMapMIO mapMIO = new StringMapMIO(altNames);
		MIGroup migroup2 = masterTable.getDataSet().getMIManager().newGroup(mapMIO.getType(), "Alternative terms ", (new File(path).getName()));
		migroup2.add(masterTable.getDataSet(),mapMIO);
		
		/*
		String s1 = graphMIO.serialize(MIType.SERIAL_XML);
		GraphMIO g2 = new GraphMIO();
		g2.deSerialize(MIType.SERIAL_XML, s1);
		String s2 = g2.serialize(MIType.SERIAL_XML);
		g2.deSerialize(MIType.SERIAL_XML, s2);
		String s3 = g2.serialize(MIType.SERIAL_XML);		
		
		
		System.out.println(s2.substring(s2.length()-200)+"\n--\n");System.out.println(s3.substring(s3.length()-200));
		
		if (!(s2.equals(s3)))
			throw new RuntimeException("Serial failed");
		*/
	}
	
	@Override
	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		this.masterTable=masterTable;
		SettingDialog sd = new SettingDialog(null,getClass().getName(),getSetting());
		sd.showAsInputDialog();
		if (sd.closedWithOK()) {
			if(oboFile.getStringValue().endsWith(".obo")||oboFile.getStringValue().endsWith(".obo-xml")) {
				try{
					readOBOFile(oboFile.getStringValue());
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
				}
			}
			else {
				System.out.println("File type not supported");
			}
				 
		}
		return null;
	}
	

}

