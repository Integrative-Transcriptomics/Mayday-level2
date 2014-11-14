package mayday.wapiti.transformations.impl.rma.mp;

import java.util.Iterator;
import java.util.List;

import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;

public class MPolish {
	
	/** perform median medianpolish
	 * @param data the data matrix to polish
	 * @param curRows the names of the rows that are part of one probeset 
	 * @param results the target vector where the resulting probeset ROW will be placed
	 * @param useTRMA if true, change the order of the median polish loop, to prevent inter-array correlation artifacts
	 * as described in Giorgi et al (BMC Bioinformatics 2010, 11:553)
	 */
	public static void medianPolish(AbstractMatrix data, List<String> curRows, AbstractVector results, boolean useTRMA){
		int cols = data.ncol();
		int ncurRows = curRows.size();
		
		AbstractVector r = new DoubleVector(ncurRows);
		AbstractVector c = new DoubleVector(cols);
		double t = 0.0;
		
		//create and fill z 
		AbstractVector[] z = new AbstractVector[cols];
		for(int j = 0; j < cols; j++){
			z[j] = new DoubleVector(ncurRows);
			Iterator<String> it = curRows.iterator();
			AbstractVector colJ = data.getColumn(j);
			boolean allNa=true;
			for(int i = 0; i < ncurRows; i++){
				double next = colJ.get(it.next());
				z[j].set(i, next);
				allNa&=Double.isNaN(next);
			}
			if (allNa) {
				results.setAllToValue(Double.NaN);
				return;
			}
		}
		
		t = fitValues(z, ncurRows, cols, r, c, useTRMA);
		for(int j = 0; j < cols; j++){
			results.set(j, t + c.get(j));
		}
	}
	
	private static double fitValues(AbstractVector[] data, int rows, int cols, AbstractVector r, AbstractVector c, boolean useTRMA) {
		 return useTRMA?fitValues_transposedMP(data, rows, cols, r, c):fitValues_originalMP(data, rows, cols, r, c);
	}
		
	private static double fitValues_transposedMP(AbstractVector[] data, int rows, int cols, AbstractVector r, AbstractVector c) {
		int maxiter = 10;
		double eps = 0.01;
		double oldsum = 0.0;
		double newsum = 0.0;
		double delta;
		AbstractVector rdelta = new DoubleVector(rows);
		AbstractVector cdelta = new DoubleVector(cols);
		double t = 0.0;
		
		for(int iter = 1; iter <= maxiter; iter++){
			
			// work on the columns
			getColMedian(data, cdelta, rows, cols);
			subtractByCol(data, cdelta, rows, cols);
			cmod(c, cdelta, cols);
			delta = median(r, rows);
			for(int i = 0; i < rows; i++){
				r.set(i, r.get(i) - delta);
			}
			t += delta;
			
			// work on the rows
			getRowMedian(data, rdelta, rows, cols);
			subtractByRow(data, rdelta, rows, cols);
			rmod(r, rdelta, rows);
			delta = median(c, cols);
			for(int j = 0; j < cols; j++){
				c.set(j, c.get(j)- delta);
			}
			t += delta;
			
			newsum = sumAbs(data, rows, cols);
			if(newsum == 0.0 || Math.abs((double)(1.0 - oldsum/newsum)) < eps){
				break;
			}
			oldsum = newsum;
		}
		return t;
	}
	
	private static double fitValues_originalMP(AbstractVector[] data, int rows, int cols, AbstractVector r, AbstractVector c) {
		int maxiter = 10;
		double eps = 0.01;
		double oldsum = 0.0;
		double newsum = 0.0;
		double delta;
		AbstractVector rdelta = new DoubleVector(rows);
		AbstractVector cdelta = new DoubleVector(cols);
		double t = 0.0;
		
		for(int iter = 1; iter <= maxiter; iter++){
			
			// work on the rows
			getRowMedian(data, rdelta, rows, cols);
			subtractByRow(data, rdelta, rows, cols);
			rmod(r, rdelta, rows);
			delta = median(c, cols);
			for(int j = 0; j < cols; j++){
				c.set(j, c.get(j)- delta);
			}
			t += delta;
			
			// work on the columns
			getColMedian(data, cdelta, rows, cols);
			subtractByCol(data, cdelta, rows, cols);
			cmod(c, cdelta, cols);
			delta = median(r, rows);
			for(int i = 0; i < rows; i++){
				r.set(i, r.get(i) - delta);
			}
			t += delta;
			
			newsum = sumAbs(data, rows, cols);
			if(newsum == 0.0 || Math.abs((double)(1.0 - oldsum/newsum)) < eps){
				break;
			}
			oldsum = newsum;
		}
		return t;
	}
	/*
	 * add elementwise cdelta to c
	 */
	private static void cmod(AbstractVector c, AbstractVector cdelta, int cols){
		for(int j = 0; j < cols; j++){
			c.set(j, c.get(j) + cdelta.get(j));
		}
	}
	/*
	 * add elementwise rdelta to r
	 */
	private static void rmod(AbstractVector r, AbstractVector rdelta, int rows){
		for(int i = 0; i < rows; i++){
			r.set(i, r.get(i) + rdelta.get(i));
		}
	}
	/*
	 * subtract the elements of cdelta off each col of z
	 */
	private static void subtractByCol(AbstractVector[] data, AbstractVector cdelta, int rows, int cols){
		for(int j = 0; j < cols; j++){
			for(int i = 0; i < rows; i++){
				data[j].set(i, data[j].get(i) - cdelta.get(j));
			}
		}
	}
	/*
	 * subtract the elements of rdelta off each row of z
	 */
	private static void subtractByRow(AbstractVector[] data, AbstractVector rdelta, int rows, int cols){
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				data[j].set(i, data[j].get(i) - rdelta.get(i));
			}
		}
	}
	/*
	 * get the col medians of a matrix
	 */
	private static void getColMedian(AbstractVector[] data, AbstractVector cdelta, int rows, int cols){
		for(int j = 0; j < cols; j++){
			cdelta.set(j, median(data[j], rows));
		}
	}
	/*
	 * get row medians of a matrix
	 */
	private static void getRowMedian(AbstractVector[] data, AbstractVector rdelta, int rows, int cols){
		AbstractVector buffer = new DoubleVector(cols);
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				buffer.set(j, data[j].get(i));
			}
			rdelta.set(i, medianNoCopy(buffer, cols));
		}
	}
	/*
	 * returns the sum of the absolute values of elements of the matrix z
	 */
	private static double sumAbs(AbstractVector[] data, int rows, int cols){
		double sum = 0.0;
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				sum += Math.abs(data[j].get(i));
			}
		}
		return sum;
	}
	/*
	 * returns the median of x
	 */
	private static double median(AbstractVector x, int length){
		double med = 0.0;
		AbstractVector buffer = x.clone();
		int half = (length + 1) / 2;

		buffer.sort();
		
		med = buffer.get(half-1);
		if(length % 2 == 0){
			med = (med + buffer.get(half))/2.0;
		}
		return med;
	}
	
	private static double medianNoCopy(AbstractVector x, int length){
		double med = 0.0;
		int half = (length + 1)/2;
		x.sort();
		med = x.get(half-1);
		if(length % 2 == 0){
			med = (med + x.get(half))/2.0;
		}
		return med;
	}
}
