package mayday.wapiti.containers.featuresummarization.importer.csvmulti;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.gui.columnparse.ColumnTypeValidator;
import mayday.wapiti.containers.featuresummarization.importer.csvmulti.FeatureMapColumnTypes.CTYPE;

public class FeatureMapColumnTypeValidator implements ColumnTypeValidator<CTYPE> {

	public String getValidityHint() {
		return "You need a column of feature names and a column containing subfeature names";
	}

	public boolean isValid(List<CTYPE> columnTypes) {
		// one of each type maximum
		Set<Object> foundTypes = new HashSet<Object>();
		
		for (CTYPE o : columnTypes) {
			if (!foundTypes.add(o) && o!=null)
				return false;
		}

		return foundTypes.contains(CTYPE.Feature) && foundTypes.contains(CTYPE.Subfeature);
	}

}
