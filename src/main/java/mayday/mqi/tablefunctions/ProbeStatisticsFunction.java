package mayday.mqi.tablefunctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.datasetmanager.DataSetManager;
import mayday.mqi.vti.core.EnumeratorTableFunction;

public class ProbeStatisticsFunction extends EnumeratorTableFunction
{
	private DataSet currentDataset;
	private Iterator<Probe> currentProbes;
	
	private static  final   String[]    COLUMN_NAMES =
	{
		"name",
		"dataset",
		"min",
		"max",
		"mean",
		"var",
		"sd"		
	};

	public ProbeStatisticsFunction() throws SQLException
	{
		super( COLUMN_NAMES );
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}
	
	protected ProbeStatisticsFunction(String[] columnNames) throws SQLException
	{
		super(columnNames);	
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}
	

	public static ResultSet query() throws SQLException
	{
		return new ProbeStatisticsFunction();
	}
	
	public  String[]  makeRow( Object obj ) throws SQLException
	{
		Probe p=(Probe)obj;
		
		String[] row=new String[getColumnCount()];
		row[0]=p.getDisplayName();
		row[1]=p.getMasterTable().getDataSet().getName();
		row[2]=""+p.getMinValue();
		row[3]=""+p.getMaxValue();
		row[4]=""+p.getMean();
		row[5]=""+p.getVariance();
		row[6]=""+p.getStandardDeviation();
		
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
