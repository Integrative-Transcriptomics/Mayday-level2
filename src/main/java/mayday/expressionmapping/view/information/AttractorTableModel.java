/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mayday.expressionmapping.view.information;


import java.util.List;

import javax.swing.table.AbstractTableModel;

import mayday.expressionmapping.gnu_trove_adapter.TIntArrayList;
import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.container.PointList;

/**
 *
 * @author Stephan Gade
 */
@SuppressWarnings("serial")
public class AttractorTableModel extends AbstractTableModel {

//	private AttractorTable table = null;
	private TIntArrayList mainAccessList = null;
	private TIntArrayList subAccessList = null;
	private PointList<? extends DataPoint> points = null;
	private List<String> annotations = null;
	private int numRows = 0;
	private String[] colnames = null;
	private String[] groupLabels = null;
	/* column with point IDs is for sure
	 */
	private int numCols = 1;
//	private int colBreak;
	int dim;

	public AttractorTableModel(AttractorTable table) {

//		this.table = table;

		this.points = table.getPoints();

		/* get the annoations
		 * this can be null, if no annotations are avaiable
		 */
		this.annotations = table.getAnnotations();

		/* if annotations are set we need one more column
		 */
		if (this.annotations != null) {
			this.numCols += 1;
		}

		this.dim = this.points.getDimension();

		this.groupLabels = points.getGroupLabels();

		this.numCols += this.dim * 2;

		this.colnames = new String[this.numCols];

		this.mainAccessList = table.getMainList();
		
		System.err.println("Main List size "+this.mainAccessList.size());

		this.numRows += this.mainAccessList.size();


		/* the break between the two lists
		 * if subAccessList is set
		 */
//		this.colBreak = this.numRows;

		if (table.isSubListSet()) {

			this.subAccessList = table.getSubList();
			
			System.err.println("SubList size "+this.subAccessList.size());

			this.numRows += this.subAccessList.size();

		}

		createColumnNames();


	}

	public int getRowCount() {

		return this.numRows;

	}

	public int getColumnCount() {

		return this.numCols;

	}

	@Override
	public String getColumnName(int column) {

		return this.colnames[column];

	}

	public Object getValueAt(int rowIndex, int columnIndex) {

		int tmpRowIndex;
		int tmpColIndex;
		
		int firstListSize = this.mainAccessList.size();

		/* set the row index we need to access the PointList and the annotations
		 */
		if (rowIndex >= firstListSize) {

			tmpRowIndex = this.subAccessList.get(rowIndex - firstListSize);

		} else {

			tmpRowIndex = this.mainAccessList.get(rowIndex);

		}

		if (columnIndex == 0) {
			
			//return (this.points.get(tmpRowIndex).getAllAttractorID() + 1);
			return (this.points.get(tmpRowIndex).getID() + 1);//ID of probes starts with 1 
			
		} /* if annotations are set they stand in the last column
		 */ else if (this.annotations != null && columnIndex == (this.numCols - 1)) {
			return this.annotations.get(tmpRowIndex);
		} else if (columnIndex > this.dim) {

			tmpColIndex = columnIndex % (this.dim + 1);

			double tmpCoordinate = this.points.get(tmpRowIndex).getCoordinates()[tmpColIndex];
			tmpCoordinate = ((long) Math.round(tmpCoordinate * 1000)) / 1000.0;

			return tmpCoordinate;

		} else {

			tmpColIndex = columnIndex - 1;

			double tmpValue = this.points.get(tmpRowIndex).getValues()[tmpColIndex];
			tmpValue = ((long) Math.round(tmpValue * 1000)) / 1000.0;

			return tmpValue;

		}


	}

	private void createColumnNames() {

		int i = 0;

		this.colnames[i++] = "ID";

		/* Values
		 */
		for (int j = 0; j < this.groupLabels.length; ++j, ++i) {

			this.colnames[i] = new String("Value: " + this.groupLabels[j]);

		}

		for (int j = 0; j < this.groupLabels.length; ++j, ++i) {

			this.colnames[i] = new String("Coordinate: " + this.groupLabels[j]);

		}

		if (this.annotations != null) {
			this.colnames[i] = "Annotations";
		}

	}
}

