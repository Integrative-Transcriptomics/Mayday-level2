package mayday.wapiti.gui.layeredpane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ReorderableHorizontalPanel extends JPanel {

	protected HorizontalLayeredPane container;
	protected SelectionModel selectionModel;
	protected boolean moving = false;
	
	public boolean isMoving() {
		return moving;
	}
	
	public void setMoving(boolean moving) {
		this.moving = moving;
	}
	
	public boolean isSelected() {
		return (selectionModel!=null && selectionModel.isSelected(this));
	}
	
	public void setSelected(boolean sel) {
		if (selectionModel!=null)
			selectionModel.setSelected(this, sel);
		repaint();
	}
	

	public void setSingleSelected(boolean sel) {
		if (selectionModel!=null) {
			selectionModel.clearSelection();
			setSelected(sel);
		}
	}	
	
	public void toggleSelected() {
		setSelected(!isSelected());
	}
	
	public void addNotify() {
		super.addNotify();
		Component comp = this;

		while (comp != null && !(comp instanceof HorizontalLayeredPane))
			comp = comp.getParent();

		if (comp != null) {
			container = (HorizontalLayeredPane)comp;
			selectionModel = container.getSelectionModel();
		}
	}
	
	public void paint(Graphics g) {
		if (isSelected()) {
			setBackground(new Color(220,220,255));
			g.clearRect(0, 0, getWidth(), getHeight());
		} else {
			setBackground(Color.WHITE);
			g.clearRect(0, 0, getWidth(), getHeight());
		}
		super.paint(g);
		if (isMoving()) {
			g.setColor(Color.RED);
			g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		}
	}
	
	public void reordered(int newIndex) {
		// nothing here
	}
	
	public int index() {
		if (container!=null)
			return container.indexOf(this);
		else return -1;
	}
	
	@SuppressWarnings("deprecation")
	public void reshape(int x, int y, int w, int h) {
		super.reshape(x, y, w, h);
		invalidate();
		validate();
	}
	
	public ReorderableHorizontalPanel() {
	}
}
