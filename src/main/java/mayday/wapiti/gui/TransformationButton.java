package mayday.wapiti.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;

import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.gui.actions.transform.TransformationExtendAction;
import mayday.wapiti.gui.actions.transform.TransformationInsertAction;
import mayday.wapiti.gui.actions.transform.TransformationPropertiesAction;
import mayday.wapiti.gui.actions.transform.TransformationRemoveAction;
import mayday.wapiti.gui.actions.transform.TransformationRemoveSeveralAction;
import mayday.wapiti.gui.actions.transform.TransformationReplaceAction;
import mayday.wapiti.gui.actions.transform.TransformationSelectionAction;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.base.Transformation;

@SuppressWarnings("serial")
public class TransformationButton extends JButton implements ChangeListener {

	protected static TBUI tbui = new TBUI();	

	protected Transformation t;
	protected Experiment e;
	protected Color c;	

	public TransformationButton(Transformation t, Experiment e) {
		this.t=t;
		this.e=e;
		this.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		this.setOpaque(true);
		this.setText(t.getIdentifier(e));
		this.setUI(tbui);
		this.setToolTipText(t.getName());
		this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new TransformationButtonMenu().show(TransformationButton.this, 0, getHeight());
			}
		});
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				new TransformationButtonMenu().show(TransformationButton.this, 0, getHeight());
			}
		});
//		t.getTransMatrix().addListener(this);
	}


	protected Color getColor() {
		if (c==null)
			c = Color.black;
		return c;
	}
	
	public void setColor(Color c1) {
		c=c1;
	}

	public Color getBackground() {
		Color bgCol = getColor();
		bgCol = new Color(bgCol.getRed(), bgCol.getGreen(), bgCol.getBlue(), 128);
		return bgCol;
	}

	public static class TBUI extends BasicButtonUI {

		public void update(Graphics g, JComponent c) {
			g.setColor(Color.white);
			g.fillRect(0, 0, c.getWidth(), c.getHeight());
			super.update(g, c);
		}
		
		protected void paintButtonPressed(Graphics g, AbstractButton b){
			g.setColor(((TransformationButton)b).getColor());
			g.fillRect(0, 0, b.getWidth(), b.getHeight());
		}

	}

	public class TransformationButtonMenu extends JPopupMenu {

		public TransformationButtonMenu() {

			SelectionModel sm = t.getTransMatrix().getPane().getSelectionModel();
			
			addHeader("-- "+t.getName());
			add( new TransformationPropertiesAction(t) );
			add( new TransformationRemoveAction(t, null) );
			
			addHeader("-- Selection");
			add( new TransformationSelectionAction(t, sm));
			add( new TransformationExtendAction(t, sm ) );
			add( new TransformationRemoveSeveralAction(t, sm));
			
			addHeader("-- Current experiment");
			add( new TransformationRemoveAction(t, e) );

			addHeader("-- Change the matrix");
			add( new TransformationInsertAction(t, sm));
			add( new TransformationReplaceAction(t));
		}
		
		protected void addHeader(String header) {
			// give us some space
			if (getComponentCount()>0) {
				AbstractAction aa = new AbstractAction("") {
					public void actionPerformed(ActionEvent e) {}
					public boolean isEnabled() {
						return false;
					}
				};
				JMenuItem jmi = new JMenuItem(aa);
				add(jmi);
			}
			
			// add a menu title
			AbstractAction aa = new AbstractAction(header) {
				public void actionPerformed(ActionEvent e) {}					
				public boolean isEnabled() {
					return false;
				}
			};
			JMenuItem jmi = new JMenuItem(aa);
			jmi.setFont(jmi.getFont().deriveFont(Font.BOLD));
			add(jmi);
		}
 
	}

	public void stateChanged(ChangeEvent e) {
//		repaint();
	}
	
	


}
