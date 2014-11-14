package mayday.expressionmapping.model.algorithm.transform;

import mayday.expressionmapping.model.geometry.DataPoint;
import mayday.expressionmapping.model.geometry.container.PointList;

/**
 * @author Stephan Gade
 *
 */
public interface TransformAlgorithm  { 
    /**
     * @param points
     */
    public void transform(PointList<? extends DataPoint> points);
}
