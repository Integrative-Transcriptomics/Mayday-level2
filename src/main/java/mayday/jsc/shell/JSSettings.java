package mayday.jsc.shell;

import java.util.ArrayList;

import mayday.core.pluma.PluginInfo;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.FilesSetting;

/** 
 * Handles JSC-specific settings 
 *
 * @version 1.0
 * @author Tobias Ries, ries@exean.net
 */
public class JSSettings
{
	PluginInfo pli;
	Settings settings;
	private static JSSettings settingsInstance;	

	public JSSettings(PluginInfo pli)	
	{
		this.pli = pli;
		this.init();
		settingsInstance = this;
	}
	
	public synchronized static JSSettings getInstance()
	{        
        return settingsInstance;
    }
		
	private void init()
	{
		ArrayList<String> defaultStartUp = new ArrayList<String>();
		defaultStartUp.add("mayday/jsc/shell/startUp.js");
				
		HierarchicalSetting mySetting = new HierarchicalSetting("Settings", LayoutStyle.TABBED, true)
			.setCombineNonhierarchicalChildren(true)
			.addSetting
			(
				new HierarchicalSetting("Options", LayoutStyle.PANEL_HORIZONTAL, false)
					.addSetting(new BooleanSetting("Classic Autocompletion (Restart required)","Advisable for slow systems, takes effect after next start of Plugin.",false))
					//.addSetting(new BooleanSetting("Create log",null,false))
			)
			.addSetting
			(
				new FilesSetting("StartUp-Scripts","Scripts to be automatically evaluated at JSC start",defaultStartUp)
			);
		
		this.settings = new Settings(mySetting, pli.getPreferences());				
	}
	
	/**
	 * 
	 * @return SettingDialog for displaying and aditting settings 
	 */
	public void showSettingsDialog()
	{
		new SettingsDialog(null, "JSConsole Settings", this.settings).showAsInputDialog();
	}
	
	public Settings getSettings()
	{
		return this.settings;
	}
	
	
}
