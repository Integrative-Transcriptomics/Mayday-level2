package mayday.Reveal.visualizations.PValueHistogram;

import javax.swing.JLabel;

import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;

/**
 * @author jaeger
 *
 */
public class PVHistUtilities {
	
	/**
	 * @param snpList
	 * @return -log2(minimum-p-value)
	 */
	public static double getMaxLogPValue(SNVList snpList) {
//		double[] pValues = snpList.getSNPpValues(); 
//		double min = Double.MAX_VALUE;
//		for(double d : pValues) {
//			if(d < min && d != -1)
//				min = d;
//		}
//		return -(Math.log(min)/Math.log(2));
		//FIXME
		return 10;
	}
	
	/**
	 * @param snpList
	 * @return length of the longest label in pixels
	 */
	public static int getMaxLabelLength(SNVList snpList) {
		String max = "";
		for(int i = 0; i < snpList.size(); i++) {
			SNV snp = snpList.get(i);
			if(max.length() < snp.getID().length()) {
				max = snp.getID();
			}
		}
		return new JLabel(max).getBounds().width;
	}
}
