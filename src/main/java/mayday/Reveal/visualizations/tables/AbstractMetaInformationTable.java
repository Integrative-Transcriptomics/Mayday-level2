package mayday.Reveal.visualizations.tables;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import mayday.Reveal.data.DataStorage;

public abstract class AbstractMetaInformationTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataStorage storage;
	
	private MetaInfoTableModel model;
	
	public AbstractMetaInformationTable(DataStorage storage) {
		this.storage = storage;
		this.model = new MetaInfoTableModel();
		this.initializeData(this.model);
		this.setModel(model);
		this.setDefaultRenderer(Double.class, new DoubleRenderer());
		this.setAutoCreateRowSorter(true);
	}
	
	public abstract void initializeData(MetaInfoTableModel model);

	public void setColumnNames(String[] columnNames) {
		this.model.setColumnNames(columnNames);
	}
	
	public DataStorage getDataStorage() {
		return this.storage;
	}
	
	public abstract void update();
	
	public MetaInfoTableModel getModel() {
		return this.model;
	}
	
	public class MetaInfoTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String[] columnNames = new String[0];
		private Object[][] data = new Object[0][0];
		
		public void setColumnNames(String[] columnNames) {
			this.columnNames = columnNames;
		}
		
		public Object[] getDataInRow(int rowIndex) {
			return this.data[rowIndex];
		}
		
		public Object[] getDataInColumn(int colIndex) {
			Object[] colData = new Object[data.length];
			for(int i = 0; i < colData.length; i++) {
				colData[i] = data[i][colIndex];
			}
			return colData;
		}
		
		public void setData(Object[][] data) {
			this.data = data;
		}
		
		public String getColumnName(int columnIndex) {
			if(columnNames == null) {
				return "Column " + Integer.toString(columnIndex);
			} else {
				return this.columnNames[columnIndex];
			}
		}
		
		@Override
		public int getRowCount() {
			return data.length;
		}

		@Override
		public int getColumnCount() {
			if(columnNames != null)
				return columnNames.length;
			else
				return data[0].length;
		}

		@Override
		public Object getValueAt(int rowIndex, int colIndex) {
			return data[rowIndex][colIndex];
		}
		
		public Class<? extends Object> getColumnClass(int colIndex) {
			return getValueAt(0, colIndex).getClass();
		}

		public int getFirstColumnPosition(Object o) {
			for(int i = 0; i < data.length; i++) {
				Object value = getValueAt(i, 0);
				if(value.equals(o)) {
					return convertRowIndexToView(i);
				}
			}
			return -1;
		}
	}
	
	public class DoubleRenderer extends DefaultTableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6683231534801323612L;
		
		public void setValue(Object doubleValue) {
			Double value = (Double)doubleValue;
			String valueText = Double.toString(value.doubleValue());
			
			this.setText(valueText);
			this.setVerticalAlignment(JLabel.CENTER);
			this.setHorizontalAlignment(JLabel.LEFT);
		}
	}
}
