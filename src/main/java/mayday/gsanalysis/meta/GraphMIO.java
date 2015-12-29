package mayday.gsanalysis.meta;

import java.util.HashMap;
import java.util.TreeSet;


import mayday.core.XMLTools;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.Pair;
import mayday.core.structures.graph.*;

public class GraphMIO extends GenericMIO<Graph>{
	public final static String myType = "PAS.MIO.Graph";
	
		
	public GraphMIO(Graph value) {
		Value=value;
	}
	
	public GraphMIO() {
		Value=new Graph();
		
	}
	
	@Override
	public MIType clone() {
		return new GraphMIO(Value); 
	}

	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(),
				myType,
				new String[0],
				Constants.MC_METAINFO,
				new HashMap<String, Object>(),
				"Roland Keller",
				"kellerr@informatik.uni-tuebingen.de",
				"Represents graphs as meta informations",
				"Graph MIO"
				);
	}

	@Override
	public boolean deSerialize(int serializationType, String serializedForm) {
		Value.clear();
		HashMap<Integer,Node>nodeMap=new HashMap<Integer,Node>();
		
		HashMap<Edge, Pair<Integer,Integer>> edgeMap = new HashMap<Edge,Pair<Integer,Integer>>();
		switch(serializationType) {
		case MIType.SERIAL_TEXT:
			String[] splits = serializedForm.split("\n");
			StringBuilder currentString=new StringBuilder();
			boolean node=true;
			for (String s : splits) {
				if(s.startsWith(Node.NODE_STRING)||s.startsWith(Edge.EDGE_STRING)) {
					if(!currentString.toString().equals("")) {
						currentString.append("\n");
						if(node) {
							Node newNode=null;
							newNode=new Node(Value);
							int id = newNode.deSerializeString(currentString.toString());
							Value.addNode(newNode);
							newNode.setID(id);
							nodeMap.put(id, newNode);
						}
						else {
							Edge newEdge=new Edge(null,null);
							Pair<Integer,Integer> ids = newEdge.deSerializeString(currentString.toString());
							edgeMap.put(newEdge,ids);
						}
					}
					
					if(s.startsWith(Node.NODE_STRING)) {
						node=true;
					}
					else {
						node=false;
					}
					if(currentString.length()>0) {
						currentString.delete(0, currentString.length());
					}
				}
				else if(s!=null && !s.equals("")){
					if(!currentString.toString().equals("")) {
						currentString.append("\n");
					}
					currentString.append(s);
				}
			}
			if(!currentString.toString().equals("")) {
				currentString.append("\n");
				if(node) {
					Node newNode=null;
					newNode=new Node(Value);
					int id = newNode.deSerializeString(currentString.toString());
					Value.addNode(newNode);
					newNode.setID(id);
					nodeMap.put(id, newNode);
				}
				else {
					Edge newEdge=new Edge(null,null);
					Pair<Integer,Integer> ids = newEdge.deSerializeString(currentString.toString());
					edgeMap.put(newEdge,ids);
				}
			}
		case MIType.SERIAL_XML:
			Object[] ret = new Object[]{null,0};
			while (true) {
				ret = XMLTools.nextSubstring(serializedForm, Node.NODE_START_TAG, Node.NODE_END_TAG, (Integer)ret[1]);
				if (ret[0]==null)
					break;
				String s = (String)ret[0];
				s=XMLTools.unxmlize(s);
				Node newNode = new Node(Value);
				int id = newNode.deSerializeXML(s);
				Value.addNode(newNode);
				nodeMap.put(id, newNode);
			}
			while (true) {
				ret = XMLTools.nextSubstring(serializedForm, Edge.EDGE_START_TAG, Edge.EDGE_END_TAG, (Integer)ret[1]);
				if (ret[0]==null)
					break;
				String s = (String)ret[0];
				s=XMLTools.unxmlize(s);
				Edge newEdge = new Edge(null,null);
				Pair<Integer,Integer> ids = newEdge.deSerializeXML(s);
				edgeMap.put(newEdge,ids);
			}
			
		}
		
		//set sources and targets for edges
		
		for(Edge e: edgeMap.keySet()) {
			Pair<Integer,Integer> ids = edgeMap.get(e);
			e.setSource(nodeMap.get(ids.getFirst()));
			e.setTarget(nodeMap.get(ids.getSecond()));
			Value.connect(e);
		}
		return true;
	}

	@Override
	public AbstractMIRenderer<?> getGUIElement() {
		return null;
	}

	@Override
	public String getType() {
		return myType;
	}

	@Override
	public String serialize(int serializationType) {
		StringBuilder ret = new StringBuilder();
		if (Value.getNodes().size()==0) { 
			return "";
		}
		TreeSet<Node> nodes = new TreeSet<Node>(Value.getNodes());
		TreeSet<Edge> edges = new TreeSet<Edge>(Value.getEdges());
		switch(serializationType) {
		case MIType.SERIAL_TEXT:
			for(Node n:nodes) {
				n.serializeString(ret);
			}
			for(Edge e: edges) {
				e.serializeString(ret);
			}
			return ret.toString();
		case MIType.SERIAL_XML:
			for(Node n: nodes) {
				n.serializeXML(ret);
			}
			for(Edge e: edges) {
				e.serializeXML(ret);
			}
			return ret.toString();
		}
		throw new RuntimeException("Unsupported SerializationType "+serializationType);
	}
	

}
