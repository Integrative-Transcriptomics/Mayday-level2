package mayday.Reveal.data;

import mayday.core.MasterTable;
import mayday.core.Probe;

/**
 * @author jaeger
 *
 */
public class Gene extends Probe {

	/**
	 * @param masterTable
	 * @throws RuntimeException
	 */
	public Gene(MasterTable masterTable) throws RuntimeException {
		super(masterTable);
	}

	private int startPosition;
	private int stopPosition;
	private String chromosome;
	
	/**
	 * @param masterTable 
	 * @param name
	 * @param start
	 * @param stop
	 * @param chromosome
	 */
	public Gene(MasterTable masterTable, String name, int start, int stop, String chromosome) {
		this(masterTable);
		this.setName(name);
		this.setStartPosition(start);
		this.setStopPosition(stop);
		this.setChromosome(chromosome);
	}

	/**
	 * @param startPosition
	 */
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	/**
	 * @return start position
	 */
	public int getStartPosition() {
		return startPosition;
	}

	/**
	 * @param stopPosition
	 */
	public void setStopPosition(int stopPosition) {
		this.stopPosition = stopPosition;
	}

	/**
	 * @return stop position
	 */
	public int getStopPosition() {
		return stopPosition;
	}

	/**
	 * @param chromosome
	 */
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	/**
	 * @return chromosome
	 */
	public String getChromosome() {
		return chromosome;
	}
	
	/**
	 * @param o
	 * @return true if o and this are the same
	 */
	public boolean euqals(Object o) {
		if(o == this)
			return true;
		if(!(o instanceof Gene)) {
			return false;
		}
		
		return ((Gene)o).name.equals(this.name);
	}
	
	public int hashCode() {
		return this.name.hashCode();
	}
	
	public String serialize() {
		return getName() + "\t" + 
					getStartPosition() + "\t" + 
					getStopPosition() + "\t" +
					getChromosome();
	}
}
