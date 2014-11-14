package mayday.mqi.shell;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.mushell.dispatch.AbstractDispatcher;

public class MQIDispatcher extends AbstractDispatcher
{
	private Connection con;
	private Statement st;
	private ResultSet result;

	private int maxline=500;

	private String command;

	public MQIDispatcher(Connection con)
	{
		this.con=con;
		try {
			this.st=con.createStatement();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		result=null;
	}

	@Override
	public Boolean dispatchCommandImpl(String command) 
	{	
		command=command.trim();
		if(command.isEmpty())
		{
			return true;
		}
		// JDBC does not like / require a ";" at the end of the command:
		if(command.endsWith(";"))
		{
			command=command.substring(0, command.length()-1);
		}
		try {
			if(dispatchCommands(command)) return true;
		} catch (SQLException e1) 
		{
			print(e1.getMessage());
		}
		String[] redirectTok=command.split(">>");
		if(redirectTok.length==2)
		{
			MQIDispatchRedirectThread t=new MQIDispatchRedirectThread(redirectTok[0],redirectTok[1]);
			Thread thread=new Thread(t);
			thread.setName("RedirectThread");
			thread.start();
			return true;
		}
		this.command=command;
		

		new Thread(new MQIDispatchThread()).start();
		System.out.println("Ende des Threads");
		
//		try {
//			
//			boolean b=st.execute(command);
//			if(b)
//			{
//				result=st.getResultSet();
//				dispatchResults();
//			}else
//			{
//				println(st.getUpdateCount()+ " rows updated");
//			}
//			
//		} catch (SQLException e) 
//		{
//			e.printStackTrace();
//			println(e.getMessage());
//			setReady(true);
//			return false;
//		}
//		setReady(true);
		return true;
	}
	
	private class MQIDispatchRedirectThread implements Runnable
	{
		private String query;
		private String target;
		
		public MQIDispatchRedirectThread(String query, String target)
		{
			this.query=query;
			this.target=target;
		}
		
		public void run()
		{
			setReady(false);
			dispatchRedirect(query,target);
			setReady(true);
			
		}
		
		private void dispatchRedirect(String query, String target)
		{
			target=target.trim();
			try {
				result=st.executeQuery(query);
				ResultSetMetaData md=result.getMetaData();
				int cc=md.getColumnCount();
				int probeName=-1;
				int datasetName=-1;
				List<Integer> experiments=new ArrayList<Integer>();
				List<String> experimentNames=new ArrayList<String>();
				for(int i=1; i<= cc; ++i)
				{
					if(md.getColumnLabel(i).equalsIgnoreCase("Name")) probeName=i;
					if(md.getColumnLabel(i).equalsIgnoreCase("Dataset")) datasetName=i;
					if(md.getColumnType(i)==Types.DOUBLE) 
					{
						experiments.add(i);
						experimentNames.add(md.getColumnLabel(i));
					}
				}
				if(probeName==-1)
				{
					println("Error redirecting to "+target+": probe name not found. Please select a column named \"name\".");
				}
				if(datasetName==-1 && experimentNames.isEmpty())
				{
					println("Error redirecting to  "+target+": dataset name not found. Please select a column named \"dataset\".");
				}
				if(experimentNames.isEmpty())
				{
					// dispatch to probeList
					dispatchToProbeList(probeName, datasetName, target);
				}else
				{
					dispatchToDataSet(probeName, experiments, experimentNames, target);
				}
				
				
			} catch (SQLException e) 
			{
				println(e.getMessage());
				setReady(true);
			}
		}
		
		private void dispatchToDataSet(int probeName, List<Integer> experiments,
				List<String> experimentNames, String target) throws SQLException 
		{
			DataSet ds=new DataSet(target);
			ds.getMasterTable().setNumberOfExperiments(experimentNames.size());
			ds.getMasterTable().setExperimentNames(experimentNames);
			ds.getAnnotation().setInfo("Created by MQI");
					
			while(result.next())
			{
				Probe p=new Probe(ds.getMasterTable());
				int exp=0;
				p.setName(result.getString(probeName));
				for(int i: experiments)
				{
					p.setValue(result.getDouble(i), exp);
					exp++;
				}
				ds.getMasterTable().addProbe(p);
			}
			DataSetManagerView.getInstance().addDataSet(ds);
			
		}

		private void dispatchToProbeList(int probeName, int datasetName, String target) throws SQLException
		{
			MultiTreeMap<String, String> datasetToProbe=new MultiTreeMap<String, String>();
			while(result.next())
			{
				datasetToProbe.put(result.getString(datasetName), result.getString(probeName) );
			}
			for(String dsName:datasetToProbe.keySet())
			{
				DataSet ds=null;
				for(DataSet d:DataSetManager.singleInstance.getDataSets())
				{
					if(d.getName().equals(dsName))
					{
						ds=d;
						break;
					}
				}
				if(ds==null)
				{
					println("Error: No dataset named "+dsName+" found. ");
					return;
				}
				
				ProbeList pl= null;
				
				for(ProbeList p:ds.getProbeListManager().getProbeLists())
				{
					if(p.getName().equals(target))
					{
						pl=p;
						break;
					}
				}
				if(pl==null)
				{
					pl=new ProbeList(ds,false);
					pl.setName(target);
					ds.getProbeListManager().addObjectAtTop(pl);
				}
				
				for(String pName:datasetToProbe.get(dsName))
				{
					Probe probe= ds.getMasterTable().getProbe(pName);
					if(probe==null) continue;
					if(!pl.contains(probe))
						pl.addProbe(probe);
				}				
			}
		}		
	}
	
	private class MQIDispatchThread implements Runnable
	{

		public void run() 
		{
			
			setReady(false);
			try 
			{
				boolean b=st.execute(command);
				if(b)
				{
					result=st.getResultSet();
					dispatchResults();
				}else
				{
					println(st.getUpdateCount()+ " rows updated");
				}				
			} catch (SQLException e) 
			{
				e.printStackTrace();
				println(e.getMessage());
				setReady(true);				
			}
			setReady(true);			
		}
		
		private void dispatchResults() throws SQLException
		{		
			ResultSetMetaData md=result.getMetaData();
			int cc=md.getColumnCount();
			int counter=0;

			for(int i=1; i<=cc; ++i)
			{
				print(md.getColumnName(i)+"\t");
			}
			print("\n");
			NumberFormat f=NumberFormat.getInstance();
			f.setMaximumFractionDigits(3);
			int skipped=0;
			while(result.next())
			{
				counter++;
				if(counter <= maxline) 
				{
					for(int i=1; i<=cc; ++i)
					{
						switch(result.getMetaData().getColumnType(i))
						{
						case Types.VARCHAR:
							if(result.getString(i)==null)
								print("NULL");
							else
								print(result.getString(i));
							print("\t");
							break;
						case Types.DOUBLE:
							print(f.format(result.getDouble(i)));
							print("\t");
							break;
						case Types.INTEGER:
							print(result.getInt(i));
							print("\t");
							break;
						default:
							System.err.println(result.getMetaData().getColumnType(i));
						}

					}	
					print("\n");
				}
				else
					skipped++;						
			}
			result.close();
			if(skipped!=0)
				println(skipped+" rows skipped");
		}		
	}
	
	private boolean dispatchCommands(String command) throws SQLException
	{
		if(command.startsWith("LIMIT"))
		{
			String[] tok=command.split("\\s");
			if(tok.length==1)
			{
				println("Maximum line count is "+maxline);
				return true;
			}
			maxline=Integer.parseInt(tok[1]);
			println("Maximum line count set to "+maxline);
			println("");
			return true;
		}
		if(command.startsWith("HELP"))
		{
			println("Available commands:");
			println("	HELP, LIMIT rowcount, DESCRIBE tablename");
			println("Use QUERY >> target to redirect:");
			println("	query name and dataset to redirect to probelist");
			println("	query name and values to redirect to dataset");
			println("");
			return true;
		}
		if(command.startsWith("DESCRIBE"))
		{
			String[] tok=command.split("\\s");
			describe(tok[1]);
			println("");
			return true;
		}
		return false;
	}
	
	private void describe(String table) throws SQLException
	{
		Statement st=con.createStatement();
		ResultSet res=st.executeQuery("SELECT * FROM "+table+" ");
		
		ResultSetMetaData md=res.getMetaData();
		int cc=md.getColumnCount();
		for(int i=1; i<=cc; ++i)
		{
			println(md.getColumnName(i)+"\t"+md.getColumnTypeName(i));
		}
		res.close();
		st.close();		
		
	}

	public String getName() {
		return "Mayday QI";
	}

	/**
	 * @return the res
	 */
	public ResultSet getRes() {
		return result;
	}





}
