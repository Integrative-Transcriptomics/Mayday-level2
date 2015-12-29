package mayday.mqi.tablefunctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.datasetmanager.DataSetManager;
import mayday.mqi.vti.core.EnumeratorTableFunction;
			 
public class ProbeValuesFunction extends EnumeratorTableFunction
{
	private Iterator<Probe> currentProbes;
	private int experiment;
	Probe currentProbe;
	DataSet currentDataSet;
	
	private static  final   String[] COLUMN_NAMES =
	{
		"name",
		"dataset",
		"experiment",
		"value"
	};

	public ProbeValuesFunction() throws SQLException
	{
		super( COLUMN_NAMES );
		setEnumeration(DataSetManager.singleInstance.getDataSets());		
		experiment=0;
	}
	
	protected ProbeValuesFunction(String[] columnNames)  throws SQLException
	{
		super(columnNames);
		setEnumeration(DataSetManager.singleInstance.getDataSets());
		experiment=0;
	}
	
	public static ResultSet query() throws SQLException
	{
		return new ProbeValuesFunction();
	}


	@Override
	public String[] makeRow(Object obj) throws SQLException {
		return null;
	}
	
	public String[] makeRow(Probe p, int exp)
	{
		String[] row=new String[getColumnCount()];
		row[0]=p.getName();
		row[1]=p.getMasterTable().getDataSet().getName();
		row[2]=Integer.toString(exp);
		row[3]=Double.toString(p.getValue(exp));
		return row;
	}

	public  boolean next() throws SQLException
	{
		if ( _enumeration == null ) 
		{ 
			return false; 
		}
		if ( !_enumeration.hasMoreElements() && experiment >= currentProbe.getNumberOfExperiments() && !currentProbes.hasNext()) 
		{ 
			return false; 
		}
		if(currentProbes==null || ( !currentProbes.hasNext() && experiment >= currentProbe.getNumberOfExperiments()) )
		{
			currentDataSet=(DataSet)_enumeration.nextElement();
			currentProbes=currentDataSet.getMasterTable().getProbes().values().iterator();
			currentProbe=currentProbes.next();
			experiment=0;
		}
		if(experiment >= currentProbe.getNumberOfExperiments())
		{
			currentProbe=currentProbes.next();
			experiment=0;
		}
		_row = makeRow( currentProbe,experiment );
		experiment++;
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
