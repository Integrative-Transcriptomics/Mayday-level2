package mayday.jsc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.filemanager.FMDirectory;
import mayday.core.pluma.filemanager.FMFile;
import mayday.core.pluma.prototypes.CorePlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.FilesSetting;
import mayday.jsc.adjustableBehaviour.JSOverloadingOperators;
import mayday.jsc.adjustableBehaviour.JSReplacements;
import mayday.jsc.recognition.JSClassRecognizer;
import mayday.jsc.recognition.JSCommandRecognizer;
import mayday.jsc.shell.JSConsole;
import mayday.jsc.shell.JSDispatcher;


/** 
 * Loads defined JavaScript-Plugins on Plugin-Load 
 *
 * @version 1.0 
 * @author Tobias Ries, tobias.ries@exean.net
 */
public class JSPluginLoaderPlugin extends AbstractPlugin implements CorePlugin
{		
	FilesSetting pluginSetting;
	
	public void init()
	{}
	
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.JSConsole.pluginsLoader",
				new String[]{},
				Constants.MC_CORE,
				(HashMap<String,Object>)null,
				"Tobias Ries",
				"tobias.ries@exean.net",
				"Loads JavaScript-Plugins on Mayday start",
				"Auto-Load JavaScript plugins on Mayday");				
		
        return pli;
	}

	@Override
	public void run()
	{
		PluginInfo.loadDefaultSettings(getSetting(), "PAS.JSConsole.pluginsLoader");
		this.executeScripts();
		//Display Settings
		//settingsDialog.showAsInputDialog();
		
		// extract example scripts
		FMDirectory fmd = PluginManager.getInstance().getFilemanager().getDirectory("mayday/jsc/examples/");
		for (FMFile f :fmd.getFiles(true))
			f.extract();
	}
	
	private void executeScripts()
	{
		JSDispatcher d;
		try
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
			d = new JSDispatcher(
						engine,
						new JSReplacements(),
						new JSOverloadingOperators(
								new JSClassRecognizer(
										engine, 
										cmdRecon)),
						cmdRecon);
			
			if(this.pluginSetting != null)
			{
				List<String> filenames = this.pluginSetting.getFileNames();								
				for(String path : filenames)
				{
					try {
						d.evalFile(path);
					} catch (ScriptException e)
					{					
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,		
					e.getMessage(),
					"Script execution failed!",					
					JOptionPane.ERROR_MESSAGE);			
		}						
	}

	
	@Override
	public Setting getSetting() {
		ArrayList<String> defaultStartUp = new ArrayList<String>();	
		//Add Default Plugins
		//Add Default Plugins - eof

		HierarchicalSetting mySetting = new HierarchicalSetting("Settings", LayoutStyle.TABBED, true)							
		.addSetting
		(
				this.pluginSetting = new FilesSetting("JavaScript Plugins","JavaScripts to be automatically evaluated at Mayday start",defaultStartUp)
		);

		return mySetting;

	}

}
