package mayday.motifsearch.preparation;


public class GeneLocation implements IGeneticCoordinate{
    public static long countu = 0;
    public static long countd = 0;
    public static String report = "";


    private long from;//offsetDNASequence; // the first Nucleotide on the DNA Sequence
    // gene starts in reference to + strand
    private long to;//endLocationDNASequence; // the last Nucleotide on the DNA
    // Sequence gene starts in reference to + strand

    private boolean isPlusStrand; // if it is on + Strand of DNA else it is on -
    // Strand

    //    private GeneticCoordinate geneticCoordinate;
    private int lengthProtein; // length in #Aminoacids

    private String PID;
    private String geneName;
    private String synonym;
    private String code;
    private String COG;
    private String product;
    private Chromosome chromosome; // the chromosome the gene is located


    public GeneLocation(int from, int to,
	    boolean isPlusStrand, int lengthProtein, String pid,
	    String geneName, String synonym, String code, String cog,
	    String product) {
		
    	super();
		this.from = from;
		this.to = to;
		this.isPlusStrand = isPlusStrand;
		this.lengthProtein = lengthProtein;
		PID = pid;
		this.geneName = geneName;
		this.synonym = synonym;
		this.code = code;
		COG = cog;
		this.product = product;
    }

    public final long getFrom() {
	return this.from;
    }

    public final long getTo() {
	return this.to;
    }
    public final boolean isPlusStrand() {
	return isPlusStrand;
    }

    public final int getLengthProtein() {
	return lengthProtein;
    }

    public final String getPID() {
	return PID;
    }

    public final String getCode() {
	return code;
    }

    public final String getProduct() {
	return product;
    }

    public final String getSynonym() {
	return synonym;
    }

    public final String getCOG() {
	return COG;
    }


    public final Chromosome getChromosome() {
	return chromosome;
    }


    public void setChromosome(Chromosome chromosome) {
	this.chromosome = chromosome;
    }

    @Override
    public String toString()
    {
	return this.chromosome +": "+this.from+"-"+this.to+" strand:"+this.getStrand();
    }
    public char getStrand()
    {
	return (this.isPlusStrand()? '+': '-');
    }

    public int checkInterferenceUpstream(int additionalUpstreamLength, boolean isSafeExtractionMode, int minUpstreamLength){
	if (isSafeExtractionMode){
	    GeneLocation predecessorGeneLocation = this.getChromosome().getGeneLocationSuccessor(this);
	    if (predecessorGeneLocation != null){
		int nucleotidesToNextGene;
		if (this.getFrom() <= predecessorGeneLocation.getTo()) { //  if nested
		    return Math.max((minUpstreamLength > (this.getFrom()-1)?(int)(this.getFrom()-1):minUpstreamLength),(additionalUpstreamLength > (this.getFrom()-1)?(int)(this.getFrom()-1):additionalUpstreamLength));
		} else {
		    nucleotidesToNextGene = (int) (this.getFrom() - predecessorGeneLocation.getTo() -1);
		    if(additionalUpstreamLength > nucleotidesToNextGene){
			//        				System.out.println("upstream parameter exceedes ("+ this.getSynonym()+ ") gene's predecessor on same strand or other strand ("+ predecessorGeneLocation.getSynonym() + "), " + Math.max(minUpstreamLength,nucleotidesToNextGene) +" instead of "+ additionalUpstreamLength +" was chosen");
			GeneLocation.report += "upstream parameter exceedes ("+ this.getSynonym()+ ") gene's predecessor on same strand or other strand ("+ predecessorGeneLocation.getSynonym() + "), " + Math.max(minUpstreamLength,nucleotidesToNextGene) +" instead of "+ additionalUpstreamLength +" was chosen" + "\n";
			GeneLocation.countu++;
			return Math.max((minUpstreamLength > (this.getFrom()-1)?(int)(this.getFrom()-1):minUpstreamLength),nucleotidesToNextGene);
		    } else {	
			return Math.max((minUpstreamLength > (this.getFrom()-1)?(int)(this.getFrom()-1):minUpstreamLength),(additionalUpstreamLength > (this.getFrom()-1)?(int)(this.getFrom()-1):additionalUpstreamLength));
		    }
		}		
	    } else {
		return Math.max((minUpstreamLength > (this.getFrom()-1)?(int)(this.getFrom()-1):minUpstreamLength),(additionalUpstreamLength > (this.getFrom()-1)?(int)(this.getFrom()-1):additionalUpstreamLength));
	    }
	} else {
	    return Math.max((minUpstreamLength > (this.getFrom()-1)?(int)(this.getFrom()-1):minUpstreamLength),(additionalUpstreamLength > (this.getFrom()-1)?(int)(this.getFrom()-1):additionalUpstreamLength)); 
	}
    }

    public int checkInterferenceDownstream(int maxDownstreamLength, boolean isSafeExtractionMode){
	long geneSequenceLength = 1 + this.getTo() - this.getFrom();
	if (isSafeExtractionMode && (((int)geneSequenceLength) < maxDownstreamLength)){
	    // System.out.println("downstream parameter exceedes ("+ this.getSynonym()+ ") gene's length and is shortend to gene's length of " +geneSequenceLength);
	    GeneLocation.report +=("downstream parameter exceedes ("+ this.getSynonym()+ ") gene's length and is shortend to gene's length of " +geneSequenceLength + "\n");

	    GeneLocation.countd++;
	    return (int) geneSequenceLength;
	} else {
	    return maxDownstreamLength;
	}

    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((COG == null) ? 0 : COG.hashCode());
	result = prime * result + ((PID == null) ? 0 : PID.hashCode());
	result = prime * result
	+ ((chromosome == null) ? 0 : chromosome.hashCode());
	result = prime * result + ((code == null) ? 0 : code.hashCode());
	result = prime * result + (int) (from ^ (from >>> 32));
	result = prime * result
	+ ((geneName == null) ? 0 : geneName.hashCode());
	result = prime * result + (isPlusStrand ? 1231 : 1237);
	result = prime * result + lengthProtein;
	result = prime * result + ((product == null) ? 0 : product.hashCode());
	result = prime * result + ((synonym == null) ? 0 : synonym.hashCode());
	result = prime * result + (int) (to ^ (to >>> 32));
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	GeneLocation other = (GeneLocation) obj;
	if (COG == null) {
	    if (other.COG != null)
		return false;
	}
	else if (!COG.equals(other.COG))
	    return false;
	if (PID == null) {
	    if (other.PID != null)
		return false;
	}
	else if (!PID.equals(other.PID))
	    return false;
	if (chromosome == null) {
	    if (other.chromosome != null)
		return false;
	}
	else if (!chromosome.equals(other.chromosome))
	    return false;
	if (code == null) {
	    if (other.code != null)
		return false;
	}
	else if (!code.equals(other.code))
	    return false;
	if (from != other.from)
	    return false;
	if (geneName == null) {
	    if (other.geneName != null)
		return false;
	}
	else if (!geneName.equals(other.geneName))
	    return false;
	if (isPlusStrand != other.isPlusStrand)
	    return false;
	if (lengthProtein != other.lengthProtein)
	    return false;
	if (product == null) {
	    if (other.product != null)
		return false;
	}
	else if (!product.equals(other.product))
	    return false;
	if (synonym == null) {
	    if (other.synonym != null)
		return false;
	}
	else if (!synonym.equals(other.synonym))
	    return false;
	if (to != other.to)
	    return false;
	return true;
    }

	public void setOriginalSynonym(String synonym) {
		this.synonym = synonym;
	}

	public String getGeneName() {
		return this.geneName;
	}
}
