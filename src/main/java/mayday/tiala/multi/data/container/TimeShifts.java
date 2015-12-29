package mayday.tiala.multi.data.container;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class TimeShifts extends ArrayList<Double> {	
	/**
	 * @param size
	 */
	public TimeShifts(int size) {
		for(int i = 0; i < size; i++) {
			add(0.);
		}
	}
	
	/**
	 * @param timeShifts
	 */
	public TimeShifts(Collection<Double> timeShifts) {
		this.addAll(timeShifts);
	}
	
	/**
	 * @param timeShifts
	 */
	public TimeShifts(Double[] timeShifts) {
		for(int i = 0; i < timeShifts.length; i++) {
			add(timeShifts[i]);
		}
	}
	
	/**
	 * @param timeShifts
	 */
	public TimeShifts(double[] timeShifts) {
		for(int i = 0; i < timeShifts.length; i++) {
			add(timeShifts[i]);
		}
	}
	
	/**
	 * @param index
	 * @param timeShift
	 */
	public void setTimeShift(int index, double timeShift) {
		if(index-1 >= 0 && index-1 < this.size())
			set(index-1, timeShift);
		else
			throw new ArrayIndexOutOfBoundsException("No data set pair available for the specified index!");
	}
}
