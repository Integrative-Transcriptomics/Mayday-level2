/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mayday.expressionmapping.view.information;

import mayday.expressionmapping.controller.MainFrame;
import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.container.PointList;

/**
 *
 * @author Stephan Gade
 */
@SuppressWarnings("serial")
public class AttractorTableWindow extends JFrame implements AttractorTable, WindowListener, Runnable {

	private MainFrame master = null;
	private int id = -1;
	private PointList<? extends DataPoint> points = null;
	private List<String> annotations = null;
	TIntArrayList mainAccessList = null;
	TIntArrayList subAccessList = null;
	JTable infoTable = null;
	int numCol = 1;
	int size = 0;

	public AttractorTableWindow(String title, int id, MainFrame master) {

		super(title);

		this.master = master;

		this.id = id;

		this.points = master.getPoints();

		if (master.isAnnotationsListSet()) {
			this.annotations = master.getAnnotations();
		}

		this.numCol += 2 * this.points.getDimension();


	}

	public void setMainAccessList(TIntArrayList mainAccessList) {

		/*just give a reference, for efficience
		 */
		this.mainAccessList = mainAccessList;

		this.size += this.mainAccessList.size();

		System.err.println(this.mainAccessList.toString());

	}

	public void setSubAccessList(TIntArrayList subAccessList) {

		this.subAccessList = subAccessList;

		this.size += this.subAccessList.size();

	}

	public boolean isMainListSet() {

		return this.mainAccessList != null;

	}

	public boolean isSubListSet() {

		return this.subAccessList != null;
	}

	public TIntArrayList getMainList() {

		return this.mainAccessList;

	}

	public TIntArrayList getSubList() {

		return this.subAccessList;

	}

	public PointList<? extends DataPoint> getPoints() {

		return this.points;

	}

	public List<String> getAnnotations() {

		return this.annotations;

	}

	public void run() {

		initComponents();

		if (this.mainAccessList == null) {
			throw new IllegalStateException("Can't excute run() before the main Accession List is not set!");
		}

		AttractorTableModel tableModel = new AttractorTableModel(this);
		this.infoTable.setModel(tableModel);

		TableRowSorter<AttractorTableModel> rowSorter = new TableRowSorter<AttractorTableModel>(tableModel);
		IntComparator intComp = new IntComparator();
		DoubleComparator doubleComp = new DoubleComparator();
		rowSorter.setComparator(0, intComp);
		for (int i = 1; i < this.numCol; ++i) {
			rowSorter.setComparator(i, doubleComp);
		}
		this.infoTable.setRowSorter(rowSorter);

		this.pack();

		this.setVisible(true);


	}

	private void initComponents() {

		this.infoTable = new JTable();
		//infoTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		this.addWindowListener(this);

		/* set up row headers
		 */
		JList rowHeader = new JList(new RowHeaderModel(this.size));
		rowHeader.setFixedCellWidth(50);
		rowHeader.setFixedCellHeight(this.infoTable.getRowHeight());
		rowHeader.setCellRenderer(new RowHeaderRenderer(this.infoTable));

		/* add table to ascroll pane
		 */
		JScrollPane scroll = new JScrollPane(infoTable);
		scroll.setRowHeaderView(rowHeader);

		this.add(scroll);


	}

	public void windowOpened(WindowEvent e) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowClosing(WindowEvent e) {
		e.getWindow().dispose();
	}

	public void windowClosed(WindowEvent e) {

		//		if (this.subAccessList == null) {
		//			this.master.removeMainAttracTable(this.id);
		//		} else {
		//			this.master.removeAttracTable(this.id);
		//		}
		this.master.removeMainAttracTable(this.id);
		this.master.removeAttracTable(this.id);
	}

	public void windowIconified(WindowEvent e) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowDeiconified(WindowEvent e) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowActivated(WindowEvent e) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	public void windowDeactivated(WindowEvent e) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}
}

class DoubleComparator implements Comparator<Double> {

	public int compare(Double s1, Double s2) {

		double d1 = s1.doubleValue();
		double d2 = s2.doubleValue();
		//		double d1 = Double.parseDouble(s1);
		//		double d2 = Double.parseDouble(s2);

		return (d1 < d2) ? -1 : ((d1 > d2) ? 1 : 0);


	}
}

class IntComparator implements Comparator<Integer> {

	public int compare(Integer s1, Integer s2) {

		int i1 = s1.intValue();
		int i2 = s2.intValue();
		//		int i1= Integer.parseInt(s1);
		//		int i2= Integer.parseInt(s2);

		return (i1 < i2) ? -1 : ((i1 > i2) ? 1 : 0);


	}
}

@SuppressWarnings("serial")
class RowHeaderModel extends AbstractListModel {

	private int size = -1;

	public RowHeaderModel(int size) {

		System.err.println("MODEL SIZE  "+size);
		this.size = size;

	}

	public int getSize() {
		return size;
	}

	public Object getElementAt(int index) {
		return ""+(index+1);
	}
}

@SuppressWarnings("serial")
class RowHeaderRenderer extends JLabel implements ListCellRenderer {

	RowHeaderRenderer(JTable table) {
		JTableHeader header = table.getTableHeader();
		setOpaque(true);
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		setHorizontalAlignment(CENTER);
		setForeground(Color.BLACK);
		setBackground(Color.lightGray);
		setFont(header.getFont());
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		setText((value == null) ? "" : value.toString());
		return this;
	}
}

