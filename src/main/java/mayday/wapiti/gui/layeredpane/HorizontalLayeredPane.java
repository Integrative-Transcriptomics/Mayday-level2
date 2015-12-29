package mayday.wapiti.gui.layeredpane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JLayeredPane;
import javax.swing.Scrollable;


@SuppressWarnings("serial")
public class HorizontalLayeredPane extends JLayeredPane implements Scrollable {

	
	protected MouseController mc;
	protected KeyController kc;
	protected PanelPositioner positioner;
	protected SelectionModel selectionModel;	
	
	public HorizontalLayeredPane(  ) {
		
		setName("JLayeredPane");
		setVisible(true);
		
		positioner = new PanelPositioner(this, true);
		selectionModel = positioner.new SelectionModel();
				
		mc = new MouseController(this);
		addMouseListener(mc);
		addMouseMotionListener(mc);
		
		kc = new KeyController(this);
		
		setBackground(Color.white);
		setOpaque(true);
	}

	public PanelPositioner getPositioner() {
		return positioner;
	}
	
	public SelectionModel getSelectionModel() {
		return selectionModel;
	}
	
	public void addPanel( ReorderableHorizontalPanel rhp ) {
		positioner.addPanel(rhp);
		rhp.addMouseListener(mc);
		rhp.addMouseMotionListener(mc);
		invalidate();
		revalidate();
	}
	
	public void removePanel( ReorderableHorizontalPanel rhp ) {
		positioner.removePanel( rhp );
		rhp.removeMouseListener(mc);
		rhp.removeMouseMotionListener(mc);
		invalidate();
		revalidate();
		validate();
	}
	
	public int indexOf( ReorderableHorizontalPanel p ) {
		return positioner.indexOf(p);
	}

	
	//#############################################
	//
	// Scrollable implementations
	//
	//#############################################
	
	public Dimension getPreferredScrollableViewportSize() {
		final Dimension dim = getPreferredSize();
		return dim;
	}

	public Dimension getPreferredSize() {
		return new Dimension(getWidth(), positioner.getTotalHeight()+50);
	}
	
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 10;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 100;
	}

	public void validate() {
		Component c = getParent();
		if (c!=null) {
			setSize(c.getWidth(), getHeight());			
		}
		positioner.reorderLocationOfPanels();
		super.validate();
	}
	
	public void addNotify() {
		super.addNotify();
		this.getParent().getParent().setFocusable(true);
		this.getParent().getParent().addKeyListener(kc);
	}
	

	
}
