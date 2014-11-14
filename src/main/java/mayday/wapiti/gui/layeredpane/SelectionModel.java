package mayday.wapiti.gui.layeredpane;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;


public abstract class SelectionModel {

	protected HashSet<ReorderableHorizontalPanel> selected = new HashSet<ReorderableHorizontalPanel>();
	
	public void setSelected(ReorderableHorizontalPanel elt, boolean sel) {
		if (sel) 
			selected.add(elt);
		else 
			selected.remove(elt);
		elt.repaint();
	}
	
	public void clearSelection() {
		LinkedList<ReorderableHorizontalPanel> tmp = new LinkedList<ReorderableHorizontalPanel>(selected);			
		selected.clear();
		for (ReorderableHorizontalPanel p : tmp)
			p.repaint();
	}
	
	public boolean isSelected(ReorderableHorizontalPanel pnl) {
		return selected.contains(pnl);
	}
	
	public void growSelection(ReorderableHorizontalPanel pnl, boolean replace) {
		int i1 = indexOf(pnl);
		LinkedList<Integer> selIdx = new LinkedList<Integer>();
		for (ReorderableHorizontalPanel p : selected)
			selIdx.add(indexOf(p));
		int i2;
		if (selIdx.size()>0) {
			int i2min = Collections.min(selIdx);
			int i2max = Collections.max(selIdx);
			i2 = (Math.abs(i1-i2min) > Math.abs(i1-i2max)) ? i2max : i2min;
			if (replace)
			clearSelection();
			if (i1>i2) {
				int k = i1;
				i1 = i2;
				i2 = k;
			}
		} else {
			i2 = i1;
		}
		for (int i = i1; i<=i2; ++i) {
			setSelected(elementAt(i), true);
		}
	}
	
	public Collection<ReorderableHorizontalPanel> getSelection() {
		return new LinkedList<ReorderableHorizontalPanel>(selected);
	}
	
	public int size() {
		return selected.size();
	}
	
	public abstract int indexOf(ReorderableHorizontalPanel elt);
	public abstract ReorderableHorizontalPanel elementAt(int index);
	
}
