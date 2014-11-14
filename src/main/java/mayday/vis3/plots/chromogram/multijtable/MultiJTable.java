package mayday.vis3.plots.chromogram.multijtable;

import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class MultiJTable extends JTable {

	public MultiJTable() {
		super();
		init();
	}

	public MultiJTable(int numRows, int numColumns) {
		super(numRows, numColumns);
		init();
	}

	public MultiJTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
		init();
	}

	public MultiJTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
		init();
	}

	public MultiJTable(TableModel dm) {
		super(dm);
		init();
	}


	private void init()
	{
		setSelectionModel(new MultiSelectionModel());
		getColumnModel().setSelectionModel(new MultiSelectionModel());

		setSelectionMode(MultiSelectionModel.MULTIPLE_SELECTION);
	}

	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle,boolean extend) 
	{
		MultiSelectionModel msm=(MultiSelectionModel)getSelectionModel();
		if(!toggle && !extend)
		{
			msm.setSelectionInterval(rowIndex,columnIndex);
		}
		if(!toggle && extend)
		{
			msm.expandSelectionTo(rowIndex, columnIndex);
		}
		if(toggle && !extend)
		{			
			msm.addSelectionInterval(rowIndex,columnIndex);
			
		}
		if(toggle && extend)
		{
			if(isCellSelected(rowIndex, columnIndex))
			{				
				msm.removeIndexInterval(rowIndex, columnIndex);
			}else
			{				
				msm.addSelectionInterval(rowIndex, columnIndex);
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) 
	{
		int dirtyRow=e.getLastIndex();
		if(dirtyRow==-1) 
			dirtyRow=getRowCount();
		Rectangle firstColumnRect = getCellRect(0, 0, false);
		Rectangle lastColumnRect = getCellRect(dirtyRow, getColumnCount(), false);
		Rectangle dirtyRegion = firstColumnRect.union(lastColumnRect);
		repaint(dirtyRegion);
	}

	@Override
	public boolean isCellSelected(int row, int column) 
	{
		return ((MultiSelectionModel)getSelectionModel()).isSelectedIndex(row, column);
	}
	
	

}
