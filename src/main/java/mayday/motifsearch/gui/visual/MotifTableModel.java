package mayday.motifsearch.gui.visual;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import mayday.motifsearch.gui.listeners.*;
import mayday.motifsearch.model.Motif;
import mayday.motifsearch.model.Sequences;
import mayday.motifsearch.tool.Rounder;

/**
 * a model for a motif table
 * 
 * @author Frederik Weber
 * 
 */

public class MotifTableModel
extends AbstractTableModel
implements MotifSelectionListener, ColorModelListener, ActivatedSequencesListener{

    private static final long serialVersionUID = 1L;
    private Vector<Entry> list;
    Sequences activatedSequences;
    //private ArrayList<Integer> bindingStatistics;
    private ArrayList<MotifChangeListener> listeners = new ArrayList<MotifChangeListener>();

    /**
     * an entry that contains information of the condition of motif in this table model
     * 
     */
    private class Entry {

	Motif motif;
	boolean isSelected; // if the motif is selected

	public Entry(Motif motif, boolean isSelected) {
	    this.motif = motif;
	    this.isSelected = true;
	}
    }

    public MotifTableModel(ArrayList<Motif> motifs) {
	this.setMotifs(motifs);
    }

    /**
     * Sets the motif List "in" the Table
     * 
     */

    public void setMotifs(ArrayList<Motif> motifs) {
	if (motifs != null) {
	    this.list = new Vector<Entry>();
	    for (Motif m : motifs) {
		this.list.add(new Entry(m, true));
	    }

	}
	this.fireTableDataChanged();
    }

    /**
     * Returns the number of Columns
     */
    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount() 
     */
    public int getColumnCount() {
	return 7;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int) 
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
	switch (columnIndex) {
	    case 0:
		return Boolean.class;
	    case 1:
		return Integer.class;
	    case 2:
		return MotifView.class;
	    case 3:
		return String.class;
	    case 4:
		return Double.class;
	    case 5:
		return SequenceLogoView.class;
	    case 6:
		return Integer.class;
	    default:
		throw new RuntimeException();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
	switch (column) {
	    case 0:
		return "selected";
	    case 1:
		return "#sites";
	    case 2:
		return "color";
	    case 3:
		return "name";
	    case 4:
		return "significance";
	    case 5:
		return "logo";
	    case 6:
		return "length";

	    default:
		throw new RuntimeException();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
	return columnIndex == 0;
    }

    /* Required for the table/cell renderer */
    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int) 
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
	Entry e = this.list.get(rowIndex);
	switch (columnIndex) {
	    case 0:
		return e.isSelected;
	    case 1:
		return this.activatedSequences.getNumberOfMotifOccurrence(e.motif);
	    case 2:
		return e.motif;
	    case 3:
		return e.motif.getName();
	    case 4:
		return Rounder.round(e.motif.getSignificanceValue(),3);
	    case 5:
		return e.motif;
	    case 6:
		return e.motif.getLength();
	    default:
		throw new RuntimeException("Unexpected");
	}
    }

    /*
     * (non-Javadoc) Sets the Value at the specified Position
     * 
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
     *      int, int)
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
	Entry e = this.list.get(rowIndex);
	switch (columnIndex) {
	    case 0: {
		e.isSelected = ((Boolean) value).booleanValue();
		fireTableCellUpdated(rowIndex, columnIndex);
		this.fireMotifsChanged(this
			.getSelectedMotifs());
		break;
	    }
	    case 1:
		break; 
	    case 2:
		break; 
	    case 3:
		break; 
	    case 4:
		break; 
	    case 5:
		break; 
	    case 6:
		break; 
	    default:
		throw new RuntimeException("Unexpected");
	}

    }

    /*
     * (non-Javadoc) Returns the number of Rows
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
	if (this.list == null)
	    return 0;
	return this.list.size();
    }

    /**
     * Returns the motif with the specified index
     * 
     * @param index
     *                the row index of a motif in the model
     * @return the motif at a specific index
     */
    public Motif getMotifFormIndex(int index) {
	return this.list.get(index).motif;
    }

    /**
     * Returns only the selected motifs
     * 
     */
    public ArrayList<Motif> getSelectedMotifs() {
	ArrayList<Motif> ms = new ArrayList<Motif>();
	for (Entry e : this.list) {
	    if (e.isSelected) {
		ms.add(e.motif);
	    }
	}
	return ms;
    }

    /**
     * if the motif of the row index is selected
     * 
     */
    public boolean isSelected(int index) {
	return list.get(index).isSelected;
    }

    /**
     * fires when all or none of the motifs should be selected 
     * 
     */
    public void fireAllSelected(boolean areAllSelected) {
	for (Entry e : this.list) {
	    e.isSelected = areAllSelected;
	}
	this.fireMotifsChanged(this.getSelectedMotifs());
	this.fireTableDataChanged();

    }

    public void addMotifChangeListener(MotifChangeListener l) {
	listeners.add(l);
    }

    public void removeMotifChangeListener(MotifChangeListener l) {
	listeners.remove(l);
    }

    protected void fireMotifsChanged(ArrayList<Motif> motifs) {
    	System.out.println("MTM : Motif changed event!");
	for (MotifChangeListener l : this.listeners) {
	    l.motifChanged(motifs);
	}
    }

    public void colorModelChanged(boolean isWhiteColorModel){
	this.fireTableDataChanged();
    }


    public void setActivatedSequences(Sequences activatedSequences) {
	this.activatedSequences = activatedSequences;
	this.fireTableDataChanged();
    }

}
