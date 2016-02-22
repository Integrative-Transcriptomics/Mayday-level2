package mayday.vis3.plots.treeviz3.classselection;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import mayday.core.ClassSelectionModel;

/**
 * @author Eugen Netz
 */
@SuppressWarnings("serial")
public class ClassPanel extends JPanel{

	private ClassSelectionModel partition;
	private JTable classTable;
	private TableModel tableModel;
	
	public ClassPanel(ClassSelectionModel model) {
		super(new BorderLayout());
		this.partition = model;
		setBorder(BorderFactory.createTitledBorder("Manual Class Assignment"));

		tableModel=new TableModel();        
		classTable=new JTable(tableModel);
		classTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		classTable.addMouseListener(new TableMouseListener());
		classTable.setModel(tableModel);
		classTable.setAutoCreateRowSorter(true);
		classTable.getColumnModel().getColumn(2).setCellRenderer(new TableCellRenderer());
		JScrollPane scrollPane = new JScrollPane(classTable);         
		add(scrollPane, BorderLayout.CENTER); 
	}

	private class TableModel extends AbstractTableModel {
		
		private String[] columnNames = {"Number","Name","Class"};
		
		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public int getRowCount() {
			return partition.getNumObjects();
		}

		@Override
		public Object getValueAt(int row, int col) {
			if (col == 0) {
				return row+1;
			}
			if (col == 1) {
				return partition.getObjectName(row);
			}			
			if (col == 2) {
				return partition.getObjectClass(row);
			}
			return null;
		}
		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		public String getColumnName(int column) {
			return columnNames[column];
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int row, int col) 	{
			return col == 2 ? true : false;
		}

	}
	
	private class TableCellRenderer extends DefaultTableCellRenderer {
		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent
		(JTable table, Object value, boolean selected, boolean focused, int row, int column) {

			setEnabled(table == null || table.isEnabled());
			
			String val = (String) value;
			ArrayList<String> valueList = new ArrayList<String>();
			valueList.addAll(partition.getClassNames());
			int c = valueList.indexOf(val);
			if(c >= 0) {
				setBackground(ClassSelectionModel.getColor(c, valueList.size()));
			}
			
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);
			return this;
		}
	}
	
	
	private class TableMouseListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			operate(e);
		}

		public void mouseReleased(MouseEvent e) {
			operate(e);
		}

		public void operate(MouseEvent e) {			
			if (e.isPopupTrigger() && classTable.getSelectedRows().length>0) {

				JPopupMenu popup=new JPopupMenu();

				String newClassName = "Class ";
				int newClassID = 0;

				for(String s:partition.getClassesLabels()) {
					JMenuItem m=new JMenuItem(s);
					m.addActionListener(new SetClassActionListener(s));
					popup.add(m);	
					while (s.equals(newClassName+newClassID)) {
						++newClassID;
					}
				}
				
				JMenuItem m = new JMenuItem(">> NO CLASS <<");
				m.addActionListener(new SetClassActionListener(partition.getNoClassLabel()));
				popup.add(m);
				
				popup.addSeparator();
				m=new JMenuItem("New Class...");
				m.addActionListener(new CreateWithNameActionListener(newClassName+newClassID));
				popup.add(m);
				popup.addSeparator();

				String oldName = partition.getObjectClass(classTable.getSelectedRow());
				m=new JMenuItem("Rename class \""+oldName+"\"...");
				m.addActionListener(new RenameActionListener(oldName));
				popup.add(m);

				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}		
	}
	
	private class SetClassActionListener implements ActionListener {
		private String name;
		
		public SetClassActionListener(String s) {
			name = s;
		}

		public void actionPerformed(ActionEvent e) {
			int[] indices = classTable.getSelectedRows();
			for(int i=0; i != indices.length; ++i) {
				//partition.setClass(indices[i], name);
				int mtIndex = (Integer) classTable.getValueAt(indices[i], 0);
				partition.setClass(mtIndex-1, name);
			}
			tableModel.fireTableRowsUpdated(0, classTable.getRowCount()-1);
		}		
	}
	
	private class RenameActionListener implements ActionListener {
		private String name;
		public RenameActionListener(String oldName){
			name=oldName;
		}

		public void actionPerformed(ActionEvent e) {
			String newName = JOptionPane.showInputDialog(ClassPanel.this, "Enter a new name for class \""+name+"\"", name);

			if (newName!=null && newName.trim().length()>0 && !newName.trim().equals(name)) {
				newName = newName.trim();
				for(int i=0; i!= partition.getNumObjects(); ++i) {
					if (partition.getObjectClass(i).equals(name)) {
						partition.setClass(i, newName);
					}
				}	
				tableModel.fireTableRowsUpdated(0, classTable.getRowCount()-1);
			}
		}		
	}
	
	private class CreateWithNameActionListener implements ActionListener {
		protected String name;
		public CreateWithNameActionListener(String suggest) {
			name  = suggest;
		}

		public void actionPerformed(ActionEvent e) {
			String newName = JOptionPane.showInputDialog(ClassPanel.this, "Enter a name for the new class", name);

			if (newName!=null && newName.trim().length()>0 && !partition.getClassesLabels().contains(newName.trim())) {
				newName = newName.trim();
				int[] indices = classTable.getSelectedRows();
				for(int i=0; i!= indices.length; ++i) {
//					partition.setClass(indices[i], newName);
					int mtIndex = (Integer) classTable.getValueAt(indices[i], 0);
					partition.setClass(mtIndex-1, newName);
				}
				tableModel.fireTableRowsUpdated(0, classTable.getRowCount()-1);
			}
		}		
	}
}
