package mayday.wapiti.containers.featuresummarization.importer.csvmulti;

import mayday.core.gui.columnparse.ColumnTypes;

public class FeatureMapColumnTypes implements ColumnTypes<FeatureMapColumnTypes.CTYPE> {

	public static enum CTYPE {
		Feature,
		Subfeature,
	}
	
	public int indexOf(CTYPE type) {
		return CTYPE.valueOf(type.name()).ordinal();
	}

	public CTYPE typeOf(String value) {
		return CTYPE.valueOf(value);
	}

	public CTYPE[] values() {
		return CTYPE.values();
	}
}
