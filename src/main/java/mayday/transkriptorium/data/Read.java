package mayday.transkriptorium.data;

import java.util.Iterator;

public interface Read {

	public boolean hasUniqueMapping();
	
	public Iterator<MappedRead> getAllMappings();
	
	public String getIdentifier();

	public Read getPartner();
}
