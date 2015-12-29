package mayday.gsanalysis.gui;

import java.awt.event.ActionEvent;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import mayday.core.structures.graph.Node;
import mayday.vis3.graph.GraphCanvas;

import mayday.gsanalysis.Result;
import mayday.gsanalysis.graph.GraphEnrichmentAnalysisPlugin;

@SuppressWarnings("serial")
public class ShowGraphAction extends AbstractAction
{
	private GraphEnrichmentAnalysisPlugin graphPlugin;
	private ResultFrame resultGUI;
	private Result result;
	
	public ShowGraphAction(GraphEnrichmentAnalysisPlugin graphPlugin, Result result, ResultFrame resultGUI) {
		super("Highlight selected terms in the graph");
		this.graphPlugin = graphPlugin;
		this.resultGUI = resultGUI;
		this.result = result;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		JTable table=resultGUI.getTable();
		Set<String> genesetNames=new TreeSet<String>();
		for(int row: table.getSelectedRows()) {
			String genesetName="";
			if(result.getEnrichmentWithClasses()) {
				genesetName = (String) table.getValueAt(row,2);
			}
			else {
				genesetName = (String) table.getValueAt(row,0);
			}
			genesetNames.add(genesetName);			
		}
		GraphCanvas graphCanvas = graphPlugin.getGraphCanvas();
		if(genesetNames.isEmpty()||graphCanvas==null)
			return;
		graphCanvas.getSelectionModel().clearSelection();
		
		for(String s: genesetNames)
		{
			Node n=graphCanvas.getModel().getGraph().findNode(s);
			if(n!=null) {
//				graphCanvas.getModel().getComponent(n).setSelected(true);
				graphCanvas.updatePlot();
				graphCanvas.center(graphCanvas.getModel().getComponent(n).getBounds(), true);
				graphCanvas.getSelectionModel().selectSilent(graphCanvas.getModel().getComponent(n));
			}
		}
	}
	

	
}
