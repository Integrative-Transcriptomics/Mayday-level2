
package mayday.expressionmapping.model.algorithm.sort;

import java.util.Comparator;
import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.container.PointList;

/**
 * @author Stephan Gade
 *
 */
public interface SortAlgorithm extends Comparator<Integer>{
    
    public static int VALUES = 0;
    
    public static int COORDINATES = 1;
	
    int[] sortAsc(PointList<? extends DataPoint> points, int mod, int index);
    
    int[] sortDesc(PointList<? extends DataPoint> points, int mod, int index);
    

}
