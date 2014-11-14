package mayday.jsc;

import java.util.HashMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;
import mayday.jsc.shell.JSConsole;
import mayday.jsc.shell.JSSettings;
import mayday.mushell.Console;


/** 
 * JavaScript Plugin for Mayday 
 *
 * @version 1.0 
 * @author Tobias Ries, tobias.ries@exean.net
 */
public class JSConsolePlugin extends AbstractPlugin implements GenericPlugin
{	
	
	public void init() {}
	
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.JSConsole.console",
				new String[]{},
				Constants.MC_SESSION,
				(HashMap<String,Object>)null,
				"Tobias Ries",
				"tobias.ries@exean.net",
				"Use JavaScript to control Mayday",
				"JavaScript Console");			
		pli.addCategory(Console.CONSOLE_SUBMENU);
        return pli;
	}

	@Override
	public void run()
	{	
		new JSSettings(PluginManager.getInstance().getPluginFromID("PAS.JSConsole.console"));		
		JSConsole console = new JSConsole("JavaScript Mayday");
		console.setVisible(true);
	};	
	
}
