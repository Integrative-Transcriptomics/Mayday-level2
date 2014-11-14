package mayday.vis3.plots.chromogram.multijtable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MultiSelectionModel implements ListSelectionModel 
{
	private List<SelectionIndex> selectedCells=new ArrayList<SelectionIndex>();
	
	public static final int MULTIPLE_SELECTION=666;
	
	protected EventListenerList listenerList = new EventListenerList();
	
	private SelectionIndex anchor=null;
	
	public MultiSelectionModel() 
	{
		super();		
	}
	
	@Override
	public void clearSelection() 
	{
		if(isSelectionEmpty()) return;
		selectedCells.clear();
		fireValueChanged(-1, false);
	}
	

	public void clearSelectionSilent() 
	{
		if(isSelectionEmpty()) return;
		selectedCells.clear();
	}
	
	@Override
	public void setSelectionMode(int selectionMode) 
	{
		// do nothing. 
	}
	
    public void setSelectionInterval(int index0, int index1)
    {
    	clearSelection();
    	selectedCells.add(new SelectionIndex(index0, index1));
    	anchor=new SelectionIndex(index0, index1);
		fireValueChanged(index0+1, false);
    }

	@Override
	public void addListSelectionListener(ListSelectionListener l) 
	{		
		listenerList.add(ListSelectionListener.class, l);
	}

	@Override
	public void addSelectionInterval(int index0, int index1) 
	{
		selectedCells.add(new SelectionIndex(index0, index1));
		anchor=new SelectionIndex(index0, index1);
		fireValueChanged(index0+1, false);
	}
	
	
	public void addSelectionIntervalSilent(int index0, int index1) 
	{
		selectedCells.add(new SelectionIndex(index0, index1));
	}

	@Override
	public int getAnchorSelectionIndex() {
		return 0;
	}

	@Override
	public int getLeadSelectionIndex() {
		return 0;
	}

	@Override
	public int getMaxSelectionIndex() {
		return 0;
	}

	@Override
	public int getMinSelectionIndex() {
		return 0;
	}

	@Override
	public int getSelectionMode() {
		return 0;
	}

	@Override
	public boolean getValueIsAdjusting() {
		return false;
	}

	@Override
	public void insertIndexInterval(int index, int length, boolean before) {
		
	}

	@Override
	public boolean isSelectedIndex(int index) 
	{
		return false;
	}
	
	public boolean isSelectedIndex(int row, int col) 
	{
		return selectedCells.contains(new SelectionIndex(row, col));
	}

	@Override
	public boolean isSelectionEmpty() 
	{
		return selectedCells.isEmpty();
	}
	
	@Override
	public void removeIndexInterval(int index0, int index1) {

	}

	@Override
	public void removeListSelectionListener(ListSelectionListener x) 
	{
		listenerList.remove(ListSelectionListener.class, x);		
	}

	
	@Override
	public void removeSelectionInterval(int index0, int index1) 
	{
		selectedCells.remove(new SelectionIndex(index0, index1));
		anchor=new SelectionIndex(index0, index1);
		fireValueChanged(index0+1, false);
	}

	@Override
	public void setAnchorSelectionIndex(int index) {
	}

	@Override
	public void setLeadSelectionIndex(int index) {
	}

	@Override
	public void setValueIsAdjusting(boolean valueIsAdjusting) {
	}
    
    protected void fireValueChanged(int maxRow, boolean isAdjusting)
    {
	Object[] listeners = listenerList.getListenerList();
	ListSelectionEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == ListSelectionListener.class) {
		if (e == null) {
		    e = new ListSelectionEvent(this, 0, maxRow, isAdjusting);
		}
		((ListSelectionListener)listeners[i+1]).valueChanged(e);
	    }
	}
    }
    
    public void expandSelectionTo(int row, int column)
    {
    	if(anchor==null)
    		addSelectionInterval(row, column);
    	int ar=anchor.row;
    	int ac=anchor.column;
    	
    	int sr=Math.min(row, ar);
    	int sc=Math.min(column, ac);
    	
    	int er=Math.max(row, ar);
    	int ec=Math.max(column, ac);
    	
    	for(int i=sr; i<=er; ++i)
    	{
    		for(int j=sc; j<=ec; ++j)
    		{
    			addSelectionInterval(i, j);
    		}
    	}
    }

	public List<SelectionIndex> getSelectedCells() {
		return selectedCells;
	}

	public void setSelectedCells(List<SelectionIndex> selectedCells) {
		this.selectedCells = selectedCells;
	}
	
    

}
