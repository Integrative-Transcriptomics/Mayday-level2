package mayday.vis3.plots.tagcloud;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.gui.MaydayDialog;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.StringListMIO;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.gradient.ColorGradientSetting.LayoutStyle;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.model.DefaultGraphModel;
import mayday.vis3.graph.model.SimpleSelectionModel;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;
import mayday.vis3.graph.renderer.primary.ProfilePlotRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plotsWithoutModel.histogram.HistogramWithMeanComponent;

@SuppressWarnings("serial")
public class TagCloudViewer extends GraphCanvas implements ViewModelListener, SettingChangeListener
{
	private ViewModel viewModel;
	private TagCloudSettings settings;
	private SuperColorProvider coloring;
	private ColorGradientSetting gradientSetting;
	private RestrictedStringSetting tagMappingSetting;
	
	private TagCloudLegend legend;
	
	public TagCloudViewer()
	{
		super(new DefaultGraphModel(new Graph()));		
		setLayouter(new TagLayout());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setup(PlotContainer plotContainer) 
	{
		super.setup(plotContainer);
		create(plotContainer);

		for (Setting s : settings.getChildren())
			plotContainer.addViewSetting(s, this);

		plotContainer.setPreferredTitle("Tag Cloud", this);	

		JMenu menu=plotContainer.getMenu(PlotContainer.ENHANCE_MENU, this);
		menu.add(new HistogramAction());
		menu.add(new FilterAction());
	}

	private void create(PlotContainer plotContainer)
	{
		viewModel=plotContainer.getViewModel();
		viewModel.addViewModelListener(this);
		coloring=new SuperColorProvider(viewModel);
		coloring.setExperiment(0);

		coloring.setMode(ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE);
		coloring.addChangeListener(new ChangeListener()	{
			public void stateChanged(ChangeEvent e) 
			{
				updatePlot();				
			}});
		viewModel.addViewModelListener(coloring);
		gradientSetting=new ColorGradientSetting("Color Gradient", "The color gradient for tag frequency", ColorGradient.createDefaultGradient(0, 1));
		gradientSetting.setLayoutStyle(LayoutStyle.FULL);
		gradientSetting.addChangeListener(this);
		plotContainer.addViewSetting(gradientSetting, this);
		setLayouter(new TagLayout());
		settings=new TagCloudSettings(viewModel.getDataSet());
		settings.addChangeListener(this);
		
		tagMappingSetting=new RestrictedStringSetting("Tag Mapping",null,0, new String[]{"Linear","Logarithmic","Exponential"});
		tagMappingSetting.addChangeListener(this);
		plotContainer.addViewSetting(tagMappingSetting, this);
		
		initialInput();		

		// create Renderer Map
		Map<String,ComponentRenderer> rendererMap=new HashMap<String, ComponentRenderer>();
		rendererMap.put(Nodes.Roles.NODE_ROLE, new DefaultComponentRenderer());
		rendererMap.put(Nodes.Roles.PROBES_ROLE, new ProfilePlotRenderer(coloring));
		rendererMap.put(TagConstants.TAG_ROLE, new TagRenderer(gradientSetting.getColorGradient()));
		setRenderer(rendererMap);
	}
	
	private AbstractTagCloudFactory tagCloudFactory(){
		if(tagMappingSetting.getSelectedIndex()==0)
			return AbstractTagCloudFactory.getLinearInstance();
		if(tagMappingSetting.getSelectedIndex()==1)
			return AbstractTagCloudFactory.getLogInstance();
		if(tagMappingSetting.getSelectedIndex()==2)
			return AbstractTagCloudFactory.getExpInstance();
		return AbstractTagCloudFactory.getLinearInstance();
		
	
	}

	public void viewModelChanged(ViewModelEvent vme) 
	{
		if(vme.getChange()==ViewModelEvent.PROBELIST_SELECTION_CHANGED || vme.getChange()==ViewModelEvent.TOTAL_PROBES_CHANGED)
		{
			MIGroup miGroup=settings.getMiGroupSetting().getMIGroup();
			TagCloudModel model=null;
			if(miGroup==null)
			{
				initialInput();
				updatePlot();
				return;
			}
			model=new TagCloudModel(tagCloudFactory().MIOByFrequency(viewModel.getProbes(), miGroup));
			setModel(model);
			
			// create Renderer Map
			Map<String,ComponentRenderer> rendererMap=new HashMap<String, ComponentRenderer>();
			rendererMap.put(Nodes.Roles.NODE_ROLE, new DefaultComponentRenderer());
			rendererMap.put(Nodes.Roles.PROBES_ROLE, new ProfilePlotRenderer(coloring));
			rendererMap.put(TagConstants.TAG_ROLE, new TagRenderer(gradientSetting.getColorGradient()));
			setRenderer(rendererMap);
			
			updatePlot();
			
			
		}
	}



	private void initialInput()
	{
		List<Tag> tags = tagCloudFactory().probeListsByProbes(viewModel.getProbeLists(false));
		TagCloudModel model=new TagCloudModel(tags);
		setModel(model);
		model.updateLegend(gradientSetting.getColorGradient());
		setSelectionModel(new SimpleSelectionModel(model, viewModel));
		
		// create Renderer Map
		Map<String,ComponentRenderer> rendererMap=new HashMap<String, ComponentRenderer>();
		rendererMap.put(Nodes.Roles.NODE_ROLE, new DefaultComponentRenderer());
		rendererMap.put(Nodes.Roles.PROBES_ROLE, new ProfilePlotRenderer(coloring));
		rendererMap.put(TagConstants.TAG_ROLE, new TagRenderer(gradientSetting.getColorGradient()));
		setRenderer(rendererMap);
		
	}
	
	@Override
	public void paintPlot(Graphics2D g) {
		(g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		(g).setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		(g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintPlot(g);
	}

	public void stateChanged(SettingChangeEvent e) 
	{
		if(e.getSource()==gradientSetting)
		{
			updateLegend();
			updatePlot();
			repaint();
		}
		if(e.getSource()==settings.getMiGroupSetting() || e.getSource()==tagMappingSetting)
		{
			updateSource();
		}
		//		if(e.getSource()==settings.getMiListGroupSetting())
		//		{
		//			MIGroup miGroup=((MIGroupSetting)e.getSource()).getMIGroup();
		//			TagCloudModel model=new TagCloudModel(TagCloudFactory.StringByStringListMIO(viewModel.getProbes(), miGroup));
		//			setModel(model);
		//		}
		if(e.getSource()==settings.getLayouterSetting())
		{
			TagLayout tl=new TagLayout(((RestrictedStringSetting)e.getSource()).getSelectedIndex());
			setLayouter(tl);
		}	

		Map<String,ComponentRenderer> rendererMap=new HashMap<String, ComponentRenderer>();
		rendererMap.put(Nodes.Roles.NODE_ROLE, new DefaultComponentRenderer());
		rendererMap.put(Nodes.Roles.PROBES_ROLE, new ProfilePlotRenderer(coloring));
		rendererMap.put(TagConstants.TAG_ROLE, new TagRenderer(gradientSetting.getColorGradient()));
		setRenderer(rendererMap);
	}

	private void updateSource() {
		MIGroup miGroup=settings.getMiGroupSetting().getMIGroup();
		TagCloudModel model;
		List<Tag> tags;
		if (StringListMIO.class.isAssignableFrom(miGroup.getMIOClass())) {
			tags = tagCloudFactory().StringByStringListMIO(viewModel.getProbes(), miGroup);
			model=new TagCloudModel(tags);
		} else {
			tags = tagCloudFactory().MIOByFrequency(viewModel.getProbes(), miGroup);
			model=new TagCloudModel(tags);
		}
		setModel(model);
		updateLegend();
		viewModel.removeViewModelListener((SimpleSelectionModel)getSelectionModel());
		setSelectionModel(new SimpleSelectionModel(model, viewModel));
	}
	
	private class HistogramAction extends AbstractAction
	{
		public HistogramAction() 
		{
			super("Show Frequency Histogram");
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			List<Double> freqs=new LinkedList<Double>();
			for(Node n:getModel().getGraph().getNodes())
			{
				freqs.add(1.0*Integer.parseInt(((DefaultNode)n).getPropertyValue(TagConstants.TAG_COUNT)));
			}
			HistogramWithMeanComponent comp=new HistogramWithMeanComponent(new Dimension(400, 300));
			comp.getHistogramPlotComponent().getValueProvider().setValues(freqs);	
			comp.getHistogramPlotComponent().updatePlot();

			MaydayDialog freqHistDialog=new MaydayDialog();
			freqHistDialog.setTitle("Frequency Distribution");

			freqHistDialog.add(comp);
			freqHistDialog.pack();
			freqHistDialog.setVisible(true);
		}
	}

	private class FilterAction extends AbstractAction
	{
		public FilterAction() 
		{
			super("Filter by Frequency");
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			List<Double> freqs=new LinkedList<Double>();
			for(Node n:getModel().getGraph().getNodes())
			{
				freqs.add(1.0*Integer.parseInt(((DefaultNode)n).getPropertyValue(TagConstants.TAG_COUNT)));
			}
			FrequencyFilteringDialog ffd=new FrequencyFilteringDialog(freqs);
			ffd.setVisible(true);

			if(ffd.getCutoff() >=0)
			{
				removeAll();
				TagCloudModel model = ((TagCloudModel)getModel());
				((TagCloudModel)getModel()).filter(ffd.getCutoff(),ffd.isInvert());
				
				for(CanvasComponent c : model.getComponents()) {
					if(c.isVisible()) {
						add(c);
					}
				}
				
				updateSize();
				updateLegend();
				layouter.layout(TagCloudViewer.this, getBounds(), getModel());
			}

		}
	}

	public void setRenderer(Map<String,ComponentRenderer> rendererMap) 
	{
		if(model==null) return;
		ComponentRenderer render=renderer;
		for(CanvasComponent c:this.model.getComponents())
		{
			if(c instanceof TagComponent)
			{
				((TagComponent)c).setRenderer(rendererMap);
			}
			if(c instanceof NodeComponent)
			{

				if(rendererMap.get(((NodeComponent) c).getNode().getRole())!=null)
					render=rendererMap.get(((NodeComponent) c).getNode().getRole());
				c.setRenderer(render);			
			}			
		}
		updateLayout();		
	}
	
	@Override
	public void removeNotify() 
	{
		viewModel.removeViewModelListener(this);
		viewModel.removeViewModelListener(coloring);
		viewModel.removeViewModelListener((SimpleSelectionModel)getSelectionModel());
		super.removeNotify();
	}

	public void setLegend(TagCloudLegend tagCloudLegend) {
		legend = tagCloudLegend;
	}
	
	private void updateLegend() {
		((TagCloudModel)getModel()).updateLegend(gradientSetting.getColorGradient());
		legend.updateLegend();
	}
	
	public void updateSize()
	{
		int maxX=0;
		int maxY=0;
		for(CanvasComponent c:this.model.getComponents())
		{
			if(c.isVisible()) {
				if(c.getBounds().getMaxX() > maxX) maxX=c.getBounds().x+c.getBounds().width;
				if(c.getBounds().getMaxY() > maxY) maxY=c.getBounds().y+c.getBounds().height;
			}
		}
		maxX+=50;
		maxY+=50;
		
		setPreferredSize(new Dimension(maxX,maxY));
		setSize(new Dimension(maxX,maxY));
		
		updatePlot();
	}
}
