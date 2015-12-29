package mayday.mqi.tablefunctions;

import java.sql.ResultSet;
import java.sql.SQLException;

import mayday.core.DataSet;
import mayday.core.datasetmanager.DataSetManager;
import mayday.mqi.vti.core.EnumeratorTableFunction;

public class ExperimentsFunction extends EnumeratorTableFunction
{
	private DataSet currentDataSet;
	private int experiment;
	
	private static  final   String[]    COLUMN_NAMES =
	{
		"name",
		"dataset",
		"number",
	};

	public ExperimentsFunction() throws SQLException
	{
		super( COLUMN_NAMES );
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}
	
	protected ExperimentsFunction(String[] columnNames) throws SQLException
	{
		super(columnNames);		
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}
	

	public static ResultSet query() throws SQLException
	{
		return new ExperimentsFunction();
	}
	
	public  String[]  makeRow( Object obj ) throws SQLException
	{
		DataSet d=(DataSet)obj;
		
		String[] row=new String[getColumnCount()];
		row[0]=d.getMasterTable().getExperimentName(experiment);
		row[1]=d.getName();
		row[2]=""+(experiment);
		return row;

	}
	
	public  boolean next() throws SQLException
	{
		if ( _enumeration == null ) { return false; }
		if ( !_enumeration.hasMoreElements() && experiment > currentDataSet.getMasterTable().getNumberOfExperiments()) 
		{ return false; 
		}
		
		if(currentDataSet ==null || experiment >= currentDataSet.getMasterTable().getNumberOfExperiments())
		{
			if(!_enumeration.hasMoreElements()) return false;
			
			currentDataSet=(DataSet)_enumeration.nextElement();
			experiment=0;
		}
		_row = makeRow( currentDataSet );
		experiment++;
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
