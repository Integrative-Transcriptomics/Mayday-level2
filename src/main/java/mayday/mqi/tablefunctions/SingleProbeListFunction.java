package mayday.mqi.tablefunctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import mayday.core.Probe;
import mayday.mqi.vti.core.EnumeratorTableFunction;

public class SingleProbeListFunction  extends EnumeratorTableFunction
{
	private static List<Probe> probes = Collections.emptyList();
	private static String[] COLUMN_NAMES = new String[0];
	
	
	public SingleProbeListFunction() throws SQLException
	{
		super( COLUMN_NAMES );
		setEnumeration(probes);		
	}
	
	protected SingleProbeListFunction(String[] columnNames)  throws SQLException
	{
		super(columnNames);
		setEnumeration(probes);		
	}
	
	public static void setProbes(List<Probe> probes)
	{
		SingleProbeListFunction.probes=probes;
		setColumnNames();
	}
	
	private static void setColumnNames()
	{
		COLUMN_NAMES=new String[probes.get(0).getNumberOfExperiments()+2];
		COLUMN_NAMES[0]="probe";
		COLUMN_NAMES[1]="dataset";
		for(int i=0; i!= probes.get(0).getNumberOfExperiments(); ++i)
		{
			COLUMN_NAMES[i+2]=probes.get(0).getMasterTable().getExperimentName(i);
		}		
	}
	
	public static ResultSet query() throws SQLException
	{
		return new SingleProbeListFunction();
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

		Probe currentProbe=(Probe)_enumeration.nextElement();	
		_row = makeRow( currentProbe);		
		return true;
	}

	@Override
	public String[] makeRow(Object obj) throws SQLException 
	{
		return null;
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
