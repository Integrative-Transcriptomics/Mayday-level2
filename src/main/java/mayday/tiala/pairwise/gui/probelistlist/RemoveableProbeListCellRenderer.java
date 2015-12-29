package mayday.tiala.pairwise.gui.probelistlist;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import mayday.core.probelistmanager.gui.cellrenderer.ProbeListCellRenderer;

@SuppressWarnings("serial")
public class RemoveableProbeListCellRenderer extends JPanel implements ListCellRenderer {

	ProbeListCellRenderer plcr;
	JLabel closer;
	
	public RemoveableProbeListCellRenderer() {
		plcr = new ProbeListCellRenderer();
		closer = new JLabel("x");
		closer.setMaximumSize(closer.getMinimumSize());
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(plcr);
		add(Box.createHorizontalGlue());
		add(closer);
	}
	
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		plcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		closer.setText("<html><small><b><font color=#ff8888>x");
		
		if ( isSelected )
		{
			setBackground( list.getSelectionBackground() );
		}
		else
		{
			setBackground( list.getBackground() );
		}	

		setEnabled( list.isEnabled() );
		setFont( list.getFont() );
		setOpaque( true );
		
		return this;
	}

}
