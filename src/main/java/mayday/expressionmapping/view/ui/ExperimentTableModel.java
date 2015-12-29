/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mayday.expressionmapping.view.ui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Stephan Gade
 */
@SuppressWarnings("serial")
public class ExperimentTableModel extends AbstractTableModel{

	private ExperimentTable table;

	private List<String> experimentNames;

	private int rowCount;

	static private String[] colNames ={"No.","Experiment Name"};


	public ExperimentTableModel(ExperimentTable table) {

		this.table = table;

		this.experimentNames = this.table.getexperimentNames();

		this.rowCount = this.experimentNames.size();

	}


	@Override
	public int getRowCount() {
		
		return this.rowCount;
	}

	@Override
	public int getColumnCount() {

		//only 2 columns
		return 2;

	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		if (columnIndex == 0)
			return rowIndex+1;
		else
			return this.experimentNames.get(rowIndex);
	}

	@Override
	public String getColumnName(int column)  {

		return ExperimentTableModel.colNames[column];

	}

}
