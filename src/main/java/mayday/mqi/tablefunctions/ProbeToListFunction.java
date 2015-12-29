package mayday.mqi.tablefunctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.mqi.vti.core.EnumeratorTableFunction;

public class ProbeToListFunction extends EnumeratorTableFunction
{
	private Iterator<ProbeList> currentProbeLists;
	private Iterator<Probe> currentProbes;
	private ProbeList probeList;
	
	private static  final   String[] COLUMN_NAMES =
	{
		"probe",
		"dataset",
		"probelist",
	};

	public ProbeToListFunction() throws SQLException
	{
		super( COLUMN_NAMES );
		setEnumeration(DataSetManager.singleInstance.getDataSets());			
	}
	
	protected ProbeToListFunction(String[] columnNames)  throws SQLException
	{
		super(columnNames);
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}
	
	public static ResultSet query() throws SQLException
	{
		return new ProbeToListFunction();
	}


	@Override
	public String[] makeRow(Object obj) throws SQLException {
		return null;
	}
	
	public String[] makeRow(Probe p, ProbeList pl)
	{
		String[] row=new String[getColumnCount()];
		row[0]=p.getName();
		row[1]=p.getMasterTable().getDataSet().getName();
		row[2]=pl.getName();
		return row;
	}

	public  boolean next() throws SQLException
	{
		if ( _enumeration == null ) 
		{ 
			return false; 
		}
		if ( !_enumeration.hasMoreElements() && !currentProbeLists.hasNext() && !currentProbes.hasNext()) 
		{ 
			return false; 
		}
		if(currentProbeLists==null || ( !currentProbeLists.hasNext() && !currentProbes.hasNext() ) )
		{
			if(!_enumeration.hasMoreElements()) return false;
			DataSet currentDataSet=(DataSet)_enumeration.nextElement();
			currentProbeLists=currentDataSet.getProbeListManager().getProbeLists().iterator();
			probeList=currentProbeLists.next();
			currentProbes=probeList.getAllProbes().iterator();
		}
		while(!currentProbes.hasNext())
		{			
			
			probeList=currentProbeLists.next();
			currentProbes=probeList.getAllProbes().iterator();
		}
		if(!currentProbes.hasNext())
		{
			System.out.println("!!!!");
		}
		_row = makeRow(currentProbes.next(), probeList);
		
		return true;
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
