package mayday.vis3.plots.termpyramid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIGroup;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.structures.maps.MultiHashMap;
import mayday.vis3.model.ViewModel;

public class TermPyramidModel implements SettingChangeListener, TableModel
{
	private List<TableModelListener> listeners=new ArrayList<TableModelListener>();
	
	private ProbeList probeList1;
	private ProbeList probeList2;
	
	private MIGroup miGroup; 
	private TermPyramidSettings settings;
	
	private List<ModelRow> rows=new ArrayList<ModelRow>();
	
	private int pl1Max;
	private int pl2Max;

	public TermPyramidModel(ViewModel model, TermPyramidSettings settings) 
	{
		probeList1=model.getProbeLists(false).get(0);
		if(model.getProbeLists(false).size()>=2)
			probeList2=model.getProbeLists(false).get(1);
		else
			probeList2=model.getProbeLists(false).get(0);
		this.settings=settings;		
	}
	
	@Override
	public void stateChanged(SettingChangeEvent e) 
	{
		miGroup=settings.getMiGroup().getMIGroup();
		probeList1=settings.getLeftProbeList();
		probeList2=settings.getRightProbeList();
		setRows();
	}



	@Override
	public void addTableModelListener(TableModelListener l) 
	{
		listeners.add(l);		
	}



	@Override
	public Class<?> getColumnClass(int columnIndex) 
	{
		if(columnIndex==0 || columnIndex==4)
		{
			return Collection.class;
		}
		if(columnIndex==1 || columnIndex==3)
		{
			return Integer.class;
		}
		return String.class;
	}



	@Override
	public int getColumnCount() 
	{
		return 5;
	}

	@Override
	public String getColumnName(int columnIndex) 
	{
		switch (columnIndex) 
		{
			case 0: return probeList1.getName();
			case 1: return "#";
			case 2: return miGroup==null?"Term":miGroup.getName();
			case 3: return "#";
			case 4: return probeList2.getName();
			default: return null;			
		}	
		
	}

	@Override
	public int getRowCount() 
	{
		if(rows.isEmpty()) return 1;
		return rows.size();
	}



	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		if(rows.isEmpty())
		{
			if(columnIndex==2)
			{
				return "Select a MI Group";
			}				
			return "";
		}		
		switch (columnIndex) 
		{
		case 1: return rows.get(rowIndex).pl1Probes.size();
		case 0: return rows.get(rowIndex).pl1Probes;
		case 2: return rows.get(rowIndex).term;
		case 4: return rows.get(rowIndex).pl2Probes;
		case 3: return rows.get(rowIndex).pl2Probes.size();
		default: return 0;
		}
	}



	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) 
	{		
		return false;
	}



	@Override
	public void removeTableModelListener(TableModelListener l) 
	{
		listeners.remove(l);		
	}



	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {	}
	
	@SuppressWarnings("unchecked")
	private void setRows()
	{
		if(miGroup == null )
			return;
		rows.clear();
		
		MultiHashMap<String, Probe> pl1Probes=new MultiHashMap<String, Probe>();
		MultiHashMap<String, Probe> pl2Probes=new MultiHashMap<String, Probe>();


		for(Probe p:probeList1)
		{
			if(!miGroup.contains(p)) continue;
			if(((GenericMIO)miGroup.getMIO(p)).getValue() instanceof Iterable)
			{
				for(Object o:(((Iterable)((GenericMIO)miGroup.getMIO(p)).getValue())))
				{			
					pl1Probes.put(o.toString(), p);
				}
			}else
			{
				String t=((GenericMIO)miGroup.getMIO(p)).getValue().toString();			
				pl1Probes.put(t, p);
			}
		}
		
		for(Probe p:probeList2)
		{
			if(!miGroup.contains(p)) continue;
			if(((GenericMIO)miGroup.getMIO(p)).getValue() instanceof Iterable)
			{
				for(Object o:(((Iterable)((GenericMIO)miGroup.getMIO(p)).getValue())))
				{			
					pl2Probes.put(o.toString(), p);
				}
			}else
			{
				String t=((GenericMIO)miGroup.getMIO(p)).getValue().toString();			
				pl2Probes.put(t, p);	
			}
		}
		
		Set<String> allTerms=new TreeSet<String>();
		allTerms.addAll(pl1Probes.keySet());
		allTerms.addAll(pl2Probes.keySet());
		
		for(String s: allTerms)
		{
			ModelRow row=new ModelRow();
			row.term=s;
			row.pl1Probes=pl1Probes.containsKey(s)?pl1Probes.get(s):new ArrayList<Probe>();
			row.pl2Probes=pl2Probes.containsKey(s)?pl2Probes.get(s):new ArrayList<Probe>();
			rows.add(row);
		}
		// get max counts
		pl1Max=0;
		pl2Max=0;
		for(ModelRow r: rows)
		{
			if(r.pl1Probes.size() > pl1Max) pl1Max=r.pl1Probes.size();
			if(r.pl2Probes.size() > pl2Max) pl2Max=r.pl2Probes.size();
		}
		
//		System.out.println(rows.size());
		TableModelEvent e=new TableModelEvent(this);
		for(TableModelListener l:listeners)
			l.tableChanged(e);
		
	}
	
	public static class ModelRow
	{
		String term;
		List<Probe> pl1Probes;
		List<Probe> pl2Probes;
	}
	
	public ProbeList getProbeList1() {
		return probeList1;
	}

	public ProbeList getProbeList2() {
		return probeList2;
	}

	public int getPl1Max() {
		return pl1Max;
	}

	public int getPl2Max() {
		return pl2Max;
	}

	public ModelRow getRow(int i) 
	{
		return rows.get(i);		
	}
	
	public boolean isEmpty() 
	{
		return rows.isEmpty();	
	}
	
	
}
