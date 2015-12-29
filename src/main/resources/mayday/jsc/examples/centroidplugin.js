@NOOPERATORS
/*
Author: Tobias Ries
Creates a ProbeListPlugin for Calculating centroids of a given set of ProbeLists
*/
importClass(Packages.mayday.core.pluma.prototypes.ProbelistPlugin);
importClass(Packages.mayday.core.ProbeList);
importClass(Packages.mayday.core.meta.types.AnnotationMIO);
importClass(Packages.mayday.jsc.tools.PluginInterface);
importClass(java.util.ArrayList);

//Create Plugin-Action
var centrPl = new ProbelistPlugin()
{
 run:function(probeLists, masterTable)
 {
  var res = new ArrayList();
  for(var i = 0; i < probeLists.size(); i++)
  {  
   var pli = probeLists.get(i);
   
   //Create new ProbeList with similar properties as current base-probelist
   var pl = new ProbeList(pli.getDataSet(), false);
   pl.setColor(pli.getColor());
   pl.setName("Centroid of "+pli.getName());
   //Create new ProbeList - EOF
   
   //Calculate and add centroid
   var centroid = pli.getStatistics().getMedian();
   centroid.setName("Median of "+pli.getName());
   pl.addProbe(centroid);
   //Calculate and add centroid - EOF
   
   //Annotation
   var quickinfo = "Centroid of "+pli.getName()+" over "+centroid.getNumberOfExperiments()+" Experiments.";
   var info = quickinfo +"Max: "+centroid.getMaxValue()+" - Min:"+centroid.getMinValue()+" - Mean: "+centroid.getMean()+" - Variance: "+centroid.getVariance();
   pl.setAnnotation(new AnnotationMIO(info,quickinfo));
   //Annotation - EOF  
   
   res.add(pl);
  }
  return res;
 }
}

//Register Plugin
PluginInterface.registerPlugin(centrPl, "Create Centroids", "js.crCentroids");
