package mayday.mqi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;
import mayday.mqi.shell.MQIConsole;
import mayday.mushell.Console;

public class MQIPlugin  extends AbstractPlugin implements GenericPlugin
{

	@Override
	public void init() {
		Properties p = System.getProperties();
		p.setProperty("derby.stream.error.method", "mayday.mqi.MQIUtils.disableDerbyLogFile");
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.MQI.probelist",
				new String[]{},
				Constants.MC_SESSION,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Query a probelist using SQL",
				"SQL Query interface..."
				);
		pli.addCategory(Console.CONSOLE_SUBMENU);
		return pli;
	}

	@Override
	public void run() 
	{
		Connection con=null;

		
		Properties p = System.getProperties();
		p.setProperty("derby.system.home", MaydayDefaults.Prefs.getPluginDirectory()+"/derby-dbs/");
		
		try 
		{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (Throwable e) {
			throw new RuntimeException("Could not load Apache Derby Database Driver");
		}
		
		try {
			con = DriverManager.getConnection( "jdbc:derby:mqi;create=true" );			
			Statement st=con.createStatement();
		
			MQIUtils.dropFunctions(st);			
			MQIUtils.initFunctions(st);	
			
			MQIConsole console=new MQIConsole(con);
			console.setVisible(true);			

		} catch (SQLException e) {
			try {
				if (con!=null) {
					con.rollback();
					con.close();
					
					DriverManager.getConnection("jdbc:derby:mqi;shutdown=true");
				}
			} catch (SQLException e1) {
//				System.out.println(e1.getNextException().getMessage());
				throw new RuntimeException(e1.getMessage());
			}
			e.printStackTrace();
			System.out.println(e.getNextException().getMessage());
			throw new RuntimeException(e.getMessage());
		}
		
	}
	
	@Override
	public void unload() 
	{
		super.unload();
		try {
			DriverManager.getConnection("jdbc:derby:mqi;shutdown=true");			
		} catch (SQLException e) {	} // never mind. 
	}

}
