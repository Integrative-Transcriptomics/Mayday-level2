package mayday.tiala.pairwise.gui.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractSpinnerModel;

import mayday.tiala.pairwise.data.AlignmentStore;


public class AlignmentSpinnerModel extends AbstractSpinnerModel {

	private List<Double> list;
	private int index;
	protected Double value;

	public AlignmentSpinnerModel(AlignmentStore alignmentStore)  {		
		list=(new ArrayList<Double>(alignmentStore.getPossibleAlignments().getAllShifts()));
		setValue(0.0);
	}


	// override to allow elements that are not in the list
	public void setValue(Object elt) {
		Double d; 
		if (elt instanceof String)
			d = Double.parseDouble((String)elt);
		else
			d = (Double)elt;
		 
		int newindex = Collections.binarySearch(list, d);
		boolean changed = false;
		
		if (newindex >= 0) { // element from the list
			changed = (value!=null) || (index!=newindex); 
			value = null;
			index = newindex;
		} else { // element outside the list
			changed = (value != d);
			value = d;
			newindex = -newindex;
			// find closest match
			newindex = Math.min(list.size()-1, Math.max(0,index));
			index = newindex;
		}
		
		if (changed)
			fireStateChanged();
	}

	public Double getValue() {
		if (value==null)
			return list.get(index);
		else
			return value;
	}

	public Object getNextValue() {
		return (index >= (list.size() - 1)) ? null : list.get(index + 1);
	}


	public Object getPreviousValue() {
		return (index <= 0) ? null : list.get(index - 1);
	}



}
