package mayday.Reveal.utilities;

public class RevealUtilities {

	/**
	 * assign a index to each pair:
	 * AA = 0; AT = 1; AC = 2; AG = 3; TT = 4; TC = 5; TG = 6; CC = 7; CG = 8; GG = 9;
	 * @param a 
	 * @param b 
	 * @return index of the provided pairs
	 */
	public static int getPairIndex(char a, char b) {
		
		if(a > b) {
			char t = a;
			a = b;
			b = t;
		}
		
		if(a == 'A') {
			if(b == 'A') 
				return 0;
			if(b == 'T')
				return 1;
			if(b == 'C')
				return 2;
			if(b == 'G')
				return 3;
		}
		if(a == 'T') {
			if(b == 'T')
				return 4;
		}
		if(a == 'C') {
			if(b == 'T')
				return 5;
			if(b == 'C')
				return 7;
			if(b == 'G')
				return 8;
		}
		if(a == 'G') {
			if(b == 'T')
				return 6;
			if(b == 'G')
				return 9;
		}
		return -1;
	}
}
