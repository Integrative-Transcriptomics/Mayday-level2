package mayday.mqi.tablefunctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.mqi.vti.core.EnumeratorTableFunction;

public class ProbeListMIOsFunction extends EnumeratorTableFunction
{
	private DataSet currentDataset;
	private Iterator<ProbeList> currentProbeLists;
	private Iterator<MIGroup> currentGroups;
	private ProbeList currentProbeList;
	
	private static  final   String[]    COLUMN_NAMES =
	{
		"name",
		"dataset",
		"probelist",
		"color"
	};

	public static ResultSet query() throws SQLException
	{
		return new ProbeListMIOsFunction();
	}

	public String[] makeRow(ProbeList p, MIGroup grp, MIType mio)
	{
		String[] row=new String[getColumnCount()];
		row[0]=grp.getName();
		row[1]=p.getDataSet().getName();
		row[2]=p.getName();
		row[3]=mio.toString();
		return row;
	}


	public ProbeListMIOsFunction() throws SQLException
	{
		super( COLUMN_NAMES );
		setEnumeration(DataSetManager.singleInstance.getDataSets());

	}

	protected ProbeListMIOsFunction(String[] columnNames) throws SQLException
	{
		super(columnNames);		
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}

	public  boolean next() throws SQLException
	{		
		if ( _enumeration == null ) { return false; }
		if ( !_enumeration.hasMoreElements() && !currentProbeLists.hasNext()) { return false; }

		if(currentDataset ==null || !currentProbeLists.hasNext())
		{
			if(!_enumeration.hasMoreElements()) return false;
			currentDataset=(DataSet)_enumeration.nextElement();
			currentProbeLists=currentDataset.getProbeListManager().getProbeLists().iterator();	
			currentProbeList=currentProbeLists.next();
			currentGroups=currentDataset.getMIManager().getGroupsForObject(currentProbeList).iterator();
		}
		while(!currentGroups.hasNext())
		{
			if(currentProbeLists.hasNext())
				currentProbeList=currentProbeLists.next();
			else
			{
				if(!_enumeration.hasMoreElements())
				{
					return false;
					}
				currentDataset=(DataSet)_enumeration.nextElement();
				
				currentProbeLists=currentDataset.getProbeListManager().getProbeLists().iterator();
				currentProbeList=currentProbeLists.next();
			}
			currentGroups=currentDataset.getMIManager().getGroupsForObject(currentProbeList).iterator();
			
		}
		MIGroup currentGroup=currentGroups.next();
		_row = makeRow( currentProbeList, currentGroup, currentGroup.getMIO(currentProbeList)  );
		return true;
	}

	@Override
	public String[] makeRow(Object obj) throws SQLException 
	{
		return null;
	}

	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getObject(String columnLabel, Class<T> type)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}