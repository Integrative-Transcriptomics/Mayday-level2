package mayday.wapiti.gui.layeredpane;


import java.awt.Dimension;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JPanel;


public class PanelPositioner {

	protected LinkedList<ReorderableHorizontalPanel> idx2pnl = null;
	protected HashMap<ReorderableHorizontalPanel, Integer> pnl2idx = null;
		
	protected int totalHeight = 0;
	protected boolean allowHorizontalOversize = false;
	
	protected HorizontalLayeredPane container;	
	
	public PanelPositioner(HorizontalLayeredPane container){
		this(container, false);
	}
	
	public PanelPositioner(HorizontalLayeredPane container, boolean allowHorizontalOversize){
		idx2pnl = new LinkedList<ReorderableHorizontalPanel>();
		pnl2idx = new HashMap<ReorderableHorizontalPanel,Integer>();
		this.container = container;
		this.allowHorizontalOversize=allowHorizontalOversize;
	}
	
	/**
	 * change panel with index by actual panel.
	 * @param index
	 * @param panel
	 */
	public void changePanel(int index, ReorderableHorizontalPanel panel) {
		if (index > idx2pnl.size())
			System.err.println("POSITION " + index + " NOT contained");
		else {
			JPanel panel2delete = idx2pnl.get(index);
			if (panel2delete!=null) {
				container.remove(panel2delete);
				pnl2idx.remove(panel2delete);
			}
			idx2pnl.set(index, panel);
			pnl2idx.put(panel, index);
		}		
		reorderLocationOfPanels();
	}

	
	public void addPanel(ReorderableHorizontalPanel panel){
		idx2pnl.add(null);
		changePanel(idx2pnl.size()-1, panel);
//		panel.setSize(panel.getPreferredSize());
		container.add(panel);
	}


	public void removePanel(ReorderableHorizontalPanel panelToRemove){
		if (pnl2idx.containsKey(panelToRemove)) {
			int i = pnl2idx.get(panelToRemove);
			idx2pnl.remove(i);
			updateMapping();
			container.getSelectionModel().setSelected(panelToRemove, false);
			container.remove(panelToRemove);			
		}
		reorderLocationOfPanels();
	}
	

	protected void updateMapping() {
		pnl2idx.clear();
		int i=0;
		for (ReorderableHorizontalPanel p : idx2pnl)
			pnl2idx.put(p, i++);
	}
	
	public Integer indexOf( ReorderableHorizontalPanel p ) {
		return pnl2idx.get(p);
	}

	public ReorderableHorizontalPanel panelAt(int index) {
		return idx2pnl.get(index);
	}
	

	public void movePanel(ReorderableHorizontalPanel panel, int newPositionY ){
		int oldIndex = pnl2idx.get(panel); 
		int height = panel.getHeight();
		
		if(newPositionY <= 0)
			newPositionY = 0;
		
		int newIndex = indexFromCoordinate(newPositionY, height, oldIndex);
		
		movePanelToIndex(panel, newIndex);
	}
	
	public void movePanelToIndex(ReorderableHorizontalPanel panel, int newIndex) {
		int oldIndex = pnl2idx.get(panel); 

		if(newIndex >= idx2pnl.size()){
			newIndex = idx2pnl.size()-1;
		}

		if (oldIndex!=newIndex) {
			idx2pnl.remove(oldIndex);			
			if (oldIndex < newIndex)
				newIndex--;
			idx2pnl.add(newIndex, panel);
		}
		
		updateMapping();		
		reorderLocationOfPanels();
	}
	
	
	private boolean noreentry = false;

	public void reorderLocationOfPanels() {
		if (noreentry)
			return;
		noreentry = true;
		// get preferred widths
		int maxW = container.getWidth();
		if (allowHorizontalOversize)
			for(JPanel p : idx2pnl) {	
				maxW = Math.max(maxW, p.getPreferredSize().width);
			}
			
		
		int posY = 0;
		int newIndex=0;
		for(JPanel p : idx2pnl) {			
			p.setLocation(0, posY);
			Dimension d = p.getPreferredSize();
			d.width = maxW;
			p.setSize(d);
			if (p instanceof ReorderableHorizontalPanel) {
				((ReorderableHorizontalPanel)p).reordered(newIndex);
			}
			++newIndex;
			posY = posY + p.getHeight();
		}
		totalHeight = posY;
		noreentry = false;
		if (allowHorizontalOversize)
			container.setSize(maxW, container.getHeight());
	}
	
	public int getTotalHeight() {
		return totalHeight;
	}
	
	private int indexFromCoordinate(int newPositionY, int heightTP, int oldIndex) {
		int pos_y = 0;
		int newIndex = -1;
		
		int index = 0;
		for (JPanel panel : idx2pnl) {
			int height = panel.getHeight();
			int panel_y_bottom = pos_y + height;

			int halfHeight = (int) Math.round((double) height / 2.);
			int panel_y_half = panel_y_bottom - halfHeight;

			if (panel_y_half < newPositionY) {
				pos_y = pos_y + height;
				newIndex = index;
			} else {
				newIndex = index;
				break;
			}
			++index;
		}

		if(newIndex < 0){
			newIndex = oldIndex;
		}
		
		return newIndex;
	}
	
	public Iterable<ReorderableHorizontalPanel> panels() {
		return Collections.unmodifiableList(idx2pnl);
	}
	
	public class SelectionModel extends mayday.wapiti.gui.layeredpane.SelectionModel {
		public ReorderableHorizontalPanel elementAt(int index) {
			return idx2pnl.get(index);
		}
		public int indexOf(ReorderableHorizontalPanel elt) {
			return pnl2idx.get(elt);
		}
		
	}
	
}
