package mayday.tiala.pairwise.mio;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import mayday.core.XMLTools;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.meta.gui.AbstractMITableRenderer;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class AlignmentMIO extends GenericMIO<Map<String,String>> {

	public final static String myType = "PAS.MIO.alignedDS.Alignment";
	
	private final static String START_TAG = "<Entry";
	private final static String END_TAG = "/>";
	private final static String KEY_ATTR = "key=\"";
	private final static String VALUE_ATTR = "value=\"";
	private final static String ATTR_END="\"";
	
	private final static String KEY_TIMESHIFT="TimeShift (aligned DataSet)";
	private final static String KEY_SCORE="Alignment Score";
	private final static String KEY_QUANTILES="Score Quantiles";
//	private final static String PREFIX_LINKEDPROBELIST = "Link";
	
	public AlignmentMIO() {
		Value = new TreeMap<String,String>();
	}
	
	public void init() {
		//deSerialize(MIType.SERIAL_TEXT,"Test 1=5, Test2=BLA&equals;NO , Test3=&");
		//deSerialize(MIType.SERIAL_XML,serialize(MIType.SERIAL_XML));		
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(),
				myType,
				new String[0],
				Constants.MC_METAINFO,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Represents a set of aligned datasets",
				"Alignment MIO"
				);
	}



	private String protect(String s ) {
		return s.replace("&","&ampersand;").replace(",","&komma;").replace("=","&equals;");
	}
	
	private String unprotect(String s) {
		return s.replace("&komma;",",").replace("&equals;","=").replace("&ampersand;", "&");
	}
	
	public boolean deSerialize(int serializationType, String serializedForm) {
		Value.clear();
		switch(serializationType) {
		case MIType.SERIAL_TEXT:
			String[] splits = serializedForm.split(",");
			for (String s : splits) {
				String[] parts = s.split("=");
				String theValue;
				if (parts.length>1)
					theValue = unprotect(parts[1].trim());
				else theValue="";
				Value.put(unprotect(parts[0].trim()), theValue);
			}
		case MIType.SERIAL_XML:
			Object[] ret = new Object[]{null,0};
			while (true) {
				ret = XMLTools.nextSubstring(serializedForm, START_TAG, END_TAG, (Integer)ret[1]);
				if (ret[0]==null)
					break;
				String key = (String)XMLTools.nextSubstring((String)ret[0], KEY_ATTR, ATTR_END, 0)[0];
				String value = (String)XMLTools.nextSubstring((String)ret[0], VALUE_ATTR, ATTR_END, 0)[0];				
				Value.put(XMLTools.unxmlize(key), XMLTools.unxmlize(value)); //no trimming here
			}
		}
		
		return true;
	}
	
	public String serialize(int serializationType) {
		LinkedList<String> oldKeys = new LinkedList<String>();
//		for (String key : Value.keySet())
//			if (key.startsWith(PREFIX_LINKEDPROBELIST))
//				oldKeys.add(key);
		for (String key : oldKeys)
			Value.remove(key);
//		
//		DataSetManagerViewInterface dsmv = DataSetManagerView.getInstance();
//		if (dsmv instanceof DataSetManagerViewAligned) {
//			DataSetManagerViewAligned dsmvml = (DataSetManagerViewAligned)dsmv;
//			LinkedProbeLists lpl = dsmvml.getLinkedProbeLists();
//			if (lpl!=null) {
//				int suffix=0;
//				for (Collection<ProbeList> cpl : lpl.getEverything()) {
//					String key = PREFIX_LINKEDPROBELIST+(suffix++);
//					String value = ""; 
//					for (ProbeList pl : cpl) {
//						String s = protect(pl.getDataSet().getName())+"="+protect(pl.getName());
//						value+=protect(s)+",";
//					}
//					Value.put(key,value);
//				}
//			}
//		}
		
		StringBuilder ret = new StringBuilder();
		if (Value.size()==0) 
			return "";
		switch(serializationType) {
		case MIType.SERIAL_TEXT:
			for (Entry<String,String> i: Value.entrySet())
				ret.append(protect(i.getKey())+"="+protect(i.getValue())+",");
			return ret.substring(0, ret.length()-1);
		case MIType.SERIAL_XML:
			for (Entry<String,String> i: Value.entrySet())
				ret.append(START_TAG+" "
						+KEY_ATTR+  XMLTools.xmlize(i.getKey()) + "\" " 
						+VALUE_ATTR+  XMLTools.xmlize(i.getValue()) + "\"" 
						+END_TAG);
			return ret.toString();
		}
		throw new RuntimeException("Unsupported SerializationType "+serializationType);
	}

	@SuppressWarnings("unchecked")
	public AbstractMIRenderer getGUIElement() {
		return new StringMapMIORenderer();
	}


	public AlignmentMIO clone() {
		AlignmentMIO slm = new AlignmentMIO();
		slm.deSerialize(MIType.SERIAL_TEXT, this.serialize(SERIAL_TEXT));
		return slm;
	}

	
	@SuppressWarnings("serial")
	private static class StringMapMIORenderer extends AbstractMITableRenderer<AlignmentMIO> {

		private AlignmentMIO value; 
		
		public StringMapMIORenderer() {
			tableModel.setColumnCount(2);
			value = new AlignmentMIO();
		}

		@Override
		public String getEditorValue() {
			TreeMap<String,String> theMap = new TreeMap<String,String>();
			for (int i=0; i!=tableModel.getRowCount(); ++i) {
				String key =(String)tableModel.getValueAt(i, 0);
				String value = (String)tableModel.getValueAt(i, 1);
				if (key!=null) {
					if (value==null)
						value = "";
					theMap.put(key,value);
				}
			}
			value.setValue(theMap);
			return value.serialize(MIType.SERIAL_TEXT);
		}

		@Override
		public void setEditorValue(String serializedValue) {
			value.deSerialize(MIType.SERIAL_TEXT, serializedValue);
			tableModel.setRowCount(value.getValue().size());
			int position=0;
			for (Entry<String,String> e: value.getValue().entrySet()) {
				tableModel.setValueAt(e.getKey(), position, 0);
				tableModel.setValueAt(e.getValue(), position++, 1);
			}
		}
		
	}
	
	public double getTimeShift() {
		String ts = Value.get(KEY_TIMESHIFT);
		return Double.parseDouble(ts);
	}
	
	public void setTimeShift(double shift) {
		Value.put(KEY_TIMESHIFT, ""+shift);
	}
	
	public Double getScore() {
		String ts = Value.get(KEY_SCORE);
		return Double.parseDouble(ts);
	}
	
	public void setScore(Double score) {
		Value.put(KEY_SCORE, ""+score);
	}
	
	public String getQuantiles() {
		String ts = Value.get(KEY_QUANTILES);
		return ts;
	}
	
	public void setQuantiles(String Quantiles) {
		Value.put(KEY_QUANTILES, Quantiles);
	}

	
//	public void rebuildLinks() {
//		LinkedProbeLists lpl = LinkedProbeLists.getInstance();
//		for (Entry<String,String> e : this.Value.entrySet()) {
//			if (e.getKey().startsWith(PREFIX_LINKEDPROBELIST)) {
//				String v = e.getValue();					
//				String[] protectedStrings = v.split(",");
//				LinkedList<ProbeList> linkedPL = new LinkedList<ProbeList>();
//				for (String protectedString: protectedStrings) {
//					if (protectedString.trim().length()>0) {
//						String value = unprotect(protectedString);
//						String[] parts = value.split("=");
//						String dsName = unprotect(parts[0]);
//						String plName = unprotect(parts[1]);
//						for (DataSet ds : DataSetManager.singleInstance.getDataSets()) {
//							if (ds.getName().equals(dsName)) {
//								for (ProbeList pl : ds.getProbeListManager().getProbeLists()) {
//									if (pl.getName().equals(plName)) {
//										linkedPL.add(pl);
//										break;
//									}
//								}
//								break;										
//							}
//						}
//					}
//				}
//				lpl.addProbeLists(linkedPL);
//			}		
//		}
//	}

	
	public String getType() {
		return myType;
	}

}
