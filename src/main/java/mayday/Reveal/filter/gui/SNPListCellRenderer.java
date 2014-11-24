package mayday.Reveal.filter.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import mayday.Reveal.data.SNPList;

@SuppressWarnings("serial")
public class SNPListCellRenderer extends JLabel implements ListCellRenderer {

	public Component getListCellRendererComponent (
			JList list,
			Object value,            // value to display
			int index,               // cell index
			boolean isSelected,      // is the cell selected
			boolean cellHasFocus )    // the list and the cell have the focus
	{
		if (!(value instanceof SNPList)) {
			setText( value.toString() );

			return ( this ); 
		}

		String  s = "<html><nobr>"; 
		s += ((SNPList)value).getAttribute().getName();

		s += "<small><font color=#888888>";
		s += "&nbsp;&nbsp;S=" + ((SNPList)value).size();
//		s += "&nbsp;&nbsp;M=" + ((SNPList)value).getDataSet().getMIManager().getGroupsForObject(value).size();
		s += "</nobr></small>";
		s += "</html>"; 
		setText( s );

		if ( isSelected ) {
			setForeground( Color.BLACK );
			setBackground( list.getSelectionBackground() );
		} else {
			setForeground( Color.BLACK );
			setBackground( list.getBackground() );
		}	

		setEnabled( list.isEnabled() );
		setFont( list.getFont() );
		setOpaque( true );

		setToolTipText( ((SNPList)value).getAttribute().getInformation());

		return ( this );
	}	
}
