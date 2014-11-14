package mayday.tiala.multi.gui;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * 
 * @author battke
 *
 */
@SuppressWarnings("serial")
public class ShowHidePanel extends JPanel implements ActionListener {

	JButton extendButton;
	JPanel center;
	Window op;
	
	public ShowHidePanel( Window outmostparent, boolean startExpanded ) {
		super(new BorderLayout());
		op = outmostparent;
		extendButton = new JButton("Click");
		extendButton.addActionListener(this);
		center = new JPanel(new BorderLayout());
		add(extendButton, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		setExpanded(startExpanded, false);
	}

	public JPanel getPanel() {
		return center;
	}
	
	public boolean isExpanded() {
		return center.isVisible();
	}
	
	public void setExpanded(boolean expanded, boolean revalidate) {
		center.setVisible(expanded);
		extendButton.setText(expanded ? "<< Less" : "More >>");
		if (revalidate) {
			invalidate();
			validate();		
			op.invalidate();	
			op.pack();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		setExpanded(!isExpanded(), true);
	}
}
