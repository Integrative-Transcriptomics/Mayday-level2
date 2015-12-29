package mayday.wapiti.containers.identifiermapping.importer.csv;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.gui.columnparse.ColumnTypeValidator;
import mayday.wapiti.containers.identifiermapping.importer.csv.IdentifierColumnTypes.CTYPE;

public class IdentifierColumnTypeValidator implements ColumnTypeValidator<CTYPE> {

	public String getValidityHint() {
		return "You need to specify an original identifier column as well as a new identifier column.";
	}

	public boolean isValid(List<CTYPE> columnTypes) {
		// one of each type maximum
		Set<Object> foundTypes = new HashSet<Object>();
		
		for (CTYPE o : columnTypes) {
			if (!foundTypes.add(o) && o!=null)
				return false;
		}

		return foundTypes.contains(CTYPE.New_Identifier) && foundTypes.contains(CTYPE.Identifier);
	}

}
