package mayday.mqi;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.mqi.tablefunctions.SingleDatasetFunction;
import mayday.mqi.tablefunctions.SingleProbeListFunction;

public class MQIUtils 
{
	public static void initFunctions(Statement st) throws SQLException
	{
		st.executeUpdate("" +
				"CREATE FUNCTION probes () " +
				"RETURNS TABLE ( name VARCHAR(250), dataset VARCHAR(250), info VARCHAR(2000), quickinfo VARCHAR(250), displayname VARCHAR(250) )" +
				"LANGUAGE java parameter style DERBY_JDBC_RESULT_SET no sql " +
				"external name 'mayday.mqi.tablefunctions.ProbesFunction.query'");
		st.executeUpdate("CREATE VIEW probes AS SELECT * FROM table(probes())s");
		
		st.executeUpdate("" +
				"CREATE FUNCTION probeValues () " +
				"RETURNS TABLE ( name VARCHAR(250),dataset VARCHAR(250), exp INTEGER, value DOUBLE )" +
				"LANGUAGE java parameter style DERBY_JDBC_RESULT_SET no sql " +
				"external name 'mayday.mqi.tablefunctions.ProbeValuesFunction.query'");
		st.executeUpdate("CREATE VIEW probevalues AS SELECT * FROM table(probeValues())s");
		
		st.executeUpdate("" +
				"CREATE FUNCTION experiments () " +
				"RETURNS TABLE ( name VARCHAR(250),dataset VARCHAR(250), exp INTEGER )" +
				"LANGUAGE java parameter style DERBY_JDBC_RESULT_SET no sql " +
				"external name 'mayday.mqi.tablefunctions.ExperimentsFunction.query'");
		st.executeUpdate("CREATE VIEW experiments AS SELECT * FROM table(experiments())s");
		
		st.executeUpdate("" +
				"CREATE FUNCTION datasets () " +
				"RETURNS TABLE ( name VARCHAR(250), info VARCHAR(2000), quickinfo VARCHAR(500) )" +
				"LANGUAGE java parameter style DERBY_JDBC_RESULT_SET no sql " +
				"external name 'mayday.mqi.tablefunctions.DatasetsFunction.query'");
				st.executeUpdate("CREATE VIEW datasets AS SELECT * FROM table(datasets())s");
				
		st.executeUpdate("" +
				"CREATE FUNCTION probelists () " +
				"RETURNS TABLE ( name VARCHAR(250), dataset VARCHAR(250), parent VARCHAR(200), color INTEGER, info VARCHAR(2000), quickInfo VARCHAR(500) )" +
				"LANGUAGE java parameter style DERBY_JDBC_RESULT_SET no sql " +
				"external name 'mayday.mqi.tablefunctions.ProbeListsFunction.query'");
		st.executeUpdate("CREATE VIEW probelists AS SELECT * FROM table(probelists())s");
		
		st.executeUpdate("" +
				"CREATE FUNCTION probestatistics() " +
				"RETURNS TABLE ( name VARCHAR(250), dataset VARCHAR(250), minimim DOUBLE, maximim DOUBLE, mean DOUBLE, var DOUBLE, sd DOUBLE )" +
				"LANGUAGE java parameter style DERBY_JDBC_RESULT_SET no sql " +
				"external name 'mayday.mqi.tablefunctions.ProbeStatisticsFunction.query'");
		st.executeUpdate("CREATE VIEW probestatistics AS SELECT * FROM table(probestatistics())s");
		
		st.executeUpdate("" +
				"CREATE FUNCTION probeToList() " +
				"RETURNS TABLE ( name VARCHAR(250), dataset VARCHAR(250), probelist VARCHAR(250) )" +
				"LANGUAGE java parameter style DERBY_JDBC_RESULT_SET no sql " +
				"external name 'mayday.mqi.tablefunctions.ProbeToListFunction.query'");
		st.executeUpdate("CREATE VIEW probeToList AS SELECT * FROM table(probeToList())s");
		
		st.executeUpdate("" +
				"CREATE FUNCTION miogroups() " +
				"RETURNS TABLE ( name VARCHAR(250), dataset VARCHAR(250), datatype VARCHAR(250) )" +
				"LANGUAGE java parameter style DERBY_JDBC_RESULT_SET no sql " +
				"external name 'mayday.mqi.tablefunctions.MIOGroupsFunction.query'");
		st.executeUpdate("CREATE VIEW miogroups AS SELECT * FROM table(miogroups())s");
		
		st.executeUpdate("" +
				"CREATE FUNCTION datasetMIOs() " +
				"RETURNS TABLE ( name VARCHAR(250), dataset VARCHAR(250), val VARCHAR(1000) )" +
				"LANGUAGE java parameter style DERBY_JDBC_RESULT_SET no sql " +
				"external name 'mayday.mqi.tablefunctions.DataSetMIOsFunction.query'");
		st.executeUpdate("CREATE VIEW datasetMIOs AS SELECT * FROM table(datasetMIOs())s");
		
		st.executeUpdate("" +
				"CREATE FUNCTION probeMIOs() " +
				"RETURNS TABLE ( name VARCHAR(250), dataset VARCHAR(250), probe VARCHAR(250), val VARCHAR(1000) )" +
				"LANGUAGE java parameter style DERBY_JDBC_RESULT_SET no sql " +
				"external name 'mayday.mqi.tablefunctions.ProbeMIOsFunction.query'");
		st.executeUpdate("CREATE VIEW probeMIOs AS SELECT * FROM table(probeMIOs())s");
		
		st.executeUpdate("" +
				"CREATE FUNCTION probelistMIOs() " +
				"RETURNS TABLE ( name VARCHAR(250), dataset VARCHAR(250), probe VARCHAR(250), val VARCHAR(1000) )" +
				"LANGUAGE java parameter style DERBY_JDBC_RESULT_SET no sql " +
				"external name 'mayday.mqi.tablefunctions.ProbeListMIOsFunction.query'");
		st.executeUpdate("CREATE VIEW probelistMIOs AS SELECT * FROM table(probelistMIOs())s");
		
		st.executeUpdate("" +
				"CREATE FUNCTION addToProbeList (probelist VARCHAR(250), probe VARCHAR(250), dataset VARCHAR(250) ) " +
				"RETURNS INTEGER " +
				"LANGUAGE java parameter style java no sql " +
				"external name 'mayday.mqi.functions.CreateProbeListFunction.execute'");		
	}
	
	public static void dropFunctions(Statement st) 
	{			
		drop(st,"VIEW probes");
		drop(st,"VIEW probevalues");
		drop(st,"VIEW experiments");
		drop(st,"VIEW datasets");
		drop(st,"VIEW probelists");
		drop(st,"VIEW probestatistics");
		drop(st,"VIEW probeToList");
		drop(st,"VIEW miogroups");
		drop(st,"VIEW datasetMIOs");
		drop(st,"VIEW probeMIOs");
		drop(st,"VIEW probelistMIOs");
		drop(st,"FUNCTION probes");
		drop(st,"FUNCTION probevalues");
		drop(st,"FUNCTION experiments");
		drop(st,"FUNCTION datasets");
		drop(st,"FUNCTION probelists");
		drop(st,"FUNCTION probestatistics");
		drop(st,"FUNCTION addToProbeList");	
		drop(st,"FUNCTION probeToList");
		drop(st,"FUNCTION miogroups");
		drop(st,"FUNCTION datasetMIOs");
		drop(st,"FUNCTION probeMIOs");
		drop(st,"FUNCTION probelistMIOs");
		
	}
	
	public static void drop(Statement st, String s)
	{
		try{
			st.execute("DROP "+s);
		}catch(Exception e){
	e.printStackTrace();
		} // die silently.
	}
	
	public static void createSingleDatasetFunction(Statement st, DataSet ds) throws SQLException
	{
		drop(st," VIEW currentDataset");
		drop(st, "FUNCTION currentDataset");
		String s="" +
					"CREATE FUNCTION currentDataset() \n" +
					"RETURNS TABLE ( "+buildColumnNames(ds)+" )" +
					"LANGUAGE java parameter style DERBY_JDBC_RESULT_SET no sql " +
					"external name 'mayday.mqi.tablefunctions.SingleDatasetFunction.query'";
				
		st.executeUpdate(s);
		st.executeUpdate("CREATE VIEW currentDataset AS SELECT * FROM table(currentDataset())s");
		// prepare table functions
		SingleDatasetFunction.setDataset(ds);
	}
	
	public static void createSingleProbeListFunction(Statement st, Collection<Probe> probes) throws SQLException
	{
		drop(st," VIEW currentProbeList");
		drop(st, "FUNCTION currentProbeList");
		String s="" +
					"CREATE FUNCTION currentProbeList() \n" +
					"RETURNS TABLE ( "+buildColumnNames(probes.iterator().next().getMasterTable().getDataSet())+" )" +
					"LANGUAGE java parameter style DERBY_JDBC_RESULT_SET no sql " +
					"external name 'mayday.mqi.tablefunctions.SingleProbeListFunction.query'";
				
		st.executeUpdate(s);
		st.executeUpdate("CREATE VIEW currentProbeList AS SELECT * FROM table(currentProbeList())s");
		// prepare table functions
		SingleProbeListFunction.setProbes(new ArrayList<Probe>(probes));
	}
	
	private static String buildColumnNames(DataSet dataset)
	{
		StringBuffer sb=new StringBuffer("name VARCHAR(250), dataset VARCHAR(250)");
		for(int i=0; i!=dataset.getMasterTable().getNumberOfExperiments(); ++i)
		{
			sb.append(", \""+dataset.getMasterTable().getExperimentName(i)+ "\" DOUBLE" );
		}
		return sb.toString();
	}
	
	public static OutputStream disableDerbyLogFile() { 
		return new java.io.OutputStream() { 
			public void write(int b) throws IOException {
				// Ignore all log messages 
			} 
		}; 
	}

}
