package mayday.wapiti.experiments.impl.microarray;

public class ArrayLayout {
	
	/* typically, an arraylayout consists of 4 numbers: 
	 * metarows, metacolumns, rows, columns
	 * The total number of features is metarows*metacolumns * rows*columns
	 * 
	 * Some platforms have "blocks" instead of metarows/columns. That case 
	 * is represented as a layout with 1 metacolumn and "blocks" metarows. 
	 */
	
	protected int[] numbers;
	
	public ArrayLayout() {
		this(1,1,0,0);
	}
	
	/** Construct a new array layout 
	 *@param mrow the number of meta-rows
	 *@param mcol the number of meta-columns
	 *@param row the number of spot rows per block
	 *@param col the number of spot columns per block 
	 * */
	public ArrayLayout(int mrow, int mcol, int row, int col) {
		numbers = new int[]{mrow,mcol,row,col};
	}
	
	/** Construct a new array layout with only one printtip */
	public ArrayLayout(int row, int col) {
		this(1,1,row,col);
	}
	
	/** Construct a new array layout 
	 *@param blocks the number of blocks
	 *@param row the number of spot rows per block
	 *@param col the number of spot columns per block 
	 * */
	public ArrayLayout(int blocks, int row, int col) {
		this(blocks,1,row,col);
	}

	/** The total number of spot columns */
	public int totalCols() {
		return cols()*metaCols();
	}
	
	/** The total number of spot rows */
	public int totalRows() {
		return rows()*metaRows();
	}
	
	/** The number of features per block / printtip*/
	public int featuresPerBlock() {
		return rows()*cols();
	}
	
	/** The total numbre of features */
	public int features() {
		return totalRows()*totalCols();
	}
	
	/** The number of blocks resp. printtips */
	public int blocks() {
		return metaCols()*metaRows();
	}

	/** The number of meta-rows */
	public int metaRows() {
		return numbers[0];
	}

	/** The number of meta-columns */
	public int metaCols() {
		return numbers[1];
	}

	/** The number of spot rows per block */
	public int rows() {
		return numbers[2];
	}

	
	/** The number of spot columns per block */
	public int cols() {
		return numbers[3];
	}
	
	/** Map the x,y-position of a spot to an index used to access data from a double[] array or AbstractVector */
	public int position2Index( int totalrow, int totalcol ) {
		// we are using row-major layouts
		return totalCols()*totalrow+totalcol;
	}
	
	public int position2Index( int mrow, int mcol, int row, int col ) {
		// map position to global index
		return position2Index( rows()*mrow+row, cols()*mcol+col );
	}
	
	public int position2Index( int mrow, int mcol, int subIndex, boolean ignored ) {
		int col = subIndex % cols();
		int row = (subIndex-col) / cols();
		// map position to global index
		return position2Index( mrow, mcol, row, col );
	}
	
	
	public int position2Index( int block, int row, int col ) {
		// map block to meta-row, metacol
		int metacol = block % metaCols();
		int metarow = (block-metacol) / metaCols();
		return position2Index( metacol, metarow, row, col );
	}
	
	public int position2Index( int block, int subIndex, boolean ignored ) {
		// map block to meta-row, metacol
		int metacol = block % metaCols();
		int metarow = (block-metacol) / metaCols();
		return position2Index( metacol, metarow, subIndex );
	}
	

	public int[] blockIndices(int block) {
		int metacol = block % metaCols();
		int metarow = (block-metacol) / metaCols();
		return blockIndices(metarow, metacol);
	}
	
	public int[] blockIndices(int mrow, int mcol) {
		int[] ret = new int[featuresPerBlock()];
		for (int i=0; i!=ret.length; ++i) {
			ret[i] = position2Index(mrow, mcol, i, true);
		}
		return ret;
	}
	
}
