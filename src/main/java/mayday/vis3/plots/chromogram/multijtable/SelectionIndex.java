/**
 * 
 */
package mayday.vis3.plots.chromogram.multijtable;

public  class SelectionIndex
{
	public int row;
	public int column;
	
	public SelectionIndex(int r, int c) 
	{
		row=r;
		column=c;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if(obj instanceof SelectionIndex)
			return row==((SelectionIndex)obj).row && column==((SelectionIndex)obj).column; 
		return false;
	}
	
	@Override
	public String toString() 
	{
		return "("+row+","+column+")";
	}
}