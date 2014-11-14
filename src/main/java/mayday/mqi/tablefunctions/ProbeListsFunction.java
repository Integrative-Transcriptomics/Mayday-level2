package mayday.mqi.tablefunctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.mqi.vti.core.EnumeratorTableFunction;

public class ProbeListsFunction extends EnumeratorTableFunction
{
	private DataSet currentDataset;
	private Iterator<ProbeList> currentProbeLists;
	
	private static  final   String[]    COLUMN_NAMES =
	{
		"name",
		"dataset",
		"parent",
		"color",
		"info",
		"quickInfo",
		
	};

	public static ResultSet query() throws SQLException
	{
		return new ProbeListsFunction();
	}
	
	public  String[]  makeRow( Object obj ) throws SQLException
	{
		ProbeList pl=(ProbeList)obj;
		
		String[] row=new String[getColumnCount()];
		row[0]=pl.getName();
		row[1]=pl.getDataSet().getName();
		if(pl.getParent()==null)
		{
			row[2]=null;
		}else
		{
			row[2]=pl.getParent().getName();
		}
		row[3]=Integer.toString(pl.getColor().getRGB());
		row[4]=pl.getAnnotation().getInfo();
		row[5]=pl.getAnnotation().getQuickInfo();		
		return row;

	}

	public ProbeListsFunction() throws SQLException
	{
		super( COLUMN_NAMES );
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	
	}
	
	protected ProbeListsFunction(String[] columnNames) throws SQLException
	{
		super(columnNames);		
		setEnumeration(DataSetManager.singleInstance.getDataSets());
	}
	
	public  boolean next() throws SQLException
	{		
		if ( _enumeration == null ) { return false; }
		if ( !_enumeration.hasMoreElements() && !currentProbeLists.hasNext()) { return false; }
		
		if(currentDataset ==null || !currentProbeLists.hasNext())
		{
			if(!_enumeration.hasMoreElements()) return false;
			currentDataset=(DataSet)_enumeration.nextElement();
			currentProbeLists=currentDataset.getProbeListManager().getProbeLists().iterator();			
		}
		_row = makeRow(currentProbeLists.next() );
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