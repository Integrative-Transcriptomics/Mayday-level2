@NOOPERATORS
importClass(Packages.mayday.vis3.model.Visualizer);
importClass(Packages.mayday.vis3.plots.boxplot.BoxPlotComponent);
importPackage(Packages.mayday.vis3.components);
importPackage(Packages.mayday.core.settings.typed);
importClass(Packages.mayday.core.settings.generic.HierarchicalSetting);
importClass(Packages.mayday.core.gui.MaydayFrame);

function bplot(ds, initialSelection, title, width, height){
 var vis = new Visualizer(ds, initialSelection);
 var bp  = new PlotWithLegendAndTitle(new BoxPlotComponent());
 var spc = new ScriptablePlotContainer(bp, vis, width, height);
 var hsc = spc.getPlotSettings();
 var bs = hsc.getChild("Overlay ProbeList boxes",true);
   bs.setBooleanValue(true);
   bs = hsc.getChild("Show Legend",true);
   bs.setBooleanValue(true);
   bs = hsc.getChild("Show caption",true);
   bs.setBooleanValue(true);
 var ss = hsc.getChild("Caption text", true);
   ss.setStringValue(title);
   hs = hsc.getChild("Grid", true);
   bs = hs.getChild("visible");
   bs.setBooleanValue(false);
   vis.getViewModel().setProbeSelection(initialSelection.get(0).getProbe(0));
 var f = new MaydayFrame("BoxPlot: "+title);
   f.add(bp);
   f.setSize(width, height);
   f.setVisible(true);
};

//bplot(DataSetMgrInstance.getDataSets().get(0),DataSetMgrInstance.getDataSets().get(0).getProbeListManager().getProbeLists(),"testplot",300,200);
