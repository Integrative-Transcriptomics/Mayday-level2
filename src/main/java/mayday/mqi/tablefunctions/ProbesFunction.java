package mayday.mqi.tablefunctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.datasetmanager.DataSetManager;
import mayday.mqi.vti.core.EnumeratorTableFunction;

public class ProbesFunction extends EnumeratorTableFunction
{
	private DataSet currentDataset;
	private Iterator<Probe> currentProbes;
	
	private static  final   String[]    COLUMN_NAMES =
	{
		"name",
		"dataset",
		"info",
		"quickInfo",
		"displayname"
	};

	public ProbesFunction() throws SQLException
	{
		super( COLUMN_NAMES );
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}
	
	protected ProbesFunction(String[] columnNames) throws SQLException
	{
		super(columnNames);	
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}
	

	public static ResultSet query() throws SQLException
	{
		return new ProbesFunction();
	}
	
	public  String[]  makeRow( Object obj ) throws SQLException
	{
		Probe p=(Probe)obj;
		
		String[] row=new String[getColumnCount()];
		row[0]=p.getName();
		row[1]=p.getMasterTable().getDataSet().getName();
		if(p.getAnnotation()==null)
		{
			row[2]=null;
			row[3]=null;
		}else
		{
			row[2]=p.getAnnotation().getInfo();
			row[3]=p.getAnnotation().getQuickInfo();
		}
		row[4]=p.getDisplayName();
		return row;

	}
	
	public  boolean next() throws SQLException
	{		
		if ( _enumeration == null ) { return false; }
		if ( !_enumeration.hasMoreElements() && !currentProbes.hasNext()) { return false; }
		
		if(currentDataset ==null || !currentProbes.hasNext())
		{
			if(!_enumeration.hasMoreElements()) return false;
			currentDataset=(DataSet)_enumeration.nextElement();
			currentProbes=currentDataset.getMasterTable().getProbes().values().iterator();			
		}
		_row = makeRow(currentProbes.next() );
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
