package mayday.GWAS.visualizations.snpcharacterization;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import mayday.GWAS.utilities.AminoAcidColorScheme;
import mayday.GWAS.utilities.ColorScheme;

public class AAChangeRenderer implements TableCellRenderer {
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object o,
			boolean isSelected, boolean hasFocus, int row, int column) {
		SequenceComparisonField scf = (SequenceComparisonField)o;
		
		JPanel p = new JPanel();
		p.setOpaque(true);
		
		if(scf.original == null || scf.modifiedA == null || scf.modifiedB == null) {
			p.setBackground(Color.WHITE);
			return p;
		}
		
		if(scf.original.equals(scf.modifiedA) && scf.original.equals(scf.modifiedB)) {
			p.setBackground(Color.WHITE);
			return p;
		}
		
		ColorScheme cs = AminoAcidColorScheme.Shapely;
		
		if(scf.modifiedA.equals(scf.modifiedB) 
				|| !scf.modifiedA.equals(scf.original)) {
			Color c1 = cs.getColor(scf.original);
			Color c2 = cs.getColor(scf.modifiedA);
			String text = scf.original + " -> " + scf.modifiedA;
			DualColorLabel l = new DualColorLabel(c1, c2, text);
			l.setOpaque(false);
			l.setHorizontalAlignment(JLabel.CENTER);
			return l;
		} else {
			Color c1 = cs.getColor(scf.original);
			Color c2 = cs.getColor(scf.modifiedB);
			String text = scf.original + " -> " + scf.modifiedB;
			DualColorLabel l = new DualColorLabel(c1, c2, text);
			l.setOpaque(false);
			l.setHorizontalAlignment(JLabel.CENTER);
			return l;
		}
	}
	
	@SuppressWarnings("serial")
	private class DualColorLabel extends JLabel {
		
		Color c1, c2;
		
		public DualColorLabel(Color c1, Color c2, String text) {
			super(text);
			this.c1 = new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 100);
			this.c2 = new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 100);
		}
		
		protected void paintComponent(Graphics grphcs) {
			Graphics2D g2d = (Graphics2D) grphcs;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2d.setColor(c1);
			g2d.fillRect(0, 0, getWidth()/2, getHeight());
			g2d.setColor(c2);
			g2d.fillRect(getWidth()/2, 0, getWidth(), getHeight());
			
	        super.paintComponent(grphcs);
		}
	}
}
