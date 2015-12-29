package mayday.Reveal.io.gff3;

public class ChromosomalLocation {

	private String chromosome;
	private int start;
	private int stop;
	char strand;
	
	public ChromosomalLocation(String chr, int start, int stop, char strand) {
		this.chromosome = chr;
		this.start = start;
		this.stop = stop;
		this.strand = strand;
	}
	
	public String getChromosome() {
		return this.chromosome;
	}
	
	public int getStart() {
		return this.start;
	}
	
	public int getStop() {
		return this.stop;
	}
	
	public char getStrand() {
		return this.strand;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof ChromosomalLocation))
			return false;
		ChromosomalLocation l = (ChromosomalLocation)o;
		if(l.chromosome.equals(this.chromosome) &&
				l.start == this.start &&
				l.stop == this.stop &&
				l.strand == this.strand) {
			return true;
		}
		return false;
	}
	
	public static final int UPSTREAM = 0;
	public static final int DOWNSTREAM = 1;
	public static final int CONTAINED = 2;
	public static final int NOT_CONTAINED = 3;

	public int contains(String chromosome, long position, long upstream, long downstream) {
		if(!this.chromosome.equals(chromosome)) {
			return NOT_CONTAINED;
		} else {
			if(start - upstream <= position && stop + downstream >= position) {
				if(start > position) {
					return UPSTREAM;
				}
				if(stop < position) {
					return DOWNSTREAM;
				}
				return CONTAINED;
			}
			return NOT_CONTAINED;
		}
	}
}
