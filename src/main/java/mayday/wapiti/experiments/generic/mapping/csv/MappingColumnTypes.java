package mayday.wapiti.experiments.generic.mapping.csv;

import mayday.core.gui.columnparse.ColumnTypes;

public class MappingColumnTypes implements ColumnTypes<MappingColumnTypes.CTYPE> {

	// this can be mapped to VariableGeneticCoordinateElement by adding one
	public static enum CTYPE {
		Species,
		Chromosome,
		Strand,
		From,
		To,
		Length;
				
		public int vgce() {
			return ordinal()+1;
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
