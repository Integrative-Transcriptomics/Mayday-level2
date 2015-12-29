package mayday.jsc.tools;

import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.SurrogatePlugin;
import mayday.core.pluma.SurrogatePluginInfo;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.core.pluma.prototypes.ProbelistPlugin;

/*
 * Almost the same as in RConnector, only changed surrogate-object-type
 */
public class PluginInterface
{
	public static void registerPlugin(ProbelistPlugin run, String name, String id )
	{						
		try {
			addPlugin(new SurrogatePluginInfo<ProbelistPlugin, JSProbelistPlugin>(
					JSProbelistPlugin.class, 
					run,
					id,
					new String[0],
					Constants.MC_PROBELIST,
					new HashMap<String, Object>(),
					"x", "x", "x",
					name));						
		} catch (PluginManagerException e) {
			e.printStackTrace();
		}
	}
	
	public static void registerPlugin(DatasetPlugin run, String name, String id )
	{						
		try {
			addPlugin(new SurrogatePluginInfo<DatasetPlugin, JSDatasetPlugin>(
					JSDatasetPlugin.class, 
					run,
					id,
					new String[0],
					Constants.MC_DATASET,
					new HashMap<String, Object>(),
					"x", "x", "x",
					name));						
		} catch (PluginManagerException e) {
			e.printStackTrace();
		}
	}
	
	public static void registerPlugin(DistanceMeasurePlugin run, String name, String id )
	{						
		try {
			addPlugin(new SurrogatePluginInfo<DistanceMeasurePlugin, JSDistanceMeasurePlugin>(
					JSDistanceMeasurePlugin.class, 
					run,
					id,
					new String[0],
					Constants.MC_PROBELIST,
					new HashMap<String, Object>(),
					"x", "x", "x",
					name));						
		} catch (PluginManagerException e) {
			e.printStackTrace();
		}
	}
	
	private static void addPlugin(PluginInfo pli)
	{
		PluginManager.getInstance().addLatePlugin(pli);
	}

	
	public static class JSProbelistPlugin extends AbstractPlugin implements ProbelistPlugin, SurrogatePlugin<ProbelistPlugin>
	{	
		ProbelistPlugin myCommand;
		@Override
		public void initializeWithObject(ProbelistPlugin surrogateObject, PluginInfo pli)
		{
			myCommand = surrogateObject;			
		}

		public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable)
		{
			return this.myCommand.run(probeLists, masterTable);			
		}

		@Override
		public void init()
		{
		}

		@Override
		public PluginInfo register() throws PluginManagerException 
		{		
			return null;
		}
		
	}
	
	public static class JSDatasetPlugin extends AbstractPlugin implements DatasetPlugin, SurrogatePlugin<DatasetPlugin>
	{	
		DatasetPlugin myCommand;
		
		@Override
		public void initializeWithObject(DatasetPlugin surrogateObject, PluginInfo pli)
		{
			myCommand = surrogateObject;			
		}

		@Override
		public void init() {
		}

		public PluginInfo register() throws PluginManagerException
		{
			return null;
		}

		@Override
		public List<DataSet> run(List<DataSet> datasets)
		{
			return this.myCommand.run(datasets);		
		}
		
	}
	
	public static class JSDistanceMeasurePlugin extends DistanceMeasurePlugin implements SurrogatePlugin<DistanceMeasurePlugin>
	{	
		DistanceMeasurePlugin myCommand;

		@Override
		public void initializeWithObject(DistanceMeasurePlugin surrogateObject, PluginInfo pli)
		{
			this.myCommand = surrogateObject;
			
		}
		
		@Override
		public double getDistance(double[] VectorOne, double[] VectorTwo)
		{
			return this.myCommand.getDistance(VectorOne, VectorTwo);
		}

		@Override
		public PluginInfo register() throws PluginManagerException
		{		
			return null;
		}

	}
	
}
