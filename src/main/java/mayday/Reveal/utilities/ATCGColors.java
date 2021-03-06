package mayday.Reveal.utilities;

import java.awt.Color;

/**
 * @author jaeger
 *
 */
public class ATCGColors {
	public static final Color A = new Color(215, 25, 28);//Color.RED;
	public static final Color T = new Color(43, 131, 186);//Color.BLUE;
	public static final Color C = new Color(230, 171, 2);//Color.ORANGE;
	public static final Color G = new Color(102, 189, 99);//Color.GREEN;
	
	/**
	 * @return BrBG genotype colors
	 */
	public static Color[] getGenotypeColors() {
		return new Color[] {new Color(84,48,5),
		new Color(140,81,10),
		new Color(191,129,45),
		new Color(223,194,125),
		new Color(246,232,195),
		new Color(199,234,229),
		new Color(128,205,193),
		new Color(53,151,143),
		new Color(1,102,94),
		new Color(0,60,48)};
	}
	
	/**
	 * @param index
	 * @return color[] of length two
	 */
	public static Color[] getColorPairs(int index) {
		switch(index) {
		case 0: return new Color[]{ATCGColors.A, ATCGColors.A};
		case 1: return new Color[]{ATCGColors.A, ATCGColors.T};
		case 2: return new Color[]{ATCGColors.A, ATCGColors.C};
		case 3: return new Color[]{ATCGColors.A, ATCGColors.G};
		case 4: return new Color[]{ATCGColors.T, ATCGColors.T};
		case 5: return new Color[]{ATCGColors.T, ATCGColors.C};
		case 6: return new Color[]{ATCGColors.T, ATCGColors.G};
		case 7: return new Color[]{ATCGColors.C, ATCGColors.C};
		case 8: return new Color[]{ATCGColors.C, ATCGColors.G};
		case 9: return new Color[]{ATCGColors.G, ATCGColors.G};
		default: return new Color[]{Color.WHITE, Color.WHITE};
		}
	}
	
	/**
	 * @param c
	 * @return allel color
	 */
	public static Color getColor(char c) {
		switch(c) {
			case 'A': return A;
			case 'T': return T;
			case 'C': return C;
			case 'G': return G;
			default: return Color.WHITE;
		}
	}
}
