package mayday.wapiti.experiments.generic.reads.csv;

import mayday.core.gui.columnparse.ColumnTypes;

public class MappingColumnTypes implements ColumnTypes<MappingColumnTypes.CTYPE> {

	public static enum CTYPE {
		ReadID,
		Species,
		Chromosome,
		Strand,
		From,
		To,
		Length,
		Quality;
		
		public int vgce() {
			if (this==Quality)
				return 9;
			return ordinal();
		}
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
