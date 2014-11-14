package mayday.gsanalysis.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import mayday.core.gui.MaydayFrame;
import mayday.core.structures.Pair;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.gsanalysis.AbstractGSAnalysisPlugin;
import mayday.gsanalysis.Geneset;
import mayday.gsanalysis.Result;
import mayday.gsanalysis.gsea.GSEAEnrichment;
import mayday.gsanalysis.gsea.GSEAPlugin;

public class ResultListener extends MouseAdapter{
	private AbstractGSAnalysisPlugin plugin;
	private ResultFrame resultGUI;
	private Result result;
	public ResultListener(AbstractGSAnalysisPlugin plugin, Result result, ResultFrame resultGUI) {
		this.plugin=plugin;
		this.resultGUI=resultGUI;
		this.result=result;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount()!=2) {
			return;
		}
		int row = resultGUI.getTable().rowAtPoint(e.getPoint());
		if(row!=-1) {
			if(plugin.getName().equals("GSEA")&&((GSEAPlugin)plugin).getPermutationTests()) {
				String class1 = (String)resultGUI.getTable().getValueAt(row, 0);
				String class2 = (String)resultGUI.getTable().getValueAt(row, 1);
				String genesetName = (String) resultGUI.getTable().getValueAt(row,2);
				leadingEdgeAnalysis(new Pair<String,String>(class1,class2), genesetName);
			}
		}
	}

	
	protected void leadingEdgeAnalysis(Pair<String,String> classes, String genesetName) {
		Geneset geneset = null;
		for(Geneset g:plugin.getGenesets()) {
			if(g.getName().equals(genesetName)) {
				geneset=g;
				break;
			}
		}
		if(geneset==null) {
			return;
		}
 		GSEAEnrichment e = (GSEAEnrichment) result.getEnrichment(geneset,classes);
		if(e==null) {
			return;
		}
		
		MaydayFrame frame = new MaydayFrame();
		DefaultTableModel model = new DefaultTableModel();
		PermutableMatrix genes = e.getLeadingEdge();
		
		String[] columnIdentifiers = new String[genes.ncol()+1];
		columnIdentifiers[0]="Genename";
		for(int i=0;i!=genes.ncol();i++) {
			columnIdentifiers[i+1]=genes.getColumnName(i);
		}
		model.setColumnIdentifiers(columnIdentifiers);
		for(int row=0; row!=genes.nrow();row++) {
			Object[] newRow = new Object[genes.ncol()+1];
			newRow[0]=genes.getRowName(row);
			for(int col=0;col!=genes.ncol();col++) {
				newRow[col+1]=genes.getValue(row,col);
			}
			model.addRow(newRow);
		}
		JTable leadingEdgeTable = new JTable(model) {
	        private static final long serialVersionUID = 1L;

	        public boolean isCellEditable(int x, int y) {
	        	   return false;
	           }
	           
		};
		leadingEdgeTable.setAutoCreateRowSorter(true);
		frame.add(new JScrollPane(leadingEdgeTable));
		frame.setTitle("Leading edge for " + genesetName);
		frame.pack();
		frame.setVisible(true);
		
	}
	
}
