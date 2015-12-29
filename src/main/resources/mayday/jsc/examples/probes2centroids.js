@NOOPERATORS
/*
Author: Tobias Ries
Assigns Probes from a Probelist to a given set of centroids
*/

importClass(java.util.ArrayList);

importPackage(Packages.mayday.core.math.distance.measures);
importClass(Packages.mayday.core.pluma.prototypes.ProbelistPlugin);
importClass(Packages.mayday.core.pluma.PluginManager);
importClass(Packages.mayday.core.ProbeList);
importClass(Packages.mayday.core.gui.probelist.ProbeListSelectionDialog);
importClass(Packages.mayday.core.settings.methods.DistanceMeasureSetting);
importClass(Packages.mayday.core.settings.generic.HierarchicalSetting);
importClass(Packages.mayday.core.settings.SettingDialog);
importClass(Packages.mayday.jsc.tools.PluginInterface);

//Create Plugin-Action
var centr = new ProbelistPlugin()
{
 run:function(probeLists, masterTable)
 {
 var distMeasure = new EuclideanDistance();//Default: Euclidean
 
 //Settings for choosing a distance measure
var distMeasureSetting;  
var mySetting = new HierarchicalSetting("Settings", Packages.mayday.core.settings.generic.HierarchicalSetting.LayoutStyle.TABBED, true)		
.addSetting
(
distMeasureSetting = new DistanceMeasureSetting("Distance Measure", "Choose a Distance Measure to be used while assigning Probes to Centroids", distMeasure)
);
var setDia = new SettingDialog(null, "Choose a Distance Measure", mySetting);
if (setDia.showAsInputDialog().closedWithOK())
 distMeasure = 
PluginManager.getInstance().getPluginFromID(distMeasureSetting.getStringValue()).newInstance();
 //Settings for choosing a distance measure - eof		
		
  //Selection of centroid-ProbeLists
  var plM = new ArrayList();
  for(var i = 0; i < DataSetMgrInstance.size(); i++)
   plM.add(DataSetMgrInstance.getDataSets().get(i).getProbeListManager());
  var plDia = new ProbeListSelectionDialog(plM);
  
  plDia.setDialogDescription("Please choose ProbeLists containing only centroids.")
  plDia.setVisible(true);
  var centroids = plDia.getSelection();
  if(plDia.isCanceled() || centroids.size() < 1)//Centroid-Selection-Canceled/Nothing selected
   return null;      
   
  //Selection of centroid-ProbeLists - EOF  
  for(var j = 0; j < probeLists.size(); j++)  
  for(var i = 0; i < probeLists.get(0).getAllProbes().size(); i++)
  {  
   var pli = probeLists.get(j).getProbe(i);
   
   var bestMatch = centroids.get(0);
   var bestValue = distMeasure.getDistance(bestMatch.getProbe(0).getValues(),pli.getValues());
   for(var c = 1; c < centroids.size(); c++)
   {
    var plc = centroids.get(c);
    var curValue = distMeasure.getDistance(plc.getProbe(0).getValues(), pli.getValues());
    if(curValue < bestValue)
    {
     bestValue = curValue;
     bestMatch = plc;
    } 
   }
   
   bestMatch.addProbe(pli);
  }  
  return null;
 }
}

//Register Plugin
PluginInterface.registerPlugin(centr, "Assign Probes to Centroids", "js.assProbes2Centroids");
