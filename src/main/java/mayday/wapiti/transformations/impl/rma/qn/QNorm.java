package mayday.wapiti.transformations.impl.rma.qn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.structures.linalg.matrix.VectorBasedMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;

public class QNorm {
	
	/** perform quantile normalization as in R, without special treatment of NA values */
	public static AbstractMatrix qnormC(AbstractMatrix data) {
		return qnormC(data, false);
	}
	
	/** perform quantile normalization as in R with the additional option to mask NaN values.
	 * @param data the unnormalized matrix
	 * @param maskNA if true, all NA values are set to x-(|x|/2) where x is the smallest value in the respective column. After
	 * normalization, the NA values are restored. Effectively, this allows columns without NA values to be normalized correctly
	 * without adding unnecessary NAs (thus removing valid numbers) or adding imaginary data points (by replacing NAs).
	 * @return the normalized matrix
	 */
	public static AbstractMatrix qnormC(AbstractMatrix data, boolean maskNA) {
		
		int rows = data.nrow();
		int cols = data.ncol();
		
		AbstractVector[] resColumns = new AbstractVector[cols];
		
		DoubleVector rowMean = new DoubleVector(rows);

		/* first find the normalizing distribution */
		for (int j = 0; j < cols; j++) {
			AbstractVector v = data.getColumn(j).shallow_clone(); // do not change original sort order
			v.sort();
			List<Integer> NApositions = null;
			if (maskNA) {
				NApositions = v.whichIsNA();
				if (!NApositions.isEmpty()) {
					double minReplacement = v.min(true, true);
					minReplacement = minReplacement - (Math.abs(minReplacement)/2.0);
					v.replaceNA(minReplacement);
				}
			}
			rowMean.add(v);
			if (NApositions != null && !NApositions.isEmpty()) {
				for (Integer i : NApositions)
					v.set(i, Double.NaN);
			}
		}
		rowMean.divide(cols);

		double[] ranks = new double[rows];
		ArrayList<dataitem> iv = new ArrayList<dataitem>();
		dataitem di;
		
		/* now assign back distribution */
		for (int j = 0; j < cols; j++) {
			AbstractVector column = data.getColumn(j);

			// fb: the following code is not adapted to the vector/matrix framework, because I am afraid I might break it.
			for (int i = 0; i < rows; i++) {
				di = new dataitem();
				di.data = column.get(i);
				di.rank = i;
				iv.add(di);
			}
			Collections.sort(iv);
			getRanks(ranks, iv, rows);
			
			DoubleVector newColumn = new DoubleVector(rows);
				
			for (int i = 0; i < rows; i++) {
				int ind = iv.get(i).rank;
				double oldV = column.get(ind);
				boolean wasNA = Double.isNaN(oldV);
				if (!maskNA || !wasNA) { // we can set the new value
					int i1 = (int) Math.floor(ranks[i]) - 1;
					double newVal;
					if (ranks[i] - Math.floor(ranks[i]) > 0.4) {
						int i2 = (int) Math.floor(ranks[i]);
						double val = 0.5 * (rowMean.get(i1) + rowMean.get(i2));
						newVal = val;
					} else {
						newVal = rowMean.get(i1);
					}
					newColumn.set(ind, newVal);
				} else {
					newColumn.set(ind, Double.NaN);
				}
			}
			resColumns[j] = newColumn;
			iv.clear();
		}
		
		return new VectorBasedMatrix(resColumns, false);
	}

	/*
	 * get ranks in the same manner as R does. Assume that x is already sorted
	 */
	public static void getRanks(double[] rank, ArrayList<dataitem> x, int n) {
		int i = 0, j;
		while (i < n) {
			j = i;
			while ((j < n - 1) && (x.get(j).data == x.get(j + 1).data))
				j++;
			if (i != j) {
				for (int k = i; k <= j; k++) {
					rank[k] = (i + j + 2) / 2.0;
				}
			} else {
				rank[i] = i + 1;
			}
			i = j + 1;
		}
	}

	/*
	 * public boolean itemComp(dataitem a, dataitem b){ return a.data < b.data; }
	 */

	private static class dataitem implements Comparable<dataitem> {
		double data;
		int rank;

		public int compareTo(dataitem b) {
			return Double.compare(this.data, b.data);
		}
	}
}
