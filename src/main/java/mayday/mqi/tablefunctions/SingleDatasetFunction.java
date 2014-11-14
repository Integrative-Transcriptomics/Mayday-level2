package mayday.mqi.tablefunctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.mqi.vti.core.EnumeratorTableFunction;

public class SingleDatasetFunction extends EnumeratorTableFunction
{
	private static DataSet dataset;
	
	
	private static String[] COLUMN_NAMES = new String[0];

	public SingleDatasetFunction() throws SQLException
	{
		super( COLUMN_NAMES );
		if (dataset==null)
			setEnumeration(Collections.emptyList());
		else
			setEnumeration(dataset.getMasterTable().getProbes().values());		
	}
	
	protected SingleDatasetFunction(String[] columnNames)  throws SQLException
	{
		super(columnNames);
		if (dataset==null)
			setEnumeration(Collections.emptyList());
		else
			setEnumeration(dataset.getMasterTable().getProbes().values());		
	}
	
	private static void setColumnNames()
	{
		COLUMN_NAMES=new String[dataset.getMasterTable().getNumberOfExperiments()+2];
		COLUMN_NAMES[0]="probe";
		COLUMN_NAMES[1]="dataset";
		for(int i=0; i!= dataset.getMasterTable().getNumberOfExperiments(); ++i)
		{
			COLUMN_NAMES[i+2]=dataset.getMasterTable().getExperimentName(i);
		}		
	}
	
	public static ResultSet query() throws SQLException
	{
		return new SingleDatasetFunction();
	}
	
	public String[] makeRow(Probe p)
	{
		String[] row=new String[getColumnCount()];
		row[0]=p.getName();
		row[1]=p.getMasterTable().getDataSet().getName();
		for(int i=0; i!= p.getNumberOfExperiments(); ++i)
		{
			row[2+i]=Double.toString(p.getValue(i));
		}
		return row;
	}

	public  boolean next()
	throws SQLException
	{
		
		if ( _enumeration == null ) { return false; }
		if ( !_enumeration.hasMoreElements()) { return false; }

//		if(currentProbe ==null)
//		{
//			if(!_enumeration.hasMoreElements()) return false;
//			currentProbe=(Probe)_enumeration.nextElement();			
//		}
		Probe currentProbe=(Probe)_enumeration.nextElement();	
		_row = makeRow( currentProbe);		
		return true;
	}

	@Override
	public String[] makeRow(Object obj) throws SQLException 
	{
		return null;
	}
	
	public static void setDataset(DataSet ds)
	{
		dataset=ds;
		setColumnNames();
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
