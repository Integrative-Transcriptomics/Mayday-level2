package mayday.mqi.tablefunctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import mayday.core.DataSet;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.meta.MIGroup;
import mayday.mqi.vti.core.EnumeratorTableFunction;

public class MIOGroupsFunction extends EnumeratorTableFunction
{
	private Iterator<MIGroup> groups;
	private DataSet currentDataSet;
	
	private static  final   String[] COLUMN_NAMES =
	{
		"miogroupname",
		"dataset",
		"datatype"
	};
	
	public MIOGroupsFunction() throws SQLException
	{
		super( COLUMN_NAMES );
		setEnumeration(DataSetManager.singleInstance.getDataSets());				
	}
	
	protected MIOGroupsFunction(String[] columnNames)  throws SQLException
	{
		super(columnNames);
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}
	
	public static ResultSet query() throws SQLException
	{
		return new MIOGroupsFunction();
	}


	@Override
	public String[] makeRow(Object obj) throws SQLException 
	{
		return null;
	}
	
	public String[] makeRow(MIGroup m, DataSet ds)
	{
		String[] row=new String[getColumnCount()];
		row[0]=m.getName();
		row[1]=ds.getName();
		row[2]=m.getMIOType();
		return row;
	}

	public boolean next() throws SQLException
	{
		if ( _enumeration == null ) 
		{ 
			return false; 
		}
		if ( !_enumeration.hasMoreElements() && !groups.hasNext()) 
		{ 
			return false; 
		}
		MIGroup grp=null;
		if(groups==null || ( !groups.hasNext()))
		{
			currentDataSet=(DataSet)_enumeration.nextElement();
			groups=currentDataSet.getMIManager().getGroups().iterator();
		}
		grp=groups.next();
		_row = makeRow( grp,currentDataSet );
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
