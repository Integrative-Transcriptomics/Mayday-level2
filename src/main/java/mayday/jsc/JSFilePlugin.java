package mayday.jsc;

import java.util.HashMap;

import javax.script.ScriptEngine;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;
import mayday.jsc.adjustableBehaviour.JSOverloadingOperators;
import mayday.jsc.adjustableBehaviour.JSReplacements;
import mayday.jsc.recognition.JSClassRecognizer;
import mayday.jsc.recognition.JSCommandRecognizer;
import mayday.jsc.shell.JSConsole;
import mayday.jsc.shell.JSDispatcher;


/** 
 * JavaScript direct Execution Plugin for Mayday 
 *
 * @version 1.0 
 * @author Tobias Ries, tobias.ries@exean.net
 */
public class JSFilePlugin extends AbstractPlugin implements GenericPlugin
{		
	JSDispatcher dispatcher;
	
	public void init() {}
	
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.JSConsole.file",
				new String[]{},
				Constants.MC_SESSION,
				(HashMap<String,Object>)null,
				"Tobias Ries",
				"tobias.ries@exean.net",
				"Use JavaScripts without the JSC",
				"Execute a JavaScript");
		//pli.addCategory(MaydayDefaults.Plugins.CATEGORY_DATAMINING);				

        return pli;
	}

	@Override
	public void run()
	{

		//Choose a file to be executed
		JFileChooser j = new JFileChooser();
		if(j.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			return;
					
		try
		{
			/*
			 * Lazy initialization since most users won't use js on every session
			 */
			if(this.dispatcher == null)
			{
				/*
				 * Try to get the script-dispatcher up and running.
				 * It wouldn't be sufficient to just initialize the ScripEngine
				 * as we can expect that most of the Scripts we'll deal with are
				 * written within the JSC using meta-commands which wouldn't be
				 * available in pure JavaScript (e.g. definition and usage of 
				 * overloaded operators).
				 */
				ScriptEngine engine = JSConsole.initEngine();
				JSCommandRecognizer cmdRecon = new JSCommandRecognizer(engine);
				this.dispatcher = new JSDispatcher(
						engine,
						new JSReplacements(),
						new JSOverloadingOperators(
								new JSClassRecognizer(
										engine, 
										cmdRecon)),
						cmdRecon);
			}
			this.dispatcher.evalFile(j.getSelectedFile().getAbsolutePath());			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,		
					e.getMessage(),
					"Script execution failed!",					
					JOptionPane.ERROR_MESSAGE);			
		}			
		
		JOptionPane.showMessageDialog(null,		
				"Execution of script '"+j.getSelectedFile().getName()+"' finished",
				"Script execution finished",					
				JOptionPane.INFORMATION_MESSAGE);		
		
	};	
	

	
}
