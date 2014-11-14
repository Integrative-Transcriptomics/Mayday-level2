package mayday.tiala.multi.gui.probelistlist;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;

import mayday.core.ProbeList;
import mayday.core.gui.dragndrop.MaydayTransferHandler;
import mayday.core.gui.probelist.ProbeListListbox;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.probelists.MirrorProbeList;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
/**
 * 
 * @author battke
 *
 */
@SuppressWarnings("serial")
public class DragNDropProbeListListBox extends ProbeListListbox {

	protected AlignmentStore store;
	
	public DragNDropProbeListListBox(AlignmentStore Store) {
		super();
		store = Store;
		setTransferHandler(new TransferHandler());
		
		store.getVisualizerCombined().getViewModel().addViewModelListener(new ViewModelListener(){
			public void viewModelChanged(ViewModelEvent vme) {
				if (vme.getChange()==ViewModelEvent.PROBELIST_SELECTION_CHANGED) {
					fill();
				}
			}
		});
		
		setCellRenderer(new RemoveableProbeListCellRenderer());
		
		fill();
		
		setMinimumSize(new Dimension(50,50));
		setPreferredSize(new Dimension(50,50));
		
		addMouseListener( new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				if ( event.getButton() == MouseEvent.BUTTON1 ) {
					if ( event.getClickCount() == 1 ) {
						if ( getSelectedValue() != null ) {
							// map coordinates
							int x = event.getX();
							int y = event.getY();
							Rectangle r = getCellBounds(getSelectedIndex(),getSelectedIndex());
							x-= r.x;
							y-= r.y;
							// account for the inset        
							Rectangle image = ((RemoveableProbeListCellRenderer)getCellRenderer()).closer.getBounds();
							if (image.contains(x,y)) {
								store.removeProbeListFromViewModels((ProbeList)getSelectedValue());
							}
						}
					}
				}
			}
		});
	}
	
	protected void fill() {
		DefaultListModel dlm = (DefaultListModel)getModel();
		dlm.clear();
		for (ProbeList pl : store.getProbeListsInViewModels(false))
			if (!(pl instanceof MirrorProbeList))
				dlm.addElement(pl);
	}
	
	
	public class TransferHandler extends MaydayTransferHandler<ProbeList> {

		public TransferHandler() {
			super(ProbeList.class);
		}

		public int getSourceActions(JComponent c) {
			return TransferHandler.COPY;
		}

		@Override
		protected Object getContextObject() {
			return null;
		}

		@Override
		public ProbeList[] getDragObject(JComponent c) {
			return new ProbeList[0];
		}

		@Override
		protected void processDrop(Component c, ProbeList[] droppedObjects,
				TransferSupport info) {
			for (ProbeList pl : droppedObjects)
				store.addProbeListToViewModels(pl);	
		}
	}
}
