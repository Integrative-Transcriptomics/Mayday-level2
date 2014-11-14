package mayday.motifsearch.tool;

import java.math.BigDecimal;

/**
 * class for rounding methods
 * 
 * @author Frederik Weber
 * 
 */
public class Rounder {

    /**
     * round a double with a precision after the comma
     * 
     * @param d
     *                the double to be rounded
     * @param scale
     *                the precision after the comma
     * @return the rounded double value
     */
    public static double round(double d, int scale) {
	if (Double.isNaN(d) || Double.isInfinite(d))
	    return d;
	scale = Math.max(scale, 0);
	BigDecimal bd = BigDecimal.valueOf(d);
	BigDecimal bc = new BigDecimal(bd.unscaledValue(), bd.precision() - 1);
	return ((bc.setScale(scale, java.math.RoundingMode.HALF_EVEN))
		.scaleByPowerOfTen(bc.scale() - bd.scale())).doubleValue();
    }

}
