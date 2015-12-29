package mayday.wapiti.containers.identifiermapping.importer.csv;

import mayday.core.gui.columnparse.ColumnTypes;

public class IdentifierColumnTypes implements ColumnTypes<IdentifierColumnTypes.CTYPE> {

	public static enum CTYPE {
		Identifier("Original identifier"),
		New_Identifier("New identifier");
		
		protected String name;
		
		CTYPE(String name) {
			this.name=name;
		}
		public String toString() {
			return name;
		}
	}
	
	
	public int indexOf(CTYPE type) {
		return CTYPE.valueOf(type.name()).ordinal();
	}

	public CTYPE typeOf(String value) {
		for (CTYPE ct : CTYPE.values())
			if (ct.name.equals(value))
				return ct;
		return CTYPE.valueOf(value);
	}

	public CTYPE[] values() {
		return CTYPE.values();
	}

}
