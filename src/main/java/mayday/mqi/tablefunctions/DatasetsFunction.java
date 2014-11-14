package mayday.mqi.tablefunctions;

import java.sql.ResultSet;
import java.sql.SQLException;

import mayday.core.DataSet;
import mayday.core.datasetmanager.DataSetManager;
import mayday.mqi.vti.core.EnumeratorTableFunction;

public class DatasetsFunction extends EnumeratorTableFunction
{
	private static  final   String[]    COLUMN_NAMES =
	{
		"name",
		"info",
		"quickInfo",
	};

	public DatasetsFunction() throws SQLException
	{
		super( COLUMN_NAMES );
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}
	
	protected DatasetsFunction(String[] columnNames) throws SQLException
	{
		super(columnNames);		
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}
	

	public static ResultSet query() throws SQLException
	{
		return new DatasetsFunction();
	}
	
	public  String[]  makeRow( Object obj ) throws SQLException
	{
		DataSet d=(DataSet)obj;
		
		String[] row=new String[getColumnCount()];
		row[0]=d.getName();
		row[1]=d.getAnnotation().getInfo();
		row[2]=d.getAnnotation().getQuickInfo();
		return row;

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
