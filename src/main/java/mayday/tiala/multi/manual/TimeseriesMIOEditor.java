package mayday.tiala.multi.manual;

import java.awt.BorderLayout;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import mayday.core.MasterTable;
import mayday.tiala.multi.data.TimepointDataSet;

@SuppressWarnings("serial")
public class TimeseriesMIOEditor extends JPanel  {

	protected TimepointTableModel tm;
	protected JTable table;
	
	public TimeseriesMIOEditor(TimepointDataSet ds) {
		super(new BorderLayout());
		buildPanel(ds);
	}
	
	public void buildPanel(TimepointDataSet ds) {
		tm = new TimepointTableModel(ds); 
		table = new JTable(tm);
		JScrollPane scrollpane = new JScrollPane(table);
		add(scrollpane, BorderLayout.CENTER);
	}
	
	public boolean save(TimepointDataSet ds) {
		if (table.getCellEditor()!=null)
			table.getCellEditor().stopCellEditing();
		Set<Double> unique = new TreeSet<Double>(tm.getTimepoints());
		if (unique.size()!=tm.getTimepoints().size()) {
			JOptionPane.showMessageDialog(null, "Timepoints must be unique for "+ds.getDataSet().getName(), "Timepoint conflict", JOptionPane.ERROR_MESSAGE, null);
			return false;
		};
		double d = tm.getTimepoints().get(0);
		for (int i=1; i!=tm.getTimepoints().size(); ++i) {
			double d2 = tm.getTimepoints().get(i);
			if (d2<d) {
				JOptionPane.showMessageDialog(null, "Timepoints must be ordered (monotonely growing) for "+ds.getDataSet().getName(), "Timepoint conflict", JOptionPane.ERROR_MESSAGE, null);
				return false;
			}
			d = d2;
		}
		
		for (int i=0; i!=tm.getTimepoints().size(); ++i)
			ds.set(i, tm.getTimepoints().get(i));
		ds.createShifted(0); // store the changes
		return true; 
	}
	
	private class TimepointTableModel extends AbstractTableModel {

		protected Vector<Double> timepoints = new Vector<Double>();
		protected Vector<String> experimentNames = new Vector<String>();
		
		public TimepointTableModel(TimepointDataSet ds) {
			MasterTable mt = ds.getDataSet().getMasterTable();
			for (int i=0; i!=mt.getNumberOfExperiments(); ++i) {
				experimentNames.add(mt.getExperimentName(i));
				timepoints.add(ds.get(i));
			}
		}
		
		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return experimentNames.size();			
		}
		
		public boolean isCellEditable(int arg0, int arg1) {
			return (arg1==1);
		}
		
		public Object getValueAt(int row, int col) {
			switch(col) {
			case 0:
				return experimentNames.get(row);
			case 1:
				return timepoints.get(row);
			}
			return null;
		}
		
		public void setValueAt(Object aValue, int row, int col) {
			if (col==1) {				
				Double newValue = Double.parseDouble(aValue.toString());
				timepoints.set(row, newValue);
			}
		}

		public String getColumnName(int column) {
			switch(column) {
			case 0: return "Experiment";
			case 1: return "Time point";
			}
			return null;
		}
		
		public Vector<Double> getTimepoints() {
			return timepoints;
		}	
	}
}
