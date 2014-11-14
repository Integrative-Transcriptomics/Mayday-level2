package mayday.transkriptorium.data;

import java.util.Iterator;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;

public interface MappedRead {
	
	public long getReadID();
	
	public int getStartInRead();
	
	public int getEndInRead();
	
	public double quality();
	
	public AbstractGeneticCoordinate getTargetCoordinate();
	
	public boolean isUniqueMapping();
	
	public Iterator<MappedRead> getAllReadMappings();

	public Read getRead();
	
	public String getReadIdentifier();
	
	public String toString(boolean allMappings);
}
