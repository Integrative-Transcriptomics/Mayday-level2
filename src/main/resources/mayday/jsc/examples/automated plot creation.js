@NOOPERATORS

/*
Author: Tobias Ries
Reads a textfile and creates according plots
 Textfile:
  Name\tProbeX,ProbeY,ProbeZ
  Name2\tProbe1,Probe2,Probe3
  ...
*/

importClass(Packages.javax.swing.JOptionPane);
importClass(Packages.javax.swing.JFileChooser);
importClass(Packages.java.io.FileInputStream);
importClass(Packages.java.io.InputStream);
importClass(Packages.java.io.File);
importClass(Packages.java.io.InputStreamReader);
importClass(Packages.java.io.BufferedReader);
importClass(Packages.java.util.ArrayList);

importClass(Packages.mayday.core.pluma.filemanager.FMFile);
importClass(Packages.mayday.core.pluma.prototypes.ProbelistPlugin);
importClass(Packages.mayday.core.ProbeList);

importClass(Packages.mayday.core.settings.typed.IntSetting);
importClass(Packages.mayday.core.settings.generic.HierarchicalSetting);
importClass(Packages.mayday.core.settings.SettingDialog);

importClass(Packages.mayday.core.gui.MaydayFrame);
importClass(Packages.mayday.core.plugins.probelist.RecolorProbeLists);

importClass(Packages.mayday.jsc.tools.PluginInterface);

importPackage(Packages.mayday.vis3.components);
importClass(Packages.mayday.vis3.model.Visualizer);
importClass(Packages.mayday.vis3.plots.profile.ProfilePlotComponent);

var centrPl = new ProbelistPlugin()
{
 run:function(probeLists, masterTable)
 {
   //Default size of final plot
   var width = 800;
   var height = 600;
    
 //Settings
var widthSetting, heightSetting;  
var mySetting = new HierarchicalSetting("Settings", Packages.mayday.core.settings.generic.HierarchicalSetting.LayoutStyle.TABBED, true)		
.addSetting
(
widthSetting = new IntSetting("Plot width", "Exact width of the resulting image", width)
)
.addSetting
(
heightSetting = new IntSetting("Plot height", "Exact height of the resulting image", height)
);
var setDia = new SettingDialog(null, "Automated Plot Creation", mySetting);
if (setDia.showAsInputDialog().closedWithOK())
{
 width = widthSetting.getIntValue();
 height = heightSetting.getIntValue();
}
 //Settings - eof	
  

    
   //Settings for Plots, will be set after First Plot is created.
   var userS;
   
   //Unique Color Plugin
   var uniqueColors =  PluginManager.getInstance().getPluginFromID("PAS.core.RecolorProbelists").newInstance();
   
   //Setup Exportplugin
   var svgPlug = PluginManager.getInstance().getPluginFromID("PAS.vis3.export.png").newInstance();
   var res = svgPlug.getSetting();
   res.setAntialiasing(true, false);
   //Setup Export - EOF
   
   //Select File 
   var j = new JFileChooser();
   if(j.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
     return null;
   var path = j.getSelectedFile().getAbsolutePath();
   //Select File - EOF
   
   //Outputfolder
   j.setCurrentDirectory(j.getSelectedFile());
   j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
   j.setDialogTitle("Select target folder");
   if(j.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
     return null;
   j.getSelectedFile().mkdirs();
   var output = j.getSelectedFile().getAbsolutePath();
   //Outputfolder - EOF
   
   //Prepare reading file
   var rconn = PluginManager.getInstance().getFilemanager().getFile(path);
   var fs = rconn != null ? rconn.getStream() : new FileInputStream(path);	
   var br = new BufferedReader(new InputStreamReader(fs));
   //Prepare reading file - EOF   	
   			
   //Read each line and create plot
   var line;
   while ((line=br.readLine())!=null)
   {   
    line = line.split("\t");//Name and Probelist seperated bei \t
    var name = line[0];
    var probes = line[1].split(",");//Probes seperated bei ,
    
    //These will contain all created ProbeLists
    var arr = new ArrayList();    
    
    //Create a ProbeList for each Probe
    for(var pID in probes)
    {
     var p = masterTable.getProbe(probes[pID]);
     if (p!=null) {
       var pl = new ProbeList(masterTable.getDataSet(), false);
       pl.setName(p.getDisplayName());     
       pl.addProbe(p);
       arr.add(pl);
     }
    }
    //Create a ProbeList for each Probe - EOF    
    
    //Unique Color Plugin
    uniqueColors.run(arr,masterTable);

    //Create Plot:    
    var vis = new Visualizer(masterTable.getDataSet(), arr);
    var bp  = new PlotWithLegendAndTitle(new ProfilePlotComponent());
    var spc = new ScriptablePlotContainer(bp, vis, width, height);
    var hsc = spc.getPlotSettings();      
    
    //Display first Plot, get Settings
    if(userS == null)
    {
     var f = new MaydayFrame(name);
     f.add(bp);
     f.setSize(width, height);
     f.setVisible(true);
     var setDia = new SettingDialog(null, "Plot Settings", hsc);
    
     if (setDia.showAsInputDialog().closedWithOK())
      userS = hsc;
     
     f.setVisible(false);    
    }
    else
     hsc.fromPrefNode(userS.toPrefNode());
    //Settings - EOF
    
    // export the file
    spc.setSize(width, height);
    spc.exportToFile(svgPlug, output+File.separator+name+".png");       
   }
   //Read each line and create plot - EOF
   
   //Close reader 
   br.close();  
      
   return null;
 }
}

//Register Plugin
new PluginInterface().registerPlugin(centrPl, "Automated Plot generation", "js.AutomatedPlots");
