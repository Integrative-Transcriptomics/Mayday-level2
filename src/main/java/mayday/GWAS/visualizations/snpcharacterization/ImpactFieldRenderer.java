package mayday.GWAS.visualizations.snpcharacterization;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class ImpactFieldRenderer extends JLabel implements TableCellRenderer {

	public ImpactFieldRenderer() {
		setOpaque(true);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		Color bgColor = Color.WHITE;
		
		switch(Integer.parseInt(value.toString())) {
		case 1:
			bgColor = new Color(254, 232, 200);
			break;
		case 2:
			bgColor = new Color(253, 187, 132);
			break;
		case 3:
			bgColor = new Color(227, 74, 51);
			break;
		}
		
		setBackground(bgColor);
		setText(value.toString());
		setHorizontalAlignment(JLabel.CENTER);
		
		return this;
	}
}
