package mayday.mqi.tablefunctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import mayday.core.DataSet;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.mqi.vti.core.EnumeratorTableFunction;

public class DataSetMIOsFunction extends EnumeratorTableFunction
{
	private Iterator<MIGroup> groups;
	private DataSet currentDataSet;
	
	private static  final   String[] COLUMN_NAMES =
	{
		"miogroupname",
		"dataset",
		"val"
	};
	
	public DataSetMIOsFunction() throws SQLException
	{
		super( COLUMN_NAMES );
		setEnumeration(DataSetManager.singleInstance.getDataSets());				
	}
	
	protected DataSetMIOsFunction(String[] columnNames)  throws SQLException
	{
		super(columnNames);
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}
	
	public static ResultSet query() throws SQLException
	{
		return new DataSetMIOsFunction();
	}


	@Override
	public String[] makeRow(Object obj) throws SQLException {
		return null;
	}
	
	public String[] makeRow(MIType m, MIGroup grp, DataSet ds)
	{
		String[] row=new String[getColumnCount()];
		row[0]=grp.getName();
		row[1]=ds.getName();
		if(m==null)
			row[2]=null;
		else
			row[2]=m.toString();

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
			groups=currentDataSet.getMIManager().getGroupsForObject(currentDataSet).iterator();
//			groups=currentDataSet.getMIManager().getGroups().iterator();

		}
		grp=groups.next();
	
		_row = makeRow(grp.getMIO(currentDataSet),grp,currentDataSet );
		return true;
	}

	public <T> T getObject(int arg0, Class<T> arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getObject(String arg0, Class<T> arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
