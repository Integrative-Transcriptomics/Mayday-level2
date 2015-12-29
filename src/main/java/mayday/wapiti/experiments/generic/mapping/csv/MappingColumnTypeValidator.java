package mayday.wapiti.experiments.generic.mapping.csv;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.gui.columnparse.ColumnTypeValidator;
import mayday.wapiti.experiments.generic.mapping.csv.MappingColumnTypes.CTYPE;

public class MappingColumnTypeValidator implements ColumnTypeValidator<CTYPE> {

	protected Set<CTYPE> foundTypes = new HashSet<CTYPE>();
	
	public String getValidityHint() {
		return "You need at least a start position for each read.";
	}

	public boolean isValid(List<CTYPE> columnTypes) {
		
		foundTypes.clear();
		
		// we need at least a FROM coordinate
		boolean hasPosition = false;
		
		// one of each type maximum
		
		for (CTYPE o : columnTypes) {
			if (o==CTYPE.From)
				hasPosition = true;
			if (!foundTypes.add(o) && o!=null)
				return false;
		}

		return hasPosition;
	}
	
	public Set<CTYPE> getTypes() {
		return foundTypes;
	}

}
