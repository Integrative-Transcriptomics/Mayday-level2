package mayday.motifsearch.preparation;


public interface IGeneticCoordinate {
    
    public Chromosome getChromosome();

    public long getFrom();
    
    public long getTo();

    public char getStrand();

    public boolean isPlusStrand();
    
    public String toString();

}
