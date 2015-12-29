package mayday.motifsearch.tool;


public class DNASequenceUtils {
    public static long countu = 0;
    public static long countd = 0;
    public static String report = "";

    /**
     * gets the complement sequence
     * 
     * @author Frederik Weber 
     */
    public static final String complementSequence(String DNASequence) {
	return DNASequence.toUpperCase().replace("A", "B").replace("C", "D")
	.replace("G", "C").replace("T", "A").replace("B", "T").replace(
		"D", "G");
    }

    /**
     * gets the reverse complement sequence
     * 
     * @author Frederik Weber 
     */
    public static final String reverseComplementSequence(String DNASequence) {
	return DNASequenceUtils.complementSequence((new StringBuffer(DNASequence)).reverse().toString());
    }

    /**
     * @author Frederik Weber 
     * 
     * gets Substring from a sequence considering up- and downstream parameters
     *  
     * Sequence starts with Index 1 and ends with index Length(Sequence)
     * upstream + downstream = returned length
     */
    public static final String substringFromSectionSequence(String searchSequence, long offset, long endLocation, int additionalUpstreamLength, int maxDownstreamLength, boolean useReversComplement) throws IndexOutOfBoundsException {
	return DNASequenceUtils.substringFromSectionSequence(searchSequence, (int)offset, (int)endLocation, additionalUpstreamLength, maxDownstreamLength, useReversComplement);
    }

    /**
     * @author Frederik Weber 
     * 
     * gets Substring from a sequence considering up- and downstream parameters
     *  
     * Sequence starts with Index 1 and ends with index Length(Sequence)
     * upstream + downstream = returned length
     */
    public static final String substringFromSectionSequence(String searchSequence, int offset, int endLocation, int additionalUpstreamLength, int maxDownstreamLength, boolean useReversComplement) throws IndexOutOfBoundsException {
	String tmpReturnSeqence;
	int tmpOffsetDNASequence = 0;
	int tmpEndLocationDNASequence = 0;

	/* consider Strand of gene and maybe create reverse complement */
	if (useReversComplement) {

	    tmpOffsetDNASequence = Math.max(
		    (offset - 1), 
		    (endLocation - maxDownstreamLength));
	    tmpEndLocationDNASequence = endLocation + additionalUpstreamLength;


	    /* check and set bounds of search and consider upstream region width */
	    if ((tmpOffsetDNASequence >= 0) 
		    && (tmpEndLocationDNASequence <= searchSequence.length())){

		/* get gene sequence from genome */
		tmpReturnSeqence = searchSequence.substring(tmpOffsetDNASequence,
			tmpEndLocationDNASequence);

		/* create reverse complement */
		tmpReturnSeqence = DNASequenceUtils.reverseComplementSequence(tmpReturnSeqence);

	    } else {
		/* get gene sequence from genome with upstream as long as possible*/
		int NumberOfNucleotidesLeft = searchSequence.length() - endLocation;
		//		System.out.println("upstream chosen to big for searched DNA strand - " + " choose: " + String.valueOf(NumberOfNucleotidesLeft));
		DNASequenceUtils.report += "upstream chosen to big for searched DNA strand - " + " choose: " + NumberOfNucleotidesLeft+ "\n";
		DNASequenceUtils.countu++;
		//tmpOffsetDNASequence = offset - 1;
		tmpEndLocationDNASequence = endLocation + NumberOfNucleotidesLeft;

		tmpReturnSeqence = searchSequence.substring(tmpOffsetDNASequence,
			tmpEndLocationDNASequence);
	    }

	} else { 
	    tmpOffsetDNASequence = offset - 1 - additionalUpstreamLength;
	    tmpEndLocationDNASequence = Math.min(
		    endLocation, 
		    offset - 1 + maxDownstreamLength);

	    /* check and set bounds of search and consider upstream region width */
	    if ((tmpOffsetDNASequence >= 0) 
		    && (tmpEndLocationDNASequence <= searchSequence.length())){

		/* get gene sequence from genome */
		tmpReturnSeqence = searchSequence.substring(tmpOffsetDNASequence,
			tmpEndLocationDNASequence);
	    } else {
		/* get gene sequence from genom with upstream as long as possible*/
		int NumberOfNucleotidesLeft = offset - 1 - 0;
		//		System.out.println("upstream chosen to big for searched DNA strand + " + " choose: " + String.valueOf(NumberOfNucleotidesLeft));
		DNASequenceUtils.report += "upstream chosen to big for searched DNA strand + " + " choose: " + NumberOfNucleotidesLeft + "\n";
		DNASequenceUtils.countd++;
		tmpOffsetDNASequence = offset - 1 - NumberOfNucleotidesLeft;

		tmpReturnSeqence = searchSequence.substring(tmpOffsetDNASequence,
			tmpEndLocationDNASequence);
	    }
	}
	return tmpReturnSeqence;
    }




}
