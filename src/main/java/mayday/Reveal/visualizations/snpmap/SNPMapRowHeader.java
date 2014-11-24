package mayday.Reveal.visualizations.snpmap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

@SuppressWarnings("serial")
public class SNPMapRowHeader extends JComponent implements MouseListener, ViewModelListener, KeyListener, MouseMotionListener {

	private SNPMap snpMap;
	
	public SNPMapRowHeader(SNPMap map) {
		this.snpMap = map;
		this.addMouseListener(this);
		this.addKeyListener(this);
		this.addMouseMotionListener(this);
	}
	
	public void removeNotify() {
		if(snpMap.getViewModel() != null)
			snpMap.getViewModel().removeViewModelListener(this);
		super.removeNotify();
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setBackground(Color.white);
		g2.clearRect(0,0, getWidth(), getHeight());
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paint(g2);
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		SubjectList persons = snpMap.getData().getSubjects();
		int cellHeight = snpMap.setting.getCellHeight();

		Rectangle vBounds = snpMap.compScroller.getVisibleRect();
		int starty = snpMap.compScroller.getVerticalScrollBar().getValue();
		int start2 = Math.max((int)Math.floor(starty / cellHeight), 0);
		int stop2 = (int)Math.ceil((starty + vBounds.height) / cellHeight) + 1;
		stop2 = Math.min(stop2, snpMap.persons.size());
		
		AffineTransform af = g2.getTransform();
		double transY = start2 * cellHeight;
		af.translate(0, transY);
		g2.setTransform(af);

		Color selectionColor = snpMap.setting.getSelectionColor();
		Color c = new Color(selectionColor.getRed(), selectionColor.getGreen(), selectionColor.getBlue(), 100);
		Rectangle2D rec = new Rectangle2D.Double(0, 0, getWidth(), cellHeight);
		
		int count = 0;
		for(int i = start2; i < stop2; i++) {
			Integer index = snpMap.personIndices.get(i);
			Subject p = persons.get(index.intValue());
			String id = p.getID();
			
			Rectangle2D bounds = g2.getFontMetrics().getStringBounds(id, g2);
		
			if(cellHeight < bounds.getHeight()/1.5)
				break;
			
			g2.translate(0, count * cellHeight + 1);
			count++;
			
			if(snpMap.getViewModel().isSelected(p)) {
				g2.setColor(c);
				g2.fill(rec);
				g2.setColor(Color.BLACK);
			}
			
			g2.translate(0, bounds.getHeight() + (cellHeight - bounds.getHeight())/2. - 2);
			g2.drawString(id, 0, 0);
			g2.setTransform(af);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			int y = e.getY();
			int cellSize = snpMap.setting.getCellHeight();
			Integer index = y / cellSize;
			
			if(index < snpMap.personIndices.size()) {
				Integer personIndex = snpMap.personIndices.get(index);
//				System.out.println(personIndex);
				Subject p = snpMap.persons.get(personIndex);
				
				if(e.isControlDown()) {
					snpMap.getViewModel().togglePersonSelected(p);
				} else {
					snpMap.getViewModel().setPersonSelection(p);
				}
			}
		}
		
		if(e.getButton() == MouseEvent.BUTTON3) {
			snpMap.aggregate();
		}
	}

	private int startIndex = -1;
	private int stopIndex = -1;
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			int y = e.getY();
			int cellSize = snpMap.setting.getCellHeight();
			startIndex = y / cellSize;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			int y = e.getY();
			int cellSize = snpMap.setting.getCellHeight();
			stopIndex = y / cellSize;
			
			if(draged) {
				draged = false;
				
				if(startIndex > stopIndex) {
					int tmp = startIndex;
					startIndex = stopIndex;
					stopIndex = tmp;
				}
				
				if(startIndex < snpMap.personIndices.size() && startIndex >= 0
						&& stopIndex < snpMap.personIndices.size() && stopIndex >= 0) {
					
					if(startIndex != stopIndex) {
						Set<Subject> selection = new HashSet<Subject>();
						for(int i = startIndex; i <= stopIndex; i++) {
							selection.add(snpMap.persons.get(((Integer)snpMap.personIndices.get(i)).intValue()));
						}
						
						Set<Subject> newSelection = new HashSet<Subject>(snpMap.getViewModel().getSelectedPersons());
						
						if(e.isControlDown()) {
							if(newSelection.containsAll(selection)) {
								newSelection.removeAll(selection);
							} else {
								newSelection.addAll(selection);
							}
						} else {
							newSelection = selection;
						}
						snpMap.getViewModel().setPersonSelection(newSelection);
					}
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.PERSON_SELECTION_CHANGED:
			repaint();
		}
	}

	private boolean aggregate = false;
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.isControlDown() && e.isShiftDown()) {
			if(e.getKeyCode() == KeyEvent.VK_A) {
				aggregate = true;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		System.out.println(aggregate);
		if(aggregate) {
			snpMap.aggregate();
			aggregate = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	private boolean draged = false;
	
	@Override
	public void mouseDragged(MouseEvent e) {
		draged = true;
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
}
