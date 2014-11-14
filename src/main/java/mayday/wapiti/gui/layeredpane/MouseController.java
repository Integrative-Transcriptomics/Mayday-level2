package mayday.wapiti.gui.layeredpane;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class MouseController implements MouseListener, MouseMotionListener {

	protected HorizontalLayeredPane container;
	protected SelectionModel selectionModel;
	
	protected JPanel movingPanel = null;
	protected boolean pressedflag = false;
	private Point p1 = new Point();
	private Point p2 = new Point();
	private Point prevLocation = new Point();


	public MouseController(HorizontalLayeredPane container) {	
		this.container = container;
		selectionModel = container.getSelectionModel();
	}

	
	public void mouseClicked(MouseEvent e) {
		
		if (e.getSource()==container) {
			if (e.getButton() == MouseEvent.BUTTON1)
				selectionModel.clearSelection();
		}
		
		if (e.getSource() instanceof ReorderableHorizontalPanel) {
			container.getParent().getParent().requestFocus();
			ReorderableHorizontalPanel rhp = (ReorderableHorizontalPanel)e.getSource();
			boolean withCTRL = (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0;
			boolean withSHFT = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;

			if (e.getButton()==MouseEvent.BUTTON1) {
				if (withCTRL) {
					if (withSHFT) {
						selectionModel.growSelection(rhp, false);
					} else {
						rhp.toggleSelected();
					}
				} else {
					if (withSHFT) {
						selectionModel.growSelection(rhp, true);
					} else {
						rhp.setSingleSelected(true);
					}
				}
			}
		}
	}
	

	
	public void mouseEntered(MouseEvent e) {
		
	}

	
	public void mouseExited(MouseEvent e) {
	}

	
	public void mousePressed(MouseEvent e) {
		pressedflag = true;
		
		if(e.getSource() instanceof ReorderableHorizontalPanel && e.getButton()==MouseEvent.BUTTON1){
			ReorderableHorizontalPanel rhp = (ReorderableHorizontalPanel)e.getSource();			
			rhp.setMoving(true);
			container.moveToFront(rhp);
			p1.setLocation(e.getPoint());
			prevLocation = rhp.getLocation();
		} 
	}

	public void moveComponentByDragging(ReorderableHorizontalPanel rhp) {
		prevLocation = rhp.getLocation();
		p2 = container.getMousePosition();
		int newY = prevLocation.y;
		if(p2 != null){
			newY = p2.y - p1.y;
		}
		rhp.setLocation(0, newY);
	}
	
	
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() instanceof ReorderableHorizontalPanel) {
			ReorderableHorizontalPanel rhp = (ReorderableHorizontalPanel)e.getSource();
			rhp.setMoving(false);
			int newY = prevLocation.y;
			if (pressedflag) {
				p2 = container.getMousePosition();
				if (p2!=null) {
					newY = p2.y - p1.y;
				} 
			}
			container.getPositioner().movePanel(rhp, newY);
		}
		
		pressedflag = false;

	}



	public void mouseDragged(MouseEvent e) {
		
		if(e.getSource() instanceof ReorderableHorizontalPanel){
			ReorderableHorizontalPanel rhp = (ReorderableHorizontalPanel)e.getSource();
			if (rhp.isMoving())
				moveComponentByDragging(rhp);
		}		
	}


	public void mouseMoved(MouseEvent e) {
	}


}
