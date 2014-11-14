package mayday.vis3.plots.termpyramid;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import mayday.core.Probe;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.components.BasicPlotPanel;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.gradient.ColorGradientSetting.LayoutStyle;
import mayday.vis3.graph.vis3.SuperColorProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.termpyramid.BarCellRenderer.ColorMode;


@SuppressWarnings("serial")
public class TermPyramidPlot extends BasicPlotPanel implements SettingChangeListener, TableModelListener, ViewModelListener
{
	private TermPyramidSettings settings;
	private JTable table=new JTable();
	private ViewModel viewModel;
	private TermPyramidModel model; 

	private SelectableHierarchicalSetting colorSetting;
	private ColorGradientSetting colorGradientSetting;
	private ObjectSelectionSetting<String> rendererSetting;
	private BooleanHierarchicalSetting spacerSetting;
	private IntSetting spacerValueSetting=new IntSetting("Spacer", "How much space should there be between sparkline points",2,0,10,true,true);

	private DefaultTableCellRenderer leftRenderer=new BarCellRenderer(false);
	private DefaultTableCellRenderer rightRenderer=new BarCellRenderer(true);

	private SuperColorProvider coloring;

	private static final String[] renderers={"Adundance","Mean Expression (Heatmap)","Mean Expression (Bar)",
		"Mean Expression (Profile)","Deviance from mean (Bar)"};

	public TermPyramidPlot() 
	{
		setLayout(new BorderLayout(5,0));
		//		addComponentListener(new ResizeHandler());
	}

	@Override
	public void setup(PlotContainer plotContainer) 
	{
		if(viewModel==null)
			create(plotContainer);
		plotContainer.setPreferredTitle("Term Pyramid", this);

		for(Setting s: settings.getChildren())
			plotContainer.addViewSetting(s, this);

		plotContainer.addViewSetting(colorSetting, this);
		plotContainer.addViewSetting(colorGradientSetting, this);
		plotContainer.addViewSetting(rendererSetting, this);
		plotContainer.addViewSetting(spacerSetting, this);
		plotContainer.addViewSetting(coloring.getSetting(), this);
	}

	private void create(PlotContainer plotContainer)
	{
		viewModel=plotContainer.getViewModel();
		settings=new TermPyramidSettings(viewModel);
		
		
		model=new TermPyramidModel(viewModel, settings);	
		settings.addChangeListener(model);
		settings.addChangeListener(this);
		coloring=new SuperColorProvider(viewModel,"HeatMap Color Gradient");
		viewModel.addViewModelListener(coloring);
		coloring.getSetting().addChangeListener(this);
		table.setModel(model);
		leftRenderer=new BarCellRenderer(false);
		rightRenderer=new BarCellRenderer(true);
		setTableHeader();
		table.setBackground(Color.WHITE);
		table.setShowGrid(false);

		TableRowSorter<TermPyramidModel> sorter=new TableRowSorter<TermPyramidModel>(model);
		sorter.setComparator(0, new CollectionSizeComparator());
		sorter.setComparator(4, new CollectionSizeComparator());
		table.setRowSorter(sorter);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() 
		{			
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				if(model.isEmpty()) return;
				Set<Probe> currentSelection=new HashSet<Probe>();
				for(int i: table.getSelectedRows())
				{
					int ic=table.getRowSorter().convertRowIndexToModel(i);
					currentSelection.addAll(model.getRow(ic).pl1Probes);
					currentSelection.addAll(model.getRow(ic).pl2Probes);
				}
				viewModel.setProbeSelection(currentSelection);
			}
		});
		model.addTableModelListener(this);
		add(table,BorderLayout.CENTER);
		add(table.getTableHeader(),BorderLayout.NORTH);
		String[] colorOptions={"ProbeList","Term","Value"};		
		colorSetting=new SelectableHierarchicalSetting("Color Terms by",null,0,colorOptions);
		colorGradientSetting=new ColorGradientSetting("Abundance Color Gradient",null,ColorGradient.createDefaultGradient(0, 1));
		colorGradientSetting.setLayoutStyle(LayoutStyle.FULL);
		rendererSetting=new RestrictedStringSetting("Renderer", "How to render the probes with a term", 0, renderers);
		spacerSetting=new BooleanHierarchicalSetting("Bar renderer fixed space",null,true);		
		spacerSetting.addSetting(spacerValueSetting);
		spacerSetting.addChangeListener(this);
		colorSetting.addChangeListener(this);
		colorGradientSetting.addChangeListener(this);

		rendererSetting.addChangeListener(this);
		spacerSetting.addChangeListener(this);

		viewModel.addViewModelListener(this);


	}

	public void setTableHeader()
	{
		DefaultTableColumnModel columnModel=new DefaultTableColumnModel();	

		TableColumn col0=new TableColumn(0, 200, leftRenderer,null);		
		TableColumn col1=new TableColumn(1, 25, new AlignedLabelRenderer(SwingConstants.LEFT),null);
		TableColumn col2=new TableColumn(2, 200, new AlignedLabelRenderer(SwingConstants.CENTER),null);
		TableColumn col3=new TableColumn(3, 25, new AlignedLabelRenderer(SwingConstants.RIGHT),null);			
		TableColumn col4=new TableColumn(4, 200, rightRenderer,null);

		col0.setHeaderValue(model.getColumnName(0));
		col0.setHeaderRenderer(new AlignedScaleHeaderRenderer(SwingConstants.RIGHT, SwingConstants.TOP));

		col1.setHeaderValue(model.getColumnName(1));
		col1.setHeaderRenderer(new SimpleHeaderRenderer(SwingConstants.CENTER,SwingConstants.TOP));

		col2.setHeaderValue(model.getColumnName(2));
		col2.setHeaderRenderer(new SimpleHeaderRenderer(SwingConstants.CENTER, SwingConstants.TOP));

		col3.setHeaderValue(model.getColumnName(3));
		col3.setHeaderRenderer(new SimpleHeaderRenderer(SwingConstants.CENTER,SwingConstants.TOP));

		col4.setHeaderValue(model.getColumnName(4));
		col4.setHeaderRenderer(new AlignedScaleHeaderRenderer(SwingConstants.LEFT, SwingConstants.TOP));

		columnModel.addColumn(col0);
		columnModel.addColumn(col1);
		columnModel.addColumn(col2);
		columnModel.addColumn(col3);
		columnModel.addColumn(col4);

		table.setColumnModel(columnModel);	
		JTableHeader header= new JTableHeader(columnModel);
		table.setTableHeader(header);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(),50));

		//		table.getTableHeader().setDefaultRenderer(new SimpleHeaderRenderer(SwingConstants.CENTER));

	}


	@Override
	public void updatePlot() 
	{
		repaint();				
	}

	@Override
	public void stateChanged(SettingChangeEvent e) 
	{
		if(e.getSource()==coloring.getSetting())
		{
			updatePlot();
			repaint();
		}
		if(e.getSource()==colorSetting || e.getSource()==rendererSetting || e.getSource()==spacerSetting || e.getSource()==spacerValueSetting)
		{
			ColorMode mode=null;
			switch (colorSetting.getSelectedIndex()) 
			{
			case 0: mode=ColorMode.PROBELIST;					
			break;
			case 1: mode=ColorMode.MIGROUP;				
			break;	
			case 2: mode=ColorMode.VALUE;
			break;		
			default:
				break;
			}

			if(rendererSetting.getSelectedIndex()==0)
			{
				BarCellRenderer rl=new BarCellRenderer(false);
				rl.setColoring(mode, colorGradientSetting.getColorGradient());
				BarCellRenderer rr=new BarCellRenderer(true);
				rr.setColoring(mode, colorGradientSetting.getColorGradient());
				leftRenderer=rl;
				rightRenderer=rr;
			}
			if(rendererSetting.getSelectedIndex()==1)
			{
				leftRenderer=new HeatStreamCellRenderer(coloring);
				rightRenderer=new HeatStreamCellRenderer(coloring);				
			}
			if(rendererSetting.getSelectedIndex()>=2)
			{
				leftRenderer=new SparklineRenderer(coloring,rendererSetting.getSelectedIndex(),spacerSetting.getBooleanValue()?spacerValueSetting.getIntValue():-1);
				rightRenderer=new SparklineRenderer(coloring,rendererSetting.getSelectedIndex(),spacerSetting.getBooleanValue()?spacerValueSetting.getIntValue():-1);

			}
			setTable();
			updatePlot();
//			return;
		}




		//		leftRenderer.setColoring(mode, colorGradientSetting.getColorGradient());
		//		rightRenderer.setColoring(mode, colorGradientSetting.getColorGradient());			

		table.repaint();
		updatePlot();
	}

	private void setTable()
	{
		remove(table);
		remove(table.getTableHeader());
		table.setModel(model);		
		setTableHeader();
		table.setBackground(Color.WHITE);
		table.setShowGrid(false);		
		TableRowSorter<TermPyramidModel> sorter=new TableRowSorter<TermPyramidModel>(model);
		sorter.setComparator(0, new CollectionSizeComparator());
		sorter.setComparator(4, new CollectionSizeComparator());
		table.setRowSorter(sorter);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() 
		{			
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				if(model.isEmpty()) return;
				Set<Probe> currentSelection=new HashSet<Probe>();
				for(int i: table.getSelectedRows())
				{
					int ic=table.getRowSorter().convertRowIndexToModel(i);
					currentSelection.addAll(model.getRow(ic).pl1Probes);
					currentSelection.addAll(model.getRow(ic).pl2Probes);
				}
				viewModel.setProbeSelection(currentSelection);
			}
		});		
		model.addTableModelListener(this);		
		add(table,BorderLayout.CENTER);
		add(table.getTableHeader(),BorderLayout.NORTH);
//		model.fireTableStructureChanged();
	}

	@Override
	public void tableChanged(TableModelEvent e) 
	{
		table.getTableHeader().getColumnModel().getColumn(0).setHeaderValue(model.getProbeList1().getName());
		table.getTableHeader().getColumnModel().getColumn(2).setHeaderValue(settings.getMiGroup().getMIGroup().getName());
		table.getTableHeader().getColumnModel().getColumn(4).setHeaderValue(model.getProbeList2().getName());	
		table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(),50));		
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) 
	{
		if(vme.getChange()==ViewModelEvent.TOTAL_PROBES_CHANGED || vme.getChange()==ViewModelEvent.PROBELIST_SELECTION_CHANGED)
			settings.setProbeLists();
		updatePlot();
	}
	
	@Override
	public void removeNotify() 
	{
		super.removeNotify();
		coloring.removeNotify();
		viewModel.removeViewModelListener(coloring);
		viewModel.removeViewModelListener(this);
	}

}
