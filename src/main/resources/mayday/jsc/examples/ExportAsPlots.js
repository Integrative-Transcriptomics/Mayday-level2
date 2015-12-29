@NOOPERATORS

importClass(Packages.java.util.ArrayList);
importClass(Packages.mayday.core.ProbeList);
importClass(Packages.mayday.core.pluma.prototypes.ProbelistPlugin);
importClass(Packages.mayday.core.settings.typed.IntSetting);
importClass(Packages.mayday.core.settings.typed.StringSetting);
importClass(Packages.mayday.core.settings.typed.PathSetting);
importClass(Packages.mayday.core.settings.generic.HierarchicalSetting);
importClass(Packages.mayday.core.settings.generic.PluginTypeSetting);
importClass(Packages.mayday.core.settings.SettingDialog);
importClass(Packages.mayday.core.gui.MaydayFrame);
importClass(Packages.mayday.vis3.model.Visualizer);
importClass(Packages.mayday.vis3.plots.profile.ProfilePlotComponent);
importPackage(Packages.mayday.vis3.components);
importClass(Packages.mayday.jsc.tools.PluginInterface);

var plotter = new ProbelistPlugin()
{
	run:function(probeLists, masterTable)
 	{
		//Default values
		var width = 800;
		var height = 600;    
		var exportPlugin = PluginManager.getInstance().getPluginFromID("PAS.vis3.export.png").newInstance();
		var exportPlugins = PluginManager.getInstance().getPluginsFor("Graphics Export Filter");
		var manip = PluginManager.getInstance().getPluginFromID("PAS.manipulator.identity").newInstance();
		var manips = PluginManager.getInstance().getPluginsFor("Math/Data Manipulators");

		//Settings
		var mySetting = new HierarchicalSetting("Automatic plot exporter")		
		.addSetting(outputSetting = new PathSetting("Output folder","Select the output folder", "", true, true, false))
		.addSetting(nameSetting = new StringSetting("Name prefix", null, "plot"))
		.addSetting(widthSetting = new IntSetting("Plot width", "Exact width of the resulting image", width))
		.addSetting(heightSetting = new IntSetting("Plot height", "Exact height of the resulting image", height))
		.addSetting(manipSetting = new PluginTypeSetting("Data manipulation","Select the data manipulation", manip, manips))
		.addSetting(fileTypeSetting = new PluginTypeSetting("Format","Select the output format", exportPlugin, exportPlugins));
		
		var setDia = new SettingDialog(null, "Automated Plot Creation", mySetting);
		if (!setDia.showAsInputDialog().closedWithOK()) 
			return null;
			
		width = widthSetting.getIntValue();
		height = heightSetting.getIntValue();
		name = nameSetting.getStringValue();
		exportPlugin = fileTypeSetting.getInstance();
		fmtname = exportPlugin.getFormatName();
		manip = manipSetting.getInstance();
		output = outputSetting.getStringValue();
  
		//Settings for Plots, will be set after First Plot is created.
   		var userS;
   
		for (pli=0; pli!=probeLists.size(); ++pli) {   			
			var pl = probeLists.get(pli);
			var arr = new ArrayList();    
			arr.add(pl);
			var vis = new Visualizer(masterTable.getDataSet(), arr);
			vis.getViewModel().getDataManipulator().setManipulation(manip);
    			var bp  = new PlotWithLegendAndTitle(new ProfilePlotComponent());
			var spc = new ScriptablePlotContainer(bp, vis, width, height);
    			var hsc = spc.getPlotSettings();      

    		          //Display first Plot? ==> configure Settings
			if(userS == null) {
				var f = new MaydayFrame(name);
     				f.add(bp);
				f.setSize(width, height);
     				f.setVisible(true);
				var setDia = new SettingDialog(null, "Plot Settings", hsc);
    
				if (!setDia.showAsInputDialog().closedWithOK())
					return null;
				userS = hsc;
				width = bp.getWidth();
				height = bp.getHeight();
				f.dispose();
				// recreate the plot without gui interference
				vis = new Visualizer(masterTable.getDataSet(), arr);
				vis.getViewModel().getDataManipulator().setManipulation(manip);
    				bp  = new PlotWithLegendAndTitle(new ProfilePlotComponent());
				spc = new ScriptablePlotContainer(bp, vis, width, height);
    				hsc = spc.getPlotSettings();      
			} 

			hsc.fromPrefNode(userS.toPrefNode());
			// export the file
    			spc.setSize(width, height);
    			spc.exportToFile(exportPlugin, output+File.separator+name+pl.getName()+"."+fmtname);       
		}
	 
	return null;
	}
}

//Register Plugin
new PluginInterface().registerPlugin(plotter, "Export each as plot", "js.ExportPlots");

