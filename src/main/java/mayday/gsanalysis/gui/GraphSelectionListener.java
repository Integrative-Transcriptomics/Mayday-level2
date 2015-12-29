package mayday.gsanalysis.gui;

import javax.swing.JTable;

import mayday.gsanalysis.Result;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModelSelectionListener;

public class GraphSelectionListener implements GraphModelSelectionListener 
{
	JTable resultTable;
	Result result;

	GraphCanvas canvas;

	
	public GraphSelectionListener(JTable resultTable, Result result, GraphCanvas canvas) {
		this.resultTable=resultTable;
		this.result=result;
		this.canvas=canvas;
	}
	@Override
	public void selectionChanged() 
	{
		int col=result.getEnrichmentWithClasses()?2:0;
		resultTable.clearSelection();
		for(CanvasComponent cc: canvas.getSelectionModel().getSelectedComponents())
		{
			for(int i=0; i!= resultTable.getRowCount(); ++i)
			{
				if(cc.getLabel().equals(resultTable.getValueAt(i, col)))
				{
					resultTable.getSelectionModel().addSelectionInterval(i, i);
					resultTable.scrollRectToVisible(resultTable.getCellRect(i, 0, true));
				}
			}
		}

	}
}
