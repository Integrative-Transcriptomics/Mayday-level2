package mayday.wapiti.gui.layeredpane;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyController implements KeyListener {

	protected HorizontalLayeredPane container;
	protected SelectionModel selectionModel;
	
	public KeyController(HorizontalLayeredPane container) {	
		this.container = container;
		selectionModel = container.getSelectionModel();
	}

	
	public void keyPressed(KeyEvent e) {
		int CONTROLMASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		switch(e.getKeyCode()) {
		case KeyEvent.VK_A:
			if ((e.getModifiers() & CONTROLMASK)==CONTROLMASK)
				for (Component c : container.getComponents())
					if (c instanceof ReorderableHorizontalPanel)
						selectionModel.setSelected((ReorderableHorizontalPanel)c, true);
			break;
		case KeyEvent.VK_UP:
			int firstSelected=container.getComponentCount();
			for (Component c : container.getComponents()) {
				if (c instanceof ReorderableHorizontalPanel && selectionModel.isSelected((ReorderableHorizontalPanel)c)) {
						firstSelected = Math.min(firstSelected, container.getPositioner().indexOf((ReorderableHorizontalPanel)c));
				}
			}
			if (!e.isShiftDown())
				selectionModel.clearSelection();
			if (container.getComponentCount()>0) {
				ReorderableHorizontalPanel newFirstPanel = container.getPositioner().panelAt(Math.max(firstSelected-1,0));
				selectionModel.setSelected(newFirstPanel, true);
				container.scrollRectToVisible(newFirstPanel.getBounds());
			}
			break;
		case KeyEvent.VK_DOWN:
			int lastSelected=0;
			for (Component c : container.getComponents()) {
				if (c instanceof ReorderableHorizontalPanel && selectionModel.isSelected((ReorderableHorizontalPanel)c)) {
					lastSelected = Math.max(lastSelected, container.getPositioner().indexOf((ReorderableHorizontalPanel)c));
				}
			}
			if (!e.isShiftDown())
				selectionModel.clearSelection();
			if (container.getComponentCount()>0) {
				ReorderableHorizontalPanel newLastPanel = container.getPositioner().panelAt(Math.min(lastSelected+1,container.getComponentCount()-1));
				selectionModel.setSelected(newLastPanel, true);
				container.scrollRectToVisible(newLastPanel.getBounds());
			}
			break;
		}		
	}


	public void keyReleased(KeyEvent e) {
	}


	public void keyTyped(KeyEvent e) {

	
	}
	



}
