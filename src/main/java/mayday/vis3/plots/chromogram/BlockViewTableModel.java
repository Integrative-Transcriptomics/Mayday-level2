package mayday.vis3.plots.chromogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.StringListMIO;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.chromogram.multijtable.SelectionIndex;
import mayday.vis3.plots.chromogram.probelistsort.ProbeComparatorFactory;
import mayday.vis3.plots.chromogram.probelistsort.ProbeComparisonMode;
import mayday.vis3.plots.chromogram.probelistsort.ProbeExperimentComparator;
import mayday.vis3.plots.chromogram.probelistsort.ProbeExtremeExperimentComparator;
import mayday.vis3.plots.chromogram.probelistsort.ProbeSortSetting;

@SuppressWarnings("serial")
public class BlockViewTableModel extends DefaultTableModel implements SettingChangeListener, ViewModelListener
{
	private List<Probe> sortedProbes;	
	private NameSourceSetting dataSetting;
	private ProbeSortSetting sortSetting;

	private int columns=30;

	private List<TableItem> sortedData=new ArrayList<TableItem>();

	private DataMode mode=DataMode.probemode;
	private ViewModel viewModel;
	
	private enum DataMode
	{
		probemode, stringmode
	}

	public BlockViewTableModel(List<Probe> sortedProbes, NameSourceSetting dataSetting, ProbeSortSetting sortSetting, ViewModel viewModel) 
	{
		this.sortedProbes=sortedProbes;
		this.dataSetting=dataSetting;
		this.dataSetting.addChangeListener(this);
		this.sortSetting=sortSetting;
		this.sortSetting.addChangeListener(this);
		this.viewModel=viewModel;
		updateContents();
	}

	private void updateContents()
	{
		if(dataSetting.getMappingSource()==NameSourceSetting.LIST_MIO)
		{
			sortedData.clear();
			mode=DataMode.stringmode;
			MIGroup group=dataSetting.getListGroup();
			for(Probe p:sortedProbes)
			{
				if(!group.contains(p)) continue;
				List<String> lst=((StringListMIO)group.getMIO(p)).getValue();
				
				Collections.sort(lst);
				for(String s: lst)
				{
					sortedData.add(new TableItem(p,s));
				}
			}
		}
		else
		{
			mode=DataMode.probemode;
		}

	}

	@Override
	public int getColumnCount() 
	{
		return 1+columns; // index column
	}

	@Override
	public int getRowCount() 
	{
		
		if(mode==DataMode.probemode)
		{
			if(sortedProbes==null) return 0;
			return (int) Math.ceil(sortedProbes.size() / (1.0*columns));
		}else
		{
			if(sortedData==null) return 0;
			if(sortedData.isEmpty()) return 0;
			return (int) Math.ceil(sortedData.size() / (1.0*columns));
		}

	}

	@Override
	public Object getValueAt(int row, int column) 
	{
		if(column==0)
		{
			return row*columns+1;
		}

		int index=row*columns+(column-1);

		if(mode==DataMode.probemode)
		{
			if(index >= sortedProbes.size())
			{
				return "";
			}
			switch (dataSetting.getMappingSource()) 
			{
			case MappingSourceSetting.PROBE_NAMES:
				return sortedProbes.get(index).getName();
			case MappingSourceSetting.PROBE_DISPLAY_NAMES:
				return sortedProbes.get(index).getDisplayName();
			case MappingSourceSetting.MIO:
				if(dataSetting.getMappingGroup().getMIO(sortedProbes.get(index))!=null)
					return dataSetting.getMappingGroup().getMIO(sortedProbes.get(index)).toString();
				return "";
			default:
				return "";
			}
		}else
		{
			if(index >= sortedData.size())
			{
				return "";
			}
			return sortedData.get(index).value;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) 
	{
		if(columnIndex==0)
			return Integer.class;
		return String.class;
	}

	@Override
	public String getColumnName(int column)
	{
		return "";
	}

	public void stateChanged(SettingChangeEvent e) 
	{
		if(e.getAdditionalSources().contains(sortSetting))
		{
			sort();
		}
		if(e.getAdditionalSources().contains(dataSetting))
		{
			updateContents();
		}
		fireTableDataChanged();		
	}

	private void sort()
	{
		ProbeComparisonMode mode=ProbeComparisonMode.forString(sortSetting.getModeSetting().getStringValue());
		MIGroup gr=sortSetting.getMiGroup().getMIGroup();
		int exp=sortSetting.getExperimentSetting().getSelectedIndex();
		Comparator<Probe> comparator=ProbeComparatorFactory.createProbeComparator(mode, exp, gr);
		if(comparator instanceof ProbeExperimentComparator)
		{
			((ProbeExperimentComparator) comparator).setViewModel(viewModel);
		}
		if(comparator instanceof ProbeExtremeExperimentComparator)
		{
			((ProbeExtremeExperimentComparator) comparator).setViewModel(viewModel);
		}
		Collections.sort(sortedProbes, comparator);
		if(sortSetting.getReverseSetting().getBooleanValue())
		{
			Collections.reverse(sortedProbes);
		}
		updateContents();
	}
	
	public void setProbes(List<Probe> probes)
	{
		this.sortedProbes=probes;
		sort();
		fireTableDataChanged();	
	}

	/**
	 * Returns the row the model keeps for this position. May be null if there is no such row. 
	 * @param row
	 * @param column
	 * @return
	 */
	public Probe getProbe(int row, int column)
	{			
		if(column==0)
		{
			return null;
		}
		int index=row*columns+(column-1);
		if(mode==DataMode.probemode)
		{			
			if(index >= sortedProbes.size() || index < 0)
			{
				return null;
			}
			return sortedProbes.get(index);		
		}else
		{
			return sortedData.get(index).probe;
		}
	}

	/**
	 * @return the columns
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(int columns) 
	{
		this.columns = columns;
		fireTableDataChanged();
		fireTableStructureChanged();
	}

	public SelectionIndex getProbeIndex(Probe p)
	{
		int i=sortedProbes.indexOf(p);
		int row=i / columns;
		int col=i % columns;
		
		return new SelectionIndex(row, col+1);
	}

	private class TableItem implements Comparable<TableItem>
	{
		public Probe probe;
		public String value;

		public TableItem(Probe p, String v) 
		{
			this.probe=p;
			this.value=v;
		}

		@Override
		public boolean equals(Object obj) 
		{
			return probe.equals(((TableItem)obj).probe) && value.equals(((TableItem)obj).value);
		}

		@Override
		public int compareTo(TableItem o) 
		{
			return probe.compareTo(o.probe)==0?value.compareTo(o.value):probe.compareTo(o.probe);
		}



	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) 
	{
		if(vme.getChange()==ViewModelEvent.DATA_MANIPULATION_CHANGED)
		{
			sort();
			fireTableDataChanged();	
		}
	}


}

