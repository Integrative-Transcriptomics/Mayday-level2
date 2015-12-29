package mayday.expressionmapping.model.algorithm.transform;

import mayday.core.math.distance.DistanceMeasurePlugin;

/**
 * @author jaeger
 *
 */
public interface AttractorComputer extends TransformAlgorithm {
	/**
	 * @param distance
	 */
	public void setDistance (DistanceMeasurePlugin distance);
}
