package mayday.mqi.tablefunctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.mqi.vti.core.EnumeratorTableFunction;

public class ProbeMIOsFunction extends EnumeratorTableFunction
{
	private Iterator<Probe> currentProbes;
	private Iterator<MIGroup> currentGroups;
	Probe currentProbe;
	DataSet currentDataSet;
	
	private static  final   String[] COLUMN_NAMES =
	{
		"name",
		"dataset",
		"probe",
		"value"
	};

	public ProbeMIOsFunction() throws SQLException
	{
		super( COLUMN_NAMES );
		setEnumeration(DataSetManager.singleInstance.getDataSets());		
	}
	
	protected ProbeMIOsFunction(String[] columnNames)  throws SQLException
	{
		super(columnNames);
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}
	
	public static ResultSet query() throws SQLException
	{
		return new ProbeMIOsFunction();
	}


	@Override
	public String[] makeRow(Object obj) throws SQLException {
		return null;
	}
	
	public String[] makeRow(Probe p, MIGroup grp, MIType mio)
	{
		String[] row=new String[getColumnCount()];
		row[0]=grp.getName();
		row[1]=p.getMasterTable().getDataSet().getName();
		row[2]=p.getName();
		row[3]=mio.toString();
		return row;
	}

	public  boolean next() throws SQLException
	{
		if ( _enumeration == null ) 
		{ 
			return false; 
		}
		if ( !_enumeration.hasMoreElements() && !currentGroups.hasNext() && !currentProbes.hasNext()) 
		{ 
			return false; 
		}
		if(currentProbes==null || ( !currentProbes.hasNext() && !currentGroups.hasNext()) )
		{
			currentDataSet=(DataSet)_enumeration.nextElement();
			currentProbes=currentDataSet.getMasterTable().getProbes().values().iterator();
			currentProbe=currentProbes.next();
			currentGroups=currentDataSet.getMIManager().getGroupsForObject(currentProbe).iterator();
		}
		while(!currentGroups.hasNext())
		{
			if(currentProbes.hasNext())
				currentProbe=currentProbes.next();
			else
			{
				if(!_enumeration.hasMoreElements())
				{
					return false;
					}
				currentDataSet=(DataSet)_enumeration.nextElement();
				
				currentProbes=currentDataSet.getMasterTable().getProbes().values().iterator();
				currentProbe=currentProbes.next();
			}
			currentGroups=currentDataSet.getMIManager().getGroupsForObject(currentProbe).iterator();
			
		}
		MIGroup currentGroup=currentGroups.next();
		_row = makeRow( currentProbe, currentGroup, currentGroup.getMIO(currentProbe)  );
		return true;
		
//		if ( _enumeration == null ) { return false; }
//		if ( !_enumeration.hasMoreElements() && experiment >= currentProbe.getNumberOfExperiments() && !currentProbes.hasNext()) { return false; }
//		
//		if(currentProbes==null ||   )
//		{
//			currentDataSet=(DataSet)_enumeration.nextElement();
//			currentProbes=currentDataSet.getMasterTable().getProbes().values().iterator();
//			currentProbe=currentProbes.next();
//			experiment=0;
//		}
//		
//		if(currentProbe ==null || experiment >= currentProbe.getNumberOfExperiments())
//		{
//			if(!_enumeration.hasMoreElements()) return false;
//			currentProbe=currentProbes.next();
//			experiment=0;
//		}
//		_row = makeRow( currentProbe,experiment );
//		experiment++;
//		return true;
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
