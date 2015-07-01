package mayday.Reveal.data;

/**
 * @author jaeger
 *
 */
public class SNV implements Comparable<SNV> {
	
	private String id;
	private int position;
	private char referenceNucleotide;
	private String chromosome;
	private String gene;
	private int index;
	private double geneticDistance;
	
	public SNV(String id, char referenceNucleotide, int index) {
		this(id, null, 0., 0, index);
		this.referenceNucleotide = referenceNucleotide;
	}
	
	/**
	 * @param gene 
	 * @param id 
	 * @param chromosome 
	 * @param position 
	 * @param referenceNucleotide 
	 * @param index 
	 */
	public SNV(String gene, String id, String chromosome, int position, char referenceNucleotide, int index) {
		this(id, chromosome, 0., position, index);
		this.referenceNucleotide = referenceNucleotide;
		this.gene = gene;
	}
	
	/**
	 * @param id
	 * @param chromosome
	 * @param geneticDistance 
	 * @param position
	 * @param index
	 */
	public SNV(String id, String chromosome, double geneticDistance, int position, int index) {
		this.id = id;
		this.position = position;
		this.chromosome = chromosome;
		this.index = index;
		this.geneticDistance = geneticDistance;
		
		this.gene = "no_gene";
		this.referenceNucleotide = 'N';
	}
	
	/**
	 * @return snp position on chromosome
	 */
	public int getPosition() {
		return this.position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	/**
	 * @return snp identifier
	 * 
	 */
	public String getID() {
		return this.id;
	}
	
	/**
	 * @return reference nucleotide
	 */
	public char getReferenceNucleotide() {
		return this.referenceNucleotide;
	}
	
	/**
	 * @param ref
	 */
	public void setReferenceNucleotide(char ref) {
		this.referenceNucleotide = ref;
	}
	
	/**
	 * @return the chromosome where this snp is located
	 */
	public String getChromosome() {
		return this.chromosome;
	}
	
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	
	/**
	 * @return the gene with which this is snp is associated
	 */
	public String getGene() {
		return this.gene;
	}
	
	/**
	 * @param geneID
	 */
	public void setGene(String geneID) {
		if(geneID == null)
			this.gene = "no_gene";
		else
			this.gene = geneID;
	}
	
	public boolean equals(Object o) {
		if(o == this)
			return true;
		if(!(o instanceof SNV)){
			return false;
		}
		return ((SNV)o).id.equals(this.id);
	}

	/**
	 * @return the index of this snp
	 */
	public Integer getIndex() {
		return this.index;
	}
	
	/**
	 * @return the genetic distance
	 */
	public double getGeneticDistance() {
		return this.geneticDistance;
	}
	
	public void setGeneticDistance(double distance) {
		this.geneticDistance = distance;
	}

	public String serialize() {
		StringBuffer b = new StringBuffer();
		b.append(id);
		b.append("\t");
		b.append(position);
		b.append("\t");
		b.append(referenceNucleotide);
		b.append("\t");
		b.append(chromosome);
		b.append("\t");
		b.append(gene);
		b.append("\t");
		b.append(index);
		b.append("\t");
		b.append(geneticDistance);
		return b.toString();
	}

	@Override
	public int compareTo(SNV o) {
		return getID().compareTo(o.getID());
	}
}
