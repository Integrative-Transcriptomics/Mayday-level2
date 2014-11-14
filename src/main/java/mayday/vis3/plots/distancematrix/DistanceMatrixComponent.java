package mayday.vis3.plots.distancematrix;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import mayday.core.Probe;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.math.distance.measures.EuclideanDistance;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.ColorProvider;
import mayday.vis3.SortedProbeList;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.model.Visualizer;
/**
 * 
 * @author Jennifer Lange
 *
 */
@SuppressWarnings("serial")
public class DistanceMatrixComponent extends JTable implements	ListSelectionListener, ViewModelListener {

	protected Visualizer visualizer;
	protected IntSetting percentileStep;

	protected boolean isSilent; // indicates whether listeners are notified or not
	
	protected final static int PROBECOL = 1;

	protected boolean displayNames = true;
	
	protected SortedProbeList probes;
	protected ColorProvider coloring;

	DistanceMeasureSetting dms;
	DistanceMeasurePlugin dmp;

	public DistanceMatrixComponent(Visualizer _visualizer)  {
		this.visualizer = _visualizer;
		this.isSilent = false;
		
		visualizer.getViewModel().addViewModelListener(this);
		probes = new SortedProbeList( visualizer.getViewModel(), visualizer.getViewModel().getProbes());
		probes.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateTable();
			}
		});
		
		dms = new DistanceMeasureSetting("Distance measure", null, new EuclideanDistance());
		dms.addChangeListener(new SettingChangeListener() {			
			public void stateChanged(SettingChangeEvent e) {
				dmp = dms.getInstance();
				updateTable();
			}
		});
		dmp = dms.getInstance();
		
		coloring = new ColorProvider(visualizer.getViewModel());
		coloring.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				DistanceMatrixComponent.this.repaint();
			}			
		});

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton()==MouseEvent.BUTTON3) {
					ProbeMenu pm = new ProbeMenu(getViewModel().getSelectedProbes(), getViewModel().getDataSet().getMasterTable());
					pm.getPopupMenu().show(DistanceMatrixComponent.this, e.getX(), e.getY());
				}
			}
		});
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		isSilent = true;
		AbstractTask at = new AbstractTask("Preparing table...") {
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				DefaultTableModel l_tableData = new DistanceTableModel(); // first index: row, second index: column
				setModel(l_tableData);
				setAutoCreateRowSorter(true);
			}
			
		};
		at.start();
		at.waitFor();
		
		isSilent = false;


		TableColumn l_identifierColumn = getColumnModel().getColumn(PROBECOL);
		l_identifierColumn.setCellRenderer(new IdentifierTableCellRenderer());
		
		setSelectedProbes(visualizer.getViewModel().getSelectedProbes());
	}
	
	public void updateTable() {
		isSilent=true;
		tableChanged(new TableModelEvent(getModel()));
		setSelectedProbes(visualizer.getViewModel().getSelectedProbes());
		isSilent=false;
	}

	public boolean goToProbe(String probeIdentifier) {  
		DefaultTableModel model = (DefaultTableModel)getModel();
		int row;
		for (row = 0; row!=model.getRowCount(); ++row) {
			Object opb = model.getValueAt(row, PROBECOL);
			if (opb instanceof Probe 
					&& (((Probe)opb).getName().equals(probeIdentifier)) 
						|| ((Probe)opb).getDisplayName().equals(probeIdentifier))
				break;
		}
		
		if (row < model.getRowCount()) {			
//			if (row+5<model.getRowCount())
//				row+=5;
			scrollRectToVisible(this.getCellRect(row, 0, true));
			return true;
		}
		
		return false;
	}


	private class DistanceTableModel extends DefaultTableModel {

		@Override
		public int getRowCount() {
			return probes.size();
		}

		@Override
		public int getColumnCount() {
			return probes.size()+2;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch(columnIndex) {
				case 0 : return "Row#";
				case 1 : return "Identifier";
			}
			return probes.get(columnIndex-2).getDisplayName();
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch(columnIndex) {
				case 0 : return Integer.class;
				case 1 : return Probe.class;
			}
			return Double.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
				case 0: return rowIndex;
				case 1: return probes.get(rowIndex);
			}
			return dmp.getDistance(probes.get(rowIndex), probes.get(columnIndex-2));
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			throw new RuntimeException("Distance matrix is read only");
		}

	}
	
	public void removeNotify() {
		super.removeNotify();
		visualizer.getViewModel().removeViewModelListener(this);
	}

	public Setting[] getSettings() {
		return new Setting[] { dms, coloring.getSetting(), probes.getSetting() };
	}

	public ViewModel getViewModel() {
		return visualizer.getViewModel();
	}

	protected class IdentifierTableCellRenderer extends	DefaultTableCellRenderer {
		public void setValue(Object value) {
			if (value instanceof Probe) {
				Probe l_probe = (Probe) value;
				setForeground(coloring.getColor(l_probe));
				if (displayNames)
					setText(l_probe.getDisplayName());
				else
					setText(l_probe.getName());
			} else {
				super.setValue(value);
			}
		}
	}

	public boolean isSilent() {
		return isSilent;
	}

	public void setSilent(boolean isSilent) {
		this.isSilent = isSilent;
	}

	public void setSelectedProbes(Set<Probe> selection) {
		setSilent(true);
		getSelectionModel().clearSelection();
		for (int row = 0; row != getModel().getRowCount(); ++row)
			if (selection.contains((Probe)getModel().getValueAt(row, 1)))
				getSelectionModel().addSelectionInterval(row, row);
		setSilent(false);
	}

	public void valueChanged(ListSelectionEvent event) {
		if (!isSilent()) {
			int[] l_selectedRows = getSelectedRows();

			LinkedList<Probe> newSelection = new LinkedList<Probe>();
			for (int i : l_selectedRows)
				newSelection.add((Probe)getModel().getValueAt(i, PROBECOL));						
			visualizer.getViewModel().removeViewModelListener(this);
			visualizer.getViewModel().setProbeSelection(newSelection);
			visualizer.getViewModel().addViewModelListener(this);
		}
		// perform default actions associated with the table
		super.valueChanged(event);
	}

	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange()==ViewModelEvent.PROBE_SELECTION_CHANGED)
			setSelectedProbes(visualizer.getViewModel().getSelectedProbes());
		// total probes is already checked by SortedProbeList and the associated listener
	}
}
