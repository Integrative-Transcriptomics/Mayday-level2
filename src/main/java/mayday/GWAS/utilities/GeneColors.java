package mayday.GWAS.utilities;

import java.awt.Color;

/**
 * @author jaeger
 *
 */
public class GeneColors {

	/**
	 * brewer color scheme
	 */
	public static Color[] brewerColors = new Color[]{
		new Color(166, 206, 227),
		new Color(31, 120, 180),
		new Color(178, 223, 138),
		new Color(51, 160, 44),
		new Color(251, 154, 153),
		new Color(227, 26, 28),
		new Color(253, 191, 111),
		new Color(255, 127, 0),
		new Color(202, 178, 214),
		new Color(106, 61, 154),
		new Color(255, 255, 153)
	};
	
	/**
	 * @param colors
	 * @return color[] of up to 11 distinct colors
	 */
	public static Color[] colorBrewer(int colors) {
		Color[] brewer = new Color[colors];
		
		for(int i = 0; i < colors; i++) {
			if(i < brewerColors.length)
				brewer[i] = brewerColors[i];
			else
				//FIXME use distinct colors here!!!!O
				brewer[i] = rainbow(i,0.8)[0];
		}
		return brewer;
	}
	
	public static Color[] rainbow(int colors, double saturation)
	{
		int groups = 6;
		
		if ( saturation > 1 )
			saturation = 1;

		if ( saturation < 0 )
			saturation = 0;

		int l_maxComponentValue =(int)(255 * saturation);

		int r = 255-l_maxComponentValue;
		int g = l_maxComponentValue;
		int b = l_maxComponentValue;
		int counter = 0;

		int maxC = groups * (l_maxComponentValue + 1);
		
		Color[] l_rainbow = new Color[maxC];

		
		for ( counter = 0; counter < maxC; ++counter )
		{
			      if ( counter < 1 * l_maxComponentValue )
			      {
			        l_rainbow[counter] = new Color( r, g, b-- ); // from cyan to green
			      }
			      if ( counter < 2 * l_maxComponentValue && counter >= 1 * l_maxComponentValue )
			      {
			        l_rainbow[counter] = new Color( r++, g, b ); // from green to yellow 
			      }
			      if ( counter < 3 * l_maxComponentValue && counter >= 2 * l_maxComponentValue )
			      {
			        l_rainbow[counter] = new Color( r, g--, b ); // from yellow to red
			      }
			      if ( counter < 4 * l_maxComponentValue && counter >= 3 * l_maxComponentValue )
			      {
			        l_rainbow[counter] = new Color( r, g, b++ ); // from red to magenta
			      }
			      if ( counter < 5 * l_maxComponentValue && counter >= 4 * l_maxComponentValue )
			      {
			        l_rainbow[counter] = new Color( r--, g, b ); // from magenta to blue
			      }
			      if ( counter < 6 * l_maxComponentValue && counter >= 5 * l_maxComponentValue )
			      {
			        l_rainbow[counter] = new Color( r, g++, b ); // from blue to cyan
			      }
		}

		Color[] l_result = new Color[colors];

		for ( int i = 0; i < colors; ++i )
		{
			double pos = ((double)maxC  / (double)colors ) * i;			
			l_result[i] = l_rainbow[(int)pos];
		}

		return ( l_result ); 
	}
}
