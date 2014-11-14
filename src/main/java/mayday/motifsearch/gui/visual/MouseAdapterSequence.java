package mayday.motifsearch.gui.visual;

/**
 * This class handles the mouse events of the sequence table 
 * and informs the sequence color model over this event if necessary
 * 
 * @author Frederik Weber
 */
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class MouseAdapterSequence
extends MouseAdapter {

    private SequenceTable sequenceTable;
    private byte clickcount = 0; // counts the clicks
    private Rectangle saveLastRect = new Rectangle(0, 0, -1, -1); // double click window
    private long lastClickTimeMili; // stores the time between clicks

    /**
     * constructor of MouseAdapterSequence
     * 
     * @param sequenceTable
     *                the sequence table this adapter is used for
     * 
     */
    public MouseAdapterSequence(SequenceTable sequenceTable) {
	super();
	this.sequenceTable = sequenceTable;
    }

    /**
     * @see java.awt.event.MouseAdapter#mouseClicked(MouseEvent)
     * 
     */
    @Override
    public void mouseClicked(MouseEvent e) {
	MotifSelectPopupMenu pm = this.sequenceTable.getMotifSelectPopupMenu();
	if (pm != null) {
	    pm.setVisible(false);
	}
	int row = sequenceTable.rowAtPoint(new Point(e.getX(), e.getY()));
	int column = sequenceTable.columnAtPoint(new Point(e.getX(), e.getY()));
	this.sequenceTable.clearSelection();
	this.sequenceTable.changeSelection(row, column, false, false);

	if (!this.saveLastRect.contains(e.getPoint())) { // first click
	    this.clickcount = 1;
	    this.saveLastRect = new Rectangle(e.getX() - 2, e.getY() - 2, 5, 5);
	    this.lastClickTimeMili = System.currentTimeMillis();
	}
	else { // second click in the double click window
	    this.clickcount++;
	    if (this.clickcount >= 2) {
		this.clickcount = 0;
		this.saveLastRect = new Rectangle(0, 0, -1, -1);

		/* determine if time window for double click is valid */
		if ((System.currentTimeMillis() - this.lastClickTimeMili) <= 800) {
		    if (sequenceTable.getCellRenderer(row, column) instanceof SequenceCellRenderer) {
			SequenceCellRenderer pcr = (SequenceCellRenderer) sequenceTable
			.getCellRenderer(row, column);

			/*
			 * gets the colorModel loaded with the sequence corresponding
			 * to the row
			 */
			SequenceView view = (SequenceView) pcr
			.getTableCellRendererComponent(sequenceTable,
				sequenceTable.getValueAt(row, column),
				sequenceTable.isCellSelected(row,
					column), sequenceTable
					.isFocusOwner(), row, column);
			Rectangle r = sequenceTable.getCellRect(row, column,
				true);

			/*
			 * sets the relative Location to the SequenceViews
			 * relative Location
			 */
			int relx = e.getX() - r.x;
			int rely = e.getY() - r.y;

			/*
			 * creates dump mouse event for the SequenceView to
			 * handle
			 */
			MouseEvent me = new MouseEvent(view, e.getID(), e
				.getWhen(), e.getModifiers(), relx, rely, e
				.getX(), // attention: not the arg the
				// mouse event constructor expects
				e.getY(), // attention: not the arg the
				// mouse event constructor expects
				e.getClickCount(), e.isPopupTrigger(), e
				.getButton());
			(view).mouseClicked(me,
				this.sequenceTable);
		    }
		}
		else {
		    this.lastClickTimeMili = System.currentTimeMillis();
		}
	    }
	}
    }
}
