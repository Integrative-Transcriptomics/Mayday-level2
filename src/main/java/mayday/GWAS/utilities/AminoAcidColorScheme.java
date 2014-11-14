package mayday.GWAS.utilities;

import java.awt.Color;

/*
 * most color schemes are obtained from:
 * http://www.bioinformatics.nl/~berndb/aacolour.html
 */

public class AminoAcidColorScheme {
	public static ColorScheme Shapely = new ColorScheme() {
		
		@Override
		public Color getColor(String identifier) {
			char aa = identifier.charAt(0);
			switch(aa) {
			case 'A':
				return new Color(140,255,140);
			case 'G':
				return new Color(255,255,255);
			case 'L':
				return new Color(69,94,69);
			case 'S':
				return new Color(255,112,66);
			case 'V':
				return new Color(255,140,255);
			case 'T':
				return new Color(184,76,0);
			case 'K':
				return new Color(71,71,184);
			case 'D':
				return new Color(160,0,66);
			case 'I':
				return new Color(0,76,0);
			case 'N':
				return new Color(255,124,112);
			case 'E':
				return new Color(102,0,0);
			case 'P':
				return new Color(82,82,82);
			case 'R':
				return new Color(0,0,124);
			case 'F':
				return new Color(83,76,66);
			case 'Q':
				return new Color(255,76,76);
			case 'Y':
				return new Color(140,112,76);
			case 'H':
				return new Color(112,112,255);
			case 'C':
				return new Color(255,255,112);
			case 'M':
				return new Color(184,160,66);
			case 'W':
				return new Color(79,70,0);
			}
			
			return Color.WHITE;
		}
	};

	/*
	 * Color scheme in Lest, Introduction to Bioinformatics
	 */
	public static ColorScheme Lesk = new ColorScheme() {
		@Override
		public Color getColor(String identifier) {
			char aa = identifier.charAt(0);
			switch(aa) {
			case 'A':
				return Color.ORANGE;
			case 'G':
				return Color.ORANGE;
			case 'L':
				return Color.GREEN;
			case 'S':
				return Color.ORANGE;
			case 'V':
				return Color.GREEN;
			case 'T':
				return Color.ORANGE;
			case 'K':
				return Color.BLUE;
			case 'D':
				return Color.RED;
			case 'I':
				return Color.GREEN;
			case 'N':
				return Color.MAGENTA;
			case 'E':
				return Color.RED;
			case 'P':
				return Color.GREEN;
			case 'R':
				return Color.BLUE;
			case 'F':
				return Color.GREEN;
			case 'Q':
				return Color.MAGENTA;
			case 'Y':
				return Color.GREEN;
			case 'H':
				return Color.MAGENTA;
			case 'C':
				return Color.GREEN;
			case 'M':
				return Color.GREEN;
			case 'W':
				return Color.GREEN;
			}
			
			return null;
		}
	};
	
	public static ColorScheme Cinema = new ColorScheme() {
		@Override
		public Color getColor(String identifier) {
			char aa = identifier.charAt(0);
			switch(aa) {
			case 'A':
				return Color.WHITE;
			case 'G':
				return Color.ORANGE.darker().darker();
			case 'L':
				return Color.WHITE;
			case 'S':
				return Color.GREEN;
			case 'V':
				return Color.WHITE;
			case 'T':
				return Color.GREEN;
			case 'K':
				return Color.BLUE;
			case 'D':
				return Color.RED;
			case 'I':
				return Color.WHITE;
			case 'N':
				return Color.GREEN;
			case 'E':
				return Color.RED;
			case 'P':
				return Color.ORANGE.darker().darker();
			case 'R':
				return Color.BLUE;
			case 'F':
				return Color.MAGENTA;
			case 'Q':
				return Color.GREEN;
			case 'Y':
				return Color.MAGENTA;
			case 'H':
				return Color.BLUE;
			case 'C':
				return Color.YELLOW;
			case 'M':
				return Color.WHITE;
			case 'W':
				return Color.MAGENTA;
			}
			
			return null;
		}
	};
}
