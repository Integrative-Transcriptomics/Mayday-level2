package mayday.GWAS.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class RevealTabbedPane extends JTabbedPane {

	/**
	 * add a new tab
	 * @param title 
	 * @param component 
	 * @param menu 
	 */
	public void addTab(String title, Component component) {
		super.addTab(title, component);
		int n = getTabCount() - 1;
		setTabComponentAt(n, new ClosableTabComponent(this));
	}
	
	private class ClosableTabComponent extends JPanel {	
		/**
		 * 
		 */
		private static final long serialVersionUID = -7674917455729706746L;
		
		public ClosableTabComponent(final JTabbedPane pane) {
			//unset default FlowLayout' gaps
	        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
	        
	        if (pane == null) {
	            throw new NullPointerException("TabbedPane is null");
	        }
	        setOpaque(false);
	        
	        //make JLabel read titles from JTabbedPane
	        JLabel label = new JLabel() {
	            /**
				 * 
				 */
				private static final long serialVersionUID = 63831682934034228L;

				public String getText() {
	                int i = pane.indexOfTabComponent(ClosableTabComponent.this);
	                if (i != -1) {
	                    return pane.getTitleAt(i);
	                }
	                return null;
	            }
	        };
	        
	        add(label);
	        //add more space between the label and the button
	        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
	        //tab button
	        JButton button = new TabButton();
	        add(button);
	        //add more space to the top of the component
	        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		}
		
		private class TabButton extends JButton {
			/**
			 * 
			 */
			private static final long serialVersionUID = 125832911663608195L;

			public TabButton() {
				int size = 17;
				setPreferredSize(new Dimension(size, size));
				setToolTipText("close this tab");
				// Make the button looks the same for all Laf's
				setUI(new BasicButtonUI());
				// Make it transparent
				setContentAreaFilled(false);
				// No need to be focusable
				setFocusable(false);
				setBorder(BorderFactory.createEtchedBorder());
				setBorderPainted(false);
				// Making nice roll-over effect
				addMouseListener(new MouseListener() {
					@Override
					public void mouseEntered(MouseEvent e) {
						Component component = e.getComponent();
			            if (component instanceof AbstractButton) {
			                AbstractButton button = (AbstractButton) component;
			                button.setBorderPainted(true);
			            }	
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						Component component = e.getComponent();
			            if (component instanceof AbstractButton) {
			                AbstractButton button = (AbstractButton) component;
			                button.setBorderPainted(false);
			            }
					}
					
					@Override
					public void mouseClicked(MouseEvent e) {}
					@Override
					public void mousePressed(MouseEvent e) {}
					@Override
					public void mouseReleased(MouseEvent e) {}
				});
				setRolloverEnabled(true);
				// Close the proper tab by clicking the button
				addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int i = indexOfTabComponent(ClosableTabComponent.this);
						if (i != -1) {
							RevealTabbedPane.this.remove(i);
						}
					}
				});
			}

			// we don't want to update UI for this button
			public void updateUI() {}

			// paint the cross
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				// shift the image for pressed buttons
				if (getModel().isPressed()) {
					g2.translate(1, 1);
				}
				g2.setStroke(new BasicStroke(2));
				g2.setColor(Color.BLACK);
				if (getModel().isRollover()) {
					g2.setColor(Color.RED);
				}
				int delta = 6;
				g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight()
						- delta - 1);
				g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight()
						- delta - 1);
				g2.dispose();
			}
		}
	}
}
