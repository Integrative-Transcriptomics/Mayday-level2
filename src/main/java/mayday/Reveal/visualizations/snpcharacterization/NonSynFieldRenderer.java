package mayday.Reveal.visualizations.snpcharacterization;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class NonSynFieldRenderer extends JLabel implements TableCellRenderer {
	
	public NonSynFieldRenderer() {
		setOpaque(true);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object o,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if(o instanceof NonSynField) {
			boolean ns = ((NonSynField)o).nonSynonymous;
			
			if(ns) {
				setBackground(new Color(227, 74, 51)); // red
			} else {
				setBackground(new Color(49, 163, 84)); // green
			}
			
			setToolTipText(ns ? "Non-Synonymous" : "Synonymous");
			setText(ns ? "YES" : "NO");
			setHorizontalAlignment(JLabel.CENTER);
		}
		
		return this;
	}
}
