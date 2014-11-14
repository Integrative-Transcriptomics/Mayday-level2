package mayday.GWAS.visualizations.PValueHistogram;

import javax.swing.JLabel;

import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPList;

/**
 * @author jaeger
 *
 */
public class PVHistUtilities {
	
	/**
	 * @param snpList
	 * @return -log2(minimum-p-value)
	 */
	public static double getMaxLogPValue(SNPList snpList) {
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
	public static int getMaxLabelLength(SNPList snpList) {
		String max = "";
		for(int i = 0; i < snpList.size(); i++) {
			SNP snp = snpList.get(i);
			if(max.length() < snp.getID().length()) {
				max = snp.getID();
			}
		}
		return new JLabel(max).getBounds().width;
	}
}
