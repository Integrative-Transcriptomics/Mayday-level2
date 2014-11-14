package mayday.vis3.plots.chromogram;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;

import mayday.core.Probe;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.components.BasicPlotPanel;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.dialog.ComponentZoomFrame;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.primary.ProfilePlotRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.chromogram.multijtable.MultiJTable;
import mayday.vis3.plots.chromogram.multijtable.MultiSelectionModel;
import mayday.vis3.plots.chromogram.multijtable.SelectionIndex;
import mayday.vis3.plots.chromogram.probelistsort.ProbeComparatorFactory;
import mayday.vis3.plots.chromogram.probelistsort.ProbeComparisonMode;
import mayday.vis3.plots.chromogram.probelistsort.ProbeSortSetting;

@SuppressWarnings("serial")
public class ChromgramPlot extends BasicPlotPanel implements SettingChangeListener, ViewModelListener
{
	private NameSourceSetting displayDataSetting;
	private ProbeSortSetting probeSortSetting;
	private ChromogramDisplaySetting displaySetting; 
	private MultiJTable table=new MultiJTable();
	private ViewModel viewModel;
	private BlockViewTableModel model; 
	private ChromogramRenderer renderer;
	
	private SharedListSelectionHandler sharedListSelectionListener;

	public ChromgramPlot() 
	{
		setLayout(new BorderLayout());
		add(table,BorderLayout.CENTER);
		addComponentListener(new ResizeHandler());
	}

	@Override
	public void setup(PlotContainer plotContainer) 
	{
		if(viewModel==null)
			create(plotContainer);



		plotContainer.addViewSetting(displayDataSetting, this);
		plotContainer.addViewSetting(probeSortSetting, this);
		plotContainer.addViewSetting(displaySetting, this);		
		plotContainer.setPreferredTitle("Chromogram", this);


	}

	private void create(PlotContainer plotContainer)
	{
		viewModel=plotContainer.getViewModel();

		displayDataSetting= new NameSourceSetting(viewModel.getDataSet());
		probeSortSetting=new ProbeSortSetting(viewModel.getDataSet());
		displaySetting=new ChromogramDisplaySetting();

		List<Probe> probes=new ArrayList<Probe>(viewModel.getProbes());
		Collections.sort(probes, ProbeComparatorFactory.createProbeComparator(ProbeComparisonMode.VAR, null, null));

		model=new BlockViewTableModel(probes,displayDataSetting, probeSortSetting,viewModel);
		model.setColumns(displaySetting.getColumnsSetting().getIntValue());
		table.setModel(model);

		renderer=new ChromogramRenderer(displaySetting.getRenderText().getBooleanValue());
		table.setDefaultRenderer(String.class, renderer );
		table.setTableHeader(null);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0,5));
		table.addMouseListener(new TableMouseAdapter());

		//		table.setRowSelectionAllowed(true);
		//		table.setColumnSelectionAllowed(true);
		sharedListSelectionListener=new SharedListSelectionHandler();
		table.getSelectionModel().addListSelectionListener(sharedListSelectionListener);
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		displaySetting.addChangeListener(this);		
		viewModel.addViewModelListener(sharedListSelectionListener);
		viewModel.addViewModelListener(this);
	}

	public void updatePlot() 
	{

		repaint();		
	}

	private class ResizeHandler extends ComponentAdapter
	{
		@Override
		public void componentResized(ComponentEvent e) 
		{
			int w=getVisibleRect().width;
			w-=100;
			int cw=w/table.getColumnCount();
			for(int i=1; i!= table.getColumnCount(); ++i)
			{
				TableColumn col = table.getColumnModel().getColumn(i);
				col.setMinWidth(cw);
				col.setMaxWidth(cw);
				col.setPreferredWidth(cw);
			}
		}
	}

	private class TableMouseAdapter extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e) 
		{
			if(e.getButton()==MouseEvent.BUTTON2)
			{
				// lets try to extract the probe on the requsted position
				Probe p=model.getProbe(table.rowAtPoint(e.getPoint()), table.columnAtPoint(e.getPoint()));
				if(p==null) return;
				Graph g=new Graph();
				MultiProbeNode node=new MultiProbeNode(g,p );
				g.addNode(node);
				MultiProbeComponent comp=new MultiProbeComponent(node);

				SuperColorProvider coloring =new SuperColorProvider(viewModel);

				coloring=new SuperColorProvider(viewModel);
				coloring.setExperiment(0);

				coloring.setMode(ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE);
				coloring.setExperiment(0);

				ComponentRenderer renderer=new ProfilePlotRenderer(coloring);
				comp.setSize(renderer.getSuggestedSize(null,null));
				ComponentZoomFrame frame=new ComponentZoomFrame(comp,renderer);
				frame.setLocation(e.getXOnScreen(), e.getYOnScreen());
				frame.setVisible(true);
			}
			if(e.getButton()==MouseEvent.BUTTON3)
			{
				List<Probe> probes=new ArrayList<Probe>();
				Probe p=model.getProbe(table.rowAtPoint(e.getPoint()), table.columnAtPoint(e.getPoint()));
				probes.add(p);
				ProbeMenu menu=new ProbeMenu(probes,p.getMasterTable());
				menu.getPopupMenu().show(ChromgramPlot.this, e.getX(), e.getY());
			}
		}
	}

	class SharedListSelectionHandler implements ListSelectionListener, ViewModelListener
	{
		public void valueChanged(ListSelectionEvent e) 
		{ 
			// identify the selected probes
			Set<Probe> probes=new HashSet<Probe>();
			for(SelectionIndex i:((MultiSelectionModel)table.getSelectionModel()).getSelectedCells())
			{
				probes.add(model.getProbe(i.row,i.column));
			}
			// notify viewModel 
			viewModel.setProbeSelection(probes);        	
		}

		@Override
		public void viewModelChanged(ViewModelEvent vme) 
		{
			if(vme.getChange()==ViewModelEvent.PROBE_SELECTION_CHANGED)
			{
				((MultiSelectionModel)table.getSelectionModel()).clearSelectionSilent();
				for(Probe p:viewModel.getSelectedProbes())
				{
					// get index of probe and  select this index
					SelectionIndex idx=model.getProbeIndex(p);
					((MultiSelectionModel)table.getSelectionModel()).addSelectionIntervalSilent(idx.row, idx.column);
				} 
				table.repaint();
			}
		}
	}

	public void stateChanged(SettingChangeEvent e) 
	{
		if(e.getSource()==displaySetting.getRenderText())
		{
			renderer.setRenderText(displaySetting.getRenderText().getBooleanValue());
		}
		if(e.getSource()==displaySetting.getColumnsSetting())
		{
			model.setColumns(displaySetting.getColumnsSetting().getIntValue());
		}    	
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) 
	{
		if(vme.getChange()==ViewModelEvent.PROBELIST_SELECTION_CHANGED || vme.getChange()==ViewModelEvent.TOTAL_PROBES_CHANGED)
		{
			List<Probe> probes=new ArrayList<Probe>(viewModel.getProbes());
			model.setProbes(probes);
			updatePlot();
			table.tableChanged(new TableModelEvent(model));
		}

	}
	
	@Override
	public void removeNotify() 
	{
		super.removeNotify();
		viewModel.removeViewModelListener(this);
		viewModel.removeViewModelListener(sharedListSelectionListener);
	}

}
